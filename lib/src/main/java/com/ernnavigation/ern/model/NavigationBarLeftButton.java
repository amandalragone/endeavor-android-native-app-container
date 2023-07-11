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

public class NavigationBarLeftButton implements Parcelable, Bridgeable {
    public static final Creator<NavigationBarLeftButton> CREATOR =
            new Creator<NavigationBarLeftButton>() {
                @Override
                public NavigationBarLeftButton createFromParcel(Parcel in) {
                    return new NavigationBarLeftButton(in);
                }

                @Override
                public NavigationBarLeftButton[] newArray(int size) {
                    return new NavigationBarLeftButton[size];
                }
            };

    private String title;
    private String icon;
    private String tint;
    private String id;
    private Boolean disabled;
    private String adaLabel;

    private NavigationBarLeftButton() {
    }

    private NavigationBarLeftButton(Builder builder) {
        this.title = builder.title;
        this.icon = builder.icon;
        this.tint = builder.tint;
        this.id = builder.id;
        this.disabled = builder.disabled;
        this.adaLabel = builder.adaLabel;
    }

    private NavigationBarLeftButton(Parcel in) {
        this(in.readBundle());
    }

    public NavigationBarLeftButton(@NonNull Bundle bundle) {
        this.title = bundle.getString("title");
        this.icon = bundle.getString("icon");
        this.tint = bundle.getString("tint");
        this.id = bundle.getString("id");
        this.disabled = bundle.containsKey("disabled") ? bundle.getBoolean("disabled") : null;
        this.adaLabel = bundle.getString("adaLabel");
    }

    /**
     * Button title if any, applied only for iOS.
     *
     * @return String
     */
    @Nullable
    public String getTitle() {
        return title;
    }

    /**
     * Icon resource identifier that can be used find the icon
     *
     * @return String
     */
    @Nullable
    public String getIcon() {
        return icon;
    }

    /**
     * Specifies a tint for the icon. Supported formats: #RRGGBB, #AARRGGBB. Supported values: red, blue, green, black, white, gray, cyan, magenta, yellow, lightgray, darkgray, grey, lightgrey, darkgrey, aqua, fuchsia, lime, maroon, navy, olive, purple, silver and teal
     *
     * @return String
     */
    @Nullable
    public String getTint() {
        return tint;
    }

    /**
     * Id of the button, this namespace will be used as an identifier when a button click event is emitted. If included, click will not be handled by native instead an event will be fired for react native to handle the backpress.
     *
     * @return String
     */
    @Nullable
    public String getId() {
        return id;
    }

    /**
     * Default to false. If set to true the button will be disabled(non-clickable). Android will remove the left icon indicator
     *
     * @return Boolean
     */
    @Nullable
    public Boolean getDisabled() {
        return disabled;
    }

    /**
     * Accessibility label
     *
     * @return String
     */
    @Nullable
    public String getAdaLabel() {
        return adaLabel;
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
        if (title != null) {
            bundle.putString("title", this.title);
        }
        if (icon != null) {
            bundle.putString("icon", this.icon);
        }
        if (tint != null) {
            bundle.putString("tint", this.tint);
        }
        if (id != null) {
            bundle.putString("id", this.id);
        }
        if (this.disabled != null) {
            bundle.putBoolean("disabled", this.disabled);
        }
        if (adaLabel != null) {
            bundle.putString("adaLabel", this.adaLabel);
        }
        return bundle;
    }

    @Override
    public String toString() {
        return "{"
                + "title:" + (title != null ? "\"" + title + "\"" : null) + ","
                + "icon:" + (icon != null ? "\"" + icon + "\"" : null) + ","
                + "tint:" + (tint != null ? "\"" + tint + "\"" : null) + ","
                + "id:" + (id != null ? "\"" + id + "\"" : null) + ","
                + "disabled:" + disabled + ","
                + "adaLabel:" + (adaLabel != null ? "\"" + adaLabel + "\"" : null)
                + "}";
    }

    public static class Builder {
        private String title;
        private String icon;
        private String tint;
        private String id;
        private Boolean disabled;
        private String adaLabel;

        public Builder() {
        }

        @NonNull
        public Builder title(@Nullable String title) {
            this.title = title;
            return this;
        }

        @NonNull
        public Builder icon(@Nullable String icon) {
            this.icon = icon;
            return this;
        }

        @NonNull
        public Builder tint(@Nullable String tint) {
            this.tint = tint;
            return this;
        }

        @NonNull
        public Builder id(@Nullable String id) {
            this.id = id;
            return this;
        }

        @NonNull
        public Builder disabled(@Nullable Boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        @NonNull
        public Builder adaLabel(@Nullable String adaLabel) {
            this.adaLabel = adaLabel;
            return this;
        }

        @NonNull
        public NavigationBarLeftButton build() {
            return new NavigationBarLeftButton(this);
        }
    }
}
