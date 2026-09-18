package org.example.sharedmodule.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Predicate;

public final class SlugUtils {

    private static final String FALLBACK_SLUG = "item";

    private SlugUtils() {}

    /**
     * Canonical slug base: lowercases, strips diacritics and any non-alphanumeric
     * runs. Returns an empty string when the input has no usable characters.
     */
    public static String slugify(String text) {
        if (text == null || text.isBlank()) return "";
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    /**
     * Produces a slug that is unique according to {@code isTaken} by appending
     * a random 4-char suffix on collision. Blank input falls back to {@code item}.
     */
    public static String generateUniqueSlug(String text, Predicate<String> isTaken) {
        String base = slugify(text);
        if (base.isEmpty()) base = FALLBACK_SLUG;

        String candidate = base;
        while (isTaken.test(candidate)) {
            candidate = base + "-" + UUID.randomUUID().toString().substring(0, 4);
        }
        return candidate;
    }
}