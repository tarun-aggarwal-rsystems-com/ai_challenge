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
    public UserStoryResult generate(@RequestBody(required = false) String rawText, @RequestParam(defaultValue = "gemini") String provider) {

        System.out.println("====== INCOMING REQUEST DEBUG ======");
        if (rawText == null || rawText.trim().isEmpty()) {
            System.out.println("No of characters: 0 (Input is null or empty)");
            throw new IllegalArgumentException("Requirements text cannot be empty.");
        } else {
            System.out.println("Received Input: " + rawText);
            System.out.println("No of characters: " + rawText.length());
        }
        System.out.println("====================================");

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

                .chatModel(model)

                .build();



        // Calculate character count early so we have it no matter what happens

        int charCount = (rawText != null) ? rawText.length() : 0;



        try {

            // 1. ATTEMPT: Let the AI generate the complex stuff

            UserStoryResult aiResult = agent.generateUserStories(rawText);



            // 2. SUCCESS: Package the AI result WITH your calculated character count

            return new UserStoryResult(

                    charCount,

                    aiResult.projectSummary(),

                    aiResult.ambiguities(),

                    aiResult.mermaidDiagram(),

                    aiResult.userStories()

            );



        } catch (Exception e) {

            // 3. FAILURE HANDLING: The AI crashed (Quota, Leaked Key, Timeout, etc.)

            System.err.println("🚨 AI Service Error: " + e.getMessage());



            // 4. GRACEFUL FALLBACK: Return the character count, but empty out the AI fields

            // so the frontend doesn't crash with a 500 error!

            return new UserStoryResult(

                    charCount, // <-- TL's requirement: Char count still gets returned!

                    "⚠️ AI Generation Failed: The AI provider is currently unavailable (e.g., Quota Exceeded). However, your text was successfully received.",

                    java.util.List.of(), // Empty List for ambiguities

                    "",                  // Empty String for diagram

                    java.util.List.of()  // Empty List for user stories

            );

        }
       // return agent.generateUserStories(rawText);
    }
}