package com.sph.healthtrac.planner.addfood;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.obsessive.zbar.CameraManager;
import com.obsessive.zbar.CameraPreview;

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

public class HTBarCodeCaptureActivity extends Activity {

    private static RelativeLayout mainContentLayout;
    private static InputMethodManager imm;

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private CameraManager mCameraManager;

    private FrameLayout scanPreview;
    private RelativeLayout scanCropView;
//    private ImageView scanLine;

    private Rect mCropRect = null;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    private ImageScanner mImageScanner = null;

    private TextView resultView;

    private String defaultMessage = "Bring barcode into square to scan it";

    private String foodSearchMeal = "";
    private String foodSearchUPC = "";
    private String foodSearchItemId = "";

    static {
        System.loadLibrary("iconv");
    }

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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_capture);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        foodSearchMeal = getIntent().getStringExtra("foodSearchMeal");

        scanPreview = (FrameLayout) findViewById(R.id.capture_preview);
        mainContentLayout = (RelativeLayout) findViewById(R.id.capture_containter);
        scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_layout);

        resultView = (TextView) findViewById(R.id.resultView);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        RelativeLayout.LayoutParams params;
        // action bar
        View mActionBar = null;
        mActionBar = HTActionBar.getActionBar(this, "Scan a Barcode", "leftArrow", "");

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

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
//        initViews();
        autoFocusHandler = new Handler();
    }

    public void onResume() {
       super.onResume();

        initViews();
    }

    private void initViews() {
        previewing = true;

        resultView.setText(defaultMessage);

        mImageScanner = new ImageScanner();
        mImageScanner.setConfig(0, Config.X_DENSITY, 3);
        mImageScanner.setConfig(0, Config.Y_DENSITY, 3);

        mCameraManager = new CameraManager(this);
        try {
            mCameraManager.openDriver();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera = mCameraManager.getCamera();
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        scanPreview.addView(mPreview);

//        TranslateAnimation animation = new TranslateAnimation(
//                Animation.RELATIVE_TO_PARENT, 0.0f,
//                Animation.RELATIVE_TO_PARENT, 0.0f,
//                Animation.RELATIVE_TO_PARENT, 0.0f,
//                Animation.RELATIVE_TO_PARENT, 0.95f);
//        animation.setDuration(2000);
//        animation.setRepeatCount(-1);
//        animation.setRepeatMode(Animation.REVERSE);
//        scanLine.startAnimation(animation);
    }

    public void onPause() {
        super.onPause();
        releaseCamera();

        scanPreview.removeAllViews();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Size size = camera.getParameters().getPreviewSize();

            byte[] rotatedData = new byte[data.length];
            for (int y = 0; y < size.height; y++) {
                for (int x = 0; x < size.width; x++)
                    rotatedData[x * size.height + size.height - y - 1] = data[x
                            + y * size.width];
            }

            byte[] new_rotatedData = new byte[data.length];

            int tmp = size.width;
            size.width = size.height;
            size.height = tmp;

            initCrop();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(rotatedData);
            barcode.setCrop(mCropRect.left, mCropRect.top, mCropRect.width(),
                    mCropRect.height());

            int result = mImageScanner.scanImage(barcode);
            String resultStr = null;

            if (result != 0) {
                SymbolSet syms = mImageScanner.getResults();
                for (Symbol sym : syms) {
                    resultStr = sym.getData();
                }
            }

            if (!TextUtils.isEmpty(resultStr)) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                barcodeScanned = true;
                resultView.setText(resultStr);
                foodSearchUPC = resultStr;
                getUPCSearchResults();
            }
        }
    };

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    private void initCrop() {
        int cameraWidth = mCameraManager.getCameraResolution().y;
        int cameraHeight = mCameraManager.getCameraResolution().x;

        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        int containerWidth = mainContentLayout.getWidth();
        int containerHeight = mainContentLayout.getHeight();

        int x = cropLeft * cameraWidth / containerWidth;
        int y = cropTop * cameraHeight / containerHeight;

        int width = cropWidth * cameraWidth / containerWidth;
        int height = cropHeight * cameraHeight / containerHeight;

        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void restartScan(){
        if (barcodeScanned) {
            barcodeScanned = false;
            resultView.setText(defaultMessage);
            mCamera.setPreviewCallback(previewCb);
            mCamera.startPreview();
            previewing = true;
            mCamera.autoFocus(autoFocusCB);
        }
    }

    private void getUPCSearchResults() {

        foodSearchItemId = "";

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_food_search_results"));
                    nameValuePairs.add(new BasicNameValuePair("nutritionix", "true"));
                    nameValuePairs.add(new BasicNameValuePair("WhichCategory", "upc"));
                    nameValuePairs.add(new BasicNameValuePair("upc", foodSearchUPC));
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
                    NodeList nodes = doc.getElementsByTagName("add_food_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTBarCodeCaptureActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                String tempString = "";

                                if (XMLFunctions.tagExists(e2, "food_id_1")) {
                                    tempString = XMLFunctions.getValue(e2, "food_id_1");
                                    foodSearchItemId = HTGlobals.getInstance().cleanStringAfterReceiving(tempString);
                                }

                                if("".equals(foodSearchItemId)){
                                    showItemNotFoundDlg();
                                }else{
                                    Intent intent = new Intent(HTBarCodeCaptureActivity.this, HTAddFoodSelectItemActivity.class);
                                    intent.putExtra("showChangeFoodSelection", false);
                                    intent.putExtra("addFoodCategory", "general");
                                    intent.putExtra("selectedFoodID", foodSearchItemId);
                                    intent.putExtra("inTemplateString", "");

                                    // new planner time slots
                                    intent.putExtra("selectedFoodTimeFraction", ":00");
                                    if("Breakfast".equals(foodSearchMeal)){
                                        intent.putExtra("selectedFoodTime", "9");
                                        intent.putExtra("selectedFoodTimeAmPm", "am");
                                    } else if("Lunch".equals(foodSearchMeal)){
                                        intent.putExtra("selectedFoodTime", "13");
                                        intent.putExtra("selectedFoodTimeAmPm", "pm");
                                    } else if("Dinner".equals(foodSearchMeal)){
                                        intent.putExtra("selectedFoodTime", "18");
                                        intent.putExtra("selectedFoodTimeAmPm", "pm");
                                    } else { //snack
                                        intent.putExtra("selectedFoodTime", "16");
                                        intent.putExtra("selectedFoodTimeAmPm", "pm");
                                    }

                                    startActivityForResult(intent, 1);
                                }

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

//                                    toast = HTToast.showToast(HTBarCodeCaptureActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTBarCodeCaptureActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showItemNotFoundDlg(){
        Dialog dialog = new AlertDialog.Builder(HTBarCodeCaptureActivity.this)
                .setTitle("Item Not Found")
                .setCancelable(false)
                .setMessage("Would you like to add this item manually?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(HTBarCodeCaptureActivity.this, HTAddFoodSelectItemActivity.class);
                        intent.putExtra("showChangeFoodSelection", false);
                        intent.putExtra("addFoodCategory", "quickadd");

                        intent.putExtra("inTemplateString", "");

                        // new planner time slots
                        intent.putExtra("selectedFoodTimeFraction", ":00");
                        if("Breakfast".equals(foodSearchMeal)){
                            intent.putExtra("selectedFoodTime", "9");
                            intent.putExtra("selectedFoodTimeAmPm", "am");
                        } else if("Lunch".equals(foodSearchMeal)){
                            intent.putExtra("selectedFoodTime", "13");
                            intent.putExtra("selectedFoodTimeAmPm", "pm");
                        } else if("Dinner".equals(foodSearchMeal)){
                            intent.putExtra("selectedFoodTime", "18");
                            intent.putExtra("selectedFoodTimeAmPm", "pm");
                        } else { //snack
                            intent.putExtra("selectedFoodTime", "16");
                            intent.putExtra("selectedFoodTimeAmPm", "pm");
                        }

                        startActivityForResult(intent, 1);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restartScan();
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == 2) {
            setResult(2);
            finish();
            return;
        }
    }
}