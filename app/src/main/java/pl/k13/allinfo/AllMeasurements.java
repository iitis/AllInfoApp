package pl.k13.allinfo;

import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import pl.k13.allinfo.AllMeas2DB.Modes;

public class AllMeasurements
{
    private String ExperimentId;
    private String DeviceId;
    private String UserMotion;
    private String UserPhoneLocation;
    private long Timestamp;
    private HashMap<Long, FloatXYZ> Accelerometer;
    private HashMap<Long, FloatXYZ> Gyroscope;
    private HashMap<Long, FloatXYZ> Orientation;
    private int Steps;
    private float Battery;
    private float Pressure;
    private HashMap<Long, Float> Light;
    private HashMap<Long, Boolean> ScreenOn;
    private HashMap<Long, Float> Proximity;
    private float Stationary;
    private float Motion;
    private float SignificantMotion;
    private HashMap<String, Float> GoogleActivity;
    private WifiMeasurement WifiMeasurement;
    private LTECellInfo LTEMeasurement;
    private boolean saved2file;
    private boolean posted2db;

    public boolean isSaved2file()
    {
        return saved2file;
    }

    public void setSaved2file(boolean saved2file)
    {
        this.saved2file = saved2file;
    }

    public boolean isPosted2db()
    {
        return posted2db;
    }

    public void setPosted2db(boolean posted2db)
    {
        this.posted2db = posted2db;
    }

    public List<AllMeas2DB> toAllMesList() throws JSONException
    {
        List<AllMeas2DB> output = new ArrayList<>();

        AllMeas2DB m = new AllMeas2DB(this.Timestamp, this.ExperimentId, this.DeviceId);
        m.setExperimentId(this.ExperimentId);
        m.setDeviceId(this.DeviceId);
        m.setUserMotion(this.UserMotion);
        m.setuserPhoneLocation(this.UserPhoneLocation);
        m.setSteps(this.Steps);
        m.setStationary(this.Stationary);
        m.setMotion(this.Motion);
        m.setPressure(this.Pressure);
        m.setSignificantMotion(this.SignificantMotion);
        m.setWifiName(this.WifiMeasurement.getSsid());
        m.setWifiRssi(this.WifiMeasurement.getRssi());
        m.setWifiOther(this.WifiMeasurement.getBssid() + ", " + this.WifiMeasurement.getAngle());
        m.setLteRssi(this.LTEMeasurement.getSS_Rsrp());
        m.setLteOther(this.LTEMeasurement.getCI_NetName() + ", " + this.LTEMeasurement.getCI_Mnc());
        m.setType(Modes.GENERAL);
        output.add(m);

        if (this.Battery > -1)
        {
            AllMeas2DB b = new AllMeas2DB(this.Timestamp, this.ExperimentId, this.DeviceId);
            b.setExperimentId(this.ExperimentId);
            b.setDeviceId(this.DeviceId);
            b.setUserMotion(this.UserMotion);
            b.setuserPhoneLocation(this.UserPhoneLocation);
            b.setBattery(this.Battery);
            b.setType(Modes.BATTERY);
            output.add(b);
        }


        for (Map.Entry<Long, FloatXYZ> entry : Accelerometer.entrySet())
        {
            Long key = entry.getKey();
            FloatXYZ value = entry.getValue();
            AllMeas2DB mes = new AllMeas2DB(key, this.ExperimentId, this.DeviceId);
            mes.setAccelerometerX(value.getX());
            mes.setAccelerometerY(value.getY());
            mes.setAccelerometerZ(value.getZ());
            mes.setUserMotion(this.UserMotion);
            mes.setuserPhoneLocation(this.UserPhoneLocation);
            mes.setType(Modes.ACCELEROMETER);
            output.add(mes);
        }

        for (Map.Entry<Long, FloatXYZ> entry : Gyroscope.entrySet())
        {
            Long key = entry.getKey();
            FloatXYZ value = entry.getValue();
            AllMeas2DB mes = new AllMeas2DB(key, this.ExperimentId, this.DeviceId);
            mes.setGyroscopeX(value.getX());
            mes.setGyroscopeY(value.getY());
            mes.setGyroscopeZ(value.getZ());
            mes.setUserMotion(this.UserMotion);
            mes.setuserPhoneLocation(this.UserPhoneLocation);
            mes.setType(Modes.GYROSCOPE);
            output.add(mes);
        }

        for (Map.Entry<Long, FloatXYZ> entry : Orientation.entrySet())
        {
            Long key = entry.getKey();
            FloatXYZ value = entry.getValue();
            AllMeas2DB mes = new AllMeas2DB(key, this.ExperimentId, this.DeviceId);
            mes.setOrientationX(value.getX());
            mes.setOrientationY(value.getY());
            mes.setOrientationZ(value.getZ());
            mes.setUserMotion(this.UserMotion);
            mes.setuserPhoneLocation(this.UserPhoneLocation);
            mes.setType(Modes.ORIENTATION);
            output.add(mes);
        }

        for (Map.Entry<Long, Float> entry : Light.entrySet())
        {
            Long key = entry.getKey();
            Float value = entry.getValue();
            AllMeas2DB mes = new AllMeas2DB(key, this.ExperimentId, this.DeviceId);
            mes.setLight(value);
            mes.setUserMotion(this.UserMotion);
            mes.setuserPhoneLocation(this.UserPhoneLocation);
            mes.setType(Modes.LIGHT);
            output.add(mes);
        }

        for (Map.Entry<Long, Boolean> entry : ScreenOn.entrySet())
        {
            Long key = entry.getKey();
            Boolean value = entry.getValue();
            AllMeas2DB mes = new AllMeas2DB(key, this.ExperimentId, this.DeviceId);
            mes.setScreenOn(value);
            mes.setUserMotion(this.UserMotion);
            mes.setuserPhoneLocation(this.UserPhoneLocation);
            mes.setType(Modes.SCREEN);
            output.add(mes);
        }

        for (Map.Entry<Long, Float> entry : Proximity.entrySet())
        {
            Long key = entry.getKey();
            Float value = entry.getValue();
            AllMeas2DB mes = new AllMeas2DB(key, this.ExperimentId, this.DeviceId);
            mes.setProximity(value);
            mes.setUserMotion(this.UserMotion);
            mes.setuserPhoneLocation(this.UserPhoneLocation);
            mes.setType(Modes.PROXIMITY);
            output.add(mes);
        }

        for (Map.Entry<String, Float> entry : GoogleActivity.entrySet())
        {
            String key = entry.getKey();
            Float value = entry.getValue();
            AllMeas2DB mes = new AllMeas2DB(this.Timestamp, this.ExperimentId, this.DeviceId);
            mes.setGoogleActivityType(key);
            mes.setGoogleActivityValue(value);
            mes.setUserMotion(this.UserMotion);
            mes.setuserPhoneLocation(this.UserPhoneLocation);
            mes.setType(Modes.GOOGLEACTIVITY);
            output.add(mes);
        }
//        Collections.sort(output); TODO
        return output;
    }


    public AllMeasurements()
    {
        Accelerometer = new HashMap<>();
        Gyroscope = new HashMap<>();
        Orientation = new HashMap<>();
        Battery = -1.0f;
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

    public String getUserMotion()
    {
        return UserMotion;
    }

    public void setUserMotion(String userMotion)
    {
        UserMotion = userMotion;
    }

    public String getUserPhoneLocation()
    {
        return UserPhoneLocation;
    }

    public void setUserPhoneLocation(String userPhoneLocation)
    {
        UserPhoneLocation = userPhoneLocation;
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

    public float getBattery()
    {
        return Battery;
    }

    public void setBattery(float battery)
    {
        Battery = battery;
    }

    public float getPressure()
    {
        return Pressure;
    }

    public void setPressure(float preasure)
    {
        Pressure = preasure;
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

    public void setGoogleActivity(String googleActivityKey, float googleActivityValue)
    {
        GoogleActivity.put(googleActivityKey, googleActivityValue);
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
        for (Long key : keyList)
        {
            outputString += "{" + key.toString() + "=" + input.get(key).toString() + "}, ";
        }
        return outputString;
    }

    private String formatListByTimeF(HashMap<Long, Float> input)
    {
        String outputString = "";
        SortedSet<Long> keyList = new TreeSet<>(input.keySet());
        for (Long key : keyList)
        {
            outputString += "{" + key.toString() + "=" + input.get(key).toString() + "}, ";
        }
        return outputString;
    }

    private String formatListByTimeB(HashMap<Long, Boolean> input)
    {
        String outputString = "";
        SortedSet<Long> keyList = new TreeSet<>(input.keySet());
        for (Long key : keyList)
        {
            outputString += "{" + key.toString() + "=" + input.get(key).toString() + "}, ";
        }
        return outputString;
    }

    @Override
    public String toString()
    {
        return "{" +
                "Timestamp=" + Timestamp +
                ", ExperimentId='" + ExperimentId + '\'' +
                ", DeviceId='" + DeviceId + '\'' +
                ", UserPhoneLocation='" + UserPhoneLocation + '\'' +
                ", UserMotion='" + UserMotion + '\'' +
                ", Steps=" + Steps +
                ", Battery=" + Battery +
                ", Pressure=" + Pressure +
                ", Light=" + formatListByTimeF(Light) +
                ", ScreenOn=" + formatListByTimeB(ScreenOn) +
                ", Proximity=" + formatListByTimeF(Proximity) +
                ", Stationary=" + Stationary +
                ", Motion=" + Motion +
                ", SignificantMotion=" + SignificantMotion +
                ", GoogleActivity=" + GoogleActivity +
                ", Accelerometer=" + formatListByTime3F(Accelerometer) +
                ", Gyroscope=" + formatListByTime3F(Gyroscope) +
                ", Orientation=" + formatListByTime3F(Orientation) +
                ", WifiMeasurement=" + WifiMeasurement +
                ", LTEMeasurement=" + LTEMeasurement +
                '}';
    }
}
