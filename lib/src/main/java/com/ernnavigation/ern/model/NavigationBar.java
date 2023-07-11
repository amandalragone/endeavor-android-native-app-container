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

public class NavigationBar implements Parcelable, Bridgeable {
    public static final Creator<NavigationBar> CREATOR =
            new Creator<NavigationBar>() {
                @Override
                public NavigationBar createFromParcel(Parcel in) {
                    return new NavigationBar(in);
                }

                @Override
                public NavigationBar[] newArray(int size) {
                    return new NavigationBar[size];
                }
            };

    private String title;
    private Boolean hide;
    private List<NavigationBarButton> buttons;
    private NavigationBarLeftButton leftButton;

    private NavigationBar() {
    }

    private NavigationBar(Builder builder) {
        this.title = builder.title;
        this.hide = builder.hide;
        this.buttons = builder.buttons;
        this.leftButton = builder.leftButton;
    }

    private NavigationBar(Parcel in) {
        this(in.readBundle());
    }

    public NavigationBar(@NonNull Bundle bundle) {
        if (!bundle.containsKey("title")) {
            throw new IllegalArgumentException("title property is required");
        }

        this.title = bundle.getString("title");
        this.hide = bundle.containsKey("hide") ? bundle.getBoolean("hide") : null;
        this.buttons = bundle.containsKey("buttons") ? getList(bundle.getParcelableArray("buttons"), NavigationBarButton.class) : null;
        this.leftButton = bundle.containsKey("leftButton") ? new NavigationBarLeftButton(bundle.getBundle("leftButton")) : null;
    }

    /**
     * Title of Nav Bar
     *
     * @return String
     */
    @NonNull
    public String getTitle() {
        return title;
    }

    /**
     * Use to hide the navigation bar.
     *
     * @return Boolean
     */
    @Nullable
    public Boolean getHide() {
        return hide;
    }

    /**
     * Right button properties
     *
     * @return List<NavigationBarButton>
     */
    @Nullable
    public List<NavigationBarButton> getButtons() {
        return buttons;
    }

    @Nullable
    public NavigationBarLeftButton getLeftButton() {
        return leftButton;
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
        bundle.putString("title", this.title);
        if (this.hide != null) {
            bundle.putBoolean("hide", this.hide);
        }
        if (this.buttons != null) {
            updateBundleWithList(this.buttons, bundle, "buttons");
        }
        if (this.leftButton != null) {
            bundle.putBundle("leftButton", this.leftButton.toBundle());
        }
        return bundle;
    }

    @Override
    public String toString() {
        return "{"
                + "title:" + (title != null ? "\"" + title + "\"" : null) + ","
                + "hide:" + hide + ","
                + "buttons:" + (buttons != null ? buttons.toString() : null) + ","
                + "leftButton:" + (leftButton != null ? leftButton.toString() : null)
                + "}";
    }

    public static class Builder {
        private final String title;
        private Boolean hide;
        private List<NavigationBarButton> buttons;
        private NavigationBarLeftButton leftButton;

        public Builder(@NonNull String title) {
            this.title = title;
        }

        @NonNull
        public Builder hide(@Nullable Boolean hide) {
            this.hide = hide;
            return this;
        }

        @NonNull
        public Builder buttons(@Nullable List<NavigationBarButton> buttons) {
            this.buttons = buttons;
            return this;
        }

        @NonNull
        public Builder leftButton(@Nullable NavigationBarLeftButton leftButton) {
            this.leftButton = leftButton;
            return this;
        }

        @NonNull
        public NavigationBar build() {
            return new NavigationBar(this);
        }
    }
}
