package com.jumble;

import java.io.PrintStream;

/**
 * This class wraps a PrintStream and ignores the close() call.
 * This is usefull because the JumbleScorePrinterListener closes the stream even
 * if I want to run multimple tests in a single jvm.
 */
public class UnclosablePrintStream extends PrintStream {

    public UnclosablePrintStream(PrintStream stream) {
        super(stream);
    }

    @Override
    public void close() {} //ignore close() call

}
