package com.gladys.cybuverse.Utils.GeneralUtils.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.MapAction;
import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.format;
import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.toBoolean;
import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.toCharacter;
import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.toDouble;
import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.toFloat;
import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.toInteger;
import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.toStrings;

public class JSONData implements Iterable<KeyValuePair<Object, Object>> {

    private Dictionary<Object, Object> DATA = new Dictionary<>();

    private boolean isJsonString = false;

    public JSONData() {
    }

    public JSONData(Object jsonString) throws JSONException {
        isJsonString = !isJsonString;
        buildJsonArray(jsonString.toString().trim());
        isJsonString = !isJsonString;
    }

    public JSONData(Map mapData) {
        for (Object key : mapData.keySet()) {
            add(key, mapData.get(key));
        }
    }

    public JSONData(Dictionary<Object, Object> dictionaryData) {
        List<KeyValuePair<Object, Object>> list = dictionaryData.getList();
        for (KeyValuePair o : list) {
            add(o.getKey(), o.getValue());
        }
    }

    private void buildJsonArray(String jsonstring) throws JSONException {
        if (validateJson(jsonstring)) {
            jsonstring = jsonstring.substring(1, jsonstring.length() - 1).trim();

            KeyValuePair<Object, Object> kv = new KeyValuePair<>();
            Stack<Character> s = new Stack<>();
            List<String> stringList = new ArrayList<>();
            StringBuilder data = new StringBuilder();

            //breaking string by comma
            for (Character c : jsonstring.toCharArray()) {

                if (!s.isEmpty() && c == s.peekTop()) {
                    s.pop();
                    data.append(c);
                } else if (!s.isEmpty() && ((s.peekTop() == '{' && c == '}') || (s.peekTop() == '(' && c == ')') || (s.peekTop() == '[' && c == ']'))) {
                    s.pop();
                    data.append(c);
                } else if (c == '\'' || c == '"') {
                    s.push(c);
                    data.append(c);
                } else if ((s.isEmpty() || (s.peekTop() != '"' && s.peekTop() != '\'')) && (c == '{' || c == '[' || c == '(')) {
                    s.push(c);
                    data.append(c);
                } else if (c == ',' && s.isEmpty()) {
                    stringList.add(data.toString().trim());
                    data = new StringBuilder();
                } else {
                    data.append(c);
                }

            }
            if (data.length() > 0) {
                stringList.add(data.toString().trim());
            }


            //breaking string into key values
            for (String line : stringList) {
                s.clear();
                data = new StringBuilder();
                for (Character c : line.toCharArray()) {
                    if (!s.isEmpty() && s.peekTop() != ':' && c == s.peekTop()) {
                        s.pop();
                        data.append(c);
                    } else if (s.isEmpty() && (c == '\'' || c == '"')) {
                        s.push(c);
                        data.append(c);
                    } else if (c == ':' && s.isEmpty()) {
                        s.push(c);
                        kv.setKey(data.toString().trim());
                        data = new StringBuilder();
                    } else {
                        data.append(c);
                    }

                }
                if (data.length() > 0) {
                    kv.setValue(data.toString().trim());
                }
                add(kv.getKey(), kv.getValue());
            }


        } else {
            throw new JSONException();
        }
    }

    private boolean validateJson(String jsonstring) {
        if (jsonstring != null && jsonstring.charAt(0) == '{' && jsonstring.charAt(jsonstring.length() - 1) == '}') {
            return jsonstring.contains(":") || (jsonstring.contains(":") && jsonstring.contains(","));
        }
        return false;
    }

    private boolean isString(String line) {
        line = line.trim();
        return line.charAt(0) == '"' && line.charAt(line.length() - 1) == '"';
    }

    public JSONData add(Object key, Object value) {
        if (isJsonString) {
            DATA.set(key, value);
        } else {
            DATA.set(prepareObject(key), prepareObject(value));
        }
        return this;
    }

    public void addIfAbsent(Object key, Object value) {
        DATA.setIfAbsent(prepareObject(key), prepareObject(value));
    }

    public Object get(Object key) {
        return getObject(DATA.get(prepareObject(key)));
    }

    public Object getOrDefault(Object key, Object defaultValue) {
        if (get(key) == null) {
            return defaultValue;
        }
        return get(key);
    }

    private Object prepareObject(Object line) {
        if (line != null && line.getClass().getSimpleName().equals("String")) {
            line = '"' + line.toString() + '"';
        }
        return line;
    }

    private Object getObject(Object line) {
        if (line != null && isString(line.toString())) {
            return line.toString().substring(1, line.toString().length() - 1);
        }
        return line;
    }

    public String getString(Object key) {
        return toStrings(get(key));
    }

    public String getStringOrDefault(Object key, Object defaultValue) {
        return toStrings(getOrDefault(key, defaultValue));
    }

    public Character getCharacter(Object key) {
        return toCharacter(get(key));
    }

    public Character getCharacterOrDefault(Object key, Object defaultValue) {
        return toCharacter(getOrDefault(key, defaultValue));
    }

    public Integer getInteger(Object key) {
        return toInteger(get(key));
    }

    public Integer getIntegerOrDefault(Object key, Object defaultValue) {
        return toInteger(getOrDefault(key, defaultValue));
    }

    public Double getDouble(Object key) {
        return toDouble(get(key));
    }

    public Double getDoubleOrDefault(Object key, Object defaultValue) {
        return toDouble(getOrDefault(key, defaultValue));
    }

    public Float getFloat(Object key) {
        return toFloat(get(key));
    }

    public Float getFloatOrDefault(Object key, Object defaultValue) {
        return toFloat(getOrDefault(key, defaultValue));
    }

    public Boolean getBoolean(Object key) {
        return toBoolean(get(key));
    }

    public Boolean getBooleanOrDefault(Object key, Object defaultValue) {
        return toBoolean(getOrDefault(key, defaultValue));
    }

    public JSONData getJSONObject(Object key) throws JSONException {
        return new JSONData(get(key));
    }

    public Object[] getArray(Object key) {
        return getArrayFromString(getString(key)).toArray();
    }

    private List getArrayFromString(String stringToConvert) {
        Dictionary<Character, Character> dictionary = new Dictionary<>();
        dictionary.set('{', '}');
        dictionary.set('(', ')');
        dictionary.set('[', ']');
        dictionary.set('\'', '\'');
        dictionary.set('"', '"');
        String string = prepareString(stringToConvert);
        List<String> stringList = new ArrayList<>();
        Stack<Character> s = new Stack<>();
        StringBuilder data = new StringBuilder();

        for (Character c : string.toCharArray()) {

            if (!s.isEmpty() && dictionary.get(s.peekTop()) == c) {
                s.pop();
                data.append(c);
            } else if (dictionary.hasKey(c) && (s.isEmpty() || (!s.isEmpty() && !(s.peekTop() == '"' || s.peekTop() == '\'')))) {
                s.push(c);
                data.append(c);
            } else if (c == ',' && s.isEmpty()) {
                stringList.add(data.toString().trim());
                data = new StringBuilder();
            } else {
                data.append(c);
            }

        }

        if (data.length() > 0) {
            stringList.add(data.toString().trim());
        }

        return stringList;
    }

    private String prepareString(String data) {
        Dictionary<Character, Character> dictionary = new Dictionary<>();
        dictionary.set('{', '}');
        dictionary.set('(', ')');
        dictionary.set('[', ']');
        if (dictionary.keys().contains(data.charAt(0))) {
            if (data.charAt(data.length() - 1) == dictionary.get(data.charAt(0))) {
                data = data.substring(1, data.length() - 1).trim();
            }
        }
        return data;
    }

    public boolean isEmpty() {
        return DATA.isEmpty();
    }

    public int size() {
        return DATA.size();
    }

    public void clear() {
        DATA.clear();
    }

    public void remove(Object key) {
        DATA.remove(prepareObject(key));
    }

    public void remove(Object key, Object value) {
        DATA.remove(prepareObject(key), prepareObject(value));
    }

    public boolean hasKey(Object key) {
        return DATA.hasKey(prepareObject(key));
    }

    public boolean hasValue(Object value) {
        return DATA.hasValue(prepareObject(value));
    }

    public boolean hasKeyValue(Object key, Object value) {
        return DATA.hasKeyValue(prepareObject(key), prepareObject(value));
    }

    public void replace(Object key, Object value) {
        DATA.replace(prepareObject(key), prepareObject(value));
    }

    public void replace(Object key, Object value, Object new_value) {
        DATA.replace(prepareObject(key), prepareObject(value), prepareObject(new_value));
    }

    public List<KeyValuePair<Object, Object>> getAsList() {
        List<KeyValuePair<Object, Object>> list = DATA.getList();
        List<KeyValuePair<Object, Object>> new_list = new ArrayList<>();
        for (KeyValuePair<Object, Object> o : list) {
            new_list.add(new KeyValuePair<>(getObject(o.getKey()), getObject(o.getValue())));
        }
        return new_list;
    }

    public List<Object> keys() {
        List<Object> list = new ArrayList<>();
        for (KeyValuePair pair : getAsList()) {
            list.add(pair.getKey());
        }
        return list;
    }

    public Collection<Object> values() {
        List<Object> new_collection = new ArrayList<>();
        for (Object o : DATA.values()) {
            new_collection.add(getObject(o));
        }
        return new_collection;
    }

    @Override
    public Iterator<KeyValuePair<Object, Object>> iterator() {
        return DATA.iterator();
    }

    public void forEach(MapAction<Object, Object> action) {
        for (KeyValuePair<Object, Object> kv : DATA) {
            action.operate(kv.getKey(), kv.getValue());
        }
    }

    public Enumerator<KeyValuePair<Object, Object>> getEnumeration() {
        return new Enumerator<>(getAsList());
    }

    @Override
    public String toString() {
        StringBuilder data = new StringBuilder("{");
        Enumerator<KeyValuePair<Object, Object>> list = DATA.getEnumerator();
        while (list.hasNext()) {
            KeyValuePair x = list.getNext();
            data.append(format("\n\t<>: <>", x.getKey(), x.getValue()));
            if (list.hasNext()) {
                data.append(",");
            }
        }
        return data + "\n}";
    }

    public static class JSONException extends Exception {
    }
}