package com.Polarice3.goety_spillage.common.events;

import com.Polarice3.Goety.common.entities.hostile.cultists.Cultist;
import com.Polarice3.Goety.common.entities.neutral.AbstractHauntedArmor;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.*;
import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.common.capabilities.spillage.ISpillage;
import com.Polarice3.goety_spillage.common.capabilities.spillage.SpillageCapHelper;
import com.Polarice3.goety_spillage.common.capabilities.spillage.SpillageProvider;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.entities.IAttackMyOwner;
import com.Polarice3.goety_spillage.common.entities.ally.RagnoServant;
import com.Polarice3.goety_spillage.common.entities.neutral.VillagerVictim;
import com.Polarice3.goety_spillage.common.entities.projectiles.ThrownAxe;
import com.Polarice3.goety_spillage.common.items.MutationPotion;
import com.Polarice3.goety_spillage.common.items.curios.FreakyHatItem;
import com.Polarice3.goety_spillage.config.GSMobsConfig;
import com.Polarice3.goety_spillage.init.GSLootTables;
import com.yellowbrossproductions.illageandspillage.entities.*;
import com.yellowbrossproductions.illageandspillage.entities.projectile.AxeEntity;
import com.yellowbrossproductions.illageandspillage.util.ItemRegisterer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = GoetySpillage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GSEvents {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player player = event.getEntity();
        Player original = event.getOriginal();

        original.reviveCaps();

        ISpillage capability2 = SpillageCapHelper.getCapability(original);

        player.getCapability(SpillageProvider.CAPABILITY)
                .ifPresent(spillage ->
                        spillage.setCasting(false));
        player.getCapability(SpillageProvider.CAPABILITY)
                .ifPresent(spillage ->
                        spillage.setSpinning(false));
    }

    @SubscribeEvent
    public static void LivingEvent(LivingEvent.LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (!livingEntity.level.isClientSide) {
            if (livingEntity.tickCount % 2 == 0) {
                if (SpillageCapHelper.isCasting(livingEntity)) {
                    SpillageCapHelper.setCasting(livingEntity, false);
                }
                if (SpillageCapHelper.isSpinning(livingEntity)) {
                    SpillageCapHelper.setSpinning(livingEntity, false);
                }
            }
        }
        if (livingEntity instanceof Mob mob){
            if (mob.getLastHurtByMob() instanceof IAttackMyOwner victim){
                if (victim.getTrueOwner() != null && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(victim.getTrueOwner())){
                    mob.setLastHurtByMob(victim.getTrueOwner());
                } else {
                    mob.setLastHurtByMob(null);
                }
            }
            if (mob.getTarget() instanceof IAttackMyOwner victim && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(victim.getTrueOwner())){
                if (victim.getTrueOwner() != null){
                    mob.setTarget(victim.getTrueOwner());
                } else {
                    mob.setTarget(null);
                }
            }
            if (mob instanceof VillagerSoulEntity soul){
                if (soul.isCharging()) {
                    if (soul.getTarget() instanceof Player player) {
                        if (soul.distanceToSqr(player) <= 4.0F) {
                            SEHelper.increaseSouls(player, 25);
                        }
                    }
                }
            }
            if (mob instanceof Villager villager){
                if (!villager.level.isClientSide) {
                    Brain<?> brain = villager.getBrain();
                    Player player = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).orElse(null);
                    if (player != null) {
                        if (GSMobsConfig.VillagerHateRagno.get()) {
                            for (Owned owned : player.level.getEntitiesOfClass(Owned.class, player.getBoundingBox().inflate(16.0D))) {
                                if (owned instanceof RagnoServant) {
                                    if (owned.getTrueOwner() == player || owned.getMasterOwner() == player) {
                                        if (villager.getPlayerReputation(player) > -200) {
                                            villager.getGossips().add(player.getUUID(), GossipType.MAJOR_NEGATIVE, 25);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void AttackEvent(LivingAttackEvent event){
        LivingEntity victim = event.getEntity();
        Entity direct = event.getSource().getDirectEntity();
        if (direct instanceof ThrownAxe || direct instanceof AxeEntity){
            if (victim instanceof AbstractHauntedArmor armor){
                armor.disableShield(true);
            }
        }
        if (direct instanceof ImpEntity impEntity){
            if (impEntity.getOwner() != null && !(impEntity.getOwner() instanceof Raider)){
                if (MobUtil.areAllies(victim, impEntity.getOwner())){
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void HurtEvent(LivingHurtEvent event){
        LivingEntity victim = event.getEntity();
        if (CuriosFinder.hasCurio(victim, item -> (item.getItem() instanceof FreakyHatItem))){
            if (victim.getVehicle() instanceof RagnoServant || victim.getVehicle() instanceof RagnoEntity){
                event.setAmount(event.getAmount() / 2.0F);
            }
        }
    }

    @SubscribeEvent
    public static void DeathEvents(LivingDeathEvent event){
        SpillageCapHelper.setCasting(event.getEntity(), false);
    }

    @SubscribeEvent
    public static void DropEvents(LivingDropsEvent event){
        Entity killed = event.getEntity();
        if (killed instanceof Player player){
            if (player.getVehicle() instanceof RagnoServant ragnoServant){
                Optional<ItemEntity> bag = event.getDrops().stream()
                        .filter(Objects::nonNull)
                        .findFirst()
                        .filter(itemEntity -> itemEntity.getItem().is(ItemRegisterer.BAG_OF_HORRORS.get()));
                if (bag.isPresent()){
                    bag.get().setPos(player.getX(), player.getY(), player.getZ());
                    bag.get().setDeltaMovement(0.0D, 0.6D, 0.0D);
                    bag.get().setNeverPickUp();
                    bag.get().setUnlimitedLifetime();
                    bag.get().noPhysics = true;
                    ragnoServant.item = bag.get();
                    ragnoServant.goCrazy();
                }
            }
        }
        if (killed.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            if (killed instanceof RagnoEntity ragno) {
                if (ragno.level.getServer() != null) {
                    LootTable loottable = ragno.level.getServer().getLootData().getLootTable(GSLootTables.RAGNO_EXTRA);
                    LootParams.Builder lootcontext$builder = MobUtil.createLootContext(event.getSource(), ragno);
                    LootParams ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
                    loottable.getRandomItems(ctx).forEach((loot) -> event.getDrops().add(ItemHelper.itemEntityDrop(ragno, loot)));
                }
            }
            if (killed instanceof LivingEntity livingEntity) {
                if (killed instanceof FreakagerEntity || killed instanceof RagnoEntity || killed instanceof MagispellerEntity || killed instanceof SpiritcallerEntity) {
                    if (livingEntity.level.getServer() != null) {
                        LootTable loottable = livingEntity.level.getServer().getLootData().getLootTable(GSLootTables.ILLAGER_BOSS_EXTRA);
                        LootParams.Builder lootcontext$builder = MobUtil.createLootContext(event.getSource(), livingEntity);
                        LootParams ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
                        loottable.getRandomItems(ctx).forEach((loot) -> event.getDrops().add(ItemHelper.itemEntityDrop(livingEntity, loot)));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void OnInteractEvents(PlayerInteractEvent.EntityInteractSpecific event) {
        Player player = event.getEntity();
        if (!event.getLevel().isClientSide) {
            if (event.getTarget() instanceof AbstractVillager villager){
                if (event.getItemStack().getItem() instanceof MutationPotion){
                    if (MutationPotion.canSummon(player.level, player)) {
                        event.getItemStack().shrink(1);
                        event.setCanceled(true);
                        event.setCancellationResult(InteractionResult.SUCCESS);
                        event.getItemStack().shrink(1);
                        VillagerVictim victim = new VillagerVictim(GSEntityTypes.VILLAGER_VICTIM.get(), event.getLevel());
                        victim.copyPosition(villager);
                        victim.setYRot(villager.getYRot());
                        victim.setYHeadRot(villager.getYHeadRot());
                        victim.setXRot(villager.getXRot());
                        if (villager.hasCustomName()) {
                            victim.setCustomName(villager.getCustomName());
                            victim.setCustomNameVisible(villager.isCustomNameVisible());
                        }
                        victim.setHealth(villager.getHealth());
                        MobUtil.summonTame(victim, player);
                        if (event.getLevel() instanceof ServerLevel) {
                            if (villager instanceof Villager villager1) {
                                victim.setVillagerData(villager1.getVillagerData());
                            } else if (villager instanceof WanderingTrader){
                                victim.setIsTrader(true);
                            }
                        }
                        event.getLevel().addFreshEntity(victim);
                        victim.playSound(SoundEvents.WITCH_DRINK, 2.0F, 1.0F);
                        if (villager.isPassenger()) {
                            Entity entity = villager.getVehicle();
                            if (entity != null) {
                                villager.stopRiding();
                            }
                        }
                        villager.discard();
                    }
                }
            }
        }
    }
}
