package net.frosty.fractals.world.biome;

import net.frosty.fractals.Fractals;
import net.minecraft.util.Identifier;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.api.TerraBlenderApi;

public class ModTerrablenderAPI implements TerraBlenderApi {
    @Override
    public void onTerraBlenderInitialized() {
        Regions.register(new ModOverworldRegion(Identifier.of(Fractals.MOD_ID, "overworld"), RegionType.OVERWORLD,6));


    }
}
