package com.veryworks.android.threadbasic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView result;
    Button btnStart,btnStop;

    // 핸들러 메시지에 담겨오는 what 에 대한 정의
    public static final int SET_TEXT = 100;

    // 메시지를 받는 서버를 오픈
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case SET_TEXT:
                    result.setText(msg.arg1+"");
                    break;
            }
        }
    };

    // 서브 쓰레드
    Thread thread;
    // 서브 쓰레드의 반복문에서 사용되는 플래그
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = (TextView) findViewById(R.id.textResult);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag){
                    Toast.makeText(MainActivity.this,"실행중입니다",Toast.LENGTH_SHORT).show();
                }else {
                    flag = true;
                    thread = new CustomThread();
                    thread.start();
                }
            }
        });
        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopProgram();
            }
        });
    }

    public void stopProgram(){
        flag = false;
    }

    class CustomThread extends Thread {
        @Override
        public void run(){
            int sec = 0;
            // thread 안에서 무한반복할때는
            // thread 를 중단시킬 수 있는 키값을 꼭 세팅해서
            // 메인 thread가 종료시에 같이 종료될 수 있도록 해야한다.
            // 왜!! : 경우에 따라 interrupt 로 thread가 종료되지 않을 수 있기 때문에...
            while(flag){

                // 1. 메시지와 데이터를 보낼때
                Message msg = new Message();
                msg.what = SET_TEXT; // 정해진 메시지 상수 입력
                msg.arg1 = sec;      // 넘겨줄 데이터값입력. setBundle 함수로 Bundle 도 사용할 수 있다

                // sendMessage 함수로 메시지를 Queue 에 담아두면
                // Looper 가 해당 메시지를 꺼내서 handler의 handleMessage 함수에 던져준다
                handler.sendMessage(msg);
                sec++;

                // 2. 메시지만 보낼때는 sendEmptyMessage 를 사용할 수 있다
                // handler.sendEmptyMessage(SET_TEXT);
                try {
                    Thread.sleep(1000);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        flag = false;
        thread.interrupt();

        super.onDestroy();
    }
}
