package com.psddev.cms.db;

import com.psddev.dari.db.CompoundPredicate;
import com.psddev.dari.db.Predicate;
import com.psddev.dari.db.PredicateParser;
import com.psddev.dari.util.ClassFinder;
import com.psddev.dari.util.TypeDefinition;

import java.util.stream.Collectors;

/**
 * This interface enables defining logic around whether or not a
 * {@link ToolUser} can access this object.
 *
 * <p>An implementation of this interface will warrant {@link ToolEntity}
 * permission configurations via {@link ToolRole} similar to sites, widgets,
 * etc.</p>
 */
public interface PermissionAssignable {

    /**
     * Returns a {@link Predicate} that filters out any objects that are not
     * accessible by this {@code user}.
     *
     * @param user Nonnull.
     * @return Nonnull.
     */
    Predicate itemsPredicate(ToolUser user);

    /**
     * Static utility methods.
     */
    final class Static {

        private Static() { }

        /**
         * Returns a {@link Predicate} that filters out any objects that are
         * not accessible by this {@code user} based on all implementations of
         * {@link PermissionAssignable}.
         *
         * @param user Nullable.
         * @return Nullable.
         */
        public static Predicate itemsPredicate(ToolUser user) {
            if (user == null) {
                return null;
            }

            return new CompoundPredicate(
                    PredicateParser.OR_OPERATOR,
                    ClassFinder.findConcreteClasses(PermissionAssignable.class).stream()
                            .map(clazz -> TypeDefinition.getInstance(clazz).newInstance())
                            .map(object -> object.itemsPredicate(user))
                            .collect(Collectors.toList()));
        }

        /**
         * @return {@code true} if the {@code object} is accessible by the
         * given {@code user}, {@code false}.
         */
        public static boolean isObjectAccessible(ToolUser user, Object object) {
            return !(object instanceof PermissionAssignable)
                    || PredicateParser.Static.evaluate(object, ((PermissionAssignable) object).itemsPredicate(user));
        }
    }
}
