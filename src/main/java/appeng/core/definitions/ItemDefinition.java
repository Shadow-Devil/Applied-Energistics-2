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

package appeng.core.definitions;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.resources.ResourceLocation;

import appeng.util.Platform;
import net.minecraft.world.item.ItemStack;

public class ItemDefinition<T extends Item> implements ItemLike {
    private final ResourceLocation id;
    private final T item;

    public ItemDefinition(ResourceLocation id, T item) {
        Preconditions.checkNotNull(id, "id");
        this.id = id;
        this.item = item;
    }

    @Nonnull
    public ResourceLocation id() {
        return this.id;
    }

    public net.minecraft.world.item.ItemStack stack() {
        return stack(1);
    }

    public net.minecraft.world.item.ItemStack stack(final int stackSize) {
        return new net.minecraft.world.item.ItemStack(item, stackSize);
    }

    /**
     * Compare {@link net.minecraft.world.item.ItemStack} with this
     *
     * @param comparableStack compared item
     *
     * @return true if the item stack is a matching item.
     */
    public final boolean isSameAs(final ItemStack comparableStack) {
        return Platform.itemComparisons().isEqualItemType(comparableStack, this.stack());
    }

    @Override
    public T asItem() {
        return item;
    }
}
