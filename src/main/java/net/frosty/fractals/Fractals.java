package net.frosty.fractals;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.frosty.fractals.block.ModBlocks;
import net.frosty.fractals.commands.commandCentre;
import net.frosty.fractals.item.ModItems;
import net.frosty.fractals.mixin.TrunkPlacerTypeInvoker;
import net.frosty.fractals.world.tree.custom.FractalTrunkPlacer;
import net.frosty.fractals.world.gen.ModWorldGeneration;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Fractals implements ModInitializer {
	public static final String MOD_ID = "fractals";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final TrunkPlacerType<FractalTrunkPlacer> FRACTAL_TRUNK_PLACER = TrunkPlacerTypeInvoker.callRegister("fractals:fractal_trunk_placer", FractalTrunkPlacer.CODEC);
	public static Map<ChunkPos, List<BlockPos>> deferredLogs = new ConcurrentHashMap<>();
	public static Map<ChunkPos, List<BlockPos>> deferredLeaves =  new ConcurrentHashMap<>();
	public static Map<ChunkPos, BlockPos> minDistanceBuffer = new ConcurrentHashMap<>();

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModWorldGeneration.generateModWorldGen();

//		CommandRegistrationCallback.EVENT.register(commandCentre::register);

		ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
	}

	private void onServerTick(MinecraftServer server){
		ServerWorld world = server.getOverworld();

		Iterator<Map.Entry<ChunkPos, List<BlockPos>>> it = deferredLeaves.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<ChunkPos, List<BlockPos>> entry = it.next();
			ChunkPos chunkPos = entry.getKey();
			if (world.isChunkLoaded(chunkPos.x,chunkPos.z)){
				for (BlockPos bp : new ArrayList<>(entry.getValue())){
					world.setBlockState(bp, ModBlocks.LIGHT_LEAVES.getDefaultState().with(Properties.PERSISTENT,true));
				}
				it.remove();
			}
		}

		it = deferredLogs.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<ChunkPos, List<BlockPos>> entry = it.next();
			ChunkPos chunkPos = entry.getKey();
			if (world.isChunkLoaded(chunkPos.x,chunkPos.z)){
				for (BlockPos bp : new ArrayList<>(entry.getValue())){
					world.setBlockState(bp, Blocks.OAK_WOOD.getDefaultState());
				}
				it.remove();
//				minDistanceBuffer.remove(entry.getKey());
			}
		}

	}


}