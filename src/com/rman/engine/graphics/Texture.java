package com.rman.engine.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.rman.engine.Log;

public class Texture {
	private static final int BPP = 4;
	
	private String name;
	
	private int textureID;
	
	private int width;
	private int height;
	
	public Texture(String name, URL loc) {
		this.name = name;
		
		ByteBuffer data = null;
		try {
			BufferedImage image = ImageIO.read(loc);
			width = image.getWidth();
			height = image.getHeight();
			data = loadImage(image);
		} catch (Exception e) {
			Log.logError(String.format("Error loading texture %s: ", name), e);
		}
		if (data == null)
			Log.logError(String.format("Error creating texture %s: texture data is null", name));
		
		initOpenGL(data);
	}
	
	/**
	 * Don't use this (it doesn't do anything)
	 */
	public Texture() {
		
	}
	
	private void initOpenGL(ByteBuffer data) {
		textureID = GL11.glGenTextures();
		bind();
		
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		
		bind();
	}
	
	public void bind() {
		bind(0);
	}
	
	public void bind(int textureUnitNum) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + textureUnitNum);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}
	
	public void destroy() {
		GL11.glDeleteTextures(textureID);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String newName) {
		name = newName;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public static ByteBuffer loadImage(BufferedImage image) throws IOException {
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BPP);
	        
		for (int y = 0; y < image.getHeight(); y++) {
		    for (int x = 0; x < image.getWidth(); x++) {
		        int pixel = pixels[y * image.getWidth() + x];
		        buffer.put((byte) ((pixel >> 16) & 0xFF));
		        buffer.put((byte) ((pixel >> 8) & 0xFF));
		        buffer.put((byte) (pixel & 0xFF));
		        buffer.put((byte) ((pixel >> 24) & 0xFF));
		    }
		}
		
		buffer.flip();
		return buffer;
	}
	
	public static Texture[][] loadSpriteSheet(String name, URL loc, int numRows, int numColumns) {
		BufferedImage image = null;
		Texture[][] spriteSheet = null;
		try {
			image = ImageIO.read(loc);
			
			if (image == null)
				Log.logError(String.format("Error loading texture %s: Image is null", name));
			if (numRows <= 0 || numRows > image.getHeight())
				throw new IllegalArgumentException("Number of rows is invalid");
			if (numColumns <= 0 || numColumns > image.getWidth())
				throw new IllegalArgumentException("Number of columns is invalid");
			
			int subImageWidth = image.getWidth() / numColumns;
			int subImageHeight = image.getHeight() / numRows;
			
			spriteSheet = new Texture[numRows][numColumns];
			for (int row = 0; row < numRows; row++) {
				for (int col = 0; col < numColumns; col++) {
					BufferedImage subImage = image.getSubimage(col * subImageWidth, row * subImageHeight, subImageWidth, subImageHeight);
					spriteSheet[row][col] = new Texture();
					spriteSheet[row][col].name = String.format("%s (%d %d)", name, col, row);
					spriteSheet[row][col].width = subImageWidth;
					spriteSheet[row][col].height = subImageHeight;
					spriteSheet[row][col].initOpenGL(loadImage(subImage));
				}
			}
		} catch (Exception e) {
			Log.logError(String.format("Error loading texture %s: ", name), e);
		}
		return spriteSheet;
	}
}
