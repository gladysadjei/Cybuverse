package com.gladys.cybuverse.Utils.APIClientTool.NLPRestApiClient;

import com.gladys.cybuverse.Utils.GeneralUtils.collections.JSONData;

import androidx.annotation.NonNull;

public class Document {

    public static String PLAIN_TEXT = "PLAIN_TEXT";

    String type;
    String content;

    public Document(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isValid() {
        return !type.trim().isEmpty() && !content.trim().isEmpty();
    }

    public JSONData toJSON() {
        JSONData data = new JSONData();
        JSONData document = new JSONData()
                .add("info", getType())
                .add("content", getContent());
        data.add("document", document);
        return data;
    }

    @NonNull
    @Override
    public String toString() {
        return "Document<" + getType() + ", " + getContent() + ">";
    }
}
