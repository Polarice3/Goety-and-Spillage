package com.Polarice3.goety_spillage.common.capabilities.spillage;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpillageProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
    public static Capability<ISpillage> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    ISpillage instance = new SpillageImp();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CAPABILITY ? LazyOptional.of(() -> (T) instance) : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return SpillageCapHelper.save(new CompoundTag(), instance);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        SpillageCapHelper.load(nbt, instance);
    }
}
