package net.frosty.fractals.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.frosty.fractals.LSystemHelper;
import net.frosty.fractals.TreeBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class commandCentre {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registry, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("2D_TREE")
                .then(CommandManager.argument("x", IntegerArgumentType.integer())
                .then(CommandManager.argument("y", IntegerArgumentType.integer())
                .then(CommandManager.argument("z", IntegerArgumentType.integer())
                .then(CommandManager.argument("size", FloatArgumentType.floatArg())
                .then(CommandManager.argument("delta", FloatArgumentType.floatArg())
                .then(CommandManager.argument("iterations", IntegerArgumentType.integer())
                .then(CommandManager.argument("ruleset", IntegerArgumentType.integer()).executes(commandCentre::twoTree)))))))));

        dispatcher.register(CommandManager.literal("3D_TREE")
                .then(CommandManager.argument("x", IntegerArgumentType.integer())
                .then(CommandManager.argument("y", IntegerArgumentType.integer())
                .then(CommandManager.argument("z", IntegerArgumentType.integer())
                .then(CommandManager.argument("length", FloatArgumentType.floatArg())
                .then(CommandManager.argument("radius", FloatArgumentType.floatArg())
                .then(CommandManager.argument("delta", FloatArgumentType.floatArg())
                .then(CommandManager.argument("iterations", IntegerArgumentType.integer())
                .then(CommandManager.argument("ruleset", IntegerArgumentType.integer()).executes(commandCentre::threeTree))))))))));

        dispatcher.register(CommandManager.literal("STOCHASTIC_TREE")
                .then(CommandManager.argument("x", IntegerArgumentType.integer())
                .then(CommandManager.argument("y", IntegerArgumentType.integer())
                .then(CommandManager.argument("z", IntegerArgumentType.integer())
                .then(CommandManager.argument("length", FloatArgumentType.floatArg())
                .then(CommandManager.argument("radius", FloatArgumentType.floatArg())
                .then(CommandManager.argument("delta", FloatArgumentType.floatArg())
                .then(CommandManager.argument("iterations", IntegerArgumentType.integer())
                .then(CommandManager.argument("ruleset", IntegerArgumentType.integer()).executes(commandCentre::stochasticTree))))))))));

    }

    private static int twoTree(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int x = IntegerArgumentType.getInteger(context,"x");
        int y = IntegerArgumentType.getInteger(context,"y");
        int z = IntegerArgumentType.getInteger(context,"z");
        float size = FloatArgumentType.getFloat(context,"size");
        float delta = FloatArgumentType.getFloat(context,"delta");
        int iterations = IntegerArgumentType.getInteger(context,"iterations");
        int ruleNo = IntegerArgumentType.getInteger(context,"ruleset");
        World world = context.getSource().getWorld();

        System.out.println("GENERATING TREE...");

        String[] axiom = {"F"};
        HashMap<Integer, HashMap<String, String[]>> rulesets  = new HashMap<>() ;
            HashMap<String, String[]> rules = new HashMap<>();
            rules.put("F", new String[]{"F", "[", "+", "F", "]", "F", "[", "-", "F", "]", "F"});
            rulesets.put(1,rules);

            rules = new HashMap<>();
            rules.put("F", new String[]{"F", "F", "-", "[", "-", "F", "+", "F", "+", "F", "]","+","[","+","F","-","F","-","F","]"});
            rulesets.put(2,rules);

        String[] sentence = axiom.clone();
        for (int i=0;i<iterations;i++){
            System.out.println("ITERATION " + (i+1) + "...");
            sentence = LSystemHelper.UpdateSentence(sentence, rulesets.get(ruleNo));
//            System.out.println("BUILDING... " + (i+1) + "...");
            TreeBuilder.buildSimple(sentence,x,y,z,delta,size,world);
        }

        return 1;
    }

    private static void asyncThree(MinecraftServer server, World world, int x, int y, int z, float length, float radius, float delta, int iterations, String axiom, HashMap<String, String[]> rules){
        final String[][] sentenceHolder = { new String[]{axiom}};

        Runnable runIteration = new Runnable() {
            int i = 0; // iteration counter

            @Override
            public void run() {
                if (i >= iterations) {
                    System.out.println("Tree complete!");
                    return;
                }
                int iteration = i + 1;
                System.out.println("ITERATION " + iteration + "...");
                sentenceHolder[0] = LSystemHelper.UpdateSentence(sentenceHolder[0], rules);

                // Schedule the block placement on the server thread
                server.execute(() -> {
                    System.out.println("BUILDING iteration " + iteration + "...");
                    TreeBuilder.buildThree(sentenceHolder[0], x, y, z, delta, length, radius, world);
                });

                i++;
                // Schedule the next iteration after a delay (e.g., 1 second)
                CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS)
                        .execute(this);
            }
        };

        CompletableFuture.runAsync(runIteration);

    }

    private static int threeTree(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int x = IntegerArgumentType.getInteger(context,"x");
        int y = IntegerArgumentType.getInteger(context,"y");
        int z = IntegerArgumentType.getInteger(context,"z");
        float length = FloatArgumentType.getFloat(context,"length");
        float radius = FloatArgumentType.getFloat(context,"radius");
        float delta = FloatArgumentType.getFloat(context,"delta");
        int iterations = IntegerArgumentType.getInteger(context,"iterations");
        int ruleNo = IntegerArgumentType.getInteger(context,"ruleset");
        World world = context.getSource().getWorld();
        MinecraftServer server = context.getSource().getServer();

        System.out.println("GENERATING TREE...");
        HashMap<Integer, String> axioms = new HashMap<>();
        HashMap<Integer, HashMap<String, String[]>> rulesets  = new HashMap<>() ;
        HashMap<String, String[]> rules = new HashMap<>();
        axioms.put(1,"A");
        rules.put("A", new String[]{"F","[","&","-","-","-","F","!","@","A","]","F","[","^","-","F","!","@","A","]","F","[","&","+","F","!","@","A","]"});
        rules.put("F", new String[]{"F","[",">",">","!","!","L","]"});
        rulesets.put(1,rules);

        rules = new HashMap<>();
        axioms.put(2,"A");
        rules.put("A", new String[]{"F","[","&","-","-","-","F","!","@","F","]","[","^","-","F","!","@","F","]","[","&","+","F","!","@","F","]"});
        rules.put("F", new String[]{"F","[",">",">","!","!","L","]"});
        rulesets.put(2,rules);

        asyncThree(server,world,x,y,z,length,radius,delta,iterations,axioms.get(ruleNo),rulesets.get(ruleNo));

        return 1;
    }

    private static int stochasticTree(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int x = IntegerArgumentType.getInteger(context,"x");
        int y = IntegerArgumentType.getInteger(context,"y");
        int z = IntegerArgumentType.getInteger(context,"z");
        float length = FloatArgumentType.getFloat(context,"length");
        float radius = FloatArgumentType.getFloat(context,"radius");
        float delta = FloatArgumentType.getFloat(context,"delta");
        int iterations = IntegerArgumentType.getInteger(context,"iterations");
        int ruleNo = IntegerArgumentType.getInteger(context,"ruleset");
        World world = context.getSource().getWorld();

        System.out.println("GENERATING TREE...");
        HashMap<Integer, String> axioms = new HashMap<>();
        HashMap<Integer, HashMap<String, String[][]>> rulesets  = new HashMap<>() ;
        HashMap<String, String[][]> rules = new HashMap<>();
        axioms.put(1,"A");
        rules.put("A", new String[][]{
                {"F","[","&","-","-","-","F","!","@","A","]","A","!","@"},
                {"F","[","^","-","F","!","@","A","]","A","!","@"},
                {"F","[","&","+","F","!","@","A","]","A","!","@"},
                {"F","[","&","-","-","-","F","!","@","A","]","[","^","-","F","!","@","A","]","[","&","+","F","!","@","A","]"},
                {"F","[","!","@","F","A","]"}
        });
        rules.put("F", new String[][]{
                {"F","[",">",">","!","!","L","]"},
                {"F","[","<","<","!","!","L","]"}
        });
        rulesets.put(1,rules);

        rules = new HashMap<>();
        axioms.put(2,"A");
        rules.put("A", new String[][]{{"F","[","&","-","-","-","F","!","@","F","]","[","^","-","F","!","@","F","]","[","&","+","F","!","@","F","]"}});
        rules.put("F", new String[][]{{"F","[",">",">","!","!","L","]"}});
        rulesets.put(2,rules);

        String[] sentence = new String[] {axioms.get(ruleNo)};
        for (int i=0;i<iterations;i++){
            System.out.println("ITERATION " + (i+1) + "...");
            sentence = LSystemHelper.UpdateStochasticSentence(sentence, rulesets.get(ruleNo));
            System.out.println("BUILDING... " + (i+1) + "...");
            TreeBuilder.buildThree(sentence,x,y,z,delta,length,radius,world);
        }

        return 1;
    }

}
