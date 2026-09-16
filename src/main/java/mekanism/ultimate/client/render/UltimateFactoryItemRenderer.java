package mekanism.ultimate.client.render;

import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class UltimateFactoryItemRenderer implements IItemRenderer
{
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        GL11.glPushMatrix();
        if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }
        GL11.glRotatef(-90F, 0.0F, 1.0F, 0.0F);
        MekanismRenderer.renderCustomItem((RenderBlocks)data[0], item);
        GL11.glPopMatrix();
    }
}
