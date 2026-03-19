package defense.client.model.missile;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.tmt.ModelRendererTurbo;

@SideOnly(Side.CLIENT)
public class ModelIncendiaryMissile extends ModelMissileBase
{
    // fields
    ModelRendererTurbo[] bodyModel;

    public ModelIncendiaryMissile()
    {
		int textureX = 64;
		int textureY = 256;
		
		bodyModel = new ModelRendererTurbo[72];
		bodyModel[0] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Import ImportBox0
		bodyModel[1] = new ModelRendererTurbo(this, 0, 36, textureX, textureY); // Import ImportBox1
		bodyModel[2] = new ModelRendererTurbo(this, 0, 36, textureX, textureY); // Import ImportBox2
		bodyModel[3] = new ModelRendererTurbo(this, 0, 67, textureX, textureY); // Import ImportBox5
		bodyModel[4] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Import ImportBox6
		bodyModel[5] = new ModelRendererTurbo(this, 0, 67, textureX, textureY); // Import ImportBox7
		bodyModel[6] = new ModelRendererTurbo(this, 0, 92, textureX, textureY); // Import ImportBox8
		bodyModel[7] = new ModelRendererTurbo(this, 0, 102, textureX, textureY); // Import ImportBox9
		bodyModel[8] = new ModelRendererTurbo(this, 0, 102, textureX, textureY); // Import ImportBox10
		bodyModel[9] = new ModelRendererTurbo(this, 0, 110, textureX, textureY); // Import ImportBox11
		bodyModel[10] = new ModelRendererTurbo(this, 0, 110, textureX, textureY); // Import ImportBox12
		bodyModel[11] = new ModelRendererTurbo(this, 0, 67, textureX, textureY); // Import ImportBox13
		bodyModel[12] = new ModelRendererTurbo(this, 0, 92, textureX, textureY); // Import ImportBox14
		bodyModel[13] = new ModelRendererTurbo(this, 0, 67, textureX, textureY); // Import ImportBox15
		bodyModel[14] = new ModelRendererTurbo(this, 0, 102, textureX, textureY); // Import ImportBox16
		bodyModel[15] = new ModelRendererTurbo(this, 0, 102, textureX, textureY); // Import ImportBox17
		bodyModel[16] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Import ImportBox18
		bodyModel[17] = new ModelRendererTurbo(this, 0, 110, textureX, textureY); // Import ImportBox19
		bodyModel[18] = new ModelRendererTurbo(this, 0, 67, textureX, textureY); // Import ImportBox20
		bodyModel[19] = new ModelRendererTurbo(this, 0, 92, textureX, textureY); // Import ImportBox21
		bodyModel[20] = new ModelRendererTurbo(this, 0, 67, textureX, textureY); // Import ImportBox22
		bodyModel[21] = new ModelRendererTurbo(this, 0, 102, textureX, textureY); // Import ImportBox23
		bodyModel[22] = new ModelRendererTurbo(this, 0, 102, textureX, textureY); // Import ImportBox24
		bodyModel[23] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Import ImportBox25
		bodyModel[24] = new ModelRendererTurbo(this, 0, 110, textureX, textureY); // Import ImportBox26
		bodyModel[25] = new ModelRendererTurbo(this, 0, 67, textureX, textureY); // Import ImportBox27
		bodyModel[26] = new ModelRendererTurbo(this, 0, 92, textureX, textureY); // Import ImportBox28
		bodyModel[27] = new ModelRendererTurbo(this, 0, 67, textureX, textureY); // Import ImportBox29
		bodyModel[28] = new ModelRendererTurbo(this, 0, 102, textureX, textureY); // Import ImportBox30
		bodyModel[29] = new ModelRendererTurbo(this, 0, 102, textureX, textureY); // Import ImportBox31
		bodyModel[30] = new ModelRendererTurbo(this, 0, 80, textureX, textureY); // Import ImportBox32
		bodyModel[31] = new ModelRendererTurbo(this, 0, 115, textureX, textureY); // Import ImportBox33
		bodyModel[32] = new ModelRendererTurbo(this, 0, 119, textureX, textureY); // Import ImportBox34
		bodyModel[33] = new ModelRendererTurbo(this, 0, 123, textureX, textureY); // Import ImportBox36
		bodyModel[34] = new ModelRendererTurbo(this, 0, 123, textureX, textureY); // Import ImportBox37
		bodyModel[35] = new ModelRendererTurbo(this, 0, 115, textureX, textureY); // Import ImportBox38
		bodyModel[36] = new ModelRendererTurbo(this, 0, 119, textureX, textureY); // Import ImportBox39
		bodyModel[37] = new ModelRendererTurbo(this, 0, 123, textureX, textureY); // Import ImportBox40
		bodyModel[38] = new ModelRendererTurbo(this, 0, 115, textureX, textureY); // Import ImportBox41
		bodyModel[39] = new ModelRendererTurbo(this, 0, 119, textureX, textureY); // Import ImportBox42
		bodyModel[40] = new ModelRendererTurbo(this, 0, 123, textureX, textureY); // Import ImportBox43
		bodyModel[41] = new ModelRendererTurbo(this, 0, 115, textureX, textureY); // Import ImportBox44
		bodyModel[42] = new ModelRendererTurbo(this, 0, 119, textureX, textureY); // Import ImportBox45
		bodyModel[43] = new ModelRendererTurbo(this, 0, 127, textureX, textureY); // Import ImportBox46
		bodyModel[44] = new ModelRendererTurbo(this, 0, 135, textureX, textureY); // Import ImportBox47
		bodyModel[45] = new ModelRendererTurbo(this, 0, 135, textureX, textureY); // Import ImportBox48
		bodyModel[46] = new ModelRendererTurbo(this, 0, 142, textureX, textureY); // Import ImportBox49
		bodyModel[47] = new ModelRendererTurbo(this, 0, 142, textureX, textureY); // Import ImportBox50
		bodyModel[48] = new ModelRendererTurbo(this, 0, 142, textureX, textureY); // Import ImportBox51
		bodyModel[49] = new ModelRendererTurbo(this, 0, 142, textureX, textureY); // Import ImportBox52
		bodyModel[50] = new ModelRendererTurbo(this, 0, 152, textureX, textureY); // Import ImportBox53
		bodyModel[51] = new ModelRendererTurbo(this, 0, 171, textureX, textureY); // Import ImportBox54
		bodyModel[52] = new ModelRendererTurbo(this, 0, 171, textureX, textureY); // Import ImportBox55
		bodyModel[53] = new ModelRendererTurbo(this, 0, 187, textureX, textureY); // Import ImportBox56
		bodyModel[54] = new ModelRendererTurbo(this, 0, 197, textureX, textureY); // Import ImportBox57
		bodyModel[55] = new ModelRendererTurbo(this, 0, 207, textureX, textureY); // Import ImportBox58
		bodyModel[56] = new ModelRendererTurbo(this, 0, 187, textureX, textureY); // Import ImportBox59
		bodyModel[57] = new ModelRendererTurbo(this, 0, 197, textureX, textureY); // Import ImportBox60
		bodyModel[58] = new ModelRendererTurbo(this, 0, 207, textureX, textureY); // Import ImportBox61
		bodyModel[59] = new ModelRendererTurbo(this, 0, 187, textureX, textureY); // Import ImportBox62
		bodyModel[60] = new ModelRendererTurbo(this, 0, 197, textureX, textureY); // Import ImportBox63
		bodyModel[61] = new ModelRendererTurbo(this, 0, 207, textureX, textureY); // Import ImportBox64
		bodyModel[62] = new ModelRendererTurbo(this, 0, 187, textureX, textureY); // Import ImportBox65
		bodyModel[63] = new ModelRendererTurbo(this, 0, 197, textureX, textureY); // Import ImportBox66
		bodyModel[64] = new ModelRendererTurbo(this, 0, 207, textureX, textureY); // Import ImportBox67
		bodyModel[65] = new ModelRendererTurbo(this, 0, 214, textureX, textureY); // Import ImportBox68
		bodyModel[66] = new ModelRendererTurbo(this, 0, 225, textureX, textureY); // Import ImportBox70
		bodyModel[67] = new ModelRendererTurbo(this, 0, 225, textureX, textureY); // Import ImportBox71
		bodyModel[68] = new ModelRendererTurbo(this, 0, 234, textureX, textureY); // Import ImportBox72
		bodyModel[69] = new ModelRendererTurbo(this, 0, 234, textureX, textureY); // Import ImportBox73
		bodyModel[70] = new ModelRendererTurbo(this, 0, 234, textureX, textureY); // Import ImportBox74
		bodyModel[71] = new ModelRendererTurbo(this, 0, 242, textureX, textureY); // Import ImportBox75

		bodyModel[0].addBox(-4F, -8F, -3F, 8, 29, 6, 0F); // Import ImportBox0
		bodyModel[0].setRotationPoint(0F, -2F, 0F);
		bodyModel[0].rotateAngleY = -0.78539816F;

		bodyModel[1].addShapeBox(-4F, -8F, -4F, 8, 29, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox1
		bodyModel[1].setRotationPoint(0F, -2F, 0F);
		bodyModel[1].rotateAngleY = -0.78539816F;

		bodyModel[2].addShapeBox(-4F, -8F, 3F, 8, 29, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Import ImportBox2
		bodyModel[2].setRotationPoint(0F, -2F, 0F);
		bodyModel[2].rotateAngleY = -0.78539816F;

		bodyModel[3].addShapeBox(1F, 16F, 4F, 1, 8, 4, 0F, 0F, 4F, 0F, 0F, 4F, 0F, 0F, 1F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F); // Import ImportBox5
		bodyModel[3].setRotationPoint(0F, -2F, 0F);
		bodyModel[3].rotateAngleY = -0.78539816F;

		bodyModel[4].addShapeBox(-1F, 16F, 4F, 2, 8, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox6
		bodyModel[4].setRotationPoint(0F, -2F, 0F);
		bodyModel[4].rotateAngleY = -0.78539816F;

		bodyModel[5].addShapeBox(-2F, 16F, 4F, 1, 8, 4, 0F, 0F, 4F, 0F, 0F, 4F, 0F, 0F, 0F, 0F, 0F, 1F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Import ImportBox7
		bodyModel[5].setRotationPoint(0F, -2F, 0F);
		bodyModel[5].rotateAngleY = -0.78539816F;

		bodyModel[6].addShapeBox(-1F, 16F, 7F, 2, 8, 1, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox8
		bodyModel[6].setRotationPoint(0F, -2F, 0F);
		bodyModel[6].rotateAngleY = -0.78539816F;

		bodyModel[7].addShapeBox(-1F, 16F, 4F, 1, 4, 3, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, -3F, 0F, -0.4F, -3F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F); // Import ImportBox9
		bodyModel[7].setRotationPoint(0F, -6F, 0F);
		bodyModel[7].rotateAngleY = -0.78539816F;

		bodyModel[8].addShapeBox(0F, 16F, 4F, 1, 4, 3, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, -3F, 0F, -0.4F, -3F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F); // Import ImportBox10
		bodyModel[8].setRotationPoint(0F, -6F, 0F);
		bodyModel[8].rotateAngleY = -0.78539816F;

		bodyModel[9].addShapeBox(-2F, 21F, 3F, 4, 3, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox11
		bodyModel[9].setRotationPoint(0F, -2F, 0F);
		bodyModel[9].rotateAngleY = -0.78539816F;

		bodyModel[10].addShapeBox(-2F, 21F, 3F, 4, 3, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox12
		bodyModel[10].setRotationPoint(0F, -2F, 0F);
		bodyModel[10].rotateAngleY = -2.35619449F;

		bodyModel[11].addShapeBox(-2F, 16F, 4F, 1, 8, 4, 0F, 0F, 4F, 0F, 0F, 4F, 0F, 0F, 0F, 0F, 0F, 1F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Import ImportBox13
		bodyModel[11].setRotationPoint(0F, -2F, 0F);
		bodyModel[11].rotateAngleY = -2.35619449F;

		bodyModel[12].addShapeBox(-1F, 16F, 7F, 2, 8, 1, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox14
		bodyModel[12].setRotationPoint(0F, -2F, 0F);
		bodyModel[12].rotateAngleY = -2.35619449F;

		bodyModel[13].addShapeBox(1F, 16F, 4F, 1, 8, 4, 0F, 0F, 4F, 0F, 0F, 4F, 0F, 0F, 1F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F); // Import ImportBox15
		bodyModel[13].setRotationPoint(0F, -2F, 0F);
		bodyModel[13].rotateAngleY = -2.35619449F;

		bodyModel[14].addShapeBox(-1F, 16F, 4F, 1, 4, 3, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, -3F, 0F, -0.4F, -3F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F); // Import ImportBox16
		bodyModel[14].setRotationPoint(0F, -6F, 0F);
		bodyModel[14].rotateAngleY = -2.35619449F;

		bodyModel[15].addShapeBox(0F, 16F, 4F, 1, 4, 3, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, -3F, 0F, -0.4F, -3F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F); // Import ImportBox17
		bodyModel[15].setRotationPoint(0F, -6F, 0F);
		bodyModel[15].rotateAngleY = -2.35619449F;

		bodyModel[16].addShapeBox(-1F, 16F, 4F, 2, 8, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox18
		bodyModel[16].setRotationPoint(0F, -2F, 0F);
		bodyModel[16].rotateAngleY = -2.35619449F;

		bodyModel[17].addShapeBox(-2F, 21F, 3F, 4, 3, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox19
		bodyModel[17].setRotationPoint(0F, -2F, 0F);
		bodyModel[17].rotateAngleY = -3.92699081F;

		bodyModel[18].addShapeBox(-2F, 16F, 4F, 1, 8, 4, 0F, 0F, 4F, 0F, 0F, 4F, 0F, 0F, 0F, 0F, 0F, 1F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Import ImportBox20
		bodyModel[18].setRotationPoint(0F, -2F, 0F);
		bodyModel[18].rotateAngleY = -3.92699081F;

		bodyModel[19].addShapeBox(-1F, 16F, 7F, 2, 8, 1, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox21
		bodyModel[19].setRotationPoint(0F, -2F, 0F);
		bodyModel[19].rotateAngleY = -3.92699081F;

		bodyModel[20].addShapeBox(1F, 16F, 4F, 1, 8, 4, 0F, 0F, 4F, 0F, 0F, 4F, 0F, 0F, 1F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F); // Import ImportBox22
		bodyModel[20].setRotationPoint(0F, -2F, 0F);
		bodyModel[20].rotateAngleY = -3.92699081F;

		bodyModel[21].addShapeBox(-1F, 16F, 4F, 1, 4, 3, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, -3F, 0F, -0.4F, -3F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F); // Import ImportBox23
		bodyModel[21].setRotationPoint(0F, -6F, 0F);
		bodyModel[21].rotateAngleY = -3.92699081F;

		bodyModel[22].addShapeBox(0F, 16F, 4F, 1, 4, 3, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, -3F, 0F, -0.4F, -3F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F); // Import ImportBox24
		bodyModel[22].setRotationPoint(0F, -6F, 0F);
		bodyModel[22].rotateAngleY = -3.92699081F;

		bodyModel[23].addShapeBox(-1F, 16F, 4F, 2, 8, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox25
		bodyModel[23].setRotationPoint(0F, -2F, 0F);
		bodyModel[23].rotateAngleY = -3.92699081F;

		bodyModel[24].addShapeBox(-2F, 21F, 3F, 4, 3, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox26
		bodyModel[24].setRotationPoint(0F, -2F, 0F);
		bodyModel[24].rotateAngleY = -5.49778714F;

		bodyModel[25].addShapeBox(-2F, 16F, 4F, 1, 8, 4, 0F, 0F, 4F, 0F, 0F, 4F, 0F, 0F, 0F, 0F, 0F, 1F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Import ImportBox27
		bodyModel[25].setRotationPoint(0F, -2F, 0F);
		bodyModel[25].rotateAngleY = -5.49778714F;

		bodyModel[26].addShapeBox(-1F, 16F, 7F, 2, 8, 1, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox28
		bodyModel[26].setRotationPoint(0F, -2F, 0F);
		bodyModel[26].rotateAngleY = -5.49778714F;

		bodyModel[27].addShapeBox(1F, 16F, 4F, 1, 8, 4, 0F, 0F, 4F, 0F, 0F, 4F, 0F, 0F, 1F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F); // Import ImportBox29
		bodyModel[27].setRotationPoint(0F, -2F, 0F);
		bodyModel[27].rotateAngleY = -5.49778714F;

		bodyModel[28].addShapeBox(-1F, 16F, 4F, 1, 4, 3, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, -3F, 0F, -0.4F, -3F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F); // Import ImportBox30
		bodyModel[28].setRotationPoint(0F, -6F, 0F);
		bodyModel[28].rotateAngleY = -5.49778714F;

		bodyModel[29].addShapeBox(0F, 16F, 4F, 1, 4, 3, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, -3F, 0F, -0.4F, -3F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F); // Import ImportBox31
		bodyModel[29].setRotationPoint(0F, -6F, 0F);
		bodyModel[29].rotateAngleY = -5.49778714F;

		bodyModel[30].addShapeBox(-1F, 16F, 4F, 2, 8, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox32
		bodyModel[30].setRotationPoint(0F, -2F, 0F);
		bodyModel[30].rotateAngleY = -5.49778714F;

		bodyModel[31].addShapeBox(-1.5F, 24F, 5F, 3, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0.5F, 0F, 0.5F, 0.5F, 0F, 0.5F, 0.5F, 0F, 0.5F, 0.5F, 0F, 0.5F); // Import ImportBox33
		bodyModel[31].setRotationPoint(0F, -2F, 0F);
		bodyModel[31].rotateAngleY = -0.78539816F;

		bodyModel[32].addShapeBox(-1.5F, 24F, 4F, 3, 2, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F, 0.5F, 0F, -0.5F, 0.5F, 0F, -0.5F); // Import ImportBox34
		bodyModel[32].setRotationPoint(0F, -2F, 0F);
		bodyModel[32].rotateAngleY = -0.78539816F;

		bodyModel[33].addShapeBox(-1.5F, 24F, 6F, 3, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0.5F, 0F, -0.5F, 0.5F, 0F, -0.5F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F); // Import ImportBox36
		bodyModel[33].setRotationPoint(0F, -2F, 0F);
		bodyModel[33].rotateAngleY = -0.78539816F;

		bodyModel[34].addShapeBox(-1.5F, 24F, 6F, 3, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0.5F, 0F, -0.5F, 0.5F, 0F, -0.5F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F); // Import ImportBox37
		bodyModel[34].setRotationPoint(0F, -2F, 0F);
		bodyModel[34].rotateAngleY = -2.35619449F;

		bodyModel[35].addShapeBox(-1.5F, 24F, 5F, 3, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0.5F, 0F, 0.5F, 0.5F, 0F, 0.5F, 0.5F, 0F, 0.5F, 0.5F, 0F, 0.5F); // Import ImportBox38
		bodyModel[35].setRotationPoint(0F, -2F, 0F);
		bodyModel[35].rotateAngleY = -2.35619449F;

		bodyModel[36].addShapeBox(-1.5F, 24F, 4F, 3, 2, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F, 0.5F, 0F, -0.5F, 0.5F, 0F, -0.5F); // Import ImportBox39
		bodyModel[36].setRotationPoint(0F, -2F, 0F);
		bodyModel[36].rotateAngleY = -2.35619449F;

		bodyModel[37].addShapeBox(-1.5F, 24F, 6F, 3, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0.5F, 0F, -0.5F, 0.5F, 0F, -0.5F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F); // Import ImportBox40
		bodyModel[37].setRotationPoint(0F, -2F, 0F);
		bodyModel[37].rotateAngleY = -3.92699081F;

		bodyModel[38].addShapeBox(-1.5F, 24F, 5F, 3, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0.5F, 0F, 0.5F, 0.5F, 0F, 0.5F, 0.5F, 0F, 0.5F, 0.5F, 0F, 0.5F); // Import ImportBox41
		bodyModel[38].setRotationPoint(0F, -2F, 0F);
		bodyModel[38].rotateAngleY = -3.92699081F;

		bodyModel[39].addShapeBox(-1.5F, 24F, 4F, 3, 2, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F, 0.5F, 0F, -0.5F, 0.5F, 0F, -0.5F); // Import ImportBox42
		bodyModel[39].setRotationPoint(0F, -2F, 0F);
		bodyModel[39].rotateAngleY = -3.92699081F;

		bodyModel[40].addShapeBox(-1.5F, 24F, 6F, 3, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0.5F, 0F, -0.5F, 0.5F, 0F, -0.5F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F); // Import ImportBox43
		bodyModel[40].setRotationPoint(0F, -2F, 0F);
		bodyModel[40].rotateAngleY = -5.49778714F;

		bodyModel[41].addShapeBox(-1.5F, 24F, 5F, 3, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0.5F, 0F, 0.5F, 0.5F, 0F, 0.5F, 0.5F, 0F, 0.5F, 0.5F, 0F, 0.5F); // Import ImportBox44
		bodyModel[41].setRotationPoint(0F, -2F, 0F);
		bodyModel[41].rotateAngleY = -5.49778714F;

		bodyModel[42].addShapeBox(-1.5F, 24F, 4F, 3, 2, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F, 0.5F, 0F, -0.5F, 0.5F, 0F, -0.5F); // Import ImportBox45
		bodyModel[42].setRotationPoint(0F, -2F, 0F);
		bodyModel[42].rotateAngleY = -5.49778714F;

		bodyModel[43].addShapeBox(-2F, 21F, -1F, 4, 5, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0.5F, 0F, 0.5F, 0.5F, 0F, 0.5F, 0.5F, 0F, 0.5F, 0.5F, 0F, 0.5F); // Import ImportBox46
		bodyModel[43].setRotationPoint(0F, -2F, 0F);
		bodyModel[43].rotateAngleY = -0.78539816F;

		bodyModel[44].addShapeBox(-2F, 21F, -2F, 4, 5, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F, 0.5F, 0F, -0.5F, 0.5F, 0F, -0.5F); // Import ImportBox47
		bodyModel[44].setRotationPoint(0F, -2F, 0F);
		bodyModel[44].rotateAngleY = -0.78539816F;

		bodyModel[45].addShapeBox(-2F, 21F, 1F, 4, 5, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0.5F, 0F, -0.5F, 0.5F, 0F, -0.5F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0.5F); // Import ImportBox48
		bodyModel[45].setRotationPoint(0F, -2F, 0F);
		bodyModel[45].rotateAngleY = -0.78539816F;

		bodyModel[46].addShapeBox(-0.5F, 18F, 8F, 1, 6, 3, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, -4F, 0F, -0.2F, -4F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, -1F, 0F, -0.2F, -1F, 0F); // Import ImportBox49
		bodyModel[46].setRotationPoint(0F, -2F, 0F);
		bodyModel[46].rotateAngleY = -5.49778714F;

		bodyModel[47].addShapeBox(-0.5F, 18F, 8F, 1, 6, 3, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, -4F, 0F, -0.2F, -4F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, -1F, 0F, -0.2F, -1F, 0F); // Import ImportBox50
		bodyModel[47].setRotationPoint(0F, -2F, 0F);
		bodyModel[47].rotateAngleY = -3.92699081F;

		bodyModel[48].addShapeBox(-0.5F, 18F, 8F, 1, 6, 3, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, -4F, 0F, -0.2F, -4F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, -1F, 0F, -0.2F, -1F, 0F); // Import ImportBox51
		bodyModel[48].setRotationPoint(0F, -2F, 0F);
		bodyModel[48].rotateAngleY = -0.78539816F;

		bodyModel[49].addShapeBox(-0.5F, 18F, 8F, 1, 6, 3, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, -4F, 0F, -0.2F, -4F, 0F, -0.2F, 0F, 0F, -0.2F, 0F, 0F, -0.2F, -1F, 0F, -0.2F, -1F, 0F); // Import ImportBox52
		bodyModel[49].setRotationPoint(0F, -2F, 0F);
		bodyModel[49].rotateAngleY = -2.35619449F;

		bodyModel[50].addShapeBox(-4F, -20F, -3F, 8, 12, 6, 0F, -1F, 0F, -1F, -1F, 0F, -1F, -1F, 0F, -1F, -1F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox53
		bodyModel[50].setRotationPoint(0F, -2F, 0F);
		bodyModel[50].rotateAngleY = -0.78539816F;

		bodyModel[51].addShapeBox(-4F, -20F, -4F, 8, 12, 3, 0F, -2F, 0F, -1F, -2F, 0F, -1F, -1F, 0F, -1F, -1F, 0F, -1F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F); // Import ImportBox54
		bodyModel[51].setRotationPoint(0F, -2F, 0F);
		bodyModel[51].rotateAngleY = -0.78539816F;

		bodyModel[52].addShapeBox(-4F, -20F, 1F, 8, 12, 3, 0F, -1F, 0F, -1F, -1F, 0F, -1F, -2F, 0F, -1F, -2F, 0F, -1F, 0F, 0F, -2F, 0F, 0F, -2F, -1F, 0F, 0F, -1F, 0F, 0F); // Import ImportBox55
		bodyModel[52].setRotationPoint(0F, -2F, 0F);
		bodyModel[52].rotateAngleY = -0.78539816F;

		bodyModel[53].addShapeBox(-1F, -10F, 3F, 2, 7, 2, 0F, 0F, 1F, 0F, 0F, 1F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 2F, 0F, 0F, 2F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F); // Import ImportBox56
		bodyModel[53].setRotationPoint(0F, -2F, 0F);
		bodyModel[53].rotateAngleY = -0.78539816F;

		bodyModel[54].addShapeBox(-0.5F, -10F, 5F, 1, 7, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox57
		bodyModel[54].setRotationPoint(0F, -2F, 0F);
		bodyModel[54].rotateAngleY = -0.78539816F;

		bodyModel[55].addShapeBox(-0.5F, -8F, 7F, 1, 5, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox58
		bodyModel[55].setRotationPoint(0F, -2F, 0F);
		bodyModel[55].rotateAngleY = -0.78539816F;

		bodyModel[56].addShapeBox(-1F, -10F, 3F, 2, 7, 2, 0F, 0F, 1F, 0F, 0F, 1F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 2F, 0F, 0F, 2F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F); // Import ImportBox59
		bodyModel[56].setRotationPoint(0F, -2F, 0F);
		bodyModel[56].rotateAngleY = -2.35619449F;

		bodyModel[57].addShapeBox(-0.5F, -10F, 5F, 1, 7, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox60
		bodyModel[57].setRotationPoint(0F, -2F, 0F);
		bodyModel[57].rotateAngleY = -2.35619449F;

		bodyModel[58].addShapeBox(-0.5F, -8F, 7F, 1, 5, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox61
		bodyModel[58].setRotationPoint(0F, -2F, 0F);
		bodyModel[58].rotateAngleY = -2.35619449F;

		bodyModel[59].addShapeBox(-1F, -10F, 3F, 2, 7, 2, 0F, 0F, 1F, 0F, 0F, 1F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 2F, 0F, 0F, 2F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F); // Import ImportBox62
		bodyModel[59].setRotationPoint(0F, -2F, 0F);
		bodyModel[59].rotateAngleY = -5.49778714F;

		bodyModel[60].addShapeBox(-0.5F, -10F, 5F, 1, 7, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox63
		bodyModel[60].setRotationPoint(0F, -2F, 0F);
		bodyModel[60].rotateAngleY = -5.49778714F;

		bodyModel[61].addShapeBox(-0.5F, -8F, 7F, 1, 5, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox64
		bodyModel[61].setRotationPoint(0F, -2F, 0F);
		bodyModel[61].rotateAngleY = -5.49778714F;

		bodyModel[62].addShapeBox(-1F, -10F, 3F, 2, 7, 2, 0F, 0F, 1F, 0F, 0F, 1F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 2F, 0F, 0F, 2F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F); // Import ImportBox65
		bodyModel[62].setRotationPoint(0F, -2F, 0F);
		bodyModel[62].rotateAngleY = -3.92699081F;

		bodyModel[63].addShapeBox(-0.5F, -10F, 5F, 1, 7, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox66
		bodyModel[63].setRotationPoint(0F, -2F, 0F);
		bodyModel[63].rotateAngleY = -3.92699081F;

		bodyModel[64].addShapeBox(-0.5F, -8F, 7F, 1, 5, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox67
		bodyModel[64].setRotationPoint(0F, -2F, 0F);
		bodyModel[64].rotateAngleY = -3.92699081F;

		bodyModel[65].addShapeBox(-3F, -26F, -2F, 6, 6, 4, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox68
		bodyModel[65].setRotationPoint(0F, -2F, 0F);
		bodyModel[65].rotateAngleY = -0.78539816F;

		bodyModel[66].addShapeBox(-3F, -26F, -3F, 6, 6, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F); // Import ImportBox70
		bodyModel[66].setRotationPoint(0F, -2F, 0F);
		bodyModel[66].rotateAngleY = -0.78539816F;

		bodyModel[67].addShapeBox(-3F, -26F, 1F, 6, 6, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, -1F, 0F, 0F, -1F, 0F, 0F); // Import ImportBox71
		bodyModel[67].setRotationPoint(0F, -2F, 0F);
		bodyModel[67].rotateAngleY = -0.78539816F;

		bodyModel[68].addShapeBox(-3F, -30F, -1F, 6, 4, 2, 0F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox72
		bodyModel[68].setRotationPoint(0F, -2F, 0F);
		bodyModel[68].rotateAngleY = -0.78539816F;

		bodyModel[69].addShapeBox(-3F, -30F, -3F, 6, 4, 2, 0F, -2.5F, 0F, -1.5F, -2.5F, 0F, -1.5F, -1.5F, 0F, 0.5F, -1.5F, 0F, 0.5F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox73
		bodyModel[69].setRotationPoint(0F, -2F, 0F);
		bodyModel[69].rotateAngleY = -0.78539816F;

		bodyModel[70].addShapeBox(-3F, -30F, 1F, 6, 4, 2, 0F, -1.5F, 0F, 0.5F, -1.5F, 0F, 0.5F, -2.5F, 0F, -1.5F, -2.5F, 0F, -1.5F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Import ImportBox74
		bodyModel[70].setRotationPoint(0F, -2F, 0F);
		bodyModel[70].rotateAngleY = -0.78539816F;

		bodyModel[71].addShapeBox(-0.5F, -35F, -0.5F, 1, 6, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox75
		bodyModel[71].setRotationPoint(0F, -2F, 0F);
		bodyModel[71].rotateAngleY = -0.78539816F;


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
