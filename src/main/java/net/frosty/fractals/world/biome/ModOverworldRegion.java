package net.frosty.fractals.world.biome;

import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.util.function.Consumer;

import static terrablender.api.ParameterUtils.*;

public class ModOverworldRegion extends Region {
    public ModOverworldRegion(Identifier name, RegionType type, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> mapper) {

        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(Temperature.span(Temperature.NEUTRAL, Temperature.COOL))
                .humidity(Humidity.span(Humidity.NEUTRAL, Humidity.HUMID))
                .continentalness(Continentalness.span(Continentalness.MID_INLAND,Continentalness.INLAND))
                .erosion(Erosion.EROSION_3, Erosion.EROSION_5)
                .depth(Depth.SURFACE)
                .weirdness(Weirdness.MID_SLICE_NORMAL_ASCENDING, Weirdness.MID_SLICE_NORMAL_DESCENDING)
                .build().forEach(point -> builder.add(point, ModBiomes.FRACTAL_FOREST));

        // Add our points to the mapper
        builder.build().forEach(mapper);
    }


//        this.addModifiedVanillaOverworldBiomes(mapper,modifiedVanillaOverworldBuilder -> {
//            modifiedVanillaOverworldBuilder.replaceBiome(BiomeKeys.FOREST, ModBiomes.FRACTAL_FOREST);
//        });
//    }
}
