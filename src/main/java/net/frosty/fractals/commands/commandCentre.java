package net.frosty.fractals.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.frosty.fractals.world.tree.custom.FractalGeneration.FractalBuilder;
import net.frosty.fractals.world.tree.custom.FractalGeneration.LSystemHelper;
import net.frosty.fractals.world.tree.custom.FractalGeneration.TreeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Stack;

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

        dispatcher.register(CommandManager.literal("STOCHASTIC_TREE")
                .then(CommandManager.argument("x", IntegerArgumentType.integer())
                .then(CommandManager.argument("y", IntegerArgumentType.integer())
                .then(CommandManager.argument("z", IntegerArgumentType.integer())
                .then(CommandManager.argument("length", FloatArgumentType.floatArg())
                .then(CommandManager.argument("radius", FloatArgumentType.floatArg())
                .then(CommandManager.argument("delta", FloatArgumentType.floatArg())
                .then(CommandManager.argument("decay", FloatArgumentType.floatArg())
                .then(CommandManager.argument("iterations", IntegerArgumentType.integer())
                .then(CommandManager.argument("ruleset", IntegerArgumentType.integer()).executes(commandCentre::stochasticTree)))))))))));

        dispatcher.register(CommandManager.literal("GIANT_TREE")
                .executes(context -> giantTree(context, 150))
                .then(CommandManager.argument("height", IntegerArgumentType.integer(40, 300))
                        .executes(context -> giantTree(context, IntegerArgumentType.getInteger(context, "height")))));
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

        HashMap<Integer, HashMap<String, String[]>> rulesets  = new HashMap<>() ;
        HashMap<Integer, String[]> axioms = new HashMap<>();
        HashMap<Integer, Block> blocks = new HashMap<>();
        HashMap<Integer, Float> deltas = new HashMap<>();
        axioms.put(1,new String[]{"F"});
        blocks.put(1, Blocks.SPRUCE_WOOD);
        deltas.put(1,25.7F);
            HashMap<String, String[]> rules = new HashMap<>();
            rules.put("F", new String[]{"F", "[", "+", "F", "]", "F", "[", "-", "F", "]", "F"});
            rulesets.put(1,rules);

        deltas.put(2,120F); //Sierpinski Triangle
        axioms.put(2,new String[]{"A","-","B","-","B"});
        blocks.put(2, Blocks.BLACK_CONCRETE);
            rules = new HashMap<>();
            rules.put("A", new String[]{"A","-","B","+","A","+","B","-","A"});
            rules.put("B", new String[]{"B","B"});
            rulesets.put(2,rules);

        axioms.put(3,new String[]{"A"});    //Hilbert Curve
        blocks.put(3, Blocks.BLACK_CONCRETE);
        deltas.put(3,90F);
        rules = new HashMap<>();
        rules.put("A", ("-BF+AFA+FB-").split(""));
        rules.put("B", ("+AF-BFB-FA+").split(""));
        rulesets.put(3,rules);

        deltas.put(4,60F);  //Koch Snowflake
        axioms.put(4, ("F++F++F").split(""));
        blocks.put(4, Blocks.BLACK_CONCRETE);
        rules = new HashMap<>();
        rules.put("F", ("F-F++F-F").split(""));
        rulesets.put(4,rules);

        deltas.put(5,90F);
        axioms.put(5, ("FX").split(""));
        blocks.put(5, Blocks.BLACK_CONCRETE);
        rules = new HashMap<>();
        rules.put("X", ("X+YF").split(""));
        rules.put("Y", ("FX-Y").split(""));
        rulesets.put(5,rules);

        deltas.put(6,20F);
        axioms.put(6, ("F").split(""));
        blocks.put(6, Blocks.SPRUCE_WOOD);
        rules = new HashMap<>();
        rules.put("F", ("F[+F]F[-F][F]").split(""));
        rulesets.put(6,rules);

        deltas.put(7,22.5F);
        axioms.put(7, ("F").split(""));
        blocks.put(7, Blocks.SPRUCE_WOOD);
        rules = new HashMap<>();
        rules.put("F", ("FF-[-F+F+F]+[+F-F-F]").split(""));
        rulesets.put(7,rules);

        deltas.put(8,22.5F);
        axioms.put(8, ("X").split(""));
        blocks.put(8, Blocks.SPRUCE_WOOD);
        rules = new HashMap<>();
        rules.put("X", ("F-[[X]+X]+F[+FX]-X").split(""));
        rules.put("F", ("FF").split(""));
        rulesets.put(8,rules);

        deltas.put(9,90F);  //BROKEN
        axioms.put(9, ("F-F-F-F").split(""));
        blocks.put(9, Blocks.BLACK_CONCRETE);
        rules = new HashMap<>();
        rules.put("X", ("FF-F-F-F-FF").split(""));
        rulesets.put(9,rules);

        deltas.put(10,90F); //BROKEN
        axioms.put(10, ("F-F-F-F").split(""));
        blocks.put(10, Blocks.BLACK_CONCRETE);
        rules = new HashMap<>();
        rules.put("X", ("FF-F-F-F-F-F+F").split(""));
        rulesets.put(10,rules);

        deltas.put(11,90F);
        axioms.put(11, ("F+F+F+F").split(""));
        blocks.put(11, Blocks.BLACK_CONCRETE);
        rules = new HashMap<>();
        rules.put("F", ("F+f-FF+F+FF+Ff+FF-f+FF-F-FF-Ff-FFF").split(""));
        rules.put("f", ("ffffff").split(""));
        rulesets.put(11,rules);

        deltas.put(12,20F);
        axioms.put(12, ("F").split(""));
        blocks.put(12, Blocks.SPRUCE_WOOD);
        rules = new HashMap<>();
        rules.put("F", ("F[+F][-F]").split(""));
        rulesets.put(12,rules);



        String[] sentence = axioms.get(ruleNo);
        for (int i=0;i<iterations;i++){
            System.out.println("ITERATION " + (i+1) + "...");
            sentence = LSystemHelper.UpdateSentence(sentence, rulesets.get(ruleNo), false);
//            System.out.println("BUILDING... " + (i+1) + "...");
        }
        TreeBuilder.buildSimple(sentence,x,y,z,deltas.get(ruleNo),size,world,blocks.get(ruleNo));

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
        MinecraftServer server = context.getSource().getServer();

        System.out.println("GENERATING TREE...");
        HashMap<Integer, String[]> axioms = new HashMap<>();
        HashMap<Integer, Block> blocks = new HashMap<>();
        HashMap<Integer, HashMap<String, String[]>> rulesets  = new HashMap<>() ;
        HashMap<String, String[]> rules = new HashMap<>();
        HashMap<Integer, Float> deltas = new HashMap<>();
        HashMap<Integer, Float> decays = new HashMap<>();

        axioms.put(1,new String[]{"A"});
        blocks.put(1,Blocks.OAK_WOOD);
        deltas.put(1,20F);
        decays.put(1,0.8F);
        rules.put("A", ("f[-^<@!A]F[&@!A]F[+^<@!A]").split(""));
        rules.put("F", ("f[<<L]").split(""));
        rulesets.put(1,rules);

        axioms.put(2,new String[]{"X"});
        blocks.put(2, Blocks.BLACK_CONCRETE);
        rules = new HashMap<>();
        deltas.put(2,90F);
        decays.put(2,1F);
        rules.put("X", ("^<XF^<XFX-F^>>XFX&F+>>XFX-F>X->").split(""));
        rulesets.put(2,rules);

        axioms.put(3,new String[]{"A"});
        blocks.put(3, Blocks.OAK_WOOD);
        rules = new HashMap<>();
        deltas.put(3,22.5F);
        decays.put(3,1F);
        rules.put("A", ("[&@FL!A]>>>>>'[&@FL!A]>>>>>>>'[&@FL!A]").split(""));
        rules.put("F", ("S>>>>>F").split(""));
        rules.put("S", ("F[^^L]").split(""));
        rulesets.put(3,rules);

        axioms.put(4,new String[]{"A"});
        blocks.put(4, Blocks.OAK_WOOD);
        rules = new HashMap<>();
        deltas.put(4,22.5F);
        decays.put(4,1F);
        rules.put("A", ("[&@FL!A]>>>>>'[&@FL!A]>>>>>>>'[&@FL!A]").split(""));
        rules.put("F", ("S>>>>>F").split(""));
        rules.put("S", ("F[^^L]").split(""));
        rulesets.put(4,rules);

        FractalBuilder.asyncThree(server,world,x,y,z,length,radius,deltas.get(ruleNo),iterations,axioms.get(ruleNo),rulesets.get(ruleNo),context.getSource().getPlayer(),blocks.get(ruleNo),decays.get(ruleNo));


        return 1;
    }

    private static int stochasticTree(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int x = IntegerArgumentType.getInteger(context,"x");
        int y = IntegerArgumentType.getInteger(context,"y");
        int z = IntegerArgumentType.getInteger(context,"z");
        float length = FloatArgumentType.getFloat(context,"length");
        float radius = FloatArgumentType.getFloat(context,"radius");
        float delta = FloatArgumentType.getFloat(context,"delta");
        float decay = FloatArgumentType.getFloat(context,"decay");
        int iterations = IntegerArgumentType.getInteger(context,"iterations");
        int ruleNo = IntegerArgumentType.getInteger(context,"ruleset");
        World world = context.getSource().getWorld();
        MinecraftServer server = context.getSource().getServer();

        System.out.println("GENERATING TREE...");
        HashMap<Integer, String[]> axioms = new HashMap<>();
        HashMap<Integer, HashMap<String, String[][]>> rulesets  = new HashMap<>() ;
        HashMap<String, String[][]> rules = new HashMap<>();
        axioms.put(1,new String[]{"B"});
        rules.put("A", new String[][]{
                {"F","[","^","-","-","-","F","!","@","A","]","A","!","@"},
                {"F","[","&","-","F","!","@","A","]","A","!","@"},
                {"F","[","^","+","F","!","@","A","]","A","!","@"},
                {"F","[","^","-","-","-","F","!","@","A","]","[","&","-","F","!","@","A","]","[","^","+","F","!","@","A","]"}
        });
        rules.put("B", new String[][]{
                {"f","[","!","@","f","A","]"}
        });
        rules.put("F", new String[][]{
                {"F","[","<","<","!","!","L","]"},
                {"F","[",">",">","!","!","L","]"},
                {"F","[","&","&","!","!","L","]"},
                {"F","[","^","^","!","!","L","]"}
        });
        rulesets.put(1,rules);

        rules = new HashMap<>();
        axioms.put(2,new String[]{"B"});
        rules.put("A", new String[][]{
                ("F[^&@FL!A][@F!A]").split(""),
                ("F[^>>>>>'&@FL!A][@F!A]").split(""),
                ("F[^>>>>>>>>>>>>&@FL!A][@F!A]").split(""),
                ("F[&@FL!A]>>>>>'[&@FL!A]>>>>>>>'[&@FL!A]").split(""),
                ("F[&@FL!A]>>>>>'[&@FL!A]>>>>>>>'[&@FL!A]").split("")
        });
        rules.put("B", new String[][]{
                {"f","[","!","@","A","]"}
        });
        rules.put("F", new String[][]{
                ("f[^^L]").split(""),
                ("f[&&L]").split("")
        });
        rulesets.put(2,rules);

        rules = new HashMap<>();
        axioms.put(3,new String[]{"B"});
        rules.put("A", new String[][]{
                ("F![^&@FLA][@FA]").split(""),
                ("F![^>>>>'&@FLA][@FA]").split(""),
                ("F![^>>>>>>>>>&@FLA][@FA]").split(""),
                ("F![&@FLA]>>>>'[&@FLA]>>>>>'[&@FLA]").split(""),
                ("F![&@FLA]>>>>'[&@FLA]>>>>>'[&@FLA]").split("")
        }); //rules for base
        rules.put("B", new String[][]{
                ("[!|P]f[!@A]").split("")
        }); //rules for placing leaves
        rules.put("F", new String[][]{
                ("f[^^L]").split(""),
                ("f[&&L]").split("")
        }); //rules for branches, leaf spawning
        rules.put("F", new String[][]{
                ("f[^^L]").split(""),
                ("f[&&L]").split("")
        }); //root base rules
        rules.put("P", new String[][]{
                ("[>>>>&&&f*!!R]>>>>>[&&&f*!!R]>>>>>[&&&f*!!R]").split(""),
                (">>[>&&&f*!!R]>>>>[&&&f*!!R]>>>>>[&&&f*!!R]").split(""),
                ("[>&&&f*!!R]>>>>[&&&f*!!R]>>>>>[&&&f*!!R]").split(""),
        }); //root rules
        rules.put("R", new String[][]{
                ("[+.f*!R][.f*!R][-.f*!R]").split(""),
                ("[+.f*!R][-.f*!R]").split(""),
        });
        rulesets.put(3,rules);

        FractalBuilder.asyncStochasticThree(server,world,x,y,z,length,radius,delta,iterations,axioms.get(ruleNo),rulesets.get(ruleNo),context.getSource().getPlayer(), decay);

        return 1;
    }

    private static int giantTree(CommandContext<ServerCommandSource> context, int targetHeight) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        World world = context.getSource().getWorld();
        MinecraftServer server = context.getSource().getServer();
        BlockPos base = player.getBlockPos();

        float delta = 20F;
        float decay = 0.8F;
        float radius = 2F;
        int iterations = 7;
        String[] axiom = new String[]{"A"};
        HashMap<String, String[]> rules = new HashMap<>();
        rules.put("A", ("f[-^<@!A]F[&@!A]F[+^<@!A]").split(""));
        rules.put("F", ("f[<<L]").split(""));

        String[] sentence = axiom;
        for (int i = 0; i < iterations; i++) {
            sentence = LSystemHelper.UpdateSentence(sentence, rules, false);
        }

        float baseHeight = estimateMaxHeight(sentence, delta, 1F, decay);
        float branchLength = targetHeight / Math.max(baseHeight, 1F);

        player.sendMessage(Text.of("Generating giant tree (target height: " + targetHeight + ", branch length: " + String.format("%.2f", branchLength) + ")"), false);
        FractalBuilder.asyncThree(server, world, base.getX(), base.getY(), base.getZ(), branchLength, radius, delta, iterations, axiom, rules, player, Blocks.OAK_WOOD, decay);
        return 1;
    }

    private static float estimateMaxHeight(String[] sentence, float deltaDegrees, float initialLength, float decay) {
        float delta = (float) Math.toRadians(deltaDegrees);
        Vector3f position = new Vector3f(0, 0, 0);
        Vector3f direction = new Vector3f(0, 1, 0);
        Vector3f up = new Vector3f(0, 0, 1);
        Vector3f right = direction.cross(up, new Vector3f());
        if (right.lengthSquared() == 0) {
            right = new Vector3f(1, 0, 0);
        } else {
            right.normalize();
        }
        float length = initialLength;
        float maxY = 0F;

        Stack<Vector3f> posStack = new Stack<>();
        Stack<Vector3f> dirStack = new Stack<>();
        Stack<Vector3f> upStack = new Stack<>();
        Stack<Vector3f> rightStack = new Stack<>();
        Stack<Float> lengthStack = new Stack<>();

        for (String symbol : sentence) {
            direction.normalize();
            up.normalize();
            right.normalize();
            if (symbol.equals("F") || symbol.equals("f")) {
                position.add(new Vector3f(direction).mul(length));
                maxY = Math.max(maxY, position.y);
            } else if (symbol.equals("-")) {
                Matrix3f rot = new Matrix3f().rotation(-delta, up.x, up.y, up.z);
                direction.mul(rot);
                right.mul(rot);
            } else if (symbol.equals("+")) {
                Matrix3f rot = new Matrix3f().rotation(delta, up.x, up.y, up.z);
                direction.mul(rot);
                right.mul(rot);
            } else if (symbol.equals("&")) {
                Matrix3f rot = new Matrix3f().rotation(-delta, right.x, right.y, right.z);
                direction.mul(rot);
                up.mul(rot);
            } else if (symbol.equals("^")) {
                Matrix3f rot = new Matrix3f().rotation(delta, right.x, right.y, right.z);
                direction.mul(rot);
                up.mul(rot);
            } else if (symbol.equals("<")) {
                Matrix3f rot = new Matrix3f().rotation(-delta, direction.x, direction.y, direction.z);
                right.mul(rot);
                up.mul(rot);
            } else if (symbol.equals(">")) {
                Matrix3f rot = new Matrix3f().rotation(delta, direction.x, direction.y, direction.z);
                right.mul(rot);
                up.mul(rot);
            } else if (symbol.equals("@")) {
                length *= decay;
            } else if (symbol.equals("[")) {
                posStack.push(new Vector3f(position));
                dirStack.push(new Vector3f(direction));
                upStack.push(new Vector3f(up));
                rightStack.push(new Vector3f(right));
                lengthStack.push(length);
            } else if (symbol.equals("]")) {
                position = posStack.pop();
                direction = dirStack.pop();
                up = upStack.pop();
                right = rightStack.pop();
                length = lengthStack.pop();
            }
        }

        return maxY;
    }

}
