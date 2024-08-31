// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.shaders;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import net.minecraft.src.Config;
import java.util.HashSet;
import net.optifine.config.MatchBlock;

public class BlockAlias
{
    private int blockAliasId;
    private MatchBlock[] matchBlocks;
    
    public BlockAlias(final int blockAliasId, final MatchBlock[] matchBlocks) {
        this.blockAliasId = blockAliasId;
        this.matchBlocks = matchBlocks;
    }
    
    public int getBlockAliasId() {
        return this.blockAliasId;
    }
    
    public boolean matches(final int id, final int metadata) {
        for (int i = 0; i < this.matchBlocks.length; ++i) {
            final MatchBlock matchblock = this.matchBlocks[i];
            if (matchblock.matches(id, metadata)) {
                return true;
            }
        }
        return false;
    }
    
    public int[] getMatchBlockIds() {
        final Set<Integer> set = new HashSet<Integer>();
        for (int i = 0; i < this.matchBlocks.length; ++i) {
            final MatchBlock matchblock = this.matchBlocks[i];
            final int j = matchblock.getBlockId();
            set.add(j);
        }
        final Integer[] ainteger = set.toArray(new Integer[set.size()]);
        final int[] aint = Config.toPrimitive(ainteger);
        return aint;
    }
    
    public MatchBlock[] getMatchBlocks(final int matchBlockId) {
        final List<MatchBlock> list = new ArrayList<MatchBlock>();
        for (int i = 0; i < this.matchBlocks.length; ++i) {
            final MatchBlock matchblock = this.matchBlocks[i];
            if (matchblock.getBlockId() == matchBlockId) {
                list.add(matchblock);
            }
        }
        final MatchBlock[] amatchblock = list.toArray(new MatchBlock[list.size()]);
        return amatchblock;
    }
    
    @Override
    public String toString() {
        return "block." + this.blockAliasId + "=" + Config.arrayToString(this.matchBlocks);
    }
}
