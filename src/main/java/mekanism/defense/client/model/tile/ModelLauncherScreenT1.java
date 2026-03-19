package defense.client.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.tmt.ModelRendererTurbo;

@SideOnly(Side.CLIENT)
public class ModelLauncherScreenT1 extends ModelBase
{
    // fields
    ModelRendererTurbo[] bodyModel;

    public ModelLauncherScreenT1()
    {
		int textureX = 512;
		int textureY = 512;

		bodyModel = new ModelRendererTurbo[17];
		bodyModel[0] = new ModelRendererTurbo(this, 120, 0, textureX, textureY); // Box 0
		bodyModel[1] = new ModelRendererTurbo(this, 120, 26, textureX, textureY); // Box 1
		bodyModel[2] = new ModelRendererTurbo(this, 120, 26, textureX, textureY); // Box 2
		bodyModel[3] = new ModelRendererTurbo(this, 120, 33, textureX, textureY); // Box 3
		bodyModel[4] = new ModelRendererTurbo(this, 120, 40, textureX, textureY); // Box 4
		bodyModel[5] = new ModelRendererTurbo(this, 120, 55, textureX, textureY); // Box 5
		bodyModel[6] = new ModelRendererTurbo(this, 120, 71, textureX, textureY); // Box 6
		bodyModel[7] = new ModelRendererTurbo(this, 120, 80, textureX, textureY); // Box 7
		bodyModel[8] = new ModelRendererTurbo(this, 120, 80, textureX, textureY); // Box 8
		bodyModel[9] = new ModelRendererTurbo(this, 120, 80, textureX, textureY); // Box 9
		bodyModel[10] = new ModelRendererTurbo(this, 120, 80, textureX, textureY); // Box 10
		bodyModel[11] = new ModelRendererTurbo(this, 120, 92, textureX, textureY); // Box 11
		bodyModel[12] = new ModelRendererTurbo(this, 120, 118, textureX, textureY); // Box 12
		bodyModel[13] = new ModelRendererTurbo(this, 120, 131, textureX, textureY); // Box 13
		bodyModel[14] = new ModelRendererTurbo(this, 120, 138, textureX, textureY); // Box 14
		bodyModel[15] = new ModelRendererTurbo(this, 120, 154, textureX, textureY); // Box 15
		bodyModel[16] = new ModelRendererTurbo(this, 120, 164, textureX, textureY); // Box 16

		bodyModel[0].addBox(0F, 0F, 0F, 16, 2, 12, 0F); // Box 0
		bodyModel[0].setRotationPoint(-8F, 22F, -6F);

		bodyModel[1].addShapeBox(0F, 0F, 0F, 16, 2, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Box 1
		bodyModel[1].setRotationPoint(-8F, 22F, 6F);

		bodyModel[2].addShapeBox(0F, 0F, 0F, 16, 2, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 2
		bodyModel[2].setRotationPoint(-8F, 22F, -8F);

		bodyModel[3].addShapeBox(0F, 0F, 0F, 14, 2, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Box 3
		bodyModel[3].setRotationPoint(-7F, 20F, -6F);

		bodyModel[4].addShapeBox(0F, 0F, 0F, 2, 8, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 4
		bodyModel[4].setRotationPoint(5F, 11F, -6F);

		bodyModel[5].addShapeBox(0F, 0F, 0F, 2, 8, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 5
		bodyModel[5].setRotationPoint(-7F, 11F, -6F);

		bodyModel[6].addShapeBox(0F, 0F, 0F, 14, 2, 3, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 6
		bodyModel[6].setRotationPoint(-7F, 8F, -6F);

		bodyModel[7].addShapeBox(0F, 0F, 0F, 2, 1, 3, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 7
		bodyModel[7].setRotationPoint(-7F, 10F, -6F);

		bodyModel[8].addShapeBox(0F, 0F, 0F, 2, 1, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F); // Box 8
		bodyModel[8].setRotationPoint(-7F, 19F, -6F);

		bodyModel[9].addShapeBox(0F, 0F, 0F, 2, 1, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, 0F, 0F); // Box 9
		bodyModel[9].setRotationPoint(5F, 19F, -6F);

		bodyModel[10].addShapeBox(0F, 0F, 0F, 2, 1, 3, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 10
		bodyModel[10].setRotationPoint(5F, 10F, -6F);

		bodyModel[11].addShapeBox(0F, 0F, 0F, 12, 12, 10, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, -5F, 0F, -2F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Box 11
		bodyModel[11].setRotationPoint(-6F, 9F, -3F);

		bodyModel[12].addShapeBox(0F, 0F, 0F, 12, 6, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 12
		bodyModel[12].setRotationPoint(-6F, 16F, 0F);

		bodyModel[13].addShapeBox(0F, 0F, 0F, 12, 1, 3, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 13
		bodyModel[13].setRotationPoint(-6F, 15F, 0F);

		bodyModel[14].addShapeBox(0F, 0F, 0F, 6, 5, 7, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, -4F, 0F, -2F, -4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Box 14
		bodyModel[14].setRotationPoint(-3F, 9F, -1F);

		bodyModel[15].addShapeBox(0F, 0F, 0F, 6, 5, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 15
		bodyModel[15].setRotationPoint(-3F, 9F, -3F);

		bodyModel[16].addShapeBox(0F, 0F, 0F, 100, 100, 1, 0F, -90F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -90F, 0F, 0F, -90F, -90F, 0F, 0F, -90F, 0F, 0F, -90F, 0F, -90F, -90F, 0F); // Box 16
		bodyModel[16].setRotationPoint(-95F, 10F, -5.8F);

    }

    public void render(float f5)
    {
    	render(bodyModel, f5);
    }
    
	private void render(ModelRendererTurbo[] models, float f)
	{
		for(ModelRendererTurbo model : models)
			if(model != null)
				model.render(f);
	}

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}
