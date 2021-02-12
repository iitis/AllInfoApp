package pl.k13.allinfo;

import android.util.Log;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

public class AllMeasurements
{
    private String ExperimentId;
    private String DeviceId;
    private long Timestamp;
    private HashMap<Long, FloatXYZ> Accelerometer;
    private HashMap<Long, FloatXYZ>  Gyroscope;
    private HashMap<Long, FloatXYZ> Orientation;
    private int Steps;
    private HashMap<Long, Float>  Light;
    private HashMap<Long, Boolean>  ScreenOn;
    private HashMap<Long, Float>  Proximity;
    private float Stationary;
    private float Motion;
    private float SignificantMotion;
    private HashMap<String, Float> GoogleActivity;
    private WifiMeasurement WifiMeasurement;
    private LTECellInfo LTEMeasurement;

    public AllMeasurements()
    {
        Accelerometer = new HashMap<>();
        Gyroscope = new HashMap<>();
        Orientation = new HashMap<>();
        Light = new HashMap<>();
        ScreenOn = new HashMap<>();
        Proximity = new HashMap<>();
        GoogleActivity = new HashMap<>();
    }

    public String getExperimentId()
    {
        return ExperimentId;
    }

    public void setExperimentId(String experimentId)
    {
        ExperimentId = experimentId;
    }

    public String getDeviceId()
    {
        return DeviceId;
    }

    public void setDeviceId(String deviceId)
    {
        DeviceId = deviceId;
    }

    public long getTimestamp()
    {
        return Timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        Timestamp = timestamp;
    }

    public HashMap<Long, FloatXYZ> getAccelerometer()
    {
        return Accelerometer;
    }

    public void addAccelerometer(float accelerometerX, float accelerometerY, float accelerometerZ)
    {
        Accelerometer.put(System.currentTimeMillis(), new FloatXYZ(accelerometerX, accelerometerY, accelerometerZ));
    }

    public HashMap<Long, FloatXYZ> getGyroscope()
    {
        return Gyroscope;
    }

    public void addGyroscope(float gyroscopeX, float gyroscopeY, float gyroscopeZ)
    {
        Gyroscope.put(System.currentTimeMillis(), new FloatXYZ(gyroscopeX, gyroscopeY, gyroscopeZ));
    }

    public HashMap<Long, FloatXYZ> getOrientation()
    {
        return Orientation;
    }

    public void addOrientation(float orientationAzimuth, float orientationPitch, float orientationRoll)
    {
        Orientation.put(System.currentTimeMillis(), new FloatXYZ(orientationAzimuth, orientationPitch, orientationRoll));
    }

    public int getSteps()
    {
        return Steps;
    }

    public void setSteps(int steps)
    {
       Steps = steps;
    }

    public HashMap<Long, Float> getLight()
    {
        return Light;
    }

    public void addLight(float light)
    {
        Light.put(System.currentTimeMillis(), light);
    }

    public HashMap<Long, Boolean> getScreenOn()
    {
        return ScreenOn;
    }

    public void addScreenOn(boolean screenOn)
    {
        ScreenOn.put(System.currentTimeMillis(), screenOn);
    }

    public HashMap<Long, Float> getProximity()
    {
        return Proximity;
    }

    public void addProximity(float proximity)
    {
        Proximity.put(System.currentTimeMillis(), proximity);
    }

    public float getStationary()
    {
        return Stationary;
    }

    public void setStationary(float stationary)
    {
        Stationary = stationary;
    }

    public float getMotion()
    {
        return Motion;
    }

    public void setMotion(float motion)
    {
        Motion = motion;
    }

    public float getSignificantMotion()
    {
        return SignificantMotion;
    }

    public void setSignificantMotion(float significantMotion)
    {
        SignificantMotion = significantMotion;
    }

    public HashMap<String, Float> getGoogleActivity()
    {
        return GoogleActivity;
    }

    public void setGoogleActivity(String googleActivityKey,  float googleActivityValue)
    {
        GoogleActivity.put(googleActivityKey,  googleActivityValue);
    }

    public pl.k13.allinfo.WifiMeasurement getWifiMeasurement()
    {
        return WifiMeasurement;
    }

    public void setWifiMeasurement(pl.k13.allinfo.WifiMeasurement wifiMeasurement)
    {
        WifiMeasurement = wifiMeasurement;
    }

    public LTECellInfo getLTEMeasurement()
    {
        return LTEMeasurement;
    }

    public void setLTEMeasurement(LTECellInfo LTEMeasurement)
    {
        this.LTEMeasurement = LTEMeasurement;
    }

    private String formatListByTime3F(HashMap<Long, FloatXYZ> input)
    {
        String outputString = "";
        SortedSet<Long> keyList = new TreeSet<>(input.keySet());
        for (Long key:keyList)
        {
            outputString += "{" + key.toString() + "=" + input.get(key).toString() + "}, ";
        }
        return outputString;
    }

    private String formatListByTimeF(HashMap<Long, Float> input)
    {
        String outputString = "";
        SortedSet<Long> keyList = new TreeSet<>(input.keySet());
        for (Long key:keyList)
        {
            outputString += "{" + key.toString() + "=" + input.get(key).toString() + "}, ";
        }
        return outputString;
    }
    private String formatListByTimeB(HashMap<Long, Boolean> input)
    {
        String outputString = "";
        SortedSet<Long> keyList = new TreeSet<>(input.keySet());
        for (Long key:keyList)
        {
            outputString += "{" + key.toString() + "=" + input.get(key).toString() + "}, ";
        }
        return outputString;
    }

    @Override
    public String toString()
    {




//        SortedSet<Long> gyroscopeKeyList = new TreeSet<>(Accelerometer.keySet());
//        String gyroscopeString = "";
//        for (Long key:gyroscopeKeyList)
//        {
//            gyroscopeString += "{" + key.toString() + "=" + Gyroscope.get(key).toString() + "}, ";
//        }


        return "AllMeasurements{" +
                "ExperimentId='" + ExperimentId + '\'' +
                ", DeviceId='" + DeviceId + '\'' +
                ", Timestamp=" + Timestamp +
                ", Accelerometer=" + formatListByTime3F(Accelerometer) +
                ", Gyroscope=" + formatListByTime3F(Gyroscope) +
                ", Orientation=" + formatListByTime3F(Orientation) +
                ", Steps=" + Steps +
                ", Light=" + formatListByTimeF(Light) +
                ", ScreenOn=" + formatListByTimeB(ScreenOn) +
                ", Proximity=" + formatListByTimeF(Proximity) +
                ", Stationary=" + Stationary +
                ", Motion=" + Motion +
                ", SignificantMotion=" + SignificantMotion +
                ", GoogleActivity=" + GoogleActivity +
                ", WifiMeasurement=" + WifiMeasurement +
                ", LTEMeasurement=" + LTEMeasurement +
                '}';
    }
}
