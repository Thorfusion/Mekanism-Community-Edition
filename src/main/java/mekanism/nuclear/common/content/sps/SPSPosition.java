package mekanism.nuclear.common.content.sps;

import net.minecraftforge.common.util.ForgeDirection;

/** Immutable block coordinate used by the old-version-native SPS model. */
public final class SPSPosition
{
    private final int x;
    private final int y;
    private final int z;

    public SPSPosition(int x, int y, int z)
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

    public SPSPosition add(int x, int y, int z)
    {
        return new SPSPosition(this.x + x, this.y + y, this.z + z);
    }

    public SPSPosition offset(ForgeDirection side)
    {
        return add(side.offsetX, side.offsetY, side.offsetZ);
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (!(object instanceof SPSPosition))
        {
            return false;
        }
        SPSPosition other = (SPSPosition)object;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int hashCode()
    {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
