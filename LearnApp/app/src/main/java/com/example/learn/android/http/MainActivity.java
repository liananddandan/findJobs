package com.example.learn.android.http;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.learn.android.R;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        httpGetRequest();
//        httpPostRequest();
//        httpPostRequestJson();
        httpDownload();
    }

    private void httpDownload() {
        new Thread(() -> {
            String web = "https://otatvos.mgtv.com/static/nunai_app/silence_upgrade/com.dangbei.education/20220929/education_v1.3.6_brand_release_bn294_mgtv_20220929150401.apk";
            try {
                URL url = new URL(web);

                File dir = new File(getApplication().getExternalCacheDir().getAbsoluteFile()
                        + File.separator + "download" + File.separator);
                String filePathUrl = url.getFile();
                String fileName = filePathUrl.substring(filePathUrl.lastIndexOf(File.separator) + 1);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, fileName);
                if (file.exists()) {
                    // break-pointer resume
                    Log.e(TAG, "httpDownload: break-pointer resume start.");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    long fileSize = file.length();
                    Log.e(TAG, "already down size = " + fileSize);
                    connection.addRequestProperty("range", "bytes=" + fileSize + "-");
                    connection.connect();
                    int code = connection.getResponseCode();
                    if (code == HttpURLConnection.HTTP_PARTIAL) {
                        long unfinishedSize = connection.getContentLength();
                        long size = fileSize + unfinishedSize;
                        InputStream inputStream = connection.getInputStream();
                        FileOutputStream fos = new FileOutputStream(file, true);
                        byte[] buf = new byte[1024 * 8];
                        int len = -1;
                        int lastProgress = -1;
                        while ((len = inputStream.read(buf)) != -1) {
                            fileSize += len;
                            fos.write(buf, 0, len);
                            int progress = (int) ((fileSize * 100) / size);
                            if (progress > lastProgress) {
                                Log.e(TAG, "download progress: " + (fileSize * 100) / size + "%");
                                lastProgress = progress;
                            }
                        }
                        Log.e(TAG, "totalDownload size = " + size);

                    }
                } else {
                    Log.e(TAG, "httpDownload: download start");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    int fileLength = connection.getContentLength();
                    Log.e(TAG, "fileName=" + fileName + " ,fileLength = " + fileLength);
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buf = new byte[1024 * 8];
                        int len = -1;
                        int size = 0;
                        int lastProgress = -1;
                        while ((len = inputStream.read(buf)) != -1) {
                            size += len;
                            fos.write(buf, 0, len);
                            int progress = (size * 100) / fileLength;
                            if (progress > lastProgress) {
                                Log.e(TAG, "download progress: " + (size * 100) / fileLength + "%");
                                lastProgress = progress;
                            }
                            if (progress > 50) {
                                break;
                            }
                        }
                        Log.e(TAG, "totalDownload size = " + size);
                        fos.flush();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void httpPostRequestJson() {
        new Thread(() -> {
            String web = "https://www.baidu.com";
            try {
                URL url = new URL(web);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                connection.connect();
                String body = "{userName:zhangshan,password:123456";
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(body);
                writer.close();
                int responseCode = connection.getResponseCode();
                Log.e(TAG, "httpPostRequest: responseCode=" + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    Log.e(TAG, "httpPostRequest: result=" + HttpUtils.inputStream2String(inputStream));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void httpPostRequest() {
        new Thread(() -> {
            String web = "https://www.baidu.com";
            try {
                URL url = new URL(web);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.connect();
                String body = "userName=zhangshan&password=123456";
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(body);
                writer.close();
                int responseCode = connection.getResponseCode();
                Log.e(TAG, "httpPostRequest: responseCode=" + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    Log.e(TAG, "httpPostRequest: result=" + HttpUtils.inputStream2String(inputStream));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void httpGetRequest() {
        new Thread(() -> {
            String web = "https://www.baidu.com/";
            try {
                URL url = new URL(web);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int responseCode = connection.getResponseCode();
                Log.e(TAG, "requestCode=" + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    String result = HttpUtils.inputStream2String(inputStream);
                    Log.e(TAG, "GET result = " + result);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}