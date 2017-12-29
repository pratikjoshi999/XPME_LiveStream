package com.muvi.www.xmpe.chat;

/**
 * Created by MUVI on 6/8/2017.
 */

public class XPMECommentFragment {


    private String alias;
    private String content;
    private double createdAt;

    public XPMECommentFragment(String alias, String content, Double createdAt) {
        this.alias = alias;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getAlias() {
        return this.alias;
    }

    public String getContent() {
        return this.content;
    }

    public Double getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public String toString() {
        return this.getAlias() + ": " + this.getContent() + " (" + this.getCreatedAt() + ")";
    }
}
