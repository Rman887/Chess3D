package com.rman.engine.graphics;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.rman.engine.Log;

public class Mesh {
	
	private ArrayList<Vector3f> vertices;
	private ArrayList<Vector2f> texCoords;
	private ArrayList<Vector3f> normals;
	private ArrayList<Integer> indices;
	
	private int vertexBufferID;
	private int texCoordBufferID;
	private int normalBufferID;
	private int elementBufferID;
	
	private Matrix4f modelMatrix;
	private Vector3f position;
	private Vector3f rotation;
	private Vector3f scale;
	
	private Vector3f left;
	private Vector3f right;
	private Vector3f top;
	private Vector3f bottom;
	private Vector3f nearest;
	private Vector3f farthest;
	
	public Mesh() {
		vertices = new ArrayList<Vector3f>();
		texCoords = new ArrayList<Vector2f>();
		normals = new ArrayList<Vector3f>();
		indices = new ArrayList<Integer>();
		
		position = new Vector3f(0.0f, 0.0f, 0.0f);
		rotation = new Vector3f(0.0f, 0.0f, 0.0f);
		scale = new Vector3f(1.0f, 1.0f, 1.0f);
		updateModelMatrix();
		
		left = new Vector3f(0.0f, 0.0f, 0.0f);
		right = new Vector3f(0.0f, 0.0f, 0.0f);
		top = new Vector3f(0.0f, 0.0f, 0.0f);
		bottom = new Vector3f(0.0f, 0.0f, 0.0f);
		nearest = new Vector3f(0.0f, 0.0f, 0.0f);
		farthest = new Vector3f(0.0f, 0.0f, 0.0f);
	}
	
	public void bindVertexBuffer() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferID);
	}
	
	public void bindTexCoordBuffer() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texCoordBufferID);
	}
	
	public void bindNormalBuffer() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalBufferID);
	}
	
	public void draw() {
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBufferID);
		GL11.glDrawElements(GL11.GL_TRIANGLES, indices.size(), GL11.GL_UNSIGNED_INT, 0L);
	}
	
	public void destroy() {
		GL15.glDeleteBuffers(vertexBufferID);
		GL15.glDeleteBuffers(texCoordBufferID);
		GL15.glDeleteBuffers(normalBufferID);
		GL15.glDeleteBuffers(elementBufferID);
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public float getX() {
		return position.getX();
	}
	
	public float getY() {
		return position.getY();
	}
	
	public float getZ() {
		return position.getZ();
	}
	
	public void setPosition(Vector3f newPosition) {
		position = newPosition;
	}
	
	public void translate(float x, float y, float z) {
		position.translate(x, y, z);
	}
	
	public Vector3f getRotation() {
		return rotation;
	}
	
	public void setRotation(Vector3f newRotation) {
		rotation = newRotation;
	}
	
	public void rotate(float x, float y, float z) {
		rotation.translate(x, y, z);
	}
	
	public Vector3f getScale() {
		return scale;
	}
	
	public void setScale(Vector3f newScale) {
		scale = newScale;
	}
	
	public void scale(float x, float y, float z) {
		scale.translate(x, y, z);
	}
	
	public float getWidth() {
		return Math.abs(left.getX() - right.getX());
	}
	
	public float getHeight() {
		return Math.abs(top.getY() - bottom.getY());
	}
	
	public float getDepth() {
		return Math.abs(nearest.getZ() - farthest.getZ());
	}
	
	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}
	
	public void updateModelMatrix() {
		modelMatrix = new Matrix4f();
		Matrix4f.translate(position, modelMatrix, modelMatrix);
		Matrix4f.rotate((float) (rotation.getX() * Math.PI / 180.0), new Vector3f(1.0f, 0.0f, 0.0f), modelMatrix, modelMatrix);
		Matrix4f.rotate((float) (rotation.getY() * Math.PI / 180.0), new Vector3f(0.0f, 1.0f, 0.0f), modelMatrix, modelMatrix);
		Matrix4f.rotate((float) (rotation.getZ() * Math.PI / 180.0), new Vector3f(0.0f, 0.0f, 1.0f), modelMatrix, modelMatrix);
		Matrix4f.scale(scale, modelMatrix, modelMatrix);
	}
	
	private void generateVBOs() {
		FloatBuffer vertexData = BufferUtils.createFloatBuffer(vertices.size() * 3);
		for (Vector3f vertex : vertices)
			vertexData.put(new float[] {vertex.getX(), vertex.getY(), vertex.getZ()});
		vertexData.flip();
		vertexBufferID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_STATIC_DRAW);
		
		FloatBuffer texCoordData = BufferUtils.createFloatBuffer(texCoords.size() * 2);
		for (Vector2f texCoord : texCoords)
			texCoordData.put(new float[] {texCoord.getX(), texCoord.getY()});
		texCoordData.flip();
		texCoordBufferID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texCoordBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texCoordData, GL15.GL_STATIC_DRAW);
		
		FloatBuffer normalData = BufferUtils.createFloatBuffer(normals.size() * 3);
		for (Vector3f normal : normals)
			normalData.put(new float[] {normal.getX(), normal.getY(), normal.getZ()});
		normalData.flip();
		normalBufferID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normalData, GL15.GL_STATIC_DRAW);
		
		IntBuffer indicesData = BufferUtils.createIntBuffer(indices.size());
		for (Integer index : indices)
			indicesData.put(index);
		indicesData.flip();
		elementBufferID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBufferID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesData, GL15.GL_STATIC_DRAW);
	}
	
	public static Mesh createMesh(Iterable<Vector3f> vertices, Iterable<Vector2f> texCoords, Iterable<Vector3f> normals) {
		// Index vertices and store them in a mesh object
		Mesh mesh = new Mesh();
		
		TreeMap<PackedVertex, Integer> verticesSoFar = new TreeMap<PackedVertex, Integer>();
		Iterator<Vector3f> unIndexedVerticesItr = vertices.iterator();
		Iterator<Vector2f> unIndexedTexCoordsItr = texCoords.iterator();
		Iterator<Vector3f> unIndexedNormalsItr = normals.iterator();
		
		while (unIndexedVerticesItr.hasNext()) {
			Vector3f vertex = unIndexedVerticesItr.next();
			Vector2f texCoord = unIndexedTexCoordsItr.next();
			Vector3f normal = unIndexedNormalsItr.next();
			
			PackedVertex packedVertex = new PackedVertex(vertex, texCoord, normal);
			Integer index = verticesSoFar.get(packedVertex);
			if (index == null) {
				if (vertex.getX() < mesh.left.getX())
					mesh.left = new Vector3f(vertex);
				if (vertex.getX() > mesh.right.getX())
					mesh.right = new Vector3f(vertex);
				if (vertex.getY() < mesh.bottom.getY())
					mesh.bottom = new Vector3f(vertex);
				if (vertex.getY() > mesh.top.getY())
					mesh.top = new Vector3f(vertex);
				if (vertex.getZ() < mesh.farthest.getZ())
					mesh.farthest = new Vector3f(vertex);
				if (vertex.getZ() > mesh.nearest.getZ())
					mesh.nearest = new Vector3f(vertex);
				
				mesh.vertices.add(vertex);
				mesh.texCoords.add(texCoord);
				mesh.normals.add(normal);
				mesh.indices.add(mesh.vertices.size() - 1);
				verticesSoFar.put(packedVertex, mesh.vertices.size() - 1);
			} else {
				mesh.indices.add(index);
			}
		}
		
		mesh.generateVBOs();
		return mesh;
	}
	
	public static Mesh loadOBJ(URL fileLoc) {
		LinkedList<Integer> vertexIndices = new LinkedList<Integer>();
		LinkedList<Integer> texCoordIndices = new LinkedList<Integer>();
		LinkedList<Integer> normalIndices = new LinkedList<Integer>();
		
		ArrayList<Vector3f> tempVertices = new ArrayList<Vector3f>();
		ArrayList<Vector2f> tempTexCoords = new ArrayList<Vector2f>();
		ArrayList<Vector3f> tempNormals = new ArrayList<Vector3f>();
		
		try {
			// Parse and store OBJ file
			BufferedReader input = new BufferedReader(new InputStreamReader(fileLoc.openStream()));
			String line = "";
			
			while ((line = input.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line);
				String start = st.nextToken();
				
				if (start.equals("v")) {
					tempVertices.add(new Vector3f(Float.parseFloat(st.nextToken()), Float.parseFloat(st.nextToken()), Float.parseFloat(st.nextToken())));
				} else if (start.equals("vt")) {
					tempTexCoords.add(new Vector2f(Float.parseFloat(st.nextToken()), Float.parseFloat(st.nextToken())));
				} else if (start.equals("vn")) {
					tempNormals.add(new Vector3f(Float.parseFloat(st.nextToken()), Float.parseFloat(st.nextToken()), Float.parseFloat(st.nextToken())));
				} else if (start.equals("f")) {
					while (st.hasMoreTokens()) {
						StringTokenizer faceTokenizer = new StringTokenizer(st.nextToken(), "/");
						vertexIndices.add(Integer.parseInt(faceTokenizer.nextToken()));
						
						if (faceTokenizer.hasMoreTokens()) {
							String texCoord = faceTokenizer.nextToken();
							if (!texCoord.isEmpty())
								texCoordIndices.add(Integer.parseInt(texCoord));
						}
						if (faceTokenizer.hasMoreTokens()) {
							String normal = faceTokenizer.nextToken();
							if (!normal.isEmpty())
								normalIndices.add(Integer.parseInt(normal));
						}
					}
				}
			}
			input.close();
		} catch (Exception e) {
			Log.logError(String.format("Error reading OBJ file %s: ", fileLoc), e);
			return null;
		}
		
		// Add vertices in proper order
		LinkedList<Vector3f> unIndexedVertices = new LinkedList<Vector3f>();
		LinkedList<Vector2f> unIndexedTexCoords = new LinkedList<Vector2f>();
		LinkedList<Vector3f> unIndexedNormals = new LinkedList<Vector3f>();
		
		Iterator<Integer> vertIndicesItr = vertexIndices.iterator();
		Iterator<Integer> texIndicesItr = texCoordIndices.iterator();
		Iterator<Integer> normIndicesItr = normalIndices.iterator();
		
		while (vertIndicesItr.hasNext()) {
			int vertexIndex = vertIndicesItr.next();
			int texCoordIndex = texIndicesItr.next();
			int normalIndex = normIndicesItr.next();
			
			unIndexedVertices.add(tempVertices.get(vertexIndex - 1));
			unIndexedTexCoords.add(tempTexCoords.get(texCoordIndex - 1));
			unIndexedNormals.add(tempNormals.get(normalIndex - 1));
		}
		
		return createMesh(unIndexedVertices, unIndexedTexCoords, unIndexedNormals);
	}
	
	private static class PackedVertex implements Comparable<PackedVertex> {
		Vector3f position;
		Vector2f texCoord;
		Vector3f normal;
		
		public PackedVertex(Vector3f position, Vector2f texCoord, Vector3f normal) {
			this.position = position;
			this.texCoord = texCoord;
			this.normal = normal;
		}
		
		public int compareTo(PackedVertex other) {
			int positionCompare = compareVector3f(this.position, other.position);
			if (positionCompare == 0) {
				int texCoordCompare = compareVector2f(this.texCoord, other.texCoord);
				if (texCoordCompare == 0) {
					int normalCompare = compareVector3f(this.normal, other.normal);
					return normalCompare;
				} else {
					return texCoordCompare;
				}
			} else {
				return positionCompare;
			}
		}
		
		private int compareVector2f(Vector2f v1, Vector2f v2) {
			if (v1.getX() < v2.getX()) return -1;
			if (v1.getX() > v2.getX()) return 1;
			if (v1.getY() < v2.getY()) return -1;
			if (v1.getY() > v2.getY()) return 1;
			return 0;
		}
		
		private int compareVector3f(Vector3f v1, Vector3f v2) {
			if (v1.getX() < v2.getX()) return -1;
			if (v1.getX() > v2.getX()) return 1;
			if (v1.getY() < v2.getY()) return -1;
			if (v1.getY() > v2.getY()) return 1;
			if (v1.getZ() < v2.getZ()) return -1;
			if (v1.getZ() > v2.getZ()) return 1;
			return 0;
		}
	}
}
