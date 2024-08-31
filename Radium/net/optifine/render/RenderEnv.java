// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.render;

import net.minecraft.block.BlockLeaves;
import net.minecraft.src.Config;
import net.minecraft.client.renderer.block.model.BreakingFour;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.util.EnumWorldBlockLayer;
import java.util.ArrayList;
import net.optifine.model.ListQuadsOverlay;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import java.util.List;
import net.minecraft.util.EnumFacing;
import net.optifine.BlockPosM;
import net.minecraft.client.renderer.BlockModelRenderer;
import java.util.BitSet;
import net.minecraft.util.BlockPos;
import net.minecraft.block.state.IBlockState;

public class RenderEnv
{
    private IBlockState blockState;
    private BlockPos blockPos;
    private int blockId;
    private int metadata;
    private int breakingAnimation;
    private int smartLeaves;
    private float[] quadBounds;
    private BitSet boundsFlags;
    private BlockModelRenderer.AmbientOcclusionFace aoFace;
    private BlockPosM colorizerBlockPosM;
    private boolean[] borderFlags;
    private boolean[] borderFlags2;
    private boolean[] borderFlags3;
    private EnumFacing[] borderDirections;
    private List<BakedQuad> listQuadsCustomizer;
    private List<BakedQuad> listQuadsCtmMultipass;
    private BakedQuad[] arrayQuadsCtm1;
    private BakedQuad[] arrayQuadsCtm2;
    private BakedQuad[] arrayQuadsCtm3;
    private BakedQuad[] arrayQuadsCtm4;
    private RegionRenderCacheBuilder regionRenderCacheBuilder;
    private ListQuadsOverlay[] listsQuadsOverlay;
    private boolean overlaysRendered;
    private static final int UNKNOWN = -1;
    private static final int FALSE = 0;
    private static final int TRUE = 1;
    
    public RenderEnv(final IBlockState blockState, final BlockPos blockPos) {
        this.blockId = -1;
        this.metadata = -1;
        this.breakingAnimation = -1;
        this.smartLeaves = -1;
        this.quadBounds = new float[EnumFacing.VALUES.length * 2];
        this.boundsFlags = new BitSet(3);
        this.aoFace = new BlockModelRenderer.AmbientOcclusionFace();
        this.colorizerBlockPosM = null;
        this.borderFlags = null;
        this.borderFlags2 = null;
        this.borderFlags3 = null;
        this.borderDirections = null;
        this.listQuadsCustomizer = new ArrayList<BakedQuad>();
        this.listQuadsCtmMultipass = new ArrayList<BakedQuad>();
        this.arrayQuadsCtm1 = new BakedQuad[1];
        this.arrayQuadsCtm2 = new BakedQuad[2];
        this.arrayQuadsCtm3 = new BakedQuad[3];
        this.arrayQuadsCtm4 = new BakedQuad[4];
        this.regionRenderCacheBuilder = null;
        this.listsQuadsOverlay = new ListQuadsOverlay[EnumWorldBlockLayer.values().length];
        this.overlaysRendered = false;
        this.blockState = blockState;
        this.blockPos = blockPos;
    }
    
    public void reset(final IBlockState blockStateIn, final BlockPos blockPosIn) {
        if (this.blockState != blockStateIn || this.blockPos != blockPosIn) {
            this.blockState = blockStateIn;
            this.blockPos = blockPosIn;
            this.blockId = -1;
            this.metadata = -1;
            this.breakingAnimation = -1;
            this.smartLeaves = -1;
            this.boundsFlags.clear();
        }
    }
    
    public int getBlockId() {
        if (this.blockId < 0) {
            if (this.blockState instanceof BlockStateBase) {
                final BlockStateBase blockstatebase = (BlockStateBase)this.blockState;
                this.blockId = blockstatebase.getBlockId();
            }
            else {
                this.blockId = Block.getIdFromBlock(this.blockState.getBlock());
            }
        }
        return this.blockId;
    }
    
    public int getMetadata() {
        if (this.metadata < 0) {
            if (this.blockState instanceof BlockStateBase) {
                final BlockStateBase blockstatebase = (BlockStateBase)this.blockState;
                this.metadata = blockstatebase.getMetadata();
            }
            else {
                this.metadata = this.blockState.getBlock().getMetaFromState(this.blockState);
            }
        }
        return this.metadata;
    }
    
    public float[] getQuadBounds() {
        return this.quadBounds;
    }
    
    public BitSet getBoundsFlags() {
        return this.boundsFlags;
    }
    
    public BlockModelRenderer.AmbientOcclusionFace getAoFace() {
        return this.aoFace;
    }
    
    public boolean isBreakingAnimation(final List listQuads) {
        if (this.breakingAnimation == -1 && listQuads.size() > 0) {
            if (listQuads.get(0) instanceof BreakingFour) {
                this.breakingAnimation = 1;
            }
            else {
                this.breakingAnimation = 0;
            }
        }
        return this.breakingAnimation == 1;
    }
    
    public boolean isBreakingAnimation(final BakedQuad quad) {
        if (this.breakingAnimation < 0) {
            if (quad instanceof BreakingFour) {
                this.breakingAnimation = 1;
            }
            else {
                this.breakingAnimation = 0;
            }
        }
        return this.breakingAnimation == 1;
    }
    
    public boolean isBreakingAnimation() {
        return this.breakingAnimation == 1;
    }
    
    public IBlockState getBlockState() {
        return this.blockState;
    }
    
    public BlockPosM getColorizerBlockPosM() {
        if (this.colorizerBlockPosM == null) {
            this.colorizerBlockPosM = new BlockPosM(0, 0, 0);
        }
        return this.colorizerBlockPosM;
    }
    
    public boolean[] getBorderFlags() {
        if (this.borderFlags == null) {
            this.borderFlags = new boolean[4];
        }
        return this.borderFlags;
    }
    
    public boolean[] getBorderFlags2() {
        if (this.borderFlags2 == null) {
            this.borderFlags2 = new boolean[4];
        }
        return this.borderFlags2;
    }
    
    public boolean[] getBorderFlags3() {
        if (this.borderFlags3 == null) {
            this.borderFlags3 = new boolean[4];
        }
        return this.borderFlags3;
    }
    
    public EnumFacing[] getBorderDirections() {
        if (this.borderDirections == null) {
            this.borderDirections = new EnumFacing[4];
        }
        return this.borderDirections;
    }
    
    public EnumFacing[] getBorderDirections(final EnumFacing dir0, final EnumFacing dir1, final EnumFacing dir2, final EnumFacing dir3) {
        final EnumFacing[] aenumfacing = this.getBorderDirections();
        aenumfacing[0] = dir0;
        aenumfacing[1] = dir1;
        aenumfacing[2] = dir2;
        aenumfacing[3] = dir3;
        return aenumfacing;
    }
    
    public boolean isSmartLeaves() {
        if (this.smartLeaves == -1) {
            if (Config.isTreesSmart() && this.blockState.getBlock() instanceof BlockLeaves) {
                this.smartLeaves = 1;
            }
            else {
                this.smartLeaves = 0;
            }
        }
        return this.smartLeaves == 1;
    }
    
    public List<BakedQuad> getListQuadsCustomizer() {
        return this.listQuadsCustomizer;
    }
    
    public BakedQuad[] getArrayQuadsCtm(final BakedQuad quad) {
        this.arrayQuadsCtm1[0] = quad;
        return this.arrayQuadsCtm1;
    }
    
    public BakedQuad[] getArrayQuadsCtm(final BakedQuad quad0, final BakedQuad quad1) {
        this.arrayQuadsCtm2[0] = quad0;
        this.arrayQuadsCtm2[1] = quad1;
        return this.arrayQuadsCtm2;
    }
    
    public BakedQuad[] getArrayQuadsCtm(final BakedQuad quad0, final BakedQuad quad1, final BakedQuad quad2) {
        this.arrayQuadsCtm3[0] = quad0;
        this.arrayQuadsCtm3[1] = quad1;
        this.arrayQuadsCtm3[2] = quad2;
        return this.arrayQuadsCtm3;
    }
    
    public BakedQuad[] getArrayQuadsCtm(final BakedQuad quad0, final BakedQuad quad1, final BakedQuad quad2, final BakedQuad quad3) {
        this.arrayQuadsCtm4[0] = quad0;
        this.arrayQuadsCtm4[1] = quad1;
        this.arrayQuadsCtm4[2] = quad2;
        this.arrayQuadsCtm4[3] = quad3;
        return this.arrayQuadsCtm4;
    }
    
    public List<BakedQuad> getListQuadsCtmMultipass(final BakedQuad[] quads) {
        this.listQuadsCtmMultipass.clear();
        if (quads != null) {
            for (int i = 0; i < quads.length; ++i) {
                final BakedQuad bakedquad = quads[i];
                this.listQuadsCtmMultipass.add(bakedquad);
            }
        }
        return this.listQuadsCtmMultipass;
    }
    
    public RegionRenderCacheBuilder getRegionRenderCacheBuilder() {
        return this.regionRenderCacheBuilder;
    }
    
    public void setRegionRenderCacheBuilder(final RegionRenderCacheBuilder regionRenderCacheBuilder) {
        this.regionRenderCacheBuilder = regionRenderCacheBuilder;
    }
    
    public ListQuadsOverlay getListQuadsOverlay(final EnumWorldBlockLayer layer) {
        ListQuadsOverlay listquadsoverlay = this.listsQuadsOverlay[layer.ordinal()];
        if (listquadsoverlay == null) {
            listquadsoverlay = new ListQuadsOverlay();
            this.listsQuadsOverlay[layer.ordinal()] = listquadsoverlay;
        }
        return listquadsoverlay;
    }
    
    public boolean isOverlaysRendered() {
        return this.overlaysRendered;
    }
    
    public void setOverlaysRendered(final boolean overlaysRendered) {
        this.overlaysRendered = overlaysRendered;
    }
}
