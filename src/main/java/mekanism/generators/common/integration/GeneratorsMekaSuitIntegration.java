package mekanism.generators.common.integration;

import mekanism.generators.common.MekanismGenerators;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleRegistry;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.api.gear.ModuleType;
import mekanism.mekasuit.common.content.gear.ModuleContainer;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.item.ItemMekaModule;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.registries.IForgeRegistry;

/** Optional Generators-owned units contributed to MekaSuit when both modules are installed. */
public final class GeneratorsMekaSuitIntegration {

    public static final String MEKASUIT_MODID = "mekanismmekasuit";

    public static final ModuleType SOLAR_RECHARGING_UNIT = ModuleType.builder(
          new ResourceLocation(MekanismGenerators.MODID, "solar_recharging_unit"), ModuleTarget.HELMET)
          .maxInstallCount(8).build();
    public static final ModuleType GEOTHERMAL_GENERATOR_UNIT = ModuleType.builder(
          new ResourceLocation(MekanismGenerators.MODID, "geothermal_generator_unit"), ModuleTarget.PANTS)
          .maxInstallCount(8).build();

    public static final ItemMekaModule SolarRechargingUnit = moduleItem(
          SOLAR_RECHARGING_UNIT, "module_solar_recharging_unit");
    public static final ItemMekaModule GeothermalGeneratorUnit = moduleItem(
          GEOTHERMAL_GENERATOR_UNIT, "module_geothermal_generator_unit");

    private static final GeneratorsMekaSuitIntegration INSTANCE = new GeneratorsMekaSuitIntegration();
    private static boolean registered;

    private GeneratorsMekaSuitIntegration() {
    }

    public static void registerItems(IForgeRegistry<Item> registry) {
        MekaSuitModules.bootstrap();
        ModuleRegistry.getInstance().register(SOLAR_RECHARGING_UNIT);
        ModuleRegistry.getInstance().register(GEOTHERMAL_GENERATOR_UNIT);
        registry.register(SolarRechargingUnit);
        registry.register(GeothermalGeneratorUnit);
    }

    public static void registerHandlers() {
        if (!registered) {
            MinecraftForge.EVENT_BUS.register(INSTANCE);
            registered = true;
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != Phase.END || event.player.world.isRemote) {
            return;
        }
        tickSolar(event.player, event.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
        tickGeothermal(event.player, event.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer) || !event.getSource().isFireDamage()) {
            return;
        }
        ItemStack pants = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        ModuleData module = getEnabledModule(pants, ModuleTarget.PANTS, GEOTHERMAL_GENERATOR_UNIT);
        if (module != null) {
            double maximum = GeneratorsMekaSuitConfig.heatDamageReductionRatio;
            event.setAmount(remainingHeatDamage(event.getAmount(), maximum, module.getInstalledCount()));
        }
    }

    private static void tickSolar(EntityPlayer player, ItemStack helmet) {
        ModuleData module = getEnabledModule(helmet, ModuleTarget.HELMET, SOLAR_RECHARGING_UNIT);
        if (module == null || !(helmet.getItem() instanceof ItemMekaSuitArmor)) {
            return;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor) helmet.getItem();
        if (armor.getEnergy(helmet) >= armor.getMaxEnergy(helmet)) {
            return;
        }
        BlockPos pos = new BlockPos(player.posX, player.getEntityBoundingBox().maxY + 0.2D, player.posZ);
        if (!player.world.isDaytime() || player.world.provider.isNether() || !player.world.canSeeSky(pos)) {
            return;
        }
        Biome biome = player.world.provider.getBiomeForCoords(pos);
        boolean canRain = biome.canRain();
        float temperatureEfficiency = 0.3F * (0.8F - biome.getTemperature(pos));
        float humidityEfficiency = canRain ? -0.3F * biome.getRainfall() : 0F;
        double production = solarProduction(GeneratorsMekaSuitConfig.solarRechargingRate,
              module.getInstalledCount(), temperatureEfficiency, humidityEfficiency,
              player.world.getSunBrightnessFactor(1F), canRain && (player.world.isRaining() || player.world.isThundering()));
        armor.setEnergy(helmet, armor.getEnergy(helmet) + production);
    }

    private static void tickGeothermal(EntityPlayer player, ItemStack pants) {
        ModuleData module = getEnabledModule(pants, ModuleTarget.PANTS, GEOTHERMAL_GENERATOR_UNIT);
        if (module == null || !(pants.getItem() instanceof ItemMekaSuitArmor)) {
            return;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor) pants.getItem();
        if (armor.getEnergy(pants) >= armor.getMaxEnergy(pants)) {
            return;
        }
        double degreesAboveAmbient = player.isInLava() ? 1_000D : player.isBurning() ? 200D : 0D;
        if (degreesAboveAmbient > 0) {
            double generated = geothermalProduction(module.getInstalledCount(),
                  GeneratorsMekaSuitConfig.geothermalChargingRate, degreesAboveAmbient);
            armor.setEnergy(pants, armor.getEnergy(pants) + generated);
        }
    }

    static double solarProduction(double rate, int installed, double temperatureEfficiency,
          double humidityEfficiency, double brightness, boolean raining) {
        double production = Math.max(0D, rate) * Math.max(0, installed)
              * Math.max(0D, 1D + temperatureEfficiency + humidityEfficiency)
              * Math.max(0D, brightness);
        return raining ? production * 0.2D : production;
    }

    static double geothermalProduction(int installed, double rate, double degreesAboveAmbient) {
        return Math.max(0, installed) * Math.max(0D, rate) * Math.max(0D, degreesAboveAmbient);
    }

    static float remainingHeatDamage(float damage, double maximumRatio, int installed) {
        double absorbed = Math.min(1D, Math.max(0D, maximumRatio) * Math.max(0, installed) / 8D);
        return (float) (Math.max(0F, damage) * (1D - absorbed));
    }

    private static ModuleData getEnabledModule(ItemStack stack, ModuleTarget target, ModuleType type) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemMekaSuitArmor)
              || ((ItemMekaSuitArmor) stack.getItem()).getModuleTarget() != target) {
            return null;
        }
        ModuleData data = ModuleContainer.fromStack(stack, target).get(type);
        return data != null && data.isEnabled() ? data : null;
    }

    private static ItemMekaModule moduleItem(ModuleType type, String name) {
        ItemMekaModule item = new ItemMekaModule(type, EnumRarity.RARE);
        item.setRegistryName(new ResourceLocation(MekanismGenerators.MODID, name));
        item.setTranslationKey(name);
        return item;
    }
}
