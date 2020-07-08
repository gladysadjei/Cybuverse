package com.gladys.cybuverse.CyberBullyingAwarenessGame.ProfanityChecker;

import java.util.List;
import java.util.Map;

import static com.gladys.cybuverse.CyberBullyingAwarenessGame.ProfanityChecker.ProfanityFilter.prepareWord;


public class ProfanityPhraseFilter {

    private final Map<Character, List<String>> phrasesMap;
    private String sentence;
    private boolean matchFound;

    public ProfanityPhraseFilter(Map<Character, List<String>> phrasesMap) {
        this.phrasesMap = phrasesMap;
        this.sentence = "";
        this.matchFound = false;
    }

    public String isMatchForWord(String word, boolean strictMatch) {

        if (!word.trim().isEmpty() && !phrasesMap.isEmpty()) {

            String p = prepareWord(word);
            Character i;

            if (p.equals(""))
                i = word.charAt(0);
            else
                i = p.charAt(0);

            List<String> badWordsList = phrasesMap.get(i);

            if (badWordsList == null)
                return null;

            if (strictMatch) {
                return badWordsList.contains(word) ? word : null;
            } else {

                if (!p.equals(""))
                    word = p;

                for (String badWord : badWordsList) {

                    p = prepareWord(badWord);

                    if (!p.equals(""))
                        badWord = p;

                    if (word.equals(badWord) || ((word.contains(badWord) || badWord.contains(word)) && word.length() > 6
                            && com.gladys.cybuverse.CyberBullyingAwarenessGame.ProfanityChecker.DoubleMetaphone.stringMatchScore(word, badWord) > 0.5f)) {
                        return badWord;
                    } else if (word.length() > 6 && badWord.length() <= word.length() + (0.5 * word.length())) {
                        com.gladys.cybuverse.CyberBullyingAwarenessGame.ProfanityChecker.DoubleMetaphone doubleMetaphone = new com.gladys.cybuverse.CyberBullyingAwarenessGame.ProfanityChecker.DoubleMetaphone();
                        if (doubleMetaphone.isDoubleMetaphoneEqual(word, badWord)) {
                            if (DoubleMetaphone.stringMatchScore(word, badWord) > 0.5f) {
                                return badWord;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public String searchMatchWithNewWord(String word) {

        if (!matchFound && !phrasesMap.isEmpty()) {

            String p = prepareWord(word);

            if (!p.equals(""))
                word = p;

            sentence = sentence + " " + word;
            sentence = sentence.trim();
            String[] words = sentence.split(" ");

            if (words.length > 1) {

                for (int i = 0; i < words.length; i++) {
                    String w = words[i];

                    List<String> list = phrasesMap.get(w.charAt(0));

                    if (list != null) {
                        String t;
                        if (i + 1 < words.length) {
                            t = w + words[i + 1];
                            String r = w + " " + words[i + 1];
                            String result = isMatchForWord(t, false);
                            if (result != null) {
                                this.matchFound = true;
                            }

                            if (i + 2 < words.length) {
                                t = t + words[i + 2];
                                if (isMatchForWord(t, false) != null) {
                                    this.matchFound = true;
                                    return r + " " + words[i + 2];
                                }
                            }

                            if (this.matchFound) {
                                return r;
                            }
                        } else {
                            break;
                        }
                    }

                }
            }
        }
        return null;
    }
}
