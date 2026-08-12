/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.build;


import net.rainy_juzixiao.justforbuilding.build.operation.BuildOperation;

import java.util.ArrayDeque;
import java.util.Deque;

public class BuildState {
    public BuildState() {
        this.building = false;
        this.mode = BuildMode.NONE;
        this.anchor = RectAnchor.FRONT_LEFT;
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

    public BuildMode getMode() {
        return mode;
    }

    public void setMode(BuildMode mode) {
        this.mode = mode;
    }

    public String getSelectedBlock() {
        return selectedBlock;
    }

    public void setSelectedBlock(String selectedBlock) {
        this.selectedBlock = selectedBlock;
    }

    public int getUndoSize() {
        return undoStack.size();
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public BuildDirection getDirection() {
        return direction;
    }

    public void setDirection(BuildDirection direction) {
        this.direction = direction;
    }

    public boolean isKeep() {
        return keep;
    }

    public void setKeep(boolean keep) {
        this.keep = keep;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isHollow() {
        return hollow;
    }

    public void setHollow(boolean hollow) {
        this.hollow = hollow;
    }

    public RectAnchor getAnchor() {
        return anchor;
    }

    public void setAnchor(RectAnchor anchor) {
        this.anchor = anchor;
    }

    private boolean building;
    private BuildMode mode;

    private String selectedBlock;

    private int length;
    private int interval;
    private BuildDirection direction;
    private boolean keep;

    private int width;
    private boolean hollow;
    private RectAnchor anchor;

    private final Deque<BuildOperation> undoStack;
    private final Deque<BuildOperation> redoStack;
}