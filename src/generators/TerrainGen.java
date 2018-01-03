package generators;

import main.*;
import tMethods.*;

import static main.Chunk.*;

public class TerrainGen extends Generator {
	private final TNoise.NoiseGen2D noise = new TNoise.NoiseGen2D();

	private final short GRASS;
	private final short DIRT;
	private final short STONE;
	private final short AIR;
	private final float scale = 32.0f;
	private final int minHeight = 16;
	private final int maxHeighDif = 16;
	private final int minChunkHeight = (minHeight - maxHeighDif) / CHUNK_SIZE;
	private final int maxChunkHeight = minChunkHeight + maxHeighDif / CHUNK_SIZE;

	public TerrainGen(World world) {
		super(world);
		GRASS = world.getServer().getBlockId("VoxelMod:grass");
		DIRT = world.getServer().getBlockId("VoxelMod:dirt");
		STONE = world.getServer().getBlockId("VoxelMod:stone");
		AIR = world.getServer().getBlockId("VoxelMod:air");
	}

	@Override
	public void generate(Chunk chunk) {
		if (chunk.chunkY > maxChunkHeight) {
			chunk.fillNow(AIR);
			return;
		}

		if (chunk.chunkY < minChunkHeight) {
			chunk.fillNow(STONE);
			return;
		}

		for (byte x = 0; x < CHUNK_SIZE; x++) {
			for (byte z = 0; z < CHUNK_SIZE; z++) {
				int height = (int) (minHeight + noise.getVal((chunk.blockOffsetX + x) / scale, (chunk.blockOffsetZ + z) / scale) * maxHeighDif);
				int dif = height - chunk.blockOffsetY;

				if (dif < 0) {
					for (byte y = 0; y < CHUNK_SIZE; y++) {
						chunk.setBlockNow(x, y, z, AIR);
					}
				} else if (dif >= CHUNK_SIZE) {
					for (byte y = 0; y < CHUNK_SIZE; y++) {
						chunk.setBlockNow(x, y, z, STONE);
					}
					if (dif - 3 < CHUNK_SIZE) {
						byte dirtDepth = (byte) Math.max(dif - 3, 0);
						for (byte y = dirtDepth; y < CHUNK_SIZE; y++) {
							chunk.setBlockNow(x, y, z, DIRT);
						}
					}
				} else {
					chunk.setBlockNow(x, (byte) dif, z, GRASS);
					byte dirtDepth = (byte) Math.max(dif - 3, 0);
					for (byte y = dirtDepth; y < dif; y++) {
						chunk.setBlockNow(x, y, z, DIRT);
					}
					for (byte y = 0; y < dirtDepth; y++) {
						chunk.setBlockNow(x, y, z, STONE);
					}
				}
			}
		}
	}
}
