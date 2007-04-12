/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.loader;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;


/**
 * Tests for PgDiffLoader class.
 *
 * @author fordfrog
 * @version $Id$
 */
@RunWith(value = Parameterized.class)
public class PgDumpLoaderTest {
    /**
     * Index of the file that should be tested.
     */
    private final int fileIndex;

    /**
     * Creates a new instance of PgDumpLoaderTest.
     *
     * @param fileIndex {@link #fileIndex fileIndex}
     */
    public PgDumpLoaderTest(final int fileIndex) {
        this.fileIndex = fileIndex;
    }

    /**
     * Provides parameters for running the tests.
     *
     * @return parameters for the tests
     */
    @Parameters
    public static Collection parameters() {
        return Arrays.asList(
                new Object[][] {
                    { 1 },
                    { 2 },
                    { 3 },
                    { 4 },
                    { 5 }
                });
    }

    /**
     * Runs single test.
     */
    @Test(timeout = 1000)
    public void loadSchema() {
        PgDumpLoader.loadSchema(
                this.getClass()
                    .getResourceAsStream("schema_" + fileIndex + ".sql"));
    }
}
