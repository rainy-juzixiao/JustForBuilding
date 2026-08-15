/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.build.operation;

import java.util.List;

public class BulkOperation extends BuildOperation {

    public BulkOperation(List<BuildOperation> operations) {
        super(OperationType.BULK, "", 0L, "", "");
        this.operations = operations;
    }

    public List<BuildOperation> getOperations() {
        return operations;
    }

    private final List<BuildOperation> operations;
}
