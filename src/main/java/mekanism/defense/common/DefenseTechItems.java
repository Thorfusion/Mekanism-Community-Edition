package defense.common;

import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import defense.common.item.ItemAntidote;
import defense.common.item.ItemBombCart;
import defense.common.item.ItemDefuser;
import defense.common.item.ItemGrenade;
import defense.common.item.ItemLaserDesignator;
import defense.common.item.ItemMissile;
import defense.common.item.ItemPoisonPowder;
import defense.common.item.ItemRadarGun;
import defense.common.item.ItemRemoteDetonator;
import defense.common.item.ItemRocketLauncher;
import defense.common.item.ItemSignalDisrupter;
import defense.common.item.ItemSulfurDust;
import defense.common.item.ItemTracker;

@ObjectHolder(Reference.NAME)
public class DefenseTechItems 
{
    public static Item itemAntidote = new ItemAntidote();
    public static Item itemSignalDisrupter = new ItemSignalDisrupter();
    public static Item itemTracker = new ItemTracker();

    public static Item itemPoisonPowder = new ItemPoisonPowder();
    
    public static Item itemMissile = new ItemMissile();
    public static Item itemDefuser = new ItemDefuser();
    public static Item itemRadarGun = new ItemRadarGun();
    public static Item itemRemoteDetonator = new ItemRemoteDetonator();
    public static Item itemLaserDesignator = new ItemLaserDesignator();
    public static Item itemRocketLauncher = new ItemRocketLauncher();
    public static Item itemGrenade = new ItemGrenade();
    public static Item itemBombCart = new ItemBombCart();
    
	public static void register()
	{
		GameRegistry.registerItem(itemPoisonPowder, "poisonPowder");
        GameRegistry.registerItem(itemAntidote, "antidote");
        GameRegistry.registerItem(itemSignalDisrupter, "signalDisrupter");
        GameRegistry.registerItem(itemTracker, "tracker");
        
        GameRegistry.registerItem(itemMissile, "missile");
        GameRegistry.registerItem(itemDefuser, "defuser");
        GameRegistry.registerItem(itemRadarGun, "radarGun");
        GameRegistry.registerItem(itemRemoteDetonator, "remoteDetonator");
        GameRegistry.registerItem(itemLaserDesignator, "laserDesignator");
        GameRegistry.registerItem(itemRocketLauncher, "rocketLauncher");
        GameRegistry.registerItem(itemGrenade, "grenade");
        GameRegistry.registerItem(itemBombCart, "minecart");
	}
}
