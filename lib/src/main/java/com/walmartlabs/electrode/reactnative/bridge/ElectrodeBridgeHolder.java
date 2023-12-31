/*
 * Copyright 2017 WalmartLabs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.walmartlabs.electrode.reactnative.bridge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Client facing class.
 * Facade to ElectrodeBridgeTransceiver.
 * Handles queuing every method calls until react native is ready.
 */
public final class ElectrodeBridgeHolder {

    private static final String TAG = ElectrodeBridgeHolder.class.getSimpleName();

    private static boolean isReactNativeReady;

    private static ElectrodeNativeBridge electrodeNativeBridge;

    // We queue requests/events as long as react native initialization is not complete.
    // Indeed, if a client of the bridge calls `sendRequest` upon it's application start,
    // it will throw an exception due to the fact that react native initialization is not
    // complete (react native bridge not ready). RN initialization is asynchronous.
    // Doing this greatly simplifies things for the electrode bridge client as he does not
    // have to bother with burdensome code to wait for RN to be ready. We take care of that !
    // This solution does not really scale in the sense that if the user sends a 1000 requests
    // upon native app start, it can become problematic. But I don't see why a user would do that
    // unless it's a bug in its app
    static final HashMap<String, RequestHandlerPlaceholder> mQueuedRequestHandlersRegistration = new HashMap<>();
    static final HashMap<String, List<EventListenerPlaceholder>> mQueuedEventListenersRegistration = new HashMap<>();
    static final HashMap<ElectrodeBridgeRequest, ElectrodeBridgeResponseListener<ElectrodeBridgeResponse>> mQueuedRequests = new HashMap<>();
    static final List<ElectrodeBridgeEvent> mQueuedEvents = new ArrayList<>();

    static {
        ElectrodeBridgeTransceiver.registerReactNativeReadyListener(new ElectrodeBridgeTransceiver.ReactNativeReadyListener() {
            @Override
            public void onReactNativeReady() {
                isReactNativeReady = true;
                electrodeNativeBridge = ElectrodeBridgeTransceiver.instance();
                registerQueuedEventListeners();
                registerQueuedRequestHandlers();
                sendQueuedRequests();
                emitQueuedEvents();
            }
        });

    }

    /**
     * Emits an event with some data to the JS react native side
     *
     * @param event The event to emit
     */
    @SuppressWarnings("unused")
    public static void emitEvent(@NonNull ElectrodeBridgeEvent event) {
        if (!isReactNativeReady) {
            Logger.d(TAG, "Queuing event. Will emit once react native initialization is complete.");
            mQueuedEvents.add(event);
            return;
        }

        electrodeNativeBridge.sendEvent(event);
    }

    /**
     * Sends a request
     *
     * @param request          The request to send
     * @param responseListener Listener to be called upon request completion
     */
    @SuppressWarnings("unused")
    public static void sendRequest(
            @NonNull ElectrodeBridgeRequest request,
            @NonNull final ElectrodeBridgeResponseListener<ElectrodeBridgeResponse> responseListener) {
        if (!isReactNativeReady) {
            Logger.d(TAG, "Queuing request(%s). Will send once react native initialization is complete.", request);
            mQueuedRequests.put(request, responseListener);
            return;
        }

        electrodeNativeBridge.sendRequest(request, responseListener);
    }

    /**
     * Registers a request handler
     *
     * @param name           The request name this handler can handle
     * @param requestHandler The request handler instance
     * @return {@link UUID} of the {@code requestHandler}
     */
    public static UUID registerRequestHandler(@NonNull String name,
                                              @NonNull ElectrodeBridgeRequestHandler<ElectrodeBridgeRequest, Object> requestHandler) {
        UUID handlerUUID = UUID.randomUUID();
        if (!isReactNativeReady) {
            Logger.d(TAG, "Queuing request handler registration for request(name=%s). Will register once react native initialization is complete.", name);
            mQueuedRequestHandlersRegistration.put(name, new RequestHandlerPlaceholder(handlerUUID, requestHandler));
            return handlerUUID;
        }

        electrodeNativeBridge.registerRequestHandler(name, requestHandler, handlerUUID);
        return handlerUUID;
    }

    /**
     * Registers an event listener
     *
     * @param name          The event name this listener is interested in
     * @param eventListener The event listener
     * @return {@link UUID} of the {@code eventListener}
     */
    public static UUID addEventListener(@NonNull String name,
                                        @NonNull ElectrodeBridgeEventListener<ElectrodeBridgeEvent> eventListener) {
        UUID eventUUID = UUID.randomUUID();
        if (!isReactNativeReady) {
            Logger.d(TAG, "Queuing event handler registration for event(name=%s). Will register once react native initialization is complete.", name);
            List<EventListenerPlaceholder> placeholderList = mQueuedEventListenersRegistration.get(name);
            if (placeholderList == null) {
                placeholderList = new ArrayList<>();
                mQueuedEventListenersRegistration.put(name, placeholderList);
            }
            placeholderList.add(new EventListenerPlaceholder(eventUUID, eventListener));
            return eventUUID;
        }

        electrodeNativeBridge.addEventListener(name, eventListener, eventUUID);
        return eventUUID;
    }

    public static void addConstantsProvider(@NonNull ConstantsProvider constantsProvider) {
        ElectrodeBridgeTransceiver.addConstantsProvider(constantsProvider);
    }

    /**
     * Remove the event listener
     *
     * @param eventListenerUuid {@link UUID}
     * @return
     */
    @Nullable
    public static ElectrodeBridgeEventListener<ElectrodeBridgeEvent> removeEventListener(@NonNull UUID eventListenerUuid) {
        if (!isReactNativeReady) {
            ElectrodeBridgeEventListener<ElectrodeBridgeEvent> eventListener = null;
            synchronized (mQueuedEventListenersRegistration) {
                String key = null;
                boolean shouldRemove = false;
                for (Map.Entry<String, List<EventListenerPlaceholder>> entry : mQueuedEventListenersRegistration.entrySet()) {
                    List<EventListenerPlaceholder> placeholderList = entry.getValue();
                    EventListenerPlaceholder matchingPlaceHolder = null;
                    for (EventListenerPlaceholder placeholder : placeholderList) {
                        if (eventListenerUuid == placeholder.getUUID()) {
                            key = entry.getKey();
                            eventListener = placeholder.getEventListener();
                            matchingPlaceHolder = placeholder;
                            break;
                        }
                    }
                    if (matchingPlaceHolder != null) {
                        placeholderList.remove(matchingPlaceHolder);
                        if (placeholderList.size() == 0) {
                            shouldRemove = true;
                        }
                        break;
                    }
                }
                if (shouldRemove) {
                    mQueuedEventListenersRegistration.remove(key);
                }
            }
            return eventListener;
        } else {
            return electrodeNativeBridge.removeEventListener(eventListenerUuid);
        }
    }

    /**
     * Unregisters a request handler
     *
     * @param requestHandlerUuid {@link UUID} of registerRequestHandler
     * @return registerRequestHandler unregistered
     */
    public static ElectrodeBridgeRequestHandler<ElectrodeBridgeRequest, Object> unregisterRequestHandler(@NonNull UUID requestHandlerUuid) {
        if (!isReactNativeReady) {
            ElectrodeBridgeRequestHandler<ElectrodeBridgeRequest, Object> requestHandler = null;
            synchronized (mQueuedRequestHandlersRegistration) {
                String eventName = null;
                for (Map.Entry<String, RequestHandlerPlaceholder> entry : mQueuedRequestHandlersRegistration.entrySet()) {
                    if (entry.getValue().getUUID() == requestHandlerUuid) {
                        eventName = entry.getKey();
                        requestHandler = entry.getValue().getRequestHandler();
                        break;
                    }
                }
                if (eventName != null) {
                    mQueuedRequestHandlersRegistration.remove(eventName);
                }
            }
            return requestHandler;
        } else {
            return electrodeNativeBridge.unregisterRequestHandler(requestHandlerUuid);
        }
    }

    public static boolean isRegistered(@NonNull UUID requestHandlerUuid) {
        if (!isReactNativeReady) {
            synchronized (mQueuedRequestHandlersRegistration) {
                for (Map.Entry<String, RequestHandlerPlaceholder> entry : mQueuedRequestHandlersRegistration.entrySet()) {
                    if (entry.getValue().getUUID() == requestHandlerUuid) {
                        return true;
                    }
                }
            }
        } else {
            return electrodeNativeBridge.isRegistered(requestHandlerUuid);
        }
        return false;
    }

    private static void registerQueuedRequestHandlers() {
        for (Map.Entry<String, RequestHandlerPlaceholder> entry : mQueuedRequestHandlersRegistration.entrySet()) {
            electrodeNativeBridge.registerRequestHandler(
                    entry.getKey(),
                    entry.getValue().getRequestHandler(),
                    entry.getValue().getUUID());
        }
        mQueuedRequestHandlersRegistration.clear();
    }

    private static void registerQueuedEventListeners() {
        for (Map.Entry<String, List<EventListenerPlaceholder>> entry : mQueuedEventListenersRegistration.entrySet()) {
            List<EventListenerPlaceholder> placeholderList = entry.getValue();
            for (EventListenerPlaceholder placeholder : placeholderList) {
                electrodeNativeBridge.addEventListener(
                        entry.getKey(),
                        placeholder.getEventListener(),
                        placeholder.getUUID());
            }

        }
        mQueuedEventListenersRegistration.clear();
    }

    private static void sendQueuedRequests() {
        for (Map.Entry<ElectrodeBridgeRequest, ElectrodeBridgeResponseListener<ElectrodeBridgeResponse>> entry : mQueuedRequests.entrySet()) {
            electrodeNativeBridge.sendRequest(entry.getKey(), entry.getValue());
        }
        mQueuedRequests.clear();
    }

    private static void emitQueuedEvents() {
        for (ElectrodeBridgeEvent event : mQueuedEvents) {
            electrodeNativeBridge.sendEvent(event);
        }
        mQueuedEvents.clear();
    }
}
