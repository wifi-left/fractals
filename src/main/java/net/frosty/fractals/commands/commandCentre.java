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
        dispatcher.register(CommandManager.literal("fractalTree")
                .then(CommandManager.argument("x", IntegerArgumentType.integer())
                .then(CommandManager.argument("y", IntegerArgumentType.integer())
                .then(CommandManager.argument("z", IntegerArgumentType.integer())
                .then(CommandManager.argument("size", FloatArgumentType.floatArg())
                .then(CommandManager.argument("iterations", IntegerArgumentType.integer())
                .then(CommandManager.argument("seed", IntegerArgumentType.integer()).executes(commandCentre::fractalTree))))))));

    }

    private static int fractalTree(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Integer x = IntegerArgumentType.getInteger(context,"x");
        Integer y = IntegerArgumentType.getInteger(context,"y");
        Integer z = IntegerArgumentType.getInteger(context,"z");
        Float size = FloatArgumentType.getFloat(context,"size");
        Integer iterations = IntegerArgumentType.getInteger(context,"iterations");
        Integer seed = IntegerArgumentType.getInteger(context,"seed");
        World world = context.getSource().getWorld();

        System.out.println("GENERATING TREE...");

        String[] axiom = {"F"};
        float delta = 25.7F;
        HashMap<String, String[]> rules = new HashMap<>();
        rules.put("F", new String[]{"F", "[", "+", "F", "]", "F", "[", "-", "F", "]", "F"});

        String[] sentence = axiom.clone();
        for (int i=0;i<iterations;i++){
            System.out.println("ITERATION " + (i+1) + "...");
            sentence = LSystemHelper.UpdateSentence(sentence, rules);
//            System.out.println("BUILDING... " + (i+1) + "...");
            TreeBuilder.buildSimple(sentence,x,y,z,delta,size,world);
        }

        return 1;
    }
}
