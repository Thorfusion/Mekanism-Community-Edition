package defense.client.model.missile;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.tmt.ModelRendererTurbo;

@SideOnly(Side.CLIENT)
public class ModelHomingMissile extends ModelMissileBase
{
    // fields
    ModelRendererTurbo[] bodyModel;

    public ModelHomingMissile()
    {
		int textureX = 32;
		int textureY = 128;

		bodyModel = new ModelRendererTurbo[64];
		bodyModel[0] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 0
		bodyModel[1] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 1
		bodyModel[2] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 2
		bodyModel[3] = new ModelRendererTurbo(this, 0, 30, textureX, textureY); // Box 4
		bodyModel[4] = new ModelRendererTurbo(this, 0, 30, textureX, textureY); // Box 5
		bodyModel[5] = new ModelRendererTurbo(this, 0, 30, textureX, textureY); // Box 6
		bodyModel[6] = new ModelRendererTurbo(this, 0, 39, textureX, textureY); // Box 7
		bodyModel[7] = new ModelRendererTurbo(this, 0, 62, textureX, textureY); // Box 8
		bodyModel[8] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 9
		bodyModel[9] = new ModelRendererTurbo(this, 0, 83, textureX, textureY); // Box 10
		bodyModel[10] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 11
		bodyModel[11] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 12
		bodyModel[12] = new ModelRendererTurbo(this, 0, 83, textureX, textureY); // Box 13
		bodyModel[13] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 14
		bodyModel[14] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 15
		bodyModel[15] = new ModelRendererTurbo(this, 0, 83, textureX, textureY); // Box 16
		bodyModel[16] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 17
		bodyModel[17] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 18
		bodyModel[18] = new ModelRendererTurbo(this, 0, 83, textureX, textureY); // Box 19
		bodyModel[19] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 20
		bodyModel[20] = new ModelRendererTurbo(this, 0, 83, textureX, textureY); // Box 21
		bodyModel[21] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 22
		bodyModel[22] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 23
		bodyModel[23] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 24
		bodyModel[24] = new ModelRendererTurbo(this, 0, 83, textureX, textureY); // Box 25
		bodyModel[25] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 26
		bodyModel[26] = new ModelRendererTurbo(this, 0, 62, textureX, textureY); // Box 27
		bodyModel[27] = new ModelRendererTurbo(this, 0, 39, textureX, textureY); // Box 28
		bodyModel[28] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 29
		bodyModel[29] = new ModelRendererTurbo(this, 0, 83, textureX, textureY); // Box 30
		bodyModel[30] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 31
		bodyModel[31] = new ModelRendererTurbo(this, 0, 83, textureX, textureY); // Box 32
		bodyModel[32] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 33
		bodyModel[33] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 34
		bodyModel[34] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 35
		bodyModel[35] = new ModelRendererTurbo(this, 0, 83, textureX, textureY); // Box 36
		bodyModel[36] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 37
		bodyModel[37] = new ModelRendererTurbo(this, 0, 62, textureX, textureY); // Box 38
		bodyModel[38] = new ModelRendererTurbo(this, 0, 39, textureX, textureY); // Box 39
		bodyModel[39] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 40
		bodyModel[40] = new ModelRendererTurbo(this, 0, 83, textureX, textureY); // Box 41
		bodyModel[41] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 42
		bodyModel[42] = new ModelRendererTurbo(this, 0, 83, textureX, textureY); // Box 43
		bodyModel[43] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 44
		bodyModel[44] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 45
		bodyModel[45] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 46
		bodyModel[46] = new ModelRendererTurbo(this, 0, 83, textureX, textureY); // Box 47
		bodyModel[47] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Box 48
		bodyModel[48] = new ModelRendererTurbo(this, 0, 62, textureX, textureY); // Box 49
		bodyModel[49] = new ModelRendererTurbo(this, 0, 39, textureX, textureY); // Box 50
		bodyModel[50] = new ModelRendererTurbo(this, 0, 87, textureX, textureY); // Box 51
		bodyModel[51] = new ModelRendererTurbo(this, 0, 87, textureX, textureY); // Box 52
		bodyModel[52] = new ModelRendererTurbo(this, 0, 87, textureX, textureY); // Box 53
		bodyModel[53] = new ModelRendererTurbo(this, 0, 117, textureX, textureY); // Box 54
		bodyModel[54] = new ModelRendererTurbo(this, 0, 117, textureX, textureY); // Box 55
		bodyModel[55] = new ModelRendererTurbo(this, 0, 117, textureX, textureY); // Box 56
		bodyModel[56] = new ModelRendererTurbo(this, 10, 87, textureX, textureY); // Box 57
		bodyModel[57] = new ModelRendererTurbo(this, 10, 95, textureX, textureY); // Box 58
		bodyModel[58] = new ModelRendererTurbo(this, 10, 95, textureX, textureY); // Box 59
		bodyModel[59] = new ModelRendererTurbo(this, 10, 87, textureX, textureY); // Box 60
		bodyModel[60] = new ModelRendererTurbo(this, 10, 95, textureX, textureY); // Box 61
		bodyModel[61] = new ModelRendererTurbo(this, 10, 87, textureX, textureY); // Box 62
		bodyModel[62] = new ModelRendererTurbo(this, 10, 95, textureX, textureY); // Box 63
		bodyModel[63] = new ModelRendererTurbo(this, 10, 87, textureX, textureY); // Box 64

		bodyModel[0].addBox(-3F, -1F, -1F, 6, 23, 2, 0F); // Box 0
		bodyModel[0].setRotationPoint(0F, 2F, 0F);
		bodyModel[0].rotateAngleY = 3.92699082F;

		bodyModel[1].addShapeBox(-3F, -1F, -3F, 6, 23, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 1
		bodyModel[1].setRotationPoint(0F, 2F, 0F);
		bodyModel[1].rotateAngleY = 3.92699082F;

		bodyModel[2].addShapeBox(-3F, -1F, 1F, 6, 23, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Box 2
		bodyModel[2].setRotationPoint(0F, 2F, 0F);
		bodyModel[2].rotateAngleY = 3.92699082F;

		bodyModel[3].addShapeBox(-3F, -7F, -1F, 6, 6, 2, 0F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 4
		bodyModel[3].setRotationPoint(0F, 2F, 0F);
		bodyModel[3].rotateAngleY = 3.92699082F;

		bodyModel[4].addShapeBox(-3F, -7F, -3F, 6, 6, 2, 0F, -2.5F, 0F, -1.5F, -2.5F, 0F, -1.5F, -1.5F, 0F, 0.5F, -1.5F, 0F, 0.5F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 5
		bodyModel[4].setRotationPoint(0F, 2F, 0F);
		bodyModel[4].rotateAngleY = 3.92699082F;

		bodyModel[5].addShapeBox(-3F, -7F, 1F, 6, 6, 2, 0F, -1.5F, 0F, 0.5F, -1.5F, 0F, 0.5F, -2.5F, 0F, -1.5F, -2.5F, 0F, -1.5F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Box 6
		bodyModel[5].setRotationPoint(0F, 2F, 0F);
		bodyModel[5].rotateAngleY = 3.92699082F;

		bodyModel[6].addShapeBox(-0.5F, 0F, 2.5F, 1, 21, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 7
		bodyModel[6].setRotationPoint(0F, 2F, 0F);
		bodyModel[6].rotateAngleY = 3.92699082F;

		bodyModel[7].addShapeBox(-0.5F, 8F, 3.5F, 1, 13, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -10F, 0F, 0F, -10F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 8
		bodyModel[7].setRotationPoint(0F, 2F, 0F);
		bodyModel[7].rotateAngleY = 3.92699082F;

		bodyModel[8].addShapeBox(0.5F, 11F, 2.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 9
		bodyModel[8].setRotationPoint(0F, 2F, 0F);
		bodyModel[8].rotateAngleY = 3.92699082F;

		bodyModel[9].addShapeBox(1.5F, 11F, 1.5F, 2, 1, 2, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F); // Box 10
		bodyModel[9].setRotationPoint(0F, 2F, 0F);
		bodyModel[9].rotateAngleY = 3.92699082F;

		bodyModel[10].addShapeBox(2.5F, 11F, 0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 11
		bodyModel[10].setRotationPoint(0F, 2F, 0F);
		bodyModel[10].rotateAngleY = 3.92699082F;

		bodyModel[11].addShapeBox(0.5F, 16F, 2.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 12
		bodyModel[11].setRotationPoint(0F, 2F, 0F);
		bodyModel[11].rotateAngleY = 3.92699082F;

		bodyModel[12].addShapeBox(1.5F, 16F, 1.5F, 2, 1, 2, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F); // Box 13
		bodyModel[12].setRotationPoint(0F, 2F, 0F);
		bodyModel[12].rotateAngleY = 3.92699082F;

		bodyModel[13].addShapeBox(2.5F, 16F, 0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 14
		bodyModel[13].setRotationPoint(0F, 2F, 0F);
		bodyModel[13].rotateAngleY = 3.92699082F;

		bodyModel[14].addShapeBox(0.5F, 19F, 2.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 15
		bodyModel[14].setRotationPoint(0F, 2F, 0F);
		bodyModel[14].rotateAngleY = 3.92699082F;

		bodyModel[15].addShapeBox(1.5F, 19F, 1.5F, 2, 1, 2, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F); // Box 16
		bodyModel[15].setRotationPoint(0F, 2F, 0F);
		bodyModel[15].rotateAngleY = 3.92699082F;

		bodyModel[16].addShapeBox(2.5F, 19F, 0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 17
		bodyModel[16].setRotationPoint(0F, 2F, 0F);
		bodyModel[16].rotateAngleY = 3.92699082F;

		bodyModel[17].addShapeBox(0.5F, 19F, 2.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 18
		bodyModel[17].setRotationPoint(0F, 2F, 0F);
		bodyModel[17].rotateAngleY = 2.35619449F;

		bodyModel[18].addShapeBox(1.5F, 19F, 1.5F, 2, 1, 2, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F); // Box 19
		bodyModel[18].setRotationPoint(0F, 2F, 0F);
		bodyModel[18].rotateAngleY = 2.35619449F;

		bodyModel[19].addShapeBox(2.5F, 19F, 0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 20
		bodyModel[19].setRotationPoint(0F, 2F, 0F);
		bodyModel[19].rotateAngleY = 2.35619449F;

		bodyModel[20].addShapeBox(1.5F, 11F, 1.5F, 2, 1, 2, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F); // Box 21
		bodyModel[20].setRotationPoint(0F, 2F, 0F);
		bodyModel[20].rotateAngleY = 2.35619449F;

		bodyModel[21].addShapeBox(2.5F, 11F, 0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 22
		bodyModel[21].setRotationPoint(0F, 2F, 0F);
		bodyModel[21].rotateAngleY = 2.35619449F;

		bodyModel[22].addShapeBox(0.5F, 11F, 2.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 23
		bodyModel[22].setRotationPoint(0F, 2F, 0F);
		bodyModel[22].rotateAngleY = 2.35619449F;

		bodyModel[23].addShapeBox(0.5F, 16F, 2.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 24
		bodyModel[23].setRotationPoint(0F, 2F, 0F);
		bodyModel[23].rotateAngleY = 2.35619449F;

		bodyModel[24].addShapeBox(1.5F, 16F, 1.5F, 2, 1, 2, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F); // Box 25
		bodyModel[24].setRotationPoint(0F, 2F, 0F);
		bodyModel[24].rotateAngleY = 2.35619449F;

		bodyModel[25].addShapeBox(2.5F, 16F, 0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 26
		bodyModel[25].setRotationPoint(0F, 2F, 0F);
		bodyModel[25].rotateAngleY = 2.35619449F;

		bodyModel[26].addShapeBox(-0.5F, 8F, 3.5F, 1, 13, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -10F, 0F, 0F, -10F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 27
		bodyModel[26].setRotationPoint(0F, 2F, 0F);
		bodyModel[26].rotateAngleY = 2.35619449F;

		bodyModel[27].addShapeBox(-0.5F, 0F, 2.5F, 1, 21, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 28
		bodyModel[27].setRotationPoint(0F, 2F, 0F);
		bodyModel[27].rotateAngleY = 2.35619449F;

		bodyModel[28].addShapeBox(0.5F, 19F, 2.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 29
		bodyModel[28].setRotationPoint(0F, 2F, 0F);
		bodyModel[28].rotateAngleY = 0.78539816F;

		bodyModel[29].addShapeBox(1.5F, 19F, 1.5F, 2, 1, 2, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F); // Box 30
		bodyModel[29].setRotationPoint(0F, 2F, 0F);
		bodyModel[29].rotateAngleY = 0.78539816F;

		bodyModel[30].addShapeBox(2.5F, 19F, 0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 31
		bodyModel[30].setRotationPoint(0F, 2F, 0F);
		bodyModel[30].rotateAngleY = 0.78539816F;

		bodyModel[31].addShapeBox(1.5F, 11F, 1.5F, 2, 1, 2, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F); // Box 32
		bodyModel[31].setRotationPoint(0F, 2F, 0F);
		bodyModel[31].rotateAngleY = 0.78539816F;

		bodyModel[32].addShapeBox(2.5F, 11F, 0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 33
		bodyModel[32].setRotationPoint(0F, 2F, 0F);
		bodyModel[32].rotateAngleY = 0.78539816F;

		bodyModel[33].addShapeBox(0.5F, 11F, 2.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 34
		bodyModel[33].setRotationPoint(0F, 2F, 0F);
		bodyModel[33].rotateAngleY = 0.78539816F;

		bodyModel[34].addShapeBox(0.5F, 16F, 2.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 35
		bodyModel[34].setRotationPoint(0F, 2F, 0F);
		bodyModel[34].rotateAngleY = 0.78539816F;

		bodyModel[35].addShapeBox(1.5F, 16F, 1.5F, 2, 1, 2, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F); // Box 36
		bodyModel[35].setRotationPoint(0F, 2F, 0F);
		bodyModel[35].rotateAngleY = 0.78539816F;

		bodyModel[36].addShapeBox(2.5F, 16F, 0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 37
		bodyModel[36].setRotationPoint(0F, 2F, 0F);
		bodyModel[36].rotateAngleY = 0.78539816F;

		bodyModel[37].addShapeBox(-0.5F, 8F, 3.5F, 1, 13, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -10F, 0F, 0F, -10F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 38
		bodyModel[37].setRotationPoint(0F, 2F, 0F);
		bodyModel[37].rotateAngleY = 0.78539816F;

		bodyModel[38].addShapeBox(-0.5F, 0F, 2.5F, 1, 21, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 39
		bodyModel[38].setRotationPoint(0F, 2F, 0F);
		bodyModel[38].rotateAngleY = 0.78539816F;

		bodyModel[39].addShapeBox(0.5F, 19F, 2.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 40
		bodyModel[39].setRotationPoint(0F, 2F, 0F);
		bodyModel[39].rotateAngleY = -0.78539816F;

		bodyModel[40].addShapeBox(1.5F, 19F, 1.5F, 2, 1, 2, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F); // Box 41
		bodyModel[40].setRotationPoint(0F, 2F, 0F);
		bodyModel[40].rotateAngleY = -0.78539816F;

		bodyModel[41].addShapeBox(2.5F, 19F, 0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 42
		bodyModel[41].setRotationPoint(0F, 2F, 0F);
		bodyModel[41].rotateAngleY = -0.78539816F;

		bodyModel[42].addShapeBox(1.5F, 11F, 1.5F, 2, 1, 2, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F); // Box 43
		bodyModel[42].setRotationPoint(0F, 2F, 0F);
		bodyModel[42].rotateAngleY = -0.78539816F;

		bodyModel[43].addShapeBox(2.5F, 11F, 0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 44
		bodyModel[43].setRotationPoint(0F, 2F, 0F);
		bodyModel[43].rotateAngleY = -0.78539816F;

		bodyModel[44].addShapeBox(0.5F, 11F, 2.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 45
		bodyModel[44].setRotationPoint(0F, 2F, 0F);
		bodyModel[44].rotateAngleY = -0.78539816F;

		bodyModel[45].addShapeBox(0.5F, 16F, 2.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 46
		bodyModel[45].setRotationPoint(0F, 2F, 0F);
		bodyModel[45].rotateAngleY = -0.78539816F;

		bodyModel[46].addShapeBox(1.5F, 16F, 1.5F, 2, 1, 2, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F); // Box 47
		bodyModel[46].setRotationPoint(0F, 2F, 0F);
		bodyModel[46].rotateAngleY = -0.78539816F;

		bodyModel[47].addShapeBox(2.5F, 16F, 0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 48
		bodyModel[47].setRotationPoint(0F, 2F, 0F);
		bodyModel[47].rotateAngleY = -0.78539816F;

		bodyModel[48].addShapeBox(-0.5F, 8F, 3.5F, 1, 13, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -10F, 0F, 0F, -10F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 49
		bodyModel[48].setRotationPoint(0F, 2F, 0F);
		bodyModel[48].rotateAngleY = -0.78539816F;

		bodyModel[49].addShapeBox(-0.5F, 0F, 2.5F, 1, 21, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 50
		bodyModel[49].setRotationPoint(0F, 2F, 0F);
		bodyModel[49].rotateAngleY = -0.78539816F;

		bodyModel[50].addShapeBox(-1.5F, -35F, -0.5F, 3, 28, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 51
		bodyModel[50].setRotationPoint(0F, 2F, 0F);
		bodyModel[50].rotateAngleY = 3.92699082F;

		bodyModel[51].addShapeBox(-1.5F, -35F, -1.5F, 3, 28, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 52
		bodyModel[51].setRotationPoint(0F, 2F, 0F);
		bodyModel[51].rotateAngleY = 3.92699082F;

		bodyModel[52].addShapeBox(-1.5F, -35F, 0.5F, 3, 28, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 53
		bodyModel[52].setRotationPoint(0F, 2F, 0F);
		bodyModel[52].rotateAngleY = 3.92699082F;

		bodyModel[53].addShapeBox(-1.5F, -41F, -0.5F, 3, 6, 1, 0F, -1F, 0F, -0.33F, -1F, 0F, -0.33F, -1F, 0F, -0.33F, -1F, 0F, -0.33F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 54
		bodyModel[53].setRotationPoint(0F, 2F, 0F);
		bodyModel[53].rotateAngleY = 3.92699082F;

		bodyModel[54].addShapeBox(-1.5F, -41F, -1.5F, 3, 6, 1, 0F, -1.33F, 0F, -1F, -1.33F, 0F, -1F, -1F, 0F, 0.33F, -1F, 0F, 0.33F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 55
		bodyModel[54].setRotationPoint(0F, 2F, 0F);
		bodyModel[54].rotateAngleY = 3.92699082F;

		bodyModel[55].addShapeBox(-1.5F, -41F, 0.5F, 3, 6, 1, 0F, -1F, 0F, 0.33F, -1F, 0F, 0.33F, -1.33F, 0F, -1F, -1.33F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 56
		bodyModel[55].setRotationPoint(0F, 2F, 0F);
		bodyModel[55].rotateAngleY = 3.92699082F;

		bodyModel[56].addShapeBox(-0.5F, -12F, 1.5F, 1, 4, 4, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, -2F, 0F, -0.2F, -2F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F); // Box 57
		bodyModel[56].setRotationPoint(0F, 2F, 0F);
		bodyModel[56].rotateAngleY = 3.92699082F;

		bodyModel[57].addShapeBox(-0.5F, -26F, 1.5F, 1, 11, 3, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, -1F, 0F, -0.2F, -1F, 0F); // Box 58
		bodyModel[57].setRotationPoint(0F, 2F, 0F);
		bodyModel[57].rotateAngleY = 3.92699082F;

		bodyModel[58].addShapeBox(-0.5F, -26F, 1.5F, 1, 11, 3, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, -1F, 0F, -0.2F, -1F, 0F); // Box 59
		bodyModel[58].setRotationPoint(0F, 2F, 0F);
		bodyModel[58].rotateAngleY = 2.35619449F;

		bodyModel[59].addShapeBox(-0.5F, -12F, 1.5F, 1, 4, 4, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, -2F, 0F, -0.2F, -2F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F); // Box 60
		bodyModel[59].setRotationPoint(0F, 2F, 0F);
		bodyModel[59].rotateAngleY = 2.35619449F;

		bodyModel[60].addShapeBox(-0.5F, -26F, 1.5F, 1, 11, 3, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, -1F, 0F, -0.2F, -1F, 0F); // Box 61
		bodyModel[60].setRotationPoint(0F, 2F, 0F);
		bodyModel[60].rotateAngleY = -0.78539816F;

		bodyModel[61].addShapeBox(-0.5F, -12F, 1.5F, 1, 4, 4, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, -2F, 0F, -0.2F, -2F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F); // Box 62
		bodyModel[61].setRotationPoint(0F, 2F, 0F);
		bodyModel[61].rotateAngleY = -0.78539816F;

		bodyModel[62].addShapeBox(-0.5F, -26F, 1.5F, 1, 11, 3, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, -1F, 0F, -0.2F, -1F, 0F); // Box 63
		bodyModel[62].setRotationPoint(0F, 2F, 0F);
		bodyModel[62].rotateAngleY = 0.78539816F;

		bodyModel[63].addShapeBox(-0.5F, -12F, 1.5F, 1, 4, 4, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, -2F, 0F, -0.2F, -2F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F); // Box 64
		bodyModel[63].setRotationPoint(0F, 2F, 0F);
		bodyModel[63].rotateAngleY = 0.78539816F;
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
