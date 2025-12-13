package com.Polarice3.goety_spillage.common.magic.spells;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.entities.ally.GSSpiritHand;
import com.Polarice3.goety_spillage.config.GSSpellConfig;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpiritHandSpell extends Spell {
    @Override
    public int defaultSoulCost() {
        return GSSpellConfig.SpiritHandCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return GSSpellConfig.SpiritHandDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return null;
    }

    @Override
    public SoundEvent loopSound(LivingEntity entityLiving) {
        return IllageAndSpillageSoundEvents.ENTITY_SPIRITCALLER_SPIRITHANDS.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return GSSpellConfig.SpiritHandCoolDown.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int soulPower = 1;
        if (caster instanceof Player player){
            float rawPercent = (float) SEHelper.getSoulAmountInt(player) / MainConfig.MaxArcaSouls.get();
            int sePercent = (int) (rawPercent * 10);
            soulPower = Math.min(8, sePercent);
        }

        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }

        if (isShifting(caster)) {
            for (Entity entity : worldIn.getAllEntities()) {
                if (entity instanceof GSSpiritHand spiritHand) {
                    if (spiritHand.getTrueOwner() == caster) {
                        spiritHand.moveTo(caster.position());
                    }
                }
            }
        } else {
            GSSpiritHand hand1 = GSEntityTypes.SPIRIT_HAND.get().create(worldIn);
            if (hand1 != null) {
                hand1.setPos(caster.getX(), caster.getY() + 1.0, caster.getZ());
                hand1.setGoodOrEvil(true);
                hand1.setDeltaMovement((-0.5 + worldIn.random.nextDouble()) / 2.0, (-0.5 + worldIn.random.nextDouble()) / 2.0, (-0.5 + worldIn.random.nextDouble()) / 2.0);
                hand1.setTrueOwner(caster);
                hand1.setTarget(this.getTarget(caster));
                hand1.setPower(potency);
                hand1.setLimitedLife(600 + MathHelper.secondsToTicks(duration * 5));
                worldIn.addFreshEntity(hand1);
            }
            GSSpiritHand hand2 = GSEntityTypes.SPIRIT_HAND.get().create(worldIn);

            if (hand2 != null) {
                hand2.setPos(caster.getX(), caster.getY() + 1.0, caster.getZ());
                hand2.setGoodOrEvil(false);
                hand2.setDeltaMovement((-0.5 + worldIn.random.nextDouble()) / 2.0, (-0.5 + worldIn.random.nextDouble()) / 2.0, (-0.5 + worldIn.random.nextDouble()) / 2.0);
                hand2.setTrueOwner(caster);
                hand2.setTarget(this.getTarget(caster));
                hand2.setLimitedLife(600 + MathHelper.secondsToTicks(duration * 5));
                hand2.setPower(potency);
                worldIn.addFreshEntity(hand2);
            }
        }

        this.playSound(worldIn, caster, IllageAndSpillageSoundEvents.ENTITY_SPIRITCALLER_CLAP.get(), 2.0F, 1.0F);
    }
}
