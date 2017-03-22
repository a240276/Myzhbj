package com.huasuan.myzhbj.controller.menu.news;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.huasuan.myzhbj.R;
import com.huasuan.myzhbj.utils.SPUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_news_detail)
public class NewsDetailActivity extends AppCompatActivity {
    @ViewInject(R.id.activity_news_datail_pd)
    ProgressBar mProgressBar;
    @ViewInject(R.id.activity_news_datail_wv)
    WebView mWebView;
    @ViewInject(R.id.activity_news_detail_title)
    TextView mTitle;
    private int mCruCheckIndex;
    public static final String CRUCHECKINDEX = "mCruCheckIndex";
    private SPUtils mSPUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_news_detail);
        x.view().inject(this);
        mSPUtils = new SPUtils(NewsDetailActivity.this);
        initWebView();
    }

    private void initWebView() {
        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        mProgressBar.setVisibility(View.GONE);
        mTitle.setTextSize(14);
        mTitle.setText(title);
        WebSettings settings = mWebView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        //设置支持JS
        settings.setJavaScriptEnabled(true);


        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //设置进度条
                mProgressBar.setProgress(newProgress);
            }
        });
        //监听WebView的加载进度
        mWebView.setWebViewClient(new WebViewClient() {
            //显示加载进度条
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            //加载完成隐藏进度条
            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });
        //加载URL
        mWebView.loadUrl(url);
        mCruCheckIndex = mSPUtils.getInt(CRUCHECKINDEX,2);
        changeWebViewTextSize();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //判断是否可以退回
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //设置返回
    @Event(R.id.activity_news_detail_back)
    private void setBack(View view) {
        finish();
    }

    //设置字体
    @Event(R.id.activity_news_detail_textsize)
    private void setTextSize(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewsDetailActivity.this);
        String[] items = {"超大字体", "大字体", "正常字体", "小字体", "超小字体"};

        //设置点击选中
        builder.setSingleChoiceItems(items, mCruCheckIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCruCheckIndex = which;
                mSPUtils.putInt(CRUCHECKINDEX, mCruCheckIndex);

            }
        });
        //设置点击确定以后
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeWebViewTextSize();
            }
        });
        builder.show();
        Toast.makeText(this, "字体", Toast.LENGTH_SHORT).show();

    }

    /**
     * 设置字体大小
     */
    private void changeWebViewTextSize() {
        switch (mCruCheckIndex) {
            case 0://超大字体
                mWebView.getSettings().setTextSize(WebSettings.TextSize.LARGEST);
                break;
            case 1://大字体
                mWebView.getSettings().setTextSize(WebSettings.TextSize.LARGER);
                break;
            case 2://正常字体
                mWebView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
                break;
            case 3://小字体
                mWebView.getSettings().setTextSize(WebSettings.TextSize.SMALLER);
                break;
            case 4://超小字体
                mWebView.getSettings().setTextSize(WebSettings.TextSize.SMALLEST);
                break;
            default:
                break;
        }
    }

    //设置分享
    @Event(R.id.activity_news_detail_share)
    private void share(View view) {
        Toast.makeText(this, "分享", Toast.LENGTH_SHORT).show();

    }

}
