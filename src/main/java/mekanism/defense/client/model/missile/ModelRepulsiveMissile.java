package defense.client.model.missile;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.tmt.ModelRendererTurbo;

@SideOnly(Side.CLIENT)
public class ModelRepulsiveMissile extends ModelMissileBase
{
    // fields
    ModelRendererTurbo[] bodyModel;

    public ModelRepulsiveMissile()
    {
		int textureX = 32;
		int textureY = 128;

		bodyModel = new ModelRendererTurbo[58];
		bodyModel[0] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Import ImportImportBox0
		bodyModel[1] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Import ImportImportBox1
		bodyModel[2] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Import ImportImportBox2
		bodyModel[3] = new ModelRendererTurbo(this, 0, 15, textureX, textureY); // Import ImportBox0
		bodyModel[4] = new ModelRendererTurbo(this, 0, 15, textureX, textureY); // Import ImportBox1
		bodyModel[5] = new ModelRendererTurbo(this, 0, 15, textureX, textureY); // Import ImportBox2
		bodyModel[6] = new ModelRendererTurbo(this, 0, 23, textureX, textureY); // Import ImportBox3
		bodyModel[7] = new ModelRendererTurbo(this, 0, 23, textureX, textureY); // Import ImportBox4
		bodyModel[8] = new ModelRendererTurbo(this, 0, 23, textureX, textureY); // Import ImportBox5
		bodyModel[9] = new ModelRendererTurbo(this, 0, 32, textureX, textureY); // Import ImportBox6
		bodyModel[10] = new ModelRendererTurbo(this, 0, 47, textureX, textureY); // Import ImportBox7
		bodyModel[11] = new ModelRendererTurbo(this, 0, 47, textureX, textureY); // Import ImportBox8
		bodyModel[12] = new ModelRendererTurbo(this, 0, 61, textureX, textureY); // Import ImportBox9
		bodyModel[13] = new ModelRendererTurbo(this, 0, 61, textureX, textureY); // Import ImportBox10
		bodyModel[14] = new ModelRendererTurbo(this, 0, 61, textureX, textureY); // Import ImportBox11
		bodyModel[15] = new ModelRendererTurbo(this, 0, 66, textureX, textureY); // Import ImportBox12
		bodyModel[16] = new ModelRendererTurbo(this, 0, 66, textureX, textureY); // Import ImportBox13
		bodyModel[17] = new ModelRendererTurbo(this, 0, 66, textureX, textureY); // Import ImportBox14
		bodyModel[18] = new ModelRendererTurbo(this, 0, 69, textureX, textureY); // Import ImportBox15
		bodyModel[19] = new ModelRendererTurbo(this, 0, 69, textureX, textureY); // Import ImportBox16
		bodyModel[20] = new ModelRendererTurbo(this, 0, 69, textureX, textureY); // Import ImportBox17
		bodyModel[21] = new ModelRendererTurbo(this, 0, 75, textureX, textureY); // Import ImportBox18
		bodyModel[22] = new ModelRendererTurbo(this, 0, 75, textureX, textureY); // Import ImportBox19
		bodyModel[23] = new ModelRendererTurbo(this, 0, 75, textureX, textureY); // Import ImportBox20
		bodyModel[24] = new ModelRendererTurbo(this, 0, 82, textureX, textureY); // Import ImportBox21
		bodyModel[25] = new ModelRendererTurbo(this, 0, 82, textureX, textureY); // Import ImportBox22
		bodyModel[26] = new ModelRendererTurbo(this, 0, 82, textureX, textureY); // Import ImportBox23
		bodyModel[27] = new ModelRendererTurbo(this, 0, 82, textureX, textureY); // Import ImportBox24
		bodyModel[28] = new ModelRendererTurbo(this, 0, 91, textureX, textureY); // Box 0
		bodyModel[29] = new ModelRendererTurbo(this, 0, 97, textureX, textureY); // Box 1
		bodyModel[30] = new ModelRendererTurbo(this, 0, 97, textureX, textureY); // Box 2
		bodyModel[31] = new ModelRendererTurbo(this, 0, 91, textureX, textureY); // Box 3
		bodyModel[32] = new ModelRendererTurbo(this, 0, 97, textureX, textureY); // Box 4
		bodyModel[33] = new ModelRendererTurbo(this, 0, 97, textureX, textureY); // Box 5
		bodyModel[34] = new ModelRendererTurbo(this, 0, 101, textureX, textureY); // Box 6
		bodyModel[35] = new ModelRendererTurbo(this, 0, 105, textureX, textureY); // Box 7
		bodyModel[36] = new ModelRendererTurbo(this, 0, 109, textureX, textureY); // Box 8
		bodyModel[37] = new ModelRendererTurbo(this, 0, 109, textureX, textureY); // Box 9
		bodyModel[38] = new ModelRendererTurbo(this, 0, 109, textureX, textureY); // Box 10
		bodyModel[39] = new ModelRendererTurbo(this, 0, 109, textureX, textureY); // Box 11
		bodyModel[40] = new ModelRendererTurbo(this, 0, 109, textureX, textureY); // Box 12
		bodyModel[41] = new ModelRendererTurbo(this, 0, 109, textureX, textureY); // Box 13
		bodyModel[42] = new ModelRendererTurbo(this, 0, 109, textureX, textureY); // Box 14
		bodyModel[43] = new ModelRendererTurbo(this, 0, 109, textureX, textureY); // Box 15
		bodyModel[44] = new ModelRendererTurbo(this, 0, 101, textureX, textureY); // Box 16
		bodyModel[45] = new ModelRendererTurbo(this, 0, 105, textureX, textureY); // Box 17
		bodyModel[46] = new ModelRendererTurbo(this, 0, 109, textureX, textureY); // Box 18
		bodyModel[47] = new ModelRendererTurbo(this, 0, 109, textureX, textureY); // Box 19
		bodyModel[48] = new ModelRendererTurbo(this, 0, 109, textureX, textureY); // Box 20
		bodyModel[49] = new ModelRendererTurbo(this, 0, 109, textureX, textureY); // Box 21
		bodyModel[50] = new ModelRendererTurbo(this, 0, 101, textureX, textureY); // Box 22
		bodyModel[51] = new ModelRendererTurbo(this, 0, 105, textureX, textureY); // Box 23
		bodyModel[52] = new ModelRendererTurbo(this, 0, 109, textureX, textureY); // Box 24
		bodyModel[53] = new ModelRendererTurbo(this, 0, 109, textureX, textureY); // Box 25
		bodyModel[54] = new ModelRendererTurbo(this, 0, 109, textureX, textureY); // Box 26
		bodyModel[55] = new ModelRendererTurbo(this, 0, 109, textureX, textureY); // Box 27
		bodyModel[56] = new ModelRendererTurbo(this, 0, 101, textureX, textureY); // Box 28
		bodyModel[57] = new ModelRendererTurbo(this, 0, 105, textureX, textureY); // Box 29

		bodyModel[0].addBox(-3F, -1F, -1F, 6, 12, 2, 0F); // Import ImportImportBox0
		bodyModel[0].setRotationPoint(0F, 7F, 0F);
		bodyModel[0].rotateAngleY = 3.92699082F;

		bodyModel[1].addShapeBox(-3F, -1F, -3F, 6, 12, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportImportBox1
		bodyModel[1].setRotationPoint(0F, 7F, 0F);
		bodyModel[1].rotateAngleY = 3.92699082F;

		bodyModel[2].addShapeBox(-3F, -1F, 1F, 6, 12, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Import ImportImportBox2
		bodyModel[2].setRotationPoint(0F, 7F, 0F);
		bodyModel[2].rotateAngleY = 3.92699082F;

		bodyModel[3].addShapeBox(-3F, -1F, -1F, 6, 6, 2, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox0
		bodyModel[3].setRotationPoint(0F, 19F, 0F);
		bodyModel[3].rotateAngleY = 3.92699082F;

		bodyModel[4].addShapeBox(-3F, -1F, -3F, 6, 6, 2, 0F, -2F, 0F, -1F, -2F, 0F, -1F, -1F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox1
		bodyModel[4].setRotationPoint(0F, 19F, 0F);
		bodyModel[4].rotateAngleY = 3.92699082F;

		bodyModel[5].addShapeBox(-3F, -1F, 1F, 6, 6, 2, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, -1F, -2F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Import ImportBox2
		bodyModel[5].setRotationPoint(0F, 19F, 0F);
		bodyModel[5].rotateAngleY = 3.92699082F;

		bodyModel[6].addShapeBox(-3F, -1F, -3F, 6, 7, 2, 0F, -2F, 0F, -1F, -2F, 0F, -1F, -1F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox3
		bodyModel[6].setRotationPoint(0F, 0F, 0F);
		bodyModel[6].rotateAngleY = 3.92699082F;

		bodyModel[7].addShapeBox(-3F, -1F, -1F, 6, 7, 2, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox4
		bodyModel[7].setRotationPoint(0F, 0F, 0F);
		bodyModel[7].rotateAngleY = 3.92699082F;

		bodyModel[8].addShapeBox(-3F, -1F, 1F, 6, 7, 2, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, -1F, -2F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Import ImportBox5
		bodyModel[8].setRotationPoint(0F, 0F, 0F);
		bodyModel[8].rotateAngleY = 3.92699082F;

		bodyModel[9].addShapeBox(-2F, -1F, -1F, 4, 13, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox6
		bodyModel[9].setRotationPoint(0F, -13F, 0F);
		bodyModel[9].rotateAngleY = 3.92699082F;

		bodyModel[10].addShapeBox(-2F, -1F, -2F, 4, 13, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox7
		bodyModel[10].setRotationPoint(0F, -13F, 0F);
		bodyModel[10].rotateAngleY = 3.92699082F;

		bodyModel[11].addShapeBox(-2F, -1F, 1F, 4, 13, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Import ImportBox8
		bodyModel[11].setRotationPoint(0F, -13F, 0F);
		bodyModel[11].rotateAngleY = 3.92699082F;

		bodyModel[12].addShapeBox(-3F, -1F, 1F, 6, 3, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, -1F, -2F, 0F, -1F); // Import ImportBox9
		bodyModel[12].setRotationPoint(0F, -16F, 0F);
		bodyModel[12].rotateAngleY = 3.92699082F;

		bodyModel[13].addShapeBox(-3F, -1F, -1F, 6, 3, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Import ImportBox10
		bodyModel[13].setRotationPoint(0F, -16F, 0F);
		bodyModel[13].rotateAngleY = 3.92699082F;

		bodyModel[14].addShapeBox(-3F, -1F, -3F, 6, 3, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, -1F, -2F, 0F, -1F, -1F, 0F, 0F, -1F, 0F, 0F); // Import ImportBox11
		bodyModel[14].setRotationPoint(0F, -16F, 0F);
		bodyModel[14].rotateAngleY = 3.92699082F;

		bodyModel[15].addShapeBox(-3F, -1F, 1F, 6, 1, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Import ImportBox12
		bodyModel[15].setRotationPoint(0F, -17F, 0F);
		bodyModel[15].rotateAngleY = 3.92699082F;

		bodyModel[16].addBox(-3F, -1F, -1F, 6, 1, 2, 0F); // Import ImportBox13
		bodyModel[16].setRotationPoint(0F, -17F, 0F);
		bodyModel[16].rotateAngleY = 3.92699082F;

		bodyModel[17].addShapeBox(-3F, -1F, -3F, 6, 1, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox14
		bodyModel[17].setRotationPoint(0F, -17F, 0F);
		bodyModel[17].rotateAngleY = 3.92699082F;

		bodyModel[18].addShapeBox(-3F, -1F, -1F, 6, 4, 2, 0F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox15
		bodyModel[18].setRotationPoint(0F, -21F, 0F);
		bodyModel[18].rotateAngleY = 3.92699082F;

		bodyModel[19].addShapeBox(-3F, -1F, -3F, 6, 4, 2, 0F, -2.5F, 0F, -1.5F, -2.5F, 0F, -1.5F, -1.5F, 0F, 0.5F, -1.5F, 0F, 0.5F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox16
		bodyModel[19].setRotationPoint(0F, -21F, 0F);
		bodyModel[19].rotateAngleY = 3.92699082F;

		bodyModel[20].addShapeBox(-3F, -1F, 1F, 6, 4, 2, 0F, -1.5F, 0F, 0.5F, -1.5F, 0F, 0.5F, -2.5F, 0F, -1.5F, -2.5F, 0F, -1.5F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Import ImportBox17
		bodyModel[20].setRotationPoint(0F, -21F, 0F);
		bodyModel[20].rotateAngleY = 3.92699082F;

		bodyModel[21].addShapeBox(-1.5F, 0F, -0.5F, 3, 6, 1, 0F, -1F, 0F, -0.33F, -1F, 0F, -0.33F, -1F, 0F, -0.33F, -1F, 0F, -0.33F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F); // Import ImportBox18
		bodyModel[21].setRotationPoint(0F, -27.5F, 0F);
		bodyModel[21].rotateAngleY = 3.92699082F;

		bodyModel[22].addShapeBox(-1.5F, 0F, -1.5F, 3, 6, 1, 0F, -1.33F, 0F, -1F, -1.33F, 0F, -1F, -1F, 0F, 0.33F, -1F, 0F, 0.33F, -1F, -0.5F, 0F, -1F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F); // Import ImportBox19
		bodyModel[22].setRotationPoint(0F, -27.5F, 0F);
		bodyModel[22].rotateAngleY = 3.92699082F;

		bodyModel[23].addShapeBox(-1.5F, 0F, 0.5F, 3, 6, 1, 0F, -1F, 0F, 0.33F, -1F, 0F, 0.33F, -1.33F, 0F, -1F, -1.33F, 0F, -1F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, -1F, -0.5F, 0F, -1F, -0.5F, 0F); // Import ImportBox20
		bodyModel[23].setRotationPoint(0F, -27.5F, 0F);
		bodyModel[23].rotateAngleY = 3.92699082F;

		bodyModel[24].addShapeBox(-7F, -1F, -0.5F, 4, 8, 1, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox21
		bodyModel[24].setRotationPoint(0F, 11F, 0F);
		bodyModel[24].rotateAngleY = 3.92699082F;

		bodyModel[25].addShapeBox(-7F, -1F, -0.5F, 4, 8, 1, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox22
		bodyModel[25].setRotationPoint(0F, 11F, 0F);
		bodyModel[25].rotateAngleY = 5.49778714F;

		bodyModel[26].addShapeBox(-7F, -1F, -0.5F, 4, 8, 1, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox23
		bodyModel[26].setRotationPoint(0F, 11F, 0F);
		bodyModel[26].rotateAngleY = 2.35619449F;

		bodyModel[27].addShapeBox(-7F, -1F, -0.5F, 4, 8, 1, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox24
		bodyModel[27].setRotationPoint(0F, 11F, 0F);
		bodyModel[27].rotateAngleY = 0.78539816F;

		bodyModel[28].addShapeBox(-2.5F, -1F, -1.5F, 5, 3, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 0
		bodyModel[28].setRotationPoint(0F, -12F, 0F);
		bodyModel[28].rotateAngleY = 3.92699082F;

		bodyModel[29].addShapeBox(-2.5F, -1F, -2.5F, 5, 3, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 1
		bodyModel[29].setRotationPoint(0F, -12F, 0F);
		bodyModel[29].rotateAngleY = 3.92699082F;

		bodyModel[30].addShapeBox(-2.5F, -1F, 1.5F, 5, 3, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 2
		bodyModel[30].setRotationPoint(0F, -12F, 0F);
		bodyModel[30].rotateAngleY = 3.92699082F;

		bodyModel[31].addShapeBox(-2.5F, -1F, -1.5F, 5, 3, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 3
		bodyModel[31].setRotationPoint(0F, -5F, 0F);
		bodyModel[31].rotateAngleY = 3.92699082F;

		bodyModel[32].addShapeBox(-2.5F, -1F, 1.5F, 5, 3, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Box 4
		bodyModel[32].setRotationPoint(0F, -5F, 0F);
		bodyModel[32].rotateAngleY = 3.92699082F;

		bodyModel[33].addShapeBox(-2.5F, -1F, -2.5F, 5, 3, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 5
		bodyModel[33].setRotationPoint(0F, -5F, 0F);
		bodyModel[33].rotateAngleY = 3.92699082F;

		bodyModel[34].addShapeBox(2.5F, -1F, -0.5F, 5, 3, 1, 0F, 0F, 0F, 0F, 0F, -3F, -0.4F, 0F, -3F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, -0.4F, 0F, 1F, -0.4F, 0F, 0F, 0F); // Box 6
		bodyModel[34].setRotationPoint(0F, -12F, 0F);
		bodyModel[34].rotateAngleY = 3.92699082F;

		bodyModel[35].addShapeBox(2.5F, -1F, -0.5F, 3, 3, 1, 0F, 0F, 0F, 0F, 0F, -4F, -0.4F, 0F, -4F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 1.5F, -0.4F, -1F, 1.5F, -0.4F, 0F, 0F, 0F); // Box 7
		bodyModel[35].setRotationPoint(0F, -5F, 0F);
		bodyModel[35].rotateAngleY = 3.92699082F;

		bodyModel[36].addShapeBox(2.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 8
		bodyModel[36].setRotationPoint(0F, -11.8F, 0F);
		bodyModel[36].rotateAngleY = 4.71238898F;

		bodyModel[37].addShapeBox(2.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 9
		bodyModel[37].setRotationPoint(0F, -10.2F, 0F);
		bodyModel[37].rotateAngleY = 4.71238898F;

		bodyModel[38].addShapeBox(2.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 10
		bodyModel[38].setRotationPoint(0F, -3.2F, 0F);
		bodyModel[38].rotateAngleY = 4.71238898F;

		bodyModel[39].addShapeBox(2.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 11
		bodyModel[39].setRotationPoint(0F, -4.8F, 0F);
		bodyModel[39].rotateAngleY = 4.71238898F;

		bodyModel[40].addShapeBox(2.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 12
		bodyModel[40].setRotationPoint(0F, -3.2F, 0F);
		bodyModel[40].rotateAngleY = 3.14159265F;

		bodyModel[41].addShapeBox(2.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 13
		bodyModel[41].setRotationPoint(0F, -4.8F, 0F);
		bodyModel[41].rotateAngleY = 3.14159265F;

		bodyModel[42].addShapeBox(2.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 14
		bodyModel[42].setRotationPoint(0F, -11.8F, 0F);
		bodyModel[42].rotateAngleY = 3.14159265F;

		bodyModel[43].addShapeBox(2.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 15
		bodyModel[43].setRotationPoint(0F, -10.2F, 0F);
		bodyModel[43].rotateAngleY = 3.14159265F;

		bodyModel[44].addShapeBox(2.5F, -1F, -0.5F, 5, 3, 1, 0F, 0F, 0F, 0F, 0F, -3F, -0.4F, 0F, -3F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, -0.4F, 0F, 1F, -0.4F, 0F, 0F, 0F); // Box 16
		bodyModel[44].setRotationPoint(0F, -12F, 0F);
		bodyModel[44].rotateAngleY = 2.35619449F;

		bodyModel[45].addShapeBox(2.5F, -1F, -0.5F, 3, 3, 1, 0F, 0F, 0F, 0F, 0F, -4F, -0.4F, 0F, -4F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 1.5F, -0.4F, -1F, 1.5F, -0.4F, 0F, 0F, 0F); // Box 17
		bodyModel[45].setRotationPoint(0F, -5F, 0F);
		bodyModel[45].rotateAngleY = 2.35619449F;

		bodyModel[46].addShapeBox(2.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 18
		bodyModel[46].setRotationPoint(0F, -3.2F, 0F);
		bodyModel[46].rotateAngleY = 1.57079633F;

		bodyModel[47].addShapeBox(2.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 19
		bodyModel[47].setRotationPoint(0F, -4.8F, 0F);
		bodyModel[47].rotateAngleY = 1.57079633F;

		bodyModel[48].addShapeBox(2.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 20
		bodyModel[48].setRotationPoint(0F, -11.8F, 0F);
		bodyModel[48].rotateAngleY = 1.57079633F;

		bodyModel[49].addShapeBox(2.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 21
		bodyModel[49].setRotationPoint(0F, -10.2F, 0F);
		bodyModel[49].rotateAngleY = 1.57079633F;

		bodyModel[50].addShapeBox(2.5F, -1F, -0.5F, 5, 3, 1, 0F, 0F, 0F, 0F, 0F, -3F, -0.4F, 0F, -3F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, -0.4F, 0F, 1F, -0.4F, 0F, 0F, 0F); // Box 22
		bodyModel[50].setRotationPoint(0F, -12F, 0F);
		bodyModel[50].rotateAngleY = 0.78539817F;

		bodyModel[51].addShapeBox(2.5F, -1F, -0.5F, 3, 3, 1, 0F, 0F, 0F, 0F, 0F, -4F, -0.4F, 0F, -4F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 1.5F, -0.4F, -1F, 1.5F, -0.4F, 0F, 0F, 0F); // Box 23
		bodyModel[51].setRotationPoint(0F, -5F, 0F);
		bodyModel[51].rotateAngleY = 0.78539817F;

		bodyModel[52].addShapeBox(2.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 24
		bodyModel[52].setRotationPoint(0F, -3.2F, 0F);

		bodyModel[53].addShapeBox(2.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 25
		bodyModel[53].setRotationPoint(0F, -4.8F, 0F);

		bodyModel[54].addShapeBox(2.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 26
		bodyModel[54].setRotationPoint(0F, -11.8F, 0F);

		bodyModel[55].addShapeBox(2.5F, -1F, -0.5F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 27
		bodyModel[55].setRotationPoint(0F, -10.2F, 0F);

		bodyModel[56].addShapeBox(2.5F, -1F, -0.5F, 5, 3, 1, 0F, 0F, 0F, 0F, 0F, -3F, -0.4F, 0F, -3F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, -0.4F, 0F, 1F, -0.4F, 0F, 0F, 0F); // Box 28
		bodyModel[56].setRotationPoint(0F, -12F, 0F);
		bodyModel[56].rotateAngleY = -0.78539816F;

		bodyModel[57].addShapeBox(2.5F, -1F, -0.5F, 3, 3, 1, 0F, 0F, 0F, 0F, 0F, -4F, -0.4F, 0F, -4F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 1.5F, -0.4F, -1F, 1.5F, -0.4F, 0F, 0F, 0F); // Box 29
		bodyModel[57].setRotationPoint(0F, -5F, 0F);
		bodyModel[57].rotateAngleY = -0.78539816F;
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
