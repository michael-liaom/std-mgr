package com.weijie.stdmgr;

public class SimplePickItemData {
    String  name;
    Integer id;
    boolean isPicked;

    public SimplePickItemData(String name, Integer id, boolean isPicked) {
        this.name       = name;
        this.id         = id;
        this.isPicked   = isPicked;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public boolean isPicked() {
        return isPicked;
    }

    public void setPicked(boolean isPicked) {
        this.isPicked = isPicked;
    }
}
