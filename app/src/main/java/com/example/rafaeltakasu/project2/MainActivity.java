package com.example.rafaeltakasu.project2;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.TimerTask;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private WebView webview;
    private EditText editText;
    private Spinner listSizeSpinner;
    private Spinner runTimesSpinner;
    private Spinner backgroundColorSpinner;
    private RelativeLayout tab1;
    private RelativeLayout tab2;
    private RelativeLayout tab3;
    private TextView timerTextView;
    private TextView selectionSortTextView;

    private static final String TAG = "Main";
    private ProgressDialog progressBar;

    // Declare buttons
    private Button back_button;
    private Button refresh_button;
    private Button min_brightness_button;
    private Button med_brightness_button;
    private Button max_brightness_button;
    private Button selectionSortButton;

    long startTime = 0;
    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_browser:
                    tab1.setVisibility(View.VISIBLE);
                    tab2.setVisibility(View.INVISIBLE);
                    tab3.setVisibility(View.INVISIBLE);
                    selectionSortTextView.setText("");
                    return true;
                case R.id.navigation_brightness:
                    tab1.setVisibility(View.INVISIBLE);
                    tab2.setVisibility(View.VISIBLE);
                    tab3.setVisibility(View.INVISIBLE);
                    selectionSortTextView.setText("");
                    return true;
                case R.id.navigation_algorithms:
                    tab1.setVisibility(View.INVISIBLE);
                    tab2.setVisibility(View.INVISIBLE);
                    tab3.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        this.webview = (WebView)findViewById(R.id.webview);
        this.editText = (EditText)findViewById(R.id.editText);
        this.listSizeSpinner = (Spinner)findViewById(R.id.listSizeSpinner);
        String[] items = new String[]{"10", "1,000", "10,000", "100,000"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        listSizeSpinner.setAdapter(adapter);
        this.runTimesSpinner = (Spinner)findViewById(R.id.runTimesSpinner);
        items = new String[]{"10", "50", "100"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        runTimesSpinner.setAdapter(adapter);
        this.timerTextView = (TextView)findViewById(R.id.timerTextView);
        this.selectionSortTextView = (TextView)findViewById(R.id.selectionSortTextView);
        this.tab1 = (RelativeLayout)findViewById(R.id.tab1);
        this.tab2 = (RelativeLayout)findViewById(R.id.tab2);
        this.tab3 = (RelativeLayout)findViewById(R.id.tab3);


        this.backgroundColorSpinner = (Spinner)findViewById(R.id.backgroundColorSpinner);
        items = new String[]{"Choose Background Color","Black", "White", "Red", "Green", "Blue"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        backgroundColorSpinner.setAdapter(adapter);
        backgroundColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String color = backgroundColorSpinner.getSelectedItem().toString();
                if (color == "Black"){
                    tab2.setBackgroundColor(Color.BLACK);
                    timerTextView.setTextColor(Color.WHITE);
                } else if (color == "White"){
                    tab2.setBackgroundColor(Color.WHITE);
                    timerTextView.setTextColor(Color.BLACK);
                } else if (color == "Red"){
                    tab2.setBackgroundColor(Color.RED);
                    timerTextView.setTextColor(Color.BLACK);
                } else if (color == "Green"){
                    tab2.setBackgroundColor(Color.GREEN);
                    timerTextView.setTextColor(Color.BLACK);
                } else if (color == "Blue"){
                    tab2.setBackgroundColor(Color.BLUE);
                    timerTextView.setTextColor(Color.BLACK);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        back_button = (Button) findViewById(R.id.back_button);
        BackButtonClickListener backButtonClickListener = new BackButtonClickListener();
        back_button.setOnClickListener(backButtonClickListener);

        refresh_button = (Button) findViewById(R.id.refresh_button);
        RefreshButtonClickListener refreshButtonClickListener = new RefreshButtonClickListener();
        refresh_button.setOnClickListener(refreshButtonClickListener);

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

        selectionSortButton = (Button) findViewById(R.id.selectionSortButton);
        SelectionSortClickListener selectionClickListener = new SelectionSortClickListener();
        selectionSortButton.setOnClickListener(selectionClickListener);

        min_brightness_button = (Button) findViewById(R.id.min_brightness);
        med_brightness_button = (Button) findViewById(R.id.med_brightness);
        max_brightness_button = (Button) findViewById(R.id.max_brightness);


        // onClickListener for the brightness button
        MinBrightnessClickListener minOnClickListener = new MinBrightnessClickListener();
        min_brightness_button.setOnClickListener(minOnClickListener);
        MedBrightnessClickListener medOnClickListener = new MedBrightnessClickListener();
        med_brightness_button.setOnClickListener(medOnClickListener);
        MaxBrightnessClickListener maxOnClickListener = new MaxBrightnessClickListener();
        max_brightness_button.setOnClickListener(maxOnClickListener);
    }


    private class BackButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if (webview.canGoBack()) {
                webview.goBack();
            }

        }

    }

    private class RefreshButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            webview.reload();

        }

    }

    private class SelectionSortClickListener implements View.OnClickListener  {
        @Override
        public void onClick(View v)  {
            boolean complete = false;
            Random rand = new Random();
            String size = listSizeSpinner.getSelectedItem().toString();
            String times = runTimesSpinner.getSelectedItem().toString();
            int numTimes = Integer.parseInt(times);
            for (int x = 0; x < numTimes; x++) {
                int listSize = Integer.parseInt(size.replaceAll(",", ""));
                int list[] = new int[listSize];
                for (int i = 0; i < list.length; i++) {
                    list[i] = rand.nextInt(listSize);
                }
                int min;
                for (int i = 0; i < list.length; i++) {
                    min = i;
                    for (int j = i + 1; j < list.length; j++) {
                        if (list[j] < list[min]) {
                            min = j;
                        }
                    }
                    if (min != i) {
                        final int temp = list[i];
                        list[i] = list[min];
                        list[min] = temp;
                    }
                }
            }
            selectionSortTextView.setText("Finished sorting " + times + " lists of size " + size + " using Selection Sort!");
        }

    }

    private class MinBrightnessClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
            WindowManager.LayoutParams layout = getWindow().getAttributes();
            layout.screenBrightness = 0F;
            getWindow().setAttributes(layout);

        }

    }

    private class MedBrightnessClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
            WindowManager.LayoutParams layout = getWindow().getAttributes();
            layout.screenBrightness = 0.5F;
            getWindow().setAttributes(layout);

        }

    }

    private class MaxBrightnessClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
            WindowManager.LayoutParams layout = getWindow().getAttributes();
            layout.screenBrightness = 1F;
            getWindow().setAttributes(layout);

        }

    }

}
