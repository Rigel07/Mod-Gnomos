package com.gnomos.entity;

public enum GnomeProfession {
    FARMER("farmer"),
    MINER("miner"),
    BLACKSMITH("blacksmith"),
    FORAGER("forager"),
    ELDER("elder");

    private final String key;

    GnomeProfession(String key) {
        this.key = key;
    }

    public String getName() {
        return key;
    }

    public static GnomeProfession byId(int id) {
        GnomeProfession[] all = values();
        return all[Math.floorMod(id, all.length)];
    }
}
