package mekanism.nuclear.common.world;

import java.util.Random;
import mekanism.nuclear.common.NuclearBlocks;
import mekanism.nuclear.common.NuclearOreType;
import mekanism.nuclear.common.config.NuclearWorldGenConfig;
import mekanism.nuclear.common.config.NuclearWorldGenConfig.OreSettings;
import net.minecraft.block.Block;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

/** Native 1.12 Uranium and Fluorite generation owned entirely by Nuclear. */
public final class NuclearWorldGenerator implements IWorldGenerator {

    public static final NuclearWorldGenerator INSTANCE = new NuclearWorldGenerator();

    private NuclearWorldGenerator() {
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
          IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (chunkGenerator instanceof ChunkGeneratorHell || chunkGenerator instanceof ChunkGeneratorEnd) {
            return;
        }
        generateOre(random, chunkX, chunkZ, world, NuclearOreType.URANIUM, NuclearBlocks.UraniumOre);
        generateOre(random, chunkX, chunkZ, world, NuclearOreType.FLUORITE, NuclearBlocks.FluoriteOre);
    }

    private static void generateOre(Random random, int chunkX, int chunkZ, World world,
          NuclearOreType type, Block block) {
        OreSettings settings = NuclearWorldGenConfig.get(type);
        if (!settings.isEnabled() || settings.getVeinsPerChunk() == 0) {
            return;
        }
        WorldGenMinable generator = new WorldGenMinable(block.getDefaultState(), settings.getMaxVeinSize(),
              BlockMatcher.forBlock(Blocks.STONE));
        for (int attempt = 0; attempt < settings.getVeinsPerChunk(); attempt++) {
            BlockPos pos = new BlockPos(
                  chunkX * 16 + random.nextInt(16),
                  sampleHeight(random, settings.getMaxHeight()),
                  chunkZ * 16 + random.nextInt(16));
            generator.generate(world, random, pos);
        }
    }

    static int sampleHeight(Random random, int maxHeight) {
        if (maxHeight <= 0) {
            throw new IllegalArgumentException("maxHeight must be positive");
        }
        return random.nextInt(maxHeight);
    }
}
