package com.Polarice3.goety_spillage.init;

import com.Polarice3.goety_spillage.GoetySpillage;
import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.Set;

public class GSLootTables {
    private static final Set<ResourceLocation> LOCATIONS = Sets.newHashSet();
    private static final Set<ResourceLocation> IMMUTABLE_LOCATIONS = Collections.unmodifiableSet(LOCATIONS);
    public static final ResourceLocation EMPTY = new ResourceLocation("empty");

    public static final ResourceLocation ENGINEER_EXTRA = register("entities/engineer_extra");
    public static final ResourceLocation ILLAGER_BOSS_EXTRA = register("entities/illager_boss_extra");

    private static ResourceLocation register(String pId) {
        return register(GoetySpillage.location(pId));
    }

    private static ResourceLocation register(ResourceLocation pId) {
        if (LOCATIONS.add(pId)) {
            return pId;
        } else {
            throw new IllegalArgumentException(pId + " is already a registered built-in loot table");
        }
    }

    public static Set<ResourceLocation> all() {
        return IMMUTABLE_LOCATIONS;
    }

}
