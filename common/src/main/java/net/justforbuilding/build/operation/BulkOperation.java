package net.justforbuilding.build.operation;

import java.util.List;

/**
 * 一次批量操作的容器：一次撤销/重做回滚整批方块。
 * 作为 BuildOperation 的子类入栈，无需修改 BuildState/BuildOperation。
 */
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
