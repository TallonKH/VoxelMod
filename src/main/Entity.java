package main;

import data.*;
import engine.*;
import org.joml.Math;
import org.joml.*;

import java.util.*;

public abstract class Entity {
	private World world;
	private boolean alive = true;
	private Vector3f location = new Vector3f();
	private Vector3f rotation = new Vector3f();
	private float scale = 1;
	private boolean alwaysLoaded = false;
	/**
	 * THIS SHOULD ALMOST ALWAYS BE 0
	 */
	private byte chunkLoadRadius = 0;
	/**
	 * VERY FEW ENTITIES SHOULD BE CHUNKLOADING<br>NATURALLY-SPAWNED ENTITIES SHOULD NEVER BE CHUNKLOADING
	 */
	private boolean chunkloading = chunkLoadRadius > 0;
	private double age;
	private Vector3f velocity = new Vector3f();
	private Vector3f acceleration = new Vector3f();
	private List<BoundingBox> boundingBoxes = new ArrayList<>();
	private Map<String, EntityComponent> entityComponents = new HashMap<>();
	public final EntityType type;
	private Set<EntityListener> listeners = new HashSet<>();

	public boolean isAlwaysLoaded() {
		return alwaysLoaded;
	}

	public void setAlwaysLoaded(boolean loaded) {
		alwaysLoaded = loaded;
	}

	public void setChunkLoadRadius(byte radius) {
		chunkLoadRadius = radius;
		chunkloading = radius > 0;
	}

	public void listen(EntityListener listener) {
		listeners.add(listener);
	}

	public boolean stopListen(EntityListener listener) {
		return listeners.remove(listener);
	}

	public boolean isChunkloading() {
		return chunkloading;
	}

	public Entity(EntityType type, World world, float x, float y, float z) {
		this.world = world;
		this.type = type;
		this.location = new Vector3f(x, y, z);
	}


	public EntityComponent getEntityComponent(String name) {
		return entityComponents.get(name);
	}

	public World getWorld() {
		return world;
	}

	public EntityRenderer makeRenderer(main.ResourceBundle bundle) {
		return new EntityRenderer(this, bundle);
	}

	protected void addBoundingBox(BoundingBox box) {
		boundingBoxes.add(box);
	}

	protected void addEntityComponent(String name, EntityComponent comp) {
		entityComponents.put(name, comp);
	}

	protected void removeEntityComponent(String name) {
		entityComponents.remove(name);
	}

	public void doUpdate(float delta) {
		age += delta;
		velocity.add(acceleration.x * delta, acceleration.y * delta, acceleration.z * delta);
//		location.add(velocity.x * delta, velocity.y * delta, velocity.z * delta);
//		tryMove(velocity);
		if (chunkloading) {
			Utils.boxLoop(chunkLoadRadius, (x, y, z) -> {
				int cx = Utils.blockWToChunkW(location.x);
				int cy = Utils.blockWToChunkW(location.y);
				int cz = Utils.blockWToChunkW(location.z);
				world.getChunk(cx + x, cy + y, cz + z, true);
			});
		}
	}

	public void forceMove(Vector3f offset) {
		if (offset.x == 0 || offset.y == 0 || offset.z != 0) {
			return;
		}
		for (BoundingBox box : boundingBoxes) {
			box.move(offset);
		}
		location.add(offset);
		boundingBoxes.forEach((box) -> box.move(offset));
		onMoved(offset.x, offset.y, offset.z);
	}

	public void teleport(Vector3f location) {
		forceMove(this.location.sub(location));
	}

	public void forceMove(Side side, float distance) {
		float x = side.x * distance;
		float y = side.y * distance;
		float z = side.z * distance;
		if (distance == 0) {
			return;
		}
		for (BoundingBox box : boundingBoxes) {
			box.move(x, y, z);
		}
		location.add(x, y, z);
		onMoved(x, y, z);
	}

	public float tryMove(Side side, float distance) {
		for (BoundingBox box : boundingBoxes) {
			if (box.blocksMovement()) {
				distance = Math.min(distance, box.sideMovementTrace(getWorld(), side, distance, chunkloading));
			}
		}
		forceMove(side, distance);
		return distance;
	}

	public void tryMove(Vector3f offset) {

	}

	public void saveData(Map<String, String> datas) {
		datas.put("dAge", Double.toString(age));
		datas.put("vLocat", Utils.vectorToTFile(location));
		datas.put("vFacing", Utils.vectorToTFile(rotation));
		datas.put("vVelo", Utils.vectorToTFile(velocity));
		datas.put("vAccel", Utils.vectorToTFile(acceleration));
	}

	public void loadData(Map<String, String> data) {
		age = Float.parseFloat(data.get("dAge"));
		location = Utils.parseVector3f(data.get("vLocat"));
		rotation = Utils.parseVector3f(data.get("vFacing"));
		velocity = Utils.parseVector3f(data.get("vVelo"));
		acceleration = Utils.parseVector3f(data.get("vAccel"));
	}

	public List<BoundingBox> getBoundingBoxes() {
		return new ArrayList<>(boundingBoxes);
	}

	public HashMap<String, EntityComponent> getEntityComponents() {
		return new HashMap<>(entityComponents);
	}

	public boolean isAlive() {
		return alive;
	}

	public Vector3f getLocation() {
		return location;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public Vector3f getAcceleration() {
		return acceleration;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}

	public void onMoved(float offsetX, float offsetY, float offsetZ) {
		listeners.forEach((o) -> o.entityMoved(this, offsetX, offsetY, offsetZ));
		onLocationChanged();

	}

	public void onLocationChanged() {
		listeners.forEach((o) -> o.entityChangedLocation(this));
	}

	public void onKilled() {
		listeners.forEach((o) -> o.entityKilled(this));
	}

	public void onCreated() {
		listeners.forEach((o) -> o.entityCreated(this));
	}

	public interface EntityListener {
		default void entityCreated(Entity entity) {
		}

		default void entityKilled(Entity entity) {
		}

		default void entityMoved(Entity entity, float offsetX, float offsetY, float offsetZ) {
		}

		default void entityChangedLocation(Entity entity) {
		}
	}
}
