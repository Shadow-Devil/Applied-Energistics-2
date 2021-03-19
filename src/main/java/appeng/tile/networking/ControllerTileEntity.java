/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2015, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.tile.networking;

import java.util.EnumSet;

import appeng.api.networking.IGrid;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkControllerChange;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.events.MENetworkPowerStorage;
import appeng.api.networking.events.MENetworkPowerStorage.PowerEventType;
import appeng.api.networking.pathing.ControllerState;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.block.networking.ControllerBlock;
import appeng.block.networking.ControllerBlock.ControllerBlockState;
import appeng.me.GridAccessException;
import appeng.tile.grid.AENetworkPowerTileEntity;
import appeng.util.inv.InvOperation;

public class ControllerTileEntity extends AENetworkPowerTileEntity {
    private boolean isValid = false;

    public ControllerTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        this.setInternalMaxPower(8000);
        this.setInternalPublicPowerStorage(true);
        this.getProxy().setIdlePowerUsage(3);
        this.getProxy().setFlags(GridFlags.CANNOT_CARRY, GridFlags.DENSE_CAPACITY);
    }

    @Override
    public AECableType getCableConnectionType(final AEPartLocation dir) {
        return AECableType.DENSE_SMART;
    }

    @Override
    public void onReady() {
        this.onNeighborChange(true);
        super.onReady();
    }

    public void onNeighborChange(final boolean force) {
        final boolean xx = this.checkController(this.pos.offset(Direction.EAST))
                && this.checkController(this.pos.offset(Direction.WEST));
        final boolean yy = this.checkController(this.pos.offset(Direction.UP))
                && this.checkController(this.pos.offset(Direction.DOWN));
        final boolean zz = this.checkController(this.pos.offset(Direction.NORTH))
                && this.checkController(this.pos.offset(Direction.SOUTH));

        // int meta = world.getBlockMetadata( xCoord, yCoord, zCoord );
        // boolean hasPower = meta > 0;
        // boolean isConflict = meta == 2;

        final boolean oldValid = this.isValid;

        this.isValid = (xx && !yy && !zz) || (!xx && yy && !zz) || (!xx && !yy && zz)
                || ((xx ? 1 : 0) + (yy ? 1 : 0) + (zz ? 1 : 0) <= 1);

        if (oldValid != this.isValid || force) {
            if (this.isValid) {
                this.getProxy().setValidSides(EnumSet.allOf(Direction.class));
            } else {
                this.getProxy().setValidSides(EnumSet.noneOf(Direction.class));
            }

            this.updateMeta();
        }

    }

    private void updateMeta() {
        ControllerBlockState metaState = ControllerBlockState.offline;

        IGrid grid = this.getProxy().tryGetGrid();
        if (grid != null) {
            if (grid.getEnergyGrid().isNetworkPowered()) {
                metaState = ControllerBlockState.online;

                if (grid.getPathingGrid().getControllerState() == ControllerState.CONTROLLER_CONFLICT) {
                    metaState = ControllerBlockState.conflicted;
                }
            }
        }

        if (this.checkController(this.pos)
                && this.world.getBlockState(this.pos).get(ControllerBlock.CONTROLLER_STATE) != metaState) {
            this.world.setBlockState(this.pos,
                    this.world.getBlockState(this.pos).with(ControllerBlock.CONTROLLER_STATE, metaState));
        }

    }

    @Override
    protected double getFunnelPowerDemand(final double maxReceived) {
        if (getProxy().isGridConnected()) {
            return getProxy().getEnergy().getEnergyDemand(maxReceived);
        } else {
            // no grid? use local...
            return super.getFunnelPowerDemand(maxReceived);
        }
    }

    @Override
    protected double funnelPowerIntoStorage(final double power, final Actionable mode) {
        if (this.getProxy().isGridConnected()) {
            final IEnergyGrid grid = this.getProxy().getEnergy();
            final double leftOver = grid.injectPower(power, mode);

            return leftOver;
        } else {
            // no grid? use local...
            return super.funnelPowerIntoStorage(power, mode);
        }
    }

    @Override
    protected void PowerEvent(final PowerEventType x) {
        this.getProxy().tryPostEvent(new MENetworkPowerStorage(this, x));
    }

    @MENetworkEventSubscribe
    public void onControllerChange(final MENetworkControllerChange status) {
        this.updateMeta();
    }

    @MENetworkEventSubscribe
    public void onPowerChange(final MENetworkPowerStatusChange status) {
        this.updateMeta();
    }

    @Override
    public IItemHandler getInternalInventory() {
        return EmptyHandler.INSTANCE;
    }

    @Override
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc,
            final ItemStack removed, final ItemStack added) {
    }

    /**
     * Check for a controller at this coordinates as well as is it loaded.
     *
     * @return true if there is a loaded controller
     */
    private boolean checkController(final BlockPos pos) {
        if (this.world.getChunkProvider().canTick(pos)) {
            return this.world.getTileEntity(pos) instanceof ControllerTileEntity;
        }

        return false;
    }
}
