package net.minecraft.util;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class RegistrySimple<K, V> implements IRegistry<K, V> {
    private static final Logger logger = LogManager.getLogger();
    protected final Map<K, V> registryObjects = this.createUnderlyingMap();

    protected Map<K, V> createUnderlyingMap() {
        return Maps.newHashMap();
    }

    public V getObject(final K name) {
        return this.registryObjects.get(name);
    }

    /**
     * Register an object on this registry.
     */
    public void putObject(final K p_82595_1_, final V p_82595_2_) {
        Validate.notNull(p_82595_1_);
        Validate.notNull(p_82595_2_);

        if (this.registryObjects.containsKey(p_82595_1_)) {
            logger.debug("Adding duplicate key '" + p_82595_1_ + "' to registry");
        }

        this.registryObjects.put(p_82595_1_, p_82595_2_);
    }

    public Set<K> getKeys() {
        return Collections.unmodifiableSet(this.registryObjects.keySet());
    }

    /**
     * Does this registry contain an entry for the given key?
     */
    public boolean containsKey(final K p_148741_1_) {
        return this.registryObjects.containsKey(p_148741_1_);
    }

    public Iterator<V> iterator() {
        return this.registryObjects.values().iterator();
    }
}
