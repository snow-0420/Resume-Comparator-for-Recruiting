package com.example.task1.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GPTChatService {

    public String getGPTResponseFromComparingTwoResume(String resume1, String resume2, String jobPosting) {
        List<ChatMessage> prompt = new ArrayList<>();

        ChatMessage system = new ChatMessage("system", "You are a helpful assistant.");
        prompt.add(system);

        ChatMessage sendJobListing = new ChatMessage("user",
                "I will upload a job posting, and I want you to help comparing two resume according " +
                        "to this job listing. Here is the job listing:\n" + jobPosting);
        prompt.add(sendJobListing);

        ChatMessage getJobListing = new ChatMessage("assistant",
                "I will remember this job listing for further instruction.");
        prompt.add(getJobListing);

        ChatMessage sendResume1 = new ChatMessage("user",
                "This is the first resume:\n" + resume1);
        prompt.add(sendResume1);

        ChatMessage getResume1 = new ChatMessage("assistant",
                "I received the first resume.");
        prompt.add(getResume1);

        ChatMessage sendResume2 = new ChatMessage("user",
                "Here is the second resume:\n" + resume2);
        prompt.add(sendResume2);

        ChatMessage getResume2 = new ChatMessage("assistant",
                "I received the second resume.");
        prompt.add(getResume2);

        ChatMessage compareRequest = new ChatMessage("user",
                "Now I want you to score each resume out of 100 accordingly " +
                        "to the job posting above, and reply strictly in this format:\n" +
                        "\"<First/Second> resume is better.\nFirst Resume: <your score for the first resume>/100" +
                        "\nSecond Resume: <your score for the second resume>/100\"\n" +
                        "Don't reply in other format than specified.");
        prompt.add(compareRequest);

        OpenAiService service = new OpenAiService("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");
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .messages(prompt)
                .model("gpt-3.5-turbo-1106")
                .build();

        return service.createChatCompletion(completionRequest).getChoices().getFirst().getMessage().getContent();
    }
}
