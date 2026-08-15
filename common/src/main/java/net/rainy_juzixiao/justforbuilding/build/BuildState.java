/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.build;

import net.rainy_juzixiao.justforbuilding.build.operation.BuildOperation;
import net.minecraft.core.BlockPos;

import java.util.ArrayDeque;
import java.util.Deque;

public class BuildState {
    public BuildState() {
        this.building = false;
        this.keep = false;
        this.destroy = false;
        this.context = null;
        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
    }

    public void pushOperation(BuildOperation operation) {
        undoStack.push(operation);
        redoStack.clear();
    }

    public BuildOperation undo() {
        if (undoStack.isEmpty()) {
            return null;
        }
        BuildOperation operation = undoStack.pop();
        redoStack.push(operation);
        return operation;
    }

    public BuildOperation redo() {
        if (redoStack.isEmpty()) {
            return null;
        }
        BuildOperation operation = redoStack.pop();
        undoStack.push(operation);
        return operation;
    }

    public boolean isBuilding() {
        return building;
    }

    public void setBuilding(boolean building) {
        this.building = building;
    }

    public int getUndoSize() {
        return undoStack.size();
    }

    public boolean isKeep() {
        return keep;
    }

    public void setKeep(boolean keep) {
        this.keep = keep;
    }

    public boolean isDestroy() {
        return destroy;
    }

    public void setDestroy(boolean destroy) {
        this.destroy = destroy;
    }

    public BuildContext getContext() {
        return context;
    }

    public void setContext(BuildContext context) {
        this.context = context;
    }

    public BlockPos getBasePos() {
        return basePos;
    }

    public void setBasePos(BlockPos basePos) {
        this.basePos = basePos;
    }

    public void clearBasePos() {
        this.basePos = null;
    }

    private boolean building;
    private boolean keep;
    private boolean destroy;
    private BuildContext context;
    private BlockPos basePos;

    private final Deque<BuildOperation> undoStack;
    private final Deque<BuildOperation> redoStack;
}