package net.frosty.fractals;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.frosty.fractals.commands.commandCentre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fractals implements ModInitializer {
	public static final String MOD_ID = "fractals";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(commandCentre::register);
	}
}