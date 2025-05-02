package com.Polarice3.goety_spillage.common.magic.spells;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.WandUtil;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.entities.projectiles.GSImp;
import com.Polarice3.goety_spillage.config.GSSpellConfig;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ImpSpell extends Spell {

    public int defaultSoulCost() {
        return GSSpellConfig.ImpishCost.get();
    }

    public int defaultCastDuration() {
        return GSSpellConfig.ImpishDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return IllageAndSpillageSoundEvents.ENTITY_SPIRITCALLER_IMPRISE.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return GSSpellConfig.ImpishCoolDown.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        HitResult hitResult = this.rayTrace(worldIn, caster, spellStat.getRange(), spellStat.getRadius());
        int random = worldIn.random.nextInt(3) + 1;
        if (random == 1) {
            this.createLineImps(caster, hitResult.getLocation());
        }

        if (random == 2) {
            this.createRandomImps(caster, hitResult.getLocation());
        }

        if (random == 3) {
            this.createRingImps(caster, hitResult.getLocation());
        }

        this.playSound(worldIn, caster, IllageAndSpillageSoundEvents.ENTITY_SPIRITCALLER_CLAP.get(), 2.0F, 1.0F);
    }

    protected void createLineImps(LivingEntity livingEntity, Vec3 vec3) {
        LivingEntity target = this.getTarget(livingEntity);
        if (target != null) {
            vec3 = target.position();
        }
        double d0 = Math.min(vec3.y(), livingEntity.getY() - 5.0);
        double d1 = Math.max(vec3.y(), livingEntity.getY() - 5.0) + 1.0;
        float f = (float) Mth.atan2(vec3.z() - livingEntity.getZ(), vec3.x() - livingEntity.getX());

        for(int l = 0; l < 16; ++l) {
            double d2 = 1.25 * (double)(l + 1);
            this.createSpellEntity(livingEntity, livingEntity.getX() + (double)Mth.cos(f) * d2, livingEntity.getZ() + (double)Mth.sin(f) * d2, d0, d1, f, l, l);
        }
    }

    protected void createRandomImps(LivingEntity livingEntity, Vec3 vec3) {
        LivingEntity target = this.getTarget(livingEntity);
        if (target != null) {
            vec3 = target.position();
        }
        double d0 = Math.min(vec3.y(), livingEntity.getY() - 5.0);
        double d1 = Math.max(vec3.y(), livingEntity.getY() - 5.0) + 1.0;
        float f = (float)Mth.atan2(vec3.z() - livingEntity.getZ(), vec3.x() - livingEntity.getX());

        for(int l = 0; l < 30; ++l) {
            this.createSpellEntity(livingEntity, livingEntity.getX() + (double)(-15 + livingEntity.getRandom().nextInt(30)), livingEntity.getZ() + (double)(-15 + livingEntity.getRandom().nextInt(30)), d0, d1, f, l, l);
        }

    }

    protected void createRingImps(LivingEntity livingEntity, Vec3 vec3) {
        LivingEntity target = this.getTarget(livingEntity);
        if (target != null) {
            vec3 = target.position();
        }
        double d0 = Math.min(vec3.y(), livingEntity.getY() - 5.0);
        double d1 = Math.max(vec3.y(), livingEntity.getY() - 5.0) + 1.0;
        float f = (float)Mth.atan2(vec3.z() - livingEntity.getZ(), vec3.x() - livingEntity.getX());
        int j = 1;
        this.createSpellEntity(livingEntity, vec3.x() - 3.0, vec3.z() - 0.0, d0, d1, f, j, 0);
        this.createSpellEntity(livingEntity, vec3.x() - 2.0, vec3.z() - -1.0, d0, d1, f, j, 1);
        this.createSpellEntity(livingEntity, vec3.x() - 1.0, vec3.z() - -2.0, d0, d1, f, j, 2);
        this.createSpellEntity(livingEntity, vec3.x() - 0.0, vec3.z() - -3.0, d0, d1, f, j, 3);
        this.createSpellEntity(livingEntity, vec3.x() - -1.0, vec3.z() - -2.0, d0, d1, f, j, 4);
        this.createSpellEntity(livingEntity, vec3.x() - -2.0, vec3.z() - -1.0, d0, d1, f, j, 5);
        this.createSpellEntity(livingEntity, vec3.x() - -3.0, vec3.z() - 0.0, d0, d1, f, j, 6);
        this.createSpellEntity(livingEntity, vec3.x() - -2.0, vec3.z() - 1.0, d0, d1, f, j, 7);
        this.createSpellEntity(livingEntity, vec3.x() - -1.0, vec3.z() - 2.0, d0, d1, f, j, 8);
        this.createSpellEntity(livingEntity, vec3.x() - 0.0, vec3.z() - 3.0, d0, d1, f, j, 9);
        this.createSpellEntity(livingEntity, vec3.x() - 1.0, vec3.z() - 2.0, d0, d1, f, j, 10);
        this.createSpellEntity(livingEntity, vec3.x() - 2.0, vec3.z() - 1.0, d0, d1, f, j, 11);
        this.createSpellEntity(livingEntity, vec3.x() - 0.0, vec3.z() - 0.0, d0, d1, f, j, 12);

    }

    private void createSpellEntity(LivingEntity livingEntity, double p_190876_1_, double p_190876_3_, double p_190876_5_, double p_190876_7_, float p_190876_9_, int p_190876_10_, int time) {
        BlockPos blockpos = BlockPos.containing(p_190876_1_, p_190876_7_, p_190876_3_);
        boolean flag = false;
        double d0 = 0.0;

        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = livingEntity.level.getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(livingEntity.level, blockpos1, Direction.UP)) {
                if (!livingEntity.level.isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = livingEntity.level.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(livingEntity.level, blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }

                flag = true;
                break;
            }

            blockpos = blockpos.below();
        } while(blockpos.getY() >= Mth.floor(p_190876_5_) - 1);

        if (flag) {
            int potency = 0;
            if (WandUtil.enchantedFocus(livingEntity)){
                potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), livingEntity);
            }

            GSImp imp = GSEntityTypes.IMP.get().create(livingEntity.level);
            if (imp != null) {
                imp.setPos((double) blockpos.getX() + 0.5, (double) blockpos.getY() + d0, (double) blockpos.getZ() + 0.5);
                imp.setTrueOwner(livingEntity);
                imp.setPower(potency);
                imp.setTarget(this.getTarget(livingEntity));
                imp.setWaitTime(time);
                imp.setInvisible(true);
                if (livingEntity.getTeam() != null) {
                    livingEntity.level.getScoreboard().addPlayerToTeam(imp.getStringUUID(), livingEntity.level.getScoreboard().getPlayerTeam(livingEntity.getTeam().getName()));
                }

                livingEntity.level.addFreshEntity(imp);
            }
        }

    }
}
