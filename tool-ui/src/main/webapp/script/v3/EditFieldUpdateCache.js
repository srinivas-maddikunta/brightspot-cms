define(['jquery'], function($) {

    var VIEWERS_CACHE = (function() {

        var viewerDataCache = { },
            hitCount = 0,
            missCount = 0,
            fetchCount = 0,
            putCount = 0,
            cleanCallCount = 0,

            debugViewersCache = function() {
                return window.LOG_VIEWERS_REPORTS && typeof console !== "undefined";
            },

            report = function(force) {

                if (debugViewersCache() && !force && !((putCount + fetchCount) % 15 === 0)) {
                    return;
                }

                var total = hitCount + missCount,
                    ratio = (total === 0 && hitCount === 0) ? 0.0 : (total === 0 ? 1.0 : (hitCount === 0 ? 0.0 : hitCount / total));

                ratio *= 100;

                console.log(
                    "putCount: ", putCount,
                    ", fetchCount: ", fetchCount,
                    ", ratio: ", ratio + "%",
                    "size: ", Object.keys(viewerDataCache).length
                );
            },

        // caches the specified viewer data in the specified cache object,
        // keyed by contentId then userId.
            cacheData = function(cache, data) {

                putCount += 1;

                var contentId = data.contentId,
                    userId = data.userId,
                    contentData,
                    userDataIndex = undefined,
                    i;

                contentData = cache[contentId];

                if (contentData === undefined) {
                    contentData = [ ];
                    cache[contentId] = contentData;
                }

                for (i = 0; i < contentData.length; i += 1) {
                    if (contentData[i].userId === userId) {
                        userDataIndex = i;
                    }
                }

                if (userDataIndex !== undefined && userDataIndex >= 0) {
                    contentData.splice(userDataIndex, 1, data);
                } else {
                    contentData.push(data);
                }

                report();
            },

        // fetches data from cache
            fetchData = function(contentId) {

                fetchCount += 1;

                report();

                return viewerDataCache[contentId];
            },

            containsKey = function(key) {
                return viewerDataCache[key];
            };

        return {

            putEmpty: function(key) {

                if (!viewerDataCache[key]) {

                    if (debugViewersCache()) {
                        console.log("%cSEED", "color: green", key);
                    }

                    viewerDataCache[key] = [ ];
                }
            },

            put: function(data) {

                if (data && data.contentId) {

                    // only cache data that's existed or been
                    // pre-seeded to ensure that data in the
                    // cache was intentionally placed there
                    // starting with a restore
                    if (containsKey(data.contentId)) {

                        if (debugViewersCache()) {
                            console.log("PUT", data.contentId);
                        }

                        cacheData(viewerDataCache, data);
                    } else {

                        if (debugViewersCache()) {
                            console.log("SKIP", data.contentId);
                        }
                    }
                }
            },

            fetch: function(contentId) {

                var result = fetchData(contentId);

                if (result) {

                    hitCount += 1;
                    if (debugViewersCache()) {
                        console.log("%cCACHE HIT", "color: blue", contentId);
                    }

                } else {

                    missCount += 1;

                    if (debugViewersCache()) {
                        console.log("%cCACHE MISS", "color: red", contentId);
                    }
                }

                return result;
            },

            clearUnused: function() {

                cleanCallCount += 1;

                if (!(cleanCallCount % 20 === 0)) {
                    return;
                }

                if (debugViewersCache()) {
                    console.log("CLEAR");
                }

                // clean out unused cache entries before making call to restore
                var cleanCache = { };

                $('[data-rtc-content-id]').each(function() {
                    var contentId = $(this).attr('data-rtc-content-id'),
                        cachedData = fetchData(contentId);

                    if (cachedData) {
                        cleanCache[contentId] = cachedData;
                    }
                });

                viewerDataCache = cleanCache;
            }
        };
    })();

    window.VIEWERS_CACHE = VIEWERS_CACHE;

    return VIEWERS_CACHE;
});
