<img src="src/main/resources/icon.png" width="128">

# Embeddium · Gene Fork

A maintained Embeddium branch for **Minecraft 1.20.1 / Forge**, focused on terrain
rendering performance and integration with additional visual effects.

This branch starts from Embeddium 0.3.31. The `-gene.N` version suffix identifies
Gene Fork builds; the mod ID remains `embeddium`. Install one Embeddium JAR at a time.

## Changes

- Fewer section visits when scheduling translucent mesh sorting.
- Shader and program cleanup on compilation, linking and initialization failures.
- A versioned, read-only terrain interface with mesh lifetime callbacks.
- Existing OpenGL feature requirements and upstream compatibility paths retained.

See [changes and build instructions](CHANGES-GENE.md) and the
[terrain integration contract](docs/terrain-access.md).
Report Gene Fork issues in [this repository](https://github.com/17Artist/embeddium/issues).
Upstream Embeddium is available at [FiniteReality/embeddium](https://github.com/FiniteReality/embeddium).

## Credits

* JellySquid & the CaffeineMC team, for making Sodium in the first place, without which this project would not be possible
* Asek3, for the initial port to Forge
* XFactHD, for providing a list of gamebreaking Rubidium issues to start this work off, and for testing early builds
* Pepper, for their invaluable assistance with getting Forge lighting to work on Sodium 0.5
* @CelestialAbyss and @input-Here for making the new logo design

## License

Embeddium is licensed under the Lesser GNU General Public License version 3.

Portions of the option screen code are based on Reese's Sodium Options by FlashyReese, and are used under the terms of
the [MIT license](https://opensource.org/license/mit), located in `src/main/resources/licenses/rso.txt`. 
