package com.sph.healthtrac.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sph.healthtrac.R;

public class HTBrowserActivity extends Activity {

    private static RelativeLayout relativeLayoutWeb;
    private WebView webView;
    private ProgressDialog progressDialog;

    private boolean hideCloseButton;
    private boolean hideBackButton;

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybrowser);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        String title = getIntent().getStringExtra("title");
        String url = getIntent().getStringExtra("open_link");
        boolean isPDFDocument = getIntent().getBooleanExtra("isPDFDocument", false);
        hideCloseButton = getIntent().getBooleanExtra("hideCloseButton", false);
        hideBackButton = getIntent().getBooleanExtra("hideBackButton", false);
        relativeLayoutWeb = (RelativeLayout) findViewById(R.id.webContainer);
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());

        RelativeLayout.LayoutParams params;

        // action bar
        View mActionBar = HTActionBar.getActionBar(this, title, "leftArrow", "deleteMark"); // actually, browser close button

        relativeLayoutWeb.addView(mActionBar);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        float displayDensity = displayMetrics.density;

        int dpValue = 44;
        int topBarHeight = (int) (dpValue * displayDensity);

        params = (RelativeLayout.LayoutParams) mActionBar.getLayoutParams();
        params.height = topBarHeight;

        mActionBar.setLayoutParams(params);

        // Browser back button
        mActionBar.findViewById(R.id.leftArrowAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean onFirstPage = false;
                String historyUrl = "";
                WebBackForwardList webBackForwardList = webView.copyBackForwardList();

                if (webBackForwardList.getCurrentIndex() > 0) {
                    historyUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - 1).getUrl();
                    if (historyUrl.contains("login_external.asp")) {
                        onFirstPage = true;
                    }
                } else {
                    onFirstPage = true;
                }

                if (webView.canGoBack() && !onFirstPage && !hideCloseButton) {
                    webView.goBack();
                } else {
                    if(hideCloseButton) {
                        webView.clearHistory();
                        webView.clearFormData();
                        webView.clearCache(true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            CookieManager.getInstance().removeAllCookies(null);
                            CookieManager.getInstance().flush();
                        } else {
                            CookieManager.getInstance().removeAllCookie();
                        }
                    }
                    setResult(1);
                    finish();
                }
            }
        });

        if(hideBackButton) {
            /*
            ((ImageView) mActionBar.findViewById(R.id.leftArrowAction)).setImageResource(R.drawable.ht_nav_bar_button_close);
            mActionBar.findViewById(R.id.leftArrowAction).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setResult(1);
                    finish();
                }
            });

            mActionBar.findViewById(R.id.rightCheckAction).setVisibility(View.GONE);
            */
            ((ImageView) mActionBar.findViewById(R.id.rightCheckAction)).setImageResource(R.drawable.ht_nav_bar_button_close);
            mActionBar.findViewById(R.id.rightCheckAction).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setResult(1);
                    finish();
                }
            });

            mActionBar.findViewById(R.id.leftArrowAction).setVisibility(View.GONE);
        } else {
            if (title.equals("Video Tutorials")) {
                mActionBar.findViewById(R.id.rightCheckAction).setVisibility(View.GONE);
            } else {
                //browser close button
                if (hideCloseButton) {
                    mActionBar.findViewById(R.id.rightCheckAction).setVisibility(View.GONE);
                } else {
                    ((ImageView) mActionBar.findViewById(R.id.rightCheckAction)).setImageResource(R.drawable.ht_nav_bar_button_close);
                    mActionBar.findViewById(R.id.rightCheckAction).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setResult(1);
                            finish();
                        }
                    });
                }
            }
        }

        progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.show();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setSaveFormData(false);

        if (isPDFDocument) {
            webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);
        } else {
            webView.loadUrl(url);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.endsWith(".pdf") && !url.contains("google.com")) {
                view.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);
            } else {
                view.loadUrl(url);
            }

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            try {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}

