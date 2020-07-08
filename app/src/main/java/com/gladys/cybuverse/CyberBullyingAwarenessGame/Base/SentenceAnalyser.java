package com.gladys.cybuverse.CyberBullyingAwarenessGame.Base;

import java.util.HashSet;
import java.util.List;

public class SentenceAnalyser {

    private final boolean ignoreCasing;
    private String sentence;

    public SentenceAnalyser(String sentence) {
        this.ignoreCasing = true;
        this.sentence = sentence;
    }

    public SentenceAnalyser(String sentence, boolean ignoreCasing) {
        this.ignoreCasing = ignoreCasing;
        this.sentence = sentence;
    }

    public static String cleanSentence(String sentence) {
        sentence = sentence.trim();
        sentence = sentence.replace("?", "")
                .replace("'", "")
                .replace(".", "")
                .replace("!", "")
                .replace("\n", " ")
                .replace("  ", " ");

        if (sentence.toLowerCase().contains("so") && sentence.toLowerCase().indexOf("so") == 0) {
            String[] splitted = sentence.split(" ", 1);
            if (splitted.length > 1 && splitted[0].toLowerCase().charAt(splitted[0].length() - 1) == 'o')
                sentence = splitted[1];
        }

        return sentence.trim();
    }

    public void cleanSentence() {
        this.sentence = cleanSentence(this.sentence);
    }

    public boolean isPositive() {
        boolean state = true;

        String[] parts = sentence.split(" ");
        for (String word : parts) {
            if (isNegative(word)) {
                state = !state;
            }
        }
        return state;
    }

    public HashSet getVerbs() {
        List<String> verbsFromQuestion = getVerbsFromQuestion();
        List<String> verbsFromStatement = getVerbsFromStatement();
        verbsFromQuestion.addAll(verbsFromStatement);
        return new HashSet(verbsFromQuestion);
    }

    private List<String> getVerbsFromStatement() {
        return null;
    }

    private List<String> getVerbsFromQuestion() {
        return null;
    }

    private boolean isNegative(String word) {
        return checkMatchContains(word.toLowerCase(),
                new String[]{"cant", "wont", "dont", "can't", "won't", "don't", "not", "nt", "n't"});
    }

    private boolean checkMatch(String word, String[] array) {
        for (String i : array) {
            if (!ignoreCasing && i.trim().equals(word.trim()))
                return true;
            else if (ignoreCasing && i.trim().toLowerCase().equals(word.trim().toLowerCase()))
                return true;
        }
        return false;
    }

    private boolean checkMatchContains(String word, String[] array) {
        for (String i : array) {
            if (!ignoreCasing && i.trim().contains(word.trim()))
                return true;
            else if (ignoreCasing && i.trim().toLowerCase().contains(word.trim().toLowerCase()))
                return true;
        }
        return false;
    }

    public boolean matchAny(String... array) {
        String[] parts = sentence.trim().split(" ");
        for (String word : array) {
            if (checkMatch(word, parts)) {
                return true;
            }
        }
        return false;
    }

    public boolean matchAll(String... array) {
        String[] parts = sentence.trim().split(" ");
        for (String word : array) {
            if (!checkMatch(word, parts)) {
                return false;
            }
        }
        return true;
    }

    public boolean matchContainsAny(String... array) {
        String[] parts = sentence.trim().split(" ");
        for (String word : array) {
            if (checkMatchContains(word, parts)) {
                return true;
            }
        }
        return false;
    }

    public boolean matchContainsAll(String... array) {
        String[] parts = sentence.trim().split(" ");
        for (String word : array) {
            if (!checkMatchContains(word, parts)) {
                return false;
            }
        }
        return true;
    }

    public boolean matchExactly(String... lines) {
        for (String line : lines) {
            if (ignoreCasing)
                if (line.equalsIgnoreCase(getSentence())) return true;
                else if (line.equals(getSentence())) return true;
        }
        return false;
    }

    public String getSentence() {
        return this.sentence;
    }

}
