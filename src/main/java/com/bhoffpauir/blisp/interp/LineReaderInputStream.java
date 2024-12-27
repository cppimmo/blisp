package com.bhoffpauir.blisp.interp;

import java.io.IOException;
import java.io.InputStream;
import org.jline.reader.LineReader;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;

/**
 * 
 */
public class LineReaderInputStream extends InputStream {
    private final LineReader lineReader;
    private String buffer = null;
    private int bufferPos = 0;

    public LineReaderInputStream(LineReader lineReader) {
        this.lineReader = lineReader;
    }

    @Override
    public int read() throws IOException {
        if (buffer == null || bufferPos >= buffer.length()) {
            try {
                // Read a new line from LineReader
                buffer = lineReader.readLine("") + "\n"; // Add newline to simulate standard input behavior
                bufferPos = 0;
            } catch (UserInterruptException e) {
                // Handle interrupt (Ctrl+C) by returning -1 or throwing an exception
                return -1;
            } catch (EndOfFileException e) {
                // Handle EOF (Ctrl+D) by returning -1
                return -1;
            }
        }
        // Return the next character in the buffer as a byte
        return buffer.charAt(bufferPos++);
    }

    @Override
    public int available() throws IOException {
        return buffer != null ? buffer.length() - bufferPos : 0;
    }
}
