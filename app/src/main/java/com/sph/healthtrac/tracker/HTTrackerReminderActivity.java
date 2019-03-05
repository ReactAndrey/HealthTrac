package com.sph.healthtrac.tracker;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTTimePickerDialog;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
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

public class HTTrackerReminderActivity extends FragmentActivity {

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

    private static boolean reminderColorYesterdayChecked;
    private static boolean reminderColorTodayChecked;

    private static boolean reminderDailyChecked;
    private static boolean reminderMondayChecked;
    private static boolean reminderTuesdayChecked;
    private static boolean reminderWednesdayChecked;
    private static boolean reminderThursdayChecked;
    private static boolean reminderFridayChecked;
    private static boolean reminderSaturdayChecked;
    private static boolean reminderSundayChecked;

    private static String reminderTime;
    private static String reminderTimeAmPm;
    private static String reminderDays;
    private static String reminderColorDay;

    Toast toast;

    private static RelativeLayout relativeLayoutReminders;

    EditText editTextReminderTime;

    ImageView reminderCheckboxDaily;
    ImageView reminderCheckboxMonday;
    ImageView reminderCheckboxTuesday;
    ImageView reminderCheckboxWednesday;
    ImageView reminderCheckboxThursday;
    ImageView reminderCheckboxFriday;
    ImageView reminderCheckboxSaturday;
    ImageView reminderCheckboxSunday;

    Calendar calendar;

    HttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;

    private Thread myThread = null;

    ProgressDialog progressDialog;

    private static InputMethodManager imm;

    private View mActionBar;

    DisplayMetrics displayMetrics;

    float displayDensity;
    float screenWidth;
    float screenHeight;

    private String whichMetric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        relativeLayoutReminders = (RelativeLayout) findViewById(R.id.remindersContainer);

        // get the current date, if none was passed in
        calendar = Calendar.getInstance();
        currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        currentMonth =  calendar.get(Calendar.MONTH);
        currentYear =  calendar.get(Calendar.YEAR);

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

        displayMetrics = this.getResources().getDisplayMetrics();

        displayDensity = displayMetrics.density;
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        configureActionBar(HTTrackerReminderActivity.this, "Set a Reminder", "leftArrow", "checkMark");

        whichMetric = getIntent().getExtras().getString("metric");

        if(login.equals("") || pw.equals("") || whichMetric.equals("")) {

            Intent intent = new Intent(HTTrackerReminderActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            imm.hideSoftInputFromWindow(relativeLayoutReminders.getWindowToken(), 0);
            finish();

        } else {

            getReminders();
        }
    }

    private void configureActionBar(Context context, String titleText, String leftButtonText, String rightButtonText) {

        // action bar
        RelativeLayout.LayoutParams params;

        relativeLayoutReminders.removeView(mActionBar);

        mActionBar = HTActionBar.getActionBar(context, titleText, leftButtonText, rightButtonText);

        relativeLayoutReminders.addView(mActionBar);

        int dpValue = 44;
        int topBarHeight = (int)(dpValue * displayDensity);

        params = (RelativeLayout.LayoutParams) mActionBar.getLayoutParams();
        params.height = topBarHeight;

        mActionBar.setLayoutParams(params);

        mActionBar.findViewById(R.id.leftArrowAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imm.hideSoftInputFromWindow(relativeLayoutReminders.getWindowToken(), 0);

                setResult(1);
                finish();
            }
        });

        mActionBar.findViewById(R.id.rightCheckAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateReminders();
            }
        });
    }

    private void getReminders() {

        imm.hideSoftInputFromWindow(relativeLayoutReminders.getWindowToken(), 0);

        progressDialog.show();

        reminderColorYesterdayChecked = false;
        reminderColorTodayChecked = false;

        reminderDailyChecked = false;
        reminderMondayChecked = false;
        reminderTuesdayChecked = false;
        reminderWednesdayChecked = false;
        reminderThursdayChecked = false;
        reminderFridayChecked = false;
        reminderSaturdayChecked = false;
        reminderSundayChecked = false;

        reminderTime = "";
        reminderTimeAmPm = "";
        reminderDays = "";
        reminderColorDay = "";

        if (passDate.getTime() > currentDate.getTime()) {

            // get the current date
            calendar = Calendar.getInstance();
            currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            currentMonth =  calendar.get(Calendar.MONTH);
            currentYear =  calendar.get(Calendar.YEAR);

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
                    nameValuePairs.add(new BasicNameValuePair("action", "get_tracker_reminder"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));

                    nameValuePairs.add(new BasicNameValuePair("metric", whichMetric));

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();
                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);

                    NodeList error = doc.getElementsByTagName("error");
                    NodeList nodes = doc.getElementsByTagName("reminder_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTTrackerReminderActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    }
                    else {

                        final Element e2 = (Element)nodes.item(0);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                Integer reminderTimeInt;

                                reminderTime = XMLFunctions.getValue(e2, "reminder_time");

                                if (!reminderTime.equals("")) {

                                    reminderTimeInt = Integer.parseInt(reminderTime);

                                    if (reminderTimeInt > 12 && reminderTimeInt != 24) {

                                        reminderTime = (reminderTimeInt - 12) + "";
                                        reminderTimeAmPm = "pm";

                                    } else if (reminderTimeInt == 24 || reminderTimeInt == 0) {

                                        reminderTime = "12";
                                        reminderTimeAmPm = "am";

                                    } else if (reminderTimeInt == 12) {

                                        reminderTime = "12";
                                        reminderTimeAmPm = "pm";

                                    } else {

                                        reminderTime = reminderTimeInt + "";
                                        reminderTimeAmPm = "am";
                                    }
                                }

                                reminderColorDay = XMLFunctions.getValue(e2, "reminder_color_day");

                                if (reminderColorDay.equals("yesterday")) {

                                    reminderColorYesterdayChecked = true;
                                    reminderColorTodayChecked = false;

                                } else if (reminderColorDay.equals("today")) {

                                    reminderColorYesterdayChecked = false;
                                    reminderColorTodayChecked = true;
                                }

                                reminderDays = XMLFunctions.getValue(e2, "reminder_days");

                                if (reminderDays.contains("DLY")) {

                                    reminderDailyChecked = true;
                                }

                                if (reminderDays.contains("MON")) {

                                    reminderMondayChecked = true;
                                }

                                if (reminderDays.contains("TUE")) {

                                    reminderTuesdayChecked = true;
                                }

                                if (reminderDays.contains("WED")) {

                                    reminderWednesdayChecked = true;
                                }

                                if (reminderDays.contains("THU")) {

                                    reminderThursdayChecked = true;
                                }

                                if (reminderDays.contains("FRI")) {

                                    reminderFridayChecked = true;
                                }

                                if (reminderDays.contains("SAT")) {

                                    reminderSaturdayChecked = true;
                                }

                                if (reminderDays.contains("SUN")) {

                                    reminderSundayChecked = true;
                                }

                                if (reminderDailyChecked) {

                                    reminderMondayChecked = true;
                                    reminderTuesdayChecked = true;
                                    reminderWednesdayChecked = true;
                                    reminderThursdayChecked = true;
                                    reminderFridayChecked = true;
                                    reminderSaturdayChecked = true;
                                    reminderSundayChecked = true;
                                }

                                if (reminderMondayChecked && reminderTuesdayChecked && reminderWednesdayChecked && reminderThursdayChecked && reminderFridayChecked && reminderSaturdayChecked && reminderSundayChecked) {

                                    reminderDailyChecked = true;

                                } else {

                                    reminderDailyChecked = false;
                                }

                                showReminders();
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

//                            toast = HTToast.showToast(HTTrackerReminderActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(HTTrackerReminderActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void updateReminders() {

        imm.hideSoftInputFromWindow(relativeLayoutReminders.getWindowToken(), 0);

        reminderDays = "";

        if (reminderDailyChecked) {

            reminderDays = reminderDays + "DLY,";
        }

        if (reminderMondayChecked) {

            reminderDays = reminderDays + "MON,";
        }

        if (reminderTuesdayChecked) {

            reminderDays = reminderDays + "TUE,";
        }

        if (reminderWednesdayChecked) {

            reminderDays = reminderDays + "WED,";
        }

        if (reminderThursdayChecked) {

            reminderDays = reminderDays + "THU,";
        }

        if (reminderFridayChecked) {

            reminderDays = reminderDays + "FRI,";
        }

        if (reminderSaturdayChecked) {

            reminderDays = reminderDays + "SAT,";
        }

        if (reminderSundayChecked) {

            reminderDays = reminderDays + "SUN,";
        }

        if (!reminderDays.equals("")) {

            reminderDays = reminderDays.substring(0, reminderDays.length() - 1); // trim off the last comma
        }

        if (whichMetric.equals("color") && !reminderTime.equals("") && !reminderColorYesterdayChecked && !reminderColorTodayChecked && !reminderDays.equals("")) {

            toast = HTToast.showToast(HTTrackerReminderActivity.this, "Please select Yesterday or Today for your Color My Day reminder", Toast.LENGTH_LONG);
            return;
        }

        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "update_tracker_reminder"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));

                    nameValuePairs.add(new BasicNameValuePair("metric", whichMetric));

                    Integer reminderTimeInt;

                    if (!reminderTime.equals("")) {

                        reminderTimeInt = Integer.parseInt(reminderTime);

                        if (reminderTimeAmPm.equals("pm") && reminderTimeInt < 12) {

                            reminderTime = (reminderTimeInt + 12) + "";

                        } else if (reminderTimeAmPm.equals("am") && reminderTimeInt == 12) {

                            reminderTime = "0";
                        }
                    }

                    nameValuePairs.add(new BasicNameValuePair("reminder_days", reminderDays));
                    nameValuePairs.add(new BasicNameValuePair("reminder_hour", reminderTime));

                    reminderColorDay = "";

                    if (whichMetric.equals("color")) {

                        if (reminderColorYesterdayChecked) {

                            reminderColorDay = "yesterday";

                        } else if (reminderColorTodayChecked) {

                            reminderColorDay = "today";
                        }
                    }

                    nameValuePairs.add(new BasicNameValuePair("reminder_color_day", reminderColorDay));

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

                                progressDialog.dismiss();

                                toast = HTToast.showToast(HTTrackerReminderActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    }
                    else {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                progressDialog.dismiss();

                                imm.hideSoftInputFromWindow(relativeLayoutReminders.getWindowToken(), 0);

                                setResult(1);
                                finish();
                            }
                        });
                    }

                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            progressDialog.dismiss();

//                            toast = HTToast.showToast(HTTrackerReminderActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(HTTrackerReminderActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                        }
                    });

                }
            }
        };

        myThread.start();
    }

    private void checkBoxChecked(String WhichCheckBox) {

        if (WhichCheckBox.equals("daily")) {

            if (reminderDailyChecked) {

                reminderDailyChecked = false;

                reminderMondayChecked = false;
                reminderTuesdayChecked = false;
                reminderWednesdayChecked = false;
                reminderThursdayChecked = false;
                reminderFridayChecked = false;
                reminderSaturdayChecked = false;
                reminderSundayChecked = false;

            } else {

                reminderDailyChecked = true;

                reminderMondayChecked = true;
                reminderTuesdayChecked = true;
                reminderWednesdayChecked = true;
                reminderThursdayChecked = true;
                reminderFridayChecked = true;
                reminderSaturdayChecked = true;
                reminderSundayChecked = true;
            }

        } else if (WhichCheckBox.equals("monday")) {

            if (reminderMondayChecked) {

                reminderMondayChecked = false;

            } else {

                reminderMondayChecked = true;
            }

        } else if (WhichCheckBox.equals("tuesday")) {

            if (reminderTuesdayChecked) {

                reminderTuesdayChecked = false;

            } else {

                reminderTuesdayChecked = true;
            }

        } else if (WhichCheckBox.equals("wednesday")) {

            if (reminderWednesdayChecked) {

                reminderWednesdayChecked = false;

            } else {

                reminderWednesdayChecked = true;
            }

        } else if (WhichCheckBox.equals("thursday")) {

            if (reminderThursdayChecked) {

                reminderThursdayChecked = false;

            } else {

                reminderThursdayChecked = true;
            }

        } else if (WhichCheckBox.equals("friday")) {

            if (reminderFridayChecked) {

                reminderFridayChecked = false;

            } else {

                reminderFridayChecked = true;
            }

        } else if (WhichCheckBox.equals("saturday")) {

            if (reminderSaturdayChecked) {

                reminderSaturdayChecked = false;

            } else {

                reminderSaturdayChecked = true;
            }

        } else if (WhichCheckBox.equals("sunday")) {

            if (reminderSundayChecked) {

                reminderSundayChecked = false;

            } else {

                reminderSundayChecked = true;
            }
        }

        if (reminderMondayChecked && reminderTuesdayChecked && reminderWednesdayChecked && reminderThursdayChecked && reminderFridayChecked && reminderSaturdayChecked && reminderSundayChecked) {

            reminderDailyChecked = true;

        } else {

            reminderDailyChecked = false;
        }

        showReminders();
    }

    private void radioButtonChecked(String WhichRadio) {

        if (WhichRadio.equals("yesterday")) {

            reminderColorTodayChecked = false;

            if (reminderColorYesterdayChecked) {

                reminderColorYesterdayChecked = false;

            } else {

                reminderColorYesterdayChecked = true;
            }

        } else if (WhichRadio.equals("today")) {

            reminderColorYesterdayChecked = false;

            if (reminderColorTodayChecked) {

                reminderColorTodayChecked = false;

            } else {

                reminderColorTodayChecked = true;
            }
        }

        showReminders();
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

            if (selectedHour > 12 && selectedHour != 24) {

                reminderTime = (selectedHour - 12) + "";
                reminderTimeAmPm = "pm";

            } else if (selectedHour == 24 || selectedHour == 0) {

                reminderTime = "12";
                reminderTimeAmPm = "am";

            } else if (selectedHour == 12) {

                reminderTime = "12";
                reminderTimeAmPm = "pm";

            } else {

                reminderTime = selectedHour + "";
                reminderTimeAmPm = "am";
            }

            showReminders();
        }
    };

    private void showReminders() {

        final LinearLayout linearLayoutReminders = (LinearLayout) relativeLayoutReminders.findViewById(R.id.linearLayoutReminders);

        LayoutInflater inflater = this.getLayoutInflater();

        RelativeLayout.LayoutParams params;

        View rowView;

        float rowHeight;

        int checkSize = (int)(35 * displayDensity);

        rowHeight = ((screenHeight - (44 * displayDensity)) / 9) - (4 * displayDensity);

        if (rowHeight < 46) {

            checkSize = 29;
        }

        Integer reminderTimeInt;

        if (!reminderTime.equals("")) {

            reminderTimeInt = Integer.parseInt(reminderTime);

            if (reminderTimeAmPm.equals("pm") && reminderTimeInt < 12) {

                reminderTimeInt = reminderTimeInt + 12;

            } else if (reminderTimeAmPm.equals("am") && reminderTimeInt == 12) {

                reminderTimeInt = 0;
            }

        } else {

            reminderTimeInt = 12;
        }

        final HTTimePickerDialog timePickerDialog = new HTTimePickerDialog(this, TimePickerDialog.THEME_HOLO_LIGHT, timePickerListener, reminderTimeInt, 0, false);

        if (whichMetric.equals("color")) {

            rowHeight = ((screenHeight - (44 * displayDensity)) / 10) - (4 * displayDensity);
        }

        Typeface typeReminderLabels = Typeface.createFromAsset(this.getAssets(), "fonts/AvenirNext-Regular.ttf");
        Typeface typeReminderTime = Typeface.createFromAsset(this.getAssets(), "fonts/OpenSans-Light.ttf");

        TextView textViewRemindersLabel;

        linearLayoutReminders.removeAllViewsInLayout();

        ////////////////////////////////////////////////////////////////
        // REMINDER TIME
        ////////////////////////////////////////////////////////////////

        rowView = inflater.inflate(R.layout.reminders_row, null, true);

        textViewRemindersLabel = (TextView) rowView.findViewById(R.id.textViewRemindersLabel);

        textViewRemindersLabel.setTypeface(typeReminderLabels);
        textViewRemindersLabel.setTextColor(this.getResources().getColor(R.color.ht_color_gray_text));
        textViewRemindersLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textViewRemindersLabel.setText("Text Reminder");

        if (rowHeight < 46) {

            textViewRemindersLabel.setPadding(0, 0, 0, (int)(6 * displayDensity));
        }

        params = (RelativeLayout.LayoutParams)textViewRemindersLabel.getLayoutParams();

        if (rowHeight < 46) {

            params.height = (int)rowHeight + (int)(6 * displayDensity);

        } else {

            params.height = (int)rowHeight;
        }

        params.setMargins((int)(16 * displayDensity), 0, 0, (int)(6 * displayDensity));
        textViewRemindersLabel.setLayoutParams(params);

        editTextReminderTime = (EditText) rowView.findViewById(R.id.editTextReminder);

        editTextReminderTime.setTypeface(typeReminderTime);
        editTextReminderTime.setTextColor(this.getResources().getColor(R.color.ht_color_gray_text));
        editTextReminderTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        if (!reminderTime.equals("")) {

            editTextReminderTime.setText(reminderTime + ":00" + reminderTimeAmPm);
        }

        params = (RelativeLayout.LayoutParams)editTextReminderTime.getLayoutParams();
        params.height = (int)(31 * displayDensity);
        params.width = (int)(90 * displayDensity);
        params.setMargins(0, (((int)rowHeight - (int)(29 * displayDensity)) / 2), (int)(16 * displayDensity), 0);
        editTextReminderTime.setLayoutParams(params);

        editTextReminderTime.setFocusableInTouchMode(false);

        editTextReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timePickerDialog.show();
            }
        });

        linearLayoutReminders.addView(rowView);

        ////////////////////////////////////////////////////////////////
        // COLOR FOR...
        ////////////////////////////////////////////////////////////////

        if (whichMetric.equals("color")) {

            rowView = inflater.inflate(R.layout.reminders_row, null, true);

            textViewRemindersLabel = (TextView) rowView.findViewById(R.id.textViewRemindersLabel);

            textViewRemindersLabel.setTypeface(typeReminderLabels);
            textViewRemindersLabel.setTextColor(this.getResources().getColor(R.color.ht_color_gray_text));
            textViewRemindersLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textViewRemindersLabel.setText("To color my day:");

            if (rowHeight < 46) {

                textViewRemindersLabel.setPadding(0, 0, 0, (int)(6 * displayDensity));
            }

            params = (RelativeLayout.LayoutParams)textViewRemindersLabel.getLayoutParams();
            params.height = (int) rowHeight;
            params.setMargins((int)(16 * displayDensity), 0, 0, (int)(6 * displayDensity));

            textViewRemindersLabel.setLayoutParams(params);

            linearLayoutReminders.addView(rowView);

            ImageView reminderRadioYesterday = (ImageView) rowView.findViewById(R.id.imageViewRadioYesterday);
            ImageView reminderRadioToday = (ImageView) rowView.findViewById(R.id.imageViewRadioToday);

            TextView textViewYesterdayLabel = (TextView) rowView.findViewById(R.id.textViewYesterdayLabel);
            TextView textViewTodayLabel = (TextView) rowView.findViewById(R.id.textViewTodayLabel);

            reminderRadioYesterday.setImageResource(R.drawable.ht_color_day_button_off);
            reminderRadioToday.setImageResource(R.drawable.ht_color_day_button_off);

            textViewYesterdayLabel.setTypeface(typeReminderLabels);
            textViewYesterdayLabel.setTextColor(this.getResources().getColor(R.color.ht_color_gray_text));

            if (screenWidth == 320) {

                textViewYesterdayLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            } else {

                textViewYesterdayLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }

            textViewYesterdayLabel.setText("Yesterday");

            if (rowHeight < 46) {

                textViewYesterdayLabel.setPadding(0, 0, 0, (int)(4 * displayDensity));
            }

            textViewTodayLabel.setTypeface(typeReminderLabels);
            textViewTodayLabel.setTextColor(this.getResources().getColor(R.color.ht_color_gray_text));

            if (screenWidth == 320) {

                textViewTodayLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            } else {

                textViewTodayLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }

            textViewTodayLabel.setText("Today");

            if (rowHeight < 46) {

                textViewTodayLabel.setPadding(0, 0, 0, (int)(4 * displayDensity));
            }

            if (reminderColorYesterdayChecked) {

                reminderRadioYesterday.setImageResource(R.drawable.ht_color_day_button_on);

            } else if (reminderColorTodayChecked) {

                reminderRadioToday.setImageResource(R.drawable.ht_color_day_button_on);
            }

            params = (RelativeLayout.LayoutParams)reminderRadioYesterday.getLayoutParams();

            if (screenWidth == 320) {

                params.height = (int)(rowHeight - (4 * displayDensity));
                params.width = (int)(40 * displayDensity);

            } else {

                params.height = (int)(rowHeight + (2 * displayDensity));
                params.width = (int)(56 * displayDensity);
            }

            reminderRadioYesterday.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams)reminderRadioToday.getLayoutParams();

            if (screenWidth == 320) {

                params.height = (int)(rowHeight - (4 * displayDensity));
                params.width = (int)(40 * displayDensity);

            } else {

                params.height = (int)(rowHeight + (2 * displayDensity));
                params.width = (int)(56 * displayDensity);
            }

            reminderRadioToday.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams)textViewYesterdayLabel.getLayoutParams();
            params.height = (int)rowHeight;
            params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
            params.setMargins(0, 0, 0, (int)(6 * displayDensity));
            textViewYesterdayLabel.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams)textViewTodayLabel.getLayoutParams();
            params.height = (int)rowHeight;
            params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
            params.setMargins(0, 0, 0, (int)(6 * displayDensity));
            textViewTodayLabel.setLayoutParams(params);

            reminderRadioYesterday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    radioButtonChecked("yesterday");
                }
            });

            reminderRadioToday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    radioButtonChecked("today");
                }
            });
        }

        ////////////////////////////////////////////////////////////////
        // DAILY
        ////////////////////////////////////////////////////////////////

        rowView = inflater.inflate(R.layout.reminders_row, null, true);

        textViewRemindersLabel = (TextView) rowView.findViewById(R.id.textViewRemindersLabel);

        textViewRemindersLabel.setTypeface(typeReminderLabels);
        textViewRemindersLabel.setTextColor(this.getResources().getColor(R.color.ht_color_gray_text));
        textViewRemindersLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textViewRemindersLabel.setText("Daily");

        if (rowHeight < 46) {

            textViewRemindersLabel.setPadding(0, 0, 0, (int)(6 * displayDensity));
        }

        params = (RelativeLayout.LayoutParams)textViewRemindersLabel.getLayoutParams();
        params.height = (int) rowHeight;
        params.setMargins((int)(16 * displayDensity), 0, 0, (int)(6 * displayDensity));
        textViewRemindersLabel.setLayoutParams(params);

        reminderCheckboxDaily = (ImageView) rowView.findViewById(R.id.imageViewReminderCheck);

        params = (RelativeLayout.LayoutParams)reminderCheckboxDaily.getLayoutParams();
        params.height = checkSize;
        params.width = checkSize;
        params.setMargins(0, (((int)rowHeight - (int)(33 * displayDensity)) / 2), (int)(16 * displayDensity), 0);
        reminderCheckboxDaily.setLayoutParams(params);

        reminderCheckboxDaily.setImageResource(R.drawable.ht_check_off_green);

        if (reminderDailyChecked) {

            reminderCheckboxDaily.setImageResource(R.drawable.ht_check_on_green);
        }

        reminderCheckboxDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkBoxChecked("daily");
            }
        });

        linearLayoutReminders.addView(rowView);

        ////////////////////////////////////////////////////////////////
        // MONDAY
        ////////////////////////////////////////////////////////////////

        rowView = inflater.inflate(R.layout.reminders_row, null, true);

        textViewRemindersLabel = (TextView) rowView.findViewById(R.id.textViewRemindersLabel);

        textViewRemindersLabel.setTypeface(typeReminderLabels);
        textViewRemindersLabel.setTextColor(this.getResources().getColor(R.color.ht_color_gray_text));
        textViewRemindersLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textViewRemindersLabel.setText("Monday");

        if (rowHeight < 46) {

            textViewRemindersLabel.setPadding(0, 0, 0, (int)(6 * displayDensity));
        }

        params = (RelativeLayout.LayoutParams)textViewRemindersLabel.getLayoutParams();
        params.height = (int) rowHeight;
        params.setMargins((int)(16 * displayDensity), 0, 0, (int)(6 * displayDensity));
        textViewRemindersLabel.setLayoutParams(params);

        reminderCheckboxMonday = (ImageView) rowView.findViewById(R.id.imageViewReminderCheck);

        params = (RelativeLayout.LayoutParams)reminderCheckboxMonday.getLayoutParams();
        params.height = checkSize;
        params.width = checkSize;
        params.setMargins(0, (((int)rowHeight - (int)(33 * displayDensity)) / 2), (int)(16 * displayDensity), 0);
        reminderCheckboxMonday.setLayoutParams(params);

        reminderCheckboxMonday.setImageResource(R.drawable.ht_check_off_green);

        if (reminderMondayChecked) {

            reminderCheckboxMonday.setImageResource(R.drawable.ht_check_on_green);
        }

        reminderCheckboxMonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkBoxChecked("monday");
            }
        });

        linearLayoutReminders.addView(rowView);

        ////////////////////////////////////////////////////////////////
        // TUESDAY
        ////////////////////////////////////////////////////////////////

        rowView = inflater.inflate(R.layout.reminders_row, null, true);

        textViewRemindersLabel = (TextView) rowView.findViewById(R.id.textViewRemindersLabel);

        textViewRemindersLabel.setTypeface(typeReminderLabels);
        textViewRemindersLabel.setTextColor(this.getResources().getColor(R.color.ht_color_gray_text));
        textViewRemindersLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textViewRemindersLabel.setText("Tuesday");

        if (rowHeight < 46) {

            textViewRemindersLabel.setPadding(0, 0, 0, (int)(6 * displayDensity));
        }

        params = (RelativeLayout.LayoutParams)textViewRemindersLabel.getLayoutParams();
        params.height = (int) rowHeight;
        params.setMargins((int)(16 * displayDensity), 0, 0, (int)(6 * displayDensity));
        textViewRemindersLabel.setLayoutParams(params);

        reminderCheckboxTuesday = (ImageView) rowView.findViewById(R.id.imageViewReminderCheck);

        params = (RelativeLayout.LayoutParams)reminderCheckboxTuesday.getLayoutParams();
        params.height = checkSize;
        params.width = checkSize;
        params.setMargins(0, (((int)rowHeight - (int)(33 * displayDensity)) / 2), (int)(16 * displayDensity), 0);
        reminderCheckboxTuesday.setLayoutParams(params);

        reminderCheckboxTuesday.setImageResource(R.drawable.ht_check_off_green);

        if (reminderTuesdayChecked) {

            reminderCheckboxTuesday.setImageResource(R.drawable.ht_check_on_green);
        }

        reminderCheckboxTuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkBoxChecked("tuesday");
            }
        });

        linearLayoutReminders.addView(rowView);

        ////////////////////////////////////////////////////////////////
        // WEDNESDAY
        ////////////////////////////////////////////////////////////////

        rowView = inflater.inflate(R.layout.reminders_row, null, true);

        textViewRemindersLabel = (TextView) rowView.findViewById(R.id.textViewRemindersLabel);

        textViewRemindersLabel.setTypeface(typeReminderLabels);
        textViewRemindersLabel.setTextColor(this.getResources().getColor(R.color.ht_color_gray_text));
        textViewRemindersLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textViewRemindersLabel.setText("Wednesday");

        if (rowHeight < 46) {

            textViewRemindersLabel.setPadding(0, 0, 0, (int)(6 * displayDensity));
        }

        params = (RelativeLayout.LayoutParams)textViewRemindersLabel.getLayoutParams();
        params.height = (int) rowHeight;
        params.setMargins((int)(16 * displayDensity), 0, 0, (int)(6 * displayDensity));
        textViewRemindersLabel.setLayoutParams(params);

        reminderCheckboxWednesday = (ImageView) rowView.findViewById(R.id.imageViewReminderCheck);

        params = (RelativeLayout.LayoutParams)reminderCheckboxWednesday.getLayoutParams();
        params.height = checkSize;
        params.width = checkSize;
        params.setMargins(0, (((int)rowHeight - (int)(33 * displayDensity)) / 2), (int)(16 * displayDensity), 0);
        reminderCheckboxWednesday.setLayoutParams(params);

        reminderCheckboxWednesday.setImageResource(R.drawable.ht_check_off_green);

        if (reminderWednesdayChecked) {

            reminderCheckboxWednesday.setImageResource(R.drawable.ht_check_on_green);
        }

        reminderCheckboxWednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkBoxChecked("wednesday");
            }
        });

        linearLayoutReminders.addView(rowView);

        ////////////////////////////////////////////////////////////////
        // THURSDAY
        ////////////////////////////////////////////////////////////////

        rowView = inflater.inflate(R.layout.reminders_row, null, true);

        textViewRemindersLabel = (TextView) rowView.findViewById(R.id.textViewRemindersLabel);

        textViewRemindersLabel.setTypeface(typeReminderLabels);
        textViewRemindersLabel.setTextColor(this.getResources().getColor(R.color.ht_color_gray_text));
        textViewRemindersLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textViewRemindersLabel.setText("Thursday");

        if (rowHeight < 46) {

            textViewRemindersLabel.setPadding(0, 0, 0, (int)(6 * displayDensity));
        }

        params = (RelativeLayout.LayoutParams)textViewRemindersLabel.getLayoutParams();
        params.height = (int) rowHeight;
        params.setMargins((int)(16 * displayDensity), 0, 0, (int)(6 * displayDensity));
        textViewRemindersLabel.setLayoutParams(params);

        reminderCheckboxThursday = (ImageView) rowView.findViewById(R.id.imageViewReminderCheck);

        params = (RelativeLayout.LayoutParams)reminderCheckboxThursday.getLayoutParams();
        params.height = checkSize;
        params.width = checkSize;
        params.setMargins(0, (((int)rowHeight - (int)(33 * displayDensity)) / 2), (int)(16 * displayDensity), 0);
        reminderCheckboxThursday.setLayoutParams(params);

        reminderCheckboxThursday.setImageResource(R.drawable.ht_check_off_green);

        if (reminderThursdayChecked) {

            reminderCheckboxThursday.setImageResource(R.drawable.ht_check_on_green);
        }

        reminderCheckboxThursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkBoxChecked("thursday");
            }
        });

        linearLayoutReminders.addView(rowView);

        ////////////////////////////////////////////////////////////////
        // FRIDAY
        ////////////////////////////////////////////////////////////////

        rowView = inflater.inflate(R.layout.reminders_row, null, true);

        textViewRemindersLabel = (TextView) rowView.findViewById(R.id.textViewRemindersLabel);

        textViewRemindersLabel.setTypeface(typeReminderLabels);
        textViewRemindersLabel.setTextColor(this.getResources().getColor(R.color.ht_color_gray_text));
        textViewRemindersLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textViewRemindersLabel.setText("Friday");

        if (rowHeight < 46) {

            textViewRemindersLabel.setPadding(0, 0, 0, (int)(6 * displayDensity));
        }

        params = (RelativeLayout.LayoutParams)textViewRemindersLabel.getLayoutParams();
        params.height = (int) rowHeight;
        params.setMargins((int)(16 * displayDensity), 0, 0, (int)(6 * displayDensity));
        textViewRemindersLabel.setLayoutParams(params);

        reminderCheckboxFriday = (ImageView) rowView.findViewById(R.id.imageViewReminderCheck);

        params = (RelativeLayout.LayoutParams)reminderCheckboxFriday.getLayoutParams();
        params.height = checkSize;
        params.width = checkSize;
        params.setMargins(0, (((int)rowHeight - (int)(33 * displayDensity)) / 2), (int)(16 * displayDensity), 0);
        reminderCheckboxFriday.setLayoutParams(params);

        reminderCheckboxFriday.setImageResource(R.drawable.ht_check_off_green);

        if (reminderFridayChecked) {

            reminderCheckboxFriday.setImageResource(R.drawable.ht_check_on_green);
        }

        reminderCheckboxFriday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkBoxChecked("friday");
            }
        });

        linearLayoutReminders.addView(rowView);

        ////////////////////////////////////////////////////////////////
        // SATURDAY
        ////////////////////////////////////////////////////////////////

        rowView = inflater.inflate(R.layout.reminders_row, null, true);

        textViewRemindersLabel = (TextView) rowView.findViewById(R.id.textViewRemindersLabel);

        textViewRemindersLabel.setTypeface(typeReminderLabels);
        textViewRemindersLabel.setTextColor(this.getResources().getColor(R.color.ht_color_gray_text));
        textViewRemindersLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textViewRemindersLabel.setText("Saturday");

        if (rowHeight < 46) {

            textViewRemindersLabel.setPadding(0, 0, 0, (int)(6 * displayDensity));
        }

        params = (RelativeLayout.LayoutParams)textViewRemindersLabel.getLayoutParams();
        params.height = (int) rowHeight;
        params.setMargins((int)(16 * displayDensity), 0, 0, (int)(6 * displayDensity));
        textViewRemindersLabel.setLayoutParams(params);

        reminderCheckboxSaturday = (ImageView) rowView.findViewById(R.id.imageViewReminderCheck);

        params = (RelativeLayout.LayoutParams)reminderCheckboxSaturday.getLayoutParams();
        params.height = checkSize;
        params.width = checkSize;
        params.setMargins(0, (((int)rowHeight - (int)(33 * displayDensity)) / 2), (int)(16 * displayDensity), 0);
        reminderCheckboxSaturday.setLayoutParams(params);

        reminderCheckboxSaturday.setImageResource(R.drawable.ht_check_off_green);

        if (reminderSaturdayChecked) {

            reminderCheckboxSaturday.setImageResource(R.drawable.ht_check_on_green);
        }

        reminderCheckboxSaturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkBoxChecked("saturday");
            }
        });

        linearLayoutReminders.addView(rowView);

        ////////////////////////////////////////////////////////////////
        // SUNDAY
        ////////////////////////////////////////////////////////////////

        rowView = inflater.inflate(R.layout.reminders_row, null, true);

        textViewRemindersLabel = (TextView) rowView.findViewById(R.id.textViewRemindersLabel);

        textViewRemindersLabel.setTypeface(typeReminderLabels);
        textViewRemindersLabel.setTextColor(this.getResources().getColor(R.color.ht_color_gray_text));
        textViewRemindersLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textViewRemindersLabel.setText("Sunday");

        if (rowHeight < 46) {

            textViewRemindersLabel.setPadding(0, 0, 0, (int)(6 * displayDensity));
        }

        params = (RelativeLayout.LayoutParams)textViewRemindersLabel.getLayoutParams();
        params.height = (int) rowHeight;
        params.setMargins((int)(16 * displayDensity), 0, 0, (int)(6 * displayDensity));
        textViewRemindersLabel.setLayoutParams(params);

        reminderCheckboxSunday = (ImageView) rowView.findViewById(R.id.imageViewReminderCheck);

        params = (RelativeLayout.LayoutParams)reminderCheckboxSunday.getLayoutParams();
        params.height = checkSize;
        params.width = checkSize;
        params.setMargins(0, (((int)rowHeight - (int)(33 * displayDensity)) / 2), (int)(16 * displayDensity), 0);
        reminderCheckboxSunday.setLayoutParams(params);

        reminderCheckboxSunday.setImageResource(R.drawable.ht_check_off_green);

        if (reminderSundayChecked) {

            reminderCheckboxSunday.setImageResource(R.drawable.ht_check_on_green);
        }

        reminderCheckboxSunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkBoxChecked("sunday");
            }
        });

        linearLayoutReminders.addView(rowView);
    }
}