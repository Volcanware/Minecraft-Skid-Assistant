// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.world;

import net.minecraft.util.IChatComponent;

public interface IWorldNameable
{
    String getCommandSenderName();
    
    boolean hasCustomName();
    
    IChatComponent getDisplayName();
}
