package dev.tazer.post_mortem.block;

import net.minecraft.util.StringRepresentable;

public enum FleshQuality implements StringRepresentable {
    ROTTEN("rotten"),
    NORMAL("normal"),
    FRESH("fresh");

    private final String name;

    FleshQuality(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
