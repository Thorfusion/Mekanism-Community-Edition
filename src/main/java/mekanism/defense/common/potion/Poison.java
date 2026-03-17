package defense.common.potion;

import java.util.EnumSet;
import java.util.HashMap;

import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import defense.api.IAntiPoisonArmor;
import defense.api.IAntiPoisonBlock;
import defense.common.Settings;

/** A poison registry class used to register different types of poison effects.
 * 
 * @author Calclavia */
public abstract class Poison
{
    public enum ArmorType
    {
        HELM,
        BODY,
        LEGGINGS,
        BOOTS
    }

    static HashMap<String, Poison> poisons = new HashMap();
    static BiMap<String, Integer> poisonIDs = HashBiMap.create();
    private static int maxID = 0;

    protected String name;
    protected EnumSet<ArmorType> armorRequired = EnumSet.range(ArmorType.HELM, ArmorType.BOOTS);
    protected final boolean isDisabled;

    public static Poison getPoison(String name)
    {
        return poisons.get(name);
    }

    public static Poison getPoison(int id)
    {
        return poisons.get(getName(id));
    }

    public static String getName(int fluidID)
    {
        return poisonIDs.inverse().get(fluidID);
    }

    public static int getID(String name)
    {
        return poisonIDs.get(name);
    }

    public Poison(String name)
    {
        this.name = name;
        poisons.put(name, this);
        poisonIDs.put(name, ++maxID);
        isDisabled = Settings.CONFIGURATION.get("Disable Poison", "Disable " + this.name, false).getBoolean(false);
    }

    public String getName()
    {
        return this.name;
    }

    public final int getID()
    {
        return getID(this.getName());
    }

    public EnumSet<ArmorType> getArmorRequired()
    {
        return this.armorRequired;
    }

    /** Called to poison this specific entity with this specific type of poison.
     * 
     * @amiplifier - The amplification value.
     * @armorRequired - The amount of pieces of armor required to be protected.
     * @param entity */
    public void poisonEntity(Pos3D emitPosition, EntityLivingBase entity, int amplifier)
    {
        if (!isEntityProtected(emitPosition, entity, amplifier))
        {
            doPoisonEntity(emitPosition, entity, amplifier);
        }
    }

    public void poisonEntity(Pos3D emitPosition, EntityLivingBase entity)
    {
        this.poisonEntity(emitPosition, entity, 0);
    }

    public boolean isEntityProtected(Pos3D emitPosition, EntityLivingBase entity, int amplifier)
    {
        EnumSet<ArmorType> armorWorn = EnumSet.noneOf(ArmorType.class);

        if (entity instanceof EntityPlayer)
        {
            EntityPlayer entityPlayer = (EntityPlayer) entity;

            for (int i = 0; i < entityPlayer.inventory.armorInventory.length; i++)
            {
                if (entityPlayer.inventory.armorInventory[i] != null)
                {
                    if (entityPlayer.inventory.armorInventory[i].getItem() instanceof IAntiPoisonArmor)
                    {
                        IAntiPoisonArmor armor = (IAntiPoisonArmor) entityPlayer.inventory.armorInventory[i].getItem();

                        if (armor.isProtectedFromPoison(entityPlayer.inventory.armorInventory[i], entity, this.getName()))
                        {
                            armorWorn.add(ArmorType.values()[armor.getArmorType() % ArmorType.values().length]);
                            // TODO: Consider putting this in another method.
                            armor.onProtectFromPoison(entityPlayer.inventory.armorInventory[i], entity, this.getName());
                        }
                    }
                }
            }
        }

        return armorWorn.containsAll(this.armorRequired);
    }

    public int getAntiPoisonBlockCount(World world, Pos3D startingPosition, Pos3D endingPosition)
    {
        Pos3D delta = endingPosition.clone().diff(startingPosition).normalize();
        Pos3D targetPosition = startingPosition.clone();
        double totalDistance = startingPosition.distance(endingPosition);

        int count = 0;

        if (totalDistance > 1)
        {
            while (targetPosition.distance(endingPosition) <= totalDistance)
            {
                Block block = targetPosition.getCoord(world.provider.dimensionId).getBlock(world);

                if (!world.isAirBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos))
                {
                    if (block instanceof IAntiPoisonBlock)
                    {
                        if (((IAntiPoisonBlock) block).isPoisonPrevention(world, (int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, this.getName()))
                        {
                            count++;
                        }
                    }
                }

                targetPosition.translate(delta);
            }
        }

        return count;
    }

    protected abstract void doPoisonEntity(Pos3D emitPosition, EntityLivingBase entity, int amplifier);
}