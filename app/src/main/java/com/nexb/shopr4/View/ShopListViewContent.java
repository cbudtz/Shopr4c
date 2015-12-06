package com.nexb.shopr4.View;

/**
 * @author Christian
 */
public abstract class ShopListViewContent {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public enum contentType {CATEGORY,ITEM};
    public abstract contentType getType();
}
