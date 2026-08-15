/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding;

import me.shedaniel.architectury.event.events.client.ClientTickEvent;
import net.rainy_juzixiao.justforbuilding.command.ModCommands;
import net.rainy_juzixiao.justforbuilding.preview.PreviewFactory;
import net.rainy_juzixiao.justforbuilding.preview.PreviewerRegistry;
import net.rainy_juzixiao.justforbuilding.preview.RenderPreviewer;
import net.rainy_juzixiao.justforbuilding.preview.circle.CirclePreviewSync;
import net.rainy_juzixiao.justforbuilding.preview.cube.CubePreviewSync;
import net.rainy_juzixiao.justforbuilding.preview.line.LinePreviewSync;
import net.rainy_juzixiao.justforbuilding.preview.rect.RectPreviewSync;
import net.rainy_juzixiao.justforbuilding.preview.sphere.SpherePreviewSync;
import net.rainy_juzixiao.justforbuilding.preview.tree.TreePreviewSync;

public class justforbuilding {
    public static final String MOD_ID = "justforbuilding";

    public static void init() {
        ModCommands.register();
    }

    public static void initClient() {
        RectPreviewSync.register();
        LinePreviewSync.register();
        CubePreviewSync.register();
        CirclePreviewSync.register();
        SpherePreviewSync.register();
        TreePreviewSync.register();
        PreviewerRegistry.registerAll();
        ClientTickEvent.CLIENT_PRE.register(minecraft -> {
            for (RenderPreviewer previewer : PreviewFactory.all()) {
                previewer.tick(minecraft);
            }
        });
    }
}
