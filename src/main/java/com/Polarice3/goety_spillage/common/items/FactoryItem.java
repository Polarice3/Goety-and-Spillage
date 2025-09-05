package com.Polarice3.goety_spillage.common.items;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.entities.ally.factory.GSFactory;
import com.Polarice3.goety_spillage.common.entities.ally.factory.IEngineerMachine;
import com.Polarice3.goety_spillage.config.GSSpellConfig;
import com.Polarice3.goety_spillage.util.GSMobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FactoryItem extends Item {
    public int type;

    public FactoryItem(int type) {
        super(new Properties());
        this.type = Mth.clamp(type, 0, 2);
    }

    public InteractionResult useOn(UseOnContext p_40510_) {
        Direction direction = p_40510_.getClickedFace();
        Level level = p_40510_.getLevel();
        Player player = p_40510_.getPlayer();
        if (direction == Direction.DOWN) {
            return InteractionResult.FAIL;
        } else if (player != null && GSMobUtil.getMachines(level, player).size() >= GSSpellConfig.EngineerMachineLimit.get()){
            player.displayClientMessage(Component.translatable("info.goety.summon.limit"), true);
            return InteractionResult.FAIL;
        } else {
            BlockPlaceContext blockplacecontext = new BlockPlaceContext(p_40510_);
            BlockPos blockpos = blockplacecontext.getClickedPos();
            ItemStack itemstack = p_40510_.getItemInHand();
            Vec3 vec3 = Vec3.atBottomCenterOf(blockpos);
            EntityType<?> entityType = GSEntityTypes.CHAGRIN.get();
            if (this.type == 1){
                entityType = GSEntityTypes.HINDER.get();
            } else if (this.type == 2){
                entityType = GSEntityTypes.FACTORY.get();
            }
            AABB aabb = entityType.getDimensions().makeBoundingBox(vec3.x(), vec3.y(), vec3.z());
            if (level.noCollision(null, aabb) && level.getEntities(null, aabb).isEmpty()) {
                if (level instanceof ServerLevel serverlevel) {
                    Entity entity = entityType.create(serverlevel);
                    if (entity instanceof Owned owned) {
                        float f = (float) Mth.floor((Mth.wrapDegrees(p_40510_.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                        owned.moveTo(vec3.x(), vec3.y(), vec3.z(), f, 0.0F);
                        owned.setTrueOwner(player);
                        if (owned instanceof IEngineerMachine machine){
                            machine.setInMotion(true);
                            if (machine instanceof GSFactory factory){
                                factory.setAnimationState(1);
                            }
                        }
                        owned.finalizeSpawn(serverlevel, serverlevel.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                        if (itemstack.hasCustomHoverName()) {
                            owned.setCustomName(itemstack.getHoverName());
                        }
                        serverlevel.addFreshEntityWithPassengers(owned);
                        owned.gameEvent(GameEvent.ENTITY_PLACE, p_40510_.getPlayer());
                    } else {
                        return InteractionResult.FAIL;
                    }
                }

                itemstack.shrink(1);
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.FAIL;
            }
        }
    }
}
