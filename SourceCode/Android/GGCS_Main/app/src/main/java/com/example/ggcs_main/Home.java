package com.example.ggcs_main;

        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Info 버튼
        Button info_button = (Button)findViewById(R.id.home_info_b_b);
        info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent info_intent = new Intent(getApplicationContext(), Info.class);
                startActivity(info_intent);
            }
        });

        // Training 버튼
        Button training_button = (Button)findViewById(R.id.home_training_b_b);
        training_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent training_intent = new Intent(getApplicationContext(), Training.class);
                startActivity(training_intent);
            }
        });

        // History 버튼
        Button history_button = (Button)findViewById(R.id.home_history_b_b);
        history_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent history_intent = new Intent(getApplicationContext(), History.class);
                startActivity(history_intent);
            }
        });

        // Face Call 버튼
        Button face_call_button = (Button)findViewById(R.id.home_face_call_b_b);
        face_call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent face_call_intent = new Intent(getApplicationContext(), FaceCall.class);
                startActivity(face_call_intent);
            }
        });

        // Monitoring 버튼
        Button monitoring_button = (Button)findViewById(R.id.home_monitoring_b_b);
        monitoring_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent monitoring_intent = new Intent(getApplicationContext(), Monitoring.class);
                startActivity(monitoring_intent);
            }
        });

        // Setting 버튼
        Button setting_button = (Button)findViewById(R.id.home_setting_b_b);
        setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setting_intent = new Intent(getApplicationContext(), Setting.class);
                startActivity(setting_intent);
            }
        });
    }
}