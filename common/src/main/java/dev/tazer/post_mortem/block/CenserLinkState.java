package dev.tazer.post_mortem.block;

import net.minecraft.util.StringRepresentable;

public enum CenserLinkState implements StringRepresentable {
    ABSENT("absent"),
    WEAK("weak"),
    STRONG("strong");

    private final String name;

    CenserLinkState(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
