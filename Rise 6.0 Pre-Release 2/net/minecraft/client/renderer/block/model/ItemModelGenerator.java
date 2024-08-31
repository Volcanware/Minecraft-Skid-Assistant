package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;
import java.util.Map;

public class ItemModelGenerator {
    public static final List<String> LAYERS = Lists.newArrayList("layer0", "layer1", "layer2", "layer3", "layer4");

    public ModelBlock makeItemModel(final TextureMap textureMapIn, final ModelBlock blockModel) {
        final Map<String, String> map = Maps.newHashMap();
        final List<BlockPart> list = Lists.newArrayList();

        for (int i = 0; i < LAYERS.size(); ++i) {
            final String s = LAYERS.get(i);

            if (!blockModel.isTexturePresent(s)) {
                break;
            }

            final String s1 = blockModel.resolveTextureName(s);
            map.put(s, s1);
            final TextureAtlasSprite textureatlassprite = textureMapIn.getAtlasSprite((new ResourceLocation(s1)).toString());
            list.addAll(this.func_178394_a(i, s, textureatlassprite));
        }

        if (list.isEmpty()) {
            return null;
        } else {
            map.put("particle", blockModel.isTexturePresent("particle") ? blockModel.resolveTextureName("particle") : map.get("layer0"));
            return new ModelBlock(list, map, false, false, blockModel.func_181682_g());
        }
    }

    private List<BlockPart> func_178394_a(final int p_178394_1_, final String p_178394_2_, final TextureAtlasSprite p_178394_3_) {
        final Map<EnumFacing, BlockPartFace> map = Maps.newHashMap();
        map.put(EnumFacing.SOUTH, new BlockPartFace(null, p_178394_1_, p_178394_2_, new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
        map.put(EnumFacing.NORTH, new BlockPartFace(null, p_178394_1_, p_178394_2_, new BlockFaceUV(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
        final List<BlockPart> list = Lists.newArrayList();
        list.add(new BlockPart(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), map, null, true));
        list.addAll(this.func_178397_a(p_178394_3_, p_178394_2_, p_178394_1_));
        return list;
    }

    private List<BlockPart> func_178397_a(final TextureAtlasSprite p_178397_1_, final String p_178397_2_, final int p_178397_3_) {
        final float f = (float) p_178397_1_.getIconWidth();
        final float f1 = (float) p_178397_1_.getIconHeight();
        final List<BlockPart> list = Lists.newArrayList();

        for (final ItemModelGenerator.Span itemmodelgenerator$span : this.func_178393_a(p_178397_1_)) {
            float f2 = 0.0F;
            float f3 = 0.0F;
            float f4 = 0.0F;
            float f5 = 0.0F;
            float f6 = 0.0F;
            float f7 = 0.0F;
            float f8 = 0.0F;
            float f9 = 0.0F;
            float f10 = 0.0F;
            float f11 = 0.0F;
            final float f12 = (float) itemmodelgenerator$span.func_178385_b();
            final float f13 = (float) itemmodelgenerator$span.func_178384_c();
            final float f14 = (float) itemmodelgenerator$span.func_178381_d();
            final ItemModelGenerator.SpanFacing itemmodelgenerator$spanfacing = itemmodelgenerator$span.func_178383_a();

            switch (itemmodelgenerator$spanfacing) {
                case UP:
                    f6 = f12;
                    f2 = f12;
                    f4 = f7 = f13 + 1.0F;
                    f8 = f14;
                    f3 = f14;
                    f9 = f14;
                    f5 = f14;
                    f10 = 16.0F / f;
                    f11 = 16.0F / (f1 - 1.0F);
                    break;

                case DOWN:
                    f9 = f14;
                    f8 = f14;
                    f6 = f12;
                    f2 = f12;
                    f4 = f7 = f13 + 1.0F;
                    f3 = f14 + 1.0F;
                    f5 = f14 + 1.0F;
                    f10 = 16.0F / f;
                    f11 = 16.0F / (f1 - 1.0F);
                    break;

                case LEFT:
                    f6 = f14;
                    f2 = f14;
                    f7 = f14;
                    f4 = f14;
                    f9 = f12;
                    f3 = f12;
                    f5 = f8 = f13 + 1.0F;
                    f10 = 16.0F / (f - 1.0F);
                    f11 = 16.0F / f1;
                    break;

                case RIGHT:
                    f7 = f14;
                    f6 = f14;
                    f2 = f14 + 1.0F;
                    f4 = f14 + 1.0F;
                    f9 = f12;
                    f3 = f12;
                    f5 = f8 = f13 + 1.0F;
                    f10 = 16.0F / (f - 1.0F);
                    f11 = 16.0F / f1;
            }

            final float f15 = 16.0F / f;
            final float f16 = 16.0F / f1;
            f2 = f2 * f15;
            f4 = f4 * f15;
            f3 = f3 * f16;
            f5 = f5 * f16;
            f3 = 16.0F - f3;
            f5 = 16.0F - f5;
            f6 = f6 * f10;
            f7 = f7 * f10;
            f8 = f8 * f11;
            f9 = f9 * f11;
            final Map<EnumFacing, BlockPartFace> map = Maps.newHashMap();
            map.put(itemmodelgenerator$spanfacing.getFacing(), new BlockPartFace(null, p_178397_3_, p_178397_2_, new BlockFaceUV(new float[]{f6, f8, f7, f9}, 0)));

            switch (itemmodelgenerator$spanfacing) {
                case UP:
                    list.add(new BlockPart(new Vector3f(f2, f3, 7.5F), new Vector3f(f4, f3, 8.5F), map, null, true));
                    break;

                case DOWN:
                    list.add(new BlockPart(new Vector3f(f2, f5, 7.5F), new Vector3f(f4, f5, 8.5F), map, null, true));
                    break;

                case LEFT:
                    list.add(new BlockPart(new Vector3f(f2, f3, 7.5F), new Vector3f(f2, f5, 8.5F), map, null, true));
                    break;

                case RIGHT:
                    list.add(new BlockPart(new Vector3f(f4, f3, 7.5F), new Vector3f(f4, f5, 8.5F), map, null, true));
            }
        }

        return list;
    }

    private List<ItemModelGenerator.Span> func_178393_a(final TextureAtlasSprite p_178393_1_) {
        final int i = p_178393_1_.getIconWidth();
        final int j = p_178393_1_.getIconHeight();
        final List<ItemModelGenerator.Span> list = Lists.newArrayList();

        for (int k = 0; k < p_178393_1_.getFrameCount(); ++k) {
            final int[] aint = p_178393_1_.getFrameTextureData(k)[0];

            for (int l = 0; l < j; ++l) {
                for (int i1 = 0; i1 < i; ++i1) {
                    final boolean flag = !this.func_178391_a(aint, i1, l, i, j);
                    this.func_178396_a(ItemModelGenerator.SpanFacing.UP, list, aint, i1, l, i, j, flag);
                    this.func_178396_a(ItemModelGenerator.SpanFacing.DOWN, list, aint, i1, l, i, j, flag);
                    this.func_178396_a(ItemModelGenerator.SpanFacing.LEFT, list, aint, i1, l, i, j, flag);
                    this.func_178396_a(ItemModelGenerator.SpanFacing.RIGHT, list, aint, i1, l, i, j, flag);
                }
            }
        }

        return list;
    }

    private void func_178396_a(final ItemModelGenerator.SpanFacing p_178396_1_, final List<ItemModelGenerator.Span> p_178396_2_, final int[] p_178396_3_, final int p_178396_4_, final int p_178396_5_, final int p_178396_6_, final int p_178396_7_, final boolean p_178396_8_) {
        final boolean flag = this.func_178391_a(p_178396_3_, p_178396_4_ + p_178396_1_.func_178372_b(), p_178396_5_ + p_178396_1_.func_178371_c(), p_178396_6_, p_178396_7_) && p_178396_8_;

        if (flag) {
            this.func_178395_a(p_178396_2_, p_178396_1_, p_178396_4_, p_178396_5_);
        }
    }

    private void func_178395_a(final List<ItemModelGenerator.Span> p_178395_1_, final ItemModelGenerator.SpanFacing p_178395_2_, final int p_178395_3_, final int p_178395_4_) {
        ItemModelGenerator.Span itemmodelgenerator$span = null;

        for (final ItemModelGenerator.Span itemmodelgenerator$span1 : p_178395_1_) {
            if (itemmodelgenerator$span1.func_178383_a() == p_178395_2_) {
                final int i = p_178395_2_.func_178369_d() ? p_178395_4_ : p_178395_3_;

                if (itemmodelgenerator$span1.func_178381_d() == i) {
                    itemmodelgenerator$span = itemmodelgenerator$span1;
                    break;
                }
            }
        }

        final int j = p_178395_2_.func_178369_d() ? p_178395_4_ : p_178395_3_;
        final int k = p_178395_2_.func_178369_d() ? p_178395_3_ : p_178395_4_;

        if (itemmodelgenerator$span == null) {
            p_178395_1_.add(new ItemModelGenerator.Span(p_178395_2_, k, j));
        } else {
            itemmodelgenerator$span.func_178382_a(k);
        }
    }

    private boolean func_178391_a(final int[] p_178391_1_, final int p_178391_2_, final int p_178391_3_, final int p_178391_4_, final int p_178391_5_) {
        return p_178391_2_ < 0 || p_178391_3_ < 0 || p_178391_2_ >= p_178391_4_ || p_178391_3_ >= p_178391_5_ || (p_178391_1_[p_178391_3_ * p_178391_4_ + p_178391_2_] >> 24 & 255) == 0;
    }

    static class Span {
        private final ItemModelGenerator.SpanFacing spanFacing;
        private int field_178387_b;
        private int field_178388_c;
        private final int field_178386_d;

        public Span(final ItemModelGenerator.SpanFacing spanFacingIn, final int p_i46216_2_, final int p_i46216_3_) {
            this.spanFacing = spanFacingIn;
            this.field_178387_b = p_i46216_2_;
            this.field_178388_c = p_i46216_2_;
            this.field_178386_d = p_i46216_3_;
        }

        public void func_178382_a(final int p_178382_1_) {
            if (p_178382_1_ < this.field_178387_b) {
                this.field_178387_b = p_178382_1_;
            } else if (p_178382_1_ > this.field_178388_c) {
                this.field_178388_c = p_178382_1_;
            }
        }

        public ItemModelGenerator.SpanFacing func_178383_a() {
            return this.spanFacing;
        }

        public int func_178385_b() {
            return this.field_178387_b;
        }

        public int func_178384_c() {
            return this.field_178388_c;
        }

        public int func_178381_d() {
            return this.field_178386_d;
        }
    }

    enum SpanFacing {
        UP(EnumFacing.UP, 0, -1),
        DOWN(EnumFacing.DOWN, 0, 1),
        LEFT(EnumFacing.EAST, -1, 0),
        RIGHT(EnumFacing.WEST, 1, 0);

        private final EnumFacing facing;
        private final int field_178373_f;
        private final int field_178374_g;

        SpanFacing(final EnumFacing facing, final int p_i46215_4_, final int p_i46215_5_) {
            this.facing = facing;
            this.field_178373_f = p_i46215_4_;
            this.field_178374_g = p_i46215_5_;
        }

        public EnumFacing getFacing() {
            return this.facing;
        }

        public int func_178372_b() {
            return this.field_178373_f;
        }

        public int func_178371_c() {
            return this.field_178374_g;
        }

        private boolean func_178369_d() {
            return this == DOWN || this == UP;
        }
    }
}
