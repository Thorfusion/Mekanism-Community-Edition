package mekanism.mekasuit.common.integration;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mekanism.common.Mekanism;
import mekanism.common.MekanismItems;
import mekanism.common.recipe.ShapedMekanismRecipe;
import mekanism.generators.common.GeneratorsBlocks;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleRegistry;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.api.gear.ModuleType;
import mekanism.mekasuit.common.MekaSuitItems;
import mekanism.mekasuit.common.MekanismMekaSuit;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.content.gear.ModuleContainer;
import mekanism.mekasuit.common.item.ItemMekaModule;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/**
 * MekaSuit-owned integration with the required Generators module.
 *
 * Keeping this bridge in MekaSuit prevents the stable Generators module from
 * acquiring any knowledge of, or class linkage to, the backport module.
 */
public final class MekaSuitGeneratorsIntegration
{
    private static final String MODULE_NAMESPACE = MekanismMekaSuit.MODID;

    public static final ModuleType SOLAR_RECHARGING_UNIT =
          ModuleType.builder(new ResourceLocation(MODULE_NAMESPACE,
                "solar_recharging_unit"), ModuleTarget.HELMET)
                .maxInstallCount(8).build();
    public static final ModuleType GEOTHERMAL_GENERATOR_UNIT =
          ModuleType.builder(new ResourceLocation(MODULE_NAMESPACE,
                "geothermal_generator_unit"), ModuleTarget.PANTS)
                .maxInstallCount(8).build();

    public static final ItemMekaModule SolarRechargingUnit = moduleItem(
          SOLAR_RECHARGING_UNIT, "module_solar_recharging_unit",
          "ModuleSolarRechargingUnit");
    public static final ItemMekaModule GeothermalGeneratorUnit = moduleItem(
          GEOTHERMAL_GENERATOR_UNIT, "module_geothermal_generator_unit",
          "ModuleGeothermalGeneratorUnit");

    private static final MekaSuitGeneratorsIntegration INSTANCE =
          new MekaSuitGeneratorsIntegration();
    private static boolean itemsRegistered;
    private static boolean handlersRegistered;

    private MekaSuitGeneratorsIntegration()
    {
    }

    public static synchronized void registerItems()
    {
        if (itemsRegistered)
        {
            return;
        }
        MekaSuitModules.bootstrap();
        ModuleRegistry.getInstance().register(SOLAR_RECHARGING_UNIT);
        ModuleRegistry.getInstance().register(GEOTHERMAL_GENERATOR_UNIT);
        SolarRechargingUnit.setCreativeTab(Mekanism.tabMekanism);
        GeothermalGeneratorUnit.setCreativeTab(Mekanism.tabMekanism);
        GameRegistry.registerItem(SolarRechargingUnit,
              SolarRechargingUnit.getRegistryName());
        GameRegistry.registerItem(GeothermalGeneratorUnit,
              GeothermalGeneratorUnit.getRegistryName());
        itemsRegistered = true;
    }

    public static synchronized void registerHandlers()
    {
        if (!handlersRegistered)
        {
            FMLCommonHandler.instance().bus().register(INSTANCE);
            MinecraftForge.EVENT_BUS.register(INSTANCE);
            handlersRegistered = true;
        }
    }

    @SuppressWarnings("unchecked")
    public static void addRecipes()
    {
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(new ItemStack(SolarRechargingUnit),
                    new Object[] {
                          "A#A", "APA", "HHH",
                          Character.valueOf('A'), "alloyElite",
                          Character.valueOf('#'),
                                new ItemStack(GeneratorsBlocks.Generator, 1, 5),
                          Character.valueOf('P'), MekaSuitItems.ModuleBase,
                          Character.valueOf('H'), MekanismItems.AtomicAlloy
                    }));
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(new ItemStack(GeothermalGeneratorUnit),
                    new Object[] {
                          "A#A", "APA", "HHH",
                          Character.valueOf('A'), "alloyElite",
                          Character.valueOf('#'),
                                new ItemStack(GeneratorsBlocks.Generator, 1, 0),
                          Character.valueOf('P'), MekaSuitItems.ModuleBase,
                          Character.valueOf('H'), MekanismItems.AtomicAlloy
                    }));
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        if (event.phase != Phase.END || event.player.worldObj.isRemote)
        {
            return;
        }
        tickSolar(event.player, event.player.getCurrentArmor(3));
        tickGeothermal(event.player, event.player.getCurrentArmor(1));
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event)
    {
        if (!(event.entityLiving instanceof EntityPlayer)
              || event.entityLiving.worldObj.isRemote
              || event.source == null || !event.source.isFireDamage()
              || event.ammount <= 0)
        {
            return;
        }
        EntityPlayer player = (EntityPlayer)event.entityLiving;
        ModuleData module = getEnabledModule(player.getCurrentArmor(1),
              ModuleTarget.PANTS, GEOTHERMAL_GENERATOR_UNIT);
        if (module != null)
        {
            event.ammount = remainingHeatDamage(event.ammount,
                  MekaSuitConfig.heatDamageReductionRatio,
                  module.getInstalledCount());
        }
    }

    private static void tickSolar(EntityPlayer player, ItemStack helmet)
    {
        ModuleData module = getEnabledModule(helmet, ModuleTarget.HELMET,
              SOLAR_RECHARGING_UNIT);
        if (module == null
              || !(helmet.getItem() instanceof ItemMekaSuitArmor))
        {
            return;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor)helmet.getItem();
        if (armor.getEnergy(helmet) >= armor.getMaxEnergy(helmet))
        {
            return;
        }
        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.boundingBox.maxY + 0.2D);
        int z = MathHelper.floor_double(player.posZ);
        if (!player.worldObj.isDaytime() || player.worldObj.provider.hasNoSky
              || !player.worldObj.canBlockSeeTheSky(x, y, z))
        {
            return;
        }
        BiomeGenBase biome = player.worldObj.getBiomeGenForCoords(x, z);
        boolean canRain = !biome.getEnableSnow()
              && biome.canSpawnLightningBolt();
        float temperatureEfficiency = 0.3F
              * (0.8F - biome.getFloatTemperature(x, y, z));
        float humidityEfficiency = canRain ? -0.3F * biome.rainfall : 0F;
        boolean raining = canRain && (player.worldObj.isRaining()
              || player.worldObj.isThundering());
        double production = solarProduction(
              MekaSuitConfig.solarRechargingRate,
              module.getInstalledCount(), temperatureEfficiency,
              humidityEfficiency, player.worldObj.getSunBrightness(1F),
              raining);
        armor.setEnergy(helmet, armor.getEnergy(helmet) + production);
    }

    private static void tickGeothermal(EntityPlayer player, ItemStack pants)
    {
        ModuleData module = getEnabledModule(pants, ModuleTarget.PANTS,
              GEOTHERMAL_GENERATOR_UNIT);
        if (module == null
              || !(pants.getItem() instanceof ItemMekaSuitArmor))
        {
            return;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor)pants.getItem();
        if (armor.getEnergy(pants) >= armor.getMaxEnergy(pants))
        {
            return;
        }
        double degreesAboveAmbient = player.isInsideOfMaterial(Material.lava)
              ? 1_000D : player.isBurning() ? 200D : 0D;
        if (degreesAboveAmbient > 0)
        {
            double generated = geothermalProduction(
                  module.getInstalledCount(),
                  MekaSuitConfig.geothermalChargingRate,
                  degreesAboveAmbient);
            armor.setEnergy(pants, armor.getEnergy(pants) + generated);
        }
    }

    static double solarProduction(double rate, int installed,
          double temperatureEfficiency, double humidityEfficiency,
          double brightness, boolean raining)
    {
        double production = Math.max(0D, rate) * Math.max(0, installed)
              * Math.max(0D, 1D + temperatureEfficiency
                    + humidityEfficiency)
              * Math.max(0D, brightness);
        return raining ? production * 0.2D : production;
    }

    static double geothermalProduction(int installed, double rate,
          double degreesAboveAmbient)
    {
        return Math.max(0, installed) * Math.max(0D, rate)
              * Math.max(0D, degreesAboveAmbient);
    }

    static float remainingHeatDamage(float damage, double maximumRatio,
          int installed)
    {
        double absorbed = Math.min(1D, Math.max(0D, maximumRatio)
              * Math.max(0, installed) / 8D);
        return (float)(Math.max(0F, damage) * (1D - absorbed));
    }

    private static ModuleData getEnabledModule(ItemStack stack,
          ModuleTarget target, ModuleType type)
    {
        if (stack == null
              || !(stack.getItem() instanceof ItemMekaSuitArmor)
              || ((ItemMekaSuitArmor)stack.getItem()).getModuleTarget()
                    != target)
        {
            return null;
        }
        ModuleData data = ModuleContainer.fromStack(stack, target).get(type);
        return data != null && data.isEnabled() ? data : null;
    }

    private static ItemMekaModule moduleItem(ModuleType type,
          String registryName, String translationKey)
    {
        return new ItemMekaModule(type, EnumRarity.rare, registryName,
              translationKey, MODULE_NAMESPACE);
    }
}
