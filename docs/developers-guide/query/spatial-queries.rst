Spatial Queries
---------------

Dari supports spatial queries on MySQL, PostgreSQL, and Solr. To use Dari's spatial features, define a field of type `com.psddev.dari.db.Location` on the model that will do spatial lookups. This type is a container for latitude and longitude values. This field should be indexed using the `@Index` annotation.

For example:

.. code-block:: java

    public class Venue {
        private String name;
        @Index private Location location;

        // Getters and Setters
    }

To find all venues in a 10 mile radius of the Reston Town Center in Reston, VA, issue the following query:

.. code-block:: java

    double degrees = Region.milesToDegrees(10);
    double y = -77.24234;
    double x = 34.55454;
    PaginatedResult<Venue> venues = Query.from(Venue.class).
        where("location = ?", Region.sphericalCircle(y, x, degrees));

Sorting venues by closest distance works as well:

.. code-block:: java

    double degrees = Region.milesToDegrees(10);
    double y = -77.24234;
    double x = 34.55454;
    PaginatedResult<Venue> venues = Query.from(Venue.class).
        where("location = ?", Region.sphericalCircle(y, x, degrees))).
        sortClosest("location", new Location(y, x));

When using `sortClosest`, limit the results to be inside a given distance with a `WHERE` clause for optimal performance.