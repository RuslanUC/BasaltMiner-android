package com.rdev.basaltminer;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LoadingView extends WebView {
    AppCompatActivity ctx;

    public LoadingView(@NonNull Context context) {
        super(context);
    }

    public LoadingView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Deprecated
    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    public void init(AppCompatActivity context) {
        ctx = context;
        getSettings().setJavaScriptEnabled(true);
        setWebViewClient(new WebViewClient());
        addJavascriptInterface(new WebInterface(), "Android");
        loadUrl("file:///android_asset/index.html");
        setBackgroundColor(Color.parseColor("#80000000"));
        setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        setLongClickable(false);
        setHapticFeedbackEnabled(false);
    }

    private class WebInterface {
        @JavascriptInterface
        public void hideLoading() {
            hide();
        }
    }

    public void hide() {
        if(ctx == null || getVisibility() == View.INVISIBLE)
            return;
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
                anim.setDuration(1000);
                startAnimation(anim);
                setVisibility(View.INVISIBLE);
            }
        });
    }

    public void show() {
        if(ctx == null || getVisibility() == View.VISIBLE)
            return;
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadUrl("file:///android_asset/index.html");
                setVisibility(View.VISIBLE);
                AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setDuration(1000);
                startAnimation(anim);
            }
        });
    }

    public void fail() {
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView web, String url) {
                web.loadUrl("javascript:fail()");
            }
        });
    }
}
