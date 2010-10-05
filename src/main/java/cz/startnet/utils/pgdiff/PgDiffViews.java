/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgView;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * Diffs views.
 *
 * @author fordfrog
 */
public class PgDiffViews {

    /**
     * Creates a new instance of PgDiffViews.
     */
    private PgDiffViews() {
    }

    /**
     * Outputs statements for creation of views.
     *
     * @param writer writer the output should be written to
     * @param oldSchema original schema
     * @param newSchema new schema
     * @param searchPathHelper search path helper
     */
    public static void createViews(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        for (final PgView newView : newSchema.getViews()) {
            if (oldSchema == null
                    || !oldSchema.containsView(newView.getName())
                    || isViewModified(
                    oldSchema.getView(newView.getName()), newView)) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(newView.getCreationSQL());
            }
        }
    }

    /**
     * Outputs statements for dropping views.
     *
     * @param writer writer the output should be written to
     * @param oldSchema original schema
     * @param newSchema new schema
     * @param searchPathHelper search path helper
     */
    public static void dropViews(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        if (oldSchema != null) {
            for (final PgView oldView : oldSchema.getViews()) {
                final PgView newView = newSchema.getView(oldView.getName());

                if (newView == null || isViewModified(oldView, newView)) {
                    searchPathHelper.outputSearchPath(writer);
                    writer.println();
                    writer.println(oldView.getDropSQL());
                }
            }
        }
    }

    /**
     * Returns true if either column names or query of the view has
     * been modified.
     *
     * @param oldView old view
     * @param newView new view
     *
     * @return true if view has been modified, otherwise false
     */
    private static boolean isViewModified(final PgView oldView,
            final PgView newView) {
        final String[] oldViewColumnNames;

        if (oldView.getColumnNames() == null
                || oldView.getColumnNames().isEmpty()) {
            oldViewColumnNames = null;
        } else {
            oldViewColumnNames = oldView.getColumnNames().toArray(
                    new String[oldView.getColumnNames().size()]);
        }

        final String[] newViewColumnNames;

        if (newView.getColumnNames() == null
                || newView.getColumnNames().isEmpty()) {
            newViewColumnNames = null;
        } else {
            newViewColumnNames = newView.getColumnNames().toArray(
                    new String[newView.getColumnNames().size()]);
        }

        if (oldViewColumnNames == null && newViewColumnNames == null) {
            return !oldView.getQuery().trim().equals(newView.getQuery().trim());
        } else {
            return !Arrays.equals(oldViewColumnNames, newViewColumnNames);
        }
    }

    /**
     * Outputs statements for altering view default values.
     *
     * @param writer writer
     * @param oldSchema old schema
     * @param newSchema new schema
     * @param searchPathHelper search path helper
     */
    public static void alterViews(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        if (oldSchema != null) {
            for (final PgView oldView : oldSchema.getViews()) {
                final PgView newView = newSchema.getView(oldView.getName());

                if (oldView != null && newView != null) {
                    diffDefaultValues(
                            writer, oldView, newView, searchPathHelper);
                }
            }
        }
    }

    /**
     * Diffs default values in views.
     *
     * @param writer writer
     * @param oldView old view
     * @param newView new view
     * @param searchPathHelper search path helper
     */
    private static void diffDefaultValues(final PrintWriter writer,
            final PgView oldView, final PgView newView,
            final SearchPathHelper searchPathHelper) {
        final List<PgView.DefaultValue> oldValues =
                oldView.getDefaultValues();
        final List<PgView.DefaultValue> newValues =
                newView.getDefaultValues();

        // modify defaults that are in old view
        for (final PgView.DefaultValue oldValue : oldValues) {
            boolean found = false;

            for (final PgView.DefaultValue newValue : newValues) {
                if (oldValue.getColumnName().equals(newValue.getColumnName())) {
                    found = true;

                    if (!oldValue.getDefaultValue().equals(
                            newValue.getDefaultValue())) {
                        searchPathHelper.outputSearchPath(writer);
                        writer.println();
                        writer.print("ALTER VIEW ");
                        writer.print(
                                PgDiffUtils.getQuotedName(newView.getName()));
                        writer.print(" ALTER COLUMN ");
                        writer.print(PgDiffUtils.getQuotedName(
                                newValue.getColumnName()));
                        writer.print(" SET DEFAULT ");
                        writer.print(newValue.getDefaultValue());
                        writer.println(';');
                    }

                    break;
                }
            }

            if (!found) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.print("ALTER VIEW ");
                writer.print(PgDiffUtils.getQuotedName(newView.getName()));
                writer.print(" ALTER COLUMN ");
                writer.print(PgDiffUtils.getQuotedName(
                        oldValue.getColumnName()));
                writer.println(" DROP DEFAULT;");
            }
        }

        // add new defaults
        for (final PgView.DefaultValue newValue : newValues) {
            boolean found = false;

            for (final PgView.DefaultValue oldValue : oldValues) {
                if (newValue.getColumnName().equals(oldValue.getColumnName())) {
                    found = true;
                    break;
                }
            }

            if (found) {
                continue;
            }

            searchPathHelper.outputSearchPath(writer);
            writer.println();
            writer.print("ALTER VIEW ");
            writer.print(PgDiffUtils.getQuotedName(newView.getName()));
            writer.print(" ALTER COLUMN ");
            writer.print(PgDiffUtils.getQuotedName(newValue.getColumnName()));
            writer.print(" SET DEFAULT ");
            writer.print(newValue.getDefaultValue());
            writer.println(';');
        }
    }
}
