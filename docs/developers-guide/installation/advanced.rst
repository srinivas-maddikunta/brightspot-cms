*********************
Advanced Installation
*********************

To start a Brightspot development project, install the following open-source software required to run Brightspot locally:

* `Java 8 JDK <http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html>`_
* `Maven 3+ <https://maven.apache.org/download.cgi>`_
* `MySQL 5.6.* <http://dev.mysql.com/downloads/>`_
* `Solr 4.8.1 <http://lucene.apache.org/solr/downloads.html>`_
* `Tomcat 8 <https://tomcat.apache.org/download-80.cgi>`_

MySQL
=====

Start by `downloading MySQL <http://dev.mysql.com/downloads/>`_. Run it locally and create a blank database. The database name and user authentication information will be added to the Tomcat context.xml file in the next step.

Tomcat
======

`Download Tomcat 8 <https://tomcat.apache.org/download-80.cgi>`_ and configure it to run Brightspot projects (Core zip version). You can use this as a base for future projects. The recommended best practice is to run an instance of Tomcat alongside each Brightspot project. The context.xml file will contain project-specific settings and point to a project specific database. When running multiple projects locally, you can stop Tomcat or use a different port for each project to run them concurrently:

::

    workspace/
    │
    ├── brightspot-tomcat /  
    │   
    ├── brightspot-project /

Add Solr
========

Solr is used as a text matching database in Brightspot projects. It contains the same data that is stored in the SQL database. `Download Solr <http://archive.apache.org/dist/lucene/solr/>`_ and install it in the default Tomcat.

Place the solr.war file in the Tomcat webapps directory:

cp Solr/example/webapps/solr.war brightspot-tomcat/webapps

Copy the Solr database directory into the Tomcat root directory:

cp -r Solr/example/solr brightspot-tomcat

Two configuration files found in Solr must be updated with Brightspot specific configurations. The files must be named solrconfig.xml and schema.xml.

`Download the Brightspot Solr Config file <https://github.com/perfectsense/dari/tree/master/etc/solr>`_ and replace brightspot-tomcat/solr/collection1/conf/solrconfig.xml.

`Download the Brightspot Solr Schema file <https://github.com/perfectsense/dari/tree/master/etc/solr>`_ and replace brightspot-tomcat/solr/collection1/conf/schema.xml.

Edit the solr.xml file, found in solr/solr.xml. Replace the default host post with the tomcat port <int name="hostPort">9480</int>.

The final step in configuring Solr is to copy the logging jar files from solr/example/lib into brightspot-tomcat/lib, e.g. slf4j-api-1.7.6.jar, slf4j-log4j12-1.7.6.jar.

Add MySQL Connector
===================

`Download <http://dev.mysql.com/downloads/connector/j/>`_ the MySQL Connector jar file for Tomcat and place it in the lib directory:

::

    cp mysql-connector-java-5.*.jar brightspot-tomcat/lib

Add Local Storage Directory
===========================

Brightspot can store uploaded files locally in a media directory. Create this directory in the Tomcat webapps directory:

::

    mkdir brightspot-tomcat/webapps/media

Replace Context.xml
===================

Replace the default context.xml file found in Tomcat with a new file containing the default Brightspot configurations:

`Download the Brightspot context.xml <https://gist.githubusercontent.com/kphenix/54ca0f473ef7e034811a/raw/29acee59ecc2e431cd1bfc46a4bcb049c52e1e8d/default-context-2.4.xml>`_, copy the default context.xml, and replace brightspot-tomcat/conf/context.xml.

Find and replace:

DATABASE_NAME = Find and replace with an empty local MySQL database

DATABASE_USER = Find and replace with username that created the local database

DATABASE_PASS = Find and replace with password that created the local database

TOMCAT_PATH = Find and replace with path to Tomcat

.. note:: By default, Tomcat runs on port 9480, but this value can be updated in the context.xml and brightspot-tomcat/conf/server.xml

   The context.xml file provided above is the most basic version. For full configuration options, see the `Advanced Configuration chapter <http://documentation.brightspot.com/docs/3.0/advanced-configuration/tomcat>`_.

Add Maven
=========

Brightspot projects are created using Maven. `Download <http://maven.apache.org/download.cgi>`_ and install Maven.

Run the following Archetype to create a Brightspot project with defined Brightspot and Dari dependencies in the pom.xml. Run the archetype alongside the configured Tomcat to create the project. The -DgroupId and -DartifactId can be customized.

::

    mvn archetype:generate -B \
        -DarchetypeRepository=http://artifactory.psdops.com/public/ \
        -DarchetypeGroupId=com.psddev \
        -DarchetypeArtifactId=cms-app-archetype \
        -DarchetypeVersion=3.2-SNAPSHOT \
        -DgroupId=com.project \
        -DartifactId=projectName

.. note::

     Windows users will need to run the Archetype on one line, without breaks (\).

Following a successful build, download Brightspot into the project. Navigating into the project alongside the 'pom.xml' and run mvn clean install to generate a target directory and a WAR file.

Copy the generated WAR file from the target directory in the project to the brightspot-tomcat webapps directory and rename it.

::

    cd brightspot-project/target
    mv brightspot-1.0.0-SNAPSHOT.war ../../brightspot-tomcat/webapps/ROOT.war

.. note::

    For information about Brightspot releases or upgrading to a new version, see the `Brightspot Releases <http://www.brightspot.com/docs/3.2/updates/about-brightspot-upgrades>`_.

Start Application Server
========================

With the ROOT.war file added to the brightspot-tomcat/webapps directory, navigate to the Root directory of Tomcat and start the application server:

::

    ./bin/startup.sh or ./bin/startup.bat

The ROOT.war will deploy. Access Brightspot at http://localhost:9480/cms or localhost:8080

.. image:: http://cdn.brightspotcms.psdops.com/dims4/default/005b8d3/2147483647/resize/700x/quality/90/?url=http%3A%2F%2Fd3qqon7jsl4v2v.cloudfront.net%2F11%2Fe5%2Fb65842834f59a705c16c37686d91%2Fscreen-shot-2014-12-03-at-121734-pmpng.40.55%20AM.png

Upgrading Brightspot
====================

To enjoy the features of the most current release of Brightspot, see `Upgrading Brightspot <http://www.brightspot.com/docs/3.2/updates/about-brightspot-upgrades>`_.

FAQ
===

Default Landing
---------------

The default project contains an index.jsp file in the webapp directory. Remove this to replace it with a custom root landing page.

Java Heap Size
--------------

Tomcat memory allocation can be configured. If errors appear in the logs regarding Java Heap Size, add the following directly above the # OS specific support config in the Tomcat catalina.sh file, found at $TOMCAT_HOME/bin/catalina.sh.

::

    # ----- Adding more Memory
    CATALINA_OPTS="-Xmx1024m -XX:MaxPermSize=256M -Djava.awt.headless=true "

Storage
-------

::

    There was an unexpected error!
    java.lang.ClassNotFoundException: org.jets3t.service.ServiceException
    java.lang.NoClassDefFoundError: org/jets3t/service/ServiceException
    com.google.common.util.concurrent.ExecutionError: java.lang.NoClassDefFoundError: org/jets3t/service/ServiceException

If you're using Amazon to upload, add the following dependency to the pom.xml file:

::

    <dependency>
        <groupId>net.java.dev.jets3t</groupId>
        <artifactId>jets3t</artifactId>
        <version>0.8.0</version>
    </dependency>