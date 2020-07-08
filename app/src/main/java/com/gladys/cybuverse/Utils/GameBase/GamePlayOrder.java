package com.gladys.cybuverse.Utils.GameBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePlayOrder {
    private Map<Integer, Integer> players;
    private Map<Integer, Integer> orders;

    public GamePlayOrder() {
        this.players = new HashMap<>();
        this.orders = new HashMap<>();
    }

    public void put(int player, int order) {
        if (!players.containsKey(player) && !players.containsValue(order)) {
            players.put(player, order);
            orders.put(order, player);
        } else {
            orders.remove(players.get(player));
            players.remove(orders.get(order));
            players.put(player, order);
            orders.put(order, player);
        }
    }

    public int getOrderForPlayer(int player) {
        return players.get(player);
    }

    public int getPlayerForOrder(int order) {
        return orders.get(order);
    }

    public boolean hasOrderForPlayer(int player) {
        return players.containsKey(player);
    }

    public boolean hasPlayerForOrder(int order) {
        return orders.containsKey(order);
    }

    public List<Integer> getPlayers() {
        List<Integer> list = new ArrayList<>(orders.values());
        Collections.sort(list);
        return list;
    }

    public List<Integer> getOrdering() {
        List<Integer> list = new ArrayList<>(players.values());
        Collections.sort(list);
        return list;
    }

    public Integer getPlayerAfterOrder(int order) {
        if (hasPlayerForOrder(order)) {
            List<Integer> sorted = new ArrayList<>(getOrdering());
            int index = sorted.indexOf(order);
            if (index + 1 < sorted.size() - 1)
                return getPlayerForOrder(index + 1);
            return getPlayerForOrder(0);
        }
        return null;
    }

    public Integer getPlayerAfterPlayer(int player) {
        if (hasOrderForPlayer(player))
            return getPlayerAfterOrder(getOrderForPlayer(player));
        return null;
    }

    public Integer getPlayerBeforeOrder(int order) {
        if (hasPlayerForOrder(order)) {
            List<Integer> sorted = new ArrayList<>(getOrdering());
            Collections.sort(sorted);
            int index = sorted.indexOf(order);
            if (index - 1 >= 0)
                return getPlayerForOrder(index - 1);
            return getPlayerForOrder(size() - 1);
        }
        return null;
    }

    public Integer getPlayerBeforePlayer(int player) {
        if (hasOrderForPlayer(player))
            return getPlayerBeforeOrder(getOrderForPlayer(player));
        return null;
    }

    public void clear() {
        players.clear();
        orders.clear();
    }

    public int size() {
        return players.size();
    }
}
