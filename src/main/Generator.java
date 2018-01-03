package main;

/**
 * Created by TT on 7/21/2017.
 */
public abstract class Generator {
	public Generator(World world){}

	public abstract void generate(Chunk chunk);
}
