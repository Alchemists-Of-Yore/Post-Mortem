package dev.tazer.post_mortem.platform;

import dev.tazer.post_mortem.PostMortem;
import dev.tazer.post_mortem.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        PostMortem.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}