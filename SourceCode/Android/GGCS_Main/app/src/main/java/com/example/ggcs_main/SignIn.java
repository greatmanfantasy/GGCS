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

public class SignIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        final EditText id_text = (EditText)findViewById(R.id.sign_in_id_t_pt);
        final EditText pw_text = (EditText)findViewById(R.id.sign_in_pw_t_pt);
        final EditText name_text = (EditText)findViewById(R.id.sign_in_name_t_pt);
        final EditText pet_name_text = (EditText)findViewById(R.id.sign_in_pet_name_t_pt);
        final EditText email_text = (EditText)findViewById(R.id.sign_in_email_t_pt);

        // Previous 버튼
        Button prev_button = (Button)findViewById(R.id.sign_in_prev_b_b);
        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Sign In 버튼
        Button sign_in_button = (Button)findViewById(R.id.sign_in_sign_in_b_b);
        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력된 id, pw를 받아오는 부분
                String id = id_text.getText().toString();
                String pw = pw_text.getText().toString();
                String name = name_text.getText().toString();
                String pet_name = pet_name_text.getText().toString();
                String email = email_text.getText().toString();

                // 서버로부터 sign in에 대해 ok sign을 받으면 is_sign_in_ok 가 true가 되어 다시 login page로 이동
                boolean is_sign_in_ok = false;

                // 1. create TCPClient
                TCPClient tcp_client = new TCPClient();

                // 2. set send_byte_buffer
                // page_flag(1) + 공백(1) + id + 공백(1) + pw + 공백(1) + name + 공백(1) + pet_name + 공백(1) + email
                tcp_client.send_byte_buffer = ByteBuffer.allocate(1 + 1 + id.length() + 1 + pw.length() + 1 + name.length() + 1 + pet_name.length() + 1 + email.length());
                tcp_client.send_byte_buffer.order(ByteOrder.LITTLE_ENDIAN);
                tcp_client.send_byte_buffer.put("1".getBytes()); // sign in page flag : 1
                tcp_client.send_byte_buffer.put(" ".getBytes());
                tcp_client.send_byte_buffer.put(id.getBytes());
                tcp_client.send_byte_buffer.put(" ".getBytes());
                tcp_client.send_byte_buffer.put(pw.getBytes());
                tcp_client.send_byte_buffer.put(" ".getBytes());
                tcp_client.send_byte_buffer.put(name.getBytes());
                tcp_client.send_byte_buffer.put(" ".getBytes());
                tcp_client.send_byte_buffer.put(pet_name.getBytes());
                tcp_client.send_byte_buffer.put(" ".getBytes());
                tcp_client.send_byte_buffer.put(email.getBytes());

                // 3. start SignInTCPClient
                tcp_client.start();
                try {
                    tcp_client.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 4. processing receive data from server
                String receive_string = null;
                try {
                    receive_string = new String(tcp_client.receive_byte, 0, tcp_client.receive_byte_length, "UTF-8");
                    Log.d(this.getClass().getName(), "Receive Message from Server : " + receive_string + ", Message Length : " + receive_string.length());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (receive_string.charAt(0) == '1') { // receive sign in success flag from server
                    is_sign_in_ok = true;
                } else { // receive sign in not success flag from server
                    Toast.makeText(getApplicationContext(), "Sign In Failed!", Toast.LENGTH_LONG).show();
                }

                // 5. close the socket and stream
                tcp_client.close_socket_and_stream();

                // 서버로부터 sign in에 대해 ok sign을 받았을 경우에만 다시 login page로 이동
                if (is_sign_in_ok) {
                    finish();
                }
            }
        });
    }
}
