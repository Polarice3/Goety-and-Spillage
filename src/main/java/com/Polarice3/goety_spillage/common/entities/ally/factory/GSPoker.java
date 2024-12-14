package com.Polarice3.goety_spillage.common.entities.ally.factory;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class GSPoker extends FactoryServant{
    public GSPoker(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new WanderGoal<>(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.ATTACK_DAMAGE, 1.0);
    }

    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor p_37856_, DifficultyInstance p_37857_, MobSpawnType p_37858_, @Nullable SpawnGroupData p_37859_, @Nullable CompoundTag p_37860_) {
        this.populateDefaultEquipmentSlots(p_37856_.getRandom(), p_37857_);
        return super.finalizeSpawn(p_37856_, p_37857_, p_37858_, p_37859_, p_37860_);
    }

    protected void populateDefaultEquipmentSlots(RandomSource p_217055_, DifficultyInstance p_217056_) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        this.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
    }

    public boolean doHurtTarget(Entity p_21372_) {
        boolean superHurt = super.doHurtTarget(p_21372_);
        if (superHurt) {
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_ENGINEER_POKER_POKE.get(), 2.0F, this.getVoicePitch());
        }

        return superHurt;
    }
}
