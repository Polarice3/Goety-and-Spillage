package com.Polarice3.goety_spillage.common.entities.ally.illager.train;

import com.Polarice3.Goety.api.entities.ally.illager.ITrainIllager;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ally.illager.Neollager;
import com.Polarice3.Goety.common.ritual.RitualChecker;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraftforge.common.Tags;

public class GSIllagerType implements ITrainIllager {

    @Override
    public boolean canSpawn(Level level, BlockPos blockPos, int range) {
        return this.getIllager(level, blockPos, range) != null;
    }

    @Override
    public boolean mobCanTrainTo(Mob mob, Level level, BlockPos blockPos, int range) {
        return mob instanceof Neollager;
    }

    @Override
    public EntityType<? extends Mob> getIllager(Level level, BlockPos blockPos, int range) {
        RitualChecker checker = new RitualChecker(level, blockPos, (blockState) -> true, range, 0);
        if (checker.hasBlocks(blockState -> blockState.is(Blocks.MAGMA_BLOCK), 32)
                && checker.hasBlocks(blockState -> blockState.is(Blocks.SNOW_BLOCK), 16)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof CraftingTableBlock, 1)
                && checker.hasBlocks(blockState -> blockState.is(Tags.Blocks.STORAGE_BLOCKS_IRON), 2)
                && checker.hasBlocks(blockState -> blockState.is(Tags.Blocks.STORAGE_BLOCKS_COAL), 4)
                && checker.hasBlocks(blockState -> blockState.is(Tags.Blocks.STORAGE_BLOCKS_REDSTONE), 4)
                && checker.hasBlocks(blockState -> blockState.is(Blocks.COBBLESTONE), 64)) {
            return GSEntityTypes.IGNITER_SERVANT.get();
        }/* else if (BlockFinder.getNearbyEnchantPower(level, blockPos, range, 32)
                && checker.hasBlocks(blockState -> blockState.is(Tags.Blocks.STORAGE_BLOCKS_REDSTONE), 16)
                && checker.hasBlocks(blockState -> blockState.is(ModBlocks.STASH_URN.get()), 4)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof DispenserBlock, 4)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof BrewingStandBlock, 4)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof ComparatorBlock, 4)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof CraftingTableBlock, 1)) {
            return GSEntityTypes.ENGINEER_SERVANT.get();
        }*/ else if (checker.hasBlocks(blockState -> blockState.is(Blocks.HAY_BLOCK), 64)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof BarrelBlock, 16)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof PistonBaseBlock, 16)) {
            return GSEntityTypes.PRESERVER_SERVANT.get();
        }
        return null;
    }
}
