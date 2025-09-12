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
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class commandCentre {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registry, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("2D_TREE")
                .then(CommandManager.argument("x", IntegerArgumentType.integer())
                .then(CommandManager.argument("y", IntegerArgumentType.integer())
                .then(CommandManager.argument("z", IntegerArgumentType.integer())
                .then(CommandManager.argument("size", FloatArgumentType.floatArg())
                .then(CommandManager.argument("iterations", IntegerArgumentType.integer())
                .then(CommandManager.argument("ruleset", IntegerArgumentType.integer()).executes(commandCentre::twoTree))))))));

        dispatcher.register(CommandManager.literal("3D_TREE")
                .then(CommandManager.argument("x", IntegerArgumentType.integer())
                .then(CommandManager.argument("y", IntegerArgumentType.integer())
                .then(CommandManager.argument("z", IntegerArgumentType.integer())
                .then(CommandManager.argument("length", FloatArgumentType.floatArg())
                .then(CommandManager.argument("radius", FloatArgumentType.floatArg())
                .then(CommandManager.argument("iterations", IntegerArgumentType.integer())
                .then(CommandManager.argument("ruleset", IntegerArgumentType.integer()).executes(commandCentre::threeTree)))))))));

    }

    private static int twoTree(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int x = IntegerArgumentType.getInteger(context,"x");
        int y = IntegerArgumentType.getInteger(context,"y");
        int z = IntegerArgumentType.getInteger(context,"z");
        float size = FloatArgumentType.getFloat(context,"size");
        int iterations = IntegerArgumentType.getInteger(context,"iterations");
        int ruleNo = IntegerArgumentType.getInteger(context,"ruleset");
        World world = context.getSource().getWorld();

        System.out.println("GENERATING TREE...");

        String[] axiom = {"F"};
        float delta = 22.5F;
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

    private static int threeTree(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int x = IntegerArgumentType.getInteger(context,"x");
        int y = IntegerArgumentType.getInteger(context,"y");
        int z = IntegerArgumentType.getInteger(context,"z");
        float length = FloatArgumentType.getFloat(context,"length");
        float radius = FloatArgumentType.getFloat(context,"radius");
        int iterations = IntegerArgumentType.getInteger(context,"iterations");
        int ruleNo = IntegerArgumentType.getInteger(context,"ruleset");
        World world = context.getSource().getWorld();

        System.out.println("GENERATING TREE...");
        HashMap<Integer, String> axioms = new HashMap<>();
        float delta =30F;
        HashMap<Integer, HashMap<String, String[]>> rulesets  = new HashMap<>() ;
        HashMap<String, String[]> rules = new HashMap<>();
        axioms.put(1,"A");
        rules.put("A", new String[]{"F","[","&","-","-","-","F","!","@","A","]","F","[","^","-","F","!","@","A","]","F","[","&","+","F","!","@","A","]"});
        rules.put("F", new String[]{"F","[",">",">","!","!","F","L","]"});
        rulesets.put(1,rules);

        rules = new HashMap<>();
        axioms.put(2,"F");
        rules.put("F", new String[]{"F","F","[","&","-","-","-","F","!","@","F","]","F","[","^","-","F","!","@","F","]","[","&","+","F","!","@","F","]"});
        rulesets.put(2,rules);

        String[] sentence = new String[] {axioms.get(ruleNo)};
        for (int i=0;i<iterations;i++){
            System.out.println("ITERATION " + (i+1) + "...");
            sentence = LSystemHelper.UpdateSentence(sentence, rulesets.get(ruleNo));
            System.out.println("BUILDING... " + (i+1) + "...");
            TreeBuilder.buildThree(sentence,x,y,z,delta,length,radius,world);
        }

        return 1;
    }
}
