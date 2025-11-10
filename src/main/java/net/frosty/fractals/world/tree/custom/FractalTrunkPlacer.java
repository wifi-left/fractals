package net.frosty.fractals.world.tree.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frosty.fractals.Fractals;
import net.frosty.fractals.world.tree.custom.FractalGeneration.LSystemHelper;
import net.frosty.fractals.world.tree.custom.FractalGeneration.LightTreeBuilder;
import net.frosty.fractals.world.tree.custom.FractalGeneration.TreeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import org.spongepowered.include.com.google.common.collect.ImmutableList;

import java.util.*;
import java.util.function.BiConsumer;

public class FractalTrunkPlacer extends TrunkPlacer {

    // Use the fillTrunkPlacerFields to create our codec
    public static final MapCodec<FractalTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            fillTrunkPlacerFields(instance).apply(instance, FractalTrunkPlacer::new));

    public FractalTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
    }

    public int minSeparation = 90;

    @Override
    protected TrunkPlacerType<?> getType() {
        return Fractals.FRACTAL_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {

        //check if tree is too close
        Iterator<Map.Entry<ChunkPos, BlockPos>> it = Fractals.minDistanceBuffer.entrySet().iterator();
        boolean goodSeparation = true;
        while(it.hasNext()) {
            Map.Entry<ChunkPos, BlockPos> entry = it.next();
            double distance = Math.sqrt(startPos.getSquaredDistance(entry.getValue()));
            if (distance < minSeparation){
                goodSeparation = false;
            }
        }

        //dont generate tree if too close
        if (!goodSeparation){
            System.out.println("prevented spawn at: " + startPos);
            return ImmutableList.of(new FoliagePlacer.TreeNode(startPos.up(height), 0, false),
                    new FoliagePlacer.TreeNode(startPos.east().north().up(height), 0, false));
        }

        Fractals.minDistanceBuffer.put(new ChunkPos(startPos),startPos);

        // Set the ground beneath the trunk to dirt
        setToDirt(world, replacer, random, startPos.down(), config);

        HashMap<String, String[][]> rules = new HashMap<>();
        String[] axiom = new String[]{"B"};
        //rules for anchor nodes
        rules.put("A", new String[][]{
                ("F!>>>>[^&@FLA][@FA]").split(""),
                ("F!>>>[^>>>>'&@FLA][@FA]").split(""),
                ("F!>>[^>>>>>>>>>&@FLA][@FA]").split(""),
                ("F![&@FLA]>>>>'[&@FLA]>>>>>'[&@FLA]").split(""),
                ("F![&@FLA]>>>>'[&@FLA]>>>>>'[&@FLA]").split("")
        }); //rules for base
        rules.put("B", new String[][]{
                ("[!!@|P]f[!@A]").split("")
        }); //rules for placing leaves
        rules.put("F", new String[][]{
                ("f[^^L]").split(""),
                ("f[&&L]").split("")
        }); //rules for branches, leaf spawning
        rules.put("F", new String[][]{
                ("f[^^L]").split(""),
                ("f[&&L]").split("")
        }); //root base rule
        rules.put("P", new String[][]{
                ("[>&&f@!R]>>>>[&&f@!R]>>>>>[&&f@!R]").split(""),
                ("[>>>&&f@!R][<<<<&&f@!R]").split(""),
        }); //root rules
        rules.put("R", new String[][]{
                ("[>..f@!R][>>>>..f@!R][<<..f@!R]").split(""),
                ("[>>>..f@!R][<<..f@!R]").split(""),
        });

        String[] sentenceHolder = axiom.clone();
        int generations = 9;
        for(int i=0;i<generations;i++){
            sentenceHolder = LSystemHelper.UpdateStochasticSentence(sentenceHolder, rules, false);
        }
        double randomRadius = 2.5 + Math.random() * (3.5-2.5);
        double randomLength = 10 + Math.random() * (15-10);
        HashSet<BlockPos>[] context = LightTreeBuilder.buildLightTree(sentenceHolder, startPos, 26f, (float) randomLength, (float) randomRadius,0.85f, 0.75f, 5F);
        HashSet<BlockPos> toEdit = context[0];
        HashSet<BlockPos> toLeaf = context[1];

        for (BlockPos bp: toEdit) {
            List<BlockPos> currentDefer = Fractals.deferredLogs.get(new ChunkPos(bp));
            if (currentDefer==null){
                Fractals.deferredLogs.put(new ChunkPos(bp), new ArrayList<>(List.of(bp)));
            }
            else{
                currentDefer.add(bp);
                Fractals.deferredLogs.put(new ChunkPos(bp), currentDefer);
            }
        }

        for (BlockPos bp: toLeaf) {
            List<BlockPos> currentDefer = Fractals.deferredLeaves.get(new ChunkPos(bp));
            if (currentDefer==null){
                Fractals.deferredLeaves.put(new ChunkPos(bp), new ArrayList<>(List.of(bp)));
            }
            else{
                currentDefer.add(bp);
                Fractals.deferredLeaves.put(new ChunkPos(bp), currentDefer);
            }
        }

        // We create two TreeNodes - one for the first trunk, and the other for the second
        // Put the highest block in the trunk as the center position for the FoliagePlacer to use
        return ImmutableList.of(new FoliagePlacer.TreeNode(startPos.up(height), 0, false),
                new FoliagePlacer.TreeNode(startPos.east().north().up(height), 0, false));
    }
}