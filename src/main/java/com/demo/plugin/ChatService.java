package com.demo.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ChatService {

    @NotNull
    public String generateVariableName(String methodText, String variableName) {

        String content = """
                Your duty is rename variable for given method, return variable new name for user. 
                Given below method, please rename variable %s, and return it's new name. 
                For return result, there are some rules: 
                1. do not return full method. 
                2. return result should be plain text, not markdown format, only includes letter. 
                3. the naming should follow camel case.
                Given method: %s
                """.formatted(variableName, methodText)
                .replace("\"", "\\\"").replace("\n", "\\n");

        String requestJson = """
                {
                  "model": "gemma2:2b",
                  "messages": [
                    {
                      "role": "system",
                      "content": "%s"
                    }
                  ],
                  "stream": false
                }
                """.formatted(content);

        System.out.println("chat request json: \n" + requestJson);

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:11434/api/chat")
                .post(RequestBody.create(requestJson, MediaType.parse("application/json")))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            String json = response.body().string();
            JsonNode node = new ObjectMapper().readTree(json);
            String generatedVariableName = node.get("message").get("content").asText();
            System.out.println("chat generated variable name: " + generatedVariableName);

            return reformatVariable(generatedVariableName);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @NotNull
    private static String reformatVariable(String generatedVariableName) {
        return generatedVariableName
                .replace("\n", "")
                .replace("`", "")
                .trim();
    }
}
