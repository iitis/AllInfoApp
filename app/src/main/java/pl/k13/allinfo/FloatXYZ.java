package pl.k13.allinfo;

public class FloatXYZ
{
    float X;
    float Y;
    float Z;

    public FloatXYZ(float x, float y, float z)
    {
        X = x;
        Y = y;
        Z = z;
    }

    public float getX()
    {
        return X;
    }

    public float getY()
    {
        return Y;
    }

    public float getZ()
    {
        return Z;
    }

    @Override
    public String toString()
    {
        return "{" +
                "X=" + X +
                ", Y=" + Y +
                ", Z=" + Z +
                '}';
    }
}
