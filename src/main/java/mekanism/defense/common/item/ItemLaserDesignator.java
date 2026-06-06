package defense.common.item;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.api.Pos3D;
import mekanism.common.Mekanism;
import mekanism.common.item.ItemEnergized;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.util.LangUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.api.FrequencyGrid;
import defense.api.IBlockFrequency;
import defense.api.IItemFrequency;
import defense.common.CreativeTabHandler;
import defense.common.DefenseTech;
import defense.common.DefenseTechBlocks;
import defense.common.DefenseTechItems;
import defense.common.Reference;
import defense.common.Settings;
import defense.common.base.IItemPacket;
import defense.common.entity.EntityLightBeam;
import defense.common.network.PacketItem.ItemMessage;
import defense.common.tile.TileLauncherPrefab;
import defense.common.tile.TileLauncherScreen;

public class ItemLaserDesignator extends ItemEnergized implements IItemFrequency, IItemPacket
{
    public static final int YONG_DIAN_LIANG = 8000;

    public ItemLaserDesignator()
    {
        super(100000);
        
        this.setUnlocalizedName("laserDesignator");
        this.setCreativeTab(CreativeTabHandler.INSTANCE);
        this.setTextureName(Reference.PREFIX + "laserDesignator");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
       itemIcon = reg.registerIcon(getIconString());
    }

    /** Allows items to add custom lines of information to the mouseover description */
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        super.addInformation(itemStack, par2EntityPlayer, par3List, par4);

        if (this.getFrequency(itemStack) > 0)
        {
            par3List.add(LangUtils.localize("info.misc.freq") + " " + getFrequency(itemStack));
        }
        else
        {
            par3List.add(LangUtils.localize("info.designator.noFreq"));
        }
    }

    @Override
    public int getFrequency(ItemStack itemStack)
    {
        if (itemStack.stackTagCompound == null)
        {
            return 0;
        }
        return itemStack.stackTagCompound.getInteger("frequency");
    }

    @Override
    public void setFrequency(int frequency, ItemStack itemStack)
    {
        if (itemStack.stackTagCompound == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        itemStack.stackTagCompound.setInteger("frequency", frequency);
    }

    public int getLauncherCountDown(ItemStack par1ItemStack)
    {
        if (par1ItemStack.stackTagCompound == null)
        {
            return -1;
        }

        return par1ItemStack.stackTagCompound.getInteger("countDown");
    }

    public void setLauncherCountDown(ItemStack par1ItemStack, int value)
    {
        if (par1ItemStack.stackTagCompound == null)
        {
            par1ItemStack.setTagCompound(new NBTTagCompound());
        }

        par1ItemStack.stackTagCompound.setInteger("countDown", value);
    }

    public int getLauncherCount(ItemStack par1ItemStack)
    {
        if (par1ItemStack.stackTagCompound == null)
        {
            return 0;
        }
        return par1ItemStack.stackTagCompound.getInteger("launcherCount");
    }

    public void setLauncherCount(ItemStack par1ItemStack, int value)
    {
        if (par1ItemStack.stackTagCompound == null)
        {
            par1ItemStack.setTagCompound(new NBTTagCompound());
        }

        par1ItemStack.stackTagCompound.setInteger("launcherCount", value);
    }

    public int getLauncherDelay(ItemStack par1ItemStack)
    {
        if (par1ItemStack.stackTagCompound == null)
        {
            return 0;
        }
        return par1ItemStack.stackTagCompound.getInteger("launcherDelay");
    }

    public void setLauncherDelay(ItemStack par1ItemStack, int value)
    {
        if (par1ItemStack.stackTagCompound == null)
        {
            par1ItemStack.setTagCompound(new NBTTagCompound());
        }

        par1ItemStack.stackTagCompound.setInteger("launcherDelay", value);
    }

    /** Called each tick as long the item is on a player inventory. Uses by maps to check if is on a
     * player hand and update it's contents. */
    @Override
    public void onUpdate(ItemStack itemStack, World world, Entity par3Entity, int par4, boolean par5)
    {
        super.onUpdate(itemStack, world, par3Entity, par4, par5);

        if (!world.isRemote)
        {
            List<TileLauncherPrefab> connectedLaunchers = new ArrayList<TileLauncherPrefab>();

            if (this.getLauncherCountDown(itemStack) > 0 || this.getLauncherCount(itemStack) > 0)
            {
                Pos3D position = new Pos3D(par3Entity.posX, par3Entity.posY, par3Entity.posZ);

                for (IBlockFrequency blockFrequency : FrequencyGrid.instance().get(world, position, Settings.MAX_MISSILE_DISTANCE, this.getFrequency(itemStack)))
                {
                    if (blockFrequency instanceof TileLauncherPrefab)
                    {
                        // Do airstrike!
                        TileLauncherPrefab missileLauncher = (TileLauncherPrefab) blockFrequency;

                        if (missileLauncher.canLaunch())
                        {
                            connectedLaunchers.add(missileLauncher);
                        }
                    }
                }
            }

            if (this.getLauncherCountDown(itemStack) > 0 && connectedLaunchers.size() > 0)
            {
                if (this.getLauncherCountDown(itemStack) % 20 == 0)
                {
                    ((EntityPlayer) par3Entity).addChatMessage(new ChatComponentText(Reference.CHAT_DESC + EnumColor.GREY + " " + LangUtils.localize("message.designator.callTime") + " " + (int) Math.floor(this.getLauncherCountDown(itemStack) / 20)));
                }

                if (this.getLauncherCountDown(itemStack) == 1)
                {
                    this.setLauncherCount(itemStack, connectedLaunchers.size());
                    this.setLauncherDelay(itemStack, 0);
                    ((EntityPlayer) par3Entity).addChatMessage(new ChatComponentText(Reference.CHAT_DESC + EnumColor.GREY + " " + LangUtils.localize("message.designator.blast")));
                }

                this.setLauncherCountDown(itemStack, this.getLauncherCountDown(itemStack) - 1);
            }

            if (this.getLauncherCount(itemStack) > 0 && this.getLauncherCount(itemStack) <= connectedLaunchers.size() && connectedLaunchers.size() > 0)
            {
                // Launch a missile every two seconds from different launchers
                if (this.getLauncherDelay(itemStack) % 40 == 0)
                {
                	System.out.println(connectedLaunchers.get(this.getLauncherCount(itemStack) - 1).getTarget());
                    connectedLaunchers.get(this.getLauncherCount(itemStack) - 1).launch();
                    this.setLauncherCount(itemStack, this.getLauncherCount(itemStack) - 1);
                }

                if (this.getLauncherCount(itemStack) == 0)
                {
                    this.setLauncherDelay(itemStack, 0);
                    connectedLaunchers.clear();
                }

                this.setLauncherDelay(itemStack, this.getLauncherDelay(itemStack) + 1);
            }
        }
    }

    /** Callback for item usage. If the item does something special on right clicking, he will have
     * one of those. Return True if something happen and false if it don't. This is for ITEMS, not
     * BLOCKS ! */
    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
        if(!par3World.isRemote)
        {
            // SET FREQUENCY OF REMOTE
            TileEntity tileEntity = par3World.getTileEntity(x, y, z);

            if(tileEntity != null)
            {
                if(tileEntity instanceof TileLauncherPrefab)
                {
                    TileLauncherPrefab missileLauncher = (TileLauncherPrefab)tileEntity;

                    if(missileLauncher.getFrequency() > 0)
                    {
                        setFrequency(missileLauncher.getFrequency(), par1ItemStack);
                        par2EntityPlayer.addChatMessage(new ChatComponentText(Reference.CHAT_DESC + EnumColor.GREY + " " + LangUtils.localize("message.designator.setFreq") + " " + EnumColor.DARK_GREY + getFrequency(par1ItemStack)));
                    }
                    else {
                        par2EntityPlayer.addChatMessage(new ChatComponentText(Reference.CHAT_DESC + EnumColor.GREY + " " + LangUtils.localize("message.designator.failFreq")));
                    }
                }
            }
        }

        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if(world.isRemote)
        {
            MovingObjectPosition objectMouseOver = player.rayTrace(Settings.MAX_MISSILE_DISTANCE * 2, 1);

            if(objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectType.BLOCK)
            {
                Block block = world.getBlock(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
                int blockMetadata = world.getBlockMetadata(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);

                if(getLauncherCountDown(stack) > 0)
                {
                    return stack;
                }

                /*
                 * Prevents calling air strike if the user is trying to set the frequency of the
                 * remote.
                 */
                if(block == DefenseTechBlocks.blockMachine)
                {
                    return stack;
                }
                else {
                    int airStrikeFreq = this.getFrequency(stack);

                    // Check if it is possible to do an air strike.
                    if(airStrikeFreq > 0)
                    {
                        if(getEnergy(stack) >= YONG_DIAN_LIANG)
                        {
                            Pos3D position = new Pos3D(player.posX, player.posY, player.posZ);

                            boolean doAirStrike = false;
                            int errorCount = 0;

                            for(IBlockFrequency blockFrequency : FrequencyGrid.instance().get(world, position, Settings.MAX_MISSILE_DISTANCE, airStrikeFreq))
                            {
                                if(blockFrequency instanceof TileLauncherPrefab)
                                {
                                    // Do airstrike!
                                    TileLauncherPrefab missileLauncher = (TileLauncherPrefab)blockFrequency;

                                    double yHit = objectMouseOver.blockY;

                                    if(missileLauncher instanceof TileLauncherScreen)
                                    {
                                        if(missileLauncher.getTarget() != null)
                                        {
                                            yHit = missileLauncher.getTarget().yPos;
                                        }
                                        else {
                                            yHit = 0;
                                        }
                                    }

                                    missileLauncher.setTarget(new Pos3D(objectMouseOver.blockX, yHit, objectMouseOver.blockZ));
                                    
                                    ArrayList data = new ArrayList();
                                    
                                    data.add(2);
                                    data.add((int)missileLauncher.getTarget().xPos);
                                    data.add((int)missileLauncher.getTarget().yPos);
                                    data.add((int)missileLauncher.getTarget().zPos);
                                    
                                    Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(missileLauncher), data));

                                    if(missileLauncher.canLaunch())
                                    {
                                        doAirStrike = true;
                                    }
                                    else {
                                        errorCount++;
                                        player.addChatMessage(new ChatComponentText("#" + errorCount + " Missile Launcher Error: " + missileLauncher.getStatus()));
                                    }
                                }
                            }

                            if(doAirStrike && this.getLauncherCountDown(stack) >= 0)
                            {
                            	ArrayList data = new ArrayList();
                            	
                            	data.add(objectMouseOver.blockX);
                            	data.add(objectMouseOver.blockY);
                            	data.add(objectMouseOver.blockZ);
                            	
                            	DefenseTech.netHandler.sendToServer(new ItemMessage(data));
                                player.addChatMessage(new ChatComponentText(Reference.CHAT_DESC + EnumColor.GREY + " " + LangUtils.localize("message.designator.callBlast")));
                            }
                        }
                        else {
                            player.addChatMessage(new ChatComponentText(Reference.CHAT_DESC + EnumColor.GREY + " " + LangUtils.localize("message.designator.nopower")));
                        }
                    }
                    else {
                        player.addChatMessage(new ChatComponentText(Reference.CHAT_DESC + EnumColor.GREY + " " + LangUtils.localize("message.designator.noFreq")));
                    }
                }
            }
        }

        return stack;
    }

    @Override
    public void handlePacket(EntityPlayer player, ByteBuf data)
    {
        ItemStack itemStack = (ItemStack)player.getCurrentEquippedItem();
        Pos3D position = new Pos3D(data.readInt(), data.readInt(), data.readInt());

        ((ItemLaserDesignator)DefenseTechItems.itemLaserDesignator).setLauncherCountDown(itemStack, 119);

        player.worldObj.playSoundEffect(position.xPos, player.worldObj.getHeightValue((int)position.yPos, (int)position.zPos), position.zPos, Reference.PREFIX + "airstrike", 5.0F, (1.0F + (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
        player.worldObj.spawnEntityInWorld(new EntityLightBeam(player.worldObj, position, 5 * 20, 0F, 1F, 0F));
       
        if(itemStack.getItem() instanceof ItemEnergized)
        {
        	ItemEnergized item = (ItemEnergized)itemStack.getItem();
        	
            item.setEnergy(itemStack, item.getEnergy(itemStack) - ItemRadarGun.YONG_DIAN_LIANG);
        }
    }
}
