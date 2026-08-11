package net.justforbuilding.build.history;

import net.justforbuilding.build.operation.BuildOperation;

import java.util.ArrayDeque;
import java.util.Deque;

public class UndoHistory {
    private final static int limit = 100;

    public UndoHistory() {
        undo = new ArrayDeque<>();
        redo = new ArrayDeque<>();
    }

    public void push(BuildOperation operation) {
        undo.push(operation);
        redo.clear();
        while (undo.size() > limit) {
            undo.removeLast();
        }
    }

    public BuildOperation undo() {
        if (undo.isEmpty()) {
            return null;
        }
        BuildOperation op = undo.pop();
        redo.push(op);
        return op;
    }


    public BuildOperation redo() {
        if (redo.isEmpty()) {
            return null;
        }
        BuildOperation op = redo.pop();
        undo.push(op);
        return op;
    }

    private final Deque<BuildOperation> undo;
    private final Deque<BuildOperation> redo;
}