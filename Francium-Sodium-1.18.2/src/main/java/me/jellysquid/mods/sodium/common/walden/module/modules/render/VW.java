package me.jellysquid.mods.sodium.common.walden.module.modules.render;

import me.jellysquid.mods.sodium.common.walden.event.events.ItemRenderListener;
import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;
import me.jellysquid.mods.sodium.common.walden.module.setting.BooleanSetting;
import me.jellysquid.mods.sodium.common.walden.module.setting.DecimalSetting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3f;

public class VW extends Module implements ItemRenderListener {
    public VW() {
        super("ViewModel", "changes scale, pos and rotation of hands", false, Category.RENDER);
    }

    private final BooleanSetting mScale = BooleanSetting.Builder.newInstance()
            .setName("MainHand Scale")
            .setDescription("mainhand scale")
            .setModule(this)
            .setValue(true)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting mScaleX = DecimalSetting.Builder.newInstance()
            .setName("Scale X")
            .setDescription("mainhand scale x")
            .setModule(this)
            .setValue(1.0)
            .setMin(-5.0)
            .setMax(5.0)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting mScaleY = DecimalSetting.Builder.newInstance()
            .setName("Scale Y")
            .setDescription("mainhand scale y")
            .setModule(this)
            .setValue(1.0)
            .setMin(-5.0)
            .setMax(5.0)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting mScaleZ = DecimalSetting.Builder.newInstance()
            .setName("Scale Z")
            .setDescription("mainhand scale z")
            .setModule(this)
            .setValue(1.0)
            .setMin(-5.0)
            .setMax(5.0)
            .setAvailability(() -> true)
            .build();

    private final BooleanSetting mPos = BooleanSetting.Builder.newInstance()
            .setName("MainHand Position")
            .setDescription("mainhand pos")
            .setModule(this)
            .setValue(true)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting mPosX = DecimalSetting.Builder.newInstance()
            .setName("Position X")
            .setDescription("mainhand pos x")
            .setModule(this)
            .setValue(0.0)
            .setMin(-10.0)
            .setMax(10.0)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting mPosY = DecimalSetting.Builder.newInstance()
            .setName("Position Y")
            .setDescription("mainhand pos y")
            .setModule(this)
            .setValue(0.0)
            .setMin(-10.0)
            .setMax(10.0)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting mPosZ = DecimalSetting.Builder.newInstance()
            .setName("Position Z")
            .setDescription("mainhand pos z")
            .setModule(this)
            .setValue(0.0)
            .setMin(-10.0)
            .setMax(10.0)
            .setAvailability(() -> true)
            .build();

    private final BooleanSetting mRot = BooleanSetting.Builder.newInstance()
            .setName("MainHand Rotation")
            .setDescription("mainhand rot")
            .setModule(this)
            .setValue(true)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting mRotX = DecimalSetting.Builder.newInstance()
            .setName("Rotation X")
            .setDescription("mainhand rot x")
            .setModule(this)
            .setValue(0.0)
            .setMin(-180.0)
            .setMax(180.0)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting mRotY = DecimalSetting.Builder.newInstance()
            .setName("Rotation Y")
            .setDescription("mainhand rot y")
            .setModule(this)
            .setValue(0.0)
            .setMin(-180.0)
            .setMax(180.0)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting mRotZ = DecimalSetting.Builder.newInstance()
            .setName("Rotation Z")
            .setDescription("mainhand rot z")
            .setModule(this)
            .setValue(0.0)
            .setMin(-180.0)
            .setMax(180.0)
            .setAvailability(() -> true)
            .build();

    private final BooleanSetting oScale = BooleanSetting.Builder.newInstance()
            .setName("OffHand Scale")
            .setDescription("offhand scale")
            .setModule(this)
            .setValue(true)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting oScaleX = DecimalSetting.Builder.newInstance()
            .setName("Scale X")
            .setDescription("offhand scale x")
            .setModule(this)
            .setValue(1.0)
            .setMin(-5.0)
            .setMax(5.0)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting oScaleY = DecimalSetting.Builder.newInstance()
            .setName("Scale Y")
            .setDescription("offhand scale y")
            .setModule(this)
            .setValue(1.0)
            .setMin(-5.0)
            .setMax(5.0)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting oScaleZ = DecimalSetting.Builder.newInstance()
            .setName("Scale Z")
            .setDescription("offhand scale z")
            .setModule(this)
            .setValue(1.0)
            .setMin(-5.0)
            .setMax(5.0)
            .setAvailability(() -> true)
            .build();

    private final BooleanSetting oPos = BooleanSetting.Builder.newInstance()
            .setName("Offhand Position")
            .setDescription("offhand pos")
            .setModule(this)
            .setValue(true)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting oPosX = DecimalSetting.Builder.newInstance()
            .setName("Position X")
            .setDescription("offhand pos x")
            .setModule(this)
            .setValue(0.0)
            .setMin(-10.0)
            .setMax(10.0)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting oPosY = DecimalSetting.Builder.newInstance()
            .setName("Position Y")
            .setDescription("offhand pos y")
            .setModule(this)
            .setValue(0.0)
            .setMin(-10.0)
            .setMax(10.0)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting oPosZ = DecimalSetting.Builder.newInstance()
            .setName("Position Z")
            .setDescription("offhand pos z")
            .setModule(this)
            .setValue(0.0)
            .setMin(-10.0)
            .setMax(10.0)
            .setAvailability(() -> true)
            .build();

    private final BooleanSetting oRot = BooleanSetting.Builder.newInstance()
            .setName("OffHand Rotation")
            .setDescription("offhand rot")
            .setModule(this)
            .setValue(true)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting oRotX = DecimalSetting.Builder.newInstance()
            .setName("Rotation X")
            .setDescription("offhand rot x")
            .setModule(this)
            .setValue(0.0)
            .setMin(-180.0)
            .setMax(180.0)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting oRotY = DecimalSetting.Builder.newInstance()
            .setName("Rotation Y")
            .setDescription("offhand rot y")
            .setModule(this)
            .setValue(0.0)
            .setMin(-180.0)
            .setMax(180.0)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting oRotZ = DecimalSetting.Builder.newInstance()
            .setName("Rotation Z")
            .setDescription("offhand rot z")
            .setModule(this)
            .setValue(0.0)
            .setMin(-180.0)
            .setMax(180.0)
            .setAvailability(() -> true)
            .build();





    @Override
    public void onEnable() {
        super.onEnable();

        eventManager.add(ItemRenderListener.class, this);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        eventManager.remove(ItemRenderListener.class, this);
    }

    @Override
    public void onItemRender(ItemRenderEvent event) {
        MatrixStack matrices = event.getMatrices();

        if (event.getHand() == Hand.MAIN_HAND) {
            if (mPos.get()) {
                matrices.translate(mPosX.get() * 0.1, mPosY.get() * 0.1, mPosZ.get() * 0.1);
            }
            if (mRot.get()) {
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(mRotX.get().floatValue()));
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(mRotY.get().floatValue()));
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(mRotZ.get().floatValue()));
            }
            if (mScale.get()) {
                matrices.scale(1 - (1 - mScaleX.get().floatValue()) * 0.1F, 1 - (1 - mScaleY.get().floatValue()) * 0.1F, 1 - (1 - mScaleZ.get().floatValue()) * 0.1F);
            }
        }

        if (event.getHand() == Hand.OFF_HAND) {
            if (oPos.get()) {
                matrices.translate(oPosX.get() * 0.1, oPosY.get() * 0.1, oPosZ.get() * 0.1);
                matrices.translate(oPosX.get(), oPosY.get(), oPosZ.get());
            }
            if (oRot.get()) {
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(oRotX.get().floatValue()));
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(oRotY.get().floatValue()));
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(oRotZ.get().floatValue()));
            }
            if (oScale.get()) {
                matrices.scale(1 - (1 - oScaleX.get().floatValue()) * 0.1F, 1 - (1 - oScaleY.get().floatValue()) * 0.1F, 1 - (1 - oScaleZ.get().floatValue()) * 0.1F);
            }
        }

    }
}
