package com.example.ggcs_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;

public class InstructionTraining extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instruction_training);

        final EditText manual_repeat_time_text = (EditText)findViewById(R.id.instruction_training_repeat_input_t_n);
        final EditText auto_interval_time_text = (EditText)findViewById(R.id.instruction_training_auto_interval_input_t_n);
        final EditText auto_repeat_time_text = (EditText)findViewById(R.id.instruction_training_auto_repeat_input_t_n);

        // Previous 버튼
        Button prev_button = (Button)findViewById(R.id.instruction_training_prev_b_b);
        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Home 버튼
        Button home_button = (Button)findViewById(R.id.instruction_training_home_b_b);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home_intent = new Intent(getApplicationContext(), Home.class);
                startActivity(home_intent);
            }
        });

        // Come 버튼
        Button come_button = (Button)findViewById(R.id.instruction_training_come_b_b);
        come_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String manual_repeat_time = manual_repeat_time_text.getText().toString();
            }
        });

        // Wait 버튼
        Button wait_button = (Button)findViewById(R.id.instruction_training_wait_b_b);
        wait_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String manual_repeat_time = manual_repeat_time_text.getText().toString();
            }
        });

        // Lie 버튼
        Button lie_button = (Button)findViewById(R.id.instruction_training_lie_b_b);
        lie_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String manual_repeat_time = manual_repeat_time_text.getText().toString();
            }
        });

        // Auto Start 버튼
        Button auto_start_button = (Button)findViewById(R.id.instruction_training_auto_start_b_b);
        auto_start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String auto_interval_time = auto_interval_time_text.getText().toString();
                String auto_repeat_time = auto_repeat_time_text.getText().toString();
            }
        });

        // Auto Stop 버튼
        Button auto_stop_button = (Button)findViewById(R.id.instruction_training_auto_stop_b_b);
        auto_stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
