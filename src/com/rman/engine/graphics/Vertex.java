package com.rman.engine.graphics;

import java.util.Arrays;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Vertex {
	public static final int ELEMENT_BYTE_COUNT = 4;
	 
	public static final int POSITION_ELEMENT_COUNT = 3;
	public static final int NORMALS_ELEMENT_COUNT = 3;
	public static final int TEXTURE_ELEMENT_COUNT = 2;
	 
	public static final int POSITION_BYTE_COUNT = POSITION_ELEMENT_COUNT * ELEMENT_BYTE_COUNT;
	public static final int NORMALS_BYTE_COUNT = NORMALS_ELEMENT_COUNT * ELEMENT_BYTE_COUNT;
	public static final int TEXTURE_BYTE_COUNT = TEXTURE_ELEMENT_COUNT * ELEMENT_BYTE_COUNT;
	
	public static final int POSITION_BYTE_OFFSET = 0;
	public static final int NORMALS_BYTE_OFFSET = POSITION_BYTE_OFFSET + POSITION_BYTE_COUNT;
	public static final int TEXTURE_BYTE_OFFSET = NORMALS_BYTE_OFFSET + NORMALS_BYTE_COUNT;
	
	private Vector3f position;
	private Vector3f normals;
	private Vector2f texCoords;
	
	public Vertex(Vector3f position) {
		this(position, new Vector3f(0.0f, 0.0f, 0.0f), new Vector2f(0.0f, 0.0f));
	}
	
	public Vertex(Vector3f position, Vector3f normals) {
		this(position, normals, new Vector2f(0.0f, 0.0f));
	}
	
	public Vertex(Vector3f position, Vector2f texCoords) {
		this(position, new Vector3f(0.0f, 0.0f, 0.0f), texCoords);
	}
	
	public Vertex(Vector3f position, Vector3f normals, Vector2f texCoords) {
		this.position = position;
		this.normals = normals;
		this.texCoords = texCoords;
	}
	
	public float[] getElements() {
		return new float[] {
			position.x, position.y, position.z,
			normals.x, normals.y, normals.z,
			texCoords.x, texCoords.y
		};
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Vector3f getNormals() {
		return normals;
	}
	
	public Vector2f getTexCoords() {
		return texCoords;
	}
	
	public void setPosition(Vector3f newPosition) {
		position = newPosition;
	}
	
	public void setNormals(Vector3f newNormals) {
		normals = newNormals;
	}
	
	public void setTexCoords(Vector2f newTexCoords) {
		texCoords = newTexCoords;
	}
	
	public boolean equals(Object other) {
		if (other instanceof Vertex) {
			Vertex v = (Vertex) other;
			return Arrays.equals(this.getElements(), v.getElements());
		}
		return false;
	}
}
