package mekanism.nuclear.common.tile;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.api.IConfigCardAccess;
import mekanism.api.Range4D;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.GasTransmission;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.IGasItem;
import mekanism.api.gas.IGasTankInfoProvider;
import mekanism.api.gas.ITubeConnection;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.Mekanism;
import mekanism.common.SideData;
import mekanism.common.Upgrade;
import mekanism.common.Upgrade.IUpgradeInfoHandler;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.base.ITankManager;
import mekanism.common.base.IUpgradeItem;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.TileEntityNoisyElectricBlock;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.tile.component.TileComponentUpgrade;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.nuclear.common.NuclearChemicals;
import mekanism.nuclear.common.recipe.NuclearRecipeRegistry;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.IChemicalHandlerCE;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.LongChemicalTank;
import mekanism.ultimate.common.integration.mekanism.LongBackedMekGasTank;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import mekanism.ultimate.common.recipe.type.ItemChemicalToItemRecipeCE;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

/** Stable Antiprotonic Nucleosynthesizer adapted to native 1.7 APIs. */
public class TileEntityAntiprotonicNucleosynthesizer
      extends TileEntityNoisyElectricBlock
      implements IGasHandler, IGasTankInfoProvider, ITubeConnection,
      IChemicalHandlerCE, IRedstoneControl, ISideConfiguration,
      IConfigCardAccess, ITankManager, IUpgradeTile,
      IUpgradeInfoHandler, ISecurityTile
{
    public static final int MAX_GAS = 10000;
    public static final int BASE_TICKS_REQUIRED = 400;
    public static final double BASE_ENERGY_USAGE = 100000D;
    public static final double BASE_MAX_ENERGY = 1000000000D;

    private static final int GAS_SLOT = 0;
    private static final int INPUT_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;
    private static final int ENERGY_SLOT = 3;
    private static final int UPGRADE_SLOT = 4;

    private final LongChemicalTank chemicalTank =
          new LongChemicalTank(MAX_GAS);
    public final LongBackedMekGasTank inputTank =
          new LongBackedMekGasTank(chemicalTank,
                NuclearRecipeRegistry::type);

    public final TileComponentConfig configComponent;
    public final TileComponentEjector ejectorComponent;
    public final TileComponentUpgrade upgradeComponent =
          new TileComponentUpgrade(this, UPGRADE_SLOT);
    public final TileComponentSecurity securityComponent =
          new TileComponentSecurity(this);

    public RedstoneControl controlType = RedstoneControl.DISABLED;
    public boolean isActive;
    public boolean clientActive;
    public int updateDelay;
    public int operatingTicks;
    public int ticksRequired = BASE_TICKS_REQUIRED;
    public double energyPerTick = BASE_ENERGY_USAGE;
    public double clientEnergyUsed;
    public double prevEnergy;

    @Nullable
    private ResourceLocation activeRecipe;
    private int currentRedstoneLevel;

    public TileEntityAntiprotonicNucleosynthesizer()
    {
        super("machine.cheminfuser", "AntiprotonicNucleosynthesizer",
              BASE_MAX_ENERGY);
        inventory = new ItemStack[5];
        // Stable Mekanism intentionally does not allow this machine to be sped up.
        upgradeComponent.setSupported(Upgrade.SPEED, false);
        upgradeComponent.setSupported(Upgrade.MUFFLING);

        configComponent = new TileComponentConfig(this,
              TransmissionType.ITEM, TransmissionType.ENERGY,
              TransmissionType.GAS);
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("Gas", EnumColor.DARK_AQUA,
                    new int[] {GAS_SLOT}));
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("Input", EnumColor.DARK_GREEN,
                    new int[] {INPUT_SLOT}));
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("Output", EnumColor.DARK_RED,
                    new int[] {OUTPUT_SLOT}));
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("Energy", EnumColor.DARK_BLUE,
                    new int[] {ENERGY_SLOT}));
        configComponent.setConfig(TransmissionType.ITEM,
              new byte[] {3, 2, 0, 0, 1, 4});

        configComponent.addOutput(TransmissionType.GAS,
              new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.GAS,
              new SideData("Input", EnumColor.DARK_GREEN,
                    new int[] {0}));
        configComponent.fillConfig(TransmissionType.GAS, 1);
        configComponent.setCanEject(TransmissionType.GAS, false);
        configComponent.setInputConfig(TransmissionType.ENERGY);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(TransmissionType.ITEM,
              configComponent.getOutputs(TransmissionType.ITEM).get(3));
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (worldObj.isRemote)
        {
            if (updateDelay > 0 && --updateDelay == 0
                  && clientActive != isActive)
            {
                isActive = clientActive;
                MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
            }
            return;
        }

        if (updateDelay > 0 && --updateDelay == 0
              && clientActive != isActive)
        {
            sendUpdatePacket();
        }

        ChargeUtils.discharge(ENERGY_SLOT, this);
        transferInputContainer();
        ItemChemicalToItemRecipeCE recipe = getRecipe();
        updateRecipeIdentity(recipe);

        boolean canOperate = canOperate(recipe);
        boolean running = canOperate && getEnergy() >= energyPerTick
              && MekanismUtils.canFunction(this);
        if (running)
        {
            setEnergy(getEnergy() - energyPerTick);
            clientEnergyUsed = energyPerTick;
            setActive(true);
            if (++operatingTicks >= ticksRequired)
            {
                operate(recipe);
                operatingTicks = 0;
            }
        }
        else
        {
            clientEnergyUsed = 0;
            setActive(false);
            if (!canOperate)
            {
                operatingTicks = 0;
            }
        }
        prevEnergy = getEnergy();

        int redstoneLevel = getRedstoneLevel();
        if (redstoneLevel != currentRedstoneLevel)
        {
            currentRedstoneLevel = redstoneLevel;
            worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
        }
    }

    private void transferInputContainer()
    {
        ItemStack stack = inventory[GAS_SLOT];
        if (stack == null || !(stack.getItem() instanceof IGasItem))
        {
            return;
        }
        GasStack contained = ((IGasItem)stack.getItem()).getGas(stack);
        if (contained == null
              || contained.getGas() != NuclearChemicals.Antimatter)
        {
            return;
        }
        GasStack removed = GasTransmission.removeGas(stack,
              NuclearChemicals.Antimatter, inputTank.getNeeded());
        if (removed == null)
        {
            return;
        }
        int accepted = receiveGas(ForgeDirection.UNKNOWN, removed, true);
        if (accepted < removed.amount)
        {
            GasTransmission.addGas(stack, removed.copy().withAmount(
                  removed.amount - accepted));
        }
    }

    @Nullable
    public ItemChemicalToItemRecipeCE getRecipe()
    {
        return NuclearRecipeRegistry.findNucleosynthesizing(
              inventory[INPUT_SLOT], chemicalTank.getStack());
    }

    private void updateRecipeIdentity(
          @Nullable ItemChemicalToItemRecipeCE recipe)
    {
        ResourceLocation next = recipe == null ? null : recipe.getId();
        if (next == null ? activeRecipe != null : !next.equals(activeRecipe))
        {
            operatingTicks = 0;
            activeRecipe = next;
        }
        ticksRequired = recipe == null
              ? BASE_TICKS_REQUIRED : recipe.getDuration();
    }

    public boolean canOperate(@Nullable ItemChemicalToItemRecipeCE recipe)
    {
        if (recipe == null
              || !recipe.getItemInput().hasRequiredAmount(
                    inventory[INPUT_SLOT])
              || !recipe.getChemicalInput().hasRequiredAmount(
                    chemicalTank.getStack()))
        {
            return false;
        }
        ItemStack result = recipe.getOutput();
        ItemStack stored = inventory[OUTPUT_SLOT];
        if (stored == null)
        {
            return true;
        }
        return stored.isItemEqual(result)
              && ItemStack.areItemStackTagsEqual(stored, result)
              && stored.stackSize + result.stackSize <= Math.min(
                    stored.getMaxStackSize(), getInventoryStackLimit());
    }

    void operate(ItemChemicalToItemRecipeCE recipe)
    {
        long chemicalAmount = recipe.getChemicalInput().getAmount();
        IChemicalStackCE extracted = chemicalTank.extract(chemicalAmount,
              Action.EXECUTE);
        if (extracted == null || extracted.getAmount() != chemicalAmount)
        {
            throw new IllegalStateException(
                  "Nucleosynthesizer chemical commit diverged from validation");
        }

        ItemStack input = inventory[INPUT_SLOT];
        input.stackSize -= recipe.getItemInput().getAmount();
        if (input.stackSize <= 0)
        {
            inventory[INPUT_SLOT] = null;
        }
        ItemStack result = recipe.getOutput();
        if (inventory[OUTPUT_SLOT] == null)
        {
            inventory[OUTPUT_SLOT] = result;
        }
        else
        {
            inventory[OUTPUT_SLOT].stackSize += result.stackSize;
        }
        markDirty();
    }

    @Override
    public int receiveGas(ForgeDirection side, GasStack stack,
          boolean doTransfer)
    {
        if (stack == null || !canReceiveGas(side, stack.getGas()))
        {
            return 0;
        }
        int accepted = (int)chemicalTank.insert(new ChemicalStackCE(
              NuclearRecipeRegistry.type(stack.getGas()), stack.amount),
              doTransfer ? Action.EXECUTE : Action.SIMULATE);
        if (doTransfer && accepted > 0)
        {
            markDirty();
        }
        return accepted;
    }

    @Deprecated
    @Override
    public int receiveGas(ForgeDirection side, GasStack stack)
    {
        return receiveGas(side, stack, true);
    }

    @Nullable
    @Override
    public GasStack drawGas(ForgeDirection side, int amount,
          boolean doTransfer)
    {
        return null;
    }

    @Deprecated
    @Nullable
    @Override
    public GasStack drawGas(ForgeDirection side, int amount)
    {
        return null;
    }

    @Override
    public boolean canReceiveGas(ForgeDirection side, Gas gas)
    {
        return gas == NuclearChemicals.Antimatter && isInputSide(side)
              && chemicalTank.getStored() < chemicalTank.getCapacity();
    }

    @Override
    public boolean canDrawGas(ForgeDirection side, Gas gas)
    {
        return false;
    }

    private boolean isInputSide(@Nullable ForgeDirection side)
    {
        return side == null || side == ForgeDirection.UNKNOWN
              || configComponent.getOutput(TransmissionType.GAS,
                    side.ordinal(), facing).hasSlot(0);
    }

    @Override
    public boolean canTubeConnect(ForgeDirection side)
    {
        return isInputSide(side);
    }

    @Nonnull
    @Override
    public GasTankInfo[] getTankInfo()
    {
        return new GasTankInfo[] {inputTank};
    }

    @Override
    public int getChemicalTankCount(@Nullable ForgeDirection side)
    {
        return 1;
    }

    @Override
    public IChemicalTankCE getChemicalTank(int tank,
          @Nullable ForgeDirection side)
    {
        if (tank == 0)
        {
            return chemicalTank;
        }
        throw new IndexOutOfBoundsException("Chemical tank index: " + tank);
    }

    @Override
    public boolean canInsertChemical(int tank, IChemicalTypeCE type,
          @Nullable ForgeDirection side)
    {
        return tank == 0 && type instanceof MekGasChemicalType
              && canReceiveGas(side, ((MekGasChemicalType)type).getGas());
    }

    @Override
    public boolean canExtractChemical(int tank, IChemicalTypeCE type,
          @Nullable ForgeDirection side)
    {
        return false;
    }

    @Override
    public EnumSet<ForgeDirection> getConsumingSides()
    {
        return configComponent.getSidesForData(TransmissionType.ENERGY,
              facing, 1);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return side >= 0 && side < 6
              ? configComponent.getOutput(TransmissionType.ITEM, side,
                    facing).availableSlots : InventoryUtils.EMPTY;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (stack == null)
        {
            return false;
        }
        if (slot == GAS_SLOT && stack.getItem() instanceof IGasItem)
        {
            GasStack gas = ((IGasItem)stack.getItem()).getGas(stack);
            return gas != null
                  && gas.getGas() == NuclearChemicals.Antimatter;
        }
        if (slot == INPUT_SLOT)
        {
            return NuclearRecipeRegistry.containsNucleosynthesizingInput(
                  stack);
        }
        if (slot == ENERGY_SLOT)
        {
            return ChargeUtils.canBeDischarged(stack);
        }
        if (slot == UPGRADE_SLOT
              && stack.getItem() instanceof IUpgradeItem)
        {
            return upgradeComponent.supports(((IUpgradeItem)stack.getItem())
                  .getUpgradeType(stack));
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        if (stack == null)
        {
            return false;
        }
        if (slot == GAS_SLOT && stack.getItem() instanceof IGasItem)
        {
            return ((IGasItem)stack.getItem()).getGas(stack) == null;
        }
        if (slot == OUTPUT_SLOT)
        {
            return true;
        }
        return slot == ENERGY_SLOT
              && ChargeUtils.canBeOutputted(stack, false);
    }

    @Override
    public boolean canSetFacing(int side)
    {
        return side != 0 && side != 1;
    }

    @Override
    public RedstoneControl getControlType()
    {
        return controlType;
    }

    @Override
    public void setControlType(RedstoneControl type)
    {
        controlType = type;
        MekanismUtils.saveChunk(this);
    }

    @Override
    public boolean canPulse()
    {
        return false;
    }

    @Override
    public boolean getActive()
    {
        return isActive;
    }

    @Override
    public void setActive(boolean active)
    {
        isActive = active;
        if (clientActive != active && updateDelay == 0)
        {
            sendUpdatePacket();
            updateDelay = 10;
            clientActive = active;
        }
    }

    private void sendUpdatePacket()
    {
        Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(
              Coord4D.get(this), getNetworkedData(new ArrayList())),
              new Range4D(Coord4D.get(this)));
    }

    @Override
    public boolean renderUpdate()
    {
        return false;
    }

    @Override
    public boolean lightUpdate()
    {
        return true;
    }

    @Override
    public void recalculateUpgradables(Upgrade upgrade)
    {
        super.recalculateUpgradables(upgrade);
        if (upgrade == Upgrade.ENERGY)
        {
            energyPerTick = MekanismUtils.getEnergyPerTick(this,
                  BASE_ENERGY_USAGE);
            maxEnergy = MekanismUtils.getMaxEnergy(this, BASE_MAX_ENERGY);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        isActive = data.getBoolean("isActive");
        operatingTicks = data.getInteger("operatingTicks");
        String recipe = data.getString("activeRecipe");
        activeRecipe = recipe.isEmpty() ? null
              : new ResourceLocation(recipe);
        int control = data.getInteger("controlType");
        controlType = control >= 0
              && control < RedstoneControl.values().length
              ? RedstoneControl.values()[control]
              : RedstoneControl.DISABLED;
        chemicalTank.readFromNBT(data.getCompoundTag("AntimatterTank"),
              (kind, name) ->
              {
                  Gas gas = GasRegistry.getGas(name);
                  return gas == null ? null
                        : NuclearRecipeRegistry.type(gas);
              });
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        data.setBoolean("isActive", isActive);
        data.setInteger("operatingTicks", operatingTicks);
        data.setInteger("controlType", controlType.ordinal());
        if (activeRecipe != null)
        {
            data.setString("activeRecipe", activeRecipe.toString());
        }
        NBTTagCompound chemicals = new NBTTagCompound();
        chemicalTank.writeToNBT(chemicals);
        data.setTag("AntimatterTank", chemicals);
    }

    @Override
    public void handlePacketData(ByteBuf data)
    {
        super.handlePacketData(data);
        if (worldObj.isRemote)
        {
            isActive = data.readBoolean();
            controlType = RedstoneControl.values()[data.readInt()];
            operatingTicks = data.readInt();
            ticksRequired = data.readInt();
            clientEnergyUsed = data.readDouble();
            readTankSnapshot(data);
            MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
        }
    }

    @Override
    public ArrayList getNetworkedData(ArrayList data)
    {
        super.getNetworkedData(data);
        data.add(isActive);
        data.add(controlType.ordinal());
        data.add(operatingTicks);
        data.add(ticksRequired);
        data.add(clientEnergyUsed);
        GasStack gas = inputTank.getGas();
        data.add(gas != null);
        if (gas != null)
        {
            data.add(gas.getGas().getID());
            data.add(gas.amount);
        }
        return data;
    }

    private void readTankSnapshot(ByteBuf data)
    {
        if (data.readBoolean())
        {
            inputTank.setGas(new GasStack(GasRegistry.getGas(data.readInt()),
                  data.readInt()));
        }
        else
        {
            inputTank.setGas(null);
        }
    }

    public double getScaledProgress()
    {
        return ticksRequired <= 0 ? 0
              : (double)operatingTicks / ticksRequired;
    }

    public int getRedstoneLevel()
    {
        return Container.calcRedstoneFromInventory(this);
    }

    @Override
    public TileComponentConfig getConfig()
    {
        return configComponent;
    }

    @Override
    public int getOrientation()
    {
        return facing;
    }

    @Override
    public TileComponentEjector getEjector()
    {
        return ejectorComponent;
    }

    @Override
    public TileComponentUpgrade getComponent()
    {
        return upgradeComponent;
    }

    @Override
    public TileComponentSecurity getSecurity()
    {
        return securityComponent;
    }

    @Override
    public Object[] getTanks()
    {
        return new Object[] {inputTank};
    }

    @Override
    public List<String> getInfo(Upgrade upgrade)
    {
        return upgrade.getMultScaledInfo(this);
    }

    @Override
    public String getInventoryName()
    {
        return LangUtils.localize("tile.AntiprotonicNucleosynthesizer.name");
    }
}
