package net.frosty.fractals;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.frosty.fractals.block.ModBlocks;
import net.frosty.fractals.commands.commandCentre;
import net.frosty.fractals.item.ModItems;
import net.frosty.fractals.mixin.TrunkPlacerTypeInvoker;
import net.frosty.fractals.world.tree.custom.FractalTrunkPlacer;
import net.frosty.fractals.world.gen.ModWorldGeneration;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fractals implements ModInitializer {
	public static final String MOD_ID = "fractals";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final TrunkPlacerType<FractalTrunkPlacer> FRACTAL_TRUNK_PLACER = TrunkPlacerTypeInvoker.callRegister("fractals:fractal_trunk_placer", FractalTrunkPlacer.CODEC);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModWorldGeneration.generateModWorldGen();


		CommandRegistrationCallback.EVENT.register(commandCentre::register);
	}

}