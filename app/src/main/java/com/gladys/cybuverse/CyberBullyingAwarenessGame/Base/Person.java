package com.gladys.cybuverse.CyberBullyingAwarenessGame.Base;

import com.gladys.cybuverse.Utils.GeneralUtils.collections.Dictionary;

public class Person {
    private Dictionary<String, Object> properties;
    private String name;
    private Integer age;
    private String job;

    public Person(String name, Integer age, String job) {
        this.properties = new Dictionary<>();
        this.name = name;
        this.age = age;
        this.job = job;
    }

    public Person(String name, Integer age) {
        this.properties = new Dictionary<>();
        this.name = name;
        this.age = age;
        this.job = "none";
    }

    public Person(String name) {
        this.properties = new Dictionary<>();
        this.name = name;
        this.age = null;
        this.job = "none";
    }

    public Dictionary<String, Object> getProperties() {
        return properties;
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public void setProperty(String key, Object value) {
        this.properties.setIfAbsent(key, value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    @Override
    public String toString() {
        return "Person<" + getName() + ">";
    }
}
