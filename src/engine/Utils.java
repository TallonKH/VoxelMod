package engine;

import main.*;
import org.joml.*;
import resources.*;
import tStringManager.*;

import java.io.*;
import java.lang.Math;
import java.util.*;

public class Utils {
	public static String loadResource(String fileName) throws Exception {
		String result;
		try (InputStream in = ResourceMarker.class.getResourceAsStream(fileName); Scanner scanner = new Scanner(in, "UTF-8")) {
			result = scanner.useDelimiter("\\A").next();
		}
		return result;
	}

	@FunctionalInterface
	public interface IntCoordConsumer {
		void consume(int x, int y, int z);
	}

	public static int blockWToChunkW(float n) {
		return (int) Math.floor(n / Chunk.CHUNK_SIZE);
	}

	public static void boxLoop(int radius, IntCoordConsumer consumer) {
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					consumer.consume(x, y, z);
				}
			}
		}
	}

	public static Vector3f parseVector3f(String str) {
		List<String> nums = TFiles.parseList(str);
		return new Vector3f(Float.parseFloat(nums.get(0)),
							Float.parseFloat(nums.get(1)),
							Float.parseFloat(nums.get(2)));
	}

	public static String vectorToTFile(Vector3f vec) {
		return TFiles.composeList(vec.x, vec.y, vec.z);
	}

	public static Map<String, String> readConfig(String file) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			StringBuilder configBuilder = new StringBuilder();
			Iterator<String> readerator = reader.lines().iterator();
			while (readerator.hasNext()) {
				configBuilder.append(readerator.next());
			}
			return TFiles.parseMap(configBuilder.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Vector3f getForwardVector(Vector3f rotation) {
		float pitch = (float) (rotation.y * 0.0087266462);
		float yaw = (float) (rotation.x * 0.0087266462);
		float cosPitch = (float) Math.cos(pitch);
		return new Vector3f((float) Math.cos(yaw) * cosPitch,
							(float) Math.sin(yaw) * cosPitch,
							(float) Math.sin(pitch));
	}
}