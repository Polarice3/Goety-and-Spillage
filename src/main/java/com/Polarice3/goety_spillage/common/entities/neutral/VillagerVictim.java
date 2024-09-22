package com.Polarice3.goety_spillage.common.entities.neutral;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.ExplosionUtil;
import com.Polarice3.Goety.utils.LootingExplosion;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.entities.ally.RagnoServant;
import com.yellowbrossproductions.illageandspillage.entities.CameraShakeEntity;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class VillagerVictim extends Summoned implements VillagerDataHolder {
    private static final EntityDataAccessor<Integer> VILLAGER_FACE = SynchedEntityData.defineId(VillagerVictim.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_TRADER = SynchedEntityData.defineId(VillagerVictim.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA = SynchedEntityData.defineId(VillagerVictim.class, EntityDataSerializers.VILLAGER_DATA);
    private int introTicks;
    public AnimationState transformAnimationState = new AnimationState();

    public VillagerVictim(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AlwaysWatchTargetGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VILLAGER_FACE, 0);
        this.entityData.define(IS_TRADER, false);
        this.entityData.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    protected Component getTypeName() {
        if (this.isTrader()) {
            return EntityType.WANDERING_TRADER.getDescription();
        } else {
            return super.getTypeName();
        }
    }

    @Override
    public boolean canUpdateMove() {
        return false;
    }

    public void tick() {
        if (this.level.isClientSide){
            this.transformAnimationState.startIfStopped(this.tickCount);
        }
        ++this.introTicks;
        if (this.introTicks > 0) {
            this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
        }
        if (this.introTicks < 5) {
            if (!this.level.isClientSide) {
                this.setVillagerFace(1);
            }
        }
        if (this.introTicks == 5) {
            if (!this.level.isClientSide) {
                this.setVillagerFace(2);
            }
        }
        if (this.introTicks == 10) {
            if (!this.level.isClientSide) {
                this.setVillagerFace(3);
            }

            this.playSound(SoundEvents.VILLAGER_TRADE, 1.0F, 1.0F);
        }
        if (this.introTicks == 20) {
            this.playSound(SoundEvents.TNT_PRIMED, 1.0F, 1.0F);
        }
        if (this.introTicks == 25) {
            if (!this.level.isClientSide) {
                this.setVillagerFace(0);
            }

            this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_VILLAGERHISS.get(), 1.0F, 1.0F);
        }
        if (this.introTicks == 40) {
            this.setVillagerFace(4);
        }
        if (this.introTicks == 50) {
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_SPAWN.get(), 1.0F, 1.0F);
        }
        if (this.introTicks == 65) {
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(this.level);
            if (lightning != null) {
                lightning.setPos(this.getX(), this.getY(), this.getZ());
                lightning.setVisualOnly(true);
                this.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 3.0F, 1.0F);
                this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 10000.0F, 1.0F);
                this.level.addFreshEntity(lightning);
                CameraShakeEntity.cameraShake(this.level, this.position(), 50.0F, 0.2F, 0, 10);
                if (!this.level.isClientSide) {
                    ExplosionUtil.lootExplode(this.level, this, this.getX(), this.getY(), this.getZ(), 4.0F, false, Explosion.BlockInteraction.KEEP, LootingExplosion.Mode.LOOT);
                    ExplosionUtil.lootExplode(this.level, this, this.getX(), this.getY(), this.getZ(), 4.0F, false, Explosion.BlockInteraction.KEEP, LootingExplosion.Mode.LOOT);
                    ExplosionUtil.lootExplode(this.level, this, this.getX(), this.getY(), this.getZ(), 4.0F, false, Explosion.BlockInteraction.KEEP, LootingExplosion.Mode.LOOT);
                }
            }

            RagnoServant ragno = GSEntityTypes.RAGNO_SERVANT.get().create(this.level);
            if (ragno != null) {
                ragno.setPos(this.getX(), this.getY(), this.getZ());
                ragno.setDeltaMovement(0.0D, 0.6D, 0.0D);
                ragno.setTarget(this.getTarget());
                if (this.getTrueOwner() != null) {
                    ragno.setTrueOwner(this.getTrueOwner());
                    if (this.getTrueOwner().distanceTo(ragno) <= 8.0F && !this.getTrueOwner().isPassenger()) {
                        this.getTrueOwner().startRiding(ragno);
                    }
                }
                if (this.getTarget() != null) {
                    ragno.lookAt(this.getTarget(), 30.0F, 30.0F);
                }
                if (this.level.addFreshEntity(ragno)){
                    if (this.getTrueOwner() instanceof ServerPlayer serverPlayer){
                        CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, this);
                    }
                }

                ragno.playIntro();
                this.makeRagnoParticles(ragno);
            }
            this.discard();
        }
        super.tick();
    }

    public void makeRagnoParticles(Entity caught) {
        if (this.level instanceof ServerLevel serverLevel){
            for(int i = 0; i < 75; ++i) {
                double d0 = -0.5D + this.random.nextGaussian();
                double d1 = -0.5D + this.random.nextGaussian();
                double d2 = -0.5D + this.random.nextGaussian();
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, caught.getRandomX(0.5), caught.getRandomY(), caught.getRandomZ(0.5), 0, d0, d1, d2, 0.5F);
            }

            for(int i = 0; i < 50; ++i) {
                double d0 = -0.5D + this.random.nextGaussian();
                double d1 = -0.5D + this.random.nextGaussian();
                double d2 = -0.5D + this.random.nextGaussian();
                serverLevel.sendParticles(ParticleTypes.POOF, caught.getRandomX(0.5), caught.getRandomY(), caught.getRandomZ(0.5), 0, d0, d1, d2, 0.5F);
            }

            for(int i = 0; i < 5; ++i) {
                double d0 = -0.5D + this.random.nextGaussian();
                double d1 = -0.5D + this.random.nextGaussian();
                double d2 = -0.5D + this.random.nextGaussian();
                serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, caught.getRandomX(0.5), caught.getRandomY(), caught.getRandomZ(0.5), 0, d0, d1, d2, 0.5F);
            }
        }
    }

    public boolean isPushable() {
        return false;
    }

    public void push(Entity entityIn) {
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        return pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    public int getVillagerFace() {
        return this.entityData.get(VILLAGER_FACE);
    }

    public void setVillagerFace(int face) {
        this.entityData.set(VILLAGER_FACE, face);
    }

    @Override
    public VillagerData getVillagerData() {
        return this.entityData.get(DATA_VILLAGER_DATA);
    }

    @Override
    public void setVillagerData(VillagerData p_150027_) {
        this.entityData.set(DATA_VILLAGER_DATA, p_150027_);
    }

    public boolean isTrader(){
        return this.entityData.get(IS_TRADER);
    }

    public void setIsTrader(boolean isTrader){
        this.entityData.set(IS_TRADER, isTrader);
    }

    @Override
    public void tryKill(Player player) {
    }

    class AlwaysWatchTargetGoal extends Goal {
        public AlwaysWatchTargetGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
        }

        public boolean canUse() {
            return VillagerVictim.this.getTrueOwner() != null;
        }

        public boolean canContinueToUse() {
            return VillagerVictim.this.getTrueOwner() != null;
        }

        public void tick() {
            VillagerVictim.this.getNavigation().stop();
            if (VillagerVictim.this.getTrueOwner() != null) {
                VillagerVictim.this.getLookControl().setLookAt(VillagerVictim.this.getTrueOwner(), 100.0F, 100.0F);
            }
        }
    }
}
