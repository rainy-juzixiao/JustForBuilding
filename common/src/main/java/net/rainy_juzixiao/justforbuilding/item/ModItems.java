/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.item;

/**
 * 模组物品实例。注册由各平台入口负责：
 * forge 使用 DeferredRegister，fabric 直接注册到 Registry.ITEM。
 */
public class ModItems {

    public static final NBSStaffItem NBS_STAFF = new NBSStaffItem();
}
