package mekanism.client.render.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.api.Coord4D;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.DisplayInteger;
import mekanism.client.render.MekanismRenderer.Model3D;
import mekanism.common.content.tank.TankUpdateProtocol;
import mekanism.common.tile.TileEntityThermalEvaporationController;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class RenderThermalEvaporationController extends TileEntitySpecialRenderer
{
	// Keep display-list generation bounded to avoid large one-time render stalls.
	private static final int FLUID_RENDER_STAGE_DIVISOR = 1000;
	private static final int MAX_FLUID_RENDER_STAGES = 256;
	private static Map<SalinationRenderData, HashMap<Fluid, DisplayInteger[]>> cachedCenterFluids = new HashMap<SalinationRenderData, HashMap<Fluid, DisplayInteger[]>>();

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick)
	{
		renderAModelAt((TileEntityThermalEvaporationController)tileEntity, x, y, z, partialTick);
	}

	public void renderAModelAt(TileEntityThermalEvaporationController tileEntity, double x, double y, double z, float partialTick)
	{
		if(tileEntity.structured && tileEntity.inputTank.getFluid() != null)
		{
			SalinationRenderData data = new SalinationRenderData();

			data.height = tileEntity.height-2;
			data.side = ForgeDirection.getOrientation(tileEntity.facing);

			bindTexture(MekanismRenderer.getBlocksTexture());
			
			if(data.height >= 1 && tileEntity.inputTank.getCapacity() > 0)
			{
				Coord4D renderLoc = tileEntity.getRenderLocation();

				push();

				GL11.glTranslated(getX(renderLoc.xCoord), getY(renderLoc.yCoord), getZ(renderLoc.zCoord));

				MekanismRenderer.glowOn(tileEntity.inputTank.getFluid().getFluid().getLuminosity());

				int stages = getStages(data.height);
				int displayIndex = Math.min(stages - 1, (int)(((float) tileEntity.inputTank.getFluidAmount() / tileEntity.inputTank.getCapacity()) * (stages - 1)));
				DisplayInteger displayList = getListAndRender(data, tileEntity.inputTank.getFluid().getFluid(), displayIndex, stages);

				if(displayList != null)
				{
					displayList.render();
				}


				MekanismRenderer.glowOff();

				pop();
			}
		}
	}

	private void pop()
	{
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private void push()
	{
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	@SuppressWarnings("incomplete-switch")
	private DisplayInteger getListAndRender(SalinationRenderData data, Fluid fluid, int stage, int stages)
	{
		if(fluid == null || fluid.getIcon() == null)
		{
			return null;
		}

		HashMap<Fluid, DisplayInteger[]> fluidCache = cachedCenterFluids.get(data);

		if(fluidCache == null)
		{
			fluidCache = new HashMap<Fluid, DisplayInteger[]>();
			cachedCenterFluids.put(data, fluidCache);
		}

		DisplayInteger[] displays = fluidCache.get(fluid);

		if(displays == null || displays.length != stages)
		{
			displays = new DisplayInteger[stages];
			fluidCache.put(fluid, displays);
		}

		int clampedStage = Math.max(0, Math.min(stages - 1, stage));
		DisplayInteger cachedDisplay = displays[clampedStage];

		if(cachedDisplay != null)
		{
			return cachedDisplay;
		}

		Model3D toReturn = new Model3D();
		toReturn.baseBlock = Blocks.water;
		toReturn.setTexture(fluid.getIcon());

		switch(data.side)
		{
			case NORTH:
				toReturn.minX = 0 + .01;
				toReturn.minY = 0 + .01;
				toReturn.minZ = 0 + .01;

				toReturn.maxX = 2 - .01;
				toReturn.maxY = ((float)clampedStage/(float)stages)*data.height - .01;
				toReturn.maxZ = 2 - .01;
				break;
			case SOUTH:
				toReturn.minX = -1 + .01;
				toReturn.minY = 0 + .01;
				toReturn.minZ = -1 + .01;

				toReturn.maxX = 1 - .01;
				toReturn.maxY = ((float)clampedStage/(float)stages)*data.height - .01;
				toReturn.maxZ = 1 - .01;
				break;
			case WEST:
				toReturn.minX = 0 + .01;
				toReturn.minY = 0 + .01;
				toReturn.minZ = -1 + .01;

				toReturn.maxX = 2 - .01;
				toReturn.maxY = ((float)clampedStage/(float)stages)*data.height - .01;
				toReturn.maxZ = 1 - .01;
				break;
			case EAST:
				toReturn.minX = -1 + .01;
				toReturn.minY = 0 + .01;
				toReturn.minZ = 0 + .01;

				toReturn.maxX = 1 - .01;
				toReturn.maxY = ((float)clampedStage/(float)stages)*data.height - .01;
				toReturn.maxZ = 2 - .01;
				break;
		}

		DisplayInteger display = DisplayInteger.createAndStart();
		MekanismRenderer.colorFluid(fluid);
		MekanismRenderer.renderObject(toReturn);
		display.endList();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		displays[clampedStage] = display;
		return display;
	}

	private int getStages(int height)
	{
		int stages = height * Math.max(1, TankUpdateProtocol.FLUID_PER_TANK / FLUID_RENDER_STAGE_DIVISOR);
		return Math.max(2, Math.min(MAX_FLUID_RENDER_STAGES, stages));
	}

	private double getX(int x)
	{
		return x - TileEntityRendererDispatcher.staticPlayerX;
	}

	private double getY(int y)
	{
		return y - TileEntityRendererDispatcher.staticPlayerY;
	}

	private double getZ(int z)
	{
		return z - TileEntityRendererDispatcher.staticPlayerZ;
	}

	public static class SalinationRenderData
	{
		public int height;
		public ForgeDirection side;

		@Override
		public int hashCode()
		{
			int code = 1;
			code = 31 * code + height;
			code = 31 * code + (side == null ? 0 : side.ordinal());
			return code;
		}

		@Override
		public boolean equals(Object data)
		{
			return data instanceof SalinationRenderData && ((SalinationRenderData)data).height == height &&
					((SalinationRenderData)data).side == side;
		}
	}

	public static void resetDisplayInts()
	{
		cachedCenterFluids.clear();
	}
}
