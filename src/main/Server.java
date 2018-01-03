package main;

import engine.*;
import entities.*;
import generators.*;

import java.io.*;
import java.net.*;
import java.util.*;
public class Server {
	private final static String fsep = java.nio.file.FileSystems.getDefault().getSeparator();
	private final String directory = "/Users/default/Documents/VoxelMod/Server";
	private final File packageDirectory = new File(directory + fsep + "packages");

	private boolean running;
	private int targetTPS = 60;
	private int currentTPS;
	private float deltaTime;

	private final Map<String, Short> blockTypeMap = new HashMap<>();
	private final Map<String, Short> entityTypeMap = new HashMap<>();
	private final List<BlockType> blockTypes = new ArrayList<>();
	private final List<EntityType> entityTypes = new ArrayList<>();
	private ResourceBundle resourceBundle;
	private boolean forceResourceBundle = false;

	private final List<World> worlds = new ArrayList<>();
	private final List<World> loadedWorlds = new ArrayList<>();
	private final List<Client> connectedClients = new ArrayList<>();

	void connect(Client c) {
		connectedClients.add(c);
		EntityPlayer entityPlayer = (EntityPlayer) loadedWorlds.get(0).spawnEntity(getEntityType("VoxelMod:player"), 50f, 50f, 50f, true);
		entityPlayer.setClient(c);
		entityPlayer.listen(c);
		c.setControlledEntity(entityPlayer);
	}

	private Thread serverThread;
	public final short blockTypeCount;
	public final short entityTypeCount;

	public static void main(String args[]) {

	}

	public Thread getServerThread(){
		return serverThread;
	}

	public List<BlockType> getBlockTypes() {
		return new ArrayList<>(blockTypes);
	}

	public short getBlockId(String name) {
		return blockTypeMap.get(name);
	}

	public short getEntityId(String name) {
		return entityTypeMap.get(name);
	}

	public EntityType getEntityType(String name) {
		return entityTypes.get(entityTypeMap.get(name));
	}

	public BlockType getBlockType(String name) {
		return blockTypes.get(blockTypeMap.get(name));
	}

	public BlockType getBlockType(short id) {
		return blockTypes.get(id);
	}

	public List<World> getWorlds() {
		return new ArrayList<>(worlds);
	}

	public boolean doesForceResourceBundle() {
		return forceResourceBundle;
	}

	public World getWorld(int index) {
		return worlds.get(index);
	}

	public List<Client> getConnectedClients() {
		return new ArrayList<>(connectedClients);
	}

	public Server() {
		File[] packages = packageDirectory.listFiles();
		short blockId = 0;
		List<BlockTextureObj> blockTextureObjs = new ArrayList<>();
		// display names
		List<String> blockNames = new ArrayList<>();

		short entityId = 0;
		List<CustomModelObj> entityModels = new ArrayList<>();
		// display names
		List<String> entityNames = new ArrayList<>();

		for (File pack : packages) {
			if(pack.isDirectory()) {
				Map<String, String> packConfig = Utils.readConfig(pack.getAbsolutePath() + fsep + "config.txt");
				String packName = packConfig.get("name") + ':';
				if (Boolean.parseBoolean(packConfig.get("blocks"))) {
					File[] blockFiles = (new File(pack.getAbsolutePath() + fsep + "blocks")).listFiles();
					for (File blockFile : blockFiles) {
						if(blockFile.isDirectory()) {
							String blockPath = blockFile.getAbsolutePath() + fsep;
							Map<String, String> blockConfig = Utils.readConfig(blockPath + "config.txt");

							BlockTextureObj resource = BlockTextureObj.parse(blockPath, blockConfig);
							String localName = blockFile.getName();
							String name = packName + localName;
							blockTypes.add(new BlockType(blockId, name, blockConfig));
							blockTypeMap.put(name, blockId);
							blockNames.add(blockConfig.get("name"));
							blockTextureObjs.add(resource);

							System.out.println("Added block " + name);
							blockId++;
						}
					}
				}

				if (Boolean.parseBoolean(packConfig.get("entities"))) {
					File[] entityFiles = (new File(pack.getAbsolutePath() + fsep + "entities")).listFiles();
					for (File entityFile : entityFiles) {
						if(entityFile.isDirectory()) {
							String entityPath = entityFile.getAbsolutePath() + fsep;
							Map<String, String> entityConfig = Utils.readConfig(entityPath + "config.txt");

							CustomModelObj resource = CustomModelObj.parse(entityPath, entityConfig);
							// file name
							String localName = entityFile.getName();
							// package:localName
							String name = packName + localName;

							Class entityClass;
							try {
								ClassLoader loader = new URLClassLoader(new URL[]{entityFile.toURI().toURL()});
								entityClass = loader.loadClass(entityConfig.get("class"));

								entityTypes.add(new EntityType(entityClass, entityId, name, entityConfig));
								entityTypeMap.put(name, entityId);
								entityNames.add(entityConfig.get("name"));
								entityModels.add(resource);

								System.out.println("Added entity " + name);
								entityId++;
							} catch (MalformedURLException | ClassNotFoundException e) {
								System.out.println("Unable to find .class for entity " + name);
							}
						}
					}
				}
			}
		}
		resourceBundle = new ResourceBundle(blockTextureObjs, entityModels, blockNames, entityNames);

		blockTypeCount = blockId;
		entityTypeCount = entityId;

		//temp
		World w = new World(this, RandoGen.class);
		worlds.add(w);
		loadedWorlds.add(w);
	}

	public float getDelta(){
		return deltaTime;
	}

	public void stop(){
		running = false;
	}

	public void startGameLoop() {
		if (!running) {
			running = true;
			serverThread = new Thread(new Runnable(){
				public void run() {
					final int timeDif = 1000 / targetTPS;
					long lastTime = System.currentTimeMillis();
					int tickrateTimer = 0;
					int secondsTimer = 0;
					int tpsCounter = 0;
					while (running) {
						long currentTime = System.currentTimeMillis();
						int deltaMillis = (int) (currentTime - lastTime);
						tickrateTimer += deltaMillis;
						secondsTimer += deltaMillis;
						if (tickrateTimer >= timeDif) {
							deltaTime = (float) tickrateTimer / timeDif;
							update(deltaTime);
							tpsCounter++;
							tickrateTimer = 0;
						} else {
							try {
								Thread.sleep(timeDif - tickrateTimer);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						if (secondsTimer >= 1000) {
							currentTPS = tpsCounter;
							tpsCounter = 0;
							secondsTimer = 0;
						}
						lastTime = currentTime;
					}
				}
			}, "VoxelModServer");
			serverThread.start();
		}
	}

	public int getTargetTPS() {
		return targetTPS;
	}

	public int getTPS() {
		return currentTPS;
	}

	public boolean isRunning() {
		return running;
	}

	void update(float delta) {
		for (World w : loadedWorlds) {
			w.doUpdate(delta);
		}
		for (Client client : connectedClients){
			client.serverTickInput();
		}
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
}
