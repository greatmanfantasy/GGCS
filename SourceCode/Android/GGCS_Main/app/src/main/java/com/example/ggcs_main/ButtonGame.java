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

public class ButtonGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_game);

        final EditText manual_level_text = (EditText)findViewById(R.id.button_game_manual_level_input_t_n);
        final EditText auto_interval_time_text = (EditText)findViewById(R.id.button_game_auto_interval_input_t_n);
        final EditText auto_level_text = (EditText)findViewById(R.id.button_game_auto_level_input_t_n);

        // Previous 버튼
        Button prev_button = (Button)findViewById(R.id.button_game_prev_b_b);
        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Home 버튼
        Button home_button = (Button)findViewById(R.id.button_game_home_b_b);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home_intent = new Intent(getApplicationContext(), Home.class);
                startActivity(home_intent);
            }
        });

        // Start 버튼
        Button start_button = (Button)findViewById(R.id.button_game_start_b_b);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String manual_level = manual_level_text.getText().toString();

                // 1. create TCPClient
                TCPClient tcp_client = new TCPClient();

                // 2. set send_byte_buffer
                tcp_client.send_byte_buffer = ByteBuffer.allocate(1 + 1 + 1 + 1 + 1); // page_flag(1) + 공백(1) + button_flag(1) + 공백(1) + level(1)
                tcp_client.send_byte_buffer.order(ByteOrder.LITTLE_ENDIAN);
                tcp_client.send_byte_buffer.put("3".getBytes()); // button game page flag : 3
                tcp_client.send_byte_buffer.put(" ".getBytes());
                tcp_client.send_byte_buffer.put("0".getBytes()); // start button flag : 0
                tcp_client.send_byte_buffer.put(" ".getBytes());
                tcp_client.send_byte_buffer.put(manual_level.getBytes());

                // 3. start LoginTCPClient
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

                // 5. close the socket and stream
                tcp_client.close_socket_and_stream();

            }
        });

        // Auto Start 버튼
        Button auto_start_button = (Button)findViewById(R.id.button_game_auto_start_b_b);
        auto_start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String auto_interval_time = auto_interval_time_text.getText().toString();
                String auto_level = auto_level_text.getText().toString();
            }
        });

        // Auto Stop 버튼
        Button auto_stop_button = (Button)findViewById(R.id.button_game_auto_stop_b_b);
        auto_stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
