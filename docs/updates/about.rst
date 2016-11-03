#########################
About Brightspot Upgrades
#########################

*******************
Brightspot Releases
*******************

Brightspot receives frequent minor patches and bug fixes. A new patch will be released approximately every six to eight weeks. For the most current version of Brightspot, please visit the `Brightspot GitHub repository <https://github.com/perfectsense/brightspot-cms>`_.

*****************
Brightspot Issues
*****************

Brightspot is an open-source, community-driven project, and we value your feedback and contributions. To report issues you find in Brightspot, please visit the `Brightspot Issues page <https://github.com/perfectsense/brightspot-cms/issues>`_ on GitHub.

**************
How to Upgrade
**************

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