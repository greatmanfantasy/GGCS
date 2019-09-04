package com.example.ggcs_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        final EditText id_text = (EditText)findViewById(R.id.login_id_t_pt);
        final EditText pw_text = (EditText)findViewById(R.id.login_pw_t_pw);

        // Info 버튼
        Button info_button =   (Button)findViewById(R.id.login_info_b_b);
        info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent info_intent = new Intent(getApplicationContext(), Info.class);
                startActivity(info_intent);
            }
        });

        // Login 버튼
        Button login_button = (Button)findViewById(R.id.login_login_b_b);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력된 id, pw를 받아오는 부분
                String id = id_text.getText().toString();
                String pw = pw_text.getText().toString();

                // 서버로부터 login에 대해 ok sign을 받으면 is_login_ok 가 true가 되어 home page로 이동
                boolean is_login_ok = false;

                // 1. create LoginTCPClient
                TCPClient login_tcp_client = new TCPClient();

                // 2. set send_byte_buffer
                login_tcp_client.send_byte_buffer = ByteBuffer.allocate(1 + 1 + id.length() + 1 + pw.length()); // page_flag(1) + 공백(1) + id + 공백(1) + pw
                login_tcp_client.send_byte_buffer.order(ByteOrder.LITTLE_ENDIAN);
                login_tcp_client.send_byte_buffer.put("0".getBytes()); // login page flag : 0
                login_tcp_client.send_byte_buffer.put(" ".getBytes());
                login_tcp_client.send_byte_buffer.put(id.getBytes());
                login_tcp_client.send_byte_buffer.put(" ".getBytes());
                login_tcp_client.send_byte_buffer.put(pw.getBytes());

                // 3. start LoginTCPClient
                login_tcp_client.start();
                try {
                    login_tcp_client.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 4. processing receive data from server
                String receive_string = null;
                try {
                    receive_string = new String(login_tcp_client.receive_byte, 0, login_tcp_client.receive_byte_length, "UTF-8");
                    Log.d(this.getClass().getName(), "Receive Message from Server : " + receive_string + ", Message Length : " + receive_string.length());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (receive_string.charAt(0) == '1') { // receive login success flag from server
                    is_login_ok = true;
                    String[] login_data = receive_string.split(" ");
                    UserData userData = (UserData)getApplication();
                    userData.setUserId(login_data[1]);
                    userData.setUserPw(login_data[2]);
                    userData.setUserName(login_data[3]);
                    userData.setPetName(login_data[4]);
                    userData.setUserEmail(login_data[5]);
                    Log.d(this.getClass().getName(), userData.getUserId() + " " + userData.getUserPw() + " " + userData.getUserName() + " " + userData.getPetName() + " " + userData.getUserEmail());
                } else { // receive login not success flag from server
                    Toast.makeText(getApplicationContext(), "Login Failed! (Wrong ID/PW)", Toast.LENGTH_LONG).show();
                }

                // 5. close the socket and stream
                login_tcp_client.close_socket_and_stream();

                // 서버로부터 login에 대해 ok sign을 받았을 경우에만 home page로 이동
                if (is_login_ok) {
                    Intent home_intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(home_intent);
                }
            }
        });

        // Sign In 버튼
        Button sign_in_button = (Button)findViewById(R.id.login_sign_in_b_b);
        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign_in_intent = new Intent(getApplicationContext(), Sign_in.class);
                startActivity(sign_in_intent);
            }
        });

        // Forgot ID PW 버튼
        Button forgot_button = (Button)findViewById(R.id.login_forgot_b_b);
        forgot_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 유저들이 ID PW 분실시 문의할 메일주소를 화면에 출력
                Toast.makeText(getApplicationContext(), "greatmanfantasy@gmail.com 으로 문의 부탁드립니다.", Toast.LENGTH_LONG).show();
            }
        });
    }
}