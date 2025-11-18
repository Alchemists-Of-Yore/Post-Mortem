package dev.tazer.post_mortem;

import dev.tazer.post_mortem.platform.Services;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostMortem {

    public static final String MOD_ID = "post_mortem";
    public static final Logger LOGGER = LoggerFactory.getLogger("Post Mortem");

    public static void init() {
        if (Services.PLATFORM.isClient()) {
            PostMortemClient.init();
        }
    }

    /**
     * @param path the resource path
     * @return a {@link ResourceLocation} with the namespace {@link PostMortem#MOD_ID}
     */
    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    /**
     * @param key the lang key
     * @return a translatable {@link Component} with the prefix {@link PostMortem#MOD_ID}
     */
    public static Component lang(String key) {
        return Component.translatable(MOD_ID + "." + key);
    }
}