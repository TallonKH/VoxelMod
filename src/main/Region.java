package main;

import data.*;

import java.util.*;

public class Region implements Iterable<Chunk> {
	public final static short REGION_SIZE = 1024;
	public final static int REGION_BLOCK_SIZE = REGION_SIZE * Chunk.CHUNK_SIZE;
	public final short regionX;
	public final short regionY;
	public final short regionZ;

	public final World world;
	private final Map<Integer, Chunk> chunks = new HashMap<>();

	public Region(World world, short x, short y, short z) {
		this.world = world;
		regionX = x;
		regionY = y;
		regionZ = z;
	}

	private Chunk initializeChunk(short x, short y, short z) {
		Chunk c = new Chunk(this, x, y, z);
		chunks.put(Short3.asIntUnsigned(x, y, z), c);
		return c;
	}

	/**
	 * @param gen generate the chunk and region if necessary
	 * @return the chunk at region chunk coordinates
	 */
	public Chunk getChunk(short x, short y, short z, boolean gen) {
		Chunk c = chunks.get(Short3.asIntUnsigned(x, y, z));
		if (c == null) {
			if (gen) {
				c = initializeChunk(x, y, z);
				c.generate();
				return c;
			}
			return null;
		}
		return c;
	}

	@Override
	public Iterator<Chunk> iterator() {
		return chunks.values().iterator();
	}
}
