package com.Polarice3.goety_spillage.common.magic.spells;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.entities.projectiles.GSSoulBeam;
import com.Polarice3.goety_spillage.config.GSSpellConfig;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SoulBeamSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return GSSpellConfig.SoulBeamCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return GSSpellConfig.SoulBeamDuration.get();
    }

    @Override
    public int castDuration(LivingEntity entityLiving) {
        return GSSpellConfig.SoulBeamDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return null;
    }

    @Override
    public SoundEvent loopSound(LivingEntity entityLiving) {
        return IllageAndSpillageSoundEvents.ENTITY_SPIRITCALLER_CHARGELASER.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return GSSpellConfig.SoulBeamCoolDown.get();
    }

    public ColorUtil particleColors(LivingEntity entityLiving) {
        return new ColorUtil(0.1F, 0.1F, 0.2F);
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel serverLevel, LivingEntity livingEntity, ItemStack itemStack) {
        int potency = 0;
        int duration = 0;
        if (WandUtil.enchantedFocus(livingEntity)){
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), livingEntity);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), livingEntity);
        }

        double degToRad = Math.PI / 180.0D;
        GSSoulBeam beam = new GSSoulBeam(GSEntityTypes.SOUL_BEAM.get(),
                serverLevel,
                livingEntity,
                livingEntity.getX() + 0.8D * Math.sin((double)(-livingEntity.getYRot()) * degToRad),
                livingEntity.getY() + 1.0D,
                livingEntity.getZ() + 0.8D * Math.cos((double)(-livingEntity.getYRot()) * degToRad),
                (float)((double)(livingEntity.yHeadRot + 90.0F) * degToRad),
                (float)((double)(-livingEntity.getXRot()) * degToRad),
                40 + MathHelper.secondsToTicks(duration),
                potency);
        float radius = 1.1F;
        double x = livingEntity.getX() + 0.8D * Math.sin((double) (-livingEntity.getYRot()) * degToRad) + (double) radius * Math.sin((double) (-livingEntity.yHeadRot) * degToRad) * Math.cos((double) (-livingEntity.getXRot()) * degToRad);
        double y = livingEntity.getY() + 1.0D + (double) radius * Math.sin((double) (-livingEntity.getXRot()) * degToRad);
        double z = livingEntity.getZ() + 0.8D * Math.cos((double) (-livingEntity.getYRot()) * degToRad) + (double) radius * Math.cos((double) (-livingEntity.yHeadRot) * degToRad) * Math.cos((double) (-livingEntity.getXRot()) * degToRad);
        beam.setPos(x, y, z);
        serverLevel.addFreshEntity(beam);
        serverLevel.playSound((Player)null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), IllageAndSpillageSoundEvents.ENTITY_SPIRITCALLER_LASER.get(), this.getSoundSource(), 2.0F, 1.0F);
    }
}
