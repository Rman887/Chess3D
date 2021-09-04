package com.rman.chess.pieces;

import com.rman.engine.graphics.Mesh;

public class Queen extends ChessPiece {

	public Queen(boolean isWhite) {
		super("Queen", isWhite);
		mesh = Mesh.loadOBJ(this.getClass().getResource("models/queen.obj"));
	}
	
	@Override
	public int[] getMoves() {
		return new int[] {2, 2, 2, 2, 2, 2, 2, 2};
	}
}
