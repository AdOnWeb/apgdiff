/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores schema information.
 *
 * @author fordfrog
 */
public class PgSchema {

    /**
     * List of functions defined in the schema.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgFunction> functions = new ArrayList<PgFunction>();
    /**
     * List of sequences defined in the schema.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgSequence> sequences = new ArrayList<PgSequence>();
    /**
     * List of tables defined in the schema.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgTable> tables = new ArrayList<PgTable>();
    /**
     * List of views defined in the schema.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgView> views = new ArrayList<PgView>();
    /**
     * List of constraints defined in the schema.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgConstraint> constraints =
            new ArrayList<PgConstraint>();
    /**
     * List of indexes defined in the schema.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgIndex> indexes = new ArrayList<PgIndex>();
    /**
     * List of triggers defined in the schema.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgTrigger> triggers = new ArrayList<PgTrigger>();
    /**
     * Name of the schema.
     */
    private final String name;
    /**
     * Schema authorization.
     */
    private String authorization;
    /**
     * Optional definition of schema elements.
     */
    private String definition;
    /**
     * Comment.
     */
    private String comment;

    /**
     * Creates a new PgSchema object.
     *
     * @param name {@link #name}
     */
    public PgSchema(final String name) {
        this.name = name;
    }

    /**
     * Setter for {@link #authorization}.
     *
     * @param authorization {@link #authorization}
     */
    public void setAuthorization(final String authorization) {
        this.authorization = authorization;
    }

    /**
     * Getter for {@link #authorization}.
     *
     * @return {@link #authorization}
     */
    public String getAuthorization() {
        return authorization;
    }

    /**
     * Getter for {@link #comment}.
     *
     * @return {@link #comment}
     */
    public String getComment() {
        return comment;
    }

    /**
     * Setter for {@link #comment}.
     *
     * @param comment {@link #comment}
     */
    public void setComment(final String comment) {
        this.comment = comment;
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
     * Setter for {@link #definition}.
     *
     * @param definition {@link #definition}
     */
    public void setDefinition(final String definition) {
        this.definition = definition;
    }

    /**
     * Creates and returns SQL for creation of the schema.
     *
     * @return created SQL
     */
    public String getCreationSQL() {
        final StringBuilder sbSQL = new StringBuilder(50);
        sbSQL.append("CREATE SCHEMA ");
        sbSQL.append(PgDiffUtils.getQuotedName(getName()));

        if (getAuthorization() != null) {
            sbSQL.append(" AUTHORIZATION ");
            sbSQL.append(PgDiffUtils.getQuotedName(getAuthorization()));
        }

        sbSQL.append(';');

        return sbSQL.toString();
    }

    /**
     * Finds function according to specified function <code>signature</code>.
     *
     * @param signature signature of the function to be searched
     *
     * @return found function or null if no such function has been found
     */
    public PgFunction getFunction(final String signature) {
        for (PgFunction function : functions) {
            if (function.getSignature().equals(signature)) {
                return function;
            }
        }

        return null;
    }

    /**
     * Getter for {@link #functions}. The list cannot be modified.
     *
     * @return {@link #functions}
     */
    public List<PgFunction> getFunctions() {
        return Collections.unmodifiableList(functions);
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
     * Finds constraint according to specified constraint <code>name</code>.
     *
     * @param name name of the constraint to be searched
     *
     * @return found constraint or null if no such constraint has been found
     */
    public PgConstraint getConstraint(final String name) {
        PgConstraint constraint = null;

        for (PgConstraint curConstraint : constraints) {
            if (curConstraint.getName().equals(name)) {
                constraint = curConstraint;

                break;
            }
        }

        return constraint;
    }

    /**
     * Finds index according to specified index <code>name</code>.
     *
     * @param name name of the index to be searched
     *
     * @return found index or null if no such index has been found
     */
    public PgIndex getIndex(final String name) {
        PgIndex index = null;

        for (PgIndex curIndex : indexes) {
            if (curIndex.getName().equals(name)) {
                index = curIndex;

                break;
            }
        }

        return index;
    }

    /**
     * Finds trigger according to specified trigger <code>name</code>.
     *
     * @param name name of the trigger to be searched
     *
     * @return found trigger or null if no such trigger has been found
     */
    public PgTrigger getTrigger(final String name) {
        PgTrigger trigger = null;

        for (PgTrigger curTrigger : triggers) {
            if (curTrigger.getName().equals(name)) {
                trigger = curTrigger;

                break;
            }
        }

        return trigger;
    }

    /**
     * Finds sequence according to specified sequence <code>name</code>.
     *
     * @param name name of the sequence to be searched
     *
     * @return found sequence or null if no such sequence has been found
     */
    public PgSequence getSequence(final String name) {
        PgSequence sequence = null;

        for (PgSequence curSequence : sequences) {
            if (curSequence.getName().equals(name)) {
                sequence = curSequence;

                break;
            }
        }

        return sequence;
    }

    /**
     * Getter for {@link #constraints}. The list cannot be modified.
     *
     * @return {@link #constraints}
     */
    public List<PgConstraint> getConstraints() {
        return Collections.unmodifiableList(constraints);
    }

    /**
     * Getter for {@link #indexes}. The list cannot be modified.
     *
     * @return {@link #indexes}
     */
    public List<PgIndex> getIndexes() {
        return Collections.unmodifiableList(indexes);
    }

    /**
     * Getter for {@link #triggers}. The list cannot be modified.
     *
     * @return {@link #triggers}
     */
    public List<PgTrigger> getTriggers() {
        return Collections.unmodifiableList(triggers);
    }

    /**
     * Getter for {@link #sequences}. The list cannot be modified.
     *
     * @return {@link #sequences}
     */
    public List<PgSequence> getSequences() {
        return Collections.unmodifiableList(sequences);
    }

    /**
     * Finds table according to specified table <code>name</code>.
     *
     * @param name name of the table to be searched
     *
     * @return found table or null if no such table has been found
     */
    public PgTable getTable(final String name) {
        PgTable table = null;

        for (PgTable curTable : tables) {
            if (curTable.getName().equals(name)) {
                table = curTable;

                break;
            }
        }

        return table;
    }

    /**
     * Getter for {@link #tables}. The list cannot be modified.
     *
     * @return {@link #tables}
     */
    public List<PgTable> getTables() {
        return Collections.unmodifiableList(tables);
    }

    /**
     * Finds view according to specified view <code>name</code>.
     *
     * @param name name of the view to be searched
     *
     * @return found view or null if no such view has been found
     */
    public PgView getView(final String name) {
        PgView view = null;

        for (PgView curView : views) {
            if (curView.getName().equals(name)) {
                view = curView;

                break;
            }
        }

        return view;
    }

    /**
     * Getter for {@link #views}. The list cannot be modified.
     *
     * @return {@link #views}
     */
    public List<PgView> getViews() {
        return Collections.unmodifiableList(views);
    }

    /**
     * Adds <code>constraint</code> to the list of constraints.
     *
     * @param constraint constraint
     */
    public void addConstraint(final PgConstraint constraint) {
        constraints.add(constraint);
    }

    /**
     * Adds <code>index</code> to the list of indexes.
     *
     * @param index index
     */
    public void addIndex(final PgIndex index) {
        indexes.add(index);
    }

    /**
     * Adds <code>trigger</code> to the list of triggers.
     *
     * @param trigger trigger
     */
    public void addTrigger(final PgTrigger trigger) {
        triggers.add(trigger);
    }

    /**
     * Adds <code>function</code> to the list of functions.
     *
     * @param function function
     */
    public void addFunction(final PgFunction function) {
        functions.add(function);
    }

    /**
     * Adds <code>sequence</code> to the list of sequences.
     *
     * @param sequence sequence
     */
    public void addSequence(final PgSequence sequence) {
        sequences.add(sequence);
    }

    /**
     * Adds <code>table</code> to the list of tables.
     *
     * @param table table
     */
    public void addTable(final PgTable table) {
        tables.add(table);
    }

    /**
     * Adds <code>view</code> to the list of views.
     *
     * @param view view
     */
    public void addView(final PgView view) {
        views.add(view);
    }

    /**
     * Returns true if schema contains function with given
     * <code>signature</code>, otherwise false.
     *
     * @param signature signature of the function
     *
     * @return true if schema contains function with given
     *         <code>signature</code>, otherwise false
     */
    public boolean containsFunction(final String signature) {
        for (PgFunction function : functions) {
            if (function.getSignature().equals(signature)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if schema contains sequence with given
     * <code>name</code>, otherwise false.
     *
     * @param name name of the sequence
     *
     * @return true if schema contains sequence with given <code>name</code>,
     *         otherwise false
     */
    public boolean containsSequence(final String name) {
        boolean found = false;

        for (PgSequence sequence : sequences) {
            if (sequence.getName().equals(name)) {
                found = true;

                break;
            }
        }

        return found;
    }

    /**
     * Returns true if schema contains table with given
     * <code>name</code>, otherwise false.
     *
     * @param name name of the table
     *
     * @return true if schema contains table with given <code>name</code>,
     *         otherwise false.
     */
    public boolean containsTable(final String name) {
        boolean found = false;

        for (PgTable table : tables) {
            if (table.getName().equals(name)) {
                found = true;

                break;
            }
        }

        return found;
    }

    /**
     * Returns true if schema contains view with given
     * <code>name</code>, otherwise false.
     *
     * @param name name of the view
     *
     * @return true if schema contains view with given <code>name</code>,
     *         otherwise false.
     */
    public boolean containsView(final String name) {
        boolean found = false;

        for (PgView view : views) {
            if (view.getName().equals(name)) {
                found = true;

                break;
            }
        }

        return found;
    }
}
