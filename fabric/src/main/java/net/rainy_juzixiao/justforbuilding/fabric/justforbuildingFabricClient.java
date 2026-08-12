/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.fabric;

import net.rainy_juzixiao.justforbuilding.justforbuilding;
import net.fabricmc.api.ClientModInitializer;

public class justforbuildingFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        justforbuilding.initClient();
    }
}
