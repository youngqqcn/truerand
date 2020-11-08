package com.example.truerand;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ignoreSSLHandshake();

        Button btnStart = (Button) findViewById(R.id.idBtnStart);



        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        // cachedImage = asyncImageLoader.loadDrawable(imageUrl, position);
                        // imageView.setImageDrawable(cachedImage);
                        String rsp = SendGetRequest("");
                        Log.i("rsp", rsp);
                        TextView txtOutput = (TextView)findViewById(R.id.idTxtOutput);
                        txtOutput.setText(rsp);
                    }
                }).start();

            }
        });
    }


    public static String SendGetRequest(String content){

        HttpURLConnection conn=null;
        String strRet = "";
        try {

//            String strUrl ="http://81.68.110.75:52334/";
//            String strUrl="https://www.baidu.com";
            String strUrl = "https://www.random.org/integers/?num=2&min=1&max=1000000000&col=2&base=10&format=plain&rnd=new";
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if(HttpURLConnection.HTTP_OK== conn.getResponseCode()){
                Log.i("PostGetUtil","get请求成功");
                InputStream in = conn.getInputStream();
                String backcontent = readInputStream(in);
                backcontent = URLDecoder.decode(backcontent,"UTF-8");
                Log.i("PostGetUtil",backcontent);
                strRet = backcontent;
                strRet = strRet.replace("\n", "");
                in.close();
            }
            else {
                Log.i("PostGetUtil","get请求失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            conn.disconnect();
        }
        return strRet;
    }

    private static  String readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toString();
    }


    private static void ignoreSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("TLS");
            // trustAllCerts信任所有的证书
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
        }
    }

}