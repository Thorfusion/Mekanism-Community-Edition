package defense.client.render.item;

import mekanism.common.Tier.BaseTier;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.render.tile.RenderCruiseLauncher;
import defense.client.render.tile.RenderEmpTower;
import defense.client.render.tile.RenderLauncherBase;
import defense.client.render.tile.RenderLauncherFrame;
import defense.client.render.tile.RenderLauncherScreen;
import defense.client.render.tile.RenderRadarStation;
import defense.common.block.BlockMachine.MachineData;
import defense.common.item.ItemBlockMachine;

@SideOnly(Side.CLIENT)
public class RenderItemMachine implements IItemRenderer
{
	private final RenderItem renderItem = (RenderItem)RenderManager.instance.getEntityClassRenderObject(EntityItem.class);

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
		RenderBlocks renderBlocks = (RenderBlocks)data[0];
		int metadata = item.getItemDamage();

		if(type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON)
		{
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		}

		GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (metadata == MachineData.LauncherBase.ordinal())
        {
            BaseTier tier = ((ItemBlockMachine)item.getItem()).getBaseTier(item);

            GL11.glRotatef(180f, 0f, 0f, 1f);
            GL11.glScalef(0.4f, 0.4f, 0.4f);

            if (tier == BaseTier.BASIC)
            {
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderLauncherBase.TEXTURE_FILE_0);
                RenderLauncherBase.modelBase0.render(0.0625F);
                RenderLauncherBase.modelRail0.render(0.0625F);
            }
            else if (tier == BaseTier.ADVANCED)
            {
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderLauncherBase.TEXTURE_FILE_1);

                RenderLauncherBase.modelBase1.render(0.0625F);
                RenderLauncherBase.modelRail1.render(0.0625F);
                GL11.glRotatef(180F, 0F, 180F, 1.0F);
                RenderLauncherBase.modelRail1.render(0.0625F);
            }
            else if (tier == BaseTier.ELITE)
            {
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderLauncherBase.TEXTURE_FILE_2);
                RenderLauncherBase.modelBase2.render(0.0625F);
                RenderLauncherBase.modelRail2.render(0.0625F);
                GL11.glRotatef(180F, 0F, 180F, 1.0F);
                RenderLauncherBase.modelRail2.render(0.0625F);
            }
        }
        else if (metadata == MachineData.LauncherScreen.ordinal())
        {
        	BaseTier tier = ((ItemBlockMachine)item.getItem()).getBaseTier(item);
            GL11.glTranslatef(0f, 0.9f, 0f);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            GL11.glRotatef(180f, 0f, 180f, 1f);

            if (tier == BaseTier.BASIC)
            {
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderLauncherScreen.TEXTURE_FILE_0);
                RenderLauncherScreen.model0.render(0.0625F);
            }
            else if (tier == BaseTier.ADVANCED)
            {
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderLauncherScreen.TEXTURE_FILE_1);
                RenderLauncherScreen.model1.render(0.0625F);
            }
            else if (tier == BaseTier.ELITE)
            {
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderLauncherScreen.TEXTURE_FILE_2);
                RenderLauncherScreen.model2.render(0.0625F);
            }
        }
        else if (metadata == MachineData.LauncherFrame.ordinal())
        {
        	BaseTier tier = ((ItemBlockMachine)item.getItem()).getBaseTier(item);
            GL11.glTranslatef(0f, -0.1f, 0f);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            GL11.glScalef(0.8f, 0.4f, 0.8f);

            FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderLauncherFrame.TEXTURE_FILE);

            RenderLauncherFrame.MODEL.render(0.0625F);
        }
        else if (metadata == MachineData.RadarStation.ordinal())
        {
            GL11.glTranslatef(0f, 1f, 0f);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            GL11.glRotatef(180f, 0, 1, 0);

            FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderRadarStation.TEXTURE_FILE);

            RenderRadarStation.MODEL.render(0.0625f, 0, 1.2f);
        }
        else if (metadata == MachineData.EmpTower.ordinal())
        {
            GL11.glTranslatef(0f, 0.3f, 0f);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            GL11.glScalef(0.6f, 0.6f, 0.6f);

            FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderEmpTower.TEXTURE_FILE);

            RenderEmpTower.MODEL.render(0, 0.0625F);
        }
        else if (metadata == MachineData.CruiseLauncher.ordinal())
        {
            GL11.glTranslatef(0f, 0.4f, 0f);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            GL11.glScalef(0.55f, 0.5f, 0.55f);

            FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderCruiseLauncher.TEXTURE_FILE);

            RenderCruiseLauncher.MODEL0.render(0.0625F);
            RenderCruiseLauncher.MODEL1.render(0.0625F);
        }

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
	}
}
