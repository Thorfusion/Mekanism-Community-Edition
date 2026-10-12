package mekanism.ultimate.common.tile;

import mekanism.common.tile.TileEntityPersonalChest;
import mekanism.common.util.LangUtils;

/** Personal Chest-compatible secure storage in barrel form. */
public final class TileEntityPersonalBarrel extends TileEntityPersonalChest
{
    @Override
    public String getInventoryName()
    {
        return LangUtils.localize("tile.PersonalBarrel.name");
    }
}
