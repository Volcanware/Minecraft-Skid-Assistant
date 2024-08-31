package net.minecraft.client.resources.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.util.*;

public class IMetadataSerializer {
    private final IRegistry<String, IMetadataSerializer.Registration<? extends IMetadataSection>> metadataSectionSerializerRegistry = new RegistrySimple();
    private final GsonBuilder gsonBuilder = new GsonBuilder();

    /**
     * Cached Gson instance. Set to null when more sections are registered, and then re-created from the builder.
     */
    private Gson gson;

    public IMetadataSerializer() {
        this.gsonBuilder.registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent.Serializer());
        this.gsonBuilder.registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle.Serializer());
        this.gsonBuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
    }

    public <T extends IMetadataSection> void registerMetadataSectionType(final IMetadataSectionSerializer<T> p_110504_1_, final Class<T> p_110504_2_) {
        this.metadataSectionSerializerRegistry.putObject(p_110504_1_.getSectionName(), new IMetadataSerializer.Registration(p_110504_1_, p_110504_2_));
        this.gsonBuilder.registerTypeAdapter(p_110504_2_, p_110504_1_);
        this.gson = null;
    }

    public <T extends IMetadataSection> T parseMetadataSection(final String p_110503_1_, final JsonObject p_110503_2_) {
        if (p_110503_1_ == null) {
            throw new IllegalArgumentException("Metadata section name cannot be null");
        } else if (!p_110503_2_.has(p_110503_1_)) {
            return null;
        } else if (!p_110503_2_.get(p_110503_1_).isJsonObject()) {
            throw new IllegalArgumentException("Invalid metadata for '" + p_110503_1_ + "' - expected object, found " + p_110503_2_.get(p_110503_1_));
        } else {
            final IMetadataSerializer.Registration<?> registration = this.metadataSectionSerializerRegistry.getObject(p_110503_1_);

            if (registration == null) {
                throw new IllegalArgumentException("Don't know how to handle metadata section '" + p_110503_1_ + "'");
            } else {
                return (T) this.getGson().fromJson(p_110503_2_.getAsJsonObject(p_110503_1_), registration.field_110500_b);
            }
        }
    }

    /**
     * Returns a Gson instance with type adapters registered for metadata sections.
     */
    private Gson getGson() {
        if (this.gson == null) {
            this.gson = this.gsonBuilder.create();
        }

        return this.gson;
    }

    class Registration<T extends IMetadataSection> {
        final IMetadataSectionSerializer<T> field_110502_a;
        final Class<T> field_110500_b;

        private Registration(final IMetadataSectionSerializer<T> p_i1305_2_, final Class<T> p_i1305_3_) {
            this.field_110502_a = p_i1305_2_;
            this.field_110500_b = p_i1305_3_;
        }
    }
}
