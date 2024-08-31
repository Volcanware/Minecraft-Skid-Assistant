package net.optifine.shaders.config;

import net.optifine.Lang;
import net.optifine.shaders.ShaderUtils;
import net.optifine.shaders.Shaders;

import java.util.ArrayList;
import java.util.List;

public class ShaderOptionProfile extends ShaderOption {
    private ShaderProfile[] profiles = null;
    private ShaderOption[] options = null;
    private static final String NAME_PROFILE = "<profile>";
    private static final String VALUE_CUSTOM = "<custom>";

    public ShaderOptionProfile(final ShaderProfile[] profiles, final ShaderOption[] options) {
        super("<profile>", "", detectProfileName(profiles, options), getProfileNames(profiles), detectProfileName(profiles, options, true), null);
        this.profiles = profiles;
        this.options = options;
    }

    public void nextValue() {
        super.nextValue();

        if (this.getValue().equals("<custom>")) {
            super.nextValue();
        }

        this.applyProfileOptions();
    }

    public void updateProfile() {
        final ShaderProfile shaderprofile = this.getProfile(this.getValue());

        if (shaderprofile == null || !ShaderUtils.matchProfile(shaderprofile, this.options, false)) {
            final String s = detectProfileName(this.profiles, this.options);
            this.setValue(s);
        }
    }

    private void applyProfileOptions() {
        final ShaderProfile shaderprofile = this.getProfile(this.getValue());

        if (shaderprofile != null) {
            final String[] astring = shaderprofile.getOptions();

            for (int i = 0; i < astring.length; ++i) {
                final String s = astring[i];
                final ShaderOption shaderoption = this.getOption(s);

                if (shaderoption != null) {
                    final String s1 = shaderprofile.getValue(s);
                    shaderoption.setValue(s1);
                }
            }
        }
    }

    private ShaderOption getOption(final String name) {
        for (int i = 0; i < this.options.length; ++i) {
            final ShaderOption shaderoption = this.options[i];

            if (shaderoption.getName().equals(name)) {
                return shaderoption;
            }
        }

        return null;
    }

    private ShaderProfile getProfile(final String name) {
        for (int i = 0; i < this.profiles.length; ++i) {
            final ShaderProfile shaderprofile = this.profiles[i];

            if (shaderprofile.getName().equals(name)) {
                return shaderprofile;
            }
        }

        return null;
    }

    public String getNameText() {
        return Lang.get("of.shaders.profile");
    }

    public String getValueText(final String val) {
        return val.equals("<custom>") ? Lang.get("of.general.custom", "<custom>") : Shaders.translate("profile." + val, val);
    }

    public String getValueColor(final String val) {
        return val.equals("<custom>") ? "\u00a7c" : "\u00a7a";
    }

    public String getDescriptionText() {
        final String s = Shaders.translate("profile.comment", null);

        if (s != null) {
            return s;
        } else {
            final StringBuffer stringbuffer = new StringBuffer();

            for (int i = 0; i < this.profiles.length; ++i) {
                final String s1 = this.profiles[i].getName();

                if (s1 != null) {
                    final String s2 = Shaders.translate("profile." + s1 + ".comment", null);

                    if (s2 != null) {
                        stringbuffer.append(s2);

                        if (!s2.endsWith(". ")) {
                            stringbuffer.append(". ");
                        }
                    }
                }
            }

            return stringbuffer.toString();
        }
    }

    private static String detectProfileName(final ShaderProfile[] profs, final ShaderOption[] opts) {
        return detectProfileName(profs, opts, false);
    }

    private static String detectProfileName(final ShaderProfile[] profs, final ShaderOption[] opts, final boolean def) {
        final ShaderProfile shaderprofile = ShaderUtils.detectProfile(profs, opts, def);
        return shaderprofile == null ? "<custom>" : shaderprofile.getName();
    }

    private static String[] getProfileNames(final ShaderProfile[] profs) {
        final List<String> list = new ArrayList();

        for (int i = 0; i < profs.length; ++i) {
            final ShaderProfile shaderprofile = profs[i];
            list.add(shaderprofile.getName());
        }

        list.add("<custom>");
        final String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }
}
