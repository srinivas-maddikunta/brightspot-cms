Change Log
----------

The Build tool provides access to the build history for an application, showing commits and other information. You can configure external services like GitHub, Hudson and JIRA, allowing you to reference bugs you have fixed and code you have changed.

When fixing a JIRA bug, add a reference to the bug code, EG ABC-123, to the commit message. This is then linked automatically in the Build tool. You can add multiple bug references to a single commit, and all will be linked individually.

**Build Tool Configuration:**

Add the following to a Maven project `POM.xml` file to configure the build tool:

.. code-block:: xml

    <issuemanagement>
        <system>JIRA</system>
        <url>JIRA_URL_HERE</url>
    </issuemanagement>

    <scm>
        <connection>scm:git:ssh://git@github.com/GITHUB_NAME_HERE/REPO_NAME_HERE.git</connection>
        <developerconnection>scm:git:ssh://git@github.com/GITHUB_NAME_HERE/REPO_NAME_HERE.git</developerconnection>
        <url>https://github.com/GITHUB_NAME_HERE/REPO_NAME_HERE</url>
    </scm>