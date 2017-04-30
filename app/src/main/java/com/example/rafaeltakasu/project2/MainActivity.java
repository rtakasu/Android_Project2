package com.example.rafaeltakasu.project2;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {



    private WebView webview;
    private EditText editText;

    private static final String TAG = "Main";
    private ProgressDialog progressBar;

    // Declare buttons
    private Button max_brightness_button;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    webview.setVisibility(View.VISIBLE);
                    max_brightness_button.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    webview.setVisibility(View.INVISIBLE);
                    max_brightness_button.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Webview
        this.webview = (WebView)findViewById(R.id.webview);
        this.editText = (EditText)findViewById(R.id.editText);

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        progressBar = ProgressDialog.show(MainActivity.this, "WebView", "Loading...");

        webview.setWebViewClient(new ProjectWebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "Processing webview url click...");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "Finished loading URL: " +url);
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

        });
        webview.loadUrl("https://www.google.com");

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    webview.loadUrl(editText.getText().toString());

                    handled = true;
                }
                return handled;
            }
        });



        max_brightness_button = (Button) findViewById(R.id.max_brightness);

        // onClickListener for the random button
        BrightnessClickListener randomOnClickListener = new BrightnessClickListener();
        max_brightness_button.setOnClickListener(randomOnClickListener);

    }

    private class BrightnessClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // When the random button is clicked, a random int is chosen from 1-3 to represent
            // the a random choice for the player. 1=elephant, 2=mouse, 3=cat.
            // This then simulates a button click for the animal that was picked.

            WindowManager.LayoutParams layout = getWindow().getAttributes();
            layout.screenBrightness = 1F;
            getWindow().setAttributes(layout);

        }

    }

}
