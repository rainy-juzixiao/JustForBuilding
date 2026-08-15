/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.build.executor;

import net.rainy_juzixiao.justforbuilding.build.BlockOperations;
import net.rainy_juzixiao.justforbuilding.build.BuildExecutor;
import net.rainy_juzixiao.justforbuilding.build.operation.BuildOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class TreeExecutor implements BuildExecutor {
    public enum TreeType {
        RAINFOREST, BANYAN, FORKED, DWARF, MEDIUM, PINE
    }

    private final TreeType type;

    public TreeExecutor(TreeType type) {
        this.type = type;
    }

    public TreeType getType() {
        return type;
    }

    /**
     * 记录模式：非 null 时 setTile 在落方块的同时记录操作，供命令撤销/重做使用。
     */
    private List<BuildOperation> recording;

    @Override
    public List<BuildOperation> execute(ServerLevel level, BlockPos pos, BlockState seed, ServerPlayer player) {
        List<BuildOperation> operations = new ArrayList<>();
        this.recording = operations;
        try {
            execute(level, pos, seed, Blocks.OAK_LEAVES.defaultBlockState(), level.getRandom());
        } finally {
            this.recording = null;
        }
        return operations;
    }

    public void execute(LevelAccessor level, BlockPos pos, BlockState logState, BlockState leafState, Random random) {
        switch (type) {
            case RAINFOREST: generateRainforest(level, pos, logState, leafState, random); break;
            case BANYAN: generateBanyan(level, pos, logState, leafState, random); break;
            case FORKED: generateForked(level, pos, logState, leafState, random); break;
            case DWARF: generateDwarf(level, pos, logState, leafState, random); break;
            case MEDIUM: generateMedium(level, pos, logState, leafState, random); break;
            case PINE: generatePine(level, pos, logState, leafState, random); break;
        }
    }

    private void setTile(LevelAccessor level, BlockPos pos, BlockState state) {
        if (pos.getY() < 0 || pos.getY() >= level.getHeight()) return;
        if (recording != null && level instanceof ServerLevel) {
            BlockOperations.setBlockWithRecord((ServerLevel) level, pos, state, recording);
            return;
        }
        level.setBlock(pos, state, 3);
    }

    private BlockState getLogStateWithAxis(BlockState baseState, int dx, int dy, int dz) {
        if (!baseState.hasProperty(RotatedPillarBlock.AXIS)) return baseState;
        Direction.Axis axis;
        if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > Math.abs(dz)) axis = Direction.Axis.X;
        else if (Math.abs(dz) > Math.abs(dy) && Math.abs(dz) > Math.abs(dx)) axis = Direction.Axis.Z;
        else axis = Direction.Axis.Y;
        return baseState.setValue(RotatedPillarBlock.AXIS, axis);
    }

    private void extendY(LevelAccessor level, BlockPos pos, double length, BlockState state) {
        int l = (int) (length * 10);
        setTile(level, pos, state);
        for (int m = 1; m < l; m++) setTile(level, pos.above(m), state);
        for (int m = 1; m < l; m++) setTile(level, pos.below(m), state);
    }

    private void connectLine(LevelAccessor level, BlockPos start, BlockPos end, BlockState state) {
        int dx = end.getX() - start.getX(), dy = end.getY() - start.getY(), dz = end.getZ() - start.getZ();
        int max = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
        if (max == 0) { setTile(level, start, state); return; }
        for (int m = 0; m <= max; m++) {
            double ratio = (double) m / max;
            int x = (int) Math.round(start.getX() + dx * ratio);
            int y = (int) Math.round(start.getY() + dy * ratio);
            int z = (int) Math.round(start.getZ() + dz * ratio);
            setTile(level, new BlockPos(x, y, z), getLogStateWithAxis(state, dx, dy, dz));
        }
    }

    private void spreadLeaves(LevelAccessor level, BlockPos pos, double radius, BlockState state, Set<BlockPos> visited) {
        if (radius < 0 || visited.contains(pos)) return;
        visited.add(pos);
        if (level.getBlockState(pos).getBlock() == Blocks.AIR) setTile(level, pos, state);
        double random = level.getRandom().nextDouble();
        List<Direction> dirs = Arrays.asList(Direction.values());
        Collections.shuffle(dirs, level.getRandom());
        for (Direction dir : dirs) {
            BlockPos next = pos.relative(dir);
            if (next.getY() >= 0 && next.getY() < level.getHeight()) spreadLeaves(level, next, radius - random, state, visited);
        }
    }

    private void hangLeaves(LevelAccessor level, BlockPos pos, double length, BlockState state) {
        int l = (int) Math.floor(length);
        for (int m = 1; m < l; m++) {
            BlockPos below = pos.below(m);
            if (below.getY() < 0) break;
            BlockState current = level.getBlockState(below);
            if (current.getBlock() == Blocks.AIR || current.getBlock() == state.getBlock()) setTile(level, below, state);
            else break;
        }
    }

    private void buildCylinder(LevelAccessor level, BlockPos pos, int height, int radius, BlockState state) {
        for (int y = 0; y <= height; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z <= radius * radius) setTile(level, pos.offset(x, y, z), state);
                }
            }
        }
    }

    private void connectBranchPine(LevelAccessor level, BlockPos start, BlockPos end, BlockState logState, BlockState leafState) {
        int dx = end.getX() - start.getX(), dy = end.getY() - start.getY(), dz = end.getZ() - start.getZ();
        int max = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
        if (max == 0) return;
        for (int m = 0; m <= max; m++) {
            double ratio = (double) m / max;
            int x = (int) Math.round(start.getX() + dx * ratio);
            int y = (int) Math.round(start.getY() + dy * ratio);
            int z = (int) Math.round(start.getZ() + dz * ratio);
            BlockPos pos = new BlockPos(x, y, z);
            setTile(level, pos, getLogStateWithAxis(logState, dx, dy, dz));
            setTile(level, pos.above(), leafState); setTile(level, pos.below(), leafState);
            setTile(level, pos.east(), leafState); setTile(level, pos.west(), leafState);
            setTile(level, pos.north(), leafState); setTile(level, pos.south(), leafState);
            setTile(level, pos.below(2), leafState);
        }
    }

    private void generateRainforest(LevelAccessor level, BlockPos pos, BlockState logState, BlockState leafState, Random random) {
        double l = 15 + random.nextDouble() * 20;
        extendY(level, pos, l * 0.1, logState); extendY(level, pos.east(), l * 0.1, logState);
        extendY(level, pos.east().south(), l * 0.1, logState); extendY(level, pos.south(), l * 0.1, logState);
        BlockPos top1 = new BlockPos(pos.getX(), pos.getY() + (int)l, pos.getZ());
        BlockPos top2 = new BlockPos(pos.getX() + 1, pos.getY() + (int)l, pos.getZ() + 1);
        spreadLeaves(level, top1, 4, leafState, new HashSet<>()); spreadLeaves(level, top2, 4, leafState, new HashSet<>());
        for (int i = 0; i < 2; i++) {
            int ox = (int) (-4.5 + random.nextDouble() * 7), oz = (int) (-4.5 + random.nextDouble() * 7);
            hangLeaves(level, new BlockPos(pos.getX() + ox, pos.getY() + (int)l, pos.getZ() + oz), random.nextDouble() * l * 2, leafState);
        }
    }

    private void generateBanyan(LevelAccessor level, BlockPos pos, BlockState logState, BlockState leafState, Random random) {
        double pi = Math.PI / 180.0; int l = 5;
        buildCylinder(level, pos, l, 1, logState); buildCylinder(level, pos, 1, 2, logState); buildCylinder(level, pos, 0, 3, logState);
        BlockPos s1 = new BlockPos(pos.getX(), pos.getY() + l, pos.getZ()); int o = 10;
        for (int m = 0; m < 16; m++) {
            double a = Math.cos((random.nextDouble() + m) * 22.5 * pi) * o, b = Math.sin(random.nextDouble() * 90 * pi) * o, c = Math.sin((random.nextDouble() + m) * 22.5 * pi) * o;
            BlockPos s2 = new BlockPos(pos.getX() + (int)a, pos.getY() + l + (int)b, pos.getZ() + (int)c);
            connectLine(level, s1, s2, logState); spreadLeaves(level, s2, 4, leafState, new HashSet<>());
        }
        for (int m = 0; m < 8; m++) {
            double a = Math.cos((random.nextDouble() + m) * 45 * pi) * o / 2.0, b = Math.sin(random.nextDouble() * 90 * pi) * o * 1.5, c = Math.sin((random.nextDouble() + m) * 45 * pi) * o / 2.0;
            BlockPos s2 = new BlockPos(pos.getX() + (int)a, pos.getY() + l + (int)b, pos.getZ() + (int)c);
            connectLine(level, s1, s2, logState); spreadLeaves(level, s2, 4, leafState, new HashSet<>());
        }
    }

    private void generateForked(LevelAccessor level, BlockPos pos, BlockState logState, BlockState leafState, Random random) {
        double pi = Math.PI / 180.0, l = 10 + random.nextDouble() * 10;
        extendY(level, pos, l * 0.12, logState);
        spreadLeaves(level, new BlockPos(pos.getX(), pos.getY() + (int)(l * 1.2), pos.getZ()), 3, leafState, new HashSet<>());
        int o = (int) (random.nextDouble() * 3 + 1);
        for (int m = 0; m < o; m++) {
            double q = random.nextDouble();
            BlockPos s1 = new BlockPos(pos.getX(), pos.getY() + (int)(l * q * 0.5 + 4), pos.getZ());
            double a = Math.cos(random.nextDouble() * 7200 * pi) * 0.4 * l, b = Math.sin(random.nextDouble() * 90 * pi) * 0.3 * l, c = Math.sin(random.nextDouble() * 7200 * pi) * 0.4 * l;
            BlockPos s2 = new BlockPos(pos.getX() + (int)a, pos.getY() + (int)(l * q * 0.5 + 4 + b), pos.getZ() + (int)c);
            connectLine(level, s1, s2, logState); spreadLeaves(level, s2, 2, leafState, new HashSet<>());
        }
    }

    private void generateDwarf(LevelAccessor level, BlockPos pos, BlockState logState, BlockState leafState, Random random) {
        double l = 3 + random.nextDouble() * 3;
        extendY(level, pos, l * 0.1, logState);
        spreadLeaves(level, new BlockPos(pos.getX(), pos.getY() + (int)l, pos.getZ()), 2, leafState, new HashSet<>());
    }

    private void generateMedium(LevelAccessor level, BlockPos pos, BlockState logState, BlockState leafState, Random random) {
        double l = 10 + random.nextDouble() * 5;
        extendY(level, pos, l * 0.1, logState);
        spreadLeaves(level, new BlockPos(pos.getX(), pos.getY() + (int)l, pos.getZ()), 2, leafState, new HashSet<>());
        spreadLeaves(level, new BlockPos(pos.getX(), pos.getY() + (int)(l * 0.65), pos.getZ()), 2, leafState, new HashSet<>());
    }

    private void generatePine(LevelAccessor level, BlockPos pos, BlockState logState, BlockState leafState, Random random) {
        double pi = Math.PI / 180.0, l = 27 + random.nextDouble() * 5;
        int o = (int) (random.nextDouble() * 3 + 7);
        for (int m = 0; m < o; m++) {
            for (double k = 0; k < 1; k += 0.2 * m / o + 0.25) {
                double is = 360 * pi * k, yaw = random.nextDouble() * 7200 * pi, l2 = (1 - (double) m / o) * 4 + 1;
                BlockPos s1 = new BlockPos(pos.getX(), pos.getY() + (int)(l / o * m + 5 + 4), pos.getZ());
                double a = Math.cos(yaw + is) * l2, b = Math.sin(-random.nextDouble() * 60 * pi) * l2 * 0.23, c = Math.sin(yaw + is) * l2;
                BlockPos s2 = new BlockPos(pos.getX() + (int)a, pos.getY() + (int)(l / o * m + 5 + b), pos.getZ() + (int)c);
                connectBranchPine(level, s1, s2, logState, leafState);
            }
        }
        connectLine(level, pos, new BlockPos(pos.getX(), pos.getY() + (int)l, pos.getZ()), logState);
    }
}