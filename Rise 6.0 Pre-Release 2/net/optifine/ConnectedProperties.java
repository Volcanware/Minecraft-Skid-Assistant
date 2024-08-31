package net.optifine;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.*;
import net.optifine.util.MathUtils;
import net.optifine.util.TextureUtils;

import java.util.*;

public class ConnectedProperties {
    public String name = null;
    public String basePath = null;
    public MatchBlock[] matchBlocks = null;
    public int[] metadatas = null;
    public String[] matchTiles = null;
    public int method = 0;
    public String[] tiles = null;
    public int connect = 0;
    public int faces = 63;
    public BiomeGenBase[] biomes = null;
    public RangeListInt heights = null;
    public int renderPass = 0;
    public boolean innerSeams = false;
    public int[] ctmTileIndexes = null;
    public int width = 0;
    public int height = 0;
    public int[] weights = null;
    public int randomLoops = 0;
    public int symmetry = 1;
    public boolean linked = false;
    public NbtTagValue nbtName = null;
    public int[] sumWeights = null;
    public int sumAllWeights = 1;
    public TextureAtlasSprite[] matchTileIcons = null;
    public TextureAtlasSprite[] tileIcons = null;
    public MatchBlock[] connectBlocks = null;
    public String[] connectTiles = null;
    public TextureAtlasSprite[] connectTileIcons = null;
    public int tintIndex = -1;
    public IBlockState tintBlockState = Blocks.air.getDefaultState();
    public EnumWorldBlockLayer layer = null;
    public static final int METHOD_NONE = 0;
    public static final int METHOD_CTM = 1;
    public static final int METHOD_HORIZONTAL = 2;
    public static final int METHOD_TOP = 3;
    public static final int METHOD_RANDOM = 4;
    public static final int METHOD_REPEAT = 5;
    public static final int METHOD_VERTICAL = 6;
    public static final int METHOD_FIXED = 7;
    public static final int METHOD_HORIZONTAL_VERTICAL = 8;
    public static final int METHOD_VERTICAL_HORIZONTAL = 9;
    public static final int METHOD_CTM_COMPACT = 10;
    public static final int METHOD_OVERLAY = 11;
    public static final int METHOD_OVERLAY_FIXED = 12;
    public static final int METHOD_OVERLAY_RANDOM = 13;
    public static final int METHOD_OVERLAY_REPEAT = 14;
    public static final int METHOD_OVERLAY_CTM = 15;
    public static final int CONNECT_NONE = 0;
    public static final int CONNECT_BLOCK = 1;
    public static final int CONNECT_TILE = 2;
    public static final int CONNECT_MATERIAL = 3;
    public static final int CONNECT_UNKNOWN = 128;
    public static final int FACE_BOTTOM = 1;
    public static final int FACE_TOP = 2;
    public static final int FACE_NORTH = 4;
    public static final int FACE_SOUTH = 8;
    public static final int FACE_WEST = 16;
    public static final int FACE_EAST = 32;
    public static final int FACE_SIDES = 60;
    public static final int FACE_ALL = 63;
    public static final int FACE_UNKNOWN = 128;
    public static final int SYMMETRY_NONE = 1;
    public static final int SYMMETRY_OPPOSITE = 2;
    public static final int SYMMETRY_ALL = 6;
    public static final int SYMMETRY_UNKNOWN = 128;
    public static final String TILE_SKIP_PNG = "<skip>.png";
    public static final String TILE_DEFAULT_PNG = "<default>.png";

    public ConnectedProperties(final Properties props, final String path) {
        final ConnectedParser connectedparser = new ConnectedParser("ConnectedTextures");
        this.name = connectedparser.parseName(path);
        this.basePath = connectedparser.parseBasePath(path);
        this.matchBlocks = connectedparser.parseMatchBlocks(props.getProperty("matchBlocks"));
        this.metadatas = connectedparser.parseIntList(props.getProperty("metadata"));
        this.matchTiles = this.parseMatchTiles(props.getProperty("matchTiles"));
        this.method = parseMethod(props.getProperty("method"));
        this.tiles = this.parseTileNames(props.getProperty("tiles"));
        this.connect = parseConnect(props.getProperty("connect"));
        this.faces = parseFaces(props.getProperty("faces"));
        this.biomes = connectedparser.parseBiomes(props.getProperty("biomes"));
        this.heights = connectedparser.parseRangeListInt(props.getProperty("heights"));

        if (this.heights == null) {
            final int i = connectedparser.parseInt(props.getProperty("minHeight"), -1);
            final int j = connectedparser.parseInt(props.getProperty("maxHeight"), 1024);

            if (i != -1 || j != 1024) {
                this.heights = new RangeListInt(new RangeInt(i, j));
            }
        }

        this.renderPass = connectedparser.parseInt(props.getProperty("renderPass"), -1);
        this.innerSeams = connectedparser.parseBoolean(props.getProperty("innerSeams"), false);
        this.ctmTileIndexes = this.parseCtmTileIndexes(props);
        this.width = connectedparser.parseInt(props.getProperty("width"), -1);
        this.height = connectedparser.parseInt(props.getProperty("height"), -1);
        this.weights = connectedparser.parseIntList(props.getProperty("weights"));
        this.randomLoops = connectedparser.parseInt(props.getProperty("randomLoops"), 0);
        this.symmetry = parseSymmetry(props.getProperty("symmetry"));
        this.linked = connectedparser.parseBoolean(props.getProperty("linked"), false);
        this.nbtName = connectedparser.parseNbtTagValue("name", props.getProperty("name"));
        this.connectBlocks = connectedparser.parseMatchBlocks(props.getProperty("connectBlocks"));
        this.connectTiles = this.parseMatchTiles(props.getProperty("connectTiles"));
        this.tintIndex = connectedparser.parseInt(props.getProperty("tintIndex"), -1);
        this.tintBlockState = connectedparser.parseBlockState(props.getProperty("tintBlock"), Blocks.air.getDefaultState());
        this.layer = connectedparser.parseBlockRenderLayer(props.getProperty("layer"), EnumWorldBlockLayer.CUTOUT_MIPPED);
    }

    private int[] parseCtmTileIndexes(final Properties props) {
        if (this.tiles == null) {
            return null;
        } else {
            final Map<Integer, Integer> map = new HashMap();

            for (final Object object : props.keySet()) {
                if (object instanceof String) {
                    final String s = (String) object;
                    final String s1 = "ctm.";

                    if (s.startsWith(s1)) {
                        final String s2 = s.substring(s1.length());
                        String s3 = props.getProperty(s);

                        if (s3 != null) {
                            s3 = s3.trim();
                            final int i = Config.parseInt(s2, -1);

                            if (i >= 0 && i <= 46) {
                                final int j = Config.parseInt(s3, -1);

                                if (j >= 0 && j < this.tiles.length) {
                                    map.put(i, j);
                                } else {
                                    Config.warn("Invalid CTM tile index: " + s3);
                                }
                            } else {
                                Config.warn("Invalid CTM index: " + s2);
                            }
                        }
                    }
                }
            }

            if (map.isEmpty()) {
                return null;
            } else {
                final int[] aint = new int[47];

                for (int k = 0; k < aint.length; ++k) {
                    aint[k] = -1;

                    if (map.containsKey(k)) {
                        aint[k] = map.get(k);
                    }
                }

                return aint;
            }
        }
    }

    private String[] parseMatchTiles(final String str) {
        if (str == null) {
            return null;
        } else {
            final String[] astring = Config.tokenize(str, " ");

            for (int i = 0; i < astring.length; ++i) {
                String s = astring[i];

                if (s.endsWith(".png")) {
                    s = s.substring(0, s.length() - 4);
                }

                s = TextureUtils.fixResourcePath(s, this.basePath);
                astring[i] = s;
            }

            return astring;
        }
    }

    private static String parseName(final String path) {
        String s = path;
        final int i = path.lastIndexOf(47);

        if (i >= 0) {
            s = path.substring(i + 1);
        }

        final int j = s.lastIndexOf(46);

        if (j >= 0) {
            s = s.substring(0, j);
        }

        return s;
    }

    private static String parseBasePath(final String path) {
        final int i = path.lastIndexOf(47);
        return i < 0 ? "" : path.substring(0, i);
    }

    private String[] parseTileNames(final String str) {
        if (str == null) {
            return null;
        } else {
            final List list = new ArrayList();
            final String[] astring = Config.tokenize(str, " ,");
            label32:

            for (final String s : astring) {
                if (s.contains("-")) {
                    final String[] astring1 = Config.tokenize(s, "-");

                    if (astring1.length == 2) {
                        final int j = Config.parseInt(astring1[0], -1);
                        final int k = Config.parseInt(astring1[1], -1);

                        if (j >= 0 && k >= 0) {
                            if (j > k) {
                                Config.warn("Invalid interval: " + s + ", when parsing: " + str);
                                continue;
                            }

                            int l = j;

                            while (true) {
                                if (l > k) {
                                    continue label32;
                                }

                                list.add(String.valueOf(l));
                                ++l;
                            }
                        }
                    }
                }

                list.add(s);
            }

            final String[] astring2 = (String[]) list.toArray(new String[list.size()]);

            for (int i1 = 0; i1 < astring2.length; ++i1) {
                String s1 = astring2[i1];
                s1 = TextureUtils.fixResourcePath(s1, this.basePath);

                if (!s1.startsWith(this.basePath) && !s1.startsWith("textures/") && !s1.startsWith("mcpatcher/")) {
                    s1 = this.basePath + "/" + s1;
                }

                if (s1.endsWith(".png")) {
                    s1 = s1.substring(0, s1.length() - 4);
                }

                if (s1.startsWith("/")) {
                    s1 = s1.substring(1);
                }

                astring2[i1] = s1;
            }

            return astring2;
        }
    }

    private static int parseSymmetry(String str) {
        if (str == null) {
            return 1;
        } else {
            str = str.trim();

            if (str.equals("opposite")) {
                return 2;
            } else if (str.equals("all")) {
                return 6;
            } else {
                Config.warn("Unknown symmetry: " + str);
                return 1;
            }
        }
    }

    private static int parseFaces(final String str) {
        if (str == null) {
            return 63;
        } else {
            final String[] astring = Config.tokenize(str, " ,");
            int i = 0;

            for (final String s : astring) {
                final int k = parseFace(s);
                i |= k;
            }

            return i;
        }
    }

    private static int parseFace(String str) {
        str = str.toLowerCase();

        if (!str.equals("bottom") && !str.equals("down")) {
            if (!str.equals("top") && !str.equals("up")) {
                switch (str) {
                    case "north":
                        return 4;
                    case "south":
                        return 8;
                    case "east":
                        return 32;
                    case "west":
                        return 16;
                    case "sides":
                        return 60;
                    case "all":
                        return 63;
                    default:
                        Config.warn("Unknown face: " + str);
                        return 128;
                }
            } else {
                return 2;
            }
        } else {
            return 1;
        }
    }

    private static int parseConnect(String str) {
        if (str == null) {
            return 0;
        } else {
            str = str.trim();

            switch (str) {
                case "block":
                    return 1;
                case "tile":
                    return 2;
                case "material":
                    return 3;
                default:
                    Config.warn("Unknown connect: " + str);
                    return 128;
            }
        }
    }

    public static IProperty getProperty(final String key, final Collection properties) {
        for (final Object e : properties) {
            final IProperty iproperty = (IProperty) e;
            if (key.equals(iproperty.getName())) {
                return iproperty;
            }
        }

        return null;
    }

    private static int parseMethod(String str) {
        if (str == null) {
            return 1;
        } else {
            str = str.trim();

            if (!str.equals("ctm") && !str.equals("glass")) {
                if (str.equals("ctm_compact")) {
                    return 10;
                } else if (!str.equals("horizontal") && !str.equals("bookshelf")) {
                    switch (str) {
                        case "vertical":
                            return 6;
                        case "top":
                            return 3;
                        case "random":
                            return 4;
                        case "repeat":
                            return 5;
                        case "fixed":
                            return 7;
                        default:
                            if (!str.equals("horizontal+vertical") && !str.equals("h+v")) {
                                if (!str.equals("vertical+horizontal") && !str.equals("v+h")) {
                                    switch (str) {
                                        case "overlay":
                                            return 11;
                                        case "overlay_fixed":
                                            return 12;
                                        case "overlay_random":
                                            return 13;
                                        case "overlay_repeat":
                                            return 14;
                                        case "overlay_ctm":
                                            return 15;
                                        default:
                                            Config.warn("Unknown method: " + str);
                                            return 0;
                                    }
                                } else {
                                    return 9;
                                }
                            }
                            return 8;
                    }
                } else {
                    return 2;
                }
            } else {
                return 1;
            }
        }
    }

    public boolean isValid(final String path) {
        if (this.name != null && this.name.length() > 0) {
            if (this.basePath == null) {
                Config.warn("No base path found: " + path);
                return false;
            } else {
                if (this.matchBlocks == null) {
                    this.matchBlocks = this.detectMatchBlocks();
                }

                if (this.matchTiles == null && this.matchBlocks == null) {
                    this.matchTiles = this.detectMatchTiles();
                }

                if (this.matchBlocks == null && this.matchTiles == null) {
                    Config.warn("No matchBlocks or matchTiles specified: " + path);
                    return false;
                } else if (this.method == 0) {
                    Config.warn("No method: " + path);
                    return false;
                } else if (this.tiles != null && this.tiles.length > 0) {
                    if (this.connect == 0) {
                        this.connect = this.detectConnect();
                    }

                    if (this.connect == 128) {
                        Config.warn("Invalid connect in: " + path);
                        return false;
                    } else if (this.renderPass > 0) {
                        Config.warn("Render pass not supported: " + this.renderPass);
                        return false;
                    } else if ((this.faces & 128) != 0) {
                        Config.warn("Invalid faces in: " + path);
                        return false;
                    } else if ((this.symmetry & 128) != 0) {
                        Config.warn("Invalid symmetry in: " + path);
                        return false;
                    } else {
                        switch (this.method) {
                            case 1:
                                return this.isValidCtm(path);

                            case 2:
                                return this.isValidHorizontal(path);

                            case 3:
                                return this.isValidTop(path);

                            case 4:
                                return this.isValidRandom(path);

                            case 5:
                                return this.isValidRepeat(path);

                            case 6:
                                return this.isValidVertical(path);

                            case 7:
                                return this.isValidFixed(path);

                            case 8:
                                return this.isValidHorizontalVertical(path);

                            case 9:
                                return this.isValidVerticalHorizontal(path);

                            case 10:
                                return this.isValidCtmCompact(path);

                            case 11:
                                return this.isValidOverlay(path);

                            case 12:
                                return this.isValidOverlayFixed(path);

                            case 13:
                                return this.isValidOverlayRandom(path);

                            case 14:
                                return this.isValidOverlayRepeat(path);

                            case 15:
                                return this.isValidOverlayCtm(path);

                            default:
                                Config.warn("Unknown method: " + path);
                                return false;
                        }
                    }
                } else {
                    Config.warn("No tiles specified: " + path);
                    return false;
                }
            }
        } else {
            Config.warn("No name found: " + path);
            return false;
        }
    }

    private int detectConnect() {
        return this.matchBlocks != null ? 1 : (this.matchTiles != null ? 2 : 128);
    }

    private MatchBlock[] detectMatchBlocks() {
        final int[] aint = this.detectMatchBlockIds();

        if (aint == null) {
            return null;
        } else {
            final MatchBlock[] amatchblock = new MatchBlock[aint.length];

            for (int i = 0; i < amatchblock.length; ++i) {
                amatchblock[i] = new MatchBlock(aint[i]);
            }

            return amatchblock;
        }
    }

    private int[] detectMatchBlockIds() {
        if (!this.name.startsWith("block")) {
            return null;
        } else {
            final int i = "block".length();
            int j;

            for (j = i; j < this.name.length(); ++j) {
                final char c0 = this.name.charAt(j);

                if (c0 < 48 || c0 > 57) {
                    break;
                }
            }

            if (j == i) {
                return null;
            } else {
                final String s = this.name.substring(i, j);
                final int k = Config.parseInt(s, -1);
                return k < 0 ? null : new int[]{k};
            }
        }
    }

    private String[] detectMatchTiles() {
        final TextureAtlasSprite textureatlassprite = getIcon(this.name);
        return textureatlassprite == null ? null : new String[]{this.name};
    }

    private static TextureAtlasSprite getIcon(final String iconName) {
        final TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
        TextureAtlasSprite textureatlassprite = texturemap.getSpriteSafe(iconName);

        if (textureatlassprite != null) {
            return textureatlassprite;
        } else {
            textureatlassprite = texturemap.getSpriteSafe("blocks/" + iconName);
            return textureatlassprite;
        }
    }

    private boolean isValidCtm(final String path) {
        if (this.tiles == null) {
            this.tiles = this.parseTileNames("0-11 16-27 32-43 48-58");
        }

        if (this.tiles.length < 47) {
            Config.warn("Invalid tiles, must be at least 47: " + path);
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidCtmCompact(final String path) {
        if (this.tiles == null) {
            this.tiles = this.parseTileNames("0-4");
        }

        if (this.tiles.length < 5) {
            Config.warn("Invalid tiles, must be at least 5: " + path);
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidOverlay(final String path) {
        if (this.tiles == null) {
            this.tiles = this.parseTileNames("0-16");
        }

        if (this.tiles.length < 17) {
            Config.warn("Invalid tiles, must be at least 17: " + path);
            return false;
        } else if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
            return true;
        } else {
            Config.warn("Invalid overlay layer: " + this.layer);
            return false;
        }
    }

    private boolean isValidOverlayFixed(final String path) {
        if (!this.isValidFixed(path)) {
            return false;
        } else if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
            return true;
        } else {
            Config.warn("Invalid overlay layer: " + this.layer);
            return false;
        }
    }

    private boolean isValidOverlayRandom(final String path) {
        if (!this.isValidRandom(path)) {
            return false;
        } else if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
            return true;
        } else {
            Config.warn("Invalid overlay layer: " + this.layer);
            return false;
        }
    }

    private boolean isValidOverlayRepeat(final String path) {
        if (!this.isValidRepeat(path)) {
            return false;
        } else if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
            return true;
        } else {
            Config.warn("Invalid overlay layer: " + this.layer);
            return false;
        }
    }

    private boolean isValidOverlayCtm(final String path) {
        if (!this.isValidCtm(path)) {
            return false;
        } else if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
            return true;
        } else {
            Config.warn("Invalid overlay layer: " + this.layer);
            return false;
        }
    }

    private boolean isValidHorizontal(final String path) {
        if (this.tiles == null) {
            this.tiles = this.parseTileNames("12-15");
        }

        if (this.tiles.length != 4) {
            Config.warn("Invalid tiles, must be exactly 4: " + path);
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidVertical(final String path) {
        if (this.tiles == null) {
            Config.warn("No tiles defined for vertical: " + path);
            return false;
        } else if (this.tiles.length != 4) {
            Config.warn("Invalid tiles, must be exactly 4: " + path);
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidHorizontalVertical(final String path) {
        if (this.tiles == null) {
            Config.warn("No tiles defined for horizontal+vertical: " + path);
            return false;
        } else if (this.tiles.length != 7) {
            Config.warn("Invalid tiles, must be exactly 7: " + path);
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidVerticalHorizontal(final String path) {
        if (this.tiles == null) {
            Config.warn("No tiles defined for vertical+horizontal: " + path);
            return false;
        } else if (this.tiles.length != 7) {
            Config.warn("Invalid tiles, must be exactly 7: " + path);
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidRandom(final String path) {
        if (this.tiles != null && this.tiles.length > 0) {
            if (this.weights != null) {
                if (this.weights.length > this.tiles.length) {
                    Config.warn("More weights defined than tiles, trimming weights: " + path);
                    final int[] aint = new int[this.tiles.length];
                    System.arraycopy(this.weights, 0, aint, 0, aint.length);
                    this.weights = aint;
                }

                if (this.weights.length < this.tiles.length) {
                    Config.warn("Less weights defined than tiles, expanding weights: " + path);
                    final int[] aint1 = new int[this.tiles.length];
                    System.arraycopy(this.weights, 0, aint1, 0, this.weights.length);
                    final int i = MathUtils.getAverage(this.weights);

                    for (int j = this.weights.length; j < aint1.length; ++j) {
                        aint1[j] = i;
                    }

                    this.weights = aint1;
                }

                this.sumWeights = new int[this.weights.length];
                int k = 0;

                for (int l = 0; l < this.weights.length; ++l) {
                    k += this.weights[l];
                    this.sumWeights[l] = k;
                }

                this.sumAllWeights = k;

                if (this.sumAllWeights <= 0) {
                    Config.warn("Invalid sum of all weights: " + k);
                    this.sumAllWeights = 1;
                }
            }

            if (this.randomLoops >= 0 && this.randomLoops <= 9) {
                return true;
            } else {
                Config.warn("Invalid randomLoops: " + this.randomLoops);
                return false;
            }
        } else {
            Config.warn("Tiles not defined: " + path);
            return false;
        }
    }

    private boolean isValidRepeat(final String path) {
        if (this.tiles == null) {
            Config.warn("Tiles not defined: " + path);
            return false;
        } else if (this.width <= 0) {
            Config.warn("Invalid width: " + path);
            return false;
        } else if (this.height <= 0) {
            Config.warn("Invalid height: " + path);
            return false;
        } else if (this.tiles.length != this.width * this.height) {
            Config.warn("Number of tiles does not equal width x height: " + path);
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidFixed(final String path) {
        if (this.tiles == null) {
            Config.warn("Tiles not defined: " + path);
            return false;
        } else if (this.tiles.length != 1) {
            Config.warn("Number of tiles should be 1 for method: fixed.");
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidTop(final String path) {
        if (this.tiles == null) {
            this.tiles = this.parseTileNames("66");
        }

        if (this.tiles.length != 1) {
            Config.warn("Invalid tiles, must be exactly 1: " + path);
            return false;
        } else {
            return true;
        }
    }

    public void updateIcons(final TextureMap textureMap) {
        if (this.matchTiles != null) {
            this.matchTileIcons = registerIcons(this.matchTiles, textureMap, false, false);
        }

        if (this.connectTiles != null) {
            this.connectTileIcons = registerIcons(this.connectTiles, textureMap, false, false);
        }

        if (this.tiles != null) {
            this.tileIcons = registerIcons(this.tiles, textureMap, true, !isMethodOverlay(this.method));
        }
    }

    private static boolean isMethodOverlay(final int method) {
        switch (method) {
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                return true;

            default:
                return false;
        }
    }

    private static TextureAtlasSprite[] registerIcons(final String[] tileNames, final TextureMap textureMap, final boolean skipTiles, final boolean defaultTiles) {
        if (tileNames == null) {
            return null;
        } else {
            final List list = new ArrayList();

            for (final String s : tileNames) {
                final ResourceLocation resourcelocation = new ResourceLocation(s);
                final String s1 = resourcelocation.getResourceDomain();
                String s2 = resourcelocation.getResourcePath();

                if (!s2.contains("/")) {
                    s2 = "textures/blocks/" + s2;
                }

                final String s3 = s2 + ".png";

                if (skipTiles && s3.endsWith("<skip>.png")) {
                    list.add(null);
                } else if (defaultTiles && s3.endsWith("<default>.png")) {
                    list.add(ConnectedTextures.SPRITE_DEFAULT);
                } else {
                    final ResourceLocation resourcelocation1 = new ResourceLocation(s1, s3);
                    final boolean flag = Config.hasResource(resourcelocation1);

                    if (!flag) {
                        Config.warn("File not found: " + s3);
                    }

                    final String s4 = "textures/";
                    String s5 = s2;

                    if (s2.startsWith(s4)) {
                        s5 = s2.substring(s4.length());
                    }

                    final ResourceLocation resourcelocation2 = new ResourceLocation(s1, s5);
                    final TextureAtlasSprite textureatlassprite = textureMap.registerSprite(resourcelocation2);
                    list.add(textureatlassprite);
                }
            }

            return (TextureAtlasSprite[]) list.toArray(new TextureAtlasSprite[list.size()]);
        }
    }

    public boolean matchesBlockId(final int blockId) {
        return Matches.blockId(blockId, this.matchBlocks);
    }

    public boolean matchesBlock(final int blockId, final int metadata) {
        return Matches.block(blockId, metadata, this.matchBlocks) && Matches.metadata(metadata, this.metadatas);
    }

    public boolean matchesIcon(final TextureAtlasSprite icon) {
        return Matches.sprite(icon, this.matchTileIcons);
    }

    public String toString() {
        return "CTM name: " + this.name + ", basePath: " + this.basePath + ", matchBlocks: " + Config.arrayToString(this.matchBlocks) + ", matchTiles: " + Config.arrayToString(this.matchTiles);
    }

    public boolean matchesBiome(final BiomeGenBase biome) {
        return Matches.biome(biome, this.biomes);
    }

    public int getMetadataMax() {
        int i = -1;
        i = this.getMax(this.metadatas, i);

        if (this.matchBlocks != null) {
            for (final MatchBlock matchblock : this.matchBlocks) {
                i = this.getMax(matchblock.getMetadatas(), i);
            }
        }

        return i;
    }

    private int getMax(final int[] mds, int max) {
        if (mds != null) {
            for (final int j : mds) {
                if (j > max) {
                    max = j;
                }
            }
        }
        return max;
    }
}
