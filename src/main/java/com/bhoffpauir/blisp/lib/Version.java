package com.bhoffpauir.blisp.lib;

/**
 * Representation for the language version.
 */
public record Version(int major, int minor, int patch) {
	@Override
	public String toString() {
		return String.format("v%d.%d.%d", major, minor, patch);
	}
}
