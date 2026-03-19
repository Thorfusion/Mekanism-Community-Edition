package defense.client.model.missile;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.tmt.ModelRendererTurbo;

@SideOnly(Side.CLIENT)
public class ModelAttractiveMissile extends ModelMissileBase
{
    // fields
    ModelRendererTurbo[] bodyModel;


    public ModelAttractiveMissile()
    {
		int textureX = 32;
		int textureY = 128;

		bodyModel = new ModelRendererTurbo[93];
		bodyModel[0] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Import ImportBox0
		bodyModel[1] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Import ImportBox1
		bodyModel[2] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Import ImportBox2
		bodyModel[3] = new ModelRendererTurbo(this, 0, 24, textureX, textureY); // Import Box0
		bodyModel[4] = new ModelRendererTurbo(this, 0, 24, textureX, textureY); // Import Box1
		bodyModel[5] = new ModelRendererTurbo(this, 0, 24, textureX, textureY); // Import Box2
		bodyModel[6] = new ModelRendererTurbo(this, 0, 32, textureX, textureY); // Import Box3
		bodyModel[7] = new ModelRendererTurbo(this, 0, 32, textureX, textureY); // Import Box4
		bodyModel[8] = new ModelRendererTurbo(this, 0, 32, textureX, textureY); // Import Box5
		bodyModel[9] = new ModelRendererTurbo(this, 0, 41, textureX, textureY); // Import Box6
		bodyModel[10] = new ModelRendererTurbo(this, 0, 47, textureX, textureY); // Import Box7
		bodyModel[11] = new ModelRendererTurbo(this, 0, 47, textureX, textureY); // Import Box8
		bodyModel[12] = new ModelRendererTurbo(this, 0, 52, textureX, textureY); // Import Box9
		bodyModel[13] = new ModelRendererTurbo(this, 0, 52, textureX, textureY); // Import Box10
		bodyModel[14] = new ModelRendererTurbo(this, 0, 52, textureX, textureY); // Import Box11
		bodyModel[15] = new ModelRendererTurbo(this, 0, 56, textureX, textureY); // Import Box12
		bodyModel[16] = new ModelRendererTurbo(this, 0, 56, textureX, textureY); // Import Box13
		bodyModel[17] = new ModelRendererTurbo(this, 0, 56, textureX, textureY); // Import Box14
		bodyModel[18] = new ModelRendererTurbo(this, 0, 65, textureX, textureY); // Import Box15
		bodyModel[19] = new ModelRendererTurbo(this, 0, 65, textureX, textureY); // Import Box16
		bodyModel[20] = new ModelRendererTurbo(this, 0, 65, textureX, textureY); // Import Box17
		bodyModel[21] = new ModelRendererTurbo(this, 0, 70, textureX, textureY); // Import Box18
		bodyModel[22] = new ModelRendererTurbo(this, 0, 70, textureX, textureY); // Import Box19
		bodyModel[23] = new ModelRendererTurbo(this, 0, 70, textureX, textureY); // Import Box20
		bodyModel[24] = new ModelRendererTurbo(this, 0, 72, textureX, textureY); // Import Box21
		bodyModel[25] = new ModelRendererTurbo(this, 0, 72, textureX, textureY); // Import Box22
		bodyModel[26] = new ModelRendererTurbo(this, 0, 72, textureX, textureY); // Import Box23
		bodyModel[27] = new ModelRendererTurbo(this, 0, 72, textureX, textureY); // Import Box24
		bodyModel[28] = new ModelRendererTurbo(this, 0, 81, textureX, textureY); // Box 0
		bodyModel[29] = new ModelRendererTurbo(this, 0, 85, textureX, textureY); // Box 1
		bodyModel[30] = new ModelRendererTurbo(this, 0, 81, textureX, textureY); // Box 2
		bodyModel[31] = new ModelRendererTurbo(this, 0, 85, textureX, textureY); // Box 3
		bodyModel[32] = new ModelRendererTurbo(this, 0, 93, textureX, textureY); // Box 5
		bodyModel[33] = new ModelRendererTurbo(this, 0, 98, textureX, textureY); // Box 12
		bodyModel[34] = new ModelRendererTurbo(this, 0, 100, textureX, textureY); // Box 13
		bodyModel[35] = new ModelRendererTurbo(this, 0, 98, textureX, textureY); // Box 15
		bodyModel[36] = new ModelRendererTurbo(this, 0, 98, textureX, textureY); // Box 16
		bodyModel[37] = new ModelRendererTurbo(this, 0, 98, textureX, textureY); // Box 17
		bodyModel[38] = new ModelRendererTurbo(this, 0, 93, textureX, textureY); // Box 29
		bodyModel[39] = new ModelRendererTurbo(this, 0, 81, textureX, textureY); // Box 30
		bodyModel[40] = new ModelRendererTurbo(this, 0, 85, textureX, textureY); // Box 31
		bodyModel[41] = new ModelRendererTurbo(this, 0, 85, textureX, textureY); // Box 32
		bodyModel[42] = new ModelRendererTurbo(this, 0, 81, textureX, textureY); // Box 33
		bodyModel[43] = new ModelRendererTurbo(this, 0, 98, textureX, textureY); // Box 35
		bodyModel[44] = new ModelRendererTurbo(this, 0, 98, textureX, textureY); // Box 36
		bodyModel[45] = new ModelRendererTurbo(this, 0, 98, textureX, textureY); // Box 39
		bodyModel[46] = new ModelRendererTurbo(this, 0, 98, textureX, textureY); // Box 40
		bodyModel[47] = new ModelRendererTurbo(this, 0, 100, textureX, textureY); // Box 41
		bodyModel[48] = new ModelRendererTurbo(this, 0, 100, textureX, textureY); // Box 42
		bodyModel[49] = new ModelRendererTurbo(this, 0, 100, textureX, textureY); // Box 43
		bodyModel[50] = new ModelRendererTurbo(this, 0, 81, textureX, textureY); // Box 44
		bodyModel[51] = new ModelRendererTurbo(this, 0, 85, textureX, textureY); // Box 45
		bodyModel[52] = new ModelRendererTurbo(this, 0, 81, textureX, textureY); // Box 46
		bodyModel[53] = new ModelRendererTurbo(this, 0, 85, textureX, textureY); // Box 47
		bodyModel[54] = new ModelRendererTurbo(this, 0, 98, textureX, textureY); // Box 48
		bodyModel[55] = new ModelRendererTurbo(this, 0, 98, textureX, textureY); // Box 49
		bodyModel[56] = new ModelRendererTurbo(this, 0, 98, textureX, textureY); // Box 50
		bodyModel[57] = new ModelRendererTurbo(this, 0, 98, textureX, textureY); // Box 51
		bodyModel[58] = new ModelRendererTurbo(this, 0, 100, textureX, textureY); // Box 52
		bodyModel[59] = new ModelRendererTurbo(this, 0, 100, textureX, textureY); // Box 53
		bodyModel[60] = new ModelRendererTurbo(this, 0, 87, textureX, textureY); // Box 54
		bodyModel[61] = new ModelRendererTurbo(this, 0, 93, textureX, textureY); // Box 55
		bodyModel[62] = new ModelRendererTurbo(this, 0, 81, textureX, textureY); // Box 56
		bodyModel[63] = new ModelRendererTurbo(this, 0, 85, textureX, textureY); // Box 57
		bodyModel[64] = new ModelRendererTurbo(this, 0, 81, textureX, textureY); // Box 58
		bodyModel[65] = new ModelRendererTurbo(this, 0, 85, textureX, textureY); // Box 59
		bodyModel[66] = new ModelRendererTurbo(this, 0, 98, textureX, textureY); // Box 60
		bodyModel[67] = new ModelRendererTurbo(this, 0, 98, textureX, textureY); // Box 61
		bodyModel[68] = new ModelRendererTurbo(this, 0, 98, textureX, textureY); // Box 62
		bodyModel[69] = new ModelRendererTurbo(this, 0, 98, textureX, textureY); // Box 63
		bodyModel[70] = new ModelRendererTurbo(this, 0, 100, textureX, textureY); // Box 64
		bodyModel[71] = new ModelRendererTurbo(this, 0, 100, textureX, textureY); // Box 65
		bodyModel[72] = new ModelRendererTurbo(this, 0, 93, textureX, textureY); // Box 67
		bodyModel[73] = new ModelRendererTurbo(this, 0, 103, textureX, textureY); // Box 68
		bodyModel[74] = new ModelRendererTurbo(this, 0, 112, textureX, textureY); // Box 69
		bodyModel[75] = new ModelRendererTurbo(this, 0, 112, textureX, textureY); // Box 70
		bodyModel[76] = new ModelRendererTurbo(this, 14, 71, textureX, textureY); // Box 71
		bodyModel[77] = new ModelRendererTurbo(this, 12, 76, textureX, textureY); // Box 72
		bodyModel[78] = new ModelRendererTurbo(this, 14, 71, textureX, textureY); // Box 73
		bodyModel[79] = new ModelRendererTurbo(this, 14, 82, textureX, textureY); // Box 74
		bodyModel[80] = new ModelRendererTurbo(this, 12, 86, textureX, textureY); // Box 75
		bodyModel[81] = new ModelRendererTurbo(this, 14, 82, textureX, textureY); // Box 76
		bodyModel[82] = new ModelRendererTurbo(this, 15, 91, textureX, textureY); // Box 77
		bodyModel[83] = new ModelRendererTurbo(this, 0, 120, textureX, textureY); // Box 78
		bodyModel[84] = new ModelRendererTurbo(this, 15, 91, textureX, textureY); // Box 79
		bodyModel[85] = new ModelRendererTurbo(this, 0, 120, textureX, textureY); // Box 80
		bodyModel[86] = new ModelRendererTurbo(this, 15, 91, textureX, textureY); // Box 81
		bodyModel[87] = new ModelRendererTurbo(this, 0, 120, textureX, textureY); // Box 82
		bodyModel[88] = new ModelRendererTurbo(this, 15, 91, textureX, textureY); // Box 83
		bodyModel[89] = new ModelRendererTurbo(this, 0, 120, textureX, textureY); // Box 84
		bodyModel[90] = new ModelRendererTurbo(this, 0, 87, textureX, textureY); // Box 85
		bodyModel[91] = new ModelRendererTurbo(this, 0, 87, textureX, textureY); // Box 86
		bodyModel[92] = new ModelRendererTurbo(this, 0, 87, textureX, textureY); // Box 87

		bodyModel[0].addBox(-3F, -1F, -1F, 6, 22, 2, 0F); // Import ImportBox0
		bodyModel[0].setRotationPoint(0F, -3F, 0F);
		bodyModel[0].rotateAngleY = 3.92699082F;

		bodyModel[1].addShapeBox(-3F, -1F, -3F, 6, 22, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox1
		bodyModel[1].setRotationPoint(0F, -3F, 0F);
		bodyModel[1].rotateAngleY = 3.92699082F;

		bodyModel[2].addShapeBox(-3F, -1F, 1F, 6, 22, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Import ImportBox2
		bodyModel[2].setRotationPoint(0F, -3F, 0F);
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

		bodyModel[6].addShapeBox(-3F, -1F, -3F, 6, 7, 2, 0F, -2F, 0F, -1F, -2F, 0F, -1F, -1F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box3
		bodyModel[6].setRotationPoint(0F, -10F, 0F);
		bodyModel[6].rotateAngleY = 3.92699082F;

		bodyModel[7].addShapeBox(-3F, -1F, -1F, 6, 7, 2, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box4
		bodyModel[7].setRotationPoint(0F, -10F, 0F);
		bodyModel[7].rotateAngleY = 3.92699082F;

		bodyModel[8].addShapeBox(-3F, -1F, 1F, 6, 7, 2, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, -1F, -2F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Import Box5
		bodyModel[8].setRotationPoint(0F, -10F, 0F);
		bodyModel[8].rotateAngleY = 3.92699082F;

		bodyModel[9].addShapeBox(-2F, -1F, -1F, 4, 4, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box6
		bodyModel[9].setRotationPoint(0F, -14F, 0F);
		bodyModel[9].rotateAngleY = 3.92699082F;

		bodyModel[10].addShapeBox(-2F, -1F, -2F, 4, 4, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box7
		bodyModel[10].setRotationPoint(0F, -14F, 0F);
		bodyModel[10].rotateAngleY = 3.92699082F;

		bodyModel[11].addShapeBox(-2F, -1F, 1F, 4, 4, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Import Box8
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

		bodyModel[18].addShapeBox(-3F, -1F, -1F, 6, 3, 2, 0F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box15
		bodyModel[18].setRotationPoint(0F, -26F, 0F);
		bodyModel[18].rotateAngleY = 3.92699082F;

		bodyModel[19].addShapeBox(-3F, -1F, -3F, 6, 3, 2, 0F, -2.5F, 0F, -1.5F, -2.5F, 0F, -1.5F, -1.5F, 0F, 0.5F, -1.5F, 0F, 0.5F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box16
		bodyModel[19].setRotationPoint(0F, -26F, 0F);
		bodyModel[19].rotateAngleY = 3.92699082F;

		bodyModel[20].addShapeBox(-3F, -1F, 1F, 6, 3, 2, 0F, -1.5F, 0F, 0.5F, -1.5F, 0F, 0.5F, -2.5F, 0F, -1.5F, -2.5F, 0F, -1.5F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Import Box17
		bodyModel[20].setRotationPoint(0F, -26F, 0F);
		bodyModel[20].rotateAngleY = 3.92699082F;

		bodyModel[21].addShapeBox(-1.5F, 0F, -0.5F, 3, 1, 1, 0F, -1F, 0F, -0.33F, -1F, 0F, -0.33F, -1F, 0F, -0.33F, -1F, 0F, -0.33F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F); // Import Box18
		bodyModel[21].setRotationPoint(0F, -27.5F, 0F);
		bodyModel[21].rotateAngleY = 3.92699082F;

		bodyModel[22].addShapeBox(-1.5F, 0F, -1.5F, 3, 1, 1, 0F, -1.33F, 0F, -1F, -1.33F, 0F, -1F, -1F, 0F, 0.33F, -1F, 0F, 0.33F, -1F, -0.5F, 0F, -1F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F); // Import Box19
		bodyModel[22].setRotationPoint(0F, -27.5F, 0F);
		bodyModel[22].rotateAngleY = 3.92699082F;

		bodyModel[23].addShapeBox(-1.5F, 0F, 0.5F, 3, 1, 1, 0F, -1F, 0F, 0.33F, -1F, 0F, 0.33F, -1.33F, 0F, -1F, -1.33F, 0F, -1F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, -1F, -0.5F, 0F, -1F, -0.5F, 0F); // Import Box20
		bodyModel[23].setRotationPoint(0F, -27.5F, 0F);
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

		bodyModel[28].addTrapezoid(2F, -1F, -1F, 3, 2, 2, 0F, -0.50F, ModelRendererTurbo.MR_LEFT); // Box 0
		bodyModel[28].setRotationPoint(0F, -26F, 0F);
		bodyModel[28].rotateAngleY = 2.35619449F;
		bodyModel[28].rotateAngleZ = 0.95993109F;

		bodyModel[29].addTrapezoid(5F, -0.5F, -0.5F, 3, 1, 1, 0F, -0.40F, ModelRendererTurbo.MR_LEFT); // Box 1
		bodyModel[29].setRotationPoint(0F, -26F, 0F);
		bodyModel[29].rotateAngleY = 2.35619449F;
		bodyModel[29].rotateAngleZ = 0.95993109F;

		bodyModel[30].addTrapezoid(3F, -1F, -1F, 3, 2, 2, 0F, -0.50F, ModelRendererTurbo.MR_LEFT); // Box 2
		bodyModel[30].setRotationPoint(0F, -24F, 0F);
		bodyModel[30].rotateAngleY = 2.35619449F;
		bodyModel[30].rotateAngleZ = 1.04719755F;

		bodyModel[31].addTrapezoid(6F, -0.5F, -0.5F, 3, 1, 1, 0F, -0.40F, ModelRendererTurbo.MR_LEFT); // Box 3
		bodyModel[31].setRotationPoint(0F, -24F, 0F);
		bodyModel[31].rotateAngleY = 2.35619449F;
		bodyModel[31].rotateAngleZ = 1.04719755F;

		bodyModel[32].addShapeBox(3F, 3.5F, -0.5F, 1, 4, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 2F, 0F, 0F); // Box 5
		bodyModel[32].setRotationPoint(0F, -16F, 0F);
		bodyModel[32].rotateAngleY = 3.92699082F;

		bodyModel[33].addTrapezoid(3F, 3.5F, -1.5F, 1, 1, 1, 0F, 0F, ModelRendererTurbo.MR_TOP); // Box 12
		bodyModel[33].setRotationPoint(0F, -19F, 0F);
		bodyModel[33].rotateAngleY = 3.92699082F;

		bodyModel[34].addShapeBox(2F, 3.5F, -3.5F, 2, 1, 2, 0F, 0.5F, 0F, 0.5F, 0F, 0F, -2F, -1F, 0F, 0F, 0.5F, 0F, -1.5F, 0.5F, 0F, 0.5F, 0F, 0F, -2F, -1F, 0F, 0F, 0.5F, 0F, -1.5F); // Box 13
		bodyModel[34].setRotationPoint(0F, -19F, 0F);
		bodyModel[34].rotateAngleY = 3.92699082F;

		bodyModel[35].addTrapezoid(3F, 3.5F, -1.5F, 1, 1, 1, 0F, 0F, ModelRendererTurbo.MR_TOP); // Box 15
		bodyModel[35].setRotationPoint(0F, -17F, 0F);
		bodyModel[35].rotateAngleY = 3.92699082F;

		bodyModel[36].addTrapezoid(3F, 3.5F, 0.5F, 1, 1, 1, 0F, 0F, ModelRendererTurbo.MR_TOP); // Box 16
		bodyModel[36].setRotationPoint(0F, -19F, 0F);
		bodyModel[36].rotateAngleY = 3.92699082F;

		bodyModel[37].addTrapezoid(3F, 3.5F, 0.5F, 1, 1, 1, 0F, 0F, ModelRendererTurbo.MR_TOP); // Box 17
		bodyModel[37].setRotationPoint(0F, -17F, 0F);
		bodyModel[37].rotateAngleY = 3.92699082F;

		bodyModel[38].addShapeBox(3F, 3.5F, -0.5F, 1, 4, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 2F, 0F, 0F); // Box 29
		bodyModel[38].setRotationPoint(0F, -16F, 0F);
		bodyModel[38].rotateAngleY = 2.35619449F;

		bodyModel[39].addTrapezoid(3F, -1F, -1F, 3, 2, 2, 0F, -0.50F, ModelRendererTurbo.MR_LEFT); // Box 30
		bodyModel[39].setRotationPoint(0F, -24F, 0F);
		bodyModel[39].rotateAngleY = 0.78539816F;
		bodyModel[39].rotateAngleZ = 1.04719755F;

		bodyModel[40].addTrapezoid(6F, -0.5F, -0.5F, 3, 1, 1, 0F, -0.40F, ModelRendererTurbo.MR_LEFT); // Box 31
		bodyModel[40].setRotationPoint(0F, -24F, 0F);
		bodyModel[40].rotateAngleY = 0.78539816F;
		bodyModel[40].rotateAngleZ = 1.04719755F;

		bodyModel[41].addTrapezoid(5F, -0.5F, -0.5F, 3, 1, 1, 0F, -0.40F, ModelRendererTurbo.MR_LEFT); // Box 32
		bodyModel[41].setRotationPoint(0F, -26F, 0F);
		bodyModel[41].rotateAngleY = 0.78539816F;
		bodyModel[41].rotateAngleZ = 0.95993109F;

		bodyModel[42].addTrapezoid(2F, -1F, -1F, 3, 2, 2, 0F, -0.50F, ModelRendererTurbo.MR_LEFT); // Box 33
		bodyModel[42].setRotationPoint(0F, -26F, 0F);
		bodyModel[42].rotateAngleY = 0.78539816F;
		bodyModel[42].rotateAngleZ = 0.95993109F;

		bodyModel[43].addTrapezoid(3F, 3.5F, -1.5F, 1, 1, 1, 0F, 0F, ModelRendererTurbo.MR_TOP); // Box 35
		bodyModel[43].setRotationPoint(0F, -17F, 0F);
		bodyModel[43].rotateAngleY = 2.35619449F;

		bodyModel[44].addTrapezoid(3F, 3.5F, -1.5F, 1, 1, 1, 0F, 0F, ModelRendererTurbo.MR_TOP); // Box 36
		bodyModel[44].setRotationPoint(0F, -19F, 0F);
		bodyModel[44].rotateAngleY = 2.35619449F;

		bodyModel[45].addTrapezoid(3F, 3.5F, 0.5F, 1, 1, 1, 0F, 0F, ModelRendererTurbo.MR_TOP); // Box 39
		bodyModel[45].setRotationPoint(0F, -17F, 0F);
		bodyModel[45].rotateAngleY = 2.35619449F;

		bodyModel[46].addTrapezoid(3F, 3.5F, 0.5F, 1, 1, 1, 0F, 0F, ModelRendererTurbo.MR_TOP); // Box 40
		bodyModel[46].setRotationPoint(0F, -19F, 0F);
		bodyModel[46].rotateAngleY = 2.35619449F;

		bodyModel[47].addShapeBox(2F, 3.5F, -3.5F, 2, 1, 2, 0F, 0.5F, 0F, 0.5F, 0F, 0F, -2F, -1F, 0F, 0F, 0.5F, 0F, -1.5F, 0.5F, 0F, 0.5F, 0F, 0F, -2F, -1F, 0F, 0F, 0.5F, 0F, -1.5F); // Box 41
		bodyModel[47].setRotationPoint(0F, -17F, 0F);
		bodyModel[47].rotateAngleY = 3.92699082F;

		bodyModel[48].addShapeBox(2F, 3.5F, -3.5F, 2, 1, 2, 0F, 0.5F, 0F, 0.5F, 0F, 0F, -2F, -1F, 0F, 0F, 0.5F, 0F, -1.5F, 0.5F, 0F, 0.5F, 0F, 0F, -2F, -1F, 0F, 0F, 0.5F, 0F, -1.5F); // Box 42
		bodyModel[48].setRotationPoint(0F, -17F, 0F);
		bodyModel[48].rotateAngleY = 2.35619449F;

		bodyModel[49].addShapeBox(2F, 3.5F, -3.5F, 2, 1, 2, 0F, 0.5F, 0F, 0.5F, 0F, 0F, -2F, -1F, 0F, 0F, 0.5F, 0F, -1.5F, 0.5F, 0F, 0.5F, 0F, 0F, -2F, -1F, 0F, 0F, 0.5F, 0F, -1.5F); // Box 43
		bodyModel[49].setRotationPoint(0F, -19F, 0F);
		bodyModel[49].rotateAngleY = 2.35619449F;

		bodyModel[50].addTrapezoid(2F, -1F, -1F, 3, 2, 2, 0F, -0.50F, ModelRendererTurbo.MR_LEFT); // Box 44
		bodyModel[50].setRotationPoint(0F, -26F, 0F);
		bodyModel[50].rotateAngleY = -0.78539816F;
		bodyModel[50].rotateAngleZ = 0.95993109F;

		bodyModel[51].addTrapezoid(5F, -0.5F, -0.5F, 3, 1, 1, 0F, -0.40F, ModelRendererTurbo.MR_LEFT); // Box 45
		bodyModel[51].setRotationPoint(0F, -26F, 0F);
		bodyModel[51].rotateAngleY = -0.78539816F;
		bodyModel[51].rotateAngleZ = 0.95993109F;

		bodyModel[52].addTrapezoid(3F, -1F, -1F, 3, 2, 2, 0F, -0.50F, ModelRendererTurbo.MR_LEFT); // Box 46
		bodyModel[52].setRotationPoint(0F, -24F, 0F);
		bodyModel[52].rotateAngleY = -0.78539816F;
		bodyModel[52].rotateAngleZ = 1.04719755F;

		bodyModel[53].addTrapezoid(6F, -0.5F, -0.5F, 3, 1, 1, 0F, -0.40F, ModelRendererTurbo.MR_LEFT); // Box 47
		bodyModel[53].setRotationPoint(0F, -24F, 0F);
		bodyModel[53].rotateAngleY = -0.78539816F;
		bodyModel[53].rotateAngleZ = 1.04719755F;

		bodyModel[54].addTrapezoid(3F, 3.5F, 0.5F, 1, 1, 1, 0F, 0F, ModelRendererTurbo.MR_TOP); // Box 48
		bodyModel[54].setRotationPoint(0F, -19F, 0F);
		bodyModel[54].rotateAngleY = 0.78539816F;

		bodyModel[55].addTrapezoid(3F, 3.5F, 0.5F, 1, 1, 1, 0F, 0F, ModelRendererTurbo.MR_TOP); // Box 49
		bodyModel[55].setRotationPoint(0F, -17F, 0F);
		bodyModel[55].rotateAngleY = 0.78539816F;

		bodyModel[56].addTrapezoid(3F, 3.5F, -1.5F, 1, 1, 1, 0F, 0F, ModelRendererTurbo.MR_TOP); // Box 50
		bodyModel[56].setRotationPoint(0F, -17F, 0F);
		bodyModel[56].rotateAngleY = 0.78539816F;

		bodyModel[57].addTrapezoid(3F, 3.5F, -1.5F, 1, 1, 1, 0F, 0F, ModelRendererTurbo.MR_TOP); // Box 51
		bodyModel[57].setRotationPoint(0F, -19F, 0F);
		bodyModel[57].rotateAngleY = 0.78539816F;

		bodyModel[58].addShapeBox(2F, 3.5F, -3.5F, 2, 1, 2, 0F, 0.5F, 0F, 0.5F, 0F, 0F, -2F, -1F, 0F, 0F, 0.5F, 0F, -1.5F, 0.5F, 0F, 0.5F, 0F, 0F, -2F, -1F, 0F, 0F, 0.5F, 0F, -1.5F); // Box 52
		bodyModel[58].setRotationPoint(0F, -19F, 0F);
		bodyModel[58].rotateAngleY = 0.78539816F;

		bodyModel[59].addShapeBox(2F, 3.5F, -3.5F, 2, 1, 2, 0F, 0.5F, 0F, 0.5F, 0F, 0F, -2F, -1F, 0F, 0F, 0.5F, 0F, -1.5F, 0.5F, 0F, 0.5F, 0F, 0F, -2F, -1F, 0F, 0F, 0.5F, 0F, -1.5F); // Box 53
		bodyModel[59].setRotationPoint(0F, -17F, 0F);
		bodyModel[59].rotateAngleY = 0.78539816F;

		bodyModel[60].addShapeBox(3F, 3.5F, -0.5F, 1, 5, 1, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 54
		bodyModel[60].setRotationPoint(0F, -21F, 0F);
		bodyModel[60].rotateAngleY = 0.78539816F;

		bodyModel[61].addShapeBox(3F, 3.5F, -0.5F, 1, 4, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 2F, 0F, 0F); // Box 55
		bodyModel[61].setRotationPoint(0F, -16F, 0F);
		bodyModel[61].rotateAngleY = 0.78539816F;

		bodyModel[62].addTrapezoid(2F, -1F, -1F, 3, 2, 2, 0F, -0.50F, ModelRendererTurbo.MR_LEFT); // Box 56
		bodyModel[62].setRotationPoint(0F, -26F, 0F);
		bodyModel[62].rotateAngleY = 3.92699082F;
		bodyModel[62].rotateAngleZ = 0.95993109F;

		bodyModel[63].addTrapezoid(5F, -0.5F, -0.5F, 3, 1, 1, 0F, -0.40F, ModelRendererTurbo.MR_LEFT); // Box 57
		bodyModel[63].setRotationPoint(0F, -26F, 0F);
		bodyModel[63].rotateAngleY = 3.92699082F;
		bodyModel[63].rotateAngleZ = 0.95993109F;

		bodyModel[64].addTrapezoid(3F, -1F, -1F, 3, 2, 2, 0F, -0.50F, ModelRendererTurbo.MR_LEFT); // Box 58
		bodyModel[64].setRotationPoint(0F, -24F, 0F);
		bodyModel[64].rotateAngleY = 3.92699082F;
		bodyModel[64].rotateAngleZ = 1.04719755F;

		bodyModel[65].addTrapezoid(6F, -0.5F, -0.5F, 3, 1, 1, 0F, -0.40F, ModelRendererTurbo.MR_LEFT); // Box 59
		bodyModel[65].setRotationPoint(0F, -24F, 0F);
		bodyModel[65].rotateAngleY = 3.92699082F;
		bodyModel[65].rotateAngleZ = 1.04719755F;

		bodyModel[66].addTrapezoid(3F, 3.5F, 0.5F, 1, 1, 1, 0F, 0F, ModelRendererTurbo.MR_TOP); // Box 60
		bodyModel[66].setRotationPoint(0F, -19F, 0F);
		bodyModel[66].rotateAngleY = 5.49778714F;

		bodyModel[67].addTrapezoid(3F, 3.5F, 0.5F, 1, 1, 1, 0F, 0F, ModelRendererTurbo.MR_TOP); // Box 61
		bodyModel[67].setRotationPoint(0F, -17F, 0F);
		bodyModel[67].rotateAngleY = 5.49778714F;

		bodyModel[68].addTrapezoid(3F, 3.5F, -1.5F, 1, 1, 1, 0F, 0F, ModelRendererTurbo.MR_TOP); // Box 62
		bodyModel[68].setRotationPoint(0F, -17F, 0F);
		bodyModel[68].rotateAngleY = 5.49778714F;

		bodyModel[69].addTrapezoid(3F, 3.5F, -1.5F, 1, 1, 1, 0F, 0F, ModelRendererTurbo.MR_TOP); // Box 63
		bodyModel[69].setRotationPoint(0F, -19F, 0F);
		bodyModel[69].rotateAngleY = 5.49778714F;

		bodyModel[70].addShapeBox(2F, 3.5F, -3.5F, 2, 1, 2, 0F, 0.5F, 0F, 0.5F, 0F, 0F, -2F, -1F, 0F, 0F, 0.5F, 0F, -1.5F, 0.5F, 0F, 0.5F, 0F, 0F, -2F, -1F, 0F, 0F, 0.5F, 0F, -1.5F); // Box 64
		bodyModel[70].setRotationPoint(0F, -19F, 0F);
		bodyModel[70].rotateAngleY = 5.49778714F;

		bodyModel[71].addShapeBox(2F, 3.5F, -3.5F, 2, 1, 2, 0F, 0.5F, 0F, 0.5F, 0F, 0F, -2F, -1F, 0F, 0F, 0.5F, 0F, -1.5F, 0.5F, 0F, 0.5F, 0F, 0F, -2F, -1F, 0F, 0F, 0.5F, 0F, -1.5F); // Box 65
		bodyModel[71].setRotationPoint(0F, -17F, 0F);
		bodyModel[71].rotateAngleY = 5.49778714F;

		bodyModel[72].addShapeBox(3F, 3.5F, -0.5F, 1, 4, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 2F, 0F, 0F); // Box 67
		bodyModel[72].setRotationPoint(0F, -16F, 0F);
		bodyModel[72].rotateAngleY = 5.49778714F;

		bodyModel[73].addBox(-3.5F, -1F, -1.5F, 7, 6, 3, 0F); // Box 68
		bodyModel[73].setRotationPoint(0F, -2F, 0F);
		bodyModel[73].rotateAngleY = 3.92699082F;

		bodyModel[74].addShapeBox(-3.5F, -1F, -3.5F, 7, 6, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 69
		bodyModel[74].setRotationPoint(0F, -2F, 0F);
		bodyModel[74].rotateAngleY = 3.92699082F;

		bodyModel[75].addShapeBox(-3.5F, -1F, 1.5F, 7, 6, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Box 70
		bodyModel[75].setRotationPoint(0F, -2F, 0F);
		bodyModel[75].rotateAngleY = 3.92699082F;

		bodyModel[76].addShapeBox(-3.5F, -1F, 1.5F, 7, 2, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Box 71
		bodyModel[76].setRotationPoint(0F, 5F, 0F);
		bodyModel[76].rotateAngleY = 3.92699082F;

		bodyModel[77].addBox(-3.5F, -1F, -1.5F, 7, 2, 3, 0F); // Box 72
		bodyModel[77].setRotationPoint(0F, 5F, 0F);
		bodyModel[77].rotateAngleY = 3.92699082F;

		bodyModel[78].addShapeBox(-3.5F, -1F, -3.5F, 7, 2, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 73
		bodyModel[78].setRotationPoint(0F, 5F, 0F);
		bodyModel[78].rotateAngleY = 3.92699082F;

		bodyModel[79].addShapeBox(-3.5F, -1F, 1.5F, 7, 1, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Box 74
		bodyModel[79].setRotationPoint(0F, 8F, 0F);
		bodyModel[79].rotateAngleY = 3.92699082F;

		bodyModel[80].addBox(-3.5F, -1F, -1.5F, 7, 1, 3, 0F); // Box 75
		bodyModel[80].setRotationPoint(0F, 8F, 0F);
		bodyModel[80].rotateAngleY = 3.92699082F;

		bodyModel[81].addShapeBox(-3.5F, -1F, -3.5F, 7, 1, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 76
		bodyModel[81].setRotationPoint(0F, 8F, 0F);
		bodyModel[81].rotateAngleY = 3.92699082F;

		bodyModel[82].addShapeBox(-9.5F, -1F, -0.5F, 6, 8, 1, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 77
		bodyModel[82].setRotationPoint(0F, -2F, 0F);
		bodyModel[82].rotateAngleY = 2.35619449F;

		bodyModel[83].addShapeBox(-9.5F, -1F, -0.5F, 6, 3, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F); // Box 78
		bodyModel[83].setRotationPoint(0F, 6F, 0F);
		bodyModel[83].rotateAngleY = 2.35619449F;

		bodyModel[84].addShapeBox(-9.5F, -1F, -0.5F, 6, 8, 1, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 79
		bodyModel[84].setRotationPoint(0F, -2F, 0F);
		bodyModel[84].rotateAngleY = 0.78539816F;

		bodyModel[85].addShapeBox(-9.5F, -1F, -0.5F, 6, 3, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F); // Box 80
		bodyModel[85].setRotationPoint(0F, 6F, 0F);
		bodyModel[85].rotateAngleY = 0.78539816F;

		bodyModel[86].addShapeBox(-9.5F, -1F, -0.5F, 6, 8, 1, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 81
		bodyModel[86].setRotationPoint(0F, -2F, 0F);
		bodyModel[86].rotateAngleY = 3.92699082F;

		bodyModel[87].addShapeBox(-9.5F, -1F, -0.5F, 6, 3, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F); // Box 82
		bodyModel[87].setRotationPoint(0F, 6F, 0F);
		bodyModel[87].rotateAngleY = 3.92699082F;

		bodyModel[88].addShapeBox(-9.5F, -1F, -0.5F, 6, 8, 1, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 83
		bodyModel[88].setRotationPoint(0F, -2F, 0F);
		bodyModel[88].rotateAngleY = 5.49778714F;

		bodyModel[89].addShapeBox(-9.5F, -1F, -0.5F, 6, 3, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F); // Box 84
		bodyModel[89].setRotationPoint(0F, 6F, 0F);
		bodyModel[89].rotateAngleY = 5.49778714F;

		bodyModel[90].addShapeBox(3F, 3.5F, -0.5F, 1, 5, 1, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 85
		bodyModel[90].setRotationPoint(0F, -21F, 0F);
		bodyModel[90].rotateAngleY = 2.35619449F;

		bodyModel[91].addShapeBox(3F, 3.5F, -0.5F, 1, 5, 1, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 86
		bodyModel[91].setRotationPoint(0F, -21F, 0F);
		bodyModel[91].rotateAngleY = 3.92699082F;

		bodyModel[92].addShapeBox(3F, 3.5F, -0.5F, 1, 5, 1, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 87
		bodyModel[92].setRotationPoint(0F, -21F, 0F);
		bodyModel[92].rotateAngleY = 5.49778714F;
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
