package com.gladys.cybuverse.CyberBullyingAwarenessGame.Base;

import com.gladys.cybuverse.Models.Chat;
import com.gladys.cybuverse.Utils.GeneralUtils.collections.graph.Edge;

import java.util.ArrayList;
import java.util.List;

public class ChatBot {

    private static ChatBot chatBot;
    private static Actor gladysActor;
    private static Chat gladysChat;
    private ChatBotBrain chatBotBrain;

    private ChatBot() {
        chatBotBrain = new ChatBotBrain();
    }

    public static ChatBot getInstance() {
        if (chatBot == null) {
            chatBot = new ChatBot();
        }
        return chatBot;
    }

    public static String getUID() {
        return "--gladys-chat-bot--";
    }

    public static Actor asActor() {
        if (gladysActor == null) {
            gladysActor = new Actor("Gladys", 0, "chat-bot", "your-helper");
        }
        return gladysActor;
    }

    public static Chat asChat() {
        if (gladysChat == null) {
            gladysChat = new Chat("Gladys");
            gladysChat.setLastMessage("ask me anything...");
            gladysChat.setLastSeen("online");
            gladysChat.setUnreadMessagesCount(1);
            gladysChat.setId(getUID());
        }
        return gladysChat;
    }

    public ChatBotResponse respondToMessage(MessageContent content) {
        String _content = content.getData().toString();
        return chatBotBrain.process(_content);
    }

    public List<MessageContent> generateContentOptions(MessageContent content) {
        List<MessageContent> options = new ArrayList<>();
        try {
            List<Edge<String>> respondList = chatBotBrain.getConversationGraph()
                    .getEdges(content.getData().toString());

            for (Edge<String> respond : respondList) {
                options.add(new MessageContent(respond.getEndPoint(), MessageContent.TYPE_TEXT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return options;
    }
}
