// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.client.gui.MinecraftFontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.Block;
import java.util.Iterator;
import net.minecraft.world.World;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.entity.EntityLiving;
import net.optifine.shaders.Shaders;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.src.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.optifine.entity.model.IEntityRenderer;
import net.minecraft.entity.Entity;

public abstract class Render<T extends Entity> implements IEntityRenderer
{
    private static final ResourceLocation shadowTextures;
    protected final RenderManager renderManager;
    public float shadowSize;
    protected float shadowOpaque;
    private Class entityClass;
    private ResourceLocation locationTextureCustom;
    
    static {
        shadowTextures = new ResourceLocation("textures/misc/shadow.png");
    }
    
    protected Render(final RenderManager renderManager) {
        this.shadowOpaque = 1.0f;
        this.entityClass = null;
        this.locationTextureCustom = null;
        this.renderManager = renderManager;
    }
    
    public static void renderOffsetAABB(final AxisAlignedBB boundingBox, final double x, final double y, final double z) {
        GlStateManager.disableTexture2D();
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        worldrenderer.setTranslation(x, y, z);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_NORMAL);
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).func_181663_c(0.0f, 0.0f, -1.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).func_181663_c(0.0f, 0.0f, -1.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).func_181663_c(0.0f, 0.0f, -1.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).func_181663_c(0.0f, 0.0f, -1.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).func_181663_c(0.0f, 0.0f, 1.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).func_181663_c(0.0f, 0.0f, 1.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).func_181663_c(0.0f, 0.0f, 1.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).func_181663_c(0.0f, 0.0f, 1.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).func_181663_c(0.0f, -1.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).func_181663_c(0.0f, -1.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).func_181663_c(0.0f, -1.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).func_181663_c(0.0f, -1.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).func_181663_c(0.0f, 1.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).func_181663_c(0.0f, 1.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).func_181663_c(0.0f, 1.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).func_181663_c(0.0f, 1.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).func_181663_c(-1.0f, 0.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).func_181663_c(-1.0f, 0.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).func_181663_c(-1.0f, 0.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).func_181663_c(-1.0f, 0.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).func_181663_c(1.0f, 0.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).func_181663_c(1.0f, 0.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).func_181663_c(1.0f, 0.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).func_181663_c(1.0f, 0.0f, 0.0f).endVertex();
        tessellator.draw();
        worldrenderer.setTranslation(0.0, 0.0, 0.0);
        GlStateManager.enableTexture2D();
    }
    
    public static void setModelBipedMain(final RenderBiped p_setModelBipedMain_0_, final ModelBiped p_setModelBipedMain_1_) {
        p_setModelBipedMain_0_.modelBipedMain = p_setModelBipedMain_1_;
    }
    
    public boolean shouldRender(final T livingEntity, final ICamera camera, final double camX, final double camY, final double camZ) {
        AxisAlignedBB axisalignedbb = livingEntity.getEntityBoundingBox();
        if (axisalignedbb.func_181656_b() || axisalignedbb.getAverageEdgeLength() == 0.0) {
            axisalignedbb = new AxisAlignedBB(livingEntity.posX - 2.0, livingEntity.posY - 2.0, livingEntity.posZ - 2.0, livingEntity.posX + 2.0, livingEntity.posY + 2.0, livingEntity.posZ + 2.0);
        }
        return livingEntity.isInRangeToRender3d(camX, camY, camZ) && (livingEntity.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(axisalignedbb));
    }
    
    public void doRender(final T entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        this.renderName(entity, x, y, z);
    }
    
    protected void renderName(final T entity, final double x, final double y, final double z) {
        if (this.canRenderName(entity)) {
            this.renderLivingLabel(entity, entity.getDisplayName().getFormattedText(), x, y, z, 64);
        }
    }
    
    protected boolean canRenderName(final T entity) {
        return entity.getAlwaysRenderNameTagForRender() && entity.hasCustomName();
    }
    
    protected void renderOffsetLivingLabel(final T entityIn, final double x, final double y, final double z, final String str, final float p_177069_9_, final double p_177069_10_) {
        this.renderLivingLabel(entityIn, str, x, y, z, 64);
    }
    
    protected abstract ResourceLocation getEntityTexture(final T p0);
    
    protected boolean bindEntityTexture(final T entity) {
        ResourceLocation resourcelocation = this.getEntityTexture(entity);
        if (this.locationTextureCustom != null) {
            resourcelocation = this.locationTextureCustom;
        }
        if (resourcelocation == null) {
            return false;
        }
        this.bindTexture(resourcelocation);
        return true;
    }
    
    public void bindTexture(final ResourceLocation location) {
        this.renderManager.renderEngine.bindTexture(location);
    }
    
    private void renderEntityOnFire(final Entity entity, final double x, final double y, final double z, final float partialTicks) {
        GlStateManager.disableLighting();
        final TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
        final TextureAtlasSprite textureatlassprite = texturemap.getAtlasSprite("minecraft:blocks/fire_layer_0");
        final TextureAtlasSprite textureatlassprite2 = texturemap.getAtlasSprite("minecraft:blocks/fire_layer_1");
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        final float f = entity.width * 1.4f;
        GL11.glScalef(f, f, f);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        float f2 = 0.5f;
        final float f3 = 0.0f;
        float f4 = entity.height / f;
        float f5 = (float)(entity.posY - entity.getEntityBoundingBox().minY);
        GL11.glRotatef(-this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(0.0f, 0.0f, -0.3f + (int)f4 * 0.02f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f6 = 0.0f;
        int i = 0;
        final boolean flag = Config.isMultiTexture();
        if (flag) {
            worldrenderer.setBlockLayer(EnumWorldBlockLayer.SOLID);
        }
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        while (f4 > 0.0f) {
            final TextureAtlasSprite textureatlassprite3 = (i % 2 == 0) ? textureatlassprite : textureatlassprite2;
            worldrenderer.setSprite(textureatlassprite3);
            this.bindTexture(TextureMap.locationBlocksTexture);
            float f7 = textureatlassprite3.getMinU();
            final float f8 = textureatlassprite3.getMinV();
            float f9 = textureatlassprite3.getMaxU();
            final float f10 = textureatlassprite3.getMaxV();
            if (i / 2 % 2 == 0) {
                final float f11 = f9;
                f9 = f7;
                f7 = f11;
            }
            worldrenderer.pos(f2 - f3, 0.0f - f5, f6).tex(f9, f10).endVertex();
            worldrenderer.pos(-f2 - f3, 0.0f - f5, f6).tex(f7, f10).endVertex();
            worldrenderer.pos(-f2 - f3, 1.4f - f5, f6).tex(f7, f8).endVertex();
            worldrenderer.pos(f2 - f3, 1.4f - f5, f6).tex(f9, f8).endVertex();
            f4 -= 0.45f;
            f5 -= 0.45f;
            f2 *= 0.9f;
            f6 += 0.03f;
            ++i;
        }
        tessellator.draw();
        if (flag) {
            worldrenderer.setBlockLayer(null);
            GlStateManager.bindCurrentTexture();
        }
        GL11.glPopMatrix();
        GlStateManager.enableLighting();
    }
    
    private void renderShadow(final Entity entityIn, final double x, final double y, final double z, final float shadowAlpha, final float partialTicks) {
        if (!Config.isShaders() || !Shaders.shouldSkipDefaultShadow) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            this.renderManager.renderEngine.bindTexture(Render.shadowTextures);
            final World world = this.getWorldFromRenderManager();
            GlStateManager.depthMask(false);
            float f = this.shadowSize;
            if (entityIn instanceof EntityLiving) {
                final EntityLiving entityliving = (EntityLiving)entityIn;
                f *= entityliving.getRenderSizeModifier();
                if (entityliving.isChild()) {
                    f *= 0.5f;
                }
            }
            final double d5 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
            final double d6 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
            final double d7 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
            final int i = MathHelper.floor_double(d5 - f);
            final int j = MathHelper.floor_double(d5 + f);
            final int k = MathHelper.floor_double(d6 - f);
            final int l = MathHelper.floor_double(d6);
            final int i2 = MathHelper.floor_double(d7 - f);
            final int j2 = MathHelper.floor_double(d7 + f);
            final double d8 = x - d5;
            final double d9 = y - d6;
            final double d10 = z - d7;
            final Tessellator tessellator = Tessellator.getInstance();
            final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            for (final BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(i, k, i2), new BlockPos(j, l, j2))) {
                final Block block = world.getBlockState(blockpos.down()).getBlock();
                if (block.getRenderType() != -1 && world.getLightFromNeighbors(blockpos) > 3) {
                    this.func_180549_a(block, x, y, z, blockpos, shadowAlpha, f, d8, d9, d10);
                }
            }
            tessellator.draw();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
        }
    }
    
    private World getWorldFromRenderManager() {
        return this.renderManager.worldObj;
    }
    
    private void func_180549_a(final Block blockIn, final double p_180549_2_, final double p_180549_4_, final double p_180549_6_, final BlockPos pos, final float p_180549_9_, final float p_180549_10_, final double p_180549_11_, final double p_180549_13_, final double p_180549_15_) {
        if (blockIn.isFullCube()) {
            final Tessellator tessellator = Tessellator.getInstance();
            final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            double d0 = (p_180549_9_ - (p_180549_4_ - (pos.getY() + p_180549_13_)) / 2.0) * 0.5 * this.getWorldFromRenderManager().getLightBrightness(pos);
            if (d0 >= 0.0) {
                if (d0 > 1.0) {
                    d0 = 1.0;
                }
                final double d2 = pos.getX() + blockIn.getBlockBoundsMinX() + p_180549_11_;
                final double d3 = pos.getX() + blockIn.getBlockBoundsMaxX() + p_180549_11_;
                final double d4 = pos.getY() + blockIn.getBlockBoundsMinY() + p_180549_13_ + 0.015625;
                final double d5 = pos.getZ() + blockIn.getBlockBoundsMinZ() + p_180549_15_;
                final double d6 = pos.getZ() + blockIn.getBlockBoundsMaxZ() + p_180549_15_;
                final float f = (float)((p_180549_2_ - d2) / 2.0 / p_180549_10_ + 0.5);
                final float f2 = (float)((p_180549_2_ - d3) / 2.0 / p_180549_10_ + 0.5);
                final float f3 = (float)((p_180549_6_ - d5) / 2.0 / p_180549_10_ + 0.5);
                final float f4 = (float)((p_180549_6_ - d6) / 2.0 / p_180549_10_ + 0.5);
                worldrenderer.pos(d2, d4, d5).tex(f, f3).color4f(1.0f, 1.0f, 1.0f, (float)d0).endVertex();
                worldrenderer.pos(d2, d4, d6).tex(f, f4).color4f(1.0f, 1.0f, 1.0f, (float)d0).endVertex();
                worldrenderer.pos(d3, d4, d6).tex(f2, f4).color4f(1.0f, 1.0f, 1.0f, (float)d0).endVertex();
                worldrenderer.pos(d3, d4, d5).tex(f2, f3).color4f(1.0f, 1.0f, 1.0f, (float)d0).endVertex();
            }
        }
    }
    
    public void doRenderShadowAndFire(final Entity entityIn, final double x, final double y, final double z, final float yaw, final float partialTicks) {
        if (this.renderManager.options != null) {
            if (this.renderManager.options.field_181151_V && this.shadowSize > 0.0f && !entityIn.isInvisible() && this.renderManager.isRenderShadow()) {
                final double d0 = this.renderManager.getDistanceToCamera(entityIn.posX, entityIn.posY, entityIn.posZ);
                final float f = (float)((1.0 - d0 / 256.0) * this.shadowOpaque);
                if (f > 0.0f) {
                    this.renderShadow(entityIn, x, y, z, f, partialTicks);
                }
            }
            if (entityIn.canRenderOnFire() && (!(entityIn instanceof EntityPlayer) || !((EntityPlayer)entityIn).isSpectator())) {
                this.renderEntityOnFire(entityIn, x, y, z, partialTicks);
            }
        }
    }
    
    public MinecraftFontRenderer getFontRendererFromRenderManager() {
        return this.renderManager.getFontRenderer();
    }
    
    protected void renderLivingLabel(final T entityIn, final String str, final double x, final double y, final double z, final int maxDistance) {
        final double d0 = entityIn.getDistanceSqToEntity(this.renderManager.livingPlayer);
        if (d0 <= maxDistance * maxDistance) {
            final MinecraftFontRenderer fontrenderer = this.getFontRendererFromRenderManager();
            final float f = 1.6f;
            final float f2 = 0.016666668f * f;
            GL11.glPushMatrix();
            GL11.glTranslatef((float)x + 0.0f, (float)y + entityIn.height + 0.5f, (float)z);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GL11.glRotatef(-this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
            GL11.glScalef(-f2, -f2, f2);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            final Tessellator tessellator = Tessellator.getInstance();
            final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            final int i = 0;
            final int j = fontrenderer.getStringWidth(str) / 2;
            GlStateManager.disableTexture2D();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos(-j - 1, -1 + i, 0.0).color4f(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
            worldrenderer.pos(-j - 1, 8 + i, 0.0).color4f(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
            worldrenderer.pos(j + 1, 8 + i, 0.0).color4f(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
            worldrenderer.pos(j + 1, -1 + i, 0.0).color4f(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
            fontrenderer.drawString(str, (float)(-fontrenderer.getStringWidth(str) / 2), (float)i, 553648127);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            fontrenderer.drawString(str, (float)(-fontrenderer.getStringWidth(str) / 2), (float)i, -1);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
    }
    
    public RenderManager getRenderManager() {
        return this.renderManager;
    }
    
    public boolean isMultipass() {
        return false;
    }
    
    public void renderMultipass(final T p_renderMultipass_1_, final double p_renderMultipass_2_, final double p_renderMultipass_4_, final double p_renderMultipass_6_, final float p_renderMultipass_8_, final float p_renderMultipass_9_) {
    }
    
    @Override
    public Class getEntityClass() {
        return this.entityClass;
    }
    
    @Override
    public void setEntityClass(final Class p_setEntityClass_1_) {
        this.entityClass = p_setEntityClass_1_;
    }
    
    @Override
    public ResourceLocation getLocationTextureCustom() {
        return this.locationTextureCustom;
    }
    
    @Override
    public void setLocationTextureCustom(final ResourceLocation p_setLocationTextureCustom_1_) {
        this.locationTextureCustom = p_setLocationTextureCustom_1_;
    }
}
