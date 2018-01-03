package main;

import java.util.*;

public class BlockType {
	public final short id;
	public final String name;
	public final boolean collide;

	public BlockType(short id, String name, Map<String,String> config) {
		this.id = id;
		this.name = name;
		this.collide = Boolean.parseBoolean(config.get("collide"));
	}
}