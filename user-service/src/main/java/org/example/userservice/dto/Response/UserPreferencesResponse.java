package org.example.userservice.dto.Response;

import java.util.List;

public record UserPreferencesResponse(
        List<String> favGenres,
        List<String> favLanguages,
        Boolean adultContent,
        Boolean emailNotifs,
        Boolean publicWatchlist
) {}

