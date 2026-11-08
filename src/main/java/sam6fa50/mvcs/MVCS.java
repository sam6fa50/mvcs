package sam6fa50.mvcs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sam6fa50.mvcs.tracking.Tracker;
import sam6fa50.mvcs.tracking.MixinBus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class MVCS implements ModInitializer {
	public static final String MOD_ID = "mvcs";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Our tracker object, per world
	private static Tracker tracker = null;

	// Our event intercept, singleton per launch
	private static final MixinBus MIXIN_BUS = MixinBus.getInstance();

	// Our GSON interface object
	public static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.create();


    @Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
			// Our tracker object is per world
			tracker = Tracker.getCurrentInstance();
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(minecraftServer -> {
			Path saveDirectory = minecraftServer
					.getWorldPath(LevelResource.ROOT)
					.resolve("mvcs");

            try {
                Files.createDirectories(saveDirectory);

				Path trackerFile = saveDirectory.resolve("tracker.json");

				Files.writeString(
						trackerFile,
						tracker.serialize(),
						StandardCharsets.UTF_8);

				LOGGER.info("Tracker saved to {}", trackerFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Tracker killed each world save after serializing
			Tracker.closeCurrentInstance();
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
