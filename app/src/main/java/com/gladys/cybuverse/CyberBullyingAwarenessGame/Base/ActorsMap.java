package com.gladys.cybuverse.CyberBullyingAwarenessGame.Base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ActorsMap {

    private static ActorsMap actorsMap;
    private Map<String, Actor> stringActorMap;

    private ActorsMap() {
        stringActorMap = new HashMap<>();
        initializeDefaultActors();
    }

    public static ActorsMap getInstance() {
        if (actorsMap == null) {
            actorsMap = new ActorsMap();
        }
        return actorsMap;
    }

    private void initializeDefaultActors() {
        add(ChatBot.asActor());
        add(new Actor("Mommy", "acts as player's biological mother. she lives " +
                "together with the player and is the player's legal guardian and care taker"));
        add(new Actor("Mike", "player's first friend from school and also crush"));
        add(new Actor("Janet", "player's first friend from school and also crush"));
        add(new Actor("Ella", "player's friend from school"));
        add(new Actor("Ethel", "player's friend from school"));
        add(new Actor("Chris", "player's friend from school"));
        add(new Actor("Bismark", "player's friend from school"));
        add(new Actor("Jessica", "class mate from school"));
        add(new Actor("Daniel", "class mate from school"));
        add(new Actor("Bernard", "class mate from school and the class prefect boyfriend"));
        add(new Actor("Lawrencia", "class mate from school and the class prefect girlfriend"));
        add(new Actor("Tony", "class mate from school"));
        add(new Actor("Mrs. Thompson", "player's class teacher"));
    }

    public void add(Actor... actor) {
        addAll(Arrays.asList(actor));
    }

    public void addAll(Collection<Actor> actors) {
        for (Actor actor : actors) {
            stringActorMap.put(actor.getName().toLowerCase(), actor);
        }
    }

    public Actor get(String name) {
        return stringActorMap.get(name);
    }

    public Set<String> keys() {
        return stringActorMap.keySet();
    }

    public List<Actor> values() {
        return new ArrayList<>(stringActorMap.values());
    }
}
