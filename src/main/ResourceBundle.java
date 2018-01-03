package main;

import java.util.*;

public class ResourceBundle {
	private final Map<Short, BlockTextureObj> blockTextures;
	private final Map<Short, String> blockNames;
	private final Map<Short, CustomModelObj> entityModels;
	private final Map<Short, String> entityNames;

	public ResourceBundle(List<BlockTextureObj> blockTextureObjs, List<CustomModelObj> entityModels, List<String> blockNames, List<String> entityNames) {
		this.blockTextures = new HashMap<>();
		this.blockNames = new HashMap<>();
		for (short i = 0; i < blockTextureObjs.size(); i++) {
			this.blockTextures.put(i, blockTextureObjs.get(i));
			this.blockNames.put(i, blockNames.get(i));
		}

		this.entityModels = new HashMap<>();
		this.entityNames = new HashMap<>();
		for (short i = 0; i < entityModels.size(); i++) {
			this.entityModels.put(i, entityModels.get(i));
			this.entityNames.put(i, entityNames.get(i));
		}
	}

	public ResourceBundle(Map<Short, BlockTextureObj> blockTextureObjs, Map<Short, CustomModelObj> entityModels, Map<Short, String> blockNames, Map<Short, String> entityNames) {
		this.blockTextures = blockTextureObjs;
		this.entityModels = entityModels;
		this.blockNames = blockNames;
		this.entityNames = entityNames;
	}

	public ResourceBundle(ResourceBundle... bundles) {
		blockTextures = new HashMap<>();
		blockNames = new HashMap<>();
		entityModels = new HashMap<>();
		entityNames = new HashMap<>();
		for (ResourceBundle bundle : bundles) {
			if (bundle != null) {
				blockTextures.putAll(bundle.blockTextures);
				blockNames.putAll(bundle.blockNames);
				entityModels.putAll(bundle.entityModels);
				entityNames.putAll(bundle.entityNames);
			}
		}
	}

	public CustomModelObj getEntityModel(short entity) {
		return entityModels.get(entity);
	}

	public BlockTextureObj getBlockTextureObj(short block) {
		return blockTextures.get(block);
	}

	public boolean isBlockOpaque(short block) {
		if (block < 0) {
			return false;
		}
		return blockTextures.get(block).opaque;
	}
}
