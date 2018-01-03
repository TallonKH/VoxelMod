package engine;

import org.joml.*;

import java.util.*;

public class SceneComponent {

	private final List<Mesh> meshes = new ArrayList<>();
	private final Vector3f position;
	private float scale;
	private final Vector3f rotation;

	public SceneComponent(Mesh... meshes) {
		for (Mesh m : meshes) {
			if (m != null) {
				this.meshes.add(m);
			}
		}
		position = new Vector3f(0, 0, 0);
		scale = 1;
		rotation = new Vector3f(0, 0, 0);
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f pos) {
		setPosition(pos.x, pos.y, pos.z);
	}

	public void setPosition(float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rot) {
		setRotation(rot.x, rot.y, rot.z);
	}

	public void setRotation(float x, float y, float z) {
		this.rotation.x = x;
		this.rotation.y = y;
		this.rotation.z = z;
	}

	public List<Mesh> getMeshes() {
		return meshes;
	}
}