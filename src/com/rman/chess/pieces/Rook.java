package com.rman.chess.pieces;

import com.rman.engine.graphics.Mesh;

public class Rook extends ChessPiece {

	public Rook(boolean isWhite) {
		super("Rook", isWhite);
		mesh = Mesh.loadOBJ(this.getClass().getResource("models/rook.obj"));
		mesh.rotate(0.0f, 45.0f, 0.0f);
	}
	
	@Override
	public int[] getMoves() {
		return new int[] {0, 2, 0, 2, 0, 2, 0, 2};
	}
}
