package me.cortex.voxy.client.core.util;

import me.cortex.voxy.client.core.VoxyRenderSystem;
import me.cortex.voxy.client.core.rendering.Viewport;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.caffeinemc.mods.sodium.client.util.FogParameters;
import net.neoforged.fml.ModList;

public class IrisUtil {

    public record CapturedViewportParameters(ChunkRenderMatrices matrices, FogParameters parameters, int width, int height, double x, double y, double z) {
        public Viewport<?> apply(VoxyRenderSystem vrs) {
            return vrs.setupViewport(this.matrices.projection(), this.matrices.modelView(), this.parameters, this.width, this.height, this.x, this.y, this.z);
        }
    }

    public static CapturedViewportParameters CAPTURED_VIEWPORT_PARAMETERS;

    public static final boolean IRIS_INSTALLED = ModList.get().isLoaded("iris");
    public static final boolean SHADER_SUPPORT = true;

    private static boolean irisShadowActive0() {
        try {
            Class<?> shadowClass = Class.forName("net.irisshaders.iris.shadows.ShadowRenderer");
            java.lang.reflect.Field activeField = shadowClass.getField("ACTIVE");
            return (boolean) activeField.get(null);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean irisShadowActive() {
        return IRIS_INSTALLED && irisShadowActive0();
    }

    public static void clearIrisSamplers() {
        if (IRIS_INSTALLED) clearIrisSamplers0();
    }
    public static void reload() {
        if (IRIS_INSTALLED) reload0();
    }

    private static void reload0() {
        try {
            Class<?> irisApiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            Object apiInstance = irisApiClass.getMethod("getInstance").invoke(null);
            boolean hasPack = (boolean) irisApiClass.getMethod("isShaderPackInUse").invoke(apiInstance);
            boolean shadersEnabled = (boolean) irisApiClass.getMethod("getConfig").invoke(apiInstance)
                    .getClass().getMethod("areShadersEnabled").invoke(irisApiClass.getMethod("getConfig").invoke(apiInstance));
            if (hasPack || shadersEnabled) {
                Class.forName("net.irisshaders.iris.Iris").getMethod("reload").invoke(null);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void clearIrisSamplers0() {
        try {
            Class<?> irisRenderSystem = Class.forName("net.irisshaders.iris.gl.IrisRenderSystem");
            for (int i = 0; i < 16; i++) {
                irisRenderSystem.getMethod("bindSamplerToUnit", int.class, int.class).invoke(null, i, 0);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean irisShaderPackEnabled0() {
        try {
            Class<?> irisClass = Class.forName("net.irisshaders.iris.Iris");
            Object currentPack = irisClass.getMethod("getCurrentPack").invoke(null);
            return currentPack != null && !(Boolean) currentPack.getClass().getMethod("isEmpty").invoke(currentPack);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean irisShaderPackEnabled() {
        return IRIS_INSTALLED && irisShaderPackEnabled0();
    }

    private static boolean irisShadersEnabledInConfig0() {
        try {
            Class<?> irisClass = Class.forName("net.irisshaders.iris.Iris");
            Object currentPack = irisClass.getMethod("getCurrentPack").invoke(null);
            return currentPack != null && !(Boolean) currentPack.getClass().getMethod("isEmpty").invoke(currentPack);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean irisShadersEnabledInConfig() {
        return IRIS_INSTALLED && irisShadersEnabledInConfig0();
    }

    public static void disableIrisShaders() {
        if(IRIS_INSTALLED) disableIrisShaders0();
    }

    private static void disableIrisShaders0() {
        try {
            Class<?> irisApiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            Object apiInstance = irisApiClass.getMethod("getInstance").invoke(null);
            Object config = irisApiClass.getMethod("getConfig").invoke(apiInstance);
            config.getClass().getMethod("setShadersEnabledAndApply", boolean.class).invoke(config, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
