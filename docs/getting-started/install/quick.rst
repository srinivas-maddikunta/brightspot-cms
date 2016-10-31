******************
Quick Installation
******************

The quick installation of Brightspot uses the Cargo plugin and Tomcat to run the application locally. Installation is a two-step process that requires Java 8 and Maven 3 or higher.

Step 1. Create a Project
========================

Once you have `downloaded and installed Maven <http://maven.apache.org/download.cgi>`_, create a Brightspot project by running the following archetype on the command line.

:: 

    mvn archetype:generate -B \
        -DarchetypeRepository=http://artifactory.psdops.com/public/ \
        -DarchetypeGroupId=com.psddev \
        -DarchetypeArtifactId=cms-app-archetype \
        -DarchetypeVersion=3.2-SNAPSHOT \
        -DgroupId=com.project \
        -DartifactId=projectName

The -DgroupId= and -DartifactId= can be customized.

.. note::

    Windows users must run the archetype on one line without breaks (\).

The default archetype uses a MySQL database.

Step 2. Run the Project
=======================

When you've successfully built a starter project, navigate to the pom.xml file in the root directory.

Run the command mvn -P run clean package cargo:run to download Brightspot in the project and start the application on port 9480 (http://localhost:9480/cms).

.. note::

    Make sure nothing is running on port 9480 before running the final command, and ensure that MySQL is not running when using Cargo.

To continue with your first Brightspot project, see :doc:`../hello-world/all`.