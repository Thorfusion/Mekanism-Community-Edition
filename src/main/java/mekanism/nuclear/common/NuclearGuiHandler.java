package mekanism.nuclear.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class NuclearGuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return MekanismNuclear.proxy.getServerGui(id, player, world, new BlockPos(x, y, z));
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return MekanismNuclear.proxy.getClientGui(id, player, world, new BlockPos(x, y, z));
    }
}
