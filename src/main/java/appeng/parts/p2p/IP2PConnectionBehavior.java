package appeng.parts.p2p;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public interface IP2PConnectionBehavior {

    /**
     * Gets a capability exposed by the P2P tunnel.
     */
    <T> LazyOptional<T> getTunnelCapability(Capability<T> cap);

    /**
     * Called when the participants in the connection have changed (i.e. new output or input joined or left).
     */
    default void onTunnelNetworkChange() {
    }
}

@FunctionalInterface
interface IP2PConnectionBehaviorFactory {

    IP2PConnectionBehavior create(IP2PConnection connection);

}
