package fr.TheSakyo.EvhoUtility.utils.worldgenerator;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.Random;

/************************/
/* GÉNÈRE UN MONDE PLAT */
/************************/
public class FlatGenerator extends ChunkGenerator {

        @Override
        public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {

            SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(worldInfo.getSeed()), 8);
            generator.setScale(0.0001D);

            for(int X = 0; X < 16; X++)

                for(int Z = 0; Z < 16; Z++) {

                    int currentHeight = (int) (generator.noise(chunkX * 16 + X, chunkZ * 16 + Z, 0.1D, 0.1D) * 5D + 35D);

                    chunkData.setBlock(X, currentHeight, Z, Material.GRASS_BLOCK);
                    chunkData.setBlock(X, currentHeight - 1, Z, Material.DIRT);
                    chunkData.setBlock(X, currentHeight - 2, Z, Material.DIRT);
                    chunkData.setBlock(X, currentHeight - 3, Z, Material.DIRT);

                    for(int i = currentHeight - 4; i > 6; i--) chunkData.setBlock(X, i, Z, Material.STONE);

                    chunkData.setBlock(X, 6, Z, Material.BEDROCK);
                    chunkData.setBlock(X, 5, Z, Material.BEDROCK);
                }
        }

    /*@Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {

        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);

        ChunkData chunk = createChunkData(world);

        generator.setScale(0.0001D);

        for(int X = 0; X < 16; X++)

            for(int Z = 0; Z < 16; Z++) {

                int currentHeight = (int) (generator.noise(chunkX * 16 + X, chunkZ * 16 + Z, 0.1D, 0.1D) * 5D + 35D);

                chunk.setBlock(X, currentHeight, Z, Material.GRASS_BLOCK);
                chunk.setBlock(X, currentHeight - 1, Z, Material.DIRT);
                chunk.setBlock(X, currentHeight - 2, Z, Material.DIRT);
                chunk.setBlock(X, currentHeight - 3, Z, Material.DIRT);

                for(int i = currentHeight - 4; i > 6; i--) chunk.setBlock(X, i, Z, Material.STONE);

                chunk.setBlock(X, 6, Z, Material.BEDROCK);
                chunk.setBlock(X, 5, Z, Material.BEDROCK);
            }

        return chunk;
    }*/
}
/************************/
/* GÉNÈRE UN MONDE PLAT */
/************************/
