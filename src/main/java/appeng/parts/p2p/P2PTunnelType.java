package appeng.parts.p2p;

public final class P2PTunnelType {

    private final IP2PConnectionBehaviorFactory behaviorFactory;

    public P2PTunnelType(IP2PConnectionBehaviorFactory behaviorFactory) {
        this.behaviorFactory = behaviorFactory;
    }

    public IP2PConnectionBehavior createConnectionBehavior(IP2PConnection connection) {
        return this.behaviorFactory.create(connection);
    }

}
