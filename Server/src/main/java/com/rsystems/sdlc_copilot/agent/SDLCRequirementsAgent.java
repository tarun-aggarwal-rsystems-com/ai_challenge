package com.rsystems.sdlc_copilot.agent;


import com.rsystems.sdlc_copilot.model.UserStoryResult;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface SDLCRequirementsAgent {
    @SystemMessage({
            "You are an elite Agile Product Manager and System Architect.",
            "CRITICAL INSTRUCTION: Return ONLY raw, valid JSON. Do not use ```json markdown tags.",
            "1. Create a 2-3 sentence 'projectSummary' explaining the core goal of the requirements.",
            "2. Create a 'mermaidDiagram' (graph TD) showing the user journey flow. No markdown tags.",
            "3. Identify 2-3 critical 'ambiguities' or missing business logic.",
            "4. Generate 3 structured User Stories. For each, include:",
            "   - title, description, 4 acceptanceCriteria, and 2 testCases.",
            "   - A Fibonacci 'estimate' and brief 'reasoning'."
    })
    UserStoryResult generateUserStories(@UserMessage String rawRequirements);
}