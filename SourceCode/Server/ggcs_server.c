#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <pthread.h>

#define BUF_SIZE 100 // client와 주고받을 데이터 버퍼의 사이즈
#define MAX_CLNT 256 // 최대 연결 가능한 client 수

// 안드로이드 어플리케이션의 어느페이지에서의 통신인지를 구별하기 위한
// 수신한 제일 앞 한 문자 flag value 매크로 (0~9,a~z,A~Z 사용)
#define LOGIN_PAGE '0'
#define SIGN_IN_PAGE '1'
#define INSTRUCTION_TRAINING_PAGE '2'
#define BUTTON_GAME_PAGE '3'
#define HISTORY_PAGE '4'
#define FACE_CALL_PAGE '5'
#define MONITORING_PAGE '6'
#define MEAL_SETTING_PAGE '7'
#define INSTRUCTION_TRAINING_SETTING_PAGE '8'
#define TOILET_TRAINING_SETTING_PAGE '9'

// client가 연결되면 client를 핸들할 thread의 main함수가 되는 함수
// arg에는 client socket의 주소값이 전달됨
void * handle_clnt(void * arg);

// 길이 len의 msg를 clnt에게 보내는 기능을 하는 함수
void send_msg_to_client(int clnt_sock, char * msg, int msg_len);

// error handling function
void error_handling(char * msg);

// 로그인 페이지 처리 함수
void login(int clnt_sock, char * msg);

// 회원가입 페이지 처리 함수
void sign_in(int clnt_sock, char * msg);

// 버튼게임 페이지 처리 함수
void button_game(int clnt_sock, char * msg);

int clnt_cnt = 0; // 현재 연결된 client의 개수
int clnt_socks[MAX_CLNT]; // 현재 연결된 client들의 소켓 file descriptor 값이 담긴 배열
pthread_mutex_t mutx; // critical section handling을 위한 mutex 

// User는 한 명을 가정하여
// DB를 사용하지 않고 서버 파일에 전역변수로서 정보 관리!
// 새롭게 회원가입을 하면 기존의 회원정보는 모두 지워지고 새롭게 덮어씌워지는 방식으로 구현되어 있음
char * user_id = NULL;
char * user_pw = NULL;
char * user_name = NULL;
char * pet_name = NULL;
char * user_email = NULL;

char * r_pi_ip = NULL;
char * r_pi_port = NULL;

// 디버깅용 임시코드
void print_cur_user_info() {
	printf("cur user info : %s %s %s %s %s\n", user_id, user_pw, user_name, pet_name, user_email);
}

int main(int argc, char *argv[]) {
	int serv_sock, clnt_sock;
	struct sockaddr_in serv_adr, clnt_adr;
	int clnt_adr_sz;
	pthread_t t_id;

	// 만약 사용자가 서버프로그램을 잘못 사용했을 경우 사용법 출력
	if(argc!=4) {
		printf("Usage : %s <Port> <RaspberryPi IP> <RaspberryPi Port>\n", argv[0]);
		exit(1);
	}
	r_pi_ip = (char *)calloc(strlen(argv[2]), sizeof(argv[2]));
	r_pi_port = (char *)calloc(strlen(argv[3]), sizeof(argv[3]));	
	strcpy(r_pi_ip, argv[2]);
	strcpy(r_pi_port, argv[3]);
  
	pthread_mutex_init(&mutx, NULL); // 뮤텍스 생성
	serv_sock=socket(PF_INET, SOCK_STREAM, 0); // server socket 생성

	// server socket create 및 init
	memset(&serv_adr, 0, sizeof(serv_adr));
	serv_adr.sin_family = AF_INET; 
	serv_adr.sin_addr.s_addr = htonl(INADDR_ANY);
	serv_adr.sin_port = htons(atoi(argv[1]));
	
	// binding(소켓에 주소 할당) 및 binding 에러 처리
	if(bind(serv_sock, (struct sockaddr *)&serv_adr, sizeof(serv_adr)) == -1) {
		error_handling("bind() error");
	}

	// listening(연결요청 대기) 및 listening 에러 처리 - 연결요청 대기 큐의 크기는 5로 설정해둔 상태
	if(listen(serv_sock, 5) == -1) {
		error_handling("listen() error");
	}
	
	// 서버가 계속해서 listening 상태를 유지하며 client socket의 연결요청을 accept해주고자 while(1)
	// (아직 이 while문을 탈출할 조건 즉, 서버 종료 조건은 만들지 않았음)
	while(1) {
		clnt_adr_sz = sizeof(clnt_adr);
		// server로 연결요청한 client socket에 대해 연결 허용 
		clnt_sock = accept(serv_sock, (struct sockaddr *)&clnt_adr, &clnt_adr_sz);

		// 연결된 client_socket을 현재 연결중인 client socket들을 관리하는 clnt_socks[]에 추가
		// (clnt_socks[]는 critical section이므로 mutex로 동기화)
		pthread_mutex_lock(&mutx);
		clnt_socks[clnt_cnt++] = clnt_sock;
		pthread_mutex_unlock(&mutx);
	
		// 이 아래부터는 연결된 client socket에 대한 작업부가 오면 됨	
		printf("Connected client IP: %s \n", inet_ntoa(clnt_adr.sin_addr));
		pthread_create(&t_id, NULL, handle_clnt, (void *)&clnt_sock);
		pthread_detach(t_id);
	}

	// 모든 작업이 끝나면 serer socket을 닫고 mutex제거 후 종료
	close(serv_sock);
	pthread_mutex_destroy(&mutx);
	return 0;
}
	
void * handle_clnt(void * arg) {
	int clnt_sock = *((int *)arg);
	char msg[BUF_SIZE]; // client로부터 받아올 데이터가 담길 문자열
	int msg_len = 0; // client로부터 받아올 데이터의 길이가 담길 변수 
	
	while( (msg_len = read(clnt_sock, msg, sizeof(msg))) != 0 ) {
		printf("receive message from client : %s, message length : %ld\n", msg, strlen(msg));
		switch(msg[0]) {
			case LOGIN_PAGE:
				printf("client : %s\n", "Login Page");
				login(clnt_sock, msg);
				break;
			case SIGN_IN_PAGE:
				printf("client : %s\n", "Sign In Page");
				sign_in(clnt_sock, msg);
				break;
			case INSTRUCTION_TRAINING_PAGE:
				printf("client : %s\n", "Instruction Training Page");
				break;
			case BUTTON_GAME_PAGE:
				printf("client : %s\n", "Button Game Page");
				button_game(clnt_sock, msg);
				break;
			case HISTORY_PAGE:
				printf("client : %s\n", "Login Page");
				break;
			case FACE_CALL_PAGE:
				printf("client : %s\n", "Login Page");
				break;
			case MONITORING_PAGE:
				printf("client : %s\n", "Login Page");
				break;
			case MEAL_SETTING_PAGE:
				printf("client : %s\n", "Login Page");
				break;
			case INSTRUCTION_TRAINING_SETTING_PAGE:
				printf("client : %s\n", "Login Page");
				break;
			case TOILET_TRAINING_SETTING_PAGE:
				printf("client : %s\n", "Login Page");
				break;
			default:
				break;
		}
		// msg 버퍼 초기화
		memset(msg, 0, BUF_SIZE);
	}
	
	// 한번 echo받은 후 disconnected된 clnt_sock를 제거하기 위해
	// clnt_socks 배열의 clnt_sock 이후 원소들을 한칸씩 당겨주는 작업를 한 후, close(clnt_sock);
	// (clnt_socks[]는 critical section이므로 mutex로 동기화)
	pthread_mutex_lock(&mutx);
	for(int i = 0; i < clnt_cnt; ++i) {
		if(clnt_sock == clnt_socks[i]) {
			while(i++ < clnt_cnt - 1) {
				clnt_socks[i] = clnt_socks[i + 1];
			}
			break;
		}
	}
	--clnt_cnt;
	pthread_mutex_unlock(&mutx);
	close(clnt_sock);
	return NULL;
}

void send_msg_to_client(int clnt_sock, char * msg, int msg_len) {
	write(clnt_sock, msg, msg_len);
}

void error_handling(char * msg) {
	fputs(msg, stderr);
	fputc('\n', stderr);
	exit(1);
}

void login(int clnt_sock, char * msg) {
	char is_login_ok[2] = {'0', '1'};

	// 만약 설정된 id 정보가 없다면 회원가입된 정보가 없는 것이므로 로그인 실패처리
	if (user_id == NULL) {
		printf("Login Fail! (No User Sign Up!)\n");
		send_msg_to_client(clnt_sock, &is_login_ok[0], 1);
		return;
	}

	char * char_ptr_ptr[3]; // page flag, id, pw 
	int i = 0;
	char * ptr = strtok(msg, " "); // page로부터 받은 문자열을 " " 공백 문자를 기준으로 자름, 포인터 반환

	while (ptr != NULL) { // 자른 문자열이 나오지 않을 때까지 즉, 모두 잘랐을 때까지 반복
		char_ptr_ptr[i++] = ptr; // 자른 문자열(page_flag, id, pw)을 char_ptr_ptr에 저장
    	ptr = strtok(NULL, " "); // 다음 문자열을 잘라서 포인터를 반환
	}
	
	// 만약 id, pw가 모두 일치하면 '1'을 보냄
	if(strcmp(char_ptr_ptr[1], user_id) == 0 && strcmp(char_ptr_ptr[2], user_pw) == 0) {
		printf("Login Success!\n");
		char sendStr[100] = "1"; // '1'과 login한 유저의 모든 정보를 다 합쳐도 100자가 안된다고 가정
		strcat(sendStr, " ");
		strcat(sendStr, user_id);
		strcat(sendStr, " ");
		strcat(sendStr, user_pw);
		strcat(sendStr, " ");
		strcat(sendStr, user_name);
		strcat(sendStr, " ");
		strcat(sendStr, pet_name);
		strcat(sendStr, " ");
		strcat(sendStr, user_email);
		send_msg_to_client(clnt_sock, sendStr, strlen(sendStr));
	} else { // 만약 일치하지 않으면 '0'을 보냄
		printf("Login Fail!\n");
		send_msg_to_client(clnt_sock, &is_login_ok[0], 1);
	}
}

void sign_in(int clnt_sock, char * msg) {
	char is_sign_in_ok[2] = {'0', '1'};

	char * char_ptr_ptr[6]; // page flag, id, pw, name, pet name, email 
	int i = 0;
	char * ptr = strtok(msg, " "); // page로부터 받은 문자열을 " " 공백 문자를 기준으로 자름, 포인터 반환

	while (ptr != NULL) { // 자른 문자열이 나오지 않을 때까지 즉, 모두 잘랐을 때까지 반복
		char_ptr_ptr[i++] = ptr; // 자른 문자열(page_flag, id, pw, name, pet_name, email)을 char_ptr_ptr에 저장
    	ptr = strtok(NULL, " "); // 다음 문자열을 잘라서 포인터를 반환
	}

	// 설정된 user_id가 없다면 새롭게 Sign In을 진행
	// 만약 가입하려는 id가 기존에 존재하는 id와 다르면 기존의 정보를 모두 지우고 새롭게 Sign In을 진행 (성공의 의미로 '1'을 보냄)
	if (user_id == NULL || strcmp(char_ptr_ptr[1], user_id) != 0) {
		free(user_id);
		free(user_pw);
		free(user_name);
		free(pet_name);
		free(user_email);
		user_id = NULL;
		user_pw = NULL;
		user_name = NULL;
		pet_name = NULL;
		user_email = NULL;
		user_id = (char *)calloc(strlen(char_ptr_ptr[1]), sizeof(char_ptr_ptr[1]));
		user_pw = (char *)calloc(strlen(char_ptr_ptr[2]), sizeof(char_ptr_ptr[2]));
		user_name = (char *)calloc(strlen(char_ptr_ptr[3]), sizeof(char_ptr_ptr[3]));
		pet_name = (char *)calloc(strlen(char_ptr_ptr[4]), sizeof(char_ptr_ptr[4]));
		user_email = (char *)calloc(strlen(char_ptr_ptr[5]), sizeof(char_ptr_ptr[5]));		
		strcpy(user_id, char_ptr_ptr[1]);
		strcpy(user_pw, char_ptr_ptr[2]);
		strcpy(user_name, char_ptr_ptr[3]);
		strcpy(pet_name, char_ptr_ptr[4]);
		strcpy(user_email, char_ptr_ptr[5]);
		printf("Sign In Success!\n");
		send_msg_to_client(clnt_sock, &is_sign_in_ok[1], 1);
	}
	else { // 만약 가입하려는 id가 기존에 존재하는 id이면 sign in 실패의 의미로 '0'을 보냄
		printf("Sign In Fail!\n");
		send_msg_to_client(clnt_sock, &is_sign_in_ok[0], 1);
	}
}

void button_game(int clnt_sock, char * msg) {
	char * char_ptr_ptr[4]; // page flag, button flag, level, interval time 
	int i = 0;
	char * ptr = strtok(msg, " "); // page로부터 받은 문자열을 " " 공백 문자를 기준으로 자름, 포인터 반환

	while (ptr != NULL) { // 자른 문자열이 나오지 않을 때까지 즉, 모두 잘랐을 때까지 반복
		char_ptr_ptr[i++] = ptr; // 자른 문자열(page_flag, id, pw)을 char_ptr_ptr에 저장
    	ptr = strtok(NULL, " "); // 다음 문자열을 잘라서 포인터를 반환
	}

	// 라즈베리파이의 서버에 클라이언트로서 연결하기 위한 설정부
	int sock;
	struct sockaddr_in serv_addr;
	sock = socket(PF_INET, SOCK_STREAM, 0);
	memset(&serv_addr, 0, sizeof(serv_addr));
	serv_addr.sin_family = AF_INET;
	serv_addr.sin_addr.s_addr = inet_addr(r_pi_ip);
	serv_addr.sin_port = htons(atoi(r_pi_port));
	if (connect(sock, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) == -1) {
		error_handling("connect() error");
	}

	// 라즈베리파이와 연결됐으니 데이터 전달 (button flag, level, interval time)
	if(strcmp(char_ptr_ptr[1], "0") == 0) { // start button
		char level = char_ptr_ptr[2][0];
		// 레벨 1,2,3에 따라 라즈베리파이로 실행 메세지 보내기
		printf("%c\n", level);
		// 라즈베리파이에 메세지 전달
		write(sock, msg, strlen(msg));
		// 안드로이드에 잘 됐다고 메세지 전달
		send_msg_to_client(clnt_sock, "Button Game Start Success!", 26);

	} else if (strcmp(char_ptr_ptr[1], "1") == 0) { // auto start button
		;
	} else if (strcmp(char_ptr_ptr[1], "2") == 0) { // auto stop button
		;
	}
	//

	// 라즈베리파이 서버와 연결을 마쳤으니 소켓 닫기
	close(sock);

	
}
