package com.demo.webviewscreenshots;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private float scale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //当你在 Android 5.0 及更高版本系统的设备中操作时，发现截图显示并不完全。虽然图片宽高符合实际要求，但是内容只包含当前屏幕显示区域内 WebView 的内容
        //原因在于，为了减少内存占用和提升性能，从 Android 5.0 开始，系统能够智能化地选择部分 Html 文档进行渲染。所以，默认情况下，我们只能截取到部分屏幕显示区域内 WebView 的内容，也就出现了上述问题。
        //系统也提供了对应的 API 来修改这一默认优化行为,这段代码必须添加在 WebView 实例被创建之前。如果使用 Activity 的话，也就是在 setContentView() 方法前面
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            WebView.enableSlowWholeDocumentDraw();
        }
        setContentView(R.layout.activity_main);
        final WebView wv = (WebView) findViewById(R.id.wv);
        String url = "https://www.zhihu.com/question/263509544/answer/269846193";

        wv.setWebViewClient(new WebViewClient(){
            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                super.onScaleChanged(view, oldScale, newScale);
                scale = newScale;
            }
        });
        wv.loadUrl(url);

        Button btn = (Button) findViewById(R.id.btn_screenshots);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Bitmap bitmap = getScreenShot(wv);
//                Bitmap bitmap = captureWebview(wv);
                Bitmap bitmap = captureWebView2(wv);
                saveBitmap(bitmap);
            }
        });

        Button btn2 = (Button) findViewById(R.id.btn_webviewuse);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,WebviewActivity.class));
            }
        });

        Button btn3 = (Button) findViewById(R.id.btn_localwebview);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LocalWebviewActivity.class));
            }
        });


    }

    private void saveBitmap(Bitmap bitmap) {
        File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,80,fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //通过给方法会得到内容不完整的截图
    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private Bitmap captureWebview(WebView wv) {
        //虽然 capturePicture() 方法已经能够获取 WebView 截图，但是到 API 19 时该方法被系统废弃掉了。取而代之的是使用 onDraw() 方法获取获取 Bitmap 对象
        Picture picture = wv.capturePicture();
        int width = picture.getWidth();
        int height = picture.getHeight();
        if (width > 0 && height > 0){
            Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            picture.draw(canvas);
            return bitmap;
        }
        return null;
    }

    private Bitmap captureWebView2(WebView webView) {
        //getScale() 方法从 API 17 开始也被系统废弃掉了。所以获取 scale 值的另一种更优雅的方式是在webview的setWebViewClient的onScaleChanged方法中获取scale
        float scale = webView.getScale();
        int width = webView.getWidth();
        int height = (int) (webView.getHeight() * scale);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        webView.draw(canvas);
        return bitmap;
    }
}
