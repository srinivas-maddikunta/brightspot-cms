Automatically Creating Permalinks
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Permalinks are the links external readers use to access a published web page. Brightspot includes an interface for creating permalinks automatically.

#. Returning to the IDE, update the file Article.java with the following:

.. literalinclude:: ../../../tutorial/snippet/tutorial/article/permalinks/Step1.snippet
   :language: java
   :emphasize-lines: 4,6,10,13,37-40
   :linenos:

2. Refresh the web page running at localhost:9480. A permalink appears in the URLs widget.

.. image:: article/images/permalink.png

3. View your article at http://localhost:9480/crippled-by-earths-gravity-aliens-demand-submission.

.. image:: article/images/published.png

In the previous listing, the ``createPermalink`` method in line 38 generates the permalink. For more information about the permalink interface, see `Interface Directory.Item <https://artifactory.psdops.com/psddev-releases/com/psddev/cms/3.2.6504-ad4fbd/cms-3.2.6504-ad4fbd-javadoc.jar!/com/psddev/cms/db/Directory.Item.html>`_. 
