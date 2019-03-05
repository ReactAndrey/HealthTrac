package com.sph.healthtrac.learn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTTabBar;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
import com.sph.healthtrac.common.XMLFunctions;

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

/**
 * Created by rcr on 18/05/15.
 */
public class LearnActivity extends Fragment {
    FragmentActivity fragmentActivity;
    RelativeLayout relativeLayout;
    ScrollView scrollViewLearningModules;
    LinearLayout layoutLearningModules;

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

    Calendar calendar;

    DefaultHttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;

    private Thread myThread = null;
    Toast toast;
    RelativeLayout.LayoutParams params;

    private int learningModuleCount = 0;
    private int selectedLearningModuleID = 0;

    private String numberOfNewMessages = "";
    private String numberOfEatingPlans = "";
    private String numberOfLearningModules = "";

    private List<String> learningModuleIdList = new ArrayList<>();
    private List<String> learningModuleSessionIdList = new ArrayList<>();
    private List<String> learningModuleStatusList = new ArrayList<>();
    private List<String> learningModuleTitleList = new ArrayList<>();

    Typeface learnHeaderFont;
    Typeface learnTitleFont;

    float displayDensity;

    SharedPreferences mSharedPreferences;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentActivity = super.getActivity();
        mSharedPreferences = fragmentActivity.getSharedPreferences("SPHPrefs", Activity.MODE_PRIVATE);

        HTGlobals.getInstance().plannerShouldRefresh = true;

        learnHeaderFont = Typeface.createFromAsset(fragmentActivity.getAssets(), "fonts/AvenirNext-Medium.ttf");
        learnTitleFont = Typeface.createFromAsset(fragmentActivity.getAssets(), "fonts/AvenirNext-Regular.ttf");

        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.activity_learn, container, false);

        scrollViewLearningModules = (ScrollView) relativeLayout.findViewById(R.id.scrollViewLearn);
        layoutLearningModules = (LinearLayout) relativeLayout.findViewById(R.id.linearLayoutLearnModules);

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
        View mActionBar = HTActionBar.getActionBar(fragmentActivity, "Learning Modules", "", "");

        relativeLayout.addView(mActionBar);

        DisplayMetrics displayMetrics = fragmentActivity.getResources().getDisplayMetrics();

        displayDensity = displayMetrics.density;

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
        tabBarLabel.setTextColor(Color.WHITE);

        tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.moreText);
        tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));


        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(fragmentActivity, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            fragmentActivity.finish();

        } else {
            getLearningModules();
        }

        return relativeLayout;
    }

    private void getLearningModules() {

        numberOfNewMessages = "";
        numberOfLearningModules = "";
        numberOfEatingPlans = "";

        learningModuleIdList.clear();
        learningModuleSessionIdList.clear();
        learningModuleStatusList.clear();
        learningModuleTitleList.clear();

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(fragmentActivity);
        progressDialog.show();

        if (passDate.getTime() > currentDate.getTime()) {

            // get the current date
            calendar = Calendar.getInstance();
            currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            currentMonth = calendar.get(Calendar.MONTH);
            currentYear = calendar.get(Calendar.YEAR);

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            currentDate = calendar.getTime();

            passDate = currentDate;
            passDay = currentDayOfMonth;
            passMonth = currentMonth;
            passYear = currentYear;

            HTGlobals.getInstance().passDate = currentDate;
            HTGlobals.getInstance().passDay = currentDayOfMonth;
            HTGlobals.getInstance().passMonth = currentMonth;
            HTGlobals.getInstance().passYear = currentYear;
        }

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_learning_modules"));
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
                    final NodeList nodes = doc.getElementsByTagName("learn_details");

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

                                Double tempDouble;
                                String tempString;

                                int i = 1;
                                while (XMLFunctions.tagExists(e2, "learning_module_item_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "learning_module_id_" + i);
                                    learningModuleIdList.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "learning_module_session_id_" + i);
                                    learningModuleSessionIdList.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "learning_module_session_status_" + i);
                                    learningModuleStatusList.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "learning_module_title_" + i);
                                    learningModuleTitleList.add(tempString);
                                    i++;
                                }

                                learningModuleCount = i - 1;

                                //Show messages
                                if (XMLFunctions.tagExists(e2, "show_messages")) {
                                    String value = XMLFunctions.getValue(e2, "show_messages");
                                    if ("1".equals(value)) {
                                        if (XMLFunctions.tagExists(e2, "new_messages")) {
                                            numberOfNewMessages = XMLFunctions.getValue(e2, "new_messages");
                                        }

                                        if ("0".equals(numberOfNewMessages)) {
                                            ((HTTabBar) fragmentActivity).showDashboardBadgeView(false);
                                            ((HTTabBar) fragmentActivity).showMoreBadgeView(false);
                                        } else {
                                            Set<String> tmpCheckedItems = mSharedPreferences.getStringSet("CheckedDashboardItems", null);
                                            if (tmpCheckedItems == null || tmpCheckedItems.contains("Messages"))
                                                ((HTTabBar) fragmentActivity).showDashboardBadgeView(true);
                                            else
                                                ((HTTabBar) fragmentActivity).showDashboardBadgeView(false);
                                            ((HTTabBar) fragmentActivity).showMoreBadgeView(true);
                                            ((HTTabBar) fragmentActivity).setDashboardBadgeCount(numberOfNewMessages);
                                            ((HTTabBar) fragmentActivity).setMoreBadgeCount(numberOfNewMessages);
                                        }
                                    }
                                }

                                //new learning modules
                                if (XMLFunctions.tagExists(e2, "new_learning_modules")) {
                                    numberOfLearningModules = XMLFunctions.getValue(e2, "new_learning_modules");
                                }

                                if ("0".equals(numberOfLearningModules)) {
                                    ((HTTabBar) fragmentActivity).showLearnBadgeView(false);
                                } else {
                                    ((HTTabBar) fragmentActivity).showLearnBadgeView(true);
                                    ((HTTabBar) fragmentActivity).setLearnBadgeCount(numberOfLearningModules);
                                }

                                //new eating plan
                                if (XMLFunctions.tagExists(e2, "new_eating_plan")) {
                                    numberOfEatingPlans = XMLFunctions.getValue(e2, "new_eating_plan");
                                }

                                if(!HTGlobals.getInstance().hidePlanner) {
                                    if ("0".equals(numberOfEatingPlans)) {
                                        ((HTTabBar) fragmentActivity).showPlanBadgeView(false);
                                    } else {
                                        ((HTTabBar) fragmentActivity).showPlanBadgeView(true);
                                        ((HTTabBar) fragmentActivity).setPlanBadgeCount(numberOfEatingPlans);
                                    }
                                }

                                //display badge number on App Icon.
                                int totalBadgeCount = 0, tempCount;
                                try {
                                    tempCount = Integer.parseInt(numberOfNewMessages);
                                }catch(NumberFormatException e){
                                    tempCount = 0;
                                }
                                totalBadgeCount += tempCount;
                                try {
                                    tempCount = Integer.parseInt(numberOfLearningModules);
                                }catch(NumberFormatException e){
                                    tempCount = 0;
                                }
                                totalBadgeCount += tempCount;
                                if(!HTGlobals.getInstance().hidePlanner) {
                                    try {
                                        tempCount = Integer.parseInt(numberOfEatingPlans);
                                    } catch (NumberFormatException e) {
                                        tempCount = 0;
                                    }
                                    totalBadgeCount += tempCount;
                                }

                                HTGlobals.getInstance().setAppIconBadge(fragmentActivity, totalBadgeCount);

                                showLearningModules();
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

    private void showLearningModules() {

        if (layoutLearningModules.getChildCount() > 0)
            layoutLearningModules.removeAllViews();

        boolean hasCurrentModules = false;
        boolean hasFutureModules = false;
        boolean hasArchivedModules = false;

        boolean headerOneShown = false;
        boolean headerTwoShown = false;
        boolean headerThreeShown = false;

        for (int i = 0; i < learningModuleCount; i++) {
            if ("UNLOCKED".equals(learningModuleStatusList.get(i)) || "VIEWED".equals(learningModuleStatusList.get(i))
                    || "UPDATED".equals(learningModuleStatusList.get(i))) {
                hasCurrentModules = true;
            } else if ("LOCKED".equals(learningModuleStatusList.get(i))) {
                hasFutureModules = true;
            } else if ("DONE".equals(learningModuleStatusList.get(i))) {
                hasArchivedModules = true;
            }
        }

//        learningModuleCount = 0;
        // learning modules
        if (learningModuleCount == 0) {
            View nextModuleCell = HTLearningNextModule.getLearningNextModuleView(fragmentActivity, "", false);
            layoutLearningModules.addView(nextModuleCell);
        }

        for (int i = 0; i < learningModuleCount; i++) {
            final int index = i;
            if (hasCurrentModules == false && headerOneShown == false) { // show the empty container for this section
                View nextModuleCell = HTLearningNextModule.getLearningNextModuleView(fragmentActivity, "", false);
                layoutLearningModules.addView(nextModuleCell);
                headerOneShown = true;
            }

            if ("UNLOCKED".equals(learningModuleStatusList.get(i)) || "VIEWED".equals(learningModuleStatusList.get(i))
                    || "UPDATED".equals(learningModuleStatusList.get(i))) {
                if (headerOneShown == false) {
                    View nextModuleCell = HTLearningNextModule.getLearningNextModuleView(fragmentActivity, learningModuleTitleList.get(i), true);
                    nextModuleCell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectLearningModule(index);
                        }
                    });
                    layoutLearningModules.addView(nextModuleCell);
                    headerOneShown = true;
                } else { // additional "current" modules
                    View learningModuleCell = HTLearningModule.getLearningModuleItemView(fragmentActivity, learningModuleTitleList.get(i));
                    learningModuleCell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectLearningModule(index);
                        }
                    });
                    layoutLearningModules.addView(learningModuleCell);
                }
            }

            if (!hasFutureModules && !headerTwoShown && (i == learningModuleCount - 1 ||
                    (!"UNLOCKED".equals(learningModuleStatusList.get(i)) &&
                            !"VIEWED".equals(learningModuleStatusList.get(i)) &&
                            !"UPDATED".equals(learningModuleStatusList.get(i))))) {

                TextView learnLabel = new TextView(fragmentActivity);
                learnLabel.setTypeface(learnHeaderFont);
                learnLabel.setTextColor(fragmentActivity.getResources().getColor(R.color.ht_gray_title_text));
                learnLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                learnLabel.setText("FUTURE MODULES");

                int left_marginValue = (int) (12 * displayDensity);
                int top_marginValue = (int) (22 * displayDensity);
                int bottom_marginValue = (int) (12 * displayDensity);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(left_marginValue, top_marginValue, left_marginValue, bottom_marginValue);
                learnLabel.setLayoutParams(params);
                layoutLearningModules.addView(learnLabel);

                View learningModuleCell = HTLearningModule.getLearningModuleItemView(fragmentActivity, "There are no modules in this section");
                View contentView = learningModuleCell.findViewById(R.id.contentLayout);
                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) contentView.getLayoutParams();
                params1.setMargins(0, 0, 0, 0);
                contentView.setLayoutParams(params1);
                contentView.setBackgroundColor(Color.WHITE);
                ImageView iconView = (ImageView) learningModuleCell.findViewById(R.id.imageView);
                iconView.setVisibility(View.GONE);
                layoutLearningModules.addView(learningModuleCell);

                headerTwoShown = true;
            }

            if ("LOCKED".equals(learningModuleStatusList.get(i))) {
                if (!headerTwoShown) { // just show the header
                    TextView learnLabel = new TextView(fragmentActivity);
                    learnLabel.setTypeface(learnHeaderFont);
                    learnLabel.setTextColor(fragmentActivity.getResources().getColor(R.color.ht_gray_title_text));
                    learnLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    learnLabel.setText("FUTURE MODULES");

                    int left_marginValue = (int) (12 * displayDensity);
                    int top_marginValue = (int) (22 * displayDensity);
                    int bottom_marginValue = (int) (8 * displayDensity);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(left_marginValue, top_marginValue, left_marginValue, bottom_marginValue);
                    learnLabel.setLayoutParams(params);
                    layoutLearningModules.addView(learnLabel);

                    headerTwoShown = true;
                }
                View learningModuleCell = HTLearningModule.getLearningModuleItemView(fragmentActivity, learningModuleTitleList.get(i));
//                learningModuleCell.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        selectLearningModule(index);
//                    }
//                });
                layoutLearningModules.addView(learningModuleCell);
            }

            if (!hasArchivedModules && !headerThreeShown && (i == learningModuleCount - 1 ||
                    (!"UNLOCKED".equals(learningModuleStatusList.get(i)) &&
                            !"VIEWED".equals(learningModuleStatusList.get(i)) &&
                            !"UPDATED".equals(learningModuleStatusList.get(i)) &&
                            !"LOCKED".equals(learningModuleStatusList.get(i))))) {
                TextView learnLabel = new TextView(fragmentActivity);
                learnLabel.setTypeface(learnHeaderFont);
                learnLabel.setTextColor(fragmentActivity.getResources().getColor(R.color.ht_gray_title_text));
                learnLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                learnLabel.setText("ARCHIVED MODULES");

                int left_marginValue = (int) (12 * displayDensity);
                int top_marginValue = (int) (22 * displayDensity);
                int bottom_marginValue = (int) (12 * displayDensity);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(left_marginValue, top_marginValue, left_marginValue, bottom_marginValue);
                learnLabel.setLayoutParams(params);
                layoutLearningModules.addView(learnLabel);

                View learningModuleCell = HTLearningModule.getLearningModuleItemView(fragmentActivity, "There are no modules in this section");
                View contentView = learningModuleCell.findViewById(R.id.contentLayout);
                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) contentView.getLayoutParams();
                params1.setMargins(0, 0, 0, 0);
                contentView.setLayoutParams(params1);
                contentView.setBackgroundColor(Color.WHITE);
                ImageView iconView = (ImageView) learningModuleCell.findViewById(R.id.imageView);
                iconView.setVisibility(View.GONE);
                layoutLearningModules.addView(learningModuleCell);

                headerThreeShown = true;
            }

            if ("DONE".equals(learningModuleStatusList.get(i))) {
                if (!headerThreeShown) { // just show the header
                    TextView learnLabel = new TextView(fragmentActivity);
                    learnLabel.setTypeface(learnHeaderFont);
                    learnLabel.setTextColor(fragmentActivity.getResources().getColor(R.color.ht_gray_title_text));
                    learnLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    learnLabel.setText("ARCHIVED MODULES");

                    int left_marginValue = (int) (12 * displayDensity);
                    int top_marginValue = (int) (22 * displayDensity);
                    int bottom_marginValue = (int) (8 * displayDensity);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(left_marginValue, top_marginValue, left_marginValue, bottom_marginValue);
                    learnLabel.setLayoutParams(params);
                    layoutLearningModules.addView(learnLabel);
                    headerThreeShown = true;
                }
                View learningModuleCell = HTLearningModule.getLearningModuleItemView(fragmentActivity, learningModuleTitleList.get(i));
                View contentView = learningModuleCell.findViewById(R.id.contentLayout);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) contentView.getLayoutParams();
                params.setMargins(0, (int) (4 * displayDensity), 0, 0);
                contentView.setLayoutParams(params);
                contentView.setBackgroundColor(Color.WHITE);
                ImageView iconView = (ImageView) learningModuleCell.findViewById(R.id.imageView);
                iconView.setImageResource(R.drawable.ht_learning_module_gray);
                learningModuleCell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectLearningModule(index);
                    }
                });
                layoutLearningModules.addView(learningModuleCell);
            }
        }

        scrollViewLearningModules.scrollTo(0, 0);
    }

    private void selectLearningModule(int index) {
        Intent intent = new Intent(fragmentActivity, LearnDetailActivity.class);
        intent.putExtra("learningModuleID", learningModuleSessionIdList.get(index));
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // coming back from a child activity, refresh the calendar

        passDate = HTGlobals.getInstance().passDate;
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;

        getLearningModules();
    }

}
