package me.cortex.voxy.common.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.cortex.voxy.common.Logger;
import me.cortex.voxy.commonImpl.VoxyCommon;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarFile;

public class Serialization {
    public static final Set<Class<?>> CONFIG_TYPES = new HashSet<>();
    public static Gson GSON;

    private static final class GsonConfigSerialization <T> implements TypeAdapterFactory {
        private final String typeField = "TYPE";
        private final Class<T> clz;

        private final Map<String, Class<? extends T>> name2type = new HashMap<>();
        private final Map<Class<? extends T>, String> type2name = new HashMap<>();

        private GsonConfigSerialization(Class<T> clz) {
            this.clz = clz;
        }

        public GsonConfigSerialization<T> register(String typeName, Class<? extends T> cls) {
            if (this.name2type.put(typeName, cls) != null) {
                throw new IllegalStateException("Type name already registered: " + typeName);
            }
            if (this.type2name.put(cls, typeName) != null) {
                throw new IllegalStateException("Class already registered with type name: " + typeName + ", " + cls);
            }
            return this;
        }

        private T deserialize(Gson gson, JsonElement json) {
            var retype = this.name2type.get(json.getAsJsonObject().remove(this.typeField).getAsString());
            return gson.getDelegateAdapter(this, TypeToken.get(retype)).fromJsonTree(json);
        }

        private JsonElement serialize(Gson gson, T value) {
            String name = this.type2name.get(value.getClass());
            if (name == null) {
                name = "UNKNOWN_TYPE_{" + value.getClass().getName() + "}";
            }
            var vjson = gson
                    .getDelegateAdapter(this, TypeToken.get((Class<T>) value.getClass()))
                    .toJsonTree(value);
            var json = new JsonObject();
            json.addProperty(this.typeField, name);
            vjson.getAsJsonObject().asMap().forEach(json::add);
            return json;
        }

        @Override
        public <X> TypeAdapter<X> create(Gson gson, TypeToken<X> type) {
            if (this.clz.isAssignableFrom(type.getRawType())) {
                var jsonObjectAdapter = gson.getAdapter(JsonElement.class);
                return (TypeAdapter<X>) new TypeAdapter<T>() {
                    @Override
                    public void write(JsonWriter out, T value) throws IOException {
                        jsonObjectAdapter.write(out, GsonConfigSerialization.this.serialize(gson, value));
                    }
                    @Override
                    public T read(JsonReader in) throws IOException {
                        var obj = jsonObjectAdapter.read(in);
                        return GsonConfigSerialization.this.deserialize(gson, obj);
                    }
                };
            }
            return null;
        }
    }

    public static void init() {
        Map<Class<?>, GsonConfigSerialization<?>> serializers = new HashMap<>();
        int count = 0;

        outer:
        for (String clzName : collectAllClasses("me.cortex.voxy")) {
            if (VoxyCommon.IS_DEDICATED_SERVER && clzName.startsWith("me.cortex.voxy.client")) {
                continue;
            }
            if (!clzName.toLowerCase(Locale.ROOT).contains("config")) {
                continue;
            }
            if (clzName.contains("mixin")) {
                continue;
            }
            if (clzName.contains("ModMenuIntegration")) {
                continue;
            }
            if (clzName.contains("VoxyConfigScreenPages")) {
                continue;
            }
            if (clzName.endsWith("VoxyConfig") && !clzName.contains("$")) {
                continue;
            }
            if (clzName.equals(Serialization.class.getName())) {
                continue;
            }

            try {
                var clz = Class.forName(clzName);
                if (Modifier.isAbstract(clz.getModifiers())) {
                    continue;
                }
                var original = clz;
                Class<?> superclass = clz.getSuperclass();
                while (superclass != null) {
                    if (CONFIG_TYPES.contains(superclass)) {
                        Method nameMethod = null;
                        try {
                            nameMethod = original.getMethod("getConfigTypeName");
                            nameMethod.setAccessible(true);
                        } catch (NoSuchMethodException e) {}
                        if (nameMethod == null) {
                            Logger.error("WARNING: Config class " + clzName + " doesnt contain a getConfigTypeName");
                            break;
                        }
                        count++;
                        String name = (String) nameMethod.invoke(null);
                        serializers.computeIfAbsent(superclass, GsonConfigSerialization::new)
                                .register(name, (Class) original);
                        Logger.info("Registered " + original.getSimpleName() + " as " + name + " for config type " + superclass.getSimpleName());
                        break;
                    }
                    superclass = superclass.getSuperclass();
                }
            } catch (Throwable e) {
                Logger.error("Error while setting up config serialization for " + clzName, e);
            }
        }

        var builder = new GsonBuilder().setPrettyPrinting();
        for (var entry : serializers.entrySet()) {
            builder.registerTypeAdapterFactory(entry.getValue());
        }

        GSON = builder.create();
        Logger.info("Registered " + count + " config types");
    }

    /**
     * Lists every class of the given package tree. getResourceAsStream() cannot enumerate package
     * contents when the mod is loaded from a jar, so the code source (mod jar, or the classes
     * directory during development) is walked directly.
     */
    private static List<String> collectAllClasses(String pack) {
        Set<String> classes = new LinkedHashSet<>();
        String resourcePath = pack.replace('.', '/');
        try {
            var codeSource = Serialization.class.getProtectionDomain().getCodeSource();
            if (codeSource == null) {
                Logger.error("Could not resolve code source, cannot scan " + pack);
                return List.of();
            }
            var location = Paths.get(codeSource.getLocation().toURI());
            if (Files.isDirectory(location)) {
                var root = location.resolve(resourcePath);
                if (Files.isDirectory(root)) {
                    try (var stream = Files.walk(root)) {
                        stream.filter(Files::isRegularFile)
                                .map(p -> location.relativize(p).toString())
                                .filter(n -> n.endsWith(".class"))
                                .forEach(n -> classes.add(n.substring(0, n.length() - ".class".length())
                                        .replace(File.separatorChar, '.').replace('/', '.')));
                    }
                }
            } else {
                try (var jar = new JarFile(location.toFile())) {
                    var entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        var name = entries.nextElement().getName();
                        if (name.startsWith(resourcePath + "/") && name.endsWith(".class")) {
                            classes.add(name.substring(0, name.length() - ".class".length()).replace('/', '.'));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to collect classes in package: " + pack, e);
        }
        Logger.info("Collected " + classes.size() + " classes from " + pack + " (" + codeSourceDescription() + ")");
        return new ArrayList<>(classes);
    }

    private static String codeSourceDescription() {
        try {
            var codeSource = Serialization.class.getProtectionDomain().getCodeSource();
            return codeSource == null ? "no code source" : codeSource.getLocation().toString();
        } catch (Throwable t) {
            return "unknown";
        }
    }
}
