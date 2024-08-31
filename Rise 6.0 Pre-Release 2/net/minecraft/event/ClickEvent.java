package net.minecraft.event;

import com.google.common.collect.Maps;

import java.util.Map;

public class ClickEvent {
    private final ClickEvent.Action action;
    private final String value;

    public ClickEvent(final ClickEvent.Action theAction, final String theValue) {
        this.action = theAction;
        this.value = theValue;
    }

    /**
     * Gets the action to perform when this event is raised.
     */
    public ClickEvent.Action getAction() {
        return this.action;
    }

    /**
     * Gets the value to perform the action on when this event is raised.  For example, if the action is "open URL",
     * this would be the URL to open.
     */
    public String getValue() {
        return this.value;
    }

    public boolean equals(final Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            final ClickEvent clickevent = (ClickEvent) p_equals_1_;

            if (this.action != clickevent.action) {
                return false;
            } else {
                if (this.value != null) {
                    return this.value.equals(clickevent.value);
                } else return clickevent.value == null;
            }
        } else {
            return false;
        }
    }

    public String toString() {
        return "ClickEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
    }

    public int hashCode() {
        int i = this.action.hashCode();
        i = 31 * i + (this.value != null ? this.value.hashCode() : 0);
        return i;
    }

    public enum Action {
        OPEN_URL("open_url", true),
        OPEN_FILE("open_file", false),
        RUN_COMMAND("run_command", true),
        TWITCH_USER_INFO("twitch_user_info", false),
        SUGGEST_COMMAND("suggest_command", true),
        CHANGE_PAGE("change_page", true);

        private static final Map<String, ClickEvent.Action> nameMapping = Maps.newHashMap();
        private final boolean allowedInChat;
        private final String canonicalName;

        Action(final String canonicalNameIn, final boolean allowedInChatIn) {
            this.canonicalName = canonicalNameIn;
            this.allowedInChat = allowedInChatIn;
        }

        public boolean shouldAllowInChat() {
            return this.allowedInChat;
        }

        public String getCanonicalName() {
            return this.canonicalName;
        }

        public static ClickEvent.Action getValueByCanonicalName(final String canonicalNameIn) {
            return nameMapping.get(canonicalNameIn);
        }

        static {
            for (final ClickEvent.Action clickevent$action : values()) {
                nameMapping.put(clickevent$action.getCanonicalName(), clickevent$action);
            }
        }
    }
}
