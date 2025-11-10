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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;

public class FractalTrunkPlacer extends TrunkPlacer {

    // Use the fillTrunkPlacerFields to create our codec
    public static final MapCodec<FractalTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            fillTrunkPlacerFields(instance).apply(instance, FractalTrunkPlacer::new));

    public FractalTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
    }

    @Override
    protected TrunkPlacerType<?> getType() {
        return Fractals.FRACTAL_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {

        TreeFeatureConfig leafConfig = new TreeFeatureConfig.Builder(
                BlockStateProvider.of(Blocks.OAK_LEAVES),config.trunkPlacer,
                config.foliageProvider,config.foliagePlacer,
                config.minimumSize
        ).build();

        // Set the ground beneath the trunk to dirt
        setToDirt(world, replacer, random, startPos.down(), config);

        HashMap<String, String[][]> rules = new HashMap<>();
        String[] axiom = new String[]{"B"};
        rules.put("A", new String[][]{
                ("F![^&@FLA][@FA]").split(""),
                ("F![^>>>>'&@FLA][@FA]").split(""),
                ("F![^>>>>>>>>>&@FLA][@FA]").split(""),
                ("F![&@FLA]>>>>'[&@FLA]>>>>>'[&@FLA]").split(""),
                ("F![&@FLA]>>>>'[&@FLA]>>>>>'[&@FLA]").split("")
        });
        rules.put("B", new String[][]{
                {"f","[","!","@","A","]"}
        });
        rules.put("F", new String[][]{
                ("f[^^L]").split(""),
                ("f[&&L]").split("")
        });

        String[] sentenceHolder = axiom.clone();
        int generations = 9;
        for(int i=0;i<generations;i++){
            sentenceHolder = LSystemHelper.UpdateStochasticSentence(sentenceHolder, rules, false);
        }
        double randomRadius = 2 + Math.random() * (3-2);
        double randomLength = 10 + Math.random() * (15-10);
        HashSet<BlockPos>[] context = LightTreeBuilder.buildLightTree(sentenceHolder, startPos, 26f, (float) randomLength, (float) randomRadius,0.85f, 0.75f, 5F);
        HashSet<BlockPos> toEdit = context[0];
        HashSet<BlockPos> toLeaf = context[1];

        // Iterate until the trunk height limit and place two blocks using the getAndSetState method from TrunkPlacer
        for (BlockPos bp: toEdit) {
//            boolean pass = this.getAndSetState(world, replacer, random, bp, config);
//            if (pass==false){
            List<BlockPos> currentDefer = Fractals.deferredLogs.get(new ChunkPos(bp));
            if (currentDefer==null){
                Fractals.deferredLogs.put(new ChunkPos(bp), new ArrayList<>(List.of(bp)));
            }
            else{
                currentDefer.add(bp);
                Fractals.deferredLogs.put(new ChunkPos(bp), currentDefer);
//            }
            }
        }

        for (BlockPos bp: toLeaf) {
//            this.getAndSetState(world, replacer, random, bp, leafConfig);
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