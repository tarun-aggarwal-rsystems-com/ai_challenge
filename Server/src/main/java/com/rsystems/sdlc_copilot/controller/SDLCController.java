package com.rsystems.sdlc_copilot.controller;


import com.rsystems.sdlc_copilot.agent.SDLCRequirementsAgent;
import com.rsystems.sdlc_copilot.model.UserStoryResult;
import dev.langchain4j.model.chat.ChatModel; // <-- Fixed import!
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sdlc")
@CrossOrigin(origins = "*")
public class SDLCController {

    @Value("${gemini.api.key}")
    private String geminiKey;

    @Value("${openai.api.key}")
    private String openaiKey;

    @PostMapping("/generate-stories")
    public UserStoryResult generate(@RequestBody String rawText, @RequestParam(defaultValue = "gemini") String provider) {

        ChatModel model; // <-- Fixed class name!

        // Logic to switch between models
        if ("openai".equalsIgnoreCase(provider)) {
            System.out.println("Routing request to OpenAI...");
            model = OpenAiChatModel.builder()
                    .apiKey(openaiKey)
                    .modelName("gpt-4o-mini") // Fast, cheap, and smart
                    .build();
        } else {
            System.out.println("Routing request to Gemini...");
            model = GoogleAiGeminiChatModel.builder()
                    .apiKey(geminiKey)
                    .modelName("gemini-2.5-flash")
                    .build();
        }

        // Create the agent using the selected model
        SDLCRequirementsAgent agent = AiServices.builder(SDLCRequirementsAgent.class)
                .chatModel(model) // <-- Fixed builder method!
                .build();

        return agent.generateUserStories(rawText);
    }
}