package com.rsystems.sdlc_copilot.model;

import java.util.List;

public record UserStoryResult(
        List<String> ambiguities,
        List<UserStory> userStories
) {
    public record UserStory(
            String title,
            String description,
            List<String> acceptanceCriteria,
            List<String> testCases // <-- WE JUST ADDED THIS LINE
    ) {}
}