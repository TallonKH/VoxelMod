package main;

import engine.*;

import java.util.*;

/**
 * Created by TT on 7/24/2017.
 */
public class BlockTextureObj {
	public final int textureXP;
	public final int textureXN;
	public final int textureYP;
	public final int textureYN;
	public final int textureZP;
	public final int textureZN;
	public final boolean opaque;
	public final EmissiveTextureObj emissive;
//	public final CustomModelObj customModel;

	public static BlockTextureObj parse(String folder, Map<String, String> tConfig) {
		boolean opaque = Boolean.parseBoolean(tConfig.get("opaque"));
		if (opaque) {
			EmissiveTextureObj emissive = null;
			if (Boolean.parseBoolean(tConfig.get("emissive"))) {
				switch (tConfig.get("emissiveFormat")) {
					case "0":
					case "simple":
						emissive = new EmissiveTextureObj(Texture.loadTexture(folder + "em_texture.png"));
						break;
					case "1":
					case "pillar":
						emissive = new EmissiveTextureObj(Texture.loadTexture(folder + "em_top.png"),
														  Texture.loadTexture(folder + "em_side.png"),
														  Texture.loadTexture(folder + "em_bottom.png"));
						break;
					case "2":
					case "complex":
						emissive = new EmissiveTextureObj(Texture.loadTexture(folder + "em_top.png"),
														  Texture.loadTexture(folder + "em_bottom.png"),
														  Texture.loadTexture(folder + "em_left.png"),
														  Texture.loadTexture(folder + "em_right.png"),
														  Texture.loadTexture(folder + "em_front.png"),
														  Texture.loadTexture(folder + "em_back.png"));
						break;
					case "3":
					case "model":
						break;
				}
			}
			switch (tConfig.get("textureFormat")) {
				case "0":
				case "simple":
					return new BlockTextureObj(opaque, Texture.loadTexture(folder + "texture.png"), emissive);
				case "1":
				case "pillar":
					return new BlockTextureObj(opaque,
											   Texture.loadTexture(folder + "top.png"),
											   Texture.loadTexture(folder + "side.png"),
											   Texture.loadTexture(folder + "bottom.png"),
											   emissive);
				case "2":
				case "complex":
					return new BlockTextureObj(opaque,
											   Texture.loadTexture(folder + "top.png"),
											   Texture.loadTexture(folder + "bottom.png"),
											   Texture.loadTexture(folder + "left.png"),
											   Texture.loadTexture(folder + "right.png"),
											   Texture.loadTexture(folder + "front.png"),
											   Texture.loadTexture(folder + "back.png"),
											   emissive);
				case "3":
				case "model":
					break;
			}
		} else {
			return new BlockTextureObj(false, -1, null);
		}
		return null;
	}

	public BlockTextureObj(boolean opaque, int texture, EmissiveTextureObj emissive) {
		this.opaque = opaque;
		textureXP = texture;
		textureXN = texture;
		textureYP = texture;
		textureYN = texture;
		textureZP = texture;
		textureZN = texture;
		this.emissive = emissive;
	}

	public BlockTextureObj(boolean opaque, int topTexture, int sideTexture, int bottomTexture, EmissiveTextureObj emissive) {
		this.opaque = opaque;
		textureXP = sideTexture;
		textureXN = sideTexture;
		textureYP = topTexture;
		textureYN = bottomTexture;
		textureZP = sideTexture;
		textureZN = sideTexture;
		this.emissive = emissive;
	}

	public BlockTextureObj(boolean opaque, int top, int bottom, int left, int right, int front, int back, EmissiveTextureObj emissive) {
		this.opaque = opaque;
		textureXP = front;
		textureXN = back;
		textureZP = right;
		textureZN = left;
		textureYP = top;
		textureYN = bottom;
		this.emissive = emissive;
	}

	public static class EmissiveTextureObj {
		public final int emissiveXP;
		public final int emissiveXN;
		public final int emissiveYP;
		public final int emissiveYN;
		public final int emissiveZP;
		public final int emissiveZN;

		public EmissiveTextureObj(int texture) {
			emissiveXP = texture;
			emissiveXN = texture;
			emissiveYP = texture;
			emissiveYN = texture;
			emissiveZP = texture;
			emissiveZN = texture;
		}

		public EmissiveTextureObj(int topTexture, int sideTexture, int bottomTexture) {
			emissiveXP = sideTexture;
			emissiveXN = sideTexture;
			emissiveYP = topTexture;
			emissiveYN = bottomTexture;
			emissiveZP = sideTexture;
			emissiveZN = sideTexture;
		}

		public EmissiveTextureObj(int top, int bottom, int left, int right, int front, int back) {
			emissiveXP = front;
			emissiveXN = back;
			emissiveYP = right;
			emissiveYN = left;
			emissiveZP = top;
			emissiveZN = bottom;
		}
	}
}
