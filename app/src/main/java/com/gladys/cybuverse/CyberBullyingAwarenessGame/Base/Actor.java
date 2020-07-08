package com.gladys.cybuverse.CyberBullyingAwarenessGame.Base;

public class Actor extends Person {

    private static final String ROLE = "__role__";
    private static final String IS_ACTOR = "__is_actor__";

    public Actor(String name, Integer age, String job, String role) {
        super(name, age, job);
        setProperty(IS_ACTOR, true);
        setProperty(ROLE, role);
    }

    public Actor(String name, Integer age, String job) {
        super(name, age, job);
        setProperty(IS_ACTOR, true);
        setProperty(ROLE, null);
    }

    public Actor(String name, Integer age) {
        super(name, age);
        setProperty(IS_ACTOR, true);
        setProperty(ROLE, null);
    }

    public Actor(String name, String role) {
        super(name, null);
        setProperty(IS_ACTOR, true);
        setProperty(ROLE, role);
    }

    public Actor(String name) {
        super(name, null);
        setProperty(IS_ACTOR, true);
        setProperty(ROLE, null);
    }

    public final String getRole() {
        return (getProperty(ROLE) != null) ? getProperty(ROLE).toString() : "";
    }

    public final void setRole(String role) {
        setProperty(ROLE, role);
    }

    @Override
    public String toString() {
        return "Actor<" + getName() + ">";
    }
}
