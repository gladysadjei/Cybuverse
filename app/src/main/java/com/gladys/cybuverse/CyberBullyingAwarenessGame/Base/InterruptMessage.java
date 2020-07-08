package com.gladys.cybuverse.CyberBullyingAwarenessGame.Base;

import java.util.List;

public class InterruptMessage extends Message {
    public InterruptMessage(Actor sender, List<Actor> receivers, MessageContent content) {
        super(sender, receivers, content);
        setProperty("is_interrupt", true);
    }

    public InterruptMessage(Actor sender, Actor receiver, MessageContent content) {
        super(sender, receiver, content);
        setProperty("is_interrupt", true);
    }

    public InterruptMessage(Actor sender, List<Actor> receivers) {
        super(sender, receivers);
        setProperty("is_interrupt", true);
    }

    public InterruptMessage(Actor sender, Actor receiver) {
        super(sender, receiver);
        setProperty("is_interrupt", true);
    }

    public InterruptMessage() {
        setProperty("is_interrupt", true);
    }

}
