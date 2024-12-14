package com.Polarice3.goety_spillage.common.items;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.entities.ally.factory.GSChagrin;
import com.Polarice3.goety_spillage.common.entities.ally.factory.GSFactory;
import com.Polarice3.goety_spillage.common.entities.ally.factory.GSHinder;
import com.Polarice3.goety_spillage.common.entities.ally.factory.IEngineerMachine;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EngineerMalletItem extends Item implements Vanishable {
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public EngineerMalletItem() {
        super(new Properties().durability(64).rarity(Rarity.UNCOMMON));
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 4.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.9D, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    public boolean hurtEnemy(ItemStack p_43278_, LivingEntity p_43279_, LivingEntity p_43280_) {
        p_43278_.hurtAndBreak(2, p_43280_, (p_43296_) -> {
            p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    @Nonnull
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack container = itemStack.copy();
        if (container.getDamageValue() <= container.getMaxDamage()) {
            container.setDamageValue(itemStack.getDamageValue() + 5);
        } else {
            container = ItemStack.EMPTY;
        }

        return container;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.isShiftKeyDown()) {
            if (!SEHelper.getFocusCoolDown(player).isOnCooldown(this)) {
                if (entity instanceof Owned owned) {
                    if (owned instanceof IEngineerMachine && owned.getMasterOwner() == player) {
                        player.playSound(IllageAndSpillageSoundEvents.ENTITY_ENGINEER_REPAIR.get(), 2.0F, player.getVoicePitch());
                        owned.heal(5.0F);
                        ItemHelper.hurtAndBreak(stack, 1, player);
                        SEHelper.addCooldown(player, this, 100);
                    }
                }
            }
        } else if (SpellConfig.OwnerHitKill.get()) {
            if (entity instanceof IServant servant) {
                if (servant instanceof IEngineerMachine && servant.getMasterOwner() == player) {
                    servant.tryKill(player);
                }
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (!SpellConfig.OwnerHitKill.get()){
            if (entity instanceof IServant servant) {
                if (servant instanceof IEngineerMachine && servant.getMasterOwner() == player) {
                    servant.tryKill(player);
                }
            }
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    public List<LivingEntity> getMachines(Level level, Player player) {
        List<LivingEntity> list = new ArrayList<>();
        if (level instanceof ServerLevel serverLevel){
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof LivingEntity livingEntity && entity instanceof IEngineerMachine servant && entity instanceof IOwned owned){
                    if (owned.getMasterOwner() == player && livingEntity.isAlive()){
                        list.add(livingEntity);
                    }
                }
            }
        }
        return list;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level instanceof ServerLevel serverLevel) {
            boolean flag = false;
            if (!SEHelper.getFocusCoolDown(player).isOnCooldown(this) && this.getMachines(level, player).size() < 3) {
                player.playSound(SoundEvents.WITCH_THROW, 1.0F, player.getVoicePitch());
                ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.WITCH_THROW, 1.0F, player.getVoicePitch()));
                int randomSelection = level.getRandom().nextInt(0, 3);
                if (randomSelection == 0) {
                    GSHinder hinder = GSEntityTypes.HINDER.get().create(level);
                    if (hinder != null) {
                        hinder.setPos(player.getX(), player.getY() + 1.0, player.getZ());
                        hinder.setDeltaMovement((double) (-2 + level.getRandom().nextInt(5)) * 0.4, 0.6, (double) (-2 + level.getRandom().nextInt(5)) * 0.4);
                        hinder.setInMotion(true);
                        hinder.setTrueOwner(player);
                        hinder.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(player.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                        if (level.addFreshEntity(hinder)){
                            flag = true;
                        }
                    }
                } else if (randomSelection == 1) {
                    GSChagrin sentry = GSEntityTypes.CHAGRIN.get().create(level);
                    if (sentry != null) {
                        sentry.setPos(player.getX(), player.getY() + 1.0, player.getZ());
                        sentry.setDeltaMovement((double) (-2 + level.getRandom().nextInt(5)) * 0.4, 0.6, (double) (-2 + level.getRandom().nextInt(5)) * 0.4);
                        sentry.setInMotion(true);
                        sentry.setTrueOwner(player);
                        sentry.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(player.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                        if (level.addFreshEntity(sentry)){
                            flag = true;
                        }
                    }
                } else {
                    GSFactory factory = GSEntityTypes.FACTORY.get().create(level);
                    if (factory != null) {
                        factory.setPos(player.getX(), player.getY() + 1.0, player.getZ());
                        factory.setDeltaMovement((double) (-2 + level.getRandom().nextInt(5)) * 0.4, 0.6, (double) (-2 + level.getRandom().nextInt(5)) * 0.4);
                        factory.setInMotion(true);
                        factory.setTrueOwner(player);
                        factory.setAnimationState(1);
                        factory.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(player.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                        if (level.addFreshEntity(factory)){
                            flag = true;
                        }
                    }
                }
            }
            if (flag) {
                player.swing(hand);
                ItemHelper.hurtAndBreak(player.getItemInHand(hand), 5, player);
                SEHelper.addCooldown(player, this, level.getRandom().nextInt(300, 501));
                return InteractionResultHolder.consume(player.getItemInHand(hand));
            }
        }
        return super.use(level, player, hand);
    }

    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.is(Tags.Items.INGOTS_IRON) || super.isValidRepairItem(pToRepair, pRepair);
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getAttributeModifiers(slot, stack);
    }
}
