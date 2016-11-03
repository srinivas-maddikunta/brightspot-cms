Background Tasks
----------------

Through Dari, Brightspot provides an easy-to-use task system for creating and monitoring background tasks. To create a new task, subclass ``com.psddev.dari.util.Task`` and implement ``doTask()`` method on the object. Since tasks typically loop over data and process it, the best practice is to call ``shouldContinue()`` on each iteration of the loop to determine if the task has been stopped or paused by the Task Manager interface.

Alternatively, create an anonymous class implementation of Task:

.. code-block:: java

    Task task = new Task("Migration", "Migration Blog Data") {
        @Override
        public void doTask() throws Exception {
            boolean done = false;
            while(!done && shouldContinue()) {
                // Do processing here
            }
        }
    }

    task.start();
    
This task will show up under the Migration group of the Task Manager interface and will be called Migration Blog Data. The Task Manager displays all running tasks on the server and provides methods for starting and stopping them.