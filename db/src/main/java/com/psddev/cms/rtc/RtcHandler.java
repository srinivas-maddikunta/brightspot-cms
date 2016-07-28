package com.psddev.cms.rtc;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.psddev.cms.db.ToolUser;
import com.psddev.cms.tool.AuthenticationFilter;
import com.psddev.dari.db.Query;
import com.psddev.dari.util.IoUtils;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.TypeDefinition;
import com.psddev.dari.util.UuidUtils;
import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter;
import org.atmosphere.cpr.AtmosphereResourceFactory;
import org.atmosphere.handler.AbstractReflectorAtmosphereHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class RtcHandler extends AbstractReflectorAtmosphereHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RtcHandler.class);

    private ScheduledExecutorService executor;

    private final Cache<UUID, Long> lastPings = CacheBuilder
            .newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .build();

    private final LoadingCache<UUID, Optional<UUID>> userIds = CacheBuilder
            .newBuilder()
            .maximumSize(1000L)
            .build(new CacheLoader<UUID, Optional<UUID>>() {

                @Override
                @ParametersAreNonnullByDefault
                public Optional<UUID> load(UUID sessionId) throws Exception {
                    RtcSession session = Query
                            .from(RtcSession.class)
                            .where("_id = ?", sessionId)
                            .first();

                    return session != null
                            ? Optional.of(session.getUserId())
                            : Optional.empty();
                }
            });

    private final AtmosphereResourceEventListener disconnectListener = new AtmosphereResourceEventListenerAdapter.OnDisconnect() {

        @Override
        @SuppressWarnings("unchecked")
        public void onDisconnect(AtmosphereResourceEvent event) {
            disconnectSession(createSessionId(event.getResource()));
        }
    };

    private UUID createSessionId(AtmosphereResource resource) {
        String resourceUuid = resource.uuid();
        UUID sessionId = ObjectUtils.to(UUID.class, resourceUuid);

        if (sessionId == null) {
            sessionId = UuidUtils.createVersion3Uuid(resourceUuid);
        }

        return sessionId;
    }

    private void disconnectSession(UUID sessionId) {
        if (sessionId == null) {
            return;
        }

        lastPings.invalidate(sessionId);
        userIds.invalidate(sessionId);

        RtcSession session = Query
                .from(RtcSession.class)
                .where("_id = ?", sessionId)
                .first();

        if (session != null) {
            session.disconnect();
        }
    }

    @Override
    public void init(AtmosphereConfig config) throws ServletException {
        executor = Executors.newSingleThreadScheduledExecutor();

        // Clean up any sessions that haven't been pinged in a while.
        executor.scheduleWithFixedDelay(() -> {
            AtmosphereResourceFactory factory = config.resourcesFactory();
            long now = System.currentTimeMillis();

            for (AtmosphereResource resource : factory.findAll()) {
                try {
                    UUID sessionId = createSessionId(resource);
                    Long lastPing = lastPings.getIfPresent(sessionId);

                    if (lastPing == null || now - lastPing > 30000) {
                        disconnectSession(sessionId);
                        resource.removeFromAllBroadcasters();
                        factory.unRegisterUuidForFindCandidate(resource);
                    }

                } catch (Exception error) {
                    LOGGER.debug(
                            String.format("Can't check resource [%s] for clean-up!", resource),
                            error);
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void destroy() {
        if (executor == null) {
            return;
        }

        try {
            executor.shutdown();

            boolean terminated;

            try {
                terminated = executor.awaitTermination(5, TimeUnit.SECONDS);

            } catch (InterruptedException error) {
                terminated = false;
            }

            if (!terminated) {
                executor.shutdownNow();
            }

        } finally {
            executor = null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onRequest(AtmosphereResource resource) throws IOException {
        try {
            AtmosphereRequest request = resource.getRequest();
            String method = request.getMethod();

            if ("get".equalsIgnoreCase(method)) {
                UUID userId = RtcFilter.getUserId(request);

                if (userId == null) {
                    ToolUser user = AuthenticationFilter.Static.getUser(resource.getRequest().wrappedRequest());
                    if (user != null) {
                        userId = user.getId();
                    }
                }

                if (userId == null) {
                    return;
                }

                RtcSession session = new RtcSession();

                session.getState().setId(createSessionId(resource));
                session.setUserId(userId);
                updateLastPing(session);
                session.save();
                resource.addEventListener(disconnectListener);

            } else if ("post".equalsIgnoreCase(method)) {
                try (InputStream requestInput = request.getInputStream()) {
                    String message = IoUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
                    Map<String, Object> messageJson = (Map<String, Object>) ObjectUtils.fromJson(message);
                    String type = (String) messageJson.get("type");

                    if ("disconnect".equals(type)) {
                        disconnectSession(ObjectUtils.to(UUID.class, messageJson.get("sessionId")));
                        return;
                    }

                    UUID sessionId = createSessionId(resource);

                    if ("ping".equals(type)) {
                        RtcSession session = Query
                                .from(RtcSession.class)
                                .where("_id = ?", sessionId)
                                .first();

                        if (session != null) {
                            updateLastPing(session);
                            session.save();
                        }

                        return;
                    }

                    String className = (String) messageJson.get("className");
                    Map<String, Object> data = (Map<String, Object>) messageJson.get("data");
                    UUID userId = userIds.getUnchecked(sessionId).orElse(null);

                    if (userId == null) {
                        return;
                    }

                    switch (type) {
                        case "action" :
                            createInstance(RtcAction.class, className)
                                    .execute(data, userId, sessionId);

                            break;

                        case "state" :
                            RtcState state = createInstance(RtcState.class, className);

                            for (Object event : state.create(data)) {
                                RtcBroadcast.forEachBroadcast(event, (broadcast, broadcastData) ->
                                        writeBroadcast(broadcast, broadcastData, userId, resource));
                            }

                            break;

                        default :
                            throw new UnsupportedOperationException();
                    }
                }
            }

        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    private void updateLastPing(RtcSession session) {
        long ping = System.currentTimeMillis();

        lastPings.put(session.getId(), ping);
        session.setLastPing(ping);
    }

    @Override
    public void onStateChange(AtmosphereResourceEvent event) throws IOException {
        try {
            if (event.isSuspended()) {
                Object message = event.getMessage();

                if (message instanceof RtcBroadcastMessage) {
                    RtcBroadcastMessage broadcastMessage = (RtcBroadcastMessage) message;
                    AtmosphereResource resource = event.getResource();
                    UUID userId = userIds.getUnchecked(createSessionId(resource)).orElse(null);

                    if (userId == null) {
                        return;
                    }

                    writeBroadcast(
                            broadcastMessage.getBroadcast(),
                            broadcastMessage.getData(),
                            userId,
                            resource);
                }
            }

            postStateChange(event);

        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T createInstance(Class<T> returnClass, String className) {
        Class<?> c = ObjectUtils.getClassByName(className);

        if (c == null) {
            throw new IllegalArgumentException(String.format(
                    "[%s] isn't a valid class name!",
                    className));

        } else if (!returnClass.isAssignableFrom(c)) {
            throw new IllegalArgumentException(String.format(
                    "[%s] isn't assignable from [%s]!",
                    returnClass.getName(),
                    c.getName()));
        }

        return (T) TypeDefinition.getInstance(c).newInstance();
    }

    private void writeBroadcast(
            RtcBroadcast<Object> broadcast,
            Map<String, Object> data,
            UUID currentUserId,
            AtmosphereResource resource) {

        if (broadcast.shouldBroadcast(data, currentUserId)) {
            resource.write(ObjectUtils.toJson(ImmutableMap.of(
                    "broadcast", broadcast.getClass().getName(),
                    "data", data)));
        }
    }
}
