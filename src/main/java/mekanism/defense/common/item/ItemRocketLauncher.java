package defense.common.item;

import java.util.HashMap;
import java.util.List;

import mekanism.api.EnumColor;
import mekanism.api.Pos3D;
import mekanism.common.item.ItemEnergized;
import mekanism.common.util.LangUtils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.api.ExplosionEvent.ExplosivePreDetonationEvent;
import defense.api.ExplosiveType;
import defense.common.CreativeTabHandler;
import defense.common.Reference;
import defense.common.Settings;
import defense.common.entity.EntityMissile;
import defense.common.explosion.Explosion;
import defense.common.explosive.Explosive;
import defense.common.explosive.ExplosiveRegistry;

/** Rocket Launcher
 * 
 * @author Calclavia */

public class ItemRocketLauncher extends ItemEnergized
{
    private static final int ENERGY = 1000000;
    private static final int firingDelay = 1000;
    private HashMap<String, Long> clickTimePlayer = new HashMap<String, Long>();

    public ItemRocketLauncher()
    {
        super(8000000);
        
        this.setUnlocalizedName("rocketLauncher");
        this.setCreativeTab(CreativeTabHandler.INSTANCE);
        this.setTextureName(Reference.PREFIX + "rocketLauncher");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {}

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.bow;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            long clickMs = System.currentTimeMillis();
            if (clickTimePlayer.containsKey(player.getCommandSenderName()))
            {
                if (clickMs - clickTimePlayer.get(player.getCommandSenderName()) < firingDelay)
                {
                    //TODO play weapon empty click audio to note the gun is reloading
                    return itemStack;
                }
            }
            if (this.getEnergy(itemStack) >= ENERGY || player.capabilities.isCreativeMode)
            {
                // Check the player's inventory and look for missiles.
                for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++)
                {
                    ItemStack inventoryStack = player.inventory.getStackInSlot(slot);

                    if (inventoryStack != null)
                    {
                        if (inventoryStack.getItem() instanceof ItemMissile)
                        {
                            int meta = inventoryStack.getItemDamage();
                            Explosive ex = ExplosiveRegistry.get(meta);

                            if (ex instanceof Explosion)
                            {
                                ExplosivePreDetonationEvent evt = new ExplosivePreDetonationEvent(world, player.posX, player.posY, player.posZ, ExplosiveType.AIR, ExplosiveRegistry.get(meta));
                                MinecraftForge.EVENT_BUS.post(evt);

                                if (((Explosion) ex) != null && !evt.isCanceled())
                                {
                                    // Limit the missile to tier two.
                                    if (((Explosion) ex).getTier() <= Settings.MAX_ROCKET_LAUNCHER_TIER && ((Explosion) ex).isCruise())
                                    {
                                        Pos3D launcher = new Pos3D(player).translate(new Pos3D(0, 0.5, 0));
                                        Pos3D playerAim = new Pos3D(player.getLook(1));
                                        Pos3D start = launcher.clone().translate(playerAim.scale(1.1));
                                        Pos3D target = launcher.clone().translate(playerAim.scale(100));
                                        //TODO: possible need to fix this, scale is not cloned (aidancbrady)

                                        //TOD: Fix this rotation when we use the proper model loader.
                                        EntityMissile entityMissile = new EntityMissile(world, start, ((Explosion) ex).getID(), -player.rotationYaw, -player.rotationPitch);
                                        world.spawnEntityInWorld(entityMissile);

                                        if (player.isSneaking())
                                        {
                                            player.mountEntity(entityMissile);
                                            player.setSneaking(false);
                                        }

                                        entityMissile.ignore(player);
                                        entityMissile.launch(target);

                                        if (!player.capabilities.isCreativeMode)
                                        {
                                            player.inventory.setInventorySlotContents(slot, null);
                                            this.setEnergy(itemStack, this.getEnergy(itemStack) - ENERGY);
                                        }
                                        
                                        //Store last time player launched a rocket
                                        clickTimePlayer.put(player.getCommandSenderName(), clickMs);
                                        
                                        return itemStack;
                                    }
                                }
                                else
                                {
                                    player.addChatMessage(new ChatComponentText(Reference.CHAT_DESC + EnumColor.GREY + " " + LangUtils.localize("message.launcher.protected")));
                                }
                            }

                        }
                    }
                }
            }
        }

        return itemStack;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4)
    {
        String str = LangUtils.localize("info.rocketlauncher.tooltip").replaceAll("%s", String.valueOf(Settings.MAX_ROCKET_LAUNCHER_TIER));
        list.add(str);

        super.addInformation(itemStack, entityPlayer, list, par4);
    }
}
