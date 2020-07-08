package com.gladys.cybuverse.Utils.GameBase;

public class GameExceptions {

    public static class GameException extends Exception {
        public GameException(String message) {
            super(message);
        }
    }

    public static class GameRunException extends GameException {
        public GameRunException(String message) {
            super(message);
        }
    }

    public static class GamePlayException extends GameException {
        public GamePlayException(String message) {
            super(message);
        }
    }

    public static class NoPlayerException extends Exception {
        public NoPlayerException() {
        }
    }

    public static class PlayerNotInGameException extends Exception {
        PlayerNotInGameException() {
        }
    }

    public static class ScoreIncreaseRateNotSetException extends Exception {
        ScoreIncreaseRateNotSetException() {
            System.out.println("Use method setIncreaseScoreRate to set score increase rate.");
        }
    }

    public static class HealthIncreaseRateNotSetException extends Exception {
        HealthIncreaseRateNotSetException() {
            System.out.println("Use method setIncreaseHealthRate to set health increase rate.");
        }
    }

    public static class ScoreDecreaseRateNotSetException extends Exception {
        ScoreDecreaseRateNotSetException() {
            System.out.println("Use method setDecreaseScoreRate to set score decrease rate.");
        }
    }

    public static class HealthDecreaseRateNotSetException extends Exception {
        HealthDecreaseRateNotSetException() {
            System.out.println("Use method setDecreaseHealthRate to set health decrease rate.");
        }
    }

}
