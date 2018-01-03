package main;

import org.joml.*;

public class EntityComponent {
	public final Entity entity;
	private Vector3f location = new Vector3f();
	private Vector3f rotation = new Vector3f();
	private String mesh;
	private float scale = 1;

	public EntityComponent(Entity entity, String mesh){
		this.entity = entity;
		this.mesh = mesh;
	}


	public Vector3f getLocation() {
		return location;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}

	public void setOffset(Vector3f offset){
		this.location.set(offset.add(entity.getLocation()));
	}

	public void setLocation(Vector3f location) {
		this.location.set(location);
	}

	public void setRotation(Vector3f rotation) {
		this.rotation.set(rotation);
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public String getMesh() {
		return mesh;
	}
}
