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

// client가 연결되면 client를 핸들할 thread의 main함수가 되는 함수
// arg에는 client socket의 주소값이 전달됨
void * handle_clnt(void * arg);

// 길이 len의 msg를 clnt에게 보내는 기능을 하는 함수
void send_msg_to_client(int clnt_sock, char * msg, int msg_len);

// error handling function
void error_handling(char * msg);

int clnt_cnt = 0; // 현재 연결된 client의 개수
int clnt_socks[MAX_CLNT]; // 현재 연결된 client들의 소켓 file descriptor 값이 담긴 배열
pthread_mutex_t mutx; // critical section handling을 위한 mutex 

int main(int argc, char *argv[]) {
	int serv_sock, clnt_sock;
	struct sockaddr_in serv_adr, clnt_adr;
	int clnt_adr_sz;
	pthread_t t_id;

	// 만약 사용자가 서버프로그램을 잘못 사용했을 경우 사용법 출력
	if(argc!=2) {
		printf("Usage : %s <port>\n", argv[0]);
		exit(1);
	}
  
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
	
	// 현재 handle_clnt()는
	// 연결된 소켓으로부터 데이터를 받아 화면에 출력한 후
	// 다시 client에게 echo하는 기능으로 구현되어 있음
	while( (msg_len = read(clnt_sock, msg, sizeof(msg))) != 0 ) {
		printf("receive message from client : %s\n", msg);
		send_msg_to_client(clnt_sock, msg, msg_len);
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
	msg[msg_len] ='\0';
	write(clnt_sock, msg, msg_len + 1);
}

void error_handling(char * msg) {
	fputs(msg, stderr);
	fputc('\n', stderr);
	exit(1);
}
