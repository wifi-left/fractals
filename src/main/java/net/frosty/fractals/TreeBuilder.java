package net.frosty.fractals;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import java.util.Stack;

public class TreeBuilder {

    public static void drawBranch(Vector3f start, Vector3f end, World world, Block block){
        Vector3f dir = new Vector3f(end).sub(start).normalize(0.5F);
        float distance = start.distance(end);
        Vector3f pos = new Vector3f(start);
        while (start.distance(pos)<distance){
            BlockPos bp = new BlockPos(Math.round(pos.x),Math.round(pos.y),Math.round(pos.z));
            world.setBlockState(bp, block.getDefaultState());
            pos.add(dir);
        }
        BlockPos bp = new BlockPos(Math.round(end.x),Math.round(end.y),Math.round(end.z));
        world.setBlockState(bp, block.getDefaultState());

    }

    public static void buildSimple(String[] sentence, int x, int y, int z, float delta, float size, World world){

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
                drawBranch(prev,pos,world,Blocks.OAK_WOOD);
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

    public static void draw3DBranch(Vector3f start, Vector3f end, float radius, World world){
        Vector3f dir = new Vector3f(end).sub(start).normalize();
        float distance = start.distance(end);
        Vector3f pos = new Vector3f(start);

        Vector3f arbitrary = Math.abs(dir.y) < 0.9f ? new Vector3f(0,1,0) : new Vector3f(1,0,0);
        Vector3f normal = dir.cross(arbitrary, new Vector3f()).normalize();
        Vector3f binormal = dir.cross(normal, new Vector3f()).normalize();

        while (start.distance(pos)<distance){
            float angleStep = 1/(2*radius);
            float theta = 0F;

            while (theta<2*Math.PI){
                theta+=angleStep;
                float cosT = (float) Math.cos(theta);
                float sinT = (float) Math.sin(theta);
                Vector3f offset = new Vector3f(normal).mul(cosT*radius).add(new Vector3f(binormal).mul(sinT*radius));
                Vector3f circlePoint = new Vector3f(pos).add(offset);
                drawBranch(pos,circlePoint,world,Blocks.OAK_WOOD);
            }
            pos.add(new Vector3f(dir).mul(0.5F));
        }
        BlockPos bp = new BlockPos(Math.round(end.x),Math.round(end.y),Math.round(end.z));
        world.setBlockState(bp, Blocks.OAK_WOOD.getDefaultState());

    }

    public static void drawLeaf(Vector3f start, Vector3f dir,float radius, World world){
        Vector3f centre  = new Vector3f(start).add(new Vector3f(dir).mul(radius));

        Vector3f arbitrary = Math.abs(dir.y) < 0.9f ? new Vector3f(0,1,0) : new Vector3f(1,0,0);
        Vector3f normal = dir.cross(arbitrary, new Vector3f()).normalize();

        float angleStep = 1/(2*radius);
        float theta = 0F;

        while (theta<2*Math.PI){
            theta+=angleStep;
            float cosT = (float) Math.cos(theta);
            float sinT = (float) Math.sin(theta);
            Vector3f offset = new Vector3f(dir).mul(cosT*radius).add(new Vector3f(normal).mul(sinT*radius*0.5F));
            Vector3f ovalPoint = new Vector3f(centre).add(offset);
            drawBranch(ovalPoint,ovalPoint,world,Blocks.OAK_LEAVES); //make this take in an argument of OAK_LEAVES
        }

    }

    public static void buildThree(String[] sentence, int x, int y, int z, float delta, float length, float radius, World world) {
        Stack<Vector3f> posStack = new Stack<>();
        Stack<Vector3f> dirStack = new Stack<>();
        Stack<Float> rStack = new Stack<>();
        Stack<Float> lStack = new Stack<>();
        posStack.push(new Vector3f(x,y,z));
        dirStack.push(new Vector3f(0,1,0));
        rStack.push(radius);
        lStack.push(length);

        Vector3f pos = new Vector3f(x,y,z);
        Vector3f prev;
        Vector3f direction = new Vector3f(0,1,0);
        delta = (float) Math.toRadians(delta);

        for (int i=0;i<sentence.length;i++){
            prev = new Vector3f(pos);
            String symbol = sentence[i];
            if (symbol.equals("F")) {
                pos = pos.add(new Vector3f(direction).mul(length));
                System.out.println("DRAWING BRANCH - " + (float) (i) / sentence.length * 100 + "%");
                draw3DBranch(prev, pos, radius, world);

            } else if (symbol.equals("A")){
                drawLeaf(pos,direction,4F,world);

            } else if (symbol.equals("-")) {
                direction.mul(new Matrix3f().rotationY(-delta));

            } else if (symbol.equals("+")) {
                direction.mul(new Matrix3f().rotationY(delta));

            } else if (symbol.equals("&")) {
                direction.mul(new Matrix3f().rotationX(delta));

            } else if (symbol.equals("^")) {
                direction.mul(new Matrix3f().rotationX(-delta));

            } else if (symbol.equals("<")) {
                direction.mul(new Matrix3f().rotationZ(-delta));

            } else if (symbol.equals(">")) {
                direction.mul(new Matrix3f().rotationZ(-delta));

            } else if (symbol.equals("!")) {
                radius *= 0.75F;

            } else if (symbol.equals("@")) {
                length *= 0.75F;

            } else if (symbol.equals("[")) {
                posStack.push(new Vector3f(pos));
                dirStack.push(new Vector3f(direction));
                rStack.push(radius);
                lStack.push(length);
            } else if (symbol.equals("]")) {
                pos = posStack.pop();
                direction = dirStack.pop();
                radius = rStack.pop();
                length = lStack.pop();
            }

        }
    }
}
