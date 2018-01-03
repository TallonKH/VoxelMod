package entities;

import main.*;

public class EntityPlayer extends Entity implements Entity.EntityListener{
	private Client client;
	public EntityPlayer(EntityType type, World world, float x, float y, float z) {
		super(type, world, x, y, z);
		this.addEntityComponent("A", new EntityComponent(this, "A"));
		float r = 0.1f;
		this.addBoundingBox(new BoundingBox(x - r, y - r, z - r, x + r, y + r, z + r));
		this.setAlwaysLoaded(true);
	}

	public void setClient(Client client){
		this.client = client;
		this.setChunkLoadRadius(client.getChunkGenDistance());
	}

	@Override
	public void onLocationChanged(){
		super.onLocationChanged();
	}

	@Override
	public void entityChangedLocation(Entity entity){

	}
}
