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
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.TaggedMessageContent;
import com.gladys.cybuverse.Models.Chat;
import com.gladys.cybuverse.Models.User;

import java.util.List;
import java.util.Random;

import static com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.MessageContent.TYPE_TEXT;

public class SchoolTingo extends Scene {
    private User user;
    private Actor gladys;
    private Actor mom;
    private Actor crush;
    private Actor friend1;
    private Actor friend2;
    private Actor partner;
    private Actor partner_friend;
    private Actor teacher;
    private Actor player;

    public SchoolTingo(User user) {
        super("Basic School");
        this.user = user;
        this.gladys = ActorsMap.getInstance().get("gladys");
        this.mom = ActorsMap.getInstance().get("mommy");
        this.teacher = ActorsMap.getInstance().get("mrs. thompson");

        this.crush = ActorsMap.getInstance().get(user.getGender().equals("male") ? "janet": "mike");
        this.friend1 = ActorsMap.getInstance().get(user.getGender().equals("male") ? "chris": "ethel");
        this.friend2 = ActorsMap.getInstance().get(user.getGender().equals("male") ? "bismark": "ella");
        this.partner = ActorsMap.getInstance().get(user.getGender().equals("male") ? "bernard": "lawrencia");
        this.partner_friend = ActorsMap.getInstance().get(user.getGender().equals("male") ? "daniel": "jessica");
        this.player = new Actor(user.getName(), "current-user");
        init();
    }

    private void init() {
        setDescription("You would be introduce to your new class and you'll also have the chance to make new friends.");
        setAim("In the following scenes. you will be introduced to some of the characters in this game.");
        addConversation(getConversationWithFriends());
        addConversation(getConversationWithCrush());
        addConversation(getConversationWithClassGroup());
        addConversation(getConversationWithMommy());
        addConversation(getConversationWithFriendsAboutParty());
        addConversation(getConversationWithCrushAboutParty());
        addConversation(getConversationWithFriendsAboutPartyIncident());
        addConversation(getConversationWithCrushAboutPartyIncident());
        addConversation(getConversationWithFriendsAboutVideo());
        addConversation(getConversationWithCrushesEx());
    }

    private Conversation getConversationWithFriends() {
        Chat chat = new Chat(user.getGender().equals("female") ? "Girl Friends." : "Boys Club.");
        chat.setId("--friends--");
        chat.setLastSeen("online");
        chat.setUnreadMessagesCount(1);
        final Conversation conversation = new Conversation(chat);
        conversation.addActor(gladys, friend1, friend2, getPlayer());

        conversation.addMessage(friend1, getPlayer(), new MessageContent("How are you?"));

        MessageGetter firstResponse = new MessageGetter(getPlayer(), friend1)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("I am fine. You?"),
                        new MessageContent("I'm good."),
                        new MessageContent("I am okay"),
                        new MessageContent("I am not so good.")
                ).setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
                    @Override
                    public void onSuccess(MessageContent content) {
                        switch (content.getData().toString()) {
                            case "I am fine. You?":
                                conversation.addInterruptMessage(new Message(friend1, getPlayer(), new MessageContent("Well I'm fine too.")));
                                break;
                            case "I'm good.":
                                conversation.addInterruptMessage(new Message(friend1, getPlayer(), new MessageContent("Awesome")));
                                break;
                            case "I am okay":
                                conversation.addInterruptMessage(new Message(friend1, getPlayer(), new MessageContent("Thats great.")));
                                break;
                            case "I am not so good.":
                                conversation.addInterruptMessage(
                                        new Message(friend1, getPlayer(), new MessageContent("What happened?")),
                                        new MessageGetter(getPlayer(), friend1),
                                        new Message(friend1, getPlayer(), new MessageContent("We will figure it out."))
                                );
                                break;
                            default:
                                conversation.addInterruptMessage(new Message(friend1, getPlayer(), new MessageContent("Well I'm great.")));
                                break;
                        }
                    }
                });
        conversation.addMessage(firstResponse);

        conversation.addMessage(friend2, getPlayer(), new MessageContent("Hey. "+(user.getGender().equals("male") ? "guys":"girls")+"."));
        conversation.addMessage(friend2, getPlayer(), new MessageContent("Hope i didn't miss anything here."));
        conversation.addMessage(friend1, getPlayer(), new MessageContent("No not really.\nBut i was just about to ask "+player.getName()
                +" why people keep calling her 'tingo'. I don't know if its a part of your name that we don't know about."));

        conversation.addMessage(friend2, getPlayer(), new MessageContent("Ow so you heard that too. What is it anyway?"));

        MessageGetter tingoResponse = new MessageGetter(getPlayer(), friend1)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new TaggedMessageContent("I don't know what it is.",
                                "Are you sure? I have never heard anyone call me that " +
                                        "plus i don't even know what that means. \nMaybe you're mistaken."),
                        new MessageContent("I think. i know what it is."),
                        new TaggedMessageContent("Really?. I had no idea.",
                                "Really?. I had no idea people called me that. " +
                                        "Does anyone know why? or what it means?")
                )
                .setContentValidator(new MessageGetter.DefaultContentValidator())
                .setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
                    @Override
                    public void onSuccess(MessageContent content) {
                        SentenceAnalyser analyser = new SentenceAnalyser(content.getData().toString());
                        analyser.cleanSentence();
                        if (content.getData().toString().equals("I think. i know what it is.")){
                            MessageGetter messageGetter = new MessageGetter(getPlayer(), friend2)
                                    .setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
                                        @Override
                                        public void onSuccess(MessageContent content) {
                                            SentenceAnalyser analyser = new SentenceAnalyser(content.getData().toString());
                                            analyser.cleanSentence();
                                            if (analyser.matchContainsAll("short") && analyser.matchContainsAny("ugly", "ugli")){
                                                conversation.addInterruptMessage(
                                                        new Message(friend1, getPlayer(),
                                                                new MessageContent("Really? Thats so not nice. " +
                                                                        "People can be really rude.")),
                                                        new Message(friend2, getPlayer(),
                                                                new MessageContent("I think you should talk " +
                                                                        "to the class prefect about it."))
                                                );
                                            }
                                            else{
                                                conversation.addInterruptMessage(
                                                        new Message(friend1, getPlayer(), new MessageContent("I don't think thats it. we should probably ask someone who might know. maybe the class prefect?."))
                                                );
                                            }
                                        }
                                    });

                            conversation.addInterruptMessage(
                                    new Message(friend2, getPlayer(), new MessageContent("What is it?")), messageGetter
                            );
                        }else {
                            conversation.addInterruptMessage(
                                    new Message(friend1, getPlayer(),
                                            new MessageContent("I think we should ask someone who might know. " +
                                                    "because i am certain people call you that.")),
                                    new Message(friend1, getPlayer(),
                                            new MessageContent("We could ask "+crush.getName()+"."))
                            );
                        }

                    }

                });
        conversation.addMessage(tingoResponse);

        MessageGetter crushResponse = new MessageGetter(getPlayer(), friend1)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("Yeah. i'll text "+(user.getGender().equals("male")? "her":"him")+"."),
                        new MessageContent("Okay. Anyone knows where he might be?.")
                )
                .setContentValidator(new MessageGetter.DefaultContentValidator())
                .setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
                    @Override
                    public void onSuccess(MessageContent content) {
                        SentenceAnalyser analyser = new SentenceAnalyser(content.getData().toString());
                        analyser.cleanSentence();
                        if (analyser.matchAny("text")){
                            conversation.addInterruptMessage(new Message(friend1, getPlayer(), new MessageContent("Yeah. you should.")));
                        }
                        else{
                            conversation.addInterruptMessage(
                                    new Message(friend2, getPlayer(),
                                            new MessageContent((user.getGender().equals("male")? "she's":"he's")+" in the library."))
                            );
                        }
                    }
                });
        conversation.addMessage(crushResponse);
        conversation.addMessage(friend2, getPlayer(), new MessageContent("We'll also look into it."));
        conversation.addMessage(friend2, getPlayer(), new MessageContent("Lets meet up here later."));
        conversation.addMessage(friend1, getPlayer(), new MessageContent("Take care guys.."));

        conversation.addMessage(new Message(gladys, getPlayer(), new MessageContent("Hello. Am gladys. your next task is to initiate a chat with your class prefect.")).setProperty("gladys-info", true));

        chat.setLastMessage(conversation.getMessages().get(0).getContent().getData().toString());
        return conversation;
    }


    private Conversation getConversationWithCrush() {
        Chat chat = new Chat(crush.getName());
        chat.setId("--crush--");
        chat.setLastSeen("online");
        chat.setUnreadMessagesCount(0);
        chat.setLastMessage("am online");
        final Conversation conversation = new Conversation(chat);
        conversation.addActor(gladys, crush, getPlayer());

        MessageGetter hiResponse = new MessageGetter(getPlayer(), crush)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(new MessageContent("Hi."), new MessageContent("Hello."), new MessageContent("Hey."))
                .setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
                    @Override
                    public void onSuccess(MessageContent content) {
                        SentenceAnalyser analyser = new SentenceAnalyser(content.getData().toString());
                        analyser.cleanSentence();
                        if (analyser.matchAny("hi")){
                            conversation.addInterruptMessage(new Message(crush, getPlayer(), new MessageContent("Hello.")));
                        }
                        else if (analyser.matchAny("hello")){
                            conversation.addInterruptMessage(new Message(crush, getPlayer(), new MessageContent("Hi.")));
                        }
                        else if (analyser.matchAny("hey")){
                            conversation.addInterruptMessage(new Message(crush, getPlayer(), new MessageContent("Hey.")));
                        }
                        else{
                            List<MessageContent> response = ChatBot.getInstance().generateContentOptions(content);
                            if (!response.isEmpty()){
                                conversation.addInterruptMessage(
                                        new Message(crush, getPlayer(), response.get(new Random().nextInt(response.size())))
                                );
                            }else{
                                conversation.addInterruptMessage(new Message(crush, getPlayer(), new MessageContent("Hello.")));
                            }
                        }
                    }
                });
        conversation.addMessage(hiResponse);

        conversation.addMessage(new Message(crush, getPlayer(), new MessageContent("How are you?.")));

        MessageGetter howResponse = new MessageGetter(getPlayer(), crush)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("Am not so good."),
                        new MessageContent("Am okay. I just wanted to ask you something."),
                        new MessageContent("Am fine."))
                .setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
                    @Override
                    public void onSuccess(MessageContent content) {
                        SentenceAnalyser analyser = new SentenceAnalyser(content.getData().toString());
                        analyser.cleanSentence();
                        if (analyser.matchAny("ask", "question")){
                            conversation.addInterruptMessage(new Message(crush, getPlayer(), new MessageContent("Okay. What is it?.")));
                        }
                        else if (analyser.matchAny("im","am", "not") && analyser.matchContainsAny("good", "okay", "well", "sad", "confuse")){
                            conversation.addInterruptMessage(new Message(crush, getPlayer(), new MessageContent("Why. What's wrong?.")));
                        }
                    }
                });
        conversation.addMessage(howResponse);

        MessageGetter feelResponse = new MessageGetter(getPlayer(), crush)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("Did you know people called me 'tingo'?."),
                        new MessageContent("Have you heard anyone ever call me a weird name?.")
                )
                .setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
                    @Override
                    public void onSuccess(MessageContent content) {
                        SentenceAnalyser analyser = new SentenceAnalyser(content.getData().toString());
                        analyser.cleanSentence();
                        if (analyser.matchAny("tingo") || analyser.matchAny("name")){
                            conversation.addInterruptMessage(new Message(crush, getPlayer(),
                                    new MessageContent("Yeah. 'tingo'. some people do. I almost called you that too. " +
                                            "I thought it was your name until i found out its actually not. \nBut don't worry about it.")),
                                    new Message(crush, getPlayer(), new MessageContent("You should be studying for our mid sem exams. That's what's important right now."))
                            );
                        }
                    }
                });
        conversation.addMessage(feelResponse);

        MessageGetter aboutResponse = new MessageGetter(getPlayer(), crush)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("What does it means?."),
                        new MessageContent("Please tell me what it means."),
                        new MessageContent("I don't even want to know what it means.")
                )
                .setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
                    @Override
                    public void onSuccess(MessageContent content) {
                        SentenceAnalyser analyser = new SentenceAnalyser(content.getData().toString());
                        analyser.cleanSentence();
                        if (analyser.isPositive() && analyser.matchContainsAny("does", "mean", "why", "call")){
                            conversation.addInterruptMessage(
                                    new Message(crush, getPlayer(), new MessageContent("Well from what i understand, " +
                                            "it means you're the shortest and less pretty person amongst your friends.")),
                                    new Message(crush, getPlayer(),
                                            new MessageContent("But its so not true. You're a very nice person. " +
                                                    "I don't know why people can be so mean."))
                            );
                        }
                        else{
                            conversation.addInterruptMessage(
                                    new Message(crush, getPlayer(), new MessageContent("Well whatever it means. if its a bad thing. i know its so not true because you're a very nice person."))
                            );
                        }
                    }
                });
        conversation.addMessage(aboutResponse);

        conversation.addMessage(crush, getPlayer(), new MessageContent("I think you should go see the guidance and counselling teacher. Talk to her about it."));

        MessageGetter byeResponse = new MessageGetter(getPlayer(), crush)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("Okay. ill talk to you later. take care."),
                        new MessageContent("Alright. i have to go now. thank you."),
                        new MessageContent("Okay.")
                )
                .setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
                    @Override
                    public void onSuccess(MessageContent content) {
                        SentenceAnalyser analyser = new SentenceAnalyser(content.getData().toString());
                        analyser.cleanSentence();
                        if (analyser.matchContainsAny("take", "care", "bye", "later")){
                            conversation.addInterruptMessage(new Message(crush, getPlayer(),
                                    new MessageContent("Take care. And don't forget to study for the exam."))
                            );
                        }
                    }
                });
        conversation.addMessage(byeResponse);



        return conversation;
    }


    private Conversation getConversationWithClassGroup() {
        Chat classChat = new Chat("Class Group");
        classChat.setId("--class-group--");
        classChat.setLastSeen("online");
        classChat.setUnreadMessagesCount(1);
        final Conversation conversation = new Conversation(classChat);
        conversation.addActor(gladys, crush, friend1, friend2, teacher, getPlayer());

        conversation.addMessage(teacher, getPlayer(), new MessageContent("Hi class. how are you all?."));
        conversation.addMessage(friend1, teacher, new MessageContent("Am good."));
        conversation.addMessage(friend2, teacher, new MessageContent("Am fine."));

        conversation.addMessage(new MessageGetter(getPlayer(), crush, 10000));

        conversation.addMessage(crush, teacher, new MessageContent("Am sure everyone's good."));
        conversation.addMessage(teacher, getPlayer(), new MessageContent("Hope everyone is studying for the mid sem exams?."));
        conversation.addMessage(crush, teacher, new MessageContent("Yes madam. we are."));
        conversation.addMessage(friend1, teacher, new MessageContent("Were trying."));

        conversation.addMessage(new MessageGetter(getPlayer(), crush, 10000));

        conversation.addMessage(teacher, getPlayer(), new MessageContent("Okay that's good."));
        conversation.addMessage(teacher, getPlayer(), new MessageContent("I have a surprise for any of you that will do really well in the exam. so don't stop studying."));

        conversation.addMessage(new Message(gladys, getPlayer(), new MessageContent("class group offline"))
                .setProperty("gladys-info", true));

        classChat.setLastMessage(conversation.getMessages().get(0).getContent().getData().toString());
        return conversation;
    }


    private Conversation getConversationWithMommy() {
        Chat mommyChat = new Chat(mom.getName());
        mommyChat.setId("--mommy--");
        mommyChat.setLastSeen("online");
        mommyChat.setUnreadMessagesCount(1);
        final Conversation conversation = new Conversation(mommyChat);
        conversation.addActor(gladys, mom, getPlayer());

        conversation.addMessage(mom, getPlayer(), new MessageContent("How are you?" + ((user.getGender().equals("male")) ? "  Son." : "") + " and how's school?"));

        MessageGetter firstResponse = new MessageGetter(getPlayer(), mom)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new TaggedMessageContent("Tell your about the exams.",
                                "Am fine mom and we have an upcoming exams. i am even about to go study."),
                        new TaggedMessageContent("Tell your mom about the 'tingo' name.",
                                "Mom am not okay. Some students are being mean to me in school. " +
                                        "They're calling me 'tingo' and they say its because am short and not pretty. " +
                                        "They text me mean things and insults. I want to stop going to school."),
                        new TaggedMessageContent("Tell her nothing",
                                "Everything fine mom.  i have to go to do something offline")
                )
                .setContentValidator(new MessageGetter.DefaultContentValidator())
                .setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
                    @Override
                    public void onSuccess(MessageContent content) {
                        SentenceAnalyser analyser = new SentenceAnalyser(content.getData().toString());
                        analyser.cleanSentence();
                        if (analyser.matchContainsAny("exam")){
                            conversation.addInterruptMessage(new Message(mom, getPlayer(), new MessageContent("Alright. you should study hard. Dinner would be ready soon. I'll let you know when am done..")));
                        }else if (analyser.matchContainsAny("tingo")){
                            conversation.addInterruptMessage(new Message(mom, getPlayer(),
                                            new MessageContent("What? That's so mean. Ill have to go see your head master tomorrow. I cant believe this.")),
                                    new Message(mom, getPlayer(), new MessageContent("Don't listen to them okay. you're beautiful as you are. Any one who tells you otherwise is just jealous.")),
                                    new Message(mom, getPlayer(), new MessageContent("Ill talk to your dad about it. We need to let the school know."))
                            );
                        }
                        else{
                            conversation.addInterruptMessage(new Message(mom, getPlayer(), new MessageContent("Dinner would be ready soon. I'll let you know when am done..")));
                            conversation.addInterruptMessage(new Message(mom, getPlayer(), new MessageContent("Don't forget to do any homework you may have.")));
                        }
                    }
                });
        conversation.addMessage(firstResponse);


        mommyChat.setLastMessage(conversation.getMessages().get(0).getContent().getData().toString());
        return conversation;
    }


    private Conversation getConversationWithFriendsAboutParty() {
        Chat chat = new Chat(user.getGender().equals("female") ? "Girl Friends." : "Boys Club.");
        chat.setId("--friends--");
        chat.setLastSeen("online");
        chat.setUnreadMessagesCount(1);
        final Conversation conversation = new Conversation(chat);
        conversation.addActor(gladys, friend1, friend2, getPlayer());

        conversation.addMessage(new Message(friend1, getPlayer(), new MessageContent("Hey. "+(user.getGender().equals("female") ? "girls." : "guys.")+" Did you know there's going to be a party after the mid sem exams")));
        conversation.addMessage(new Message(friend2, getPlayer(), new MessageContent("Yeah. i heard some of the school kids organized it.")));
        conversation.addMessage(new Message(friend1, getPlayer(), new MessageContent("Are we going?. "+player.getName()+" what you think?")));
        MessageGetter response = new MessageGetter(getPlayer(), friend1)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("yes. i think we should"),
                        new MessageContent("no. i have stuff to do."),
                        new MessageContent("maybe."), new MessageContent("i have to study.")
                ).setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
                    @Override
                    public void onSuccess(MessageContent content) {
                        SentenceAnalyser analyser = new SentenceAnalyser(content.getData().toString());
                        analyser.cleanSentence();
                        if (analyser.matchContainsAny("yes", "maybe", "sure")){
                            conversation.addInterruptMessage(
                                    new Message(friend1, getPlayer(), new MessageContent("Alright. so were going..")),
                                    new Message(friend2, getPlayer(), new MessageContent("I'm so excited."))
                            );
                        }
                        else{
                            conversation.addInterruptMessage(
                                    new Message(friend1, getPlayer(), new MessageContent("We're taking you no matter what you say.")),
                                    new Message(friend1, getPlayer(), new MessageContent("I promise its goin to be fun."))
                            );
                        }
                    }
                });
        conversation.addMessage(response);
        conversation.addMessage(new Message(friend2, getPlayer(), new MessageContent("Its your first school party. you should be excited.")));
        conversation.addMessage(new Message(friend1, getPlayer(),
                new MessageContent("Ask your parents if you have to. and if they don't let you. " +
                        "Well come there and convince them. \nOr you could always sneak out ;-)")));
        conversation.addMessage(new MessageGetter(getPlayer(), friend2, 15000));
        conversation.addMessage(new Message(friend2, getPlayer(), new MessageContent("Haha. Thats funny.")));

        chat.setLastMessage(conversation.getMessages().get(0).getContent().getData().toString());
        return conversation;
    }


    private Conversation getConversationWithCrushAboutParty() { //mike or janet
        Chat chat = new Chat(crush.getName());
        chat.setId("--crush--");
        chat.setLastSeen("online");
        chat.setUnreadMessagesCount(1);
        final Conversation conversation = new Conversation(chat);
        conversation.addActor(gladys, crush, getPlayer());

        conversation.addMessage(new Message(crush, getPlayer(), new MessageContent("Hello "+player.getName()+". Did you hear about the party?. I hope you're going.")));
        MessageGetter response = new MessageGetter(getPlayer(), crush)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(new MessageContent("Yeah. with my friends."),
                        new MessageContent("Yeah. My "+ (user.getGender().equals("female") ? "girls" : "guys")+" are making me go."),
                        new MessageContent("No. not going."));
        conversation.addMessage(response);
        conversation.addMessage(new Message(crush, getPlayer(), new MessageContent("Well am going with my "+
                (user.getGender().equals("male") ? "boyfriend. " : "girlfriend. ")+partner.getName()+
                ". you know "+(user.getGender().equals("male") ? "him" : "her")+" right?.")));

        MessageGetter messageGetter = new MessageGetter(getPlayer(), crush)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("no. not really."),
                        new MessageContent("yes. i think so"),
                        new MessageContent("maybe. am not sure")
                ).setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
                    @Override
                    public void onSuccess(MessageContent content) {

                        conversation.addInterruptMessage(
                                new Message(crush, getPlayer(), new MessageContent("You might get to see her at the party. That's if you show up.")),
                                new Message(crush, getPlayer(), new MessageContent("I really hope you do.")),
                                new Message(crush, getPlayer(), new MessageContent("If you want you could go with i and "+
                                        partner.getName()+ " Am sure "+
                                        (user.getGender().equals("male") ? "he" : "she")+ " wont mind.")),
                                new MessageGetter(getPlayer(), crush)
                                        .setExpectedContentType(TYPE_TEXT)
                                        .addContentOptions(new MessageContent("okay"), new MessageContent("ill think about it.")),
                                new Message(crush, getPlayer(),
                                        new MessageContent("And Ill be going to get some new clothes " +
                                                "for the party. you can tag along with me.. if you want to.")),
                                new MessageGetter(getPlayer(), crush)
                                        .setExpectedContentType(TYPE_TEXT)
                                        .addContentOptions(
                                                new MessageContent("yes. sure."),
                                                new MessageContent("no. am okay. thanks anyway.")
                                        ),
                                new Message(crush, getPlayer(), new MessageContent("Okay. Take care. (:-)."))
                        );
                    }
                });
        conversation.addMessage(messageGetter);


        chat.setLastMessage(conversation.getMessages().get(0).getContent().getData().toString());
        return conversation;
    }


    private Conversation getConversationWithFriendsAboutPartyIncident() {
        Chat chat = new Chat(user.getGender().equals("female") ? "Girl Friends." : "Boys Club.");
        chat.setId("--friends--");
        chat.setLastSeen("online");
        chat.setUnreadMessagesCount(1);
        final Conversation conversation = new Conversation(chat);
        conversation.addActor(gladys, friend1, friend2, getPlayer());

        conversation.addMessage(new Message(friend1, getPlayer(),
                new MessageContent("Hey. "+player.getName()+". How are you. I can't be that happened to you at the party.")));
        conversation.addMessage(new Message(friend2, getPlayer(),
                new MessageContent("Everything was going great. I saw "+player.getName()+" dancing with "+
                crush.getName()+(user.getGender().equals("female") ? ". she" : ". he.")+
                " looked so happy. i think "+ partner.getName() +
                " saw them and got really jealous. That's why they did what they did.")));
        conversation.addMessage(new Message(friend1, getPlayer(),
                new MessageContent("Yeah. It was so not cool. Everyone was surprised when "+
                partner.getName()+" and "+partner_friend.getName()+" shouted 'tingo' during the dance. But they all laughed."+
                "\ni don't even think "+crush.getName()+" was happy with what "+partner.getName()+" did.")));

        conversation.addMessage(new Message(friend1, getPlayer(),
                new MessageContent(player.getName()+". We're really sorry okay. We should have stayed home.")));
        conversation.addMessage(new Message(friend2, getPlayer(), new MessageContent(
                "I'm just hoping no one one took a video when she poured the drink on "+
                player.getName()+" and pretended it was an accident.")));
        conversation.addMessage(new Message(friend1, getPlayer(),
                new MessageContent("Students in this school like to make silly thing go viral. I hate that about this school.")));
        conversation.addMessage(new Message(friend1, getPlayer(), new MessageContent("You should tell your mom about it.")));
        conversation.addMessage(new Message(friend2, getPlayer(),
                new MessageContent("And am definitely telling. "+ teacher.getName()+" what "+partner.getName()+" did.")));


        chat.setLastMessage(conversation.getMessages().get(0).getContent().getData().toString());
        return conversation;
    }


    private Conversation getConversationWithCrushAboutPartyIncident() {
        Chat chat = new Chat(crush.getName());
        chat.setId("--crush--");
        chat.setLastSeen("online");
        chat.setUnreadMessagesCount(1);
        final Conversation conversation = new Conversation(chat);
        conversation.addActor(gladys, crush, getPlayer());

        conversation.addMessage(new Message(crush, getPlayer(),
                new MessageContent("Hello. "+player.getName()+". I hope you're good?.")));
        conversation.addMessage(new MessageGetter(getPlayer(), crush, 30000));
        conversation.addMessage(new Message(friend2, getPlayer(),
                new MessageContent("I just wanted to tell you am really sorry about what happened at the party." +
                        "And that i broke things off with "+(user.getGender().equals("male") ? "him" : "her.")+
                        "People should not be that mean.")));


        chat.setLastMessage(conversation.getMessages().get(0).getContent().getData().toString());
        return conversation;
    }


    private Conversation getConversationWithFriendsAboutVideo() {
        Chat chat = new Chat(user.getGender().equals("female") ? "Girl Friends." : "Boys Club.");
        chat.setId("--friends--");
        chat.setLastSeen("online");
        chat.setUnreadMessagesCount(1);
        final Conversation conversation = new Conversation(chat);
        conversation.addActor(gladys, friend1, friend2, getPlayer());

        conversation.addMessage(new Message(friend1, getPlayer(),
                new MessageContent(player.getName()+". There's bad news.")));

        MessageGetter messageGetter = new MessageGetter(getPlayer(), friend1)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("what bad news?."),
                        new MessageContent("i can't take any more bad news."),
                        new MessageContent("whats is it?.")
                ).setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
                    @Override
                    public void onSuccess(MessageContent content) {
                        SentenceAnalyser analyser = new SentenceAnalyser(content.getData().toString());
                        analyser.cleanSentence();
                        if (analyser.isPositive() && analyser.matchAny("what", "new")){
                            conversation.addInterruptMessage(new Message(friend1, getPlayer(),
                                    new MessageContent("i'm sorry but i think the party incident went viral. " +
                                            "Apparently someone uploaded it on youtube and everyone's talking about it in school.")),
                                    new Message(friend1, getPlayer(), new MessageContent("i'm so sorry."))
                            );
                        }
                        else{
                            conversation.addInterruptMessage(new Message(friend1, getPlayer(),
                                    new MessageContent("Yeah. I don't think you should bother yourself with it."))
                            );
                        }
                    }
                });
        conversation.addMessage(messageGetter);

        conversation.addMessage(new Message(friend2, getPlayer(),
                new MessageContent("Hmm. It'll surely pass. They forget about it soon enough.")));

        conversation.addMessage(new Message(friend2, getPlayer(),
                new MessageContent(friend1.getName()+" you know what i think we should do?")));

        conversation.addMessage(new Message(friend2, getPlayer(),
                new MessageContent("We need to tell "+partner.getName()+"'s parent. They should take away her phone or something.")));

        conversation.addMessage(new Message(friend2, getPlayer(),
                new MessageContent("And "+player.getName()+" needs a new look. people need to stop calling her by that awful name.")));

        conversation.addMessage(new Message(friend1, getPlayer(),
                new MessageContent("Ow. and a good hobby. like acting or cheerleading or something with sport. "+
                        player.getName()+" what do you think.")));

        conversation.addMessage(new Message(friend2, getPlayer(),
                new MessageContent("Yeah it does'nt matter the awful things people say to you. you can do anything you set your mind to.")));



        chat.setLastMessage(conversation.getMessages().get(0).getContent().getData().toString());
        return conversation;
    }


    private Conversation getConversationWithCrushesEx() {
        Chat chat = new Chat(partner.getName());
        chat.setId("--crush-ex--");
        chat.setLastSeen("online");
        chat.setUnreadMessagesCount(1);
        final Conversation conversation = new Conversation(chat);
        conversation.addActor(gladys, partner, getPlayer());

        conversation.addMessage(new Message(partner, getPlayer(),
                new MessageContent("Hello. "+player.getName()+".")));

        MessageGetter messageGetter = new MessageGetter(getPlayer(), partner)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("Hi."),
                        new MessageContent("What do you want from me now?"),
                        new MessageContent("Please don't text me.")
                ).setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
                    @Override
                    public void onSuccess(MessageContent content) {
                        conversation.addInterruptMessage(new Message(partner, getPlayer(),
                                new MessageContent("I'm so sorry about what i did to you. You know before you got here. i used to be bullied until i met "+
                                        crush.getName()+". All of sudden i got this fame and i started being mean to people. i forgot how it felt to be the one being bullied." +
                                        " What i did was wrong.. and i understand if you can't forgive me but i'd like to be your friend too. " +
                                        "and make right with you.")),
                                new Message(friend1, getPlayer(), new MessageContent("I'm so so sorry."))
                        );
                    }
                });
        conversation.addMessage(messageGetter);


        MessageGetter messageGetter1 = new MessageGetter(getPlayer(), partner)
                .setExpectedContentType(TYPE_TEXT)
                .addContentOptions(
                        new MessageContent("Its okay. I forgive you. and we can be friends."),
                        new MessageContent("Fine. I forgive you. but we cant be friends."),
                        new MessageContent("I don't want to talk to you. Please stop texting me")
                ).setOnReceiveContentCompleteListener(new MessageGetter.OnReceiveContentCompleteListener() {
                    @Override
                    public void onSuccess(MessageContent content) {
//                        SentenceAnalyser analyser = new SentenceAnalyser(content.getData().toString());
//                        analyser.cleanSentence();
                        conversation.addInterruptMessage(new Message(partner, getPlayer(), new MessageContent("Okay. well take care..And again i'm so sorry.")));
                    }
                });
        conversation.addMessage(messageGetter1);


        chat.setLastMessage(conversation.getMessages().get(0).getContent().getData().toString());
        return conversation;
    }




    public Actor getPlayer() {
        return player;
    }

}
