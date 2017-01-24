Reindexing
----------

The `@Indexed` annotation is often added to existing objects. To update content that needs to be indexed, use the Db-Bulk Tool. You can re-index on a single type or all types. The process status can be seen in the Task Tool. When importing a new or updated database, Solr will also need to be indexed, which you can do here. 

.. image:: images/db-bulk-tool.png


Copy Solr / SQL
~~~~~~~~~~~~~~~

Use the Copy tool to copy data from one database to another. The copy tool is often used when a new MySQL database is added and the Solr database needs to be created. Copying SQL to Solr populates the Solr database with the SQL data.
