package com.Polarice3.goety_spillage.common.items.curios;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.items.curios.SingleStackItem;
import com.Polarice3.Goety.init.ModKeybindings;
import com.Polarice3.goety_spillage.common.capabilities.spillage.SpillageCapHelper;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.entities.ally.GSEyesore;
import com.Polarice3.goety_spillage.common.entities.ally.GSTot;
import com.Polarice3.goety_spillage.common.entities.ally.undead.GSFunnybone;
import com.Polarice3.goety_spillage.common.entities.projectiles.*;
import com.Polarice3.goety_spillage.config.GSAttributesConfig;
import com.Polarice3.goety_spillage.config.GSItemConfig;
import com.yellowbrossproductions.illageandspillage.util.EffectRegisterer;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import com.yellowbrossproductions.illageandspillage.util.ItemRegisterer;
import com.yellowbrossproductions.illageandspillage.util.PotionRegisterer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nullable;
import java.util.List;

public class FreakyRobeItem extends SingleStackItem {
    private static final String POTION_DISTANCE = "Potion Distance";
    private static final String ATTACK_TYPE = "Attack Type";
    private static final String ATTACK_TICK = "Attack Tick";
    private static final String ATTACK_COOLDOWN = "Attack Cooldown";
    private static final String BOMB_COOLDOWN = "Bomb Cooldown";
    private static final String POTION_COOLDOWN = "Potion Cooldown";
    private static final String SCYTHE_COOLDOWN = "Scythe Cooldown";
    private static final String TOT_COOLDOWN = "ToT Cooldown";
    private static final String MINION_COOLDOWN = "Minion Cooldown";
    private static final String AXE_COOLDOWN = "Axe Cooldown";
    private static final int BOMBS_ATTACK = 1;
    private static final int POTIONS_ATTACK = 2;
    private static final int SCYTHE_ATTACK = 3;
    private static final int TOT_ATTACK = 4;
    private static final int MINION_ATTACK = 5;
    private static final int AXE_ATTACK = 6;

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof LivingEntity livingEntity) {
            CompoundTag compound = stack.getOrCreateTag();
            if (stack.getTag() == null) {
                compound.putDouble(POTION_DISTANCE, 0.0D);
                compound.putInt(ATTACK_TYPE, 0);
                compound.putInt(ATTACK_TICK, 0);
                compound.putInt(ATTACK_COOLDOWN, 0);
                compound.putInt(BOMB_COOLDOWN, 0);
                compound.putInt(POTION_COOLDOWN, 0);
                compound.putInt(SCYTHE_COOLDOWN, 0);
                compound.putInt(TOT_COOLDOWN, 0);
                compound.putInt(MINION_COOLDOWN, 0);
                compound.putInt(AXE_COOLDOWN, 0);
            } else {
                if (getAttackType(stack) > 0){
                    increaseAttackTick(stack);
                }
                if (getAttackCooldown(stack) > 0) {
                    decreaseAttackCooldown(stack);
                }
                if (getAttackType(stack) < 1){
                    if (getAttackTypeCooldown(stack, BOMB_COOLDOWN) > 0){
                        decreaseAttackTypeCooldown(stack, BOMB_COOLDOWN);
                    }
                    if (getAttackTypeCooldown(stack, POTION_COOLDOWN) > 0){
                        decreaseAttackTypeCooldown(stack, POTION_COOLDOWN);
                    }
                    if (getAttackTypeCooldown(stack, SCYTHE_COOLDOWN) > 0){
                        decreaseAttackTypeCooldown(stack, SCYTHE_COOLDOWN);
                    }
                    if (getAttackTypeCooldown(stack, TOT_COOLDOWN) > 0){
                        decreaseAttackTypeCooldown(stack, TOT_COOLDOWN);
                    }
                    if (getAttackTypeCooldown(stack, MINION_COOLDOWN) > 0 && hasFewEnoughMinions(livingEntity)){
                        decreaseAttackTypeCooldown(stack, MINION_COOLDOWN);
                    }
                    if (getAttackTypeCooldown(stack, AXE_COOLDOWN) > 0){
                        decreaseAttackTypeCooldown(stack, AXE_COOLDOWN);
                    }
                }
                stopAttacking(stack);
                summonHorrors(livingEntity, stack);
                if (!worldIn.isClientSide) {
                    if (!GSItemConfig.FreakyPotionSpin.get()){
                        SpillageCapHelper.setCasting(livingEntity, getAttackType(stack) > 0 && (getAttackType(stack) != POTIONS_ATTACK));
                    } else {
                        SpillageCapHelper.setCasting(livingEntity, getAttackType(stack) > 0);
                    }
                }
            }
            if (!worldIn.isClientSide){
                if (livingEntity.hasEffect(EffectRegisterer.MUTATION.get())){
                    livingEntity.removeEffect(EffectRegisterer.MUTATION.get());
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public static int getAttackType(ItemStack robe){
        CompoundTag compound = robe.getTag();
        if (compound != null){
            return compound.getInt(ATTACK_TYPE);
        }
        return 0;
    }

    public static void setAttackType(ItemStack robe, int type){
        CompoundTag compound = robe.getTag();
        if (compound != null){
            compound.putInt(ATTACK_TYPE, type);
        }
    }

    public static int getAttackTick(ItemStack robe){
        CompoundTag compound = robe.getTag();
        if (compound != null){
            return compound.getInt(ATTACK_TICK);
        }
        return 0;
    }

    public static int getAttackCooldown(ItemStack robe){
        CompoundTag compound = robe.getTag();
        if (compound != null){
            return compound.getInt(ATTACK_COOLDOWN);
        }
        return 0;
    }

    public static void decreaseAttackCooldown(ItemStack robe){
        CompoundTag compound = robe.getTag();
        if (compound != null){
            compound.putInt(ATTACK_COOLDOWN, getAttackCooldown(robe) - 1);
        }
    }

    public static void setAttackCooldown(ItemStack robe, int cool){
        CompoundTag compound = robe.getTag();
        if (compound != null){
            compound.putInt(ATTACK_COOLDOWN, cool);
        }
    }

    public static int getAttackTypeCooldown(ItemStack robe, String attackType){
        CompoundTag compound = robe.getTag();
        if (compound != null){
            return compound.getInt(attackType);
        }
        return 0;
    }

    public static void decreaseAttackTypeCooldown(ItemStack robe, String attackType){
        CompoundTag compound = robe.getTag();
        if (compound != null){
            compound.putInt(attackType, getAttackTypeCooldown(robe, attackType) - 1);
        }
    }

    public static void setAttackTypeCooldown(ItemStack robe, String attackType, int cool){
        CompoundTag compound = robe.getTag();
        if (compound != null){
            compound.putInt(attackType, cool);
        }
    }

    public static void increaseAttackTick(ItemStack robe){
        CompoundTag compound = robe.getTag();
        if (compound != null){
            compound.putInt(ATTACK_TICK, getAttackTick(robe) + 1);
        }
    }

    public static void setAttackTick(ItemStack robe, int tick){
        CompoundTag compound = robe.getTag();
        if (compound != null){
            compound.putInt(ATTACK_TICK, tick);
        }
    }

    public static double getPotionDistance(ItemStack robe){
        CompoundTag compound = robe.getTag();
        if (compound != null){
            return compound.getDouble(POTION_DISTANCE);
        }
        return 0.0D;
    }

    public static void setPotionDistance(ItemStack robe, double distance){
        CompoundTag compound = robe.getTag();
        if (compound != null){
            compound.putDouble(POTION_DISTANCE, distance);
        }
    }

    public static boolean canAttack(ItemStack robe){
        return getAttackType(robe) == 0 && getAttackCooldown(robe) < 1;
    }

    public static void startAttack(LivingEntity wearer, ItemStack robe){
        if (canAttack(robe)) {
            for (int i = 0; i < 16; ++i) {
                if (getAttackTypeCooldown(robe, AXE_COOLDOWN) < 1
                        && wearer.getHealth() < (wearer.getMaxHealth() / 2.0D)
                        && wearer.getMainHandItem().is(Items.IRON_AXE)
                        && wearer.getOffhandItem().is(Items.IRON_AXE)
                        && wearer.getRandom().nextInt(16) == 0) {
                    setAttackType(robe, AXE_ATTACK);
                } else if (getAttackTypeCooldown(robe, TOT_COOLDOWN) < 1 && wearer.getHealth() < wearer.getMaxHealth() && wearer.getRandom().nextInt(16) == 0) {
                    setAttackType(robe, TOT_ATTACK);
                } else if (getAttackTypeCooldown(robe, SCYTHE_COOLDOWN) < 1 && getTarget(wearer) != null && wearer.getRandom().nextInt(16) == 0) {
                    setAttackType(robe, SCYTHE_ATTACK);
                } else if (getAttackTypeCooldown(robe, MINION_COOLDOWN) < 1 && hasFewEnoughMinions(wearer) && wearer.getRandom().nextInt(16) == 0) {
                    setAttackType(robe, MINION_ATTACK);
                } else if (getAttackTypeCooldown(robe, POTION_COOLDOWN) < 1 && wearer.getRandom().nextInt(16) == 0) {
                    setAttackType(robe, POTIONS_ATTACK);
                } else if (getAttackTypeCooldown(robe, BOMB_COOLDOWN) < 1 && wearer.getRandom().nextInt(16) == 0) {
                    setAttackType(robe, BOMBS_ATTACK);
                }
                if (getAttackType(robe) != 0){
                    break;
                }
            }
        }
    }

    public static void stopAttacking(ItemStack robe){
        if (getAttackType(robe) == TOT_ATTACK) {
            if (getAttackTick(robe) > 30){
                setAttackTick(robe, 0);
                setAttackType(robe, 0);
                setAttackTypeCooldown(robe, TOT_COOLDOWN, 900);
                setAttackCooldown(robe, 100);
            }
        }
        if (getAttackType(robe) == SCYTHE_ATTACK) {
            if (getAttackTick(robe) > 24){
                setAttackTick(robe, 0);
                setAttackType(robe, 0);
                setAttackTypeCooldown(robe, SCYTHE_COOLDOWN, 200);
                setAttackCooldown(robe, 100);
            }
        }
        if (getAttackType(robe) == POTIONS_ATTACK) {
            if (getAttackTick(robe) > 60){
                setAttackTick(robe, 0);
                setAttackType(robe, 0);
                setAttackTypeCooldown(robe, POTION_COOLDOWN, 200);
                setAttackCooldown(robe, 100);
            }
        }
        if (getAttackType(robe) == BOMBS_ATTACK) {
            if (getAttackTick(robe) > 30){
                setAttackTick(robe, 0);
                setAttackType(robe, 0);
                setAttackTypeCooldown(robe, BOMB_COOLDOWN, 200);
                setAttackCooldown(robe, 100);
            }
        }
        if (getAttackType(robe) == MINION_ATTACK) {
            if (getAttackTick(robe) > 50){
                setAttackTick(robe, 0);
                setAttackType(robe, 0);
                setAttackTypeCooldown(robe, MINION_COOLDOWN, 400);
                setAttackCooldown(robe, 100);
            }
        }
        if (getAttackType(robe) == AXE_ATTACK) {
            if (getAttackTick(robe) > 115){
                setAttackTick(robe, 0);
                setAttackType(robe, 0);
                setAttackTypeCooldown(robe, AXE_COOLDOWN, 200);
                setAttackCooldown(robe, 100);
            }
        }
    }

    public static void summonHorrors(LivingEntity wearer, ItemStack robe){
        if (wearer.isAlive()){
            if (getAttackType(robe) == BOMBS_ATTACK && getAttackTick(robe) == 20) {
                summonBombs(wearer);
            }
            if (getAttackType(robe) == POTIONS_ATTACK){
                if (GSItemConfig.FreakyPotionSpin.get()){
                    wearer.setYRot(wearer.getYRot() + 96.0F);
                    wearer.setYHeadRot(wearer.getYRot());
                    wearer.yRotO = wearer.getYRot();
                } else {
                    if (!wearer.level.isClientSide) {
                        SpillageCapHelper.setSpinning(wearer, true);
                    }
                }
                if (getAttackTick(robe) == 10) {
                    wearer.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_SPIN.get(), 2.0F, 1.0F);
                    setPotionDistance(robe, 0.0D);
                }

                if (getAttackTick(robe) >= 10 && getAttackTick(robe) <= 50) {
                    makePotionParticles(wearer);
                }

                if (getAttackTick(robe) >= 20 && getAttackTick(robe) <= 50) {
                    setPotionDistance(robe, getPotionDistance(robe) + 0.02D);
                    throwPotions(wearer, robe);
                }
            }
            if (getAttackType(robe) == SCYTHE_ATTACK){
                if (getTarget(wearer) != null) {
                    if (getAttackTick(robe) == 14) {
                        wearer.playSound(SoundEvents.SNOWBALL_THROW, 2.0F, wearer.getVoicePitch());
                    }

                    if (getAttackTick(robe) == 16) {
                        shootScythe(wearer);
                    }
                }
            }

            if (getAttackType(robe) == TOT_ATTACK && getAttackTick(robe) == 21) {
                wearer.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_CYMBAL.get(), 2.0F, 1.0F);
                int amount = 7;

                for(int i = 0; i < amount; ++i) {
                    GSTot treat = GSEntityTypes.TRICK_OR_TREAT.get().create(wearer.level);
                    if (treat != null) {
                        treat.circleTime = i * 20;
                        treat.bounceTime = i;
                        treat.setPos(wearer.getX(), wearer.getY(), wearer.getZ());
                        treat.setTrueOwner(wearer);
                        treat.setTreat(wearer.getRandom().nextInt(5) + 1);
                        if (wearer.getTeam() != null) {
                            PlayerTeam playerTeam = wearer.level.getScoreboard().getPlayerTeam(wearer.getTeam().getName());
                            if (playerTeam != null) {
                                wearer.level.getScoreboard().addPlayerToTeam(treat.getStringUUID(), playerTeam);
                            }
                        }
                        treat.distract = true;

                        circleTreat(treat, i, amount);
                        wearer.level.addFreshEntity(treat);
                    }
                }
            }

            if (getAttackType(robe) == MINION_ATTACK){
                if (getAttackTick(robe) == 40){
                    summonMinions(wearer);
                }
            }

            if (getAttackType(robe) == AXE_ATTACK){
                if ((getAttackTick(robe) - 40) % 7 == 0) {
                    wearer.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_VILLAGERWAVE.get(), 3.0F, wearer.getVoicePitch());
                }
                if (getAttackTick(robe) >= 40 && getAttackTick(robe) < 96){
                    shootAxe(wearer, robe);
                    if (!wearer.level.isClientSide) {
                        SpillageCapHelper.setSpinning(wearer, true);
                    }
                }
            }
        }
    }

    public static boolean hasFewEnoughMinions(LivingEntity wearer) {
        List<LivingEntity> list = wearer.level.getEntitiesOfClass(LivingEntity.class, wearer.getBoundingBox().inflate(100.0), (predicate) -> {
            return predicate.isAlive() && predicate instanceof IServant servant && (servant instanceof GSEyesore || servant instanceof GSFunnybone) && servant.getTrueOwner() == wearer;
        });
        return list.size() < 3;
    }

    public static void circleTreat(Entity entity, int number, int amount) {
        float TAU = 6.2831855F;
        float velocity = 0.5F;
        float yaw = (float)number * (TAU / (float)amount);
        float vy = 0.3F;
        float vx = velocity * Mth.cos(yaw);
        float vz = velocity * Mth.sin(yaw);
        entity.setDeltaMovement(vx, vy, vz);
    }

    public static void summonBombs(LivingEntity wearer){
        double throwSpeed = 0.7D;
        wearer.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_CYMBAL.get(), 1.0F, 1.0F);

        for(int i = 0; i < 4; ++i) {
            if (wearer.getHealth() < wearer.getMaxHealth() / 2.0F) {
                GSSkullBomb bomb = GSEntityTypes.SKULL_BOMB.get().create(wearer.level);
                if (bomb != null) {
                    bomb.setPos(wearer.getX(), wearer.getY() + 0.25D, wearer.getZ());
                    bomb.setTrueOwner(wearer);
                    if (i == 0) {
                        bomb.setDeltaMovement(-throwSpeed, 0.5D, -throwSpeed);
                    } else if (i == 1) {
                        bomb.setDeltaMovement(-throwSpeed, 0.5D, throwSpeed);
                    } else if (i == 2) {
                        bomb.setDeltaMovement(throwSpeed, 0.5D, -throwSpeed);
                    } else {
                        bomb.setDeltaMovement(throwSpeed, 0.53D, throwSpeed);
                    }

                    wearer.level.addFreshEntity(bomb);
                }
            } else {
                GSPumpkinBomb bomb = GSEntityTypes.PUMPKIN_BOMB.get().create(wearer.level);
                if (bomb != null) {
                    bomb.setPos(wearer.getX(), wearer.getY() + 0.25D, wearer.getZ());
                    bomb.setTrueOwner(wearer);
                    bomb.setTarget(getTarget(wearer));
                    if (i == 0) {
                        bomb.setDeltaMovement(-throwSpeed, 0.3D, -throwSpeed);
                    } else if (i == 1) {
                        bomb.setDeltaMovement(-throwSpeed, 0.3D, throwSpeed);
                    } else if (i == 2) {
                        bomb.setDeltaMovement(throwSpeed, 0.3D, -throwSpeed);
                    } else {
                        bomb.setDeltaMovement(throwSpeed, 0.3D, throwSpeed);
                    }

                    if (wearer.getTeam() != null) {
                        wearer.level.getScoreboard().addPlayerToTeam(bomb.getStringUUID(), wearer.level.getScoreboard().getPlayerTeam(wearer.getTeam().getName()));
                    }

                    wearer.level.addFreshEntity(bomb);
                }
            }
        }
    }

    public static void makePotionParticles(LivingEntity wearer) {
        if (wearer.level instanceof ServerLevel serverLevel) {
            for(int i = 0; i < 1; ++i) {
                double d0 = (-0.5D + wearer.getRandom().nextGaussian()) / 4.0D;
                double d1 = (-0.5D + wearer.getRandom().nextGaussian()) / 4.0D;
                double d2 = (-0.5D + wearer.getRandom().nextGaussian()) / 4.0D;
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, wearer.getRandomX(1.0) + (-0.5 + wearer.getRandom().nextDouble()) * 0.8, wearer.getRandomY() - (-0.5 + wearer.getRandom().nextDouble()) * 0.4, wearer.getRandomZ(1.0) + (-0.5 + wearer.getRandom().nextDouble()) * 0.8, 0, d0, d1, d2, 0.5F);
            }
        }
    }

    public static void throwPotions(LivingEntity wearer, ItemStack robe){
        for(int i = 0; i < 2; ++i) {
            if (!wearer.level.isClientSide) {
                DarkPotion potion = GSEntityTypes.DARK_POTION.get().create(wearer.level);
                if (potion != null) {
                    potion.setPos(wearer.getX(), wearer.getY() + 2.0D, wearer.getZ());
                    potion.setOwner(wearer);
                    int lingerChance = wearer.getHealth() <= wearer.getMaxHealth() / 2.0F ? wearer.getRandom().nextInt(0, 15) : 1;
                    potion.setItem(PotionUtils.setPotion(new ItemStack(lingerChance == 0 ? ItemRegisterer.DARK_LINGER.get() : ItemRegisterer.DARK_SPLASH.get()), PotionRegisterer.MUTATION.get()));
                    potion.setXRot(-20.0F);
                    potion.setDeltaMovement((-2.0D + wearer.getRandom().nextDouble() + wearer.getRandom().nextDouble() + wearer.getRandom().nextDouble() + wearer.getRandom().nextDouble()) * (getPotionDistance(robe) / 4.0D), 1.0, (-2.0D + wearer.getRandom().nextDouble() + wearer.getRandom().nextDouble() + wearer.getRandom().nextDouble() + wearer.getRandom().nextDouble()) * (getPotionDistance(robe) / 4.0D));
                    wearer.level.addFreshEntity(potion);
                }
            }
        }
    }

    public static void summonMinions(LivingEntity wearer){
        wearer.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_CYMBAL.get(), 1.0F, 1.0F);

        for(int i = 0; i < 5; ++i) {
            if (wearer.getHealth() <= wearer.getMaxHealth() / 2.0F) {
                GSEyesore entity = GSEntityTypes.EYESORE.get().create(wearer.level);
                if (entity != null) {
                    entity.setPos(wearer.getX(), wearer.getY() + 0.25, wearer.getZ());
                    entity.setTrueOwner(wearer);
                    entity.setTarget(getTarget(wearer));
                    entity.setFlying(true);
                    entity.setDeltaMovement((double) (-2 + wearer.getRandom().nextInt(5)) * 0.4, 0.6, (double) (-2 + wearer.getRandom().nextInt(5)) * 0.4);

                    if (wearer.getTeam() != null) {
                        wearer.level.getScoreboard().addPlayerToTeam(entity.getStringUUID(), wearer.level.getScoreboard().getPlayerTeam(wearer.getTeam().getName()));
                    }

                    wearer.level.addFreshEntity(entity);
                }
            } else {
                GSFunnybone entity = GSEntityTypes.FUNNYBONE.get().create(wearer.level);
                if (entity != null) {
                    entity.setPos(wearer.getX(), wearer.getY() + 0.25, wearer.getZ());
                    entity.setTrueOwner(wearer);
                    entity.setTarget(getTarget(wearer));
                    entity.setFlying(true);
                    entity.setDeltaMovement((double) (-2 + wearer.getRandom().nextInt(5)) * 0.4, 0.6, (double) (-2 + wearer.getRandom().nextInt(5)) * 0.4);

                    if (wearer.getTeam() != null) {
                        wearer.level.getScoreboard().addPlayerToTeam(entity.getStringUUID(), wearer.level.getScoreboard().getPlayerTeam(wearer.getTeam().getName()));
                    }

                    wearer.level.addFreshEntity(entity);
                }
            }
        }
    }

    public static void shootScythe(LivingEntity wearer){
        FreakyScythe scythe = GSEntityTypes.FREAKY_SCYTHE.get().create(wearer.level);
        LivingEntity target = getTarget(wearer);
        if (scythe != null && target != null) {
            scythe.setPos(wearer.getX(), wearer.getY() + 1.5D, wearer.getZ());
            double x = scythe.getX() - target.getX();
            double y = scythe.getY() - target.getY();
            double z = scythe.getZ() - target.getZ();
            double d = Math.sqrt(x * x + y * y + z * z);
            float power = 3.0F;
            double motionX = -(x / d * (double) power * 0.2D);
            double motionY = -(y / d * (double) power * 0.2D);
            double motionZ = -(z / d * (double) power * 0.2D);
            scythe.setAcceleration(motionX, motionY, motionZ);
            scythe.halfHP = wearer.getHealth() < wearer.getMaxHealth() / 2.0F;
            scythe.setGoFor(target);
            scythe.setShooter(wearer);
            scythe.setDamage(GSAttributesConfig.BoundFreakagerScytheDamage.get().floatValue());
            wearer.level.addFreshEntity(scythe);
        }
    }

    public static void shootAxe(LivingEntity wearer, ItemStack robe){
        LivingEntity target = getTarget(wearer);
        float f = wearer.yBodyRot * 0.017453292F * 0.25F;
        float f1 = Mth.cos(f);
        float f2 = Mth.sin(f);
        Vec3 vec3;
        if (getAttackTick(robe) % 2 == 0) {
            vec3 = new Vec3(wearer.getX() + (double)f1 * 0.6, wearer.getY() + 0.7, wearer.getZ() + (double)f2 * 0.6);
        } else {
            vec3 = new Vec3(wearer.getX() - (double)f1 * 0.6, wearer.getY() + 0.7, wearer.getZ() - (double)f2 * 0.6);
        }

        Vec3 vec31 = wearer.getLookAngle();
        double x = -vec31.x;
        double y = -vec31.y;
        double z = -vec31.z;
        if (target != null){
            x = vec3.x - target.getX();
            y = vec3.y - (target.getY() + (double)(target.getEyeHeight() / 2.0F));
            z = vec3.z - target.getZ();
        }
        ThrownAxe projectile = new ThrownAxe(wearer.level, wearer, -x, -y, -z);
        projectile.moveTo(vec3);
        projectile.setRot(wearer);
        projectile.shoot(-x, -y, -z, 1.0F, 20.0F);
        projectile.setOwner(wearer);
        projectile.setDamage(GSAttributesConfig.BoundFreakagerAxeDamage.get().floatValue());
        wearer.level.addFreshEntity(projectile);
    }

    @Nullable
    public static LivingEntity getTarget(LivingEntity caster){
        return getTarget(caster, 16);
    }

    @Nullable
    public static LivingEntity getTarget(LivingEntity caster, int range){
        if (caster instanceof Mob mob){
            return mob.getTarget();
        } else {
            HitResult hitResult = rayTrace(caster.level, caster, range, 3);
            if (hitResult instanceof EntityHitResult entityHitResult){
                if (entityHitResult.getEntity() instanceof PartEntity<?> partEntity &&
                        partEntity.getParent() instanceof LivingEntity living){
                    return living;
                } else if (entityHitResult.getEntity() instanceof LivingEntity living){
                    return living;
                }
            }
            return null;
        }
    }

    public static HitResult rayTrace(Level worldIn, LivingEntity livingEntity, int range, double radius) {
        if (entityResult(worldIn, livingEntity, range, radius) == null){
            return blockResult(worldIn, livingEntity, range);
        } else {
            return entityResult(worldIn, livingEntity, range, radius);
        }
    }

    public static BlockHitResult blockResult(Level worldIn, LivingEntity livingEntity, double range) {
        float f = livingEntity.getXRot();
        float f1 = livingEntity.getYRot();
        Vec3 vector3d = livingEntity.getEyePosition(1.0F);
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3 vector3d1 = vector3d.add((double)f6 * range, (double)f5 * range, (double)f7 * range);
        return worldIn.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, livingEntity));
    }

    public static EntityHitResult entityResult(Level worldIn, LivingEntity livingEntity, int range, double radius){
        Vec3 srcVec = livingEntity.getEyePosition(1.0F);
        Vec3 lookVec = livingEntity.getViewVector(1.0F);
        Vec3 destVec = srcVec.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        AABB axisalignedbb = livingEntity.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(radius, radius, radius);
        return ProjectileUtil.getEntityHitResult(worldIn, livingEntity, srcVec, destVec, axisalignedbb, entity -> entity instanceof LivingEntity && !entity.isSpectator() && entity.isPickable());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        ChatFormatting main = ChatFormatting.DARK_PURPLE;
        ChatFormatting secondary = ChatFormatting.BLUE;

        if (stack.getItem() instanceof FreakyRobeItem) {
            tooltip.add(Component.translatable("info.goety_spillage.freaky_robe_power", ModKeybindings.keyBindings[3].getTranslatedKeyMessage().getString()).withStyle(main));
            tooltip.add(Component.translatable("info.goety_spillage.freaky_robe_axe").withStyle(secondary));
        }
    }
}
