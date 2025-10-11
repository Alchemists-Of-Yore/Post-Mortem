package dev.tazer.post_mortem.client;

public interface IGuiExtension {
    default SpectreGui getSpectreGui() {
        throw new AssertionError("Implemented in Mixin");
    }
}
