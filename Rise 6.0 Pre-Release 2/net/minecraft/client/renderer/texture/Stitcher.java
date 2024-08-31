package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.util.MathHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Stitcher {
    private final int mipmapLevelStitcher;
    private final Set<Stitcher.Holder> setStitchHolders = Sets.newHashSetWithExpectedSize(256);
    private final List<Stitcher.Slot> stitchSlots = Lists.newArrayListWithCapacity(256);
    private int currentWidth;
    private int currentHeight;
    private final int maxWidth;
    private final int maxHeight;
    private final boolean forcePowerOf2;

    /**
     * Max size (width or height) of a single tile
     */
    private final int maxTileDimension;

    public Stitcher(final int maxTextureWidth, final int maxTextureHeight, final boolean p_i45095_3_, final int p_i45095_4_, final int mipmapLevel) {
        this.mipmapLevelStitcher = mipmapLevel;
        this.maxWidth = maxTextureWidth;
        this.maxHeight = maxTextureHeight;
        this.forcePowerOf2 = p_i45095_3_;
        this.maxTileDimension = p_i45095_4_;
    }

    public int getCurrentWidth() {
        return this.currentWidth;
    }

    public int getCurrentHeight() {
        return this.currentHeight;
    }

    public void addSprite(final TextureAtlasSprite p_110934_1_) {
        final Stitcher.Holder stitcher$holder = new Stitcher.Holder(p_110934_1_, this.mipmapLevelStitcher);

        if (this.maxTileDimension > 0) {
            stitcher$holder.setNewDimension(this.maxTileDimension);
        }

        this.setStitchHolders.add(stitcher$holder);
    }

    public void doStitch() {
        final Stitcher.Holder[] astitcher$holder = this.setStitchHolders.toArray(new Holder[this.setStitchHolders.size()]);
        Arrays.sort(astitcher$holder);

        for (final Stitcher.Holder stitcher$holder : astitcher$holder) {
            if (!this.allocateSlot(stitcher$holder)) {
                final String s = String.format("Unable to fit: %s, size: %dx%d, atlas: %dx%d, atlasMax: %dx%d - Maybe try a lower resolution resourcepack?", stitcher$holder.getAtlasSprite().getIconName(), Integer.valueOf(stitcher$holder.getAtlasSprite().getIconWidth()), Integer.valueOf(stitcher$holder.getAtlasSprite().getIconHeight()), Integer.valueOf(this.currentWidth), Integer.valueOf(this.currentHeight), Integer.valueOf(this.maxWidth), Integer.valueOf(this.maxHeight));
                throw new StitcherException(stitcher$holder, s);
            }
        }

        if (this.forcePowerOf2) {
            this.currentWidth = MathHelper.roundUpToPowerOfTwo(this.currentWidth);
            this.currentHeight = MathHelper.roundUpToPowerOfTwo(this.currentHeight);
        }
    }

    public List<TextureAtlasSprite> getStichSlots() {
        final List<Stitcher.Slot> list = Lists.newArrayList();

        for (final Stitcher.Slot stitcher$slot : this.stitchSlots) {
            stitcher$slot.getAllStitchSlots(list);
        }

        final List<TextureAtlasSprite> list1 = Lists.newArrayList();

        for (final Stitcher.Slot stitcher$slot1 : list) {
            final Stitcher.Holder stitcher$holder = stitcher$slot1.getStitchHolder();
            final TextureAtlasSprite textureatlassprite = stitcher$holder.getAtlasSprite();
            textureatlassprite.initSprite(this.currentWidth, this.currentHeight, stitcher$slot1.getOriginX(), stitcher$slot1.getOriginY(), stitcher$holder.isRotated());
            list1.add(textureatlassprite);
        }

        return list1;
    }

    private static int getMipmapDimension(final int p_147969_0_, final int p_147969_1_) {
        return (p_147969_0_ >> p_147969_1_) + ((p_147969_0_ & (1 << p_147969_1_) - 1) == 0 ? 0 : 1) << p_147969_1_;
    }

    /**
     * Attempts to find space for specified tile
     */
    private boolean allocateSlot(final Stitcher.Holder p_94310_1_) {
        for (int i = 0; i < this.stitchSlots.size(); ++i) {
            if (this.stitchSlots.get(i).addSlot(p_94310_1_)) {
                return true;
            }

            p_94310_1_.rotate();

            if (this.stitchSlots.get(i).addSlot(p_94310_1_)) {
                return true;
            }

            p_94310_1_.rotate();
        }

        return this.expandAndAllocateSlot(p_94310_1_);
    }

    /**
     * Expand stitched texture in order to make space for specified tile
     */
    private boolean expandAndAllocateSlot(final Stitcher.Holder p_94311_1_) {
        final int i = Math.min(p_94311_1_.getWidth(), p_94311_1_.getHeight());
        final boolean flag = this.currentWidth == 0 && this.currentHeight == 0;
        final boolean flag1;

        if (this.forcePowerOf2) {
            final int j = MathHelper.roundUpToPowerOfTwo(this.currentWidth);
            final int k = MathHelper.roundUpToPowerOfTwo(this.currentHeight);
            final int l = MathHelper.roundUpToPowerOfTwo(this.currentWidth + i);
            final int i1 = MathHelper.roundUpToPowerOfTwo(this.currentHeight + i);
            final boolean flag2 = l <= this.maxWidth;
            final boolean flag3 = i1 <= this.maxHeight;

            if (!flag2 && !flag3) {
                return false;
            }

            final boolean flag4 = j != l;
            final boolean flag5 = k != i1;

            if (flag4 ^ flag5) {
                flag1 = !flag4;
            } else {
                flag1 = flag2 && j <= k;
            }
        } else {
            final boolean flag6 = this.currentWidth + i <= this.maxWidth;
            final boolean flag7 = this.currentHeight + i <= this.maxHeight;

            if (!flag6 && !flag7) {
                return false;
            }

            flag1 = flag6 && (flag || this.currentWidth <= this.currentHeight);
        }

        final int j1 = Math.max(p_94311_1_.getWidth(), p_94311_1_.getHeight());

        if (MathHelper.roundUpToPowerOfTwo((!flag1 ? this.currentHeight : this.currentWidth) + j1) > (!flag1 ? this.maxHeight : this.maxWidth)) {
            return false;
        } else {
            final Stitcher.Slot stitcher$slot;

            if (flag1) {
                if (p_94311_1_.getWidth() > p_94311_1_.getHeight()) {
                    p_94311_1_.rotate();
                }

                if (this.currentHeight == 0) {
                    this.currentHeight = p_94311_1_.getHeight();
                }

                stitcher$slot = new Stitcher.Slot(this.currentWidth, 0, p_94311_1_.getWidth(), this.currentHeight);
                this.currentWidth += p_94311_1_.getWidth();
            } else {
                stitcher$slot = new Stitcher.Slot(0, this.currentHeight, this.currentWidth, p_94311_1_.getHeight());
                this.currentHeight += p_94311_1_.getHeight();
            }

            stitcher$slot.addSlot(p_94311_1_);
            this.stitchSlots.add(stitcher$slot);
            return true;
        }
    }

    public static class Holder implements Comparable<Stitcher.Holder> {
        private final TextureAtlasSprite theTexture;
        private final int width;
        private final int height;
        private final int mipmapLevelHolder;
        private boolean rotated;
        private float scaleFactor = 1.0F;

        public Holder(final TextureAtlasSprite p_i45094_1_, final int p_i45094_2_) {
            this.theTexture = p_i45094_1_;
            this.width = p_i45094_1_.getIconWidth();
            this.height = p_i45094_1_.getIconHeight();
            this.mipmapLevelHolder = p_i45094_2_;
            this.rotated = Stitcher.getMipmapDimension(this.height, p_i45094_2_) > Stitcher.getMipmapDimension(this.width, p_i45094_2_);
        }

        public TextureAtlasSprite getAtlasSprite() {
            return this.theTexture;
        }

        public int getWidth() {
            return this.rotated ? Stitcher.getMipmapDimension((int) ((float) this.height * this.scaleFactor), this.mipmapLevelHolder) : Stitcher.getMipmapDimension((int) ((float) this.width * this.scaleFactor), this.mipmapLevelHolder);
        }

        public int getHeight() {
            return this.rotated ? Stitcher.getMipmapDimension((int) ((float) this.width * this.scaleFactor), this.mipmapLevelHolder) : Stitcher.getMipmapDimension((int) ((float) this.height * this.scaleFactor), this.mipmapLevelHolder);
        }

        public void rotate() {
            this.rotated = !this.rotated;
        }

        public boolean isRotated() {
            return this.rotated;
        }

        public void setNewDimension(final int p_94196_1_) {
            if (this.width > p_94196_1_ && this.height > p_94196_1_) {
                this.scaleFactor = (float) p_94196_1_ / (float) Math.min(this.width, this.height);
            }
        }

        public String toString() {
            return "Holder{width=" + this.width + ", height=" + this.height + '}';
        }

        public int compareTo(final Stitcher.Holder p_compareTo_1_) {
            final int i;

            if (this.getHeight() == p_compareTo_1_.getHeight()) {
                if (this.getWidth() == p_compareTo_1_.getWidth()) {
                    if (this.theTexture.getIconName() == null) {
                        return p_compareTo_1_.theTexture.getIconName() == null ? 0 : -1;
                    }

                    return this.theTexture.getIconName().compareTo(p_compareTo_1_.theTexture.getIconName());
                }

                i = this.getWidth() < p_compareTo_1_.getWidth() ? 1 : -1;
            } else {
                i = this.getHeight() < p_compareTo_1_.getHeight() ? 1 : -1;
            }

            return i;
        }
    }

    public static class Slot {
        private final int originX;
        private final int originY;
        private final int width;
        private final int height;
        private List<Stitcher.Slot> subSlots;
        private Stitcher.Holder holder;

        public Slot(final int p_i1277_1_, final int p_i1277_2_, final int widthIn, final int heightIn) {
            this.originX = p_i1277_1_;
            this.originY = p_i1277_2_;
            this.width = widthIn;
            this.height = heightIn;
        }

        public Stitcher.Holder getStitchHolder() {
            return this.holder;
        }

        public int getOriginX() {
            return this.originX;
        }

        public int getOriginY() {
            return this.originY;
        }

        public boolean addSlot(final Stitcher.Holder holderIn) {
            if (this.holder != null) {
                return false;
            } else {
                final int i = holderIn.getWidth();
                final int j = holderIn.getHeight();

                if (i <= this.width && j <= this.height) {
                    if (i == this.width && j == this.height) {
                        this.holder = holderIn;
                        return true;
                    } else {
                        if (this.subSlots == null) {
                            this.subSlots = Lists.newArrayListWithCapacity(1);
                            this.subSlots.add(new Stitcher.Slot(this.originX, this.originY, i, j));
                            final int k = this.width - i;
                            final int l = this.height - j;

                            if (l > 0 && k > 0) {
                                final int i1 = Math.max(this.height, k);
                                final int j1 = Math.max(this.width, l);

                                if (i1 >= j1) {
                                    this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, i, l));
                                    this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, this.height));
                                } else {
                                    this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, j));
                                    this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, this.width, l));
                                }
                            } else if (k == 0) {
                                this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, i, l));
                            } else if (l == 0) {
                                this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, j));
                            }
                        }

                        for (final Stitcher.Slot stitcher$slot : this.subSlots) {
                            if (stitcher$slot.addSlot(holderIn)) {
                                return true;
                            }
                        }

                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        public void getAllStitchSlots(final List<Stitcher.Slot> p_94184_1_) {
            if (this.holder != null) {
                p_94184_1_.add(this);
            } else if (this.subSlots != null) {
                for (final Stitcher.Slot stitcher$slot : this.subSlots) {
                    stitcher$slot.getAllStitchSlots(p_94184_1_);
                }
            }
        }

        public String toString() {
            return "Slot{originX=" + this.originX + ", originY=" + this.originY + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.holder + ", subSlots=" + this.subSlots + '}';
        }
    }
}
