package defense.client.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.tmt.ModelRendererTurbo;

@SideOnly(Side.CLIENT)
public class ModelLauncherFrame extends ModelBase
{
    // fields
    ModelRendererTurbo[] bodyModel;

    public ModelLauncherFrame()
    {
		int textureX = 512;
		int textureY = 512;

		bodyModel = new ModelRendererTurbo[106];
		bodyModel[0] = new ModelRendererTurbo(this, 50, 0, textureX, textureY); // Box 0
		bodyModel[1] = new ModelRendererTurbo(this, 50, 52, textureX, textureY); // Box 1
		bodyModel[2] = new ModelRendererTurbo(this, 50, 100, textureX, textureY); // Box 2
		bodyModel[3] = new ModelRendererTurbo(this, 50, 100, textureX, textureY); // Box 3
		bodyModel[4] = new ModelRendererTurbo(this, 50, 107, textureX, textureY); // Box 4
		bodyModel[5] = new ModelRendererTurbo(this, 50, 122, textureX, textureY); // Box 5
		bodyModel[6] = new ModelRendererTurbo(this, 50, 122, textureX, textureY); // Box 6
		bodyModel[7] = new ModelRendererTurbo(this, 50, 0, textureX, textureY); // Box 7
		bodyModel[8] = new ModelRendererTurbo(this, 50, 100, textureX, textureY); // Box 8
		bodyModel[9] = new ModelRendererTurbo(this, 50, 100, textureX, textureY); // Box 9
		bodyModel[10] = new ModelRendererTurbo(this, 50, 0, textureX, textureY); // Box 10
		bodyModel[11] = new ModelRendererTurbo(this, 50, 100, textureX, textureY); // Box 11
		bodyModel[12] = new ModelRendererTurbo(this, 50, 100, textureX, textureY); // Box 12
		bodyModel[13] = new ModelRendererTurbo(this, 50, 122, textureX, textureY); // Box 13
		bodyModel[14] = new ModelRendererTurbo(this, 50, 107, textureX, textureY); // Box 14
		bodyModel[15] = new ModelRendererTurbo(this, 50, 122, textureX, textureY); // Box 15
		bodyModel[16] = new ModelRendererTurbo(this, 50, 0, textureX, textureY); // Box 16
		bodyModel[17] = new ModelRendererTurbo(this, 50, 100, textureX, textureY); // Box 17
		bodyModel[18] = new ModelRendererTurbo(this, 50, 100, textureX, textureY); // Box 18
		bodyModel[19] = new ModelRendererTurbo(this, 50, 122, textureX, textureY); // Box 19
		bodyModel[20] = new ModelRendererTurbo(this, 50, 107, textureX, textureY); // Box 20
		bodyModel[21] = new ModelRendererTurbo(this, 50, 122, textureX, textureY); // Box 21
		bodyModel[22] = new ModelRendererTurbo(this, 50, 129, textureX, textureY); // Box 22
		bodyModel[23] = new ModelRendererTurbo(this, 50, 129, textureX, textureY); // Box 23
		bodyModel[24] = new ModelRendererTurbo(this, 50, 129, textureX, textureY); // Box 24
		bodyModel[25] = new ModelRendererTurbo(this, 50, 129, textureX, textureY); // Box 25
		bodyModel[26] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 26
		bodyModel[27] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 27
		bodyModel[28] = new ModelRendererTurbo(this, 50, 191, textureX, textureY); // Box 28
		bodyModel[29] = new ModelRendererTurbo(this, 50, 191, textureX, textureY); // Box 29
		bodyModel[30] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 30
		bodyModel[31] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 31
		bodyModel[32] = new ModelRendererTurbo(this, 50, 191, textureX, textureY); // Box 32
		bodyModel[33] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 33
		bodyModel[34] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 34
		bodyModel[35] = new ModelRendererTurbo(this, 50, 191, textureX, textureY); // Box 35
		bodyModel[36] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 36
		bodyModel[37] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 37
		bodyModel[38] = new ModelRendererTurbo(this, 50, 198, textureX, textureY); // Box 38
		bodyModel[39] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 39
		bodyModel[40] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 40
		bodyModel[41] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 41
		bodyModel[42] = new ModelRendererTurbo(this, 50, 198, textureX, textureY); // Box 42
		bodyModel[43] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 43
		bodyModel[44] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 44
		bodyModel[45] = new ModelRendererTurbo(this, 50, 198, textureX, textureY); // Box 45
		bodyModel[46] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 46
		bodyModel[47] = new ModelRendererTurbo(this, 50, 198, textureX, textureY); // Box 47
		bodyModel[48] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 48
		bodyModel[49] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 49
		bodyModel[50] = new ModelRendererTurbo(this, 50, 191, textureX, textureY); // Box 50
		bodyModel[51] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 51
		bodyModel[52] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 52
		bodyModel[53] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 53
		bodyModel[54] = new ModelRendererTurbo(this, 50, 191, textureX, textureY); // Box 54
		bodyModel[55] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 55
		bodyModel[56] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 56
		bodyModel[57] = new ModelRendererTurbo(this, 50, 191, textureX, textureY); // Box 57
		bodyModel[58] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 58
		bodyModel[59] = new ModelRendererTurbo(this, 50, 191, textureX, textureY); // Box 59
		bodyModel[60] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 60
		bodyModel[61] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 61
		bodyModel[62] = new ModelRendererTurbo(this, 50, 198, textureX, textureY); // Box 62
		bodyModel[63] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 63
		bodyModel[64] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 64
		bodyModel[65] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 65
		bodyModel[66] = new ModelRendererTurbo(this, 50, 198, textureX, textureY); // Box 66
		bodyModel[67] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 67
		bodyModel[68] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 68
		bodyModel[69] = new ModelRendererTurbo(this, 50, 198, textureX, textureY); // Box 69
		bodyModel[70] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 70
		bodyModel[71] = new ModelRendererTurbo(this, 50, 198, textureX, textureY); // Box 71
		bodyModel[72] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 72
		bodyModel[73] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 73
		bodyModel[74] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 74
		bodyModel[75] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 75
		bodyModel[76] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 76
		bodyModel[77] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 77
		bodyModel[78] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 78
		bodyModel[79] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 79
		bodyModel[80] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 80
		bodyModel[81] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 81
		bodyModel[82] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 82
		bodyModel[83] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 83
		bodyModel[84] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 84
		bodyModel[85] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 85
		bodyModel[86] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 86
		bodyModel[87] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 87
		bodyModel[88] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 88
		bodyModel[89] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 89
		bodyModel[90] = new ModelRendererTurbo(this, 50, 198, textureX, textureY); // Box 90
		bodyModel[91] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 91
		bodyModel[92] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 92
		bodyModel[93] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 93
		bodyModel[94] = new ModelRendererTurbo(this, 50, 191, textureX, textureY); // Box 94
		bodyModel[95] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 95
		bodyModel[96] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 96
		bodyModel[97] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 97
		bodyModel[98] = new ModelRendererTurbo(this, 50, 191, textureX, textureY); // Box 98
		bodyModel[99] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 99
		bodyModel[100] = new ModelRendererTurbo(this, 50, 179, textureX, textureY); // Box 100
		bodyModel[101] = new ModelRendererTurbo(this, 50, 198, textureX, textureY); // Box 101
		bodyModel[102] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 102
		bodyModel[103] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 103
		bodyModel[104] = new ModelRendererTurbo(this, 50, 205, textureX, textureY); // Box 104
		bodyModel[105] = new ModelRendererTurbo(this, 50, 210, textureX, textureY); // Box 105

		bodyModel[0].addBox(0F, 0F, 0F, 16, 2, 12, 0F); // Box 0
		bodyModel[0].setRotationPoint(-8F, 21F, -6F);

		bodyModel[1].addBox(0F, 0F, 0F, 1, 44, 1, 0F); // Box 1
		bodyModel[1].setRotationPoint(-0.5F, -32F, -0.5F);

		bodyModel[2].addShapeBox(0F, 0F, 0F, 16, 2, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 2
		bodyModel[2].setRotationPoint(-8F, 21F, -8F);

		bodyModel[3].addShapeBox(0F, 0F, 0F, 16, 2, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Box 3
		bodyModel[3].setRotationPoint(-8F, 21F, 6F);

		bodyModel[4].addBox(0F, 0F, 0F, 14, 1, 10, 0F); // Box 4
		bodyModel[4].setRotationPoint(-7F, 20F, -5F);

		bodyModel[5].addShapeBox(0F, 0F, 0F, 14, 1, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 5
		bodyModel[5].setRotationPoint(-7F, 20F, -7F);

		bodyModel[6].addShapeBox(0F, 0F, 0F, 14, 1, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Box 6
		bodyModel[6].setRotationPoint(-7F, 20F, 5F);

		bodyModel[7].addBox(0F, 0F, 0F, 16, 2, 12, 0F); // Box 7
		bodyModel[7].setRotationPoint(-8F, 18F, -6F);

		bodyModel[8].addShapeBox(0F, 0F, 0F, 16, 2, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 8
		bodyModel[8].setRotationPoint(-8F, 18F, -8F);

		bodyModel[9].addShapeBox(0F, 0F, 0F, 16, 2, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Box 9
		bodyModel[9].setRotationPoint(-8F, 18F, 6F);

		bodyModel[10].addBox(0F, 0F, 0F, 16, 2, 12, 0F); // Box 10
		bodyModel[10].setRotationPoint(-8F, 15F, -6F);

		bodyModel[11].addShapeBox(0F, 0F, 0F, 16, 2, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 11
		bodyModel[11].setRotationPoint(-8F, 15F, -8F);

		bodyModel[12].addShapeBox(0F, 0F, 0F, 16, 2, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Box 12
		bodyModel[12].setRotationPoint(-8F, 15F, 6F);

		bodyModel[13].addShapeBox(0F, 0F, 0F, 14, 1, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 13
		bodyModel[13].setRotationPoint(-7F, 17F, -7F);

		bodyModel[14].addBox(0F, 0F, 0F, 14, 1, 10, 0F); // Box 14
		bodyModel[14].setRotationPoint(-7F, 17F, -5F);

		bodyModel[15].addShapeBox(0F, 0F, 0F, 14, 1, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Box 15
		bodyModel[15].setRotationPoint(-7F, 17F, 5F);

		bodyModel[16].addBox(0F, 0F, 0F, 16, 2, 12, 0F); // Box 16
		bodyModel[16].setRotationPoint(-8F, 12F, -6F);

		bodyModel[17].addShapeBox(0F, 0F, 0F, 16, 2, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 17
		bodyModel[17].setRotationPoint(-8F, 12F, -8F);

		bodyModel[18].addShapeBox(0F, 0F, 0F, 16, 2, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Box 18
		bodyModel[18].setRotationPoint(-8F, 12F, 6F);

		bodyModel[19].addShapeBox(0F, 0F, 0F, 14, 1, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 19
		bodyModel[19].setRotationPoint(-7F, 14F, -7F);

		bodyModel[20].addBox(0F, 0F, 0F, 14, 1, 10, 0F); // Box 20
		bodyModel[20].setRotationPoint(-7F, 14F, -5F);

		bodyModel[21].addShapeBox(0F, 0F, 0F, 14, 1, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Box 21
		bodyModel[21].setRotationPoint(-7F, 14F, 5F);

		bodyModel[22].addBox(0F, 0F, 0F, 2, 44, 2, 0F); // Box 22
		bodyModel[22].setRotationPoint(-6F, -32F, -6F);

		bodyModel[23].addBox(0F, 0F, 0F, 2, 44, 2, 0F); // Box 23
		bodyModel[23].setRotationPoint(-6F, -32F, 4F);

		bodyModel[24].addBox(0F, 0F, 0F, 2, 44, 2, 0F); // Box 24
		bodyModel[24].setRotationPoint(4F, -32F, 4F);

		bodyModel[25].addBox(0F, 0F, 0F, 2, 44, 2, 0F); // Box 25
		bodyModel[25].setRotationPoint(4F, -32F, -6F);

		bodyModel[26].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F); // Box 26
		bodyModel[26].setRotationPoint(-5.5F, -32F, -4F);

		bodyModel[27].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 27
		bodyModel[27].setRotationPoint(-5.5F, -32F, -4F);

		bodyModel[28].addShapeBox(0F, 0F, 0F, 1, 2, 2, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F); // Box 28
		bodyModel[28].setRotationPoint(-5.5F, -28.5F, -1F);

		bodyModel[29].addShapeBox(0F, 0F, 0F, 1, 2, 2, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F); // Box 29
		bodyModel[29].setRotationPoint(-5.5F, -19.5F, -1F);

		bodyModel[30].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F); // Box 30
		bodyModel[30].setRotationPoint(-5.5F, -23F, -4F);

		bodyModel[31].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 31
		bodyModel[31].setRotationPoint(-5.5F, -23F, -4F);

		bodyModel[32].addShapeBox(0F, 0F, 0F, 1, 2, 2, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F); // Box 32
		bodyModel[32].setRotationPoint(-5.5F, -10.5F, -1F);

		bodyModel[33].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F); // Box 33
		bodyModel[33].setRotationPoint(-5.5F, -14F, -4F);

		bodyModel[34].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 34
		bodyModel[34].setRotationPoint(-5.5F, -14F, -4F);

		bodyModel[35].addShapeBox(0F, 0F, 0F, 1, 2, 2, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F); // Box 35
		bodyModel[35].setRotationPoint(-5.5F, -1.5F, -1F);

		bodyModel[36].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F); // Box 36
		bodyModel[36].setRotationPoint(-5.5F, -5F, -4F);

		bodyModel[37].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 37
		bodyModel[37].setRotationPoint(-5.5F, -5F, -4F);

		bodyModel[38].addShapeBox(0F, 0F, 0F, 2, 2, 1, 0F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F); // Box 38
		bodyModel[38].setRotationPoint(-1F, -1.5F, -5.5F);

		bodyModel[39].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F); // Box 39
		bodyModel[39].setRotationPoint(-4F, -5F, -5.5F);

		bodyModel[40].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F); // Box 40
		bodyModel[40].setRotationPoint(-4F, -5F, -5.5F);

		bodyModel[41].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F); // Box 41
		bodyModel[41].setRotationPoint(-4F, -14F, -5.5F);

		bodyModel[42].addShapeBox(0F, 0F, 0F, 2, 2, 1, 0F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F); // Box 42
		bodyModel[42].setRotationPoint(-1F, -10.5F, -5.5F);

		bodyModel[43].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F); // Box 43
		bodyModel[43].setRotationPoint(-4F, -14F, -5.5F);

		bodyModel[44].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F); // Box 44
		bodyModel[44].setRotationPoint(-4F, -23F, -5.5F);

		bodyModel[45].addShapeBox(0F, 0F, 0F, 2, 2, 1, 0F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F); // Box 45
		bodyModel[45].setRotationPoint(-1F, -19.5F, -5.5F);

		bodyModel[46].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F); // Box 46
		bodyModel[46].setRotationPoint(-4F, -23F, -5.5F);

		bodyModel[47].addShapeBox(0F, 0F, 0F, 2, 2, 1, 0F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F); // Box 47
		bodyModel[47].setRotationPoint(-1F, -28.5F, -5.5F);

		bodyModel[48].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F); // Box 48
		bodyModel[48].setRotationPoint(-4F, -32F, -5.5F);

		bodyModel[49].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F); // Box 49
		bodyModel[49].setRotationPoint(-4F, -32F, -5.5F);

		bodyModel[50].addShapeBox(0F, 0F, 0F, 1, 2, 2, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F); // Box 50
		bodyModel[50].setRotationPoint(4.5F, -1.5F, -1F);

		bodyModel[51].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 51
		bodyModel[51].setRotationPoint(4.5F, -5F, -4F);

		bodyModel[52].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F); // Box 52
		bodyModel[52].setRotationPoint(4.5F, -5F, -4F);

		bodyModel[53].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 53
		bodyModel[53].setRotationPoint(4.5F, -14F, -4F);

		bodyModel[54].addShapeBox(0F, 0F, 0F, 1, 2, 2, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F); // Box 54
		bodyModel[54].setRotationPoint(4.5F, -10.5F, -1F);

		bodyModel[55].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F); // Box 55
		bodyModel[55].setRotationPoint(4.5F, -14F, -4F);

		bodyModel[56].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F); // Box 56
		bodyModel[56].setRotationPoint(4.5F, -23F, -4F);

		bodyModel[57].addShapeBox(0F, 0F, 0F, 1, 2, 2, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F); // Box 57
		bodyModel[57].setRotationPoint(4.5F, -19.5F, -1F);

		bodyModel[58].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 58
		bodyModel[58].setRotationPoint(4.5F, -23F, -4F);

		bodyModel[59].addShapeBox(0F, 0F, 0F, 1, 2, 2, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F); // Box 59
		bodyModel[59].setRotationPoint(4.5F, -28.5F, -1F);

		bodyModel[60].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F); // Box 60
		bodyModel[60].setRotationPoint(4.5F, -32F, -4F);

		bodyModel[61].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 61
		bodyModel[61].setRotationPoint(4.5F, -32F, -4F);

		bodyModel[62].addShapeBox(0F, 0F, 0F, 2, 2, 1, 0F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F); // Box 62
		bodyModel[62].setRotationPoint(-1F, -1.5F, 4.5F);

		bodyModel[63].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F); // Box 63
		bodyModel[63].setRotationPoint(-4F, -5F, 4.5F);

		bodyModel[64].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F); // Box 64
		bodyModel[64].setRotationPoint(-4F, -5F, 4.5F);

		bodyModel[65].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F); // Box 65
		bodyModel[65].setRotationPoint(-4F, -14F, 4.5F);

		bodyModel[66].addShapeBox(0F, 0F, 0F, 2, 2, 1, 0F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F); // Box 66
		bodyModel[66].setRotationPoint(-1F, -10.5F, 4.5F);

		bodyModel[67].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F); // Box 67
		bodyModel[67].setRotationPoint(-4F, -14F, 4.5F);

		bodyModel[68].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F); // Box 68
		bodyModel[68].setRotationPoint(-4F, -23F, 4.5F);

		bodyModel[69].addShapeBox(0F, 0F, 0F, 2, 2, 1, 0F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F); // Box 69
		bodyModel[69].setRotationPoint(-1F, -19.5F, 4.5F);

		bodyModel[70].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F); // Box 70
		bodyModel[70].setRotationPoint(-4F, -23F, 4.5F);

		bodyModel[71].addShapeBox(0F, 0F, 0F, 2, 2, 1, 0F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F); // Box 71
		bodyModel[71].setRotationPoint(-1F, -28.5F, 4.5F);

		bodyModel[72].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F); // Box 72
		bodyModel[72].setRotationPoint(-4F, -32F, 4.5F);

		bodyModel[73].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F); // Box 73
		bodyModel[73].setRotationPoint(-4F, -32F, 4.5F);

		bodyModel[74].addShapeBox(0F, 0F, 0F, 1, 1, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 74
		bodyModel[74].setRotationPoint(-0.5F, -28F, -4.5F);

		bodyModel[75].addShapeBox(0F, 0F, 0F, 1, 1, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 75
		bodyModel[75].setRotationPoint(-0.5F, -19F, -4.5F);

		bodyModel[76].addShapeBox(0F, 0F, 0F, 1, 1, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 76
		bodyModel[76].setRotationPoint(-0.5F, -10F, -4.5F);

		bodyModel[77].addShapeBox(0F, 0F, 0F, 1, 1, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 77
		bodyModel[77].setRotationPoint(-0.5F, -1F, -4.5F);

		bodyModel[78].addShapeBox(0F, 0F, 0F, 4, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 78
		bodyModel[78].setRotationPoint(0.5F, -1F, -0.5F);

		bodyModel[79].addShapeBox(0F, 0F, 0F, 4, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 79
		bodyModel[79].setRotationPoint(0.5F, -10F, -0.5F);

		bodyModel[80].addShapeBox(0F, 0F, 0F, 4, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 80
		bodyModel[80].setRotationPoint(0.5F, -19F, -0.5F);

		bodyModel[81].addShapeBox(0F, 0F, 0F, 4, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 81
		bodyModel[81].setRotationPoint(0.5F, -28F, -0.5F);

		bodyModel[82].addShapeBox(0F, 0F, 0F, 1, 1, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 82
		bodyModel[82].setRotationPoint(-0.5F, -1F, 0.5F);

		bodyModel[83].addShapeBox(0F, 0F, 0F, 1, 1, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 83
		bodyModel[83].setRotationPoint(-0.5F, -10F, 0.5F);

		bodyModel[84].addShapeBox(0F, 0F, 0F, 1, 1, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 84
		bodyModel[84].setRotationPoint(-0.5F, -19F, 0.5F);

		bodyModel[85].addShapeBox(0F, 0F, 0F, 1, 1, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 85
		bodyModel[85].setRotationPoint(-0.5F, -28F, 0.5F);

		bodyModel[86].addShapeBox(0F, 0F, 0F, 4, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 86
		bodyModel[86].setRotationPoint(-4.5F, -1F, -0.5F);

		bodyModel[87].addShapeBox(0F, 0F, 0F, 4, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 87
		bodyModel[87].setRotationPoint(-4.5F, -10F, -0.5F);

		bodyModel[88].addShapeBox(0F, 0F, 0F, 4, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 88
		bodyModel[88].setRotationPoint(-4.5F, -19F, -0.5F);

		bodyModel[89].addShapeBox(0F, 0F, 0F, 4, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 89
		bodyModel[89].setRotationPoint(-4.5F, -28F, -0.5F);

		bodyModel[90].addShapeBox(0F, 0F, 0F, 2, 2, 1, 0F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F); // Box 90
		bodyModel[90].setRotationPoint(-1F, 7.5F, 4.5F);

		bodyModel[91].addShapeBox(0F, 0F, 0F, 1, 1, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 91
		bodyModel[91].setRotationPoint(-0.5F, 8F, 0.5F);

		bodyModel[92].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F); // Box 92
		bodyModel[92].setRotationPoint(4.5F, 4F, -4F);

		bodyModel[93].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 93
		bodyModel[93].setRotationPoint(4.5F, 4F, -4F);

		bodyModel[94].addShapeBox(0F, 0F, 0F, 1, 2, 2, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F); // Box 94
		bodyModel[94].setRotationPoint(4.5F, 7.5F, -1F);

		bodyModel[95].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F); // Box 95
		bodyModel[95].setRotationPoint(-4F, 4F, 4.5F);

		bodyModel[96].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F); // Box 96
		bodyModel[96].setRotationPoint(-4F, 4F, 4.5F);

		bodyModel[97].addShapeBox(0F, 0F, 0F, 4, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 97
		bodyModel[97].setRotationPoint(-4.5F, 8F, -0.5F);

		bodyModel[98].addShapeBox(0F, 0F, 0F, 1, 2, 2, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F); // Box 98
		bodyModel[98].setRotationPoint(-5.5F, 7.5F, -1F);

		bodyModel[99].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 99
		bodyModel[99].setRotationPoint(-5.5F, 4F, -4F);

		bodyModel[100].addShapeBox(0F, 0F, 0F, 1, 1, 8, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F); // Box 100
		bodyModel[100].setRotationPoint(-5.5F, 4F, -4F);

		bodyModel[101].addShapeBox(0F, 0F, 0F, 2, 2, 1, 0F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F, 0F, 0F, 0.2F); // Box 101
		bodyModel[101].setRotationPoint(-1F, 7.5F, -5.5F);

		bodyModel[102].addShapeBox(0F, 0F, 0F, 1, 1, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 102
		bodyModel[102].setRotationPoint(-0.5F, 8F, -4.5F);

		bodyModel[103].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F); // Box 103
		bodyModel[103].setRotationPoint(-4F, 4F, -5.5F);

		bodyModel[104].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -8F, 0F, 0F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 8F, 0F); // Box 104
		bodyModel[104].setRotationPoint(-4F, 4F, -5.5F);

		bodyModel[105].addShapeBox(0F, 0F, 0F, 4, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 105
		bodyModel[105].setRotationPoint(0.5F, 8F, -0.5F);

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
