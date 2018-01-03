package engine;

import data.*;
import org.joml.Math;
import org.joml.*;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

	/**
	 * Field of View in Radians
	 */
	private static final float FOV = (float) Math.toRadians(60.0f);

	private static final float Z_NEAR = 0.01f;

	private static final float Z_FAR = 1000.f;

	private final Transformation transformation;

	private ShaderProgram shaderProgram;

	public Renderer() {
		transformation = new Transformation();
	}

	public void init(GameWindow window) throws Exception {
		// Create shader
		shaderProgram = new ShaderProgram();
		shaderProgram.createVertexShader(Utils.loadResource("shaders/vertex.vs"));
		shaderProgram.createFragmentShader(Utils.loadResource("shaders/fragment.fs"));
		shaderProgram.link();

		// Create uniforms for modelView and projection matrices and texture
		shaderProgram.createUniform("projectionMatrix");
		shaderProgram.createUniform("modelViewMatrix");
		shaderProgram.createUniform("texture_sampler");
		shaderProgram.createUniform("emissive_sampler");
		shaderProgram.createUniform("brightness");
	}

	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	public void render(GameWindow window, Camera camera, List<SceneComponent> sceneComponents) {
		clear();

		if (window.isResized()) {
			glViewport(0, 0, window.getWidth(), window.getHeight());
			window.setResized(false);
		}

		shaderProgram.bind();

		// Update projection Matrix
		Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);

		// Update view Matrix
		Matrix4f viewMatrix = transformation.getViewMatrix(camera);

		shaderProgram.setUniform("texture_sampler", 0);
		shaderProgram.setUniform("emissive_sampler", 1);

		// Render each scene component
		for (SceneComponent sceneComponent : sceneComponents) {
			// Set model view matrix for this component
			Matrix4f modelViewMatrix = transformation.getModelViewMatrix(sceneComponent, viewMatrix);
			shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
			// Render the mes for this game component
			for (Mesh mesh : sceneComponent.getMeshes()) {
				mesh.render(shaderProgram);
			}
		}

		shaderProgram.unbind();
	}

	public void cleanup() {
		if (shaderProgram != null) {
			shaderProgram.cleanup();
		}
	}
}