// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.config;

public class GlVersion
{
    private int major;
    private int minor;
    private int release;
    private String suffix;
    
    public GlVersion(final int major, final int minor) {
        this(major, minor, 0);
    }
    
    public GlVersion(final int major, final int minor, final int release) {
        this(major, minor, release, null);
    }
    
    public GlVersion(final int major, final int minor, final int release, final String suffix) {
        this.major = major;
        this.minor = minor;
        this.release = release;
        this.suffix = suffix;
    }
    
    public int getMajor() {
        return this.major;
    }
    
    public int getMinor() {
        return this.minor;
    }
    
    public int getRelease() {
        return this.release;
    }
    
    public int toInt() {
        return (this.minor > 9) ? (this.major * 100 + this.minor) : ((this.release > 9) ? (this.major * 100 + this.minor * 10 + 9) : (this.major * 100 + this.minor * 10 + this.release));
    }
    
    @Override
    public String toString() {
        return (this.suffix == null) ? (this.major + "." + this.minor + "." + this.release) : (this.major + "." + this.minor + "." + this.release + this.suffix);
    }
}
