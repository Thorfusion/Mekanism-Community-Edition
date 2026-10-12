package mekanism.nuclear.common.world;

import cpw.mods.fml.common.IWorldGenerator;
import java.util.Random;
import mekanism.nuclear.common.NuclearBlocks;
import mekanism.nuclear.common.NuclearOreType;
import mekanism.nuclear.common.config.NuclearWorldGenConfig;
import mekanism.nuclear.common.config.NuclearWorldGenConfig.OreSettings;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraft.world.gen.ChunkProviderHell;
import net.minecraft.world.gen.feature.WorldGenMinable;

/** Native Uranium and Fluorite generation owned entirely by Nuclear. */
public final class NuclearWorldGenerator implements IWorldGenerator
{
    public static final NuclearWorldGenerator INSTANCE = new NuclearWorldGenerator();

    private NuclearWorldGenerator()
    {
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
          IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        if (chunkGenerator instanceof ChunkProviderHell
              || chunkGenerator instanceof ChunkProviderEnd)
        {
            return;
        }
        generateOre(random, chunkX, chunkZ, world,
              NuclearOreType.URANIUM, NuclearBlocks.UraniumOre);
        generateOre(random, chunkX, chunkZ, world,
              NuclearOreType.FLUORITE, NuclearBlocks.FluoriteOre);
    }

    private static void generateOre(Random random, int chunkX, int chunkZ,
          World world, NuclearOreType type, Block block)
    {
        OreSettings settings = NuclearWorldGenConfig.get(type);
        if (!settings.isEnabled() || settings.getVeinsPerChunk() == 0)
        {
            return;
        }
        WorldGenMinable generator = new WorldGenMinable(block, 0,
              settings.getMaxVeinSize(), Blocks.stone);
        for (int attempt = 0; attempt < settings.getVeinsPerChunk(); attempt++)
        {
            int x = chunkX * 16 + random.nextInt(16);
            int y = sampleHeight(random, settings.getMaxHeight());
            int z = chunkZ * 16 + random.nextInt(16);
            generator.generate(world, random, x, y, z);
        }
    }

    static int sampleHeight(Random random, int maxHeight)
    {
        if (maxHeight <= 0)
        {
            throw new IllegalArgumentException("maxHeight must be positive");
        }
        return random.nextInt(maxHeight);
    }
}
