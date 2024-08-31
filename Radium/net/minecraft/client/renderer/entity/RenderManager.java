// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.client.renderer.entity;

import java.util.Collections;
import net.minecraft.util.Vec3;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.client.renderer.GlStateManager;
import net.optifine.entity.model.CustomEntityModels;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.BlockBed;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockPos;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.optifine.reflect.Reflector;
import net.optifine.player.PlayerItemsLayer;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.client.renderer.tileentity.RenderWitherSkull;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.init.Items;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.client.model.ModelRabbit;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.client.model.ModelCow;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.client.model.ModelSheep2;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPig;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityCaveSpider;
import com.google.common.collect.Maps;
import net.minecraft.client.gui.MinecraftFontRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.client.renderer.texture.TextureManager;
import java.util.Map;

public class RenderManager
{
    public static double renderPosX;
    public static double renderPosY;
    public static double renderPosZ;
    public static double viewerPosX;
    public static double viewerPosY;
    public static double viewerPosZ;
    private final Map<String, RenderPlayer> skinMap;
    private final RenderPlayer playerRenderer;
    public TextureManager renderEngine;
    public World worldObj;
    public Entity livingPlayer;
    public Entity pointedEntity;
    public float playerViewY;
    public float playerViewX;
    public GameSettings options;
    public Render renderRender;
    private Map<Class, Render> entityRenderMap;
    private MinecraftFontRenderer textRenderer;
    private boolean renderOutlines;
    private boolean renderShadow;
    private boolean debugBoundingBox;
    
    public RenderManager(final TextureManager renderEngineIn, final RenderItem itemRendererIn) {
        this.skinMap = (Map<String, RenderPlayer>)Maps.newHashMap();
        this.renderRender = null;
        this.entityRenderMap = (Map<Class, Render>)Maps.newHashMap();
        this.renderOutlines = false;
        this.renderShadow = true;
        this.debugBoundingBox = false;
        this.renderEngine = renderEngineIn;
        this.entityRenderMap.put(EntityCaveSpider.class, new RenderCaveSpider(this));
        this.entityRenderMap.put(EntitySpider.class, new RenderSpider(this));
        this.entityRenderMap.put(EntityPig.class, new RenderPig(this, new ModelPig(), 0.7f));
        this.entityRenderMap.put(EntitySheep.class, new RenderSheep(this, new ModelSheep2(), 0.7f));
        this.entityRenderMap.put(EntityCow.class, new RenderCow(this, new ModelCow(), 0.7f));
        this.entityRenderMap.put(EntityMooshroom.class, new RenderMooshroom(this, new ModelCow(), 0.7f));
        this.entityRenderMap.put(EntityWolf.class, new RenderWolf(this, new ModelWolf(), 0.5f));
        this.entityRenderMap.put(EntityChicken.class, new RenderChicken(this, new ModelChicken(), 0.3f));
        this.entityRenderMap.put(EntityOcelot.class, new RenderOcelot(this, new ModelOcelot(), 0.4f));
        this.entityRenderMap.put(EntityRabbit.class, new RenderRabbit(this, new ModelRabbit(), 0.3f));
        this.entityRenderMap.put(EntitySilverfish.class, new RenderSilverfish(this));
        this.entityRenderMap.put(EntityEndermite.class, new RenderEndermite(this));
        this.entityRenderMap.put(EntityCreeper.class, new RenderCreeper(this));
        this.entityRenderMap.put(EntityEnderman.class, new RenderEnderman(this));
        this.entityRenderMap.put(EntitySnowman.class, new RenderSnowMan(this));
        this.entityRenderMap.put(EntitySkeleton.class, new RenderSkeleton(this));
        this.entityRenderMap.put(EntityWitch.class, new RenderWitch(this));
        this.entityRenderMap.put(EntityBlaze.class, new RenderBlaze(this));
        this.entityRenderMap.put(EntityPigZombie.class, new RenderPigZombie(this));
        this.entityRenderMap.put(EntityZombie.class, new RenderZombie(this));
        this.entityRenderMap.put(EntitySlime.class, new RenderSlime(this, new ModelSlime(16), 0.25f));
        this.entityRenderMap.put(EntityMagmaCube.class, new RenderMagmaCube(this));
        this.entityRenderMap.put(EntityGiantZombie.class, new RenderGiantZombie(this, new ModelZombie(), 0.5f, 6.0f));
        this.entityRenderMap.put(EntityGhast.class, new RenderGhast(this));
        this.entityRenderMap.put(EntitySquid.class, new RenderSquid(this, new ModelSquid(), 0.7f));
        this.entityRenderMap.put(EntityVillager.class, new RenderVillager(this));
        this.entityRenderMap.put(EntityIronGolem.class, new RenderIronGolem(this));
        this.entityRenderMap.put(EntityBat.class, new RenderBat(this));
        this.entityRenderMap.put(EntityGuardian.class, new RenderGuardian(this));
        this.entityRenderMap.put(EntityDragon.class, new RenderDragon(this));
        this.entityRenderMap.put(EntityEnderCrystal.class, new RenderEnderCrystal(this));
        this.entityRenderMap.put(EntityWither.class, new RenderWither(this));
        this.entityRenderMap.put(Entity.class, new RenderEntity(this));
        this.entityRenderMap.put(EntityPainting.class, new RenderPainting(this));
        this.entityRenderMap.put(EntityItemFrame.class, new RenderItemFrame(this, itemRendererIn));
        this.entityRenderMap.put(EntityLeashKnot.class, new RenderLeashKnot(this));
        this.entityRenderMap.put(EntityArrow.class, new RenderArrow(this));
        this.entityRenderMap.put(EntitySnowball.class, new RenderSnowball(this, Items.snowball, itemRendererIn));
        this.entityRenderMap.put(EntityEnderPearl.class, new RenderSnowball(this, Items.ender_pearl, itemRendererIn));
        this.entityRenderMap.put(EntityEnderEye.class, new RenderSnowball(this, Items.ender_eye, itemRendererIn));
        this.entityRenderMap.put(EntityEgg.class, new RenderSnowball(this, Items.egg, itemRendererIn));
        this.entityRenderMap.put(EntityPotion.class, new RenderPotion(this, itemRendererIn));
        this.entityRenderMap.put(EntityExpBottle.class, new RenderSnowball(this, Items.experience_bottle, itemRendererIn));
        this.entityRenderMap.put(EntityFireworkRocket.class, new RenderSnowball(this, Items.fireworks, itemRendererIn));
        this.entityRenderMap.put(EntityLargeFireball.class, new RenderFireball(this, 2.0f));
        this.entityRenderMap.put(EntitySmallFireball.class, new RenderFireball(this, 0.5f));
        this.entityRenderMap.put(EntityWitherSkull.class, new RenderWitherSkull(this));
        this.entityRenderMap.put(EntityItem.class, new RenderEntityItem(this, itemRendererIn));
        this.entityRenderMap.put(EntityXPOrb.class, new RenderXPOrb(this));
        this.entityRenderMap.put(EntityTNTPrimed.class, new RenderTNTPrimed(this));
        this.entityRenderMap.put(EntityFallingBlock.class, new RenderFallingBlock(this));
        this.entityRenderMap.put(EntityArmorStand.class, new ArmorStandRenderer(this));
        this.entityRenderMap.put(EntityMinecartTNT.class, new RenderTntMinecart(this));
        this.entityRenderMap.put(EntityMinecartMobSpawner.class, new RenderMinecartMobSpawner(this));
        this.entityRenderMap.put(EntityMinecart.class, new RenderMinecart(this));
        this.entityRenderMap.put(EntityBoat.class, new RenderBoat(this));
        this.entityRenderMap.put(EntityFishHook.class, new RenderFish(this));
        this.entityRenderMap.put(EntityHorse.class, new RenderHorse(this, new ModelHorse(), 0.75f));
        this.entityRenderMap.put(EntityLightningBolt.class, new RenderLightningBolt(this));
        this.playerRenderer = new RenderPlayer(this);
        this.skinMap.put("default", this.playerRenderer);
        this.skinMap.put("slim", new RenderPlayer(this, true));
        PlayerItemsLayer.register(this.skinMap);
        if (Reflector.RenderingRegistry_loadEntityRenderers.exists()) {
            Reflector.call(Reflector.RenderingRegistry_loadEntityRenderers, this, this.entityRenderMap);
        }
    }
    
    public void setRenderPosition(final double renderPosXIn, final double renderPosYIn, final double renderPosZIn) {
        RenderManager.renderPosX = renderPosXIn;
        RenderManager.renderPosY = renderPosYIn;
        RenderManager.renderPosZ = renderPosZIn;
    }
    
    public <T extends Entity> Render<T> getEntityClassRenderObject(final Class<? extends Entity> p_78715_1_) {
        Render<? extends Entity> render = this.entityRenderMap.get(p_78715_1_);
        if (render == null && p_78715_1_ != Entity.class) {
            render = this.getEntityClassRenderObject((Class<? extends Entity>)p_78715_1_.getSuperclass());
            this.entityRenderMap.put(p_78715_1_, render);
        }
        return (Render<T>)render;
    }
    
    public <T extends Entity> Render<T> getEntityRenderObject(final Entity entityIn) {
        if (entityIn instanceof AbstractClientPlayer) {
            final String s = ((AbstractClientPlayer)entityIn).getSkinType();
            final RenderPlayer renderplayer = this.skinMap.get(s);
            return (Render<T>)((renderplayer != null) ? renderplayer : this.playerRenderer);
        }
        return this.getEntityClassRenderObject(entityIn.getClass());
    }
    
    public void cacheActiveRenderInfo(final World worldIn, final MinecraftFontRenderer textRendererIn, final Entity livingPlayerIn, final Entity pointedEntityIn, final GameSettings optionsIn, final float partialTicks) {
        this.worldObj = worldIn;
        this.options = optionsIn;
        this.livingPlayer = livingPlayerIn;
        this.pointedEntity = pointedEntityIn;
        this.textRenderer = textRendererIn;
        if (livingPlayerIn instanceof EntityLivingBase && ((EntityLivingBase)livingPlayerIn).isPlayerSleeping()) {
            final IBlockState iblockstate = worldIn.getBlockState(new BlockPos(livingPlayerIn));
            final Block block = iblockstate.getBlock();
            if (Reflector.callBoolean(block, Reflector.ForgeBlock_isBed, iblockstate, worldIn, new BlockPos(livingPlayerIn), livingPlayerIn)) {
                final EnumFacing enumfacing = (EnumFacing)Reflector.call(block, Reflector.ForgeBlock_getBedDirection, iblockstate, worldIn, new BlockPos(livingPlayerIn));
                final int i = enumfacing.getHorizontalIndex();
                this.playerViewY = (float)(i * 90 + 180);
                this.playerViewX = 0.0f;
            }
            else if (block == Blocks.bed) {
                final int j = iblockstate.getValue((IProperty<EnumFacing>)BlockBed.FACING).getHorizontalIndex();
                this.playerViewY = (float)(j * 90 + 180);
                this.playerViewX = 0.0f;
            }
        }
        else {
            this.playerViewY = livingPlayerIn.prevRotationYaw + (livingPlayerIn.rotationYaw - livingPlayerIn.prevRotationYaw) * partialTicks;
            this.playerViewX = livingPlayerIn.prevRotationPitch + (livingPlayerIn.rotationPitch - livingPlayerIn.prevRotationPitch) * partialTicks;
        }
        if (optionsIn.thirdPersonView == 2) {
            this.playerViewY += 180.0f;
        }
        RenderManager.viewerPosX = livingPlayerIn.lastTickPosX + (livingPlayerIn.posX - livingPlayerIn.lastTickPosX) * partialTicks;
        RenderManager.viewerPosY = livingPlayerIn.lastTickPosY + (livingPlayerIn.posY - livingPlayerIn.lastTickPosY) * partialTicks;
        RenderManager.viewerPosZ = livingPlayerIn.lastTickPosZ + (livingPlayerIn.posZ - livingPlayerIn.lastTickPosZ) * partialTicks;
    }
    
    public void setPlayerViewY(final float playerViewYIn) {
        this.playerViewY = playerViewYIn;
    }
    
    public boolean isRenderShadow() {
        return this.renderShadow;
    }
    
    public void setRenderShadow(final boolean renderShadowIn) {
        this.renderShadow = renderShadowIn;
    }
    
    public boolean isDebugBoundingBox() {
        return this.debugBoundingBox;
    }
    
    public void setDebugBoundingBox(final boolean debugBoundingBoxIn) {
        this.debugBoundingBox = debugBoundingBoxIn;
    }
    
    public void renderEntitySimple(final Entity entityIn, final float partialTicks) {
        this.renderEntityStatic(entityIn, partialTicks, false);
    }
    
    public boolean shouldRender(final Entity entityIn, final ICamera camera, final double camX, final double camY, final double camZ) {
        final Render<Entity> render = this.getEntityRenderObject(entityIn);
        return render != null && render.shouldRender(entityIn, camera, camX, camY, camZ);
    }
    
    public void renderEntityStatic(final Entity entity, final float partialTicks, final boolean p_147936_3_) {
        if (entity.ticksExisted == 0) {
            entity.lastTickPosX = entity.posX;
            entity.lastTickPosY = entity.posY;
            entity.lastTickPosZ = entity.posZ;
        }
        final double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        final double d2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        final double d3 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        final float f = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        int i = entity.getBrightnessForRender(partialTicks);
        if (entity.isBurning()) {
            i = 15728880;
        }
        final int j = i % 65536;
        final int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0f, k / 1.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.doRenderEntity(entity, d0 - RenderManager.renderPosX, d2 - RenderManager.renderPosY, d3 - RenderManager.renderPosZ, f, partialTicks, p_147936_3_, false);
    }
    
    public void renderWitherSkull(final Entity entityIn, final float partialTicks) {
        final double d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
        final double d2 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
        final double d3 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
        final Render<Entity> render = this.getEntityRenderObject(entityIn);
        if (render != null && this.renderEngine != null) {
            final int i = entityIn.getBrightnessForRender(partialTicks);
            final int j = i % 65536;
            final int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0f, k / 1.0f);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            render.renderName(entityIn, d0 - RenderManager.renderPosX, d2 - RenderManager.renderPosY, d3 - RenderManager.renderPosZ);
        }
    }
    
    public void renderEntityWithPosYaw(final Entity entityIn, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        this.renderEntityWithPosYaw(entityIn, x, y, z, entityYaw, partialTicks, false);
    }
    
    public void renderEntityWithPosYaw(final Entity entityIn, final double x, final double y, final double z, final float entityYaw, final float partialTicks, final boolean onScreen) {
        this.doRenderEntity(entityIn, x, y, z, entityYaw, partialTicks, false, onScreen);
    }
    
    public void doRenderEntity(final Entity entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks, final boolean p_147939_10_, final boolean onScreen) {
        final Render<Entity> render = this.getEntityRenderObject(entity);
        if (render != null && this.renderEngine != null) {
            if (render instanceof RendererLivingEntity) {
                ((RendererLivingEntity)render).setRenderOutlines(this.renderOutlines);
            }
            if (CustomEntityModels.isActive()) {
                this.renderRender = render;
            }
            render.doRender(entity, x, y, z, entityYaw, partialTicks);
            if (!onScreen && !this.renderOutlines) {
                render.doRenderShadowAndFire(entity, x, y, z, entityYaw, partialTicks);
            }
            if (!onScreen && this.debugBoundingBox && !entity.isInvisible() && !p_147939_10_) {
                this.renderDebugBoundingBox(entity, x, y, z, entityYaw, partialTicks);
            }
        }
    }
    
    private void renderDebugBoundingBox(final Entity entityIn, final double p_85094_2_, final double p_85094_4_, final double p_85094_6_, final float p_85094_8_, final float p_85094_9_) {
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        final float f = entityIn.width / 2.0f;
        final AxisAlignedBB axisalignedbb = entityIn.getEntityBoundingBox();
        final AxisAlignedBB axisalignedbb2 = new AxisAlignedBB(axisalignedbb.minX - entityIn.posX + p_85094_2_, axisalignedbb.minY - entityIn.posY + p_85094_4_, axisalignedbb.minZ - entityIn.posZ + p_85094_6_, axisalignedbb.maxX - entityIn.posX + p_85094_2_, axisalignedbb.maxY - entityIn.posY + p_85094_4_, axisalignedbb.maxZ - entityIn.posZ + p_85094_6_);
        RenderGlobal.func_181563_a(axisalignedbb2, 255, 255, 255, 255);
        if (entityIn instanceof EntityLivingBase) {
            final float f2 = 0.01f;
            RenderGlobal.func_181563_a(new AxisAlignedBB(p_85094_2_ - f, p_85094_4_ + entityIn.getEyeHeight() - 0.009999999776482582, p_85094_6_ - f, p_85094_2_ + f, p_85094_4_ + entityIn.getEyeHeight() + 0.009999999776482582, p_85094_6_ + f), 255, 0, 0, 255);
        }
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        final Vec3 vec3 = entityIn.getLook(p_85094_9_);
        worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(p_85094_2_, p_85094_4_ + entityIn.getEyeHeight(), p_85094_6_).color4i(0, 0, 255, 255).endVertex();
        worldrenderer.pos(p_85094_2_ + vec3.xCoord * 2.0, p_85094_4_ + entityIn.getEyeHeight() + vec3.yCoord * 2.0, p_85094_6_ + vec3.zCoord * 2.0).color4i(0, 0, 255, 255).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }
    
    public void set(final World worldIn) {
        this.worldObj = worldIn;
    }
    
    public double getDistanceToCamera(final double p_78714_1_, final double p_78714_3_, final double p_78714_5_) {
        final double d0 = p_78714_1_ - RenderManager.viewerPosX;
        final double d2 = p_78714_3_ - RenderManager.viewerPosY;
        final double d3 = p_78714_5_ - RenderManager.viewerPosZ;
        return d0 * d0 + d2 * d2 + d3 * d3;
    }
    
    public MinecraftFontRenderer getFontRenderer() {
        return this.textRenderer;
    }
    
    public void setRenderOutlines(final boolean renderOutlinesIn) {
        this.renderOutlines = renderOutlinesIn;
    }
    
    public Map<Class, Render> getEntityRenderMap() {
        return this.entityRenderMap;
    }
    
    public void setEntityRenderMap(final Map p_setEntityRenderMap_1_) {
        this.entityRenderMap = (Map<Class, Render>)p_setEntityRenderMap_1_;
    }
    
    public Map<String, RenderPlayer> getSkinMap() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends RenderPlayer>)this.skinMap);
    }
}
