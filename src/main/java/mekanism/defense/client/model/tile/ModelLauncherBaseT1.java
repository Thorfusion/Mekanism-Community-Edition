package defense.client.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.tmt.ModelRendererTurbo;

@SideOnly(Side.CLIENT)
public class ModelLauncherBaseT1 extends ModelBase
{
    // fields
    ModelRendererTurbo[] bodyModel;

    public ModelLauncherBaseT1()
    {
		int textureX = 512;
		int textureY = 512;
		
		bodyModel = new ModelRendererTurbo[13];
		bodyModel[0] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 0
		bodyModel[1] = new ModelRendererTurbo(this, 0, 44, textureX, textureY); // Box 1
		bodyModel[2] = new ModelRendererTurbo(this, 0, 39, textureX, textureY); // Box 2
		bodyModel[3] = new ModelRendererTurbo(this, 0, 39, textureX, textureY); // Box 3
		bodyModel[4] = new ModelRendererTurbo(this, 0, 44, textureX, textureY); // Box 4
		bodyModel[5] = new ModelRendererTurbo(this, 0, 52, textureX, textureY); // Box 5
		bodyModel[6] = new ModelRendererTurbo(this, 0, 52, textureX, textureY); // Box 6
		bodyModel[7] = new ModelRendererTurbo(this, 0, 61, textureX, textureY); // Box 7
		bodyModel[8] = new ModelRendererTurbo(this, 0, 71, textureX, textureY); // Box 8
		bodyModel[9] = new ModelRendererTurbo(this, 0, 71, textureX, textureY); // Box 9
		bodyModel[10] = new ModelRendererTurbo(this, 0, 75, textureX, textureY); // Box 10
		bodyModel[11] = new ModelRendererTurbo(this, 0, 89, textureX, textureY); // Box 11
		bodyModel[12] = new ModelRendererTurbo(this, 0, 89, textureX, textureY); // Box 12

		bodyModel[0].addBox(0F, 0F, 0F, 10, 2, 8, 0F); // Box 0
		bodyModel[0].setRotationPoint(-5F, 22F, -4F);

		bodyModel[1].addShapeBox(0F, 0F, 0F, 4, 2, 3, 0F, -1F, -1F, 0F, -1F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 1
		bodyModel[1].setRotationPoint(-2F, 22F, -8F);

		bodyModel[2].addShapeBox(0F, 0F, 0F, 10, 2, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 2
		bodyModel[2].setRotationPoint(-5F, 22F, -5F);

		bodyModel[3].addShapeBox(0F, 0F, 0F, 10, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 3
		bodyModel[3].setRotationPoint(-5F, 22F, 4F);

		bodyModel[4].addShapeBox(0F, 0F, 0F, 4, 2, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -1F, 0F, -1F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 4
		bodyModel[4].setRotationPoint(-2F, 22F, 5F);

		bodyModel[5].addShapeBox(0F, 0F, 0F, 3, 2, 4, 0F, 0F, -1F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Box 5
		bodyModel[5].setRotationPoint(-8F, 22F, -2F);

		bodyModel[6].addShapeBox(0F, 0F, 0F, 3, 2, 4, 0F, 0F, 0F, 0F, 0F, -1F, -1F, 0F, -1F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F); // Box 6
		bodyModel[6].setRotationPoint(5F, 22F, -2F);

		bodyModel[7].addBox(0F, 0F, 0F, 8, 1, 6, 0F); // Box 7
		bodyModel[7].setRotationPoint(-4F, 21F, -3F);

		bodyModel[8].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 8
		bodyModel[8].setRotationPoint(-4F, 21F, -4F);

		bodyModel[9].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 9
		bodyModel[9].setRotationPoint(-4F, 21F, 3F);

		bodyModel[10].addBox(0F, 0F, 0F, 12, 1, 10, 0F); // Box 10
		bodyModel[10].setRotationPoint(-6F, 20F, -5F);

		bodyModel[11].addShapeBox(0F, 0F, 0F, 12, 1, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 11
		bodyModel[11].setRotationPoint(-6F, 20F, -6F);

		bodyModel[12].addShapeBox(0F, 0F, 0F, 12, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 12
		bodyModel[12].setRotationPoint(-6F, 20F, 5F);

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
