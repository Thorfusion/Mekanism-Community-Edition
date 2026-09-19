package mekanism.ultimate.common.tile;

import mekanism.api.EnumColor;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.SideData;
import mekanism.common.Tier.FactoryTier;
import mekanism.common.Upgrade;
import mekanism.common.tile.TileEntityFactory;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.TileComponentUpgrade;
import mekanism.common.util.InventoryUtils;
import mekanism.ultimate.common.UltimateConfig;

public class TileEntityUltimateFactory extends TileEntityFactory
{
    public TileEntityUltimateFactory()
    {
        super(FactoryTier.ULTIMATE, "Factory", UltimateConfig.getFactoryMaxEnergy());

        BASE_ENERGY_PER_TICK = UltimateConfig.getFactoryUsage();
        energyPerTick = BASE_ENERGY_PER_TICK;

        int[] inputSlots = new int[FactoryTier.ULTIMATE.processes];
        int[] outputSlots = new int[FactoryTier.ULTIMATE.processes];
        for (int i = 0; i < FactoryTier.ULTIMATE.processes; i++)
        {
            inputSlots[i] = getInputSlot(i);
            outputSlots[i] = getOutputSlot(i);
        }

        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.ENERGY, TransmissionType.GAS);
        configComponent.addOutput(TransmissionType.ITEM, new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Input", EnumColor.DARK_RED, inputSlots));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Output", EnumColor.DARK_BLUE, outputSlots));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Energy", EnumColor.DARK_GREEN, new int[] {1}));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Extra", EnumColor.PURPLE, new int[] {4}));
        configComponent.setConfig(TransmissionType.ITEM, new byte[] {4, 0, 0, 3, 1, 2});

        configComponent.addOutput(TransmissionType.GAS, new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.GAS, new SideData("Gas", EnumColor.DARK_RED, new int[] {0}));
        configComponent.fillConfig(TransmissionType.GAS, 1);
        configComponent.setCanEject(TransmissionType.GAS, false);
        configComponent.setInputConfig(TransmissionType.ENERGY);

        upgradeComponent = new TileComponentUpgrade(this, 0);
        upgradeComponent.setSupported(Upgrade.MUFFLING);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(TransmissionType.ITEM, configComponent.getOutputs(TransmissionType.ITEM).get(2));
    }
}
