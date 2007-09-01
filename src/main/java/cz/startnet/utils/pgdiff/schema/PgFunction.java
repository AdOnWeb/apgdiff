/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.schema;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Stores function information.
 *
 * @author fordfrog
 * @version $Id$
 */
public class PgFunction {
    /**
     * Pattern for checking whether function definition contains CREATE
     * OR REPLACE FUNCTION string.
     */
    private static final Pattern PATTERN_CREATE_FUNCTION =
        Pattern.compile(
                "(?:CREATE[\\s]+FUNCTION)([\\s]+.*)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /**
     * Declaration of the function. Contains function name and
     * arguments.
     */
    private String declaration;

    /**
     * Whole definition of the function.
     */
    private String definition;

    /**
     * Name of the function including argument types.
     */
    private String name;

    /**
     * Returns creation SQL of the function.
     *
     * @return creation SQL
     */
    public String getCreationSQL() {
        final String result;
        final Matcher matcher = PATTERN_CREATE_FUNCTION.matcher(definition);

        if (matcher.matches()) {
            result = "CREATE OR REPLACE FUNCTION" + matcher.group(1);
        } else {
            result = getDefinition();
        }

        return result;
    }

    /**
     * Setter for {@link #declaration}.
     *
     * @param declaration {@link #declaration}
     */
    public void setDeclaration(final String declaration) {
        this.declaration = declaration;
    }

    /**
     * Getter for {@link #declaration}.
     *
     * @return {@link #declaration}
     */
    public String getDeclaration() {
        return declaration;
    }

    /**
     * Setter for {@link #definition}.
     *
     * @param definition {@link #definition}
     */
    public void setDefinition(final String definition) {
        this.definition = definition;
    }

    /**
     * Getter for {@link #definition}.
     *
     * @return {@link #definition}
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Creates and returns SQL for dropping the function.
     *
     * @return created SQL
     */
    public String getDropSQL() {
        return "DROP FUNCTION " + getDeclaration() + ";";
    }

    /**
     * Setter for {@link #name}.
     *
     * @param name {@link #name}
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter for {@link #name}.
     *
     * @return {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     *
     * @param object {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean equals(final Object object) {
        boolean equals = false;

        if (this == object) {
            equals = true;
        } else if (object instanceof PgFunction) {
            final PgFunction function = (PgFunction) object;
            equals =
                declaration.equals(function.declaration)
                && getDefinition().equals(function.getDefinition())
                && name.equals(function.name);
        }

        return equals;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return (getClass().getName() + "|" + declaration + "|"
        + getDefinition() + "|" + name).hashCode();
    }
}
