package mekanism.nuclear.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.NuclearItems;
import mekanism.nuclear.common.NuclearOreType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/** Native Uranium or Fluorite ore adapted to the 1.7.10 mining model. */
public class BlockNuclearOre extends Block
{
    private final NuclearOreType type;

    public BlockNuclearOre(NuclearOreType type)
    {
        super(Material.rock);
        this.type = type;
        setHardness(5F);
        setResistance(9F);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", 2);
    }

    public NuclearOreType getOreType()
    {
        return type;
    }

    @Override
    public Item getItemDropped(int metadata, Random random, int fortune)
    {
        return type == NuclearOreType.FLUORITE
              ? NuclearItems.FluoriteGem : Item.getItemFromBlock(this);
    }

    @Override
    public int quantityDropped(Random random)
    {
        return type == NuclearOreType.FLUORITE ? 2 + random.nextInt(3) : 1;
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random)
    {
        if (type == NuclearOreType.FLUORITE && fortune > 0)
        {
            int multiplier = random.nextInt(fortune + 2) - 1;
            return quantityDropped(random) * (Math.max(multiplier, 0) + 1);
        }
        return quantityDropped(random);
    }

    @Override
    public int getExpDrop(IBlockAccess world, int metadata, int fortune)
    {
        if (type == NuclearOreType.FLUORITE)
        {
            Random random = world instanceof World ? ((World)world).rand : new Random();
            return MathHelper.getRandomIntegerInRange(random,
                  type.getMinExperience(), type.getMaxExperience());
        }
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        blockIcon = register.registerIcon(MekanismNuclear.MODID + ":" + type.getName() + "_ore");
    }
}
