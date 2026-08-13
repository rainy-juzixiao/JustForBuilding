/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.preview;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class PreviewFactory {

    private static final Map<BuildMode, RenderPreviewer> PREVIEWERS = new HashMap<>();

    private PreviewFactory() {
    }

    public static void register(BuildMode mode, RenderPreviewer previewer) {
        PREVIEWERS.put(mode, previewer);
    }

    public static RenderPreviewer get(BuildMode mode) {
        RenderPreviewer previewer = PREVIEWERS.get(mode);
        if (previewer == null) {
            throw new IllegalArgumentException("No previewer registered for mode: " + mode);
        }
        return previewer;
    }

    public static Collection<RenderPreviewer> all() {
        return PREVIEWERS.values();
    }
}
