package pl.k13.allinfo;

import java.util.HashMap;

public class AllMeasurements
{
    private String ExperimentId;
    private String DeviceId;
    private long Timestamp;
    private HashMap<Long, Float> Accelerometer;
    private HashMap<Long, Float>  Gyroscope;
    private HashMap<Long, Float>  OrientationX;
    private HashMap<Long, Float>  OrientationY;
    private HashMap<Long, Float>  OrientationZ;
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
        OrientationX = new HashMap<>();
        OrientationY = new HashMap<>();
        OrientationZ = new HashMap<>();
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

    public HashMap<Long, Float> getAccelerometer()
    {
        return Accelerometer;
    }

    public void addAccelerometer(float accelerometer)
    {
        Accelerometer.put(System.currentTimeMillis(), accelerometer);
    }

    public HashMap<Long, Float> getGyroscope()
    {
        return Gyroscope;
    }

    public void addGyroscope(float gyroscope)
    {
        Gyroscope.put(System.currentTimeMillis(), gyroscope);
    }

    public HashMap<Long, Float> getOrientationX()
    {
        return OrientationX;
    }

    public void addOrientationX(float orientationX)
    {
        OrientationX.put(System.currentTimeMillis(), orientationX);
    }

    public HashMap<Long, Float> getOrientationY()
    {
        return OrientationY;
    }

    public void addOrientationY(float orientationY)
    {
        OrientationY.put(System.currentTimeMillis(), orientationY);
    }

    public HashMap<Long, Float> getOrientationZ()
    {
        return OrientationZ;
    }

    public void  addOrientationZ(float orientationZ)
    {
        OrientationZ.put(System.currentTimeMillis(), orientationZ);
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

    @Override
    public String toString()
    {
        //SortedSet<String> keySet = new TreeSet<>(map.keySet());
        return "AllMeasurements{" +
                "ExperimentId='" + ExperimentId + '\'' +
                ", DeviceId='" + DeviceId + '\'' +
                ", Timestamp=" + Timestamp +
                ", Accelerometer=" + Accelerometer +
                ", Gyroscope=" + Gyroscope +
                ", OrientationX=" + OrientationX +
                ", OrientationY=" + OrientationY +
                ", OrientationZ=" + OrientationZ +
                ", Steps=" + Steps +
                ", Light=" + Light +
                ", ScreenOn=" + ScreenOn +
                ", Proximity=" + Proximity +
                ", Stationary=" + Stationary +
                ", Motion=" + Motion +
                ", SignificantMotion=" + SignificantMotion +
                ", GoogleActivity=" + GoogleActivity +
                ", WifiMeasurement=" + WifiMeasurement +
                ", LTEMeasurement=" + LTEMeasurement +
                '}';
    }
}
