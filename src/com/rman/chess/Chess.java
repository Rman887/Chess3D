package com.rman.chess;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.rman.engine.Camera;
import com.rman.engine.GameWindow;

public class Chess extends GameWindow {
	private static final Vector3f WHITE_CAMERA_POS = new Vector3f(4.34f, 7.99f, 10.4f);
	private static final Vector3f WHITE_CAMERA_ROT = new Vector3f(54.55f, 0.0f, 0.0f);
	private static final Vector3f BLACK_CAMERA_POS = new Vector3f(3.68f, 7.99f, -2.4f);
	private static final Vector3f BLACK_CAMERA_ROT = new Vector3f(305.45f, 180.1f, 0.0f);
	
	private boolean running;
	
	private ChessBoard board;
	
	private Camera camera;
	
	public Chess() {
		super(60, "Chess");
		init();
		
		// Main Loop
		running = true;
		while (running) {
			clear();
			
			update();
			render();
			
			updateWindow();
			if (isClosing())
				running = false;
		}
		
		destroy();
		destroyWindow();
		System.exit(0);
	}
	
	/**
	 * <pre>private void init()<pre>
	 * 
	 * <p> A method that contains all code that initializes the application. </p>
	 */
	private void init() {
		board = new ChessBoard(this);
		
		camera = new Camera(WHITE_CAMERA_POS, WHITE_CAMERA_ROT, 60, (float) getWidth() / (float) getHeight(), 0.1f, 100.0f);
		//setMouseGrabbed(true);
	}
	
	/**
	 * <pre>private void update()</pre>
	 * 
	 * <p> A method that contains all code that updates the state of the application. </p>
	 */
	private void update() {
		/*
		camera.resetProjectionMatrix(60, (float) getWidth() / (float) getHeight(), 0.1f, 100.0f);
		
		final float camSpeed = 0.02f;
		final float mouseSpeed = 0.05f;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
			camera.moveForward(camSpeed);
		if (Keyboard.isKeyDown(Keyboard.KEY_S))
			camera.moveBackward(camSpeed);
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
			camera.moveLeft(camSpeed);
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			camera.moveRight(camSpeed);
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			camera.moveUp(camSpeed);
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			camera.moveDown(camSpeed);
		if (Keyboard.isKeyDown(Keyboard.KEY_P))
			camera = new Camera(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), 60.0f, (float) getWidth() / (float) getHeight(), 0.1f, 100.0f);
		
		if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
			setMouseGrabbed(!isMouseGrabbed());
		}
		if (isMouseGrabbed()){
			int mx = Mouse.getX();
			int my = Mouse.getY();
			Mouse.setCursorPosition(getWidth() / 2, getHeight() / 2);
			camera.rotateY(mouseSpeed * (mx - getWidth() / 2));
			camera.rotateX(-mouseSpeed * (my - getHeight() / 2));
		}
		
		camera.update();
		
		System.out.println(camera.getPosition() + " " + camera.getRotation());*/
		
		//System.out.println(calcRayFromMouse(Mouse.getX(), Mouse.getY(), camera.getProjectionMatrix(), camera.getViewMatrix()));
		//System.out.println(Mouse.getX() + " " + Mouse.getY());
		
		board.update();
		
		while (Mouse.next()) {
			int button = Mouse.getEventButton();
			if (button == 0 && !Mouse.getEventButtonState()) {
				Vector3f ray = calcRayFromMouse(Mouse.getX(), Mouse.getY(), camera.getProjectionMatrix(), camera.getViewMatrix());
				Vector3f intersection = intersectWithBoard(camera.getPosition(), ray, new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(2.0f, 0.0f, 2.0f));
				if (intersection != null)
					board.updatePress((int) intersection.getZ(), (int) intersection.getX());
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			running = false;
	}
	
	/**
	 * <pre>public void flipCamera(boolean whiteDir)</pre>
	 * 
	 * <p> Flips the camera to a specified side of the board. </p>
	 * 
	 * @param whiteDir - Whether the camera should flip to the white side
	 */
	public void flipCamera(boolean whiteDir) {
		if (whiteDir) {
			camera.setPosition(WHITE_CAMERA_POS);
			camera.setRotation(WHITE_CAMERA_ROT);
			camera.update();
		} else {
			camera.setPosition(BLACK_CAMERA_POS);
			camera.setRotation(BLACK_CAMERA_ROT);
			camera.update();
		}
	}
	
	/**
	 * <pre>private {@link Vector3f Vector3f} intersectWithBoard({@link Vector3f Vector3f} rayStart, {@link Vector3f Vector3f} rayDir, {@link Vector3f Vector3f} normal, {@link Vector3f Vector3f} p)</pre>
	 * 
	 * <p> Intersects a ray thas has an origin <code>rayStart</code> and a direction <code>rayDir</code> with a plane that
	 * has a normal vector <code>normal</code> and contains the point <code>p</code>. </p>
	 * 
	 * @param rayStart - The ray's origin
	 * @param rayDir - The ray's direction
	 * @param normal - The plane's normal vector
	 * @param p - A point that the plane contains
	 * 
	 * @return The intersection point between the ray and the plane. If the plane and ray don't intersect, then null is returned.
	 */
	private Vector3f intersectWithBoard(Vector3f rayStart, Vector3f rayDir, Vector3f normal, Vector3f p) {
		float nd = Vector3f.dot(normal, rayDir);
		if (nd == 0)
			return null;
		
		Vector3f qSubE = new Vector3f();
		Vector3f.sub(p, rayStart, qSubE);
		
		float t = Vector3f.dot(normal, qSubE) / nd;
		if (t < 0)
			return null;
		
		Vector3f intersection = new Vector3f(rayDir);
		intersection.scale(t);
		Vector3f.add(rayStart, intersection, intersection);
		
		return intersection;
	}
	
	/**
	 * <pre>private {@link Vector3f Vector3f} calcRayFromMouse(int mouseX, int mouseY, {@link Matrix4f Matrix4f} projectionMatrix, {@link Matrix4f Matrix4f} viewMatrix)</pre>
	 * 
	 * <p> Constructs a ray that starts from the camera position and goes through the point which was clicked by the mouse. </p>
	 * 
	 * @param mouseX - The x-coordinate of the mouse press
	 * @param mouseY - The y-coordinate of the mouse press
	 * @param projectionMatrix - The camera's projection matrix
	 * @param viewMatrix - The camera's view matrix
	 * 
	 * @return A ray that originates from the camera and extends through the point which was clicked.
	 */
	private Vector3f calcRayFromMouse(int mouseX, int mouseY, Matrix4f projectionMatrix, Matrix4f viewMatrix) {
		Matrix4f inverseProjectionMatrix = new Matrix4f();
		Matrix4f.invert(projectionMatrix, inverseProjectionMatrix);
		Matrix4f inverseViewMatrix = new Matrix4f();
		Matrix4f.invert(viewMatrix, inverseViewMatrix);
		
		Vector4f rayClip = new Vector4f((2.0f * mouseX) / (float) getWidth() - 1.0f, (2.0f * mouseY) / (float) getHeight() - 1.0f, -1.0f, 1.0f);
		Vector4f rayEye = matVecMult(inverseProjectionMatrix, rayClip);
		rayEye = new Vector4f(rayEye.getX(), rayEye.getY(), -1.0f, 0.0f);
		Vector4f rayWorld = matVecMult(inverseViewMatrix, rayEye);
		if (rayWorld.lengthSquared() != 0)
			rayWorld.normalise();
		return new Vector3f(rayWorld.getX(), rayWorld.getY(), rayWorld.getZ());
	}
	
	/**
	 * <pre>private {@link Vector4f Vector4f} matVecMult({@link Matrix4f Matrix4f} mat, {@link Vector4f Vector4f} vec)</pre>
	 * 
	 * <p> Multiplies a 4x4 matrix with a 4-dimensional vector. </p>
	 * 
	 * @param mat - The 4x4 matrix
	 * @param vec - The 4-dimensional vector
	 * 
	 * @return The product of the matrix and vector.
	 */
	private Vector4f matVecMult(Matrix4f mat, Vector4f vec) {
		return new Vector4f(mat.m00 * vec.getX() + mat.m10 * vec.getY() + mat.m20 * vec.getZ() + mat.m30 * vec.getW(),
							mat.m01 * vec.getX() + mat.m11 * vec.getY() + mat.m21 * vec.getZ() + mat.m31 * vec.getW(),
							mat.m02 * vec.getX() + mat.m12 * vec.getY() + mat.m22 * vec.getZ() + mat.m32 * vec.getW(),
							mat.m03 * vec.getX() + mat.m13 * vec.getY() + mat.m23 * vec.getZ() + mat.m33 * vec.getW());
	}
	
	/**
	 * <pre>private void render()</pre>
	 * 
	 * <p> A method that contains all the rendering code for this application. </p>
	 */
	private void render() {
		Matrix4f projectionMatrix = camera.getProjectionMatrix();
		Matrix4f viewMatrix = camera.getViewMatrix();
		
		board.render(projectionMatrix, viewMatrix);
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3f(0, 0, 0);
		GL11.glVertex3f(1, 0, 0);
		GL11.glVertex3f(0, 0, 1);
		GL11.glVertex3f(1, 0, 1);
		GL11.glEnd();
	}
	
	/**
	 * <pre>private void destroy()</pre>
	 * 
	 * <p> A method that contains all clean-up operations that need to be done as the application closes. </p>
	 */
	private void destroy() {
		board.destroy();
	}
	
	/**
	 * <p> Starts the application by constructing a new <code>Chess</code> object. </p>
	 * 
	 * @param args - No command-line arguments
	 */
	public static void main(String[] args) {
		new Chess();
	}
}
