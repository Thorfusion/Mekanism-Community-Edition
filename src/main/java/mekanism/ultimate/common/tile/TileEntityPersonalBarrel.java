package mekanism.ultimate.common.tile;

import javax.annotation.Nonnull;
import mekanism.common.tile.TileEntityPersonalChest;
import mekanism.common.util.LangUtils;

/** Personal Chest-compatible storage in the modern barrel form. */
public final class TileEntityPersonalBarrel extends TileEntityPersonalChest {

    @Nonnull
    @Override
    public String getName() {
        return LangUtils.localize("tile.PersonalBarrel.name");
    }
}
