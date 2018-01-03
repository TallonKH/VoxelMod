package entities;

import data.*;
import main.*;

public class TestEntity extends Entity {
	public TestEntity(EntityType type, World world, float x, float y, float z) {
		super(type, world, x, y, z);
		this.addEntityComponent("A", new EntityComponent(this, "A"));
		float r = 0.1f;
		this.addBoundingBox(new BoundingBox(x - r, y - r, z - r, x + r, y + r, z + r));
	}

	@Override
	public void doUpdate(float delta){
		tryMove(Side.DOWN, delta * 0.1f);
	}
}
