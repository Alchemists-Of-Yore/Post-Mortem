package dev.tazer.post_mortem.platform.services;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Checks if the mod is being run on a client or a server
     * @return True if in a client, false otherwise.
     */
    boolean isClient();

    /**
     * Sends a packet from the client to the server
     * @param payload The packet to send
     */
    void sendToServer(CustomPacketPayload payload);

    /**
     * Sends a packet from the server to a server player's client
     * @param player The player to send the packet to
     * @param payload The packet to send
     */
    void sendToClient(ServerPlayer player, CustomPacketPayload payload);
}