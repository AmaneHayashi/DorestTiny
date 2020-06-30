package com.example.icetouch.dorestii;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private long mExitTime;
    private int seed = 1;//天数，每次更新时更改

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView title;
        Button enter, show;

        title = (TextView) findViewById(R.id.title);
        enter = (Button) findViewById(R.id.enter);
        show = (Button) findViewById(R.id.show);
        title.setText("冬试");
        enter.setText("考试入口");
        show.setText("答案与成绩单");

        getSharedPreferences("common", MODE_PRIVATE).edit().putInt("exerciseSeeds", seed).apply();

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestShowActivity.class);
                startActivity(intent);
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //天数等于训练数，表明当日已训练完
                if(getSharedPreferences("common", MODE_PRIVATE).getInt("exerciseTimes", 0) == seed) {
                    Intent intent = new Intent(MainActivity.this, GradeActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "完成今日考试后，才能查看答案与成绩单！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                MainActivity.this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}