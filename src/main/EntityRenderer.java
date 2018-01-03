package main;

import engine.*;

import java.util.*;

public class EntityRenderer {
	private final Entity entity;
	private final ResourceBundle resourceBundle;
	final Map<EntityComponent, SceneComponent> components = new HashMap<>();

	public EntityRenderer(Entity entity, ResourceBundle resourceBundle) {
		this.entity = entity;
		this.resourceBundle = resourceBundle;
		CustomModelObj model = resourceBundle.getEntityModel(entity.type.id);
		model.getMeshes().forEach((name, meshData) -> {
			EntityComponent eComp = entity.getEntityComponent(name);
			Mesh mesh = new Mesh(meshData);
			SceneComponent sComp = new SceneComponent(mesh);
			update();
			components.put(eComp, sComp);
		});
	}

	void cleanup() {
		Collection<SceneComponent> sceneComponents = components.values();
		for (SceneComponent sceneComponent : sceneComponents) {
			for (Mesh mesh : sceneComponent.getMeshes()) {
				mesh.cleanUp();
			}
		}
	}

	void update(){
		components.forEach((eComp, sComp) -> {
			sComp.setPosition(eComp.getLocation().x + entity.getLocation().x, eComp.getLocation().y + entity.getLocation().y, eComp.getLocation().z + entity.getLocation().z);
			sComp.setRotation(eComp.getRotation().x + entity.getRotation().x, eComp.getRotation().y + entity.getRotation().y, eComp.getRotation().z + entity.getRotation().z);
//			sComp.setScale(eComp.getScale() * entity.getScale());
		});
	}
}
