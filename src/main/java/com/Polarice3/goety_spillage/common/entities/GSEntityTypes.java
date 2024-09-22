package com.Polarice3.goety_spillage.common.entities;

import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.common.entities.ally.GSSpiritHand;
import com.Polarice3.goety_spillage.common.entities.ally.RagnoServant;
import com.Polarice3.goety_spillage.common.entities.ally.undead.bound.BoundFreakager;
import com.Polarice3.goety_spillage.common.entities.neutral.VillagerVictim;
import com.Polarice3.goety_spillage.common.entities.projectiles.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GSEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GoetySpillage.MOD_ID);

    public static final RegistryObject<EntityType<GSIllagerSoul>> ILLAGER_SOUL = register("illager_soul",
            EntityType.Builder.of(GSIllagerSoul::new, MobCategory.MONSTER)
                    .sized(1.0F, 1.0F)
                    .fireImmune());

    public static final RegistryObject<EntityType<GSSpiritHand>> SPIRIT_HAND = register("spirit_hand",
            EntityType.Builder.of(GSSpiritHand::new, MobCategory.CREATURE)
                    .sized(1.5F, 1.5F)
                    .fireImmune());

    public static final RegistryObject<EntityType<VillagerVictim>> VILLAGER_VICTIM = register("villager_victim",
            EntityType.Builder.of(VillagerVictim::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F));

    public static final RegistryObject<EntityType<BoundFreakager>> BOUND_FREAKAGER = register("bound_freakager",
            EntityType.Builder.of(BoundFreakager::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F));

    public static final RegistryObject<EntityType<RagnoServant>> RAGNO_SERVANT = register("ragno_servant",
            EntityType.Builder.of(RagnoServant::new, MobCategory.MONSTER)
                    .sized(3.2F, 1.8F));

    public static final RegistryObject<EntityType<GSSoulBeam>> SOUL_BEAM = register("soul_beam",
            EntityType.Builder.<GSSoulBeam>of(GSSoulBeam::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20));

    public static final RegistryObject<EntityType<GSSkullBomb>> SKULL_BOMB = register("skull_bomb",
            EntityType.Builder.of(GSSkullBomb::new, MobCategory.MONSTER)
                    .sized(0.5F, 0.5F));

    public static final RegistryObject<EntityType<GSPumpkinBomb>> PUMPKIN_BOMB = register("pumpkin_bomb",
            EntityType.Builder.of(GSPumpkinBomb::new, MobCategory.MONSTER)
                    .sized(0.75F, 0.75F));

    public static final RegistryObject<EntityType<ThrownAxe>> THROWN_AXE = register("thrown_axe",
            EntityType.Builder.of(ThrownAxe::new, MobCategory.MONSTER)
                    .sized(0.2F, 0.2F));

    public static final RegistryObject<EntityType<FreakyScythe>> FREAKY_SCYTHE = register("freaky_scythe",
            EntityType.Builder.of(FreakyScythe::new, MobCategory.MONSTER)
                    .sized(0.2F, 0.2F));

    public static final RegistryObject<EntityType<DarkPotion>> DARK_POTION = register("dark_potion",
            EntityType.Builder.of(DarkPotion::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<WebProjectile>> WEB = register("web",
            EntityType.Builder.of(WebProjectile::new, MobCategory.MONSTER)
                    .sized(0.2F, 0.2F));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String p_20635_, EntityType.Builder<T> p_20636_) {
        return ENTITY_TYPE.register(p_20635_, () -> p_20636_.build(GoetySpillage.location(p_20635_).toString()));
    }
}
