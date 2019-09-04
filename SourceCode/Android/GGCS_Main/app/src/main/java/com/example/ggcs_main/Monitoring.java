package com.example.ggcs_main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.view.KeyEvent;

// 라즈베리파이3b+ 에서 UV4L을 통해 비디오 스트리밍 구현
public class Monitoring extends AppCompatActivity {

    WebView web_view;

    String url = "http://172.30.1.18:8080/stream";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoring);

        web_view = (WebView)findViewById(R.id.monitoring_screen_w_ww);
        web_view.getSettings().setJavaScriptEnabled(true);

        web_view.loadUrl(url);
        web_view.setWebChromeClient(new WebChromeClient());
        web_view.setWebViewClient(new WebViewClient());

        // Exit 버튼
        Button exit_button = (Button)findViewById(R.id.monitoring_exit_b_b);
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//뒤로가기 버튼 이벤트
        if ((keyCode == KeyEvent.KEYCODE_BACK) && web_view.canGoBack()) {//웹뷰에서 뒤로가기 버튼을 누르면 뒤로가짐
            web_view.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class WebViewClientClass extends WebViewClient {//페이지 이동
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("check URL",url);
            view.loadUrl(url);
            return true;
        }
    }
}
