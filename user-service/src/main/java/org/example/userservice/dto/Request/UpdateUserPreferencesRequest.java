package org.example.userservice.dto.Request;

import java.util.List;

public record UpdateUserPreferencesRequest(
        List<String> favGenres,
        List<String> favLanguages,
        Boolean adultContent,
        Boolean emailNotifs,
        Boolean publicWatchlist
) {}