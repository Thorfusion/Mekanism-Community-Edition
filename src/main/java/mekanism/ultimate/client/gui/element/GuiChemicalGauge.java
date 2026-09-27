package mekanism.ultimate.client.gui.element;

import java.util.function.Supplier;
import mekanism.api.transmitters.TransmissionType;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.FluidType;
import mekanism.common.util.LangUtils;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.client.UltimateChemicalClientUtils;
import mekanism.ultimate.common.UltimateFluids;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Long-safe 1.12 gauge for the Ultimate chemical facade. */
@SideOnly(Side.CLIENT)
public final class GuiChemicalGauge extends GuiGauge<IChemicalStackCE> {

    private final Supplier<IChemicalTankCE> tankSupplier;

    public GuiChemicalGauge(Supplier<IChemicalTankCE> tankSupplier, Type type,
          IGuiWrapper gui, ResourceLocation def, int x, int y) {
        super(type, gui, def, x, y);
        this.tankSupplier = tankSupplier;
    }

    public static GuiChemicalGauge getDummy(Type type, IGuiWrapper gui,
          ResourceLocation def, int x, int y) {
        GuiChemicalGauge gauge = new GuiChemicalGauge(() -> null, type, gui, def, x, y);
        gauge.dummy = true;
        return gauge;
    }

    @Override
    public int getScaledLevel() {
        IChemicalTankCE tank = tankSupplier.get();
        if (tank == null || tank.getStored() <= 0 || tank.getCapacity() <= 0) {
            return 0;
        }
        return Math.max(1, (int) Math.min(height - 2,
              Math.ceil((double) tank.getStored() * (height - 2) / tank.getCapacity())));
    }

    @Override
    public TextureAtlasSprite getIcon() {
        return UltimateFluids.ChemicalRender == null ? null
              : MekanismRenderer.getBaseFluidTexture(UltimateFluids.ChemicalRender, FluidType.STILL);
    }

    @Override
    public String getTooltipText() {
        IChemicalTankCE tank = tankSupplier.get();
        IChemicalStackCE stack = tank == null ? null : tank.getStack();
        return stack == null ? LangUtils.localize("gui.empty")
              : UltimateChemicalClientUtils.getDisplayName(stack.getType()) + ": "
                    + LangUtils.localizeWithFormat("gui.chemical.amount", stack.getAmount());
    }

    @Override
    protected void applyRenderColor() {
        IChemicalTankCE tank = tankSupplier.get();
        IChemicalStackCE stack = tank == null ? null : tank.getStack();
        MekanismRenderer.color(stack == null ? 0xFFFFFFFF
              : UltimateChemicalClientUtils.getColor(stack.getType()));
    }

    @Override
    public TransmissionType getTransmission() {
        // The facade supports more than the legacy GAS transmission type.
        return null;
    }
}
