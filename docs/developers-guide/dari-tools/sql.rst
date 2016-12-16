Sql Database
------------

Access to the SQL tables is provided for the `db-sql` tool. Use normal SQL syntax.

.. image:: http://cdn.brightspotcms.psdops.com/dims4/default/9547376/2147483647/resize/700x/quality/90/?url=http%3A%2F%2Fd3qqon7jsl4v2v.cloudfront.net%2F29%2Fd5%2F55465d334d89a8abefbc7b5e916d%2Fscreen-shot-2014-12-06-at-114715-ampng.47.15%20AM.png

Access a database record with the following SQL query:

::

    select hex(id), hex(typeId), data from Record where id = 0xUUID_HERE;

Remove the dashes from the record ID and add ```0x``` in front of it.

View specific columns by selecting the values from a record.

::

    select hex(id), hex(typeId), symbolId, value from RecordString4 where id = 0x00000152cb06daacaf5eeb8e4ce30000;