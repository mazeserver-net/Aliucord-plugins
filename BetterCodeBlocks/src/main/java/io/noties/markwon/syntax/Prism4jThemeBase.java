/*
  Copyright 2019 Dimitry Ivanov (legal@noties.io)
  Modifications: Copyright 2021 Juby210

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package io.noties.markwon.syntax;

import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.noties.prism4j.Prism4j.Syntax;
import java.util.HashMap;

public abstract class Prism4jThemeBase implements Prism4jTheme {
    private final ColorHashMap colorHashMap = init();

    protected static class Color {
        @ColorInt
        protected final int color;

        @NonNull
        public static Color of(@ColorInt int color) {
            return new Color(color);
        }

        protected Color(@ColorInt int color) {
            this.color = color;
        }
    }

    public static class ColorHashMap extends HashMap<String, Color> {
        @NonNull
        public ColorHashMap add(@ColorInt int color, String name) {
            put(name, Color.of(color));
            return this;
        }

        @NonNull
        public ColorHashMap add(@ColorInt int color, @NonNull String name1, @NonNull String name2) {
            Color c = Color.of(color);
            put(name1, c);
            put(name2, c);
            return this;
        }

        @NonNull
        public ColorHashMap add(@ColorInt int color, @NonNull String name1, @NonNull String name2, @NonNull String name3) {
            Color c = Color.of(color);
            put(name1, c);
            put(name2, c);
            put(name3, c);
            return this;
        }

        @NonNull
        public ColorHashMap add(@ColorInt int color, String... names) {
            Color c = Color.of(color);
            for (String name : names) {
                put(name, c);
            }
            return this;
        }
    }

    @NonNull
    public abstract ColorHashMap init();

    @ColorInt
    protected static int applyAlpha(@IntRange(from = 0, to = 255) int alpha, @ColorInt int color) {
        return (16777215 & color) | (alpha << 24);
    }

    @ColorInt
    protected static int applyAlpha(@FloatRange(from = 0.0d, to = 1.0d) float alpha, @ColorInt int color) {
        return applyAlpha((int) ((255.0f * alpha) + 0.5f), color);
    }

    protected static boolean isOfType(@NonNull String expected, @NonNull String type, @Nullable String alias) {
        return expected.equals(type) || expected.equals(alias);
    }

    protected Prism4jThemeBase() {
    }

    /* Access modifiers changed, original: protected */
    @ColorInt
    public int color(@NonNull String language, @NonNull String type, @Nullable String alias) {
        Color color = (Color) this.colorHashMap.get(type);
        if (color == null && alias != null) {
            color = (Color) this.colorHashMap.get(alias);
        }
        if (color != null) {
            return color.color;
        }
        return 0;
    }

    public void apply(@NonNull String language, @NonNull Syntax syntax, @NonNull SpannableStringBuilder builder, int start, int end) {
        String type = syntax.type();
        String alias = syntax.alias();
        String str = language;
        int color = color(language, type, alias);
        if (color != 0) {
            applyColor(language, type, alias, color, builder, start, end);
        }
    }

    /* Access modifiers changed, original: protected */
    public void applyColor(@NonNull String language, @NonNull String type, @Nullable String alias, @ColorInt int color, @NonNull SpannableStringBuilder builder, int start, int end) {
        builder.setSpan(new ForegroundColorSpan(color), start, end, 33);
    }
}
