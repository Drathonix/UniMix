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
package org.spongepowered.asm.launch;

import org.spongepowered.asm.launch.platform.CommandLineOptions;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.Phase;
import org.spongepowered.asm.mixin.throwables.MixinError;
import org.spongepowered.asm.service.IMixinInternal;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.MixinService;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Bootstraps the mixin subsystem. This class acts as a bridge between the mixin
 * subsystem and the tweaker or coremod which is boostrapping it. Without this
 * class, a coremod may cause classload of MixinEnvironment in the
 * LaunchClassLoader before we have a chance to exclude it. By placing the main
 * bootstrap logic here we avoid the need for consumers to add the classloader
 * exclusion themselves.
 * 
 * <p>In development, where (because of the classloader environment at dev time)
 * it is safe to let a coremod initialise the mixin subsystem, we can perform
 * initialisation all in one go using the {@link #init} method and everything is
 * fine. However in production the tweaker must be used and the situation is a
 * little more delicate.</p>
 * 
 * <p>In an ideal world, the mixin tweaker would initialise the environment in
 * its constructor and that would be the end of the story. However we also need
 * to register the additional tweaker for environment to detect the transition
 * from pre-init to default and we cannot do this within the tweaker constructor
 * without triggering a ConcurrentModificationException in the tweaker list. To
 * work around this we register the secondary tweaker from within the mixin 
 * tweaker's acceptOptions method instead.</p>
 */
public abstract class MixinBootstrap {
    private MixinBootstrap() {}
    @Deprecated
    public static void addProxy() {}
    public static MixinPlatformManager getPlatform() {
        return null;
    }
    public static void init() {

    }
}
