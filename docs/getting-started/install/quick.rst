##################
Quick Installation
##################

This option should be used for developers looking to experiment with Brightspot locally. The sole dependency for running this project is Java 8.

****************
Step 1. Download
****************

`Download the ZIP <https://github.com/perfectsense/brightspot-demo>`_ for the Brightspot Demo project.

.. note::

    If you have git installed, you can alternatively ``git clone`` this repository.

***************
Step 2. Extract
***************

If you've downloaded the ZIP from Step 1, extract the ZIP in a location of your choice.

***********
Step 3. Run
***********

The Brightspot Demo project contains scripts for OSX/Linux and Windows environments.

OSX / Linux
===========

From the root of the project, execute the ``run.sh`` script from your terminal:

```
    ./run.sh
```

Windows
=======

From the root of the project, execute the ``run.cmd` script from the Command Prompt:

```
    run.cmd
```

After you have executed one of the above scripts, your project will build and deploy using the `Maven Cargo <https://codehaus-cargo.github.io/>`_ plugin. This local environment is comprised of a Tomcat 8 container, and an H2 database.
