
package pl.k13.allinfo;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class WifiMeasurement implements Parcelable
{
//    private String experimentId;
//    private Long timestamp;
    private String bssid;
    private String ssid;
    private Integer rssi;
    private Integer angle;
//    private String other1;
//    private Integer other2;


//    public String getExperimentId()
//    {
//        return experimentId;
//    }

//    public void setExperimentId(String experimentId)
//    {
//        this.experimentId = experimentId;
//    }

//    public Long getTimestamp()
//    {
//        return timestamp;
//    }

//    public void setTimestamp(Long timestamp)
//    {
//        this.timestamp = timestamp;
//    }

    public String getBssid()
    {
        return bssid;
    }

    public void setBssid(String bssid)
    {
        this.bssid = bssid;
    }

    public String getSsid()
    {
        return ssid;
    }

    public void setSsid(String ssid)
    {
        this.ssid = ssid;
    }

    public Integer getRssi()
    {
        return rssi;
    }

    public void setRssi(Integer rssi)
    {
        this.rssi = rssi;
    }

    public Integer getAngle()
    {
        return angle;
    }

    public void setAngle(Integer angle)
    {
        this.angle = angle;
    }

//    public String getOther1()
//    {
//        return other1;
//    }
//
//    public void setOther1(String other1)
//    {
//        this.other1 = other1;
//    }
//
//    public Integer getOther2()
//    {
//        return other2;
//    }
//
//    public void setOther2(Integer other2)
//    {
//        this.other2 = other2;
//    }

    @Override
    public String toString()
    {
        return "WifiMeasurement{" +
                "bssid='" + bssid + '\'' +
                ", ssid='" + ssid + '\'' +
                ", rssi=" + rssi +
                ", angle=" + angle +
                '}';
    }

    public String toStringShort()
    {
//        return this.timestamp + " " + this.ssid + " " + this.bssid + " " + this.rssi + " "  + this.other2;
        return this.ssid + " " + this.bssid + " " + this.rssi;
    }

    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject data = new JSONObject();
//        data.put("experimentId", this.experimentId);
//        data.put("timestamp", this.timestamp);
        data.put("bssid", this.bssid);
        data.put("ssid", this.ssid);
        data.put("rssi", this.rssi);
        data.put("angle", this.angle);
//        data.put("other1", this.other1);
//        data.put("other2", this.other2);
        return data;
    }

    protected WifiMeasurement(Parcel in)
    {
//        experimentId = in.readString();
//        timestamp = in.readByte() == 0x00 ? null : in.readLong();
        bssid = in.readString();
        ssid = in.readString();
        rssi = in.readByte() == 0x00 ? null : in.readInt();
        angle = in.readByte() == 0x00 ? null : in.readInt();
//        other1 = in.readString();
//        other2 = in.readByte() == 0x00 ? null : in.readInt();
    }

    protected WifiMeasurement()
    {
//        experimentId = "empty";
//        timestamp = (new Date()).getTime() / 1000;
        bssid = "null";
        ssid = "null";
        rssi = -128;
        angle = -360;
//        other1 = "";
//        other2 = 0;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
//        dest.writeString(experimentId);
//        if (timestamp == null)
//        {
//            dest.writeByte((byte) (0x00));
//        } else
//        {
//            dest.writeByte((byte) (0x01));
//            dest.writeLong(timestamp);
//        }
        dest.writeString(bssid);
        dest.writeString(ssid);
        if (rssi == null)
        {
            dest.writeByte((byte) (0x00));
        } else
        {
            dest.writeByte((byte) (0x01));
            dest.writeInt(rssi);
        }
        if (angle == null)
        {
            dest.writeByte((byte) (0x00));
        } else
        {
            dest.writeByte((byte) (0x01));
            dest.writeInt(angle);
        }
//        dest.writeString(other1);
//        if (other2 == null)
//        {
//            dest.writeByte((byte) (0x00));
//        } else
//        {
//            dest.writeByte((byte) (0x01));
//            dest.writeInt(other2);
//        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<WifiMeasurement> CREATOR = new Parcelable.Creator<WifiMeasurement>()
    {
        @Override
        public WifiMeasurement createFromParcel(Parcel in)
        {
            return new WifiMeasurement(in);
        }

        @Override
        public WifiMeasurement[] newArray(int size)
        {
            return new WifiMeasurement[size];
        }
    };
}