Tomcat
------

The most basic context.xml needed to start a Brightspot application in Tomcat contains the following configurations:

.. code-block:: xml

    <!--?xml version='1.0' encoding='utf-8'?-->
    <Context cachingallowed="false">

    <Watchedresource>WEB-INF/web.xml</watchedresource>
    
    <Environment name="dari/defaultDatabase" type="java.lang.String" value="DATABASE_NAME" override="false" />
    <Environment name="dari/database/DATABASE_NAME/class" override="false" type="java.lang.String" value="com.psddev.dari.db.AggregateDatabase" />
    <Environment name="dari/database/DATABASE_NAME/defaultDelegate" override="false" type="java.lang.String" value="sql" />
    <Environment name="dari/database/DATABASE_NAME/delegate/sql/class" override="false" type="java.lang.String" value="com.psddev.dari.db.SqlDatabase" />

    <Resource name="dari/database/DATABASE_NAME/delegate/sql/dataSource" auth="Container" driverclassname="com.mysql.jdbc.Driver" logabandoned="true" maxactive="100" maxidle="30" maxwait="10000" type="javax.sql.DataSource" removeabandoned="true" removeabandonedtimeout="60" username="DATABASE_USER" password="DATABASE_PASS" url="jdbc:mysql://localhost:3306/DATABASE_NAME" testonborrow="true" validationquery="SELECT 1" />

    <Environment name="solr/home" override="false" type="java.lang.String" value="TOMCAT_PATH/solr" />
    <Environment name="dari/database/DATABASE_NAME/delegate/solr/groups" override="false" type="java.lang.String" value="-* +cms.content.searchable" />
    <Environment name="dari/database/DATABASE_NAME/delegate/solr/class" override="false" type="java.lang.String" value="com.psddev.dari.db.SolrDatabase" />
    <Environment name="dari/database/DATABASE_NAME/delegate/solr/serverUrl" override="false" type="java.lang.String" value="http://localhost:8080/solr" />

    <Environment name="dari/defaultStorage" type="java.lang.String" value="local" override="false" />
    <Environment name="dari/storage/local/class" override="false" type="java.lang.String" value="com.psddev.dari.util.LocalStorageItem" />
    <Environment name="dari/storage/local/rootPath" override="false" type="java.lang.String" value="TOMCAT_PATH/webapps/media-files" />
    <Environment name="dari/storage/local/baseUrl" override="false" type="java.lang.String" value="http://localhost:8080/media-files" />

    </Context>

Production Settings
~~~~~~~~~~~~~~~~~~~

If ``isAutoCreateUser`` is enabled in the dev environment, disable it in production by setting its value to false.

::

    <Environment name="cms/tool/isAutoCreateUser" override="true" type="java.lang.Boolean" value="false" />

**Key**: PRODUCTION **Type**: java.lang.Boolean

This key enables or disables production mode. When production mode is enabled, a debugUsername and debugPassword are required to use any debug tools. This also suppresses JSP error messages in the browser. JSP errors will still show up in logs. This value defaults to false.

**Key**: dari/debugUsername **Type:** java.lang.String

The debug interface user name.

**Key**: dari/debugPassword **Type**: java.lang.String

The debug interface password.

**Key**: dari/debugRealm **Type**: java.lang.String

The debug interface realm.

.. code-block:: xml

    <Environment name="PRODUCTION" override="false" type="java.lang.Boolean" value="true" />
    <Environment name="dari/debugRealm" type="java.lang.String" value="DIRECT_HOST_NAME" override="false" />
    <Environment name="dari/debugUsername" type="java.lang.String" value="USERNAME" override="false" />
    <Environment name="dari/debugPassword" type="java.lang.String" value="PASSWORD" override="false" />

Disable Reloader
~~~~~~~~~~~~~~~~

.. code-block:: xml

    <Environment name="dari/disableFilter/com.psddev.dari.util.SourceFilter" value="true" type="java.lang.String" />

Image Editing
~~~~~~~~~~~~~

Specify an image editor to use for cropping and image resizing and editing.

Default, Java Image Editor
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: xml

    <!-- Image Editing -->
    <Environment name="dari/defaultImageEditor" override="false" type="java.lang.String" value="_java" />
    <Environment name="dari/imageEditor/_java/class" override="false" type="java.lang.String" value="com.psddev.dari.util.JavaImageEditor" />

DIMS
^^^^

.. code-block:: xml

    <Environment name="dari/defaultImageEditor" override="false" type="java.lang.String" value="dims" />
    <Environment name="dari/imageEditor/dims/class" override="false" type="java.lang.String" value="com.psddev.dari.util.DimsImageEditor" />
    <Environment name="dari/imageEditor/dims/baseUrl" override="false" type="java.lang.String" value="http://example.com/dims4/APP_ID" />
    <Environment name="dari/imageEditor/dims/sharedSecret" override="false" type="java.lang.String" value="S3cret_H3re" />
    <Environment name="dari/imageEditor/dims/quality" override="false" type="java.lang.Integer" value="90" />

Amazon Storage
^^^^^^^^^^^^^^

.. code-block:: xml

    <Environment name="dari/defaultStorage" type="java.lang.String" value="s3account" override="false" />

    <Environment name="dari/storage/s3account/class" override="false" type="java.lang.String" value="com.psddev.dari.util.AmazonStorageItem" />
    <Environment name="dari/storage/s3account/baseUrl" override="false" type="java.lang.String" value="BASEURL" />
    <Environment name="dari/storage/s3account/secureBaseUrl" override="false" type="java.lang.String" value="SECUREBASEURL" />
    <Environment name="dari/storage/s3account/access" override="false" type="java.lang.String" value="ACCESSKEY" />
    <Environment name="dari/storage/s3account/secret" override="false" type="java.lang.String" value="SECRET" />
    <Environment name="dari/storage/s3account/bucket" override="false" type="java.lang.String" value="BUCKETNAME" />

Cookie Settings
~~~~~~~~~~~~~~~

**Key**: dari/cookieSecret **Type**: java.lang.String

This is used by the com.psddev.dari.util.JspUtils class to implement secure signed cookies. It should a be reasonably long and random string of characters. When added, prevents logout on reload.

.. code-block:: xml

    <Environment name="cookieSecret" type="java.lang.String" value="cookiestring" override="true" />
