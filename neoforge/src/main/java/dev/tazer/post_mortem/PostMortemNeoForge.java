package dev.tazer.post_mortem;


import dev.tazer.post_mortem.registry.PMDataSerializers;
import dev.tazer.post_mortem.registry.RegistryHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(PostMortem.MOD_ID)
public class PostMortemNeoForge {

    public PostMortemNeoForge(IEventBus bus) {
        PostMortem.init();

        bus.addListener(PostMortemNeoForge::register);
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
}