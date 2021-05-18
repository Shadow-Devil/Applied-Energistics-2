package appeng.parts.p2p;

import appeng.api.config.PowerUnits;
import appeng.api.parts.IPartHost;
import appeng.core.AELog;
import appeng.me.GridAccessException;
import appeng.me.cache.helpers.TunnelCollection;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GenericP2PTunnelPart extends P2PTunnelPart<GenericP2PTunnelPart> {

    private static final String TAG_TUNNEL_TYPE = "tunnelType";

    private final IP2PConnection connection;

    private final IP2PConnectionBehavior connectionBehavior;

    private final P2PTunnelType tunnelType;

    public GenericP2PTunnelPart(ItemStack is) {
        // We use a secondary constructor here so that the itemstack is passed to super AFTER the tunnel type
        // has been sanitized
        this(is, getTunnelType(is));
    }

    private GenericP2PTunnelPart(ItemStack is, P2PTunnelType tunnelType) {
        super(is);
        this.connection = new Connection();
        this.tunnelType = tunnelType;
        this.connectionBehavior = tunnelType.createConnectionBehavior(this.connection);
    }

    private static P2PTunnelType getTunnelType(ItemStack is) {
        ResourceLocation tunnelTypeId = getTunnelTypeId(is);
        P2PTunnelType tunnelType = null;
        if (tunnelTypeId != null) {
            tunnelType = P2PTypeRegistry.getById(tunnelTypeId);
            if (tunnelType == null) {
                AELog.warn("Unknown P2P tunnel type: '%s'", tunnelTypeId);
                removeTunnelTypeId(is);
            }
        }

        if (tunnelType == null) {
            return Objects.requireNonNull(P2PTypeRegistry.getById(BuiltInP2PTypes.ME));
        } else {
            return tunnelType;
        }
    }

    @Nullable
    public static ResourceLocation getTunnelTypeId(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null) {
            String tunnelType = tag.getString(TAG_TUNNEL_TYPE);
            try {
                return new ResourceLocation(tunnelType);
            } catch (ResourceLocationException ignored) {
                // Invalid type stored in tunnel part item
            }
        }
        return null;
    }

    public static void setTunnelTypeId(ItemStack stack, ResourceLocation typeId) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putString(TAG_TUNNEL_TYPE, typeId.toString());
    }

    public static void removeTunnelTypeId(ItemStack stack) {
        stack.removeChildTag(TAG_TUNNEL_TYPE);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capabilityClass) {
        LazyOptional<T> result = connectionBehavior.getTunnelCapability(capabilityClass);
        if (result.isPresent()) {
            return result;
        }

        return super.getCapability(capabilityClass);
    }

    @Override
    public void onTunnelNetworkChange() {
        connectionBehavior.onTunnelNetworkChange();
    }

    private class Connection implements IP2PConnection {
        @Override
        public boolean isActive() {
            return GenericP2PTunnelPart.this.isActive();
        }

        @Override
        public boolean isOutput() {
            return GenericP2PTunnelPart.this.isOutput();
        }

        @Override
        public IP2PConnection getInput() {
            return GenericP2PTunnelPart.this.getInput().connection;
        }

        @Override
        public List<IP2PConnection> getActiveOutputs() {
            try {
                TunnelCollection<GenericP2PTunnelPart> outputs = GenericP2PTunnelPart.this.getOutputs();
                List<IP2PConnection> connections = new ArrayList<>(outputs.size());
                for (GenericP2PTunnelPart output : outputs) {
                    if (output.isActive()) {
                        connections.add(output.connection);
                    }
                }
                return connections;
            } catch (GridAccessException e) {
                return Collections.emptyList();
            }
        }

        @Override
        public <T> LazyOptional<T> getAttachedCapability(Capability<T> cap) {
            final TileEntity self = getTile();
            final TileEntity te = self.getWorld().getTileEntity(self.getPos().offset(getSide().getFacing()));

            if (te != null) {
                return te.getCapability(cap, getSide().getOpposite().getFacing());
            } else {
                return LazyOptional.empty();
            }
        }

        @Override
        public void queueTunnelDrain(PowerUnits unit, int amount) {
            GenericP2PTunnelPart.this.queueTunnelDrain(unit, amount);
        }

        @Override
        public IPartHost getHost() {
            return GenericP2PTunnelPart.this.getHost();
        }
    }

}
