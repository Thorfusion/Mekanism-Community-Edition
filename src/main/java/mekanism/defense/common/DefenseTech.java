package defense.common;

import java.util.List;
import java.util.logging.Logger;

import mekanism.api.Coord4D;
import mekanism.api.Pos3D;
import mekanism.common.MekanismBlocks;
import mekanism.common.MekanismItems;
import mekanism.common.Tier.BaseTier;
import mekanism.common.Tier.EnergyCubeTier;
import mekanism.common.Version;
import mekanism.common.item.ItemEnergized;
import mekanism.common.recipe.ShapedMekanismRecipe;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EnteringChunk;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import defense.api.FrequencyGrid;
import defense.common.base.IChunkLoadHandler;
import defense.common.block.BlockMachine.MachineData;
import defense.common.entity.EntityBombCart;
import defense.common.entity.EntityExplosion;
import defense.common.entity.EntityExplosive;
import defense.common.entity.EntityFlyingBlock;
import defense.common.entity.EntityFragments;
import defense.common.entity.EntityGrenade;
import defense.common.entity.EntityLightBeam;
import defense.common.entity.EntityMissile;
import defense.common.explosive.Explosive;
import defense.common.explosive.ExplosiveRegistry;
import defense.common.network.PacketItem;
import defense.common.network.PacketItem.ItemMessage;
import defense.common.potion.ContagiousPoison;
import defense.common.potion.PoisonContagion;
import defense.common.potion.PoisonFrostBite;
import defense.common.potion.PoisonToxin;
import defense.common.potion.PotionUtility;

@Mod(modid = Reference.NAME, name = Reference.NAME, version = "GRADLE_MODVERSION", guiFactory = "defense.client.gui.ConfigGuiFactory", dependencies = "required-after:Mekanism")
public final class DefenseTech
{
    @Instance(Reference.NAME)
    public static DefenseTech INSTANCE;

    @Metadata(Reference.NAME)
    public static ModMetadata metadata;

    @SidedProxy(clientSide = Reference.DOMAIN + ".client.ClientProxy", serverSide = Reference.DOMAIN + ".common.CommonProxy")
    public static CommonProxy proxy;

    public static final ContagiousPoison poisonous_potion = new ContagiousPoison("Chemical", 1, false);
    public static final ContagiousPoison contagios_potion = new ContagiousPoison("Contagious", 1, true);
    
    public static Version versionNumber = new Version(GRADLE_VERSIONMOD);
    
	public static String latestVersionNumber;
	public static String recentNews;

    public static final Logger LOGGER = Logger.getLogger(Reference.NAME);
    
    public static SimpleNetworkWrapper netHandler = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.CHANNEL);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
        FMLCommonHandler.instance().bus().register(INSTANCE);
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        MinecraftForge.EVENT_BUS.register(proxy);
        
        Explosive.register();

        proxy.loadConfiguration();
        
        DefenseTechItems.register();
        DefenseTechBlocks.register();
        
        PotionUtility.resizePotionArray();

        /** Decrease Obsidian Resistance */
        Blocks.obsidian.setResistance((float)Settings.REDUCE_OBSIDIAN_RESISTANCE);
        LOGGER.fine("Changed obsidian explosive resistance to: " + Blocks.obsidian.getExplosionResistance(null));

        /** Potion Effects */
        PoisonToxin.INSTANCE = new PoisonToxin(PotionUtility.getNextOptimalPotId(), true, 5149489, "toxin");
        PoisonContagion.INSTANCE = new PoisonContagion(PotionUtility.getNextOptimalPotId(), false, 5149489, "virus");
        PoisonFrostBite.INSTANCE = new PoisonFrostBite(PotionUtility.getNextOptimalPotId(), false, 5149489, "frostBite");

		CreativeTabHandler.itemStack = new ItemStack(DefenseTechBlocks.blockExplosive);

        /** Dispenser Handler */
        BlockDispenser.dispenseBehaviorRegistry.putObject(DefenseTechItems.itemGrenade, new IBehaviorDispenseItem() {
            @Override
            public ItemStack dispense(IBlockSource blockSource, ItemStack itemStack)
            {
                World world = blockSource.getWorld();

                if(!world.isRemote)
                {
                    int x = blockSource.getXInt();
                    int y = blockSource.getYInt();
                    int z = blockSource.getZInt();
                    EnumFacing enumFacing = EnumFacing.getFront(blockSource.getBlockMetadata());

                    EntityGrenade entity = new EntityGrenade(world, new Pos3D(x, y, z), itemStack.getItemDamage());
                    entity.setThrowableHeading(enumFacing.getFrontOffsetX(), 0.1D, enumFacing.getFrontOffsetZ(), 0.5F, 1.0F);
                    world.spawnEntityInWorld(entity);
                }

                itemStack.stackSize--;
               
                return itemStack;
            }
        });

        BlockDispenser.dispenseBehaviorRegistry.putObject(DefenseTechItems.itemBombCart, new IBehaviorDispenseItem() {
            private final BehaviorDefaultDispenseItem defaultItemDispenseBehavior = new BehaviorDefaultDispenseItem();

            @Override
            public ItemStack dispense(IBlockSource blockSource, ItemStack itemStack)
            {
                World world = blockSource.getWorld();

                if(!world.isRemote)
                {
                    int x = blockSource.getXInt();
                    int y = blockSource.getYInt();
                    int z = blockSource.getZInt();

                    EnumFacing var3 = EnumFacing.getFront(blockSource.getBlockMetadata());
                    World var4 = blockSource.getWorld();
                    double var5 = blockSource.getX() + var3.getFrontOffsetX() * 1.125F;
                    double var7 = blockSource.getY();
                    double var9 = blockSource.getZ() + var3.getFrontOffsetZ() * 1.125F;
                    int var11 = blockSource.getXInt() + var3.getFrontOffsetX();
                    int var12 = blockSource.getYInt();
                    int var13 = blockSource.getZInt() + var3.getFrontOffsetZ();
                    Block var14 = var4.getBlock(var11, var12, var13);
                    double var15;

                    if(BlockRailBase.func_150051_a(var14))
                    {
                        var15 = 0.0D;
                    }
                    else {
                        if(var14 != null || !BlockRailBase.func_150051_a(var4.getBlock(var11, var12 - 1, var13)))
                        {
                            return defaultItemDispenseBehavior.dispense(blockSource, itemStack);
                        }

                        var15 = -1.0D;
                    }

                    EntityBombCart var22 = new EntityBombCart(world, var5, var7 + var15, var9, itemStack.getItemDamage());
                    world.spawnEntityInWorld(var22);
                    world.playAuxSFX(1000, x, y, z, 0);
                }

                itemStack.stackSize--;
                
                return itemStack;
            }
        });

        /** Chunk loading handler. */
        ForgeChunkManager.setForcedChunkLoadingCallback(this, new LoadingCallback() {
            @Override
            public void ticketsLoaded(List<Ticket> tickets, World world)
            {
                for(Ticket ticket : tickets)
                {
                    if(ticket.getEntity() instanceof IChunkLoadHandler)
                    {
                        ((IChunkLoadHandler)ticket.getEntity()).chunkLoaderInit(ticket);
                    }
                    else {
                        if(ticket.getModData() != null)
                        {
                            Coord4D position = Coord4D.read(ticket.getModData());

                            TileEntity tileEntity = position.getTileEntity(ticket.world);

                            if(tileEntity instanceof IChunkLoadHandler)
                            {
                                ((IChunkLoadHandler)tileEntity).chunkLoaderInit(ticket);
                            }
                        }
                    }
                }
            }
        });

        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        EntityRegistry.registerGlobalEntityID(EntityFlyingBlock.class, Reference.ENTITY_PREFIX + "GravityBlock", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityFragments.class, Reference.ENTITY_PREFIX + "Fragments", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityExplosive.class, Reference.ENTITY_PREFIX + "Explosive", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityMissile.class, Reference.ENTITY_PREFIX + "Missile", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityExplosion.class, Reference.ENTITY_PREFIX + "ProceduralExplosion", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityLightBeam.class, Reference.ENTITY_PREFIX + "LightBeam", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityGrenade.class, Reference.ENTITY_PREFIX + "Grenade", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityBombCart.class, Reference.ENTITY_PREFIX + "BombCart", EntityRegistry.findGlobalUniqueEntityId());

        EntityRegistry.registerModEntity(EntityFlyingBlock.class, Reference.ENTITY_PREFIX + "GravityBlock", 0, this, 50, 15, true);
        EntityRegistry.registerModEntity(EntityFragments.class, Reference.ENTITY_PREFIX + "Fragments", 1, this, 40, 8, true);
        EntityRegistry.registerModEntity(EntityExplosive.class, Reference.ENTITY_PREFIX + "Explosive", 2, this, 50, 5, true);
        EntityRegistry.registerModEntity(EntityMissile.class, Reference.ENTITY_PREFIX + "Missile", 3, this, 500, 1, true);
        EntityRegistry.registerModEntity(EntityExplosion.class, Reference.ENTITY_PREFIX + "ProceduralExplosion", 4, this, 100, 5, true);
        EntityRegistry.registerModEntity(EntityLightBeam.class, Reference.ENTITY_PREFIX + "LightBeam", 5, this, 80, 5, true);
        EntityRegistry.registerModEntity(EntityGrenade.class, Reference.ENTITY_PREFIX + "Grenade", 6, this, 50, 5, true);
        EntityRegistry.registerModEntity(EntityBombCart.class, Reference.ENTITY_PREFIX + "BombCart", 7, this, 50, 4, true);
        
        netHandler.registerMessage(PacketItem.class, ItemMessage.class, 0, Side.SERVER);
        
        //Get data from server
      	new ThreadGetData();

        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.tnt, new Object[] { "@@@", "@R@", "@@@", '@', Items.gunpowder, 'R', Items.redstone }));

        // Poison Powder
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(DefenseTechItems.itemPoisonPowder, 3), new Object[] { Items.spider_eye, Items.rotten_flesh }));
        /** Add all Recipes */
        // Tracker
        GameRegistry.addRecipe(new ShapedOreRecipe(((ItemEnergized)DefenseTechItems.itemTracker).getUnchargedItem(), new Object[] { " Z ", "SBS", "SCS", 'Z', Items.compass, 'C', "circuitBasic", 'B', MekanismItems.EnergyTablet.getUnchargedItem(), 'S', Items.iron_ingot }));
        GameRegistry.addRecipe(new ShapedOreRecipe(((ItemEnergized)DefenseTechItems.itemTracker).getUnchargedItem(), new Object[] { " Z ", "SBS", "SCS", 'Z', Items.compass, 'C', "circuitBasic", 'B', Items.ender_pearl, 'S', Items.iron_ingot }));
        // Signal Disrupter
        GameRegistry.addRecipe(new ShapedOreRecipe(((ItemEnergized)DefenseTechItems.itemSignalDisrupter).getUnchargedItem(), new Object[] { "WWW", "SCS", "SSS", 'S', Items.iron_ingot, 'C', "circuitBasic", 'W', new ItemStack(MekanismItems.PartTransmitter, 1, 0) }));

        // Antidote
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(DefenseTechItems.itemAntidote, 6), new Object[] { "@@@", "@@@", "@@@", '@', Items.pumpkin_seeds }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(DefenseTechItems.itemAntidote), new Object[] { "@@@", "@@@", "@@@", '@', Items.wheat_seeds }));
        
        /** Add all Recipes */
        // Rocket Launcher
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(((ItemEnergized)DefenseTechItems.itemRocketLauncher).getUnchargedItem(), new Object[] { "SCR", "SB ", 'R', ((ItemEnergized)DefenseTechItems.itemRadarGun).getUnchargedItem(), 'C', new ItemStack(DefenseTechBlocks.blockMachine, 1, MachineData.CruiseLauncher.ordinal()), 'B', Blocks.stone_button, 'S', "ingotSteel" }));
        // Radar Gun
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(((ItemEnergized)DefenseTechItems.itemRadarGun).getUnchargedItem(), new Object[] { "@#!", " $!", "  !", '@', "blockGlass", '!', "ingotSteel", '#', "circuitBasic", '$', Blocks.stone_button }));
        // Remote
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(((ItemEnergized)DefenseTechItems.itemRemoteDetonator).getUnchargedItem(), new Object[] { "?@@", "@#$", "@@@", '@', "ingotSteel", '?', Items.redstone, '#', "circuitAdvanced", '$', Blocks.stone_button }));
        // Laser Designator
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(((ItemEnergized)DefenseTechItems.itemLaserDesignator).getUnchargedItem(), new Object[] { "!  ", " ? ", "  @", '@', ((ItemEnergized)DefenseTechItems.itemRemoteDetonator).getUnchargedItem(), '?', "circuitElite", '!', ((ItemEnergized)DefenseTechItems.itemRadarGun).getUnchargedItem() }));
        // Defuser
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(((ItemEnergized)DefenseTechItems.itemDefuser).getUnchargedItem(), new Object[] { "I  ", " W ", "  C", 'C', "circuitAdvanced", 'W', MekanismItems.Configurator.getUnchargedItem(), 'I', new ItemStack(MekanismItems.PartTransmitter, 1, 0) }));
        // Missile Launcher Platform
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(DefenseUtils.getLauncherBase(BaseTier.BASIC), new Object[] { "! !", "!C!", "!!!", '!', "ingotIron", 'C', "circuitBasic" }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(DefenseUtils.getLauncherBase(BaseTier.ADVANCED), new Object[] { "! !", "!C!", "!@!", '@', DefenseUtils.getLauncherBase(BaseTier.BASIC), '!', "ingotOsmium", 'C', "circuitAdvanced" }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(DefenseUtils.getLauncherBase(BaseTier.ELITE), new Object[] { "! !", "!C!", "!@!", '@', DefenseUtils.getLauncherBase(BaseTier.ADVANCED), '!', "ingotGold", 'C', "circuitElite" }));
        // Missile Launcher Panel
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(DefenseUtils.getLauncherScreen(BaseTier.BASIC), new Object[] { "!!!", "!#!", "!?!", '#', "circuitBasic", '!', "blockGlass", '?', new ItemStack(MekanismItems.PartTransmitter, 1, 0) }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(DefenseUtils.getLauncherScreen(BaseTier.ADVANCED), new Object[] { "!$!", "!#!", "!?!", '#', "circuitAdvanced", '!', "ingotOsmium", '?', new ItemStack(MekanismItems.PartTransmitter, 1, 0), '$', DefenseUtils.getLauncherScreen(BaseTier.BASIC) }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(DefenseUtils.getLauncherScreen(BaseTier.ELITE), new Object[] { "!$!", "!#!", "!?!", '#', "circuitElite", '!', "ingotGold", '?', new ItemStack(MekanismItems.PartTransmitter, 1, 0), '$', DefenseUtils.getLauncherScreen(BaseTier.ADVANCED) }));
        // Missile Launcher Support Frame
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(DefenseUtils.getLauncherFrame(BaseTier.BASIC), new Object[] { "! !", "!!!", "! !", '!', "ingotIron" }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(DefenseUtils.getLauncherFrame(BaseTier.ADVANCED), new Object[] { "! !", "!@!", "! !", '!', "ingotOsmium", '@', DefenseUtils.getLauncherFrame(BaseTier.BASIC) }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(DefenseUtils.getLauncherFrame(BaseTier.ELITE), new Object[] { "! !", "!@!", "! !", '!', "ingotGold", '@', DefenseUtils.getLauncherFrame(BaseTier.ADVANCED) }));
        // Radar Station
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(new ItemStack(DefenseTechBlocks.blockMachine, 1, 3), new Object[] { "?@?", " ! ", "!#!", '@', ((ItemEnergized)DefenseTechItems.itemRadarGun).getUnchargedItem(), '!', "alloyAdvanced", '#', "circuitBasic", '?', "ingotGold" }));
        // EMP Tower
        GameRegistry.addRecipe(new ShapedMekanismRecipe(new ItemStack(DefenseTechBlocks.blockMachine, 1, 4), new Object[] { "?W?", "@!@", "?#?", '?', "alloyElite", '!', "circuitElite", '@', MekanismUtils.getEnergyCube(EnergyCubeTier.BASIC), '#', new ItemStack(MekanismBlocks.BasicBlock, 1, 8), 'W', new ItemStack(MekanismItems.PartTransmitter, 1, 0) }));
        // Cruise Launcher
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(new ItemStack(DefenseTechBlocks.blockMachine, 1, 5), new Object[] { "?! ", "@@@", '@', "alloyAdvanced", '!', new ItemStack(DefenseTechBlocks.blockMachine, 1, 2), '?', new ItemStack(DefenseTechBlocks.blockMachine, 1, 8) }));
        // Missile module
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(new ItemStack(DefenseTechItems.itemMissile, 1, Explosive.missileModule.getID()), new Object[] { " @ ", "@#@", "@?@", '@', "ingotSteel", '?', Items.flint_and_steel, '#', "circuitBasic" }));
        // Homing
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(new ItemStack(DefenseTechItems.itemMissile, 1, Explosive.homing.getID()), new Object[] { " B ", " C ", "BMB", 'M', new ItemStack(DefenseTechItems.itemMissile, 1, Explosive.missileModule.getID()), 'C', "circuitBasic", 'B', "ingotBronze" }));
        // Anti-ballistic
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(new ItemStack(DefenseTechItems.itemMissile, 1, Explosive.antiBallistic.getID()), new Object[] { "!", "?", "@", '@', new ItemStack(DefenseTechItems.itemMissile, 1, Explosive.missileModule.getID()), '?', new ItemStack(DefenseTechBlocks.blockExplosive, 1, 0), '!', "circuitBasic" }));
        // Cluster
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(new ItemStack(DefenseTechItems.itemMissile, 1, Explosive.cluster.getID()), new Object[] { " ! ", " ? ", "!@!", '@', new ItemStack(DefenseTechItems.itemMissile, 1, Explosive.missileModule.getID()), '?', Explosive.fragmentation.getItemStack(), '!', new ItemStack(DefenseTechItems.itemMissile, 1, 0) }));
        // Nuclear Cluster
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(new ItemStack(DefenseTechItems.itemMissile, 1, Explosive.nuclearCluster.getID()), new Object[] { " N ", "NCN", 'C', new ItemStack(DefenseTechItems.itemMissile, 1, Explosive.cluster.getID()), 'N', Explosive.nuclear.getItemStack() }));

        // Add all explosive recipes.
        if(!Loader.isModLoaded("ResonantInduction|Atomic")) //TODO wither skull? what?
        {
            OreDictionary.registerOre("strangeMatter", new ItemStack(Items.skull, 1, 1));
        }

        for(Explosive explosive : ExplosiveRegistry.getExplosives())
        {
            explosive.init();
            // Missile
            
            if(explosive.hasBlockForm())
            {
            	CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(new ItemStack(DefenseTechItems.itemMissile, 1, explosive.getID()), new Object[] { new ItemStack(DefenseTechItems.itemMissile, 1, Explosive.missileModule.getID()), new ItemStack(DefenseTechBlocks.blockExplosive, 1, explosive.getID()) }));
            }
           
            if(explosive.getTier() < 2)
            {
                // Grenade
            	CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(new ItemStack(DefenseTechItems.itemGrenade, 1, explosive.getID()), new Object[] { "?", "@", '@', new ItemStack(DefenseTechBlocks.blockExplosive, 1, explosive.getID()), '?', Items.string }));
            }
            
            if(explosive.getTier() < 3)
            {
                // Minecart
            	CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(new ItemStack(DefenseTechItems.itemBombCart, 1, explosive.getID()), new Object[] { "?", "@", '?', new ItemStack(DefenseTechBlocks.blockExplosive, 1, explosive.getID()), '@', Items.minecart }));
            }
        }
    }

    @EventHandler
	public void serverStarting(FMLServerStartingEvent evt)
	{
    	FrequencyGrid.reinitiate();
    	
    	// Setup command
        ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
        ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager);
        serverCommandManager.registerCommand(new CommandHandler());
	}
    
    @SubscribeEvent
    public void enteringChunk(EnteringChunk evt)
    {
        if(evt.entity instanceof EntityMissile)
        {
            ((EntityMissile)evt.entity).updateLoadChunk(evt.newChunkX, evt.newChunkZ);
        }
    }

    @SubscribeEvent
    public void creeperDeathEvent(LivingDeathEvent evt)
    {
        if(evt.entityLiving instanceof EntityCreeper)
        {
            if(Settings.CREEPER_BLOW_UP_IN_FIRE)
            {
                if(evt.source == DamageSource.onFire || evt.source == DamageSource.inFire)
                {
                    evt.setCanceled(true);
                    boolean flag = evt.entityLiving.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");

                    if(((EntityCreeper)evt.entityLiving).getPowered())
                    {
                        evt.entityLiving.worldObj.createExplosion(evt.entityLiving, evt.entityLiving.posX, evt.entityLiving.posY, evt.entityLiving.posZ, 6f, flag);
                    }
                    else {
                        evt.entityLiving.worldObj.createExplosion(evt.entityLiving, evt.entityLiving.posX, evt.entityLiving.posY, evt.entityLiving.posZ, 3f, flag);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void creeperDropEvent(LivingDropsEvent evt)
    {
        if(evt.entityLiving instanceof EntityCreeper)
        {
            if(Settings.CREEPER_DROP_SULFUR)
            {
                evt.entityLiving.entityDropItem(new ItemStack(MekanismItems.OtherDust, 1 + evt.entityLiving.worldObj.rand.nextInt(6), 3), 0.0F);
            }
        }
    }
    
    @SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(event.modID.equals(Reference.NAME))
		{
			proxy.loadConfiguration();
		}
	}
}