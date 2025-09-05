package com.Polarice3.goety_spillage.common.entities.ally.factory;

import net.minecraft.world.item.ItemStack;

public interface IEngineerMachine {

    default boolean isMalletBorn() {
        return false;
    }

    default void setInMotion(boolean motion) {
    }

    default ItemStack getFactoryItem() {
        return ItemStack.EMPTY;
    }
}
