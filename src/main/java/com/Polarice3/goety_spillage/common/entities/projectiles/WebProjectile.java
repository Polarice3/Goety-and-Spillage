package com.Polarice3.goety_spillage.common.entities.projectiles;

import com.Polarice3.Goety.utils.MobUtil;
import com.yellowbrossproductions.illageandspillage.entities.IllagerAttack;
import com.yellowbrossproductions.illageandspillage.util.EffectRegisterer;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class WebProjectile extends MobProjectile implements IllagerAttack, ItemSupplier {
    public WebProjectile(EntityType<? extends MobProjectile> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
        this.damage = 4.0F;
    }

    public void tick() {
        LivingEntity attacker = this.shooter != null ? this.shooter : this;

        List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, new AABB(this.getX() - 0.4, this.getY() - 0.4, this.getZ() - 0.4, this.getX() + 0.4, this.getY() + 0.4, this.getZ() + 0.4), Entity::isAlive);

        for (LivingEntity target : list) {
            if (!MobUtil.areAllies(target, attacker)
                    && target.isAlive()
                    && !target.isInvulnerable()
                    && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)
                    && !target.hasEffect(EffectRegisterer.WEBBED.get())) {
                this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_WEB_HIT.get(), 1.0F, this.getVoicePitch());
                this.emitParticles();
                target.hurt(this.damageSources().thrown(this, attacker), this.getDamage());
                if (!this.level.isClientSide) {
                    target.addEffect(new MobEffectInstance(EffectRegisterer.WEBBED.get(), 200, 0, false, false, true));
                }
            }
        }

        super.tick();
    }

    private ParticleOptions getParticle() {
        ItemStack a = Items.COBWEB.getDefaultInstance();
        return new ItemParticleOption(ParticleTypes.ITEM, a);
    }

    public void emitParticles() {
        if (this.level instanceof ServerLevel serverLevel){
            for(int i = 0; i < 25; ++i) {
                double d0 = (-0.5 + this.random.nextGaussian()) / 4.0;
                double d1 = (-0.5 + this.random.nextGaussian()) / 4.0;
                double d2 = (-0.5 + this.random.nextGaussian()) / 4.0;
                ParticleOptions $$1 = this.getParticle();
                serverLevel.sendParticles($$1, this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), 0, d0, d1, d2, 0.5F);
            }
        }
    }

    public ItemStack getItem() {
        return Items.COBWEB.getDefaultInstance();
    }
}
