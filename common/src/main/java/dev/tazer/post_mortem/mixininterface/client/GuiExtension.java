package dev.tazer.post_mortem.mixininterface.client;

import dev.tazer.post_mortem.client.SpiritGui;

public interface GuiExtension {

    /**
     * @return The client's {@link SpiritGui}
     */
    default SpiritGui getSpiritGui() {
        throw new AssertionError("Implemented in Mixin");
    }
}
