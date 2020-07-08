package com.gladys.cybuverse.Utils.GameBase;

import java.util.HashMap;
import java.util.Map;

public interface EventListener {

    void processEvent(Event event);

    class Event {
        String name;
        Integer priority;
        Map<Object, Object> properties;

        public Event(String event_name) {
            this.name = event_name;
            this.priority = 1;
            this.properties = new HashMap<>();
        }

        Event(String event_name, int priority) {
            this.name = event_name;
            this.priority = priority;
            this.properties = new HashMap<>();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getPriority() {
            return priority;
        }

        public void setPriority(Integer priority) {
            this.priority = priority;
        }

        public Map getAllProperties() {
            return properties;
        }

        public void setProperties(Map<Object, Object> properties) {
            this.properties = properties;
        }

        public void addProperty(Object key, Object value) {
            this.properties.put(key, value);
        }

        public Object getProperty(Object key) {
            return this.properties.get(key);
        }

        public boolean hasPropertyKey(Object key) {
            return this.properties.containsKey(key);
        }

        public boolean hasPropertyValue(Object value) {
            return this.properties.containsValue(value);
        }

        public void removeProperty(Object key) {
            this.properties.remove(key);
        }

        @Override
        public String toString() {
            return "Event<" + getName() + ">";
        }
    }
}
