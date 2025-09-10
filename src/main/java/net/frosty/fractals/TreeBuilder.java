package net.frosty.fractals;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.Stack;
import java.util.Vector;

public class TreeBuilder {

    public static void drawBranch(Vector3f start, Vector3f end, World world){
        Vector3f dir = new Vector3f(end).sub(start).normalize(0.5F);
        float distance = start.distance(end);
        Vector3f pos = new Vector3f(start);
        while (start.distance(pos)<distance){
            BlockPos bp = new BlockPos(Math.round(pos.x),Math.round(pos.y),Math.round(pos.z));
            world.setBlockState(bp, Blocks.OAK_WOOD.getDefaultState());
            pos.add(dir);
        }
        BlockPos bp = new BlockPos(Math.round(end.x),Math.round(end.y),Math.round(end.z));
        world.setBlockState(bp, Blocks.OAK_WOOD.getDefaultState());

    }

    public static void buildSimple(String[] sentence, Integer x, Integer y, Integer z, Float delta, Float size, World world){

        Stack<Vector3f> posStack = new Stack<>();
        Stack<Float> angleStack = new Stack<>();
        posStack.push(new Vector3f(x,y,z));
        angleStack.push(0F);

        Vector3f pos = new Vector3f(x,y,z);
        Vector3f prev;
        Float angle = 0F;
        delta = (float) Math.toRadians(delta);

        for (int i=0;i<sentence.length;i++){
            prev = new Vector3f(pos);
            String symbol = sentence[i];
            if (symbol.equals("F")){
                pos.x += (float) (size*Math.sin(angle)); //move by size in direction of angle
                pos.y += (float) (size*Math.cos(angle));
//                System.out.println("DRAWING BRANCH");
                drawBranch(prev,pos, world);
            } else if (symbol.equals("-")) {
                angle -= delta;
            } else if (symbol.equals("+")) {
                angle += delta;
            } else if (symbol.equals("[")) {
                posStack.push(new Vector3f(pos));
                angleStack.push(angle);
            } else if (symbol.equals("]")) {
                pos = posStack.pop();
                angle = angleStack.pop();
            }

        }
    }

}
