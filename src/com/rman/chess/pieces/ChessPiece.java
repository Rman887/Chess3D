package com.rman.chess.pieces;

import com.rman.engine.graphics.Mesh;

/**
 * Represents a chess piece. This class stores a piece's name, color, and {@link Mesh Mesh} object.
 * It also stores a piece's {@link #getMoves() possible moves}.
 * 
 * @author Arman
 */
public abstract class ChessPiece {
	/** The name of this piece (e.g. King) */
	protected String name;
	/** The {@link Mesh Mesh} object representing this piece */
	protected Mesh mesh;
	
	/** Indicates whether this piece is white or not. */
	protected boolean isWhite;
	/** Indicates whether this piece has moved at least once. */
	private boolean hasMoved;
	
	/**
	 * <pre>public ChessPiece({@link String String} name, boolean isWhite)</pre>
	 * 
	 * <p> Creates a new chess piece. </p>
	 * 
	 * @param name - The name of the piece (e.g. King)
	 * @param isWhite - Whether the piece is white or not
	 */
	public ChessPiece(String name, boolean isWhite) {
		this.name = name;
		this.isWhite = isWhite;
		this.hasMoved = false;
	}
	
	/**
	 * <pre>public {@link String String} getName()</pre>
	 * 
	 * @return This piece's name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * <pre>public {@link Mesh Mesh} getMesh()</pre>
	 * 
	 * @return This piece's <code>Mesh</code> object.
	 */
	public Mesh getMesh() {
		return mesh;
	}
	
	/**
	 * <pre>public boolean isWhite()</pre>
	 * 
	 * @return If the piece is the color white.
	 */
	public boolean isWhite() {
		return isWhite;
	}
	
	/**
	 * <pre>public boolean hasMoved()</pre>
	 * 
	 * @return If this piece has moved at least once.
	 */
	public boolean hasMoved() {
		return hasMoved;
	}
	
	/**
	 * <pre>public void setHasMoved()</pre>
	 * 
	 * <p> Sets this piece as having moved at least once. </p>
	 */
	public void setHasMoved() {
		hasMoved = true;
	}
	
	/**
	 * <pre>public void destroyMesh()</pre>
	 * 
	 * <p> Destroys the piece's mesh by calling the mesh's {@link Mesh#destroy() destroy()} method. </p>
	 */
	public void destroyMesh() {
		mesh.destroy();
	}
	
	/**
	 * <pre>public abstract int[] getMoves()</pre>
	 * 
	 * @return An int array with the following format:						<br>
	 * {up-left, up, up-right, right, down-right, down, down-left, left}	<br><br>
	 * 
	 * 0 = can't move in direction											<br>
	 * 1 = one move in direction											<br>
	 * 2 = all moves in direction											<br>
	 * 3 = L-shaped move in direction (only in diagonal directions)			<br>
	 * 4 = can move but not attack, and can move two squares on first move	<br>
	 * 5 = can attack but not move											<br>
	 */
	public abstract int[] getMoves();
}
