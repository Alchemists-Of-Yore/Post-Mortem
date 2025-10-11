package dev.tazer.post_mortem;


import dev.tazer.post_mortem.networking.GravePayload;
import dev.tazer.post_mortem.networking.GraveRequestPayload;
import dev.tazer.post_mortem.networking.TeleportToAnchorPayload;
import dev.tazer.post_mortem.platform.PacketHandler;
import dev.tazer.post_mortem.registry.PMDataSerializers;
import dev.tazer.post_mortem.registry.RegistryHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
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

        event.register(
                NeoForgeRegistries.ENTITY_DATA_SERIALIZERS.key(),
                registry -> {
                    registry.register(PostMortem.location("soul_state"), PMDataSerializers.SOUL_STATE);
                }
        );
    }

    private static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                TeleportToAnchorPayload.TYPE,
                TeleportToAnchorPayload.STREAM_CODEC,
                PacketHandler::handleAnchorTeleport
        );

        registrar.playToServer(
                GraveRequestPayload.TYPE,
                GraveRequestPayload.STREAM_CODEC,
                PacketHandler::handleGraveRequest
        );

        registrar.playToClient(
                GravePayload.TYPE,
                GravePayload.STREAM_CODEC,
                PacketHandler::handleGrave
        );
    }
}