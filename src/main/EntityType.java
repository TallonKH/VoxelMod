package main;

import java.util.*;

public class EntityType {
	public final short id;
	public final String name;
	public final Class<? extends Entity> entityClass;

	public EntityType(Class<? extends Entity> eClass, short id, String name, Map<String,String> config) {
		this.entityClass = eClass;
		this.id = id;
		this.name = name;
	}
}
