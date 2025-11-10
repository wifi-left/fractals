package net.frosty.fractals.world.tree.custom.FractalGeneration;

import net.minecraft.util.math.BlockPos;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Stack;

public class LightTreeBuilder {

    public static HashSet<BlockPos> drawLightBranch(Vector3f start, Vector3f end){
        HashSet<BlockPos> toEdit = new HashSet<>();

        Vector3f dir = new Vector3f(end).sub(start).normalize(0.5F);
        float distance = start.distance(end);
        Vector3f pos = new Vector3f(start);

        while (start.distance(pos)<distance){
            BlockPos bp = new BlockPos(Math.round(pos.x),Math.round(pos.y),Math.round(pos.z));
            toEdit.add(bp);
            pos.add(dir);
        }
        BlockPos bp = new BlockPos(Math.round(end.x),Math.round(end.y),Math.round(end.z));
        toEdit.add(bp);

        return toEdit;

    }

    public static HashSet<BlockPos> drawLight3DBranch(Vector3f start, Vector3f end, float radius){
        HashSet<BlockPos> toEdit = new HashSet<>();
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
                HashSet<BlockPos> toAddToEdit = drawLightBranch(pos,circlePoint);
                toEdit.addAll(toAddToEdit);
            }
            pos.add(new Vector3f(dir).mul(0.5F));
        }
        BlockPos bp = new BlockPos(Math.round(end.x),Math.round(end.y),Math.round(end.z));
        toEdit.add(bp);

        return toEdit;

    }

    public static HashSet<BlockPos> drawLightLeaf(Vector3f start, Vector3f dir, float leafLength){
        HashSet<BlockPos> toLeaf = new HashSet<>();

        Vector3f centre  = new Vector3f(start).add(new Vector3f(dir).mul(leafLength));

        Vector3f arbitrary = new Vector3f((float)Math.random(),(float)Math.random(),(float)Math.random());
        Vector3f normal = dir.cross(arbitrary, new Vector3f()).normalize();

        float angleStep = 1/(2*leafLength);

        for (float theta=0f;theta<2*Math.PI;theta+=angleStep){
            float cosT = (float) Math.cos(theta);
            float sinT = (float) Math.sin(theta);
            Vector3f offset = new Vector3f(dir).mul(cosT*leafLength).add(new Vector3f(normal).mul(sinT*leafLength*0.5F));
            Vector3f ovalPoint = new Vector3f(centre).add(offset);
            HashSet<BlockPos> toAddToLeaf = drawLightBranch(centre,ovalPoint);
            toLeaf.addAll(toAddToLeaf);
        }

        return toLeaf;

    }

    public static HashSet[] buildLightTree(String[] sentence, BlockPos StartPos, float delta, float length, float radius, float decay, float trunkDecay, float leafLength) {
        HashSet<BlockPos> toEdit = new HashSet<>();
        HashSet<BlockPos> toLeaf = new HashSet<>();

        Stack<Vector3f> posStack = new Stack<>();
        Stack<Vector3f> dirStack = new Stack<>();
        Stack<Vector3f> upStack = new Stack<>();
        Stack<Vector3f> rightStack = new Stack<>();
        Stack<Float> rStack = new Stack<>();
        Stack<Float> lStack = new Stack<>();
        posStack.push(new Vector3f(StartPos.getX(), StartPos.getY(), StartPos.getZ()));
        dirStack.push(new Vector3f(0, 1, 0));
        rStack.push(radius);
        lStack.push(length);

        Vector3f pos = new Vector3f(StartPos.getX(), StartPos.getY(), StartPos.getZ());
        Vector3f prev;
        Vector3f direction = new Vector3f(0, 1, 0);
        delta = (float) Math.toRadians(delta);


        Vector3f up = new Vector3f(0, 0, 1);      // arbitrary initial up
        Vector3f right = direction.cross(up, new Vector3f()).normalize();
        upStack.push(new Vector3f(up));
        rightStack.push(new Vector3f(right));

        for (int i = 0; i < sentence.length; i++) {
            prev = new Vector3f(pos);
            direction.normalize();
            up.normalize();
            right.normalize();
            String symbol = sentence[i];
//            System.out.println(symbol + " sada: " + i);
            if (symbol.equals("F") || symbol.equals("f")) {
//                System.out.println("direction: " + direction);
                pos.add(new Vector3f(direction).mul(length));
//                System.out.println("length: "+length);
//                System.out.println("DRAWING BRANCH - " + (float) (i) / sentence.length * 100 + "%");
                HashSet<BlockPos> toAddToEdit = drawLight3DBranch(prev, pos, radius);
                toEdit.addAll(toAddToEdit);

            } else if (symbol.equals("L")) {
                HashSet<BlockPos> addToLeaf = drawLightLeaf(pos, direction,leafLength);
                toLeaf.addAll(addToLeaf);
//                drawSphereLeaf(pos,direction,4F,world);

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

            } else if (symbol.equals("|")) {
                direction.rotateX((float) Math.PI);
                up.rotateX((float) Math.PI);

            } else if (symbol.equals(".")) {
                Matrix3f rot = new Matrix3f().rotation(-delta/3, right.x, right.y, right.z);
                direction.mul(rot);
                up.mul(rot);

            } else if (symbol.equals("!")) {
                radius *= trunkDecay;

            } else if (symbol.equals("@")) {
                length *= decay;

            } else if (symbol.equals("[")) {
                posStack.push(new Vector3f(pos));
                dirStack.push(new Vector3f(direction));
                rStack.push(radius);
                lStack.push(length);
                upStack.push(new Vector3f(up));
                rightStack.push(new Vector3f(right));

            } else if (symbol.equals("]")) {
                pos = posStack.pop();
                direction = dirStack.pop();
                radius = rStack.pop();
                length = lStack.pop();
                right = rightStack.pop();
                up = upStack.pop();
            }

        }

        return new HashSet[]{toEdit, toLeaf};

    }

}
