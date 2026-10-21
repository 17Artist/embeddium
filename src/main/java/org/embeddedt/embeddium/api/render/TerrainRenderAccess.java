/* SPDX-License-Identifier: LGPL-3.0-only
 * Copyright (c) 2026 17Artist
 */
package org.embeddedt.embeddium.api.render;

import java.util.Arrays;
import java.util.Objects;
import me.jellysquid.mods.sodium.client.gl.device.GLRenderDevice;
import me.jellysquid.mods.sodium.client.gl.device.RenderDevice;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Version 1 read-only terrain integration for the 1.20.1 maintenance branch.
 * Queries and callbacks run on the render thread. Returned objects are borrowed:
 * never mutate them, enqueue builds, or retain GPU storage across a mutation callback.
 * A prepared callback marks the end of terrain setup, not complete chunk coverage.
 */
public final class TerrainRenderAccess {
    private static volatile Listener[] listeners = new Listener[0];
    private record Listener(Runnable beforeMutation, Runnable afterPrepare) {}

    private TerrainRenderAccess() {}

    public static int apiVersion() { return 1; }

    public static @Nullable RenderSectionManager manager() {
        var world = SodiumWorldRenderer.instanceNullable();
        return world == null ? null : world.getRenderSectionManager();
    }

    public static @Nullable RenderSection section(RenderSectionManager manager, int x, int y, int z) {
        return manager.getRenderSection(x, y, z);
    }

    public static boolean deviceActive() {
        return ((GLRenderDevice) RenderDevice.INSTANCE).isActive();
    }

    /** Register once; close to unsubscribe. Handlers must not mutate terrain or throw. */
    public static synchronized AutoCloseable listen(Runnable beforeMutation, Runnable afterPrepare) {
        var listener = new Listener(Objects.requireNonNull(beforeMutation), Objects.requireNonNull(afterPrepare));
        var next = Arrays.copyOf(listeners, listeners.length + 1);
        next[next.length - 1] = listener;
        listeners = next;
        return () -> remove(listener);
    }

    private static synchronized void remove(Listener listener) {
        // Identity matters: registering the same callbacks twice creates separate subscriptions.
        listeners = Arrays.stream(listeners).filter(value -> value != listener).toArray(Listener[]::new);
    }

    @ApiStatus.Internal
    public static void beforeMeshMutation() {
        for (var listener : listeners) listener.beforeMutation.run();
    }

    @ApiStatus.Internal
    public static void terrainPrepared() {
        for (var listener : listeners) listener.afterPrepare.run();
    }
}
