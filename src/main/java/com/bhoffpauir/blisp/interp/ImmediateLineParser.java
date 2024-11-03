package com.bhoffpauir.blisp.interp;

import java.util.List;
import org.jline.reader.Parser;
import org.jline.reader.ParsedLine;

public class ImmediateLineParser implements Parser {
    @Override
    public ParsedLine parse(String line, int cursor, ParseContext context) {
        return new ParsedLine() {
            @Override
            public String word() { return line; }

            @Override
            public int wordCursor() { return cursor; }

            @Override
            public int wordIndex() { return 0; }

            @Override
            public List<String> words() { return List.of(line); }

            @Override
            public String line() { return line; }

            @Override
            public int cursor() { return cursor; }
        };
    }
}
