package dev.tazer.post_mortem.block;

import net.minecraft.util.StringRepresentable;

public enum AltarPart implements StringRepresentable {
    HEAD("head"),
    MIDDLE("middle"),
    FOOT("foot");

    private final String name;

    private AltarPart(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
