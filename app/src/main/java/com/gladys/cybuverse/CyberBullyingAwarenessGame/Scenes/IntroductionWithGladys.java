package com.gladys.cybuverse.CyberBullyingAwarenessGame.Scenes;

import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.ChatBot;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Actor;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.ActorsMap;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Conversation;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Message;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.MessageContent;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.MessageGetter;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Scene;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.SentenceAnalyser;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.TimeoutMessage;
import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Models.Question;
import com.gladys.cybuverse.Models.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import static com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.MessageContent.TYPE_TEXT;

public class IntroductionWithGladys extends Scene {

    private final User user;
    private Actor gladys;
    private Actor player;

    public IntroductionWithGladys(User user) {
        super("Introduction With Gladys");
        this.user = user;
        this.gladys = ActorsMap.getInstance().get("gladys");
        this.player = new Actor(user.getName(), "current-user");
        init();
    }

    private void init() {
        setAim("We hope to teach you about the game and introduce you to Gladys.");
        setDescription("Welcome to the Cybuverse." +
                "\nAn environment created to teach and educate everyone about cyberbullying and how it affects the society." +
                "\nWe hope you have fun.");
        addConversation(getConversationWithGladys());
    }

    public Actor getPlayer() {
        return player;
    }

    private Conversation getConversationWithGladys() {

        final Conversation conversation = new Conversation(ChatBot.asChat());
        conversation.addActor(gladys, getPlayer());

        conversation.addMessage(gladys, getPlayer(), new MessageContent("Hi.")); //delay for response else say am glady

        MessageGetter hiResponse = new MessageGetter(getPlayer(), gladys, 15000)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(new MessageContent("Hi."), new MessageContent("Hello."), new MessageContent("Hey"));
        conversation.addMessage(hiResponse);

        conversation.addMessage(gladys, getPlayer(), new MessageContent("Am Gladys.\nWhat's your name?"));

        MessageGetter nameResponse = new MessageGetter(getPlayer(), gladys)
                .setExpectedContentType(TYPE_TEXT)
                .setTimeout(15000, new MessageGetter.DefaultTimerEventListener(
                        new TimeoutMessage(gladys, getPlayer(), new MessageContent("May i know your name please?")),
                        new TimeoutMessage(gladys, getPlayer(), new MessageContent("Hello... are you there?")),
                        new TimeoutMessage(gladys, getPlayer(), new MessageContent("Ill call you " + getPlayer().getName())))
                );
        conversation.addMessage(nameResponse);

        conversation.addMessage(gladys, getPlayer(), new MessageContent("Are you a child, a teenager or an adult?"));

        MessageGetter genderResponse = new MessageGetter(getPlayer(), gladys)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("child"),
                        new MessageContent("teenager"),
                        new MessageContent("adult")
                )
                .setContentValidator(new MessageGetter.ContentValidator() {
                    @Override
                    public boolean isValid(MessageContent content, List<MessageContent> contentOptions) {
                        SentenceAnalyser sentenceAnalyser = new SentenceAnalyser(content.getData().toString());
                        sentenceAnalyser.cleanSentence();
                        return sentenceAnalyser.matchAny("kid", "child", "adult", "parent", "teen", "teenager");
                    }
                });
        conversation.addMessage(genderResponse);

        conversation.addMessage(gladys, getPlayer(), new MessageContent("Okay, that's great."));
        conversation.addMessage(gladys, getPlayer(), new MessageContent("Ill be your personal guide. You can find out anything about the game from me. Ill show you how later."));

        conversation.addMessage(gladys, getPlayer(), new MessageContent("I'd like to ask you some more questions if that's okay?"));
        MessageGetter askQuestionsResponse = new MessageGetter(getPlayer(), gladys)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("yes"),
                        new MessageContent("no")
                )
                .setContentValidator(new MessageGetter.DefaultContentValidator())
                .setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener(){
                    @Override
                    public void onSuccess(MessageContent content) {
                        switch(content.getData().toString()){
                            case "no":
                                conversation.addInterruptMessage(new Message(gladys, getPlayer(), new MessageContent("Okay... we'll let me tell you about the game then.")));
                                break;
                            case "yes":
                                conversation.addInterruptConversation(getGladysQuestionsForUserConversation());
                                break;
                        }
                    }
                });
        conversation.addMessage(askQuestionsResponse);

        conversation.addMessage(new Message(gladys, getPlayer(), new MessageContent("This game will help you identify cases of cyberbulling and how to handle such situations when faced with them. " +
                "\nWe will also  help you understand the things that considered offensive or rude so that you don't accidentally offend people especially when you are in an online space (browsing the internet.)")));

        conversation.addMessage(new Message(gladys, getPlayer(), new MessageContent("Are you excited about this game?")));

        MessageGetter excitementStatusResponse = new MessageGetter(getPlayer(), gladys)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("yes"),
                        new MessageContent("no")
                )
                .setContentValidator(new MessageGetter.DefaultContentValidator())
                .setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener(){
                    @Override
                    public void onSuccess(MessageContent content) {
                        switch(content.getData().toString()){
                            case "no":
                                conversation.addInterruptMessage(new Message(gladys, getPlayer(), new MessageContent("Its going to be really fun. there's a lot of new things to learn.")));
                                break;
                            case "yes":
                                conversation.addInterruptMessage(new Message(gladys, getPlayer(), new MessageContent("Awesome. Me too. lets get to it then.")));
                                break;
                        }
                    }
                });

        conversation.addMessage(excitementStatusResponse);

        conversation.addMessage(new Message(gladys, getPlayer(), new MessageContent("You will occasionally get chats with some people and you will be given a task to complete. " +
                "You would have to text them back and i would award you marks for how you behave yourself.\n" +
                "The nicer you are to everyone, the more points you make. \nYou can also try the quick game which would allow you to text other random players of this game. The same rules apply there too.")));

        conversation.addMessage(new Message(gladys, getPlayer(), new MessageContent("You can start playing the game now. Hope you have fun.")));

//        MessageGetter imageResponse = new MessageGetter(getPlayer(), gladys, 40000)
//                .setExpectedContentType(TYPE_ALL)
//                .addContentOptions(
//                        new TaggedMessageContent("use default", "default-uri"),
//                        new TaggedMessageContent("use current", "profile-uri|default-uri")
//                );
//        conversation.addMessage(imageResponse);


        conversation.addMessage(new Message(gladys, getPlayer(), new MessageContent("Introduction Completed Successfully."))
                .setProperty("gladys-info", true));

        return conversation;
    }

    private Conversation getGladysQuestionsForUserConversation() {
        final Conversation conversation = new Conversation(null);
        conversation.addActor(gladys, getPlayer());

        conversation.addMessage(new Message(gladys, getPlayer(), new MessageContent("Great...")));

        conversation.addMessage(new Message(gladys, getPlayer(), new MessageContent("So do you know anything about cyerbullying?")));
        MessageGetter isAwareOfCyberBullyingResponse = new MessageGetter(getPlayer(), gladys)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("yes"),
                        new MessageContent("no")
                )
                .setContentValidator(new MessageGetter.DefaultContentValidator())
                .setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener(){
                    @Override
                    public void onSuccess(MessageContent content) {
                        switch(content.getData().toString()){
                            case "no":
                                conversation.addInterruptMessage(new Message(gladys, getPlayer(), new MessageContent("Okay... i'll tell you a little about it.")));
                                break;
                            case "yes":
                                conversation.addInterruptMessage(new Message(gladys, getPlayer(), new MessageContent("Okay that's good.")));
                                break;
                        }
                    }
                });
        conversation.addMessage(isAwareOfCyberBullyingResponse);

        conversation.addMessage(new Message(gladys, getPlayer(), new MessageContent("Its actually a big problem in our societies today. " +
                "Cyberbullying is basically like bullying except it is done online.\n" +
                "Most people do it because of the anonymity the internet comes with. "+
                "Cyberbullying is bullying that takes place over digital devices like cell phones, " +
                "computers, and tablets. Cyberbullying can occur through SMS, Text, and apps, " +
                "or online in social media, forums, or gaming where people can view, participate in, " +
                "or share content. Cyberbullying includes sending, posting, or sharing negative, " +
                "harmful, false, or mean content about someone else. It can include sharing personal " +
                "or private information about someone else causing embarrassment or humiliation. " +
                "Some cyberbullying crosses the line into unlawful or criminal behavior.\n" +
                "\n" +
                "The most common places where cyberbullying occurs are:\n" +
                "\n" +
                "Social Media, such as Facebook, Instagram, Snapchat, and Tik Tok\n" +
                "Text messaging and messaging apps on mobile or tablet devices\n" +
                "Instant messaging, direct messaging, and online chatting over the internet\n" +
                "Online forums, chat rooms, and message boards, such as Reddit\n" +
                "Email\n" +
                "Online gaming communities\n")));

        conversation.addMessage(new Message(gladys, getPlayer(), new MessageContent("do you understand what cyberbullying is and how it affects people?")));

        MessageGetter isUnderstandCyberBullyingResponse = new MessageGetter(getPlayer(), gladys)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("yes"),
                        new MessageContent("no")
                )
                .setContentValidator(new MessageGetter.DefaultContentValidator())
                .setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener(){
                    @Override
                    public void onSuccess(MessageContent content) {
                        switch(content.getData().toString()){
                            case "no":
                                conversation.addInterruptMessage(new Message(gladys, getPlayer(), new MessageContent("Okay... i'll try explaining with an example. .. " +
                                        "okay so one time I posted a photo of myself on Instagram with the caption “Life is good”. " +
                                        "A bunch of people including a boy I previously was hanging " +
                                        "out with attacked this post with really mean and rude comments about me. " +
                                        "I was so upset that I privately texted the girl who seemed " +
                                        "to be leading these comments asking her why she was writing " +
                                        "these hurtful things and she replied by saying “it’s a joke " +
                                        "and we are just having fun.” She then continued to write more mean " +
                                        "things about me and even made a post about me on her spam account, " +
                                        "purposefully so I could see it. She threatened me saying “I deserved this” " +
                                        "and that she would hurt me and live stream it for other people to watch. ..")));
                                break;
                            case "yes":
                                conversation.addInterruptMessage(new Message(gladys, getPlayer(), new MessageContent("Okay that's good.")));
                                break;
                        }
                    }
                });

        conversation.addMessage(isUnderstandCyberBullyingResponse);

        conversation.addMessage(new Message(gladys, getPlayer(), new MessageContent("okay.. so since you understand what cyberbullying is. Do you think you've ever engaged in the act or been a victim?")));

        MessageGetter engageStatusResponse = new MessageGetter(getPlayer(), gladys)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("i have never experienced any form of cyberbullying."),
                        new MessageContent("yes. i did cyberbully someone."),
                        new MessageContent("i was a victim.")
                )
                .setContentValidator(new MessageGetter.DefaultContentValidator())
                .setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener(){
                    @Override
                    public void onSuccess(MessageContent content) {
                        switch(content.getData().toString()){
                            case "i have never experienced any form of cyberbullying.":
                                conversation.addInterruptMessage(new Message(gladys, getPlayer(), new MessageContent("Okay that's good.")));
                                break;
                            case "yes. i did cyberbully someone.":
                                conversation.addInterruptConversation(getConversationWithOffenderPlayer());
                                break;
                            case "i was a victim.":
                                conversation.addInterruptConversation(getConversationWithVictimPlayer());
                                break;
                        }
                    }
                });;
        conversation.addMessage(engageStatusResponse);

        return conversation;
    }

    private Conversation getConversationWithVictimPlayer() {
        final Conversation conversation = new Conversation(null);
        conversation.addActor(gladys, getPlayer());
        conversation.addMessage(new Message(gladys, getPlayer(), new MessageContent("Do you want to share what happened and what how it affected you?")));

        final Conversation receiveStory = new Conversation(null);
        receiveStory.addActor(gladys, getPlayer());
        receiveStory.addMessage(new Message(gladys, getPlayer(), new MessageContent("Okay go ahead. tell the story.")));
        MessageGetter getStory = new MessageGetter(getPlayer(), gladys).setExpectedContentType(TYPE_TEXT);
        getStory.setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
            @Override
            public void onSuccess(MessageContent content) {
                sendMessageToAdmin(content);
            }
        });
        receiveStory.addMessage(getStory);
        receiveStory.addMessage(new Message(gladys, getPlayer(), new MessageContent("Aw. Sorry that happened to you.")));

        MessageGetter confirmYesOrNo = new MessageGetter(getPlayer(), gladys)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("yes"),
                        new MessageContent("no")
                )
                .setContentValidator(new MessageGetter.DefaultContentValidator())
                .setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener(){
                    @Override
                    public void onSuccess(MessageContent content) {
                        switch(content.getData().toString()){
                            case "no":
                                conversation.addInterruptMessage(new Message(gladys, getPlayer(), new MessageContent("That's okay.")));
                                break;
                            case "yes":
                                conversation.addInterruptConversation(receiveStory);
                                break;
                        }
                    }
                });

        conversation.addMessage(confirmYesOrNo);

        return conversation;
    }

    private Conversation getConversationWithOffenderPlayer() {
        final Conversation conversation = new Conversation(null);
        conversation.addActor(gladys, getPlayer());

        conversation.addMessage(new Message(gladys, getPlayer(), new MessageContent("Would you like to share you what happened?")));

        final Conversation receiveStory = new Conversation(null);
        receiveStory.addActor(gladys, getPlayer());
        receiveStory.addMessage(new Message(gladys, getPlayer(), new MessageContent("Okay go ahead.")));
        MessageGetter getStory = new MessageGetter(getPlayer(), gladys).setExpectedContentType(TYPE_TEXT);
        receiveStory.addMessage(getStory);
        receiveStory.addMessage(new Message(gladys, getPlayer(), new MessageContent("Okay. Well this game is here to help you be better.")));

        MessageGetter confirmYesOrNo = new MessageGetter(getPlayer(), gladys)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("yes"),
                        new MessageContent("no")
                )
                .setContentValidator(new MessageGetter.DefaultContentValidator())
                .setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener(){
                    @Override
                    public void onSuccess(MessageContent content) {
                        switch(content.getData().toString()){
                            case "no":
                                conversation.addInterruptMessage(new Message(gladys, getPlayer(), new MessageContent("That's okay.")));
                                break;
                            case "yes":
                                conversation.addInterruptConversation(receiveStory);
                                break;
                        }
                    }
                });
        conversation.addMessage(confirmYesOrNo);

        return conversation;
    }

    private void sendMessageToAdmin(MessageContent content) {
        FirebaseFirestore.getInstance().collection("Questions")
                .add(new Question(ChatBot.getUID(), user, content.getData().toString()));
    }
}
