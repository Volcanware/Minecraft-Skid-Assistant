package net.optifine.shaders;

public class MultiTexID {
    public int base;
    public int norm;
    public int spec;

    public MultiTexID(final int baseTex, final int normTex, final int specTex) {
        this.base = baseTex;
        this.norm = normTex;
        this.spec = specTex;
    }
}
