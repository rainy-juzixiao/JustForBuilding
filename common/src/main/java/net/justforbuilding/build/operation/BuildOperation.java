/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.justforbuilding.build.operation;


public class BuildOperation {
    public BuildOperation(
            OperationType type,
            String dimension,
            long position,
            String oldBlock,
            String newBlock
    ) {

        this.type = type;

        this.dimension = dimension;

        this.position = position;

        this.oldBlock = oldBlock;

        this.newBlock = newBlock;

    }

    public OperationType getType() {

        return type;

    }

    public String getDimension() {

        return dimension;

    }

    public long getPosition() {

        return position;

    }

    public String getOldBlock() {

        return oldBlock;

    }

    public String getNewBlock() {
        return newBlock;
    }

    private final OperationType type;
    private final String dimension;
    private final long position;
    private final String oldBlock;
    private final String newBlock;
}