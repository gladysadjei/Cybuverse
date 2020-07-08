package com.gladys.cybuverse.CyberBullyingAwarenessGame.Base;

import com.gladys.cybuverse.CyberBullyingAwarenessGame.ProfanityChecker.ProfanityFilter;
import com.gladys.cybuverse.Helpers.TechupApplication;
import com.gladys.cybuverse.Utils.GeneralUtils.collections.graph.Edge;
import com.gladys.cybuverse.Utils.GeneralUtils.collections.graph.Graph;
import com.gladys.cybuverse.Utils.GeneralUtils.collections.graph.ListGraph;
import com.gladys.cybuverse.Utils.GeneralUtils.collections.graph.Vertice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ChatBotBrain {

    private ListGraph<String> conversationGraph = new ListGraph<>();
    private ProfanityFilter profanityFilter = TechupApplication.getProfanityFilter();
    private SentenceAnalyser analyser;
    private String command;

    public ChatBotBrain() {
        initializeConversationGraph();
    }

    private void initializeConversationGraph() {
        Thread task = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    addDefaultEdges();
                } catch (Graph.InvalidVerticeException e) {
                    e.printStackTrace();
                }
            }
        });
        task.run();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public SentenceAnalyser getAnalyser() {
        return analyser;
    }

    public ListGraph<String> getConversationGraph() {
        return conversationGraph;
    }

    public void setConversationGraph(ListGraph<String> conversationGraph) {
        this.conversationGraph = conversationGraph;
    }

    public ChatBotResponse process(String command) {
        this.command = command;
        this.analyser = new SentenceAnalyser(command);
        this.analyser.cleanSentence();
        return resolve();
    }

    private ChatBotResponse resolve() {
        List<String> badWords;

        if (profanityFilter != null && (badWords = getBadWords()).size() > 0)
            return createResponseList().addResponse("Heyy.. please don't use "+(badWords.size() == 1 ? "this": "these")+" words "+
                    badWords.toString().replace("[", "'").replace("]", "'")+
                    ". people may find them offensive and rude. and you should always be nice to everyone");

        else if (analyser.matchAll("show") && analyser.isPositive()) {
            if (analyser.matchAny("help")) {
                return showHelp();
            } else if (analyser.matchAny("my")) {
                return retrieveUserInfo();
            } else if (analyser.matchAny("your")) {
                return retrieveGladysInfo();
            }
        } else if (analyser.matchExactly("yes", "no", "yeah", "sure", "nothing", "okay", "ok",
                "alright", "fine", "good", "better", "happy", "great", "awesome", "blessed",
                "wonderful", "excited", "pleasure")) {
            return createResponseList(new String[]{"okay", "cool"}[new Random().nextInt(2)]);
        } else if (analyser.matchExactly("really", "seriously", "huh", "wow")) {
            return createResponseList(new String[]{"yeah", "unhuh", "yes", "yhup"}[new Random().nextInt(4)]);
        } else if (analyser.matchAny("yes", "no", "yeah", "sure", "nothing", "ok")) {
            return createResponseList(new String[]{"okay", "cool"}[new Random().nextInt(2)]);
        } else if (analyser.matchAll("log", "me", "out") || analyser.matchAll("log", "out")
                || analyser.matchExactly("logout") && analyser.isPositive()) {
            return createResponseList("i hope to see you again. please wait while i try to log you out.")
                    .setFunction("logout-user");
        } else if (analyser.matchAll("about", "game")) {
            return createResponseList("Well. this game hopes to educate you about cyber-bullying," +
                    " how its affects you and the people you bully" +
                    " and also train you to recognize the some acts " +
                    "that may be offensive to others and how to handle being cyber bullied.");
        } else if (analyser.matchAll("about") && analyser.matchAny("your", "self", "yourself", "gladys")) {
            return createResponseList("My name is gladys and i'm an AI assistant created" +
                    " with the sole purpose of helping you and making this game interesting and easier for you.");
        } else if (analyser.matchAll("who", "is") || analyser.matchAny("whos")) {
            if (analyser.matchAny("you", "your"))
                return retrieveGladysInfo();
            return getInformationAboutActor();
        } else if (analyser.matchAny("my", "me")) {
            return retrieveUserInfo();
        } else if (analyser.matchAny("you", "your", "youre", "yourself")) {
            return retrieveGladysInfo();
        } else if (analyser.matchAny("am", "im") || analyser.matchAll("i", "am")) {
            if (analyser.matchAny("okay", "alright", "fine", "good", "better", "happy", "ok",
                    "chilling", "great", "awesome", "blessed", "wonderful", "excited"))
                return createResponseList(new String[]{"great", "okay", "cool", "awesome", "wonderful"}[new Random().nextInt(5)]);

            try {
                List<Edge<String>> edges = conversationGraph.getEdges(analyser.getSentence());
                return createResponseList(edges.get(new Random().nextInt(edges.size())).getEndPoint()).setFunction("send-to-admins");
            } catch (Graph.InvalidVerticeException e) {
                e.printStackTrace();
            }
        } else {
            try {
                List<Edge<String>> edges = conversationGraph.getEdges(analyser.getSentence());
                return createResponseList(edges.get(new Random().nextInt(edges.size())).getEndPoint());
            } catch (Graph.InvalidVerticeException e) {
                e.printStackTrace();
            }
        }

        return createResponseList("i can't response to your message now please " +
                "type show help for info on how to properly work with me.\n" +
                "ill get back to you soon with a response").setFunction("send-to-admins");
    }

    private List<String> getBadWords() {
        final List<String> matches = new ArrayList<>();
        for (String word : analyser.getSentence().split(" ")) {
            if (profanityFilter.isMatchForWord(word.toLowerCase(), true)) {
                matches.add(word);
            }
        }
        return matches;
    }

    private ChatBotResponse getInformationAboutActor() {
        //TODO: get all actors from story and check for match in sentence if match send info
        String name = analyser.getSentence()
                .replaceFirst("who", "")
                .replaceFirst(" is", "")
                .replace("whos", "").trim();

        Actor actor = ActorsMap.getInstance().get(name);

        if (actor != null)
            return createResponseList(name + ": " + actor.getRole());
        else return createResponseList("Am so sorry. i don't recall this name right now. " +
                "but ill let you know when i remember.").setFunction("send-to-admins");
    }

    private ChatBotResponse showHelp() {
        return createResponseList("you can do so much fun stuff with me." +
                        " i can help you change your profile." +
                        "you can ask me for information about a chat or a scene." +
                        "you can also check your points with me.",
                "you can show your info by simply typing show my 'info you want' or what my 'info you want'...\n" +
                        "you can ask for your name, email, points or progress",
                "find out information about a friend in your chat list or in your groups.\n" +
                        "just type who is and the name of the friend.",
                "to find out stuff about me. type whats your 'information you want to know'." +
                        " and ill you what you want to know.",
                "if i am unable to help you please don't panic..\n" +
                        "ill ask my supervisor and ill get back to you with a response.");
    }

    private ChatBotResponse retrieveUserInfo() {
        if (analyser.matchAny("name"))
            return createResponseList().addResponse(new ChatBotResponse.Response("your name is <user-name>", "<user-name>"));
        else if (analyser.matchAny("email"))
            return createResponseList().addResponse(new ChatBotResponse.Response("your email is <user-email>", "<user-email>"));
        else if (analyser.matchAny("points", "point"))
            return createResponseList().addResponse(new ChatBotResponse.Response("your have <user-points> points", "<user-points>"));
        else if (analyser.matchAny("progress"))
            return createResponseList().addResponse(new ChatBotResponse.Response("<user-progress>", "<user-progress>"));
        else if (analyser.matchAny("progress"))
            return createResponseList().addResponse(new ChatBotResponse.Response("<user-progress>", "<user-progress>"));
        else if (analyser.matchAll("tell", "story", "stories"))
            return createResponseList().addResponse(new ChatBotResponse.Response("<user-progress>", "<user-progress>"));

        return createResponseList("Sorry... i don't have an answer now but ill get back" +
                        " to you soon. you'll get an alert if i get an answer",
                "you can use 'show help' to checkout what other things i can do.").setFunction("send-to-admins");
    }

    private ChatBotResponse retrieveGladysInfo() {
        List<String> badWords;

        if (analyser.matchAny("name"))
            return createResponseList().addResponse(new ChatBotResponse.Response("my name is <gladys-name>", "<gladys-name>"));
        else if (analyser.matchAny("email"))
            return createResponseList().addResponse(new ChatBotResponse.Response("my email is <gladys-email>", "<gladys-email>"));
        else if (analyser.matchAny("hobby"))
            return createResponseList().addResponse(new ChatBotResponse.Response("i love <gladys-hobby>", "<gladys-hobby>"));
        else if (analyser.matchAny("job"))
            return createResponseList().addResponse(new ChatBotResponse.Response("my job is to <gladys-job>", "<gladys-job>"));
        else if (analyser.matchAny("gladys"))
            return createResponseList().addResponse(new ChatBotResponse.Response("yes am gladys and you're <user-name>", "<user-name>"));
        else if (analyser.matchAny("male"))
            return createResponseList().addResponse("No silly... I'm female.");
        else if (analyser.matchAny("religion"))
            return createResponseList().addResponse("I'm a mathologist. Its a real thing.");
        else if (analyser.matchAny("tribe", "history"))
            return createResponseList().addResponse("I'm from a very new tribe. We're called AI. We speak a lot of languages but i prefer Java or Python..");
        else if (analyser.matchAny("female"))
            return createResponseList().addResponse("yes am female.");
        else if (analyser.matchAny("dating", "boyfriend", "boyfriends"))
            return createResponseList().addResponse("i don't have a boyfriend yet... i'm still searching.. " +
                    "one day ill meet the perfect ai that would steal my heart.." +
                    " i kinda don't have a heart so my code...");
        else if (analyser.matchAny("about"))
            return createResponseList().addResponse("my name is Gladys and i am an AI bot... " +
                    "my job is to help you and give you the necessary information about the game." +
                    " i can also do neat ricks like logging you out or showing you some of your information." +
                    " type 'show help' to learn more");
        else if (analyser.matchAny("girlfriend", "girl", "friend", "girlfriends", "girls", "friends"))
            return createResponseList().addResponse("well am kinda new so i don't have any friends yet... " +
                    "i'll ask siri and googlerella if they would like to hang sometime... " +
                    "\nunless you want to be my friend?.");
        else if (analyser.matchAny("ai") || analyser.matchAll("artificial", "intelligence"))
            return createResponseList().addResponse("yes .but i am not complete yet.");
        else if (analyser.matchAny("bot", "robot", "machine"))
            return createResponseList().addResponse("No. Am just a program. about 10,000 lines of code...");
        else if (analyser.matchAny("how", "going"))
            return createResponseList().addResponse(new String[]{
                    "i'm okay", "i'm good. you?", "i'm fine. thanks for asking", "i'm doing okay. you?"
            }[new Random().nextInt(4)]);
        else if (profanityFilter != null && (badWords = getBadWords()).size() > 0)
            return createResponseList().addResponse("Heyy.. please don't use "+(badWords.size() == 1 ? "this": "these")+" words "+
                    badWords.toString().replace("[", "'").replace("]", "'")+
                    ". people may find them offensive and rude. and you should always be nice to everyone");
        else if (analyser.matchAny("fuck", "shit", "bitch", "asshole", "stupid", "fool", "idiot", "nigger", "niger", "niga", "nigga"))
            return createResponseList().addResponse("Heyy.. Thats not nice. you're supposed to be good to everyone.");
        else if (analyser.matchAny("i") && analyser.isPositive()) {
            if (analyser.matchAny("like"))
                return createResponseList().addResponse("Well... i like you too.");
            else if (analyser.matchAny("enjoy"))
                return createResponseList().addResponse("Thanks", "Its been my pleasure meeting you.");
            else if (analyser.matchAny("love"))
                return createResponseList().addResponse("Okay... but i think loving a human might be so much better.");
            else if (analyser.matchAny("hate"))
                return createResponseList().addResponse("Why what did i do?.").setFunction("send-to-admins");
        } else if (analyser.matchAny("i", "im", "am", "me") && !analyser.isPositive())
            if (analyser.matchContainsAny("like", "love", "enjoy"))
                return createResponseList().addResponse("Really... could you please let me know what i did wrong?.").setFunction("send-to-admins");

        try {
            List<Edge<String>> edges = conversationGraph.getEdges(analyser.getSentence());
            return createResponseList(edges.get(new Random().nextInt(edges.size())).getEndPoint());
        } catch (Graph.InvalidVerticeException e) {
            e.printStackTrace();
        }

        return createResponseList("Sorry... i don't know what to tell you. but ill get back to you later. " +
                "you can ask for my name, email, hobby, job or stuff like that.").setFunction("send-to-admins");
    }

    private ChatBotResponse createResponseList(String... responses) {
        return new ChatBotResponse(new ArrayList<>(Arrays.asList(responses)));
    }

    private void addDefaultEdges() throws Graph.InvalidVerticeException {
        addEdge("Hi", "Hello", "Hi", "Hey");
        addEdge("Hello", "Hello", "Hi", "Hey");
        addEdge("Hey", "Hello", "Hi", "Hey");
        addEdge("Yes", "Okay", "Alright");
        addEdge("No", "Okay", "Alright");
        addEdge("How are you", "Am good", "Am fine", "Am good. You");
        addEdge("Im fine You", "Am fine too");
        addEdge("Where are you", "Am always here");
        addEdge("Thank you", "you're welcome");
        addEdge("Thanks", "you're welcome.");
        addEdge("youre welcome", "Thank you.");
        addEdge("you welcome", "Thank you.");
        addEdge("you're welcome", "Thank you.");
        addEdge("Are you there", "Am always here");
        addEdge("Cool", "Yeah", "Yhup");
        addEdge("Okay", "Okay", "Yeah", "Yhup");
        addEdge("Whats up", "Nothing fun. What about you?");
        addEdge("Whatsup", "Nothing fun. What about you?");
        addEdge("Whats happening", "Nothing fun. What about you?");
        addEdge("Whats good", "Nothing fun. What about you?", "I've been trying to read a book. What about you?");
        addEdge("Am sad", "Sorry.. whats wrong?");
        addEdge("Sorry", "Its cool", "Its okay", "Thank you");
    }

    private void addEdge(String message, String... responses) throws Graph.InvalidVerticeException {
        message = message.trim().toLowerCase();
        if (!conversationGraph.hasVertice(message))
            conversationGraph.addVertice(new Vertice<>(message));
        for (String response : responses) {
            String responseClean = response.trim().toLowerCase();
            if (!conversationGraph.hasVertice(responseClean))
                conversationGraph.addVertice(new Vertice<>(responseClean));
            conversationGraph.addEdge(new Edge<>(message, responseClean));
        }
    }

    private void addEdge(String message, String response) throws Graph.InvalidVerticeException {
        message = message.trim().toLowerCase();
        response = response.trim().toLowerCase();
        if (!conversationGraph.hasVertice(message))
            conversationGraph.addVertice(new Vertice<>(message));
        if (!conversationGraph.hasVertice(response))
            conversationGraph.addVertice(new Vertice<>(response));
        conversationGraph.addEdge(new Edge<>(message, response));
    }

    private void addEdge(Edge<String> edge) throws Graph.InvalidVerticeException {
        String message = edge.getStartPoint();
        String response = edge.getEndPoint();
        message = message.trim().toLowerCase();
        response = response.trim().toLowerCase();
        if (!conversationGraph.hasVertice(message))
            conversationGraph.addVertice(new Vertice<>(message));
        if (!conversationGraph.hasVertice(response))
            conversationGraph.addVertice(new Vertice<>(response));
        conversationGraph.addEdge(new Edge<>(message, response));
    }


}
