package appeng.parts.p2p;

import appeng.core.AppEng;
import net.minecraft.util.ResourceLocation;

public class BuiltInP2PTypes {

    public static final ResourceLocation ME = AppEng.makeId("me_p2p_tunnel");

    public static final ResourceLocation FORGE_ENERGY = AppEng.makeId("fe_p2p_tunnel");

    public static void register() {
        P2PTypeRegistry.register(FORGE_ENERGY, new P2PTunnelType(ForgeEnergyConnectionBehavior::new));
    }

}
