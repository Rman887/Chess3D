package com.rman.engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class GameWindow {
	private static final Color BACKGROUND_COLOR = new Color(80, 80, 80);
	
	private Frame frame;
	private Canvas canvas;
	
	private String title;
	
	private boolean isVSyncEnabled;
	private boolean isResizable;
	private boolean isFullscreen;
	private boolean isMouseGrabbed;
	
	private boolean isCloseRequested;
	
	private int targetFPS;

	private int x;
	private int y;
	private int width;
	private int height;
	
	public GameWindow(int fps) {
		this(fps, (int) (Display.getDesktopDisplayMode().getWidth() * 0.75), (int) (Display
				.getDesktopDisplayMode().getHeight() * 0.75));
	}
	
	public GameWindow(int fps, int width, int height) {
		this(fps, width, height, "");
	}
	
	public GameWindow(int fps, String title) {
		this(fps, (int) (Display.getDesktopDisplayMode().getWidth() * 0.75), (int) (Display
				.getDesktopDisplayMode().getHeight() * 0.75), title);
	}
	
	public GameWindow(int fps, int x, int y, int width, int height, String title) {
		this(fps, width, height, title);
		setLocation(x, y);
	}
	
	public GameWindow(int fps, int width, int height, String title) {
		targetFPS = fps;
		
		initWindow(title, width, height);

		setResizable(true);
		setVSyncEnabled(false);
	}
	
	private void initWindow(String title, int width, int height) {
		/*frame = new Frame();
		frame.setLayout(new BorderLayout());
		canvas = new Canvas();
		canvas.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				setSize(canvas.getSize().width, canvas.getSize().height);
			}
		});
		frame.addWindowFocusListener(new WindowAdapter() {
			public void windowGainedFocus(WindowEvent e) {
				canvas.requestFocusInWindow();
			}
		});
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				isCloseRequested = true;
			}
		});
		frame.add(canvas, BorderLayout.CENTER);
		
		MenuBar menuBar = new MenuBar();
		Menu optionsMenu = new Menu("Game");
		MenuItem settingsItem = new MenuItem("Settings");
		optionsMenu.add(settingsItem);
		menuBar.add(optionsMenu);
		frame.setMenuBar(menuBar);*/
		
		try {
			Display.setParent(canvas);
			/*frame.setBackground(BACKGROUND_COLOR);
			frame.setTitle(title);
			frame.setPreferredSize(new Dimension(width, height));
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);*/
			setTitle(title);
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.create();
			initOpenGL();
		} catch (LWJGLException le) {
			Log.logError("Error initializing display", le);
		}
	}
	
	private void initOpenGL() {
		GL11.glClearColor((float) BACKGROUND_COLOR.getRed() / 255.0f, (float) BACKGROUND_COLOR.getGreen() / 255.0f, (float) BACKGROUND_COLOR.getBlue() / 255.0f, (float) BACKGROUND_COLOR.getAlpha() / 255.0f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public void clear() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	public void updateWindow() {
		Display.update();
		Display.sync(this.targetFPS);
	}
	
	public boolean isClosing() {
		if (!Display.isCreated()) return false;
		return isCloseRequested;
	}
	
	public void destroyWindow() {
		Display.destroy();
	}
	
	public Frame getFrame() {
		return frame;
	}
	
	public boolean isVSyncEnabled() {
		return this.isVSyncEnabled;
	}

	public void setVSyncEnabled(boolean enableVSync) {
		this.isVSyncEnabled = enableVSync;
		Display.setVSyncEnabled(enableVSync);
	}

	public boolean isResizable() {
		return this.isResizable;
	}

	public void setResizable(boolean enableResize) {
		this.isResizable = enableResize;
		Display.setResizable(this.isResizable);
	}
	
	public boolean wasResized() {
		return Display.wasResized();
	}

	public boolean isFullscreen() {
		return this.isFullscreen;
	}

	public void setFullscreen(boolean enableFullscreen) {
		try {
			Display.setFullscreen(enableFullscreen);
			isFullscreen = enableFullscreen;
		} catch (LWJGLException le) {
			Log.logError("Error setting window to fullscreen: ", le);
		}
	}
	
	public boolean isMouseGrabbed() {
		return isMouseGrabbed;
	}
	
	public void setMouseGrabbed(boolean mouseGrabbed) {
		Mouse.setGrabbed(mouseGrabbed);
		isMouseGrabbed = mouseGrabbed;
	}

	public int getTargetFPS() {
		return targetFPS;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String newTitle) {
		//frame.setTitle(newTitle);
		Display.setTitle(newTitle);
		title = newTitle;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int newX) {
		setLocation(newX, y);
	}

	public void setY(int newY) {
		setLocation(x, newY);
	}

	public void setLocation(int newX, int newY) {
		x = newX;
		y = newY;
		//frame.setLocation(x, y);
		Display.setLocation(x, y);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int newWidth) {
		setSize(newWidth, height);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int newHeight) {
		setSize(height, newHeight);
	}

	public void setSize(int newWidth, int newHeight) {
		if (newWidth < 1) {
			throw new IllegalArgumentException("Width cannot be less than one.");
		}
		if (newHeight < 1) {
			throw new IllegalArgumentException("Height cannot be less than one.");
		}

		this.width = newWidth;
		this.height = newHeight;

		//canvas.setSize(newWidth, newHeight);
	}
}
