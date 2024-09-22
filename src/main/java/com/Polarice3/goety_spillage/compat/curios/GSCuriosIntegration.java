package com.Polarice3.goety_spillage.compat.curios;

import com.Polarice3.Goety.compat.ICompatable;
import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.common.items.GSItems;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GSCuriosIntegration implements ICompatable {

    private static final Map<Item, String> TYPES = ImmutableMap.<Item, String>builder()
            .put(GSItems.FREAKY_HAT.get(), "head")
            .put(GSItems.FREAKY_ROBE.get(), "body")
            .build();

    public void setup(FMLCommonSetupEvent event) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::sendImc);
        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, this::onCapabilitiesAttach);
    }

    private void sendImc(InterModEnqueueEvent event) {
        TYPES.values().stream().distinct().forEach(t -> InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder(t).build()));
    }

    private void onCapabilitiesAttach(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (TYPES.containsKey(stack.getItem())) {
            if (stack.getItem() instanceof ICurioItem iCurioItem) {
                ItemizedCurioCapability capability = new ItemizedCurioCapability(iCurioItem, stack);
                event.addCapability(new ResourceLocation(GoetySpillage.MOD_ID, "curios"), CurioItemCapability.createProvider(capability));
            }
        }
    }

    public static class ItemizedCurioCapability implements ICurio {
        private final ItemStack stack;
        private final ICurioItem curioItem;

        public ItemizedCurioCapability(ICurioItem curio, ItemStack stack) {
            this.curioItem = curio;
            this.stack = stack;
        }

        @Override
        public ItemStack getStack() {
            return this.stack;
        }

        @Override
        public void curioTick(SlotContext slotContext) {
            this.curioItem.curioTick(slotContext, this.getStack());
        }

        @Override
        public boolean canEquip(SlotContext slotContext) {
            return this.curioItem.canEquip(slotContext, this.getStack());
        }

        @Override
        public boolean canUnequip(SlotContext slotContext) {
            return this.curioItem.canUnequip(slotContext, this.getStack());
        }

        @Override
        public List<Component> getSlotsTooltip(List<Component> tooltips) {
            return this.curioItem.getSlotsTooltip(tooltips, this.getStack());
        }

        @Override
        public void curioBreak(SlotContext slotContext) {
            this.curioItem.curioBreak(slotContext, this.getStack());
        }

        @Override
        public boolean canSync(SlotContext slotContext) {
            return this.curioItem.canSync(slotContext, this.getStack());
        }

        @Nonnull
        @Override
        public CompoundTag writeSyncData(SlotContext slotContext) {
            return this.curioItem.writeSyncData(slotContext, this.getStack());
        }

        @Override
        public void readSyncData(SlotContext slotContext, CompoundTag compound) {
            this.curioItem.readSyncData(slotContext, compound, this.getStack());
        }

        @Nonnull
        @Override
        public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel,
                                    boolean recentlyHit) {
            return this.curioItem
                    .getDropRule(slotContext, source, lootingLevel, recentlyHit, this.getStack());
        }

        @Override
        public List<Component> getAttributesTooltip(List<Component> tooltips) {
            return this.curioItem.getAttributesTooltip(tooltips, this.getStack());
        }

        @Override
        public int getFortuneLevel(SlotContext slotContext, LootContext lootContext) {
            return this.curioItem.getFortuneLevel(slotContext, lootContext, this.getStack());
        }

        @Override
        public int getLootingLevel(SlotContext slotContext, DamageSource source, LivingEntity target,
                                   int baseLooting) {
            return this.curioItem
                    .getLootingLevel(slotContext, source, target, baseLooting, this.getStack());
        }

        @Override
        public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                            UUID uuid) {
            return this.curioItem.getAttributeModifiers(slotContext, uuid, this.getStack());
        }

        @Override
        public void onEquipFromUse(SlotContext slotContext) {
            this.curioItem.onEquipFromUse(slotContext, this.getStack());
        }

        @Override
        public boolean canEquipFromUse(SlotContext slotContext) {
            return this.curioItem.canEquipFromUse(slotContext, this.getStack());
        }

        @Override
        public void onEquip(SlotContext slotContext, ItemStack prevStack) {
            this.curioItem.onEquip(slotContext, prevStack, this.getStack());
        }

        @Override
        public void onUnequip(SlotContext slotContext, ItemStack newStack) {
            this.curioItem.onUnequip(slotContext, newStack, this.getStack());
        }

        @Nonnull
        @Override
        public SoundInfo getEquipSound(SlotContext slotContext) {
            return this.curioItem.getEquipSound(slotContext, this.getStack());
        }

        @Override
        public boolean makesPiglinsNeutral(SlotContext slotContext) {
            return this.curioItem.makesPiglinsNeutral(slotContext, this.getStack());
        }

        @Override
        public boolean isEnderMask(SlotContext slotContext, EnderMan enderMan) {
            return this.curioItem.isEnderMask(slotContext, enderMan, this.getStack());
        }
    }

    public static class CurioItemCapability {

        public static ICapabilityProvider createProvider(final ICurio curio) {
            return new Provider(curio);
        }

        public static class Provider implements ICapabilityProvider {

            final LazyOptional<ICurio> capability;

            Provider(ICurio curio) {
                this.capability = LazyOptional.of(() -> curio);
            }

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                return CuriosCapability.ITEM.orEmpty(cap, this.capability);
            }
        }
    }

}
