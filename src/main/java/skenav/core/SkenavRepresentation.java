package skenav.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SkenavRepresentation {
    private long id;

    private String content;

    public SkenavRepresentation() {
        // Jackson deserialization
    }

    public SkenavRepresentation(long id, String content) {
        this.id = id;
        this.content = content;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public String getContent() {
        return content;
    }
}
