package com.gladys.cybuverse.CyberBullyingAwarenessGame.ProfanityChecker;

import com.gladys.cybuverse.Utils.FileUtils.ReadWritableFile;
import com.gladys.cybuverse.Utils.GeneralUtils.Funcs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfanityFilter {

    private Map<Character, List<String>> wordsMap;
    private Map<Character, List<String>> phrasesMap;
    private List<String> wordsList;
    private List<String> phrasesList;

    public ProfanityFilter(String[] wordsList) {
        init(Arrays.asList(wordsList), null);
    }

    public ProfanityFilter(String[] wordsList, String[] phrasesList) {
        init(Arrays.asList(wordsList), Arrays.asList(phrasesList));
    }

    public ProfanityFilter(List<String> wordsList) {
        init(wordsList, null);
    }

    public ProfanityFilter(List<String> wordsList, List<String> phrasesList) {
        init(wordsList, phrasesList);
    }

    public ProfanityFilter(File wordsFile, File phrasesFile) throws IOException {

        if (wordsFile != null) {
            List<String> wordsList = new ArrayList<>();
            List<String> phrasesList = new ArrayList<>();

            ReadWritableFile wordsReadWritableFile = new ReadWritableFile(wordsFile);
            for (Object word : wordsReadWritableFile.readEnumerate().values()) {
                if (!word.toString().trim().isEmpty()) {
                    wordsList.add(word.toString());
                }
            }

            if (phrasesFile != null) {
                ReadWritableFile phrasesReadWritableFile = new ReadWritableFile(phrasesFile);
                for (Object word : phrasesReadWritableFile.readEnumerate().values()) {
                    if (!word.toString().trim().isEmpty()) {
                        phrasesList.add(word.toString());
                    }
                }
            }
            init(wordsList, phrasesList);
        }
    }

    public static String prepareWord(String word) {
        word = word.replaceAll("1", "i");
        word = word.replaceAll("!", "i");
        word = word.replaceAll("3", "e");
        word = word.replaceAll("4", "a");
        word = word.replaceAll("@", "a");
        word = word.replaceAll("5", "s");
        word = word.replaceAll("7", "t");
        word = word.replaceAll("0", "o");
        word = word.replaceAll("9", "g");
        word = word.replaceAll("8", "ate");
        word = Funcs.replaceAll(word, "$", "S");
        word = Funcs.replaceAll(word, "+", "t");
        word = word.replaceAll("[^a-zA-Z]", "");

        return word.toLowerCase().trim();
    }

    private void init(List<String> wordsList, List<String> phrasesList) {
        this.wordsList = wordsList;
        this.phrasesList = phrasesList;
        this.wordsMap = new HashMap<>();
        this.phrasesMap = new HashMap<>();

        for (String word : wordsList) {
            if (!word.trim().isEmpty()) {
                String p = prepareWord(word);
                Character i;

                if (p.equals(""))
                    i = word.charAt(0);
                else
                    i = p.charAt(0);

                if (this.wordsMap.get(i) == null)
                    this.wordsMap.put(i, new ArrayList<String>());
                this.wordsMap.get(i).add(word);
            }
        }
        for (String word : phrasesList) {
            if (!word.trim().isEmpty()) {
                String p = prepareWord(word);
                Character i;

                if (p.equals(""))
                    i = word.charAt(0);
                else
                    i = p.charAt(0);

                if (this.phrasesMap.get(i) == null)
                    this.phrasesMap.put(i, new ArrayList<String>());
                this.phrasesMap.get(i).add(word.replace("-", ""));
            }
        }
    }

    public List<String> getWordsMap() {
        return wordsList;
    }

    public List<String> getPhrasesMap() {
        return phrasesList;
    }

    public List<String> getAllMatches(String line) {
        ArrayList<String> matches = new ArrayList<>();
        ProfanityPhraseFilter profanityPhraseFilter = new ProfanityPhraseFilter(this.phrasesMap);
        for (String word : line.split(" ")) {
            if (isMatchForWord(word)) {
                matches.add(word);
            }

            if (!phrasesMap.isEmpty()) {
                String match_group = profanityPhraseFilter.searchMatchWithNewWord(word);
                if (match_group != null) {
                    if (!matches.contains(match_group))
                        matches.add(match_group);
                }
            }

        }

        return matches;
    }

    public boolean isMatchForWord(String word, boolean strictMatch) {
        return getMatchForWord(word, strictMatch) != null;
    }

    public boolean isMatchForWord(String word) {
        return getMatchForWord(word, false) != null;
    }

    public boolean isStrictMatchForWord(String word) {
        return getMatchForWord(word, true) != null;
    }

    public String getMatchForWord(String word, boolean strictMatch) {
        if (strictMatch) {
            return getWordsMap().contains(word) ? word : null;
        } else {
            String p = prepareWord(word);
            Character i;

            if (p.equals(""))
                i = word.charAt(0);
            else
                i = p.charAt(0);

            List<String> badWordsList = wordsMap.get(i);

            if (badWordsList == null)
                return null;

            if (!p.equals(""))
                word = p;

            for (String badWord : badWordsList) {
                p = prepareWord(badWord);

                if (!p.equals(""))
                    badWord = p;

                if (word.equals(badWord) || ((word.contains(badWord) || badWord.contains(word)) && word.length() > 3
                        && com.gladys.cybuverse.CyberBullyingAwarenessGame.ProfanityChecker.DoubleMetaphone.stringMatchScore(word, badWord) > 0.5f)) {
                    return badWord;
                } else if (word.length() > 3 && badWord.length() <= word.length() + (0.5 * word.length())) {
                    com.gladys.cybuverse.CyberBullyingAwarenessGame.ProfanityChecker.DoubleMetaphone doubleMetaphone = new com.gladys.cybuverse.CyberBullyingAwarenessGame.ProfanityChecker.DoubleMetaphone();
                    if (doubleMetaphone.isDoubleMetaphoneEqual(word, badWord)) {
                        if (DoubleMetaphone.stringMatchScore(word, badWord) > 0.5f) {
                            return badWord;
                        }
                    }
                }
            }
            return null;
        }
    }

}
