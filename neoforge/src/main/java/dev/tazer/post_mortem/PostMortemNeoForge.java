package dev.tazer.post_mortem;


import dev.tazer.post_mortem.networking.TeleportToAnchorPayload;
import dev.tazer.post_mortem.platform.PacketHandler;
import dev.tazer.post_mortem.registry.RegistryHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(PostMortem.MOD_ID)
public class PostMortemNeoForge {

    public PostMortemNeoForge(IEventBus bus) {
        PostMortem.init();

        bus.addListener(PostMortemNeoForge::register);
        bus.addListener(PostMortemNeoForge::registerPayloadHandlers);
    }

    private static void register(RegisterEvent event) {
        RegistryHandler.register(event.getRegistryKey());
    }

    private static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                TeleportToAnchorPayload.TYPE,
                TeleportToAnchorPayload.STREAM_CODEC,
                PacketHandler::handleAnchorTeleport
        );
    }
}