Security
--------

The Brightspot editorial UI typically resides on a separate domain from the website it powers. The separate domain is secured using SSL.

Brightspot editorial access is controlled by a Brightspot-controlled user database.

There are a number of configurations that can be added through the context.xml for specific security requirements:

**Brightspot Access requires login:**

.. code-block:: xml

    <Environment name="cms/tool/isAutoCreateUser" override="true" type="java.lang.Boolean" value="false" />

**Debug Tools require login:**

.. code-block:: xml

    <Environment name="dari/debugRealm" type="java.lang.String" value="HOSTURL" override="false" />
    <Environment name="dari/debugUsername" type="java.lang.String" value="USERNAME" override="false" />
    <Environment name="dari/debugPassword" type="java.lang.String" value="PASSWORD" override="false" />

**Change location/name of login to Brightspot:**

.. code-block:: xml

    <Environment name="dari/routingFilter/applicationPath/cms" override="false" type="java.lang.String" value="cms-custom" />

**Custom Password Policy:**

.. code-block:: xml

    <Environment name="dari/userPasswordPolicy/CUSTOM/class" type="java.lang.String" value="com.your.passwordconfig" override="false" />
    <Environment name="cms/tool/userPasswordPolicy" type="java.lang.String" value="CUSTOM" override="false" />
    <Environment name="dari/authenticationPolicy/CUSTOM/class" type="java.lang.String" value="com.your.passwordconfig" override="false" />
    <Environment name="cms/tool/authenticationPolicy" type="java.lang.String" value="CUSTOM" override="false" />

**Password reuse limit:**

.. code-block:: xml

    <Environment name="CUSTOM/passwordHistoryLimit" type="java.lang.String" value="10" override="false" />

**Rate Limiting:**

.. code-block:: xml

    <Environment name="CUSTOM/loginAttemptLimit" type="java.lang.String" value="10" override="false" />

**Password Expiration:**

.. code-block:: xml

    <Environment name="cms/tool/passwordExpirationInDays" type="java.lang.Long" value="90" override="false" />
    <Environment name="cms/tool/changePasswordTokenExpirationInHours" type="java.lang.Long" value="24" override="false" />

**File upload restrictions:**

.. code-block:: xml

    <Environment name="cms/tool/fileContentTypeGroups" type="java.lang.String" value="-/ +image/ +application/pdf +video/ +application/zip +audio/" override="false" />
    <Environment name="cms/tool/fileContentTypeGroups" type="java.lang.String" value="-/ +image/ +application/pdf +video/ +application/zip +audio/ +application/msword +application/vnd.openxmlformats-officedocument.spreadsheetml.sheet +application/vnd.ms-excel +application/vnd.ms-powerpoi+application/x-photoshop +application/postscript " override="false" />

**Session Timeout:**

.. code-block:: xml

    <Environment name="cms/tool/sessionTimeout" override="false" type="java.lang.Long" value="1800000" />
    <Environment name="dari/databaseWriteRetryFinalPause" override="false" type="java.lang.Integer" value="700" />
    <Environment name="dari/routingFilter/applicationPath/CUSTOM" override="false" type="java.lang.String" value="/" />

**Forgot Password Configs:**

.. code-block:: xml

    <Environment name="cms/tool/forgotPasswordEmailSender" type="java.lang.String" value="you@yoursite.com" override="false" />
    <Environment name="cms/tool/admin/users/disablePasswordChange" type="java.lang.Boolean" value="true" override="false" />

    <Environment name="dari/defaultMailProvider" type="java.lang.String" value="CUSTOM" override="false" />
    <Environment name="dari/mailProvider/CUSTOM/class" type="java.lang.String" value="com.psddev.dari.util.SmtpMailProvider" override="false" />
    <Environment name="dari/mailProvider/CUSTOM/host" type="java.lang.String" value="youremail-smtp.amazonaws.com" override="false" />
    <Environment name="dari/mailProvider/CUSTOM/username" type="java.lang.String" value="USERNAME" override="false" />
    <Environment name="dari/mailProvider/CUSTOM/password" type="java.lang.String" value="PASSWORD" override="false" />
