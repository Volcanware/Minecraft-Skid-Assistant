// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VboRenderList;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.src.Config;
import net.minecraft.client.renderer.OpenGlHelper;
import java.nio.IntBuffer;
import net.optifine.util.LinkedList;
import net.minecraft.util.EnumWorldBlockLayer;

public class VboRegion
{
    private EnumWorldBlockLayer layer;
    private int glBufferId;
    private int capacity;
    private int positionTop;
    private int sizeUsed;
    private LinkedList<VboRange> rangeList;
    private VboRange compactRangeLast;
    private IntBuffer bufferIndexVertex;
    private IntBuffer bufferCountVertex;
    private int drawMode;
    private final int vertexBytes;
    
    public VboRegion(final EnumWorldBlockLayer layer) {
        this.layer = null;
        this.glBufferId = OpenGlHelper.glGenBuffers();
        this.capacity = 4096;
        this.positionTop = 0;
        this.rangeList = new LinkedList<VboRange>();
        this.compactRangeLast = null;
        this.bufferIndexVertex = Config.createDirectIntBuffer(this.capacity);
        this.bufferCountVertex = Config.createDirectIntBuffer(this.capacity);
        this.drawMode = 7;
        this.vertexBytes = DefaultVertexFormats.BLOCK.getNextOffset();
        this.layer = layer;
        this.bindBuffer();
        final long i = this.toBytes(this.capacity);
        OpenGlHelper.glBufferData(OpenGlHelper.GL_ARRAY_BUFFER, i, OpenGlHelper.GL_STATIC_DRAW);
        this.unbindBuffer();
    }
    
    public void bufferData(final ByteBuffer data, final VboRange range) {
        final int i = range.getPosition();
        final int j = range.getSize();
        final int k = this.toVertex(data.limit());
        if (k <= 0) {
            if (i >= 0) {
                range.setPosition(-1);
                range.setSize(0);
                this.rangeList.remove(range.getNode());
                this.sizeUsed -= j;
            }
        }
        else {
            if (k > j) {
                range.setPosition(this.positionTop);
                range.setSize(k);
                this.positionTop += k;
                if (i >= 0) {
                    this.rangeList.remove(range.getNode());
                }
                this.rangeList.addLast(range.getNode());
            }
            range.setSize(k);
            this.sizeUsed += k - j;
            this.checkVboSize(range.getPositionNext());
            final long l = this.toBytes(range.getPosition());
            this.bindBuffer();
            OpenGlHelper.glBufferSubData(OpenGlHelper.GL_ARRAY_BUFFER, l, data);
            this.unbindBuffer();
            if (this.positionTop > this.sizeUsed * 11 / 10) {
                this.compactRanges(1);
            }
        }
    }
    
    private void compactRanges(final int countMax) {
        if (!this.rangeList.isEmpty()) {
            VboRange vborange = this.compactRangeLast;
            if (vborange == null || !this.rangeList.contains(vborange.getNode())) {
                vborange = this.rangeList.getFirst().getItem();
            }
            int i = vborange.getPosition();
            final VboRange vborange2 = vborange.getPrev();
            if (vborange2 == null) {
                i = 0;
            }
            else {
                i = vborange2.getPositionNext();
            }
            int j = 0;
            while (vborange != null && j < countMax) {
                ++j;
                if (vborange.getPosition() == i) {
                    i += vborange.getSize();
                    vborange = vborange.getNext();
                }
                else {
                    final int k = vborange.getPosition() - i;
                    if (vborange.getSize() <= k) {
                        this.copyVboData(vborange.getPosition(), i, vborange.getSize());
                        vborange.setPosition(i);
                        i += vborange.getSize();
                        vborange = vborange.getNext();
                    }
                    else {
                        this.checkVboSize(this.positionTop + vborange.getSize());
                        this.copyVboData(vborange.getPosition(), this.positionTop, vborange.getSize());
                        vborange.setPosition(this.positionTop);
                        this.positionTop += vborange.getSize();
                        final VboRange vborange3 = vborange.getNext();
                        this.rangeList.remove(vborange.getNode());
                        this.rangeList.addLast(vborange.getNode());
                        vborange = vborange3;
                    }
                }
            }
            if (vborange == null) {
                this.positionTop = this.rangeList.getLast().getItem().getPositionNext();
            }
            this.compactRangeLast = vborange;
        }
    }
    
    private void checkRanges() {
        int i = 0;
        int j = 0;
        for (VboRange vborange = this.rangeList.getFirst().getItem(); vborange != null; vborange = vborange.getNext()) {
            ++i;
            j += vborange.getSize();
            if (vborange.getPosition() < 0 || vborange.getSize() <= 0 || vborange.getPositionNext() > this.positionTop) {
                throw new RuntimeException("Invalid range: " + vborange);
            }
            final VboRange vborange2 = vborange.getPrev();
            if (vborange2 != null && vborange.getPosition() < vborange2.getPositionNext()) {
                throw new RuntimeException("Invalid range: " + vborange);
            }
            final VboRange vborange3 = vborange.getNext();
            if (vborange3 != null && vborange.getPositionNext() > vborange3.getPosition()) {
                throw new RuntimeException("Invalid range: " + vborange);
            }
        }
        if (i != this.rangeList.getSize()) {
            throw new RuntimeException("Invalid count: " + i + " <> " + this.rangeList.getSize());
        }
        if (j != this.sizeUsed) {
            throw new RuntimeException("Invalid size: " + j + " <> " + this.sizeUsed);
        }
    }
    
    private void checkVboSize(final int sizeMin) {
        if (this.capacity < sizeMin) {
            this.expandVbo(sizeMin);
        }
    }
    
    private void copyVboData(final int posFrom, final int posTo, final int size) {
        final long i = this.toBytes(posFrom);
        final long j = this.toBytes(posTo);
        final long k = this.toBytes(size);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, this.glBufferId);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, this.glBufferId);
        OpenGlHelper.glCopyBufferSubData(OpenGlHelper.GL_COPY_READ_BUFFER, OpenGlHelper.GL_COPY_WRITE_BUFFER, i, j, k);
        Config.checkGlError("Copy VBO range");
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, 0);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, 0);
    }
    
    private void expandVbo(final int sizeMin) {
        int i;
        for (i = this.capacity * 6 / 4; i < sizeMin; i = i * 6 / 4) {}
        final long j = this.toBytes(this.capacity);
        final long k = this.toBytes(i);
        final int l = OpenGlHelper.glGenBuffers();
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, l);
        OpenGlHelper.glBufferData(OpenGlHelper.GL_ARRAY_BUFFER, k, OpenGlHelper.GL_STATIC_DRAW);
        Config.checkGlError("Expand VBO");
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, this.glBufferId);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, l);
        OpenGlHelper.glCopyBufferSubData(OpenGlHelper.GL_COPY_READ_BUFFER, OpenGlHelper.GL_COPY_WRITE_BUFFER, 0L, 0L, j);
        Config.checkGlError("Copy VBO: " + k);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, 0);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, 0);
        OpenGlHelper.glDeleteBuffers(this.glBufferId);
        this.bufferIndexVertex = Config.createDirectIntBuffer(i);
        this.bufferCountVertex = Config.createDirectIntBuffer(i);
        this.glBufferId = l;
        this.capacity = i;
    }
    
    public void bindBuffer() {
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, this.glBufferId);
    }
    
    public void drawArrays(final int drawMode, final VboRange range) {
        if (this.drawMode != drawMode) {
            if (this.bufferIndexVertex.position() > 0) {
                throw new IllegalArgumentException("Mixed region draw modes: " + this.drawMode + " != " + drawMode);
            }
            this.drawMode = drawMode;
        }
        this.bufferIndexVertex.put(range.getPosition());
        this.bufferCountVertex.put(range.getSize());
    }
    
    public void finishDraw(final VboRenderList vboRenderList) {
        this.bindBuffer();
        vboRenderList.setupArrayPointers();
        this.bufferIndexVertex.flip();
        this.bufferCountVertex.flip();
        GlStateManager.glMultiDrawArrays(this.drawMode, this.bufferIndexVertex, this.bufferCountVertex);
        this.bufferIndexVertex.limit(this.bufferIndexVertex.capacity());
        this.bufferCountVertex.limit(this.bufferCountVertex.capacity());
        if (this.positionTop > this.sizeUsed * 11 / 10) {
            this.compactRanges(1);
        }
    }
    
    public void unbindBuffer() {
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
    }
    
    public void deleteGlBuffers() {
        if (this.glBufferId >= 0) {
            OpenGlHelper.glDeleteBuffers(this.glBufferId);
            this.glBufferId = -1;
        }
    }
    
    private long toBytes(final int vertex) {
        return vertex * (long)this.vertexBytes;
    }
    
    private int toVertex(final long bytes) {
        return (int)(bytes / this.vertexBytes);
    }
    
    public int getPositionTop() {
        return this.positionTop;
    }
}
