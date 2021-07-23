/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2021, TeamAppliedEnergistics, All rights reserved.
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

package appeng.client.gui.me.common;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A proxy for a slot that will always return an itemstack with size 1, if there is an item in the slot. Used to prevent
 * the default item count from rendering.
 */
class Size1Slot extends net.minecraft.world.inventory.Slot {

    private final Slot delegate;

    public Size1Slot(Slot delegate) {
        super(delegate.container, delegate.getSlotIndex(), delegate.x, delegate.y);
        this.delegate = delegate;
    }

    @Override
    @Nonnull
    public net.minecraft.world.item.ItemStack getItem() {
        net.minecraft.world.item.ItemStack orgStack = this.delegate.getItem();
        if (!orgStack.isEmpty()) {
            net.minecraft.world.item.ItemStack modifiedStack = orgStack.copy();
            modifiedStack.setCount(1);
            return modifiedStack;
        }

        return net.minecraft.world.item.ItemStack.EMPTY;
    }

    @Override
    public boolean hasItem() {
        return this.delegate.hasItem();
    }

    @Override
    public int getMaxStackSize() {
        return this.delegate.getMaxStackSize();
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return this.delegate.getMaxStackSize(stack);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return this.delegate.mayPickup(playerIn);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isActive() {
        return this.delegate.isActive();
    }

    @Override
    public int getSlotIndex() {
        return this.delegate.getSlotIndex();
    }

    @Override
    public boolean isSameInventory(net.minecraft.world.inventory.Slot other) {
        return this.delegate.isSameInventory(other);
    }
}
