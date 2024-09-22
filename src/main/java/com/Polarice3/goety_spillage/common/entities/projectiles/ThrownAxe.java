package com.Polarice3.goety_spillage.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.MobUtil;
import com.yellowbrossproductions.illageandspillage.entities.IllagerAttack;
import com.yellowbrossproductions.illageandspillage.packet.PacketHandler;
import com.yellowbrossproductions.illageandspillage.packet.ParticlePacket;
import com.yellowbrossproductions.illageandspillage.util.EntityUtil;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class ThrownAxe extends MobProjectile implements IllagerAttack, ItemSupplier {

    public ThrownAxe(EntityType<? extends MobProjectile> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
        this.damage = 8.0F;
    }

    private void onHit() {
        if (!this.level.isClientSide()) {
            if (this.level instanceof ServerLevel serverLevel) {
                ParticleOptions particleOptions = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.IRON_AXE));

                for (int i = 0; i < 10; ++i) {
                    double d0 = serverLevel.random.nextGaussian() * 0.02;
                    double d1 = serverLevel.random.nextGaussian() * 0.02;
                    double d2 = serverLevel.random.nextGaussian() * 0.02;
                    double d3 = this.position().x + (2.0 * this.random.nextDouble() - 1.0);
                    double d4 = this.position().y + this.random.nextDouble();
                    double d5 = this.position().z + (2.0 * this.random.nextDouble() - 1.0);
                    serverLevel.sendParticles(particleOptions, d3, d4, d5, 0, d0, d1, d2, 0.5);
                }
            }
            this.playSound(SoundEvents.ITEM_BREAK);
            this.discard();
        }
    }

    public void tick() {
        LivingEntity attacker = this.shooter != null ? this.shooter : this;
        List<Entity> list = this.level.getEntities(this, new AABB(this.getX() - 0.4, this.getY() - 0.4, this.getZ() - 0.4, this.getX() + 0.4, this.getY() + 0.4, this.getZ() + 0.4), Entity::isAlive);
        for (Entity entity : list) {
            if (entity instanceof LivingEntity living) {
                if (!MobUtil.areAllies(living, attacker) && entity.isAlive() && !entity.isInvulnerable() && !entity.isSpectator()) {
                    DamageSource damageSource = DamageSource.thrown(this, attacker);
                    living.hurt(damageSource, 8.0F);
                    living.invulnerableTime = 0;
                    EntityUtil.disableShield(living, 200);
                    if (!this.level.isClientSide) {
                        this.onHit();
                    }
                }
            }
        }
        HitResult result = ProjectileUtil.getHitResult(this, this::canHitEntity);
        if (result.getType() == HitResult.Type.BLOCK) {
            this.onHit();
        }

        super.tick();
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.shooter != null){
            if (pEntity == this.shooter){
                return false;
            }
            if (this.shooter instanceof Mob mob && mob.getTarget() == pEntity){
                return super.canHitEntity(pEntity);
            } else {
                if (MobUtil.areAllies(this.shooter, pEntity)){
                    return false;
                }
                if (pEntity instanceof IOwned owned0 && this.shooter instanceof IOwned owned1){
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
        }
        return super.canHitEntity(pEntity);
    }

    @Override
    public void setParticles(ServerLevel serverLevel) {
        double d0 = -0.5D + this.random.nextGaussian();
        double d1 = -0.5D + this.random.nextGaussian();
        double d2 = -0.5D + this.random.nextGaussian();
        serverLevel.sendParticles(ParticleTypes.CRIT, this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), 0, d0, d1, d2, 0.5F);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Items.IRON_AXE);
    }
}
