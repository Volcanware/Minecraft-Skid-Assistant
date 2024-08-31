// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.client.renderer.chunk;

import java.util.Iterator;
import java.util.Set;
import net.minecraft.util.EnumFacing;

public class SetVisibility
{
    private static final int COUNT_FACES;
    private long bits;
    
    static {
        COUNT_FACES = EnumFacing.values().length;
    }
    
    public void setManyVisible(final Set<EnumFacing> p_178620_1_) {
        for (final EnumFacing enumfacing : p_178620_1_) {
            for (final EnumFacing enumfacing2 : p_178620_1_) {
                this.setVisible(enumfacing, enumfacing2, true);
            }
        }
    }
    
    public void setVisible(final EnumFacing facing, final EnumFacing facing2, final boolean p_178619_3_) {
        this.setBit(facing.ordinal() + facing2.ordinal() * SetVisibility.COUNT_FACES, p_178619_3_);
        this.setBit(facing2.ordinal() + facing.ordinal() * SetVisibility.COUNT_FACES, p_178619_3_);
    }
    
    public void setAllVisible(final boolean visible) {
        if (visible) {
            this.bits = -1L;
        }
        else {
            this.bits = 0L;
        }
    }
    
    public boolean isVisible(final EnumFacing facing, final EnumFacing facing2) {
        return this.getBit(facing.ordinal() + facing2.ordinal() * SetVisibility.COUNT_FACES);
    }
    
    @Override
    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(' ');
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing enumfacing = values[i];
            stringbuilder.append(' ').append(enumfacing.toString().toUpperCase().charAt(0));
        }
        stringbuilder.append('\n');
        EnumFacing[] values2;
        for (int length2 = (values2 = EnumFacing.values()).length, j = 0; j < length2; ++j) {
            final EnumFacing enumfacing2 = values2[j];
            stringbuilder.append(enumfacing2.toString().toUpperCase().charAt(0));
            EnumFacing[] values3;
            for (int length3 = (values3 = EnumFacing.values()).length, k = 0; k < length3; ++k) {
                final EnumFacing enumfacing3 = values3[k];
                if (enumfacing2 == enumfacing3) {
                    stringbuilder.append("  ");
                }
                else {
                    final boolean flag = this.isVisible(enumfacing2, enumfacing3);
                    stringbuilder.append(' ').append(flag ? 'Y' : 'n');
                }
            }
            stringbuilder.append('\n');
        }
        return stringbuilder.toString();
    }
    
    private boolean getBit(final int p_getBit_1_) {
        return (this.bits & (long)(1 << p_getBit_1_)) != 0x0L;
    }
    
    private void setBit(final int p_setBit_1_, final boolean p_setBit_2_) {
        if (p_setBit_2_) {
            this.setBit(p_setBit_1_);
        }
        else {
            this.clearBit(p_setBit_1_);
        }
    }
    
    private void setBit(final int p_setBit_1_) {
        this.bits |= 1 << p_setBit_1_;
    }
    
    private void clearBit(final int p_clearBit_1_) {
        this.bits &= ~(1 << p_clearBit_1_);
    }
}
