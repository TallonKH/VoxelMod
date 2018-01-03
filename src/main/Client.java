package main;

import data.*;
import engine.*;
import org.joml.*;
import org.joml.Math;

import java.io.*;
import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class Client implements Entity.EntityListener {
	private final static String fsep = java.nio.file.FileSystems.getDefault().getSeparator();
	private final String directory = "/Users/default/Documents/VoxelMod/Client";
	private final File packageDirectory = new File(directory + fsep + "packages");

	private final GameWindow window;
	private final MouseInput mouseInput;
	private final Renderer renderer;

	private final float CAMERA_POS_STEP = 0.4f;
	private final float MOUSE_SENSITIVITY = 0.4f;
	private final Vector3f cameraInc;
	private final Camera camera;

	private int targetFps = 60;
	private int currentFPS;
	private byte chunkDrawDistance = (byte) 3;
	private byte chunkGenDistance = (byte) (chunkDrawDistance + 1);
	private byte entityDrawDistance = (byte) 2;

	private Server server;
	private ResourceBundle resourceBundle;

	private final Map<Chunk, ChunkRenderer> chunkRenderers = new HashMap<>();
	private final Queue<Chunk> chunksPendingUnrender = new ArrayDeque<>();
	private final Queue<Chunk> chunksPendingRender = new ArrayDeque<>();

	private final Map<Entity, EntityRenderer> entityRenderers = new HashMap<>();
	private final Queue<Entity> entitiesPendingRender = new ArrayDeque<>();
	private final Queue<Entity> entitiesPendingUnrender = new ArrayDeque<>();

	private final List<SceneComponent> sceneComponents = new ArrayList<>();
	private final List<String> enabledResourceBundles = new ArrayList<>(Arrays.asList("Sawtooth"));
	private boolean running;

	private Entity controlledEntity;

	//Keys
	private boolean primaryAction;
	private boolean secondaryAction;
	private boolean moveForward;
	private boolean moveBackward;
	private boolean moveLeft;
	private boolean moveRight;
	private boolean moveUp;
	private boolean moveDown;

	public ChunkRenderer getChunkRenderer(Chunk chunk) {
		return chunkRenderers.get(chunk);
	}

	public byte getEntityDrawDistance() {
		return entityDrawDistance;
	}

	public Client() {
		window = new GameWindow("VoxelMod", 800, 800, false);
		renderer = new Renderer();
		camera = new Camera();
		cameraInc = new Vector3f(0, 0, 0);
		mouseInput = new MouseInput();
	}

	private void connect(Server server) {
		this.server = server;
		//temp
		if (!server.isRunning()) {
			server.startGameLoop();
		}
		server.connect(this);

		if (server.doesForceResourceBundle()) {
			this.resourceBundle = server.getResourceBundle();
		} else {
			this.resourceBundle = new ResourceBundle(server.getResourceBundle(), parseResourceBundles());
		}
	}

	public ResourceBundle parseResourceBundles() {
		File[] bundleFiles = packageDirectory.listFiles();
		if (bundleFiles == null) {
			return null;
		}

		Map<String, File> bundles = new HashMap<>();

		for (File bundle : bundleFiles) {
			if (bundle.isDirectory()) {
				bundles.put(Utils.readConfig(bundle.getAbsolutePath() + fsep + "config.txt").get("name"), bundle);
			}

		}

		Map<Short, BlockTextureObj> blockTextureObjs = new HashMap<>();
		Map<Short, CustomModelObj> entityModels = new HashMap<>();
		Map<Short, String> blockNames = new HashMap<>();
		Map<Short, String> entityNames = new HashMap<>();
		for (String name : enabledResourceBundles) {
			File[] packages = bundles.get(name).listFiles();
			if (packages != null) {
				for (File pack : packages) {
					String packName = pack.getName() + ':';
					File[] blockFiles = (new File(pack.getAbsolutePath() + fsep + "blocks")).listFiles();
					if (blockFiles != null) {
						for (File blockFile : blockFiles) {
							if (blockFile.isDirectory()) {
								String blockPath = blockFile.getAbsolutePath() + fsep;
								short id = server.getBlockId(packName + blockFile.getName());
								Map<String, String> blockConfig = Utils.readConfig(blockPath + "config.txt");
								blockTextureObjs.put(id, BlockTextureObj.parse(blockPath, blockConfig));
								blockNames.put(id, blockConfig.get("name"));
							}
						}
					}
					File[] entityFiles = (new File(pack.getAbsolutePath() + fsep + "entities")).listFiles();
					if (entityFiles != null) {
						for (File entityFile : entityFiles) {
							if (entityFile.isDirectory()) {
								String entityPath = entityFile.getAbsolutePath() + fsep;
								short id = server.getEntityId(packName + entityFile.getName());
								Map<String, String> entityConfig = Utils.readConfig(entityPath + "config.txt");
								entityModels.put(id, CustomModelObj.parse(entityPath, entityConfig));
								entityNames.put(id, entityConfig.get("name"));
							}
						}
					}
				}
			}
		}

		return new ResourceBundle(blockTextureObjs, entityModels, blockNames, entityNames);
	}

	public void start() {
		try {
			init();
			startGameLoop();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cleanup();
		}
	}

	public void init() throws Exception {
		window.init();
		mouseInput.init(window);
		renderer.init(window);

		//temp
		connect(new Server());
//		for (byte x = 0; x < 2; x++) {
//			for (byte y = 0; y < 8; y++) {
//				for (byte z = 0; z < 2; z++) {
//					Chunk c = world.getChunk(x, y, z, true);
//					ChunkRenderer cr = new ChunkRenderer(c, resourceBundle);
//					cr.update();
//					chunkRenderers.put(c, cr);
//				}
//			}
//		}
	}

	private Random rand = new Random();

	public void serverTickInput() {
		if (controlledEntity == null) {
			System.out.println("NULL");
			return;
		}
		// Update camera position
		if (moveDir != null) {
			controlledEntity.tryMove(moveDir, 0.5f);
		}
		camera.setPosition(controlledEntity.getLocation());
		cameraInc.set(0, 0, 0);
		moveDir = null;

		float x = camera.getPosition().x;
		float y = camera.getPosition().y;
		float z = camera.getPosition().z;
		if (primaryAction) {
//			getWorld().setBlock((int) x, (int) y - 1, (int) z, server.getBlockId("VoxelMod:debug"), false);
			for (byte i = 0; i < 2; i++) {
				getWorld().setBlock((int) x + rand.nextInt(8) - 4,
									(int) y + rand.nextInt(8) - 4,
									(int) z + rand.nextInt(8) - 4, server.getBlockId("VoxelMod:debug"), false);
			}
		}
		if (secondaryAction) {
			for (int a = -1; a <= 1; a++) {
				for (int b = -1; b <= 1; b++) {
					getWorld().spawnEntity(server.getEntityType("VoxelMod:test"), x + a * 1.5f, y - 1, z + b * 1.5f, false);
				}
			}
		}
	}

	public World getWorld() {
		return controlledEntity.getWorld();
	}

	//TEMP
	Side moveDir;

	void setControlledEntity(Entity entity) {
		controlledEntity = entity;
	}

	public void clientTickInput() {
		moveForward = window.isKeyPressed(GLFW_KEY_W);
		if (moveForward) {
			cameraInc.z = -1;
			moveDir = Side.FORWARD;
		} else {
			moveBackward = window.isKeyPressed(GLFW_KEY_S);
			if (moveBackward) {
				cameraInc.z = 1;
				moveDir = Side.BACKWARD;
			}
		}
		moveLeft = window.isKeyPressed(GLFW_KEY_A);
		if (moveLeft) {
			cameraInc.x = -1;
			moveDir = Side.LEFT;
		} else {
			moveRight = window.isKeyPressed(GLFW_KEY_D);
			if (moveRight) {
				cameraInc.x = 1;
				moveDir = Side.RIGHT;
			}
		}

		moveDown = window.isKeyPressed(GLFW_KEY_LEFT_SHIFT);
		if (moveDown) {
			cameraInc.y = -1;
			moveDir = Side.UP;
		} else {
			moveUp = window.isKeyPressed(GLFW_KEY_SPACE);
			if (moveUp) {
				moveDir = Side.DOWN;
			}
		}
		mouseInput.input(window);

		primaryAction = window.isKeyPressed(GLFW_KEY_E);
		secondaryAction = window.isKeyPressed(GLFW_KEY_Q);

		if (window.isKeyPressed(GLFW_KEY_ESCAPE)) {
			running = false;
			server.stop();
		}

		// Update camera based on mouse
		if (mouseInput.isRightButtonPressed()) {
			Vector2f rotVec = mouseInput.getDisplVec();
			camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
		}
	}

	public void render() {
		if (pendingChunkRenderUpdate) {
			pendingChunkRenderUpdate = false;
			int xi = Utils.blockWToChunkW(controlledEntity.getLocation().x);
			int yi = Utils.blockWToChunkW(controlledEntity.getLocation().y);
			int zi = Utils.blockWToChunkW(controlledEntity.getLocation().z);

			// Find chunks that no longer need to be rendered (out of range)
			chunkRenderers.forEach((c, cr) -> {
				if (Math.abs(c.chunkX - xi) > chunkDrawDistance ||
					Math.abs(c.chunkY - yi) > chunkDrawDistance ||
					Math.abs(c.chunkZ - zi) > chunkDrawDistance) {
					chunksPendingUnrender.add(c);
				}
			});

			// Queue nearby chunks for rendering
			Utils.boxLoop(chunkDrawDistance, (x, y, z) -> {
				Chunk c = getWorld().getChunk(xi + x, yi + y, zi + z, false);
				if (c != null && !chunkRenderers.containsKey(c)) {
					chunksPendingRender.add(c);
				}
			});
		}

		while (!chunksPendingUnrender.isEmpty()) {
			Chunk c = chunksPendingUnrender.poll();
			chunkRenderers.remove(c);
		}
		chunksPendingUnrender.clear();

		while (!chunksPendingRender.isEmpty()) {
			Chunk c = chunksPendingRender.poll();
			chunkRenderers.put(c, new ChunkRenderer(c, resourceBundle));
		}
		chunksPendingRender.clear();

		// every frame, update all existing chunk renderers
		for (ChunkRenderer renderer : chunkRenderers.values()) {
			renderer.update();
		}

		// render new entites
		while (!entitiesPendingRender.isEmpty()) {
			Entity e = entitiesPendingRender.poll();
			entityRenderers.put(e, new EntityRenderer(e, resourceBundle));
		}

		// assume entities will constantly change, and will always need rerender
		for (EntityRenderer renderer : entityRenderers.values()) {
			renderer.update();
		}


		sceneComponents.clear();
		for (ChunkRenderer chunkRenderer : chunkRenderers.values()) {
			for (SceneComponent chunkSceneComponent : chunkRenderer.chunkComponents.values()) {
				sceneComponents.add(chunkSceneComponent);
			}
		}
		for (EntityRenderer entityRenderer : entityRenderers.values()) {
			for (SceneComponent entitySceneComponent : entityRenderer.components.values()) {
				sceneComponents.add(entitySceneComponent);
			}
		}
		renderer.render(window, camera, sceneComponents);
		window.update();
	}

	private void startGameLoop() {
		if (!running) {
			running = true;
			final int timeDif = 1000 / targetFps;
			long currentTime = System.currentTimeMillis();
			long lastTime = currentTime;
			int tickrateTimer = 0;
			float secondsTimer = 0;
			int deltaMillis;
			int tpsCounter = 0;
			while (running && !window.windowShouldClose()) {
				currentTime = System.currentTimeMillis();
				deltaMillis = (int) (currentTime - lastTime);
				tickrateTimer += deltaMillis;
				secondsTimer += deltaMillis;
				if (tickrateTimer >= timeDif) {
					clientTickInput();
					render();
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
					currentFPS = tpsCounter;
					tpsCounter = 0;
					secondsTimer = 0;
				}
				lastTime = currentTime;
			}
		}
	}

	public void cleanup() {
		renderer.cleanup();
		for (ChunkRenderer c : chunkRenderers.values()) {
			c.cleanup();
		}
		for (EntityRenderer e : entityRenderers.values()) {
			e.cleanup();
		}
	}

	public static void main(String[] args) {
		Client client = new Client();
		client.start();
	}

	@Override
	public void entityKilled(Entity entity) {

	}

	public void entityCreated(Entity entity) {
		entitiesPendingRender.add(entity);
	}

	public Entity getControlledEntity() {
		return controlledEntity;
	}

	private boolean pendingChunkRenderUpdate;

	@Override
	public void entityChangedLocation(Entity entity) {
		// Update chunk renderers when player entity moves
		if (entity == controlledEntity) {
			pendingChunkRenderUpdate = true;
		}
	}

	public byte getChunkGenDistance() {
		return chunkGenDistance;
	}
}