package com.rsystems.sdlc_copilot.controller;


import com.rsystems.sdlc_copilot.agent.SDLCRequirementsAgent;
import com.rsystems.sdlc_copilot.model.RequirementRequest;
import com.rsystems.sdlc_copilot.model.UserStoryResult;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sdlc")
//@CrossOrigin(origins = "http://localhost:3000") // Allows React frontend to connect
@CrossOrigin(origins = "*")
public class SDLCController {

    private final SDLCRequirementsAgent agent;

    // Spring will automatically pull the API key from application.properties
    public SDLCController(@Value("${gemini.api.key}") String apiKey) {

        // 1. Configure the AI Model
        GoogleAiGeminiChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.5-flash")
                .build();

        // 2. Connect the model to your prompt interface
        this.agent = AiServices.create(SDLCRequirementsAgent.class, model);
    }

    // 3. The actual API endpoint you will call from React
    @PostMapping("/generate-stories")
    public UserStoryResult generate(@RequestBody RequirementRequest request) {
        return agent.generateUserStories(request.getRequirement());
    }
}
