package defense.client.model.missile;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.tmt.ModelRendererTurbo;

@SideOnly(Side.CLIENT)
public class ModelDebilitationMissile extends ModelMissileBase
{
    // fields
    ModelRendererTurbo[] bodyModel;

    public ModelDebilitationMissile()
    {
		int textureX = 32;
		int textureY = 256;

		bodyModel = new ModelRendererTurbo[58];
		bodyModel[0] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 0
		bodyModel[1] = new ModelRendererTurbo(this, 0, 14, textureX, textureY); // Box 3
		bodyModel[2] = new ModelRendererTurbo(this, 0, 14, textureX, textureY); // Box 4
		bodyModel[3] = new ModelRendererTurbo(this, 0, 27, textureX, textureY); // Box 5
		bodyModel[4] = new ModelRendererTurbo(this, 0, 40, textureX, textureY); // Box 9
		bodyModel[5] = new ModelRendererTurbo(this, 0, 50, textureX, textureY); // Box 13
		bodyModel[6] = new ModelRendererTurbo(this, 0, 54, textureX, textureY); // Box 14
		bodyModel[7] = new ModelRendererTurbo(this, 0, 27, textureX, textureY); // Box 15
		bodyModel[8] = new ModelRendererTurbo(this, 0, 40, textureX, textureY); // Box 16
		bodyModel[9] = new ModelRendererTurbo(this, 0, 50, textureX, textureY); // Box 17
		bodyModel[10] = new ModelRendererTurbo(this, 0, 54, textureX, textureY); // Box 18
		bodyModel[11] = new ModelRendererTurbo(this, 0, 27, textureX, textureY); // Box 19
		bodyModel[12] = new ModelRendererTurbo(this, 0, 40, textureX, textureY); // Box 20
		bodyModel[13] = new ModelRendererTurbo(this, 0, 50, textureX, textureY); // Box 21
		bodyModel[14] = new ModelRendererTurbo(this, 0, 54, textureX, textureY); // Box 22
		bodyModel[15] = new ModelRendererTurbo(this, 0, 27, textureX, textureY); // Box 23
		bodyModel[16] = new ModelRendererTurbo(this, 0, 40, textureX, textureY); // Box 24
		bodyModel[17] = new ModelRendererTurbo(this, 0, 50, textureX, textureY); // Box 25
		bodyModel[18] = new ModelRendererTurbo(this, 0, 54, textureX, textureY); // Box 26
		bodyModel[19] = new ModelRendererTurbo(this, 0, 59, textureX, textureY); // Box 27
		bodyModel[20] = new ModelRendererTurbo(this, 0, 66, textureX, textureY); // Box 30
		bodyModel[21] = new ModelRendererTurbo(this, 0, 66, textureX, textureY); // Box 31
		bodyModel[22] = new ModelRendererTurbo(this, 0, 72, textureX, textureY); // Box 32
		bodyModel[23] = new ModelRendererTurbo(this, 0, 72, textureX, textureY); // Box 33
		bodyModel[24] = new ModelRendererTurbo(this, 0, 72, textureX, textureY); // Box 34
		bodyModel[25] = new ModelRendererTurbo(this, 0, 90, textureX, textureY); // Box 35
		bodyModel[26] = new ModelRendererTurbo(this, 0, 86, textureX, textureY); // Box 36
		bodyModel[27] = new ModelRendererTurbo(this, 0, 90, textureX, textureY); // Box 37
		bodyModel[28] = new ModelRendererTurbo(this, 0, 93, textureX, textureY); // Box 38
		bodyModel[29] = new ModelRendererTurbo(this, 0, 101, textureX, textureY); // Box 39
		bodyModel[30] = new ModelRendererTurbo(this, 0, 101, textureX, textureY); // Box 40
		bodyModel[31] = new ModelRendererTurbo(this, 0, 108, textureX, textureY); // Box 41
		bodyModel[32] = new ModelRendererTurbo(this, 0, 113, textureX, textureY); // Box 42
		bodyModel[33] = new ModelRendererTurbo(this, 0, 113, textureX, textureY); // Box 43
		bodyModel[34] = new ModelRendererTurbo(this, 0, 117, textureX, textureY); // Box 46
		bodyModel[35] = new ModelRendererTurbo(this, 0, 117, textureX, textureY); // Box 47
		bodyModel[36] = new ModelRendererTurbo(this, 0, 117, textureX, textureY); // Box 48
		bodyModel[37] = new ModelRendererTurbo(this, 0, 124, textureX, textureY); // Box 49
		bodyModel[38] = new ModelRendererTurbo(this, 0, 120, textureX, textureY); // Box 50
		bodyModel[39] = new ModelRendererTurbo(this, 0, 124, textureX, textureY); // Box 51
		bodyModel[40] = new ModelRendererTurbo(this, 0, 127, textureX, textureY); // Box 52
		bodyModel[41] = new ModelRendererTurbo(this, 0, 127, textureX, textureY); // Box 53
		bodyModel[42] = new ModelRendererTurbo(this, 0, 127, textureX, textureY); // Box 54
		bodyModel[43] = new ModelRendererTurbo(this, 0, 124, textureX, textureY); // Box 55
		bodyModel[44] = new ModelRendererTurbo(this, 0, 120, textureX, textureY); // Box 56
		bodyModel[45] = new ModelRendererTurbo(this, 0, 124, textureX, textureY); // Box 57
		bodyModel[46] = new ModelRendererTurbo(this, 0, 131, textureX, textureY); // Box 67
		bodyModel[47] = new ModelRendererTurbo(this, 0, 135, textureX, textureY); // Box 68
		bodyModel[48] = new ModelRendererTurbo(this, 0, 139, textureX, textureY); // Box 69
		bodyModel[49] = new ModelRendererTurbo(this, 0, 139, textureX, textureY); // Box 70
		bodyModel[50] = new ModelRendererTurbo(this, 0, 131, textureX, textureY); // Box 71
		bodyModel[51] = new ModelRendererTurbo(this, 0, 135, textureX, textureY); // Box 72
		bodyModel[52] = new ModelRendererTurbo(this, 0, 139, textureX, textureY); // Box 73
		bodyModel[53] = new ModelRendererTurbo(this, 0, 131, textureX, textureY); // Box 74
		bodyModel[54] = new ModelRendererTurbo(this, 0, 135, textureX, textureY); // Box 75
		bodyModel[55] = new ModelRendererTurbo(this, 0, 131, textureX, textureY); // Box 76
		bodyModel[56] = new ModelRendererTurbo(this, 0, 139, textureX, textureY); // Box 77
		bodyModel[57] = new ModelRendererTurbo(this, 0, 135, textureX, textureY); // Box 78

		bodyModel[0].addShapeBox(-2F, -1F, -1F, 4, 11, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 0
		bodyModel[0].setRotationPoint(0F, 11F, 0F);
		bodyModel[0].rotateAngleY = 3.92699082F;

		bodyModel[1].addShapeBox(-2F, -1F, -2F, 4, 11, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 3
		bodyModel[1].setRotationPoint(0F, 11F, 0F);
		bodyModel[1].rotateAngleY = 3.92699082F;

		bodyModel[2].addShapeBox(-2F, -1F, 1F, 4, 11, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 4
		bodyModel[2].setRotationPoint(0F, 11F, 0F);
		bodyModel[2].rotateAngleY = 3.92699082F;

		bodyModel[3].addShapeBox(-1.5F, -1F, 1F, 3, 10, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -4F, 0F, 0F, -4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 5
		bodyModel[3].setRotationPoint(0F, 13F, 0F);
		bodyModel[3].rotateAngleY = 3.92699082F;

		bodyModel[4].addShapeBox(-1.5F, -1F, 3F, 3, 8, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -3F, 0F, -1F, -3F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 9
		bodyModel[4].setRotationPoint(0F, 17F, 0F);
		bodyModel[4].rotateAngleY = 3.92699082F;

		bodyModel[5].addShapeBox(-1.5F, -1F, 2F, 3, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 13
		bodyModel[5].setRotationPoint(0F, 23F, 0F);
		bodyModel[5].rotateAngleY = 3.92699082F;

		bodyModel[6].addShapeBox(-1.5F, -1F, 1F, 3, 3, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 14
		bodyModel[6].setRotationPoint(0F, 22F, 0F);
		bodyModel[6].rotateAngleY = 3.92699082F;

		bodyModel[7].addShapeBox(-1.5F, -1F, 1F, 3, 10, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -4F, 0F, 0F, -4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 15
		bodyModel[7].setRotationPoint(0F, 13F, 0F);
		bodyModel[7].rotateAngleY = 2.35619449F;

		bodyModel[8].addShapeBox(-1.5F, -1F, 3F, 3, 8, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -3F, 0F, -1F, -3F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 16
		bodyModel[8].setRotationPoint(0F, 17F, 0F);
		bodyModel[8].rotateAngleY = 2.35619449F;

		bodyModel[9].addShapeBox(-1.5F, -1F, 2F, 3, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 17
		bodyModel[9].setRotationPoint(0F, 23F, 0F);
		bodyModel[9].rotateAngleY = 2.35619449F;

		bodyModel[10].addShapeBox(-1.5F, -1F, 1F, 3, 3, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 18
		bodyModel[10].setRotationPoint(0F, 22F, 0F);
		bodyModel[10].rotateAngleY = 2.35619449F;

		bodyModel[11].addShapeBox(-1.5F, -1F, 1F, 3, 10, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -4F, 0F, 0F, -4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 19
		bodyModel[11].setRotationPoint(0F, 13F, 0F);
		bodyModel[11].rotateAngleY = 0.78539816F;

		bodyModel[12].addShapeBox(-1.5F, -1F, 3F, 3, 8, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -3F, 0F, -1F, -3F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 20
		bodyModel[12].setRotationPoint(0F, 17F, 0F);
		bodyModel[12].rotateAngleY = 0.78539816F;

		bodyModel[13].addShapeBox(-1.5F, -1F, 2F, 3, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 21
		bodyModel[13].setRotationPoint(0F, 23F, 0F);
		bodyModel[13].rotateAngleY = 0.78539816F;

		bodyModel[14].addShapeBox(-1.5F, -1F, 1F, 3, 3, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 22
		bodyModel[14].setRotationPoint(0F, 22F, 0F);
		bodyModel[14].rotateAngleY = 0.78539816F;

		bodyModel[15].addShapeBox(-1.5F, -1F, 1F, 3, 10, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -4F, 0F, 0F, -4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 23
		bodyModel[15].setRotationPoint(0F, 13F, 0F);
		bodyModel[15].rotateAngleY = 5.49778714F;

		bodyModel[16].addShapeBox(-1.5F, -1F, 3F, 3, 8, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -3F, 0F, -1F, -3F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 24
		bodyModel[16].setRotationPoint(0F, 17F, 0F);
		bodyModel[16].rotateAngleY = 5.49778714F;

		bodyModel[17].addShapeBox(-1.5F, -1F, 2F, 3, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 25
		bodyModel[17].setRotationPoint(0F, 23F, 0F);
		bodyModel[17].rotateAngleY = 5.49778714F;

		bodyModel[18].addShapeBox(-1.5F, -1F, 1F, 3, 3, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 26
		bodyModel[18].setRotationPoint(0F, 22F, 0F);
		bodyModel[18].rotateAngleY = 5.49778714F;

		bodyModel[19].addShapeBox(-2F, -1F, -1F, 4, 4, 2, 0F, -0.5F, 0F, -0.5F, -0.5F, 0F, -0.5F, -0.5F, 0F, -0.5F, -0.5F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 27
		bodyModel[19].setRotationPoint(0F, 7F, 0F);
		bodyModel[19].rotateAngleY = 3.92699082F;

		bodyModel[20].addShapeBox(-2F, -1F, -2F, 4, 4, 1, 0F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 30
		bodyModel[20].setRotationPoint(0F, 7F, 0F);
		bodyModel[20].rotateAngleY = 3.92699082F;

		bodyModel[21].addShapeBox(-2F, -1F, 1F, 4, 4, 1, 0F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 31
		bodyModel[21].setRotationPoint(0F, 7F, 0F);
		bodyModel[21].rotateAngleY = 3.92699082F;

		bodyModel[22].addShapeBox(-1.5F, -1F, -0.5F, 3, 8, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 32
		bodyModel[22].setRotationPoint(0F, -1F, 0F);
		bodyModel[22].rotateAngleY = 3.92699082F;

		bodyModel[23].addShapeBox(-1.5F, -1F, -1.5F, 3, 8, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 33
		bodyModel[23].setRotationPoint(0F, -1F, 0F);
		bodyModel[23].rotateAngleY = 3.92699082F;

		bodyModel[24].addShapeBox(-1.5F, -1F, 0.5F, 3, 8, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 34
		bodyModel[24].setRotationPoint(0F, -1F, 0F);
		bodyModel[24].rotateAngleY = 3.92699082F;

		bodyModel[25].addShapeBox(-2F, -1F, -2F, 4, 1, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F); // Box 35
		bodyModel[25].setRotationPoint(0F, -2F, 0F);
		bodyModel[25].rotateAngleY = 3.92699082F;

		bodyModel[26].addShapeBox(-2F, -1F, -1F, 4, 1, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, -0.5F, -0.5F, 0F, -0.5F, -0.5F, 0F, -0.5F, -0.5F, 0F, -0.5F); // Box 36
		bodyModel[26].setRotationPoint(0F, -2F, 0F);
		bodyModel[26].rotateAngleY = 3.92699082F;

		bodyModel[27].addShapeBox(-2F, -1F, 1F, 4, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F); // Box 37
		bodyModel[27].setRotationPoint(0F, -2F, 0F);
		bodyModel[27].rotateAngleY = 3.92699082F;

		bodyModel[28].addShapeBox(-2F, -1F, -1F, 4, 5, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 38
		bodyModel[28].setRotationPoint(0F, -7F, 0F);
		bodyModel[28].rotateAngleY = 3.92699082F;

		bodyModel[29].addShapeBox(-2F, -1F, -2F, 4, 5, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 39
		bodyModel[29].setRotationPoint(0F, -7F, 0F);
		bodyModel[29].rotateAngleY = 3.92699082F;

		bodyModel[30].addShapeBox(-2F, -1F, 1F, 4, 5, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 40
		bodyModel[30].setRotationPoint(0F, -7F, 0F);
		bodyModel[30].rotateAngleY = 3.92699082F;

		bodyModel[31].addShapeBox(-2F, -1F, -1F, 4, 2, 2, 0F, -0.5F, 0F, -0.5F, -0.5F, 0F, -0.5F, -0.5F, 0F, -0.5F, -0.5F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 41
		bodyModel[31].setRotationPoint(0F, -9F, 0F);
		bodyModel[31].rotateAngleY = 3.92699082F;

		bodyModel[32].addShapeBox(-2F, -1F, -2F, 4, 2, 1, 0F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 42
		bodyModel[32].setRotationPoint(0F, -9F, 0F);
		bodyModel[32].rotateAngleY = 3.92699082F;

		bodyModel[33].addShapeBox(-2F, -1F, 1F, 4, 2, 1, 0F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 43
		bodyModel[33].setRotationPoint(0F, -9F, 0F);
		bodyModel[33].rotateAngleY = 3.92699082F;

		bodyModel[34].addShapeBox(-1.5F, -1F, -0.5F, 3, 1, 1, 0F, -1F, 0F, -0.33F, -1F, 0F, -0.33F, -1F, 0F, -0.33F, -1F, 0F, -0.33F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 46
		bodyModel[34].setRotationPoint(0F, -10F, 0F);
		bodyModel[34].rotateAngleY = 3.92699082F;

		bodyModel[35].addShapeBox(-1.5F, -1F, -1.5F, 3, 1, 1, 0F, -1.33F, 0F, -1F, -1.33F, 0F, -1F, -1F, 0F, 0.33F, -1F, 0F, 0.33F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 47
		bodyModel[35].setRotationPoint(0F, -10F, 0F);
		bodyModel[35].rotateAngleY = 3.92699082F;

		bodyModel[36].addShapeBox(-1.5F, -1F, 0.5F, 3, 1, 1, 0F, -1F, 0F, 0.33F, -1F, 0F, 0.33F, -1.33F, 0F, -1F, -1.33F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 48
		bodyModel[36].setRotationPoint(0F, -10F, 0F);
		bodyModel[36].rotateAngleY = 3.92699082F;

		bodyModel[37].addShapeBox(-2F, -1F, -2F, 4, 1, 1, 0F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 49
		bodyModel[37].setRotationPoint(0F, 1F, 0F);
		bodyModel[37].rotateAngleY = 3.92699082F;

		bodyModel[38].addShapeBox(-2F, -1F, -1F, 4, 1, 2, 0F, -0.5F, 0F, -0.5F, -0.5F, 0F, -0.5F, -0.5F, 0F, -0.5F, -0.5F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 50
		bodyModel[38].setRotationPoint(0F, 1F, 0F);
		bodyModel[38].rotateAngleY = 3.92699082F;

		bodyModel[39].addShapeBox(-2F, -1F, 1F, 4, 1, 1, 0F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 51
		bodyModel[39].setRotationPoint(0F, 1F, 0F);
		bodyModel[39].rotateAngleY = 3.92699082F;

		bodyModel[40].addShapeBox(-2F, -1F, -1F, 4, 2, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 52
		bodyModel[40].setRotationPoint(0F, 2F, 0F);
		bodyModel[40].rotateAngleY = 3.92699082F;

		bodyModel[41].addShapeBox(-2F, -1F, 1F, 4, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 53
		bodyModel[41].setRotationPoint(0F, 2F, 0F);
		bodyModel[41].rotateAngleY = 3.92699082F;

		bodyModel[42].addShapeBox(-2F, -1F, -2F, 4, 2, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 54
		bodyModel[42].setRotationPoint(0F, 2F, 0F);
		bodyModel[42].rotateAngleY = 3.92699082F;

		bodyModel[43].addShapeBox(-2F, -1F, 1F, 4, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F); // Box 55
		bodyModel[43].setRotationPoint(0F, 4F, 0F);
		bodyModel[43].rotateAngleY = 3.92699082F;

		bodyModel[44].addShapeBox(-2F, -1F, -1F, 4, 1, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, -0.5F, -0.5F, 0F, -0.5F, -0.5F, 0F, -0.5F, -0.5F, 0F, -0.5F); // Box 56
		bodyModel[44].setRotationPoint(0F, 4F, 0F);
		bodyModel[44].rotateAngleY = 3.92699082F;

		bodyModel[45].addShapeBox(-2F, -1F, -2F, 4, 1, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F); // Box 57
		bodyModel[45].setRotationPoint(0F, 4F, 0F);
		bodyModel[45].rotateAngleY = 3.92699082F;

		bodyModel[46].addShapeBox(-2.5F, -1F, -0.5F, 1, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 67
		bodyModel[46].setRotationPoint(0F, 7F, 0F);
		bodyModel[46].rotateAngleY = 3.92699082F;

		bodyModel[47].addShapeBox(-3.5F, -1F, -0.5F, 1, 2, 1, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F); // Box 68
		bodyModel[47].setRotationPoint(0F, 7F, 0F);
		bodyModel[47].rotateAngleY = 3.92699082F;

		bodyModel[48].addShapeBox(-6.5F, -1F, -0.5F, 3, 5, 1, 0F, 0F, -4F, -0.2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -4F, -0.2F, -1F, 1F, -0.2F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 1F, -0.2F); // Box 69
		bodyModel[48].setRotationPoint(0F, 6F, 0F);
		bodyModel[48].rotateAngleY = 3.92699082F;

		bodyModel[49].addShapeBox(-6.5F, -1F, -0.5F, 3, 5, 1, 0F, 0F, -4F, -0.2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -4F, -0.2F, -1F, 1F, -0.2F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 1F, -0.2F); // Box 70
		bodyModel[49].setRotationPoint(0F, 6F, 0F);
		bodyModel[49].rotateAngleY = 2.35619449F;

		bodyModel[50].addShapeBox(-2.5F, -1F, -0.5F, 1, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 71
		bodyModel[50].setRotationPoint(0F, 7F, 0F);
		bodyModel[50].rotateAngleY = 2.35619449F;

		bodyModel[51].addShapeBox(-3.5F, -1F, -0.5F, 1, 2, 1, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F); // Box 72
		bodyModel[51].setRotationPoint(0F, 7F, 0F);
		bodyModel[51].rotateAngleY = 2.35619449F;

		bodyModel[52].addShapeBox(-6.5F, -1F, -0.5F, 3, 5, 1, 0F, 0F, -4F, -0.2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -4F, -0.2F, -1F, 1F, -0.2F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 1F, -0.2F); // Box 73
		bodyModel[52].setRotationPoint(0F, 6F, 0F);
		bodyModel[52].rotateAngleY = 5.49778714F;

		bodyModel[53].addShapeBox(-2.5F, -1F, -0.5F, 1, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 74
		bodyModel[53].setRotationPoint(0F, 7F, 0F);
		bodyModel[53].rotateAngleY = 5.49778714F;

		bodyModel[54].addShapeBox(-3.5F, -1F, -0.5F, 1, 2, 1, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F); // Box 75
		bodyModel[54].setRotationPoint(0F, 7F, 0F);
		bodyModel[54].rotateAngleY = 5.49778714F;

		bodyModel[55].addShapeBox(-2.5F, -1F, -0.5F, 1, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 76
		bodyModel[55].setRotationPoint(0F, 7F, 0F);
		bodyModel[55].rotateAngleY = 7.06858347F;

		bodyModel[56].addShapeBox(-6.5F, -1F, -0.5F, 3, 5, 1, 0F, 0F, -4F, -0.2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -4F, -0.2F, -1F, 1F, -0.2F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 1F, -0.2F); // Box 77
		bodyModel[56].setRotationPoint(0F, 6F, 0F);
		bodyModel[56].rotateAngleY = 7.06858347F;

		bodyModel[57].addShapeBox(-3.5F, -1F, -0.5F, 1, 2, 1, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F); // Box 78
		bodyModel[57].setRotationPoint(0F, 7F, 0F);
		bodyModel[57].rotateAngleY = 7.06858347F;

    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        this.render(f5);
    }

    @Override
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
