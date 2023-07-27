package dev.utils.thealtening.service;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class FieldAdapter {
    private static final Logger LOGGER;
    private final HashMap<String, MethodHandle> fields = new HashMap();
    private static final MethodHandles.Lookup LOOKUP;
    private static Field MODIFIERS;

    public FieldAdapter(String parent) {
        try {
            Class<?> clazz = Class.forName(parent);
            Field modifiers = MODIFIERS;
            Field[] fieldArray = clazz.getDeclaredFields();
            int n = fieldArray.length;
            int n2 = 0;
            while (n2 < n) {
                Field field = fieldArray[n2];
                field.setAccessible(true);
                int accessFlags = field.getModifiers();
                if (Modifier.isFinal(accessFlags)) {
                    modifiers.setInt(field, accessFlags & 0xFFFFFFEF);
                }
                MethodHandle handler = LOOKUP.unreflectSetter(field);
                handler = handler.asType(handler.type().generic().changeReturnType(Void.TYPE));
                this.fields.put(field.getName(), handler);
                ++n2;
            }
            return;
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't load/find the specified class");
        }
        catch (Exception e) {
            throw new RuntimeException("Couldn't create a method handler for the field");
        }
    }

    public void updateFieldIfPresent(String name, Object newValue) {
        Optional.ofNullable(this.fields.get(name)).ifPresent(setter -> {
            try {
                setter.invokeExact(newValue);
            }
            catch (Throwable e) {
                LOGGER.warn((Object)e);
            }
        });
    }

    static {
        MethodHandles.Lookup lookupObject;
        LOGGER = LogManager.getLogger();
        try {
            MODIFIERS = Field.class.getDeclaredField("modifiers");
            MODIFIERS.setAccessible(true);
        }
        catch (NoSuchFieldException e) {
            LOGGER.warn((Object)e);
        }
        try {
            Field lookupImplField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            lookupImplField.setAccessible(true);
            lookupObject = (MethodHandles.Lookup)lookupImplField.get(null);
        }
        catch (ReflectiveOperationException e) {
            lookupObject = MethodHandles.lookup();
        }
        LOOKUP = lookupObject;
    }
}