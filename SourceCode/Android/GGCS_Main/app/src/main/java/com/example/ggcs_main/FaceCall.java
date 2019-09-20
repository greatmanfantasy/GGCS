package com.example.ggcs_main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

// 라즈베리파이3b+ 에서 UV4L WebRTC를 통해 영상통화 구현
public class FaceCall extends AppCompatActivity {

    WebView web_view;

    String url = "https://10.14.5.141:8080/stream/webrtc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_call);

        web_view = (WebView)findViewById(R.id.face_call_screen_v_wv);
        web_view.getSettings().setJavaScriptEnabled(true);

        web_view.loadUrl(url);
        web_view.setWebChromeClient(new WebChromeClient());
        web_view.setWebViewClient(new WebViewClient());

        // Exit 버튼
        Button exit_button = (Button)findViewById(R.id.face_call_exit_b_b);
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
            if(web_view != null) {
                try {
                    Class.forName("android.webkit.WebView").getMethod("onPause", (Class[]) null).invoke(web_view, (Object[]) null);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
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
