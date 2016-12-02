Databases
---------

Using Dari, Brightspot is designed to support multiple storage back ends. It can use several at once. For example, a standard configuration is to store data in MySQL as the source of record, and also store data in Solr for full text search capabilities.

Dari will automatically handle saving to all configured storage back ends when the save() method is called on a record.

Supported databases
~~~~~~~~~~~~~~~~~~~

* MySQL
* PostgreSQL
* Oracle
* Solr
* MongoDB

**SQL-based Storage Backends**

MySQL, PostgreSQL, and Oracle are implemented using the same underlying table structure and indexing strategy.

The storage back end is implemented using seven tables. The primary table, Record, stores the raw information about the saved objects. Objects are serialized to JSON format when they are saved and stored in the data column of the Record table. The rest of the tables are used to implement field indexes.

Unique IDs are created for every object by generating a UUID. The id and typeId fields in the Record table store these UUIDs in the most appropriate datatype for the underlying database. For MySQL and Oracle, this is binary(16). On PostgreSQL, the native UUID is used.

Database Schema resources for Postgres can be found in the Dari GitHub repo.

Indexing
~~~~~~~~

Because records are stored as a JSON text blob, Dari cannot use traditional SQL indexes. To implement indexing, Dari uses several additional tables to store indexes.

When fields of an object are indexed, the field's value and object ID will be stored in the appropriate index table.

Tables
~~~~~~

Record: The primary storage table. All objects are stored in this table as serialized JSON blobs.

RecordLocation2: Stores spatial indexes. Supported on MySQL and PostgreSQL only.

RecordNumber3: Stores number and timestamp indexes.

RecordString4: Stores string and enumeration indexes.

RecordUpdate: Tracks when object were last updated.

RecordUuid3: Stores relationship indexes.

Symbol: Stores symbols such as index names. It is referenced by the other index tables.

There are a number of configuration properties that control Dari at runtime. Optional values will have a reasonable default value. Configure these settings settings.properties or, for a servlet container like Tomcat, in context.xml.

Configuration
~~~~~~~~~~~~~

All database specific configuration parameters are prefixed with dari/database/{databaseName}/.

Key: dari/defaultDatabase Type: java.lang.String

The name of the default database.

Key: dari/database/{databaseName}/class Type: java.lang.String

The classname of a com.psddev.dari.db.Database implementation.

Key: dari/database/{databaseName}/readTimeout Type: java.lang.Double

Sets the read timeout for this database. The default is 3 seconds.

Key: dari/databaseWriteRetryLimit Type: java.lang.Integer

The number of times to retry a transient failure. The default value is 10.

Key: dari/databaseWriteRetryInitialPause Type: java.lang.Integer

The initial amount of time, in milliseconds. to wait before retrying a transient failure. The default value is 10ms.

Key: dari/databaseWriteRetryFinalPause Type: java.lang.Integer

The maximum amount of time, in milliseconds, to wait before retrying a transient failure. The default value is 1000ms.

Key: dari/databaseWriteRetryPauseJitter Type: java.lang.Double

The amount of time to adjust the pause between retries so that multiple threads retrying at the same time will stagger to help break deadlocks in certain databases like MySQL. The default value is 0.5.

The pause value is calculated as initialPause + (finalPause - initialPause) > * i / (limit - 1). This is then jittered + or - pauseJitter percent. For example, if dari/databaseWriteRetryLimit is 10, dari/databaseWriteRetryFinalPause is 1000ms, and dari/databaseWriteRetryPauseJitter is 0.5, then on the first retry, Dari will wait between 5ms and 15ms. On the second try, Dari will wait between 60ms and 180ms continuing until 10th and final try which will wait between 500ms and 1500ms.

**SQL Database Configuration**

Key: dari/database/{name}/class Type: java.lang.String

This should be com.psddev.dari.db.SqlDatabase for all SQL databases.

Key: dari/isCompressSqlData Type: java.lang.Boolean

Enable or disable compression of Dari object data in the database. Dari uses the Snappy compression library for compression. You must include Snappy in your pom.xml file:

.. code-block:: xml

    <dependency>
        <groupId>org.iq80.snappy</groupId>
        <artifactId>snappy</artifactId>
        <version>0.3</version>
    </dependency>

We recommend only enabling compression if you know your dataset is large (over 50GB). The default is false.

Key: dari/database/{databaseName}/jdbcUrl Type: java.lang.String

Key: dari/database/{databaseName}/readJdbcUrl Type: java.lang.String (Optional)

The database jdbc URL. All writes will go the database configured by jdbcUrl. To have reads go to a slave, configure readJbdcUrl.

Key: dari/database/{databaseName}/jdbcUser Type: java.lang.String

Key: dari/database/{databaseName}/readJdbcUser Type: java.lang.String (Optional)

The database user name.

Key: dari/database/{databaseName}/jdbcPassword Type: java.lang.String

Key: dari/database/{databaseName}/readJdbcPassword Type: java.lang.String (Optional)

The database password.

Key: dari/database/{databaseName}/dataSource Type: Resource

Key: dari/database/{databaseName}/readDataSource Type: Resource (Optional)

The database resource. All writes will go the database configured by dataSource. To have reads go to a slave, configure readDataSource.

.. note:: 

    To use Tomcat connection pooling, define a JNDI Resource in context.xml with the name dari/database/{databaseName}/dataSource.

**Aggregate Database Configuration**

Aggregate database is an implemention of com.psddev.dari.db.AbstractDatabase, provided by Dari, that allows objects to be written to and read from multiple database back ends. Typically, this is used to read and write to both MySQL and Solr. This allows normal reads to go to MySQL, while full-text search will use Solr.

Key: dari/database/{databaseName}/defaultDelegate Type: java.lang.String

This is the name of the primary database. It will be written to first and should be considered the source of record for all objects. This is usually one of the SQL backends.

**Example Configuration**

This is an example configuration that reads from a MySQL slave and writes to a MySQL master. Solr is configured to read and write to the same host.

::

    # Aggregate Database Configuration
    dari/defaultDatabase = production
    dari/database/production/defaultDelegate = sql
    dari/database/production/class = com.psddev.dari.db.AggregateDatabase
    dari/database/production/delegate/sql/class = com.psddev.dari.db.SqlDatabase

    # Master Configuration
    dari/database/production/delegate/sql/jdbcUser = username
    dari/database/production/delegate/sql/jdbcPassword = password
    dari/database/production/delegate/sql/jdbcUrl = jdbc:msyql://master.mycompany.com:3306/dari

    # Slave Configuration
    dari/database/production/delegate/sql/readJdbcUser = username
    dari/database/production/delegate/sql/readJdbcPassword = password
    dari/database/production/delegate/sql/readJdbcUrl = jdbc:msyql://slave.mycompany.com:3306/dari

    # Solr Configuration
    dari/database/production/delegate/solr/class = com.psddev.dari.db.SolrDatabase
    dari/database/production/delegate/solr/serverUrl = http://solr.mycompany.com/solr
    
**Solr Database Configuration**

Key: dari/database/{databaseName}/class Type: java.lang.String

This should be com.psddev.dar.db.SolrDatabase for Solr databases.

Key: dari/database/{databaseName}/serverUrl Type: java.lang.String

The URL to the master Solr server.

Key: dari/database/{databaseName}/readServerUrl Type: java.lang.String (Optional)

The URL to slave Solr server.

Key: dari/database/{databaseName}/commitWithin Type: java.lang.Integer

The maximum amount of time, in seconds, to wait before committing to Solr.

Key: dari/database/{databaseName}/saveData Type: java.lang.Boolean

Disable saving of Dari record data (JSON Blob) to Solr. Disabling this will reduce the size of the Solr index at the cost of extra reads to the MySQL database. Only enable this if you have another database configured as the primary.

Key: dari/subQueryResolveLimit Type: java.lang.Integer

Since Solr does not currently support joins, Dari will execute subqueries separately. This limits the size of the results used to prevent generating too large of a query.

