package com.rman.engine;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	private Matrix4f viewMatrix;
	private Matrix4f projectionMatrix;
	
	private Vector3f position;
	private Vector3f rotation;
	
	public Camera(Vector3f position, Vector3f rotation, float fov, float aspectRatio, float zNear, float zFar) {
		setPosition(position);
		setRotation(rotation);
		resetProjectionMatrix(fov, aspectRatio, zNear, zFar);
		update();
	}
	
	public void update() {
		viewMatrix = new Matrix4f();
		Matrix4f.rotate((float) (rotation.getZ() * Math.PI / 180.0f), new Vector3f(0.0f, 0.0f, 1.0f), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) (rotation.getY() * Math.PI / 180.0f), new Vector3f(0.0f, 1.0f, 0.0f), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) (rotation.getX() * Math.PI / 180.0f), new Vector3f(1.0f, 0.0f, 0.0f), viewMatrix, viewMatrix);
		Matrix4f.translate(position, viewMatrix, viewMatrix);
	}
	
	public void resetProjectionMatrix(float fov, float aspectRatio, float zNear, float zFar) {
		float yScale = (float) (1.0 / (fov * Math.PI / 360.0));
		float xScale = yScale / aspectRatio;
		float frustumLength = zFar - zNear;
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = xScale;
		projectionMatrix.m11 = yScale;
		projectionMatrix.m22 = -((zFar + zNear) / frustumLength);
		projectionMatrix.m23 = -1.0f;
		projectionMatrix.m32 = -((2.0f * zNear * zFar) / frustumLength);
		projectionMatrix.m33 = 0.0f;
	}
	
	public Vector3f getPosition() {
		return new Vector3f(position.getX() * -1.0f, position.getY() * -1.0f, position.getZ() * -1.0f);
	}
	
	public void setPosition(Vector3f newPosition) {
		position = new Vector3f(newPosition.getX() * -1.0f, newPosition.getY() * -1.0f, newPosition.getZ() * -1.0f);
	}
	
	public Vector3f getRotation() {
		return rotation;
	}
	
	public void setRotation(Vector3f newRotation) {
		rotation = newRotation;
	}
	
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
	
	public void moveUp(float amount) {
		position.setY(position.getY() - amount);
	}
	
	public void moveDown(float amount) {
		position.setY(position.getY() + amount);
	}
	
	public void moveRight(float amount) {
		position.setX(position.getX() - amount);
	}
	
	public void moveLeft(float amount) {
		position.setX(position.getX() + amount);
	}
	
	public void moveForward(float amount) {
		position.setZ(position.getZ() + amount);
	}
	
	public void moveBackward(float amount) {
		position.setZ(position.getZ() - amount);
	}
	
	public void rotateX(float amount) {
		rotation.setX(rotation.getX() + amount);
	}
	
	public void rotateY(float amount) {
		rotation.setY(rotation.getY() + amount);
	}
	
	public void rotateZ(float amount) {
		rotation.setZ(rotation.getZ() + amount);
	}
}