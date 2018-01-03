package main;

import data.*;

import java.util.*;

public final class Chunk implements Iterable<Short> {
	public final static byte CHUNK_SIZE = (byte) 16;
	final static byte CHUNK_SIZE_MINUS_ONE = CHUNK_SIZE - 1;
	public final static int totalBlocks = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;
//	public short opaques = totalBlocks;

	public final Region region;
	/*
	 * World Chunk coordinates
	 */
	public final int chunkX;
	public final int chunkY;
	public final int chunkZ;

	/*
	 * World Block coordinates - //TODO maybe do not need to cache?
	 */
	public final int blockOffsetX;
	public final int blockOffsetY;
	public final int blockOffsetZ;

	private boolean populated;

	private final short[][][] blocks = new short[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];

	Chunk(Region region, short x, short y, short z) {
		this.region = region;
		chunkX = x + Region.REGION_SIZE * region.regionX;
		chunkY = y + Region.REGION_SIZE * region.regionY;
		chunkZ = z + Region.REGION_SIZE * region.regionZ;
		blockOffsetX = x * CHUNK_SIZE + region.regionX * Region.REGION_BLOCK_SIZE;
		blockOffsetY = y * CHUNK_SIZE + region.regionY * Region.REGION_BLOCK_SIZE;
		blockOffsetZ = z * CHUNK_SIZE + region.regionZ * Region.REGION_BLOCK_SIZE;
	}

	/**
	 * Runs through the whole chunk - <b>avoid unecessary calls</b>
	 *
	 * @return a set of the types of blocks in this chunk
	 */
	public Set<Short> getBlockTypes() {
		Set<Short> types = new HashSet<>();
		forEach(types::add);
		return types;
	}

	public void setBlockNow(byte x, byte y, byte z, short block) {
		short prevBlock = blocks[x][y][z];

		blocks[x][y][z] = block;
		for (Client cli : getServer().getConnectedClients()) {
			ChunkRenderer renderer = cli.getChunkRenderer(this);
			if (renderer != null) {
				renderer.updateBlock(block);
				renderer.updateBlock(prevBlock);
				if (renderer.getResourceBundle().isBlockOpaque(prevBlock) ^ renderer.getResourceBundle().isBlockOpaque(block)) {
					for (Side s : Side.values()) {
						byte x1 = (byte) (x + s.x);
						byte y1 = (byte) (y + s.y);
						byte z1 = (byte) (z + s.z);
						short b = getBlock(x1, y1, z1);
						if (b >= 0) {
							renderer.updateBlock(b);
						} else {
							Chunk c = getChunkRelative(s.x, s.y, s.z, false);
							if (c != null) {
								b = c.getBlock(Math.floorMod(x1, CHUNK_SIZE), Math.floorMod(y1, CHUNK_SIZE), Math.floorMod(z1, CHUNK_SIZE));
								cli.getChunkRenderer(c).updateBlock(b);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @param gen generate the chunk and region if necessary
	 * @return the chunk relative to the current one
	 */
	public Chunk getChunkRelative(int x, int y, int z, boolean gen) {
		return getWorld().getChunk(chunkX + x, chunkY + y, chunkZ + z, gen);
	}

	public void fillNow(short block) {
		for (byte x = 0; x < CHUNK_SIZE; x++) {
			for (byte y = 0; y < CHUNK_SIZE; y++) {
				for (byte z = 0; z < CHUNK_SIZE; z++) {
					blocks[x][y][z] = block;
				}
			}
		}

		for (Client c : getServer().getConnectedClients()) {
			ChunkRenderer renderer = c.getChunkRenderer(this);
			if (renderer != null) {
				renderer.filled(block);
			}
		}
	}

	void generate() {
		if (!populated) {
			populated = true;
			getWorld().getGenerator().generate(this);
		}
	}

	/**
	 * @return if the chunk has been generated already
	 */
	public boolean isPopulated() {
		return populated;
	}

	public boolean setBlock(int x, int y, int z, short block) {
		if (x >= CHUNK_SIZE || x < 0 || y >= CHUNK_SIZE || y < 0 || z >= CHUNK_SIZE || z < 0) {
			throw null;
		}
		return getWorld().setBlock(blockOffsetX + x, blockOffsetY + y, blockOffsetZ + z, block, true);
	}

	public short getBlock(int x, int y, int z) {
		try {
			return blocks[x][y][z];
		} catch (IndexOutOfBoundsException e) {
			return -1;
		}
	}

	public World getWorld() {
		return region.world;
	}

	public Server getServer() {
		return region.world.getServer();
	}

	@Override
	public Iterator<Short> iterator() {
		return new Iterator<Short>() {
			byte x = 0;
			byte y = 0;
			byte z = 0;
			Chunk chunk = Chunk.this;

			@Override
			public boolean hasNext() {
				return z < CHUNK_SIZE;
			}

			@Override
			public Short next() {
				Short b = chunk.getBlock(x, y, z);
				if (x == CHUNK_SIZE_MINUS_ONE) {
					x = 0;
					if (y == CHUNK_SIZE_MINUS_ONE) {
						y = 0;
						z++;
					} else {
						y++;
					}
				} else {
					x++;
				}
				return b;
			}
		};
	}
}
