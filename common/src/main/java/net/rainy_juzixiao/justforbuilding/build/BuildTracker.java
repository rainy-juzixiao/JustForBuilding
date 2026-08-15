package net.rainy_juzixiao.justforbuilding.build;

import net.rainy_juzixiao.justforbuilding.build.operation.BuildOperation;
import java.util.List;

public class BuildTracker {
    private static final ThreadLocal<List<BuildOperation>> OPERATIONS = new ThreadLocal<>();

    public static void startTracking(List<BuildOperation> list) {
        OPERATIONS.set(list);
    }

    public static List<BuildOperation> getOperations() {
        return OPERATIONS.get();
    }

    public static void stopTracking() {
        OPERATIONS.remove();
    }
}