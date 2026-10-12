package mekanism.nuclear.common.content.fission;

import net.minecraftforge.common.util.ForgeDirection;

/** Immutable block coordinate used by the old-version-native reactor model. */
public final class FissionReactorPosition
{
    public static final FissionReactorPosition ORIGIN =
          new FissionReactorPosition(0, 0, 0);

    private final int x;
    private final int y;
    private final int z;

    public FissionReactorPosition(int x, int y, int z)
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

    public FissionReactorPosition offset(ForgeDirection side)
    {
        return new FissionReactorPosition(x + side.offsetX,
              y + side.offsetY, z + side.offsetZ);
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (!(object instanceof FissionReactorPosition))
        {
            return false;
        }
        FissionReactorPosition other = (FissionReactorPosition)object;
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
