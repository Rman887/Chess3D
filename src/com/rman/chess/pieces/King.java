package com.rman.chess.pieces;

import com.rman.engine.graphics.Mesh;

public class King extends ChessPiece {
	
	public King(boolean isWhite) {
		super("King", isWhite);
		mesh = Mesh.loadOBJ(this.getClass().getResource("models/king.obj"));
	}
	
	@Override
	public int[] getMoves() {
		return new int[] {1, 1, 1, 1, 1, 1, 1, 1};
	}
}
