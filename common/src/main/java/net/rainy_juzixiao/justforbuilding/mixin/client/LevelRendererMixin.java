/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.rainy_juzixiao.justforbuilding.preview.PreviewFactory;
import net.rainy_juzixiao.justforbuilding.preview.RenderPreviewer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void justforbuilding$renderPreview(PoseStack poseStack, float f, long l, boolean bl,
                                               Camera camera, GameRenderer gameRenderer,
                                               LightTexture lightTexture, Matrix4f matrix4f,
                                               CallbackInfo ci) {
        for (RenderPreviewer previewer : PreviewFactory.all()) {
            previewer.render(poseStack, camera);
        }
    }
}
