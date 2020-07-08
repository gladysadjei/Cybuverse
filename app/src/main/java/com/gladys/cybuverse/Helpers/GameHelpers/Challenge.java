package com.gladys.cybuverse.Helpers.GameHelpers;

public class Challenge {

    public String opener;
    public String gameRef;

    public Challenge() {
        // required by Firebase database
    }

    public Challenge(String opener, String gameRef) {
        this.gameRef = gameRef;
        this.opener = opener;
    }
}
