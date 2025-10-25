package net.frosty.fractals.world.tree.custom.FractalGeneration;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class FractalBuilder {

    public static void asyncStochasticThree(MinecraftServer server, World world, int x, int y, int z, float length, float radius, float delta, int iterations, String[] axiom, HashMap<String, String[][]> rules, ServerPlayerEntity player, float decay){

        Runnable runIteration = new Runnable() {
            int i = 0;
            String[] sentenceHolder = axiom.clone();
            @Override
            public void run() {
                if (i >= iterations) {
                    System.out.println("Tree complete!");
                    return;
                }
                int iteration = i + 1;
//                System.out.println("ITERATION " + iteration + "...");
                sentenceHolder = LSystemHelper.UpdateStochasticSentence(sentenceHolder, rules, false);

//                System.out.println("BUILDING iteration " + iteration + "...");
                if (iteration==iterations) {
                    TreeBuilder.buildThreeFractal(sentenceHolder, x, y, z, delta, length, radius, world, player, iteration, Blocks.OAK_WOOD, decay);
                }

                i++;
                CompletableFuture.delayedExecutor(0, TimeUnit.SECONDS)
                        .execute(this);
            }
        };

        CompletableFuture.runAsync(runIteration);

    }

    public static void asyncThree(MinecraftServer server, World world, int x, int y, int z, float length, float radius, float delta, int iterations, String[] axiom, HashMap<String, String[]> rules, ServerPlayerEntity player, Block block, Float decay){
        System.out.println("iterations: " + iterations);
        System.out.println("delta: " + delta);

        Runnable runIteration = new Runnable() {
            int i = 0;
            String[] sentenceHolder = axiom.clone();
            @Override
            public void run() {
                System.out.println("i number: " + i);
                if (i >= iterations) {
                    System.out.println("Tree complete!");
                    return;
                }
                int iteration = i + 1;
//                System.out.println("ITERATION " + iteration + "...");
                if (block.equals(Blocks.BLACK_CONCRETE)) {
                    sentenceHolder = LSystemHelper.UpdateSentence(sentenceHolder, rules,false);
                } else {
                    sentenceHolder = LSystemHelper.UpdateSentence(sentenceHolder, rules, false);
                }
                System.out.println("BUILDING iteration " + iteration + "...");
                System.out.println(block);
//                System.out.println(Arrays.toString(sentenceHolder));
                if (block.equals(Blocks.BLACK_CONCRETE) && iteration==iterations) {
                    TreeBuilder.buildThreeFractal(sentenceHolder, x, y, z, delta, length, radius, world, player, iteration, block, decay);
                } else if (!block.equals(Blocks.BLACK_CONCRETE) && iteration==iterations){
//                    System.out.println('e');
                    TreeBuilder.buildThreeFractal(sentenceHolder, x, y, z, delta, length, radius, world, player, iteration, block, decay);
//                    System.out.println("built layer");
                }


                i++;
                CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS)
                        .execute(this);
            }
        };

        CompletableFuture.runAsync(runIteration);

    }

}
