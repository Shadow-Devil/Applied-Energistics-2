package appeng.parts.p2p;

import appeng.api.config.PowerUnits;
import appeng.capabilities.Capabilities;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

public class ForgeEnergyConnectionBehavior implements IP2PConnectionBehavior {

    private static final IEnergyStorage NULL_ENERGY_STORAGE = new NullEnergyStorage();
    private final IP2PConnection connection;
    private final IEnergyStorage proxyStorage;

    public ForgeEnergyConnectionBehavior(IP2PConnection connection) {
        this.connection = connection;
        if (this.connection.isOutput()) {
            proxyStorage = new OutputEnergyStorage();
        } else {
            proxyStorage = new InputEnergyStorage();
        }
    }

    private static IEnergyStorage getAttachedEnergyStorage(IP2PConnection connection) {
        if (connection.isActive()) {
            return connection.getAttachedCapability(Capabilities.FORGE_ENERGY)
                    .orElse(NULL_ENERGY_STORAGE);
        } else {
            return NULL_ENERGY_STORAGE;
        }
    }

    @Override
    public <T> LazyOptional<T> getTunnelCapability(Capability<T> cap) {
        return Capabilities.FORGE_ENERGY.orEmpty(cap, LazyOptional.of(() -> proxyStorage));
    }

    private class InputEnergyStorage implements IEnergyStorage {
        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int total = 0;

            List<IP2PConnection> outputs = connection.getActiveOutputs();
            final int outputTunnels = outputs.size();

            if (outputTunnels == 0 || maxReceive == 0) {
                return 0;
            }

            final int amountPerOutput = maxReceive / outputTunnels;
            int overflow = amountPerOutput == 0 ? maxReceive : maxReceive % amountPerOutput;

            for (IP2PConnection outputConnection : outputs) {
                final IEnergyStorage output = getAttachedEnergyStorage(outputConnection);
                final int toSend = amountPerOutput + overflow;
                final int received = output.receiveEnergy(toSend, simulate);

                overflow = toSend - received;
                total += received;
            }

            if (!simulate) {
                connection.queueTunnelDrain(PowerUnits.RF, total);
            }

            return total;
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return true;
        }

        @Override
        public int getMaxEnergyStored() {
            int total = 0;

            for (IP2PConnection outputConnection : connection.getActiveOutputs()) {
                total += getAttachedEnergyStorage(outputConnection).getMaxEnergyStored();
            }

            return total;
        }

        @Override
        public int getEnergyStored() {
            int total = 0;

            for (IP2PConnection outputConnection : connection.getActiveOutputs()) {
                total += getAttachedEnergyStorage(outputConnection).getEnergyStored();
            }

            return total;
        }
    }

    private class OutputEnergyStorage implements IEnergyStorage {
        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            final int total = getAttachedEnergyStorage(connection).extractEnergy(maxExtract, simulate);

            if (!simulate) {
                connection.queueTunnelDrain(PowerUnits.RF, total);
            }

            return total;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public boolean canExtract() {
            return getAttachedEnergyStorage(connection).canExtract();
        }

        @Override
        public boolean canReceive() {
            return false;
        }

        @Override
        public int getMaxEnergyStored() {
            return getAttachedEnergyStorage(connection).getMaxEnergyStored();
        }

        @Override
        public int getEnergyStored() {
            return getAttachedEnergyStorage(connection).getEnergyStored();
        }
    }

    private static class NullEnergyStorage implements IEnergyStorage {

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return 0;
        }

        @Override
        public int getMaxEnergyStored() {
            return 0;
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    }

}
