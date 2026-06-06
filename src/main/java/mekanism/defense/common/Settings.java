package defense.common;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Loader;

/** Settings class for various configuration settings.
 * 
 * @author Calclavia */
public class Settings
{
    /** Configuration file */
    public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), Reference.NAME.replace(" ", "") + ".cfg"));

    public static boolean USE_FUEL = true;
    public static boolean LOAD_CHUNKS = true;
    public static int MAX_MISSILE_DISTANCE = 10000;
    public static int ANTIMATTER_SIZE = 55;
    public static boolean DESTROY_BEDROCK = true;
    public static int MAX_ROCKET_LAUNCHER_TIER = 2;
    public static boolean GENERATE_SULFUR = true;
    public static int MAX_REDMATTER_LIFESPAN = 3600;
    public static boolean DO_REDMATTER_DESPAWN = true;
    public static boolean CREEPER_DROP_SULFUR = true;
    public static boolean CREEPER_BLOW_UP_IN_FIRE = true;
    public static boolean POLLUTIVE_NUCLEAR = true;
    public static boolean CREATE_NETHERRACK = true;
    public static double REDUCE_OBSIDIAN_RESISTANCE = 45F;

    public static void initiate()
    {
        USE_FUEL = CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Use Fuel", true).getBoolean(true);
        LOAD_CHUNKS = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Allow Chunk Loading", true).getBoolean(true);
        MAX_MISSILE_DISTANCE = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Max Missile Distance", 10000).getInt(10000);
        ANTIMATTER_SIZE = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Antimatter Explosion Size", 55).getInt(55);
        DESTROY_BEDROCK = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Antimatter Destroy Bedrock", true).getBoolean(true);
        MAX_ROCKET_LAUNCHER_TIER = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Rocket Launcher Max Missile Tier", 2).getInt(2);
        GENERATE_SULFUR = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Generate Sulfur Ore", true).getBoolean(true);
        MAX_REDMATTER_LIFESPAN = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "RedMatter Life Span in ticks", 3600).getInt(3600);
        DO_REDMATTER_DESPAWN = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "RedMatter despawn", true).getBoolean(true);
        CREEPER_DROP_SULFUR = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Creepers Drop Sulfur", true).getBoolean(true);
        CREEPER_BLOW_UP_IN_FIRE = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Creepers Blow up in Fire", true).getBoolean(true);
        POLLUTIVE_NUCLEAR = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Pollutive Nuclear", true).getBoolean(true);
        CREATE_NETHERRACK = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Exothermic Create Netherrack", true).getBoolean(true);
        REDUCE_OBSIDIAN_RESISTANCE = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Reduce Obsidian Resistance", 45F).getDouble(45F);
    }
}
