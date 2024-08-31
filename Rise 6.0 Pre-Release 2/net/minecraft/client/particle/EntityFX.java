package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFX extends Entity {
    protected int particleTextureIndexX;
    protected int particleTextureIndexY;
    protected float particleTextureJitterX;
    protected float particleTextureJitterY;
    protected int particleAge;
    protected int particleMaxAge;
    protected float particleScale;
    protected float particleGravity;

    /**
     * The red amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0.
     */
    protected float particleRed;

    /**
     * The green amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0.
     */
    protected float particleGreen;

    /**
     * The blue amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0.
     */
    protected float particleBlue;

    /**
     * Particle alpha
     */
    protected float particleAlpha;

    /**
     * The icon field from which the given particle pulls its texture.
     */
    protected TextureAtlasSprite particleIcon;
    public static double interpPosX;
    public static double interpPosY;
    public static double interpPosZ;

    protected EntityFX(final World worldIn, final double posXIn, final double posYIn, final double posZIn) {
        super(worldIn);
        this.particleAlpha = 1.0F;
        this.setSize(0.2F, 0.2F);
        this.setPosition(posXIn, posYIn, posZIn);
        this.lastTickPosX = this.prevPosX = posXIn;
        this.lastTickPosY = this.prevPosY = posYIn;
        this.lastTickPosZ = this.prevPosZ = posZIn;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.particleTextureJitterX = this.rand.nextFloat() * 3.0F;
        this.particleTextureJitterY = this.rand.nextFloat() * 3.0F;
        this.particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
        this.particleMaxAge = (int) (4.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
        this.particleAge = 0;
    }

    public EntityFX(final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final double xSpeedIn, final double ySpeedIn, final double zSpeedIn) {
        this(worldIn, xCoordIn, yCoordIn, zCoordIn);
        this.motionX = xSpeedIn + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
        this.motionY = ySpeedIn + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
        this.motionZ = zSpeedIn + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
        final float f = (float) (Math.random() + Math.random() + 1.0D) * 0.15F;
        final float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.motionX = this.motionX / (double) f1 * (double) f * 0.4000000059604645D;
        this.motionY = this.motionY / (double) f1 * (double) f * 0.4000000059604645D + 0.10000000149011612D;
        this.motionZ = this.motionZ / (double) f1 * (double) f * 0.4000000059604645D;
    }

    public EntityFX multiplyVelocity(final float multiplier) {
        this.motionX *= multiplier;
        this.motionY = (this.motionY - 0.10000000149011612D) * (double) multiplier + 0.10000000149011612D;
        this.motionZ *= multiplier;
        return this;
    }

    public EntityFX multipleParticleScaleBy(final float p_70541_1_) {
        this.setSize(0.2F * p_70541_1_, 0.2F * p_70541_1_);
        this.particleScale *= p_70541_1_;
        return this;
    }

    public void setRBGColorF(final float particleRedIn, final float particleGreenIn, final float particleBlueIn) {
        this.particleRed = particleRedIn;
        this.particleGreen = particleGreenIn;
        this.particleBlue = particleBlueIn;
    }

    /**
     * Sets the particle alpha (float)
     */
    public void setAlphaF(final float alpha) {
        if (this.particleAlpha == 1.0F && alpha < 1.0F) {
            Minecraft.getMinecraft().effectRenderer.moveToAlphaLayer(this);
        } else if (this.particleAlpha < 1.0F && alpha == 1.0F) {
            Minecraft.getMinecraft().effectRenderer.moveToNoAlphaLayer(this);
        }

        this.particleAlpha = alpha;
    }

    public float getRedColorF() {
        return this.particleRed;
    }

    public float getGreenColorF() {
        return this.particleGreen;
    }

    public float getBlueColorF() {
        return this.particleBlue;
    }

    public float getAlpha() {
        return this.particleAlpha;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking() {
        return false;
    }

    protected void entityInit() {
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }

        this.motionY -= 0.04D * (double) this.particleGravity;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }

    /**
     * Renders the particle
     *
     * @param worldRendererIn The WorldRenderer instance
     */
    public void renderParticle(final WorldRenderer worldRendererIn, final Entity entityIn, final float partialTicks, final float p_180434_4_, final float p_180434_5_, final float p_180434_6_, final float p_180434_7_, final float p_180434_8_) {
        float f = (float) this.particleTextureIndexX / 16.0F;
        float f1 = f + 0.0624375F;
        float f2 = (float) this.particleTextureIndexY / 16.0F;
        float f3 = f2 + 0.0624375F;
        final float f4 = 0.1F * this.particleScale;

        if (this.particleIcon != null) {
            f = this.particleIcon.getMinU();
            f1 = this.particleIcon.getMaxU();
            f2 = this.particleIcon.getMinV();
            f3 = this.particleIcon.getMaxV();
        }

        final float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        final float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        final float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
        final int i = this.getBrightnessForRender(partialTicks);
        final int j = i >> 16 & 65535;
        final int k = i & 65535;
        worldRendererIn.pos(f5 - p_180434_4_ * f4 - p_180434_7_ * f4, f6 - p_180434_5_ * f4, f7 - p_180434_6_ * f4 - p_180434_8_ * f4).tex(f1, f3).func_181666_a(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).func_181671_a(j, k).endVertex();
        worldRendererIn.pos(f5 - p_180434_4_ * f4 + p_180434_7_ * f4, f6 + p_180434_5_ * f4, f7 - p_180434_6_ * f4 + p_180434_8_ * f4).tex(f1, f2).func_181666_a(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).func_181671_a(j, k).endVertex();
        worldRendererIn.pos(f5 + p_180434_4_ * f4 + p_180434_7_ * f4, f6 + p_180434_5_ * f4, f7 + p_180434_6_ * f4 + p_180434_8_ * f4).tex(f, f2).func_181666_a(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).func_181671_a(j, k).endVertex();
        worldRendererIn.pos(f5 + p_180434_4_ * f4 - p_180434_7_ * f4, f6 - p_180434_5_ * f4, f7 + p_180434_6_ * f4 - p_180434_8_ * f4).tex(f, f3).func_181666_a(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).func_181671_a(j, k).endVertex();
    }

    public int getFXLayer() {
        return 0;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(final NBTTagCompound tagCompound) {
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(final NBTTagCompound tagCompund) {
    }

    /**
     * Sets the particle's icon.
     *
     * @param icon The icon to set for this particle
     */
    public void setParticleIcon(final TextureAtlasSprite icon) {
        final int i = this.getFXLayer();

        if (i == 1) {
            this.particleIcon = icon;
        } else {
            throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
        }
    }

    /**
     * Public method to set private field particleTextureIndex.
     */
    public void setParticleTextureIndex(final int particleTextureIndex) {
        if (this.getFXLayer() != 0) {
            throw new RuntimeException("Invalid call to Particle.setMiscTex");
        } else {
            this.particleTextureIndexX = particleTextureIndex % 16;
            this.particleTextureIndexY = particleTextureIndex / 16;
        }
    }

    public void nextTextureIndexX() {
        ++this.particleTextureIndexX;
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem() {
        return false;
    }

    public String toString() {
        return this.getClass().getSimpleName() + ", Pos (" + this.posX + "," + this.posY + "," + this.posZ + "), RGBA (" + this.particleRed + "," + this.particleGreen + "," + this.particleBlue + "," + this.particleAlpha + "), Age " + this.particleAge;
    }
}
