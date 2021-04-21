package pl.k13.allinfo;

public class GPSsatInfo
{
    private long timestamp;
    private short id;
    private short elevation;
    private short azimuth;
    private short snr;

    public GPSsatInfo()
    {
        this.timestamp = -1;
        this.id = -1;
        this.elevation = -1;
        this.azimuth = -1;
        this.snr = -1;
    }

    public GPSsatInfo(long timestamp, short id, short elevation, short azimuth, short snr)
    {
        this.timestamp = timestamp;
        this.id = id;
        this.elevation = elevation;
        this.azimuth = azimuth;
        this.snr = snr;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public short getId()
    {
        return id;
    }

    public void setId(short id)
    {
        this.id = id;
    }

    public short getElevation()
    {
        return elevation;
    }

    public void setElevation(short elevation)
    {
        this.elevation = elevation;
    }

    public short getAzimuth()
    {
        return azimuth;
    }

    public void setAzimuth(short azimuth)
    {
        this.azimuth = azimuth;
    }

    public short getSnr()
    {
        return snr;
    }

    public void setSnr(short snr)
    {
        this.snr = snr;
    }

    @Override
    public String toString()
    {
        return "{" +
                "timestamp=" + timestamp +
                ", id=" + id +
                ", elevation=" + elevation +
                ", azimuth=" + azimuth +
                ", snr=" + snr +
                '}';
    }
}
