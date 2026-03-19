package defense.common.explosive;

import mekanism.api.Pos3D;
import mekanism.common.util.LangUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.api.IExplosive;
import defense.client.model.missile.ModelMissileBase;
import defense.common.DefenseTechBlocks;
import defense.common.explosion.ExAntiGravitational;
import defense.common.explosion.ExAntimatter;
import defense.common.explosion.ExBreaching;
import defense.common.explosion.ExChemical;
import defense.common.explosion.ExCondensed;
import defense.common.explosion.ExDebilitation;
import defense.common.explosion.ExEMP;
import defense.common.explosion.ExEndothermic;
import defense.common.explosion.ExExothermic;
import defense.common.explosion.ExIncendiary;
import defense.common.explosion.ExNuclear;
import defense.common.explosion.ExRedMatter;
import defense.common.explosion.ExRejuvenation;
import defense.common.explosion.ExRepulsive;
import defense.common.explosion.ExSMine;
import defense.common.explosion.ExShrapnel;
import defense.common.explosion.ExSonic;
import defense.common.explosion.Explosion;
import defense.common.explosion.missile.MissileAntiBallistic;
import defense.common.explosion.missile.MissileCluster;
import defense.common.explosion.missile.MissileHoming;
import defense.common.explosion.missile.MissileModule;
import defense.common.explosion.missile.MissileNuclearCluster;

/** The explosive registry class. Used to register explosions. */
public abstract class Explosive implements IExplosive
{
    /** Explosives */
    public static Explosive condensed;
    public static Explosive shrapnel;
    public static Explosive incendiary;
    public static Explosive debilitation;
    public static Explosive chemical;
    public static Explosive anvil;
    public static Explosive repulsive;
    public static Explosive attractive;

    public static Explosive fragmentation;
    public static Explosive contagious;
    public static Explosive sonic;
    public static Explosive breaching;
    public static Explosive rejuvenation;
    public static Explosive thermobaric;
    public static Explosive sMine;

    public static Explosive nuclear;
    public static Explosive emp;
    public static Explosive exothermic;
    public static Explosive endothermic;
    public static Explosive antiGrav;
    public static Explosive hypersonic;

    public static Explosive antimatter;
    public static Explosive redMatter;

    /** Missiles */
    public static Explosion missileModule;
    public static Explosion homing;
    public static Explosion antiBallistic;
    public static Explosion cluster;
    public static Explosion nuclearCluster;

    public static boolean registered = false;

    public static void register()
    {
    	if(!registered)
    	{
	        condensed = ExplosiveRegistry.register(new ExCondensed("condensed", 1));
	        shrapnel = ExplosiveRegistry.register(new ExShrapnel("shrapnel", 1));
	        incendiary = ExplosiveRegistry.register(new ExIncendiary("incendiary", 1));
	        debilitation = ExplosiveRegistry.register(new ExDebilitation("debilitation", 1));
	        chemical = ExplosiveRegistry.register(new ExChemical("chemical", 1));
	        anvil = ExplosiveRegistry.register(new ExShrapnel("anvil", 1));
	        repulsive = ExplosiveRegistry.register(new ExRepulsive("repulsive", 1));
	        attractive = ExplosiveRegistry.register(new ExRepulsive("attractive", 1));
	
	        fragmentation = ExplosiveRegistry.register(new ExShrapnel("fragmentation", 2));
	        contagious = ExplosiveRegistry.register(new ExChemical("contagious", 2));
	        sonic = ExplosiveRegistry.register(new ExSonic("sonic", 2));
	        breaching = ExplosiveRegistry.register(new ExBreaching());
	        rejuvenation = ExplosiveRegistry.register(new ExRejuvenation());
	        thermobaric = ExplosiveRegistry.register(new ExNuclear("thermobaric", 2));
	        sMine = ExplosiveRegistry.register(new ExSMine("sMine", 2));
	
	        nuclear = ExplosiveRegistry.register(new ExNuclear("nuclear", 3));
	        emp = ExplosiveRegistry.register(new ExEMP());
	        exothermic = ExplosiveRegistry.register(new ExExothermic());
	        endothermic = ExplosiveRegistry.register(new ExEndothermic());
	        antiGrav = ExplosiveRegistry.register(new ExAntiGravitational());
	        hypersonic = ExplosiveRegistry.register(new ExSonic("hypersonic", 3));
	
	        antimatter = ExplosiveRegistry.register(new ExAntimatter());
	        redMatter = ExplosiveRegistry.register(new ExRedMatter());
	
	        /** Missiles */
	        missileModule = (Explosion)ExplosiveRegistry.register(new MissileModule());
	        homing = (Explosion)ExplosiveRegistry.register(new MissileHoming());
	        antiBallistic = (Explosion)ExplosiveRegistry.register(new MissileAntiBallistic());
	        cluster = (Explosion)ExplosiveRegistry.register(new MissileCluster("cluster", 2));
	        nuclearCluster = (Explosion)ExplosiveRegistry.register(new MissileNuclearCluster());
    	}
        
        registered = true;
    }

    /** The unique identification name for this explosive. */
    private String nameID;
    /** The tier of this explosive */
    private int tier;
    /** The fuse of this explosive */
    private int fuseTime;
    /** Is this explosive disabled? */
    public boolean isDisabled;
    /** Is this explosive able to be pushed by other explosions? */
    protected boolean isMobile = false;

    protected boolean hasBlock;
    protected boolean hasGrenade;
    protected boolean hasMinecart;
    protected boolean hasMissile;

    protected Explosive(String name, int t)
    {
        nameID = name;
        tier = t;
        fuseTime = 100;

        hasBlock = true;
        hasMissile = true;
        hasGrenade = tier <= 1;
        hasMinecart = tier <= 2;
    }

    @Override
    public final int getID()
    {
        return ExplosiveRegistry.getID(getUnlocalizedName());
    }

    @Override
    public String getUnlocalizedName()
    {
        return nameID;
    }

    @Override
    public String getExplosiveName()
    {
        return LangUtils.localize("explosive." + nameID + ".name");
    }

    @Override
    public String getGrenadeName()
    {
        return LangUtils.localize("grenade." + nameID + ".name");
    }

    @Override
    public String getMissileName()
    {
        return LangUtils.localize("missile." + nameID + ".name");
    }

    @Override
    public String getMinecartName()
    {
        return LangUtils.localize("minecart." + nameID + ".name");
    }

    @Override
    public int getTier()
    {
        return tier;
    }

    @Override
    public void setTier(int t)
    {
        tier = t;
    }

    public Explosive setFuseTime(int fuse)
    {
        fuseTime = fuse;
        return this;
    }

    /** The fuse of the explosion
     * 
     * @return The Fuse */
    public int getYinXin()
    {
        return fuseTime;
    }

    /** Called at the before the explosive detonated as a block.
     * 
     * @param world
     * @param entity */
    public void onPreDetonation(World world, Entity entity)
    {
        world.playSoundAtEntity(entity, "game.tnt.primed", 1.0F, 1.0F);
    }

    /** Called while the explosive is being detonated (fuse ticks) in block form.
     * 
     * @param fuseTicks - The amount of ticks this explosive is on fuse */
    public void onDetonation(World world, Pos3D position, int fuseTicks)
    {
        world.spawnParticle("smoke", position.xPos, position.yPos + 0.5D, position.zPos, 0.0D, 0.0D, 0.0D);
    }

    /** Called when the block for of this explosive is destroy by an explosion
     * 
     * @return - Fuse left */
    public int onBlockExploded()
    {
        return (int)(fuseTime / 2 + Math.random() * fuseTime / 4);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getBlockModel()
    {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getBlockResource()
    {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
    {
        return null;
    }

    public boolean hasGrenadeForm()
    {
        return hasGrenade;
    }

    public boolean hasMissileForm()
    {
        return hasMissile;
    }

    public boolean hasMinecartForm()
    {
        return hasMinecart;
    }

    public boolean hasBlockForm()
    {
        return hasBlock;
    }

    /** Called to add the recipe for this explosive */
    public void init()
    {

    }

    public ItemStack getItemStack()
    {
        return getItemStack(1);
    }

    public ItemStack getItemStack(int amount)
    {
        return new ItemStack(DefenseTechBlocks.blockExplosive, amount, getID());
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
    {
        return false;
    }

    @Override
    public void createExplosion(World world, double x, double y, double z, Entity entity)
    {
        if(!isDisabled)
        {
            doCreateExplosion(world, x, y, z, entity);
        }
    }

    public abstract void doCreateExplosion(World world, double x, double y, double z, Entity entity);
}
