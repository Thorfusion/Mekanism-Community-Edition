package mekanism.ultimate.common.item;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.EnumColor;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasItem;
import mekanism.common.Mekanism;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.base.ISustainedInventory;
import mekanism.common.config.MekanismConfig;
import mekanism.common.security.ISecurityItem;
import mekanism.common.security.ISecurityTile;
import mekanism.common.security.ISecurityTile.SecurityMode;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.LangUtils;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.IChemicalHandlerCE;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.common.block.BlockChemicalTank;
import mekanism.ultimate.common.capability.UltimateChemicalCapabilities;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.TieredChemicalTank;
import mekanism.ultimate.common.content.chemical.UltimateChemicalRegistry;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import mekanism.ultimate.common.tile.TileEntityChemicalTank;
import mekanism.ultimate.common.tier.ChemicalTankTier;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/** Portable item form of one fixed-tier modern Chemical Tank. */
public final class ItemBlockChemicalTank extends ItemBlock implements IGasItem,
      ISustainedInventory, ISecurityItem {

    private static final String NBT_STORED = "chemicalTank";
    private static final String NBT_DUMPING = "dumping";
    private static final String NBT_ITEMS = "Items";

    private final ChemicalTankTier tier;

    public ItemBlockChemicalTank(BlockChemicalTank block) {
        super(block);
        tier = block.getTier();
        setMaxStackSize(1);
        setCreativeTab(Mekanism.tabMekanism);
    }

    public ChemicalTankTier getTier() {
        return tier;
    }

    @Override
    public boolean placeBlockAt(@Nonnull ItemStack stack, @Nonnull EntityPlayer player,
          World world, @Nonnull BlockPos pos, EnumFacing side, float hitX, float hitY,
          float hitZ, @Nonnull IBlockState state) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, state)) {
            return false;
        }
        if (world.getTileEntity(pos) instanceof TileEntityChemicalTank) {
            TileEntityChemicalTank tank = (TileEntityChemicalTank) world.getTileEntity(pos);
            tank.tier = tier;
            tank.chemicalTank.configure(tier.getStorage(), tier.isCreative());
            tank.chemicalTank.clear();
            IChemicalStackCE stored = getStoredChemical(stack);
            if (stored != null) {
                tank.chemicalTank.insert(stored, Action.EXECUTE);
            }
            tank.dumping = getDumpMode(stack);
            tank.getSecurity().setOwnerUUID(getOwnerUUID(stack));
            if (hasSecurity(stack)) {
                tank.getSecurity().setMode(getSecurity(stack));
            }
            if (getOwnerUUID(stack) == null) {
                tank.getSecurity().setOwnerUUID(player.getUniqueID());
            }
            if (ItemDataUtils.hasData(stack, "sideDataStored")) {
                ((ISideConfiguration) tank).getConfig().read(ItemDataUtils.getDataMap(stack));
                ((ISideConfiguration) tank).getEjector().read(ItemDataUtils.getDataMap(stack));
            }
            tank.setInventory(getInventory(stack));
            tank.markDirty();
            if (!world.isRemote) {
                Mekanism.packetHandler.sendUpdatePacket(tank);
            }
        }
        return true;
    }

    @Nullable
    public IChemicalStackCE getStoredChemical(ItemStack stack) {
        TieredChemicalTank tank = createTank();
        if (ItemDataUtils.hasData(stack, NBT_STORED)) {
            tank.readFromNBT(ItemDataUtils.getCompound(stack, NBT_STORED),
                  UltimateChemicalRegistry.INSTANCE);
        }
        return tank.getStack();
    }

    public long getStored(ItemStack stack) {
        IChemicalStackCE stored = getStoredChemical(stack);
        return stored == null ? 0 : stored.getAmount();
    }

    public void setStoredChemical(ItemStack stack, @Nullable IChemicalStackCE stored) {
        if (stored == null || stored.isEmpty()) {
            ItemDataUtils.removeData(stack, NBT_STORED);
            return;
        }
        TieredChemicalTank tank = createTank();
        tank.insert(stored, Action.EXECUTE);
        NBTTagCompound data = new NBTTagCompound();
        tank.writeToNBT(data);
        ItemDataUtils.setCompound(stack, NBT_STORED, data);
    }

    private TieredChemicalTank createTank() {
        return new TieredChemicalTank(tier.getStorage(), tier.isCreative());
    }

    public TileEntityChemicalTank.GasMode getDumpMode(ItemStack stack) {
        int index = ItemDataUtils.getInt(stack, NBT_DUMPING);
        TileEntityChemicalTank.GasMode[] values = TileEntityChemicalTank.GasMode.values();
        return index >= 0 && index < values.length ? values[index] : TileEntityChemicalTank.GasMode.IDLE;
    }

    public void setDumpMode(ItemStack stack, TileEntityChemicalTank.GasMode mode) {
        ItemDataUtils.setInt(stack, NBT_DUMPING,
              mode == null ? TileEntityChemicalTank.GasMode.IDLE.ordinal() : mode.ordinal());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip,
          ITooltipFlag flag) {
        IChemicalStackCE stored = getStoredChemical(stack);
        if (stored == null) {
            tooltip.add(EnumColor.DARK_RED + LangUtils.localize("gui.empty") + ".");
        } else {
            String name = stored.getType() instanceof MekGasChemicalType
                  ? ((MekGasChemicalType) stored.getType()).getGas().getLocalizedName()
                  : stored.getType().getRegistryName();
            String amount = tier.isCreative() ? LangUtils.localize("gui.infinite")
                  : Long.toString(stored.getAmount());
            tooltip.add(EnumColor.ORANGE + name + ": " + EnumColor.GREY + amount);
        }
        tooltip.add(EnumColor.INDIGO + LangUtils.localize("tooltip.capacity") + ": "
              + EnumColor.GREY + (tier.isCreative() ? LangUtils.localize("gui.infinite")
              : Long.toString(tier.getStorage())));
    }

    @Nullable
    @Override
    public GasStack getGas(ItemStack stack) {
        IChemicalStackCE stored = getStoredChemical(stack);
        if (stored == null || !(stored.getType() instanceof MekGasChemicalType)) {
            return null;
        }
        return new GasStack(((MekGasChemicalType) stored.getType()).getGas(),
              (int) Math.min(Integer.MAX_VALUE, stored.getAmount()));
    }

    @Override
    public void setGas(ItemStack stack, @Nullable GasStack gas) {
        IChemicalStackCE stored = getStoredChemical(stack);
        if (stored != null && !(stored.getType() instanceof MekGasChemicalType)) {
            return;
        }
        setStoredChemical(stack, gas == null || gas.getGas() == null || gas.amount <= 0 ? null
              : new ChemicalStackCE(new MekGasChemicalType(gas.getGas()), gas.amount));
    }

    @Override
    public int getMaxGas(ItemStack stack) {
        return (int) Math.min(Integer.MAX_VALUE, tier.getStorage());
    }

    @Override
    public int getRate(ItemStack stack) {
        return (int) Math.min(Integer.MAX_VALUE, tier.getOutput());
    }

    @Override
    public int addGas(ItemStack stack, GasStack gas) {
        if (gas == null || gas.getGas() == null || gas.amount <= 0) {
            return 0;
        }
        ItemTankView view = new ItemTankView(stack);
        return (int) view.insert(new ChemicalStackCE(new MekGasChemicalType(gas.getGas()), gas.amount),
              Action.EXECUTE);
    }

    @Nullable
    @Override
    public GasStack removeGas(ItemStack stack, int amount) {
        if (amount <= 0 || getGas(stack) == null) {
            return null;
        }
        IChemicalStackCE extracted = new ItemTankView(stack).extract(amount, Action.EXECUTE);
        if (extracted == null || !(extracted.getType() instanceof MekGasChemicalType)) {
            return null;
        }
        return new GasStack(((MekGasChemicalType) extracted.getType()).getGas(),
              (int) extracted.getAmount());
    }

    @Override
    public boolean canReceiveGas(ItemStack stack, Gas type) {
        return type != null && new ItemTankView(stack).insert(
              new ChemicalStackCE(new MekGasChemicalType(type), 1), Action.SIMULATE) > 0;
    }

    @Override
    public boolean canProvideGas(ItemStack stack, @Nullable Gas type) {
        GasStack stored = getGas(stack);
        return stored != null && (type == null || stored.getGas() == type);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        ItemChemicalHandler handler = new ItemChemicalHandler(stack);
        return new ICapabilityProvider() {
            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing side) {
                return capability == UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing side) {
                return capability == UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY
                      ? UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY.cast(handler) : null;
            }
        };
    }

    @Override
    public void setInventory(NBTTagList inventory, Object... data) {
        if (data.length > 0 && data[0] instanceof ItemStack) {
            ItemDataUtils.setList((ItemStack) data[0], NBT_ITEMS,
                  inventory == null ? new NBTTagList() : inventory);
        }
    }

    @Override
    public NBTTagList getInventory(Object... data) {
        return data.length > 0 && data[0] instanceof ItemStack
              ? ItemDataUtils.getList((ItemStack) data[0], NBT_ITEMS) : null;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getStoredChemical(stack) != null;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (tier.isCreative()) {
            return getStoredChemical(stack) == null ? 1D : 0D;
        }
        return 1D - (double) getStored(stack) / tier.getStorage();
    }

    @Override
    public int getRGBDurabilityForDisplay(@Nonnull ItemStack stack) {
        return MathHelper.hsvToRGB(Math.max(0F, (float) (1D - getDurabilityForDisplay(stack))) / 3F, 1F, 1F);
    }

    @Nullable
    @Override
    public UUID getOwnerUUID(ItemStack stack) {
        if (!ItemDataUtils.hasData(stack, "ownerUUID")) {
            return null;
        }
        try {
            return UUID.fromString(ItemDataUtils.getString(stack, "ownerUUID"));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @Override
    public void setOwnerUUID(ItemStack stack, @Nullable UUID owner) {
        if (owner == null) {
            ItemDataUtils.removeData(stack, "ownerUUID");
        } else {
            ItemDataUtils.setString(stack, "ownerUUID", owner.toString());
        }
    }

    @Override
    public SecurityMode getSecurity(ItemStack stack) {
        if (!MekanismConfig.current().general.allowProtection.val()) {
            return SecurityMode.PUBLIC;
        }
        int index = ItemDataUtils.getInt(stack, "security");
        SecurityMode[] values = SecurityMode.values();
        return index >= 0 && index < values.length ? values[index] : SecurityMode.PUBLIC;
    }

    @Override
    public void setSecurity(ItemStack stack, SecurityMode mode) {
        ItemDataUtils.setInt(stack, "security",
              mode == null ? SecurityMode.PUBLIC.ordinal() : mode.ordinal());
    }

    @Override
    public boolean hasSecurity(ItemStack stack) {
        return true;
    }

    @Override
    public boolean hasOwner(ItemStack stack) {
        return true;
    }

    private final class ItemChemicalHandler implements IChemicalHandlerCE {

        private final ItemStack stack;
        private final ItemTankView tank;

        private ItemChemicalHandler(ItemStack stack) {
            this.stack = stack;
            tank = new ItemTankView(stack);
        }

        @Override
        public int getChemicalTankCount(@Nullable EnumFacing side) {
            return 1;
        }

        @Override
        public IChemicalTankCE getChemicalTank(int index, @Nullable EnumFacing side) {
            if (index != 0) {
                throw new IndexOutOfBoundsException("Chemical tank index: " + index);
            }
            return tank;
        }

        @Override
        public boolean canInsertChemical(int index, IChemicalTypeCE type, @Nullable EnumFacing side) {
            return index == 0 && type != null && tank.insert(new ChemicalStackCE(type, 1), Action.SIMULATE) > 0;
        }

        @Override
        public boolean canExtractChemical(int index, IChemicalTypeCE type, @Nullable EnumFacing side) {
            IChemicalStackCE stored = getStoredChemical(stack);
            return index == 0 && type != null && stored != null
                  && TieredChemicalTank.isSameType(stored.getType(), type);
        }
    }

    private final class ItemTankView implements IChemicalTankCE {

        private final ItemStack stack;

        private ItemTankView(ItemStack stack) {
            this.stack = stack;
        }

        private TieredChemicalTank load() {
            TieredChemicalTank tank = createTank();
            if (ItemDataUtils.hasData(stack, NBT_STORED)) {
                tank.readFromNBT(ItemDataUtils.getCompound(stack, NBT_STORED),
                      UltimateChemicalRegistry.INSTANCE);
            }
            return tank;
        }

        private void save(TieredChemicalTank tank) {
            setStoredChemical(stack, tank.getStack());
        }

        @Nullable
        @Override
        public IChemicalStackCE getStack() {
            return load().getStack();
        }

        @Override
        public long getStored() {
            return load().getStored();
        }

        @Override
        public long getCapacity() {
            return tier.getStorage();
        }

        @Override
        public long insert(IChemicalStackCE chemical, Action action) {
            TieredChemicalTank tank = load();
            IChemicalStackCE limited = new ChemicalStackCE(chemical.getType(),
                  Math.min(chemical.getAmount(), tier.getOutput()));
            long accepted = tank.insert(limited, action);
            if (accepted > 0 && action.execute()) {
                save(tank);
            }
            return accepted;
        }

        @Nullable
        @Override
        public IChemicalStackCE extract(long amount, Action action) {
            TieredChemicalTank tank = load();
            IChemicalStackCE extracted = tank.extract(Math.min(amount, tier.getOutput()), action);
            if (extracted != null && action.execute()) {
                save(tank);
            }
            return extracted;
        }
    }
}
