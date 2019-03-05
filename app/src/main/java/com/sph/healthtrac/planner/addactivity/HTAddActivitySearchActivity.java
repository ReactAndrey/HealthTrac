package com.sph.healthtrac.planner.addactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HTAddActivitySearchActivity extends Activity {
    private static RelativeLayout mainContentLayout;
    private View mActionBar;
    private LinearLayout headLayout;
    private LinearLayout typeLayout;
    private RelativeLayout searchLayout;
    private RelativeLayout searchResultLayout;

    private ListView searchResultListView;
    private SearchResultListViewAdapter listViewAdapter;

    private TextView typeLabel;
    private TextView exerciseLabel;
    private TextView stressManagementLabel;
    private TextView noteLabel;
    private ImageView exerciseCheck;
    private ImageView stressManagementCheck;
    private ImageView noteCheck;

    private EditText searchEdit;
    private TextView searchResultLabel;
    private TextView searchResultSubLabel;


    private static InputMethodManager imm;
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

    HttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;
    Toast toast;
    private Thread myThread = null;

    private List<String> addActivityID = new ArrayList<>();
    private List<String> addActivityName = new ArrayList<>();
    private List<String> addActivityType = new ArrayList<>();

    String addActivityCategory;
    String addActivitySearchFieldString;
    String addActivitySearchString;

    private int numberOfResults;

    int selectedActivityID;
    int relaunchItemID;

    boolean favoritesTypeExerciseChecked;
    boolean favoritesTypeBalanceChecked;
    boolean favoritesTypeNoteChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addactivity_search);

        addActivityCategory = getIntent().getStringExtra("addActivityCategory");
        addActivitySearchFieldString = "";

        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        headLayout = (LinearLayout) findViewById(R.id.headLayout);
        typeLayout = (LinearLayout) findViewById(R.id.favoritesTypeLayout);
        searchLayout = (RelativeLayout) findViewById(R.id.searchLayout);
        searchResultLayout = (RelativeLayout) findViewById(R.id.searchResultLayout);

        searchResultListView = (ListView) findViewById(R.id.searchResultListView);
        listViewAdapter = new SearchResultListViewAdapter(this);
        searchResultListView.setAdapter(listViewAdapter);

        typeLabel = (TextView) findViewById(R.id.typeLabel);
        exerciseLabel = (TextView) findViewById(R.id.exerciseLabel);
        stressManagementLabel = (TextView) findViewById(R.id.stressMngLabel);
        noteLabel = (TextView) findViewById(R.id.noteLabel);
        exerciseCheck = (ImageView) findViewById(R.id.exerciseCheck);
        stressManagementCheck = (ImageView) findViewById(R.id.stressMngCheck);
        noteCheck = (ImageView) findViewById(R.id.noteCheck);

        searchEdit = (EditText) findViewById(R.id.searchEdit);
        searchResultLabel = (TextView) findViewById(R.id.searchResultLabel);
        searchResultSubLabel = (TextView) findViewById(R.id.searchResultSubLabel);

        Typeface avenirNextRegularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        Typeface avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");
        Typeface avenirNextDemiBoldFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-DemiBold.ttf");

        typeLabel.setTypeface(avenirNextRegularFont);
        exerciseLabel.setTypeface(avenirNextMediumFont);
        stressManagementLabel.setTypeface(avenirNextMediumFont);
        noteLabel.setTypeface(avenirNextMediumFont);
        searchResultLabel.setTypeface(avenirNextDemiBoldFont);
        searchResultSubLabel.setTypeface(avenirNextDemiBoldFont);

        exerciseCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritesTypeExerciseChecked = !favoritesTypeExerciseChecked;
                if (favoritesTypeExerciseChecked)
                    exerciseCheck.setImageResource(R.drawable.ht_check_on_green);
                else
                    exerciseCheck.setImageResource(R.drawable.ht_check_off_green);

                getSearchResults();
            }
        });

        stressManagementCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritesTypeBalanceChecked = !favoritesTypeBalanceChecked;
                if (favoritesTypeBalanceChecked)
                    stressManagementCheck.setImageResource(R.drawable.ht_check_on_green);
                else
                    stressManagementCheck.setImageResource(R.drawable.ht_check_off_green);

                getSearchResults();
            }
        });

        noteCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritesTypeNoteChecked = !favoritesTypeNoteChecked;
                if (favoritesTypeNoteChecked)
                    noteCheck.setImageResource(R.drawable.ht_check_on_green);
                else
                    noteCheck.setImageResource(R.drawable.ht_check_off_green);

                getSearchResults();
            }
        });


        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

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

        String title;
        if ("exercise".equals(addActivityCategory)) {

            title = "Exercise";

        } else if ("favorites".equals(addActivityCategory)) {

            title = "My Favorites";

        } else {

            title = "Add Activity";
        }

        RelativeLayout.LayoutParams params;
        // action bar
        mActionBar = HTActionBar.getActionBar(this, title, "leftArrow", "New");

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

        mActionBar.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                Intent intent = new Intent(HTAddActivitySearchActivity.this, HTAddActivitySelectItemActivity.class);
                intent.putExtra("addActivityCategory", "exercise");
                intent.putExtra("relaunchPlannerItem", false);
                startActivityForResult(intent, 1);
            }
        });

        if (!"exercise".equals(addActivityCategory)) {
            mActionBar.findViewById(R.id.rightButton).setVisibility(View.INVISIBLE);
        } else {
            typeLayout.setVisibility(View.GONE);
            searchLayout.setVisibility(View.VISIBLE);
        }


        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            if ("".equals(addActivitySearchFieldString)
                    && !"favorites".equals(addActivityCategory)) {
                showSearchResults();
            } else {
                getSearchResults();
            }

            searchEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
//                    imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                    addActivitySearchFieldString = s.toString();
                    if (addActivitySearchFieldString.length() >= 1) {
                        getSearchResults();
                    } else {
                        addActivityID.clear();
                        addActivityName.clear();
                        addActivityType.clear();

                        selectedActivityID = 0;
                        numberOfResults = 0;

                        showSearchResults();
                    }
                }
            });
        }

    }

    private void getSearchResults() {
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        addActivityID.clear();
        addActivityName.clear();
        addActivityType.clear();

        selectedActivityID = 0;
        numberOfResults = 0;
        addActivitySearchFieldString = searchEdit.getText().toString();

        String favoritesTypeString = "";
        if ("favorites".equals(addActivityCategory)) {
            if (favoritesTypeExerciseChecked)
                favoritesTypeString += "M,";

            if (favoritesTypeBalanceChecked)
                favoritesTypeString += "BAL,";

            if (favoritesTypeNoteChecked)
                favoritesTypeString += "NOTE,";

            if (favoritesTypeString.length() > 0)
                favoritesTypeString = favoritesTypeString.substring(0, favoritesTypeString.length() - 1);
        }

        String searchKey = "";
        try {
            searchKey = URLEncoder.encode(searchEdit.getText().toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
        }
        addActivitySearchString = "WhichCategory=" + addActivityCategory + "&search=" + searchKey + "&relaunch=" + relaunchItemID + "&type=" + favoritesTypeString;

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_activity_search_results"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));

                    //additional parameter from addActivitySearchString
                    String[] additionalParams = addActivitySearchString.split("&");
                    for (int i = 0; i < additionalParams.length; i++) {
                        String[] addtionalParam = additionalParams[i].split("=");
                        if (addtionalParam.length > 1) {
                            nameValuePairs.add(new BasicNameValuePair(addtionalParam[0], addtionalParam[1]));
                        } else {
                            nameValuePairs.add(new BasicNameValuePair(addtionalParam[0], ""));
                        }
                    }

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();
                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);

                    NodeList error = doc.getElementsByTagName("error");
                    NodeList nodes = doc.getElementsByTagName("add_activity_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTAddActivitySearchActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();
                                String tempString = "";

                                int i = 1;
                                while (XMLFunctions.tagExists(e2, "activity_id_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "activity_id_" + i);
                                    addActivityID.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "activity_notes_" + i);
                                    addActivityName.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "activity_type_" + i);
                                    addActivityType.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                numberOfResults = addActivityID.size();
                                showSearchResults();
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    // these all happen with the post.abort() but can be ignored, as the call will be executed again
                    if (error != null) {
                        if (!error.equals("Connection already shutdown") && !error.equals("Request already aborted") && !error.equals("No peer certificate") && !error.equals("Request must not be null.") && !error.equals("Connection has been shut down.") && !error.equals("Connection to https://www.setpointhealth.com refused") && !error.equals("Connection to https://www.trackmyday.org refused")) {

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

//                                    toast = HTToast.showToast(HTAddActivitySearchActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTAddActivitySearchActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                                }
                            });

                        }
                    }

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

    private void deleteFavorite() {
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_activity_delete_favorite"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("WhichID", selectedActivityID + ""));

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

                                toast = HTToast.showToast(HTAddActivitySearchActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                getSearchResults();
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    // these all happen with the post.abort() but can be ignored, as the call will be executed again
                    if (error != null) {
                        if (!error.equals("Connection already shutdown") && !error.equals("Request already aborted") && !error.equals("No peer certificate") && !error.equals("Request must not be null.") && !error.equals("Connection has been shut down.") && !error.equals("Connection to https://www.setpointhealth.com refused") && !error.equals("Connection to https://www.trackmyday.org refused")) {

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

//                                    toast = HTToast.showToast(HTAddActivitySearchActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTAddActivitySearchActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                                }
                            });

                        }
                    }

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

    private void showSearchResults() {

        if ("favorites".equals(addActivityCategory)) {
            typeLayout.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.VISIBLE);
            imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
        } else {
            imm.showSoftInputFromInputMethod(mainContentLayout.getWindowToken(), 0);
        }

        if ("exercise".equals(addActivityCategory) && !"".equals(searchEdit.getText().toString())) {
            searchResultLayout.setVisibility(View.VISIBLE);
            if (numberOfResults == 200) {
                searchResultLabel.setText("Top " + numberOfResults + " results");
                searchResultSubLabel.setVisibility(View.VISIBLE);
                searchResultSubLabel.setText("Refine search to narrow results");
            } else {
                searchResultLabel.setText(numberOfResults + " results");
                searchResultSubLabel.setVisibility(View.GONE);
            }
        } else {
            searchResultLayout.setVisibility(View.GONE);
        }

        listViewAdapter.notifyDataSetChanged();
        searchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                Intent intent = new Intent(HTAddActivitySearchActivity.this, HTAddActivitySelectItemActivity.class);
                intent.putExtra("addActivityCategory", addActivityCategory);
                if ("favorites".equals(addActivityCategory)
                        && !"".equals(addActivityType.get(position))) {
                    if ("exercise".equals(addActivityType.get(position))) {
                        intent.putExtra("selectedActivityType", "exercise");
                    } else if ("stress".equals(addActivityType.get(position))) {
                        intent.putExtra("selectedActivityType", "stress");
                    } else if ("note".equals(addActivityType.get(position))) {
                        intent.putExtra("selectedActivityType", "note");
                    }
                }

                intent.putExtra("selectedActivityID", Integer.parseInt(addActivityID.get(position)));
                if (relaunchItemID > 0) {
                    intent.putExtra("relaunchPlannerItem", true);
                    intent.putExtra("relaunchItemID", relaunchItemID);
                } else {
                    intent.putExtra("relaunchPlannerItem", false);
                }

                startActivityForResult(intent, 1);
            }
        });

        if ("favorites".equals(addActivityCategory)) {
            searchResultListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedActivityID = Integer.parseInt(addActivityID.get(position));
                    AlertDialog.Builder builder = null;
                    builder = new AlertDialog.Builder(HTAddActivitySearchActivity.this);
                    AlertDialog alert;
                    builder.setTitle("Delete Favorite?")
                            .setMessage("Are you sure you want to delete this item from My Favorites?")
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteFavorite();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    selectedActivityID = 0;
                                }
                            });
                    alert = builder.create();
                    alert.show();
                    return false;
                }
            });
        }

//        if ("favorites".equals(addActivityCategory)) {
//            imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
//        } else {
//            imm.showSoftInputFromInputMethod(mainContentLayout.getWindowToken(), 0);
//        }
    }

    private class SearchResultListViewAdapter extends ArrayAdapter<String> {
        private final Activity context;

        public SearchResultListViewAdapter(Activity context) {
            super(context, R.layout.addactivity_searchresult_cell, addActivityID);
            this.context = context;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            ViewHolder holder = null;
            LayoutInflater inflater = context.getLayoutInflater();

            if (view == null) {
                view = inflater.inflate(R.layout.addactivity_searchresult_cell, null, true);
                holder = new ViewHolder();
                holder.typeImageView = (ImageView) view.findViewById(R.id.typeImageView);
                holder.typeLabelView = (TextView) view.findViewById(R.id.typeLabelView);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }


            holder.typeLabelView.setText(addActivityName.get(position));
            if ("favorites".equals(addActivityCategory)
                    && !"".equals(addActivityType.get(position))) {
                holder.typeImageView.setVisibility(View.VISIBLE);
                if ("exercise".equals(addActivityType.get(position))) {
                    holder.typeImageView.setImageResource(R.drawable.ht_planner_activity);
                } else if ("stress".equals(addActivityType.get(position))) {
                    holder.typeImageView.setImageResource(R.drawable.ht_planner_balance);
                } else if ("note".equals(addActivityType.get(position))) {
                    holder.typeImageView.setImageResource(R.drawable.ht_planner_note);
                }
            } else {
                holder.typeImageView.setVisibility(View.GONE);
            }
            return view;
        }

    }

    private static class ViewHolder {
        public ImageView typeImageView;
        public TextView typeLabelView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // coming back from a child activity, refresh the calendar

        if (resultCode == 2) {
            setResult(2);
            finish();
            return;
        }
        passDate = HTGlobals.getInstance().passDate;
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;

        getSearchResults();
    }
}
