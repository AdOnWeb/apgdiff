/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgView;

/**
 * Parses ALTER VIEW statements.
 * 
 * @author fordfrog
 */
public class AlterViewParser {

    /**
     * Creates new instance of AlterViewParser.
     */
    private AlterViewParser() {
    }

    /**
     * Parses ALTER VIEW statement.
     * 
     * @param database database
     * @param statement ALTER VIEW statement
     * @param outputIgnoredStatements whether ignored statements should be
     * output in the diff
     */
    public static void parse(final PgDatabase database,
            final String statement, final boolean outputIgnoredStatements) {
        final Parser parser = new Parser(statement);
        parser.expect("ALTER", "VIEW");

        final String viewName = parser.parseIdentifier();
        final String schemaName = ParserUtils.getSchemaName(viewName, database);
        final String objectName = ParserUtils.getObjectName(viewName);

        final PgView view = database.getSchema(schemaName).getView(objectName);

        while (!parser.expectOptional(";")) {
            if (parser.expectOptional("ALTER")) {
                parser.expectOptional("COLUMN");

                final String columnName =
                        ParserUtils.getObjectName(parser.parseIdentifier());

                if (parser.expectOptional("SET", "DEFAULT")) {
                    final String expression = parser.getExpression();
                    view.addColumnDefaultValue(columnName, expression);
                } else if (parser.expectOptional("DROP", "DEFAULT")) {
                    view.removeColumnDefaultValue(columnName);
                } else {
                    parser.throwUnsupportedCommand();
                }
            } else if (parser.expectOptional("OWNER", "TO")) {
                // we do not parse this one so we just consume the identifier
                if (outputIgnoredStatements) {
                    database.addIgnoredStatement("ALTER TABLE " + viewName
                            + " OWNER TO " + parser.parseIdentifier() + ';');
                } else {
                    parser.parseIdentifier();
                }
            } else {
                parser.throwUnsupportedCommand();
            }
        }
    }
}
