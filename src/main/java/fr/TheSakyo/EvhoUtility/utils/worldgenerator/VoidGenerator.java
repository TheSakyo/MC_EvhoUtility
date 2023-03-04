package fr.TheSakyo.EvhoUtility.utils.worldgenerator;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;


/************************/
/* GÉNÈRE UN MONDE VIDE */
/************************/
public class VoidGenerator extends ChunkGenerator {

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) { return Collections.<BlockPopulator>emptyList(); }

    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {}

    /*@Override
    @NotNull
    public ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z, @NotNull BiomeGrid biome) { return createChunkData(world); }*/

    @Override
    public boolean canSpawn(World world, int x, int z) { return true; }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) { return new Location(world, 0, 128, 0); }
}
/************************/
/* GÉNÈRE UN MONDE VIDE */
/************************/