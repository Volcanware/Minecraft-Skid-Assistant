// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraftforge.model;

import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Matrix4f;

public interface ITransformation
{
    Matrix4f getMatrix();
    
    EnumFacing rotate(final EnumFacing p0);
    
    int rotate(final EnumFacing p0, final int p1);
}
