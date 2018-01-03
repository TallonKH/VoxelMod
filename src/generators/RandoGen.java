package generators;

import main.*;

import java.util.*;

/**
 * Created by TT on 9/2/2017.
 */
public class RandoGen extends Generator {
	private Random rand = new Random();
	private final byte a = 0;
	private final byte b = Chunk.CHUNK_SIZE - 1;
	private short block,block2;
	public RandoGen(World world) {
		super(world);
		block = world.getServer().getBlockId("VoxelMod:debug");
		block2 = world.getServer().getBlockId("VoxelMod:dirt");

	}

	@Override
	public void generate(Chunk chunk) {
//		for (byte i = 0; i <2; i++) {
//			chunk.setBlockNow((byte) rand.nextInt(Chunk.CHUNK_SIZE),
//							  (byte) rand.nextInt(Chunk.CHUNK_SIZE),
//							  (byte) rand.nextInt(Chunk.CHUNK_SIZE), block2);
//		}
		chunk.setBlockNow(a, a, a, block);
		chunk.setBlockNow(a, a, b, block);
		chunk.setBlockNow(a, b, a, block);
		chunk.setBlockNow(a, b, b, block);
		chunk.setBlockNow(b, a, a, block);
		chunk.setBlockNow(b, a, b, block);
		chunk.setBlockNow(b, b, a, block);
		chunk.setBlockNow(b, b, b, block);
	}
}