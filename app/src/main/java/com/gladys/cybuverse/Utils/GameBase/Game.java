package com.gladys.cybuverse.Utils.GameBase;

import com.gladys.cybuverse.Utils.GeneralUtils.collections.Dictionary;
import com.gladys.cybuverse.Utils.GeneralUtils.collections.Enumerator;
import com.gladys.cybuverse.Utils.GeneralUtils.collections.KeyValuePair;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

public abstract class Game {

    private int LEVEL;
    private String GAMESTATE;
    private Integer PLAYERINDEX = 0;
    private Integer CURRENTPLAYER;
    private Integer MAXPLAYERNUMBER;
    private PlayerIterator PLAYERITERATOR;
    private Dictionary<Integer, Player> PLAYERS = new Dictionary<>();

    public Game() {
        init();
    }

    private void init() {
        if (stateIsNull()) {
            PLAYERITERATOR = getPlayerIterator();
        }
        onCreate();
        setStateCreated();
    }

    private void runGameRunner() {
        getGameEngine().getPlayableGame().gameRunner();
    }

    private void runGameLoop() {
        if (isPaused()) {
            onPause();
        } else if (isExited()) {
            onExit();
        } else {
            if (isResumed()) {
                onResume();
                setStateRunning();
            }
            runGameRunner();
        }
    }

    protected void onCreate() {
    }

    protected void onStart() throws NotEnoughPlayersException {
    }

    protected void onPause() {
    }

    protected void onResume() {
    }

    protected void onExit() {
    }

    protected abstract void onSave();

    protected abstract Player preparePlayer(Player player);

    protected PlayerIterator getPlayerIterator() {
        return new PlayerIterator() {

            @Override
            public int getPreviousPlayerKey(Integer lastPlayerKey, Dictionary<Integer, Player> players) {
                if (lastPlayerKey != null && lastPlayerKey > 0) {
                    return lastPlayerKey - 1;
                }
                return 0;
            }

            @Override
            public int getNextPlayerKey(Integer lastPlayerKey, Dictionary<Integer, Player> players) {
                if (lastPlayerKey != null && lastPlayerKey + 1 < players.size()) {
                    return lastPlayerKey + 1;
                }
                return 0;
            }

        };
    }

    public Player getPreviousPlayer() {
        int playerKey = PLAYERITERATOR.getPreviousPlayerKey(CURRENTPLAYER, PLAYERS);
        return getPlayerByKey(playerKey);
    }

    public Player getNextPlayer() {
        int playerKey = PLAYERITERATOR.getNextPlayerKey(CURRENTPLAYER, PLAYERS);
        return getPlayerByKey(playerKey);
    }

    protected void runBeforePlay() {

    }

    public final void play(GameMove gameMove) {
        if (!(isPaused() || isExited())) {
            Player player = gameMove.getPlayer();

            if (validatePlayer(player)) {

                runBeforePlay();

                if (player.isHuman()) {
                    getPlayableGame().playGameMove(gameMove);
                } else {
                    getPlayableGame().playAsComputer(player);
                }

                CURRENTPLAYER = player.getKey();

                runAfterPlay();

            } else {
                try {
                    throw new GameExceptions.PlayerNotInGameException();
                } catch (GameExceptions.PlayerNotInGameException e) {
                    e.printStackTrace();
                }
            }

            runGameLoop();
        }
    }

    protected void runAfterPlay() {

    }

    public abstract GameEngine getGameEngine();

    private PlayableGame getPlayableGame() {
        return getGameEngine().getPlayableGame();
    }

    public final void addPlayer(Player player) {
        if (isCreated()) {
            if (MAXPLAYERNUMBER == null || PLAYERINDEX < MAXPLAYERNUMBER) {
                if (!PLAYERS.hasValue(player) && player.getKey() == null) {
                    player.setKey(PLAYERINDEX);
                    if (preparePlayer(player) != null) {
                        PLAYERS.setIfAbsent(PLAYERINDEX, preparePlayer(player));
                    } else {
                        PLAYERS.setIfAbsent(PLAYERINDEX, player);
                    }

                    PLAYERINDEX++;
                }
            }
        }
    }

    public final void updatePlayer(Player player) {
        if (validatePlayer(player)) {
            PLAYERS.replace(player.getKey(), player);
        }
    }

    public final void resetGame() {
        for (Player player : PLAYERS.values()) {
            updatePlayer(preparePlayer(player));
        }
    }

    public final void removePlayerByKey(Integer key) {
        PLAYERS.remove(key);
    }

    public final void removePlayerByName(String playerName) {
        PLAYERS.remove(getPlayerByName(playerName).getKey());
    }

    public final Player getPlayerByName(String playerName) {
        for (KeyValuePair<Integer, Player> kv : PLAYERS) {
            if (kv.getValue().getName().equals(playerName)) {
                return kv.getValue();
            }
        }
        return null;
    }

    public final Player getPlayerByKey(Integer playerIndex) {
        return PLAYERS.get(playerIndex);
    }

    public final PlayerDictionary getPlayersDictionary() {
        return new PlayerDictionary();
    }

    public final int getPlayersCount() {
        return PLAYERS.size();
    }

    public final Integer getMaxNumberOfPlayers() {
        return MAXPLAYERNUMBER;
    }

    public final void setMaxNumberOfPlayers(int maxNumberOfPlayers) {
        MAXPLAYERNUMBER = maxNumberOfPlayers;
    }

    public final int getLevel() {
        return LEVEL;
    }

    public final void setLevel(int level) {
        LEVEL = level;
    }

    private boolean validatePlayer(Player player) {
        return PLAYERS.get(player.getKey()).getName().equals(player.getName()) &&
                PLAYERS.get(player.getKey()).getKey().equals(player.getKey()) &&
                PLAYERS.get(player.getKey()).isHuman() == player.isHuman();
    }

    public final String getGameState() {
        return GAMESTATE;
    }

    private void setStateCreated() {
        GAMESTATE = GameState.CREATED;
    }

    private void setStateStarted() {
        GAMESTATE = GameState.STARTED;
    }

    private void setStatePaused() {
        GAMESTATE = GameState.PAUSED;
    }

    private void setStateResumed() {
        GAMESTATE = GameState.RESUMED;
    }

    private void setStateRunning() {
        GAMESTATE = GameState.RUNNING;
    }

    private void setStateExited() {
        GAMESTATE = GameState.EXITED;
    }

    private boolean stateIsNull() {
        return GAMESTATE == null;
    }

    private boolean isCreated() {
        return GAMESTATE.equals(GameState.CREATED);
    }

    private boolean isRunning() {
        return GAMESTATE.equals(GameState.RUNNING);
    }

    private boolean isPaused() {
        return GAMESTATE.equals(GameState.PAUSED);
    }

    private boolean isResumed() {
        return GAMESTATE.equals(GameState.RESUMED);
    }

    private boolean isExited() {
        return GAMESTATE.equals(GameState.EXITED);
    }

    public final void start() throws NotEnoughPlayersException {
        if (isCreated()) {
            onStart();
            setStateStarted();
            if (getPlayersCount() > 0) {
                runGameLoop();
            } else throw new NotEnoughPlayersException();
        }
    }

    public final void pause() {
        if (!(isPaused())) {
            setStatePaused();
        }
    }

    public final void resume() {
        if (isPaused()) {
            setStateResumed();
        }
    }

    public final void save() {
        onSave();
    }

    public final void exit() {
        setStateExited();
    }

    public static class GameState {
        public static final String CREATED = "CREATED";
        public static final String STARTED = "STARTED";
        public static final String PAUSED = "PAUSED";
        public static final String RESUMED = "RESUMED";
        public static final String EXITED = "EXITED";
        public static final String RUNNING = "RUNNING";
    }

    public static abstract class PlayerIterator {
        public abstract int getPreviousPlayerKey(Integer lastPlayerKey, Dictionary<Integer, Player> players);

        public abstract int getNextPlayerKey(Integer lastPlayerKey, Dictionary<Integer, Player> players);
    }

    public class PlayerDictionary implements Iterable<KeyValuePair<Integer, Player>> {
        public Player get(Integer key) {
            return PLAYERS.get(key);
        }

        public Dictionary<Integer, Player> getActivePlayers() {
            Dictionary<Integer, Player> list = new Dictionary<>();
            for (Player player : PLAYERS.values()) {
                if (player.isActive()) {
                    list.set(player.getKey(), player);
                }
            }
            return list;
        }

        public Enumerator<KeyValuePair<Integer, Player>> getEnumeration() {
            return PLAYERS.getEnumerator();
        }

        public int size() {
            return PLAYERS.size();
        }

        public Set<Integer> keys() {
            return PLAYERS.keys();
        }

        public Collection<Player> values() {
            return PLAYERS.values();
        }

        public boolean isEmpty() {
            return PLAYERS.isEmpty();
        }

        public boolean hasPlayerWithKey(int playerKey) {
            return PLAYERS.hasKey(playerKey);
        }

        public boolean hasKeyValue(int playerKey, Player player) {
            return PLAYERS.hasKeyValue(playerKey, player);
        }

        @Override
        public Iterator<KeyValuePair<Integer, Player>> iterator() {
            return PLAYERS.iterator();
        }

        @Override
        public void forEach(Consumer<? super KeyValuePair<Integer, Player>> action) {
            PLAYERS.forEach(action);
        }

        @Override
        public String toString() {
            return PLAYERS.toString();
        }
    }

    public class NotEnoughPlayersException extends Exception {
        public NotEnoughPlayersException(String message) {
            super(message);
        }

        public NotEnoughPlayersException() {
            super("There are not enough players");
        }
    }

    public abstract class GameEngine {

        PlayableGame PLAYABLEGAME;

        public GameEngine() {
            PLAYABLEGAME = PlayableGameEngine();
        }

        protected abstract PlayableGame PlayableGameEngine();

        public PlayableGame getPlayableGame() {
            return PLAYABLEGAME;
        }
    }

    public abstract class PlayableGame {
        public abstract void gameRunner();

        public abstract void playGameMove(GameMove gameMove);

        public abstract void playAsComputer(Player computerPlayer);

        protected void updatePlayer(Player player) {
            Game.this.updatePlayer(player);
        }
    }

}
