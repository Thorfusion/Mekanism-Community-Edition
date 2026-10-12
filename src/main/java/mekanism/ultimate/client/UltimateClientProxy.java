package mekanism.ultimate.client;

import mekanism.client.gui.GuiFactory;
import mekanism.client.render.tileentity.RenderConfigurableMachine;
import mekanism.ultimate.client.render.UltimateFactoryItemRenderer;
import mekanism.ultimate.client.render.ArmoredFreeRunnersItemRenderer;
import mekanism.ultimate.client.render.HDPEElytraRenderHandler;
import mekanism.ultimate.client.render.LegacyJsonModelRenderer;
import mekanism.ultimate.client.render.RenderPigmentMixer;
import mekanism.ultimate.client.gui.GuiNutritionalLiquifier;
import mekanism.ultimate.client.gui.GuiChemicalTank;
import mekanism.ultimate.client.gui.GuiPigmentMachine;
import mekanism.ultimate.client.gui.GuiPersonalBarrel;
import mekanism.ultimate.client.gui.GuiDimensionalStabilizer;
import mekanism.ultimate.client.render.RenderPersonalBarrel;
import mekanism.ultimate.common.UltimateFluids;
import mekanism.ultimate.common.UltimateBlocks;
import mekanism.ultimate.common.UltimateItems;
import mekanism.ultimate.common.UltimateCommonProxy;
import mekanism.ultimate.common.integration.EtFuturumElytraCompat;
import mekanism.ultimate.common.tile.TileEntityUltimateFactory;
import mekanism.ultimate.common.tile.TileEntityNutritionalLiquifier;
import mekanism.ultimate.common.tile.TileEntityChemicalTank;
import mekanism.ultimate.common.tile.TileEntityPaintingMachine;
import mekanism.ultimate.common.tile.TileEntityPigmentExtractor;
import mekanism.ultimate.common.tile.TileEntityPigmentMachine;
import mekanism.ultimate.common.tile.TileEntityPigmentMixer;
import mekanism.ultimate.common.tile.TileEntityPersonalBarrel;
import mekanism.ultimate.common.tile.TileEntityIndustrialAlarm;
import mekanism.ultimate.common.tile.TileEntityDimensionalStabilizer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class UltimateClientProxy extends UltimateCommonProxy
{
    @Override
    public void registerTileEntities()
    {
        ClientRegistry.registerTileEntity(TileEntityUltimateFactory.class, "MekanismUltimateUltimateFactory", new RenderConfigurableMachine());
        GameRegistry.registerTileEntity(TileEntityNutritionalLiquifier.class,
              "mekanismultimate:nutritional_liquifier");
        ClientRegistry.registerTileEntity(TileEntityChemicalTank.class,
              "mekanismultimate:chemical_tank",
              new RenderConfigurableMachine());
        GameRegistry.registerTileEntity(TileEntityPigmentExtractor.class,
              "mekanismultimate:pigment_extractor");
        ClientRegistry.registerTileEntity(TileEntityPigmentMixer.class,
              "mekanismultimate:pigment_mixer",
              new RenderPigmentMixer());
        GameRegistry.registerTileEntity(TileEntityPaintingMachine.class,
              "mekanismultimate:painting_machine");
        ClientRegistry.registerTileEntity(TileEntityPersonalBarrel.class,
              "mekanismultimate:personal_barrel",
              new RenderPersonalBarrel());
        GameRegistry.registerTileEntity(TileEntityIndustrialAlarm.class,
              "mekanismultimate:industrial_alarm");
        GameRegistry.registerTileEntity(
              TileEntityDimensionalStabilizer.class,
              "mekanismultimate:dimensional_stabilizer");
    }

    @Override
    public void registerRenderInformation()
    {
        RenderingRegistry.registerBlockHandler(
              LegacyJsonModelRenderer.INSTANCE);
        MinecraftForge.EVENT_BUS.register(
              LegacyJsonModelRenderer.INSTANCE);
        LegacyJsonModelRenderer.INSTANCE.registerBlock(
              UltimateBlocks.NutritionalLiquifier);
        LegacyJsonModelRenderer.INSTANCE.registerBlock(
              UltimateBlocks.PigmentMixer);
        LegacyJsonModelRenderer.INSTANCE.registerBlock(
              UltimateBlocks.IndustrialAlarm);
        for (mekanism.ultimate.common.block.BlockChemicalTank tank
              : UltimateBlocks.getChemicalTanks())
        {
            LegacyJsonModelRenderer.INSTANCE.registerBlock(tank);
        }
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(UltimateBlocks.UltimateFactory), new UltimateFactoryItemRenderer());
        MinecraftForgeClient.registerItemRenderer(
              UltimateItems.ArmoredFreeRunners,
              new ArmoredFreeRunnersItemRenderer());
        MinecraftForge.EVENT_BUS.register(this);
        if (EtFuturumElytraCompat.isAvailable())
        {
            FMLCommonHandler.instance().bus().register(
                  HDPEElytraClientHandler.INSTANCE);
            MinecraftForge.EVENT_BUS.register(
                  HDPEElytraRenderHandler.INSTANCE);
        }
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event)
    {
        if (event.map.getTextureType() == 0)
        {
            UltimateFluids.NutritionalPaste.setIcons(
                  event.map.registerIcon("mekanism:liquid/LiquidSteam"));
            UltimateFluids.ChemicalRender.setIcons(
                  event.map.registerIcon("mekanism:liquid/LiquidSteam"));
        }
    }

    @Override
    public GuiScreen getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        return id == 0 && tile instanceof TileEntityUltimateFactory
              ? new GuiFactory(player.inventory,
                    (TileEntityUltimateFactory)tile)
              : id == 1 && tile instanceof TileEntityNutritionalLiquifier
                    ? new GuiNutritionalLiquifier(player.inventory,
                          (TileEntityNutritionalLiquifier)tile)
                    : id == 2 && tile instanceof TileEntityChemicalTank
                          ? new GuiChemicalTank(player.inventory,
                                (TileEntityChemicalTank)tile)
                          : id == 3
                                && tile instanceof TileEntityPersonalBarrel
                                      ? new GuiPersonalBarrel(
                                            player.inventory,
                                            (TileEntityPersonalBarrel)tile)
                          : id == 7
                                && tile instanceof
                                      TileEntityDimensionalStabilizer
                                      ? new GuiDimensionalStabilizer(
                                            player.inventory,
                                            (TileEntityDimensionalStabilizer)
                                                  tile)
                          : id >= 4 && id <= 6
                                && tile instanceof TileEntityPigmentMachine
                                      ? new GuiPigmentMachine(
                                            player.inventory,
                                            (TileEntityPigmentMachine)tile)
                                      : null;
    }
}
