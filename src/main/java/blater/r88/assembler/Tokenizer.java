package blater.r88.assembler;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.FileReader;
import java.io.StreamTokenizer;
import java.util.Objects;

import static java.io.StreamTokenizer.TT_EOF;

@Getter
public class Tokenizer {

    private StreamTokenizer tokenizer;
    private FileReader fr;
    private Token lastToken;

    public Tokenizer(String filename) {
        fr = openFile(filename);
        tokenizer = initTokenizer(fr);
    }

    @SneakyThrows
    public Token readNext() {
        tokenizer.nextToken();
        String val;
        if (tokenizer.ttype == TT_EOF)
            val = "";
        else
            val = tokenizer.sval == null ? String.valueOf(tokenizer.nval) : tokenizer.sval;

        lastToken = new Token(val);
        return new Token(val);
    }

    public boolean hasMoreTokens() {
        return ! isEndOfStream();
    }
    public boolean isEndOfStream() {
        return tokenizer.ttype == TT_EOF;
    }

    @SneakyThrows
    public void close() {
        fr.close();
    }

    @SneakyThrows
    private FileReader openFile(String filename) {
        ClassLoader classLoader = getClass().getClassLoader();
        String f = Objects.requireNonNull(classLoader.getResource(filename)).getFile();
        FileReader reader = new FileReader(f);
        System.err.println(" open file:" + f);
        return reader;
    }

    private StreamTokenizer initTokenizer(FileReader fr) {
        final StreamTokenizer tokenizer = new StreamTokenizer(fr) {
            @Override
            public void parseNumbers() {
                // super.parseNumbers(); - call to super commented out to disable special handling of numeric chars
            }
        };
        tokenizer.wordChars('!', '~');
        tokenizer.commentChar(';');
        tokenizer.eolIsSignificant(false);
        return tokenizer;
    }

}
