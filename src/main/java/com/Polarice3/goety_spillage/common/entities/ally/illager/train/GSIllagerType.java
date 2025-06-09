package com.Polarice3.goety_spillage.common.entities.ally.illager.train;

import com.Polarice3.Goety.api.entities.ally.illager.ITrainIllager;
import com.Polarice3.Goety.common.entities.ally.illager.Neollager;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CraftingTableBlock;
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
        if (BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(Blocks.MAGMA_BLOCK), range, 32)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(Blocks.SNOW_BLOCK), range, 16)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock() instanceof CraftingTableBlock, range, 1)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(Tags.Blocks.STORAGE_BLOCKS_IRON), range, 2)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(Tags.Blocks.STORAGE_BLOCKS_COAL), range, 4)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(Tags.Blocks.STORAGE_BLOCKS_REDSTONE), range, 4)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(Blocks.COBBLESTONE), range, 64)) {
            return GSEntityTypes.IGNITER_SERVANT.get();
        } else if (BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(Blocks.HAY_BLOCK), range, 64)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock() instanceof BarrelBlock, range, 16)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock() instanceof PistonBaseBlock, range, 16)) {
            return GSEntityTypes.PRESERVER_SERVANT.get();
        }
        return null;
    }
}
