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
                .then(CommandManager.argument("size", FloatArgumentType.floatArg())
                .then(CommandManager.argument("iterations", IntegerArgumentType.integer())
                .then(CommandManager.argument("ruleset", IntegerArgumentType.integer()).executes(commandCentre::threeTree))))))));

    }

    private static int twoTree(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Integer x = IntegerArgumentType.getInteger(context,"x");
        Integer y = IntegerArgumentType.getInteger(context,"y");
        Integer z = IntegerArgumentType.getInteger(context,"z");
        Float size = FloatArgumentType.getFloat(context,"size");
        Integer iterations = IntegerArgumentType.getInteger(context,"iterations");
        Integer ruleNo = IntegerArgumentType.getInteger(context,"ruleset");
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
        Integer x = IntegerArgumentType.getInteger(context,"x");
        Integer y = IntegerArgumentType.getInteger(context,"y");
        Integer z = IntegerArgumentType.getInteger(context,"z");
        Float size = FloatArgumentType.getFloat(context,"size");
        Integer iterations = IntegerArgumentType.getInteger(context,"iterations");
        Integer ruleNo = IntegerArgumentType.getInteger(context,"ruleset");
        World world = context.getSource().getWorld();

        System.out.println("GENERATING TREE...");

        String[] axiom = {"A"};
        float delta = 22.5F;
        HashMap<Integer, HashMap<String, String[]>> rulesets  = new HashMap<>() ;
        HashMap<String, String[]> rules = new HashMap<>();
        rules.put("A", new String[]{"[", "&", "F", "L", "!", "A", "]", "/", "/", "/", "/","/","'","[","&","F","L","!","A","]","/","/","/","/","/","/","/","'","[","&","F","L","!","A","]"});
        rules.put("F", new String[]{"S", "/", "/", "/", "/", "/", "F"});
        rules.put("S", new String[]{"F", "L"});
        rulesets.put(1,rules);

        rules = new HashMap<>();
        rules.put("F", new String[]{"F", "F", "-", "[", "-", "F", "+", "F", "+", "F", "]","+","[","+","F","-","F","-","F","]"});
        rulesets.put(2,rules);

        String[] sentence = axiom.clone();
        for (int i=0;i<iterations;i++){
            System.out.println("ITERATION " + (i+1) + "...");
            sentence = LSystemHelper.UpdateSentence(sentence, rulesets.get(ruleNo));
//            System.out.println("BUILDING... " + (i+1) + "...");
            TreeBuilder.buildThree(sentence,x,y,z,delta,size,world);
        }

        return 1;
    }
}
