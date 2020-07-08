package com.gladys.cybuverse.CyberBullyingAwarenessGame.Base;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ChatBotResponse {
    List<Response> responses;
    String function;

    public ChatBotResponse(List<String> responses, String function) {
        this.responses = new ArrayList<>();
        this.function = function;
        addAllResponses(responses);
    }

    public ChatBotResponse(List<String> responses) {
        this.responses = new ArrayList<>();
        addAllResponses(responses);
    }

    public ChatBotResponse() {
        this.responses = new ArrayList<>();
    }

    public List<Response> getResponses() {
        return responses;
    }

    public ChatBotResponse setResponses(List<Response> responses) {
        this.responses = responses;
        return this;
    }

    public List<String> getResponseMessages() {
        List<String> messages = new ArrayList<>();
        for (Response response : getResponses()) {
            messages.add(response.getMessage());
        }
        return messages;
    }

    public ChatBotResponse addResponse(String... responses) {
        return addAllResponses(Arrays.asList(responses));
    }

    public ChatBotResponse addResponse(Response... responses) {
        this.responses.addAll(Arrays.asList(responses));
        return this;
    }

    public ChatBotResponse addAllResponses(Collection<String> responses) {
        for (String message : responses) {
            this.responses.add(new Response(message));
        }
        return this;
    }

    public String getFunction() {
        return function;
    }

    public ChatBotResponse setFunction(String function) {
        this.function = function;
        return this;
    }

    public static class Response {
        String message;
        List<String> replacements;

        public Response(String message, List<String> replacements) {
            this.message = message;
            this.replacements = replacements;
        }

        public Response(String message, String... replacements) {
            this.message = message;
            this.replacements = Arrays.asList(replacements);
        }

        public Response(String message) {
            this.message = message;
            this.replacements = new ArrayList<>();
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<String> getReplacements() {
            return replacements;
        }

        public void setReplacements(List<String> replacements) {
            this.replacements = replacements;
        }

        public Response addReplacements(String... replacements) {
            this.replacements.addAll(Arrays.asList(replacements));
            return this;
        }

        public Response addAllReplacements(Collection<String> replacements) {
            this.replacements.addAll(replacements);
            return this;
        }

    }

}
