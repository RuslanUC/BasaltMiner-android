package com.rdev.basaltminer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StartActivity extends AppCompatActivity {
    private WebView webView;
    private LoadingView loadingWebView;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (prefs.getBoolean("offlineMode", false)) {
            Intent i = new Intent(StartActivity.this, MainActivity.class);
            i.putExtra("offlineMode", true);
            startActivity(i);
            finish();
            return;
        }

        loadingWebView = findViewById(R.id.loadingWebView);
        loadingWebView.init(this);

        webView = findViewById(R.id.webView);
        Matcher m = Pattern.compile("Chrome\\/\\d+").matcher(webView.getSettings().getUserAgentString());
        if (m.find()) {
            int ver = Integer.parseInt(m.group().replace("Chrome/", ""));
            if (ver < 74) {
                Toast.makeText(getApplicationContext(), "Внимание! Версия webview ниже 74, а именно " + ver + ".\n" +
                        "Авторизация может работать неправильно.\n" +
                        "Пожалуйста, обновите webview.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Не удалось проверить версию webview.\n" +
                    "Авторизация может работать неправильно.", Toast.LENGTH_SHORT).show();
        }
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            public void onPageFinished(WebView view, String url) {
                loadingWebView.hide();
                if (!url.contains("twitch.tv"))
                    webView.loadUrl("https://twitch.tv/login");
                String token = getCookie("https://twitch.tv", "auth-token");
                if (token != null) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("token", token);
                    editor.apply();
                    start();
                }
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                StartActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingWebView.show();
                    }
                });
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (error.getDescription().equals("net::ERR_NAME_NOT_RESOLVED"))
                    showOfflineModeButton();
            }
        });

        if (prefs.getString("token", null) == null)
            webView.loadUrl("https://twitch.tv/login");
        else
            start();
    }

    private void start() {
        String token = prefs.getString("token", null);
        if (token == null) {
            webView.loadUrl("https://twitch.tv/login");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String jwt = "";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), "[{\"operationName\":\"ExtensionsForChannel\",\"variables\":{\"channelID\":\"" + CONFIG.streamer_id + "\"},\"extensions\":{\"persistedQuery\":{\"version\":1,\"sha256Hash\":\"37a5969f117f2f76bc8776a0b216799180c0ce722acb92505c794a9a4f9737e7\"}}}]");
                Request request = new Request.Builder()
                        .url("https://gql.twitch.tv/gql")
                        .addHeader("Authorization", "OAuth " + token)
                        .addHeader("Client-Id", "kimne78kx3ncx6brgo4mv6wki5h1ko")
                        .post(body)
                        .build();

                Response response;
                try {
                    response = client.newCall(request).execute();
                } catch (UnknownHostException _e) {
                    showOfflineModeButton();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                if (response.code() == 401) {
                    StartActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(StartActivity.this, "[3] Ошибка получения данных, пожалуйста, авторизуйтесь. (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                            webView.loadUrl("https://twitch.tv/login");
                        }
                    });
                    return;
                } else if (response.code() != 200) {
                    StartActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(StartActivity.this, "[2] Неизвестный код ошибки: " + response.code() + ".", Toast.LENGTH_SHORT).show();
                            webView.loadUrl("https://twitch.tv/login");
                        }
                    });
                    return;
                }

                JSONArray json;
                try {
                    json = new JSONArray(response.body().string());
                    jwt = json.getJSONObject(0).getJSONObject("data").getJSONObject("user").getJSONObject("channel").getJSONArray("selfInstalledExtensions").getJSONObject(0).getJSONObject("token").getString("jwt");
                } catch (JSONException | IOException e) {
                    StartActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(StartActivity.this, "[4] Неизвестная ошибка: " + e + ".", Toast.LENGTH_SHORT).show();
                            webView.loadUrl("https://twitch.tv/login");
                        }
                    });
                    return;
                }

                try {
                    jwt = checkToken(jwt, json);
                } catch (JSONException | IOException e) {
                    StartActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(StartActivity.this, "[4.1] Неизвестная ошибка: " + e + ".", Toast.LENGTH_SHORT).show();
                            webView.loadUrl("https://twitch.tv/login");
                        }
                    });
                    return;
                }

                String finalJwt = jwt;
                StartActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(StartActivity.this, MainActivity.class);
                        i.putExtra("jwt", finalJwt);
                        startActivity(i);
                        finish();
                    }
                });
            }
        }).start();
    }

    private String checkToken(String jwt, JSONArray json) throws JSONException, IOException {
        JSONObject j = new JSONObject(new String(Base64.decode(jwt.split("\\.")[1], Base64.URL_SAFE), StandardCharsets.UTF_8));
        if(j.getBoolean("is_unlinked")) {
            String extension_id = json.getJSONObject(0).getJSONObject("data").getJSONObject("user").getJSONObject("channel").getJSONArray("selfInstalledExtensions").getJSONObject(0).getJSONObject("token").getString("extensionID");
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), "{\"query\": \"mutation LinkUserMutation($channelID: ID! $extensionID: ID! $token: String! $showUser: Boolean!) {extensionLinkUser(input: {channelID: $channelID, extensionID: $extensionID, jwt: $token, showUser: $showUser,}) {token {extensionID jwt}helixToken {jwt}}}\",\"variables\": {\"channelID\": \""+CONFIG.streamer_id+"\",\"extensionID\": \""+extension_id+"\",\"token\": \""+jwt+"\",\"showUser\": true}}");
            Request request = new Request.Builder()
                    .url("https://gql.twitch.tv/gql")
                    .addHeader("Authorization", "OAuth " + prefs.getString("token", null))
                    .addHeader("Client-Id", "kimne78kx3ncx6brgo4mv6wki5h1ko")
                    .post(body)
                    .build();

            Response response;
            response = client.newCall(request).execute();
            if (response.code() == 401) {
                StartActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(StartActivity.this, "[3.1] Ошибка получения данных, пожалуйста, авторизуйтесь. (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        webView.loadUrl("https://twitch.tv/login");
                    }
                });
                return null;
            } else if (response.code() != 200) {
                StartActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(StartActivity.this, "[2.1] Неизвестный код ошибки: " + response.code() + ".", Toast.LENGTH_SHORT).show();
                        webView.loadUrl("https://twitch.tv/login");
                    }
                });
                return null;
            }
            JSONObject nj;
            nj = new JSONObject(response.body().string());
            return nj.getJSONObject("data").getJSONObject("extensionLinkUser").getJSONObject("token").getString("jwt");
        }
        return jwt;
    }

    private String getCookie(String siteName, String cookieName) {
        String CookieValue = null;
        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        if (cookies == null)
            return null;
        String[] temp = cookies.split(";");
        for (String ar1 : temp) {
            if (ar1.contains(cookieName)) {
                String[] temp1 = ar1.split("=");
                CookieValue = temp1[1];
                break;
            }
        }
        return CookieValue;
    }

    private void showOfflineModeButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Нет интернет соединения. Вы можете перейти в оффлайн режим.", Toast.LENGTH_LONG).show();
                findViewById(R.id.offlineModeLay).setVisibility(View.VISIBLE);
            }
        });
    }

    public void offlineModeButton(View _) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("offlineMode", true);
        editor.apply();
        Intent i = new Intent(StartActivity.this, MainActivity.class);
        i.putExtra("offlineMode", true);
        startActivity(i);
        finish();
    }
}