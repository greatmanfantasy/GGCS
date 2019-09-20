package com.example.ggcs_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Setting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        // Previous 버튼
        Button prev_button = (Button)findViewById(R.id.setting_prev_b_b);
        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Home 버튼
        Button home_button = (Button)findViewById(R.id.setting_home_b_b);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home_intent = new Intent(getApplicationContext(), Home.class);
                startActivity(home_intent);
            }
        });

        // Meal Setting 버튼
        Button meal_setting_button = (Button)findViewById(R.id.setting_meal_setting_b_b);
        meal_setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent meal_setting_intent = new Intent(getApplicationContext(), MealSetting.class);
                startActivity(meal_setting_intent);
            }
        });

        // Instruction Training Setting 버튼
        Button instruction_training_setting_button = (Button)findViewById(R.id.setting_instruction_training_setting_b_b);
        instruction_training_setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent instruction_training_setting_intent = new Intent(getApplicationContext(), InstructionTrainingSetting.class);
                startActivity(instruction_training_setting_intent);
            }
        });

        // Toilet Training Setting 버튼
        Button toilet_training_setting_button= (Button)findViewById(R.id.setting_toilet_training_setting_b_b);
        toilet_training_setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toilet_training_setting_intent = new Intent(getApplicationContext(), ToiletTrainingSetting.class);
                startActivity(toilet_training_setting_intent);
            }
        });

    }
}
