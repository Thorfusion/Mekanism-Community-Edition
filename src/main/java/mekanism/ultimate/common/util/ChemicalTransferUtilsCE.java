package mekanism.ultimate.common.util;

import javax.annotation.Nullable;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalHandlerCE;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.common.capability.UltimateChemicalCapabilities;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/** Transactional item and adjacent-tile transfers for facade chemicals. */
public final class ChemicalTransferUtilsCE {

    private ChemicalTransferUtilsCE() {
    }

    public static long drainItem(ItemStack stack, IChemicalTankCE target, long limit,
          ChemicalKind requiredKind) {
        IChemicalHandlerCE source = itemHandler(stack);
        if (source == null || limit <= 0) {
            return 0;
        }
        long moved = 0;
        for (int i = 0; i < source.getChemicalTankCount(null) && moved < limit; i++) {
            IChemicalTankCE sourceTank = source.getChemicalTank(i, null);
            IChemicalStackCE stored = sourceTank.getStack();
            if (stored == null || stored.isEmpty() || stored.getType().getKind() != requiredKind) {
                continue;
            }
            long offered = Math.min(limit - moved, stored.getAmount());
            long accepted = target.insert(new ChemicalStackCE(stored.getType(), offered), Action.SIMULATE);
            if (accepted <= 0) {
                continue;
            }
            IChemicalStackCE extracted = sourceTank.extract(accepted, Action.SIMULATE);
            if (extracted == null || extracted.getAmount() != accepted) {
                continue;
            }
            accepted = target.insert(extracted, Action.EXECUTE);
            if (accepted > 0) {
                sourceTank.extract(accepted, Action.EXECUTE);
                moved += accepted;
            }
        }
        return moved;
    }

    public static long fillItem(ItemStack stack, IChemicalTankCE source, long limit) {
        IChemicalHandlerCE target = itemHandler(stack);
        IChemicalStackCE stored = source.getStack();
        if (target == null || stored == null || limit <= 0) {
            return 0;
        }
        long moved = 0;
        for (int i = 0; i < target.getChemicalTankCount(null) && moved < limit; i++) {
            IChemicalTankCE targetTank = target.getChemicalTank(i, null);
            long offered = Math.min(limit - moved, stored.getAmount() - moved);
            if (offered <= 0) {
                break;
            }
            long accepted = targetTank.insert(new ChemicalStackCE(stored.getType(), offered), Action.SIMULATE);
            IChemicalStackCE extracted = source.extract(accepted, Action.SIMULATE);
            if (accepted <= 0 || extracted == null || extracted.getAmount() != accepted) {
                continue;
            }
            accepted = targetTank.insert(extracted, Action.EXECUTE);
            if (accepted > 0) {
                source.extract(accepted, Action.EXECUTE);
                moved += accepted;
            }
        }
        return moved;
    }

    public static long emit(TileEntity sourceTile, IChemicalTankCE source, long limit) {
        IChemicalStackCE stored = source.getStack();
        if (sourceTile.getWorld() == null || stored == null || limit <= 0) {
            return 0;
        }
        long moved = 0;
        for (EnumFacing side : EnumFacing.VALUES) {
            TileEntity targetTile = sourceTile.getWorld().getTileEntity(sourceTile.getPos().offset(side));
            if (targetTile == null || !targetTile.hasCapability(
                  UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite())) {
                continue;
            }
            IChemicalHandlerCE handler = targetTile.getCapability(
                  UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite());
            if (handler == null) {
                continue;
            }
            for (int tank = 0; tank < handler.getChemicalTankCount(side.getOpposite()) && moved < limit; tank++) {
                if (!handler.canInsertChemical(tank, stored.getType(), side.getOpposite())) {
                    continue;
                }
                IChemicalTankCE target = handler.getChemicalTank(tank, side.getOpposite());
                long offered = Math.min(limit - moved, source.getStored());
                long accepted = target.insert(new ChemicalStackCE(stored.getType(), offered), Action.SIMULATE);
                IChemicalStackCE extracted = source.extract(accepted, Action.SIMULATE);
                if (accepted <= 0 || extracted == null || extracted.getAmount() != accepted) {
                    continue;
                }
                accepted = target.insert(extracted, Action.EXECUTE);
                if (accepted > 0) {
                    source.extract(accepted, Action.EXECUTE);
                    moved += accepted;
                }
            }
        }
        return moved;
    }

    @Nullable
    private static IChemicalHandlerCE itemHandler(ItemStack stack) {
        return stack == null || stack.isEmpty() || !stack.hasCapability(
              UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY, null) ? null
              : stack.getCapability(UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY, null);
    }
}
