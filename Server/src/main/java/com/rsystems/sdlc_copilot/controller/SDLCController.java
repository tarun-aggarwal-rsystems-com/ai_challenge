package com.rsystems.sdlc_copilot.controller;

import com.rsystems.sdlc_copilot.agent.SDLCRequirementsAgent;
import com.rsystems.sdlc_copilot.model.RefineRequest;
import com.rsystems.sdlc_copilot.model.UserStoryResult;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sdlc")
@CrossOrigin(origins = "*")
public class SDLCController {

    @Value("${gemini.api.key}")
    private String geminiKey;

    @PostMapping("/generate-stories")
    public UserStoryResult generate(@RequestBody(required = false) String rawText) {

        if (rawText == null || rawText.trim().isEmpty()) {
            System.out.println("No of characters: 0 (Input is null or empty)");
            throw new IllegalArgumentException("Requirements text cannot be empty.");
        } else {
            System.out.println("Received Input: " + rawText);
            System.out.println("No of characters: " + rawText.length());
        }

        System.out.println("Routing request exclusively to Gemini...");

        // 1. Locked to Gemini 2.5 Flash
        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(geminiKey)
                .modelName("gemini-2.5-flash")
                .build();

        SDLCRequirementsAgent agent = AiServices.builder(SDLCRequirementsAgent.class)
                .chatModel(model)
                .build();

        int charCount = (rawText != null) ? rawText.length() : 0;
        int maxAttempts = 3;

        // 2. The Retry Loop
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                UserStoryResult aiResult = agent.generateUserStories(rawText);

                return new UserStoryResult(
                        charCount,
                        null,
                        aiResult.projectSummary(),
                        aiResult.ambiguities(),
                        aiResult.mermaidDiagram(),
                        aiResult.userStories()
                );

            } catch (Exception e) {
                System.err.println("🚨 Gemini Service Error (Attempt " + attempt + "): " + e.getMessage());

                if (attempt == maxAttempts) {
                    return new UserStoryResult(
                            charCount,
                            "⚠️ AI Generation Failed: The AI provider is heavily loaded. Please try clicking 'Send' again.",
                            "",
                            java.util.List.of(),
                            "",
                            java.util.List.of()
                    );
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return new UserStoryResult(charCount, "System Error", "", java.util.List.of(), "", java.util.List.of());
    }

    @PostMapping("/refine")
    public UserStoryResult refineDashboard(@RequestBody RefineRequest request) {

        System.out.println("Instruction: " + request.refinementInstruction());

        // Locked to Gemini 2.5 Flash
        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(geminiKey)
                .modelName("gemini-2.5-flash")
                .build();

        SDLCRequirementsAgent agent = AiServices.builder(SDLCRequirementsAgent.class)
                .chatModel(model)
                .build();

        String combinedPrompt = String.format(
                "CURRENT DASHBOARD JSON:\n%s\n\nUSER REFINEMENT INSTRUCTION:\n%s\n\nPlease return the fully updated JSON.",
                request.currentDashboard().toString(),
                request.refinementInstruction()
        );

        try {
            return agent.refineUserStories(combinedPrompt);
        } catch (Exception e) {
            System.err.println("🚨 Refinement Failed: " + e.getMessage());
            return request.currentDashboard();
        }
    }
}