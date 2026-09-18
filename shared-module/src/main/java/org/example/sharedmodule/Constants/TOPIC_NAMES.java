package org.example.sharedmodule.Constants;

public final class TOPIC_NAMES {

    private TOPIC_NAMES() {}

    // ── Auth events (api-gateway → notification-service) ──
    public static final String AUTH_EMAIL_VERIFICATION = "auth.email-verification";
    public static final String AUTH_PASSWORD_RESET = "auth.password-reset";
    public static final String AUTH_PASSWORD_CHANGE = "auth.password-change";

    // ── User events (user-service → downstream consumers) ──
    public static final String USER_DEACTIVATED = "user.deactivated";
    public static final String USER_PROFILE_UPDATED = "user.profile.updated";
    public static final String USER_EMAIL_UPDATED = "user.email.updated";

    // ── Title events (title-service → downstream consumers) ──
    public static final String TITLE_UPDATED = "title.updated";
    public static final String TITLE_INDEXED = "title.indexed";

    // ── Rating events (ratings-reviews-service → downstream consumers) ──
    public static final String RATING_UPDATED = "rating.updated";
    public static final String RATING_AGGREGATED = "rating.aggregated";

    // ── People events (people-service → downstream consumers) ──
    public static final String PERSON_CREATED = "person.created";
    public static final String PERSON_UPDATED = "person.updated";
    public static final String PERSON_INDEXED = "person.indexed";

}
