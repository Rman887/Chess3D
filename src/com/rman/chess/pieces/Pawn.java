package com.rman.chess.pieces;

import com.rman.engine.graphics.Mesh;

public class Pawn extends ChessPiece {

	public Pawn(boolean isWhite) {
		super("Pawn", isWhite);
		mesh = Mesh.loadOBJ(this.getClass().getResource("models/pawn.obj"));
	}
	
	@Override
	public int[] getMoves() {
		return new int[] {5, 4, 5, 0, 0, 0, 0, 0};
	}
}
