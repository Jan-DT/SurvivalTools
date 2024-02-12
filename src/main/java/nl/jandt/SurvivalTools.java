/* Licensed under MPL 2.0, available at https://www.mozilla.org/en-US/MPL/2.0/ */
package nl.jandt;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import nl.jandt.conflict.ConstitutionBook;
import nl.jandt.information.InfoCommand;
import nl.jandt.information.MotdCommand;
import nl.jandt.utils.SrvConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SurvivalTools implements ModInitializer {
	public static final String MOD_ID = "survivaltools";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final SrvConfig CONFIG = SrvConfig.createAndLoad();
	public static final Logger LOGGER = LoggerFactory.getLogger("survivaltools");


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> InfoCommand.instance.registerCommand(dispatcher));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> MotdCommand.instance.registerCommand(dispatcher));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ConstitutionBook.instance.registerCommand(dispatcher));

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> MotdCommand.instance.registerJoin(handler));

		LOGGER.info("Loaded SurvivalTools!");
	}
}