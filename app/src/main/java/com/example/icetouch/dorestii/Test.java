package com.example.icetouch.dorestii;

import java.util.ArrayList;
import java.util.List;


public class Test {
    private String nameChinese;//中文名
    private String nameEnglish;//英文名
    private int id;//ID
    private int imageId;//插图
    private int numId;//题数
    private int timeId;//时长
    private int imageArray[];//题目图
    private String answer[];//答案组

    public Test(String nameChinese, String nameEnglish, int id, int imageId, int numId, int timeId, int imageArray[], String answer[]){
        this.nameChinese = nameChinese;
        this.nameEnglish = nameEnglish;
        this.id = id;
        this.imageId = imageId;
        this.numId = numId;
        this.timeId = timeId;
        this.imageArray = imageArray;
        this.answer = answer;
    }

    public static List<Test> getAllTests() {
        List<Test> Tests = new ArrayList<>();
        Tests.add(new Test("线性代数", "Linear Algebra",  1, R.drawable.zma, 2, 80,
                new int[]{0, R.drawable.exlm_a_01, R.drawable.exlm_a_02} ,
                new String[]{"", "D", "B"}));
        Tests.add(new Test("高等数学", "Advanced Mathematics", 2, R.drawable.zmb, 3, 80,
                new int[]{0, R.drawable.exlm_a_09, R.drawable.exlm_a_10, R.drawable.exlm_a_11} ,
                new String[]{"", "B", "C","A"}));
        /*注意：待修改的部分为：每日种子（seed，MainActivity）
                                题目组数（MomentMax，TestActivity）
                                apk的地址（apkFilePath，AutomaticApkRemover）
        */
        return Tests;
    }

    public String getNameChinese() {
        return nameChinese;
    }
    public void setNameChinese(String nameChinese) {
        this.nameChinese = nameChinese;
    }

    public String getNameEnglish(){
        return nameEnglish;
    }
    public void setNameEnglish(String nameEnglish){
        this.nameEnglish = nameEnglish;
    }

    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }

    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getNumId(){
        return numId;
    }
    public void setNumId(){
        this.numId = numId;
    }

    public int getTimeId() {
        return timeId;
    }
    public void setTimeId(int timeId) {
        this.timeId = timeId;
    }

    public int[] getImageArray(){ return imageArray; }
    public void setImageArray(){ this.imageArray = imageArray;}

    public String[] getAnswer(){
        return answer;
    }
    public void setAnswer(){
        this.answer = answer;
    }
}