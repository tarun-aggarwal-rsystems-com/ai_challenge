package com.rsystems.sdlc_copilot.model;

public record RefineRequest(
        UserStoryResult currentDashboard, // This is "Tokens 1 Set"
        String refinementInstruction      // This is "Input 2"
) {}
