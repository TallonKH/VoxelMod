package engine;

import de.matthiasmann.twl.utils.*;
import resources.*;

import java.io.*;
import java.nio.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Texture {
	public static final int missingTexture = loadTexture(ResourceMarker.class.getResourceAsStream("missing.png"), PNGDecoder.Format.RGBA);
	public static final int blackTexture = loadTexture(ResourceMarker.class.getResourceAsStream("black.png"), PNGDecoder.Format.RGBA);

	private static int loadTexture(InputStream stream, PNGDecoder.Format format){
		try {
			PNGDecoder decoder = new PNGDecoder(stream);

			// Load texture contents into a byte buffer
			ByteBuffer buf = ByteBuffer.allocateDirect(
					format.getNumComponents() * decoder.getWidth() * decoder.getHeight());
			decoder.decode(buf, decoder.getWidth() * format.getNumComponents(), format);
			buf.flip();

			// Create a new OpenGL texture
			int textureId = glGenTextures();
			// Bind the texture
			glBindTexture(GL_TEXTURE_2D, textureId);

			// Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

			//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

			int glformat = 0;
			switch(format){
				case RGB:
					glformat = GL_RGB;
					break;
				case RGBA:
					glformat = GL_RGBA;
					break;
				case ALPHA:
					glformat = GL_ALPHA;
					break;
				case LUMINANCE:
					glformat = GL_LUMINANCE;
					break;
				case LUMINANCE_ALPHA:
					glformat = GL_LUMINANCE_ALPHA;
			}
			// Upload the texture data
			glTexImage2D(GL_TEXTURE_2D, 0, glformat, decoder.getWidth(), decoder.getHeight(), 0,
						 glformat, GL_UNSIGNED_BYTE, buf);
			// Generate Mip Map
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glGenerateMipmap(GL_TEXTURE_2D);
			return textureId;
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}
	}

	public static int loadTexture(String fileName) {
		return loadTexture(fileName, PNGDecoder.Format.RGBA);
	}

	public static int loadTexture(String fileName, PNGDecoder.Format format) {
		try {
			return loadTexture(new FileInputStream(fileName), format);
		}catch(FileNotFoundException e){
			System.err.println("Resource " + fileName + " not found");
			return missingTexture;
		}
	}
}