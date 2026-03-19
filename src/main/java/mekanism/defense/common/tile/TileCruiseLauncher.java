package defense.common.tile;

import io.netty.buffer.ByteBuf;
import mekanism.api.Pos3D;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.LangUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import defense.api.ExplosionEvent.ExplosivePreDetonationEvent;
import defense.api.ExplosiveType;
import defense.api.ILauncherContainer;
import defense.api.ILauncherController;
import defense.api.IMissile;
import defense.common.DefenseTech;
import defense.common.base.IBlockActivate;
import defense.common.entity.EntityMissile;
import defense.common.explosion.Explosion;
import defense.common.explosive.ExplosiveRegistry;
import defense.common.item.ItemMissile;

public class TileCruiseLauncher extends TileLauncherPrefab implements IBlockActivate, IInventory, ILauncherContainer
{
    // The missile that this launcher is holding
    public IMissile daoDan = null;

    public float rotationYaw = 0;

    public float rotationPitch = 0;

    public TileCruiseLauncher()
    {
        super("CruiseLauncher", 10000000);
        this.targetPos = new Pos3D();
        inventory = new ItemStack[2];
    }

    /** Gets the display status of the missile launcher
     * 
     * @return The string to be displayed */
    @Override
    public String getStatus()
    {
        String color = "\u00a74";
        String status = LangUtils.localize("gui.misc.idle");

        if (getEnergy() < getMaxEnergy())
        {
            status = LangUtils.localize("gui.launcherCruise.statusNoPower");
        }
        else if (this.daoDan == null)
        {
            status = LangUtils.localize("gui.launcherCruise.statusEmpty");
        }
        else if (this.targetPos == null)
        {
            status = LangUtils.localize("gui.launcherCruise.statusInvalid");
        }
        else
        {
            color = "\u00a72";
            status = LangUtils.localize("gui.launcherCruise.statusReady");
        }

        return color + status;
    }
    
    @Override
    public void handlePacketData(ByteBuf dataStream)
    {
    	if(!worldObj.isRemote)
    	{
    		int packetType = dataStream.readInt();
    		
	    	if(packetType == 1)
	        {
	            setFrequency(dataStream.readInt());
	        }
	    	else if(packetType == 2)
	        {
	            targetPos = new Pos3D(dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
	        }
	    	
	    	return;
    	}
    	
    	super.handlePacketData(dataStream);
    }

    /** Returns the name of the inventory. */
    @Override
    public String getInventoryName()
    {
        return LangUtils.localize("gui.launcherCruise.name");
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        ChargeUtils.discharge(1, this);

        // Rotate the yaw
        if (this.getYawFromTarget() - this.rotationYaw != 0)
        {
            this.rotationYaw += (this.getYawFromTarget() - this.rotationYaw) * 0.1;
        }
        if (this.getPitchFromTarget() - this.rotationPitch != 0)
        {
            this.rotationPitch += (this.getPitchFromTarget() - this.rotationPitch) * 0.1;
        }

        if (!this.worldObj.isRemote)
        {
            this.setMissile();

            if (this.ticker % 40 == 0 && this.worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
            {
                this.launch();
            }
        }
    }

    @Override
    public void placeMissile(ItemStack itemStack)
    {
        this.inventory[0] = itemStack;
    }

    public void setMissile()
    {
        if (!this.worldObj.isRemote)
        {
            if (this.inventory[0] != null)
            {
                if (this.inventory[0].getItem() instanceof ItemMissile)
                {
                    int haoMa = this.inventory[0].getItemDamage();
                    
                    if (ExplosiveRegistry.get(haoMa) instanceof Explosion)
                    {
                        Explosion missile = (Explosion) ExplosiveRegistry.get(haoMa);

                        ExplosivePreDetonationEvent evt = new ExplosivePreDetonationEvent(this.worldObj, this.xCoord, this.yCoord, this.zCoord, ExplosiveType.AIR, missile);
                        MinecraftForge.EVENT_BUS.post(evt);

                        if (!evt.isCanceled())
                        {
                            if (this.daoDan == null)
                            {

                                if (missile.isCruise() && missile.getTier() <= 3)
                                {
                                    Pos3D startingPosition = new Pos3D((this.xCoord + 0.5f), (this.yCoord + 1f), (this.zCoord + 0.5f));
                                    this.daoDan = new EntityMissile(this.worldObj, startingPosition, new Pos3D(this), haoMa);
                                    this.worldObj.spawnEntityInWorld((Entity) this.daoDan);
                                    return;
                                }
                            }

                            if (this.daoDan != null)
                            {
                                if (this.daoDan.getExplosiveType().getID() == haoMa)
                                {
                                    return;
                                }
                            }
                        }
                    }
                }
            }

            if (this.daoDan != null)
            {
                ((Entity) this.daoDan).setDead();
            }

            this.daoDan = null;
        }
    }

    private float getPitchFromTarget()
    {
        double distance = Math.sqrt((this.targetPos.xPos - this.xCoord) * (this.targetPos.xPos - this.xCoord) + (this.targetPos.zPos - this.zCoord) * (this.targetPos.zPos - this.zCoord));
        return (float) Math.toDegrees(Math.atan((this.targetPos.yPos - (this.yCoord + 0.5F)) / distance));
    }

    private float getYawFromTarget()
    {
        double xDifference = this.targetPos.xPos - (this.xCoord + 0.5F);
        double yDifference = this.targetPos.zPos - (this.zCoord + 0.5F);
        return (float) Math.toDegrees(Math.atan2(yDifference, xDifference));
    }

    @Override
    public boolean canLaunch()
    {
        if (this.daoDan != null && this.inventory[0] != null)
        {
            Explosion missile = (Explosion) ExplosiveRegistry.get(this.inventory[0].getItemDamage());

            if (missile != null && missile.getID() == daoDan.getExplosiveType().getID() && missile.isCruise() && missile.getTier() <= 3)
            {
                if (getEnergy() == getMaxEnergy())
                {
                    if (!this.isTooClose(this.targetPos))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /** Launches the missile
     * 
     * @param targetVector - The target in which the missile will land in */
    @Override
    public void launch()
    {
        if (this.canLaunch())
        {
            this.decrStackSize(0, 1);
            this.setEnergy(0);
            this.daoDan.launch(this.targetPos);
            this.daoDan = null;
        }
    }

    // Is the target too close?
    public boolean isTooClose(Pos3D target)
    {
        return new Pos3D(this.xCoord, 0, this.zCoord).distance(new Pos3D(target.xPos, 0, target.zPos)) < 8;
    }
    
    @Override
    public boolean canSetFacing(int facing)
    {
    	return facing != 0 && facing != 1;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    @Override
    public boolean onActivated(EntityPlayer player)
    {
        if (player.inventory.getCurrentItem() != null)
        {
            if (player.inventory.getCurrentItem().getItem() instanceof ItemMissile)
            {
                if (this.getStackInSlot(0) == null)
                {

                    this.setInventorySlotContents(0, player.inventory.getCurrentItem());
                    if (!player.capabilities.isCreativeMode)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    return true;
                }
                else
                {
                    ItemStack player_held = player.inventory.getCurrentItem();
                    if (!player.capabilities.isCreativeMode)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, this.getStackInSlot(0));
                    this.setInventorySlotContents(0, player_held);
                    return true;
                }
            }
        }
        else if (this.getStackInSlot(0) != null)
        {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, this.getStackInSlot(0));
            this.setInventorySlotContents(0, null);
            return true;
        }

        player.openGui(DefenseTech.INSTANCE, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        return true;
    }

    @Override
    public LauncherType getLauncherType()
    {
        return LauncherType.CRUISE;
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemStack)
    {
        if (itemStack != null)
        {
            if (itemStack.getItem() instanceof ItemMissile && this.getStackInSlot(slotID) == null)
            {
                if (ExplosiveRegistry.get(itemStack.getItemDamage()) instanceof Explosion)
                {
                    Explosion missile = (Explosion) ExplosiveRegistry.get(itemStack.getItemDamage());

                    if (missile.isCruise() && missile.getTier() <= 3)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void setContainingMissile(IMissile missile)
    {
        this.daoDan = missile;
    }

    @Override
    public ILauncherController getController()
    {
        return this;
    }

    @Override
    public IMissile getMissile()
    {
        return this.daoDan;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB.getBoundingBox(xCoord - 1, yCoord, zCoord - 1, xCoord + 1, yCoord + 1, zCoord + 1);
    }
}
