package mekanism.nuclear.common.tile;

import java.util.Collections;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.IConfigurable;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.GasTransmission;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.IGasTankInfoProvider;
import mekanism.api.gas.ITubeConnection;
import mekanism.common.base.IActiveState;
import mekanism.common.tile.TileEntityBasicBlock;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.nuclear.common.NuclearChemicals;
import mekanism.nuclear.common.config.NuclearRadiationConfig;
import mekanism.nuclear.common.radiation.RadiationManager;
import mekanism.nuclear.common.radiation.RadiationPosition;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.IChemicalHandlerCE;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.LongChemicalTank;
import mekanism.ultimate.common.integration.mekanism.LongBackedMekGasTank;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/** Long-backed legacy implementation of the stable Radioactive Waste Barrel. */
public class TileEntityRadioactiveWasteBarrel extends TileEntityBasicBlock
      implements IGasHandler, IGasTankInfoProvider, ITubeConnection,
      IChemicalHandlerCE, IActiveState, IConfigurable
{
    private static final String NBT_CHEMICAL = "WasteBarrelChemical";
    private static final String NBT_ACTIVE = "IsActive";
    private static final String NBT_PROCESS_TICKS = "ProcessTicks";

    private final WasteBarrelChemicalTank chemicalTank =
          new WasteBarrelChemicalTank(NuclearRadiationConfig.getWasteBarrelCapacity());
    public final LongBackedMekGasTank gasTank = new LongBackedMekGasTank(
          chemicalTank, TileEntityRadioactiveWasteBarrel::type);

    private boolean active;
    private int processTicks;
    private int currentRedstoneLevel;
    private long lastProcessTick = Long.MIN_VALUE;

    @Override
    public void onUpdate()
    {
        if (worldObj.isRemote)
        {
            return;
        }
        long gameTime = worldObj.getTotalWorldTime();
        if (gameTime == lastProcessTick)
        {
            return;
        }
        lastProcessTick = gameTime;

        boolean changed = processDecayTick();
        if (active)
        {
            changed |= emitDownward();
        }
        int redstoneLevel = getRedstoneLevel();
        if (redstoneLevel != currentRedstoneLevel)
        {
            currentRedstoneLevel = redstoneLevel;
            worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
        }
        if (changed)
        {
            markDirty();
            MekanismUtils.saveChunk(this);
        }
    }

    boolean processDecayTick()
    {
        IChemicalStackCE stored = chemicalTank.getStack();
        long decayAmount = NuclearRadiationConfig.getWasteBarrelDecayAmount();
        if (decayAmount <= 0 || stored == null
              || NuclearChemicals.isWasteBarrelDecayBlacklisted(
                    stored.getType().getRegistryName()))
        {
            return false;
        }
        if (++processTicks < NuclearRadiationConfig.getWasteBarrelProcessTicks())
        {
            return false;
        }
        processTicks = 0;
        return chemicalTank.extract(decayAmount, Action.EXECUTE) != null;
    }

    private boolean emitDownward()
    {
        IChemicalStackCE stored = chemicalTank.getStack();
        Gas gas = toGas(stored);
        if (gas == null || stored.getAmount() <= 0)
        {
            return false;
        }
        int amount = (int)Math.min(Integer.MAX_VALUE, stored.getAmount());
        TileEntity below = worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
        if (below instanceof TileEntityRadioactiveWasteBarrel)
        {
            amount = (int)Math.min(amount,
                  ((TileEntityRadioactiveWasteBarrel)below).getLocalNeeded());
        }
        if (amount <= 0)
        {
            return false;
        }
        int emitted = GasTransmission.emit(new GasStack(gas, amount), this,
              Collections.singleton(ForgeDirection.DOWN));
        if (emitted > 0)
        {
            chemicalTank.extract(emitted, Action.EXECUTE);
            return true;
        }
        return false;
    }

    private long getLocalNeeded()
    {
        return chemicalTank.getCapacity() - chemicalTank.getStored();
    }

    @Override
    public int receiveGas(ForgeDirection side, GasStack stack, boolean doTransfer)
    {
        if (stack == null || stack.getGas() == null || stack.amount <= 0
              || !canReceiveGas(side, stack.getGas()))
        {
            return 0;
        }
        return gasTank.receive(stack, doTransfer);
    }

    @Deprecated
    @Override
    public int receiveGas(ForgeDirection side, GasStack stack)
    {
        return receiveGas(side, stack, true);
    }

    @Nullable
    @Override
    public GasStack drawGas(ForgeDirection side, int amount, boolean doTransfer)
    {
        if (amount <= 0 || !canDrawGas(side, null))
        {
            return null;
        }
        return gasTank.draw(amount, doTransfer);
    }

    @Deprecated
    @Nullable
    @Override
    public GasStack drawGas(ForgeDirection side, int amount)
    {
        return drawGas(side, amount, true);
    }

    @Override
    public boolean canReceiveGas(ForgeDirection side, Gas gas)
    {
        return isVertical(side) && gas != null
              && NuclearChemicals.isRadioactive(gas.getName())
              && gasTank.canReceiveType(gas)
              && chemicalTank.insert(new ChemicalStackCE(type(gas), 1),
                    Action.SIMULATE) > 0;
    }

    @Override
    public boolean canDrawGas(ForgeDirection side, Gas gas)
    {
        return isVertical(side) && gasTank.canDraw(gas);
    }

    @Nonnull
    @Override
    public GasTankInfo[] getTankInfo()
    {
        return new GasTankInfo[] {gasTank};
    }

    @Override
    public boolean canTubeConnect(ForgeDirection side)
    {
        return isVertical(side);
    }

    @Override
    public int getChemicalTankCount(@Nullable ForgeDirection side)
    {
        return isVertical(side) ? 1 : 0;
    }

    @Override
    public IChemicalTankCE getChemicalTank(int tank, @Nullable ForgeDirection side)
    {
        if (tank != 0 || !isVertical(side))
        {
            throw new IndexOutOfBoundsException("Chemical tank index: " + tank);
        }
        return chemicalTank;
    }

    @Override
    public boolean canInsertChemical(int tank, IChemicalTypeCE type,
          @Nullable ForgeDirection side)
    {
        return tank == 0 && isVertical(side) && type instanceof MekGasChemicalType
              && canReceiveGas(side, ((MekGasChemicalType)type).getGas());
    }

    @Override
    public boolean canExtractChemical(int tank, IChemicalTypeCE type,
          @Nullable ForgeDirection side)
    {
        return tank == 0 && isVertical(side) && type instanceof MekGasChemicalType
              && canDrawGas(side, ((MekGasChemicalType)type).getGas());
    }

    public long getStored()
    {
        return chemicalTank.getStored();
    }

    public long getCapacity()
    {
        return chemicalTank.getCapacity();
    }

    @Nullable
    public Gas getStoredGas()
    {
        return toGas(chemicalTank.getStack());
    }

    /** Converts all contained radioactive chemical into a persisted source. */
    public void releaseContentsToRadiation()
    {
        IChemicalStackCE stored = chemicalTank.getStack();
        Gas gas = toGas(stored);
        if (worldObj != null && gas != null && stored.getAmount() > 0)
        {
            RadiationManager.INSTANCE.release(worldObj,
                  new RadiationPosition(xCoord, yCoord, zCoord), gas,
                  stored.getAmount());
            chemicalTank.clear();
            markDirty();
        }
    }

    private void contentsChanged()
    {
        if (worldObj != null && !worldObj.isRemote)
        {
            markDirty();
            MekanismUtils.saveChunk(this);
        }
    }

    private static boolean isVertical(@Nullable ForgeDirection side)
    {
        return side == null || side == ForgeDirection.UNKNOWN
              || side == ForgeDirection.UP || side == ForgeDirection.DOWN;
    }

    @Nullable
    private static Gas toGas(@Nullable IChemicalStackCE stack)
    {
        return stack != null && stack.getType() instanceof MekGasChemicalType
              ? ((MekGasChemicalType)stack.getType()).getGas() : null;
    }

    private static MekGasChemicalType type(Gas gas)
    {
        return new MekGasChemicalType(gas,
              NuclearChemicals.isRadioactive(gas.getName()));
    }

    @Override
    public boolean getActive()
    {
        return active;
    }

    @Override
    public void setActive(boolean active)
    {
        if (this.active != active)
        {
            this.active = active;
            markDirty();
            if (worldObj != null && !worldObj.isRemote)
            {
                MekanismUtils.saveChunk(this);
            }
        }
    }

    @Override
    public boolean renderUpdate()
    {
        return false;
    }

    @Override
    public boolean lightUpdate()
    {
        return false;
    }

    @Override
    public boolean onSneakRightClick(EntityPlayer player, int side)
    {
        setActive(!getActive());
        if (worldObj != null)
        {
            worldObj.playSoundEffect(xCoord, yCoord, zCoord,
                  "random.click", 0.3F, 1);
        }
        return true;
    }

    @Override
    public boolean onRightClick(EntityPlayer player, int side)
    {
        return false;
    }

    @Override
    public boolean canSetFacing(int side)
    {
        return false;
    }

    public int getRedstoneLevel()
    {
        if (getStored() <= 0)
        {
            return 0;
        }
        return 1 + (int)Math.floor(14D * getStored() / getCapacity());
    }

    public String getName()
    {
        return LangUtils.localize("tile.RadioactiveWasteBarrel.name");
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        active = data.getBoolean(NBT_ACTIVE);
        processTicks = Math.max(0, data.getInteger(NBT_PROCESS_TICKS));
        if (data.hasKey(NBT_CHEMICAL))
        {
            chemicalTank.readFromNBT(data.getCompoundTag(NBT_CHEMICAL),
                  (kind, name) ->
                  {
                      Gas gas = GasRegistry.getGas(name);
                      return gas == null || !NuclearChemicals.isRadioactive(name)
                            ? null : type(gas);
                  });
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        data.setBoolean(NBT_ACTIVE, active);
        data.setInteger(NBT_PROCESS_TICKS, processTicks);
        NBTTagCompound chemical = new NBTTagCompound();
        chemicalTank.writeToNBT(chemical);
        data.setTag(NBT_CHEMICAL, chemical);
    }

    private final class WasteBarrelChemicalTank extends LongChemicalTank
    {
        private WasteBarrelChemicalTank(long capacity)
        {
            super(capacity);
        }

        @Override
        public long insert(IChemicalStackCE stack, Action action)
        {
            if (stack == null || !(stack.getType() instanceof MekGasChemicalType)
                  || !stack.getType().isRadioactive()
                  || !NuclearChemicals.isRadioactive(
                        stack.getType().getRegistryName()))
            {
                return 0;
            }
            long accepted = super.insert(stack, action);
            long remaining = stack.getAmount() - accepted;
            if (remaining > 0 && worldObj != null)
            {
                TileEntity above = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord);
                if (above instanceof TileEntityRadioactiveWasteBarrel)
                {
                    accepted += ((TileEntityRadioactiveWasteBarrel)above)
                          .chemicalTank.insert(stack.copyWithAmount(remaining), action);
                }
            }
            if (accepted > 0 && action.execute())
            {
                contentsChanged();
            }
            return accepted;
        }

        @Nullable
        @Override
        public ChemicalStackCE extract(long amount, Action action)
        {
            ChemicalStackCE extracted = super.extract(amount, action);
            if (extracted != null && action.execute())
            {
                contentsChanged();
            }
            return extracted;
        }
    }
}
