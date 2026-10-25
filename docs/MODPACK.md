# Ad Astra: Planets — modpack guide

Everything this addon adds can be tuned or replaced without touching code. Where each thing lives:

| What | Where | Reload |
|---|---|---|
| Rocket balancing (tier, fuels, tank, launch cost, speed, seat, launch pads) | datapack `data/<ns>/adastraplanets/rocket/<rocket id>.json` | `/reload` |
| Recipes, loot, tags | datapack / KubeJS | `/reload` |
| Where Helionite ore generates | biome tag `adastraplanets:has_helionite_ore`, `worldgen/*/helionite_ore.json` | new chunks |
| Planets, solar systems, tiers of destinations | Ad Astra's `data/<ns>/planets/*.json` | `/reload` |
| Solar system picture on the planet screen | resource pack `assets/<ns>/adastraplanets/solar_systems/<id>.json` | F3+T |
| Names shown to players (planets, items, systems) | resource pack lang files / KubeJS `ClientEvents.lang` | F3+T |
| Switching content on/off | `config/adastraplanets-common.toml` | restart |
| Extra rockets and ore variants (new blocks/entities) | `config/adastraplanets/content.json` (README next to it) | restart |
| Gameplay rules (block a launch until a quest, etc.) | KubeJS events below, or NeoForge events for Java mods | `/reload` |

## Rocket definition

```json
{
  "neoforge:conditions": [{ "type": "adastraplanets:rocket_enabled", "rocket": "adastraplanets:tier_5_rocket" }],
  "tier": 5,
  "fuel": "adastraplanets:tier_5_rocket_fuel",
  "fuel_capacity": 3000,
  "launch_cost": 3000,
  "efficient_fuel": "ad_astra:efficient_fuel",
  "efficient_launch_cost": 1200,
  "acceleration": 0.005,
  "max_speed": 1.0,
  "riding_offset": 1.8,
  "launch_pads": "#ad_astra:launch_pads"
}
```

`fuel`, `efficient_fuel` are fluid tags; `launch_pads` is a block tag. Use `#adastraplanets:large_launch_pads` to
require the 5x5 Large Launch Pad (tiers 6-7 do) or `#adastraplanets:huge_launch_pads` for the 7x7 Huge one (tiers
8-10; it counts as large too). A rocket reaches any planet whose `tier` is at or below its own.

Defaults: tank and launch cost 3000, 4000, 5000, 6000, 7000, 8000 mB for tiers 5 to 10, efficient fuel at 40%,
acceleration and top speed rising with the tier. The Ancients' Ship (`ancient_ship.json`) is tier 100 with
`"launch_cost": 0` and an empty fuel tag: it launches with an empty tank. A rocket's animation file may hold
`animation.<rocket>.idle` and `animation.<rocket>.launch` loops, played while it stands and while it flies.

## Config and conditions

`config/adastraplanets-common.toml`:

- `rockets.disabled` — rocket ids to disable (recipe, definition and creative tab entry go away).
- `worldgen.helionite_ore` — Helionite ore generation.
- `launch_pads.large_launch_pad` — the Large Launch Pad recipe.
- `solar_systems.hidden` — solar systems to hide from the planet screen (e.g. Ad Astra's own two). Their planets keep
  working (gravity, oxygen, launching from them) but are no longer destinations, except for creative players and
  operators. The server's value applies to everyone.

Any datapack file can follow the same switches through `neoforge:conditions`:

```json
{ "type": "adastraplanets:option", "option": "worldgen.helionite_ore" }
{ "type": "adastraplanets:rocket_enabled", "rocket": "adastraplanets:tier_5_rocket" }
```

## KubeJS (server scripts)

```js
AdAstraPlanetsEvents.rocketLaunch(event => {
  // event.rocket, event.pilot (may be null for redstone launches), event.tier, event.rocketType
  if (event.tier >= 7 && !event.pilot?.stages.has('outer_planets')) {
    event.pilot?.tell('Finish the Saturn chapter first')
    event.cancel()
  }
})

AdAstraPlanetsEvents.rocketBoard(event => {
  // event.player, event.rocket, event.tier, event.rocketType; cancel() keeps the player out
})

AdAstraPlanetsEvents.planetArrival(event => {
  // event.player, event.planet (dimension id), event.from, event.tier, event.solarSystem,
  // event.orbit, event.oxygen, event.gravity
  event.player.tell(`Welcome to ${event.planet}`)
})
```

`rocketLaunch` covers every rocket, Ad Astra's included. A cancelled launch is asked again at most once a second.
Java mods get the same as `RocketLaunchEvent`, `RocketBoardEvent` and `PlanetArrivalEvent` on the NeoForge bus.

## Solar system map

Ad Astra draws only its own two systems on the planet screen. For a new system (planets whose JSON says
`"solar_system": "mypack:trappist"`), add `assets/mypack/adastraplanets/solar_systems/trappist.json`:

```json
{
  "sun": "ad_astra:textures/environment/red_sun.png",
  "sun_size": 16,
  "orbit_color": "#1F7F80",
  "orbit_gap": 30,
  "planets": [
    { "texture": "mypack:textures/gui/planets/ember.png", "orbit": 1, "size": 12, "speed": 2.0, "phase": 0 }
  ]
}
```

Each planet and moon can name the destination it stands for with `"planet": "<dimension id>"`. Linked icons are
clickable: a planet opens a close-up with its moons and an information panel (tier, gravity, temperature, oxygen,
solar power, whether the current rocket can reach it); reachable bodies get Ad Astra's Land button there. Moons are
picked in their planet's close-up, where the left-hand list shows that planet and its moons; elsewhere it lists the
system's planets. Bodies whose dimension does not exist yet still open their close-up, marked as not explorable. A map
file for an existing system (Ad Astra's included) replaces its built-in drawing. Planets the map does not show are added
on outer orbits (turn off with `"add_missing_planets": false`).

`"resources": ["mypack:raw_emberite"]` on a planet or moon lists, with icons, what is worth gathering there to progress;
the information panel shows it under "Resources".

The system's name comes from the lang key `solar_system.mypack.trappist`. See `examples/solar_system_demo`.

Two Ad Astra rules to keep in mind for new planets: every planet's `dimension` must exist (add a dimension JSON;
datapack dimensions load when a world loads), and a planet only shows in the planet list when it has an `orbit`
(another planet entry for its orbit dimension) — Ad Astra treats entries without one as space stations.

## Which planet is the Overworld

Ad Astra's planet whose `dimension` is `minecraft:overworld` (`data/ad_astra/planets/earth.json`) is the Overworld.
Override that file in a datapack to put it in your own solar system (and `earth_orbit.json` for its orbit); rename it
with the lang key `planet.minecraft.overworld`. See `examples/overworld_in_custom_system`. Hide Ad Astra's systems
only after moving the Overworld out of them, or players cannot pick it to fly home.

## Reshaping the universe

Registry ids never contain planet names (`helionite_ore`, not `mercury_helionite_ore`), so a pack can move Helionite to
another world, rename "Glacio" or build new solar systems without breaking saves: change the biome tag / worldgen
JSON for where things spawn, Ad Astra's planet JSONs for destinations and tiers, and lang files for names.

## Europa and its features

Europa (`adastraplanets:europa`, tier 5) is plain datapack content: an ice
shell over an ocean, with Abyssium (the tier 6 material) only around hydrothermal chimneys. Anything below can be
overridden by a datapack file at the same path.

- **Terrain**: `worldgen/noise_settings/europa.json` and the density functions in `worldgen/density_function/europa/`
  (sea floor, shell top and bottom, ice caves); biomes `europa_ice_shell` and `europa_subsurface_ocean`.
- **Hydrothermal chimneys**: `worldgen/configured_feature/hydrothermal_vent.json` (type `adastraplanets:hydrothermal_vent`:
  `chimney`, `vent`, `ore`, `height`, `mound_radius`, `ore_radius`, `ore_count`, `can_replace`) and
  `worldgen/placed_feature/hydrothermal_vent.json` for how rare they are. The feature works in any flooded world.
- **Geysers**: `worldgen/configured_feature/geyser.json` (type `adastraplanets:geyser`: `geyser`, `frost`, `frost_radius`,
  `can_replace`) places them with their ring of rime. How they erupt comes from the block data map
  `data/<namespace>/data_maps/block/geysers.json`:

  ```json
  { "values": { "adastraplanets:geyser": { "interval": 240, "duration": 60, "warning": 40, "height": 14, "strength": 0.9 } } }
  ```

  `interval`, `duration` and `warning` are in ticks, `height` in blocks, `strength` is the upward speed given to entities.
- **Liquid oceans on frozen worlds**: Ad Astra freezes water on cold planets. Water that cannot see the sky stays liquid
  in dimension types tagged `adastraplanets:insulated_oceans` (`tags/dimension_type/insulated_oceans.json`).
- **Ice looks**: the crust, rusted and rime blocks use Minecraft's calcite, packed ice and snow textures tinted in game,
  so resource packs that change those textures change Europa too.

## Environmental liner (temperature and pressure)

Every planet except Earth demands an environmental liner: four pieces (hood, vest, leggings, boots) worn in their
own Curios slots, under Ad Astra's space suit. A set protects at the level of its weakest piece; a missing piece
gives nothing. Without Curios installed the system is off. Switch it off with `environment.enabled` in the config.

- **What each place demands**: `data/<namespace>/adastraplanets/environments/*.json`:

  ```json
  { "dimensions": ["adastraplanets:europa", "adastraplanets:europa_orbit"], "thermal": 3, "pressure": 3, "pressure_kind": "low" }
  ```

  A file with `"default": true` (instead of `dimensions`) covers every Ad Astra planet no file names; non-planet
  dimensions demand nothing. `pressure_kind` is `low` (decompression: slowness, mining fatigue, weakness and damage)
  or `high` (crushing damage). Whether the thermal danger burns or freezes follows Ad Astra's temperature. Inside an
  oxygen distributor's sealed area on an airless world nothing is demanded.
- **What each piece gives**: the item data map `data/<namespace>/data_maps/item/liner_protection.json`
  (`{ "thermal": 3, "pressure": 3 }` per item). Any item can become a liner piece by adding it there and to a slot
  tag (`curios:liner_head`, `liner_chest`, `liner_legs`, `liner_feet`).
- **How hard it hits**: `data/adastraplanets/adastraplanets/environment_settings.json`
  (`check_interval` in ticks, `thermal_damage` and `pressure_damage` per missing level).
- Default levels follow the rockets, one every two tiers: I (Moon, Mars, orbits), II (Venus, Mercury, Glacio),
  III (Jupiter and Saturn systems), IV (Uranus, Neptune), V (Pluto, Eris).

## Landmarks, Light Cores and the Last Door

| Landmark | `/locate structure` id | Light Core |
|---|---|---|
| Expedition Outpost | `adastraplanets:phobos/expedition_outpost` | Ember |
| Drowned Sanctuary | `adastraplanets:europa/drowned_sanctuary` | Amber |
| Library of the Rings | `adastraplanets:titan/library_of_the_rings` | Halo |
| Tilted Observatory | `adastraplanets:miranda/tilted_observatory` | Tide |
| Abyssal Forge | `adastraplanets:triton/abyssal_forge` | Cobalt |
| Sealed Heart | `adastraplanets:pluto/sealed_heart` | Rose |
| The Last Door (Frost Trial) | `adastraplanets:eris/last_door` | Frost |

- Where and how often they generate: `worldgen/structure/<world>/<name>.json` and `worldgen/structure_set/...`
  (type `adastraplanets:levelled_jigsaw`: `footprint_radius`, `max_slope`, `anchor`). Points of interest share one
  grid per world, `structure_set/<world>/points_of_interest.json`.
- Chest loot: `loot_table/chests/<landmark>/...`.
- What opens a socket: crystal sockets take the item tag `adastraplanets:socket_keys/<glow>` (a system's crystal
  shards by default), core sockets `adastraplanets:core_keys/<glow>` (its Light Core). Swap them for quest items to
  gate the landmarks your way. Access readers take `adastraplanets:access_cards`.
- The Light Cores are catalysts in recipes: they come back out of the crafting grid. The Advanced and Stellar NASA
  Workbench recipes use the Ember and Halo cores.

## The Ancients' Suit

- Its protection: the item data map `data/<namespace>/data_maps/item/armor_stats.json`
  (`{ "armor": 9, "toughness": 4, "knockback_resistance": 0.1 }` per piece; works for any of this mod's armor).
- What it skips: items in `adastraplanets:environment_proof_armor` need no liner when worn as a full set; Ad Astra's
  `space_resistant_armor`, `oxygen_supplying_armor`, `heat_resistant_armor` and `freeze_resistant_armor` tags cover
  oxygen and temperature. Each piece also fits its liner slot of Curios (counting as a level-5 liner there), and a full
  set worn across armor and liner slots counts as worn.
- Its look: `assets/adastraplanets/models/armor/ancient_suit.json` (one mesh per slot) and
  `textures/models/armor/ancient_suit.png`.

## Other mods

Everything below loads only with its mod, from `data/adastraplanets/recipe/compat/<mod>/`; override or remove any
file with a datapack.

- Create, Mekanism, Immersive Engineering, Modern Industrialization, Ender IO, PneumaticCraft: plates from ingots
  and ore doubling, at each mod's own rate for iron.
- Industrial Foregoing: laser drill entries per metal, whitelisted to the world its ore generates on.
- Replication: matter values (metallic, plus precious rising with the world's tier).
- Applied Energistics 2: the rocky worlds' biomes are added to `ae2:has_meteorites`.
- Mystical Agriculture: a crop per metal; tiers and colours in `data/adastraplanets/compat/mysticalagriculture_crops.json`
  (read at startup); turn crops off with Mystical Agriculture's own config.
- Productive Bees: a bee per metal (`data/adastraplanets/productivebees/<metal>.json`), converted from the previous
  tier's bee by feeding it a block of the new metal (the first from Productive Bees' Ad Astra bees).
- Hephaestus Forge and Productive Metalworks: melting and casting into the molten metals `adastraplanets:molten_<metal>`
  (fluid tag `c:molten_<metal>`). The molten metals always exist but are hidden from the creative tab and JEI when
  neither smeltery is installed.
