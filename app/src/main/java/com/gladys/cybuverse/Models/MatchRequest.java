package com.gladys.cybuverse.Models;

public class MatchRequest {

    private String requester, joiner, roomName;

    public MatchRequest(String requester) {
        this.requester = requester;
        this.joiner = "";
        this.roomName = "";
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getJoiner() {
        return joiner;
    }

    public void setJoiner(String joiner) {
        this.joiner = joiner;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
