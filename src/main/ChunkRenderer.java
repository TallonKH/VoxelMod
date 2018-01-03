package main;

import data.*;
import engine.*;

import java.util.*;

import static data.Byte3.*;
import static main.Chunk.*;

public class ChunkRenderer {
	private final static float[] brightnessVals = {0.8f, 0.7f, 1.0f, 0.5f, 0.6f, 0.9f};
	private final Chunk chunk;
	private final ResourceBundle resourceBundle;
	private final Set<Short> typesPendingMesh = new HashSet<>();
	final Map<Short, SceneComponent> chunkComponents = new HashMap<>();
	private short fillValue = -2;

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public ChunkRenderer(Chunk chunk, ResourceBundle resourceBundle) {
		this.chunk = chunk;
		this.resourceBundle = resourceBundle;
		if (chunk.isPopulated()) {
			typesPendingMesh.addAll(chunk.getBlockTypes());
		}
	}

	public void filled(short type) {
		typesPendingMesh.clear();
		fillValue = type;
	}

	private void fillMesh() {
		chunkComponents.clear();
		BlockTextureObj bundle = resourceBundle.getBlockTextureObj(fillValue);
		if (bundle == null) {
			return;
		}
		if (bundle.opaque) {
			float max = CHUNK_SIZE - 0.5f;
			float min = -0.5f;

			SceneComponent cube;
			if (bundle.emissive == null) {
				cube = new SceneComponent(new Mesh(new float[]{max, min, min, max, max, min, max, min, max, max, max,
															   max},
												   new float[]{0, CHUNK_SIZE, 0, 0, CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE,
															   0},
												   new int[]{0, 1, 2, 1, 2, 3},
												   bundle.textureXP, Texture.blackTexture, brightnessVals[0]),
										  new Mesh(new float[]{min, min, min, min, max, min, min, min, max, min, max,
															   max},
												   new float[]{0, CHUNK_SIZE, 0, 0, CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE,
															   0},
												   new int[]{0, 1, 2, 1, 2, 3},
												   bundle.textureXN, Texture.blackTexture, brightnessVals[1]),
										  new Mesh(new float[]{min, max, min, max, max, min, min, max, max, max, max,
															   max},
												   new float[]{0, CHUNK_SIZE, 0, 0, CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE,
															   0},
												   new int[]{0, 1, 2, 1, 2, 3},
												   bundle.textureYP, Texture.blackTexture, brightnessVals[2]),
										  new Mesh(new float[]{min, min, min, max, min, min, min, min, max, max, min,
															   max},
												   new float[]{0, CHUNK_SIZE, 0, 0, CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE,
															   0},
												   new int[]{0, 1, 2, 1, 2, 3},
												   bundle.textureYN, Texture.blackTexture, brightnessVals[3]),
										  new Mesh(new float[]{min, min, max, max, min, max, min, max, max, max, max,
															   max},
												   new float[]{0, CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE, 0, 0, CHUNK_SIZE,
															   0},
												   new int[]{0, 1, 2, 1, 2, 3},
												   bundle.textureZP, Texture.blackTexture, brightnessVals[4]),
										  new Mesh(new float[]{min, min, min, max, min, min, min, max, min,
															   max, max,
															   min},
												   new float[]{0, CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE, 0,
															   0,
															   CHUNK_SIZE,
															   0},
												   new int[]{0, 1, 2, 1, 2, 3},
												   bundle.textureZN, Texture.blackTexture, brightnessVals[5]));
			} else {
				cube = new SceneComponent(new Mesh(new float[]{max, min, min, max, max, min, max, min,
															   max,
															   max, max,
															   max},
												   new float[]{0, CHUNK_SIZE, 0, 0, CHUNK_SIZE,
															   CHUNK_SIZE,
															   CHUNK_SIZE,
															   0},
												   new int[]{0, 1, 2, 1, 2, 3},
												   bundle.textureXP, bundle.emissive.emissiveXP, brightnessVals[0]),
										  new Mesh(new float[]{min, min, min, min, max, min, min, min,
															   max,
															   min, max,
															   max},
												   new float[]{0, CHUNK_SIZE, 0, 0, CHUNK_SIZE,
															   CHUNK_SIZE,
															   CHUNK_SIZE,
															   0},
												   new int[]{0, 1, 2, 1, 2, 3},
												   bundle.textureXN, bundle.emissive.emissiveXN, brightnessVals[1]),
										  new Mesh(new float[]{min, max, min, max, max, min, min, max,
															   max,
															   max, max,
															   max},
												   new float[]{0, CHUNK_SIZE, 0, 0, CHUNK_SIZE,
															   CHUNK_SIZE,
															   CHUNK_SIZE,
															   0},
												   new int[]{0, 1, 2, 1, 2, 3},
												   bundle.textureYP, bundle.emissive.emissiveYP, brightnessVals[2]),
										  new Mesh(new float[]{min, min, min, max, min, min, min, min,
															   max,
															   max, min,
															   max},
												   new float[]{0, CHUNK_SIZE, 0, 0, CHUNK_SIZE,
															   CHUNK_SIZE,
															   CHUNK_SIZE,
															   0},
												   new int[]{0, 1, 2, 1, 2, 3},
												   bundle.textureYN, bundle.emissive.emissiveYN, brightnessVals[3]),
										  new Mesh(new float[]{min, min, max, max, min, max, min, max,
															   max,
															   max, max,
															   max},
												   new float[]{0, CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE, 0,
															   0,
															   CHUNK_SIZE,
															   0},
												   new int[]{0, 1, 2, 1, 2, 3},
												   bundle.textureZP, bundle.emissive.emissiveZP, brightnessVals[4]),
										  new Mesh(new float[]{min, min, min, max, min, min, min, max,
															   min,
															   max, max,
															   min},
												   new float[]{0, CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE, 0,
															   0,
															   CHUNK_SIZE,
															   0},
												   new int[]{0, 1, 2, 1, 2, 3},
												   bundle.textureZN, bundle.emissive.emissiveZN, brightnessVals[5]));
			}
			cube.setPosition(chunk.blockOffsetX, chunk.blockOffsetY, chunk.blockOffsetZ);
			chunkComponents.put(fillValue, cube);
		}
	}

	public boolean isBlockSideExposed(byte x, byte y, byte z, Side side) {
		x += side.x;
		y += side.y;
		z += side.z;

		short b = chunk.getBlock(x, y, z);
		if (b > -1) {
			return !resourceBundle.isBlockOpaque(b);
		}
		return !resourceBundle.isBlockOpaque(chunk.getWorld().getBlock(chunk.blockOffsetX + x, chunk.blockOffsetY + y, chunk.blockOffsetZ + z, false));
	}

	public void updateBlock(short block) {
		typesPendingMesh.add(block);
	}

	void cleanup() {
		Collection<SceneComponent> sceneComponents = chunkComponents.values();
		for (SceneComponent sceneComponent : sceneComponents) {
			for (Mesh mesh : sceneComponent.getMeshes()) {
				mesh.cleanUp();
			}
		}
	}

	public void update() {
		if (fillValue > -2) {
			fillMesh();
			fillValue = -2;
			// Clear pending changes because they don't matter if the chunk has been filled
			typesPendingMesh.clear();
			return;
		}

		for (short s : typesPendingMesh) {
			SceneComponent comp = createBlockSceneComponent(s);
			if (comp != null) {
				chunkComponents.put(s, comp);
			}
		}
		typesPendingMesh.clear();
	}

	private SceneComponent createBlockSceneComponent(short block) {
		if (resourceBundle.isBlockOpaque(block)) {
			BlockTextureObj textureObj = resourceBundle.getBlockTextureObj(block);

//			meshes.add(meshDebug(textureObj, block));
			SceneComponent sceneComponent = new SceneComponent(
					meshXP(textureObj, block),
					meshXN(textureObj, block),
					meshYP(textureObj, block),
					meshYN(textureObj, block),
					meshZP(textureObj, block),
					meshZN(textureObj, block));

			sceneComponent.setPosition(chunk.blockOffsetX, chunk.blockOffsetY, chunk.blockOffsetZ);
			return sceneComponent;
		}
		return null;
	}

	private Mesh meshDebug(BlockTextureObj textureObj, short blockType) {
		List<Float> verts = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		List<Float> texCoords = new ArrayList<>();
		int i = 0;
		for (byte y = 0; y < CHUNK_SIZE; y++) {
			for (byte z = 0; z < CHUNK_SIZE; z++) {
				for (byte x = 0; x < CHUNK_SIZE; x++) {
					if (chunk.getBlock(x, y, z) == blockType) {
						verts.addAll(Arrays.asList(x - 0.25f, y + 0.25f, z + 0.25f,
												   x - 0.25f, y - 0.25f, z + 0.25f,
												   x + 0.25f, y - 0.25f, z + 0.25f,
												   x + 0.25f, y + 0.25f, z + 0.25f,
												   x - 0.25f, y + 0.25f, z - 0.25f,
												   x + 0.25f, y + 0.25f, z - 0.25f,
												   x - 0.25f, y - 0.25f, z - 0.25f,
												   x + 0.25f, y - 0.25f, z - 0.25f));
						indices.addAll(Arrays.asList(i, i + 1, i + 3, i + 3, i + 1, i + 2,
													 i + 4, i, i + 3, i + 5, i + 4, i + 3,
													 i + 3, i + 2, i + 7, i + 5, i + 3, i + 7,
													 i + 6, i + 1, i, i + 6, i, i + 4,
													 i + 2, i + 1, i + 6, i + 2, i + 6, i + 7,
													 i + 7, i + 6, i + 4, i + 7, i + 4, i + 5));
						texCoords.addAll(Arrays.asList(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f));
						i += 8;
					}
				}
			}
		}
		return new Mesh(verts, texCoords, indices, textureObj.textureXP, Texture.blackTexture);
	}

	private Mesh meshXP(BlockTextureObj textureObj, short blockType) {
		List<Float> verts = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		List<Float> texCoords = new ArrayList<>();
		Map<Short, Byte> skips = new HashMap<>();
		for (byte x = 0; x < CHUNK_SIZE; x++) {
			float xf = x + 0.5f;
			for (byte z = 0; z < CHUNK_SIZE; z++) {
				for (byte y = 0; y < CHUNK_SIZE; y++) {
					Byte skipNum = skips.get(bytesToShort(y, z));
					while (skipNum != null) {
						y = skipNum;
						skipNum = skips.get(bytesToShort(y, z));
					}
					if (chunk.getBlock(x, y, z) == blockType && isBlockSideExposed(x, y, z, Side.RIGHT)) {
						int cornerIndices = verts.size() / 3;
						verts.add(xf);
						verts.add(y - 0.5f);
						verts.add(z - 0.5f);

						byte maxA = (byte) (y + 1);
						for (; maxA < CHUNK_SIZE; maxA++) {
							if (skips.containsKey(bytesToShort(maxA, z)) || chunk.getBlock(x, maxA, z) != blockType || !isBlockSideExposed(x, maxA, z, Side.RIGHT)) {
								break;
							}
						}

						verts.add(xf);
						verts.add(maxA - 0.5f);
						verts.add(z - 0.5f);

						byte maxB = (byte) (z + 1);

						outerLoop:
						for (; maxB < CHUNK_SIZE; maxB++) {
							for (byte a = y; a < maxA; a++) {
								if (skips.containsKey(bytesToShort(a, maxB)) || chunk.getBlock(x, a, maxB) != blockType || !isBlockSideExposed(x, a, maxB, Side.RIGHT)) {
									break outerLoop;
								}
							}
							skips.put(bytesToShort(y, maxB), maxA);
						}

						verts.add(xf);
						verts.add(y - 0.5f);
						verts.add(maxB - 0.5f);

						verts.add(xf);
						verts.add(maxA - 0.5f);
						verts.add(maxB - 0.5f);


						indices.add(cornerIndices + 1);
						indices.add(cornerIndices);
						indices.add(cornerIndices + 2);
						indices.add(cornerIndices + 1);
						indices.add(cornerIndices + 2);
						indices.add(cornerIndices + 3);


						texCoords.add(0f);
						texCoords.add((float) maxA - y);

						texCoords.add(0f);
						texCoords.add(0f);

						texCoords.add((float) maxB - z);
						texCoords.add((float) maxA - y);

						texCoords.add((float) maxB - z);
						texCoords.add(0f);


						y = (byte) (maxA - 1);
					}
				}
			}
			skips.clear();
		}

		if (textureObj.emissive == null) {
			return new Mesh(verts, texCoords, indices, textureObj.textureXP, brightnessVals[0]);
		} else {
			return new Mesh(verts, texCoords, indices, textureObj.textureXP, textureObj.emissive.emissiveXP, brightnessVals[0]);
		}
	}

	private Mesh meshXN(BlockTextureObj textureObj, short blockType) {
		List<Float> verts = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		List<Float> texCoords = new ArrayList<>();
		Map<Short, Byte> skips = new HashMap<>();
		for (byte x = CHUNK_SIZE_MINUS_ONE; x >= 0; x--) {
			float xf = x - 0.5f;
			for (byte z = 0; z < CHUNK_SIZE; z++) {
				for (byte y = 0; y < CHUNK_SIZE; y++) {
					Byte skipNum = skips.get(bytesToShort(y, z));
					while (skipNum != null) {
						y = skipNum;
						skipNum = skips.get(bytesToShort(y, z));
					}
					if (chunk.getBlock(x, y, z) == blockType && isBlockSideExposed(x, y, z, Side.LEFT)) {
						int cornerIndices = verts.size() / 3;
						verts.add(xf);
						verts.add(y - 0.5f);
						verts.add(z - 0.5f);

						byte maxA = (byte) (y + 1);
						for (; maxA < CHUNK_SIZE; maxA++) {
							if (skips.containsKey(bytesToShort(maxA, z)) || chunk.getBlock(x, maxA, z) != blockType || !isBlockSideExposed(x, maxA, z, Side.LEFT)) {
								break;
							}
						}

						verts.add(xf);
						verts.add(maxA - 0.5f);
						verts.add(z - 0.5f);

						byte maxB = (byte) (z + 1);

						outerLoop:
						for (; maxB < CHUNK_SIZE; maxB++) {
							for (byte a = y; a < maxA; a++) {
								if (skips.containsKey(bytesToShort(a, maxB)) || chunk.getBlock(x, a, maxB) != blockType || !isBlockSideExposed(x, a, maxB, Side.LEFT)) {
									break outerLoop;
								}
							}
							skips.put(bytesToShort(y, maxB), maxA);
						}

						verts.add(xf);
						verts.add(y - 0.5f);
						verts.add(maxB - 0.5f);

						verts.add(xf);
						verts.add(maxA - 0.5f);
						verts.add(maxB - 0.5f);


						indices.add(cornerIndices);
						indices.add(cornerIndices + 1);
						indices.add(cornerIndices + 2);
						indices.add(cornerIndices + 2);
						indices.add(cornerIndices + 1);
						indices.add(cornerIndices + 3);

						texCoords.add(0f);
						texCoords.add((float) maxA - y);

						texCoords.add(0f);
						texCoords.add(0f);

						texCoords.add((float) maxB - z);
						texCoords.add((float) maxA - y);

						texCoords.add((float) maxB - z);
						texCoords.add(0f);

						y = (byte) (maxA - 1);
					}
				}
			}
			skips.clear();
		}
		if (textureObj.emissive == null) {
			return new Mesh(verts, texCoords, indices, textureObj.textureXN, brightnessVals[1]);
		} else {
			return new Mesh(verts, texCoords, indices, textureObj.textureXN, textureObj.emissive.emissiveXN, brightnessVals[1]);
		}
	}

	private Mesh meshYP(BlockTextureObj textureObj, short blockType) {
		List<Float> verts = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		List<Float> texCoords = new ArrayList<>();
		Map<Short, Byte> skips = new HashMap<>();
		for (byte y = 0; y < CHUNK_SIZE; y++) {
			float yf = y + 0.5f;
			for (byte z = 0; z < CHUNK_SIZE; z++) {
				for (byte x = 0; x < CHUNK_SIZE; x++) {
					Byte skipNum = skips.get(bytesToShort(x, z));
					while (skipNum != null) {
						x = skipNum;
						skipNum = skips.get(bytesToShort(x, z));
					}

					if (chunk.getBlock(x, y, z) == blockType && isBlockSideExposed(x, y, z, Side.UP)) {
						int cornerIndices = verts.size() / 3;
						verts.add(x - 0.5f);
						verts.add(yf);
						verts.add(z - 0.5f);

						byte maxA = (byte) (x + 1);
						for (; maxA < CHUNK_SIZE; maxA++) {
							if (skips.containsKey(bytesToShort(maxA, z)) || chunk.getBlock(maxA, y, z) != blockType || !isBlockSideExposed(maxA, y, z, Side.UP)) {
								break;
							}
						}

						verts.add(maxA - 0.5f);
						verts.add(yf);
						verts.add(z - 0.5f);

						byte maxB = (byte) (z + 1);

						outerLoop:
						for (; maxB < CHUNK_SIZE; maxB++) {
							for (byte a = x; a < maxA; a++) {
								if (skips.containsKey(bytesToShort(a, maxB)) || chunk.getBlock(a, y, maxB) != blockType || !isBlockSideExposed(a, y, maxB, Side.UP)) {
									break outerLoop;
								}
							}
							skips.put(bytesToShort(x, maxB), maxA);
						}

						verts.add(x - 0.5f);
						verts.add(yf);
						verts.add(maxB - 0.5f);

						verts.add(maxA - 0.5f);
						verts.add(yf);
						verts.add(maxB - 0.5f);


						indices.add(cornerIndices);
						indices.add(cornerIndices + 1);
						indices.add(cornerIndices + 2);
						indices.add(cornerIndices + 2);
						indices.add(cornerIndices + 1);
						indices.add(cornerIndices + 3);


						texCoords.add(0f);
						texCoords.add((float) maxA - x);

						texCoords.add(0f);
						texCoords.add(0f);

						texCoords.add((float) maxB - z);
						texCoords.add((float) maxA - x);

						texCoords.add((float) maxB - z);
						texCoords.add(0f);


						x = (byte) (maxA - 1);
					}
				}
			}
			skips.clear();
		}
		if (textureObj.emissive == null) {
			return new Mesh(verts, texCoords, indices, textureObj.textureYP, brightnessVals[2]);
		} else {
			return new Mesh(verts, texCoords, indices, textureObj.textureYP, textureObj.emissive.emissiveYP, brightnessVals[2]);
		}
	}

	private Mesh meshYN(BlockTextureObj textureObj, short blockType) {
		List<Float> verts = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		List<Float> texCoords = new ArrayList<>();
		Map<Short, Byte> skips = new HashMap<>();
		for (byte y = CHUNK_SIZE_MINUS_ONE; y >= 0; y--) {
			float yf = y - 0.5f;
			for (byte z = 0; z < CHUNK_SIZE; z++) {
				for (byte x = 0; x < CHUNK_SIZE; x++) {
					Byte skipNum = skips.get(bytesToShort(x, z));
					while (skipNum != null) {
						x = skipNum;
						skipNum = skips.get(bytesToShort(x, z));
					}
					if (chunk.getBlock(x, y, z) == blockType && isBlockSideExposed(x, y, z, Side.DOWN)) {
						int cornerIndices = verts.size() / 3;
						verts.add(x - 0.5f);
						verts.add(yf);
						verts.add(z - 0.5f);

						byte maxA = (byte) (x + 1);
						for (; maxA < CHUNK_SIZE; maxA++) {
							if (skips.containsKey(bytesToShort(maxA, z)) || chunk.getBlock(maxA, y, z) != blockType || !isBlockSideExposed(maxA, y, z, Side.DOWN)) {
								break;
							}
						}

						verts.add(maxA - 0.5f);
						verts.add(yf);
						verts.add(z - 0.5f);

						byte maxB = (byte) (z + 1);

						outerLoop:
						for (; maxB < CHUNK_SIZE; maxB++) {
							for (byte a = x; a < maxA; a++) {
								if (skips.containsKey(bytesToShort(a, maxB)) || chunk.getBlock(a, y, maxB) != blockType || !isBlockSideExposed(a, y, maxB, Side.DOWN)) {
									break outerLoop;
								}
							}
							skips.put(bytesToShort(x, maxB), maxA);
						}

						verts.add(x - 0.5f);
						verts.add(yf);
						verts.add(maxB - 0.5f);

						verts.add(maxA - 0.5f);
						verts.add(yf);
						verts.add(maxB - 0.5f);


						indices.add(cornerIndices + 1);
						indices.add(cornerIndices);
						indices.add(cornerIndices + 2);
						indices.add(cornerIndices + 1);
						indices.add(cornerIndices + 2);
						indices.add(cornerIndices + 3);


						texCoords.add(0f);
						texCoords.add((float) maxA - x);

						texCoords.add(0f);
						texCoords.add(0f);

						texCoords.add((float) maxB - z);
						texCoords.add((float) maxA - x);

						texCoords.add((float) maxB - z);
						texCoords.add(0f);


						x = (byte) (maxA - 1);
					}
				}
			}
			skips.clear();
		}
		if (textureObj.emissive == null) {
			return new Mesh(verts, texCoords, indices, textureObj.textureYN, brightnessVals[3]);
		} else {
			return new Mesh(verts, texCoords, indices, textureObj.textureYN, textureObj.emissive.emissiveYN, brightnessVals[3]);
		}
	}

	private Mesh meshZP(BlockTextureObj textureObj, short blockType) {
		List<Float> verts = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		List<Float> texCoords = new ArrayList<>();
		Map<Short, Byte> skips = new HashMap<>();
		for (byte z = 0; z < CHUNK_SIZE; z++) {
			float zf = z + 0.5f;
			for (byte y = 0; y < CHUNK_SIZE; y++) {
				for (byte x = 0; x < CHUNK_SIZE; x++) {
					Byte skipNum = skips.get(bytesToShort(x, y));
					while (skipNum != null) {
						x = skipNum;
						skipNum = skips.get(bytesToShort(x, y));
					}
					if (chunk.getBlock(x, y, z) == blockType && isBlockSideExposed(x, y, z, Side.FORWARD)) {
						int cornerIndices = verts.size() / 3;
						verts.add(x - 0.5f);
						verts.add(y - 0.5f);
						verts.add(zf);

						byte maxA = (byte) (x + 1);
						for (; maxA < CHUNK_SIZE; maxA++) {
							if (skips.containsKey(bytesToShort(maxA, y)) || chunk.getBlock(maxA, y, z) != blockType || !isBlockSideExposed(maxA, y, z, Side.FORWARD)) {
								break;
							}
						}

						verts.add(maxA - 0.5f);
						verts.add(y - 0.5f);
						verts.add(zf);

						byte maxB = (byte) (y + 1);

						outerLoop:
						for (; maxB < CHUNK_SIZE; maxB++) {
							for (byte a = x; a < maxA; a++) {
								if (skips.containsKey(bytesToShort(a, maxB)) || chunk.getBlock(a, maxB, z) != blockType || !isBlockSideExposed(a, maxB, z, Side.FORWARD)) {
									break outerLoop;
								}
							}
							skips.put(bytesToShort(x, maxB), maxA);
						}

						verts.add(x - 0.5f);
						verts.add(maxB - 0.5f);
						verts.add(zf);

						verts.add(maxA - 0.5f);
						verts.add(maxB - 0.5f);
						verts.add(zf);


						indices.add(cornerIndices + 1);
						indices.add(cornerIndices);
						indices.add(cornerIndices + 2);
						indices.add(cornerIndices + 1);
						indices.add(cornerIndices + 2);
						indices.add(cornerIndices + 3);

						texCoords.add(0f);
						texCoords.add((float) maxB - y);

						texCoords.add((float) maxA - x);
						texCoords.add((float) maxB - y);

						texCoords.add(0f);
						texCoords.add(0f);

						texCoords.add((float) maxA - x);
						texCoords.add(0f);

						x = (byte) (maxA - 1);
					}
				}
			}
			skips.clear();
		}
		if (textureObj.emissive == null) {
			return new Mesh(verts, texCoords, indices, textureObj.textureZP, brightnessVals[4]);
		} else {
			return new Mesh(verts, texCoords, indices, textureObj.textureZP, textureObj.emissive.emissiveZP, brightnessVals[4]);
		}
	}

	private Mesh meshZN(BlockTextureObj textureObj, short blockType) {
		List<Float> verts = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		List<Float> texCoords = new ArrayList<>();
		Map<Short, Byte> skips = new HashMap<>();
		for (byte z = CHUNK_SIZE_MINUS_ONE; z >= 0; z--) {
			float zf = z - 0.5f;
			for (byte y = 0; y < CHUNK_SIZE; y++) {
				for (byte x = 0; x < CHUNK_SIZE; x++) {
					Byte skipNum = skips.get(bytesToShort(x, y));
					while (skipNum != null) {
						x = skipNum;
						skipNum = skips.get(bytesToShort(x, y));
					}
					if (chunk.getBlock(x, y, z) == blockType && isBlockSideExposed(x, y, z, Side.BACKWARD)) {
						int cornerIndices = verts.size() / 3;
						verts.add(x - 0.5f);
						verts.add(y - 0.5f);
						verts.add(zf);

						byte maxA = (byte) (x + 1);
						for (; maxA < CHUNK_SIZE; maxA++) {
							if (skips.containsKey(bytesToShort(maxA, y)) || chunk.getBlock(maxA, y, z) != blockType || !isBlockSideExposed(maxA, y, z, Side.BACKWARD)) {
								break;
							}
						}

						verts.add(maxA - 0.5f);
						verts.add(y - 0.5f);
						verts.add(zf);

						byte maxB = (byte) (y + 1);

						outerLoop:
						for (; maxB < CHUNK_SIZE; maxB++) {
							for (byte a = x; a < maxA; a++) {
								if (skips.containsKey(bytesToShort(a, maxB)) || chunk.getBlock(a, maxB, z) != blockType || !isBlockSideExposed(a, maxB, z, Side.BACKWARD)) {
									break outerLoop;
								}
							}
							skips.put(bytesToShort(x, maxB), maxA);
						}

						verts.add(x - 0.5f);
						verts.add(maxB - 0.5f);
						verts.add(zf);

						verts.add(maxA - 0.5f);
						verts.add(maxB - 0.5f);
						verts.add(zf);


						indices.add(cornerIndices);
						indices.add(cornerIndices + 1);
						indices.add(cornerIndices + 2);
						indices.add(cornerIndices + 2);
						indices.add(cornerIndices + 1);
						indices.add(cornerIndices + 3);

						texCoords.add(0f);
						texCoords.add((float) maxB - y);

						texCoords.add((float) maxA - x);
						texCoords.add((float) maxB - y);

						texCoords.add(0f);
						texCoords.add(0f);

						texCoords.add((float) maxA - x);
						texCoords.add(0f);

						x = (byte) (maxA - 1);
					}
				}
			}
			skips.clear();
		}
		if (textureObj.emissive == null) {
			return new Mesh(verts, texCoords, indices, textureObj.textureZN, brightnessVals[5]);
		} else {
			return new Mesh(verts, texCoords, indices, textureObj.textureZN, textureObj.emissive.emissiveZN, brightnessVals[5]);
		}
	}
}
