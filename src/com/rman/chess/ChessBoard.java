package com.rman.chess;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.rman.chess.pieces.Bishop;
import com.rman.chess.pieces.ChessPiece;
import com.rman.chess.pieces.King;
import com.rman.chess.pieces.Knight;
import com.rman.chess.pieces.Pawn;
import com.rman.chess.pieces.Queen;
import com.rman.chess.pieces.Rook;
import com.rman.engine.graphics.Mesh;
import com.rman.engine.graphics.Shader;
import com.rman.engine.graphics.Texture;

public class ChessBoard {
	private static final long MOVE_SPEED = 20000000L;
	private static final long MOVE_TIME = 500000000L;
	private static final long FLIP_BOARD_TIME = 1500000000L;
	
	private Chess chessGame;
	
	private ChessSquare[] squares;
	private List<ChessPiece> takenPiecesW;
	private List<ChessPiece> takenPiecesB;
	private int selectedSquare = -1;
	private int moveNum = 1;

	private boolean whiteTurn = true;
	private boolean isFlipped = false;
	private boolean isKingInCheck = false;
	
	// Smooth piece movement
	private long moveTime = -1L;
	private Vector3f stepDist;
	private ChessPiece movingPiece;
	private int startSquare;
	private int destSquare;
	
	private int vaoID;
	private Shader standardShader;
	private Texture whiteWoodTexture;
	private Texture blackWoodTexture;
	
	private Mesh boardMesh;
	private Texture boardTexture;
	
	private Shader squareHighlightShader;
	
	private Mesh squareSelected;
	private Mesh squareMovable;
	private Mesh squareAttackable;
	private Texture squareSelectedTexture;
	private Texture squareMovableTexture;
	private Texture squareAttackableTexture;

	public ChessBoard(Chess cg) {
		chessGame = cg;
		squares = new ChessSquare[64];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				squares[i * 8 + j] = new ChessSquare(i, j);
			}
		}
		takenPiecesW = new ArrayList<ChessPiece>(16);
		takenPiecesB = new ArrayList<ChessPiece>(16);
		
		this.squares[0].setPiece(new Rook(false));
		this.squares[1].setPiece(new Knight(false));
		this.squares[2].setPiece(new Bishop(false));
		this.squares[3].setPiece(new Queen(false));
		this.squares[4].setPiece(new King(false));
		this.squares[5].setPiece(new Bishop(false));
		this.squares[6].setPiece(new Knight(false));
		this.squares[7].setPiece(new Rook(false));
		for (int i = 8; i < 16; i++)
			this.squares[i].setPiece(new Pawn(false));

		this.squares[56].setPiece(new Rook(true));
		this.squares[57].setPiece(new Knight(true));
		this.squares[58].setPiece(new Bishop(true));
		this.squares[59].setPiece(new Queen(true));
		this.squares[60].setPiece(new King(true));
		this.squares[61].setPiece(new Bishop(true));
		this.squares[62].setPiece(new Knight(true));
		this.squares[63].setPiece(new Rook(true));
		for (int i = 48; i < 56; i++)
			this.squares[i].setPiece(new Pawn(true));
		
		initRendering();
	}
	
	private void initRendering() {
		vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		
		initShaders();
		
		whiteWoodTexture = new Texture("White Wood Texture", this.getClass().getResource("res/textures/wood_white.png"));
		blackWoodTexture = new Texture("Black Wood Texture", this.getClass().getResource("res/textures/wood_black.png"));
		
		boardMesh = Mesh.loadOBJ(this.getClass().getResource("res/models/chessboard.obj"));
		boardMesh.setPosition(new Vector3f(4.0f, 0.0f, 4.0f));
		boardTexture = new Texture("Chess Board Texture", this.getClass().getResource("res/textures/chessboard.png"));
		
		initSquareHighlightMeshes();
		
		Texture[][] squareHighlightTextures = Texture.loadSpriteSheet("Square Highlight Textures", this.getClass().getResource("res/textures/square_highlights.png"), 2, 2);
		squareSelectedTexture = squareHighlightTextures[0][0];
		squareMovableTexture = squareHighlightTextures[0][1];
		squareAttackableTexture = squareHighlightTextures[1][0];
	}

	private void initShaders() {
		standardShader = new Shader(this.getClass().getResource("res/shaders/standard.vs"), this.getClass().getResource("res/shaders/standard.fs"));
		
		standardShader.addUniform("ProjectionMatrix");
		standardShader.addUniform("ViewMatrix");
		standardShader.addUniform("ModelMatrix");
		standardShader.addUniform("LightPosition");
		
		standardShader.addUniform("TextureSampler");
		standardShader.addUniform("LightColor");
		standardShader.addUniform("LightPower");
		standardShader.addUniform("AmbientLightColor");
		standardShader.addUniform("SpecularLightColor");
		
		squareHighlightShader = new Shader(this.getClass().getResource("res/shaders/square_highlight.vs"), this.getClass().getResource("res/shaders/square_highlight.fs"));
		
		squareHighlightShader.addUniform("ProjectionMatrix");
		squareHighlightShader.addUniform("ViewMatrix");
		squareHighlightShader.addUniform("ModelMatrix");
		squareHighlightShader.addUniform("TextureSampler");
	}
	
	private void initSquareHighlightMeshes() {
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>(6);
		vertices.add(new Vector3f(0.0f, 0.0001f, 0.0f));
		vertices.add(new Vector3f(0.0f, 0.0001f, 1.0f));
		vertices.add(new Vector3f(1.0f, 0.0001f, 0.0f));
		vertices.add(new Vector3f(1.0f, 0.0001f, 0.0f));
		vertices.add(new Vector3f(0.0f, 0.0001f, 1.0f));
		vertices.add(new Vector3f(1.0f, 0.0001f, 1.0f));
		ArrayList<Vector3f> normals = new ArrayList<Vector3f>(6);
		for (int i = 0; i < 6; i++)
			normals.add(new Vector3f(0.0f, 1.0f, 0.0f));
		
		ArrayList<Vector2f> selectedTexCoords = new ArrayList<Vector2f>(6);
		selectedTexCoords.add(new Vector2f(0.0f, 0.0f));
		selectedTexCoords.add(new Vector2f(0.0f, 0.5f));
		selectedTexCoords.add(new Vector2f(0.5f, 0.0f));
		selectedTexCoords.add(new Vector2f(0.5f, 0.0f));
		selectedTexCoords.add(new Vector2f(0.0f, 0.5f));
		selectedTexCoords.add(new Vector2f(0.5f, 0.5f));
		squareSelected = Mesh.createMesh(vertices, selectedTexCoords, normals);
		
		ArrayList<Vector2f> movableTexCoords = new ArrayList<Vector2f>(6);
		movableTexCoords.add(new Vector2f(0.5f, 0.0f));
		movableTexCoords.add(new Vector2f(0.5f, 0.5f));
		movableTexCoords.add(new Vector2f(1.0f, 0.0f));
		movableTexCoords.add(new Vector2f(1.0f, 0.0f));
		movableTexCoords.add(new Vector2f(0.5f, 0.5f));
		movableTexCoords.add(new Vector2f(1.0f, 0.5f));
		squareMovable = Mesh.createMesh(vertices, movableTexCoords, normals);
		
		ArrayList<Vector2f> attackableTexCoords = new ArrayList<Vector2f>(6);
		attackableTexCoords.add(new Vector2f(0.0f, 0.5f));
		attackableTexCoords.add(new Vector2f(0.0f, 1.0f));
		attackableTexCoords.add(new Vector2f(0.5f, 0.5f));
		attackableTexCoords.add(new Vector2f(0.5f, 0.5f));
		attackableTexCoords.add(new Vector2f(0.0f, 1.0f));
		attackableTexCoords.add(new Vector2f(0.5f, 1.0f));
		squareAttackable = Mesh.createMesh(vertices, attackableTexCoords, normals);
	}
	
	public void render(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
		// Use the standard shader for the pieces and board
		standardShader.useProgram();
		standardShader.updateUniform("ProjectionMatrix", projectionMatrix, false);
		standardShader.updateUniform("ViewMatrix", viewMatrix, false);
		standardShader.updateUniform("LightPosition", new Vector3f(4.0f, 4.0f, 4.0f));
		standardShader.updateUniform("LightColor", new Vector3f(0.3f, 0.3f, 0.3f));
		standardShader.updateUniform("LightPower", 50.0f);
		standardShader.updateUniform("AmbientLightColor", new Vector3f(0.1f, 0.1f, 0.1f));
		standardShader.updateUniform("SpecularLightColor", new Vector3f(0.3f, 0.3f, 0.3f));
		
		GL30.glBindVertexArray(vaoID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		// Render the board
		boardTexture.bind(0);
		standardShader.updateUniform("TextureSampler", 0);
		boardMesh.updateModelMatrix();
		standardShader.updateUniform("ModelMatrix", boardMesh.getModelMatrix(), false);
		
		boardMesh.bindVertexBuffer();
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
		boardMesh.bindTexCoordBuffer();
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0L);
		boardMesh.bindNormalBuffer();
		GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0L);
		boardMesh.draw();
		
		// Render the pieces
		for (int i = 0; i < squares.length; i++) {
			ChessSquare square = squares[i];
			ChessPiece piece = square.getPiece();
			
			if (piece != null && piece.getMesh() != null) {
				Mesh mesh = piece.getMesh();
				mesh.updateModelMatrix();
				standardShader.updateUniform("ModelMatrix", mesh.getModelMatrix(), false);
				
				if (piece.isWhite()) {
					whiteWoodTexture.bind(1);
					standardShader.updateUniform("TextureSampler", 1);
				} else {
					blackWoodTexture.bind(2);
					standardShader.updateUniform("TextureSampler", 2);
				}
				
				mesh.bindVertexBuffer();
				GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
				mesh.bindTexCoordBuffer();
				GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0L);
				mesh.bindNormalBuffer();
				GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0L);
				mesh.draw();
			}
		}
		for (ChessPiece piece : takenPiecesW) {
			if (piece != null && piece.getMesh() != null) {
				Mesh mesh = piece.getMesh();
				mesh.updateModelMatrix();
				standardShader.updateUniform("ModelMatrix", mesh.getModelMatrix(), false);
				
				if (piece.isWhite()) {
					whiteWoodTexture.bind(1);
					standardShader.updateUniform("TextureSampler", 1);
				} else {
					blackWoodTexture.bind(2);
					standardShader.updateUniform("TextureSampler", 2);
				}
				
				mesh.bindVertexBuffer();
				GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
				mesh.bindTexCoordBuffer();
				GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0L);
				mesh.bindNormalBuffer();
				GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0L);
				mesh.draw();
			}
		}
		for (ChessPiece piece : takenPiecesB) {
			if (piece != null && piece.getMesh() != null) {
				Mesh mesh = piece.getMesh();
				mesh.updateModelMatrix();
				standardShader.updateUniform("ModelMatrix", mesh.getModelMatrix(), false);
				
				if (piece.isWhite()) {
					whiteWoodTexture.bind(1);
					standardShader.updateUniform("TextureSampler", 1);
				} else {
					blackWoodTexture.bind(2);
					standardShader.updateUniform("TextureSampler", 2);
				}
				
				mesh.bindVertexBuffer();
				GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
				mesh.bindTexCoordBuffer();
				GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0L);
				mesh.bindNormalBuffer();
				GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0L);
				mesh.draw();
			}
		}
		
		// Use the square highlight shader for the square highlights
		squareHighlightShader.useProgram();
		squareHighlightShader.updateUniform("ProjectionMatrix", projectionMatrix, false);
		squareHighlightShader.updateUniform("ViewMatrix", viewMatrix, false);
		
		// Render square highlights
		for (int i = 0; i < squares.length; i++) {
			ChessSquare square = squares[i];
			if (square.isSelected()) {
				// Square is selected
				squareSelectedTexture.bind(3);
				squareHighlightShader.updateUniform("TextureSampler", 3);
				squareSelected.setPosition(new Vector3f((float) (i % 8), 0.0f, (float) (i / 8)));
				squareSelected.updateModelMatrix();
				squareHighlightShader.updateUniform("ModelMatrix", squareSelected.getModelMatrix(), false);
				
				squareSelected.bindVertexBuffer();
				GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
				squareSelected.bindTexCoordBuffer();
				GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0L);
				squareSelected.bindNormalBuffer();
				GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0L);
				squareSelected.draw();
			} else if (square.isMovable()) {
				if (square.getPiece() != null && square.getPiece().isWhite() != whiteTurn) {
					// Square is attackable
					squareAttackableTexture.bind(4);
					squareHighlightShader.updateUniform("TextureSampler", 4);
					squareAttackable.setPosition(new Vector3f((float) (i % 8), 0.0f, (float) (i / 8)));
					squareAttackable.updateModelMatrix();
					squareHighlightShader.updateUniform("ModelMatrix", squareAttackable.getModelMatrix(), false);
					
					squareAttackable.bindVertexBuffer();
					GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
					squareAttackable.bindTexCoordBuffer();
					GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0L);
					squareAttackable.bindNormalBuffer();
					GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0L);
					squareAttackable.draw();
				} else {
					// Square is movable
					squareMovableTexture.bind(5);
					squareHighlightShader.updateUniform("TextureSampler", 5);
					squareMovable.setPosition(new Vector3f((float) (i % 8), 0.0f, (float) (i / 8)));
					squareMovable.updateModelMatrix();
					squareHighlightShader.updateUniform("ModelMatrix", squareMovable.getModelMatrix(), false);
					
					squareMovable.bindVertexBuffer();
					GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
					squareMovable.bindTexCoordBuffer();
					GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0L);
					squareMovable.bindNormalBuffer();
					GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0L);
					squareMovable.draw();
				}
			}
		}
		
		// Clean up
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	public void destroy() {
		for (ChessPiece piece : takenPiecesW) {
			if (piece != null && piece.getMesh() != null)
				piece.destroyMesh();
		}
		for (ChessPiece piece : takenPiecesB) {
			if (piece != null && piece.getMesh() != null)
				piece.destroyMesh();
		}
		for (ChessSquare square : squares) {
			if (square.getPiece() != null && square.getPiece().getMesh() != null)
				square.getPiece().destroyMesh();
		}
			
		GL30.glDeleteVertexArrays(vaoID);
		whiteWoodTexture.destroy();
		blackWoodTexture.destroy();
		boardTexture.destroy();
		boardMesh.destroy();
		standardShader.deleteProgram();
	}
	
	public ChessPiece getMovingPiece() {
		return movingPiece;
	}
	
	public boolean isWhiteTurn() {
		return whiteTurn;
	}
	
	public boolean isKingInCheck() {
		return isKingInCheck;
	}
	
	public void resetBoard() {
		for (ChessSquare s : squares) {
			s.setMovable(false);
			s.setSelected(false);
		}
	}
	
	public void update() {
		if (moveTime != -1L) {
			long now = System.nanoTime();
			if (now - moveTime > FLIP_BOARD_TIME) {
				chessGame.flipCamera(whiteTurn);
				moveTime = -1L;
			} else if (startSquare != -1 && destSquare != -1) { 
				if (now - moveTime > MOVE_TIME) {
					squares[destSquare].setPiece(squares[startSquare].getPiece());
					squares[destSquare].getPiece().setHasMoved();
					squares[startSquare].setPiece(null);
					startSquare = -1;
					destSquare = -1;
				} else {
					squares[startSquare].getPiece().getMesh().getPosition().translate(stepDist.getX(), stepDist.getY(), stepDist.getZ());
				}
			}
		}
	}

	public void updatePress(int row, int column) {
		if (moveTime != -1L)
			return;
		
		if (row < 0 || column < 0 || row > 7 || column > 7)
			return;
		
		int clickedSquare = row * 8 + column;
		calculateMoves(clickedSquare);
	}
	
	public void calculateMoves(int clickedSquare) {
		if (selectedSquare >= 0 && squares[clickedSquare].isMovable()) { // Check if a piece has been moved
			if (squares[clickedSquare].getPiece() != null && squares[clickedSquare].getPiece().isWhite() == whiteTurn && squares[clickedSquare].getPiece() instanceof Rook) {
				// Castle
				squares[clickedSquare - 1].setPiece(squares[selectedSquare].getPiece());
				squares[clickedSquare - 2].setPiece(squares[clickedSquare].getPiece());
				squares[selectedSquare].setPiece(null);
				squares[clickedSquare].setPiece(null);
				squares[clickedSquare - 1].getPiece().setHasMoved();
				squares[clickedSquare - 2].getPiece().setHasMoved();
			} else {
				// Move taken piece to the side
				if (squares[clickedSquare].getPiece() != null && squares[clickedSquare].getPiece().getMesh() != null) {
					if (squares[clickedSquare].getPiece().isWhite()) {
						takenPiecesW.add(squares[clickedSquare].getPiece());
						int takenCount = takenPiecesW.size();
						if (takenCount >= 8)
							squares[clickedSquare].getPiece().getMesh().setPosition(new Vector3f(-1.5f, 0.0f, takenCount - 8.0f));
						else
							squares[clickedSquare].getPiece().getMesh().setPosition(new Vector3f(-0.5f, 0.0f, takenCount + 0.0f));
					} else {
						takenPiecesB.add(squares[clickedSquare].getPiece());
						int takenCount = takenPiecesB.size();
						if (takenCount >= 8)
							squares[clickedSquare].getPiece().getMesh().setPosition(new Vector3f(9.5f, 0.0f, takenCount - 8.0f));
						else
							squares[clickedSquare].getPiece().getMesh().setPosition(new Vector3f(8.5f, 0.0f, takenCount + 0.0f));
					}
					squares[clickedSquare].setPiece(null);
				}
				
				// Setup smooth piece movement
				movingPiece = squares[selectedSquare].getPiece();
				startSquare = selectedSquare;
				destSquare = clickedSquare;
				stepDist = new Vector3f((squares[destSquare].getPiecePosition().getX() - squares[startSquare].getPiecePosition().getX()) / ((float) MOVE_TIME / (float) MOVE_SPEED), 0.0f, (squares[destSquare].getPiecePosition().getZ() - squares[startSquare].getPiecePosition().getZ()) / ((float) MOVE_TIME / (float) MOVE_SPEED));
			}
			
			/*
			// Search for check
			isKingInCheck = false;
			CheckSearchLoop:
			for (int square = 0; square < squares.length; square++) {
				for (Integer posSquare : getPosSquares(false, square)) {
					if (posSquare >= 0 && squares[posSquare].getPiece() instanceof King) {
						isKingInCheck = true;
						break CheckSearchLoop;
					}
				}
			}
			*/
			whiteTurn = !whiteTurn;
			resetBoard();
			moveNum++;
			moveTime = System.nanoTime();
		} else { // Check if a new square has been selected, and if one has, calculate possible moves for it
			resetBoard();
			if (squares[clickedSquare].getPiece() == null) {
				selectedSquare = -1;
			} else if (squares[clickedSquare].getPiece().isWhite() == whiteTurn) {
				selectedSquare = clickedSquare;
				squares[selectedSquare].setSelected(true);
				
				getPosSquares(true, selectedSquare);
			}
		}
		
		// Check if pawn is in last row
		for (int i = 0; i < 8; i++) {
			if (squares[i].getPiece() != null && squares[i].getPiece().isWhite() && squares[i].getPiece() instanceof Pawn) {
				squares[i].getPiece().destroyMesh();
				squares[i].setPiece(new Queen(true));
			}
		}
		for (int i = 57; i < 64; i++) {
			if (squares[i].getPiece() != null && !squares[i].getPiece().isWhite() && squares[i].getPiece() instanceof Pawn) {
				squares[i].getPiece().destroyMesh();
				squares[i].setPiece(new Queen(false));
			}
		}
	}
	
	public Iterable<Integer> getPosSquares(boolean setMovables, int selectedSquare) {
		ArrayList<Integer> allPosSquares = new ArrayList<Integer>();
		if (this.squares[selectedSquare].getPiece() == null)
			return allPosSquares;
		
		ChessPiece selectedPiece = this.squares[selectedSquare].getPiece();
		int[] possibleMoves = selectedPiece.getMoves();
		for (int i = 0; i < possibleMoves.length; i++) {
			int[] posSquares = null;
			if (possibleMoves[i] == 1) { // One move in direction
				posSquares = getPossibleSquaresStraight(i, 1, selectedSquare);
			} else if (possibleMoves[i] == 2) { // Infinte moves in direction
				posSquares = getPossibleSquaresStraight(i, 8, selectedSquare);
			} else if (possibleMoves[i] == 3) { // L-shaped move
				posSquares = new int[] {getPossibleSquareLShaped(i)};
			} else if (possibleMoves[i] == 4) { // Move, no attack
				if (selectedPiece.isWhite() && selectedSquare / 8 == 6 || !selectedPiece.isWhite() && selectedSquare / 8 == 1) {
					posSquares = getPossibleSquaresStraight(i, 2, selectedSquare);
					posSquares[0] = (posSquares[0] != -1 && this.squares[posSquares[0]].getPiece() == null) ? posSquares[0] : -1;
					posSquares[1] = (posSquares[1] != -1 && this.squares[posSquares[1]].getPiece() == null) ? posSquares[1] : -1;
				} else {
					posSquares = getPossibleSquaresStraight(i, 1, selectedSquare);
					posSquares[0] = (this.squares[posSquares[0]].getPiece() == null) ? posSquares[0] : -1;
				}
			} else if (possibleMoves[i] == 5) { // Attack, no move
				posSquares = getPossibleSquaresStraight(i, 1, selectedSquare);
				posSquares[0] = (posSquares[0] != -1 && this.squares[posSquares[0]].getPiece() != null) ? posSquares[0] : -1;
			}
			
			// Check for castling
			if (setMovables) {
				if (this.whiteTurn) {
					if (selectedPiece instanceof King && selectedSquare == 60 && !selectedPiece.hasMoved()) {
						if (this.squares[61].getPiece() == null && this.squares[62].getPiece() == null && this.squares[63].getPiece() != null && this.squares[63].getPiece().isWhite() && this.squares[63].getPiece() instanceof Rook && !this.squares[63].getPiece().hasMoved()) {
							this.squares[63].setMovable(true);
						}
					}
				} else if (selectedPiece instanceof King && selectedSquare == 4 && !selectedPiece.hasMoved()) {
					if (this.squares[5].getPiece() == null && this.squares[6].getPiece() == null && this.squares[7].getPiece() != null && !this.squares[7].getPiece().isWhite() && this.squares[7].getPiece() instanceof Rook && !this.squares[7].getPiece().hasMoved()) {
						this.squares[7].setMovable(true);
					}
				}
			}
			
			if (posSquares != null) {
				for (int posSquare : posSquares) {
					allPosSquares.add(posSquare);
					if (setMovables && posSquare >= 0) {
						if (this.squares[posSquare].getPiece() != null && this.squares[posSquare].getPiece() instanceof King) {
						} else if (this.squares[posSquare].getPiece() == null || this.squares[posSquare].getPiece().isWhite() != this.whiteTurn) {
							this.squares[posSquare].setMovable(true);
						}
					}
				}
			}
		}
		
		return allPosSquares;
	}
	
	public int getPossibleSquareLShaped(int dir) {
		if (this.whiteTurn) {
			switch (dir) {
			case 0:
				if (this.selectedSquare % 8 > 1 && this.selectedSquare / 8 > 0)
					return this.selectedSquare - 10;
				break;
			case 1:
				if (this.selectedSquare % 8 > 0 && this.selectedSquare / 8 > 1)
					return this.selectedSquare - 17;
				break;
			case 2:
				if (this.selectedSquare % 8 < 7 && this.selectedSquare / 8 > 1)
					return this.selectedSquare - 15;
				break;
			case 3:
				if (this.selectedSquare % 8 < 6 && this.selectedSquare / 8 > 0)
					return this.selectedSquare - 6;
				break;
			case 4:
				if (this.selectedSquare % 8 < 6 && this.selectedSquare / 8 < 7)
				break;
			case 5:
				if (this.selectedSquare % 8 < 7 && this.selectedSquare / 8 < 6)
					return this.selectedSquare + 17;
				break;
			case 6:
				if (this.selectedSquare % 8 > 0 && this.selectedSquare / 8 < 6)
					return this.selectedSquare + 15;
				break;
			case 7:
				if (this.selectedSquare % 8 > 1 && this.selectedSquare / 8 < 7)
					return this.selectedSquare + 6;
				break;
			}
		} else {
			switch (dir) {
			case 0:
				if (this.selectedSquare % 8 < 6 && this.selectedSquare / 8 < 7)
					return this.selectedSquare + 10;
				break;
			case 1:
				if (this.selectedSquare % 8 < 7 && this.selectedSquare / 8 < 6)
					return this.selectedSquare + 17;
				break;
			case 2:
				if (this.selectedSquare % 8 > 0 && this.selectedSquare / 8 < 6)
					return this.selectedSquare + 15;
				break;
			case 3:
				if (this.selectedSquare % 8 > 1 && this.selectedSquare / 8 < 7)
					return this.selectedSquare + 6;
				break;
			case 4:
				if (this.selectedSquare % 8 > 1 && this.selectedSquare / 8 > 0)
					return this.selectedSquare - 10;
				break;
			case 5:
				if (this.selectedSquare % 8 > 0 && this.selectedSquare / 8 > 1)
					return this.selectedSquare - 17;
				break;
			case 6:
				if (this.selectedSquare % 8 < 7 && this.selectedSquare / 8 > 1)
					return this.selectedSquare - 15;
				break;
			case 7:
				if (this.selectedSquare % 8 < 6 && this.selectedSquare / 8 > 0)
					return this.selectedSquare - 6;
				break;
			}
		}
		
		return -1;
	}

	public int[] getPossibleSquaresStraight(int dir, int step, int selectedSquare) {
		if (step > 8)
			throw new IllegalArgumentException("Step cannot be greater than 8");
		
		int[] posSquares = new int[step];
		for (int i = 0; i < posSquares.length; i++)
			posSquares[i] = -1;
		
		for (int i = 1; i <= step; i++) {
			int posSquare = -1;
			if (whiteTurn) {
				switch (dir) {
				case 0:
					if (selectedSquare % 8 > (i - 1) && selectedSquare / 8 > (i - 1))
						posSquare = selectedSquare - 9 * i;
					break;
				case 1:
					if (selectedSquare / 8 > (i - 1))
						posSquare = selectedSquare - 8 * i;
					break;
				case 2:
					if (selectedSquare % 8 < (8 - i) && selectedSquare / 8 > (i - 1))
						posSquare = selectedSquare - 7 * i;
					break;
				case 3:
					if (selectedSquare % 8 < (8 - i))
						posSquare = selectedSquare + 1 * i;
					break;
				case 4:
					if (selectedSquare % 8 < (8 - i) && selectedSquare / 8 < (8 - i))
						posSquare = selectedSquare + 9 * i;
					break;
				case 5:
					if (selectedSquare / 8 < (8 - i))
						posSquare = selectedSquare + 8 * i;
					break;
				case 6:
					if (selectedSquare % 8 > (i - 1) && selectedSquare / 8 < (8 - i))
						posSquare = selectedSquare + 7 * i;
					break;
				case 7:
					if (selectedSquare % 8 > (i - 1))
						posSquare = selectedSquare - 1 * i;
					break;
				}
			} else {
				switch (dir) {
				case 0:
					if (selectedSquare % 8 < (8 - i) && selectedSquare / 8 < (8 - i))
						posSquare = selectedSquare + 9 * i;
					break;
				case 1:
					if (selectedSquare / 8 < (8 - i))
						posSquare = selectedSquare + 8 * i;
					break;
				case 2:
					if (selectedSquare % 8 > (i - 1) && selectedSquare / 8 < (8 - i))
						posSquare = selectedSquare + 7 * i;
					break;
				case 3:
					if (selectedSquare % 8 > (i - 1))
						posSquare = selectedSquare - 1 * i;
					break;
				case 4:
					if (selectedSquare % 8 > (i - 1) && selectedSquare / 8 > (i - 1))
						posSquare = selectedSquare - 9 * i;
					break;
				case 5:
					if (selectedSquare / 8 > (i - 1))
						posSquare = selectedSquare - 8 * i;
					break;
				case 6:
					if (selectedSquare % 8 < (8 - i) && selectedSquare / 8 > (i - 1))
						posSquare = selectedSquare - 7 * i;
					break;
				case 7:
					if (selectedSquare % 8 < (8 - step))
						posSquare = selectedSquare + 1 * i;
					break;
				}
			}
			
			posSquares[i - 1] = posSquare;
			if (posSquare != -1 && squares[posSquare].getPiece() != null)
				break;
		}
		
		return posSquares;
	}
	
	public void flip() {
		isFlipped = !isFlipped;
	}
	
	public int getMoveNum() {
		return moveNum;
	}
}
