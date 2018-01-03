package main;

import engine.*;
import tStringManager.*;

import java.util.*;

public class CustomModelObj {
	private final Map<String, MeshData> meshes;

	public CustomModelObj(Map<String, MeshData> meshes) {
		this.meshes = meshes;
	}

	public Map<String, MeshData> getMeshes() {
		return new HashMap<>(meshes);
	}

	public static CustomModelObj parse(String folder, Map<String, String> tConfig) {
		Map<String, MeshData> meshMap = new HashMap<>();
		TFiles.parseMap(tConfig.get("meshes")).forEach((meshName, tMeshData) -> {
			Map<String, String> meshData = TFiles.parseMap(tMeshData);
			List<String> tVerts = TFiles.parseList(meshData.get("verts"));
			List<String> tTris = TFiles.parseList(meshData.get("tris"));
			List<String> tUVs = TFiles.parseList(meshData.get("uvs"));

			float[] verts = new float[tVerts.size() * 3];
			int[] tris = new int[tTris.size() * 3];
			float[] uvs = new float[tUVs.size() * 2];

			int i2 = 0;
			for (int i = 0; i < tVerts.size(); i++) {
				List<String> vert = TFiles.parseList(tVerts.get(i));
				verts[i2] = Float.parseFloat(vert.get(0));
				verts[i2 + 1] = Float.parseFloat(vert.get(1));
				verts[i2 + 2] = Float.parseFloat(vert.get(2));
				i2 += 3;
			}

			i2 = 0;
			for (int i = 0; i < tTris.size(); i++) {
				List<String> tri = TFiles.parseList(tTris.get(i));
				tris[i2] = Integer.parseInt(tri.get(0));
				tris[i2 + 1] = Integer.parseInt(tri.get(1));
				tris[i2 + 2] = Integer.parseInt(tri.get(2));
				i2 += 3;
			}

			i2 = 0;
			for (int i = 0; i < tUVs.size(); i++) {
				List<String> coord = TFiles.parseList(tUVs.get(i));
				uvs[i2] = Float.parseFloat(coord.get(0));
				uvs[i2 + 1] = Float.parseFloat(coord.get(1));
				i2 += 2;
			}
			int texture = Texture.loadTexture(folder + meshData.get("texture"));
			meshMap.put(meshName, new MeshData(verts, uvs, tris, texture, 0,1f));
		});
		return new CustomModelObj(meshMap);
	}
}
