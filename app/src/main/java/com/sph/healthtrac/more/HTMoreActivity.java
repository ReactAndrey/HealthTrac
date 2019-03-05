package com.sph.healthtrac.more;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTBrowserActivity;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTTabBar;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.more.devices.HTDevicesActivity;
import com.sph.healthtrac.more.inbox.InboxActivity;
import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
import com.sph.healthtrac.common.XMLFunctions;
import com.sph.healthtrac.more.myaccount.HTMyAccountActivity;
import com.sph.healthtrac.more.support.HTSupportActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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
import java.util.Set;

public class HTMoreActivity extends Fragment {

    private Date passDate;
    private int passYear;
    private int passMonth;
    private int passDay;

    private int currentDayOfMonth;
    private int currentMonth;
    private int currentYear;
    private Date currentDate;

    private boolean showMessages = false;

    private String login;
    private String pw;
    private String numberOfMessages = "";
    private String newLearningModules = "";
    private String newEatingPlans = "";
    private String externalLoginURL = "";
    private String resourceCenterURL = "";

    private List<String> moreItems = new ArrayList<>();
    private List<Integer> moreItemImages = new ArrayList<>();

    ListView listViewDashboard;
    Toast toast;

    FragmentActivity fragmentActivity;
    RelativeLayout relativeLayout;

    private static RelativeLayout relativeLayoutMore;

    View datePicker;

    Calendar calendar;

    //HttpClient client;
    DefaultHttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;

    ProgressDialog progressDialog;
    private Thread myThread = null;

    SharedPreferences mSharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentActivity = super.getActivity();
        fragmentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mSharedPreferences = fragmentActivity.getSharedPreferences("SPHPrefs", Activity.MODE_PRIVATE);

        HTGlobals.getInstance().plannerShouldRefresh = true;

        progressDialog = HTProgressDialog.getProgressDialog(fragmentActivity);
        progressDialog.setCancelable(false);

        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.activity_more, container, false);

        relativeLayoutMore = (RelativeLayout) relativeLayout.findViewById(R.id.moreContainer);

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
        View mActionBar = HTActionBar.getActionBar(fragmentActivity, getString(R.string.app_name), "", "");

        relativeLayout.addView(mActionBar);

        DisplayMetrics displayMetrics = fragmentActivity.getResources().getDisplayMetrics();

        float displayDensity = displayMetrics.density;

        int dpValue = 44;
        int topBarHeight = (int) (dpValue * displayDensity);

        params = (RelativeLayout.LayoutParams) mActionBar.getLayoutParams();
        params.height = topBarHeight;

        mActionBar.setLayoutParams(params);

        // set the tab bar label font colors
        TextView tabBarLabel;

        tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.dashboardText);
        tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));

        tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.trackText);
        tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));

        if(!HTGlobals.getInstance().hidePlanner) {
            tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.planText);
            tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));
        }

        tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.learnText);
        tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));

        tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.moreText);
        tabBarLabel.setTextColor(Color.WHITE);

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(fragmentActivity, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            fragmentActivity.finish();

        } else {

            getMorePanelItems();
        }

        return relativeLayout;
    }

    private void getMorePanelItems() {
        numberOfMessages = "";
        newLearningModules = "";
        newEatingPlans = "";
        externalLoginURL = "";
        resourceCenterURL = "";

        moreItems.clear();
        moreItemImages.clear();
        progressDialog.show();
        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "app_more_panel_get_vals"));
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

                    final NodeList error = doc.getElementsByTagName("error");
                    final NodeList nodes = doc.getElementsByTagName("more_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        fragmentActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(fragmentActivity, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        fragmentActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                Element e2 = (Element) nodes.item(0);

                                // Show messages
                                if (XMLFunctions.tagExists(e2, "show_messages")) {

                                    String value = XMLFunctions.getValue(e2, "show_messages").replace(",", "");

                                    if ("1".equals(value)) {

                                        showMessages = true;

                                        if (XMLFunctions.tagExists(e2, "new_messages")) {
                                            numberOfMessages = XMLFunctions.getValue(e2, "new_messages").replace(",", "");
                                        }
                                        moreItems.add("MESSAGES");
                                        moreItemImages.add(R.drawable.ht_more_inbox);
                                        if ("0".equals(numberOfMessages)) {

                                            ((HTTabBar) fragmentActivity).showDashboardBadgeView(false);
                                            ((HTTabBar) fragmentActivity).showMoreBadgeView(false);
                                        } else {
                                            Set<String> tmpCheckedItems = mSharedPreferences.getStringSet("CheckedDashboardItems", null);
                                            if (tmpCheckedItems == null || tmpCheckedItems.contains("Messages"))
                                                ((HTTabBar) fragmentActivity).showDashboardBadgeView(true);
                                            else
                                                ((HTTabBar) fragmentActivity).showDashboardBadgeView(false);
                                            ((HTTabBar) fragmentActivity).showMoreBadgeView(true);
                                            ((HTTabBar) fragmentActivity).setDashboardBadgeCount(numberOfMessages);
                                            ((HTTabBar) fragmentActivity).setMoreBadgeCount(numberOfMessages);
                                        }
                                    }
                                }

                                // Show Resource Center
                                if (XMLFunctions.tagExists(e2, "show_resource_center")) {

                                    String value = XMLFunctions.getValue(e2, "show_resource_center");

                                    if ("1".equals(value)) {

                                        if (XMLFunctions.tagExists(e2, "external_login_url")) {

                                            externalLoginURL = XMLFunctions.getValue(e2, "external_login_url");
                                        }

                                        if (XMLFunctions.tagExists(e2, "resource_center_url")) {

                                            resourceCenterURL = XMLFunctions.getValue(e2, "resource_center_url");
                                        }

                                        // show it?
                                        if (!externalLoginURL.equals("") && !resourceCenterURL.equals("")) {

                                            moreItems.add("RESOURCE CENTER");
                                            moreItemImages.add(R.drawable.ht_more_orders);
                                        }
                                    }
                                }

                                // new learning modules
                                if (XMLFunctions.tagExists(e2, "new_learning_modules")) {
                                    newLearningModules = XMLFunctions.getValue(e2, "new_learning_modules");
                                }

                                if ("0".equals(newLearningModules)) {
                                    ((HTTabBar) fragmentActivity).showLearnBadgeView(false);
                                } else {
                                    ((HTTabBar) fragmentActivity).showLearnBadgeView(true);
                                    ((HTTabBar) fragmentActivity).setLearnBadgeCount(newLearningModules);
                                }

                                // new eating plans
                                if (XMLFunctions.tagExists(e2, "new_eating_plan")) {
                                    newEatingPlans = XMLFunctions.getValue(e2, "new_eating_plan");
                                }

                                if(!HTGlobals.getInstance().hidePlanner) {
                                    if ("0".equals(newEatingPlans)) {
                                        ((HTTabBar) fragmentActivity).showPlanBadgeView(false);
                                    } else {
                                        ((HTTabBar) fragmentActivity).showPlanBadgeView(true);
                                        ((HTTabBar) fragmentActivity).setPlanBadgeCount(newEatingPlans);
                                    }
                                }

                                // display badge number on App Icon.
                                int totalBadgeCount = 0, tempCount;
                                try {
                                    tempCount = Integer.parseInt(numberOfMessages);
                                }catch(NumberFormatException e){
                                    tempCount = 0;
                                }
                                totalBadgeCount += tempCount;
                                try {
                                    tempCount = Integer.parseInt(newLearningModules);
                                }catch(NumberFormatException e){
                                    tempCount = 0;
                                }
                                totalBadgeCount += tempCount;
                                if(!HTGlobals.getInstance().hidePlanner) {
                                    try {
                                        tempCount = Integer.parseInt(newEatingPlans);
                                    } catch (NumberFormatException e) {
                                        tempCount = 0;
                                    }
                                    totalBadgeCount += tempCount;
                                }

                                HTGlobals.getInstance().setAppIconBadge(fragmentActivity, totalBadgeCount);

                                moreItems.add("DEVICES");
                                moreItemImages.add(R.drawable.ht_more_devices);

                                moreItems.add("MY ACCOUNT");
                                moreItemImages.add(R.drawable.ht_more_settings);

                                moreItems.add("SUPPORT");
                                moreItemImages.add(R.drawable.ht_more_support);

                                moreItems.add("SIGN OUT");
                                moreItemImages.add(R.drawable.ht_more_sign_out);

                                showMore();
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    // these all happen with the post.abort() but can be ignored, as the call will be executed again
                    if (error != null) {
                        if (!error.equals("Connection already shutdown") && !error.equals("Request already aborted") && !error.equals("No peer certificate") && !error.equals("Request must not be null.") && !error.equals("Connection has been shut down.") && !error.equals("Connection to https://www.setpointhealth.com refused") && !error.equals("Connection to https://www.trackmyday.org refused")) {

                            fragmentActivity.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

//                                    toast = HTToast.showToast(fragmentActivity, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(fragmentActivity, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                                }
                            });

                        }
                    }

                } finally {

                    fragmentActivity.runOnUiThread(new Runnable() {

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

    private void showMore() {

        LinearLayout linearLayoutMore = (LinearLayout) relativeLayoutMore.findViewById(R.id.linearLayoutMore);

        LayoutInflater inflater = fragmentActivity.getLayoutInflater();

        RelativeLayout.LayoutParams params;

        View rowView = null;

        Typeface typeMoreLabels = Typeface.createFromAsset(fragmentActivity.getAssets(), "fonts/AvenirNext-Regular.ttf");

        ImageView imageViewMoreIcon;

        TextView textViewMoreLabel;

        linearLayoutMore.removeAllViewsInLayout();

        for (int i = 0; i < moreItems.size(); i++) {
            String itemString = moreItems.get(i);

            rowView = inflater.inflate(R.layout.more_row, null, true);

            textViewMoreLabel = (TextView) rowView.findViewById(R.id.textViewMoreLabel);
            imageViewMoreIcon = (ImageView) rowView.findViewById(R.id.imageViewMoreIcon);

            // textViewMoreLabel
            textViewMoreLabel.setTypeface(typeMoreLabels);
            textViewMoreLabel.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
            textViewMoreLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            textViewMoreLabel.setText(itemString);

            // imageViewMoreIcon
            imageViewMoreIcon.setImageResource(moreItemImages.get(i));

            if ("MESSAGES".equals(itemString) && (!"".equals(numberOfMessages) && !"0".equals(numberOfMessages))) {
                RelativeLayout badgeLayout = (RelativeLayout) rowView.findViewById(R.id.badgeLayout);
                badgeLayout.setVisibility(View.VISIBLE);
                TextView badgeCountTextView = (TextView) rowView.findViewById(R.id.badgeTextView);
                Typeface typeBadge = Typeface.createFromAsset(fragmentActivity.getAssets(), "fonts/AvenirNext-DemiBold.ttf");
                badgeCountTextView.setTypeface(typeBadge);
                badgeCountTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                badgeCountTextView.setTextColor(Color.WHITE);
                badgeCountTextView.setText(numberOfMessages);
            }

            if ("MESSAGES".equals(itemString))
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(fragmentActivity, InboxActivity.class);
                        startActivityForResult(intent, 1);
                    }
                });
            else if ("RESOURCE CENTER".equals(itemString))
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String finalRCUrl = externalLoginURL + "?login=" + login + "&password=" + pw + "&landingpage=" + resourceCenterURL + "?app=true";

                        Intent intent = new Intent(fragmentActivity, HTBrowserActivity.class);
                        intent.putExtra("title", "Resource Center");
                        intent.putExtra("open_link", finalRCUrl);
                        intent.putExtra("isPDFDocument", false);
                        startActivityForResult(intent, 1);
                    }
                });
            else if ("DEVICES".equals(itemString))
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(fragmentActivity, HTDevicesActivity.class);
                        startActivityForResult(intent, 1);
                    }
                });
            else if ("MY ACCOUNT".equals(itemString))
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(fragmentActivity, HTMyAccountActivity.class);
                        startActivityForResult(intent, 1);
                    }
                });
            else if ("SUPPORT".equals(itemString))
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(fragmentActivity, HTSupportActivity.class);
                        startActivityForResult(intent, 1);
                    }
                });
            else if ("SIGN OUT".equals(itemString))
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(fragmentActivity, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        fragmentActivity.finish();
                    }
                });

            linearLayoutMore.addView(rowView);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // coming back from a child activity, refresh the calendar

        passDate = HTGlobals.getInstance().passDate;
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;

        pw = HTGlobals.getInstance().passPw;

        getMorePanelItems();
    }
}

