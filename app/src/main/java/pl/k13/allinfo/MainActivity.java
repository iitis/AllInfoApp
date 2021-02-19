package pl.k13.allinfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
{
    private static final String LOG_TAG = "WifiInfo";
    private static final int MY_REQUEST_CODE = 2;
    public static final String NOTIFYCHANNEL_ID = "WifiInfo";
    private List<String> wifiLogString = new ArrayList<>();
    private List<JSONObject> wifiLogJson = new ArrayList<>();

    SharedPreferences sharedpreferences;
    private static final String STREXPID = "ExperimentID";
    public static String ExperimentID = "ExperimentID";
    public static String stringUserPhoneLocation = "notset";
    public static String stringUserMotion = "notset";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        (findViewById(R.id.btn_startstop)).setOnClickListener(this::onClick_btn_startstop);


        sharedpreferences = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (sharedpreferences.contains(STREXPID))
        {
            ((EditText) findViewById(R.id.expIdText)).setText(sharedpreferences.getString(STREXPID, ""));
            ExperimentID = sharedpreferences.getString(STREXPID, "");
        }

        ((EditText) findViewById(R.id.expIdText)).addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                Log.d(LOG_TAG, "onTextChanged " + s.toString());
                ExperimentID = s.toString();
                if (s.toString().length() > 0)
                {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(STREXPID, s.toString());
                    editor.commit();
                } else
                {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.remove(STREXPID);
                    editor.commit();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        ((EditText) findViewById(R.id.expIdText)).setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                String l = ((EditText) v).getText().toString();
                String r = getString(R.string.str4tv_exper);
                if (hasFocus)
                {
                    if (l.equals(r))
                    {
                        ((EditText) v).setText("");
                    }
                } else
                {
                    if (l.length() == 0)
                    {
                        ((EditText) v).setText(r);
                        ExperimentID = r;
                    }
                }
            }
        });

        ((RadioGroup) findViewById(R.id.userPhoneLocation))
                .setOnCheckedChangeListener((group, checkedId) -> stringUserPhoneLocation = ((RadioButton) findViewById(checkedId)).getText().toString());

        ((RadioGroup) findViewById(R.id.userMotionType))
                .setOnCheckedChangeListener((group, checkedId) -> stringUserMotion = ((RadioButton) findViewById(checkedId)).getText().toString());


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("wifiinfonew"));

        Log.d(LOG_TAG, "onCreate");
    }

    @Override
    public void onBackPressed()
    {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (this.isMyServiceRunning(AllInfoBackground.class))
        {
            ((Button) findViewById(R.id.btn_startstop)).setText(R.string.str4btn_startstop_stop);

        } else
        {
            ((Button) findViewById(R.id.btn_startstop)).setText(R.string.str4btn_startstop_start);
        }
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.deleteNotificationChannel(NOTIFYCHANNEL_ID);
        super.onDestroy();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // Get extra data included in the Intent
            WifiMeasurement info = (WifiMeasurement) intent.getParcelableExtra("measurement");

            wifiLogString.add(info.toStringShort());

            if (wifiLogString.size() >= 10)
            {
                wifiLogString.remove(0);
            }
//            TextView output = findViewById(R.id.outputText);
//            output.setKeyListener(null);
//            String log = "";
//            for (String str : wifiLogString)
//            {
//                log += str + "\n\n";
//            }
//            output.setText(log);
            Log.d("receiver", "Got message: " + info.getSsid() + " " + info.getBssid() + " " + info.getRssi());
        }
    };


    protected void onClick_btn_startstop(View btn)
    {
        btn.requestFocus();
        btn.requestFocusFromTouch();

        View view = this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        String l = ((EditText) findViewById(R.id.expIdText)).getText().toString();
        String r = getString(R.string.str4tv_exper);
        if (l.equals(r))
        {
            Toast.makeText(this, "Experiment ID must be set!", Toast.LENGTH_LONG).show();
        } else
        {
            startstopservice();
        }
    }

    protected void startstopservice()
    {
        Intent wifiinfointent = new Intent(this, AllInfoBackground.class);
        if (this.isMyServiceRunning(AllInfoBackground.class))
        {
            Log.d(LOG_TAG, "Stopping the service");
            stopService(wifiinfointent);
            ((Button) findViewById(R.id.btn_startstop)).setText(R.string.str4btn_startstop_start);

        } else
        {
            Log.d(LOG_TAG, "Service is starting...");
            getPermissions();
            startService(wifiinfointent);
            ((Button) findViewById(R.id.btn_startstop)).setText(R.string.str4btn_startstop_stop);
        }
    }

    private void getPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        { // 23
            int permissionSum = 0; //
            String[] perms = new String[]
                    {
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.INTERNET,
                            Manifest.permission.FOREGROUND_SERVICE,
                            Manifest.permission.ACTIVITY_RECOGNITION,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    };
            for (String perm : perms)
            {
                permissionSum += ContextCompat.checkSelfPermission(this, perm);
            }
            Log.d(LOG_TAG, "Sum of checkSelfPermission: " + Integer.toString(permissionSum));

            // Check for permissions
            if (permissionSum != PackageManager.PERMISSION_GRANTED)
            {
                Log.d(LOG_TAG, "Requesting Permissions");
                // Request permissions
                ActivityCompat.requestPermissions(this, perms, MY_REQUEST_CODE);
                Log.d(LOG_TAG, "Permissions Requested");

                return; // <-- without all permissions function will exit
            }
            Log.d(LOG_TAG, "Permissions Already Granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults)
    {
        Log.d(LOG_TAG, "onRequestPermissionsResult");

        switch (requestCode)
        {
            case MY_REQUEST_CODE:
            {
                if (grantResults.length > 0)
                {
                    int permSum = 0;
                    for (int i = 0; i < grantResults.length; i++)
                    {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        {
                            Log.d(LOG_TAG, "Permission Granted: " + permissions[i]);
                        } else
                        {
                            Log.d(LOG_TAG, "Permission DENIED!: " + permissions[i]);
                        }
                        permSum += grantResults[i];
                    }
                    Log.d(LOG_TAG, "Sum of PermissionsResult: " + Integer.toString(permSum));
                    if (permSum == PackageManager.PERMISSION_GRANTED) //Do it if all permissions are granted
                    {
                        startstopservice();
                    }
                }
                break;
            }

        }
    }

    private void createNotificationChannel()
    {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            CharSequence name = "wifiinfo";
            String description = "Recording wifi signal";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFYCHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }

}
