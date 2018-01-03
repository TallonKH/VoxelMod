package engine;

import main.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {
	private final int vaoId;
	private final List<Integer> vboIdList;
	private final int vertexCount;

	private final int texture;
	private final float brightness;
	private final int emissive;

	public Mesh(MeshData data){
		this(data.getVertices(), data.getUVs(), data.getTris(), data.getTexture(), data.getEmissive(), data.getBrightness());
	}

	public Mesh(List<Float> pos, List<Float> tex, List<Integer> ind, int texture, float brightness) {
		this(pos, tex, ind, texture, Texture.blackTexture, brightness);
	}

	public Mesh(List<Float> pos, List<Float> tex, List<Integer> ind, int texture, int emissive, float brightness) {
		float[] positions = new float[pos.size()];
		for (int i = 0; i < pos.size(); i++) {
			positions[i] = pos.get(i);
		}
		float[] texCoords = new float[tex.size()];
		for (int i = 0; i < tex.size(); i++) {
			texCoords[i] = tex.get(i);
		}
		int[] indices = new int[ind.size()];
		for (int i = 0; i < ind.size(); i++) {
			indices[i] = ind.get(i);
		}

		FloatBuffer posBuffer = null;
		FloatBuffer textCoordsBuffer = null;
		IntBuffer indicesBuffer = null;

		try {
			this.brightness = brightness;
			this.texture = texture;
			this.emissive = emissive;
			vertexCount = indices.length;
			vboIdList = new ArrayList<>();
			// TODO error is in this line
			vaoId = glGenVertexArrays();
			glBindVertexArray(vaoId);
			int vboId = glGenBuffers();	// Position VBO
			vboIdList.add(vboId);

			posBuffer = MemoryUtil.memAllocFloat(positions.length);
			posBuffer.put(positions).flip();
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

			// Texture coordinates VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			textCoordsBuffer = MemoryUtil.memAllocFloat(texCoords.length);
			posBuffer = MemoryUtil.memAllocFloat(texCoords.length);

			textCoordsBuffer.put(texCoords).flip();
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

			// Index VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			indicesBuffer = MemoryUtil.memAllocInt(indices.length);
			posBuffer = MemoryUtil.memAllocFloat(texCoords.length);

			indicesBuffer.put(indices).flip();
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindVertexArray(0);
		} finally {
			if (posBuffer != null) {
				MemoryUtil.memFree(posBuffer);
			}
			if (textCoordsBuffer != null) {
				MemoryUtil.memFree(textCoordsBuffer);
			}
			if (indicesBuffer != null) {
				MemoryUtil.memFree(indicesBuffer);
			}
		}
	}

	public Mesh(float[] positions, float[] texCoords, int[] indices, int texture, float brightness) {
		this(positions, texCoords, indices, texture, Texture.blackTexture, brightness);
	}

	public Mesh(float[] positions, float[] texCoords, int[] indices, int texture, int emissive, float brightness) {
		FloatBuffer posBuffer = null;
		FloatBuffer textCoordsBuffer = null;
		IntBuffer indicesBuffer = null;
		try {
			this.brightness = brightness;
			this.texture = texture;
			this.emissive = emissive;
			vertexCount = indices.length;
			vboIdList = new ArrayList<>();

			vaoId = glGenVertexArrays();
			glBindVertexArray(vaoId);

			// Position VBO
			int vboId = glGenBuffers();
			vboIdList.add(vboId);
			posBuffer = MemoryUtil.memAllocFloat(positions.length);
			posBuffer.put(positions).flip();
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

			// Texture coordinates VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			textCoordsBuffer = MemoryUtil.memAllocFloat(texCoords.length);
			textCoordsBuffer.put(texCoords).flip();
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

			// Index VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			indicesBuffer = MemoryUtil.memAllocInt(indices.length);
			indicesBuffer.put(indices).flip();
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindVertexArray(0);
		} finally {
			if (posBuffer != null) {
				MemoryUtil.memFree(posBuffer);
			}
			if (textCoordsBuffer != null) {
				MemoryUtil.memFree(textCoordsBuffer);
			}
			if (indicesBuffer != null) {
				MemoryUtil.memFree(indicesBuffer);
			}
		}
	}

	public int getVaoId() {
		return vaoId;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public void render(ShaderProgram shaderProgram) {
		glUniform1f(0, 0);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture);
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, emissive);

		glEnable(GL_DEPTH_TEST);

		shaderProgram.setUniform("brightness", brightness);

//		glEnable(GL_MULTISAMPLE);
//		glfwWindowHint(GLFW_SAMPLES, 4);
//		glPolygonMode( GL_FRONT, GL_LINE );
		// Draw the mesh
		glBindVertexArray(getVaoId());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

		// Restore state
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
	}

	public void cleanUp() {
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);

		// Delete the VBOs
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		for (int vboId : vboIdList) {
			glDeleteBuffers(vboId);
		}

		// Delete the texture
		glDeleteTextures(texture);
		glDeleteTextures(emissive);

		// Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
	}
}