package data;

import org.joml.*;
import engine.*;

import java.lang.Math;

public class Transformation {

	private final Matrix4f projectionMatrix;

	private final Matrix4f modelViewMatrix;

	private final Matrix4f viewMatrix;

	public Transformation() {
		projectionMatrix = new Matrix4f();
		modelViewMatrix = new Matrix4f();
		viewMatrix = new Matrix4f();
	}

	public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
		float aspectRatio = width / height;
		projectionMatrix.identity();
		projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
		return projectionMatrix;
	}

	public Matrix4f getViewMatrix(Camera camera) {
		Vector3f cameraPos = camera.getPosition();
		Vector3f rotation = camera.getRotation();

		viewMatrix.identity();
		// First do the rotation so camera rotates over its position
		viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
				  .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
		// Then do the translation
		viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		return viewMatrix;
	}

	public Matrix4f getModelViewMatrix(SceneComponent sceneComponent, Matrix4f viewMatrix) {
		Vector3f rotation = sceneComponent.getRotation();
		modelViewMatrix.identity().translate(sceneComponent.getPosition()).
				rotateX((float) Math.toRadians(-rotation.x)).
							   rotateY((float) Math.toRadians(-rotation.y)).
							   rotateZ((float) Math.toRadians(-rotation.z)).
							   scale(sceneComponent.getScale());
		Matrix4f viewCurr = new Matrix4f(viewMatrix);
		return viewCurr.mul(modelViewMatrix);
	}
}