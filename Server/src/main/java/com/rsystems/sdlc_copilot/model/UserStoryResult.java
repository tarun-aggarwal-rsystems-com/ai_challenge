package com.rsystems.sdlc_copilot.model;

import java.util.List;

public record UserStoryResult(
        Integer characterCount,
        String error,
        String projectSummary, // <-- Added to meet rubric Task 2
        List<String> ambiguities,
        String mermaidDiagram,
        List<UserStory> userStories
) {
    public record UserStory(
            String title,
            String description,
            List<String> acceptanceCriteria,
            List<String> testCases,
            String estimate,
            String reasoning
            // REMOVED codeSnippet!
    ) {}
}