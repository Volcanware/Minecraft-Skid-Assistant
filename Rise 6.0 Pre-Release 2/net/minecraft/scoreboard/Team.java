package net.minecraft.scoreboard;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

public abstract class Team {
    /**
     * Same as ==
     */
    public boolean isSameTeam(final Team other) {
        return other != null && this == other;
    }

    /**
     * Retrieve the name by which this team is registered in the scoreboard
     */
    public abstract String getRegisteredName();

    public abstract String formatString(String input);

    public abstract boolean getSeeFriendlyInvisiblesEnabled();

    public abstract boolean getAllowFriendlyFire();

    public abstract Team.EnumVisible getNameTagVisibility();

    public abstract Collection<String> getMembershipCollection();

    public abstract Team.EnumVisible getDeathMessageVisibility();

    public enum EnumVisible {
        ALWAYS("always", 0),
        NEVER("never", 1),
        HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
        HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

        private static final Map<String, Team.EnumVisible> field_178828_g = Maps.newHashMap();
        public final String field_178830_e;
        public final int field_178827_f;

        public static String[] func_178825_a() {
            return field_178828_g.keySet().toArray(new String[field_178828_g.size()]);
        }

        public static Team.EnumVisible func_178824_a(final String p_178824_0_) {
            return field_178828_g.get(p_178824_0_);
        }

        EnumVisible(final String p_i45550_3_, final int p_i45550_4_) {
            this.field_178830_e = p_i45550_3_;
            this.field_178827_f = p_i45550_4_;
        }

        static {
            for (final Team.EnumVisible team$enumvisible : values()) {
                field_178828_g.put(team$enumvisible.field_178830_e, team$enumvisible);
            }
        }
    }
}
