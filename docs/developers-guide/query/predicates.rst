Predicates
----------

Basic Comparisons
~~~~~~~~~~~~~~~~~~~~~

*`=, eq`*: The left-hand expression is equal to the right-hand expression.

*`>=, =>`*: The left-hand expression is greater than or equal to the right-hand expression.

*`<=, =<`*: The left-hand expression is less than or equal to the right-hand expression.

*`>`*: The left-hand expression is greater than the right-hand expression.

*`<`*: The left-hand expression is less than the right-hand expression.

*`!=, <>`*: The left-hand expression is not equal to the right-hand expression.


Basic Compound Predicates
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

*`AND, &&`*: Logical AND.

*`OR, ||`*: Logical OR.

*`NOT`*: Logical NOT.

Boolean Predicates
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

*`true`*: A predicate that always evaluates to TRUE.

*`false`*: A predicate that always evaluates to FALSE.

String Comparisons
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

*`startsWith`*: The left-hand expression begins with the right-hand expression.

*`matches`*: The left hand expression matches right-hand expression using a full-text search.

*`contains`*: The use of `matches` is suggested for a large body of text, but `contains` should be used on short text fields such as a name or title.

Other Predicates
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

*`missing`*: The left hand expression is missing.