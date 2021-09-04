package com.rman.chess;

import org.lwjgl.util.vector.Vector3f;

import com.rman.chess.pieces.ChessPiece;

/**
 * Contains information about a square on the {@link ChessBoard ChessBoard}
 * 
 * @author Arman
 */
public class ChessSquare {
	/** This square's row location. */
	private int row;
	/** This square's column location. */
	private int column;
	/** The position that a piece would be if it is on this square. */
	private Vector3f piecePosition;
	
	/** Whether this square is selected by the user. */
	private boolean selected;
	/** Whether a piece can be moved to this square. */
	private boolean movable;
	/** Whether a pawn can perform en passant on this square. */
	private boolean enPassantable;
	
	/** The {@link ChessPiece ChessPiece} that is on this square (null if there's no piece). */
	private ChessPiece piece;
	
	/**
	 * <pre>public ChessSquare(int row, int column)</pre>
	 * 
	 * <p> Constructs a new square on a chess board on the given row and column. </p>
	 * 
	 * @param row - The square's row location
	 * @param column - The square's column location
	 */
	public ChessSquare(int row, int column) {
		this(row, column, null);
	}
	/**
	 * <pre>public ChessSquare(int row, int column, {@link ChessPiece ChessPiece} piece)</pre>
	 * 
	 * <p> Constructs a new square on a chess board on the given row and column.
	 * The given <code>ChessPiece</code> is put on the square. </p>
	 * 
	 * @param row - The square's row location
	 * @param column - The square's column location
	 * @param piece - The piece to put on the square
	 */
	public ChessSquare(int row, int column, ChessPiece piece) {
		this.row = row;
		this.column = column;
		
		piecePosition = new Vector3f(column + 0.5f, 0.0f, row + 0.5f);
		setPiece(piece);
	}
	
	/**
	 * <pre>public {@link ChessPiece ChessPiece} getPiece()</pre>
	 * 
	 * @return The piece that's on the square.
	 */
	public ChessPiece getPiece() {
		return piece;
	}
	
	/**
	 * <pre>public void setPiece({@link ChessPiece ChessPiece} newPiece)</pre>
	 * 
	 * <p> Replaces this square's current piece with a new one. If this square already has a piece, 
	 * its mesh is destroyed using the {@link ChessPiece#destroyMesh() destoryMesh()} method. </p>
	 * 
	 * @param newPiece - The new piece
	 */
	public void setPiece(ChessPiece newPiece) {
		piece = newPiece;
		if (piece != null && piece.getMesh() != null)
			piece.getMesh().setPosition(piecePosition);
	}
	
	/**
	 * <pre>public int getRow()</pre>
	 * 
	 * @return The row location of this square on the board.
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * <pre>public int getColumn()</pre>
	 * 
	 * @return The column location of this square on the board.
	 */
	public int getColumn() {
		return column;
	}
	
	/**
	 * <pre>public boolean isSelected()</pre>
	 * 
	 * @return Whether this square is selected by the user.
	 */
	public boolean isSelected() {
		return selected;
	}
	
	/**
	 * <pre>public void setSelected(boolean isSelected)</pre>
	 * 
	 * <p> Sets whether this square is selected or not. </p>
	 * 
	 * @param isSelected - Whether or not this square is selected
	 */
	public void setSelected(boolean isSelected) {
		selected = isSelected;
	}
	
	/**
	 * <pre>public boolean isMovable()</pre>
	 * 
	 * @return Whether or not a piece an move to this square.
	 */
	public boolean isMovable() {
		return movable;
	}
	
	/**
	 * <pre>public void setMovable(boolean isMovable)</pre>
	 * 
	 * <p> Sets whether or not a piece can move to this square. </p>
	 * 
	 * @param isMovable - Whether or not a piece can move to this square
	 */
	public void setMovable(boolean isMovable) {
		movable = isMovable;
	}
	
	/**
	 * <pre>public boolean isEnPassantable()</pre>
	 * 
	 * @return Whether or not a pawn can perform en passant on this square.
	 */
	public boolean isEnPassantable() {
		return enPassantable;
	}
	
	/**
	 * <pre>public void setEnPassantable(boolean isEnPassantable)</pre>
	 * 
	 * <p> Sets whether or not a pawn can perform en passant on this square
	 * 
	 * @param isEnPassantable - Whether or not a pawn can perform en passant on this square
	 */
	public void setEnPassantable(boolean isEnPassantable) {
		this.enPassantable = isEnPassantable;
	}
	
	/**
	 * <pre>public {@link Vector3f Vector3f} getPiecePosition()</pre>
	 * 
	 * @return The position that a piece would be if it is on this square.
	 */
	public Vector3f getPiecePosition() {
		return piecePosition;
	}
}
