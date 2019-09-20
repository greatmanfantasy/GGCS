package com.example.ggcs_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Training extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training);

        // Previous 버튼
        Button prev_button = (Button)findViewById(R.id.training_prev_b_b);
        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Home 버튼
        Button home_button = (Button)findViewById(R.id.training_home_b_b);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home_intent = new Intent(getApplicationContext(), Home.class);
                startActivity(home_intent);
            }
        });

        // Instruction Training 버튼
        Button instruction_training_button = (Button)findViewById(R.id.training_instruction_training_b_b);
        instruction_training_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent instruction_training_intent = new Intent(getApplicationContext(), InstructionTraining.class);
                startActivity(instruction_training_intent);
            }
        });

        // Button Game 버튼
        Button button_game_button = (Button)findViewById(R.id.training_button_game_b_b);
        button_game_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent button_game_intent = new Intent(getApplicationContext(), ButtonGame.class);
                startActivity(button_game_intent);
            }
        });
    }
}
