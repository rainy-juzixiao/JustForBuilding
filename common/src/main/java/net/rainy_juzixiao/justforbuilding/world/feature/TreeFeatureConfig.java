package net.rainy_juzixiao.justforbuilding.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.rainy_juzixiao.justforbuilding.build.executor.TreeExecutor;

public class TreeFeatureConfig implements FeatureConfiguration {
    public static final Codec<TreeFeatureConfig> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("type").forGetter(c -> c.type.name()),
            BlockState.CODEC.fieldOf("log_state").forGetter(c -> c.logState),
            BlockState.CODEC.fieldOf("leaf_state").forGetter(c -> c.leafState)
        ).apply(instance, (typeStr, log, leaf) -> 
            new TreeFeatureConfig(TreeExecutor.TreeType.valueOf(typeStr), log, leaf)
        )
    );

    public final TreeExecutor.TreeType type;
    public final BlockState logState;
    public final BlockState leafState;

    public TreeFeatureConfig(TreeExecutor.TreeType type, BlockState logState, BlockState leafState) {
        this.type = type;
        this.logState = logState;
        this.leafState = leafState;
    }
}