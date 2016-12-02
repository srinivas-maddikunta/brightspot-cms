Constructing URLs
-----------------

Brightspot provides a URL widget in the right rail of the Content Edit view for every content type. The widget allows you to associate a URL with the content you're editing. When a content type has a Renderer.Layout or Renderer.Path attached, accessing the associated URL will render the content.

In Brightspot, URLs are called Directories. Directories are stored as JSON in the content object. Each object can have Permalinks, Aliases, and redirects added in the editorial interface. URLs can be added automatically, based on logic in the class, manually, or both.

::

    "cms.directory.paths" : [ "00000149-638b-de13-a1ed-778bc0ca0000/developer-support-documentation", "00000149-638b-de13-a1ed-778bc0ca0000/docs", "00000149-638b-de13-a1ed-778bc0ca0000/documentation" ],
    "cms.directory.pathTypes" : {
        "00000149-638b-de13-a1ed-778bc0ca0000/developer-support-documentation" : "PERMALINK",
        "00000149-638b-de13-a1ed-778bc0ca0000/docs" : "ALIAS",
        "00000149-638b-de13-a1ed-778bc0ca0000/documentation" : "REDIRECT"
    },
    "cms.directory.pathsMode" : "MANUAL",
    "_id" : "00000149-67ca-d2da-afdd-e7ff931b0000",
    "_type" : "00000149-633b-d010-afeb-6b7bd7240027"
    }

Manual URLs
~~~~~~~~~~~

You can add, remove, or edit manual URLs to one-off pages like a Homepage or Contact Us page.

.. image:: http://cdn.brightspotcms.psdops.com/dims4/default/f9070bc/2147483647/thumbnail/300x90/quality/90/?url=http%3A%2F%2Fd3qqon7jsl4v2v.cloudfront.net%2Fb6%2F71%2F369972534d06a43e393eb66b85a4%2Fscreen-shot-2014-12-06-at-111944-ampng.19.44%20AM.png

Dynamic URLs
~~~~~~~~~~~~

To create URLs dynamically, You can implement Directory.Item in a class to override a createPermalink() method:

.. code-block:: java

    @Renderer.LayoutPath("/render/common/page-container.jsp")
    @Renderer.Path("/render/model/article-object.jsp")
    public class Article extends Content implements Directory.Item {

        private String headline;
        private Author author;
        private ReferentialText bodyText;

        // Getters and Setters

        @Override
        public String createPermalink(Site site) {

            if (this.getHeadline() != null){
                return "/article/" + StringUtils.toNormalized(headline);
            } else {
                return null;
            }    
        }
    }

.. note::

    Implementing the Directory.Item interface tells Brightspot that the content type is a Main Content Type. The designation is reflected in the Search interface and adds the content type to the Common Content widget as a default Create New option.

Content edits may require that a URL should be updated dynamically.

.. code-block:: java

  @Override
  public String createPermalink (Site site) {

  Category category = this.getCategory();
        if (!ObjectUtils.isBlank(category)) {
            return "/article/" + StringUtils.toNormalized(category.getName()) + "/" + StringUtils.toNormalized(headline);
        } else {
            return "/article/" + StringUtils.toNormalized(headline);
        }
    }

URL Options
~~~~~~~~~~~

In addition to a permalink, Brightspot allows you to create aliases or redirects for your content.

Alias
^^^^^

An Alias URL is a new point of access to the page, allowing you to give your content a shorter, custom or vanity URL. For example, /spring-promo will return the same page as /promotional-offers-this-spring. The address bar for the user will show /spring-promo but the /promotional-offers-this-spring page will be displayed.

Redirect
^^^^^^^^

A Redirect URL will direct users to the permalink for the page. You can add a when content moves or is removed and an existing URL needs to be redirected to new content.