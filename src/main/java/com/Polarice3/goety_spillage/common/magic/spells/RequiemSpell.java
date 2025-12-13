package com.Polarice3.goety_spillage.common.magic.spells;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.entities.projectiles.GSIllagerSoul;
import com.Polarice3.goety_spillage.common.items.GSItems;
import com.Polarice3.goety_spillage.config.GSSpellConfig;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RequiemSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setPotency(2);
    }

    @Override
    public int defaultSoulCost() {
        return GSSpellConfig.RequiemCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return GSSpellConfig.RequiemDuration.get();
    }

    public int castDuration(LivingEntity caster, ItemStack staff) {
        return GSSpellConfig.RequiemDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return IllageAndSpillageSoundEvents.ENTITY_SPIRITCALLER_SPIRITSWARM.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return GSSpellConfig.RequiemCoolDown.get();
    }

    public void stopSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, ItemStack focus, int castTime, SpellStat spellStat) {
        if (castTime > 10){
            if (caster instanceof Player player) {
                SEHelper.addCooldown(player, GSItems.REQUIEM_FOCUS.get(), this.spellCooldown());
                SEHelper.sendSEUpdatePacket(player);
            }
        }
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        return list;
    }

    @Override
    public void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int castTime, SpellStat spellStat) {
        int soulPower = spellStat.getPotency();

        if (WandUtil.enchantedFocus(caster)){
            soulPower += WandUtil.getPotencyLevel(caster) * 2;
        }

        soulPower = Math.min(soulPower, 8);

        if (castTime > 10){
            for(int i = 0; i < soulPower; ++i) {
                GSIllagerSoul soul = GSEntityTypes.ILLAGER_SOUL.get().create(worldIn);
                if (soul != null) {
                    soul.setPos(caster.getX() + (double) (-10 - soulPower + worldIn.random.nextInt(20 + soulPower * 2)), caster.getY() + (double) (-1 + worldIn.random.nextInt(10 + soulPower * 2)), caster.getZ() + (double) (-10 - soulPower + worldIn.random.nextInt(20 + soulPower * 2)));
                    soul.setTrueOwner(caster);
                    soul.setAngelOrDevil(worldIn.random.nextBoolean());
                    if (this.getTarget(caster) != null) {
                        soul.setTarget(this.getTarget(caster));
                    }
                    soul.setDeltaMovement(0.0, 0.1, 0.0);
                    worldIn.addFreshEntity(soul);
                }
            }
        }

    }
}
