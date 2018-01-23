package com.demo.webviewscreenshots;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.solver.Cache;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Administrator on 2018/1/22 0022.
 */

public class WebviewActivity extends AppCompatActivity{

    private WebView wv;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在加载中，请稍后");

//        final String url = "https://kymjs.com/code/2015/05/03/01/";
        String url = "https://www.zhihu.com/question/265812578/answer/298816898";
        wv = (WebView) findViewById(R.id.webview);
        //注：加载webview网页必须在AndroidManifest.xml中添加访问网络权限
        WebSettings settings = wv.getSettings();
        //如果webview中有Javascript,则 WebView 必须设置支持 Javascript
        settings.setJavaScriptEnabled(true);

        //webview可以控制是否启用缓存
        //settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);////优先使用缓存
        //settings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存

        //直接调用loadURL方法，点击webview页面中的超链接默认会打开系统的browser显示
        wv.loadUrl(url);

        //如果希望点击链接继续在当前应用中响应,而不是Android的系统browser中响应该链接,必须覆盖 WebView的WebViewClient对象
        wv.setWebViewClient(new WebViewClient(){



//            这个函数会在加载超链接时回调过来；所以通过重写shouldOverrideUrlLoading，可以实现对网页中超链接的拦截；
//            返回值是boolean类型，表示是否屏蔽WebView继续加载URL的默认行为，因为这个函数是WebView加载URL前回调的，所以如果我们return true，则WebView接下来就不会再加载这个URL了，所有处理都需要在WebView中操作，包含加载。如果我们return false，则系统就认为上层没有做处理，接下来还是会继续加载这个URL的。WebViewClient默认就是return false的
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                if (url.contains("www.zhihu.com")){
//                    view.loadUrl("https://www.guokr.com/");
//                }else {
//                    view.loadUrl(url);
//                }
//                return true;

                if (url.contains("www.zhihu.com")){
                    view.loadUrl("https://www.guokr.com/");
                }
                return false;
            }

            //网页加载出来之前显示加载进度条
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressDialog.show();
            }

            //网页加载出来后吐司提示
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (progressDialog !=null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Toast.makeText(WebviewActivity.this,"加载成功",Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
