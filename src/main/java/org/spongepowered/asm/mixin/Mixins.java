/*
 * This file is part of Mixin, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.asm.mixin;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Entry point for registering global mixin resources. Compatibility with
 * pre-0.6 versions is maintained via the methods on {@link MixinEnvironment}
 * delegating to the methods here.
 */
public final class Mixins {
    private static IMixinBridge bridge;
    private static final Set<String> preloaded = new HashSet<String>();

    /**
     * Add multiple configurations
     *
     * @param configFiles config resources to add
     */
    public static void addConfigurations(String... configFiles) {
        for (String configFile : configFiles) {
            if (bridge != null) {
                bridge.addConfiguration(configFile);
            } else {
                preloaded.add(configFile);
            }
        }
    }
    /**
     * Add a mixin configuration resource
     *
     * @param configFile path to configuration resource
     */
    public static void addConfiguration(String configFile) {
        if(bridge != null){
            bridge.addConfiguration(configFile);
        }
        else{
            preloaded.add(configFile);
        }
    }

    public static void activate(IMixinBridge bridge){
        Mixins.bridge=bridge;
        for (String s : preloaded) {
            bridge.addConfiguration(s);
        }
    }
}
