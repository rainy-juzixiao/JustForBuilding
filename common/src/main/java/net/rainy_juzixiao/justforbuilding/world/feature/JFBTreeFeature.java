package net.rainy_juzixiao.justforbuilding.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.rainy_juzixiao.justforbuilding.build.executor.TreeExecutor;

import java.util.Random;

public class JFBTreeFeature extends Feature<TreeFeatureConfig> {
    public JFBTreeFeature(Codec<TreeFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel world, ChunkGenerator generator, Random rand, BlockPos pos, TreeFeatureConfig config) {
        TreeExecutor executor = new TreeExecutor(config.type);
        executor.execute(world, pos, config.logState, config.leafState, rand);
        return true;
    }
}