package mekanism.ultimate.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import javax.annotation.Nullable;
import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.api.MekanismConfig.general;
import mekanism.api.Range4D;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasItem;
import mekanism.common.Mekanism;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.base.ISustainedInventory;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.security.ISecurityItem;
import mekanism.common.security.ISecurityTile.SecurityMode;
import mekanism.common.util.LangUtils;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.IChemicalHandlerCE;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.common.block.BlockChemicalTank;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.TieredChemicalTank;
import mekanism.ultimate.common.content.chemical.UltimateChemicalRegistry;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import mekanism.ultimate.common.tile.TileEntityChemicalTank;
import mekanism.ultimate.common.tier.ChemicalTankTier;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;

/** Portable item form of one fixed-tier modern Chemical Tank. */
public final class ItemBlockChemicalTank extends ItemBlock implements IGasItem,
      ISustainedInventory, ISecurityItem
{
    private static final String NBT_STORED = "chemicalTank";
    private static final String NBT_DUMPING = "dumping";
    private static final String NBT_ITEMS = "Items";

    private final BlockChemicalTank tankBlock;
    private final ChemicalTankTier tier;

    public ItemBlockChemicalTank(Block block)
    {
        super(block);
        if (!(block instanceof BlockChemicalTank))
        {
            throw new IllegalArgumentException(
                  "Chemical Tank item requires a Chemical Tank block");
        }
        tankBlock = (BlockChemicalTank)block;
        tier = tankBlock.getTier();
        setMaxStackSize(1);
        setCreativeTab(Mekanism.tabMekanism);
    }

    public ChemicalTankTier getTier()
    {
        return tier;
    }

    @Override
    public IIcon getIconFromDamage(int damage)
    {
        return tankBlock.getIcon(1, 0);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        return LangUtils.localize("tile.ChemicalTank"
              + tier.getBaseTier().getName() + ".name");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List items)
    {
        items.add(new ItemStack(this));
        if (!tier.isCreative() || !general.prefilledGasTanks)
        {
            return;
        }
        for (Gas gas : GasRegistry.getRegisteredGasses())
        {
            if (gas.isVisible())
            {
                ItemStack filled = new ItemStack(this);
                setStoredChemical(filled, new ChemicalStackCE(
                      new MekGasChemicalType(gas), tier.getStorage()));
                items.add(filled);
            }
        }
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player,
          World world, int x, int y, int z, int side, float hitX, float hitY,
          float hitZ, int metadata)
    {
        if (!super.placeBlockAt(stack, player, world, x, y, z, side, hitX,
              hitY, hitZ, metadata))
        {
            return false;
        }
        if (world.getTileEntity(x, y, z) instanceof TileEntityChemicalTank)
        {
            TileEntityChemicalTank tank = (TileEntityChemicalTank)world
                  .getTileEntity(x, y, z);
            tank.setTier(tier);
            tank.chemicalTank.clear();
            IChemicalStackCE stored = getStoredChemical(stack);
            if (stored != null)
            {
                tank.chemicalTank.insert(stored, Action.EXECUTE);
            }
            tank.dumping = getDumpMode(stack);
            tank.getSecurity().setOwner(getOwner(stack));
            tank.getSecurity().setMode(getSecurity(stack));
            if (getOwner(stack) == null && player != null)
            {
                tank.getSecurity().setOwner(player.getCommandSenderName());
            }
            if (stack.stackTagCompound != null
                  && stack.stackTagCompound.getBoolean("sideDataStored"))
            {
                ((ISideConfiguration)tank).getConfig().read(
                      stack.stackTagCompound);
                ((ISideConfiguration)tank).getEjector().read(
                      stack.stackTagCompound);
            }
            tank.setInventory(getInventory(stack));
            tank.markDirty();
            if (!world.isRemote)
            {
                Mekanism.packetHandler.sendToReceivers(
                      new TileEntityMessage(Coord4D.get(tank),
                            tank.getNetworkedData(new java.util.ArrayList())),
                      new Range4D(Coord4D.get(tank)));
            }
        }
        return true;
    }

    @Nullable
    public IChemicalStackCE getStoredChemical(ItemStack stack)
    {
        TieredChemicalTank tank = createTank();
        if (stack != null && stack.stackTagCompound != null
              && stack.stackTagCompound.hasKey(NBT_STORED, NBT.TAG_COMPOUND))
        {
            tank.readFromNBT(stack.stackTagCompound.getCompoundTag(NBT_STORED),
                  UltimateChemicalRegistry.INSTANCE);
        }
        return tank.getStack();
    }

    public long getStored(ItemStack stack)
    {
        IChemicalStackCE stored = getStoredChemical(stack);
        return stored == null ? 0 : stored.getAmount();
    }

    public void setStoredChemical(ItemStack stack,
          @Nullable IChemicalStackCE stored)
    {
        if (stack == null)
        {
            return;
        }
        if (stored == null || stored.isEmpty())
        {
            if (stack.stackTagCompound != null)
            {
                stack.stackTagCompound.removeTag(NBT_STORED);
            }
            return;
        }
        TieredChemicalTank tank = createTank();
        tank.insert(stored, Action.EXECUTE);
        NBTTagCompound data = new NBTTagCompound();
        tank.writeToNBT(data);
        getOrCreateData(stack).setTag(NBT_STORED, data);
    }

    public IChemicalHandlerCE getChemicalHandler(ItemStack stack)
    {
        return new ItemChemicalHandler(stack);
    }

    private TieredChemicalTank createTank()
    {
        return new TieredChemicalTank(tier.getStorage(), tier.isCreative());
    }

    public TileEntityChemicalTank.GasMode getDumpMode(ItemStack stack)
    {
        int index = stack != null && stack.stackTagCompound != null
              ? stack.stackTagCompound.getInteger(NBT_DUMPING) : 0;
        return TileEntityChemicalTank.GasMode.byIndex(index);
    }

    public void setDumpMode(ItemStack stack,
          TileEntityChemicalTank.GasMode mode)
    {
        getOrCreateData(stack).setInteger(NBT_DUMPING,
              (mode == null ? TileEntityChemicalTank.GasMode.IDLE : mode)
                    .ordinal());
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player,
          List tooltip, boolean advanced)
    {
        IChemicalStackCE stored = getStoredChemical(stack);
        if (stored == null)
        {
            tooltip.add(EnumColor.DARK_RED + LangUtils.localize("gui.empty")
                  + ".");
        }
        else
        {
            String name = getChemicalName(stored);
            String amount = tier.isCreative()
                  ? LangUtils.localize("gui.infinite")
                  : Long.toString(stored.getAmount());
            tooltip.add(EnumColor.ORANGE + name + ": " + EnumColor.GREY
                  + amount);
        }
        tooltip.add(EnumColor.INDIGO + LangUtils.localize("tooltip.capacity")
              + ": " + EnumColor.GREY + (tier.isCreative()
                    ? LangUtils.localize("gui.infinite")
                    : Long.toString(tier.getStorage())));
    }

    private static String getChemicalName(IChemicalStackCE stored)
    {
        if (stored.getType() instanceof MekGasChemicalType)
        {
            return ((MekGasChemicalType)stored.getType()).getGas()
                  .getLocalizedName();
        }
        String registryName = stored.getType().getRegistryName();
        int separator = registryName.indexOf(':');
        String path = separator >= 0 ? registryName.substring(separator + 1)
              : registryName;
        String localized = LangUtils.localize("chemical."
              + stored.getType().getKind().name().toLowerCase() + "." + path);
        return localized.equals("chemical."
              + stored.getType().getKind().name().toLowerCase() + "." + path)
                    ? registryName : localized;
    }

    @Nullable
    @Override
    public GasStack getGas(ItemStack stack)
    {
        IChemicalStackCE stored = getStoredChemical(stack);
        if (stored == null
              || !(stored.getType() instanceof MekGasChemicalType))
        {
            return null;
        }
        return new GasStack(((MekGasChemicalType)stored.getType()).getGas(),
              saturatingInt(stored.getAmount()));
    }

    @Override
    public void setGas(ItemStack stack, GasStack gas)
    {
        IChemicalStackCE stored = getStoredChemical(stack);
        if (stored != null
              && !(stored.getType() instanceof MekGasChemicalType))
        {
            return;
        }
        setStoredChemical(stack,
              gas == null || gas.getGas() == null || gas.amount <= 0 ? null
                    : new ChemicalStackCE(
                          new MekGasChemicalType(gas.getGas()), gas.amount));
    }

    @Override
    public int getMaxGas(ItemStack stack)
    {
        return saturatingInt(tier.getStorage());
    }

    @Override
    public int getRate(ItemStack stack)
    {
        return saturatingInt(tier.getOutput());
    }

    @Override
    public int addGas(ItemStack stack, GasStack gas)
    {
        if (gas == null || gas.getGas() == null || gas.amount <= 0)
        {
            return 0;
        }
        return saturatingInt(new ItemTankView(stack).insert(
              new ChemicalStackCE(new MekGasChemicalType(gas.getGas()),
                    gas.amount), Action.EXECUTE));
    }

    @Nullable
    @Override
    public GasStack removeGas(ItemStack stack, int amount)
    {
        if (amount <= 0 || getGas(stack) == null)
        {
            return null;
        }
        IChemicalStackCE extracted = new ItemTankView(stack).extract(amount,
              Action.EXECUTE);
        if (extracted == null
              || !(extracted.getType() instanceof MekGasChemicalType))
        {
            return null;
        }
        return new GasStack(
              ((MekGasChemicalType)extracted.getType()).getGas(),
              saturatingInt(extracted.getAmount()));
    }

    @Override
    public boolean canReceiveGas(ItemStack stack, Gas type)
    {
        return type != null && new ItemTankView(stack).insert(
              new ChemicalStackCE(new MekGasChemicalType(type), 1),
              Action.SIMULATE) > 0;
    }

    @Override
    public boolean canProvideGas(ItemStack stack, Gas type)
    {
        GasStack stored = getGas(stack);
        return stored != null && (type == null || stored.getGas() == type);
    }

    @Override
    public void setInventory(NBTTagList inventory, Object... data)
    {
        if (data.length > 0 && data[0] instanceof ItemStack)
        {
            ItemStack stack = (ItemStack)data[0];
            if (inventory == null || inventory.tagCount() == 0)
            {
                if (stack.stackTagCompound != null)
                {
                    stack.stackTagCompound.removeTag(NBT_ITEMS);
                }
            }
            else
            {
                getOrCreateData(stack).setTag(NBT_ITEMS, inventory.copy());
            }
        }
    }

    @Override
    public NBTTagList getInventory(Object... data)
    {
        if (data.length > 0 && data[0] instanceof ItemStack)
        {
            ItemStack stack = (ItemStack)data[0];
            if (stack.stackTagCompound != null
                  && stack.stackTagCompound.hasKey(NBT_ITEMS, NBT.TAG_LIST))
            {
                return stack.stackTagCompound.getTagList(NBT_ITEMS,
                      NBT.TAG_COMPOUND);
            }
        }
        return null;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return getStoredChemical(stack) != null;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        if (tier.isCreative())
        {
            return getStoredChemical(stack) == null ? 1D : 0D;
        }
        return 1D - (double)getStored(stack) / tier.getStorage();
    }

    @Override
    public String getOwner(ItemStack stack)
    {
        return stack.stackTagCompound != null
              && stack.stackTagCompound.hasKey("owner")
                    ? stack.stackTagCompound.getString("owner") : null;
    }

    @Override
    public void setOwner(ItemStack stack, String owner)
    {
        if (owner == null || owner.isEmpty())
        {
            if (stack.stackTagCompound != null)
            {
                stack.stackTagCompound.removeTag("owner");
            }
        }
        else
        {
            getOrCreateData(stack).setString("owner", owner);
        }
    }

    @Override
    public SecurityMode getSecurity(ItemStack stack)
    {
        if (!general.allowProtection || stack.stackTagCompound == null)
        {
            return SecurityMode.PUBLIC;
        }
        int index = stack.stackTagCompound.getInteger("security");
        SecurityMode[] values = SecurityMode.values();
        return index >= 0 && index < values.length ? values[index]
              : SecurityMode.PUBLIC;
    }

    @Override
    public void setSecurity(ItemStack stack, SecurityMode mode)
    {
        getOrCreateData(stack).setInteger("security",
              (mode == null ? SecurityMode.PUBLIC : mode).ordinal());
    }

    @Override
    public boolean hasSecurity(ItemStack stack)
    {
        return true;
    }

    @Override
    public boolean hasOwner(ItemStack stack)
    {
        return true;
    }

    private static NBTTagCompound getOrCreateData(ItemStack stack)
    {
        if (stack.stackTagCompound == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.stackTagCompound;
    }

    private static int saturatingInt(long value)
    {
        return value >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)value;
    }

    private final class ItemChemicalHandler implements IChemicalHandlerCE
    {
        private final ItemStack stack;
        private final ItemTankView tank;

        private ItemChemicalHandler(ItemStack stack)
        {
            this.stack = stack;
            tank = new ItemTankView(stack);
        }

        @Override
        public int getChemicalTankCount(@Nullable ForgeDirection side)
        {
            return 1;
        }

        @Override
        public IChemicalTankCE getChemicalTank(int index,
              @Nullable ForgeDirection side)
        {
            if (index != 0)
            {
                throw new IndexOutOfBoundsException(
                      "Chemical tank index: " + index);
            }
            return tank;
        }

        @Override
        public boolean canInsertChemical(int index, IChemicalTypeCE type,
              @Nullable ForgeDirection side)
        {
            return index == 0 && type != null && tank.insert(
                  new ChemicalStackCE(type, 1), Action.SIMULATE) > 0;
        }

        @Override
        public boolean canExtractChemical(int index, IChemicalTypeCE type,
              @Nullable ForgeDirection side)
        {
            IChemicalStackCE stored = getStoredChemical(stack);
            return index == 0 && type != null && stored != null
                  && TieredChemicalTank.isSameType(stored.getType(), type);
        }
    }

    private final class ItemTankView implements IChemicalTankCE
    {
        private final ItemStack stack;

        private ItemTankView(ItemStack stack)
        {
            this.stack = stack;
        }

        private TieredChemicalTank load()
        {
            TieredChemicalTank tank = createTank();
            if (stack.stackTagCompound != null
                  && stack.stackTagCompound.hasKey(NBT_STORED,
                        NBT.TAG_COMPOUND))
            {
                tank.readFromNBT(
                      stack.stackTagCompound.getCompoundTag(NBT_STORED),
                      UltimateChemicalRegistry.INSTANCE);
            }
            return tank;
        }

        private void save(TieredChemicalTank tank)
        {
            setStoredChemical(stack, tank.getStack());
        }

        @Nullable
        @Override
        public IChemicalStackCE getStack()
        {
            return load().getStack();
        }

        @Override
        public long getStored()
        {
            return load().getStored();
        }

        @Override
        public long getCapacity()
        {
            return tier.getStorage();
        }

        @Override
        public long insert(IChemicalStackCE chemical, Action action)
        {
            if (chemical == null || action == null)
            {
                return 0;
            }
            TieredChemicalTank tank = load();
            IChemicalStackCE limited = new ChemicalStackCE(
                  chemical.getType(), Math.min(chemical.getAmount(),
                        tier.getOutput()));
            long accepted = tank.insert(limited, action);
            if (accepted > 0 && action.execute())
            {
                save(tank);
            }
            return accepted;
        }

        @Nullable
        @Override
        public IChemicalStackCE extract(long amount, Action action)
        {
            if (action == null || amount <= 0)
            {
                return null;
            }
            TieredChemicalTank tank = load();
            IChemicalStackCE extracted = tank.extract(
                  Math.min(amount, tier.getOutput()), action);
            if (extracted != null && action.execute())
            {
                save(tank);
            }
            return extracted;
        }
    }
}
