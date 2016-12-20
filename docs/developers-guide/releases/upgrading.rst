********************
Upgrading Brightspot
********************

Brightspot upgrades are managed by updating the version dependencies in the Maven project. Open the project's pom.xml and update the version of the following:

.. code-block:: xml

    <parent>
        <groupId>com.psddev</groupId>
        <artifactId>dari-parent</artifactId>
        <version>3.2-SNAPSHOT</version>
    </parent>

        <dependency>
            <groupId>com.psddev</groupId>
            <artifactId>cms-db</artifactId>
            <version>3.2-SNAPSHOT</version>
        </dependency>

        <dependency>
            <groupId>com.psddev</groupId>
            <artifactId>cms-tool-ui</artifactId>
            <version>3.2-SNAPSHOT</version>
            <type>war</type>
        </dependency>

        <dependency>
            <groupId>com.psddev</groupId>
            <artifactId>dari-db</artifactId>
            <version>3.2-SNAPSHOT</version>
        </dependency>

        <dependency>
            <groupId>com.psddev</groupId>
            <artifactId>dari-util</artifactId>
            <version>3.2-SNAPSHOT</version>
        </dependency>

Recompile the project and redeploy the created WAR file.

The latest release can be found on `Brightspot GitHub releases <https://github.com/perfectsense/brightspot-cms/releases>`_.