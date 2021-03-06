package net.minecraft.world.level.block;

import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockWeepingVinesPlant extends BlockGrowingStem {

    public static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public BlockWeepingVinesPlant(BlockBase.Info blockbase_info) {
        super(blockbase_info, EnumDirection.DOWN, BlockWeepingVinesPlant.SHAPE, false);
    }

    @Override
    protected BlockGrowingTop getHeadBlock() {
        return (BlockGrowingTop) Blocks.WEEPING_VINES;
    }
}
