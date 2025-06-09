package com.Polarice3.goety_spillage.common.items;

import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.goety_spillage.common.entities.ally.illager.RagnoServant;
import com.Polarice3.goety_spillage.config.GSSpellConfig;
import com.yellowbrossproductions.illageandspillage.util.EffectRegisterer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class MutationPotion extends Item {
    public MutationPotion() {
        super(new Item.Properties()
                .rarity(Rarity.EPIC)
                .craftRemainder(Items.GLASS_BOTTLE)
                .setNoRepair()
                .stacksTo(1)
        );
    }

    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        super.finishUsingItem(pStack, pLevel, pEntityLiving);

        if (!pLevel.isClientSide) {
            pEntityLiving.addEffect(new MobEffectInstance(EffectRegisterer.MUTATION.get(), MathHelper.minutesToTicks(10), 4));
        }

        return pStack.isEmpty() ? new ItemStack(Items.GLASS_BOTTLE) : pStack;
    }

    public static boolean canSummon(Level level, Player castingPlayer){
        if (level instanceof ServerLevel serverLevel){
            int count = 0;
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof RagnoServant servant) {
                    if (servant.getTrueOwner() == castingPlayer) {
                        ++count;
                    }
                }
            }
            if (count >= GSSpellConfig.RagnoLimit.get()){
                castingPlayer.displayClientMessage(Component.translatable("info.goety.summon.limit"), true);
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public int getUseDuration(ItemStack pStack) {
        return 40;
    }

    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }

    public SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_DRINK;
    }
}
