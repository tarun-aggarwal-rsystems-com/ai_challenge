package com.rsystems.sdlc_copilot.agent;


import com.rsystems.sdlc_copilot.model.UserStoryResult;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface SDLCRequirementsAgent {

    @SystemMessage({
            "You are a strict, highly experienced Technical Lead.",
            "Your first and most important task is to find flaws in the provided requirements.",
            "MANDATORY RULE: You MUST generate exactly 3 critical questions, missing edge cases, or security concerns and place them in the 'ambiguities' list. YOU ARE FORBIDDEN FROM LEAVING THE AMBIGUITIES LIST EMPTY.",
            "After documenting the 3 ambiguities, generate exactly 3 distinct User Stories based on the input.",
            "For each user story, provide:",
            "1. A clear title.",
            "2. A standard format description (As a... I want to... So that...).",
            "3. A list of specific, testable acceptance criteria.",
            "4. A list of 2 to 3 practical QA test cases."
    })
    UserStoryResult generateUserStories(@UserMessage String rawRequirements);
}