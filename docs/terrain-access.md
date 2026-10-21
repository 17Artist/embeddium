# Terrain access API v1

`org.embeddedt.embeddium.api.render.TerrainRenderAccess` is a read-only integration
surface for this Minecraft 1.20.1 branch. `apiVersion()` returns `1`.

- `manager()` returns the current section manager, or `null` without a world.
- `section(manager, x, y, z)` performs an indexed lookup in section coordinates.
- `deviceActive()` exposes the current managed device state without changing it.
- `listen(beforeMutation, afterPrepare)` returns an `AutoCloseable` subscription.

Queries and callbacks belong on the render thread. Registration/unsubscription may
occur during client initialization. Listeners run synchronously in registration
order. They must not throw, change the world, enqueue builds, or mutate the renderer.
Closing a subscription is idempotent. Callback iteration allocates no iterator or
snapshot each frame; the immutable listener array changes only on registration.

`beforeMutation` fires before section addition/removal, nonempty mesh uploads and
manager destruction. Empty upload checks do not invalidate borrowed meshes.
Discard references to GPU storage on this callback. `afterPrepare` fires after
terrain setup, including immediate uploads; it does **not** mean that all requested
chunks are loaded, visible, or fully built. Check each section's lifetime/build state.

Returned managers/sections use the 0.3.31 internal representation and are borrowed,
not owned. The API intentionally does not promise cross-Minecraft binary compatibility.
Integrations must not mutate main-view visibility lists to render secondary views.
They may issue their own depth draws from existing uploaded geometry while restoring
all GL state and honoring uncertain/unbuilt occluders.

Optional integrations may resolve these public methods once through method handles,
so they can retain a stock-Embeddium fallback without bundling fork implementation
classes. Do not inspect or modify private fields when API v1 is available.
