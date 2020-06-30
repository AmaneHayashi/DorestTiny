package com.example.icetouch.dorestii;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

public class TestShowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_show);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ListView mListView;

        TestAdapter testAdapter = new TestAdapter(this, R.layout.test_item, Test.getAllTests());
        mListView = (ListView) findViewById(R.id.test_listView);
        mListView.setAdapter(testAdapter);
        mListView.setTextFilterEnabled(true);
    }
}