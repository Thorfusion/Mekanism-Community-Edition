package defense.client.model.missile;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.tmt.ModelRendererTurbo;

@SideOnly(Side.CLIENT)
public class ModelShrapnelMissile extends ModelMissileBase
{
    // fields
    ModelRendererTurbo[] bodyModel;

    public ModelShrapnelMissile()
    {
		int textureX = 32;
		int textureY = 128;

		bodyModel = new ModelRendererTurbo[84];
		bodyModel[0] = new ModelRendererTurbo(this, 0, 2, textureX, textureY); // Import ImportBox0
		bodyModel[1] = new ModelRendererTurbo(this, 0, 2, textureX, textureY); // Import ImportBox1
		bodyModel[2] = new ModelRendererTurbo(this, 0, 2, textureX, textureY); // Import ImportBox2
		bodyModel[3] = new ModelRendererTurbo(this, 0, 17, textureX, textureY); // Import Box0
		bodyModel[4] = new ModelRendererTurbo(this, 0, 17, textureX, textureY); // Import Box1
		bodyModel[5] = new ModelRendererTurbo(this, 0, 17, textureX, textureY); // Import Box2
		bodyModel[6] = new ModelRendererTurbo(this, 0, 25, textureX, textureY); // Import Box3
		bodyModel[7] = new ModelRendererTurbo(this, 0, 25, textureX, textureY); // Import Box4
		bodyModel[8] = new ModelRendererTurbo(this, 0, 25, textureX, textureY); // Import Box5
		bodyModel[9] = new ModelRendererTurbo(this, 0, 33, textureX, textureY); // Import Box6
		bodyModel[10] = new ModelRendererTurbo(this, 0, 50, textureX, textureY); // Import Box7
		bodyModel[11] = new ModelRendererTurbo(this, 0, 50, textureX, textureY); // Import Box8
		bodyModel[12] = new ModelRendererTurbo(this, 0, 66, textureX, textureY); // Import Box9
		bodyModel[13] = new ModelRendererTurbo(this, 0, 66, textureX, textureY); // Import Box10
		bodyModel[14] = new ModelRendererTurbo(this, 0, 66, textureX, textureY); // Import Box11
		bodyModel[15] = new ModelRendererTurbo(this, 0, 70, textureX, textureY); // Import Box12
		bodyModel[16] = new ModelRendererTurbo(this, 0, 70, textureX, textureY); // Import Box13
		bodyModel[17] = new ModelRendererTurbo(this, 0, 70, textureX, textureY); // Import Box14
		bodyModel[18] = new ModelRendererTurbo(this, 0, 79, textureX, textureY); // Import Box15
		bodyModel[19] = new ModelRendererTurbo(this, 0, 79, textureX, textureY); // Import Box16
		bodyModel[20] = new ModelRendererTurbo(this, 0, 79, textureX, textureY); // Import Box17
		bodyModel[21] = new ModelRendererTurbo(this, 0, 83, textureX, textureY); // Import Box18
		bodyModel[22] = new ModelRendererTurbo(this, 0, 83, textureX, textureY); // Import Box19
		bodyModel[23] = new ModelRendererTurbo(this, 0, 83, textureX, textureY); // Import Box20
		bodyModel[24] = new ModelRendererTurbo(this, 0, 85, textureX, textureY); // Import Box21
		bodyModel[25] = new ModelRendererTurbo(this, 0, 85, textureX, textureY); // Import Box22
		bodyModel[26] = new ModelRendererTurbo(this, 0, 85, textureX, textureY); // Import Box23
		bodyModel[27] = new ModelRendererTurbo(this, 0, 85, textureX, textureY); // Import Box24
		bodyModel[28] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 0
		bodyModel[29] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 1
		bodyModel[30] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 2
		bodyModel[31] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 3
		bodyModel[32] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 4
		bodyModel[33] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 5
		bodyModel[34] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 6
		bodyModel[35] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 7
		bodyModel[36] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 8
		bodyModel[37] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 9
		bodyModel[38] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 10
		bodyModel[39] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 11
		bodyModel[40] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 12
		bodyModel[41] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 13
		bodyModel[42] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 14
		bodyModel[43] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 15
		bodyModel[44] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 16
		bodyModel[45] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 17
		bodyModel[46] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 18
		bodyModel[47] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 19
		bodyModel[48] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 20
		bodyModel[49] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 21
		bodyModel[50] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 22
		bodyModel[51] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 23
		bodyModel[52] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 32
		bodyModel[53] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 33
		bodyModel[54] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 34
		bodyModel[55] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 35
		bodyModel[56] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 36
		bodyModel[57] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 37
		bodyModel[58] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 38
		bodyModel[59] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 39
		bodyModel[60] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 40
		bodyModel[61] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 41
		bodyModel[62] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 42
		bodyModel[63] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 43
		bodyModel[64] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 44
		bodyModel[65] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 45
		bodyModel[66] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 46
		bodyModel[67] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Box 47
		bodyModel[68] = new ModelRendererTurbo(this, 0, 94, textureX, textureY); // Box 48
		bodyModel[69] = new ModelRendererTurbo(this, 0, 102, textureX, textureY); // Box 49
		bodyModel[70] = new ModelRendererTurbo(this, 0, 102, textureX, textureY); // Box 50
		bodyModel[71] = new ModelRendererTurbo(this, 0, 108, textureX, textureY); // Box 51
		bodyModel[72] = new ModelRendererTurbo(this, 0, 108, textureX, textureY); // Box 52
		bodyModel[73] = new ModelRendererTurbo(this, 0, 108, textureX, textureY); // Box 53
		bodyModel[74] = new ModelRendererTurbo(this, 0, 108, textureX, textureY); // Box 54
		bodyModel[75] = new ModelRendererTurbo(this, 0, 114, textureX, textureY); // Box 55
		bodyModel[76] = new ModelRendererTurbo(this, 0, 114, textureX, textureY); // Box 56
		bodyModel[77] = new ModelRendererTurbo(this, 0, 116, textureX, textureY); // Box 57
		bodyModel[78] = new ModelRendererTurbo(this, 0, 114, textureX, textureY); // Box 58
		bodyModel[79] = new ModelRendererTurbo(this, 0, 114, textureX, textureY); // Box 59
		bodyModel[80] = new ModelRendererTurbo(this, 0, 116, textureX, textureY); // Box 60
		bodyModel[81] = new ModelRendererTurbo(this, 0, 114, textureX, textureY); // Box 61
		bodyModel[82] = new ModelRendererTurbo(this, 0, 114, textureX, textureY); // Box 62
		bodyModel[83] = new ModelRendererTurbo(this, 0, 116, textureX, textureY); // Box 63

		bodyModel[0].addBox(-3F, -1F, -1F, 6, 12, 2, 0F); // Import ImportBox0
		bodyModel[0].setRotationPoint(0F, 7F, 0F);
		bodyModel[0].rotateAngleY = 3.92699082F;

		bodyModel[1].addShapeBox(-3F, -1F, -3F, 6, 12, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox1
		bodyModel[1].setRotationPoint(0F, 7F, 0F);
		bodyModel[1].rotateAngleY = 3.92699082F;

		bodyModel[2].addShapeBox(-3F, -1F, 1F, 6, 12, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Import ImportBox2
		bodyModel[2].setRotationPoint(0F, 7F, 0F);
		bodyModel[2].rotateAngleY = 3.92699082F;

		bodyModel[3].addShapeBox(-3F, -1F, -1F, 6, 6, 2, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box0
		bodyModel[3].setRotationPoint(0F, 19F, 0F);
		bodyModel[3].rotateAngleY = 3.92699082F;

		bodyModel[4].addShapeBox(-3F, -1F, -3F, 6, 6, 2, 0F, -2F, 0F, -1F, -2F, 0F, -1F, -1F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box1
		bodyModel[4].setRotationPoint(0F, 19F, 0F);
		bodyModel[4].rotateAngleY = 3.92699082F;

		bodyModel[5].addShapeBox(-3F, -1F, 1F, 6, 6, 2, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, -1F, -2F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Import Box2
		bodyModel[5].setRotationPoint(0F, 19F, 0F);
		bodyModel[5].rotateAngleY = 3.92699082F;

		bodyModel[6].addShapeBox(-3F, -1F, -3F, 6, 6, 2, 0F, -2F, 0F, -1F, -2F, 0F, -1F, -1F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box3
		bodyModel[6].setRotationPoint(0F, 1F, 0F);
		bodyModel[6].rotateAngleY = 3.92699082F;

		bodyModel[7].addShapeBox(-3F, -1F, -1F, 6, 6, 2, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box4
		bodyModel[7].setRotationPoint(0F, 1F, 0F);
		bodyModel[7].rotateAngleY = 3.92699082F;

		bodyModel[8].addShapeBox(-3F, -1F, 1F, 6, 6, 2, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, -1F, -2F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Import Box5
		bodyModel[8].setRotationPoint(0F, 1F, 0F);
		bodyModel[8].rotateAngleY = 3.92699082F;

		bodyModel[9].addShapeBox(-2F, -1F, -1F, 4, 15, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box6
		bodyModel[9].setRotationPoint(0F, -14F, 0F);
		bodyModel[9].rotateAngleY = 3.92699082F;

		bodyModel[10].addShapeBox(-2F, -1F, -2F, 4, 15, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box7
		bodyModel[10].setRotationPoint(0F, -14F, 0F);
		bodyModel[10].rotateAngleY = 3.92699082F;

		bodyModel[11].addShapeBox(-2F, -1F, 1F, 4, 15, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Import Box8
		bodyModel[11].setRotationPoint(0F, -14F, 0F);
		bodyModel[11].rotateAngleY = 3.92699082F;

		bodyModel[12].addShapeBox(-3F, -1F, 1F, 6, 2, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, -1F, -2F, 0F, -1F); // Import Box9
		bodyModel[12].setRotationPoint(0F, -16F, 0F);
		bodyModel[12].rotateAngleY = 3.92699082F;

		bodyModel[13].addShapeBox(-3F, -1F, -1F, 6, 2, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Import Box10
		bodyModel[13].setRotationPoint(0F, -16F, 0F);
		bodyModel[13].rotateAngleY = 3.92699082F;

		bodyModel[14].addShapeBox(-3F, -1F, -3F, 6, 2, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, -1F, -2F, 0F, -1F, -1F, 0F, 0F, -1F, 0F, 0F); // Import Box11
		bodyModel[14].setRotationPoint(0F, -16F, 0F);
		bodyModel[14].rotateAngleY = 3.92699082F;

		bodyModel[15].addShapeBox(-3F, -1F, 1F, 6, 7, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Import Box12
		bodyModel[15].setRotationPoint(0F, -23F, 0F);
		bodyModel[15].rotateAngleY = 3.92699082F;

		bodyModel[16].addBox(-3F, -1F, -1F, 6, 7, 2, 0F); // Import Box13
		bodyModel[16].setRotationPoint(0F, -23F, 0F);
		bodyModel[16].rotateAngleY = 3.92699082F;

		bodyModel[17].addShapeBox(-3F, -1F, -3F, 6, 7, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box14
		bodyModel[17].setRotationPoint(0F, -23F, 0F);
		bodyModel[17].rotateAngleY = 3.92699082F;

		bodyModel[18].addShapeBox(-3F, -1F, -1F, 6, 2, 2, 0F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box15
		bodyModel[18].setRotationPoint(0F, -25F, 0F);
		bodyModel[18].rotateAngleY = 3.92699082F;

		bodyModel[19].addShapeBox(-3F, -1F, -3F, 6, 2, 2, 0F, -2.5F, 0F, -1.5F, -2.5F, 0F, -1.5F, -1.5F, 0F, 0.5F, -1.5F, 0F, 0.5F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box16
		bodyModel[19].setRotationPoint(0F, -25F, 0F);
		bodyModel[19].rotateAngleY = 3.92699082F;

		bodyModel[20].addShapeBox(-3F, -1F, 1F, 6, 2, 2, 0F, -1.5F, 0F, 0.5F, -1.5F, 0F, 0.5F, -2.5F, 0F, -1.5F, -2.5F, 0F, -1.5F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Import Box17
		bodyModel[20].setRotationPoint(0F, -25F, 0F);
		bodyModel[20].rotateAngleY = 3.92699082F;

		bodyModel[21].addShapeBox(-1.5F, 0F, -0.5F, 3, 1, 1, 0F, -1F, 0F, -0.33F, -1F, 0F, -0.33F, -1F, 0F, -0.33F, -1F, 0F, -0.33F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box18
		bodyModel[21].setRotationPoint(0F, -27F, 0F);
		bodyModel[21].rotateAngleY = 3.92699082F;

		bodyModel[22].addShapeBox(-1.5F, 0F, -1.5F, 3, 1, 1, 0F, -1.33F, 0F, -1F, -1.33F, 0F, -1F, -1F, 0F, 0.33F, -1F, 0F, 0.33F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box19
		bodyModel[22].setRotationPoint(0F, -27F, 0F);
		bodyModel[22].rotateAngleY = 3.92699082F;

		bodyModel[23].addShapeBox(-1.5F, 0F, 0.5F, 3, 1, 1, 0F, -1F, 0F, 0.33F, -1F, 0F, 0.33F, -1.33F, 0F, -1F, -1.33F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Import Box20
		bodyModel[23].setRotationPoint(0F, -27F, 0F);
		bodyModel[23].rotateAngleY = 3.92699082F;

		bodyModel[24].addShapeBox(-7F, -1F, -0.5F, 4, 8, 1, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box21
		bodyModel[24].setRotationPoint(0F, 11F, 0F);
		bodyModel[24].rotateAngleY = 3.92699082F;

		bodyModel[25].addShapeBox(-7F, -1F, -0.5F, 4, 8, 1, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box22
		bodyModel[25].setRotationPoint(0F, 11F, 0F);
		bodyModel[25].rotateAngleY = 5.49778714F;

		bodyModel[26].addShapeBox(-7F, -1F, -0.5F, 4, 8, 1, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box23
		bodyModel[26].setRotationPoint(0F, 11F, 0F);
		bodyModel[26].rotateAngleY = 2.35619449F;

		bodyModel[27].addShapeBox(-7F, -1F, -0.5F, 4, 8, 1, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box24
		bodyModel[27].setRotationPoint(0F, 11F, 0F);
		bodyModel[27].rotateAngleY = 0.78539816F;

		bodyModel[28].addShapeBox(-3.7F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 0
		bodyModel[28].setRotationPoint(0F, -22F, 0F);
		bodyModel[28].rotateAngleY = 3.92699082F;

		bodyModel[29].addShapeBox(-3.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 1
		bodyModel[29].setRotationPoint(0F, -22F, 0F);
		bodyModel[29].rotateAngleY = 4.71238898F;

		bodyModel[30].addShapeBox(-3.7F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 2
		bodyModel[30].setRotationPoint(0F, -22F, 0F);
		bodyModel[30].rotateAngleY = 5.49778714F;

		bodyModel[31].addShapeBox(-3.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 3
		bodyModel[31].setRotationPoint(0F, -22F, 0F);
		bodyModel[31].rotateAngleY = 6.2831853F;

		bodyModel[32].addShapeBox(-3.7F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 4
		bodyModel[32].setRotationPoint(0F, -22F, 0F);
		bodyModel[32].rotateAngleY = 0.78539816F;

		bodyModel[33].addShapeBox(-3.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 5
		bodyModel[33].setRotationPoint(0F, -22F, 0F);
		bodyModel[33].rotateAngleY = 1.57079632F;

		bodyModel[34].addShapeBox(-3.7F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 6
		bodyModel[34].setRotationPoint(0F, -22F, 0F);
		bodyModel[34].rotateAngleY = 2.35619449F;

		bodyModel[35].addShapeBox(-3.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 7
		bodyModel[35].setRotationPoint(0F, -22F, 0F);
		bodyModel[35].rotateAngleY = 3.14159265F;

		bodyModel[36].addShapeBox(-3.7F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 8
		bodyModel[36].setRotationPoint(0F, -20F, 0F);
		bodyModel[36].rotateAngleY = 2.35619449F;

		bodyModel[37].addShapeBox(-3.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 9
		bodyModel[37].setRotationPoint(0F, -20F, 0F);
		bodyModel[37].rotateAngleY = 3.14159265F;

		bodyModel[38].addShapeBox(-3.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 10
		bodyModel[38].setRotationPoint(0F, -20F, 0F);
		bodyModel[38].rotateAngleY = 1.57079632F;

		bodyModel[39].addShapeBox(-3.7F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 11
		bodyModel[39].setRotationPoint(0F, -20F, 0F);
		bodyModel[39].rotateAngleY = 0.78539816F;

		bodyModel[40].addShapeBox(-3.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 12
		bodyModel[40].setRotationPoint(0F, -20F, 0F);
		bodyModel[40].rotateAngleY = 6.2831853F;

		bodyModel[41].addShapeBox(-3.7F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 13
		bodyModel[41].setRotationPoint(0F, -20F, 0F);
		bodyModel[41].rotateAngleY = 5.49778714F;

		bodyModel[42].addShapeBox(-3.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 14
		bodyModel[42].setRotationPoint(0F, -20F, 0F);
		bodyModel[42].rotateAngleY = 4.71238898F;

		bodyModel[43].addShapeBox(-3.7F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 15
		bodyModel[43].setRotationPoint(0F, -20F, 0F);
		bodyModel[43].rotateAngleY = 3.92699082F;

		bodyModel[44].addShapeBox(-3.7F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 16
		bodyModel[44].setRotationPoint(0F, -18F, 0F);
		bodyModel[44].rotateAngleY = 2.35619449F;

		bodyModel[45].addShapeBox(-3.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 17
		bodyModel[45].setRotationPoint(0F, -18F, 0F);
		bodyModel[45].rotateAngleY = 3.14159265F;

		bodyModel[46].addShapeBox(-3.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 18
		bodyModel[46].setRotationPoint(0F, -18F, 0F);
		bodyModel[46].rotateAngleY = 1.57079632F;

		bodyModel[47].addShapeBox(-3.7F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 19
		bodyModel[47].setRotationPoint(0F, -18F, 0F);
		bodyModel[47].rotateAngleY = 0.78539816F;

		bodyModel[48].addShapeBox(-3.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 20
		bodyModel[48].setRotationPoint(0F, -18F, 0F);
		bodyModel[48].rotateAngleY = 6.2831853F;

		bodyModel[49].addShapeBox(-3.7F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 21
		bodyModel[49].setRotationPoint(0F, -18F, 0F);
		bodyModel[49].rotateAngleY = 5.49778714F;

		bodyModel[50].addShapeBox(-3.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 22
		bodyModel[50].setRotationPoint(0F, -18F, 0F);
		bodyModel[50].rotateAngleY = 4.71238898F;

		bodyModel[51].addShapeBox(-3.7F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 23
		bodyModel[51].setRotationPoint(0F, -18F, 0F);
		bodyModel[51].rotateAngleY = 3.92699082F;

		bodyModel[52].addShapeBox(-3.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 32
		bodyModel[52].setRotationPoint(0F, -23F, 0F);
		bodyModel[52].rotateAngleY = 2.35619449F;
		bodyModel[52].rotateAngleZ = 0.48869219F;

		bodyModel[53].addShapeBox(-3.3F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 33
		bodyModel[53].setRotationPoint(0F, -23F, 0F);
		bodyModel[53].rotateAngleY = 3.14159265F;
		bodyModel[53].rotateAngleZ = 0.48869219F;

		bodyModel[54].addShapeBox(-3.3F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 34
		bodyModel[54].setRotationPoint(0F, -23F, 0F);
		bodyModel[54].rotateAngleY = 1.57079632F;
		bodyModel[54].rotateAngleZ = 0.48869219F;

		bodyModel[55].addShapeBox(-3.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 35
		bodyModel[55].setRotationPoint(0F, -23F, 0F);
		bodyModel[55].rotateAngleY = 0.78539816F;
		bodyModel[55].rotateAngleZ = 0.48869219F;

		bodyModel[56].addShapeBox(-3.3F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 36
		bodyModel[56].setRotationPoint(0F, -23F, 0F);
		bodyModel[56].rotateAngleY = 6.2831853F;
		bodyModel[56].rotateAngleZ = 0.48869219F;

		bodyModel[57].addShapeBox(-3.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 37
		bodyModel[57].setRotationPoint(0F, -23F, 0F);
		bodyModel[57].rotateAngleY = 5.49778714F;
		bodyModel[57].rotateAngleZ = 0.48869219F;

		bodyModel[58].addShapeBox(-3.3F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 38
		bodyModel[58].setRotationPoint(0F, -23F, 0F);
		bodyModel[58].rotateAngleY = 4.71238898F;
		bodyModel[58].rotateAngleZ = 0.48869219F;

		bodyModel[59].addShapeBox(-3.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 39
		bodyModel[59].setRotationPoint(0F, -23F, 0F);
		bodyModel[59].rotateAngleY = 3.92699082F;
		bodyModel[59].rotateAngleZ = 0.48869219F;

		bodyModel[60].addShapeBox(-2.8F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 40
		bodyModel[60].setRotationPoint(0F, -24F, 0F);
		bodyModel[60].rotateAngleY = 2.35619449F;
		bodyModel[60].rotateAngleZ = 0.82030475F;

		bodyModel[61].addShapeBox(-2.8F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 41
		bodyModel[61].setRotationPoint(0F, -24F, 0F);
		bodyModel[61].rotateAngleY = 3.14159265F;
		bodyModel[61].rotateAngleZ = 0.82030475F;

		bodyModel[62].addShapeBox(-2.8F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 42
		bodyModel[62].setRotationPoint(0F, -24F, 0F);
		bodyModel[62].rotateAngleY = 1.57079632F;
		bodyModel[62].rotateAngleZ = 0.82030475F;

		bodyModel[63].addShapeBox(-2.8F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 43
		bodyModel[63].setRotationPoint(0F, -24F, 0F);
		bodyModel[63].rotateAngleY = 0.78539816F;
		bodyModel[63].rotateAngleZ = 0.82030475F;

		bodyModel[64].addShapeBox(-2.8F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 44
		bodyModel[64].setRotationPoint(0F, -24F, 0F);
		bodyModel[64].rotateAngleY = 6.2831853F;
		bodyModel[64].rotateAngleZ = 0.82030475F;

		bodyModel[65].addShapeBox(-2.8F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 45
		bodyModel[65].setRotationPoint(0F, -24F, 0F);
		bodyModel[65].rotateAngleY = 5.49778714F;
		bodyModel[65].rotateAngleZ = 0.82030475F;

		bodyModel[66].addShapeBox(-2.8F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 46
		bodyModel[66].setRotationPoint(0F, -24F, 0F);
		bodyModel[66].rotateAngleY = 4.71238898F;
		bodyModel[66].rotateAngleZ = 0.82030475F;

		bodyModel[67].addShapeBox(-2.8F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 47
		bodyModel[67].setRotationPoint(0F, -24F, 0F);
		bodyModel[67].rotateAngleY = 3.92699082F;
		bodyModel[67].rotateAngleZ = 0.82030475F;

		bodyModel[68].addShapeBox(-2.5F, -1F, -1.5F, 5, 5, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 48
		bodyModel[68].setRotationPoint(0F, -7F, 0F);
		bodyModel[68].rotateAngleY = 3.92699082F;

		bodyModel[69].addShapeBox(-2.5F, -1F, -2.5F, 5, 5, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 49
		bodyModel[69].setRotationPoint(0F, -7F, 0F);
		bodyModel[69].rotateAngleY = 3.92699082F;

		bodyModel[70].addShapeBox(-2.5F, -1F, 1.5F, 5, 5, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 50
		bodyModel[70].setRotationPoint(0F, -7F, 0F);
		bodyModel[70].rotateAngleY = 3.92699082F;

		bodyModel[71].addShapeBox(2.5F, -1F, -0.5F, 7, 5, 1, 0F, 0F, 0F, 0F, 0F, -1F, -0.4F, 0F, -1F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -3F, -0.4F, 0F, -3F, -0.4F, 0F, 0F, 0F); // Box 51
		bodyModel[71].setRotationPoint(0F, -7F, 0F);
		bodyModel[71].rotateAngleY = 3.92699082F;

		bodyModel[72].addShapeBox(2.5F, -1F, -0.5F, 7, 5, 1, 0F, 0F, 0F, 0F, 0F, -1F, -0.4F, 0F, -1F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -3F, -0.4F, 0F, -3F, -0.4F, 0F, 0F, 0F); // Box 52
		bodyModel[72].setRotationPoint(0F, -7F, 0F);
		bodyModel[72].rotateAngleY = 5.49778714F;

		bodyModel[73].addShapeBox(2.5F, -1F, -0.5F, 7, 5, 1, 0F, 0F, 0F, 0F, 0F, -1F, -0.4F, 0F, -1F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -3F, -0.4F, 0F, -3F, -0.4F, 0F, 0F, 0F); // Box 53
		bodyModel[73].setRotationPoint(0F, -7F, 0F);
		bodyModel[73].rotateAngleY = 2.35619449F;

		bodyModel[74].addShapeBox(2.5F, -1F, -0.5F, 7, 5, 1, 0F, 0F, 0F, 0F, 0F, -1F, -0.4F, 0F, -1F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -3F, -0.4F, 0F, -3F, -0.4F, 0F, 0F, 0F); // Box 54
		bodyModel[74].setRotationPoint(0F, -7F, 0F);
		bodyModel[74].rotateAngleY = 0.78539817F;

		bodyModel[75].addShapeBox(-2.5F, -1F, -2.5F, 5, 1, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 55
		bodyModel[75].setRotationPoint(0F, -9F, 0F);
		bodyModel[75].rotateAngleY = 3.92699082F;

		bodyModel[76].addShapeBox(-2.5F, -1F, 1.5F, 5, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 56
		bodyModel[76].setRotationPoint(0F, -9F, 0F);
		bodyModel[76].rotateAngleY = 3.92699082F;

		bodyModel[77].addShapeBox(-2.5F, -1F, -1.5F, 5, 1, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 57
		bodyModel[77].setRotationPoint(0F, -9F, 0F);
		bodyModel[77].rotateAngleY = 3.92699082F;

		bodyModel[78].addShapeBox(-2.5F, -1F, -2.5F, 5, 1, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 58
		bodyModel[78].setRotationPoint(0F, -11F, 0F);
		bodyModel[78].rotateAngleY = 3.92699082F;

		bodyModel[79].addShapeBox(-2.5F, -1F, 1.5F, 5, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 59
		bodyModel[79].setRotationPoint(0F, -11F, 0F);
		bodyModel[79].rotateAngleY = 3.92699082F;

		bodyModel[80].addShapeBox(-2.5F, -1F, -1.5F, 5, 1, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 60
		bodyModel[80].setRotationPoint(0F, -11F, 0F);
		bodyModel[80].rotateAngleY = 3.92699082F;

		bodyModel[81].addShapeBox(-2.5F, -1F, -2.5F, 5, 1, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 61
		bodyModel[81].setRotationPoint(0F, -13F, 0F);
		bodyModel[81].rotateAngleY = 3.92699082F;

		bodyModel[82].addShapeBox(-2.5F, -1F, 1.5F, 5, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 62
		bodyModel[82].setRotationPoint(0F, -13F, 0F);
		bodyModel[82].rotateAngleY = 3.92699082F;

		bodyModel[83].addShapeBox(-2.5F, -1F, -1.5F, 5, 1, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 63
		bodyModel[83].setRotationPoint(0F, -13F, 0F);
		bodyModel[83].rotateAngleY = 3.92699082F;
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
