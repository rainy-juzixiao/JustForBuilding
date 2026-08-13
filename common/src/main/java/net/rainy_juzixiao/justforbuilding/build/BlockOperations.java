/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.build;

import net.rainy_juzixiao.justforbuilding.build.operation.BuildOperation;
import net.rainy_juzixiao.justforbuilding.build.operation.BulkOperation;
import net.rainy_juzixiao.justforbuilding.build.operation.OperationType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public final class BlockOperations {

    private static final int FLAGS = 3;

    private BlockOperations() {
    }

    public static boolean setBlockWithRecord(ServerLevel level, BlockPos pos,
                                             BlockState newState, List<BuildOperation> operations) {
        if (!level.isLoaded(pos) || pos.getY() < 0 || pos.getY() >= level.getHeight()) {
            return false;
        }
        BlockState oldState = level.getBlockState(pos);
        if (oldState.getBlock() == newState.getBlock()
                && oldState.getValues().equals(newState.getValues())) {
            return false;
        }
        level.setBlock(pos, newState, FLAGS);
        operations.add(new BuildOperation(
                OperationType.PLACE_BLOCK,
                level.dimension().location().toString(),
                pos.asLong(),
                idOf(oldState),
                idOf(newState)
        ));
        return true;
    }

    public static void executeUndo(MinecraftServer server, BuildOperation operation) {
        if (operation instanceof BulkOperation) {
            List<BuildOperation> children = ((BulkOperation) operation).getOperations();
            for (int i = children.size() - 1; i >= 0; i--) {
                apply(server, children.get(i), false);
            }
        } else {
            apply(server, operation, false);
        }
    }

    public static void executeRedo(MinecraftServer server, BuildOperation operation) {
        if (operation instanceof BulkOperation) {
            for (BuildOperation child : ((BulkOperation) operation).getOperations()) {
                apply(server, child, true);
            }
        } else {
            apply(server, operation, true);
        }
    }

    private static void apply(MinecraftServer server, BuildOperation operation, boolean redo) {
        ServerLevel level = server.getLevel(
                ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(operation.getDimension())));
        if (level == null) {
            return;
        }
        BlockPos pos = BlockPos.of(operation.getPosition());
        BlockState target = parseState(redo ? operation.getNewBlock() : operation.getOldBlock());
        level.setBlock(pos, target, FLAGS);
    }

    public static String idOf(BlockState state) {
        return Registry.BLOCK.getKey(state.getBlock()).toString();
    }

    public static BlockState parseState(String id) {
        if (id == null || id.isEmpty()) {
            return Blocks.AIR.defaultBlockState();
        }
        ResourceLocation location = new ResourceLocation(id);
        return Registry.BLOCK.getOptional(location)
                .map(Block::defaultBlockState)
                .orElse(Blocks.AIR.defaultBlockState());
    }
}
