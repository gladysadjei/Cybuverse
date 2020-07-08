package com.gladys.cybuverse.CyberBullyingAwarenessGame.Scenes;

import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Actor;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.ActorsMap;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.ChatBot;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Conversation;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Message;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.MessageContent;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.MessageGetter;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Scene;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.SentenceAnalyser;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.TimeoutMessage;
import com.gladys.cybuverse.Models.Chat;
import com.gladys.cybuverse.Models.Question;
import com.gladys.cybuverse.Models.User;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.MessageContent.TYPE_TEXT;

public class BasicSchool extends Scene {
    private User user;
    private Actor gladys;
    private Actor mom;
    private Actor crush;
    private Actor friend1;
    private Actor friend2;
    private Actor teacher;
    private Actor player;

    public BasicSchool(User user) {
        super("Basic School");
        this.user = user;
        this.gladys = ActorsMap.getInstance().get("gladys");
        this.mom = ActorsMap.getInstance().get("mommy");
        this.teacher = ActorsMap.getInstance().get("mrs. thompson");

        this.crush = ActorsMap.getInstance().get(user.getGender().equals("male") ? "janet": "mike");
        this.friend1 = ActorsMap.getInstance().get(user.getGender().equals("male") ? "chris": "ethel");
        this.friend2 = ActorsMap.getInstance().get(user.getGender().equals("male") ? "bismark": "ella");

        this.player = new Actor(user.getName(), "current-user");
        init();
    }

    private void init() {
        setAim("In the following scenes. you will be introduced to some of the characters in this game.");
        setDescription("You would be introduce to your new class and you'll also have the chance to make new friends.");
        addConversation(getConversationWithMommy());
        addConversation(getConversationWithClassGroup());
        addConversation(getConversationWithCrush());
        addConversation(getConversationWithNewFriends());
    }

    public Actor getPlayer() {
        return player;
    }

    private Conversation getConversationWithMommy() {
        Chat mommyChat = new Chat(mom.getName());
        mommyChat.setId("--mommy--");
        mommyChat.setLastSeen("online");
        mommyChat.setUnreadMessagesCount(1);
        Conversation conversation = new Conversation(mommyChat);
        conversation.addActor(gladys, mom, getPlayer());

        conversation.addMessage(mom, getPlayer(), new MessageContent("How are you?" + ((user.getGender().equals("male")) ? "  Son." : "")));

        MessageGetter firstResponse = new MessageGetter(getPlayer(), mom)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("I am fine."),
                        new MessageContent("I'm good."),
                        new MessageContent("I am okay")
                );
        conversation.addMessage(firstResponse);
        conversation.addMessage(mom, getPlayer(), new MessageContent("I hope you have nice day at school today. i know school can be difficult but you'll be fine."));
        conversation.addMessage(mom, getPlayer(), new MessageContent("love you."));

        mommyChat.setLastMessage(conversation.getMessages().get(0).getContent().getData().toString());
        return conversation;
    }

    private Conversation getConversationWithClassGroup() {
        Chat classChat = new Chat("Class Group");
        classChat.setId("--class-group--");
        classChat.setLastSeen("online");
        classChat.setUnreadMessagesCount(1);
        final Conversation conversation = new Conversation(classChat);
        conversation.addActor(gladys, crush, teacher, getPlayer());

        conversation.addMessage(new Message(gladys, getPlayer(), new MessageContent("You were added to this group."))
                .setProperty("gladys-info", true));
        conversation.addMessage(teacher, getPlayer(), new MessageContent("Welcome to the class group "+getPlayer().getName()+"."));

        MessageGetter firstResponse = new MessageGetter(getPlayer(), crush, 15000)
                .setExpectedContentType(TYPE_TEXT);
        conversation.addMessage(firstResponse);

        conversation.addMessage(teacher, getPlayer(), new MessageContent("I am your teacher and this is your class group. This space is for academic discussions."));
        conversation.addMessage(teacher, getPlayer(), new MessageContent("feel free to ask for my help if you have any questions."));
        conversation.addMessage(teacher, getPlayer(), new MessageContent("you should introduce yourself. some of your colleges don't know you yet."));

        MessageGetter introductionResponse = new MessageGetter(getPlayer(), crush)
                .setExpectedContentType(TYPE_TEXT)
                .setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
                    @Override
                    public void onSuccess(MessageContent content) {
                        String text = content.getData().toString();
                        SentenceAnalyser analyser = new SentenceAnalyser(text);
                        analyser.cleanSentence();
                        if (!analyser.matchAny(getPlayer().getName())){
                            conversation.addInterruptMessage(new Message(teacher, getPlayer(), new MessageContent("Welcome. "+player.getName())));
                        }
                    }
                })
                .setTimeout(10000, new MessageGetter.DefaultTimerEventListener(
                        new TimeoutMessage(teacher, getPlayer(), new MessageContent("Don't be shy. Tell them about yourself.")),
                        new TimeoutMessage(teacher, getPlayer(), new MessageContent("Okay i guess. you'll have to introduce yourself in class then."))
                ));

        conversation.addMessage(introductionResponse);

        conversation.addMessage(new Message(gladys, getPlayer(), new MessageContent("class group offline"))
                .setProperty("gladys-info", true));

        classChat.setLastMessage(conversation.getMessages().get(0).getContent().getData().toString());
        return conversation;
    }

    private Conversation getConversationWithCrush() { //mike or janet
        Chat chat = new Chat(crush.getName());
        chat.setId("--crush--");
        chat.setLastSeen("online");
        chat.setUnreadMessagesCount(1);
        final Conversation conversation = new Conversation(chat);
        conversation.addActor(gladys, crush, getPlayer());

        conversation.addMessage(new Message(crush, getPlayer(), new MessageContent("Hello "+player.getName())));
        MessageGetter hiResponse = new MessageGetter(getPlayer(), crush)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(new MessageContent("Hi."), new MessageContent("Hello."), new MessageContent("Hey"));
        conversation.addMessage(hiResponse);
        conversation.addMessage(new Message(crush, getPlayer(), new MessageContent("Well am "+crush.getName()+". And am your class prefect. I just wanted to say hi since i didn't get the chance to do so in the class group.")));
        conversation.addMessage(new Message(crush, getPlayer(), new MessageContent("Hope you're okay?.")));

        final MessageGetter okayResponse = new MessageGetter(getPlayer(), crush)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(new MessageContent("Yeah."), new MessageContent("Yhup. you?"), new MessageContent("No. not really."))
                .setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
                    @Override
                    public void onSuccess(MessageContent content) {
                        switch (content.getData().toString()){
                            case "Yeah.":
                                conversation.addInterruptMessage(new Message(crush, getPlayer(), new MessageContent("That's great")));
                                break;
                            case "Yhup. you?":
                                conversation.addInterruptMessage(new Message(crush, getPlayer(), new MessageContent("Am okay too.")));
                                break;
                            case "No. not really.":
                                conversation.addInterruptMessage(
                                        new Message(crush, getPlayer(), new MessageContent("What's wrong?")),
                                        new MessageGetter(getPlayer(), crush).setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
                                            @Override
                                            public void onSuccess(MessageContent content) {
                                                sendMessageToAdmin(content);
                                            }
                                        }),
                                        new Message(crush, getPlayer(), new MessageContent("Am sorry to hear that. you'll be fine."))
                                );
                                break;
                        }
                    }
                });
        conversation.addMessage(okayResponse);

        conversation.addMessage(new Message(crush, getPlayer(), new MessageContent("So. How are you liking the school so far?.")));
        MessageGetter likeResponse = new MessageGetter(getPlayer(), crush, 15000);
        conversation.addMessage(likeResponse);
        conversation.addMessage(new Message(crush, getPlayer(), new MessageContent("Okay.. well don't forget to text me if you have problem.")));
        conversation.addMessage(new Message(crush, getPlayer(), new MessageContent("Good bye and take care. I hope to see you in class.")));

        chat.setLastMessage(conversation.getMessages().get(0).getContent().getData().toString());
        return conversation;
    }

    private Conversation getConversationWithNewFriends() {
        Chat chat = new Chat(user.getGender().equals("male") ? "Boys Club." : "Girl Friends.");
        chat.setId("--friends--");
        chat.setLastSeen("online");
        chat.setUnreadMessagesCount(1);
        final Conversation conversation = new Conversation(chat);
        conversation.addActor(gladys, friend1, friend2, getPlayer());

        conversation.addMessage(new Message(gladys, getPlayer(), new MessageContent("You were added to this group."))
                .setProperty("gladys-info", true));
        conversation.addMessage(new Message(gladys, getPlayer(), new MessageContent("description: we'll we are all friends here. :-)"))
                .setProperty("gladys-info", true));

        conversation.addMessage(new Message(friend1, getPlayer(), new MessageContent("Hello. Am "+friend1.getName()+".")));
        conversation.addMessage(new Message(friend2, getPlayer(), new MessageContent("And. Am "+friend2.getName()+". Welcome.")));
        MessageGetter response = new MessageGetter(getPlayer(), friend1, 15000);
        conversation.addMessage(response);
        conversation.addMessage(new Message(friend1, getPlayer(),
                new MessageContent("We know you weren't. expecting to be added to any group. But we like you "+
                        player.getName()+". And we thought maybe we could be friends.")));
        conversation.addMessage(new Message(friend2, getPlayer(),
                new MessageContent("Well tell you everything you need to know about the school and show you which places are nice to hang.")));

        conversation.addMessage(new MessageGetter(getPlayer(), friend2, 15000));
        conversation.addMessage(new Message(friend2, getPlayer(), new MessageContent("Okay. This is going to be interesting.")));
        conversation.addMessage(new Message(friend1, getPlayer(), new MessageContent("Yeah.\n you guys i have to go now my "+
                (user.getGender().equals("male") ? "dad" : "mom")+" needs me. well talk later.")));
        conversation.addMessage(new Message(friend2, getPlayer(), new MessageContent("I should probably go too. You should rest for tomorrow.\nTake care.")));
        conversation.addMessage(new MessageGetter(getPlayer(), friend1, 15000));

        chat.setLastMessage(conversation.getMessages().get(0).getContent().getData().toString());
        return conversation;
    }


    private void sendMessageToAdmin(MessageContent content) {
        FirebaseFirestore.getInstance().collection("Questions")
                .add(new Question(ChatBot.getUID(), user, content.getData().toString()));
    }

}
