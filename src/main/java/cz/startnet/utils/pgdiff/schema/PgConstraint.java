/*
 * $CVSHeader$
 */
package cz.startnet.utils.pgdiff.schema;

/**
 * Stores table constraint information.
 *
 * @author fordfrog
 * @version $CVSHeader$
 */
public class PgConstraint {
    /**
     * Definition of the constraint.
     */
    private String definition = null;

    /**
     * Name of the constraint.
     */
    private String name = null;

    /**
     * Creates a new PgConstraint object.
     *
     * @param name name of the constraint
     */
    public PgConstraint(String name) {
        this.name = name;
    }

    /**
     * Setter for {@link #definition definition}.
     *
     * @param definition {@link #definition definition}
     */
    public void setDefinition(final String definition) {
        this.definition = definition;
    }

    /**
     * Getter for {@link #definition definition}.
     *
     * @return {@link #definition definition}
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Setter for {@link #name name}.
     *
     * @param name {@link #name name}
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter for {@link #name name}.
     *
     * @return {@link #name name}
     */
    public String getName() {
        return name;
    }

    /**
     * Returns true if this is a PRIMARY KEY constraint, otherwise
     * false.
     *
     * @return true if this is a PRIMARY KEY constraint, otherwise false
     */
    public boolean isPrimaryKeyConstraint() {
        return definition.contains("PRIMARY KEY");
    }
}
