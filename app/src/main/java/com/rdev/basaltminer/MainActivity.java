package com.rdev.basaltminer;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("ClickableViewAccessibility")
public class MainActivity extends AppCompatActivity {
    private EditText dlogin;
    private GameClient gclient;
    private WebView loadingWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.coord_layout);

        loadingWebView = findViewById(R.id.loadingWebView);
        loadingWebView.getSettings().setJavaScriptEnabled(true);
        loadingWebView.setWebViewClient(new WebViewClient());
        loadingWebView.addJavascriptInterface(new WebAppInterface(), "Android");
        loadingWebView.loadUrl("file:///android_asset/index.html");
        loadingWebView.setBackgroundColor(Color.parseColor("#80000000"));
        loadingWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        loadingWebView.setLongClickable(false);
        loadingWebView.setHapticFeedbackEnabled(false);

        String jwt = getIntent().getExtras().getString("jwt");

        ConstraintLayout layout = findViewById(R.id.main_activity);
        ImageView block = findViewById(R.id.block);
        TextView gold = findViewById(R.id.goldCount);
        TextView redstone = findViewById(R.id.redstoneCount);
        TextView level = findViewById(R.id.level);
        FrameLayout blockl = findViewById(R.id.blockLayout);
        LinearLayout gmenu = findViewById(R.id.gold_menu);
        LinearLayout lmenu = findViewById(R.id.level_menu);
        LinearLayout fmenu = findViewById(R.id.faq_menu);
        LinearLayout wmenu = findViewById(R.id.worlds_menu);
        LinearLayout tmenu = findViewById(R.id.top_menu);
        LinearLayout dmenu = findViewById(R.id.duel_menu);
        LinearLayout shmenu = findViewById(R.id.shop_menu);
        dlogin = dmenu.findViewById(R.id.dm_twitchLogin);

        Display display = getWindowManager().getDefaultDisplay();
        blockl.getLayoutParams().height = display.getHeight() / 4;
        blockl.getLayoutParams().width = display.getHeight() / 4;

        gclient = new GameClient(jwt, MainActivity.this);
        blockl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() != MotionEvent.ACTION_DOWN && motionEvent.getAction() != MotionEvent.ACTION_UP)
                    return true;
                gclient.blockTouchEvent(motionEvent);
                return true;
            }
        });

        gclient.initViews(layout, gold, redstone, level, block, blockl, loadingWebView);
        gclient.initMenus(gmenu, lmenu, fmenu, wmenu, tmenu, dmenu, shmenu);
        gclient.auth(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                    }
                });
            }
        },new Runnable() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoading(false);
                    }
                });
            }
        });
    }

    public void showGoldMenu(View v) {
        gclient.loadGoldMenu();
    }

    public void showRedstoneMenu(View v) {
        BottomSheetBehavior.from(findViewById(R.id.redstone_menu)).setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void showLevelMenu(View v) {
        gclient.loadLevelMenu();
    }

    @SuppressLint("NonConstantResourceId")
    public void updradeButton(View v) {
        switch (v.getId()) {
            case R.id.lm_levelUpgradeButton:
                gclient.upgradeLevel();
                break;
            case R.id.lm_bibaUpgradeButton:
                gclient.upgradeBiba();
                break;
            case R.id.lm_strengthUpgradeButton:
                gclient.upgradeStat(1);
                break;
            case R.id.lm_dexterityUpgradeButton:
                gclient.upgradeStat(2);
                break;
            case R.id.lm_intelligenceUpgradeButton:
                gclient.upgradeStat(3);
                break;
            case R.id.lm_resetButton:
                gclient.resetStats();
                break;
        }
    }

    public void showSettingsMenu(View v) {
        BottomSheetBehavior.from(findViewById(R.id.settings_menu)).setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @SuppressLint("NonConstantResourceId")
    public void settingsButton(View v) {
        switch (v.getId()) {
            case R.id.sm_howtoButton:
                BottomSheetBehavior.from(findViewById(R.id.howto_menu)).setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.sm_faqButton:
                gclient.loadFaqMenu();
                break;
        }
    }

    public void showWorldsMenu(View v) {
        gclient.loadWorldsMenu();
    }

    public void showTopMenu(View v) {
        gclient.loadTopMenu();
    }

    @SuppressLint("NonConstantResourceId")
    public void topButton(View v) {
        switch (v.getId()) {
            case R.id.tm_goldButton:
                gclient.loadTop(0);
                break;
            case R.id.tm_levelButton:
                gclient.loadTop(1);
                break;
            case R.id.tm_redstoneButton:
                gclient.loadTop(2);
                break;
            case R.id.tm_winsButton:
                gclient.loadTop(3);
                break;
            case R.id.tm_bibaButton:
                gclient.loadTop(4);
                break;
        }

        Drawable defD = ResourcesCompat.getDrawable(getResources(), R.drawable.button, null);
        findViewById(R.id.tm_goldButton).setBackground(defD);
        findViewById(R.id.tm_levelButton).setBackground(defD);
        findViewById(R.id.tm_redstoneButton).setBackground(defD);
        findViewById(R.id.tm_winsButton).setBackground(defD);
        findViewById(R.id.tm_bibaButton).setBackground(defD);
        LayerDrawable drawable = (LayerDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.menu_bg, null);
        ((GradientDrawable) drawable.findDrawableByLayerId(R.id.menu_bg_item)).setColor(Color.parseColor("#effdef"));
        v.setBackground(drawable);
    }

    public void showDuelMenu(View v) {
        gclient.loadDuelMenu();
    }

    @SuppressLint("NonConstantResourceId")
    public void duelButton(View v) {
        switch (v.getId()) {
            case R.id.dm_acceptRandom:
                gclient.duelSet(1);
                break;
            case R.id.dm_autoAccept:
                gclient.duelSet(2);
                break;
            case R.id.dm_randomButton:
                gclient.sendRandomDuel();
                break;
            case R.id.dm_sendRequest:
                gclient.sendDuel(dlogin.getText().toString());
                dlogin.setText("");
                break;
        }
    }

    public void showShopMenu(View v) {
        gclient.loadShopMenu();
    }

    private class WebAppInterface {
        @JavascriptInterface
        public void hideLoading() {
            MainActivity.this.hideLoading();
        }
    }

    private void hideLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
                anim.setDuration(1000);
                loadingWebView.startAnimation(anim);
                loadingWebView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void showLoading(boolean success) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingWebView.loadUrl("file:///android_asset/index.html");
                loadingWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView web, String url) {
                        if(!success)
                            web.loadUrl("javascript:fail()");
                    }
                });
                loadingWebView.setVisibility(View.VISIBLE);
                AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setDuration(1000);
                loadingWebView.startAnimation(anim);
            }
        });
    }

    private void showLoading() {
        showLoading(true);
    }
}