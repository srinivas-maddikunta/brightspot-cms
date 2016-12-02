Batch Commits
-------------

The AsyncDatabaseWriter in Dari is a background task that effectively writes to a database from a queue. It is suited to large tasks that save objects to the database in batches.

For example, a queue of ten thousand Article objects need to be committed to the database every night, so the AsynDatabaseWriter using the commitEventually flag is an efficient and time saving method:

.. code-block:: java

    AsyncDatabaseWriter< Articles > writer = new AsyncDatabaseWriter< Articles >("Article Writer", articlePathToObject.values(), Database.Static.getDefault(), WriteOperation.SAVE, COMMIT_SIZE, true);
        writer.submit();
        
The first argument in the constructor is the executor. If it is null, the default executor is used.

The second argument in the constructor is the queue input. The value can't be null.

The third argument in the constructor is the database to which the objects will be written. The value can't be null.

The fourth argument in the constructor is the WriteOperation to use. The value can't be null.

The fifth argument, COMMIT_SIZE, is the number of items to save in a single commit.

The sixth argument is the commitEventually flag. If it is true, the commitWritesEventually method is used instead of the commitWrites method.