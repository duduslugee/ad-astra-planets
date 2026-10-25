# Ad Astra: Planets

*Português: [README.pt-BR.md](README.pt-BR.md)*

An addon for [Ad Astra](https://modrinth.com/mod/ad-astra) (NeoForge, Minecraft 1.21.1) that takes the space race
past Venus: **rockets of tiers 5 to 10**, the **whole outer Solar System** with its moons, and a trail of ruins left
by a vanished civilization that ends at a door on the edge of the system.

## The journey

Each tier reaches a planet and its moons. Every new rocket is built from materials mined in the system before it, so
you have to walk on the planet *and* its moons to climb.

| Tier | Destinations |
|---|---|
| 1–4 | Ad Astra's Moon, Mars, Venus, Glacio, plus **Phobos** and **Deimos** (tier 2) and **Mercury** (tier 4) |
| 5 | **Jupiter**, Io, Europa, Ganymede, Callisto |
| 6 | **Saturn**, Titan, Enceladus, Rhea, Iapetus |
| 7 | **Uranus**, Miranda, Titania, Oberon |
| 8 | **Neptune**, Triton |
| 9 | **Pluto**, Charon |
| 10 | **Eris**, Dysnomia |

- **The Solar System on one map**: the planet screen draws the Sun with every planet on its orbit and its moons
  around it; click a planet for a close-up and its moons.
- **Every body is its own place**: its own stone and terrain, its metal, and one or two features: Europa's ice shell
  over an ocean with hydrothermal chimneys and geysers, Io's sulfur, Titan's methane lakes, Triton's nitrogen
  geysers, the gas giants' cloud layers you can land on, Eris's mirror plains.
- **23 new metals**, each with ore, raw, ingot, nugget, plate and blocks, and **rocket parts** made from them.
- **Bigger launch pads**: 5x5 for tiers 6–7 and 7x7 for tiers 8–10.
- **Environmental Liner**: past Earth, each world demands thermal and pressure protection, a four-piece liner in five
  levels worn under the space suit (needs Curios).
- **Advanced and Stellar NASA Workbenches** build the new rockets.

## Exploration and story

- **Points of interest** on every rocky world: crashed probes, abandoned camps, meteorite craters, crystal geodes,
  ice caves and alien ruins with crystal sockets.
- **Seven great landmarks**, one per system, each with a puzzle, traps and a **Light Core** at the end:
  the Expedition Outpost (Phobos), the Drowned Sanctuary (Europa), the Library of the Rings (Titan), the Tilted
  Observatory (Miranda), the Abyssal Forge (Triton), the Sealed Heart (Pluto) and the Frost Trial below
  **the Last Door** (Eris). Find them with `/locate structure adastraplanets:<world>/<name>`.
- **The lost expedition**: logs and inscriptions tell what happened to the first crew that followed the ancients.
- **The Last Door** opens with all seven Light Cores and holds the ending, a treasure, the **Ancients' Suit**
  (no oxygen, temperature or liner needed; it can be worn in the liner's Curios slots, so the armor slots stay free)
  and the **Ancients' Ship** (reaches any world, needs no fuel).

## Requirements

- Minecraft 1.21.1, NeoForge 21.1
- Ad Astra 1.16.26 or newer (with its libraries: Resourceful Lib, Resourceful Config, Common Storage Lib)
- GeckoLib 4.9.3 or newer
- Optional: Curios (the Environmental Liner; without it the liner system is off), JEI/EMI, KubeJS

## Works with

All optional; each recipe loads only when its mod is installed.

- **Create**: pressing and crushing. **Mekanism**: enriching. **Immersive Engineering**: metal press and crusher.
- **Modern Industrialization**: macerator and compressor. **Ender IO**: SAG mill. **PneumaticCraft**: pressure chamber.
- **Industrial Foregoing**: laser drill, on the world each ore comes from.
- **Mystical Agriculture**: a crop for every metal. **Productive Bees**: a bee for every metal, bred along the
  progression.
- **Hephaestus Forge** and **Productive Metalworks**: the metals melt and cast (ingots, nuggets, plates, blocks).
- **Replication**: matter values. **Applied Energistics 2**: meteorites on the rocky worlds.
- **Apotheosis**: its gems and affix gear show up in the landmarks' chests.

Not compatible with *Ad Astra: Rocketed*, which only exists for 1.20 and also adds rocket tiers.

## For modpacks

Almost everything is data: rocket stats, planets and tiers, the solar system map, recipes, worldgen, liner levels,
landmark loot and armor stats are JSON or tags, and KubeJS can cancel launches, boarding and arrivals. See
[docs/MODPACK.md](docs/MODPACK.md).

## License

**Free to use in any modpack**, on any platform, without asking. All other rights reserved: please don't re-upload
the jar on its own. Full terms in [LICENSE.md](LICENSE.md). The art is the addon's own; no Ad Astra textures are
copied or recoloured.
