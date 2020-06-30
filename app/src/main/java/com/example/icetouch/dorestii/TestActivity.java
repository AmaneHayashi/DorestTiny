package com.example.icetouch.dorestii;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

public class TestActivity extends AppCompatActivity {

    private CheckBox qta, qtb, qtc, qtd;//四个选项
    private Button bt_submit;//提交按钮
    private CountDownTimer countDownTimer;//倒计时器
    private String checker = "";//提交结果记录
    private String exerciseSubject;//训练组（第A天第B组，dayApartB）
    private int grade = 0, next_times = 1;//成绩（做对题数）与题号
    private int isThisNumberEqualsMax, isThatNumberEqualsMax;//finishedNum中间变量
    private int MomentMax = 2;//训练组数，需要更新
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context = getApplicationContext();

        final String nameC = getIntent().getStringExtra("test_nameC");
        final String nameE = getIntent().getStringExtra("test_nameE");
        final int id = getIntent().getIntExtra("test_id", 0);
        final int numId = getIntent().getIntExtra("test_num", 0);
        final int timeId = getIntent().getIntExtra("test_word", 0);
        final int imageArray[] = getIntent().getIntArrayExtra("test_imageArr");
        final String answer[] = getIntent().getStringArrayExtra("test_ans");

        int exerciseTimes = getSharedPreferences("common", MODE_PRIVATE).getInt("exerciseTimes", 0);//训练次数
        exerciseSubject = "day" + "" + (exerciseTimes) + "part" + "" + id;
        isThisNumberEqualsMax = getSharedPreferences("common", MODE_PRIVATE).getInt("FinishedNum", 0);//初始的finishedNum

        JumpToTest(nameC, nameE, id, numId, timeId, imageArray, answer);
    }

    public void JumpToTest(final String nameC, final String nameE, final int id, final int numId, final int timeId, final int imageArray[], final String answer[]){
        setContentView(R.layout.activity_test);
        Button entry;
        TextView label, reminder;

        label = (TextView) findViewById(R.id.label);
        reminder = (TextView) findViewById(R.id.reminder);
        entry = (Button) findViewById(R.id.bt_entrance);
        label.setText("题目说明");
        reminder.setText(String.format(getResources().getString(R.string.word), nameC, numId, timeId));
        entry.setText("开始");

        //如果当日已训练完直接跳出，如果未训练完，检测该项目是否完成
        if(isTodayTested()){
            testIsDone(entry, nameC, nameE, id, numId, timeId, imageArray, answer);
        }
        else {
            if (getSharedPreferences(exerciseSubject, MODE_PRIVATE).getBoolean("isFinished", false)) {
                testIsDone(entry, nameC, nameE, id, numId, timeId, imageArray, answer);
            } else {
                entry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //设置未完成（考试中）标志，考试过程中不可退出
                        getSharedPreferences(exerciseSubject, MODE_PRIVATE).edit().putBoolean("isFinished", false).apply();
                        Toast.makeText(TestActivity.this, String.format(getResources().getString(R.string.reminder), timeId), Toast.LENGTH_SHORT).show();
                        countDownTimer = new CountDownTimer(timeId * 60 * 1000 + 500, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                getSharedPreferences(exerciseSubject, MODE_PRIVATE).edit().putLong("restMinute", millisUntilFinished / 1000 / 60).apply();
                                getSharedPreferences(exerciseSubject, MODE_PRIVATE).edit().putLong("restSecond",
                                        (millisUntilFinished - millisUntilFinished / 1000 / 60 * 1000 * 60) / 1000).apply();
                            }
                            @Override
                            public void onFinish() {
                                JudgeHelper(nameC, nameE, numId, timeId, checker, grade);
                            }
                        }.start();
                        JumpToQT(nameC, nameE, id, numId, timeId, imageArray, answer);
                    }
                });
            }
        }
    }

    public void JumpToQT(final String nameC, final String nameE, final int id, final int numId, final int timeId, final int imageArray[], final String answer[]){
        setContentView(R.layout.activity_qt);

        ImageView qt;
        final Bitmap bitmap = readBitMap(getContext(), imageArray[next_times]);

        qt = (ImageView) findViewById(R.id.qt);
        qt.setImageBitmap(bitmap);
        qt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getSharedPreferences("common", MODE_PRIVATE).getBoolean("isTeacherMode", false)) {
                    if(next_times == numId) {
                        getSharedPreferences("common", MODE_PRIVATE).edit().putBoolean("isTeacherMode", false).apply();
                        TestActivity.this.finish();
                    }
                    else {
                        next_times++;
                        bitmap.recycle();//回收图片
                        System.gc();
                        JumpToQT(nameC, nameE, id, numId, timeId, imageArray, answer);
                    }
                }
                else {
                    bitmap.recycle();//回收图片
                    System.gc();
                    JumpToQTA(nameC, nameE, id, numId, timeId, imageArray, answer);
                }
            }
        });
    }

    public void JumpToQTA(final String nameC, final String nameE, final int id, final int numId, final int timeId, final int imageArray[], final String answer[]){
        setContentView(R.layout.activity_qta);
        TextView label2;
        Button bt_qt, bt_timeReq;

        label2 = (TextView) findViewById(R.id.label2);
        qta = (CheckBox) findViewById(R.id.qta);
        qtb = (CheckBox) findViewById(R.id.qtb);
        qtc = (CheckBox) findViewById(R.id.qtc);
        qtd = (CheckBox) findViewById(R.id.qtd);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        bt_qt = (Button) findViewById(R.id.bt_qt);
        bt_timeReq = (Button) findViewById(R.id.bt_timeReq) ;

        label2.setText(nameC + "·第" + "" + next_times + "题");
        qta.setText("A");
        qtb.setText("B");
        qtc.setText("C");
        qtd.setText("D");
        bt_qt.setText("返回读题");
        bt_qt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpToQT(nameC, nameE, id, numId, timeId, imageArray, answer);
            }
        });
        bt_timeReq.setText("查看剩余时间");
        bt_timeReq.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        bt_timeReq.getPaint().setAntiAlias(true);
        bt_timeReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TestActivity.this, String.format(getResources().getString(R.string.time),
                        getSharedPreferences(exerciseSubject, MODE_PRIVATE).getLong("restMinute", 0),
                        getSharedPreferences(exerciseSubject, MODE_PRIVATE).getLong("restSecond", 0)), Toast.LENGTH_SHORT).show();
            }
        });
        SetDorest(nameC, nameE, id, numId, timeId, imageArray, answer);
    }

    public void SetDorest(final String nameC, final String nameE, final int id, final int numId, final int timeId,  final int imageArray[], final String answer[]){
        qta.setChecked(true);
        qtb.setChecked(true);
        qtc.setChecked(true);
        qtd.setChecked(true);
        qta.setChecked(false);
        qtb.setChecked(false);
        qtc.setChecked(false);
        qtd.setChecked(false);
        if(next_times == numId) {
            bt_submit.setText("提交答卷");
            bt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder SubmitWarning = new AlertDialog.Builder(TestActivity.this);
                    SubmitWarning.setTitle("提示")
                            .setIcon(R.drawable.dric_legal)
                            .setMessage("你确定要提交吗？")
                            .setNegativeButton("否", null)
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    checker += mJudgeHelper(numId, qta, qtb, qtc, qtd, answer);//统计各题的错误正确
                                    grade +=  mJudgeHelper(numId, qta, qtb, qtc, qtd, answer);//统计总成绩
                                    GradeRegister();//将结果与答案保存在SharedPreferences中
                                    JudgeHelper(nameC, nameE, numId, timeId, checker, grade);
                                }
                            });
                    SubmitWarning.create().show();
                }
            });
        }
        else {
            bt_submit.setText("下一题");
            bt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checker += mJudgeHelper(next_times, qta, qtb, qtc, qtd, answer);
                    grade +=  mJudgeHelper(next_times, qta, qtb, qtc, qtd, answer);
                    GradeRegister();
                    next_times++;
                    JumpToQT(nameC, nameE, id, numId, timeId, imageArray, answer);
                }
            });
        }
    }

    //检查当日是否已完成
    public boolean isTodayTested(){
        int seedNow = getSharedPreferences("common", MODE_PRIVATE).getInt("exerciseSeeds", 0);//当前的应有天数
        int exerNow = getSharedPreferences("common", MODE_PRIVATE).getInt("exerciseTimes", 0);//当前的实际训练数
        if(seedNow == exerNow + 1)//天数=训练数+1，意味着当天还未训练或下一天还未开始
        {
            if(getSharedPreferences("common", MODE_PRIVATE).getBoolean("TodayShouldRecover", false)) {//检查当天是否需要恢复（需要开始）
                isThatNumberEqualsMax = getSharedPreferences("common", MODE_PRIVATE).getInt("RequestedRecoverTimes", 0);//设置恢复次数（每部分恢复一次）
                isThatNumberEqualsMax++;
                getSharedPreferences("common", MODE_PRIVATE).edit().putInt("RequestedRecoverTimes", isThatNumberEqualsMax).apply();//恢复1次后加1并提交
            }
            EqualsJudgement();
            return false;
        }
        else
            return true;
    }

    //检测到该部分已完成时执行
    public void testIsDone(Button start, final String nameC, final String nameE, final int id, final int numId, final int timeId, final int imageArray[], final String answer[]){
        AlertDialog.Builder enterWarning = new AlertDialog.Builder(TestActivity.this);
        enterWarning.setTitle("提示")
                .setIcon(R.drawable.dric_legal)
                .setMessage("你已完成过本部分，请选择需要进入的模式。")
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TestActivity.this.finish();
                    }
                })
                .setPositiveButton("教师模式", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSharedPreferences("common", MODE_PRIVATE).edit().putBoolean("isTeacherMode", true).apply();
                        JumpToQT(nameC, nameE, id, numId, timeId, imageArray, answer);
                    }
                });
        enterWarning.create().show();
        start.setVisibility(View.INVISIBLE);
    }

    public static Context getContext() {
        return context;
    }

    //将drawable转换为bitmap并处理压缩
    public static Bitmap readBitMap(Context context, int resId){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is,null,opt);
    }

    //各题判断函数
    public int mJudgeHelper(int s, CheckBox a, CheckBox b, CheckBox c, CheckBox d, final String answer[]){
        String aa = a.isChecked() ? "A" : "";
        String bb = b.isChecked() ? "B" : "";
        String cc = c.isChecked() ? "C" : "";
        String dd = d.isChecked() ? "D" : "";
        getSharedPreferences(exerciseSubject, MODE_PRIVATE).edit().putString("tempResult", aa + bb + cc + dd).apply();
        getSharedPreferences(exerciseSubject, MODE_PRIVATE).edit().putString("tempAnswer", answer[s]).apply();
        return answer[s].equals(aa + bb + cc + dd) ? 1 : 0;
    }

    //交卷后的处理函数
    public void JudgeHelper(String nameC, String nameE, int numId, int timeId, String check, int grade){
        Toast.makeText(TestActivity.this, "交卷成功", Toast.LENGTH_SHORT).show();

        if(check.length() < numId)//未完成（时间到了没做完）时，多余的答案用X替代
            check += new String(new char[ numId - check.length()]).replace("\0", "X");
        else if(check.length() > numId)//多次点击一个按键导致统计出错时，删除最后多余的结果
            check = check.substring(0, numId);

        SaveForGrade(nameC, nameE, numId, timeId, check, grade);//保存关键信息于SharedPreferences
        ChangeExerciseTimes();//判断训练是否完成（exerciseTimes是否需要加1）
        countDownTimer.cancel();//计时停止
        TestActivity.this.finish();
    }

    //记录结果与答案
    public void GradeRegister(){
        String result = getSharedPreferences(exerciseSubject, MODE_PRIVATE).getString("result", "");
        String key = getSharedPreferences(exerciseSubject, MODE_PRIVATE).getString("answer", "");
        String tempResult = getSharedPreferences(exerciseSubject, MODE_PRIVATE).getString("tempResult", "").equals("") ? "X" : getSharedPreferences(exerciseSubject, MODE_PRIVATE).getString("tempResult", "");
        getSharedPreferences(exerciseSubject, MODE_PRIVATE).edit().putString("result", result + " " + tempResult).apply();
        getSharedPreferences(exerciseSubject, MODE_PRIVATE).edit().putString("answer", key + " " +
                getSharedPreferences(exerciseSubject, MODE_PRIVATE).getString("tempAnswer", "")).apply();
    }

    //检查前驱变量
    public void EqualsJudgement()
    {
        if(isThatNumberEqualsMax == 1){//如果是第一次恢复，将前驱变量设置为0，即将finishedNum变为1
            isThisNumberEqualsMax = 0;
        }
        if(isThatNumberEqualsMax == MomentMax){//当恢复完成时，将当天需要恢复置为false，且将恢复次数清零
            getSharedPreferences("common", MODE_PRIVATE).edit().putBoolean("TodayShouldRecover", false).apply();
            getSharedPreferences("common", MODE_PRIVATE).edit().putInt("RequestedRecoverTimes", 0).apply();
        }
    }

    //改变训练次数
    public void ChangeExerciseTimes(){
        int finishedNum = getSharedPreferences("common", MODE_PRIVATE).getInt("FinishedNum", 0);//完成次数
        if(finishedNum == MomentMax){//如果已经全部完成，将训练次数加1并提交，将需要恢复置为true
            int exerciseTimes = getSharedPreferences("common", MODE_PRIVATE).getInt("exerciseTimes", 0);
            exerciseTimes += 1;
            getSharedPreferences("common", MODE_PRIVATE).edit().putInt("exerciseTimes", exerciseTimes).apply();
            getSharedPreferences("common", MODE_PRIVATE).edit().putBoolean("TodayShouldRecover", true).apply();
        }
    }

    //保存重要信息
    public void SaveForGrade(String nameC, String nameE, int numId, int timeId, String check, int grade) {
        getSharedPreferences(exerciseSubject, MODE_PRIVATE).edit().putString("nameC", nameC).apply();
        getSharedPreferences(exerciseSubject, MODE_PRIVATE).edit().putString("nameE", nameE).apply();
        getSharedPreferences(exerciseSubject, MODE_PRIVATE).edit().putInt("number", numId).apply();
        getSharedPreferences(exerciseSubject, MODE_PRIVATE).edit().putInt("time", timeId).apply();
        getSharedPreferences(exerciseSubject, MODE_PRIVATE).edit().putBoolean("isFinished", true).apply();
        getSharedPreferences(exerciseSubject, MODE_PRIVATE).edit().putString("checker", check).apply();
        getSharedPreferences(exerciseSubject, MODE_PRIVATE).edit().putInt("grade", grade).apply();
        isThisNumberEqualsMax++;//此处前驱受到了isThatNumberEqualsMax的影响
        getSharedPreferences("common", MODE_PRIVATE).edit().putInt("FinishedNum", isThisNumberEqualsMax).apply();
    }

    //设置按键禁止
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(!getSharedPreferences(exerciseSubject, MODE_PRIVATE).getBoolean("isFinished", true)) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    Toast.makeText(this, "考试过程中，不允许退出！", Toast.LENGTH_SHORT).show();
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}