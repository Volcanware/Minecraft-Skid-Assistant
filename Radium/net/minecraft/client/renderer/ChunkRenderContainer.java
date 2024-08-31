// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.client.renderer;

import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;
import net.optifine.SmartAnimations;
import com.google.common.collect.Lists;
import java.util.BitSet;
import net.minecraft.client.renderer.chunk.RenderChunk;
import java.util.List;

public abstract class ChunkRenderContainer
{
    private double viewEntityX;
    private double viewEntityY;
    private double viewEntityZ;
    protected List<RenderChunk> renderChunks;
    protected boolean initialized;
    private BitSet animatedSpritesRendered;
    private final BitSet animatedSpritesCached;
    
    public ChunkRenderContainer() {
        this.renderChunks = (List<RenderChunk>)Lists.newArrayListWithCapacity(17424);
        this.animatedSpritesCached = new BitSet();
    }
    
    public void initialize(final double viewEntityXIn, final double viewEntityYIn, final double viewEntityZIn) {
        this.initialized = true;
        this.renderChunks.clear();
        this.viewEntityX = viewEntityXIn;
        this.viewEntityY = viewEntityYIn;
        this.viewEntityZ = viewEntityZIn;
        if (SmartAnimations.isActive()) {
            if (this.animatedSpritesRendered != null) {
                SmartAnimations.spritesRendered(this.animatedSpritesRendered);
            }
            else {
                this.animatedSpritesRendered = this.animatedSpritesCached;
            }
            this.animatedSpritesRendered.clear();
        }
        else if (this.animatedSpritesRendered != null) {
            SmartAnimations.spritesRendered(this.animatedSpritesRendered);
            this.animatedSpritesRendered = null;
        }
    }
    
    public void preRenderChunk(final RenderChunk renderChunkIn) {
        final BlockPos blockpos = renderChunkIn.getPosition();
        GL11.glTranslatef((float)(blockpos.getX() - this.viewEntityX), (float)(blockpos.getY() - this.viewEntityY), (float)(blockpos.getZ() - this.viewEntityZ));
    }
    
    public void addRenderChunk(final RenderChunk renderChunkIn, final EnumWorldBlockLayer layer) {
        this.renderChunks.add(renderChunkIn);
        if (this.animatedSpritesRendered != null) {
            final BitSet bitset = renderChunkIn.compiledChunk.getAnimatedSprites(layer);
            if (bitset != null) {
                this.animatedSpritesRendered.or(bitset);
            }
        }
    }
    
    public abstract void renderChunkLayer(final EnumWorldBlockLayer p0);
}
