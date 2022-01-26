package com.rdev.basaltminer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.NOTIFICATION_SERVICE;

public class GameClient {
    private final String jwt;
    private final AppCompatActivity ctx;
    private int x, y;
    private boolean isTouched = false;
    private int d = 0;
    private final double k;
    private double bk = 1;
    private long _bk;
    private boolean offlineMode;
    private offline offline;

    private long count;
    private int world;
    private int block;
    private int breakTime;
    private String gold;
    private String redstone;
    private int level;
    private String levelCost;
    private int biba;
    private String bibaCost = "2500";
    private int strengthLevel;
    private int dexterityLevel;
    private int intelligenceLevel;
    private String discost = "1000";
    private int statpoints;
    private JSONArray top;
    private int wins;
    private int duelTotal;
    private boolean autoAccept;
    private boolean acceptRandom;
    private JSONArray requests;
    private JSONArray myrequests;
    private JSONArray results;
    private JSONArray streamers;

    private ConstraintLayout gameCL;
    private TextView goldTV;
    private TextView lvlTV;
    private TextView redstoneTV;
    private ImageView blockIV;
    private WebView loadingWebView;

    private LinearLayout goldMenu;
    private TextView gm_boostInfo;
    private TextView gm_streamersInfo;
    private ScrollView lm_scroll;
    private BottomSheetBehavior<View> lm_behaviour;
    private TextView lm_level;
    private TextView lm_levelCost;
    private TextView lm_biba;
    private TextView lm_bibaCost;
    private TextView lm_strength;
    private TextView lm_dexterity;
    private TextView lm_intelligence;
    private TextView lm_resetCost;
    private Button lm_strengthBtn;
    private Button lm_dexterityBtn;
    private Button lm_intelligenceBtn;
    private TextView lm_statsText;
    private LinearLayout faqMenu;
    private LinearLayout faqContainer;
    private LinearLayout worldsContainer;
    private BottomSheetBehavior<View> wm_behaviour;
    private ScrollView wm_scroll;
    private TextView topContainer;
    private BottomSheetBehavior<View> tm_behaviour;
    private ScrollView tm_scroll;
    private BottomSheetBehavior<View> dm_behaviour;
    private ScrollView dm_scroll;
    private LinearLayout dm_reqContainer;
    private LinearLayout dm_mreqContainer;
    private LinearLayout dm_resContainer;
    private TextView dm_duelCount;
    private Switch dm_acceptRandom;
    private Switch dm_autoAccept;
    private ScrollView sm_scroll;
    private LinearLayout sm_container;
    private BottomSheetBehavior<View> sm_behaviour;

    public GameClient(String jwt, AppCompatActivity context) {
        this.jwt = jwt;
        this.offlineMode = jwt == null;
        this.ctx = context;
        if (offlineMode)
            this.offline = new offline(ctx);
        else
            checkUpdates();
        this.k = 128.0 / (context.getWindowManager().getDefaultDisplay().getHeight() / 4);
    }

    public void initViews(ConstraintLayout gameCL, TextView goldTV, TextView redstoneTV, TextView lvlTV, ImageView blockIV, FrameLayout blockFL, WebView loadingWebView) {
        this.gameCL = gameCL;
        this.goldTV = goldTV;
        this.redstoneTV = redstoneTV;
        this.lvlTV = lvlTV;
        this.blockIV = blockIV;
        this.loadingWebView = loadingWebView;
    }

    public void initMenus(LinearLayout goldMenu, LinearLayout levelMenu, LinearLayout faqMenu, LinearLayout worldsMenu, LinearLayout topMenu, LinearLayout duelMenu, LinearLayout shopMenu) {
        this.goldMenu = goldMenu;
        gm_boostInfo = goldMenu.findViewById(R.id.gm_boostInfo);
        gm_streamersInfo = goldMenu.findViewById(R.id.gm_streamersInfo);

        lm_scroll = levelMenu.findViewById(R.id.lm_scroll);
        lm_behaviour = BottomSheetBehavior.from(levelMenu);
        lm_behaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull @NotNull View view, int i) {
                if (i == 4) {
                    lm_scroll.post(new Runnable() {
                        @Override
                        public void run() {
                            lm_scroll.scrollTo(0, 0);
                        }
                    });
                }
            }

            @Override
            public void onSlide(@NonNull @NotNull View view, float v) {

            }
        });
        lm_level = levelMenu.findViewById(R.id.lm_level);
        lm_levelCost = levelMenu.findViewById(R.id.lm_levelCost);
        lm_biba = levelMenu.findViewById(R.id.lm_biba);
        lm_bibaCost = levelMenu.findViewById(R.id.lm_bibaCost);
        lm_strength = levelMenu.findViewById(R.id.lm_strength);
        lm_strengthBtn = levelMenu.findViewById(R.id.lm_strengthUpgradeButton);
        lm_dexterity = levelMenu.findViewById(R.id.lm_dexterity);
        lm_dexterityBtn = levelMenu.findViewById(R.id.lm_dexterityUpgradeButton);
        lm_intelligence = levelMenu.findViewById(R.id.lm_intelligence);
        lm_intelligenceBtn = levelMenu.findViewById(R.id.lm_intelligenceUpgradeButton);
        lm_resetCost = levelMenu.findViewById(R.id.lm_resetCost);
        lm_statsText = levelMenu.findViewById(R.id.lm_statsText);

        this.faqMenu = faqMenu;
        faqContainer = faqMenu.findViewById(R.id.fm_container);

        worldsContainer = worldsMenu.findViewById(R.id.wm_container);
        wm_scroll = worldsMenu.findViewById(R.id.wm_scroll);
        wm_behaviour = BottomSheetBehavior.from(worldsMenu);
        wm_behaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull @NotNull View view, int i) {
                if (i == 4) {
                    wm_scroll.post(new Runnable() {
                        @Override
                        public void run() {
                            wm_scroll.scrollTo(0, 0);
                        }
                    });
                }
            }

            @Override
            public void onSlide(@NonNull @NotNull View view, float v) {

            }
        });

        topContainer = topMenu.findViewById(R.id.tm_container);
        tm_scroll = topMenu.findViewById(R.id.tm_scroll);
        tm_behaviour = BottomSheetBehavior.from(topMenu);
        tm_behaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull @NotNull View view, int i) {
                if (i == 4) {
                    tm_scroll.post(new Runnable() {
                        @Override
                        public void run() {
                            tm_scroll.scrollTo(0, 0);
                        }
                    });
                }
            }

            @Override
            public void onSlide(@NonNull @NotNull View view, float v) {

            }
        });

        dm_scroll = duelMenu.findViewById(R.id.dm_scroll);
        dm_reqContainer = duelMenu.findViewById(R.id.dm_reqContainer);
        dm_mreqContainer = duelMenu.findViewById(R.id.dm_mreqContainer);
        dm_resContainer = duelMenu.findViewById(R.id.dm_resContainer);
        dm_acceptRandom = duelMenu.findViewById(R.id.dm_acceptRandom);
        dm_autoAccept = duelMenu.findViewById(R.id.dm_autoAccept);
        dm_duelCount = duelMenu.findViewById(R.id.dm_duelCount);
        EditText dm_twitchLogin = duelMenu.findViewById(R.id.dm_twitchLogin);
        dm_behaviour = BottomSheetBehavior.from(duelMenu);
        dm_behaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull @NotNull View view, int i) {
                if (i == 4) {
                    dm_scroll.post(new Runnable() {
                        @Override
                        public void run() {
                            dm_scroll.scrollTo(0, 0);
                        }
                    });
                }
            }

            @Override
            public void onSlide(@NonNull @NotNull View view, float v) {

            }
        });

        sm_scroll = shopMenu.findViewById(R.id.sm_scroll);
        sm_container = shopMenu.findViewById(R.id.sm_container);
        sm_behaviour = BottomSheetBehavior.from(shopMenu);
        sm_behaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull @NotNull View view, int i) {
                if (i == 4) {
                    sm_scroll.post(new Runnable() {
                        @Override
                        public void run() {
                            sm_scroll.scrollTo(0, 0);
                        }
                    });
                }
            }

            @Override
            public void onSlide(@NonNull @NotNull View view, float v) {

            }
        });
    }

    public void blockTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isTouched)
                return;
            if (checkTouch(event))
                isTouched = true;
            else
                return;
            _bk = System.currentTimeMillis();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            d = 0;
            isTouched = false;
            clearD();
            return;
        }
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!isTouched) {
                    d = 0;
                    clearD();
                    return;
                }
                d += 1;
                if (d > 10) {
                    d = 0;
                    if (bk == 1 && System.currentTimeMillis() - _bk > breakTime) {
                        bk = breakTime / (double) (System.currentTimeMillis() - _bk);
                    }
                    blockBreaked();
                    clearD();
                    return;
                }
                drawD();
                handler.postDelayed(this, (long) (breakTime * bk / 10));
            }
        };
        handler.postDelayed(runnable, (long) (breakTime * bk / 10));
    }

    private void makeRequest(String path, YEPRunnable doneCallback, YEPRunnable failCallback) {
        if (this.offlineMode) {
            String data;
            try {
                data = offline.processOfflineModeRequest(path);
            } catch (JSONException e) {
                return;
            }
            doneCallback.init(data).run();
            return;
        }
        if (!path.contains("auth")) {
            if (path.contains("?"))
                path += "&c=" + count;
            else
                path += "?c=" + count;
        }
        String finalPath = path;
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build();
                Request request = new Request.Builder()
                        .url(CONFIG.host + finalPath)
                        .addHeader("x-extension-jwt", jwt)
                        .build();

                Response response;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                if (response.code() != 200) {
                    failCallback.init(response).run();
                } else {
                    String resp = "";
                    try {
                        resp = response.body().string();
                    } catch (IOException e) {
                        return;
                    }
                    doneCallback.init(resp).run();
                }
            }
        }).start();
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void blockBreaked() {
        makeRequest("mine/reward", new YEPRunnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject(sresp);
                    JSONObject upd = json.getJSONObject("update");
                    block = json.getInt("block");
                    gold = upd.getString("money");
                    redstone = upd.getString("points");
                    breakTime = json.getInt("time");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setBlock();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[6] Не удалось сделать запрос, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void drawD() {
        try {
            Bitmap point = Bitmap.createScaledBitmap(drawableToBitmap(Drawable.createFromStream(ctx.getAssets().open("point1.png"), null)), 16, 16, false);
            Bitmap db = Bitmap.createScaledBitmap(drawableToBitmap(Drawable.createFromStream(ctx.getAssets().open("blocks/d" + d + ".png"), null)), 128, 128, false);
            Bitmap bl = Bitmap.createScaledBitmap(drawableToBitmap(Drawable.createFromStream(ctx.getAssets().open("blocks/" + block + ".png"), null)), 128, 128, false);
            bl = bl.copy(bl.getConfig(), true);
            Canvas canv = new Canvas(bl);
            canv.drawBitmap(point, x, y, null);
            canv.drawBitmap(db, 0, 0, null);
            blockIV.setBackground(new BitmapDrawable(ctx.getResources(), bl));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearD() {
        try {
            Bitmap point = Bitmap.createScaledBitmap(drawableToBitmap(Drawable.createFromStream(ctx.getAssets().open("point1.png"), null)), 16, 16, false);
            Bitmap bl = Bitmap.createScaledBitmap(drawableToBitmap(Drawable.createFromStream(ctx.getAssets().open("blocks/" + block + ".png"), null)), 128, 128, false);
            bl = bl.copy(bl.getConfig(), true);
            Canvas canv = new Canvas(bl);
            canv.drawBitmap(point, x, y, null);
            blockIV.setBackground(new BitmapDrawable(ctx.getResources(), bl));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double getRandomNumber() {
        return (Math.random() * (0.85 - 0.15)) + 0.15;
    }

    private void setBlock() {
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap point = Bitmap.createScaledBitmap(drawableToBitmap(Drawable.createFromStream(ctx.getAssets().open("point1.png"), null)), 16, 16, false);
                    Bitmap bl = Bitmap.createScaledBitmap(drawableToBitmap(Drawable.createFromStream(ctx.getAssets().open("blocks/" + block + ".png"), null)), 128, 128, false);
                    bl = bl.copy(bl.getConfig(), true);
                    x = (int) (getRandomNumber() * bl.getWidth());
                    y = (int) (getRandomNumber() * bl.getWidth());
                    Canvas canv = new Canvas(bl);
                    canv.drawBitmap(point, x, y, null);
                    blockIV.setBackground(new BitmapDrawable(ctx.getResources(), bl));
                } catch (IOException e) {
                    Toast.makeText(ctx, "[1] Неизвестный блок: " + block, Toast.LENGTH_SHORT).show();
                    ctx.finish();
                }
                goldTV.setText(gold);
                redstoneTV.setText(redstone);
            }
        });
    }

    private void setBackground() {
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    gameCL.setBackground(Drawable.createFromStream(ctx.getAssets().open("backgrounds/" + world + ".png"), null));
                } catch (IOException e) {
                    Toast.makeText(ctx, "[0] Неизвестный фон: " + world, Toast.LENGTH_SHORT).show();
                    ctx.finish();
                }
            }
        });
    }

    private void setValues() {
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                goldTV.setText(gold);
                redstoneTV.setText(redstone);
                lvlTV.setText(String.valueOf(level));
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateLevelMenu() {
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lm_level.setText("Уровень " + level);
                lm_levelCost.setText(levelCost);
                lm_biba.setText("Длина бибы: " + biba);
                lm_bibaCost.setText(bibaCost);
                lm_strength.setText(strengthLevel + " Сила");
                lm_dexterity.setText(dexterityLevel + " Ловкость");
                lm_intelligence.setText(intelligenceLevel + " Интеллект");
                lm_statsText.setText(((statpoints > 0) ? "(" + statpoints + ") Характеристики:" : "Характеристики:"));
                if (statpoints > 0) {
                    lm_strengthBtn.setVisibility(View.VISIBLE);
                    lm_dexterityBtn.setVisibility(View.VISIBLE);
                    lm_intelligenceBtn.setVisibility(View.VISIBLE);
                } else {
                    lm_strengthBtn.setVisibility(View.INVISIBLE);
                    lm_dexterityBtn.setVisibility(View.INVISIBLE);
                    lm_intelligenceBtn.setVisibility(View.INVISIBLE);
                }
                lm_resetCost.setText(discost);
            }
        });
    }

    private boolean checkTouch(MotionEvent ev) {
        double xk = Math.min((int) ev.getX() * k, x + 18 * k) / Math.max((int) ev.getX() * k, x + 18 * k);
        double yk = Math.min((int) ev.getY() * k, y + 18 * k) / Math.max((int) ev.getY() * k, y + 18 * k);

        return xk >= 0.85 && yk >= 0.85;
    }

    private void changeWorld(int w) {
        showLoading();
        makeRequest("world/select?id=" + w, new YEPRunnable() {
            @Override
            public void run() {
                try {
                    Log.d("rdev", sresp);
                    JSONObject json = new JSONObject(sresp);
                    world = w;
                    breakTime = json.getInt("time");
                    block = json.getInt("block");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setBackground();
                setBlock();
                wm_behaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
                hideLoading();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[19] Не изменить шахту, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
                hideLoading();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setDuelsG() {
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dm_acceptRandom.setChecked(acceptRandom);
                dm_autoAccept.setChecked(autoAccept);
                dm_duelCount.setText("Всего побед: " + wins + "/" + duelTotal);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setDuels() {
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dm_reqContainer.removeAllViews();
                dm_mreqContainer.removeAllViews();
                dm_resContainer.removeAllViews();
                for (int i = 0; i < requests.length(); i++) {
                    try {
                        JSONArray arr = requests.getJSONArray(i);
                        int uid = arr.getInt(0);
                        LinearLayout layout = new LinearLayout(ctx);
                        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
                        layout.setOrientation(LinearLayout.VERTICAL);
                        TextView tv = new TextView(ctx);
                        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        tv.setText(arr.getString(1) + " (" + arr.getString(2) + ")");
                        tv.setTextColor(Color.parseColor("#000000"));
                        tv.setTextSize(15);
                        LinearLayout lay2 = new LinearLayout(ctx);
                        lay2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        Button btn1 = new Button(ctx);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.weight = 1;
                        btn1.setLayoutParams(params);
                        btn1.setBackground(ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.button, null));
                        btn1.setText("Принять");
                        int finalI = i;
                        btn1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                acceptDuel(uid, finalI);
                            }
                        });
                        Button btn2 = new Button(ctx);
                        btn2.setLayoutParams(params);
                        btn2.setBackground(ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.button, null));
                        btn2.setText("Отклонить");
                        btn2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                declineDuel(uid, finalI, 0);
                            }
                        });
                        lay2.addView(btn1);
                        lay2.addView(btn2);
                        layout.addView(tv);
                        layout.addView(lay2);
                        dm_reqContainer.addView(layout);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < myrequests.length(); i++) {
                    try {
                        JSONArray arr = myrequests.getJSONArray(i);
                        int uid = arr.getInt(0);
                        LinearLayout layout = new LinearLayout(ctx);
                        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
                        layout.setOrientation(LinearLayout.VERTICAL);
                        TextView tv = new TextView(ctx);
                        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        tv.setText(arr.getString(1) + " (" + arr.getString(2) + ")");
                        tv.setTextColor(Color.parseColor("#000000"));
                        tv.setTextSize(15);
                        LinearLayout lay2 = new LinearLayout(ctx);
                        lay2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.weight = 1;
                        Button btn = new Button(ctx);
                        btn.setLayoutParams(params);
                        btn.setBackground(ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.button, null));
                        btn.setText("Отклонить");
                        int finalI = i;
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                declineDuel(uid, finalI, 1);
                            }
                        });
                        lay2.addView(btn);
                        layout.addView(tv);
                        layout.addView(lay2);
                        dm_mreqContainer.addView(layout);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < results.length(); i++) {
                    try {
                        JSONArray arr = results.getJSONArray(i);
                        LinearLayout layout = new LinearLayout(ctx);
                        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setBackgroundColor(Color.parseColor((arr.getBoolean(3)) ? "#8000ff00" : "#80ff0000"));
                        TextView tv = new TextView(ctx);
                        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        tv.setText(arr.getString(1) + " vs " + arr.getString(0) + " (" + arr.getString(2) + ")");
                        tv.setTextColor(Color.parseColor("#000000"));
                        tv.setTextSize(15);
                        layout.addView(tv);
                        dm_resContainer.addView(layout);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void acceptDuel(int uid, int idx) {
        showLoading();
        makeRequest("duel/accept?id=" + uid, new YEPRunnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject(sresp);
                    int code = json.getInt("code");
                    if (code != 5) {
                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ctx, "[21] Не удалось отправить запрос! Код: " + code, Toast.LENGTH_SHORT).show();
                                if (code == 2 || code == 3) {
                                    Toast.makeText(ctx, "[21] У оппонента слишком много дуелей за последнее время!", Toast.LENGTH_SHORT).show();
                                } else if (code == 4) {
                                    Toast.makeText(ctx, "[21] У вас слишком много дуелей с этим игроком!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        hideLoading();
                        return;
                    }

                    duelTotal++;
                    if (json.getJSONArray("result").getBoolean(3)) {
                        wins++;
                    }
                    requests.remove(idx);
                    results.put(json.getJSONArray("result"));
                    setDuels();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideLoading();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[22] Не удалось отправить запрос, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
                hideLoading();
            }
        });
    }

    private void declineDuel(int uid, int idx, int a) {
        showLoading();
        makeRequest("duel/decline?id=" + uid, new YEPRunnable() {
            @Override
            public void run() {
                if (a == 0)
                    requests.remove(idx);
                else
                    myrequests.remove(idx);
                setDuels();
                hideLoading();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[23] Не удалось отменить запрос, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
                hideLoading();
            }
        });
    }

    private int getSizeInDP(int dp) {
        return (int) (dp * ctx.getApplicationContext().getResources().getDisplayMetrics().density + 0.5f);
    }

    @SuppressLint("SetTextI18n")
    private void setStreamersList() {
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sm_container.removeAllViews();

                for (int i = 0; i < streamers.length(); i++) {
                    int finalI = i;
                    try {
                        LinearLayout mlay = new LinearLayout(ctx);
                        LinearLayout.LayoutParams t = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
                        t.setMargins(0, 0, 0, getSizeInDP(25));
                        mlay.setLayoutParams(t);
                        mlay.setOrientation(LinearLayout.VERTICAL);
                        mlay.setBackground(ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.menu_bg, null));
                        mlay.setPadding(getSizeInDP(25), 0, getSizeInDP(25), 0);

                        LinearLayout tlay = new LinearLayout(ctx);
                        tlay.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        TextView streamerTV = new TextView(ctx);
                        streamerTV.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        streamerTV.setText(streamers.getJSONArray(finalI).getString(2) + ((streamers.getJSONArray(finalI).getInt(1) != 0) ? " " + streamers.getJSONArray(finalI).getString(1) : ""));
                        streamerTV.setTextColor(Color.parseColor("#000000"));
                        streamerTV.setTextSize(20);
                        tlay.addView(streamerTV);

                        TextView earnTV = new TextView(ctx);
                        t = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        t.setMargins(getSizeInDP(10), 0, 0, 0);
                        earnTV.setLayoutParams(t);
                        earnTV.setText(((!streamers.getJSONArray(finalI).getString(5).equals("0")) ? streamers.getJSONArray(finalI).getString(5) + "->" + streamers.getJSONArray(finalI).getString(6) : "0->" + streamers.getJSONArray(finalI).getString(6)) + "/мин");
                        earnTV.setTextColor(Color.parseColor("#000000"));
                        earnTV.setTextSize(10);
                        tlay.addView(earnTV);

                        View emptyV = new View(ctx);
                        t = new LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
                        t.weight = 1;
                        emptyV.setLayoutParams(t);
                        tlay.addView(emptyV);

                        ImageView goldIVl = new ImageView(ctx);
                        t = new LinearLayout.LayoutParams(getSizeInDP(24), getSizeInDP(24));
                        t.gravity = Gravity.END;
                        goldIVl.setLayoutParams(t);
                        goldIVl.setImageResource(R.drawable.gold);
                        tlay.addView(goldIVl);

                        TextView goldTVl = new TextView(ctx);
                        t = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        t.gravity = Gravity.END;
                        goldTVl.setLayoutParams(t);
                        goldIVl.setPadding(5, 0, 10, 0);
                        goldTVl.setText(streamers.getJSONArray(finalI).getString(3));
                        goldTVl.setTextColor(Color.parseColor("#000000"));
                        goldTVl.setTextSize(20);
                        tlay.addView(goldTVl);

                        ImageView redstoneIV = new ImageView(ctx);
                        t = new LinearLayout.LayoutParams(getSizeInDP(24), getSizeInDP(24));
                        t.gravity = Gravity.END;
                        t.setMargins(10, 0, 0, 0);
                        redstoneIV.setLayoutParams(t);
                        redstoneIV.setImageResource(R.drawable.redstone);
                        tlay.addView(redstoneIV);

                        TextView redstoneTV = new TextView(ctx);
                        t = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        t.gravity = Gravity.END;
                        redstoneTV.setLayoutParams(t);
                        redstoneIV.setPadding(5, 0, 10, 0);
                        redstoneTV.setText(streamers.getJSONArray(finalI).getString(4));
                        redstoneTV.setTextColor(Color.parseColor("#000000"));
                        redstoneTV.setTextSize(20);
                        tlay.addView(redstoneTV);

                        mlay.addView(tlay);

                        Button buyBtn = new Button(ctx);
                        buyBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        buyBtn.setBackgroundResource(R.drawable.button);
                        buyBtn.setText("Купить");
                        buyBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                buyStreamer(finalI);
                            }
                        });

                        mlay.addView(buyBtn);

                        sm_container.addView(mlay);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void hideLoading() {
        if (offlineMode)
            return;
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadingWebView.getVisibility() == View.INVISIBLE)
                    return;
                AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
                anim.setDuration(500);
                loadingWebView.startAnimation(anim);
                loadingWebView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void showLoading() {
        if (offlineMode)
            return;
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadingWebView.getVisibility() == View.VISIBLE)
                    return;
                loadingWebView.loadUrl("file:///android_asset/index.html");
                loadingWebView.setVisibility(View.VISIBLE);
                AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setDuration(500);
                loadingWebView.startAnimation(anim);
            }
        });
    }

    private void checkUpdates() {
        if (!CONFIG.check_updates)
            return;
        SharedPreferences prefs = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (!prefs.getBoolean("askForUpdates", true))
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient.Builder()
                        .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                        .build();

                Request request = new Request.Builder()
                        .url("https://api.github.com/repos/" + CONFIG.github_repo + "/releases")
                        .build();

                Response response;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                JSONArray releases;
                try {
                    releases = new JSONArray(response.body().string());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return;
                }
                List<bmVersion> versions = new ArrayList<>();
                for (int i = 0; i < releases.length(); i++) {
                    try {
                        JSONObject ver = releases.getJSONObject(i);
                        JSONObject apk = ver.getJSONArray("assets").getJSONObject(0);
                        bmVersion bmv = new bmVersion(ver.getString("tag_name"), ver.getString("body"), ver.getBoolean("prerelease"), apk.getString("browser_download_url"), apk.getLong("size"));
                        if (bmv.isBeta() && !prefs.getBoolean("askForBetaUpdates", false))
                            continue;
                        if (bmv.isNewerThanCurrent())
                            versions.add(bmv);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        continue;
                    }
                }
                if (versions.size() == 0)
                    return;
                Collections.sort(versions);
                bmVersion lastVersion = versions.get(versions.size() - 1);
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lastVersion.buildWindow(prefs).show();
                    }
                });
            }
        }).start();
    }

    public void auth(Runnable doneRunnable, Runnable failRunnable) {
        makeRequest("ext/auth", new YEPRunnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject(sresp);
                    JSONObject upd = json.getJSONObject("update");
                    level = json.getInt("level");
                    world = json.getInt("world");
                    block = json.getInt("block");
                    gold = upd.getString("money");
                    redstone = upd.getString("points");
                    count = json.getLong("count");
                    breakTime = json.getInt("time");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setBackground();
                setBlock();
                setValues();
                doneRunnable.run();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[5] Не удалось авторизоваться, код: " + resp.code(), Toast.LENGTH_LONG).show();

                        Notification notification = new NotificationCompat.Builder(ctx)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Произошла ошибка!")
                                .setContentText("Код ошибки: 5, код ответа: " + resp.code())
                                .build();

                        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(1, notification);
                    }
                });
                failRunnable.run();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void loadGoldMenu() {
        showLoading();
        makeRequest("upgrade/income", new YEPRunnable() {
            @Override
            public void run() {
                double total = 1.0;
                String income = "0";
                JSONArray boost = new JSONArray();
                try {
                    JSONObject json = new JSONObject(sresp);
                    boost = json.getJSONArray("boost");
                    total = json.getDouble("total");
                    income = json.getString("income");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray finalBoost = boost;
                String finalIncome = income;
                double finalTotal = total;
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BottomSheetBehavior.from(goldMenu).setState(BottomSheetBehavior.STATE_EXPANDED);
                        String btext = "";
                        for (int i = 0; i < finalBoost.length(); i++) {
                            try {
                                JSONArray b = finalBoost.getJSONArray(i);
                                btext += b.getString(0) + ": x" + b.getDouble(1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            btext += "\n";
                        }
                        btext += "Итоговый множитель: x" + finalTotal;
                        gm_boostInfo.setText(btext);
                        gm_streamersInfo.setText("Стримеры приносят: \n" + finalIncome + " золота/мин.");
                    }
                });
                hideLoading();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[7] Не удалось получить данные, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
                hideLoading();
            }
        });
    }

    public void loadLevelMenu() {
        showLoading();
        makeRequest("upgrade/level", new YEPRunnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject(sresp);
                    level = json.getJSONArray("level").getInt(0);
                    levelCost = json.getJSONArray("level").getString(1);
                    biba = json.getJSONArray("biba").getInt(0);
                    bibaCost = json.getJSONArray("biba").getString(1);
                    JSONArray stats = json.getJSONArray("stats");
                    strengthLevel = stats.getJSONArray(0).getInt(2);
                    dexterityLevel = stats.getJSONArray(1).getInt(2);
                    intelligenceLevel = stats.getJSONArray(2).getInt(2);
                    discost = json.getString("discost");
                    statpoints = json.getInt("statpoints");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lm_behaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                });
                updateLevelMenu();
                hideLoading();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[8] Не удалось получить данные, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
                hideLoading();
            }
        });
    }

    public void upgradeLevel() {
        showLoading();
        makeRequest("upgrade/levelup", new YEPRunnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject(sresp);
                    int code = json.getInt("code");
                    if (code != 1) {
                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ctx, "[10] Не удалось повысить уровень! Код: " + code, Toast.LENGTH_SHORT).show();
                                if (code == 2) {
                                    Toast.makeText(ctx, "[10] Недостаточно золота!", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                        hideLoading();
                        return;
                    }
                    level = json.getJSONArray("cost").getInt(0);
                    levelCost = json.getJSONArray("cost").getString(1);
                    breakTime = json.getInt("time");
                    JSONObject upd = json.getJSONObject("update");
                    gold = upd.getString("money");
                    redstone = upd.getString("points");
                    statpoints += 1;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setValues();
                updateLevelMenu();
                hideLoading();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[9] Не удалось повысить уровень, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
                hideLoading();
            }
        });
    }

    public void upgradeBiba() {
        showLoading();
        makeRequest("upgrade/bibaup", new YEPRunnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject(sresp);
                    int code = json.getInt("code");
                    if (code != 1) {
                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ctx, "[12] Не удалось повысить уровень! Код: " + code, Toast.LENGTH_SHORT).show();
                                if (code == 2) {
                                    Toast.makeText(ctx, "[12] Недостаточно редстоуна!", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                        hideLoading();
                        return;
                    }
                    biba = json.getJSONArray("cost").getInt(0);
                    bibaCost = json.getJSONArray("cost").getString(1);
                    JSONObject upd = json.getJSONObject("update");
                    gold = upd.getString("money");
                    redstone = upd.getString("points");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setValues();
                updateLevelMenu();
                hideLoading();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[11] Не удалось повысить уровень, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
                hideLoading();
            }
        });
    }

    public void upgradeStat(int stat) {
        showLoading();
        makeRequest("upgrade/statadd?id=" + stat, new YEPRunnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject(sresp);
                    int code = json.getInt("code");
                    if (code != 1) {
                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ctx, "[14] Не удалось повысить уровень! Код: " + code, Toast.LENGTH_SHORT).show();
                                if (code == 2) {
                                    Toast.makeText(ctx, "[14] Недостаточно очков характеристик!", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                        hideLoading();
                        return;
                    }
                    JSONArray stats = json.getJSONArray("stats");
                    strengthLevel = stats.getJSONArray(0).getInt(2);
                    dexterityLevel = stats.getJSONArray(1).getInt(2);
                    intelligenceLevel = stats.getJSONArray(2).getInt(2);
                    statpoints = json.getInt("statpoints");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateLevelMenu();
                hideLoading();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[13] Не удалось повысить уровень, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
                hideLoading();
            }
        });
    }

    public void resetStats() {
        showLoading();
        makeRequest("upgrade/statdis", new YEPRunnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject(sresp);
                    int code = json.getInt("code");
                    if (code != 1) {
                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ctx, "[16] Не удалось сбросить характеристики! Код: " + code, Toast.LENGTH_SHORT).show();
                                if (code == 2) {
                                    Toast.makeText(ctx, "[16] Нечего сбрасывать!", Toast.LENGTH_SHORT).show();
                                } else if (code == 3) {
                                    Toast.makeText(ctx, "[16] Недостаточно редстоуна!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        hideLoading();
                        return;
                    }
                    JSONObject upd = json.getJSONObject("update");
                    gold = upd.getString("money");
                    redstone = upd.getString("points");
                    statpoints = json.getInt("statpoints");
                    strengthLevel = 0;
                    dexterityLevel = 0;
                    intelligenceLevel = 0;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setValues();
                updateLevelMenu();
                hideLoading();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[15] Не удалось сбросить характеристики, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
                hideLoading();
            }
        });
    }

    public void loadFaqMenu() {
        showLoading();
        makeRequest("upgrade/faq", new YEPRunnable() {
            @Override
            public void run() {
                ArrayList<String> faq = new ArrayList<>();
                try {
                    JSONArray json = new JSONArray(sresp);
                    for (int i = 0; i <= json.length() - 1; i++) {
                        faq.add(json.getJSONArray(i).getString(0) + " - " + json.getJSONArray(i).getString(1));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        faqContainer.removeAllViews();
                        for (String l : faq) {
                            LinearLayout layout = new LinearLayout(ctx);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 25, 0, 0);
                            params.gravity = Gravity.CENTER_HORIZONTAL;
                            layout.setLayoutParams(params);
                            layout.setPadding(20, 0, 20, 0);
                            layout.setBackground(ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.menu_bg, null));

                            TextView textView = new TextView(ctx);
                            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            textView.setLayoutParams(params);
                            textView.setPadding(40, 0, 40, 0);
                            textView.setText(l);
                            textView.setTextColor(Color.parseColor("#000000"));
                            textView.setTextSize(20);

                            layout.addView(textView);
                            faqContainer.addView(layout);
                        }
                        BottomSheetBehavior.from(faqMenu).setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                });
                hideLoading();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[17] Не удалось получить данные, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
                hideLoading();
            }
        });
    }

    public void loadWorldsMenu() {
        showLoading();
        makeRequest("world/list", new YEPRunnable() {
            @Override
            public void run() {
                Map<String, String> wlist = new HashMap<>();
                ArrayList<String> wl = new ArrayList<>();
                try {
                    JSONArray json = new JSONArray(sresp);
                    for (int i = 0; i < json.length(); i++) {
                        Object itm = json.get(i);
                        if (itm instanceof JSONArray) {
                            wlist.put(((JSONArray) itm).getString(1), ((JSONArray) itm).getString(2));
                            wl.add(((JSONArray) itm).getString(1));
                        } else {
                            wlist.put("Требуется " + itm + " уровень!", "67323c");
                            wl.add("Требуется " + itm + " уровень!");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        worldsContainer.removeAllViews();
                        int wid = 1;
                        for (String l : wl) {
                            Button button = new Button(ctx);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 15, 0, 0);
                            button.setLayoutParams(params);
                            button.setPadding(0, 15, 0, 0);
                            button.setText(l);
                            button.setTextColor(Color.parseColor("#000000"));
                            button.setTextSize(20);
                            LayerDrawable drawable = (LayerDrawable) ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.menu_bg, null);
                            ((GradientDrawable) drawable.findDrawableByLayerId(R.id.menu_bg_item)).setColor(Color.parseColor("#" + wlist.get(l)));
                            button.setBackground(drawable);
                            if (!wlist.get(l).equals("67323c")) {
                                int finalWid = wid;
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        changeWorld(finalWid);
                                    }
                                });
                            }

                            worldsContainer.addView(button);
                            wid++;
                        }
                        wm_behaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                });
                hideLoading();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[18] Не удалось получить данные, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
                hideLoading();
            }
        });
    }

    public void loadTopMenu() {
        showLoading();
        makeRequest("top/list", new YEPRunnable() {
            @Override
            public void run() {
                try {
                    top = new JSONObject(sresp).getJSONArray("top");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loadTop(0);
                hideLoading();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[20] Не удалось получить данные, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
                hideLoading();
            }
        });
    }

    public void loadTop(int idx) {
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text = "";
                try {
                    JSONArray t = top.getJSONArray(idx);
                    for (int i = 0; i < t.length(); i++) {
                        text += (i + 1) + ". " + t.getJSONArray(i).getString(0) + " - " + t.getJSONArray(i).getString(1) + "\n";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                topContainer.setText(text);
                tm_behaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    public void loadDuelMenu() {
        if (offlineMode) {
            Toast.makeText(ctx, "В оффлайн-режиме дуели отключены.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (level < 10) {
            Toast.makeText(ctx, "Уровень должен быть больше 10!", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        makeRequest("duel/menu", new YEPRunnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject(sresp);
                    wins = json.getInt("wins");
                    duelTotal = json.getInt("count");
                    autoAccept = json.getInt("auto") == 1;
                    acceptRandom = json.getInt("rnd") == 1;
                    requests = json.getJSONArray("requests");
                    myrequests = json.getJSONArray("myrequests");
                    results = json.getJSONArray("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setDuelsG();
                setDuels();
                hideLoading();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[20] Не удалось получить данные, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
                hideLoading();
            }
        });
        dm_behaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void duelSet(int id) {
        makeRequest("duel/set?id=" + id, new YEPRunnable() {
            @Override
            public void run() {
                if (id == 1) {
                    acceptRandom = !acceptRandom;
                } else {
                    autoAccept = !autoAccept;
                }
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[23] Не удалось отправить запрос, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void sendRandomDuel() {
        showLoading();
        makeRequest("duel/rnd", new YEPRunnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject(sresp);
                    int code = json.getInt("code");
                    if (code != 3) {
                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ctx, "[24] Не удалось отправить запрос! Код: " + code, Toast.LENGTH_SHORT).show();
                                if (code == 2) {
                                    Toast.makeText(ctx, "[24] Не найдено подходящих игроков!", Toast.LENGTH_SHORT).show();
                                } else if (code == 1) {
                                    Toast.makeText(ctx, "[24] У вас слишком много дуелей!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        hideLoading();
                        return;
                    }
                    duelTotal++;
                    if (json.getJSONArray("result").getInt(3) == 1) {
                        wins++;
                    }
                    results.put(json.getJSONArray("result"));
                    setDuels();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideLoading();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[24] Не удалось отправить запрос, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
                hideLoading();
            }
        });
    }

    public void sendDuel(String login) {
        showLoading();
        makeRequest("duel/send?login=" + login, new YEPRunnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject(sresp);
                    int code = json.getInt("code");
                    if (code != 6) {
                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ctx, "[25] Не удалось отправить запрос! Код: " + code, Toast.LENGTH_SHORT).show();
                                if (code == 2) {
                                    Toast.makeText(ctx, "[25] Нельзя отправить запрос самому себе!", Toast.LENGTH_SHORT).show();
                                } else if (code == 1) {
                                    Toast.makeText(ctx, "[25] Ник не подходит!", Toast.LENGTH_SHORT).show();
                                } else if (code == 3) {
                                    Toast.makeText(ctx, "[25] У оппонента уровень ниже 9!", Toast.LENGTH_SHORT).show();
                                } else if (code == 4) {
                                    Toast.makeText(ctx, "[25] Вы уже отправили запрос этому игроку!", Toast.LENGTH_SHORT).show();
                                } else if (code == 5) {
                                    Toast.makeText(ctx, "[25] У оппонента слишком мнего дуелей за последнее время!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        hideLoading();
                        return;
                    }
                    myrequests.put(json.getJSONArray("request"));
                    setDuels();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideLoading();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[26] Не удалось отправить запрос, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
                hideLoading();
            }
        });
    }

    public void loadShopMenu() {
        showLoading();
        makeRequest("upgrade/list", new YEPRunnable() {
            @Override
            public void run() {
                try {
                    streamers = new JSONArray(sresp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setStreamersList();
                hideLoading();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[27] Не удалось получить данные, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
                hideLoading();
            }
        });
        sm_behaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void buyStreamer(int streamerID) {
        showLoading();
        makeRequest("upgrade/streamerup?id=" + streamerID, new YEPRunnable() {
            @Override
            public void run() {
                try {
                    JSONObject data = new JSONObject(sresp);
                    int code = data.getInt("code");
                    if (code != 3) {
                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ctx, "[28] Не удалось увеличить уровень! Код: " + code, Toast.LENGTH_SHORT).show();
                                if (code == 2) {
                                    Toast.makeText(ctx, "[28] Недостаточно редстоуна!", Toast.LENGTH_SHORT).show();
                                } else if (code == 1) {
                                    Toast.makeText(ctx, "[28] Недостаточно золота!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        hideLoading();
                        return;
                    }
                    streamers.put(streamerID, data.getJSONArray("info"));
                    JSONObject update = data.getJSONObject("update");
                    gold = update.getString("money");
                    redstone = update.getString("points");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setValues();
                setStreamersList();
                hideLoading();
            }
        }, new YEPRunnable() {
            @Override
            public void run() {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "[29] Не удалось увеличить уровень, код: " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                });
                hideLoading();
            }
        });
    }

    private class bmVersion implements Comparable<bmVersion> {
        private String versionString;
        private String changeLog;
        private boolean beta;
        private String apkUrl;
        private long size;

        public bmVersion(String versionString, String changeLog, boolean isBeta, String apkUrl, long size) {
            this.versionString = versionString;
            this.changeLog = changeLog;
            this.beta = isBeta;
            this.apkUrl = apkUrl;
            this.size = size;
        }

        public bmVersion(String versionString) {
            this.versionString = versionString;
        }

        public final String get() {
            return this.versionString;
        }

        @Override
        public int compareTo(bmVersion that) {
            if (that == null)
                return 1;
            String[] thisParts = this.get().split("\\.");
            String[] thatParts = that.get().split("\\.");
            int length = Math.max(thisParts.length, thatParts.length);
            for (int i = 0; i < length; i++) {
                int thisPart = i < thisParts.length ?
                        Integer.parseInt(thisParts[i]) : 0;
                int thatPart = i < thatParts.length ?
                        Integer.parseInt(thatParts[i]) : 0;
                if (thisPart < thatPart)
                    return -1;
                if (thisPart > thatPart)
                    return 1;
            }
            return 0;
        }

        public boolean isNewerThanCurrent() {
            return this.compareTo(new bmVersion(BuildConfig.VERSION_NAME)) == 1;
        }

        public boolean isBeta() {
            return this.beta;
        }

        public AlertDialog.Builder buildWindow(SharedPreferences prefs) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Доступна новая версия!");
            builder.setMessage("Версия: " + this.get() +
                    "\nРазмер файла: " + (this.size / 1024 / 1024) + "Мб" +
                    "\n\nЧто нового: " + this.changeLog);

            builder.setPositiveButton("Скачать", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl)));
                }
            });
            builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setNeutralButton("Не проверять обновления", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("askForUpdates", false);
                    editor.apply();
                    ctx.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ctx, "Проверка обновлений отключена.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            return builder;
        }
    }
}
