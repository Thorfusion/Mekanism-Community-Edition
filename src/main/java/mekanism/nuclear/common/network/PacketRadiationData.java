package mekanism.nuclear.common.network;

import io.netty.buffer.ByteBuf;
import mekanism.nuclear.common.MekanismNuclear;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Periodic server-to-client environmental rate and accumulated dose snapshot. */
public final class PacketRadiationData implements IMessage {

    private double environmental;
    private double dose;

    public PacketRadiationData() {
    }

    public PacketRadiationData(double environmental, double dose) {
        this.environmental = environmental;
        this.dose = dose;
    }

    double getEnvironmental() {
        return environmental;
    }

    double getDose() {
        return dose;
    }

    @Override
    public void fromBytes(ByteBuf data) {
        environmental = data.readDouble();
        dose = data.readDouble();
    }

    @Override
    public void toBytes(ByteBuf data) {
        data.writeDouble(environmental);
        data.writeDouble(dose);
    }

    public static final class Handler implements IMessageHandler<PacketRadiationData, IMessage> {

        @Override
        public IMessage onMessage(PacketRadiationData message, MessageContext context) {
            MekanismNuclear.proxy.handleRadiationData(message.environmental, message.dose);
            return null;
        }
    }
}
