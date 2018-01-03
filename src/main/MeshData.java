package main;

/**
 * Created by TT on 8/30/2017.
 */
public class MeshData {
	private int texture;
	private int emissive;
	private float[] vertices;
	private float[] uvs;
	private int[] tris;
	private float brightness;

	public MeshData(float[] vertices, float[] uvs, int[] tris, int texture, int emissive, float brightness) {
		this.texture = texture;
		this.emissive = emissive;
		this.vertices = vertices;
		this.uvs = uvs;
		this.tris = tris;
		this.brightness = brightness;
	}

	public int getTexture() {
		return texture;
	}

	public int getEmissive() {
		return emissive;
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getUVs() {
		return uvs;
	}

	public int[] getTris() {
		return tris;
	}

	public float getBrightness() {
		return brightness;
	}
}
