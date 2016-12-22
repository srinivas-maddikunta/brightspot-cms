Custom Index Tables
-------------------

Dari may need to access data in database tables other than the standard Record tables. For instance, if an external process frequently inserts or updates data directly in your Dari database, and you want to access the fields in that table without copying the data into Dari objects.

First, the database table you wish to access:

::

    CREATE TABLE TeamStatistics (
        id BINARY(16) NOT NULL PRIMARY KEY,
        symbolId INT NOT NULL,
        wins INT NOT NULL,
        losses INT NOT NULL,
        ranking INT NOT NULL
    );

If you are going to query on these fields, create the appropriate indexes on the columns. Dari does not enforce this.

Note that ID must be populated with a Dari Record ID. This can be done in your external process by selecting it from one of the Record index tables using an alternate identifier you know to be unique, such as the team's name.

The symbolId column should be set to the symbolId of the first field in the table, in this case, "wins". This can be retrieved at load time from the Symbol table.

Next, add a Dari Record definition:

.. code-block:: java

    public class Team extends Record {
        @Indexed(unique=true)
        private String name;

        @SqlDatabase.FieldIndexTable(value="TeamStatistics", readOnly=true, sameColumnNames=true, source=true)
        @Indexed(extraFields={"losses", "ranking"})
        private int wins;
        private int losses;
        private int ranking;

        // Getters and Setters

    }

\

.. code-block:: java

    @SqlDatabase.FieldIndexTable(value="TeamStatistics", readOnly=true, sameColumnNames=true, source=true)
    
This is the name of the custom table.

readOnly=true

This tells Dari not to insert or update the values in your custom table. If you have set readOnly=true, when you save() your Record the values will not be persisted to the database, and you will be responsible for updating the tables directly. Otherwise, Dari will handle saving the values to the database when you save(), as usual.

sameColumnNames=true

When we created our table, the column names we used are the same as the field names in the Record. Otherwise, Dari will look for columns named "value", "value2", "value3", in the order that the field names are listed in the extraFields parameter to @Indexed.

source=true

This tells Dari not to look for the values of these columns in the JSON object. Instead, it will query the TeamStatistics table and populate the field values from that table. Otherwise, Dari will use your Custom Field Index Table only when you use one of its fields as a predicate in your query.

.. code-block:: java

   @Indexed(extraFields={"losses", "ranking"})

The annotation is on the "wins" field, so this is telling Dari that these other fields are in the same FieldIndexTable.