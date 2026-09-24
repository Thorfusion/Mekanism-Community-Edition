package mekanism.nuclear.common.block;

import java.util.Random;
import javax.annotation.Nonnull;
import mekanism.common.Mekanism;
import mekanism.nuclear.common.NuclearItems;
import mekanism.nuclear.common.NuclearOreType;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/** Native Uranium or Fluorite ore adapted to the 1.12 mining model. */
public class BlockNuclearOre extends Block {

    private final NuclearOreType type;

    public BlockNuclearOre(NuclearOreType type) {
        super(Material.ROCK);
        this.type = type;
        setHardness(5F);
        setResistance(9F);
        setSoundType(SoundType.STONE);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(Mekanism.tabMekanism);
    }

    public NuclearOreType getOreType() {
        return type;
    }

    @Nonnull
    @Override
    public Item getItemDropped(IBlockState state, Random random, int fortune) {
        return type == NuclearOreType.FLUORITE ? NuclearItems.FluoriteGem : Item.getItemFromBlock(this);
    }

    @Override
    public int quantityDropped(Random random) {
        return type == NuclearOreType.FLUORITE ? 2 + random.nextInt(3) : 1;
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        if (type == NuclearOreType.FLUORITE && fortune > 0) {
            int multiplier = random.nextInt(fortune + 2) - 1;
            return quantityDropped(random) * (Math.max(multiplier, 0) + 1);
        }
        return quantityDropped(random);
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        if (type == NuclearOreType.FLUORITE) {
            return MathHelper.getInt(world instanceof World ? ((World) world).rand : new Random(),
                  type.getMinExperience(), type.getMaxExperience());
        }
        return 0;
    }
}
