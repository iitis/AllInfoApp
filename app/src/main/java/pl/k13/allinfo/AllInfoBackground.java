package pl.k13.allinfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.DisplayManager;
import android.icu.text.SimpleDateFormat;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import static java.lang.Math.round;
import static pl.k13.allinfo.MainActivity.ExperimentID;
import static pl.k13.allinfo.MainActivity.stringUserMotion;
import static pl.k13.allinfo.MainActivity.stringUserPhoneLocation;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class AllInfoBackground extends Service implements SensorEventListener
{
    private ServiceHandler serviceHandler;
    private static final String LOG_TAG = "AllMeasurementsService";
    private Timer timerMainTick;
    private Timer timer500msTick;
    private Timer timerPosting;


    WifiManager manager;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor gyroscopeSensor;
    private Sensor magneticFieldSensor;
    private Sensor stepCountSensor;
    private Sensor lightSensor;
    private Sensor proximitySensor;
    private Sensor stationaryDetectSensor;
    private Sensor motionDetectSensor;
    private Sensor significantMotionSensor;
    HashMap<Long, AllMeasurements> allMeasurementsHashMap = new HashMap<>();
    private final String deviceId = android.os.Build.MODEL; //android.os.Build.MANUFACTURER + android.os.Build.PRODUCT
    private int lastAngle = -360;
    BroadcastReceiver activityBroadcastReceiver;
    BroadcastReceiver angleBroadcastReceiver;


    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean accelerometerNotUsed = false;
    private boolean magnetometerNotUsed = false;

    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];

    private Intent mIntentService;
    private PendingIntent mPendingIntent;
    private ActivityRecognitionClient mActivityRecognitionClient;
    IBinder mBinder = new AllInfoBackground.LocalBinder();

    public class LocalBinder extends Binder
    {
        public AllInfoBackground getServerInstance()
        {
            return AllInfoBackground.this;
        }
    }

    private Long measureInit()
    {
        long currentTime = System.currentTimeMillis() / 1000;
        if (!allMeasurementsHashMap.containsKey(currentTime))
        {
            AllMeasurements newAllMess = new AllMeasurements();
            newAllMess.setTimestamp(currentTime * 1000);
            newAllMess.setDeviceId(deviceId);
            newAllMess.setExperimentId(ExperimentID);
            allMeasurementsHashMap.put(currentTime, newAllMess);
        }
        return currentTime;
    }

    private void getOrientation()
    {
        if (accelerometerNotUsed && magnetometerNotUsed)
        {
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix, orientation);
            Objects.requireNonNull(allMeasurementsHashMap.get(measureInit())).addOrientation(orientation[0], orientation[1], orientation[2]);
            accelerometerNotUsed = false;
            magnetometerNotUsed = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        switch (event.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                Objects.requireNonNull(allMeasurementsHashMap.get(measureInit())).addAccelerometer(event.values[0], event.values[1], event.values[2]);
                System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
                accelerometerNotUsed = true;
                getOrientation();
                break;
            case Sensor.TYPE_GYROSCOPE:
                Objects.requireNonNull(allMeasurementsHashMap.get(measureInit())).addGyroscope(event.values[0], event.values[1], event.values[2]);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
                magnetometerNotUsed = true;
                getOrientation();
                break;
            case Sensor.TYPE_STEP_COUNTER:
                Objects.requireNonNull(allMeasurementsHashMap.get(measureInit())).setSteps((int) event.values[0]);
                break;
            case Sensor.TYPE_LIGHT:
                Objects.requireNonNull(allMeasurementsHashMap.get(measureInit())).addLight(event.values[0]);
                break;
            case Sensor.TYPE_PROXIMITY:
                Objects.requireNonNull(allMeasurementsHashMap.get(measureInit())).addProximity(event.values[0]);
                break;
            case Sensor.TYPE_STATIONARY_DETECT:
                Objects.requireNonNull(allMeasurementsHashMap.get(measureInit())).setStationary(event.values[0]);
                break;
            case Sensor.TYPE_MOTION_DETECT:
                Objects.requireNonNull(allMeasurementsHashMap.get(measureInit())).setMotion(event.values[0]);
                break;
            case Sensor.TYPE_SIGNIFICANT_MOTION:
                Objects.requireNonNull(allMeasurementsHashMap.get(measureInit())).setSignificantMotion(event.values[0]);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        //Log.d(LOG_TAG, "Sensor " + sensor.getName() + " has changed accuracy to: " + accuracy);
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler
    {
        public ServiceHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(@NotNull Message msg) //invoked by message from timer
        {
            readWifiInfo();
            readLteInfo();
            someReadingsUpdate1000ms();
            saveFile();
        }
    }

    protected void someReadingsUpdate1000ms()
    {
        Objects.requireNonNull(allMeasurementsHashMap.get(measureInit())).setUserMotion(stringUserMotion);
        Objects.requireNonNull(allMeasurementsHashMap.get(measureInit())).setUserPhoneLocation(stringUserPhoneLocation);
    }


    protected void someReadingsUpdate500ms()
    {
        Objects.requireNonNull(allMeasurementsHashMap.get(measureInit())).addScreenOn(isScreenOn(getApplicationContext()));
    }

    protected void readWifiInfo()
    {
        manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        assert manager != null;
        WifiInfo info = manager.getConnectionInfo();
        WifiMeasurement wifimes = new WifiMeasurement();
        wifimes.setSsid(info.getSSID());
        wifimes.setBssid(info.getBSSID());
        wifimes.setRssi(info.getRssi());
        wifimes.setAngle(lastAngle);
        allMeasurementsHashMap.get(measureInit()).setWifiMeasurement(wifimes);
    }

    protected void readLteInfo()
    {
        LTECellInfo lte = new LTECellInfo();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            List<CellInfo> cellInfoList;
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            cellInfoList = tm.getAllCellInfo();
            for (CellInfo cellInfo : cellInfoList)
            {
                if (cellInfo instanceof CellInfoLte)
                {
                    if (((CellInfoLte) cellInfo).isRegistered())
                    {
                        if (Build.VERSION.SDK_INT >= 28)
                            lte.setCI_NetName(((CellInfoLte) cellInfo).getCellIdentity().getOperatorAlphaLong().toString());
                        else
                            lte.setCI_NetName(tm.getNetworkOperatorName());

                        lte.setCI_Ci(((CellInfoLte) cellInfo).getCellIdentity().getCi());
                        lte.setCI_Mnc(((CellInfoLte) cellInfo).getCellIdentity().getMnc());
                        lte.setCI_Pci(((CellInfoLte) cellInfo).getCellIdentity().getPci());
                        lte.setCI_Tac(((CellInfoLte) cellInfo).getCellIdentity().getTac());

                        lte.setSS_Dbm(((CellInfoLte) cellInfo).getCellSignalStrength().getDbm());
                        lte.setSS_Rsrp(((CellInfoLte) cellInfo).getCellSignalStrength().getRsrp());
                        lte.setSS_Rsrq(((CellInfoLte) cellInfo).getCellSignalStrength().getRsrq());
                        lte.setSS_Rssnr(((CellInfoLte) cellInfo).getCellSignalStrength().getRssnr());
                    }
                }
            }
        }
        allMeasurementsHashMap.get(measureInit()).setLTEMeasurement(lte);
    }


    protected void sendPost()
    {

        if ((allMeasurementsHashMap != null) && (!allMeasurementsHashMap.isEmpty()))
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
//                        URL url = new URL("https://bacon-train-prod.api.meetlify.com/all-measurements");
                        URL url = new URL("https://bacon-train-dev.api.meetlify.com/all-measurements");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("accept", "*/*");
//                        conn.setRequestProperty("api-key", "644453aeaf475fc95c422714a807d68prod");
                        conn.setRequestProperty("api-key", "3d8c2f28f51645a66479b85d88c7050cdev");
                        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                        conn.setRequestProperty("Accept", "application/json");

                        conn.setDoOutput(true);
                        conn.setDoInput(true);

                        long timeKey = measureInit();

                        SortedSet<Long> keyList = new TreeSet<>(allMeasurementsHashMap.keySet());
                        for (Long key : keyList)
                        {
                            if (key != timeKey)
                            {
                                if (allMeasurementsHashMap.get(key).isSaved2file() && allMeasurementsHashMap.get(key).isPosted2db())
                                {
                                    allMeasurementsHashMap.remove(key);
                                    Log.i("REMOVED", "Key: " + key);
                                } else
                                {
                                    JSONArray jsonArray = new JSONArray();
                                    if (allMeasurementsHashMap.containsKey(key))
                                    {
                                        for (AllMeas2DB ent : allMeasurementsHashMap.get(key).toAllMesList())
                                        {
                                            jsonArray.put(ent.toJSONObject());
                                        }
                                    }

                                    JSONObject jsonCollection = new JSONObject();
                                    jsonCollection.put("collection", jsonArray);
                                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                                    os.writeBytes(jsonCollection.toString());
                                    os.flush();
                                    os.close();
                                    if (conn.getResponseCode() == 201) //TODO
                                        allMeasurementsHashMap.get(key).setPosted2db(true);
//
//                                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
//                                    Log.i("MSG", conn.getResponseMessage());

                                }
                            }
                        }

                        conn.disconnect();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        }
    }

    void saveFile()
    {
        if ((allMeasurementsHashMap != null) && (!allMeasurementsHashMap.isEmpty()))
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        BufferedOutputStream bos = null;
                        @SuppressLint("SimpleDateFormat") File todayFile = new File(getExternalFilesDir(null), new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "-pomiar.txt");
                        Log.d(LOG_TAG, "File: " + todayFile.getAbsolutePath());
                        File dir = new File(getExternalFilesDir(null).getAbsolutePath());
                        File[] filesInDir = dir.listFiles();
                        for (File f : filesInDir)  //deletes older files
                        {
                            if (!f.getAbsolutePath().contentEquals(todayFile.getAbsolutePath()))
                            {
                                if (f.delete())
                                    Log.d(LOG_TAG, "Deleted");
                            }
                        }
                        try
                        {
                            bos = new BufferedOutputStream(new FileOutputStream(todayFile, true));
                            long timeKey = measureInit();
                            SortedSet<Long> keyList = new TreeSet<>(allMeasurementsHashMap.keySet());
                            for (Long key : keyList)
                            {
                                if ((key != timeKey) && (!allMeasurementsHashMap.get(key).isSaved2file()))
                                {
                                    JSONArray jsonArray = new JSONArray();
                                    if (allMeasurementsHashMap.containsKey(key))
                                    {
                                        for (AllMeas2DB ent : allMeasurementsHashMap.get(key).toAllMesList())
                                        {
                                            jsonArray.put(ent.toJSONObject());
                                        }
                                    }
                                    bos.write(jsonArray.toString().getBytes());
                                    bos.write("\n".getBytes());
                                    Log.d(LOG_TAG, "File saved");
                                    allMeasurementsHashMap.get(key).setSaved2file(true);
                                }
                            }
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        } finally
                        {
                            try
                            {
                                if (bos != null) bos.close();
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }


    @Override
    public void onCreate()
    {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        Looper serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        mIntentService = new Intent(this, DetectedActivitiesIntentService.class);
        mPendingIntent = PendingIntent.getService(this, 1, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT);
        requestActivityUpdatesButtonHandler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText(this, "Zapis rozpoczęto (" + ExperimentID + ")", Toast.LENGTH_SHORT).show();

        Log.d(LOG_TAG, "start");

        timerMainTick = new Timer();
        timerMainTick.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                Message msg = serviceHandler.obtainMessage();
                serviceHandler.sendMessage(msg);
            }
        }, 0, 1000);

        timerPosting = new Timer();
        timerPosting.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                sendPost();
            }
        }, 0, 3000);

        timer500msTick = new Timer();
        timer500msTick.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                someReadingsUpdate500ms();
            }
        }, 0, 500);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometerSensor != null)
            sensorManager.registerListener(this, accelerometerSensor, 10000);

        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyroscopeSensor != null)
            sensorManager.registerListener(this, gyroscopeSensor, 10000);

        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticFieldSensor != null)
            sensorManager.registerListener(this, magneticFieldSensor, 10000);

        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCountSensor != null)
            sensorManager.registerListener(this, stepCountSensor, 1000000);

        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null)
            sensorManager.registerListener(this, lightSensor, 500000);

        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (proximitySensor != null)
            sensorManager.registerListener(this, proximitySensor, 250000);

        stationaryDetectSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STATIONARY_DETECT);
        if (stationaryDetectSensor != null)
            sensorManager.registerListener(this, stationaryDetectSensor, 1000000);

        motionDetectSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MOTION_DETECT);
        if (motionDetectSensor != null)
            sensorManager.registerListener(this, motionDetectSensor, 1000000);

        significantMotionSensor = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        if (significantMotionSensor != null)
            sensorManager.registerListener(this, significantMotionSensor, 1000000);


        angleBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                lastAngle = intent.getIntExtra("angle", -360);
                Toast.makeText(context, "Angle: " + lastAngle, Toast.LENGTH_LONG).show();
            }
        };
        this.registerReceiver(angleBroadcastReceiver, new IntentFilter("pl.k13.wifiinfo.Broadcast"));


        activityBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                int type = intent.getIntExtra("type", -1);
                int confidence = intent.getIntExtra("confidence", 0);
                handleUserActivity(type, confidence);
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(activityBroadcastReceiver,
                new IntentFilter("activity_intent"));


        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification =
                new Notification.Builder(this, MainActivity.NOTIFYCHANNEL_ID)
                        .setContentTitle("AllInfo")
                        .setContentText("Rejestracja danych...")
                        .setSmallIcon(R.drawable.ic_baseline_bluetooth_searching_24)
                        .setContentIntent(pendingIntent)
                        .build();
        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    @Override
    public void onDestroy()
    {
        if (accelerometerSensor != null)
            sensorManager.unregisterListener(this, accelerometerSensor);
        if (gyroscopeSensor != null)
            sensorManager.unregisterListener(this, gyroscopeSensor);
        if (magneticFieldSensor != null)
            sensorManager.unregisterListener(this, magneticFieldSensor);
        if (lightSensor != null)
            sensorManager.unregisterListener(this, lightSensor);
        if (proximitySensor != null)
            sensorManager.unregisterListener(this, proximitySensor);
        if (stationaryDetectSensor != null)
            sensorManager.unregisterListener(this, stationaryDetectSensor);
        if (stepCountSensor != null)
            sensorManager.unregisterListener(this, stepCountSensor);
        if (significantMotionSensor != null)
            sensorManager.unregisterListener(this, significantMotionSensor);
        if (motionDetectSensor != null)
            sensorManager.unregisterListener(this, motionDetectSensor);

        this.unregisterReceiver(angleBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(activityBroadcastReceiver);

        if (timerMainTick != null) timerMainTick.cancel();
        if (timerPosting != null) timerPosting.cancel();
        if (timer500msTick != null) timer500msTick.cancel();

        if ((allMeasurementsHashMap != null) && (!allMeasurementsHashMap.isEmpty()))
        {
            sendPost();
        }
        allMeasurementsHashMap.clear();
        removeActivityUpdatesButtonHandler();
        Toast.makeText(this, "Zapis zatrzymany", Toast.LENGTH_SHORT).show();
    }

    public void requestActivityUpdatesButtonHandler()
    {
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                5000,
                mPendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void result)
            {
                Toast.makeText(getApplicationContext(),
                        "Successfully requested activity updates",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        task.addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(getApplicationContext(),
                        "Requesting activity updates failed to start",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public void removeActivityUpdatesButtonHandler()
    {
        Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                mPendingIntent);
        task.addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void result)
            {
                Toast.makeText(getApplicationContext(),
                        "Removed activity updates successfully!",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        task.addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(getApplicationContext(), "Failed to remove activity updates!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void handleUserActivity(int type, int confidence)
    {
        String label = "";
        switch (type)
        {
            case DetectedActivity.IN_VEHICLE:
            {
                label = "IN_VEHICLE";
                break;
            }
            case DetectedActivity.ON_BICYCLE:
            {
                label = "ON_BICYCLE";
                break;
            }
            case DetectedActivity.ON_FOOT:
            {
                label = "ON_FOOT";
                break;
            }
            case DetectedActivity.WALKING:
            {
                label = "WALKING";
                break;
            }
            case DetectedActivity.RUNNING:
            {
                label = "RUNNING";
                break;
            }
            case DetectedActivity.STILL:
            {
                label = "STILL";
                break;
            }
            case DetectedActivity.TILTING:
            {
                label = "TILTING";
                break;
            }
            case DetectedActivity.UNKNOWN:
            {
                label = "UNKNOWN";
                break;
            }
        }
        Objects.requireNonNull(allMeasurementsHashMap.get(measureInit())).setGoogleActivity(label, confidence);
        Log.e(LOG_TAG, "User activity: " + label + ", Confidence: " + confidence);
    }


    /**
     * Is the screen of the device on.
     *
     * @param context the context
     * @return true when (at least one) screen is on
     */
    public boolean isScreenOn(Context context)
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH)
        {
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            boolean screenOn = false;
            for (Display display : dm.getDisplays())
            {
                if (display.getState() != Display.STATE_OFF)
                {
                    screenOn = true;
                }
            }
            return screenOn;
        } else
        {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            //noinspection deprecation
            return pm.isScreenOn();
        }
    }

}