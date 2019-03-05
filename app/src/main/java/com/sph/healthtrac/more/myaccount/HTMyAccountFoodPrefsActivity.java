package com.sph.healthtrac.more.myaccount;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.common.XMLFunctions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HTMyAccountFoodPrefsActivity extends Activity implements View.OnClickListener{

    private static RelativeLayout mainContentLayout;
    private static InputMethodManager imm;
    private LinearLayout linearLayoutFoodPrefs;

    HttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;
    Toast toast;
    ProgressDialog progressDialog;
    private Thread myThread = null;

    Calendar calendar;
    private Date passDate;
    private int passYear;
    private int passMonth;
    private int passDay;
    private int currentDayOfMonth;
    private int currentMonth;
    private int currentYear;
    private Date currentDate;
    private String login;
    private String pw;

    private TextView nobeefLabel;
    private TextView noporkLabel;
    private TextView nofishLabel;
    private TextView noshellfishLabel;
    private TextView nodairyLabel;
    private TextView noeggLabel;
    private TextView nosoyLabel;
    private TextView noglutenLabel;
    private TextView nowheatLabel;
    private TextView notreenutsLabel;
    private TextView nopeanutsLabel;
    private TextView nomeatLabel;
    private TextView additionalFoodIntroLabel;
    private EditText additionalFoodsEdit;

    private ImageView nobeefCheck;
    private ImageView noporkCheck;
    private ImageView nofishCheck;
    private ImageView noshellfishCheck;
    private ImageView nodairyCheck;
    private ImageView noeggCheck;
    private ImageView nosoyCheck;
    private ImageView noglutenCheck;
    private ImageView nowheatCheck;
    private ImageView notreenutsCheck;
    private ImageView nopeanutsCheck;
    private ImageView nomeatCheck;

    private RelativeLayout nobeefLayout;
    private RelativeLayout noporkLayout;
    private RelativeLayout nofishLayout;
    private RelativeLayout noshellfishLayout;
    private RelativeLayout nodairyLayout;
    private RelativeLayout noeggLayout;
    private RelativeLayout nosoyLayout;
    private RelativeLayout noglutenLayout;
    private RelativeLayout nowheatLayout;
    private RelativeLayout notreenutsLayout;
    private RelativeLayout nopeanutsLayout;
    private RelativeLayout nomeatLayout;

    private Button updateBtn;

    private String selectedNoBeef;
    private String selectedNoPork;
    private String selectedNoFish;
    private String selectedNoShellfish;
    private String selectedNoDairy;
    private String selectedNoEgg;
    private String selectedNoSoy;
    private String selectedNoGluten;
    private String selectedNoWheat;
    private String selectedNoTreeNuts;
    private String selectedNoPeanuts;
    private String selectedNoMeat;
    private String selectedNoAdditional;

    private Typeface avenirNextReqularFont;
    private Typeface avenirNextMediumFont;
    private Typeface avenirRomanFont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount_foodprefs);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        linearLayoutFoodPrefs = (LinearLayout) findViewById(R.id.linearLayoutFoodPrefs);

        avenirNextReqularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");
        avenirRomanFont = Typeface.createFromAsset(getAssets(), "fonts/Avenir-Roman.ttf");

        progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);

        // get the current date, if none was passed in
        calendar = Calendar.getInstance();
        currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        currentDate = calendar.getTime();

        if (HTGlobals.getInstance().passDay != 0) {

            passDate = HTGlobals.getInstance().passDate;
            passDay = HTGlobals.getInstance().passDay;
            passMonth = HTGlobals.getInstance().passMonth;
            passYear = HTGlobals.getInstance().passYear;

        } else {

            passDate = currentDate;
            passDay = currentDayOfMonth;
            passMonth = currentMonth;
            passYear = currentYear;

            HTGlobals.getInstance().passDate = currentDate;
            HTGlobals.getInstance().passDay = currentDayOfMonth;
            HTGlobals.getInstance().passMonth = currentMonth;
            HTGlobals.getInstance().passYear = currentYear;
        }

        login = HTGlobals.getInstance().passLogin;
        pw = HTGlobals.getInstance().passPw;

        RelativeLayout.LayoutParams params;

        // action bar
        View mActionBar = HTActionBar.getActionBar(this, "Food Preferences", "leftArrow", ""); // actually, message compose button

        mainContentLayout.addView(mActionBar);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        float displayDensity = displayMetrics.density;

        int dpValue = 44;
        int topBarHeight = (int) (dpValue * displayDensity);

        params = (RelativeLayout.LayoutParams) mActionBar.getLayoutParams();
        params.height = topBarHeight;

        mActionBar.setLayoutParams(params);

        // left arrow button
        mActionBar.findViewById(R.id.leftArrowAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                setResult(1);
                finish();
            }
        });

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(HTMyAccountFoodPrefsActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();

        } else {
            nobeefLabel = (TextView) findViewById(R.id.nobeefLabel);
            noporkLabel = (TextView) findViewById(R.id.noporkLabel);
            nofishLabel = (TextView) findViewById(R.id.nofishLabel);
            noshellfishLabel = (TextView) findViewById(R.id.noshellfishLabel);
            nodairyLabel = (TextView) findViewById(R.id.nodairyLabel);
            noeggLabel = (TextView) findViewById(R.id.noeggLabel);
            nosoyLabel = (TextView) findViewById(R.id.nosoyLabel);
            noglutenLabel = (TextView) findViewById(R.id.noglutenLabel);
            nowheatLabel = (TextView) findViewById(R.id.nowheatLabel);
            notreenutsLabel = (TextView) findViewById(R.id.notreenutsLabel);
            nopeanutsLabel = (TextView) findViewById(R.id.nopeanutsLabel);
            nomeatLabel = (TextView) findViewById(R.id.nomeatLabel);
            additionalFoodIntroLabel = (TextView) findViewById(R.id.additionalFoodIntroLabel);
            additionalFoodsEdit = (EditText) findViewById(R.id.additionalFoodEdit);
            additionalFoodsEdit.setOnTouchListener(editTouchListener);

            nobeefLabel.setTypeface(avenirNextReqularFont);
            noporkLabel.setTypeface(avenirNextReqularFont);
            nofishLabel.setTypeface(avenirNextReqularFont);
            noshellfishLabel.setTypeface(avenirNextReqularFont);
            nodairyLabel.setTypeface(avenirNextReqularFont);
            noeggLabel.setTypeface(avenirNextReqularFont);
            nosoyLabel.setTypeface(avenirNextReqularFont);
            noglutenLabel.setTypeface(avenirNextReqularFont);
            nowheatLabel.setTypeface(avenirNextReqularFont);
            notreenutsLabel.setTypeface(avenirNextReqularFont);
            nopeanutsLabel.setTypeface(avenirNextReqularFont);
            nomeatLabel.setTypeface(avenirNextReqularFont);
            additionalFoodIntroLabel.setTypeface(avenirRomanFont);
            additionalFoodsEdit.setTypeface(avenirRomanFont);

            nobeefCheck = (ImageView) findViewById(R.id.nobeefCheck);
            noporkCheck = (ImageView) findViewById(R.id.noporkCheck);
            nofishCheck = (ImageView) findViewById(R.id.nofishCheck);
            noshellfishCheck = (ImageView) findViewById(R.id.noshellfishCheck);
            nodairyCheck = (ImageView) findViewById(R.id.nodairyCheck);
            noeggCheck = (ImageView) findViewById(R.id.noeggCheck);
            nosoyCheck = (ImageView) findViewById(R.id.nosoyCheck);
            noglutenCheck = (ImageView) findViewById(R.id.noglutenCheck);
            nowheatCheck = (ImageView) findViewById(R.id.nowheatCheck);
            notreenutsCheck = (ImageView) findViewById(R.id.notreenutsCheck);
            nopeanutsCheck = (ImageView) findViewById(R.id.nopeanutsCheck);
            nomeatCheck = (ImageView) findViewById(R.id.nomeatCheck);

            nobeefLayout = (RelativeLayout) findViewById(R.id.nobeefLayout);
            noporkLayout = (RelativeLayout) findViewById(R.id.noporkLayout);
            nofishLayout = (RelativeLayout) findViewById(R.id.nofishLayout);
            noshellfishLayout = (RelativeLayout) findViewById(R.id.noshellfishLayout);
            nodairyLayout = (RelativeLayout) findViewById(R.id.nodairyLayout);
            noeggLayout = (RelativeLayout) findViewById(R.id.noeggLayout);
            nosoyLayout = (RelativeLayout) findViewById(R.id.nosoyLayout);
            noglutenLayout = (RelativeLayout) findViewById(R.id.noglutenLayout);
            nowheatLayout = (RelativeLayout) findViewById(R.id.nowheatLayout);
            notreenutsLayout = (RelativeLayout) findViewById(R.id.notreenutsLayout);
            nopeanutsLayout = (RelativeLayout) findViewById(R.id.nopeanutsLayout);
            nomeatLayout = (RelativeLayout) findViewById(R.id.nomeatLayout);

            nobeefCheck.setOnClickListener(this);
            noporkCheck.setOnClickListener(this);
            nofishCheck.setOnClickListener(this);
            noshellfishCheck.setOnClickListener(this);
            nodairyCheck.setOnClickListener(this);
            noeggCheck.setOnClickListener(this);
            nosoyCheck.setOnClickListener(this);
            noglutenCheck.setOnClickListener(this);
            nowheatCheck.setOnClickListener(this);
            notreenutsCheck.setOnClickListener(this);
            nopeanutsCheck.setOnClickListener(this);
            nomeatCheck.setOnClickListener(this);

            nobeefLayout.setOnClickListener(this);
            noporkLayout.setOnClickListener(this);
            nofishLayout.setOnClickListener(this);
            noshellfishLayout.setOnClickListener(this);
            nodairyLayout.setOnClickListener(this);
            noeggLayout.setOnClickListener(this);
            nosoyLayout.setOnClickListener(this);
            noglutenLayout.setOnClickListener(this);
            nowheatLayout.setOnClickListener(this);
            notreenutsLayout.setOnClickListener(this);
            nopeanutsLayout.setOnClickListener(this);
            nomeatLayout.setOnClickListener(this);

            updateBtn = (Button) findViewById(R.id.updateBtn);
            updateBtn.setTypeface(avenirNextMediumFont);

            getFoodPrefsValues();
        }
    }

    private void getFoodPrefsValues() {
        selectedNoBeef = "";
        selectedNoPork = "";
        selectedNoFish = "";
        selectedNoShellfish = "";
        selectedNoEgg = "";
        selectedNoSoy = "";
        selectedNoDairy = "";
        selectedNoGluten = "";
        selectedNoWheat = "";
        selectedNoTreeNuts = "";
        selectedNoPeanuts = "";
        selectedNoMeat = "";
        selectedNoAdditional = "";

        progressDialog.show();
        myThread = new Thread() {

            @Override
            public void run() {

                try {
                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_food_prefs_values"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();
                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);

                    NodeList error = doc.getElementsByTagName("error");
                    NodeList nodes = doc.getElementsByTagName("food_prefs_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTMyAccountFoodPrefsActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();
                                String tempString;

                                if (XMLFunctions.tagExists(e2, "no_beef")) {
                                    tempString = XMLFunctions.getValue(e2, "no_beef");
                                    selectedNoBeef = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "no_pork")) {
                                    tempString = XMLFunctions.getValue(e2, "no_pork");
                                    selectedNoPork = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "no_fish")) {
                                    tempString = XMLFunctions.getValue(e2, "no_fish");
                                    selectedNoFish = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "no_shellfish")) {
                                    tempString = XMLFunctions.getValue(e2, "no_shellfish");
                                    selectedNoShellfish = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "no_dairy")) {
                                    tempString = XMLFunctions.getValue(e2, "no_dairy");
                                    selectedNoDairy = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "no_egg")) {
                                    tempString = XMLFunctions.getValue(e2, "no_egg");
                                    selectedNoEgg = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "no_soy")) {
                                    tempString = XMLFunctions.getValue(e2, "no_soy");
                                    selectedNoSoy = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "no_gluten")) {
                                    tempString = XMLFunctions.getValue(e2, "no_gluten");
                                    selectedNoGluten = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "no_wheat")) {
                                    tempString = XMLFunctions.getValue(e2, "no_wheat");
                                    selectedNoWheat = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "no_tree_nuts")) {
                                    tempString = XMLFunctions.getValue(e2, "no_tree_nuts");
                                    selectedNoTreeNuts = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "no_peanuts")) {
                                    tempString = XMLFunctions.getValue(e2, "no_peanuts");
                                    selectedNoPeanuts = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "no_meat")) {
                                    tempString = XMLFunctions.getValue(e2, "no_meat");
                                    selectedNoMeat = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "no_additional")) {
                                    tempString = XMLFunctions.getValue(e2, "no_additional");
                                    selectedNoAdditional = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                showFoodPrefs();
                            }
                        });
                    }

                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            HTConnectErrDialog.showDilaog(HTMyAccountFoodPrefsActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                        }
                    });

                } finally {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        };

        myThread.start();
    }

    private void showFoodPrefs() {

        if ("1".equals(selectedNoBeef)) {
            nobeefCheck.setImageResource(R.drawable.ht_check_on_green);
        } else {
            nobeefCheck.setImageResource(R.drawable.ht_check_off_green);
        }

        if ("1".equals(selectedNoPork)) {
            noporkCheck.setImageResource(R.drawable.ht_check_on_green);
        } else {
            noporkCheck.setImageResource(R.drawable.ht_check_off_green);
        }

        if ("1".equals(selectedNoFish)) {
            nofishCheck.setImageResource(R.drawable.ht_check_on_green);
        } else {
            nofishCheck.setImageResource(R.drawable.ht_check_off_green);
        }

        if ("1".equals(selectedNoShellfish)) {
            noshellfishCheck.setImageResource(R.drawable.ht_check_on_green);
        } else {
            noshellfishCheck.setImageResource(R.drawable.ht_check_off_green);
        }

        if ("1".equals(selectedNoDairy)) {
            nodairyCheck.setImageResource(R.drawable.ht_check_on_green);
        } else {
            nodairyCheck.setImageResource(R.drawable.ht_check_off_green);
        }

        if ("1".equals(selectedNoEgg)) {
            noeggCheck.setImageResource(R.drawable.ht_check_on_green);
        } else {
            noeggCheck.setImageResource(R.drawable.ht_check_off_green);
        }

        if ("1".equals(selectedNoSoy)) {
            nosoyCheck.setImageResource(R.drawable.ht_check_on_green);
        } else {
            nosoyCheck.setImageResource(R.drawable.ht_check_off_green);
        }

        if ("1".equals(selectedNoGluten)) {
            noglutenCheck.setImageResource(R.drawable.ht_check_on_green);
        } else {
            noglutenCheck.setImageResource(R.drawable.ht_check_off_green);
        }

        if ("1".equals(selectedNoWheat)) {
            nowheatCheck.setImageResource(R.drawable.ht_check_on_green);
        } else {
            nowheatCheck.setImageResource(R.drawable.ht_check_off_green);
        }

        if ("1".equals(selectedNoTreeNuts)) {
            notreenutsCheck.setImageResource(R.drawable.ht_check_on_green);
        } else {
            notreenutsCheck.setImageResource(R.drawable.ht_check_off_green);
        }

        if ("1".equals(selectedNoPeanuts)) {
            nopeanutsCheck.setImageResource(R.drawable.ht_check_on_green);
        } else {
            nopeanutsCheck.setImageResource(R.drawable.ht_check_off_green);
        }

        if ("1".equals(selectedNoMeat)) {
            nomeatCheck.setImageResource(R.drawable.ht_check_on_green);
        } else {
            nomeatCheck.setImageResource(R.drawable.ht_check_off_green);
        }

        additionalFoodsEdit.setText(selectedNoAdditional);

        linearLayoutFoodPrefs.setVisibility(View.VISIBLE);
        updateBtn.setVisibility(View.VISIBLE);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFoodPrefsValues();
            }
        });
    }

    View.OnTouchListener editTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            return false;
        }
    };

    private void updateFoodPrefsValues() {
        imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);

        HTGlobals htGlobals = HTGlobals.getInstance();
        selectedNoAdditional = htGlobals.cleanStringBeforeSending(additionalFoodsEdit.getText().toString().trim());

        progressDialog.show();
        myThread = new Thread() {

            @Override
            public void run() {

                try {
                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "update_food_prefs_values"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("no_beef", selectedNoBeef));
                    nameValuePairs.add(new BasicNameValuePair("no_pork", selectedNoPork));
                    nameValuePairs.add(new BasicNameValuePair("no_fish", selectedNoFish));
                    nameValuePairs.add(new BasicNameValuePair("no_shellfish", selectedNoShellfish));
                    nameValuePairs.add(new BasicNameValuePair("no_dairy", selectedNoDairy));
                    nameValuePairs.add(new BasicNameValuePair("no_egg", selectedNoEgg));
                    nameValuePairs.add(new BasicNameValuePair("no_soy", selectedNoSoy));
                    nameValuePairs.add(new BasicNameValuePair("no_gluten", selectedNoGluten));
                    nameValuePairs.add(new BasicNameValuePair("no_wheat", selectedNoWheat));
                    nameValuePairs.add(new BasicNameValuePair("no_tree_nuts", selectedNoTreeNuts));
                    nameValuePairs.add(new BasicNameValuePair("no_peanuts", selectedNoPeanuts));
                    nameValuePairs.add(new BasicNameValuePair("no_meat", selectedNoMeat));
                    nameValuePairs.add(new BasicNameValuePair("no_additional", selectedNoAdditional));

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();
                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);

                    NodeList error = doc.getElementsByTagName("error");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTMyAccountFoodPrefsActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        finish();
                    }

                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            HTConnectErrDialog.showDilaog(HTMyAccountFoodPrefsActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                        }
                    });

                } finally {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        };

        myThread.start();
    }

    @Override
    public void onClick(View v) {
        if(v == nobeefCheck || v == nobeefLayout) {
            if ("1".equals(selectedNoBeef)) {
                selectedNoBeef = "";
                nobeefCheck.setImageResource(R.drawable.ht_check_off_green);
            } else {
                selectedNoBeef = "1";
                nobeefCheck.setImageResource(R.drawable.ht_check_on_green);
            }
        } else if (v == noporkCheck || v == noporkLayout) {
            if ("1".equals(selectedNoPork)) {
                selectedNoPork = "";
                noporkCheck.setImageResource(R.drawable.ht_check_off_green);
            } else {
                selectedNoPork = "1";
                noporkCheck.setImageResource(R.drawable.ht_check_on_green);
            }
        } else if (v == nofishCheck || v == nofishLayout) {
            if ("1".equals(selectedNoFish)) {
                selectedNoFish = "";
                nofishCheck.setImageResource(R.drawable.ht_check_off_green);
            } else {
                selectedNoFish = "1";
                nofishCheck.setImageResource(R.drawable.ht_check_on_green);
            }
        } else if (v == noshellfishCheck || v == noshellfishLayout) {
            if ("1".equals(selectedNoShellfish)) {
                selectedNoShellfish = "";
                noshellfishCheck.setImageResource(R.drawable.ht_check_off_green);
            } else {
                selectedNoShellfish = "1";
                noshellfishCheck.setImageResource(R.drawable.ht_check_on_green);
            }
        } else if (v == nodairyCheck || v == nodairyLayout) {
            if ("1".equals(selectedNoDairy)) {
                selectedNoDairy = "";
                nodairyCheck.setImageResource(R.drawable.ht_check_off_green);
            } else {
                selectedNoDairy = "1";
                nodairyCheck.setImageResource(R.drawable.ht_check_on_green);
            }
        } else if (v == nosoyCheck || v == nosoyLayout) {
            if ("1".equals(selectedNoSoy)) {
                selectedNoSoy = "";
                nosoyCheck.setImageResource(R.drawable.ht_check_off_green);
            } else {
                selectedNoSoy = "1";
                nosoyCheck.setImageResource(R.drawable.ht_check_on_green);
            }
        } else if (v == noglutenCheck || v == noglutenLayout) {
            if ("1".equals(selectedNoGluten)) {
                selectedNoGluten = "";
                noglutenCheck.setImageResource(R.drawable.ht_check_off_green);
            } else {
                selectedNoGluten = "1";
                noglutenCheck.setImageResource(R.drawable.ht_check_on_green);
            }
        } else if (v == nowheatCheck || v == nowheatLayout) {
            if ("1".equals(selectedNoWheat)) {
                selectedNoWheat = "";
                nowheatCheck.setImageResource(R.drawable.ht_check_off_green);
            } else {
                selectedNoWheat = "1";
                nowheatCheck.setImageResource(R.drawable.ht_check_on_green);
            }
        } else if (v == notreenutsCheck || v == notreenutsLayout) {
            if ("1".equals(selectedNoTreeNuts)) {
                selectedNoTreeNuts = "";
                notreenutsCheck.setImageResource(R.drawable.ht_check_off_green);
            } else {
                selectedNoTreeNuts = "1";
                notreenutsCheck.setImageResource(R.drawable.ht_check_on_green);
            }
        } else if (v == nopeanutsCheck || v == nopeanutsLayout) {
            if ("1".equals(selectedNoPeanuts)) {
                selectedNoPeanuts = "";
                nopeanutsCheck.setImageResource(R.drawable.ht_check_off_green);
            } else {
                selectedNoPeanuts = "1";
                nopeanutsCheck.setImageResource(R.drawable.ht_check_on_green);
            }
        } else if (v == nomeatCheck || v == nomeatLayout) {
            if ("1".equals(selectedNoMeat)) {
                selectedNoMeat = "";
                nomeatCheck.setImageResource(R.drawable.ht_check_off_green);
            } else {
                selectedNoMeat = "1";
                nomeatCheck.setImageResource(R.drawable.ht_check_on_green);
            }
        } else if (v == noeggCheck || v == noeggLayout) {
            if ("1".equals(selectedNoEgg)) {
                selectedNoEgg = "";
                noeggCheck.setImageResource(R.drawable.ht_check_off_green);
            } else {
                selectedNoEgg = "1";
                noeggCheck.setImageResource(R.drawable.ht_check_on_green);
            }
        }
    }
}
