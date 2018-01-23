package com.demo.webviewscreenshots;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.net.URL;

/**
 * Created by Administrator on 2018/1/22 0022.
 */

public class LocalWebviewActivity extends AppCompatActivity{

    private WebView wv;

    @SuppressLint("JavascriptInterface")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localwebview);
        wv = (WebView) findViewById(R.id.local_wv);
        WebSettings settings = wv.getSettings();
        //如果webview中有Javascript,则 WebView 必须设置支持 Javascript
        settings.setJavaScriptEnabled(true);


//        家在本地url与加载在线URL有两点不同：
//        1、URL类型不一样
//        在加载本地URL时，是以“file:///”开头的，而assets目录所对应的路径名为anroid_asset，写成其它的将识别不了，这是assets目录的以file开头的url形式的固定访问形式。
//        2、不需要设置WebViewClient
//        这里很明显没有设置WebViewClient函数，但仍然是在webview中打开的本地文件
        wv.loadUrl("file:///android_asset/localhtml.html");




        //js调用Android中的方法
        //这里第一个参数this指的是把MyActivity对象注入到WebView中，在WebView中的对象别名叫android，因为传的是this，所以js可以调用该activity中的所有方法
        wv.addJavascriptInterface(this,"android");

        //这里是将内部类Inner注入webview中，所以通过“toast”别名只能调用Inner类中的方法，而不能调用activity中的所有方法
        wv.addJavascriptInterface(new Inner(),"toast");

        wv.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Toast.makeText(LocalWebviewActivity.this,result.toString(),Toast.LENGTH_SHORT).show();
                //它表示向WebView通知操作结果，JsResult有两个函数：JsResult.confirm()和JsResult.cancel()，JsResult.confirm()表示点击了弹出框的确定按钮，JsResult.cancel()则表示点击了弹出框的取消按钮。
                //如果没有使用JsResult来告诉WebView处理结果，则WebView就会认为这个弹出框还一直弹在那里，你再点击alert按钮，将会无效
                result.confirm();
                //如果在onJsAlert中return true，则表示告诉WebView我们已经拦截了alert函数，系统不需要再弹出alert对话框了，
                //如果return false，则表示告诉WebView我们没有拦截alert函数，使用系统的默认处理，从WebChromeClient的源码中可以看到onJsAlert默认是return false的,所以返回false在弹吐司的同时还会弹出alert框
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {

                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {

                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //android调用js中的方法
                String str = "android传给js的msg";
                wv.loadUrl("javascript:sum(2,3)");
//                wv.loadUrl("javascript:showAlert()");
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果不想在 webview 中显示网页，而是直接跳转到浏览器的话,可以使用这种方式强制使用外部浏览器打开网页
                //这是通过隐式意图的方式来启动外部应用
                //这种方式不能打开本地的html页面
                Uri uri = Uri.parse("http://www.baidu.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

    }

    public void showMessage(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("js调用Android");
        builder.setMessage(message);
        builder.show();
    }

    //如果不做任何处理 ,浏览网页,点击系统“Back”键,整个 Browser 会调用 finish()而结束自身,如果希望浏览的网页回退而不是推出浏览器,需要在当前Activity中处理并消费掉该 Back 事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && wv.canGoBack()){
            wv.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class Inner{
        public void toastMessage(String message) {
            Toast.makeText(LocalWebviewActivity.this, "调用Inner类中的方法传递的message:"+message, Toast.LENGTH_LONG).show();
        }
    }


}
