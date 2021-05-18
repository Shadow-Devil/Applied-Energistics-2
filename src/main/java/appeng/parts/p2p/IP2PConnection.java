package appeng.parts.p2p;

import appeng.api.config.PowerUnits;
import appeng.api.parts.IPartHost;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;

public interface IP2PConnection {

    boolean isActive();

    boolean isOutput();

    /**
     * The input side of this P2P connection.
     */
    IP2PConnection getInput();

    /**
     * @return All active P2P connections using the same frequency as this one.
     */
    List<IP2PConnection> getActiveOutputs();

    /**
     * Gets a capability from the block directly in front of the P2P tunnel. The side facing the P2P tunnel is
     * automatically queried.
     */
    <T> LazyOptional<T> getAttachedCapability(Capability<T> cap);

    /**
     * Queue consumption of energy for an operation performed by this connection.
     * Idle power consumption is handled separately.
     */
    void queueTunnelDrain(PowerUnits unit, int amount);

    /**
     * The part host hosting the P2P tunnel this connection represents.
     */
    IPartHost getHost();

}
