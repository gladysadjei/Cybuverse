package com.gladys.cybuverse.Utils.GameBase;

public class Player {
    private String name;
    private Integer key;
    private int level;
    private float score;
    private float health;
    private boolean is_human;
    private boolean is_active;
    private float maxHealth;
    private float maxScore;
    private float increaseScoreRate = 0;
    private float increaseHealthRate = 0;
    private float decreaseScoreRate = 0;
    private float decreaseHealthRate = 0;

    public Player(String playerName) {
        activate();
        setName(playerName);
        setPlayerLevel(0);
        setScore(0);
        setMaxScore(100);
        setMaxHealth(100);
        is_human = true;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer index) {
        this.key = index;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float value) {
        if (value <= maxScore) score = value;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float value) {
        if (value <= maxHealth) health = value;
    }

    public int getPlayerLevel() {
        return level;
    }

    public void setPlayerLevel(int level) {
        this.level = level;
    }

    public void forceSetScore(float value) {
        score = value;
    }

    public void forceSetHealth(float value) {
        health = value;
    }

    public boolean isActive() {
        return is_active;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(float value) {
        maxHealth = value;
        health = value;
    }

    public float getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(float value) {
        maxScore = value;
    }

    public void activate() {
        is_active = true;
    }

    public void deActivate() {
        is_active = false;
    }

    public float getDecreaseHealthRate() {
        return decreaseHealthRate;
    }

    public void setDecreaseHealthRate(float value) {
        decreaseHealthRate = value;
    }

    public float getIncreaseHealthRate() {
        return increaseHealthRate;
    }

    public void setIncreaseHealthRate(float value) {
        increaseHealthRate = value;
    }

    public float getDecreaseScoreRate() {
        return decreaseScoreRate;
    }

    public void setDecreaseScoreRate(float value) {
        decreaseScoreRate = value;
    }

    public float getIncreaseScoreRate() {
        return increaseScoreRate;
    }

    public void setIncreaseScoreRate(float value) {
        increaseScoreRate = value;
    }

    public void increaseHealthBy(float value) {
        health += value;
    }

    public void decreaseHealthBy(float value) {
        health -= value;
    }

    public void increaseScoreBy(float value) {
        score += value;
    }

    public void decreaseScoreBy(float value) {
        score -= value;
    }

    public void increaseHealth() {
        if (increaseHealthRate == 0.0) {
            try {
                throw new GameExceptions.HealthIncreaseRateNotSetException();
            } catch (GameExceptions.HealthIncreaseRateNotSetException e) {
                e.printStackTrace();
            }
        } else {
            if ((health + increaseHealthRate) <= maxHealth) {
                health += increaseHealthRate;
            }
        }
    }

    public void increaseScore() {
        if (increaseScoreRate == 0.0) {
            try {
                throw new GameExceptions.ScoreIncreaseRateNotSetException();
            } catch (GameExceptions.ScoreIncreaseRateNotSetException e) {
                e.printStackTrace();
            }
        } else {
            if ((score + increaseScoreRate) <= maxScore) {
                score += increaseScoreRate;
            }
        }
    }

    public void decreaseHealth() {
        if (decreaseHealthRate == 0) {
            try {
                throw new GameExceptions.HealthDecreaseRateNotSetException();
            } catch (GameExceptions.HealthDecreaseRateNotSetException e) {
                e.printStackTrace();
            }
        } else {
            if ((health - decreaseHealthRate) >= 0) {
                health -= decreaseHealthRate;
            }
        }
    }

    public void decreaseScore() {
        if (decreaseScoreRate == 0) {
            try {
                throw new GameExceptions.ScoreDecreaseRateNotSetException();
            } catch (GameExceptions.ScoreDecreaseRateNotSetException e) {
                e.printStackTrace();
            }
        } else {
            if ((score - decreaseScoreRate) >= 0) {
                score -= decreaseScoreRate;
            }
        }
    }

    public boolean isHuman() {
        return is_human;
    }

    public void setAsComputedPlayer() {
        is_human = false;
    }

    public void setAsHumanPlayer() {
        is_human = true;
    }

    @Override
    public String toString() {
        return "Player<" + getName() + ">";
    }
}
