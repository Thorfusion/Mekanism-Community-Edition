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
import mekanism.ultimate.common.config.UltimateGearConfig;
import mekanism.ultimate.common.tile.TileEntityChemicalTank;
import mekanism.ultimate.common.inventory.ContainerChemicalTank;
import mekanism.ultimate.common.tile.TileEntityIndustrialAlarm;
import mekanism.ultimate.common.tile.TileEntityPersonalBarrel;
import mekanism.ultimate.common.tile.TileEntityPaintingMachine;
import mekanism.ultimate.common.tile.TileEntityPigmentExtractor;
import mekanism.ultimate.common.tile.TileEntityPigmentMachine;
import mekanism.ultimate.common.tile.TileEntityPigmentMixer;
import mekanism.ultimate.common.tile.TileEntityDimensionalStabilizer;
import mekanism.ultimate.common.network.PacketStartHDPEElytra;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class UltimateCommonProxy implements IGuiProvider {

    public void preInit() {
    }

    public void registerBlockRenders() {
    }

    public void registerPackets() {
        MekanismUltimate.network.registerMessage(PacketStartHDPEElytra.Handler.class,
              PacketStartHDPEElytra.class, 0, Side.SERVER);
    }

    public void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityUltimateFactory.class, new ResourceLocation(MekanismUltimate.MODID, "ultimate_factory"));
        GameRegistry.registerTileEntity(TileEntityNutritionalLiquifier.class,
              new ResourceLocation(MekanismUltimate.MODID, "nutritional_liquifier"));
        GameRegistry.registerTileEntity(TileEntityChemicalTank.class,
              new ResourceLocation(MekanismUltimate.MODID, "chemical_tank"));
        GameRegistry.registerTileEntity(TileEntityPersonalBarrel.class,
              new ResourceLocation(MekanismUltimate.MODID, "personal_barrel"));
        GameRegistry.registerTileEntity(TileEntityIndustrialAlarm.class,
              new ResourceLocation(MekanismUltimate.MODID, "industrial_alarm"));
        GameRegistry.registerTileEntity(TileEntityPigmentExtractor.class,
              new ResourceLocation(MekanismUltimate.MODID, "pigment_extractor"));
        GameRegistry.registerTileEntity(TileEntityPigmentMixer.class,
              new ResourceLocation(MekanismUltimate.MODID, "pigment_mixer"));
        GameRegistry.registerTileEntity(TileEntityPaintingMachine.class,
              new ResourceLocation(MekanismUltimate.MODID, "painting_machine"));
        GameRegistry.registerTileEntity(TileEntityDimensionalStabilizer.class,
              new ResourceLocation(MekanismUltimate.MODID, "dimensional_stabilizer"));
    }

    public void loadConfiguration() {
        if (MekanismConfig.local().ultimate != null) {
            MekanismConfig.local().ultimate.load(Mekanism.configurationultimate);
            UltimateNutritionConfig.load(Mekanism.configurationultimate);
            UltimateChemicalTankConfig.load(Mekanism.configurationultimate);
            UltimateGearConfig.load(Mekanism.configurationultimate);
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
        } else if (ID == 3 && tile instanceof TileEntityPersonalBarrel) {
            return new mekanism.common.inventory.container.ContainerPersonalChest(player.inventory,
                  (TileEntityPersonalBarrel) tile);
        } else if (ID >= 4 && ID <= 6 && tile instanceof TileEntityPigmentMachine) {
            return new mekanism.ultimate.common.inventory.ContainerPigmentMachine(player.inventory,
                  (TileEntityPigmentMachine) tile);
        } else if (ID == 7 && tile instanceof TileEntityDimensionalStabilizer) {
            return new mekanism.ultimate.common.inventory.ContainerDimensionalStabilizer(player.inventory,
                  (TileEntityDimensionalStabilizer) tile);
        }
        return null;
    }

    public void updateIndustrialAlarmSound(BlockPos pos, boolean active) {
    }
}
