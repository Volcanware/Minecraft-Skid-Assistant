// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.network.play.server;

import net.minecraft.network.INetHandler;
import java.io.IOException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.Packet;

public class S44PacketWorldBorder implements Packet<INetHandlerPlayClient>
{
    private Action action;
    private int size;
    private double centerX;
    private double centerZ;
    private double targetSize;
    private double diameter;
    private long timeUntilTarget;
    private int warningTime;
    private int warningDistance;
    
    public S44PacketWorldBorder() {
    }
    
    public S44PacketWorldBorder(final WorldBorder border, final Action actionIn) {
        this.action = actionIn;
        this.centerX = border.getCenterX();
        this.centerZ = border.getCenterZ();
        this.diameter = border.getDiameter();
        this.targetSize = border.getTargetSize();
        this.timeUntilTarget = border.getTimeUntilTarget();
        this.size = border.getSize();
        this.warningDistance = border.getWarningDistance();
        this.warningTime = border.getWarningTime();
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.action = buf.readEnumValue(Action.class);
        switch (this.action) {
            case SET_SIZE: {
                this.targetSize = buf.readDouble();
                break;
            }
            case LERP_SIZE: {
                this.diameter = buf.readDouble();
                this.targetSize = buf.readDouble();
                this.timeUntilTarget = buf.readVarLong();
                break;
            }
            case SET_CENTER: {
                this.centerX = buf.readDouble();
                this.centerZ = buf.readDouble();
                break;
            }
            case SET_WARNING_BLOCKS: {
                this.warningDistance = buf.readVarIntFromBuffer();
                break;
            }
            case SET_WARNING_TIME: {
                this.warningTime = buf.readVarIntFromBuffer();
                break;
            }
            case INITIALIZE: {
                this.centerX = buf.readDouble();
                this.centerZ = buf.readDouble();
                this.diameter = buf.readDouble();
                this.targetSize = buf.readDouble();
                this.timeUntilTarget = buf.readVarLong();
                this.size = buf.readVarIntFromBuffer();
                this.warningDistance = buf.readVarIntFromBuffer();
                this.warningTime = buf.readVarIntFromBuffer();
                break;
            }
        }
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeEnumValue(this.action);
        switch (this.action) {
            case SET_SIZE: {
                buf.writeDouble(this.targetSize);
                break;
            }
            case LERP_SIZE: {
                buf.writeDouble(this.diameter);
                buf.writeDouble(this.targetSize);
                buf.writeVarLong(this.timeUntilTarget);
                break;
            }
            case SET_CENTER: {
                buf.writeDouble(this.centerX);
                buf.writeDouble(this.centerZ);
                break;
            }
            case SET_WARNING_BLOCKS: {
                buf.writeVarIntToBuffer(this.warningDistance);
                break;
            }
            case SET_WARNING_TIME: {
                buf.writeVarIntToBuffer(this.warningTime);
                break;
            }
            case INITIALIZE: {
                buf.writeDouble(this.centerX);
                buf.writeDouble(this.centerZ);
                buf.writeDouble(this.diameter);
                buf.writeDouble(this.targetSize);
                buf.writeVarLong(this.timeUntilTarget);
                buf.writeVarIntToBuffer(this.size);
                buf.writeVarIntToBuffer(this.warningDistance);
                buf.writeVarIntToBuffer(this.warningTime);
                break;
            }
        }
    }
    
    @Override
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleWorldBorder(this);
    }
    
    public void func_179788_a(final WorldBorder border) {
        switch (this.action) {
            case SET_SIZE: {
                border.setTransition(this.targetSize);
                break;
            }
            case LERP_SIZE: {
                border.setTransition(this.diameter, this.targetSize, this.timeUntilTarget);
                break;
            }
            case SET_CENTER: {
                border.setCenter(this.centerX, this.centerZ);
                break;
            }
            case SET_WARNING_BLOCKS: {
                border.setWarningDistance(this.warningDistance);
                break;
            }
            case SET_WARNING_TIME: {
                border.setWarningTime(this.warningTime);
                break;
            }
            case INITIALIZE: {
                border.setCenter(this.centerX, this.centerZ);
                if (this.timeUntilTarget > 0L) {
                    border.setTransition(this.diameter, this.targetSize, this.timeUntilTarget);
                }
                else {
                    border.setTransition(this.targetSize);
                }
                border.setSize(this.size);
                border.setWarningDistance(this.warningDistance);
                border.setWarningTime(this.warningTime);
                break;
            }
        }
    }
    
    public enum Action
    {
        SET_SIZE("SET_SIZE", 0), 
        LERP_SIZE("LERP_SIZE", 1), 
        SET_CENTER("SET_CENTER", 2), 
        INITIALIZE("INITIALIZE", 3), 
        SET_WARNING_TIME("SET_WARNING_TIME", 4), 
        SET_WARNING_BLOCKS("SET_WARNING_BLOCKS", 5);
        
        private Action(final String name, final int ordinal) {
        }
    }
}
