package com.Polarice3.goety_spillage.util;

import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.goety_spillage.GoetySpillage;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GSDamageSource extends ModDamageSource {
    public static ResourceKey<DamageType> FRICK_YOU = create("frick_you");

    public GSDamageSource(Holder<DamageType> p_270906_, @Nullable Entity p_270796_, @Nullable Entity p_270459_, @Nullable Vec3 p_270623_) {
        super(p_270906_, p_270796_, p_270459_, p_270623_);
    }

    public static ResourceKey<DamageType> create(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, GoetySpillage.location(name));
    }

    public static DamageSource frickYou(LivingEntity pMob) {
        return ModDamageSource.entityDamageSource(pMob.level, FRICK_YOU, pMob);
    }

    public static void bootstrap(BootstapContext<DamageType> context) {
        context.register(FRICK_YOU, new DamageType("goety_spillage.frickYou", 0.0F));
    }
}
