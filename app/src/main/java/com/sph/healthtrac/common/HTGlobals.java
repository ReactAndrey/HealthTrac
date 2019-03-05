package com.sph.healthtrac.common;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.sph.healthtrac.R;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HTGlobals {

    private static HTGlobals mInstance = null;

    public String passLogin;
    public String passPw;

    public Date passDate;
    public Integer passDay = 0;
    public Integer passMonth = 0;
    public Integer passYear = 0;

    public boolean hidePlanner = false;
	public boolean hideRecommendedFoodSearchCriteria = false;
    public boolean hideCalorieCalculator = false;
    public boolean exchangeItemTextSearch = false;

    public boolean plannerShouldRefresh = true;

    public String customConnErrorTitle = "Problem with Account";
    public String customConnError = MyApplication.getContext().getResources().getString(R.string.custom_connection_error);

    protected HTGlobals() {
    }

    public static synchronized HTGlobals getInstance() {

        if (null == mInstance) {

            mInstance = new HTGlobals();
        }

        return mInstance;
    }

    public Date addNumberOfDaysToPassDate(Date date, int numberOfDays, boolean updatePassDates) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.add(Calendar.DATE, numberOfDays);

        if (updatePassDates) {

            passDate = calendar.getTime();
            passDay = calendar.get(Calendar.DAY_OF_MONTH);
            passMonth = calendar.get(Calendar.MONTH);
            passYear = calendar.get(Calendar.YEAR);
        }

        return calendar.getTime();
    }

    public Date addNumberOfMonthsToPassDate(Date date, int numberOfMonths, boolean updatePassDates) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.add(Calendar.MONTH, numberOfMonths);

        if (updatePassDates) {

            passDate = calendar.getTime();
            passDay = calendar.get(Calendar.DAY_OF_MONTH);
            passMonth = calendar.get(Calendar.MONTH);
            passYear = calendar.get(Calendar.YEAR);
        }

        return calendar.getTime();
    }

    public String cleanStringBeforeSending(String string) {

        String acceptedCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890~!@#$^&*()-+=:;'\"<>,.?/\n";
        String tempString = "";

        for (int i = 0; i < string.length(); i++) {

            if (acceptedCharacters.contains(string.charAt(i) + "")) {

                tempString = tempString + string.charAt(i) + "";
            }
        }

        tempString = tempString.replace("&", "***and***");
        tempString = tempString.replace("<", "***lt***");
        tempString = tempString.replace(">", "***gt***");
        tempString = tempString.replace(" ", "+");
        tempString = tempString.replace("\"", "***double-quote***");

        return tempString;

//        String tempString = string;
//
//        tempString = tempString.replace("&", "|*|and|*|");
//        tempString = tempString.replace("<", "|*|lt|*|");
//        tempString = tempString.replace(">", "|*|gt|*|");
//        tempString = tempString.replace(" ", "%20");
//        tempString = tempString.replace("\n", "%0D%0A");
//
//        return tempString;
    }

    public String cleanStringAfterReceiving(String string) {

        String tempString = string;

        tempString = tempString.replace("|*|and|*|", "&");
        tempString = tempString.replace("|*|lt|*|", "<");
        tempString = tempString.replace("|*|gt|*|", ">");
        tempString = tempString.replace("&#39;", "'");

        return tempString;
    }

    public boolean isValidDate(String s) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setLenient(false);
        return sdf.parse(s, new ParsePosition(0)) != null;
    }

    public static String capitalize(final String str) {
        return capitalize(str, null);
    }

    public static String capitalize(final String str, final char... delimiters) {
        final int delimLen = delimiters == null ? -1 : delimiters.length;
        if (isEmpty(str) || delimLen == 0) {
            return str;
        }
        final char[] buffer = str.toCharArray();
        boolean capitalizeNext = true;
        for (int i = 0; i < buffer.length; i++) {
            final char ch = buffer[i];
            if (isDelimiter(ch, delimiters)) {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                buffer[i] = Character.toTitleCase(ch);
                capitalizeNext = false;
            }
        }
        return new String(buffer);
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * Is the character a delimiter.
     *
     * @param ch         the character to check
     * @param delimiters the delimiters
     * @return true if it is a delimiter
     */
    private static boolean isDelimiter(final char ch, final char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(ch);
        }
        for (final char delimiter : delimiters) {
            if (ch == delimiter) {
                return true;
            }
        }
        return false;
    }

    public static void setAppIconBadge(Context context, int count){
        String launcherClassName = getLauncherClassName(context);
        if(launcherClassName == null){
            return;
        }

        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }

    private static String getLauncherClassName(Context context){
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for(ResolveInfo resolveInfo : resolveInfos){
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if(pkgName.equalsIgnoreCase(context.getPackageName())){
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }

}