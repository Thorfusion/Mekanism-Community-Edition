package mekanism.nuclear.common.radiation;

/** Immutable 1.7.10 block position using the later vanilla packed-long format. */
public final class RadiationPosition
{
    private final int x;
    private final int y;
    private final int z;

    public RadiationPosition(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }

    public long toLong()
    {
        return ((long)x & 0x3FFFFFFL) << 38
              | ((long)y & 0xFFFL) << 26
              | (long)z & 0x3FFFFFFL;
    }

    public static RadiationPosition fromLong(long packed)
    {
        int x = (int)(packed >> 38);
        int y = (int)(packed >> 26 & 0xFFFL);
        int z = (int)(packed << 38 >> 38);
        return new RadiationPosition(x, y, z);
    }

    public double distanceSq(RadiationPosition other)
    {
        long deltaX = (long)x - other.x;
        long deltaY = (long)y - other.y;
        long deltaZ = (long)z - other.z;
        return (double)(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (!(object instanceof RadiationPosition))
        {
            return false;
        }
        RadiationPosition other = (RadiationPosition)object;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int hashCode()
    {
        int result = x;
        result = 31 * result + y;
        return 31 * result + z;
    }
}
