package defense.common.integration.flans;

import com.flansmod.common.guns.BulletType;
import com.flansmod.common.guns.EntityShootable;
import com.flansmod.common.guns.ItemBullet;
import com.flansmod.common.types.InfoType;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.common.Reference;
import defense.common.explosive.Explosive;
import defense.common.explosive.ExplosiveRegistry;

/** Flan ItemBullet adapter carrying one DefenseTech block-explosive payload. */
public class ItemDefenseTechBomb extends ItemBullet
{
    private final int explosiveId;

    public ItemDefenseTechBomb(BulletType type, int explosiveId)
    {
        super(type);
        this.explosiveId = explosiveId;
    }

    public int getExplosiveId()
    {
        return explosiveId;
    }

    @Override
    public EntityShootable getEntity(World world, Vec3 origin, float yaw, float pitch, double motionX, double motionY, double motionZ,
          EntityLivingBase shooter, float gunDamage, int itemDamage, InfoType shotFrom)
    {
        return new EntityDefenseTechBomb(world, origin, yaw, pitch, motionX, motionY, motionZ, shooter, gunDamage, type, shotFrom, explosiveId);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        Explosive explosive = ExplosiveRegistry.get(explosiveId);
        String payloadName = explosive == null ? "DefenseTech" : explosive.getExplosiveName();
        return StatCollector.translateToLocalFormatted("item.defenseTechAircraftBomb.name", payloadName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        itemIcon = iconRegister.registerIcon(Reference.PREFIX + "grenade_condensed");
    }
}
