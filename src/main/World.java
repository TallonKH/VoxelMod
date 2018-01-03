package main;

import data.*;
import generators.*;
import org.joml.*;

import java.lang.Math;
import java.lang.reflect.*;
import java.util.*;

public class World {
	private final HashMap<Integer, Region> regions = new HashMap<>();
	// TODO maybe need to move this to per-chunk when chunks are being loaded/unloaded from hard memory
	private Set<Entity> entites = new HashSet<>();
	private Set<Entity> loadedEntities = new HashSet<>();
	private List<Entity> entitiesPendingLoad = new ArrayList<>();
	private List<Entity> entitiesPendingUnload = new ArrayList<>();

	private final Map<Int3, Short> pendingSetblocks = new HashMap<>();

	private Generator generator;
	private Server server;

	void doUpdate(float delta) {
		Map<Int3, Short> currentSetBlocks = new HashMap<>(pendingSetblocks);
		pendingSetblocks.clear();

		for (Map.Entry<Int3, Short> set : currentSetBlocks.entrySet()) {
			setBlockNow(set.getKey().x, set.getKey().y, set.getKey().z, set.getValue(), true);
		}

		entitiesPendingUnload.forEach((o) -> loadedEntities.remove(o));
		entitiesPendingLoad.forEach((o) -> loadedEntities.add(o));
		entitiesPendingLoad.clear();
		entitiesPendingUnload.clear();

		for (Entity entity : loadedEntities) {
			entity.doUpdate(delta);
		}
	}

	/**
	 * @param gen generate the region and chunk if necessary
	 * @return the spawned entity
	 */
	public Entity spawnEntity(EntityType type, float x, float y, float z, boolean gen) {
		Chunk chunk = getChunkAt((int) x, (int) y, (int) z, gen);
		if (chunk == null) {
			return null;
		}
		try {
			Entity entity = type.entityClass.getConstructor(EntityType.class, World.class, float.class, float.class, float.class)
											.newInstance(type, this, x, y, z);
			//Alert nearby Clients that an entity has been created
			//TODO make sure this doesnt break when a new Client connects/leaves
			boolean shouldLoad = false;
			for (Client c : server.getConnectedClients()) {
				if (c.getControlledEntity() != null) {
					Vector3f cLoc = c.getControlledEntity().getLocation();
					Vector3f eLoc = entity.getLocation();
					if (Math.abs(cLoc.x - eLoc.x) <= c.getEntityDrawDistance() &&
						Math.abs(cLoc.y - eLoc.y) <= c.getEntityDrawDistance() &&
						Math.abs(cLoc.z - eLoc.z) <= c.getEntityDrawDistance()) {
						entity.listen(c);
						entity.onCreated();
						shouldLoad = true;
					}
				}
			}
			if (shouldLoad || entity.isAlwaysLoaded()) {
				entitiesPendingLoad.add(entity);
			}
			entites.add(entity);
			return entity;
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}


	public World(Server server, Class<? extends Generator> generator) {
		this.server = server;
		try {
			this.generator = generator.getDeclaredConstructor(World.class).newInstance(this);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			//temp
			this.generator = new RandoGen(this);
		}
	}

	private Region initializeRegion(short x, short y, short z) {
		Region r = new Region(this, x, y, z);
		regions.put(Short3.asIntSigned(x, y, z), r);
		return r;
	}

	/**
	 * @param gen generate the region if necessary
	 * @return the region at world region coordinates
	 */
	public Region getRegion(short x, short y, short z, boolean gen) {
		Region r = regions.get(Short3.asIntSigned(x, y, z));
		if (r == null) {
			if (gen) {
				return initializeRegion(x, y, z);
			}
			return null;
		}
		return r;
	}

	/**
	 * @param gen generate the chunk and region if necessary
	 * @return the chunk at world chunk coordinates
	 */
	public Chunk getChunk(int x, int y, int z, boolean gen) {
		Region r = getRegion((short) (Math.floor((float) x / Region.REGION_BLOCK_SIZE)),
							 (short) (Math.floor((float) y / Region.REGION_BLOCK_SIZE)),
							 (short) (Math.floor((float) z / Region.REGION_BLOCK_SIZE)), gen);

		if (r == null) {
			return null;
		}

		return r.getChunk((short) Math.floorMod(x, Region.REGION_SIZE),
						  (short) Math.floorMod(y, Region.REGION_SIZE),
						  (short) Math.floorMod(z, Region.REGION_SIZE), gen);
	}

	/**
	 * @param gen generate the chunk and region if necessary
	 * @return if the block was successfully queued
	 */
	public boolean setBlock(int x, int y, int z, short type, boolean gen) {
		Chunk c = getChunkAt(x, y, z, gen);

		if (c == null) {
			return false;
		}

		Int3 coord = new Int3(x, y, z);
		if (!pendingSetblocks.containsKey(coord)) {
			pendingSetblocks.put(coord, type);
			return true;
		}
		return false;
	}

	/**
	 * @param gen generate the chunk and region if necessary
	 * @return if the block was successfully set
	 */
	public boolean setBlockNow(int x, int y, int z, short type, boolean gen) {
		Chunk c = getChunkAt(x, y, z, gen);

		if (c == null) {
			return false;
		}

		c.setBlockNow((byte) Math.floorMod(x, Chunk.CHUNK_SIZE),
					  (byte) Math.floorMod(y, Chunk.CHUNK_SIZE),
					  (byte) Math.floorMod(z, Chunk.CHUNK_SIZE), type);

		return true;
	}

	/**
	 * @param gen generate the region if necessary
	 * @return the region at given block coordinates
	 */
	public Region getRegionAt(int x, int y, int z, boolean gen) {
		return getRegion((short) Math.floor((float) x / Region.REGION_BLOCK_SIZE),
						 (short) Math.floor((float) y / Region.REGION_BLOCK_SIZE),
						 (short) Math.floor((float) z / Region.REGION_BLOCK_SIZE), gen);
	}

	/**
	 * @param gen generate the chunk and region if necessary
	 * @return the chunk at given block coords
	 */
	public Chunk getChunkAt(int x, int y, int z, boolean gen) {
		Region r = getRegionAt(x, y, z, gen);

		if (r == null) {
			return null;
		}

		return r.getChunk((short) (Math.floorMod(x, Region.REGION_BLOCK_SIZE) / Chunk.CHUNK_SIZE),
						  (short) (Math.floorMod(y, Region.REGION_BLOCK_SIZE) / Chunk.CHUNK_SIZE),
						  (short) (Math.floorMod(z, Region.REGION_BLOCK_SIZE) / Chunk.CHUNK_SIZE), gen);
	}

	/**
	 * @param gen generate the chunk and region if necessary
	 * @return the block at world block coordinates
	 */
	public short getBlock(int x, int y, int z, boolean gen) {
		Chunk c = getChunkAt(x, y, z, gen);

		if (c == null) {
			return -1;
		}

		return c.getBlock(Math.floorMod(x, Chunk.CHUNK_SIZE),
						  Math.floorMod(y, Chunk.CHUNK_SIZE),
						  Math.floorMod(z, Chunk.CHUNK_SIZE));
	}

	public Generator getGenerator() {
		return generator;
	}

	public Server getServer() {
		return server;
	}
}
