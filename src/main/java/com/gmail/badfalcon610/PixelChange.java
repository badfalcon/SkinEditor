package com.gmail.badfalcon610;

/**
 * A single pixel change: coordinates and the ARGB value at that spot.
 * Used to record diffs for undo/redo.
 */
public class PixelChange {
	final int x;
	final int y;
	final int rgb;

	PixelChange(int x, int y, int rgb) {
		this.x = x;
		this.y = y;
		this.rgb = rgb;
	}
}
