package appeng.parts.p2p;

import com.google.common.base.Preconditions;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class P2PTypeRegistry {

    private P2PTypeRegistry() {
    }

    private static final Map<ResourceLocation, P2PTunnelType> registrations = new HashMap<>();

    public static synchronized void register(ResourceLocation id, P2PTunnelType registration) {
        Preconditions.checkState(!registrations.containsKey(id), "P2P type with id %s already registered", id);
        registrations.put(id, registration);
    }

    @Nullable
    public static synchronized P2PTunnelType getById(ResourceLocation id) {
        return registrations.get(id);
    }

}
