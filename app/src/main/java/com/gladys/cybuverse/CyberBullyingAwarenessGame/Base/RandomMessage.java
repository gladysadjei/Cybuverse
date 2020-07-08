package com.gladys.cybuverse.CyberBullyingAwarenessGame.Base;

import com.gladys.cybuverse.Utils.GeneralUtils.Funcs;

import java.util.List;

public class RandomMessage extends Message {

    public RandomMessage(Actor sender, List<Actor> receivers, MessageContent... randomMessages) {
        super(sender, receivers);
        if (randomMessages != null && randomMessages.length > 0) {
            setContent(randomMessages[Funcs.randint(randomMessages.length - 1)]);
        }
    }

    public RandomMessage(Actor sender, Actor receiver, MessageContent... randomMessages) {
        super(sender, receiver);
        if (randomMessages != null && randomMessages.length > 0) {
            setContent(randomMessages[Funcs.randint(randomMessages.length - 1)]);
        }
    }
}
