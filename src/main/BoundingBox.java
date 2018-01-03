package main;

import data.*;
import engine.*;
import org.joml.Math;
import org.joml.*;

import java.util.*;

public class BoundingBox {
	private boolean blocksMovement = true;

	private float minX;
	private float minY;
	private float minZ;
	private float maxX;
	private float maxY;
	private float maxZ;

	/**
	 * Constructs a BoundingBox from a positive and negative corner
	 */
	public BoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	/**
	 * @return A BoundingBox constructed from two opposite corners
	 */
	public BoundingBox fromCorners(float x, float y, float z, float x2, float y2, float z2) {
		return new BoundingBox(Math.min(x, x2), Math.min(y, y2), Math.min(z, z2), Math.max(x, x2), Math.max(y, y2), Math.max(z, z2));
	}

	public List<Chunk> getInhabitedChunks(World world) {
		List<Chunk> chunks = new ArrayList<>();
		int x = Utils.blockWToChunkW(minX);
		int y = Utils.blockWToChunkW(minY);
		int z = Utils.blockWToChunkW(minZ);
		for (int mX = Utils.blockWToChunkW(maxX); x < mX; x++) {
			for (int mY = Utils.blockWToChunkW(maxY); y < mY; y++) {
				for (int mZ = Utils.blockWToChunkW(maxZ); z < mZ; z++) {
					chunks.add(world.getChunk(x, y, z, false));
				}
			}
		}
		return chunks;
	}

	public void move(Vector3f vec) {
		move(vec.x, vec.y, vec.z);
	}

	public void move(float x, float y, float z) {
		minX += x;
		minY += y;
		minZ += z;
		maxX += x;
		maxY += y;
		maxZ += z;
	}

	/**
	 * @return A BoundingBox constructed from a position and radii
	 */
	public BoundingBox fromDimensions(float x, float y, float z, float xr, float yr, float zr) {
		return new BoundingBox(x - xr, y - yr, z - zr, x + xr, y + yr, z + zr);
	}

	public boolean intersects(BoundingBox other) {
		return minX <= other.maxX && maxX >= other.maxX &&
			   minY <= other.maxY && maxY >= other.maxY &&
			   minZ <= other.maxZ && maxZ >= other.maxZ;

	}

	public boolean intersects(float x, float y, float z) {
		return minX <= x && maxX >= x &&
			   minY <= y && maxY >= y &&
			   minZ <= z && maxZ >= z;
	}

	/**
	 * @return how far the box can move in a certain direction without being blocked by blocks
	 */
	public float sideMovementTrace(World world, Side side, float maxDist, boolean loadChunks) {
		int xa = java.lang.Math.round(minX);
		int ya = java.lang.Math.round(minY);
		int za = java.lang.Math.round(minZ);
		int xb = java.lang.Math.round(maxX);
		int yb = java.lang.Math.round(maxY);
		int zb = java.lang.Math.round(maxZ);
		switch (side) {
			case FORWARD: {
				int z = zb;
				for (; z <= java.lang.Math.ceil(zb + maxDist); z++) {
					for (int x = xa; x <= xb; x++) {
						for (int y = ya; y <= yb; y++) {
							short block = world.getBlock(x, y, z, loadChunks);
							if (block < 0 || world.getServer().getBlockType(block).collide) {
								return z - maxZ - 0.5f;
							}
						}
					}
				}
				return maxDist;
			}
			case BACKWARD: {
				int z = za;
				for (; z >= java.lang.Math.floor(za - maxDist); z--) {
					for (int x = xa; x <= xb; x++) {
						for (int y = ya; y <= yb; y++) {
							short block = world.getBlock(x, y, z, loadChunks);
							if (block < 0 || world.getServer().getBlockType(block).collide) {
								return minZ - z - 0.5f;
							}
						}
					}
				}
				return maxDist;
			}
			case DOWN: {
				int y = ya;
				for (; y >= java.lang.Math.floor(ya - maxDist); y--) {
					for (int x = xa; x <= xb; x++) {
						for (int z = za; z <= zb; z++) {
							short block = world.getBlock(x, y, z, loadChunks);
							if (block < 0 || world.getServer().getBlockType(block).collide) {
								return minY - y - 0.5f;
							}
						}
					}
				}
				return maxDist;
			}
			case UP: {
				int y = yb;
				for (; y <= java.lang.Math.ceil(yb + maxDist); y++) {
					for (int x = xa; x <= xb; x++) {
						for (int z = za; z <= zb; z++) {
							short block = world.getBlock(x, y, z, loadChunks);
							if (block < 0 || world.getServer().getBlockType(block).collide) {
								return y - maxY - 0.5f;
							}
						}
					}
				}
				return maxDist;
			}
			case RIGHT: {
				int x = xb;
				for (; x <= java.lang.Math.ceil(xb + maxDist); x++) {
					for (int y = ya; y <= yb; y++) {
						for (int z = za; z <= zb; z++) {
							short block = world.getBlock(x, y, z, loadChunks);
							if (block < 0 || world.getServer().getBlockType(block).collide) {
								return x - maxX - 0.5f;
							}
						}
					}
				}
				return maxDist;
			}
			case LEFT: {
				int x = xa;
				for (; x >= java.lang.Math.floor(xa - maxDist); x--) {
					for (int y = ya; y <= yb; y++) {
						for (int z = za; z <= zb; z++) {
							short block = world.getBlock(x, y, z, loadChunks);
							if (block < 0 || world.getServer().getBlockType(block).collide) {
								return minX - x - 0.5f;
							}
						}
					}
				}
				return maxDist;
			}
		}
		return 0;
	}

	/**
	 * @return if the side of a bounding box is blocked... by blocks.
	 */
	public boolean isSideBlocked(World world, Side side, boolean loadChunks) {
		int xa = java.lang.Math.round(this.minX);
		int ya = java.lang.Math.round(this.minY);
		int za = java.lang.Math.round(this.minZ);
		int xb = java.lang.Math.round(this.maxX);
		int yb = java.lang.Math.round(this.maxY);
		int zb = java.lang.Math.round(this.maxZ);
		switch (side) {
			case FORWARD:
				for (int yi = ya; yi <= yb; yi++) {
					for (int zi = za; zi <= zb; zi++) {
						short block = world.getBlock(xb, yi, zi, loadChunks);
						if (block < 0 || world.getServer().getBlockType(block).collide) {
							return true;
						}
					}
				}
			case BACKWARD:
				for (int yi = ya; yi <= yb; yi++) {
					for (int zi = za; zi <= zb; zi++) {
						short block = world.getBlock(xa, yi, zi, loadChunks);
						if (block < 0 || world.getServer().getBlockType(block).collide) {
							return true;
						}
					}
				}
				break;
			case DOWN:
				for (int xi = xa; xi <= xb; xi++) {
					for (int zi = za; zi <= zb; zi++) {
						short block = world.getBlock(xi, ya, zi, loadChunks);
						if (block < 0 || world.getServer().getBlockType(block).collide) {
							return true;
						}
					}
				}
				break;
			case UP:
				for (int xi = xa; xi <= xb; xi++) {
					for (int zi = za; zi <= zb; zi++) {
						short block = world.getBlock(xi, yb, zi, loadChunks);
						if (block < 0 || world.getServer().getBlockType(block).collide) {
							return true;
						}
					}
				}
				break;
			case RIGHT:
				for (int xi = xa; xi <= xb; xi++) {
					for (int yi = ya; yi <= yb; yi++) {
						short block = world.getBlock(xi, yi, zb, loadChunks);
						if (block < 0 || world.getServer().getBlockType(block).collide) {
							return true;
						}
					}
				}
				break;
			case LEFT:
				for (int xi = xa; xi <= xb; xi++) {
					for (int yi = ya; yi <= yb; yi++) {
						short block = world.getBlock(xi, yi, za, loadChunks);
						if (block < 0 || world.getServer().getBlockType(block).collide) {
							return true;
						}
					}
				}
				break;
		}
		return false;
	}

	public boolean blocksMovement() {
		return blocksMovement;
	}
}