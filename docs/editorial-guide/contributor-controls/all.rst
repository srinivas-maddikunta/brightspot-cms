Contributor Controls
====================

A wide range of limits and controls are available for different contributor roles in Brightspot. To assign a new role, go to **Admin > Users and Roles** where you can create new users and assign roles by selecting the appropriate options on the Users widget on the left side of the page.

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/87/e9/06409a814b1f90a2983bd359c6c2/screen-shot-2016-03-29-at-4.32.06%20PM.jpg

Assign a new role by clicking **New Tool Role** in the Roles widget. Here, you can edit the contributor controls for the role being created.

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/d1/38/dacffaad4b78bead645642bdc43e/screen-shot-2016-03-29-at-3.15.47%20PM.jpg

Choose a name for the role and edit the permissions that will apply to any user assigned to this role. These permissions are shown in multiple drop down menus:

* Sites
* Areas
* Widgets
* UI
* Tabs
* Types

Each drop-down menu offers three options: All, Some, or No.

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/13/99/2703d2744e5ab6ca75293169809e/screen-shot-2016-03-29-at-3.18.17%20PM.jpg

Sites
-----

The All Sites drop-down refers to the Multi-Site feature of Brightspot. If the Brightspot implementation has multiple sites, the All Sites drop-down menu controls which of the sites a user role can access. For example, a Spanish-speaking role might be limited to the Spanish and English sites, not French and German sites. If access to one or more sites is prohibited for a role, users with that role cannot access Global because it encompasses all sites in that Brightspot implementation.

.. image:: http://cdn.brightspotcms.psdops.com/dims4/default/da33665/2147483647/resize/380x/quality/90/?url=http%3A%2F%2Fd3qqon7jsl4v2v.cloudfront.net%2Fdc%2F65%2Fa8c427954a6088e00656c8aeb8bc%2Fscreen-shot-2014-12-05-at-120127-pmpng.44.35.png

Areas
-----

The All Areas drop-down contains three sub-categories: Content, Crosslinker, and Admin.

* Content: Covers the Dashboard and User Generated Content (UCG). Assign access to either view or both views, including all content created internally and externally.
* Crosslinker If the Crosslinker plugin has been installed, this category allows users to limit access to different features of Crosslinker, including Dictionaries, Dictionary Import, Simulations, Term Finder, Term Mappings, and Terms. If you choose "No Crosslinker," the Crosslinker tab will not appear in that role's Brightspot interface. If the Crosslinker plugin has not been installed, this section will not apply.
* Admin Controls access to all content in the Admin tab. If you permit access to all Admin, users with that role will see the full Admin tab in their Brightspot interface, including all Admin capabilities: Content Types, Production Guides, Settings, Sites, Social, Users & Roles, Variations & Profiles, and Workflows). You can choose which of these Admin features to enable depending on the role or disable Admin completely, which removes the tab.

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/7b/d8/36d7caf94759b4e553b1f5ac3ab9/screen-shot-2016-03-29-at-3.21.43%20PM.jpg

Widgets
-------

The All Widgets drop-down controls which Dashboard and Content Edit widgets are available to a role.

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/09/6b/90b4a04244fe812091e1a10965a4/screen-shot-2016-03-29-at-3.22.59%20PM.jpg

By limiting widgets, you can prevent certain roles from creating new content, scheduling content, setting URLs, and accessing other basic functions.

Types
-----

The All Types drop-down controls the content types associated with your Brightspot implementation, and how roles are permitted to interact with each content type. For each of content type, you can choose to enable all activity or limit what a role can do.

.. image:: http://cdn.brightspotcms.psdops.com/dims4/default/2221de7/2147483647/resize/700x/quality/90/?url=http%3A%2F%2Fd3qqon7jsl4v2v.cloudfront.net%2F44%2Fda%2Fc5f4639649c1843ac693715e4ec5%2Fscreen-shot-2014-12-05-at-120002-pmpng.54.01.png

For example, you could create a role that can read and edit articles but not publish them by selecting "Read" and "Write" but not "Publish."

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/f0/15/017eaaa84453a53fb6a90433e4a2/screen-shot-2016-03-29-at-3.24.54%20PM.jpg

You can also exclude specific fields in a content type. If, for example, the Editor role shouldn't be able to assign Authors to an Article, exclude the Author field from the Article content type by typing it into the "Exclude Fields" text box.

