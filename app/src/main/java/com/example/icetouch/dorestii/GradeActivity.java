package com.example.icetouch.dorestii;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import java.io.File;
import java.io.RandomAccessFile;

public class GradeActivity extends AppCompatActivity {
    private int real_qt = 0;//一次训练的题数
    private String s = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView title, content;
        String exerciseSubject;

        int exerciseTimes = getSharedPreferences("common", MODE_PRIVATE).getInt("exerciseTimes", 0);//训练天数
        int real_i =  getSharedPreferences("common", MODE_PRIVATE).getInt("FinishedNum", 0);//一次训练的组数

        //检查已经完成的题数（判断依据为ID）
        for (int i = 1; i <= real_i; i++) {
            exerciseSubject = "day" + "" + (exerciseTimes - 1) + "part" + "" + i;
            int temp = getSharedPreferences(exerciseSubject, MODE_PRIVATE).getInt("number", 0);
            real_qt += temp;
        }

        //成绩录入
        for (int i = 1; i <= real_i; i++) {
            exerciseSubject = "day" + "" + (exerciseTimes - 1) + "part" + "" + i;
            s += String.format(getResources().getString(R.string.each),
                    getSharedPreferences(exerciseSubject, MODE_PRIVATE).getString("nameC", ""),
                    getSharedPreferences(exerciseSubject, MODE_PRIVATE).getString("nameE", ""),
                    getSharedPreferences(exerciseSubject, MODE_PRIVATE).getInt("number", 0),
                    getSharedPreferences(exerciseSubject, MODE_PRIVATE).getInt("grade", 0),
                    getSharedPreferences(exerciseSubject, MODE_PRIVATE).getInt("number", 0) == 0 ? 0 : 100 *
                            getSharedPreferences(exerciseSubject, MODE_PRIVATE).getInt("grade", 0) /
                            getSharedPreferences(exerciseSubject, MODE_PRIVATE).getInt("number", 0),
                    getSharedPreferences(exerciseSubject, MODE_PRIVATE).getLong("restMinute", 0) == 0 ? 0 :
                            getSharedPreferences(exerciseSubject, MODE_PRIVATE).getInt("time", 0) - 1 - getSharedPreferences(exerciseSubject, MODE_PRIVATE).getLong("restMinute", 0),
                    getSharedPreferences(exerciseSubject, MODE_PRIVATE).getLong("restSecond", 0) == 0 ? 0 :
                            60 - getSharedPreferences(exerciseSubject, MODE_PRIVATE).getLong("restSecond", 0),
                    getSharedPreferences(exerciseSubject, MODE_PRIVATE).getString("checker", ""),
                    getSharedPreferences(exerciseSubject, MODE_PRIVATE).getString("result", ""),
                    getSharedPreferences(exerciseSubject, MODE_PRIVATE).getString("answer", ""));
        }

        title = (TextView) findViewById(R.id.grade_title);
        content = (TextView) findViewById(R.id.content);

        title.setText("成绩单");
        String text = String.format(getResources().getString(R.string.sum), exerciseTimes, real_i, real_qt) + s;
        content.setText(text);
        initData(text);
    }

    //将成绩写入record.txt
    private void initData(String string) {
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.example.icetouch.dorestii/";
        String fileName = "record.txt";
        writeTxtToFile(string, filePath, fileName);
    }

    // 将字符串写入到文本文件中
    public void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath+fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
    }
}