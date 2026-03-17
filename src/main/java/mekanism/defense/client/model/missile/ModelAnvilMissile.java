package defense.client.model.missile;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.tmt.ModelRendererTurbo;

@SideOnly(Side.CLIENT)
public class ModelAnvilMissile extends ModelMissileBase
{
    // fields
    ModelRendererTurbo[] bodyModel;

    public ModelAnvilMissile()
    {
		int textureX = 32;
		int textureY = 128;

		bodyModel = new ModelRendererTurbo[68];
		bodyModel[0] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Import ImportBox0
		bodyModel[1] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Import ImportBox1
		bodyModel[2] = new ModelRendererTurbo(this, 0, 0, textureX, textureY); // Import ImportBox2
		bodyModel[3] = new ModelRendererTurbo(this, 0, 19, textureX, textureY); // Import Box0
		bodyModel[4] = new ModelRendererTurbo(this, 0, 19, textureX, textureY); // Import Box1
		bodyModel[5] = new ModelRendererTurbo(this, 0, 19, textureX, textureY); // Import Box2
		bodyModel[6] = new ModelRendererTurbo(this, 0, 27, textureX, textureY); // Import Box3
		bodyModel[7] = new ModelRendererTurbo(this, 0, 27, textureX, textureY); // Import Box4
		bodyModel[8] = new ModelRendererTurbo(this, 0, 27, textureX, textureY); // Import Box5
		bodyModel[9] = new ModelRendererTurbo(this, 0, 39, textureX, textureY); // Import Box6
		bodyModel[10] = new ModelRendererTurbo(this, 10, 47, textureX, textureY); // Import Box7
		bodyModel[11] = new ModelRendererTurbo(this, 0, 47, textureX, textureY); // Import Box8
		bodyModel[12] = new ModelRendererTurbo(this, 0, 54, textureX, textureY); // Import Box9
		bodyModel[13] = new ModelRendererTurbo(this, 0, 54, textureX, textureY); // Import Box10
		bodyModel[14] = new ModelRendererTurbo(this, 0, 54, textureX, textureY); // Import Box11
		bodyModel[15] = new ModelRendererTurbo(this, 0, 58, textureX, textureY); // Import Box12
		bodyModel[16] = new ModelRendererTurbo(this, 0, 58, textureX, textureY); // Import Box13
		bodyModel[17] = new ModelRendererTurbo(this, 0, 58, textureX, textureY); // Import Box14
		bodyModel[18] = new ModelRendererTurbo(this, 0, 63, textureX, textureY); // Import Box15
		bodyModel[19] = new ModelRendererTurbo(this, 0, 63, textureX, textureY); // Import Box16
		bodyModel[20] = new ModelRendererTurbo(this, 0, 63, textureX, textureY); // Import Box17
		bodyModel[21] = new ModelRendererTurbo(this, 0, 66, textureX, textureY); // Import Box21
		bodyModel[22] = new ModelRendererTurbo(this, 0, 66, textureX, textureY); // Import Box22
		bodyModel[23] = new ModelRendererTurbo(this, 0, 66, textureX, textureY); // Import Box23
		bodyModel[24] = new ModelRendererTurbo(this, 0, 66, textureX, textureY); // Import Box24
		bodyModel[25] = new ModelRendererTurbo(this, 0, 75, textureX, textureY); // Box 0
		bodyModel[26] = new ModelRendererTurbo(this, 0, 78, textureX, textureY); // Box 1
		bodyModel[27] = new ModelRendererTurbo(this, 0, 84, textureX, textureY); // Box 2
		bodyModel[28] = new ModelRendererTurbo(this, 0, 88, textureX, textureY); // Box 3
		bodyModel[29] = new ModelRendererTurbo(this, 0, 91, textureX, textureY); // Box 6
		bodyModel[30] = new ModelRendererTurbo(this, 0, 94, textureX, textureY); // Box 7
		bodyModel[31] = new ModelRendererTurbo(this, 0, 96, textureX, textureY); // Box 8
		bodyModel[32] = new ModelRendererTurbo(this, 0, 96, textureX, textureY); // Box 9
		bodyModel[33] = new ModelRendererTurbo(this, 0, 94, textureX, textureY); // Box 10
		bodyModel[34] = new ModelRendererTurbo(this, 0, 91, textureX, textureY); // Box 11
		bodyModel[35] = new ModelRendererTurbo(this, 0, 99, textureX, textureY); // Box 12
		bodyModel[36] = new ModelRendererTurbo(this, 0, 102, textureX, textureY); // Box 13
		bodyModel[37] = new ModelRendererTurbo(this, 0, 105, textureX, textureY); // Box 14
		bodyModel[38] = new ModelRendererTurbo(this, 0, 84, textureX, textureY); // Box 15
		bodyModel[39] = new ModelRendererTurbo(this, 0, 88, textureX, textureY); // Box 16
		bodyModel[40] = new ModelRendererTurbo(this, 18, 0, textureX, textureY); // Box 17
		bodyModel[41] = new ModelRendererTurbo(this, 18, 0, textureX, textureY); // Box 18
		bodyModel[42] = new ModelRendererTurbo(this, 18, 0, textureX, textureY); // Box 19
		bodyModel[43] = new ModelRendererTurbo(this, 0, 110, textureX, textureY); // Box 21
		bodyModel[44] = new ModelRendererTurbo(this, 0, 110, textureX, textureY); // Box 22
		bodyModel[45] = new ModelRendererTurbo(this, 0, 110, textureX, textureY); // Box 23
		bodyModel[46] = new ModelRendererTurbo(this, 0, 110, textureX, textureY); // Box 24
		bodyModel[47] = new ModelRendererTurbo(this, 0, 110, textureX, textureY); // Box 25
		bodyModel[48] = new ModelRendererTurbo(this, 0, 110, textureX, textureY); // Box 26
		bodyModel[49] = new ModelRendererTurbo(this, 18, 0, textureX, textureY); // Box 27
		bodyModel[50] = new ModelRendererTurbo(this, 18, 0, textureX, textureY); // Box 28
		bodyModel[51] = new ModelRendererTurbo(this, 18, 0, textureX, textureY); // Box 29
		bodyModel[52] = new ModelRendererTurbo(this, 0, 110, textureX, textureY); // Box 30
		bodyModel[53] = new ModelRendererTurbo(this, 0, 110, textureX, textureY); // Box 31
		bodyModel[54] = new ModelRendererTurbo(this, 0, 110, textureX, textureY); // Box 32
		bodyModel[55] = new ModelRendererTurbo(this, 18, 0, textureX, textureY); // Box 33
		bodyModel[56] = new ModelRendererTurbo(this, 18, 0, textureX, textureY); // Box 34
		bodyModel[57] = new ModelRendererTurbo(this, 18, 0, textureX, textureY); // Box 35
		bodyModel[58] = new ModelRendererTurbo(this, 0, 110, textureX, textureY); // Box 36
		bodyModel[59] = new ModelRendererTurbo(this, 0, 110, textureX, textureY); // Box 37
		bodyModel[60] = new ModelRendererTurbo(this, 0, 110, textureX, textureY); // Box 38
		bodyModel[61] = new ModelRendererTurbo(this, 18, 0, textureX, textureY); // Box 39
		bodyModel[62] = new ModelRendererTurbo(this, 18, 0, textureX, textureY); // Box 40
		bodyModel[63] = new ModelRendererTurbo(this, 18, 0, textureX, textureY); // Box 41
		bodyModel[64] = new ModelRendererTurbo(this, 0, 117, textureX, textureY); // Box 42
		bodyModel[65] = new ModelRendererTurbo(this, 0, 117, textureX, textureY); // Box 43
		bodyModel[66] = new ModelRendererTurbo(this, 0, 117, textureX, textureY); // Box 44
		bodyModel[67] = new ModelRendererTurbo(this, 0, 117, textureX, textureY); // Box 45

		bodyModel[0].addBox(-3F, -1F, -1F, 6, 17, 2, 0F); // Import ImportBox0
		bodyModel[0].setRotationPoint(0F, 2F, 0F);
		bodyModel[0].rotateAngleY = 3.92699082F;

		bodyModel[1].addShapeBox(-3F, -1F, -3F, 6, 17, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import ImportBox1
		bodyModel[1].setRotationPoint(0F, 2F, 0F);
		bodyModel[1].rotateAngleY = 3.92699082F;

		bodyModel[2].addShapeBox(-3F, -1F, 1F, 6, 17, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Import ImportBox2
		bodyModel[2].setRotationPoint(0F, 2F, 0F);
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

		bodyModel[6].addShapeBox(-3F, -1F, -3F, 6, 10, 2, 0F, -2F, 0F, -1F, -2F, 0F, -1F, -1F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box3
		bodyModel[6].setRotationPoint(0F, -8F, 0F);
		bodyModel[6].rotateAngleY = 3.92699082F;

		bodyModel[7].addShapeBox(-3F, -1F, -1F, 6, 10, 2, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box4
		bodyModel[7].setRotationPoint(0F, -8F, 0F);
		bodyModel[7].rotateAngleY = 3.92699082F;

		bodyModel[8].addShapeBox(-3F, -1F, 1F, 6, 10, 2, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -2F, 0F, -1F, -2F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Import Box5
		bodyModel[8].setRotationPoint(0F, -8F, 0F);
		bodyModel[8].rotateAngleY = 3.92699082F;

		bodyModel[9].addShapeBox(-2F, -1F, -1F, 4, 6, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box6
		bodyModel[9].setRotationPoint(0F, -14F, 0F);
		bodyModel[9].rotateAngleY = 3.92699082F;

		bodyModel[10].addShapeBox(-2F, -1F, -2F, 4, 6, 1, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box7
		bodyModel[10].setRotationPoint(0F, -14F, 0F);
		bodyModel[10].rotateAngleY = 3.92699082F;

		bodyModel[11].addShapeBox(-2F, -1F, 1F, 4, 6, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F); // Import Box8
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

		bodyModel[15].addShapeBox(-3F, -1F, 1F, 6, 3, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Import Box12
		bodyModel[15].setRotationPoint(0F, -19F, 0F);
		bodyModel[15].rotateAngleY = 3.92699082F;

		bodyModel[16].addBox(-3F, -1F, -1F, 6, 3, 2, 0F); // Import Box13
		bodyModel[16].setRotationPoint(0F, -19F, 0F);
		bodyModel[16].rotateAngleY = 3.92699082F;

		bodyModel[17].addShapeBox(-3F, -1F, -3F, 6, 3, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box14
		bodyModel[17].setRotationPoint(0F, -19F, 0F);
		bodyModel[17].rotateAngleY = 3.92699082F;

		bodyModel[18].addShapeBox(-3F, -1F, -1F, 6, 1, 2, 0F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, -1.5F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box15
		bodyModel[18].setRotationPoint(0F, -20F, 0F);
		bodyModel[18].rotateAngleY = 3.92699082F;

		bodyModel[19].addShapeBox(-3F, -1F, -3F, 6, 1, 2, 0F, -2.5F, 0F, -1.5F, -2.5F, 0F, -1.5F, -1.5F, 0F, 0.5F, -1.5F, 0F, 0.5F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box16
		bodyModel[19].setRotationPoint(0F, -20F, 0F);
		bodyModel[19].rotateAngleY = 3.92699082F;

		bodyModel[20].addShapeBox(-3F, -1F, 1F, 6, 1, 2, 0F, -1.5F, 0F, 0.5F, -1.5F, 0F, 0.5F, -2.5F, 0F, -1.5F, -2.5F, 0F, -1.5F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Import Box17
		bodyModel[20].setRotationPoint(0F, -20F, 0F);
		bodyModel[20].rotateAngleY = 3.92699082F;

		bodyModel[21].addShapeBox(-7F, -1F, -0.5F, 4, 8, 1, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box21
		bodyModel[21].setRotationPoint(0F, 11F, 0F);
		bodyModel[21].rotateAngleY = 3.92699082F;

		bodyModel[22].addShapeBox(-7F, -1F, -0.5F, 4, 8, 1, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box22
		bodyModel[22].setRotationPoint(0F, 11F, 0F);
		bodyModel[22].rotateAngleY = 5.49778714F;

		bodyModel[23].addShapeBox(-7F, -1F, -0.5F, 4, 8, 1, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box23
		bodyModel[23].setRotationPoint(0F, 11F, 0F);
		bodyModel[23].rotateAngleY = 2.35619449F;

		bodyModel[24].addShapeBox(-7F, -1F, -0.5F, 4, 8, 1, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Import Box24
		bodyModel[24].setRotationPoint(0F, 11F, 0F);
		bodyModel[24].rotateAngleY = 0.78539816F;

		bodyModel[25].addShapeBox(-1F, -1F, -1F, 2, 1, 2, 0F, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 0
		bodyModel[25].setRotationPoint(0F, -21F, 0F);
		bodyModel[25].rotateAngleY = 3.92699082F;

		bodyModel[26].addShapeBox(-1F, -1F, -2F, 2, 2, 4, 0F, -1F, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 1
		bodyModel[26].setRotationPoint(0F, -23F, 0F);
		bodyModel[26].rotateAngleY = 3.92699082F;

		bodyModel[27].addShapeBox(0F, -1F, -2F, 2, 3, 1, 0F, 0F, 0F, 0.5F, -1F, 0F, 0.5F, -1F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.05F, 0F, 0F, -0.05F); // Box 2
		bodyModel[27].setRotationPoint(0F, -26F, 0F);
		bodyModel[27].rotateAngleY = 3.92699082F;

		bodyModel[28].addShapeBox(0F, -1F, -2.5F, 1, 2, 1, 0F, 0F, 0F, 0.33F, 0F, 0F, 0.33F, 0F, 0F, -0.33F, 0F, 0F, -0.33F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 3
		bodyModel[28].setRotationPoint(0F, -28F, 0F);
		bodyModel[28].rotateAngleY = 3.92699082F;

		bodyModel[29].addShapeBox(0F, -1F, -1F, 2, 2, 1, 0F, 0.5F, -1F, -0.05F, -2.5F, 0F, -0.05F, -2.5F, 0F, -0.05F, 0.5F, -1F, -0.05F, 0F, 0F, -0.05F, 0F, 0F, -0.05F, 0F, 0F, -0.05F, 0F, 0F, -0.05F); // Box 6
		bodyModel[29].setRotationPoint(0F, -25F, 0F);
		bodyModel[29].rotateAngleY = 3.92699082F;

		bodyModel[30].addShapeBox(-1.5F, -1F, -1F, 1, 1, 1, 0F, -0.5F, 0F, -0.05F, 0F, 0F, -0.05F, 0F, 0F, -0.05F, -0.5F, 0F, -0.05F, 0.5F, 0F, -0.05F, 0F, 0F, -0.05F, 0F, 0F, -0.05F, 0.5F, 0F, -0.05F); // Box 7
		bodyModel[30].setRotationPoint(0F, -25F, 0F);
		bodyModel[30].rotateAngleY = 3.92699082F;

		bodyModel[31].addShapeBox(-2F, -1F, -1F, 1, 2, 1, 0F, 0F, 0F, -0.05F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, -0.5F, 0F, 0.5F, 0F, 0F, 0.5F, 0F, 0F, -0.5F, -0.5F, 0F); // Box 8
		bodyModel[31].setRotationPoint(0F, -24F, 0F);
		bodyModel[31].rotateAngleY = 3.92699082F;

		bodyModel[32].addShapeBox(-2F, -1F, 0F, 1, 2, 1, 0F, 0F, 0F, -0.05F, 0F, 0F, -0.05F, 0F, 0F, -0.05F, 0F, 0F, -0.05F, -0.5F, -0.5F, -0.05F, 0.5F, 0F, -0.05F, 0.5F, 0F, -0.05F, -0.5F, -0.5F, -0.05F); // Box 9
		bodyModel[32].setRotationPoint(0F, -24F, 0F);
		bodyModel[32].rotateAngleY = 3.92699082F;

		bodyModel[33].addShapeBox(-1.5F, -1F, 0F, 1, 1, 1, 0F, -0.5F, 0F, -0.05F, 0F, 0F, -0.05F, 0F, 0F, -0.05F, -0.5F, 0F, -0.05F, 0.5F, 0F, -0.05F, 0F, 0F, -0.05F, 0F, 0F, -0.05F, 0.5F, 0F, -0.05F); // Box 10
		bodyModel[33].setRotationPoint(0F, -25F, 0F);
		bodyModel[33].rotateAngleY = 3.92699082F;

		bodyModel[34].addShapeBox(0F, -1F, 0F, 2, 2, 1, 0F, 0.5F, -1F, -0.05F, -2.5F, 0F, -0.05F, -2.5F, 0F, -0.05F, 0.5F, -1F, -0.05F, 0F, 0F, -0.05F, 0F, 0F, -0.05F, 0F, 0F, -0.05F, 0F, 0F, -0.05F); // Box 11
		bodyModel[34].setRotationPoint(0F, -25F, 0F);
		bodyModel[34].rotateAngleY = 3.92699082F;

		bodyModel[35].addShapeBox(-1F, -1F, 2F, 2, 2, 1, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F); // Box 12
		bodyModel[35].setRotationPoint(0F, -23F, 0F);
		bodyModel[35].rotateAngleY = 3.92699082F;

		bodyModel[36].addShapeBox(-2F, -1F, 2F, 1, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F); // Box 13
		bodyModel[36].setRotationPoint(0F, -23F, 0F);
		bodyModel[36].rotateAngleY = 3.92699082F;

		bodyModel[37].addShapeBox(-2F, -1F, 0F, 1, 2, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 14
		bodyModel[37].setRotationPoint(0F, -23F, 0F);
		bodyModel[37].rotateAngleY = 3.92699082F;

		bodyModel[38].addShapeBox(0F, -1F, 1F, 2, 3, 1, 0F, 0F, 0F, -0.5F, -1F, 0F, -0.5F, -1F, 0F, 0.5F, 0F, 0F, 0.5F, 0F, 0F, -0.05F, 0F, 0F, -0.05F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 15
		bodyModel[38].setRotationPoint(0F, -26F, 0F);
		bodyModel[38].rotateAngleY = 3.92699082F;

		bodyModel[39].addShapeBox(0F, -1F, 1.5F, 1, 2, 1, 0F, 0F, 0F, -0.33F, 0F, 0F, -0.33F, 0F, 0F, 0.33F, 0F, 0F, 0.33F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 16
		bodyModel[39].setRotationPoint(0F, -28F, 0F);
		bodyModel[39].rotateAngleY = 3.92699082F;

		bodyModel[40].addShapeBox(-4F, -1F, -1.5F, 1, 22, 3, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F); // Box 17
		bodyModel[40].setRotationPoint(0F, -1F, 0F);
		bodyModel[40].rotateAngleY = 4.71238898F;

		bodyModel[41].addShapeBox(-5F, -1F, -1.5F, 1, 22, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 18
		bodyModel[41].setRotationPoint(0F, -1F, 0F);
		bodyModel[41].rotateAngleY = 4.71238898F;

		bodyModel[42].addShapeBox(-6F, -1F, -1.5F, 1, 22, 3, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Box 19
		bodyModel[42].setRotationPoint(0F, -1F, 0F);
		bodyModel[42].rotateAngleY = 4.71238898F;

		bodyModel[43].addShapeBox(-5F, -1F, -1.5F, 1, 3, 3, 0F, -0.33F, 0F, -1F, -0.33F, 0F, -1F, -0.33F, 0F, -1F, -0.33F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 21
		bodyModel[43].setRotationPoint(0F, -4F, 0F);
		bodyModel[43].rotateAngleY = 4.71238898F;

		bodyModel[44].addShapeBox(-6F, -1F, -1.5F, 1, 3, 3, 0F, -1F, 0F, -1.33F, 0.33F, 0F, -1F, 0.33F, 0F, -1F, -1F, 0F, -1.33F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Box 22
		bodyModel[44].setRotationPoint(0F, -4F, 0F);
		bodyModel[44].rotateAngleY = 4.71238898F;

		bodyModel[45].addShapeBox(-4F, -1F, -1.5F, 1, 3, 3, 0F, 0.33F, 0F, -1F, -1F, 0F, -1.33F, -1F, 0F, -1.33F, 0.33F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F); // Box 23
		bodyModel[45].setRotationPoint(0F, -4F, 0F);
		bodyModel[45].rotateAngleY = 4.71238898F;

		bodyModel[46].addShapeBox(-4F, -1F, -1.5F, 1, 3, 3, 0F, 0.33F, 0F, -1F, -1F, 0F, -1.33F, -1F, 0F, -1.33F, 0.33F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F); // Box 24
		bodyModel[46].setRotationPoint(0F, -4F, 0F);
		bodyModel[46].rotateAngleY = 6.28318531F;

		bodyModel[47].addShapeBox(-5F, -1F, -1.5F, 1, 3, 3, 0F, -0.33F, 0F, -1F, -0.33F, 0F, -1F, -0.33F, 0F, -1F, -0.33F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 25
		bodyModel[47].setRotationPoint(0F, -4F, 0F);
		bodyModel[47].rotateAngleY = 6.28318531F;

		bodyModel[48].addShapeBox(-6F, -1F, -1.5F, 1, 3, 3, 0F, -1F, 0F, -1.33F, 0.33F, 0F, -1F, 0.33F, 0F, -1F, -1F, 0F, -1.33F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Box 26
		bodyModel[48].setRotationPoint(0F, -4F, 0F);
		bodyModel[48].rotateAngleY = 6.28318531F;

		bodyModel[49].addShapeBox(-6F, -1F, -1.5F, 1, 22, 3, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Box 27
		bodyModel[49].setRotationPoint(0F, -1F, 0F);
		bodyModel[49].rotateAngleY = 6.28318531F;

		bodyModel[50].addShapeBox(-5F, -1F, -1.5F, 1, 22, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 28
		bodyModel[50].setRotationPoint(0F, -1F, 0F);
		bodyModel[50].rotateAngleY = 6.28318531F;

		bodyModel[51].addShapeBox(-4F, -1F, -1.5F, 1, 22, 3, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F); // Box 29
		bodyModel[51].setRotationPoint(0F, -1F, 0F);
		bodyModel[51].rotateAngleY = 6.28318531F;

		bodyModel[52].addShapeBox(-4F, -1F, -1.5F, 1, 3, 3, 0F, 0.33F, 0F, -1F, -1F, 0F, -1.33F, -1F, 0F, -1.33F, 0.33F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F); // Box 30
		bodyModel[52].setRotationPoint(0F, -4F, 0F);
		bodyModel[52].rotateAngleY = 1.57079633F;

		bodyModel[53].addShapeBox(-5F, -1F, -1.5F, 1, 3, 3, 0F, -0.33F, 0F, -1F, -0.33F, 0F, -1F, -0.33F, 0F, -1F, -0.33F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 31
		bodyModel[53].setRotationPoint(0F, -4F, 0F);
		bodyModel[53].rotateAngleY = 1.57079633F;

		bodyModel[54].addShapeBox(-6F, -1F, -1.5F, 1, 3, 3, 0F, -1F, 0F, -1.33F, 0.33F, 0F, -1F, 0.33F, 0F, -1F, -1F, 0F, -1.33F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Box 32
		bodyModel[54].setRotationPoint(0F, -4F, 0F);
		bodyModel[54].rotateAngleY = 1.57079633F;

		bodyModel[55].addShapeBox(-6F, -1F, -1.5F, 1, 22, 3, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Box 33
		bodyModel[55].setRotationPoint(0F, -1F, 0F);
		bodyModel[55].rotateAngleY = 1.57079633F;

		bodyModel[56].addShapeBox(-5F, -1F, -1.5F, 1, 22, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 34
		bodyModel[56].setRotationPoint(0F, -1F, 0F);
		bodyModel[56].rotateAngleY = 1.57079633F;

		bodyModel[57].addShapeBox(-4F, -1F, -1.5F, 1, 22, 3, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F); // Box 35
		bodyModel[57].setRotationPoint(0F, -1F, 0F);
		bodyModel[57].rotateAngleY = 1.57079633F;

		bodyModel[58].addShapeBox(-4F, -1F, -1.5F, 1, 3, 3, 0F, 0.33F, 0F, -1F, -1F, 0F, -1.33F, -1F, 0F, -1.33F, 0.33F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F); // Box 36
		bodyModel[58].setRotationPoint(0F, -4F, 0F);
		bodyModel[58].rotateAngleY = -3.14159265F;

		bodyModel[59].addShapeBox(-5F, -1F, -1.5F, 1, 3, 3, 0F, -0.33F, 0F, -1F, -0.33F, 0F, -1F, -0.33F, 0F, -1F, -0.33F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 37
		bodyModel[59].setRotationPoint(0F, -4F, 0F);
		bodyModel[59].rotateAngleY = -3.14159265F;

		bodyModel[60].addShapeBox(-6F, -1F, -1.5F, 1, 3, 3, 0F, -1F, 0F, -1.33F, 0.33F, 0F, -1F, 0.33F, 0F, -1F, -1F, 0F, -1.33F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Box 38
		bodyModel[60].setRotationPoint(0F, -4F, 0F);
		bodyModel[60].rotateAngleY = -3.14159265F;

		bodyModel[61].addShapeBox(-6F, -1F, -1.5F, 1, 22, 3, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Box 39
		bodyModel[61].setRotationPoint(0F, -1F, 0F);
		bodyModel[61].rotateAngleY = -3.14159265F;

		bodyModel[62].addShapeBox(-5F, -1F, -1.5F, 1, 22, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 40
		bodyModel[62].setRotationPoint(0F, -1F, 0F);
		bodyModel[62].rotateAngleY = -3.14159265F;

		bodyModel[63].addShapeBox(-4F, -1F, -1.5F, 1, 22, 3, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F); // Box 41
		bodyModel[63].setRotationPoint(0F, -1F, 0F);
		bodyModel[63].rotateAngleY = -3.14159265F;

		bodyModel[64].addShapeBox(-8F, -1F, -0.5F, 6, 8, 1, 0F, 0F, -5F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, -0.4F, 0F, -1F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -0.4F); // Box 42
		bodyModel[64].setRotationPoint(0F, -5F, 0F);
		bodyModel[64].rotateAngleY = 3.92699082F;

		bodyModel[65].addShapeBox(-8F, -1F, -0.5F, 6, 8, 1, 0F, 0F, -5F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, -0.4F, 0F, -1F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -0.4F); // Box 43
		bodyModel[65].setRotationPoint(0F, -5F, 0F);
		bodyModel[65].rotateAngleY = 5.49778714F;

		bodyModel[66].addShapeBox(-8F, -1F, -0.5F, 6, 8, 1, 0F, 0F, -5F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, -0.4F, 0F, -1F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -0.4F); // Box 44
		bodyModel[66].setRotationPoint(0F, -5F, 0F);
		bodyModel[66].rotateAngleY = 0.78539816F;

		bodyModel[67].addShapeBox(-8F, -1F, -0.5F, 6, 8, 1, 0F, 0F, -5F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -5F, -0.4F, 0F, -1F, -0.4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -0.4F); // Box 45
		bodyModel[67].setRotationPoint(0F, -5F, 0F);
		bodyModel[67].rotateAngleY = 2.35619449F;
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
