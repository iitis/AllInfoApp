package pl.k13.allinfo;

import org.json.JSONException;
import org.json.JSONObject;

public class AllMeas2DB implements Comparable<AllMeas2DB>
{
    private String experimentId;
    private String deviceId;
    private String userMotion;
    private String userPhoneLocation;
    private Long timestamp;
    private Float accelerometerX;
    private Float accelerometerY;
    private Float accelerometerZ;
    private Float gyroscopeX;
    private Float gyroscopeY;
    private Float gyroscopeZ;
    private Float orientationX;
    private Float orientationY;
    private Float orientationZ;
    private Integer steps;
    private Float light;
    private Boolean screenOn;
    private Float proximity;
    private Float stationary;
    private Float motion;
    private Float significantMotion;
    private String googleActivityType;
    private Float googleActivityValue;
    private String wifiName;
    private Integer wifiRssi;
    private String wifiOther;
    private Integer lteRssi;
    private String lteOther;
    private String other;

    public AllMeas2DB(Long timestamp, String experimentId, String deviceId)
    {
        this.experimentId = experimentId;
        this.deviceId = deviceId;
        this.userMotion = "";
        this.userPhoneLocation = "";
        this.timestamp = timestamp;
        this.accelerometerX = 0.0f;
        this.accelerometerY = 0.0f;
        this.accelerometerZ = 0.0f;
        this.gyroscopeX = 0.0f;
        this.gyroscopeY = 0.0f;
        this.gyroscopeZ = 0.0f;
        this.orientationX = 0.0f;
        this.orientationY = 0.0f;
        this.orientationZ = 0.0f;
        this.steps = 0;
        this.light = 0.0f;
        this.screenOn = false;
        this.proximity = 0.0f;
        this.stationary = 0.0f;
        this.motion = 0.0f;
        this.significantMotion = 0.0f;
        this.googleActivityType = "";
        this.googleActivityValue = 0.0f;
        this.wifiName = "";
        this.wifiRssi = -255;
        this.wifiOther = "";
        this.lteRssi = -255;
        this.lteOther = "";
        this.other = "";
    }

    @Override
    public int compareTo(AllMeas2DB u)
    {
        return this.timestamp.compareTo(u.getTimestamp());
    }

    public Long getTimestamp()
    {
        return timestamp;
    }

    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject data = new JSONObject();
        data.put("experimentId", this.experimentId);
        data.put("deviceId", this.deviceId);
        if (this.userMotion != "") data.put("userMotion", this.userMotion);
        if (this.userPhoneLocation != "") data.put("userPhoneLocation", this.userPhoneLocation);
        data.put("timestamp", this.timestamp);
        if (this.accelerometerX != 0.0f) data.put("accelerometerX", this.accelerometerX);
        if (this.accelerometerY != 0.0f) data.put("accelerometerY", this.accelerometerY);
        if (this.accelerometerZ != 0.0f) data.put("accelerometerZ", this.accelerometerZ);
        if (this.gyroscopeX != 0.0f) data.put("gyroscopeX", this.gyroscopeX);
        if (this.gyroscopeY != 0.0f) data.put("gyroscopeY", this.gyroscopeY);
        if (this.gyroscopeZ != 0.0f) data.put("gyroscopeZ", this.gyroscopeZ);
        if (this.orientationX != 0.0f) data.put("orientationX", this.orientationX);
        if (this.orientationY != 0.0f) data.put("orientationY", this.orientationY);
        if (this.orientationZ != 0.0f) data.put("orientationZ", this.orientationZ);
        if (this.steps != 0.0f) data.put("steps", this.steps);
        if (this.light != 0.0f) data.put("light", this.light);
        data.put("screenOn", this.screenOn);
        if (this.proximity != 0.0f) data.put("proximity", this.proximity);
        if (this.stationary != 0.0f) data.put("stationary", this.stationary);
        if (this.motion != 0.0f) data.put("motion", this.motion);
        if (this.significantMotion != 0.0f) data.put("significantMotion", this.significantMotion);
        if (this.googleActivityType != "") data.put("googleActivityType", this.googleActivityType);
        if (this.googleActivityValue != 0.0f)
            data.put("googleActivityValue", this.googleActivityValue);
        if (this.wifiName != "") data.put("wifiName", this.wifiName);
        if (this.wifiRssi != -255) data.put("wifiRssi", this.wifiRssi);
        if (this.wifiOther != "") data.put("wifiOther", this.wifiOther);
        if (this.lteRssi != -255) data.put("lteRssi", this.lteRssi);
        if (this.lteOther != "") data.put("lteOther", this.lteOther);
        if (this.other != "") data.put("other", this.other);
        return data;
    }

    public void setExperimentId(String experimentId)
    {
        this.experimentId = experimentId;
    }

    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }

    public void setUserMotion(String userMotion)
    {
        this.userMotion = userMotion;
    }

    public void setuserPhoneLocation(String userPhoneLocation)
    {
        this.userPhoneLocation = userPhoneLocation;
    }

    public void setTimestamp(Long timestamp)
    {
        this.timestamp = timestamp;
    }

    public void setAccelerometerX(Float accelerometerX)
    {
        this.accelerometerX = accelerometerX;
    }

    public void setAccelerometerY(Float accelerometerY)
    {
        this.accelerometerY = accelerometerY;
    }

    public void setAccelerometerZ(Float accelerometerZ)
    {
        this.accelerometerZ = accelerometerZ;
    }

    public void setGyroscopeX(Float gyroscopeX)
    {
        this.gyroscopeX = gyroscopeX;
    }

    public void setGyroscopeY(Float gyroscopeY)
    {
        this.gyroscopeY = gyroscopeY;
    }

    public void setGyroscopeZ(Float gyroscopeZ)
    {
        this.gyroscopeZ = gyroscopeZ;
    }

    public void setOrientationX(Float orientationX)
    {
        this.orientationX = orientationX;
    }

    public void setOrientationY(Float orientationY)
    {
        this.orientationY = orientationY;
    }

    public void setOrientationZ(Float orientationZ)
    {
        this.orientationZ = orientationZ;
    }

    public void setSteps(Integer steps)
    {
        this.steps = steps;
    }

    public void setLight(Float light)
    {
        this.light = light;
    }

    public void setScreenOn(Boolean screenOn)
    {
        this.screenOn = screenOn;
    }

    public void setProximity(Float proximity)
    {
        this.proximity = proximity;
    }

    public void setStationary(Float stationary)
    {
        this.stationary = stationary;
    }

    public void setMotion(Float motion)
    {
        this.motion = motion;
    }

    public void setSignificantMotion(Float significantMotion)
    {
        this.significantMotion = significantMotion;
    }

    public void setGoogleActivityType(String googleActivityType)
    {
        this.googleActivityType = googleActivityType;
    }

    public void setGoogleActivityValue(Float googleActivityValue)
    {
        this.googleActivityValue = googleActivityValue;
    }

    public void setWifiName(String wifiName)
    {
        this.wifiName = wifiName;
    }

    public void setWifiRssi(Integer wifiRssi)
    {
        this.wifiRssi = wifiRssi;
    }

    public void setWifiOther(String wifiOther)
    {
        this.wifiOther = wifiOther;
    }

    public void setLteRssi(Integer lteRssi)
    {
        this.lteRssi = lteRssi;
    }

    public void setLteOther(String lteOther)
    {
        this.lteOther = lteOther;
    }

    public void setOther(String other)
    {
        this.other = other;
    }


}
