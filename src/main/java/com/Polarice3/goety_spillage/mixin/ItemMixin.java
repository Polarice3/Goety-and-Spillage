package com.Polarice3.goety_spillage.mixin;

import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.entities.projectiles.ThrownAxe;
import com.Polarice3.goety_spillage.common.items.curios.FreakyHatItem;
import com.Polarice3.goety_spillage.common.items.curios.FreakyRobeItem;
import com.Polarice3.goety_spillage.config.GSAttributesConfig;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "getUseDuration(Lnet/minecraft/world/item/ItemStack;)I", at = @At(value = "HEAD"), cancellable = true)
    public void getUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (stack.is(Items.IRON_AXE)){
            cir.setReturnValue(10);
        }
    }

    @Inject(method = "getUseAnimation(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/UseAnim;", at = @At(value = "HEAD"), cancellable = true)
    public void getUseAnimation(ItemStack stack, CallbackInfoReturnable<UseAnim> cir) {
        if (stack.is(Items.IRON_AXE)){
            cir.setReturnValue(UseAnim.SPEAR);
        }
    }

    @Inject(method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;", at = @At(value = "HEAD"), cancellable = true)
    public void use(Level worldIn, Player playerIn, InteractionHand handIn, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (CuriosFinder.hasCurio(playerIn, item -> item.getItem() instanceof FreakyRobeItem)) {
            if (itemstack.is(Items.IRON_AXE)) {
                playerIn.startUsingItem(handIn);
                cir.setReturnValue(InteractionResultHolder.consume(itemstack));
            }
        }
    }

    @Inject(method = "finishUsingItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "HEAD"), cancellable = true)
    public void finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving, CallbackInfoReturnable<ItemStack> cir) {
        if (!worldIn.isClientSide) {
            if (CuriosFinder.hasCurio(entityLiving, item -> item.getItem() instanceof FreakyRobeItem)) {
                if (stack.is(Items.IRON_AXE)) {
                    Vec3 vector3d = entityLiving.getViewVector(1.0F);
                    float power = 3.5F;
                    Vec3 vec3 = vector3d.multiply(power, power, power);
                    ThrownAxe projectile = new ThrownAxe(entityLiving.getX() + vector3d.x / 2, entityLiving.getY() + 1.0D, entityLiving.getZ() + vector3d.z / 2, vec3.x, vec3.y, vec3.z, worldIn);
                    projectile.setYHeadRot(entityLiving.getYHeadRot());
                    projectile.setYRot(entityLiving.getYHeadRot());
                    CompoundTag tag = entityLiving.getPersistentData().getCompound("Rotation");
                    projectile.readAdditionalSaveData(tag);
                    projectile.setOwner(entityLiving);
                    projectile.setDamage(GSAttributesConfig.BoundFreakagerAxeDamage.get().floatValue());
                    if (worldIn.addFreshEntity(projectile)) {
                        ModNetwork.sentToTrackingEntityAndPlayer(entityLiving, new SPlayWorldSoundPacket(entityLiving.blockPosition(), IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_SCYTHE_SPIN.get(), 2.0F, entityLiving.getVoicePitch()));
                        entityLiving.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_SCYTHE_SPIN.get(), 2.0F, entityLiving.getVoicePitch());
                        if (CuriosFinder.hasCurio(entityLiving, item -> item.getItem() instanceof FreakyHatItem)){
                            ItemHelper.hurtAndBreak(stack, 1, entityLiving);
                        } else {
                            stack.setCount(0);
                        }
                        if (entityLiving instanceof Player player){
                            player.getCooldowns().addCooldown(Items.IRON_AXE, 20);
                        }
                        cir.setReturnValue(stack);
                    }
                }
            }
        }
    }
}
