package net.frosty.fractals.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.frosty.fractals.world.ModPlacedFeatures;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.VegetationPlacedFeatures;

public class ModTreeGeneration {
    public static void generateTrees(){
//        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.PLAINS, BiomeKeys.MEADOW),
//                GenerationStep.Feature.VEGETAL_DECORATION, ModPlacedFeatures.FRACTAL_OAK_PLACED);
//
//        BiomeModifications.create(Identifier.of("fractals","remove_vanilla_trees"))
//                .add(
//                        ModificationPhase.REMOVALS,
//                        BiomeSelectors.includeByKey(BiomeKeys.PLAINS, BiomeKeys.MEADOW),
//                        context -> {
//                            context.getGenerationSettings().removeFeature(
//                                    GenerationStep.Feature.VEGETAL_DECORATION,
//                                    VegetationPlacedFeatures.TREES_PLAINS
//                            );
//                            context.getGenerationSettings().removeFeature(
//                                    GenerationStep.Feature.VEGETAL_DECORATION,
//                                    VegetationPlacedFeatures.TREES_MEADOW
//                            );
//                        }
//                );

    }
}
