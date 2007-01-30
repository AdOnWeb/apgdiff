/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgConstraint;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parses ALTER TABLE commands.
 *
 * @author fordfrog
 * @version $Id$
 */
public class AlterTableParser {
    /**
     * Pattern for matching ALTER TABLE ... OWNER TO ...;.
     */
    private static final Pattern PATTERN_OWNER =
        Pattern.compile(
                "^ALTER TABLE .* OWNER TO .*;$",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for matching table name and optional definition.
     */
    private static final Pattern PATTERN_START =
        Pattern.compile(
                "ALTER TABLE (?:ONLY )?\"?([^ \"]+)\"?(?: )?(.+)?",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for matching of trailing definition of ALTER TABLE
     * command.
     */
    private static final Pattern PATTERN_TRAILING_DEF =
        Pattern.compile(
                "(CLUSTER ON|ALTER COLUMN) \"?([^ ;\"]+)\"?"
                + "(?: SET STATISTICS )?(\\d+)?;?",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for matching ADD CONSTRAINT row.
     */
    private static final Pattern PATTERN_ADD_CONSTRAINT =
        Pattern.compile(
                "^ADD[ ]+CONSTRAINT[ ]+\"?([^ \"]+)\"?[ ]+(.*)$",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for matching ADD FOREIGN KEY row.
     */
    private static final Pattern PATTERN_ADD_FOREIGN_KEY =
        Pattern.compile(
                "^ADD[ ]+(FOREIGN[ ]+KEY[ ]+\\(([^ ]+)\\)[ ]+.*)$",
                Pattern.CASE_INSENSITIVE);

    /**
     * Creates a new instance of AlterTableParser.
     */
    private AlterTableParser() {
        super();
    }

    /**
     * Parses ALTER TABLE command.
     *
     * @param schema schema to be filled
     * @param command ALTER TABLE command
     *
     * @throws ParserException Thrown if problem occured while parsing DDL.
     */
    public static void parse(final PgSchema schema, final String command) {
        if (!PATTERN_OWNER.matcher(command).matches()) {
            String line = command;
            final Matcher matcher = PATTERN_START.matcher(line);
            final String tableName;

            if (matcher.find()) {
                tableName = matcher.group(1).trim();
            } else {
                throw new ParserException(
                        ParserException.CANNOT_PARSE_COMMAND + line);
            }

            final PgTable table = schema.getTable(tableName);
            line = ParserUtils.removeLastSemicolon(matcher.group(2));

            if (PATTERN_TRAILING_DEF.matcher(line).matches()) {
                parseTraillingDef(table, line.trim());
            } else {
                parseRows(table, line);
            }
        }
    }

    /**
     * Parses all rows in ALTER TABLE command.
     *
     * @param table table being parsed
     * @param commands commands
     *
     * @throws ParserException Thrown if problem occured while parsing DDL.
     */
    private static void parseRows(final PgTable table, final String commands) {
        String line = commands;
        String subCommand = null;

        try {
            while (line.length() > 0) {
                final int commandEnd = ParserUtils.getCommandEnd(line, 0);
                subCommand = line.substring(0, commandEnd).trim();
                line =
                    (commandEnd >= line.length()) ? ""
                                                  : line.substring(
                            commandEnd + 1);

                Matcher matcher = PATTERN_ADD_CONSTRAINT.matcher(subCommand);

                if (matcher.matches()) {
                    final String constraintName = matcher.group(1).trim();
                    final PgConstraint constraint =
                        table.getConstraint(constraintName);
                    constraint.setDefinition(matcher.group(2).trim());
                    subCommand = "";
                }

                if (subCommand.length() > 0) {
                    matcher = PATTERN_ADD_FOREIGN_KEY.matcher(subCommand);

                    if (matcher.matches()) {
                        final String columnName = matcher.group(2).trim();
                        final String constraintName =
                            table.getName() + "_" + columnName + "_fkey";
                        final PgConstraint constraint =
                            table.getConstraint(constraintName);
                        constraint.setDefinition(matcher.group(1).trim());
                        subCommand = "";
                    }
                }

                if (subCommand.length() > 0) {
                    throw new ParserException(
                            "Don't know how to parse: " + subCommand);
                }
            }
        } catch (RuntimeException ex) {
            throw new ParserException(
                    "Cannot parse ALTER TABLE '" + table.getName()
                    + "', line '" + subCommand + "'",
                    ex);
        }
    }

    /**
     * Parses trailling definition.
     *
     * @param table table being parsed
     * @param traillingDef trailling definition
     */
    private static void parseTraillingDef(
        final PgTable table,
        final String traillingDef) {
        final Matcher matcher = PATTERN_TRAILING_DEF.matcher(traillingDef);

        if (matcher.matches()) {
            if ("ALTER COLUMN".equals(matcher.group(1).trim())) {
                //Stats
                final String columnName = matcher.group(2).trim();
                final Integer value = Integer.valueOf(matcher.group(3).trim());
                final PgColumn col = table.getColumn(columnName);
                col.setStatistics(value);
            } else {
                //Cluster
                final String indexName = matcher.group(2).trim();
                table.setClusterIndexName(indexName);
            }
        }
    }
}
