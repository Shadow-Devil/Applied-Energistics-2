/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AlgorithmX2
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package appeng.api.util;

import java.util.Objects;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

/**
 * Represents a location in the Minecraft Universe
 */
public final class DimensionalBlockPos {

    @Nonnull
    private final Level world;

    @Nonnull
    private final net.minecraft.core.BlockPos pos;

    public DimensionalBlockPos(DimensionalBlockPos coordinate) {
        this(coordinate.getWorld(), coordinate.pos);
    }

    public DimensionalBlockPos(BlockEntity tileEntity) {
        this(tileEntity.getLevel(), tileEntity.getBlockPos());
    }

    public DimensionalBlockPos(Level world, net.minecraft.core.BlockPos pos) {
        this(world, pos.getX(), pos.getY(), pos.getZ());
    }

    public DimensionalBlockPos(Level world, int x, int y, int z) {
        this.world = Objects.requireNonNull(world, "world");
        this.pos = new net.minecraft.core.BlockPos(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DimensionalBlockPos that = (DimensionalBlockPos) o;
        return world.equals(that.world) && pos.equals(that.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, pos);
    }

    @Override
    public String toString() {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ() + " in " + getWorld().dimension().location();
    }

    public boolean isInWorld(final LevelAccessor world) {
        return this.world == world;
    }

    @Nonnull
    public Level getWorld() {
        return this.world;
    }

    @Nonnull
    public BlockPos getPos() {
        return pos;
    }
}
