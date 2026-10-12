package mekanism.ultimate.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.ultimate.client.model.ModelArmoredFreeRunners;
import mekanism.ultimate.client.model.ModelArmoredFreeRunnersArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/** Built-in item renderer for the three-dimensional Armored Free Runners. */
@SideOnly(Side.CLIENT)
public final class ArmoredFreeRunnersItemRenderer implements IItemRenderer
{
    private final ModelArmoredFreeRunners runners =
          new ModelArmoredFreeRunners();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type,
          ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item,
          Object... data)
    {
        GL11.glPushMatrix();
        if (type == ItemRenderType.EQUIPPED
              || type == ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }
        GL11.glRotatef(180F, 0F, 0F, 1F);
        GL11.glRotatef(90F, 0F, -1F, 0F);
        GL11.glScalef(2F, 2F, 2F);
        GL11.glTranslatef(0.2F, -1.43F, 0.12F);
        Minecraft.getMinecraft().renderEngine.bindTexture(
              ModelArmoredFreeRunnersArmor.TEXTURE);
        runners.render(0.0625F);
        GL11.glPopMatrix();
    }
}
