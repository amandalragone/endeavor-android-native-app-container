/*
 * Copyright 2020 Walmart Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ernnavigation.ern.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

import com.walmartlabs.electrode.reactnative.bridge.Bridgeable;

import static com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments.*;

public class NavEventData implements Parcelable, Bridgeable {
    public static final Creator<NavEventData> CREATOR =
            new Creator<NavEventData>() {
                @Override
                public NavEventData createFromParcel(Parcel in) {
                    return new NavEventData(in);
                }

                @Override
                public NavEventData[] newArray(int size) {
                    return new NavEventData[size];
                }
            };

    private String eventType;
    private String viewId;
    private String jsonPayload;

    private NavEventData() {
    }

    private NavEventData(Builder builder) {
        this.eventType = builder.eventType;
        this.viewId = builder.viewId;
        this.jsonPayload = builder.jsonPayload;
    }

    private NavEventData(Parcel in) {
        this(in.readBundle());
    }

    public NavEventData(@NonNull Bundle bundle) {
        if (!bundle.containsKey("eventType")) {
            throw new IllegalArgumentException("eventType property is required");
        }

        this.eventType = bundle.getString("eventType");
        this.viewId = bundle.getString("viewId");
        this.jsonPayload = bundle.getString("jsonPayload");
    }

    /**
     * Type of the event. Supported values [BUTTON_CLICK, DID_FOCUS, DID_BLUR]
     *
     * @return String
     */
    @NonNull
    public String getEventType() {
        return eventType;
    }

    /**
     * MiniApp view identifier to indicate the view instance that is firing the event. This is needed when more than one instance of the same component is mounted.
     *
     * @return String
     */
    @Nullable
    public String getViewId() {
        return viewId;
    }

    /**
     * optional payload associated with the event
     *
     * @return String
     */
    @Nullable
    public String getJsonPayload() {
        return jsonPayload;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(toBundle());
    }

    @NonNull
    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("eventType", this.eventType);
        if (viewId != null) {
            bundle.putString("viewId", this.viewId);
        }
        if (jsonPayload != null) {
            bundle.putString("jsonPayload", this.jsonPayload);
        }
        return bundle;
    }

    @Override
    public String toString() {
        return "{"
                + "eventType:" + (eventType != null ? "\"" + eventType + "\"" : null) + ","
                + "viewId:" + (viewId != null ? "\"" + viewId + "\"" : null) + ","
                + "jsonPayload:" + (jsonPayload != null ? "\"" + jsonPayload + "\"" : null)
                + "}";
    }

    public static class Builder {
        private final String eventType;
        private String viewId;
        private String jsonPayload;

        public Builder(@NonNull String eventType) {
            this.eventType = eventType;
        }

        @NonNull
        public Builder viewId(@Nullable String viewId) {
            this.viewId = viewId;
            return this;
        }

        @NonNull
        public Builder jsonPayload(@Nullable String jsonPayload) {
            this.jsonPayload = jsonPayload;
            return this;
        }

        @NonNull
        public NavEventData build() {
            return new NavEventData(this);
        }
    }
}
