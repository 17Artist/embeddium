# Gene Fork changes

## 0.3.31-gene.2 — 2026-10-21

- Use stable radix ordering for meshes with at least 4,096 translucent quads.
  Smaller meshes retain merge sorting. Equal-distance faces retain their original
  order; NaN distances use the original comparison path.
- Preserve distance calculations, movement thresholds, sort scheduling, visible
  geometry and OpenGL requirements. No additional dependencies are introduced.
- Add exact-order regression checks covering the threshold, repeated distances,
  signed zero, infinities and NaNs. See [performance notes](docs/performance.md)
  for the measured scope and limitations.

## 0.3.31-gene.1 — 2026-10-21

Based on Embeddium 0.3.31 (`87d1a75aecc230a25a1c152df8fbd7d2ca8bd2fd`).
Maintained by 17Artist for Minecraft 1.20.1 / Forge. The later unreleased upstream
`20.1/forge` commits are not implicitly included.

- Index visible translucent sections separately for sort scheduling. Keep the same
  visibility order, dynamic-sort conditions, movement thresholds and rebuild priorities.
- Discard obsolete sort state after transparent geometry disappears or a section unloads.
- Release shader/program handles when compilation, linking or interface construction fails,
  including a fragment failure after a successful vertex compile. Program builders are
  now closeable; successful links transfer ownership to the resulting program.
- Add [terrain access API v1](docs/terrain-access.md) for borrowed section lookup,
  device state and synchronous terrain lifetime callbacks.
- Identify this distribution as **Embeddium · Gene Fork**. Keep the `embeddium` mod ID.

No additional OpenGL feature requirements are introduced. These changes do not imply
support for every driver or every mod combination. GPU timing and compatibility results
must name the actual device, release JARs, scenes and settings used.

## Build

Use JDK 17 and the included Gradle wrapper:

```sh
./gradlew -Pbuild.release=true build jarJar
```

The distributable JAR is `build/libs/embeddium-0.3.31-gene.2+mc1.20.1.jar`.
The `-slim` JAR is not the normal client distribution. `check` includes 1,000
randomized render-list rebuilds compared with the original geometry-filter walk
and 864 transparent-sort cases compared with the original stable merge order.

The build retains the upstream Forge 47.2.32 compile baseline; runtime acceptance
targets Forge 47.3.0 or later on Minecraft 1.20.1. This does not raise the existing
OpenGL baseline. Original copyright notices and licenses remain in their source files.
