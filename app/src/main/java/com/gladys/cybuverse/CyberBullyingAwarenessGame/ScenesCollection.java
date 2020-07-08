package com.gladys.cybuverse.CyberBullyingAwarenessGame;

import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Scene;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Scenes.BasicSchool;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Scenes.IntroductionWithGladys;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Scenes.SchoolTingo;
import com.gladys.cybuverse.Models.User;

import java.util.ArrayList;
import java.util.List;

public class ScenesCollection {
    private User user;
    private List<Scene> scenesList;

    public ScenesCollection(User user) {
        this.scenesList = new ArrayList<>();
        this.user = user;
        addAllScenes();
    }

    public User getUser() {
        return user;
    }

    public List<Scene> getScenesList() {
        return scenesList;
    }

    private void addAllScenes() {
        this.scenesList.add(new IntroductionWithGladys(getUser()));
        this.scenesList.add(new BasicSchool(getUser()));
        this.scenesList.add(new SchoolTingo(getUser()));
        this.scenesList.add(new Scene("Home Alone"));
        this.scenesList.add(new Scene("Back To School"));
        this.scenesList.add(new Scene("High School"));
        this.scenesList.add(new Scene("Vacation Job"));
        this.scenesList.add(new Scene("Summer Time"));
        this.scenesList.add(new Scene("A Crush"));
        this.scenesList.add(new Scene("School Problems"));
        this.scenesList.add(new Scene("Prom Night"));
        this.scenesList.add(new Scene("To The University"));
        this.scenesList.add(new Scene("First Year"));
        this.scenesList.add(new Scene("The Initiation"));
        this.scenesList.add(new Scene("House Party"));
        this.scenesList.add(new Scene("Is It Love"));
        this.scenesList.add(new Scene("People Talk"));
        this.scenesList.add(new Scene("Graduation"));
    }

    public Scene get(int index) {
        if (index > -1 && index < getScenesList().size())
            return getScenesList().get(index);
        return null;
    }
}
