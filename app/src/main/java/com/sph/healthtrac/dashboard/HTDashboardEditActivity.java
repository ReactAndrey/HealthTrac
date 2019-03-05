package com.sph.healthtrac.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class HTDashboardEditActivity extends Activity {

    private static RelativeLayout relativeLayoutContainer;
    private HTCellDraggableListView listViewDashboardEdit;
    private HTDashboardEditListAdapter adapter;

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

    private ArrayList<String> dashboardEditItems;
    private ArrayList<String> sortedDashboardEditItems;
    private ArrayList<String> checkedDashboardEditItems;
    private ArrayList<String> dashboardUserSort = new ArrayList<String>();

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_edit);

        dashboardEditItems = getIntent().getStringArrayListExtra("dashboardEditItems");

        preferences = getSharedPreferences("SPHPrefs", MODE_PRIVATE);
        editor = preferences.edit();
        Set<String> tmpCheckedItems = preferences.getStringSet("CheckedDashboardItems", null);
        if (tmpCheckedItems == null) {
            checkedDashboardEditItems = new ArrayList<String>();
            checkedDashboardEditItems.addAll(dashboardEditItems);
        } else {
            checkedDashboardEditItems = new ArrayList<String>(tmpCheckedItems);
        }

        String tmpSortedItems = preferences.getString("SortedDashboardItems", null);
        sortedDashboardEditItems = new ArrayList<String>();
        if (tmpSortedItems == null) {
            sortedDashboardEditItems.addAll(dashboardEditItems);
        } else {
            String items[] = tmpSortedItems.trim().split(",");
            for (String item : items) {
                if (dashboardEditItems.contains(item))
                    sortedDashboardEditItems.add(item);
            }
            for (int i = 0; i < dashboardEditItems.size(); i++)
                if (!sortedDashboardEditItems.contains(dashboardEditItems.get(i)))
                    sortedDashboardEditItems.add(dashboardEditItems.get(i));
        }

        relativeLayoutContainer = (RelativeLayout) findViewById(R.id.dashboardEditContainer);
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

        RelativeLayout.LayoutParams params;

        // action bar
        View mActionBar = HTActionBar.getActionBar(this, getString(R.string.app_name), "Cancel", "Done");

        relativeLayoutContainer.addView(mActionBar);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        float displayDensity = displayMetrics.density;

        int dpValue = 44;
        int topBarHeight = (int) (dpValue * displayDensity);

        params = (RelativeLayout.LayoutParams) mActionBar.getLayoutParams();
        params.height = topBarHeight;

        mActionBar.setLayoutParams(params);

        // Cancel button
        mActionBar.findViewById(R.id.leftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imm.hideSoftInputFromWindow(relativeLayoutContainer.getWindowToken(), 0);
                setResult(1);
                finish();
            }
        });

        // Done button
        mActionBar.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putStringSet("CheckedDashboardItems", new HashSet<String>(adapter.getCheckedItems()));
                StringBuilder sb = new StringBuilder();
                String uncheckedItems = "";
                for (String s : sortedDashboardEditItems) {
                    if(adapter.getCheckedItems().contains(s)) {
                        sb.append(s);
                        if (sb.length() > 0)
                            sb.append(",");
                    }else{
                        uncheckedItems = uncheckedItems + "," + s;
                    }
                }
                if (sb.length() > 0)
                    sb = sb.deleteCharAt(sb.length() - 1);

                String sortedItems = sb.toString() + uncheckedItems;
                editor.putString("SortedDashboardItems", sortedItems);
                editor.commit();
                setResult(1);
                finish();
            }
        });

        listViewDashboardEdit = (HTCellDraggableListView) findViewById(R.id.listViewDashboardEdit);
        adapter = new HTDashboardEditListAdapter(this, R.layout.dashboard_editlist_cell, sortedDashboardEditItems);
        adapter.setCheckedItems(checkedDashboardEditItems);
        listViewDashboardEdit.setItemList(sortedDashboardEditItems);
        listViewDashboardEdit.setAdapter(adapter);
        listViewDashboardEdit.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }
}