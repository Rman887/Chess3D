package com.rman.chess.pieces;

import com.rman.engine.graphics.Mesh;

public class Knight extends ChessPiece {
	
	public Knight(boolean isWhite) {
		super("Knight", isWhite);
		mesh = Mesh.loadOBJ(this.getClass().getResource("models/knight.obj"));
		if (isWhite)
			mesh.rotate(0.0f, 180.0f, 0.0f);
	}
	
	@Override
	public int[] getMoves() {
		return new int[] {3, 3, 3, 3, 3, 3, 3, 3};
	}
}
