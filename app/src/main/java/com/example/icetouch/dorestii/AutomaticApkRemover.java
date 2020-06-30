package com.example.icetouch.dorestii;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import java.io.File;


public class AutomaticApkRemover extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {  //一旦apk包改变，即删除apk文件
            removeFile();
        }
    }

    public void removeFile() {
        String apkFileName = "DorestII.apk";
        String apkFilePath = "tencent/QQfile_recv/"; //此地址待定Doris
        new File(Environment.getExternalStorageDirectory(), apkFilePath + apkFileName).delete();
    }
}