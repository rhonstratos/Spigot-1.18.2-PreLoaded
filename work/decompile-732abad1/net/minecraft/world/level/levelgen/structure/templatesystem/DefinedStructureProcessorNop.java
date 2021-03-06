package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.IWorldReader;

public class DefinedStructureProcessorNop extends DefinedStructureProcessor {

    public static final Codec<DefinedStructureProcessorNop> CODEC = Codec.unit(() -> {
        return DefinedStructureProcessorNop.INSTANCE;
    });
    public static final DefinedStructureProcessorNop INSTANCE = new DefinedStructureProcessorNop();

    private DefinedStructureProcessorNop() {}

    @Nullable
    @Override
    public DefinedStructure.BlockInfo processBlock(IWorldReader iworldreader, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1, DefinedStructureInfo definedstructureinfo) {
        return definedstructure_blockinfo1;
    }

    @Override
    protected DefinedStructureStructureProcessorType<?> getType() {
        return DefinedStructureStructureProcessorType.NOP;
    }
}
