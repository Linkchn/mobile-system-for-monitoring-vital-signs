package com.grp.application.components;

public enum DurationTab {
    DAILY(0),
    WEEKLY(1),
    MONTHLY(2);

    private final int tabIndex;

    DurationTab(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public int getIndex() {
        return tabIndex;
    }
}
