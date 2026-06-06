package defense.common.base;

import net.minecraftforge.common.util.ForgeDirection;

public interface IRedstoneProvider
{
    public boolean isPoweringTo(ForgeDirection direction);

    public boolean isIndirectlyPoweringTo(ForgeDirection direction);
}
