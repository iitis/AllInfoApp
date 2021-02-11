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
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;
import android.os.Process;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.round;
import static pl.k13.allinfo.MainActivity.ExperimentID;

public class AllInfoBackground extends Service implements SensorEventListener
{
    private ServiceHandler serviceHandler;
    private static final String LOG_TAG = "AllMeasurementsService";
    private static final int ONGOING_NOTIFICATION_ID = 3;
    private Timer timerMainTick;
    private Timer timerSaveSend;

    WifiManager manager;

    private SensorManager sensorManager;
    private Sensor stepCountSensor;
    private Sensor lightSensor;
    private Sensor proximitySensor;
    private Sensor stationaryDetectSensor;
    private Sensor motionDetectSensor;
    private Sensor significantMotionSensor;
    HashMap<Long, AllMeasurements> allMeasurementsHashMap = new HashMap<>();
    private final String deviceId = android.os.Build.MODEL; //android.os.Build.MANUFACTURER + android.os.Build.PRODUCT
    private int lastAngle = -360;
    BroadcastReceiver angleBroadcastReceiver;

    private Long measureInit()
    {
        long currentTime = System.currentTimeMillis() / 1000;
        if (!allMeasurementsHashMap.containsKey(currentTime))
        {
            AllMeasurements newAllMess = new AllMeasurements();
            newAllMess.setTimestamp(currentTime*1000);
            newAllMess.setDeviceId(deviceId);
            newAllMess.setExperimentId(ExperimentID);
            allMeasurementsHashMap.put(currentTime, newAllMess);
        }
    return currentTime;
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        switch (event.sensor.getType())
        {
            case Sensor.TYPE_STEP_COUNTER:
                allMeasurementsHashMap.get(measureInit()).setSteps((int)event.values[0]);
                break;
            case Sensor.TYPE_LIGHT:
                allMeasurementsHashMap.get(measureInit()).addLight(event.values[0]);
                break;
            case Sensor.TYPE_PROXIMITY:
                allMeasurementsHashMap.get(measureInit()).addProximity(event.values[0]);
                break;
            case Sensor.TYPE_STATIONARY_DETECT:
                allMeasurementsHashMap.get(measureInit()).setStationary(event.values[0]);
                break;
            case Sensor.TYPE_MOTION_DETECT:
                allMeasurementsHashMap.get(measureInit()).setMotion(event.values[0]);
                break;
            case Sensor.TYPE_SIGNIFICANT_MOTION:
                allMeasurementsHashMap.get(measureInit()).setSignificantMotion(event.values[0]);
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
        }
    }

    protected void readWifiInfo()
    {
        manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
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
        List<WifiMeasurement> meslist = new ArrayList<>();

        if ((meslist != null) && (!meslist.isEmpty()))
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

                        JSONArray jsonArray = new JSONArray();
                        for (WifiMeasurement wfm : meslist)
                        {
                            jsonArray.put(wfm.toJSONObject());
                        }

                        JSONObject jsonCollection = new JSONObject();
                        jsonCollection.put("collection", jsonArray);

                        Log.i("JSON", jsonCollection.toString());
                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                        os.writeBytes(jsonCollection.toString());

                        os.flush();
                        os.close();

                        Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                        Log.i("MSG", conn.getResponseMessage());

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

    void savefile()
    {
        List<WifiMeasurement> meslist = new ArrayList<>();
        if ((meslist != null) && (!meslist.isEmpty()))
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
                        Log.d(LOG_TAG, "File: "+todayFile.getAbsolutePath());
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
                            for (WifiMeasurement wfm : meslist)
                            {
                                bos.write(wfm.toString().getBytes());
                                bos.write("\n".getBytes());
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

        timerSaveSend = new Timer();
        timerSaveSend.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                if ((allMeasurementsHashMap != null) && (!allMeasurementsHashMap.isEmpty()))
                {
                    savefile();
                    sendPost();
                }
            }
        }, 0, 3000);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
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

        IntentFilter filter = new IntentFilter("pl.k13.wifiinfo.Broadcast");
        this.registerReceiver(angleBroadcastReceiver, filter);

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
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy()
    {
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

        timerMainTick.cancel();
        timerSaveSend.cancel();
        if ((allMeasurementsHashMap != null) && (!allMeasurementsHashMap.isEmpty()))
        {
            savefile();
            sendPost();
        }
        allMeasurementsHashMap.clear();
        Toast.makeText(this, "Zapis zatrzymany", Toast.LENGTH_SHORT).show();
    }


    /**
     * Is the screen of the device on.
     * @param context the context
     * @return true when (at least one) screen is on
     */
    public boolean isScreenOn(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            boolean screenOn = false;
            for (Display display : dm.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    screenOn = true;
                }
            }
            return screenOn;
        } else {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            //noinspection deprecation
            return pm.isScreenOn();
        }
    }

}