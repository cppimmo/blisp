package com.bhoffpauir.blisp.lib;

import java.nio.file.Path;

/**
 * Represents the source location of a code element within a file.
 * 
 * <p>
 * A source location includes:
 * <ul>
 *   <li>The path of the file where the code is located.</li>
 *   <li>The line number within the file.</li>
 *   <li>The character offset within the specified line.</li>
 * </ul>
 * </p>
 * 
 * @param filePath       The path of the source file. Can be {@code null} if unknown or omitted.
 * @param lineNumber     The 1-based line number where the code resides.
 * @param characterOffset The 1-based character offset within the line.
 */
public record SourceLocation(Path filePath, int lineNumber, int characterOffset) {
	private static final String DEFAULT_FILENAME = "N/A";
	
	/**
	 * Constructs a {@code SourceLocation} for an unspecified file.
	 * 
	 * @param lineNumber      The 1-based line number.
	 * @param characterOffset The 1-based character offset within the line.
	 * @return A new {@code SourceLocation} without a file name.
	 */
	public static SourceLocation of(int lineNumber, int characterOffset) {
		return new SourceLocation(null, lineNumber, characterOffset);
	}
	
	public static SourceLocation of(String fileName, int lineNumber, int characterOffset) {
		return new SourceLocation(Path.of(fileName), lineNumber, characterOffset);
	}
	
	public static SourceLocation of(Path filePath, int lineNumber, int characterOffset) {
		return new SourceLocation(filePath, lineNumber, characterOffset);
	}
	
	@Override
	public String toString() {
		String fileName = (filePath != null && filePath.getFileName() != null) 
				? filePath.getFileName().toString() 
                : DEFAULT_FILENAME;
		return String.format("[FILE:%s][LINE:%d|CHAR:%d]", fileName, lineNumber, characterOffset);
	}
}
