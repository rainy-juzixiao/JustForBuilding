package net.rainy_juzixiao.justforbuilding.world;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.rainy_juzixiao.justforbuilding.world.feature.JFBTreeFeature;
import net.rainy_juzixiao.justforbuilding.world.feature.TreeFeatureConfig;
import net.rainy_juzixiao.justforbuilding.build.executor.TreeExecutor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ModFeatures {
    public static Feature<TreeFeatureConfig> JFB_TREE;
    public static ConfiguredFeature<TreeFeatureConfig, ?> RAINFOREST_TREE, BANYAN_TREE, FORKED_TREE, DWARF_TREE, MEDIUM_TREE, PINE_TREE;

    public static void registerFeatures() {
        JFB_TREE = Registry.register(Registry.FEATURE, new ResourceLocation("justforbuilding", "jfb_tree"), new JFBTreeFeature(TreeFeatureConfig.CODEC));

        BlockState log = Blocks.OAK_LOG.defaultBlockState();
        BlockState leaf = Blocks.OAK_LEAVES.defaultBlockState();

        RAINFOREST_TREE = register("rainforest_tree", JFB_TREE, new TreeFeatureConfig(TreeExecutor.TreeType.RAINFOREST, log, leaf));
        BANYAN_TREE = register("banyan_tree", JFB_TREE, new TreeFeatureConfig(TreeExecutor.TreeType.BANYAN, log, leaf));
        FORKED_TREE = register("forked_tree", JFB_TREE, new TreeFeatureConfig(TreeExecutor.TreeType.FORKED, log, leaf));
        DWARF_TREE = register("dwarf_tree", JFB_TREE, new TreeFeatureConfig(TreeExecutor.TreeType.DWARF, log, leaf));
        MEDIUM_TREE = register("medium_tree", JFB_TREE, new TreeFeatureConfig(TreeExecutor.TreeType.MEDIUM, log, leaf));
        PINE_TREE = register("pine_tree", JFB_TREE, new TreeFeatureConfig(TreeExecutor.TreeType.PINE, log, leaf));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> ConfiguredFeature<FC, ?> register(String name, F feature, FC config) {
        return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation("justforbuilding", name), new ConfiguredFeature<>(feature, config));
    }
}