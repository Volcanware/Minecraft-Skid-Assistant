// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.client.model;

import net.optifine.shaders.Shaders;
import net.minecraft.client.renderer.WorldRenderer;
import net.optifine.model.ModelSprite;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GLAllocation;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GlStateManager;
import com.google.common.collect.Lists;
import net.minecraft.src.Config;
import java.util.ArrayList;
import net.minecraft.client.renderer.RenderGlobal;
import net.optifine.entity.model.anim.ModelUpdater;
import net.minecraft.util.ResourceLocation;
import java.util.List;

public class ModelRenderer
{
    public float textureWidth;
    public float textureHeight;
    private int textureOffsetX;
    private int textureOffsetY;
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    private boolean compiled;
    private int displayList;
    public boolean mirror;
    public boolean showModel;
    public boolean isHidden;
    public List<ModelBox> cubeList;
    public List<ModelRenderer> childModels;
    public final String boxName;
    private ModelBase baseModel;
    public float offsetX;
    public float offsetY;
    public float offsetZ;
    public List spriteList;
    public boolean mirrorV;
    public float scaleX;
    public float scaleY;
    public float scaleZ;
    private int countResetDisplayList;
    private ResourceLocation textureLocation;
    private String id;
    private ModelUpdater modelUpdater;
    private RenderGlobal renderGlobal;
    
    public ModelRenderer(final ModelBase model, final String boxNameIn) {
        this.spriteList = new ArrayList();
        this.mirrorV = false;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.scaleZ = 1.0f;
        this.textureLocation = null;
        this.id = null;
        this.renderGlobal = Config.getRenderGlobal();
        this.textureWidth = 64.0f;
        this.textureHeight = 32.0f;
        this.showModel = true;
        this.cubeList = (List<ModelBox>)Lists.newArrayList();
        this.baseModel = model;
        model.boxList.add(this);
        this.boxName = boxNameIn;
        this.setTextureSize(model.textureWidth, model.textureHeight);
    }
    
    public ModelRenderer(final ModelBase model) {
        this(model, null);
    }
    
    public ModelRenderer(final ModelBase model, final int texOffX, final int texOffY) {
        this(model);
        this.setTextureOffset(texOffX, texOffY);
    }
    
    public void addChild(final ModelRenderer renderer) {
        if (this.childModels == null) {
            this.childModels = (List<ModelRenderer>)Lists.newArrayList();
        }
        this.childModels.add(renderer);
    }
    
    public ModelRenderer setTextureOffset(final int x, final int y) {
        this.textureOffsetX = x;
        this.textureOffsetY = y;
        return this;
    }
    
    public ModelRenderer addBox(String partName, final float offX, final float offY, final float offZ, final int width, final int height, final int depth) {
        partName = String.valueOf(this.boxName) + "." + partName;
        final TextureOffset textureoffset = this.baseModel.getTextureOffset(partName);
        this.setTextureOffset(textureoffset.textureOffsetX, textureoffset.textureOffsetY);
        this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0f).setBoxName(partName));
        return this;
    }
    
    public ModelRenderer addBox(final float offX, final float offY, final float offZ, final int width, final int height, final int depth) {
        this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0f));
        return this;
    }
    
    public ModelRenderer addBox(final float p_178769_1_, final float p_178769_2_, final float p_178769_3_, final int p_178769_4_, final int p_178769_5_, final int p_178769_6_, final boolean p_178769_7_) {
        this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, p_178769_1_, p_178769_2_, p_178769_3_, p_178769_4_, p_178769_5_, p_178769_6_, 0.0f, p_178769_7_));
        return this;
    }
    
    public void addBox(final float p_78790_1_, final float p_78790_2_, final float p_78790_3_, final int width, final int height, final int depth, final float scaleFactor) {
        this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, p_78790_1_, p_78790_2_, p_78790_3_, width, height, depth, scaleFactor));
    }
    
    public void setRotationPoint(final float rotationPointXIn, final float rotationPointYIn, final float rotationPointZIn) {
        this.rotationPointX = rotationPointXIn;
        this.rotationPointY = rotationPointYIn;
        this.rotationPointZ = rotationPointZIn;
    }
    
    public void render(final float p_78785_1_) {
        if (!this.isHidden && this.showModel) {
            this.checkResetDisplayList();
            if (!this.compiled) {
                this.compileDisplayList(p_78785_1_);
            }
            int i = 0;
            if (this.textureLocation != null && !this.renderGlobal.renderOverlayDamaged) {
                if (this.renderGlobal.renderOverlayEyes) {
                    return;
                }
                i = GlStateManager.getBoundTexture();
                Config.getTextureManager().bindTexture(this.textureLocation);
            }
            if (this.modelUpdater != null) {
                this.modelUpdater.update();
            }
            final boolean flag = this.scaleX != 1.0f || this.scaleY != 1.0f || this.scaleZ != 1.0f;
            GL11.glTranslatef(this.offsetX, this.offsetY, this.offsetZ);
            if (this.rotateAngleX == 0.0f && this.rotateAngleY == 0.0f && this.rotateAngleZ == 0.0f) {
                if (this.rotationPointX == 0.0f && this.rotationPointY == 0.0f && this.rotationPointZ == 0.0f) {
                    if (flag) {
                        GL11.glScalef(this.scaleX, this.scaleY, this.scaleZ);
                    }
                    GlStateManager.callList(this.displayList);
                    if (this.childModels != null) {
                        for (int l = 0; l < this.childModels.size(); ++l) {
                            this.childModels.get(l).render(p_78785_1_);
                        }
                    }
                    if (flag) {
                        GL11.glScalef(1.0f / this.scaleX, 1.0f / this.scaleY, 1.0f / this.scaleZ);
                    }
                }
                else {
                    GL11.glTranslatef(this.rotationPointX * p_78785_1_, this.rotationPointY * p_78785_1_, this.rotationPointZ * p_78785_1_);
                    if (flag) {
                        GL11.glScalef(this.scaleX, this.scaleY, this.scaleZ);
                    }
                    GlStateManager.callList(this.displayList);
                    if (this.childModels != null) {
                        for (int k = 0; k < this.childModels.size(); ++k) {
                            this.childModels.get(k).render(p_78785_1_);
                        }
                    }
                    if (flag) {
                        GL11.glScalef(1.0f / this.scaleX, 1.0f / this.scaleY, 1.0f / this.scaleZ);
                    }
                    GL11.glTranslatef(-this.rotationPointX * p_78785_1_, -this.rotationPointY * p_78785_1_, -this.rotationPointZ * p_78785_1_);
                }
            }
            else {
                GL11.glPushMatrix();
                GL11.glTranslatef(this.rotationPointX * p_78785_1_, this.rotationPointY * p_78785_1_, this.rotationPointZ * p_78785_1_);
                if (this.rotateAngleZ != 0.0f) {
                    GL11.glRotatef(this.rotateAngleZ * 57.295776f, 0.0f, 0.0f, 1.0f);
                }
                if (this.rotateAngleY != 0.0f) {
                    GL11.glRotatef(this.rotateAngleY * 57.295776f, 0.0f, 1.0f, 0.0f);
                }
                if (this.rotateAngleX != 0.0f) {
                    GL11.glRotatef(this.rotateAngleX * 57.295776f, 1.0f, 0.0f, 0.0f);
                }
                if (flag) {
                    GL11.glScalef(this.scaleX, this.scaleY, this.scaleZ);
                }
                GlStateManager.callList(this.displayList);
                if (this.childModels != null) {
                    for (int j = 0; j < this.childModels.size(); ++j) {
                        this.childModels.get(j).render(p_78785_1_);
                    }
                }
                GL11.glPopMatrix();
            }
            GL11.glTranslatef(-this.offsetX, -this.offsetY, -this.offsetZ);
            if (i != 0) {
                GlStateManager.bindTexture(i);
            }
        }
    }
    
    public void renderWithRotation(final float p_78791_1_) {
        if (!this.isHidden && this.showModel) {
            this.checkResetDisplayList();
            if (!this.compiled) {
                this.compileDisplayList(p_78791_1_);
            }
            int i = 0;
            if (this.textureLocation != null && !this.renderGlobal.renderOverlayDamaged) {
                if (this.renderGlobal.renderOverlayEyes) {
                    return;
                }
                i = GlStateManager.getBoundTexture();
                Config.getTextureManager().bindTexture(this.textureLocation);
            }
            if (this.modelUpdater != null) {
                this.modelUpdater.update();
            }
            final boolean flag = this.scaleX != 1.0f || this.scaleY != 1.0f || this.scaleZ != 1.0f;
            GL11.glPushMatrix();
            GL11.glTranslatef(this.rotationPointX * p_78791_1_, this.rotationPointY * p_78791_1_, this.rotationPointZ * p_78791_1_);
            if (this.rotateAngleY != 0.0f) {
                GL11.glRotatef(this.rotateAngleY * 57.295776f, 0.0f, 1.0f, 0.0f);
            }
            if (this.rotateAngleX != 0.0f) {
                GL11.glRotatef(this.rotateAngleX * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
            if (this.rotateAngleZ != 0.0f) {
                GL11.glRotatef(this.rotateAngleZ * 57.295776f, 0.0f, 0.0f, 1.0f);
            }
            if (flag) {
                GL11.glScalef(this.scaleX, this.scaleY, this.scaleZ);
            }
            GlStateManager.callList(this.displayList);
            if (this.childModels != null) {
                for (int j = 0; j < this.childModels.size(); ++j) {
                    this.childModels.get(j).render(p_78791_1_);
                }
            }
            GL11.glPopMatrix();
            if (i != 0) {
                GlStateManager.bindTexture(i);
            }
        }
    }
    
    public void postRender(final float scale) {
        if (!this.isHidden && this.showModel) {
            this.checkResetDisplayList();
            if (!this.compiled) {
                this.compileDisplayList(scale);
            }
            if (this.rotateAngleX == 0.0f && this.rotateAngleY == 0.0f && this.rotateAngleZ == 0.0f) {
                if (this.rotationPointX != 0.0f || this.rotationPointY != 0.0f || this.rotationPointZ != 0.0f) {
                    GL11.glTranslatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
                }
            }
            else {
                GL11.glTranslatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
                if (this.rotateAngleZ != 0.0f) {
                    GL11.glRotatef(this.rotateAngleZ * 57.295776f, 0.0f, 0.0f, 1.0f);
                }
                if (this.rotateAngleY != 0.0f) {
                    GL11.glRotatef(this.rotateAngleY * 57.295776f, 0.0f, 1.0f, 0.0f);
                }
                if (this.rotateAngleX != 0.0f) {
                    GL11.glRotatef(this.rotateAngleX * 57.295776f, 1.0f, 0.0f, 0.0f);
                }
            }
        }
    }
    
    private void compileDisplayList(final float scale) {
        if (this.displayList == 0) {
            this.displayList = GLAllocation.generateDisplayLists(1);
        }
        GL11.glNewList(this.displayList, 4864);
        final WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();
        for (int i = 0; i < this.cubeList.size(); ++i) {
            this.cubeList.get(i).render(worldrenderer, scale);
        }
        for (int j = 0; j < this.spriteList.size(); ++j) {
            final ModelSprite modelsprite = this.spriteList.get(j);
            modelsprite.render(Tessellator.getInstance(), scale);
        }
        GL11.glEndList();
        this.compiled = true;
    }
    
    public ModelRenderer setTextureSize(final int textureWidthIn, final int textureHeightIn) {
        this.textureWidth = (float)textureWidthIn;
        this.textureHeight = (float)textureHeightIn;
        return this;
    }
    
    public void addSprite(final float p_addSprite_1_, final float p_addSprite_2_, final float p_addSprite_3_, final int p_addSprite_4_, final int p_addSprite_5_, final int p_addSprite_6_, final float p_addSprite_7_) {
        this.spriteList.add(new ModelSprite(this, this.textureOffsetX, this.textureOffsetY, p_addSprite_1_, p_addSprite_2_, p_addSprite_3_, p_addSprite_4_, p_addSprite_5_, p_addSprite_6_, p_addSprite_7_));
    }
    
    public boolean getCompiled() {
        return this.compiled;
    }
    
    public int getDisplayList() {
        return this.displayList;
    }
    
    private void checkResetDisplayList() {
        if (this.countResetDisplayList != Shaders.countResetDisplayLists) {
            this.compiled = false;
            this.countResetDisplayList = Shaders.countResetDisplayLists;
        }
    }
    
    public ResourceLocation getTextureLocation() {
        return this.textureLocation;
    }
    
    public void setTextureLocation(final ResourceLocation p_setTextureLocation_1_) {
        this.textureLocation = p_setTextureLocation_1_;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String p_setId_1_) {
        this.id = p_setId_1_;
    }
    
    public void addBox(final int[][] p_addBox_1_, final float p_addBox_2_, final float p_addBox_3_, final float p_addBox_4_, final float p_addBox_5_, final float p_addBox_6_, final float p_addBox_7_, final float p_addBox_8_) {
        this.cubeList.add(new ModelBox(this, p_addBox_1_, p_addBox_2_, p_addBox_3_, p_addBox_4_, p_addBox_5_, p_addBox_6_, p_addBox_7_, p_addBox_8_, this.mirror));
    }
    
    public ModelRenderer getChild(final String p_getChild_1_) {
        if (p_getChild_1_ == null) {
            return null;
        }
        if (this.childModels != null) {
            for (int i = 0; i < this.childModels.size(); ++i) {
                final ModelRenderer modelrenderer = this.childModels.get(i);
                if (p_getChild_1_.equals(modelrenderer.getId())) {
                    return modelrenderer;
                }
            }
        }
        return null;
    }
    
    public ModelRenderer getChildDeep(final String p_getChildDeep_1_) {
        if (p_getChildDeep_1_ == null) {
            return null;
        }
        final ModelRenderer modelrenderer = this.getChild(p_getChildDeep_1_);
        if (modelrenderer != null) {
            return modelrenderer;
        }
        if (this.childModels != null) {
            for (int i = 0; i < this.childModels.size(); ++i) {
                final ModelRenderer modelrenderer2 = this.childModels.get(i);
                final ModelRenderer modelrenderer3 = modelrenderer2.getChildDeep(p_getChildDeep_1_);
                if (modelrenderer3 != null) {
                    return modelrenderer3;
                }
            }
        }
        return null;
    }
    
    public void setModelUpdater(final ModelUpdater p_setModelUpdater_1_) {
        this.modelUpdater = p_setModelUpdater_1_;
    }
    
    @Override
    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("id: " + this.id + ", boxes: " + ((this.cubeList != null) ? Integer.valueOf(this.cubeList.size()) : null) + ", submodels: " + ((this.childModels != null) ? Integer.valueOf(this.childModels.size()) : null));
        return stringbuffer.toString();
    }
}
