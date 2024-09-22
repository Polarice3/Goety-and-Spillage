package com.Polarice3.goety_spillage.common.entities.projectiles;

import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.goety_spillage.config.GSSpellConfig;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GSSoulBeam extends Entity {
    private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(GSSoulBeam.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CASTER = SynchedEntityData.defineId(GSSoulBeam.class, EntityDataSerializers.INT);
    public LivingEntity caster;
    public double endPosX;
    public double endPosY;
    public double endPosZ;
    public double collidePosX;
    public double collidePosY;
    public double collidePosZ;
    public double prevCollidePosX;
    public double prevCollidePosY;
    public double prevCollidePosZ;
    public float renderYaw;
    public float renderPitch;
    public boolean on;
    public Direction blockSide;
    private int power;
    public float prevYaw;
    public float prevPitch;

    public GSSoulBeam(EntityType<? extends GSSoulBeam> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.on = true;
        this.blockSide = null;
        this.noCulling = true;
    }

    public GSSoulBeam(EntityType<? extends GSSoulBeam> type, Level world, LivingEntity caster, double x, double y, double z, float yaw, float pitch, int duration, int pow) {
        this(type, world);
        this.caster = caster;
        this.setYaw(yaw);
        this.setPitch(pitch);
        this.setDuration(duration);
        this.setPos(x, y, z);
        this.calculateEndPos();
        this.setPower(pow);
        if (!world.isClientSide) {
            this.setCasterID(caster.getId());
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DURATION, 0);
        this.entityData.define(CASTER, -1);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.contains("Power")) {
            this.power = pCompound.getInt("Power");
        }
        if (pCompound.contains("Duration")) {
            this.setDuration(pCompound.getInt("Duration"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("Power", this.power);
        pCompound.putInt("Duration", this.getDuration());
    }

    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    public void setYaw(float yaw) {
        this.renderYaw = yaw;
    }

    public void setPitch(float pitch) {
        this.renderPitch = pitch;
    }

    public int getDuration() {
        return this.getEntityData().get(DURATION);
    }

    public void setDuration(int duration) {
        this.getEntityData().set(DURATION, duration);
    }

    public int getCasterID() {
        return this.getEntityData().get(CASTER);
    }

    public void setCasterID(int id) {
        this.getEntityData().set(CASTER, id);
    }

    public void setPower(int power) {
        this.power = power;
    }

    private void calculateEndPos() {
        double radius = 30.0;
        this.endPosX = this.getX() + radius * Math.cos(this.renderYaw) * Math.cos(this.renderPitch);
        this.endPosZ = this.getZ() + radius * Math.sin(this.renderYaw) * Math.cos(this.renderPitch);
        this.endPosY = this.getY() + radius * Math.sin(this.renderPitch);
    }

    public SoulBeamHitResult raytraceEntities(Level world, Vec3 from, Vec3 to, boolean ignoreBlockWithoutBoundingBox) {
        SoulBeamHitResult result = new SoulBeamHitResult();
        result.setBlockHit(world.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)));
        if (result.blockHit != null) {
            Vec3 hitVec = result.blockHit.getLocation();
            this.collidePosX = hitVec.x;
            this.collidePosY = hitVec.y;
            this.collidePosZ = hitVec.z;
            this.blockSide = result.blockHit.getDirection();
        } else {
            this.collidePosX = this.endPosX;
            this.collidePosY = this.endPosY;
            this.collidePosZ = this.endPosZ;
            this.blockSide = null;
        }

        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, (new AABB(Math.min(this.getX(), this.collidePosX), Math.min(this.getY(), this.collidePosY), Math.min(this.getZ(), this.collidePosZ), Math.max(this.getX(), this.collidePosX), Math.max(this.getY(), this.collidePosY), Math.max(this.getZ(), this.collidePosZ))).inflate(1.0D));
        for (LivingEntity entity : entities) {
            if (entity != this.caster && !MobUtil.areAllies(this.caster, entity)) {
                float pad = entity.getPickRadius() + 0.5F;
                AABB aabb = entity.getBoundingBox().inflate(pad, pad, pad);
                Optional<Vec3> hit = aabb.clip(from, to);
                if (aabb.contains(from)) {
                    result.addEntityHit(entity);
                } else if (hit.isPresent()) {
                    result.addEntityHit(entity);
                }
            }
        }

        return result;
    }

    public void push(Entity entityIn) {
    }

    public boolean isPickable() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 1024.0;
    }

    public void tick() {
        super.tick();
        this.prevCollidePosX = this.collidePosX;
        this.prevCollidePosY = this.collidePosY;
        this.prevCollidePosZ = this.collidePosZ;
        this.prevYaw = this.renderYaw;
        this.prevPitch = this.renderPitch;
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        if (this.tickCount == 1 && this.level.isClientSide) {
            this.caster = (LivingEntity)this.level.getEntity(this.getCasterID());
        }

        if (this.caster != null) {
            this.caster.setDeltaMovement(0, this.caster.getDeltaMovement().y, 0);
            this.caster.xxa = 0.0F;
            this.caster.zza = 0.0F;
            double degToRad = Math.PI / 180.0D;
            float radius = 1.1F;
            double x = this.caster.getX() + 0.8D * Math.sin((double) (-this.caster.getYRot()) * degToRad) + (double) radius * Math.sin((double) (-this.caster.yHeadRot) * degToRad) * Math.cos((double) (-this.caster.getXRot()) * degToRad);
            double y = this.caster.getY() + 1.0D + (double) radius * Math.sin((double) (-this.caster.getXRot()) * degToRad);
            double z = this.caster.getZ() + 0.8D * Math.cos((double) (-this.caster.getYRot()) * degToRad) + (double) radius * Math.cos((double) (-this.caster.yHeadRot) * degToRad) * Math.cos((double) (-this.caster.getXRot()) * degToRad);
            this.setPos(x, y, z);
            this.renderYaw = (float)((double)(this.caster.yHeadRot + 90.0F) * degToRad);
            this.renderPitch = (float)((double)(-this.caster.getXRot()) * degToRad);
        }

        if (!this.on) {
            this.discard();
        }

        if (this.caster != null && !this.caster.isAlive()) {
            this.discard();
        }

        this.calculateEndPos();
        List<LivingEntity> hit = this.raytraceEntities(this.level, new Vec3(this.getX(), this.getY(), this.getZ()), new Vec3(this.endPosX, this.endPosY, this.endPosZ), true).getEntities();
        if (!this.level.isClientSide) {
            for (LivingEntity target : hit) {
                target.hurt(DamageSource.indirectMagic(this, this.caster), GSSpellConfig.SoulBeamDamage.get().floatValue() + (float) this.power);
                target.hurtMarked = true;
                target.setDeltaMovement(0.0D, 0.0D, 0.0D);
                target.lerpMotion(0.0D, 0.0D, 0.0D);
            }
        }

        if (this.tickCount > this.getDuration()) {
            this.on = false;
        }

    }

    public static class SoulBeamHitResult {
        private BlockHitResult blockHit;
        private final List<LivingEntity> entities = new ArrayList<>();

        public SoulBeamHitResult() {
        }

        public BlockHitResult getBlockHit() {
            return this.blockHit;
        }

        public void setBlockHit(HitResult rayTraceResult) {
            if (rayTraceResult.getType() == HitResult.Type.BLOCK) {
                this.blockHit = (BlockHitResult)rayTraceResult;
            }

        }

        public void addEntityHit(LivingEntity entity) {
            this.entities.add(entity);
        }

        public List<LivingEntity> getEntities() {
            return this.entities;
        }
    }
}
