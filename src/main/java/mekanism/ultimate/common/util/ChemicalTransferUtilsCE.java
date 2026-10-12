package mekanism.ultimate.common.util;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nullable;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalHandlerCE;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.item.ItemBlockChemicalTank;
import mekanism.common.content.tank.DynamicTankChemicalHooks;
import mekanism.common.tile.TileEntityDynamicValve;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/** Transactional item and adjacent-tile transfers for facade chemicals. */
public final class ChemicalTransferUtilsCE
{
    private ChemicalTransferUtilsCE()
    {
    }

    public static long drainItem(ItemStack stack, IChemicalTankCE target,
          long limit, ChemicalKind requiredKind)
    {
        IChemicalHandlerCE source = itemHandler(stack);
        if (source == null || target == null || requiredKind == null
              || limit <= 0)
        {
            return 0;
        }
        long moved = 0;
        for (int i = 0; i < source.getChemicalTankCount(null)
              && moved < limit; i++)
        {
            IChemicalTankCE sourceTank = source.getChemicalTank(i, null);
            IChemicalStackCE stored = sourceTank.getStack();
            if (stored == null || stored.isEmpty()
                  || stored.getType().getKind() != requiredKind
                  || !source.canExtractChemical(i, stored.getType(), null))
            {
                continue;
            }
            long offered = Math.min(limit - moved, stored.getAmount());
            long accepted = target.insert(
                  new ChemicalStackCE(stored.getType(), offered),
                  Action.SIMULATE);
            if (accepted <= 0)
            {
                continue;
            }
            IChemicalStackCE extracted = sourceTank.extract(accepted,
                  Action.SIMULATE);
            if (extracted == null || extracted.getAmount() != accepted)
            {
                continue;
            }
            long inserted = target.insert(extracted, Action.EXECUTE);
            if (inserted > 0)
            {
                sourceTank.extract(inserted, Action.EXECUTE);
                moved += inserted;
            }
        }
        return moved;
    }

    public static long fillItem(ItemStack stack, IChemicalTankCE source,
          long limit)
    {
        IChemicalHandlerCE target = itemHandler(stack);
        if (target == null || source == null || limit <= 0)
        {
            return 0;
        }
        long moved = 0;
        for (int i = 0; i < target.getChemicalTankCount(null)
              && moved < limit; i++)
        {
            IChemicalStackCE stored = source.getStack();
            if (stored == null || stored.isEmpty())
            {
                break;
            }
            if (!target.canInsertChemical(i, stored.getType(), null))
            {
                continue;
            }
            IChemicalTankCE targetTank = target.getChemicalTank(i, null);
            long offered = Math.min(limit - moved, stored.getAmount());
            long accepted = targetTank.insert(
                  new ChemicalStackCE(stored.getType(), offered),
                  Action.SIMULATE);
            IChemicalStackCE extracted = source.extract(accepted,
                  Action.SIMULATE);
            if (accepted <= 0 || extracted == null
                  || extracted.getAmount() != accepted)
            {
                continue;
            }
            long inserted = targetTank.insert(extracted, Action.EXECUTE);
            if (inserted > 0)
            {
                source.extract(inserted, Action.EXECUTE);
                moved += inserted;
            }
        }
        return moved;
    }

    public static long emit(TileEntity sourceTile, IChemicalTankCE source,
          long limit)
    {
        return emit(sourceTile, source, limit,
              Arrays.asList(ForgeDirection.VALID_DIRECTIONS));
    }

    public static long emit(TileEntity sourceTile, IChemicalTankCE source,
          long limit, Collection<ForgeDirection> sides)
    {
        if (sourceTile == null || sourceTile.getWorldObj() == null
              || source == null || sides == null || limit <= 0)
        {
            return 0;
        }
        long moved = 0;
        for (ForgeDirection side : sides)
        {
            IChemicalStackCE stored = source.getStack();
            if (stored == null || stored.isEmpty() || moved >= limit
                  || side == null || side == ForgeDirection.UNKNOWN)
            {
                break;
            }
            TileEntity targetTile = sourceTile.getWorldObj().getTileEntity(
                  sourceTile.xCoord + side.offsetX,
                  sourceTile.yCoord + side.offsetY,
                  sourceTile.zCoord + side.offsetZ);
            IChemicalHandlerCE target = tileHandler(targetTile);
            if (target == null)
            {
                continue;
            }
            ForgeDirection targetSide = side.getOpposite();
            for (int tank = 0;
                  tank < target.getChemicalTankCount(targetSide)
                        && moved < limit; tank++)
            {
                stored = source.getStack();
                if (stored == null || !target.canInsertChemical(tank,
                      stored.getType(), targetSide))
                {
                    continue;
                }
                IChemicalTankCE targetTank = target.getChemicalTank(tank,
                      targetSide);
                long offered = Math.min(limit - moved, source.getStored());
                long accepted = targetTank.insert(
                      new ChemicalStackCE(stored.getType(), offered),
                      Action.SIMULATE);
                IChemicalStackCE extracted = source.extract(accepted,
                      Action.SIMULATE);
                if (accepted <= 0 || extracted == null
                      || extracted.getAmount() != accepted)
                {
                    continue;
                }
                long inserted = targetTank.insert(extracted,
                      Action.EXECUTE);
                if (inserted > 0)
                {
                    source.extract(inserted, Action.EXECUTE);
                    moved += inserted;
                }
            }
        }
        return moved;
    }

    @Nullable
    public static IChemicalHandlerCE itemHandler(ItemStack stack)
    {
        return stack != null
              && stack.getItem() instanceof ItemBlockChemicalTank
                    ? ((ItemBlockChemicalTank)stack.getItem())
                          .getChemicalHandler(stack) : null;
    }

    @Nullable
    public static IChemicalHandlerCE tileHandler(TileEntity tile)
    {
        if (tile instanceof IChemicalHandlerCE)
        {
            return (IChemicalHandlerCE)tile;
        }
        if (tile instanceof TileEntityDynamicValve)
        {
            Object handler = DynamicTankChemicalHooks.getChemicalHandler(
                  (TileEntityDynamicValve)tile);
            return handler instanceof IChemicalHandlerCE
                  ? (IChemicalHandlerCE)handler : null;
        }
        return null;
    }
}
