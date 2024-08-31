package net.minecraft.realms;

import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class RealmsVertexFormatElement {
    private final VertexFormatElement v;

    public RealmsVertexFormatElement(final VertexFormatElement p_i46463_1_) {
        this.v = p_i46463_1_;
    }

    public VertexFormatElement getVertexFormatElement() {
        return this.v;
    }

    public boolean isPosition() {
        return this.v.isPositionElement();
    }

    public int getIndex() {
        return this.v.getIndex();
    }

    public int getByteSize() {
        return this.v.getSize();
    }

    public int getCount() {
        return this.v.getElementCount();
    }

    public int hashCode() {
        return this.v.hashCode();
    }

    public boolean equals(final Object p_equals_1_) {
        return this.v.equals(p_equals_1_);
    }

    public String toString() {
        return this.v.toString();
    }
}
