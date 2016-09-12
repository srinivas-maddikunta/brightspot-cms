package com.psddev.cms.rtc;

import java.util.Map;
import java.util.UUID;

/**
 * For sending the current state of something back to the client when it
 * first connects to the server.
 *
 * <p>The data can be requested in JavaScript using:</p>
 *
 * <p><blockquote><pre>
 *     define([ 'v3/rtc' ], function(rtc) {
 *         rtc.restore('full.RtcStateClassName', {
 *             parameters: 'for looking up the state'
 *         });
 *     });
 * </pre></blockquote></p>
 *
 * @since 3.1
 */
public interface RtcState {

    /**
     * Returns objects that should be restored based on the given
     * {@code data}.
     *
     * @param data
     *        Can't be {@code null}.
     *
     * @return May be {@code null}.
     */
    Iterable<?> create(Map<String, Object> data);

    /**
     * Returns objects that should be closed based on the given {@code data}
     * and {@code userId}.
     *
     * @param data
     *        Can't be {@code null}.
     *
     * @param userId
     *        Can't be {@code null}.
     *
     * @return May be {@code null}.
     */
    default Iterable<?> close(Map<String, Object> data, UUID userId) {
        return null;
    }
}
