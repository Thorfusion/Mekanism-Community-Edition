package mekanism.common.config;

import mekanism.common.block.states.BlockStateMachine.MachineType;
import mekanism.common.config.options.BooleanOption;
import mekanism.common.config.options.DoubleOption;
import mekanism.common.config.options.EnumOption;
import mekanism.common.config.options.IntOption;
import mekanism.common.tier.BaseTier;
import mekanism.common.tier.GasTankTier;
import mekanism.common.util.UnitDisplayUtils.EnergyType;
import mekanism.common.util.UnitDisplayUtils.TempType;

import java.util.EnumMap;


public class MEKCEConfig extends BaseConfig {

    public final BooleanOption enablediamondcompat = new BooleanOption(this, "mekce", "EnableDiamondCompat", true,
            "Allows oredict'ed diamonds to be used in the enrichment chamber, like synthetic diamonds.");

    public final BooleanOption enablepoororescompat = new BooleanOption(this, "mekce", "EnablePoorOresCompat", true,
            "Allows poor ores from railcraft to be used in the purification chamber and gives one clump ie one ingot.");

    public final BooleanOption enablequartzcompat = new BooleanOption(this, "mekce", "EnableQuartzCompat", true,
            "Allows quartz dust to be enriched into quartz Also allows quartz ore to be enriched into quartz dust");

    public final BooleanOption enablesiliconcompat = new BooleanOption(this, "mekce", "EnableSiliconCompat", true,
            "When a mod that adds silicon (galacticraft, enderio and projectred) is detected, recipe for control circuit is changed from using iron to silicon in the metalurgic infuser");

    public final BooleanOption enableBoPProgression = new BooleanOption(this, "mekce", "enableBoPProgression", true,
            "when true and biome's o plenty is installed atomic alloy is made by using ender instead of obsidian");

    public final BooleanOption enablesingleusecardboxes = new BooleanOption(this, "mekce", "EnableSingleUseCardboxes", true,
            "This allows to force single use on cardboxes or not");
}