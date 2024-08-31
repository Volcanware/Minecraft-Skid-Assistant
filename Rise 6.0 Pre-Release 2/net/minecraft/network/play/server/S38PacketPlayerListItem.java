package net.minecraft.network.play.server;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

import java.io.IOException;
import java.util.List;

public class S38PacketPlayerListItem implements Packet<INetHandlerPlayClient> {
    private S38PacketPlayerListItem.Action action;
    private final List<S38PacketPlayerListItem.AddPlayerData> players = Lists.newArrayList();

    public S38PacketPlayerListItem() {
    }

    public S38PacketPlayerListItem(final S38PacketPlayerListItem.Action actionIn, final EntityPlayerMP... players) {
        this.action = actionIn;

        for (final EntityPlayerMP entityplayermp : players) {
            this.players.add(new S38PacketPlayerListItem.AddPlayerData(entityplayermp.getGameProfile(), entityplayermp.ping, entityplayermp.theItemInWorldManager.getGameType(), entityplayermp.getTabListDisplayName()));
        }
    }

    public S38PacketPlayerListItem(final S38PacketPlayerListItem.Action actionIn, final Iterable<EntityPlayerMP> players) {
        this.action = actionIn;

        for (final EntityPlayerMP entityplayermp : players) {
            this.players.add(new S38PacketPlayerListItem.AddPlayerData(entityplayermp.getGameProfile(), entityplayermp.ping, entityplayermp.theItemInWorldManager.getGameType(), entityplayermp.getTabListDisplayName()));
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.action = buf.readEnumValue(Action.class);
        final int i = buf.readVarIntFromBuffer();

        for (int j = 0; j < i; ++j) {
            GameProfile gameprofile = null;
            int k = 0;
            WorldSettings.GameType worldsettings$gametype = null;
            IChatComponent ichatcomponent = null;

            switch (this.action) {
                case ADD_PLAYER:
                    gameprofile = new GameProfile(buf.readUuid(), buf.readStringFromBuffer(16));
                    final int l = buf.readVarIntFromBuffer();
                    int i1 = 0;

                    for (; i1 < l; ++i1) {
                        final String s = buf.readStringFromBuffer(32767);
                        final String s1 = buf.readStringFromBuffer(32767);

                        if (buf.readBoolean()) {
                            gameprofile.getProperties().put(s, new Property(s, s1, buf.readStringFromBuffer(32767)));
                        } else {
                            gameprofile.getProperties().put(s, new Property(s, s1));
                        }
                    }

                    worldsettings$gametype = WorldSettings.GameType.getByID(buf.readVarIntFromBuffer());
                    k = buf.readVarIntFromBuffer();

                    if (buf.readBoolean()) {
                        ichatcomponent = buf.readChatComponent();
                    }

                    break;

                case UPDATE_GAME_MODE:
                    gameprofile = new GameProfile(buf.readUuid(), null);
                    worldsettings$gametype = WorldSettings.GameType.getByID(buf.readVarIntFromBuffer());
                    break;

                case UPDATE_LATENCY:
                    gameprofile = new GameProfile(buf.readUuid(), null);
                    k = buf.readVarIntFromBuffer();
                    break;

                case UPDATE_DISPLAY_NAME:
                    gameprofile = new GameProfile(buf.readUuid(), null);

                    if (buf.readBoolean()) {
                        ichatcomponent = buf.readChatComponent();
                    }

                    break;

                case REMOVE_PLAYER:
                    gameprofile = new GameProfile(buf.readUuid(), null);
            }

            this.players.add(new S38PacketPlayerListItem.AddPlayerData(gameprofile, k, worldsettings$gametype, ichatcomponent));
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeEnumValue(this.action);
        buf.writeVarIntToBuffer(this.players.size());

        for (final S38PacketPlayerListItem.AddPlayerData s38packetplayerlistitem$addplayerdata : this.players) {
            switch (this.action) {
                case ADD_PLAYER:
                    buf.writeUuid(s38packetplayerlistitem$addplayerdata.getProfile().getId());
                    buf.writeString(s38packetplayerlistitem$addplayerdata.getProfile().getName());
                    buf.writeVarIntToBuffer(s38packetplayerlistitem$addplayerdata.getProfile().getProperties().size());

                    for (final Property property : s38packetplayerlistitem$addplayerdata.getProfile().getProperties().values()) {
                        buf.writeString(property.getName());
                        buf.writeString(property.getValue());

                        if (property.hasSignature()) {
                            buf.writeBoolean(true);
                            buf.writeString(property.getSignature());
                        } else {
                            buf.writeBoolean(false);
                        }
                    }

                    buf.writeVarIntToBuffer(s38packetplayerlistitem$addplayerdata.getGameMode().getID());
                    buf.writeVarIntToBuffer(s38packetplayerlistitem$addplayerdata.getPing());

                    if (s38packetplayerlistitem$addplayerdata.getDisplayName() == null) {
                        buf.writeBoolean(false);
                    } else {
                        buf.writeBoolean(true);
                        buf.writeChatComponent(s38packetplayerlistitem$addplayerdata.getDisplayName());
                    }

                    break;

                case UPDATE_GAME_MODE:
                    buf.writeUuid(s38packetplayerlistitem$addplayerdata.getProfile().getId());
                    buf.writeVarIntToBuffer(s38packetplayerlistitem$addplayerdata.getGameMode().getID());
                    break;

                case UPDATE_LATENCY:
                    buf.writeUuid(s38packetplayerlistitem$addplayerdata.getProfile().getId());
                    buf.writeVarIntToBuffer(s38packetplayerlistitem$addplayerdata.getPing());
                    break;

                case UPDATE_DISPLAY_NAME:
                    buf.writeUuid(s38packetplayerlistitem$addplayerdata.getProfile().getId());

                    if (s38packetplayerlistitem$addplayerdata.getDisplayName() == null) {
                        buf.writeBoolean(false);
                    } else {
                        buf.writeBoolean(true);
                        buf.writeChatComponent(s38packetplayerlistitem$addplayerdata.getDisplayName());
                    }

                    break;

                case REMOVE_PLAYER:
                    buf.writeUuid(s38packetplayerlistitem$addplayerdata.getProfile().getId());
            }
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handlePlayerListItem(this);
    }

    public List<S38PacketPlayerListItem.AddPlayerData> func_179767_a() {
        return this.players;
    }

    public S38PacketPlayerListItem.Action func_179768_b() {
        return this.action;
    }

    public String toString() {
        return Objects.toStringHelper(this).add("action", this.action).add("entries", this.players).toString();
    }

    public enum Action {
        ADD_PLAYER,
        UPDATE_GAME_MODE,
        UPDATE_LATENCY,
        UPDATE_DISPLAY_NAME,
        REMOVE_PLAYER
    }

    public class AddPlayerData {
        private final int ping;
        private final WorldSettings.GameType gamemode;
        private final GameProfile profile;
        private final IChatComponent displayName;

        public AddPlayerData(final GameProfile profile, final int pingIn, final WorldSettings.GameType gamemodeIn, final IChatComponent displayNameIn) {
            this.profile = profile;
            this.ping = pingIn;
            this.gamemode = gamemodeIn;
            this.displayName = displayNameIn;
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public int getPing() {
            return this.ping;
        }

        public WorldSettings.GameType getGameMode() {
            return this.gamemode;
        }

        public IChatComponent getDisplayName() {
            return this.displayName;
        }

        public String toString() {
            return Objects.toStringHelper(this).add("latency", this.ping).add("gameMode", this.gamemode).add("profile", this.profile).add("displayName", this.displayName == null ? null : IChatComponent.Serializer.componentToJson(this.displayName)).toString();
        }
    }
}
