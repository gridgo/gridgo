package io.gridgo.bean.serialization;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.reflections.Reflections;

import io.gridgo.bean.exceptions.SerializationPluginException;
import io.gridgo.bean.factory.BFactory;
import io.gridgo.bean.factory.BFactoryAware;
import io.gridgo.bean.serialization.msgpack.MsgpackSerializer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("unchecked")
public final class BSerializerRegistry {

    private final BFactory factory;

    /**
     * take value from system property
     * <b>'gridgo.bean.serializer.binary.default'</b>, in case it's not defined, use
     * default msgpack
     */
    public static final String SYSTEM_DEFAULT_BINARY_SERIALIZER = System.getProperty("gridgo.bean.serializer.binary.default", MsgpackSerializer.NAME);

    private final AtomicReference<String> defaultSerializerName = new AtomicReference<>(null);
    private BSerializer cachedDefaultSerializer = null;

    private final Map<String, BSerializer> registry = new NonBlockingHashMap<String, BSerializer>();

    public BSerializerRegistry(@NonNull BFactory factory) {
        this(factory, SYSTEM_DEFAULT_BINARY_SERIALIZER);
    }

    public BSerializerRegistry(@NonNull BFactory factory, @NonNull String defaultSerializerName) {
        this.factory = factory;
        this.setDefaultSerializerName(defaultSerializerName);
        this.scan(this.getClass().getPackageName());
    }

    public Set<String> availableSerializers() {
        return this.registry.keySet();
    }

    public String getDefaultSerializerName() {
        return this.defaultSerializerName.get();
    }

    public void setDefaultSerializerName(@NonNull String name) {
        String currValue = this.getDefaultSerializerName();
        if (!name.equals(currValue) && this.defaultSerializerName.compareAndSet(currValue, name)) {
            var currCachedDefaultSerializer = this.cachedDefaultSerializer;
            if (currCachedDefaultSerializer != null) {
                synchronized (defaultSerializerName) {
                    if (this.cachedDefaultSerializer == currCachedDefaultSerializer) {
                        this.cachedDefaultSerializer = null;
                    }
                }
            }
        }
    }

    public <T extends BSerializer> T getDefault() {
        if (this.cachedDefaultSerializer == null) {
            synchronized (defaultSerializerName) {
                if (this.cachedDefaultSerializer == null) {
                    final String currDefaultSerializerName = getDefaultSerializerName();
                    this.cachedDefaultSerializer = this.lookup(currDefaultSerializerName);
                    if (this.cachedDefaultSerializer == null) {
                        if (log.isWarnEnabled()) {
                            log.warn("Serializer for default name " + currDefaultSerializerName + " doesn't exist");
                        }
                    }
                }
            }
        }
        return (T) this.cachedDefaultSerializer;
    }

    public <T extends BSerializer> T lookupOrDefault(String name) {
        if (name == null) {
            return this.getDefault();
        }
        return lookup(name);
    }

    public <T extends BSerializer> T lookup(@NonNull String name) {
        return (T) this.registry.get(name);
    }

    public BSerializer deregister(@NonNull String name) {
        if (defaultSerializerName.get().equals(name) && cachedDefaultSerializer != null) {
            synchronized (this.defaultSerializerName) {
                if (cachedDefaultSerializer != null) {
                    cachedDefaultSerializer = null;
                }
            }
        }
        return this.registry.remove(name);
    }

    public void register(@NonNull String name, BSerializer serializer) {
        BSerializer old = this.registry.putIfAbsent(name, serializer);
        if (old != null) {
            throw new SerializationPluginException("serialization plugin with name " + name + " is already registered");
        }
        if (serializer instanceof BFactoryAware) {
            ((BFactoryAware) serializer).setFactory(factory);
        }
    }

    public void scan(@NonNull String packageName, ClassLoader... classLoaders) {
        Reflections reflections = new Reflections(packageName, classLoaders);
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(BSerializationPlugin.class);
        for (Class<?> clazz : types) {
            if (!BSerializer.class.isAssignableFrom(clazz)) {
                throw new SerializationPluginException("Invalid serialization plugin, class must implement BSerializer");
            }
            BSerializationPlugin annotation = clazz.getAnnotation(BSerializationPlugin.class);
            String[] names = annotation.value();
            if (names == null || names.length == 0) {
                throw new SerializationPluginException("serialization plugin's name(s) must not be empty");
            }
            try {
                BSerializer serializer = (BSerializer) clazz.getConstructor().newInstance();
                for (String name : names) {
                    this.register(name, serializer);
                }
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                    | SecurityException e) {
                throw new SerializationPluginException("Cannot create instance for class " + clazz + ", require non-args constructor");
            }
        }
    }
}
