package com.sph.healthtrac.common;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sph.healthtrac.R;
import com.sph.healthtrac.planner.NewPlanActivity;
import com.sph.healthtrac.tracker.TrackerActivity;
import com.sph.healthtrac.dashboard.DashboardActivity;
import com.sph.healthtrac.learn.LearnActivity;
import com.sph.healthtrac.more.HTMoreActivity;
//import com.sph.healthtrac.planner.PlanActivity;

public class HTTabBar extends FragmentActivity {

    private RelativeLayout dashboardBadgeLayout;
    private RelativeLayout planBadgeLayout;
    private RelativeLayout learnBadgeLayout;
    private RelativeLayout moreBadgeLayout;
    private TextView dashboardBadgeTextView;
    private TextView planBadgeTextView;
    private TextView learnBadgeTextView;
    private TextView moreBadgeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_bar);

        Typeface typeTabBar = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-DemiBold.ttf");

        View tabBarIconView;
        TextView tabBarLabel;

        FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.getTabWidget().setBackgroundColor(getResources().getColor(R.color.ht_blue));
        mTabHost.getTabWidget().setDividerDrawable(null);

        // dashboard
        tabBarIconView = LayoutInflater.from(this).inflate(R.layout.tab_bar_dashboard, null);
        tabBarLabel = (TextView) tabBarIconView.findViewById(R.id.dashboardText);
        dashboardBadgeLayout = (RelativeLayout) tabBarIconView.findViewById(R.id.badgeLayout);
        dashboardBadgeTextView = (TextView) tabBarIconView.findViewById(R.id.badgeTextView);

        tabBarLabel.setTypeface(typeTabBar);
        tabBarLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        dashboardBadgeTextView.setTypeface(typeTabBar);
        dashboardBadgeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        dashboardBadgeTextView.setTextColor(Color.WHITE);

        mTabHost.addTab(mTabHost.newTabSpec("DASHBOARD").setIndicator(tabBarIconView), DashboardActivity.class, null);

        // track
        tabBarIconView = LayoutInflater.from(this).inflate(R.layout.tab_bar_track, null);
        tabBarLabel = (TextView) tabBarIconView.findViewById(R.id.trackText);

        tabBarLabel.setTypeface(typeTabBar);
        tabBarLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);

        mTabHost.addTab(mTabHost.newTabSpec("TRACK").setIndicator(tabBarIconView), TrackerActivity.class, null);

        if(!HTGlobals.getInstance().hidePlanner) {
            // plan
            tabBarIconView = LayoutInflater.from(this).inflate(R.layout.tab_bar_plan, null);
            tabBarLabel = (TextView) tabBarIconView.findViewById(R.id.planText);
            planBadgeLayout = (RelativeLayout) tabBarIconView.findViewById(R.id.badgeLayout);
            planBadgeTextView = (TextView) tabBarIconView.findViewById(R.id.badgeTextView);

            tabBarLabel.setTypeface(typeTabBar);
            tabBarLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
            planBadgeTextView.setTypeface(typeTabBar);
            planBadgeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            planBadgeTextView.setTextColor(Color.WHITE);

//            mTabHost.addTab(mTabHost.newTabSpec("PLAN").setIndicator(tabBarIconView), PlanActivity.class, null);
            mTabHost.addTab(mTabHost.newTabSpec("PLAN").setIndicator(tabBarIconView), NewPlanActivity.class, null); // new planner view
        }

        // learn
        tabBarIconView = LayoutInflater.from(this).inflate(R.layout.tab_bar_learn, null);
        tabBarLabel = (TextView) tabBarIconView.findViewById(R.id.learnText);
        learnBadgeLayout = (RelativeLayout) tabBarIconView.findViewById(R.id.badgeLayout);
        learnBadgeTextView = (TextView) tabBarIconView.findViewById(R.id.badgeTextView);

        tabBarLabel.setTypeface(typeTabBar);
        tabBarLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        learnBadgeTextView.setTypeface(typeTabBar);
        learnBadgeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        learnBadgeTextView.setTextColor(Color.WHITE);

        mTabHost.addTab(mTabHost.newTabSpec("LEARN").setIndicator(tabBarIconView), LearnActivity.class, null);

        // more
        tabBarIconView = LayoutInflater.from(this).inflate(R.layout.tab_bar_more, null);
        tabBarLabel = (TextView) tabBarIconView.findViewById(R.id.moreText);
        moreBadgeLayout = (RelativeLayout) tabBarIconView.findViewById(R.id.badgeLayout);
        moreBadgeTextView = (TextView) tabBarIconView.findViewById(R.id.badgeTextView);

        tabBarLabel.setTypeface(typeTabBar);
        tabBarLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        moreBadgeTextView.setTypeface(typeTabBar);
        moreBadgeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        moreBadgeTextView.setTextColor(Color.WHITE);

        mTabHost.addTab(mTabHost.newTabSpec("MORE").setIndicator(tabBarIconView),
                HTMoreActivity.class, null);
    }

    public void showDashboardBadgeView(boolean isShow) {
        if (isShow)
            dashboardBadgeLayout.setVisibility(View.VISIBLE);
        else
            dashboardBadgeLayout.setVisibility(View.INVISIBLE);
    }

    public void setDashboardBadgeCount(String value) {
        dashboardBadgeTextView.setText(value);
    }

    public void showLearnBadgeView(boolean isShow) {
        if (isShow)
            learnBadgeLayout.setVisibility(View.VISIBLE);
        else
            learnBadgeLayout.setVisibility(View.INVISIBLE);
    }

    public void setLearnBadgeCount(String value) {
        learnBadgeTextView.setText(value);
    }

    public void showPlanBadgeView(boolean isShow) {
        if (isShow)
            planBadgeLayout.setVisibility(View.VISIBLE);
        else
            planBadgeLayout.setVisibility(View.INVISIBLE);
    }

    public void setPlanBadgeCount(String value) {
        planBadgeTextView.setText(value);
    }

    public void showMoreBadgeView(boolean isShow) {
        if (isShow)
            moreBadgeLayout.setVisibility(View.VISIBLE);
        else
            moreBadgeLayout.setVisibility(View.INVISIBLE);
    }

    public void setMoreBadgeCount(String value) {
        moreBadgeTextView.setText(value);
    }
}