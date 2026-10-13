package defense.common.integration.flans;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import com.flansmod.common.FlansMod;
import com.flansmod.common.driveables.DriveableType;
import com.flansmod.common.driveables.EntityPlane;
import com.flansmod.common.driveables.EnumWeaponType;
import com.flansmod.common.guns.BulletType;
import com.flansmod.common.types.EnumType;
import com.flansmod.common.types.TypeFile;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import defense.api.RadarRegistry;
import defense.common.DefenseTech;
import defense.common.Reference;
import defense.common.entity.EntityMissile;
import defense.common.explosive.Explosive;
import defense.common.explosive.ExplosiveRegistry;
import defense.common.integration.IFlansModIntegration;
import defense.common.integration.FlansModIntegration;

/** Implementation loaded only after the optional {@code flansmod} mod is found. */
public class FlansModCompat implements IFlansModIntegration
{
    private static final Map<Integer, ItemDefenseTechBomb> BOMB_ITEMS = new LinkedHashMap<Integer, ItemDefenseTechBomb>();
    private static final List<BulletType> BOMB_TYPES = new ArrayList<BulletType>();

    private boolean bombItemsRegistered;

    @Override
    public void preInit()
    {
        MinecraftForge.EVENT_BUS.register(this);

        try
        {
            registerBombItems();
            bombItemsRegistered = !BOMB_ITEMS.isEmpty();
        }
        catch(Throwable throwable)
        {
            bombItemsRegistered = !BOMB_ITEMS.isEmpty();
            DefenseTech.LOGGER.log(Level.WARNING, "Could not register DefenseTech aircraft bombs for Flan's Mod.", throwable);
        }
    }

    @Override
    public void init()
    {
        if(bombItemsRegistered)
        {
            EntityRegistry.registerModEntity(EntityDefenseTechBomb.class, Reference.ENTITY_PREFIX + "FlansAircraftBomb", 8, DefenseTech.INSTANCE, 200, 1, true);
        }
    }

    @Override
    public void postInit()
    {
        if(!bombItemsRegistered)
        {
            return;
        }

        // Some content packs disable AcceptAllAmmo and list every permitted
        // payload. Adding our types here makes the optional bombs work in both
        // permissive and explicitly-whitelisted aircraft.
        for(DriveableType driveableType : DriveableType.types)
        {
            for(BulletType bombType : BOMB_TYPES)
            {
                if(!driveableType.ammo.contains(bombType))
                {
                    driveableType.ammo.add(bombType);
                }
            }
        }

        for(Map.Entry<Integer, ItemDefenseTechBomb> entry : BOMB_ITEMS.entrySet())
        {
            Explosive explosive = ExplosiveRegistry.get(entry.getKey());

            if(explosive != null && explosive.hasBlockForm())
            {
                GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(entry.getValue()),
                      " S ",
                      "SES",
                      " S ",
                      'S', "ingotSteel",
                      'E', explosive.getItemStack()));
            }
        }
    }

    private void registerBombItems() throws Exception
    {
        Map<Integer, Explosive> explosives = new TreeMap<Integer, Explosive>(ExplosiveRegistry.getAll());

        for(Map.Entry<Integer, Explosive> entry : explosives.entrySet())
        {
            Explosive explosive = entry.getValue();

            if(explosive == null || !explosive.hasBlockForm())
            {
                continue;
            }

            int explosiveId = entry.getKey();
            String shortName = "defensetech_" + explosive.getUnlocalizedName().replaceAll("[^A-Za-z0-9_]", "_").toLowerCase(Locale.ROOT) + "_aircraft_bomb";
            TypeFile typeFile = createTypeFile(shortName);
            typeFile.lines.add("ShortName " + shortName);
            typeFile.lines.add("Name " + explosive.getExplosiveName() + " Aircraft Bomb");
            typeFile.lines.add("Description DefenseTech payload for Flan aircraft");
            typeFile.lines.add("Icon grenade_condensed");
            typeFile.lines.add("Bomb true");
            typeFile.lines.add("WeaponType BOMB");
            typeFile.lines.add("RoundsPerItem 1");
            typeFile.lines.add("MaxStackSize 1");
            typeFile.lines.add("DespawnTime 1200");
            typeFile.lines.add("ExplodeOnImpact true");
            typeFile.lines.add("Damage 0");
            typeFile.lines.add("Penetration 0");
            typeFile.lines.add("ExplosionRadius 0");

            BulletType type = new BulletType(typeFile);
            type.read(typeFile);

            // Keep these values explicit for older Flan builds whose type-file
            // parser predates some of the aliases accepted by FMUSE.
            type.weaponType = EnumWeaponType.BOMB;
            type.roundsPerItem = 1;
            type.maxStackSize = 1;
            type.despawnTime = 1200;
            type.explodeOnImpact = true;
            type.penetratingPower = 0;
            type.explosionRadius = 0;
            type.texture = "defaultBullet";
            type.colour = bombColour(explosiveId, explosive.getTier());

            ItemDefenseTechBomb item = new ItemDefenseTechBomb(type, explosiveId);
            FlansMod.bulletItems.add(item);
            BOMB_TYPES.add(type);
            BOMB_ITEMS.put(explosiveId, item);
        }
    }

    /**
     * FMUSE added a content-pack parameter to TypeFile. Constructing it via
     * reflection lets the same integration remain usable with classic 1.7.10
     * Flan builds that expose the older constructor.
     */
    private static TypeFile createTypeFile(String shortName) throws Exception
    {
        try
        {
            Constructor<TypeFile> constructor = TypeFile.class.getConstructor(EnumType.class, String.class, String.class, Boolean.TYPE);
            return constructor.newInstance(EnumType.bullet, shortName, "DefenseTech", false);
        }
        catch(NoSuchMethodException ignored) {}

        try
        {
            Constructor<TypeFile> constructor = TypeFile.class.getConstructor(EnumType.class, String.class, Boolean.TYPE);
            return constructor.newInstance(EnumType.bullet, shortName, false);
        }
        catch(NoSuchMethodException ignored) {}

        try
        {
            Constructor<TypeFile> constructor = TypeFile.class.getConstructor(EnumType.class, String.class, String.class);
            return constructor.newInstance(EnumType.bullet, shortName, "DefenseTech");
        }
        catch(NoSuchMethodException ignored) {}

        Constructor<TypeFile> constructor = TypeFile.class.getConstructor(EnumType.class, String.class);
        return constructor.newInstance(EnumType.bullet, shortName);
    }

    private static int bombColour(int explosiveId, int tier)
    {
        int[] palette = { 0xD8D8D8, 0xF1B84B, 0xE66B58, 0xB06DE0, 0x62C7C9, 0x8FD05A };
        return palette[Math.abs(explosiveId + tier) % palette.length];
    }

    public static Collection<BulletType> getBombTypes()
    {
        return BOMB_TYPES;
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if(FlansModIntegration.isEnabled() && event.entity instanceof EntityPlane)
        {
            RadarRegistry.register(event.entity);
        }
    }

    @Override
    public Entity findNearestAircraft(World world, AxisAlignedBB bounds, Entity excluded)
    {
        Entity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        @SuppressWarnings("unchecked")
        List<EntityPlane> aircraft = world.getEntitiesWithinAABB(EntityPlane.class, bounds);

        for(EntityPlane plane : aircraft)
        {
            if(plane == excluded || plane.isDead)
            {
                continue;
            }

            double distance = excluded == null ? 0 : excluded.getDistanceSqToEntity(plane);

            if(distance < nearestDistance)
            {
                nearest = plane;
                nearestDistance = distance;
            }
        }

        return nearest;
    }

    @Override
    public boolean isAircraft(Entity entity)
    {
        return entity instanceof EntityPlane;
    }

    @Override
    public void interceptAircraft(Entity aircraft, EntityMissile interceptor)
    {
        if(aircraft instanceof EntityPlane && !aircraft.isDead)
        {
            aircraft.attackEntityFrom(DamageSource.generic, Float.MAX_VALUE);
        }
    }
}
