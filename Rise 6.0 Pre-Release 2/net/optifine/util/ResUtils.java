package net.optifine.util;

import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResUtils {
    public static String[] collectFiles(final String prefix, final String suffix) {
        return collectFiles(new String[]{prefix}, new String[]{suffix});
    }

    public static String[] collectFiles(final String[] prefixes, final String[] suffixes) {
        final Set<String> set = new LinkedHashSet();
        final IResourcePack[] airesourcepack = Config.getResourcePacks();

        for (int i = 0; i < airesourcepack.length; ++i) {
            final IResourcePack iresourcepack = airesourcepack[i];
            final String[] astring = collectFiles(iresourcepack, prefixes, suffixes, null);
            set.addAll(Arrays.asList(astring));
        }

        final String[] astring1 = set.toArray(new String[set.size()]);
        return astring1;
    }

    public static String[] collectFiles(final IResourcePack rp, final String prefix, final String suffix, final String[] defaultPaths) {
        return collectFiles(rp, new String[]{prefix}, new String[]{suffix}, defaultPaths);
    }

    public static String[] collectFiles(final IResourcePack rp, final String[] prefixes, final String[] suffixes) {
        return collectFiles(rp, prefixes, suffixes, null);
    }

    public static String[] collectFiles(final IResourcePack rp, final String[] prefixes, final String[] suffixes, final String[] defaultPaths) {
        if (rp instanceof DefaultResourcePack) {
            return collectFilesFixed(rp, defaultPaths);
        } else if (!(rp instanceof AbstractResourcePack)) {
            Config.warn("Unknown resource pack type: " + rp);
            return new String[0];
        } else {
            final AbstractResourcePack abstractresourcepack = (AbstractResourcePack) rp;
            final File file1 = abstractresourcepack.resourcePackFile;

            if (file1 == null) {
                return new String[0];
            } else if (file1.isDirectory()) {
                return collectFilesFolder(file1, "", prefixes, suffixes);
            } else if (file1.isFile()) {
                return collectFilesZIP(file1, prefixes, suffixes);
            } else {
                Config.warn("Unknown resource pack file: " + file1);
                return new String[0];
            }
        }
    }

    private static String[] collectFilesFixed(final IResourcePack rp, final String[] paths) {
        if (paths == null) {
            return new String[0];
        } else {
            final List list = new ArrayList();

            for (int i = 0; i < paths.length; ++i) {
                final String s = paths[i];
                final ResourceLocation resourcelocation = new ResourceLocation(s);

                if (rp.resourceExists(resourcelocation)) {
                    list.add(s);
                }
            }

            final String[] astring = (String[]) list.toArray(new String[list.size()]);
            return astring;
        }
    }

    private static String[] collectFilesFolder(final File tpFile, final String basePath, final String[] prefixes, final String[] suffixes) {
        final List list = new ArrayList();
        final String s = "assets/minecraft/";
        final File[] afile = tpFile.listFiles();

        if (afile == null) {
            return new String[0];
        } else {
            for (int i = 0; i < afile.length; ++i) {
                final File file1 = afile[i];

                if (file1.isFile()) {
                    String s3 = basePath + file1.getName();

                    if (s3.startsWith(s)) {
                        s3 = s3.substring(s.length());

                        if (StrUtils.startsWith(s3, prefixes) && StrUtils.endsWith(s3, suffixes)) {
                            list.add(s3);
                        }
                    }
                } else if (file1.isDirectory()) {
                    final String s1 = basePath + file1.getName() + "/";
                    final String[] astring = collectFilesFolder(file1, s1, prefixes, suffixes);

                    for (int j = 0; j < astring.length; ++j) {
                        final String s2 = astring[j];
                        list.add(s2);
                    }
                }
            }

            final String[] astring1 = (String[]) list.toArray(new String[list.size()]);
            return astring1;
        }
    }

    private static String[] collectFilesZIP(final File tpFile, final String[] prefixes, final String[] suffixes) {
        final List list = new ArrayList();
        final String s = "assets/minecraft/";

        try {
            final ZipFile zipfile = new ZipFile(tpFile);
            final Enumeration enumeration = zipfile.entries();

            while (enumeration.hasMoreElements()) {
                final ZipEntry zipentry = (ZipEntry) enumeration.nextElement();
                String s1 = zipentry.getName();

                if (s1.startsWith(s)) {
                    s1 = s1.substring(s.length());

                    if (StrUtils.startsWith(s1, prefixes) && StrUtils.endsWith(s1, suffixes)) {
                        list.add(s1);
                    }
                }
            }

            zipfile.close();
            final String[] astring = (String[]) list.toArray(new String[list.size()]);
            return astring;
        } catch (final IOException ioexception) {
            ioexception.printStackTrace();
            return new String[0];
        }
    }

    private static boolean isLowercase(final String str) {
        return str.equals(str.toLowerCase(Locale.ROOT));
    }

    public static Properties readProperties(final String path, final String module) {
        final ResourceLocation resourcelocation = new ResourceLocation(path);

        try {
            final InputStream inputstream = Config.getResourceStream(resourcelocation);

            if (inputstream == null) {
                return null;
            } else {
                final Properties properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                Config.dbg("" + module + ": Loading " + path);
                return properties;
            }
        } catch (final FileNotFoundException var5) {
            return null;
        } catch (final IOException var6) {
            Config.warn("" + module + ": Error reading " + path);
            return null;
        }
    }

    public static Properties readProperties(final InputStream in, final String module) {
        if (in == null) {
            return null;
        } else {
            try {
                final Properties properties = new PropertiesOrdered();
                properties.load(in);
                in.close();
                return properties;
            } catch (final FileNotFoundException var3) {
                return null;
            } catch (final IOException var4) {
                return null;
            }
        }
    }
}
