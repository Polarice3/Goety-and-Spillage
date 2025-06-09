package com.Polarice3.goety_spillage.data;

import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.util.GSDamageSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class GSRegisteryDataProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, GSDamageSource::bootstrap);

    public GSRegisteryDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, Set.of("minecraft", GoetySpillage.MOD_ID));
    }
}
