package mekanism.mekasuit.client;

import mekanism.mekasuit.common.MekaSuitCommonProxy;
import mekanism.mekasuit.common.MekaSuitItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class MekaSuitClientProxy extends MekaSuitCommonProxy {

    @Override
    public void registerItemRenders() {
        for (Item item : MekaSuitItems.allRegistered()) {
            ModelLoader.setCustomModelResourceLocation(item, 0,
                  new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }
}
