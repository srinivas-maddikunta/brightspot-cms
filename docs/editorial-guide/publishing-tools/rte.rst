Rich Text Editor
----------------

The Rich Text Editor is a robust, CodeMirror-based word processing field used to format text and embed images, videos, and other supported content. Content from a Microsoft Word or Google Docs document can be pasted directly into the Rich Text Editor without losing formatting. Copy and paste handling can be customized by your administrators. Your implementation can include clean-up rules about pasting so you can adjust what is stripped out and what remains. The default cleaner supports Word and Google Doc pasting.

The toolbar at the top of the Rich Text Editor is customizable, but contains the following tools by default.

Text Formatting
~~~~~~~~~~~~~~~

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/51/5e/fd08856c45099ae31ec23ef3f6f7/brightspot-3.1%20Text%20Editor%20Examples.gif

Bold
^^^^ 

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/0d/e5/dd314bcb4f38871cc1a6f4052dc7/screen-shot-2016-04-06-at-1.49.28%20PM.jpg

Toggles text bolding on and off.

Italic
^^^^^^ 

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/a8/d1/30d1822849e18164b777db90a887/screen-shot-2016-04-06-at-1.49.33%20PM.jpg

Toggles italicised text on and off.

Underline
^^^^^^^^^ 

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/eb/21/4167de0d42988d777f1168ae55d9/screen-shot-2016-04-07-at-9.58.44%20AM.jpg

Toggles underlined text on and off.

Strikethrough
^^^^^^^^^^^^^

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/82/6f/8fc44453407aafd560a6b31e9d00/screen-shot-2016-04-06-at-1.49.41%20PM.jpg 

Toggle text strikethrough on and off.

Superscript 
^^^^^^^^^^^

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/c5/35/977685114d98a809d52b0c1e52fa/screen-shot-2016-04-06-at-1.49.45%20PM.jpg

Toggles superscript on and off.

Subscript
^^^^^^^^^

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/71/ad/8a684b7d48998ecae11a8c40315b/screen-shot-2016-04-06-at-1.49.49%20PM.jpg

Toggles subscript on and off.

Link
^^^^

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/4b/e9/a94a339a4ffa9b349899fc6177c6/screen-shot-2016-04-06-at-1.49.53%20PM.jpg

Adds a link to any line of text. Highlight the desired text and click the  icon in the Enhancement section of the toolbar. You will be prompted to specify a URL for an external link or, if linking to a piece of internal content, you can click the Search icon and choose an asset from a Contextual Search window. By default, links will open in the same browser window, but you may elect to force the link to open in a new window instead. To edit an existing link, click the link and choose an option from the small menu that appears below the link.

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/b9/b1/ea23b2b2420183f5be55d145b21a/screen-shot-2016-02-16-at-11.58.08%20AM.jpg

Click "Edit" to change the destination URL for the link, and click "Clear" to remove the link entirely. You can also edit the link by clicking the link and then clicking the  icon in the toolbar again.

Raw HTML
^^^^^^^^

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/38/1f/b7ae44534755974c9715e70f1a8b/screen-shot-2016-04-06-at-1.49.57%20PM.jpg

By default, the Rich Text Editor ignores HTML tags and renders them as text. To render HTML, highlight the code and click the Raw HTML  button.

Clear Formatting
^^^^^^^^^^^^^^^^ 

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/46/8e/4407afe94566a68c62ea90579b66/screen-shot-2016-04-06-at-1.49.59%20PM.jpg

Removes all formatting from the selected text.

Lists
~~~~~

Bulleted List
^^^^^^^^^^^^^

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/69/8a/de30a1df4a78a962531014b1dfa9/screen-shot-2016-04-06-at-2.34.58%20PM.jpg

Add a bulleted list to your text, or make the selected text a bulleted list.

Numbered List
^^^^^^^^^^^^^

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/c9/42/9fad87024043bded3a0c9239593f/screen-shot-2016-04-06-at-2.35.06%20PM.jpg

Add a numbered list to your text, or make the selected text a numbered list.

Text Alignment
~~~~~~~~~~~~~~

Left Align Text 
^^^^^^^^^^^^^^^

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/c9/ec/34cd4e4f4198af8ce1df7afabd1b/screen-shot-2016-04-06-at-2.44.13%20PM.jpg

Align text to the left.

Center Align Text
^^^^^^^^^^^^^^^^^

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/88/e0/9c484d7d48319a7179820bc6dc12/screen-shot-2016-04-06-at-2.44.19%20PM.jpg 

Align text to the center.

Right Align Text
^^^^^^^^^^^^^^^^

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/fb/f7/2baafb524048a5dd7aed21624d3b/screen-shot-2016-04-06-at-2.44.24%20PM.jpg

Align text to the right.



Tables
~~~~~~

Brightspot 3.2 adds tables to the Rich Text Editor, providing better control for laying out your content.


Adding Tables
^^^^^^^^^^^^^

You can add tables to your content in three general ways:

Create a table directly in the Rich Text Editor: Click the "Table" button in the Rich Text Editor toolbar. The default table is one row high and two columns wide.

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/e7/13/61f879034622ab9b86c4ebaf8184/brightspot-3.2%20Adding%20a%20Table.gif

\

* Copy and paste a table: The Rich Text Editor will accept tables copied from most sources, including Microsoft Excel, Google Sheets, Apple Numbers, and rendered HTML tables.
* Create an HTML table: Write your table in raw HTML in the Rich Text Editor, select your code, and click the "Raw HTML" button, or write it in HTML mode.

Editing Tables
^^^^^^^^^^^^^^

Three buttons in the top left corner of the table allow you to change the location of the table in your content. From left to right, the buttons move the table toward the top of your content, toward the bottom of your content, and remove it from your content entirely.

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/e0/49/13df98ad4bbbbbd06b8bda3f848d/screen-shot-2016-02-16-at-1.38.49%20PM.jpg

To edit the contents of the table, click an empty cell and choose from the following options:

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/86/c9/74ada36b49b6bbd32485be442d58/screen-shot-2016-04-21-at-3.05.52%20PM.jpg

\

* Edit: Change the content of a cell.
* Insert row above: Create a new row above the currently selected cell. The new row will have the same number of columns as the current row.
* Insert row below: Create a new row below the currently selected cell. The new row will have the same number of columns as the current row.
* Insert column on the left: Create a new column to the left of the currently selected cell. The new column will have the same number of rows as the current column.
* Insert column on the right: Create a new column to the right of the currently selected cell. The new column will have the same number of rows as the current column.
* Remove row: Deletes the row containing the selected cell from the table. All content in the row will be lost.
* Remove column: Deletes the column containing the selected cell from the table. All content in the column will be lost.
* Clear Cells: Removes the contents of the current cell from the table.
* Undo: Reverts the last change made to the table.
* Redo: Recommits the last change made to the table.

All features of the Brightspot Rich Text Editor are available in tables, including links, enhancements, text formatting, and change tracking.

Additions
~~~~~~~~~

Add Block Enhancement
^^^^^^^^^^^^^^^^^^^^^

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/9e/74/9ba9146344eda23a87ec83048c06/screen-shot-2016-04-06-at-2.56.39%20PM.jpg

Opens a Contextual Search window from which you can embed links, images, videos, and other supported content. You can choose pre-existing content or create new content from the Contextual Search window. Move the enhancement to the left, right side of the page using the arrows in the Placement section of the enhancement toolbar. Use the up and down arrows to move the enhancement toward the top or bottom of the page. Text will automatically wrap around the enhancement, or you can click the intersecting arrows icon to expand the asset to the full width of the page. To remove an enhancement from your content, click the red "Remove" button. To delete an enhancement entirely, click "Remove Completely." Enhancements with media attached will be previewed in the Rich Text Editor. Because Brightspot is a flexible platform, administrators can also add new enhancement types.

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/ba/ee/60fdcc094706980c433cc603d0c9/brightspot-3.1%20Rich%20Text%20Editor%20Enhancement.jpg

Add Marker
^^^^^^^^^^

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/fe/55/7cabd30547cf9df53dc4e4b83440/screen-shot-2016-04-06-at-2.56.46%20PM.jpg

Add to denote breaks in a body of text like truncation, a "Read More" link, or a page break.

Add Table
^^^^^^^^^

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/53/b8/1faef2a0443ead98db0a75115860/screen-shot-2016-04-06-at-2.56.52%20PM.jpg

Creates an editable table in the text field. For more information, please see the `Tables chapter <http://www.brightspot.com/docs/3.2/editorial-support/tables>`_.

Change Tracking
~~~~~~~~~~~~~~~

Change tracking allows you to review edits to your content before you commit them. Deleted content will be highlighted in red, and additional content will be highlighted in green.

Track changes
^^^^^^^^^^^^^

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/80/87/2074a9f342a3a8ec7daff7ce136b/screen-shot-2016-04-06-at-3.30.09%20PM.jpg

Toggles change tracking on and off. When you stop tracking, any previously tracked changes will remain marked until you have accepted or rejected them.

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/68/6e/905a68d34e32b850e70f39ee8af6/screen-shot-2016-02-15-at-9.14.29%20AM.jpg

Accept Change
^^^^^^^^^^^^^

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/52/4d/4d05ee81421486ee8898544a60d4/screen-shot-2016-04-06-at-3.30.30%20PM.jpg

Accepts edits. To accept or reject a single change, place your cursor anywhere in the red or green highlighted area; to accept or reject multiple changes at once, select the text containing the relevant changes and click  or .

Reject Change
^^^^^^^^^^^^^ 

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/8a/9e/c73fb5fb45dd94a0333bcedd785c/screen-shot-2016-04-06-at-3.30.25%20PM.jpg

Rejects edits. To accept or reject a single change, place your cursor anywhere in the red or green highlighted area; to accept or reject multiple changes at once, select the text containing the relevant changes and click  or .

Toggle Preview
^^^^^^^^^^^^^^ 

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/ca/7c/1546f2d24b9eaa09ca6bb03d5af4/screen-shot-2016-04-06-at-3.30.15%20PM.jpg

Preview the content as it would appear with all changes committed; click it again to return to the content with changes tracked. Viewing your edited content in preview mode does not commit the changes.

Commenting
~~~~~~~~~~

Add Comment
^^^^^^^^^^^ 

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/2f/4b/3325f1464a2bb138a418b1e62d9b/screen-shot-2016-04-06-at-3.30.37%20PM.jpg

Create a new comment at the cursor's location.

Remove Comment
^^^^^^^^^^^^^^ 

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/8a/9e/c73fb5fb45dd94a0333bcedd785c/screen-shot-2016-04-06-at-3.30.25%20PM.jpg

Remove a comment permanently.

Toggle Comment Collapse
^^^^^^^^^^^^^^^^^^^^^^^

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/1f/ef/9f2b3c1140bb87689343b5ec0e04/screen-shot-2016-04-06-at-3.30.47%20PM.jpg 

Show or hide all comments.

Views
~~~~~

Toggle Fullscreen Editing
^^^^^^^^^^^^^^^^^^^^^^^^^ 

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/da/6d/1f2cf1924ca4b5311edc608a9e35/screen-shot-2016-04-06-at-3.30.53%20PM.jpg

Expands the Text Editor to fill the screen.

Toggle HTML Mode 
^^^^^^^^^^^^^^^^

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/d1/77/162273c54d6b9e8125151105bfe8/screen-shot-2016-04-06-at-3.30.58%20PM.jpg

Changes the Rich Text Editor into HTML mode. In HTML mode, your content displays with visible HTML code to help you track down an error or add some custom formatting. Click Toggle HTML Mode again to return to the Rich Text Editor with all changes intact.

Find & Replace
~~~~~~~~~~~~~~

Text fields support the following Find and Replace commands:

Find: Cmd-F (OS X), Ctrl-F (Windows)

Find Next: Cmd-G (OS X), Ctrl-G (Windows)

Find Previous: Shift-Cmd-G (OS X), Shift-Ctrl-G (Windows)

Replace: Cmd-Option-F (OS X), Shift-Ctrl-F (Windows)

Replace All: Shift-Cmd-Option-F (OS X), Shift-Ctrl-R (Windows)

Persistent Search: Alt-F, Enter to find next, Shift-Enter to find previous

Jump to Line: Alt-G
