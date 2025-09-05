package com.Polarice3.goety_spillage.common.entities.ally.factory;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.items.EngineerMalletItem;
import com.Polarice3.goety_spillage.common.items.GSItems;
import com.Polarice3.goety_spillage.config.GSSpellConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

public class GSFactory extends EngineerMachine {
    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(GSFactory.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(GSFactory.class, EntityDataSerializers.BOOLEAN);
    public AnimationState introAnimationState = new AnimationState();
    public AnimationState spinAnimationState = new AnimationState();
    private int introTicks;
    private int spawnTicks;

    public GSFactory(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.MAX_HEALTH, 30.0);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION_STATE, 0);
        this.entityData.define(ACTIVE, true);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SpawnTicks")){
            this.spawnTicks = compound.getInt("SpawnTicks");
        }
        if (compound.contains("Active")){
            this.setActive(compound.getBoolean("Active"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SpawnTicks", this.spawnTicks);
        compound.putBoolean("Active", this.isActive());
    }

    public void setAnimationState(int state) {
        this.entityData.set(ANIMATION_STATE, state);
    }

    public AnimationState getAnimationState(String var1) {
        if (Objects.equals(var1, "spin")) {
            return this.spinAnimationState;
        } else {
            return Objects.equals(var1, "intro") ? this.introAnimationState : new AnimationState();
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_21104_) {
        if (ANIMATION_STATE.equals(p_21104_) && this.level.isClientSide) {
            switch (this.entityData.get(ANIMATION_STATE)) {
                case 0:
                    this.stopAllAnimationStates();
                    break;
                case 1:
                    this.stopAllAnimationStates();
                    this.spinAnimationState.start(this.tickCount);
                    break;
                case 2:
                    this.stopAllAnimationStates();
                    this.introAnimationState.start(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(p_21104_);
    }

    private void stopAllAnimationStates() {
        this.spinAnimationState.stop();
        this.introAnimationState.stop();
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource p_147189_) {
        if (this.isInMotion()) {
            this.introTicks = 1;
            this.setAnimationState(2);
            this.setInMotion(false);
        }

        return false;
    }

    public boolean isActive() {
        return this.entityData.get(ACTIVE);
    }

    public void setActive(boolean active) {
        this.entityData.set(ACTIVE, active);
    }

    public List<FactoryServant> getMinions() {
        return this.level.getEntitiesOfClass(FactoryServant.class, this.getBoundingBox().inflate(100.0D), servant -> servant.getFactory() != null && servant.getFactory() == this && servant.isAlive());
    }

    public void tick() {
        if (this.introTicks == 1) {
            this.playSound(SoundEvents.ZOMBIE_ATTACK_IRON_DOOR);
        }

        if (this.introTicks > 0) {
            ++this.introTicks;
        }

        if (this.introTicks == 11) {
            this.playSound(SoundEvents.PISTON_EXTEND, 1.0F, 0.75F);
        }

        if (this.introTicks == 16) {
            this.playSound(SoundEvents.PISTON_EXTEND, 1.0F, 1.0F);
        }

        if (this.introTicks == 21) {
            this.playSound(SoundEvents.PISTON_EXTEND, 1.0F, 1.25F);
        }

        if (!this.isInMotion()) {
            this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
            if (this.level instanceof ServerLevel serverLevel){
                if (this.isActive()) {
                    ++this.spawnTicks;
                    if (this.spawnTicks > 60 && this.isAlive() && this.getMinions().size() < GSSpellConfig.FactoryServantLimit.get()) {
                        this.playSound(SoundEvents.DISPENSER_LAUNCH, 1.0F, 1.0F);
                        int randomSelection = this.random.nextInt(0, 3);
                        float f = this.yBodyRot * 0.017453292F * 0.25F;
                        float f1 = Mth.cos(f);
                        double jump = 0.5D;
                        FactoryServant summoned;
                        Vec3 vec3;
                        if (randomSelection == 0) {
                            summoned = GSEntityTypes.BEEPER.get().create(serverLevel);
                            vec3 = new Vec3(this.getX(), this.getY(), this.getZ() + (double) f1 * -0.1);
                        } else if (randomSelection == 1) {
                            summoned = GSEntityTypes.SNIPER.get().create(serverLevel);
                            vec3 = new Vec3(this.getX() + (double) f1 * 0.45, this.getY(), this.getZ() + (double) f1 * -0.2);
                            jump = 0.1D;
                        } else {
                            summoned = GSEntityTypes.POKER.get().create(serverLevel);
                            vec3 = new Vec3(this.getX() + (double) f1 * -0.55, this.getY(), this.getZ() + (double) f1 * 0.05);
                        }
                        if (summoned != null) {
                            summoned.setPos(vec3);
                            summoned.setDeltaMovement(0.0D, jump, 0.0D);
                            summoned.setTrueOwner(this.getTrueOwner() != null ? this.getTrueOwner() : this);
                            summoned.setFactory(this);
                            summoned.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                            serverLevel.addFreshEntity(summoned);
                        }

                        this.spawnTicks = 0;
                    }
                }
            }
        }

        if (this.onGround() && this.isInMotion()) {
            if (this.introTicks < 1) {
                this.introTicks = 1;
            }

            this.setAnimationState(2);
            this.setInMotion(false);
        }

        super.tick();
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4){
            this.setActive(true);
        } else if (p_21375_ == 5){
            this.setActive(false);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getMainHandItem();
        if (pPlayer == this.getTrueOwner() && !this.isInMotion()) {
            if (!(itemstack.getItem() instanceof IWand) && !(itemstack.getItem() instanceof EngineerMalletItem)) {
                if (!this.level.isClientSide) {
                    float f = 0.6F;
                    if (this.isActive()) {
                        this.setActive(false);
                        this.level.broadcastEntityEvent(this, (byte) 5);
                    } else {
                        this.setActive(true);
                        this.level.broadcastEntityEvent(this, (byte) 4);
                        f = 0.5F;
                    }
                    this.playSound(SoundEvents.LEVER_CLICK, 1.0F, f);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public ItemStack getFactoryItem() {
        return GSItems.FACTORY_PACKAGE.get().getDefaultInstance();
    }
}
