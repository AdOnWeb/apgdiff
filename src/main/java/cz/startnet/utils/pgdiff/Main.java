/*
 * $Id$
 */
package cz.startnet.utils.pgdiff;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Compares two PostgreSQL dumps and outputs information about differences
 * in the database schemas.
 *
 * @author fordfrog
 * @version $Id$
 */
public class Main {

    /**
     * Creates a new Main object.
     */
    private Main() {
        super();
    }

    /**
     * APgDiff main method.
     *
     * @param args the command line arguments
     *
     * @throws UnsupportedEncodingException Thrown if unsupported output
     * encoding has been encountered.
     */
    public static void main(final String[] args) throws
        UnsupportedEncodingException {
        final PrintWriter writer = new PrintWriter(System.out, true);
        final PgDiffArguments arguments = new PgDiffArguments();

        if (arguments.parse(writer, args)) {
            final PrintWriter encodedWriter =
                new PrintWriter(new OutputStreamWriter(System.out, arguments.
                getOutCharsetName()));
            PgDiff.createDiff(encodedWriter, arguments);
        }
    }
}
