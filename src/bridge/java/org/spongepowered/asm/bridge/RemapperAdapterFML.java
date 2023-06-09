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
package org.spongepowered.asm.bridge;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.extensibility.IRemapper;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.ObfuscationUtil;
import org.spongepowered.asm.util.ObfuscationUtil.IClassRemapper;

/**
 * Remapper adapter which remaps using FML's deobfuscating remapper
 */
public final class RemapperAdapterFML implements IRemapper, IClassRemapper {
    
    private static final String DEOBFUSCATING_REMAPPER_CLASS = "fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper";
    private static final String DEOBFUSCATING_REMAPPER_CLASS_FORGE = "net.minecraftforge." + RemapperAdapterFML.DEOBFUSCATING_REMAPPER_CLASS;
    private static final String DEOBFUSCATING_REMAPPER_CLASS_LEGACY = "cpw.mods." + RemapperAdapterFML.DEOBFUSCATING_REMAPPER_CLASS;
    private static final String INSTANCE_FIELD = "INSTANCE";
    private static final String UNMAP_METHOD = "unmap";

    private final ILogger logger = MixinService.getService().getLogger("mixin");

    private org.objectweb.asm.commons.Remapper fmlDeobfuscatingRemapper;
    private Method mdUnmap;
    
    private RemapperAdapterFML() {
        this.logger.info("Lazily initialising Mixin FML Remapper Adapter");
    }

    @Override
    public String mapMethodName(String owner, String name, String desc) {
        this.logger.debug("{} is remapping method {}{} for {}", this, name, desc, owner);
        if(owner == null || name == null || desc == null) {
            return name;
        }
        String newName = this.getFMLDeobfuscatingRemapper().mapMethodName(owner, name, desc);
        if (!newName.equals(name)) {
            return newName;
        }
        String obfOwner = this.unmap(owner);
        String obfDesc = this.unmapDesc(desc);
        this.logger.debug("{} is remapping obfuscated method {}{} for {}", this, name, obfDesc, obfOwner);
        return this.getFMLDeobfuscatingRemapper().mapMethodName(obfOwner, name, obfDesc);
    }

    @Override
    public String mapFieldName(String owner, String name, String desc) {
        this.logger.debug("{} is remapping field {}{} for {}", this, name, desc, owner);
        if(owner == null || name == null || desc == null) {
            return name;
        }
        String newName = this.getFMLDeobfuscatingRemapper().mapFieldName(owner, name, desc);
        if (!newName.equals(name)) {
            return newName;
        }
        String obfOwner = this.unmap(owner);
        String obfDesc = this.unmapDesc(desc);
        this.logger.debug("{} is remapping obfuscated field {}{} for {}", this, name, obfDesc, obfOwner);
        return this.getFMLDeobfuscatingRemapper().mapFieldName(obfOwner, name, obfDesc);
    }

    @Override
    public String map(String typeName) {
        this.logger.debug("{} is remapping class {}", this, typeName);
        if(typeName == null) {
            return typeName;
        }
        return this.getFMLDeobfuscatingRemapper().map(typeName);
    }

    @Override
    public String unmap(String typeName) {
        try {
            return this.mdUnmap.invoke(this.fmlDeobfuscatingRemapper, typeName).toString();
        } catch (Exception ex) {
            return typeName;
        }
    }

    @Override
    public String mapDesc(String desc) {
        if(desc == null) {
            return desc;
        }
        return this.getFMLDeobfuscatingRemapper().mapDesc(desc);
    }

    @Override
    public String unmapDesc(String desc) {
        if(desc == null) {
            return desc;
        }
        String newDesc = ObfuscationUtil.unmapDescriptor(desc, this);
        return newDesc != null ? newDesc : desc;
    }

    private org.objectweb.asm.commons.Remapper getFMLDeobfuscatingRemapper() {
        if (this.fmlDeobfuscatingRemapper == null) {
            try {
                Class<?> clDeobfRemapper = RemapperAdapterFML.getFMLDeobfuscatingRemapperClass();
                Field singletonField = clDeobfRemapper.getDeclaredField(RemapperAdapterFML.INSTANCE_FIELD);
                this.fmlDeobfuscatingRemapper = (org.objectweb.asm.commons.Remapper) singletonField.get(null);
                this.mdUnmap = clDeobfRemapper.getDeclaredMethod(RemapperAdapterFML.UNMAP_METHOD, String.class);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return this.fmlDeobfuscatingRemapper;
    }
    
    /**
     * Factory method
     */
    public static IRemapper create() {
        return new RemapperAdapterFML();
    }

    /**
     * Attempt to get the FML Deobfuscating Remapper, tries the post-1.8
     * namespace first and falls back to 1.7.10 if class lookup fails
     */
    private static Class<?> getFMLDeobfuscatingRemapperClass() throws ClassNotFoundException {
        try {
            return Class.forName(RemapperAdapterFML.DEOBFUSCATING_REMAPPER_CLASS_FORGE);
        } catch (ClassNotFoundException ex) {
            return Class.forName(RemapperAdapterFML.DEOBFUSCATING_REMAPPER_CLASS_LEGACY);
        }
    }

}
