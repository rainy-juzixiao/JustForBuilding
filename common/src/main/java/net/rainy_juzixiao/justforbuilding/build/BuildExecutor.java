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

import java.util.ArrayList;
import java.util.List;

public class BuildExecutor {
    private static final int FLAGS = 3; // UPDATE_NEIGHBORS | UPDATE_CLIENTS

    public static int placeLine(ServerLevel level, BlockPos start, BuildDirection direction,
                                int length, int interval, BlockState seed,
                                BuildState state) {
        boolean startAlready = level.getBlockState(start).getBlock() == seed.getBlock();
        List<BuildOperation> operations = new ArrayList<>();
        if (startAlready) {
            operations.add(new BuildOperation(
                    OperationType.PLACE_BLOCK,
                    level.dimension().location().toString(),
                    start.asLong(),
                    "minecraft:air",
                    idOf(seed)
            ));
        }
        BlockPos pos = start;
        int placed = 0;
        for (int i = 0; i < length; i++) {
            if (setBlockWithRecord(level, pos, seed, operations)) {
                placed++;
            }
            if (i + 1 < length) {
                pos = direction.offset(pos, interval + 1);
            }
        }
        if (!operations.isEmpty()) {
            state.pushOperation(new BulkOperation(operations));
        }
        return startAlready ? placed + 1 : placed;
    }

    public static int placeRect(ServerLevel level, BlockPos start, BuildDirection direction,
                                int length, int width, boolean hollow, RectAnchor anchor,
                                BlockState seed, BuildState state) {
        boolean startAlready = level.getBlockState(start).getBlock() == seed.getBlock();
        List<BuildOperation> operations = new ArrayList<>();
        if (startAlready) {
            operations.add(new BuildOperation(
                    OperationType.PLACE_BLOCK,
                    level.dimension().location().toString(),
                    start.asLong(),
                    "minecraft:air",
                    idOf(seed)
            ));
        }
        List<BlockPos> positions = new ArrayList<>(length * width);
        RectGeometry.fillPositions(start, direction, length, width, hollow, anchor, positions);
        int placed = 0;
        for (BlockPos pos : positions) {
            if (setBlockWithRecord(level, pos, seed, operations)) {
                placed++;
            }
        }
        if (!operations.isEmpty()) {
            state.pushOperation(new BulkOperation(operations));
        }
        return startAlready ? placed + 1 : placed;
    }

    private static boolean setBlockWithRecord(ServerLevel level, BlockPos pos,
                                              BlockState newState, List<BuildOperation> operations) {
        if (!level.isLoaded(pos) || pos.getY() < 0 || pos.getY() >= level.getHeight()) {
            return false;
        }
        BlockState oldState = level.getBlockState(pos);
        if (oldState.getBlock() == newState.getBlock()) {
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
