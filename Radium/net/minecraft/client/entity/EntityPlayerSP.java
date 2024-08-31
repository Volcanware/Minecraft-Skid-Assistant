// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.client.entity;

import vip.radium.event.impl.player.UseItemEvent;
import net.minecraft.potion.Potion;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.entity.IMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.world.IWorldNameable;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.world.IInteractionObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.init.Items;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.IChatComponent;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.stats.StatBase;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C01PacketChatMessage;
import vip.radium.event.impl.player.SendMessageEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import vip.radium.event.impl.player.SprintEvent;
import vip.radium.RadiumClient;
import vip.radium.event.impl.player.MoveEntityEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.client.Minecraft;
import vip.radium.event.impl.player.UpdatePositionEvent;
import net.minecraft.util.MovementInput;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.client.network.NetHandlerPlayClient;

public class EntityPlayerSP extends AbstractClientPlayer
{
    public final NetHandlerPlayClient sendQueue;
    private final StatFileWriter statWriter;
    public MovementInput movementInput;
    public int sprintingTicksLeft;
    public float renderArmYaw;
    public float renderArmPitch;
    public float prevRenderArmYaw;
    public float prevRenderArmPitch;
    public float timeInPortal;
    public float prevTimeInPortal;
    public float lastReportedYaw;
    public float lastReportedPitch;
    public boolean serverSprintState;
    public UpdatePositionEvent currentEvent;
    protected Minecraft mc;
    protected int sprintToggleTimer;
    private double lastReportedPosX;
    private double lastReportedPosY;
    private double lastReportedPosZ;
    private boolean serverSneakState;
    private int positionUpdateTicks;
    private boolean hasValidHealth;
    private String clientBrand;
    private int horseJumpPowerCounter;
    private float horseJumpPower;
    
    public EntityPlayerSP(final Minecraft mcIn, final World worldIn, final NetHandlerPlayClient netHandler, final StatFileWriter statFile) {
        super(worldIn, netHandler.getGameProfile());
        this.sendQueue = netHandler;
        this.statWriter = statFile;
        this.mc = mcIn;
        this.dimension = 0;
    }
    
    @Override
    public boolean attackEntityFrom(final DamageSource source, final float amount) {
        return false;
    }
    
    @Override
    public void heal(final float healAmount) {
    }
    
    @Override
    public void mountEntity(final Entity entityIn) {
        super.mountEntity(entityIn);
        if (entityIn instanceof EntityMinecart) {
            this.mc.getSoundHandler().playSound(new MovingSoundMinecartRiding(this, (EntityMinecart)entityIn));
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.worldObj.isBlockLoaded(new BlockPos(this.posX, 0.0, this.posZ))) {
            super.onUpdate();
            this.onUpdatePlayer();
        }
    }
    
    @Override
    public void moveEntity(final double x, final double y, final double z) {
        final MoveEntityEvent moveEntityEvent = new MoveEntityEvent(x, y, z);
        RadiumClient.getInstance().getEventBus().post(moveEntityEvent);
        if (moveEntityEvent.isCancelled()) {
            return;
        }
        super.moveEntity(moveEntityEvent.getX(), moveEntityEvent.getY(), moveEntityEvent.getZ());
    }
    
    public void onUpdatePlayer() {
        final boolean riding = this.isRiding();
        if (!riding) {
            final SprintEvent sprintEvent = new SprintEvent(this.isSprinting());
            RadiumClient.getInstance().getEventBus().post(sprintEvent);
            final boolean flag = sprintEvent.isSprinting();
            if (flag != this.serverSprintState) {
                if (flag) {
                    this.sendQueue.sendPacket(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SPRINTING));
                }
                else {
                    this.sendQueue.sendPacket(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SPRINTING));
                }
                this.serverSprintState = flag;
            }
            final boolean flag2 = this.isSneaking();
            if (flag2 != this.serverSneakState) {
                if (flag2) {
                    this.sendQueue.sendPacket(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SNEAKING));
                }
                else {
                    this.sendQueue.sendPacket(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SNEAKING));
                }
                this.serverSneakState = flag2;
            }
        }
        if (this.isCurrentViewEntity()) {
            final UpdatePositionEvent updatePositionEvent = new UpdatePositionEvent(this.lastReportedYaw, this.lastReportedPitch, this.posX, this.getEntityBoundingBox().minY, this.posZ, this.lastTickPosX, this.lastTickPosY, this.lastTickPosZ, this.rotationYaw, this.rotationPitch, this.onGround);
            RadiumClient.getInstance().getEventBus().post(updatePositionEvent);
            this.currentEvent = updatePositionEvent;
            final double eventX = updatePositionEvent.getPosX();
            final double eventY = updatePositionEvent.getPosY();
            final double eventZ = updatePositionEvent.getPosZ();
            final float eventYaw = updatePositionEvent.getYaw();
            final float eventPitch = updatePositionEvent.getPitch();
            final boolean eventOnGround = updatePositionEvent.isOnGround();
            final double xDif = eventX - this.lastReportedPosX;
            final double yDif = eventY - this.lastReportedPosY;
            final double zDif = eventZ - this.lastReportedPosZ;
            final float yawDif = eventYaw - this.lastReportedYaw;
            final float pitchDif = eventPitch - this.lastReportedPitch;
            final boolean updateXYZ = xDif * xDif + yDif * yDif + zDif * zDif > 9.0E-4 || this.positionUpdateTicks >= 20;
            final boolean updateYawPitch = yawDif != 0.0 || pitchDif != 0.0;
            final boolean cancelled = updatePositionEvent.isCancelled();
            if (riding) {
                if (!cancelled) {
                    this.sendQueue.sendPacket(new C03PacketPlayer.C05PacketPlayerLook(eventYaw, eventPitch, eventOnGround));
                    this.sendQueue.sendPacket(new C0CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
                }
            }
            else {
                if (!cancelled) {
                    if (updateXYZ && updateYawPitch) {
                        this.sendQueue.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(eventX, eventY, eventZ, eventYaw, eventPitch, eventOnGround));
                    }
                    else if (updateXYZ) {
                        this.sendQueue.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(eventX, eventY, eventZ, eventOnGround));
                    }
                    else if (updateYawPitch) {
                        this.sendQueue.sendPacket(new C03PacketPlayer.C05PacketPlayerLook(eventYaw, eventPitch, eventOnGround));
                    }
                    else {
                        this.sendQueue.sendPacket(new C03PacketPlayer(eventOnGround));
                    }
                }
                ++this.positionUpdateTicks;
                if (updateXYZ) {
                    this.lastReportedPosX = eventX;
                    this.lastReportedPosY = eventY;
                    this.lastReportedPosZ = eventZ;
                    this.positionUpdateTicks = 0;
                }
                if (updateYawPitch) {
                    this.lastReportedYaw = eventYaw;
                    this.lastReportedPitch = eventPitch;
                }
                updatePositionEvent.setPost();
                RadiumClient.getInstance().getEventBus().post(updatePositionEvent);
            }
        }
    }
    
    @Override
    public EntityItem dropOneItem(final boolean dropAll) {
        final C07PacketPlayerDigging.Action c07packetplayerdigging$action = dropAll ? C07PacketPlayerDigging.Action.DROP_ALL_ITEMS : C07PacketPlayerDigging.Action.DROP_ITEM;
        this.sendQueue.sendPacket(new C07PacketPlayerDigging(c07packetplayerdigging$action, BlockPos.ORIGIN, EnumFacing.DOWN));
        return null;
    }
    
    @Override
    protected void joinEntityItemWithWorld(final EntityItem itemIn) {
    }
    
    public void sendChatMessage(final String message) {
        final SendMessageEvent event = new SendMessageEvent(message);
        RadiumClient.getInstance().getEventBus().post(event);
        if (event.isCancelled()) {
            return;
        }
        this.sendQueue.sendPacket(new C01PacketChatMessage(event.getMessage()));
    }
    
    @Override
    public void swingItem() {
        super.swingItem();
        this.sendQueue.sendPacket(new C0APacketAnimation());
    }
    
    @Override
    public void respawnPlayer() {
        this.sendQueue.sendPacket(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
    }
    
    @Override
    protected void damageEntity(final DamageSource damageSrc, final float damageAmount) {
        if (!this.isEntityInvulnerable(damageSrc)) {
            this.setHealth(this.getHealth() - damageAmount);
        }
    }
    
    public void closeScreen() {
        this.sendQueue.sendPacket(new C0DPacketCloseWindow(this.openContainer.windowId));
        this.closeScreenAndDropStack();
    }
    
    public void closeScreenAndDropStack() {
        this.inventory.setItemStack(null);
        super.closeScreen();
        this.mc.displayGuiScreen(null);
    }
    
    public void setPlayerSPHealth(final float health) {
        if (this.hasValidHealth) {
            final float f = this.getHealth() - health;
            if (f <= 0.0f) {
                this.setHealth(health);
                if (f < 0.0f) {
                    this.hurtResistantTime = 20 / 2;
                }
            }
            else {
                this.lastDamage = f;
                this.setHealth(this.getHealth());
                this.hurtResistantTime = 20;
                this.damageEntity(DamageSource.generic, f);
                final int n = 10;
                this.maxHurtTime = n;
                this.hurtTime = n;
            }
        }
        else {
            this.setHealth(health);
            this.hasValidHealth = true;
        }
    }
    
    @Override
    public void addStat(final StatBase stat, final int amount) {
        if (stat != null && stat.isIndependent) {
            super.addStat(stat, amount);
        }
    }
    
    @Override
    public void sendPlayerAbilities() {
        this.sendQueue.sendPacket(new C13PacketPlayerAbilities(this.capabilities));
    }
    
    @Override
    public boolean isUser() {
        return true;
    }
    
    protected void sendHorseJump() {
        this.sendQueue.sendPacket(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.RIDING_JUMP, (int)(this.getHorseJumpPower() * 100.0f)));
    }
    
    public void sendHorseInventory() {
        this.sendQueue.sendPacket(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.OPEN_INVENTORY));
    }
    
    public String getClientBrand() {
        return this.clientBrand;
    }
    
    public void setClientBrand(final String brand) {
        this.clientBrand = brand;
    }
    
    public StatFileWriter getStatFileWriter() {
        return this.statWriter;
    }
    
    @Override
    public void addChatComponentMessage(final IChatComponent chatComponent) {
        this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponent);
    }
    
    @Override
    protected boolean pushOutOfBlocks(final double x, final double y, final double z) {
        if (this.noClip) {
            return false;
        }
        final BlockPos blockpos = new BlockPos(x, y, z);
        final double d0 = x - blockpos.getX();
        final double d2 = z - blockpos.getZ();
        if (!this.isOpenBlockSpace(blockpos)) {
            int i = -1;
            double d3 = 9999.0;
            if (this.isOpenBlockSpace(blockpos.west()) && d0 < d3) {
                d3 = d0;
                i = 0;
            }
            if (this.isOpenBlockSpace(blockpos.east()) && 1.0 - d0 < d3) {
                d3 = 1.0 - d0;
                i = 1;
            }
            if (this.isOpenBlockSpace(blockpos.north()) && d2 < d3) {
                d3 = d2;
                i = 4;
            }
            if (this.isOpenBlockSpace(blockpos.south()) && 1.0 - d2 < d3) {
                d3 = 1.0 - d2;
                i = 5;
            }
            final float f = 0.1f;
            if (i == 0) {
                this.motionX = -f;
            }
            if (i == 1) {
                this.motionX = f;
            }
            if (i == 4) {
                this.motionZ = -f;
            }
            if (i == 5) {
                this.motionZ = f;
            }
        }
        return false;
    }
    
    private boolean isOpenBlockSpace(final BlockPos pos) {
        return !this.worldObj.getBlockState(pos).getBlock().isNormalCube() && !this.worldObj.getBlockState(pos.up()).getBlock().isNormalCube();
    }
    
    @Override
    public void setSprinting(final boolean sprinting) {
        super.setSprinting(sprinting);
        this.sprintingTicksLeft = (sprinting ? 600 : 0);
    }
    
    public void setXPStats(final float currentXP, final int maxXP, final int level) {
        this.experience = currentXP;
        this.experienceTotal = maxXP;
        this.experienceLevel = level;
    }
    
    @Override
    public void addChatMessage(final IChatComponent component) {
        this.mc.ingameGUI.getChatGUI().printChatMessage(component);
    }
    
    @Override
    public boolean canCommandSenderUseCommand(final int permLevel, final String commandName) {
        return permLevel <= 0;
    }
    
    @Override
    public BlockPos getPosition() {
        return new BlockPos(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5);
    }
    
    @Override
    public void playSound(final String name, final float volume, final float pitch) {
        this.worldObj.playSound(this.posX, this.posY, this.posZ, name, volume, pitch, false);
    }
    
    @Override
    public boolean isServerWorld() {
        return true;
    }
    
    public boolean isRidingHorse() {
        return this.ridingEntity != null && this.ridingEntity instanceof EntityHorse && ((EntityHorse)this.ridingEntity).isHorseSaddled();
    }
    
    public float getHorseJumpPower() {
        return this.horseJumpPower;
    }
    
    @Override
    public void openEditSign(final TileEntitySign signTile) {
        this.mc.displayGuiScreen(new GuiEditSign(signTile));
    }
    
    @Override
    public void openEditCommandBlock(final CommandBlockLogic cmdBlockLogic) {
        this.mc.displayGuiScreen(new GuiCommandBlock(cmdBlockLogic));
    }
    
    @Override
    public void displayGUIBook(final ItemStack bookStack) {
        final Item item = bookStack.getItem();
        if (item == Items.writable_book) {
            this.mc.displayGuiScreen(new GuiScreenBook(this, bookStack, true));
        }
    }
    
    @Override
    public void displayGUIChest(final IInventory chestInventory) {
        final String s = (chestInventory instanceof IInteractionObject) ? ((IInteractionObject)chestInventory).getGuiID() : "minecraft:container";
        if ("minecraft:chest".equals(s)) {
            this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
        }
        else if ("minecraft:hopper".equals(s)) {
            this.mc.displayGuiScreen(new GuiHopper(this.inventory, chestInventory));
        }
        else if ("minecraft:furnace".equals(s)) {
            this.mc.displayGuiScreen(new GuiFurnace(this.inventory, chestInventory));
        }
        else if ("minecraft:brewing_stand".equals(s)) {
            this.mc.displayGuiScreen(new GuiBrewingStand(this.inventory, chestInventory));
        }
        else if ("minecraft:beacon".equals(s)) {
            this.mc.displayGuiScreen(new GuiBeacon(this.inventory, chestInventory));
        }
        else if (!"minecraft:dispenser".equals(s) && !"minecraft:dropper".equals(s)) {
            this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
        }
        else {
            this.mc.displayGuiScreen(new GuiDispenser(this.inventory, chestInventory));
        }
    }
    
    @Override
    public void displayGUIHorse(final EntityHorse horse, final IInventory horseInventory) {
        this.mc.displayGuiScreen(new GuiScreenHorseInventory(this.inventory, horseInventory, horse));
    }
    
    @Override
    public void displayGui(final IInteractionObject guiOwner) {
        final String s = guiOwner.getGuiID();
        if ("minecraft:crafting_table".equals(s)) {
            this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj));
        }
        else if ("minecraft:enchanting_table".equals(s)) {
            this.mc.displayGuiScreen(new GuiEnchantment(this.inventory, this.worldObj, guiOwner));
        }
        else if ("minecraft:anvil".equals(s)) {
            this.mc.displayGuiScreen(new GuiRepair(this.inventory, this.worldObj));
        }
    }
    
    @Override
    public void displayVillagerTradeGui(final IMerchant villager) {
        this.mc.displayGuiScreen(new GuiMerchant(this.inventory, villager, this.worldObj));
    }
    
    @Override
    public void onCriticalHit(final Entity entityHit) {
        this.mc.effectRenderer.emitParticleAtEntity(entityHit, EnumParticleTypes.CRIT);
    }
    
    @Override
    public void onEnchantmentCritical(final Entity entityHit) {
        this.mc.effectRenderer.emitParticleAtEntity(entityHit, EnumParticleTypes.CRIT_MAGIC);
    }
    
    @Override
    public boolean isSneaking() {
        final boolean flag = this.movementInput != null && this.movementInput.sneak;
        return flag && !this.sleeping;
    }
    
    public void updateEntityActionState() {
        super.updateEntityActionState();
        if (this.isCurrentViewEntity()) {
            this.moveStrafing = this.movementInput.moveStrafe;
            this.moveForward = this.movementInput.moveForward;
            this.isJumping = this.movementInput.jump;
            this.prevRenderArmYaw = this.renderArmYaw;
            this.prevRenderArmPitch = this.renderArmPitch;
            this.renderArmPitch += (float)((this.rotationPitch - this.renderArmPitch) * 0.5);
            this.renderArmYaw += (float)((this.rotationYaw - this.renderArmYaw) * 0.5);
        }
    }
    
    protected boolean isCurrentViewEntity() {
        return this.mc.getRenderViewEntity() == this;
    }
    
    @Override
    public void onLivingUpdate() {
        if (this.sprintingTicksLeft > 0) {
            --this.sprintingTicksLeft;
            if (this.sprintingTicksLeft == 0) {
                this.setSprinting(false);
            }
        }
        if (this.sprintToggleTimer > 0) {
            --this.sprintToggleTimer;
        }
        this.prevTimeInPortal = this.timeInPortal;
        if (this.inPortal) {
            if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame()) {
                this.mc.displayGuiScreen(null);
            }
            if (this.timeInPortal == 0.0f) {
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), this.rand.nextFloat() * 0.4f + 0.8f));
            }
            this.timeInPortal += 0.0125f;
            if (this.timeInPortal >= 1.0f) {
                this.timeInPortal = 1.0f;
            }
            this.inPortal = false;
        }
        else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60) {
            this.timeInPortal += 0.006666667f;
            if (this.timeInPortal > 1.0f) {
                this.timeInPortal = 1.0f;
            }
        }
        else {
            if (this.timeInPortal > 0.0f) {
                this.timeInPortal -= 0.05f;
            }
            if (this.timeInPortal < 0.0f) {
                this.timeInPortal = 0.0f;
            }
        }
        if (this.timeUntilPortal > 0) {
            --this.timeUntilPortal;
        }
        final boolean flag = this.movementInput.jump;
        final boolean flag2 = this.movementInput.sneak;
        final float f = 0.8f;
        final boolean flag3 = this.movementInput.moveForward >= f;
        this.movementInput.updatePlayerMoveState();
        if (this.isUsingItem() && !this.isRiding()) {
            final UseItemEvent event = new UseItemEvent();
            RadiumClient.getInstance().getEventBus().post(event);
            if (!event.isCancelled()) {
                final MovementInput movementInput = this.movementInput;
                movementInput.moveStrafe *= 0.2f;
                final MovementInput movementInput2 = this.movementInput;
                movementInput2.moveForward *= 0.2f;
                this.sprintToggleTimer = 0;
            }
        }
        this.pushOutOfBlocks(this.posX - this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + this.width * 0.35);
        this.pushOutOfBlocks(this.posX - this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - this.width * 0.35);
        this.pushOutOfBlocks(this.posX + this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - this.width * 0.35);
        this.pushOutOfBlocks(this.posX + this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + this.width * 0.35);
        final boolean flag4 = this.getFoodStats().getFoodLevel() > 6.0f || this.capabilities.allowFlying;
        if (this.onGround && !flag2 && !flag3 && this.movementInput.moveForward >= f && !this.isSprinting() && flag4 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness)) {
            if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown()) {
                this.sprintToggleTimer = 7;
            }
            else {
                this.setSprinting(true);
            }
        }
        if (!this.isSprinting() && this.movementInput.moveForward >= f && flag4 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness) && this.mc.gameSettings.keyBindSprint.isKeyDown()) {
            this.setSprinting(true);
        }
        if (this.isSprinting() && (this.movementInput.moveForward < f || this.isCollidedHorizontally || !flag4)) {
            this.setSprinting(false);
        }
        if (this.capabilities.allowFlying) {
            if (this.mc.playerController.isSpectatorMode()) {
                if (!this.capabilities.isFlying) {
                    this.capabilities.isFlying = true;
                    this.sendPlayerAbilities();
                }
            }
            else if (!flag && this.movementInput.jump) {
                if (this.flyToggleTimer == 0) {
                    this.flyToggleTimer = 7;
                }
                else {
                    this.capabilities.isFlying = !this.capabilities.isFlying;
                    this.sendPlayerAbilities();
                    this.flyToggleTimer = 0;
                }
            }
        }
        if (this.capabilities.isFlying && this.isCurrentViewEntity()) {
            if (this.movementInput.sneak) {
                this.motionY -= this.capabilities.getFlySpeed() * 3.0f;
            }
            if (this.movementInput.jump) {
                this.motionY += this.capabilities.getFlySpeed() * 3.0f;
            }
        }
        if (this.isRidingHorse()) {
            if (this.horseJumpPowerCounter < 0) {
                ++this.horseJumpPowerCounter;
                if (this.horseJumpPowerCounter == 0) {
                    this.horseJumpPower = 0.0f;
                }
            }
            if (flag && !this.movementInput.jump) {
                this.horseJumpPowerCounter = -10;
                this.sendHorseJump();
            }
            else if (!flag && this.movementInput.jump) {
                this.horseJumpPowerCounter = 0;
                this.horseJumpPower = 0.0f;
            }
            else if (flag) {
                ++this.horseJumpPowerCounter;
                if (this.horseJumpPowerCounter < 10) {
                    this.horseJumpPower = this.horseJumpPowerCounter * 0.1f;
                }
                else {
                    this.horseJumpPower = 0.8f + 2.0f / (this.horseJumpPowerCounter - 9) * 0.1f;
                }
            }
        }
        else {
            this.horseJumpPower = 0.0f;
        }
        super.onLivingUpdate();
        if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
            this.capabilities.isFlying = false;
            this.sendPlayerAbilities();
        }
    }
}
