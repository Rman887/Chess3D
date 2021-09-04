package com.rman.chess.pieces;

import com.rman.engine.graphics.Mesh;

public class Bishop extends ChessPiece {
	
	public Bishop(boolean isWhite) {
		super("Bishop", isWhite);
		mesh = Mesh.loadOBJ(this.getClass().getResource("models/bishop.obj"));
	}
	
	@Override
	public int[] getMoves() {
		return new int[] {2, 0, 2, 0, 2, 0, 2, 0};
	}
}
