package appeng.me.helpers;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkEvent;
import appeng.api.networking.pathing.IPathingGrid;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.ITickManager;
import appeng.api.util.AEColor;
import appeng.me.cache.P2PCache;
import appeng.me.cache.StatisticsCache;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public interface NetworkProxy extends IGridBlock {

    static NetworkProxy create(final IGridProxyable te, final String nbtName, final ItemStack visual,
                               final boolean inWorld) {
        if (te.isRemote()) {
            return new NoOpNetworkProxy();
        } else {
            return new ServerNetworkProxy(te, nbtName, visual, inWorld);
        }
    }

    void setVisualRepresentation(ItemStack is);
    AEColor getColor();
    void setColor(final AEColor color);
    void setValidSides(EnumSet<Direction> validSides);

    void readFromNBT(CompoundNBT tag);

    void writeToNBT(CompoundNBT data);

    void remove();

    void onChunkUnloaded();

    void setOwner(PlayerEntity player);

    void setFlags(GridFlags... requireChannel);

    void setIdlePowerUsage(double idle);

    void onReady();

    boolean isReady();

    boolean isActive();

    boolean isPowered();

    boolean isGridConnected();

    @Nonnull
    IGrid getGrid();

    @Nullable
    IGrid tryGetGrid();

    @Nonnull
    IGridNode getNode();

    @Nonnull
    default IPathingGrid getPath() {
        return getGrid().getPathingGrid();
    }

    @Nonnull
    default ITickManager getTick() {
        return getGrid().getTickManager();
    }

    @Nonnull
    default IStorageGrid getStorage() {
        return getGrid().getStorageGrid();
    }

    @Nonnull
    default P2PCache getP2P() {
        return getGrid().getCache(P2PCache.class);
    }

    @Nonnull
    default ISecurityGrid getSecurity() {
        return getGrid().getSecurityGrid();
    }

    @Nonnull
    default ICraftingGrid getCrafting() {
        return this.getGrid().getCraftingGrid();
    }

    @Nonnull
    default StatisticsCache getStatistics() {
        return this.getGrid().getCache(StatisticsCache.class);
    }

    @Nonnull
    default IEnergyGrid getEnergy() {
        return this.getGrid().getEnergyGrid();
    }

    /**
     * Sends a network event in case the grid is available, otherwise this is a noop.
     */
    default void tryPostEvent(MENetworkEvent event) {
        if (isGridConnected()) {
            getGrid().postEvent(event);
        }
    }

    /**
     * Alerts the device in case it's already connected to a grid, otherwise this is a noop.
     * @see appeng.me.cache.TickManagerCache#alertDevice(IGridNode)
     */
    default void alertDevice() {
        if (isGridConnected()) {
            getTick().alertDevice(getNode());
        }
    }

    /**
     * Puts the device to sleep in case it's already connected to a grid, otherwise this is a noop.
     * @see appeng.me.cache.TickManagerCache#sleepDevice(IGridNode)
     */
    default void sleepDevice() {
        if (isGridConnected()) {
            getTick().sleepDevice(getNode());
        }
    }

    /**
     * Puts the device to sleep in case it's already connected to a grid, otherwise this is a noop.
     * @see appeng.me.cache.TickManagerCache#wakeDevice(IGridNode)
     */
    default void wakeDevice() {
        if (isGridConnected()) {
            getTick().wakeDevice(getNode());
        }
    }

}
