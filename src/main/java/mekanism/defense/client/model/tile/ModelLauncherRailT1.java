package defense.client.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.tmt.ModelRendererTurbo;

@SideOnly(Side.CLIENT)
public class ModelLauncherRailT1 extends ModelBase
{
    // fields
    // fields
    ModelRendererTurbo[] noseModel;

    public ModelLauncherRailT1()
    {
		int textureX = 512;
		int textureY = 512;

		
		noseModel = new ModelRendererTurbo[144];
		noseModel[0] = new ModelRendererTurbo(this, 0, 94, textureX, textureY); // Box 13
		noseModel[1] = new ModelRendererTurbo(this, 0, 105, textureX, textureY); // Box 14
		noseModel[2] = new ModelRendererTurbo(this, 0, 119, textureX, textureY); // Box 15
		noseModel[3] = new ModelRendererTurbo(this, 0, 132, textureX, textureY); // Box 16
		noseModel[4] = new ModelRendererTurbo(this, 0, 145, textureX, textureY); // Box 21
		noseModel[5] = new ModelRendererTurbo(this, 0, 154, textureX, textureY); // Box 22
		noseModel[6] = new ModelRendererTurbo(this, 0, 160, textureX, textureY); // Box 23
		noseModel[7] = new ModelRendererTurbo(this, 0, 160, textureX, textureY); // Box 24
		noseModel[8] = new ModelRendererTurbo(this, 0, 165, textureX, textureY); // Box 25
		noseModel[9] = new ModelRendererTurbo(this, 0, 172, textureX, textureY); // Box 26
		noseModel[10] = new ModelRendererTurbo(this, 0, 183, textureX, textureY); // Box 27
		noseModel[11] = new ModelRendererTurbo(this, 0, 194, textureX, textureY); // Box 28
		noseModel[12] = new ModelRendererTurbo(this, 0, 226, textureX, textureY); // Box 29
		noseModel[13] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 30
		noseModel[14] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 31
		noseModel[15] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 32
		noseModel[16] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 33
		noseModel[17] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 34
		noseModel[18] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 35
		noseModel[19] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 36
		noseModel[20] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 37
		noseModel[21] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 38
		noseModel[22] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 39
		noseModel[23] = new ModelRendererTurbo(this, 0, 94, textureX, textureY); // Box 40
		noseModel[24] = new ModelRendererTurbo(this, 0, 105, textureX, textureY); // Box 41
		noseModel[25] = new ModelRendererTurbo(this, 0, 119, textureX, textureY); // Box 42
		noseModel[26] = new ModelRendererTurbo(this, 0, 132, textureX, textureY); // Box 43
		noseModel[27] = new ModelRendererTurbo(this, 0, 145, textureX, textureY); // Box 44
		noseModel[28] = new ModelRendererTurbo(this, 0, 154, textureX, textureY); // Box 45
		noseModel[29] = new ModelRendererTurbo(this, 0, 160, textureX, textureY); // Box 46
		noseModel[30] = new ModelRendererTurbo(this, 0, 160, textureX, textureY); // Box 47
		noseModel[31] = new ModelRendererTurbo(this, 0, 165, textureX, textureY); // Box 48
		noseModel[32] = new ModelRendererTurbo(this, 0, 172, textureX, textureY); // Box 49
		noseModel[33] = new ModelRendererTurbo(this, 0, 183, textureX, textureY); // Box 50
		noseModel[34] = new ModelRendererTurbo(this, 0, 194, textureX, textureY); // Box 51
		noseModel[35] = new ModelRendererTurbo(this, 0, 226, textureX, textureY); // Box 52
		noseModel[36] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 53
		noseModel[37] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 54
		noseModel[38] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 55
		noseModel[39] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 56
		noseModel[40] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 57
		noseModel[41] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 58
		noseModel[42] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 59
		noseModel[43] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 60
		noseModel[44] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 61
		noseModel[45] = new ModelRendererTurbo(this, 0, 257, textureX, textureY); // Box 62
		noseModel[46] = new ModelRendererTurbo(this, 0, 266, textureX, textureY); // Box 63
		noseModel[47] = new ModelRendererTurbo(this, 0, 275, textureX, textureY); // Box 64
		noseModel[48] = new ModelRendererTurbo(this, 0, 284, textureX, textureY); // Box 65
		noseModel[49] = new ModelRendererTurbo(this, 0, 291, textureX, textureY); // Box 66
		noseModel[50] = new ModelRendererTurbo(this, 0, 291, textureX, textureY); // Box 67
		noseModel[51] = new ModelRendererTurbo(this, 0, 291, textureX, textureY); // Box 68
		noseModel[52] = new ModelRendererTurbo(this, 0, 296, textureX, textureY); // Box 70
		noseModel[53] = new ModelRendererTurbo(this, 0, 296, textureX, textureY); // Box 71
		noseModel[54] = new ModelRendererTurbo(this, 0, 296, textureX, textureY); // Box 72
		noseModel[55] = new ModelRendererTurbo(this, 0, 296, textureX, textureY); // Box 73
		noseModel[56] = new ModelRendererTurbo(this, 0, 296, textureX, textureY); // Box 74
		noseModel[57] = new ModelRendererTurbo(this, 0, 300, textureX, textureY); // Box 75
		noseModel[58] = new ModelRendererTurbo(this, 0, 300, textureX, textureY); // Box 76
		noseModel[59] = new ModelRendererTurbo(this, 0, 296, textureX, textureY); // Box 77
		noseModel[60] = new ModelRendererTurbo(this, 0, 296, textureX, textureY); // Box 78
		noseModel[61] = new ModelRendererTurbo(this, 0, 296, textureX, textureY); // Box 79
		noseModel[62] = new ModelRendererTurbo(this, 0, 296, textureX, textureY); // Box 80
		noseModel[63] = new ModelRendererTurbo(this, 0, 296, textureX, textureY); // Box 81
		noseModel[64] = new ModelRendererTurbo(this, 0, 266, textureX, textureY); // Box 82
		noseModel[65] = new ModelRendererTurbo(this, 0, 284, textureX, textureY); // Box 83
		noseModel[66] = new ModelRendererTurbo(this, 0, 275, textureX, textureY); // Box 84
		noseModel[67] = new ModelRendererTurbo(this, 0, 291, textureX, textureY); // Box 85
		noseModel[68] = new ModelRendererTurbo(this, 0, 291, textureX, textureY); // Box 86
		noseModel[69] = new ModelRendererTurbo(this, 0, 291, textureX, textureY); // Box 87
		noseModel[70] = new ModelRendererTurbo(this, 0, 340, textureX, textureY); // Box 88
		noseModel[71] = new ModelRendererTurbo(this, 0, 371, textureX, textureY); // Box 89
		noseModel[72] = new ModelRendererTurbo(this, 0, 371, textureX, textureY); // Box 90
		noseModel[73] = new ModelRendererTurbo(this, 0, 371, textureX, textureY); // Box 91
		noseModel[74] = new ModelRendererTurbo(this, 0, 371, textureX, textureY); // Box 92
		noseModel[75] = new ModelRendererTurbo(this, 0, 371, textureX, textureY); // Box 93
		noseModel[76] = new ModelRendererTurbo(this, 0, 371, textureX, textureY); // Box 94
		noseModel[77] = new ModelRendererTurbo(this, 0, 371, textureX, textureY); // Box 95
		noseModel[78] = new ModelRendererTurbo(this, 0, 371, textureX, textureY); // Box 96
		noseModel[79] = new ModelRendererTurbo(this, 0, 340, textureX, textureY); // Box 97
		noseModel[80] = new ModelRendererTurbo(this, 0, 371, textureX, textureY); // Box 98
		noseModel[81] = new ModelRendererTurbo(this, 0, 371, textureX, textureY); // Box 99
		noseModel[82] = new ModelRendererTurbo(this, 0, 371, textureX, textureY); // Box 100
		noseModel[83] = new ModelRendererTurbo(this, 0, 371, textureX, textureY); // Box 101
		noseModel[84] = new ModelRendererTurbo(this, 0, 371, textureX, textureY); // Box 102
		noseModel[85] = new ModelRendererTurbo(this, 0, 371, textureX, textureY); // Box 103
		noseModel[86] = new ModelRendererTurbo(this, 0, 371, textureX, textureY); // Box 104
		noseModel[87] = new ModelRendererTurbo(this, 0, 371, textureX, textureY); // Box 105
		noseModel[88] = new ModelRendererTurbo(this, 0, 375, textureX, textureY); // Box 107
		noseModel[89] = new ModelRendererTurbo(this, 0, 375, textureX, textureY); // Box 108
		noseModel[90] = new ModelRendererTurbo(this, 0, 375, textureX, textureY); // Box 109
		noseModel[91] = new ModelRendererTurbo(this, 0, 375, textureX, textureY); // Box 110
		noseModel[92] = new ModelRendererTurbo(this, 0, 375, textureX, textureY); // Box 111
		noseModel[93] = new ModelRendererTurbo(this, 0, 375, textureX, textureY); // Box 112
		noseModel[94] = new ModelRendererTurbo(this, 0, 375, textureX, textureY); // Box 113
		noseModel[95] = new ModelRendererTurbo(this, 0, 375, textureX, textureY); // Box 114
		noseModel[96] = new ModelRendererTurbo(this, 0, 375, textureX, textureY); // Box 115
		noseModel[97] = new ModelRendererTurbo(this, 0, 375, textureX, textureY); // Box 116
		noseModel[98] = new ModelRendererTurbo(this, 0, 375, textureX, textureY); // Box 117
		noseModel[99] = new ModelRendererTurbo(this, 0, 375, textureX, textureY); // Box 118
		noseModel[100] = new ModelRendererTurbo(this, 0, 375, textureX, textureY); // Box 119
		noseModel[101] = new ModelRendererTurbo(this, 0, 375, textureX, textureY); // Box 120
		noseModel[102] = new ModelRendererTurbo(this, 0, 375, textureX, textureY); // Box 121
		noseModel[103] = new ModelRendererTurbo(this, 0, 375, textureX, textureY); // Box 122
		noseModel[104] = new ModelRendererTurbo(this, 0, 384, textureX, textureY); // Box 123
		noseModel[105] = new ModelRendererTurbo(this, 0, 392, textureX, textureY); // Box 124
		noseModel[106] = new ModelRendererTurbo(this, 0, 399, textureX, textureY); // Box 125
		noseModel[107] = new ModelRendererTurbo(this, 0, 406, textureX, textureY); // Box 126
		noseModel[108] = new ModelRendererTurbo(this, 0, 406, textureX, textureY); // Box 127
		noseModel[109] = new ModelRendererTurbo(this, 0, 406, textureX, textureY); // Box 128
		noseModel[110] = new ModelRendererTurbo(this, 0, 406, textureX, textureY); // Box 129
		noseModel[111] = new ModelRendererTurbo(this, 0, 406, textureX, textureY); // Box 130
		noseModel[112] = new ModelRendererTurbo(this, 0, 406, textureX, textureY); // Box 131
		noseModel[113] = new ModelRendererTurbo(this, 0, 399, textureX, textureY); // Box 132
		noseModel[114] = new ModelRendererTurbo(this, 0, 384, textureX, textureY); // Box 133
		noseModel[115] = new ModelRendererTurbo(this, 0, 392, textureX, textureY); // Box 134
		noseModel[116] = new ModelRendererTurbo(this, 0, 406, textureX, textureY); // Box 135
		noseModel[117] = new ModelRendererTurbo(this, 0, 406, textureX, textureY); // Box 136
		noseModel[118] = new ModelRendererTurbo(this, 0, 406, textureX, textureY); // Box 137
		noseModel[119] = new ModelRendererTurbo(this, 0, 406, textureX, textureY); // Box 138
		noseModel[120] = new ModelRendererTurbo(this, 0, 411, textureX, textureY); // Box 139
		noseModel[121] = new ModelRendererTurbo(this, 0, 411, textureX, textureY); // Box 140
		noseModel[122] = new ModelRendererTurbo(this, 0, 411, textureX, textureY); // Box 141
		noseModel[123] = new ModelRendererTurbo(this, 0, 411, textureX, textureY); // Box 142
		noseModel[124] = new ModelRendererTurbo(this, 0, 411, textureX, textureY); // Box 143
		noseModel[125] = new ModelRendererTurbo(this, 0, 411, textureX, textureY); // Box 144
		noseModel[126] = new ModelRendererTurbo(this, 0, 411, textureX, textureY); // Box 145
		noseModel[127] = new ModelRendererTurbo(this, 0, 411, textureX, textureY); // Box 146
		noseModel[128] = new ModelRendererTurbo(this, 0, 411, textureX, textureY); // Box 147
		noseModel[129] = new ModelRendererTurbo(this, 0, 411, textureX, textureY); // Box 148
		noseModel[130] = new ModelRendererTurbo(this, 0, 411, textureX, textureY); // Box 149
		noseModel[131] = new ModelRendererTurbo(this, 0, 411, textureX, textureY); // Box 150
		noseModel[132] = new ModelRendererTurbo(this, 0, 411, textureX, textureY); // Box 151
		noseModel[133] = new ModelRendererTurbo(this, 0, 411, textureX, textureY); // Box 152
		noseModel[134] = new ModelRendererTurbo(this, 0, 411, textureX, textureY); // Box 153
		noseModel[135] = new ModelRendererTurbo(this, 0, 411, textureX, textureY); // Box 154
		noseModel[136] = new ModelRendererTurbo(this, 0, 418, textureX, textureY); // Box 155
		noseModel[137] = new ModelRendererTurbo(this, 0, 418, textureX, textureY); // Box 156
		noseModel[138] = new ModelRendererTurbo(this, 0, 418, textureX, textureY); // Box 159
		noseModel[139] = new ModelRendererTurbo(this, 0, 418, textureX, textureY); // Box 160
		noseModel[140] = new ModelRendererTurbo(this, 0, 418, textureX, textureY); // Box 161
		noseModel[141] = new ModelRendererTurbo(this, 0, 418, textureX, textureY); // Box 162
		noseModel[142] = new ModelRendererTurbo(this, 0, 418, textureX, textureY); // Box 165
		noseModel[143] = new ModelRendererTurbo(this, 0, 418, textureX, textureY); // Box 166

		noseModel[0].addShapeBox(0F, 0F, 0F, 10, 2, 6, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 13
		noseModel[0].setRotationPoint(-18F, 22F, -3F);

		noseModel[1].addShapeBox(0F, 0F, 0F, 10, 4, 6, 0F, -1F, 0F, 0F, -4F, 0F, 0F, -4F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 14
		noseModel[1].setRotationPoint(-18F, 18F, -3F);

		noseModel[2].addShapeBox(0F, 0F, 0F, 7, 2, 8, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 15
		noseModel[2].setRotationPoint(-17F, 22F, -4F);

		noseModel[3].addShapeBox(0F, 0F, 0F, 7, 2, 8, 0F, -1F, 0F, 0F, -3F, 0F, 0F, -3F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 16
		noseModel[3].setRotationPoint(-17F, 20F, -4F);

		noseModel[4].addShapeBox(0F, 0F, 0F, 4, 4, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 21
		noseModel[4].setRotationPoint(-20F, 20F, -1F);

		noseModel[5].addShapeBox(0F, 0F, 0F, 4, 1, 2, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 22
		noseModel[5].setRotationPoint(-20F, 19F, -1F);

		noseModel[6].addShapeBox(0F, 0F, 0F, 2, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 23
		noseModel[6].setRotationPoint(-22F, 21F, -0.5F);

		noseModel[7].addShapeBox(0F, 0F, 0F, 2, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 24
		noseModel[7].setRotationPoint(-22F, 23F, -0.5F);

		noseModel[8].addShapeBox(0F, 0F, 0F, 1, 4, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 25
		noseModel[8].setRotationPoint(-23F, 20F, -0.5F);

		noseModel[9].addShapeBox(0F, 0F, 0F, 3, 4, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 26
		noseModel[9].setRotationPoint(-17F, 14F, -2F);

		noseModel[10].addShapeBox(0F, 0F, 0F, 3, 4, 4, 0F, -3F, 0F, 0F, 4F, 0F, 0F, 4F, 0F, 0F, -3F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 27
		noseModel[10].setRotationPoint(-17F, 10F, -2F);

		noseModel[11].addShapeBox(0F, 0F, 0F, 4, 25, 4, 0F, 0F, 0F, 0F, -1F, -2F, 0F, -1F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 28
		noseModel[11].setRotationPoint(-14F, -15F, -2F);

		noseModel[12].addShapeBox(0F, 0F, 0F, 1, 25, 3, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Box 29
		noseModel[12].setRotationPoint(-15F, -13F, -1.5F);

		noseModel[13].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 30
		noseModel[13].setRotationPoint(-15F, -12F, -2F);

		noseModel[14].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Box 31
		noseModel[14].setRotationPoint(-16F, -12F, -2F);

		noseModel[15].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Box 32
		noseModel[15].setRotationPoint(-16F, -7F, -2F);

		noseModel[16].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 33
		noseModel[16].setRotationPoint(-15F, -7F, -2F);

		noseModel[17].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Box 34
		noseModel[17].setRotationPoint(-16F, -2F, -2F);

		noseModel[18].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 35
		noseModel[18].setRotationPoint(-15F, -2F, -2F);

		noseModel[19].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Box 36
		noseModel[19].setRotationPoint(-16F, 3F, -2F);

		noseModel[20].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 37
		noseModel[20].setRotationPoint(-15F, 3F, -2F);

		noseModel[21].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F); // Box 38
		noseModel[21].setRotationPoint(-16F, 8F, -2F);

		noseModel[22].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 39
		noseModel[22].setRotationPoint(-15F, 8F, -2F);

		noseModel[23].addShapeBox(0F, 0F, 0F, 10, 2, 6, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 40
		noseModel[23].setRotationPoint(8F, 22F, -3F);

		noseModel[24].addShapeBox(0F, 0F, 0F, 10, 4, 6, 0F, -4F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 41
		noseModel[24].setRotationPoint(8F, 18F, -3F);

		noseModel[25].addShapeBox(0F, 0F, 0F, 7, 2, 8, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 42
		noseModel[25].setRotationPoint(10F, 22F, -4F);

		noseModel[26].addShapeBox(0F, 0F, 0F, 7, 2, 8, 0F, -3F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -3F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 43
		noseModel[26].setRotationPoint(10F, 20F, -4F);

		noseModel[27].addShapeBox(0F, 0F, 0F, 4, 4, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 44
		noseModel[27].setRotationPoint(16F, 20F, -1F);

		noseModel[28].addShapeBox(0F, 0F, 0F, 4, 1, 2, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 45
		noseModel[28].setRotationPoint(16F, 19F, -1F);

		noseModel[29].addShapeBox(0F, 0F, 0F, 2, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 46
		noseModel[29].setRotationPoint(20F, 21F, -0.5F);

		noseModel[30].addShapeBox(0F, 0F, 0F, 2, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 47
		noseModel[30].setRotationPoint(20F, 23F, -0.5F);

		noseModel[31].addShapeBox(0F, 0F, 0F, 1, 4, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 48
		noseModel[31].setRotationPoint(22F, 20F, -0.5F);

		noseModel[32].addShapeBox(0F, 0F, 0F, 3, 4, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 49
		noseModel[32].setRotationPoint(14F, 14F, -2F);

		noseModel[33].addShapeBox(0F, 0F, 0F, 3, 4, 4, 0F, 4F, 0F, 0F, -3F, 0F, 0F, -3F, 0F, 0F, 4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 50
		noseModel[33].setRotationPoint(14F, 10F, -2F);

		noseModel[34].addShapeBox(0F, 0F, 0F, 4, 25, 4, 0F, -1F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 51
		noseModel[34].setRotationPoint(10F, -15F, -2F);

		noseModel[35].addShapeBox(0F, 0F, 0F, 1, 25, 3, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F); // Box 52
		noseModel[35].setRotationPoint(14F, -13F, -1.5F);

		noseModel[36].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 53
		noseModel[36].setRotationPoint(14F, -12F, -2F);

		noseModel[37].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F); // Box 54
		noseModel[37].setRotationPoint(15F, -12F, -2F);

		noseModel[38].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F); // Box 55
		noseModel[38].setRotationPoint(15F, -7F, -2F);

		noseModel[39].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 56
		noseModel[39].setRotationPoint(14F, -7F, -2F);

		noseModel[40].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F); // Box 57
		noseModel[40].setRotationPoint(15F, -2F, -2F);

		noseModel[41].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 58
		noseModel[41].setRotationPoint(14F, -2F, -2F);

		noseModel[42].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F); // Box 59
		noseModel[42].setRotationPoint(15F, 3F, -2F);

		noseModel[43].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 60
		noseModel[43].setRotationPoint(14F, 3F, -2F);

		noseModel[44].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F); // Box 61
		noseModel[44].setRotationPoint(15F, 8F, -2F);

		noseModel[45].addShapeBox(0F, 0F, 0F, 1, 2, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 62
		noseModel[45].setRotationPoint(14F, 8F, -2F);

		noseModel[46].addShapeBox(0F, 0F, 0F, 3, 5, 2, 0F, -2F, 0F, -0.5F, 1F, 0F, -0.5F, 1F, 0F, -0.5F, -2F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 63
		noseModel[46].setRotationPoint(-14F, -18F, -1F);

		noseModel[47].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 64
		noseModel[47].setRotationPoint(-12F, -26F, -0.5F);

		noseModel[48].addShapeBox(0F, 0F, 0F, 1, 3, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F); // Box 65
		noseModel[48].setRotationPoint(-12F, -21F, -0.5F);

		noseModel[49].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 66
		noseModel[49].setRotationPoint(-11F, -26F, -0.5F);

		noseModel[50].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 67
		noseModel[50].setRotationPoint(-11F, -22F, -0.5F);

		noseModel[51].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 68
		noseModel[51].setRotationPoint(-11F, -24F, -0.5F);

		noseModel[52].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 70
		noseModel[52].setRotationPoint(-17F, -11.5F, -0.5F);

		noseModel[53].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 71
		noseModel[53].setRotationPoint(-17F, -6.5F, -0.5F);

		noseModel[54].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 72
		noseModel[54].setRotationPoint(-17F, -1.5F, -0.5F);

		noseModel[55].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 73
		noseModel[55].setRotationPoint(-17F, 3.5F, -0.5F);

		noseModel[56].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 74
		noseModel[56].setRotationPoint(-17F, 8.5F, -0.5F);

		noseModel[57].addShapeBox(0F, 0F, 0F, 1, 35, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F, 0F); // Box 75
		noseModel[57].setRotationPoint(-18F, -15F, -0.5F);

		noseModel[58].addShapeBox(0F, 0F, 0F, 1, 35, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F, 0F, 2F, 0F, 0F, 0F, 0F, 0F); // Box 76
		noseModel[58].setRotationPoint(17F, -15F, -0.5F);

		noseModel[59].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 77
		noseModel[59].setRotationPoint(16F, -11.5F, -0.5F);

		noseModel[60].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 78
		noseModel[60].setRotationPoint(16F, -6.5F, -0.5F);

		noseModel[61].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 79
		noseModel[61].setRotationPoint(16F, -1.5F, -0.5F);

		noseModel[62].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 80
		noseModel[62].setRotationPoint(16F, 3.5F, -0.5F);

		noseModel[63].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 81
		noseModel[63].setRotationPoint(16F, 8.5F, -0.5F);

		noseModel[64].addShapeBox(0F, 0F, 0F, 3, 5, 2, 0F, 1F, 0F, -0.5F, -2F, 0F, -0.5F, -2F, 0F, -0.5F, 1F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 82
		noseModel[64].setRotationPoint(11F, -18F, -1F);

		noseModel[65].addShapeBox(0F, 0F, 0F, 1, 3, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, 0F, 0F); // Box 83
		noseModel[65].setRotationPoint(11F, -21F, -0.5F);

		noseModel[66].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 84
		noseModel[66].setRotationPoint(11F, -26F, -0.5F);

		noseModel[67].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 85
		noseModel[67].setRotationPoint(10F, -22F, -0.5F);

		noseModel[68].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 86
		noseModel[68].setRotationPoint(10F, -24F, -0.5F);

		noseModel[69].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 87
		noseModel[69].setRotationPoint(10F, -26F, -0.5F);

		noseModel[70].addShapeBox(0F, 0F, 0F, 1, 27, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 88
		noseModel[70].setRotationPoint(8F, -19F, -0.5F);

		noseModel[71].addShapeBox(0F, 0F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F, 0F, 2F, 0F, 0F, 0F, 0F); // Box 89
		noseModel[71].setRotationPoint(9F, -15F, -0.5F);

		noseModel[72].addShapeBox(0F, 0F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F, 0F, 2F, 0F, 0F, 0F, 0F); // Box 90
		noseModel[72].setRotationPoint(9F, -12F, -0.5F);

		noseModel[73].addShapeBox(0F, 0F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F, 0F, 2F, 0F, 0F, 0F, 0F); // Box 91
		noseModel[73].setRotationPoint(9F, -9F, -0.5F);

		noseModel[74].addShapeBox(0F, 0F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F, 0F, 2F, 0F, 0F, 0F, 0F); // Box 92
		noseModel[74].setRotationPoint(9F, -6F, -0.5F);

		noseModel[75].addShapeBox(0F, 0F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F, 0F, 2F, 0F, 0F, 0F, 0F); // Box 93
		noseModel[75].setRotationPoint(9F, -3F, -0.5F);

		noseModel[76].addShapeBox(0F, 0F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F, 0F, 2F, 0F, 0F, 0F, 0F); // Box 94
		noseModel[76].setRotationPoint(9F, 0F, -0.5F);

		noseModel[77].addShapeBox(0F, 0F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F, 0F, 2F, 0F, 0F, 0F, 0F); // Box 95
		noseModel[77].setRotationPoint(9F, 3F, -0.5F);

		noseModel[78].addShapeBox(0F, 0F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F, 0F, 2F, 0F, 0F, 0F, 0F); // Box 96
		noseModel[78].setRotationPoint(9F, 6F, -0.5F);

		noseModel[79].addShapeBox(0F, 0F, 0F, 1, 27, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 97
		noseModel[79].setRotationPoint(-9F, -19F, -0.5F);

		noseModel[80].addShapeBox(0F, 0F, 0F, 3, 1, 1, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F); // Box 98
		noseModel[80].setRotationPoint(-12F, -15F, -0.5F);

		noseModel[81].addShapeBox(0F, 0F, 0F, 3, 1, 1, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F); // Box 99
		noseModel[81].setRotationPoint(-12F, -12F, -0.5F);

		noseModel[82].addShapeBox(0F, 0F, 0F, 3, 1, 1, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F); // Box 100
		noseModel[82].setRotationPoint(-12F, -9F, -0.5F);

		noseModel[83].addShapeBox(0F, 0F, 0F, 3, 1, 1, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F); // Box 101
		noseModel[83].setRotationPoint(-12F, -6F, -0.5F);

		noseModel[84].addShapeBox(0F, 0F, 0F, 3, 1, 1, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F); // Box 102
		noseModel[84].setRotationPoint(-12F, -3F, -0.5F);

		noseModel[85].addShapeBox(0F, 0F, 0F, 3, 1, 1, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F); // Box 103
		noseModel[85].setRotationPoint(-12F, 0F, -0.5F);

		noseModel[86].addShapeBox(0F, 0F, 0F, 3, 1, 1, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F); // Box 104
		noseModel[86].setRotationPoint(-12F, 3F, -0.5F);

		noseModel[87].addShapeBox(0F, 0F, 0F, 3, 1, 1, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 2F, 0F); // Box 105
		noseModel[87].setRotationPoint(-12F, 6F, -0.5F);

		noseModel[88].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F); // Box 107
		noseModel[88].setRotationPoint(-9F, -19F, 0.5F);

		noseModel[89].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 108
		noseModel[89].setRotationPoint(-9F, -19F, -1.5F);

		noseModel[90].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 109
		noseModel[90].setRotationPoint(-9F, -12F, -1.5F);

		noseModel[91].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F); // Box 110
		noseModel[91].setRotationPoint(-9F, -12F, 0.5F);

		noseModel[92].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 111
		noseModel[92].setRotationPoint(-9F, -4F, -1.5F);

		noseModel[93].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F); // Box 112
		noseModel[93].setRotationPoint(-9F, -4F, 0.5F);

		noseModel[94].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 113
		noseModel[94].setRotationPoint(-9F, 3F, -1.5F);

		noseModel[95].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F); // Box 114
		noseModel[95].setRotationPoint(-9F, 3F, 0.5F);

		noseModel[96].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F); // Box 115
		noseModel[96].setRotationPoint(8F, 3F, 0.5F);

		noseModel[97].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 116
		noseModel[97].setRotationPoint(8F, 3F, -1.5F);

		noseModel[98].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 117
		noseModel[98].setRotationPoint(8F, -4F, -1.5F);

		noseModel[99].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F); // Box 118
		noseModel[99].setRotationPoint(8F, -4F, 0.5F);

		noseModel[100].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F); // Box 119
		noseModel[100].setRotationPoint(8F, -12F, 0.5F);

		noseModel[101].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 120
		noseModel[101].setRotationPoint(8F, -12F, -1.5F);

		noseModel[102].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 121
		noseModel[102].setRotationPoint(8F, -19F, -1.5F);

		noseModel[103].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F); // Box 122
		noseModel[103].setRotationPoint(8F, -19F, 0.5F);

		noseModel[104].addShapeBox(0F, 0F, 0F, 6, 2, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 123
		noseModel[104].setRotationPoint(8F, 16F, -1F);

		noseModel[105].addShapeBox(0F, 0F, 0F, 6, 2, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Box 124
		noseModel[105].setRotationPoint(8F, 18F, -1F);

		noseModel[106].addShapeBox(0F, 0F, 0F, 6, 2, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 125
		noseModel[106].setRotationPoint(8F, 14F, -1F);

		noseModel[107].addShapeBox(-4F, -0.5F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 126
		noseModel[107].setRotationPoint(11F, 17F, -0.5F);

		noseModel[108].addShapeBox(-4F, -0.5F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 127
		noseModel[108].setRotationPoint(11F, 17F, -0.5F);
		noseModel[108].rotateAngleZ = 0.78539816F;

		noseModel[109].addShapeBox(-4F, -0.5F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 128
		noseModel[109].setRotationPoint(11F, 17F, -0.5F);
		noseModel[109].rotateAngleZ = -0.78539816F;

		noseModel[110].addShapeBox(-4F, -0.5F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 129
		noseModel[110].setRotationPoint(11F, 17F, -0.5F);
		noseModel[110].rotateAngleZ = -1.57079633F;

		noseModel[111].addShapeBox(-4F, -0.5F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 130
		noseModel[111].setRotationPoint(11F, 17F, -0.5F);
		noseModel[111].rotateAngleZ = -2.35619449F;

		noseModel[112].addShapeBox(-4F, -0.5F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 131
		noseModel[112].setRotationPoint(-11F, 17F, -0.5F);
		noseModel[112].rotateAngleZ = -2.35619449F;

		noseModel[113].addShapeBox(0F, 0F, 0F, 6, 2, 2, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 132
		noseModel[113].setRotationPoint(-14F, 14F, -1F);

		noseModel[114].addShapeBox(0F, 0F, 0F, 6, 2, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 133
		noseModel[114].setRotationPoint(-14F, 16F, -1F);

		noseModel[115].addShapeBox(0F, 0F, 0F, 6, 2, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F); // Box 134
		noseModel[115].setRotationPoint(-14F, 18F, -1F);

		noseModel[116].addShapeBox(-4F, -0.5F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 135
		noseModel[116].setRotationPoint(-11F, 17F, -0.5F);
		noseModel[116].rotateAngleZ = -3.14159265F;

		noseModel[117].addShapeBox(-4F, -0.5F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 136
		noseModel[117].setRotationPoint(-11F, 17F, -0.5F);
		noseModel[117].rotateAngleZ = -3.92699082F;

		noseModel[118].addShapeBox(-4F, -0.5F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 137
		noseModel[118].setRotationPoint(-11F, 17F, -0.5F);
		noseModel[118].rotateAngleZ = -0.78539816F;

		noseModel[119].addShapeBox(-4F, -0.5F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 138
		noseModel[119].setRotationPoint(-11F, 17F, -0.5F);
		noseModel[119].rotateAngleZ = -1.57079633F;

		noseModel[120].addShapeBox(0F, 0F, 0F, 1, 2, 1, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 139
		noseModel[120].setRotationPoint(8F, -17.5F, -2.5F);

		noseModel[121].addShapeBox(0F, 0F, 0F, 1, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F); // Box 140
		noseModel[121].setRotationPoint(8F, -17.5F, 1.5F);

		noseModel[122].addShapeBox(0F, 0F, 0F, 1, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F); // Box 141
		noseModel[122].setRotationPoint(8F, -10.5F, 1.5F);

		noseModel[123].addShapeBox(0F, 0F, 0F, 1, 2, 1, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 142
		noseModel[123].setRotationPoint(8F, -10.5F, -2.5F);

		noseModel[124].addShapeBox(0F, 0F, 0F, 1, 2, 1, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 143
		noseModel[124].setRotationPoint(-9F, -17.5F, -2.5F);

		noseModel[125].addShapeBox(0F, 0F, 0F, 1, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F); // Box 144
		noseModel[125].setRotationPoint(-9F, -17.5F, 1.5F);

		noseModel[126].addShapeBox(0F, 0F, 0F, 1, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F); // Box 145
		noseModel[126].setRotationPoint(-9F, -10.5F, 1.5F);

		noseModel[127].addShapeBox(0F, 0F, 0F, 1, 2, 1, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 146
		noseModel[127].setRotationPoint(-9F, -10.5F, -2.5F);

		noseModel[128].addShapeBox(0F, 0F, 0F, 1, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F); // Box 147
		noseModel[128].setRotationPoint(-9F, -2.5F, 1.5F);

		noseModel[129].addShapeBox(0F, 0F, 0F, 1, 2, 1, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 148
		noseModel[129].setRotationPoint(-9F, -2.5F, -2.5F);

		noseModel[130].addShapeBox(0F, 0F, 0F, 1, 2, 1, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 149
		noseModel[130].setRotationPoint(8F, -2.5F, -2.5F);

		noseModel[131].addShapeBox(0F, 0F, 0F, 1, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F); // Box 150
		noseModel[131].setRotationPoint(8F, -2.5F, 1.5F);

		noseModel[132].addShapeBox(0F, 0F, 0F, 1, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F); // Box 151
		noseModel[132].setRotationPoint(-9F, 4.5F, 1.5F);

		noseModel[133].addShapeBox(0F, 0F, 0F, 1, 2, 1, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 152
		noseModel[133].setRotationPoint(-9F, 4.5F, -2.5F);

		noseModel[134].addShapeBox(0F, 0F, 0F, 1, 2, 1, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 153
		noseModel[134].setRotationPoint(8F, 4.5F, -2.5F);

		noseModel[135].addShapeBox(0F, 0F, 0F, 1, 2, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F); // Box 154
		noseModel[135].setRotationPoint(8F, 4.5F, 1.5F);

		noseModel[136].addShapeBox(0F, 0F, 0F, 1, 1, 5, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 155
		noseModel[136].setRotationPoint(-13F, -12.5F, -2.5F);

		noseModel[137].addShapeBox(0F, 0F, 0F, 1, 1, 5, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 156
		noseModel[137].setRotationPoint(-13F, -8.5F, -2.5F);

		noseModel[138].addShapeBox(0F, 0F, 0F, 1, 1, 5, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 159
		noseModel[138].setRotationPoint(-13F, 3.5F, -2.5F);

		noseModel[139].addShapeBox(0F, 0F, 0F, 1, 1, 5, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 160
		noseModel[139].setRotationPoint(-13F, 7.5F, -2.5F);

		noseModel[140].addShapeBox(0F, 0F, 0F, 1, 1, 5, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 161
		noseModel[140].setRotationPoint(12F, 7.5F, -2.5F);

		noseModel[141].addShapeBox(0F, 0F, 0F, 1, 1, 5, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 162
		noseModel[141].setRotationPoint(12F, 3.5F, -2.5F);

		noseModel[142].addShapeBox(0F, 0F, 0F, 1, 1, 5, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 165
		noseModel[142].setRotationPoint(12F, -8.5F, -2.5F);

		noseModel[143].addShapeBox(0F, 0F, 0F, 1, 1, 5, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 166
		noseModel[143].setRotationPoint(12F, -12.5F, -2.5F);

    }


    public void render(float f5)
    {
    	render(noseModel, f5);
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
