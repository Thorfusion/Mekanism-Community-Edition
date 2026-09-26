package mekanism.ultimate.common;

import mekanism.common.Mekanism;
import mekanism.common.base.IGuiProvider;
import mekanism.common.config.MekanismConfig;
import mekanism.common.inventory.container.ContainerFactory;
import mekanism.ultimate.common.tile.TileEntityUltimateFactory;
import mekanism.ultimate.common.tile.TileEntityNutritionalLiquifier;
import mekanism.ultimate.common.inventory.ContainerNutritionalLiquifier;
import mekanism.ultimate.common.config.UltimateNutritionConfig;
import mekanism.ultimate.common.config.UltimateChemicalTankConfig;
import mekanism.ultimate.common.tile.TileEntityChemicalTank;
import mekanism.ultimate.common.inventory.ContainerChemicalTank;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class UltimateCommonProxy implements IGuiProvider {

    public void preInit() {
    }

    public void registerBlockRenders() {
    }

    public void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityUltimateFactory.class, new ResourceLocation(MekanismUltimate.MODID, "ultimate_factory"));
        GameRegistry.registerTileEntity(TileEntityNutritionalLiquifier.class,
              new ResourceLocation(MekanismUltimate.MODID, "nutritional_liquifier"));
        GameRegistry.registerTileEntity(TileEntityChemicalTank.class,
              new ResourceLocation(MekanismUltimate.MODID, "chemical_tank"));
    }

    public void loadConfiguration() {
        if (MekanismConfig.local().ultimate != null) {
            MekanismConfig.local().ultimate.load(Mekanism.configurationultimate);
            UltimateNutritionConfig.load(Mekanism.configurationultimate);
            UltimateChemicalTankConfig.load(Mekanism.configurationultimate);
            if (Mekanism.configurationultimate.hasChanged()) {
                Mekanism.configurationultimate.save();
            }
        }
    }

    @Override
    public Object getClientGui(int ID, EntityPlayer player, World world, BlockPos pos) {
        return null;
    }

    @Override
    public Container getServerGui(int ID, EntityPlayer player, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (ID == 0 && tile instanceof TileEntityUltimateFactory) {
            return new ContainerFactory(player.inventory, (TileEntityUltimateFactory) tile);
        } else if (ID == 1 && tile instanceof TileEntityNutritionalLiquifier) {
            return new ContainerNutritionalLiquifier(player.inventory, (TileEntityNutritionalLiquifier) tile);
        } else if (ID == 2 && tile instanceof TileEntityChemicalTank) {
            return new ContainerChemicalTank(player.inventory, (TileEntityChemicalTank) tile);
        }
        return null;
    }
}
