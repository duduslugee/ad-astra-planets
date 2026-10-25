# Ad Astra: Planets — config folder

The switches live here too, one file per topic (created on first launch, each commented):

| File | What it switches |
|------|------------------|
| `adastraplanets-rockets.toml` | rockets to disable |
| `adastraplanets-worldgen.toml` | ore generation |
| `adastraplanets-launch_pads.toml` | the 5x5 Large Launch Pad |
| `adastraplanets-solar_systems.toml` | solar systems hidden from the planet screen |
| `adastraplanets-environment.toml` | the environmental liner (temperature and pressure) |
| `adastraplanets-debug.toml` | the Ore Scanner debug item: scan radius, block tag, glowing highlights |
| `adastraplanets-ore_tooltips.toml` | the "Found on: ..." line on ores (client side, any item can get one) |

## content.json

Minecraft only lets blocks, items and entities be registered while the game starts, so the few things a modpack
needs to *create* (rather than tweak) go in `config/adastraplanets/content.json`. Everything else stays in
datapacks, resource packs or KubeJS. Restart the game after editing this file.

```json
{
  "rockets": [
    { "id": "heavy_rocket", "width": 1.6, "height": 10.0, "name": "Heavy Rocket" }
  ],
  "ores": [
    {
      "id": "helionite_ore_mars",
      "base": "ad_astra:block/mars_stone",
      "overlay": "adastraplanets:block/helionite_ore_overlay",
      "drop": "adastraplanets:raw_helionite",
      "properties_from": "minecraft:iron_ore",
      "tool_tier": "diamond",
      "tags": ["c:ores/helionite"],
      "name": "Mars Helionite Ore"
    }
  ],
  "globes": [
    { "id": "frost_nova_globe", "name": "Frost Nova Globe" }
  ]
}
```

Ids are registered under `adastraplanets:`. Keep them neutral (no planet names) if the pack might be reshaped later:
the id is stored in saves, the `name` is only what players see and can be changed in a resource pack's lang files.

## Rockets

| field | meaning |
|---|---|
| `id` | entity and item id |
| `width`, `height` | hitbox, in blocks |
| `name` | optional English name (`item.` and `entity.adastraplanets.<id>` lang keys) |

Also provide:

- **Datapack** `data/<any>/adastraplanets/rocket/<id>.json` — tier, fuel, tank, launch cost, speed, seat height and
  allowed launch pads (same format as `adastraplanets:tier_5_rocket`). Without it the rocket cannot launch.
- **Resource pack** `assets/adastraplanets/geo/entity/<id>.geo.json`, `textures/entity/<id>.png` and
  `animations/entity/<id>.animation.json` (GeckoLib / Blockbench export).
- A recipe (e.g. `ad_astra:nasa_workbench`) from a datapack or KubeJS.

The rocket item is carried overhead like Ad Astra's and reuses the Tier 5 rocket's item display; a resource pack can
replace `models/item/<id>.json`.

## Ores

| field | meaning |
|---|---|
| `id` | block and item id |
| `base` | texture of the stone the ore sits in |
| `overlay` | texture drawn on top (transparent around the ore) |
| `drop` | item dropped without silk touch (fortune applies) |
| `properties_from` | vanilla block to copy hardness and sound from (default `minecraft:iron_ore`) |
| `tool_tier` | `wood`, `stone`, `iron` or `diamond` (default `stone`) |
| `tags` | extra block and item tags, e.g. `c:ores/helionite` so existing smelting recipes accept it |
| `name` | optional English name |

The ore is generated wherever a datapack places it: add a configured feature, a placed feature and a
`neoforge:add_features` biome modifier (see `adastraplanets:helionite_ore` for an example).

## Globes

| field | meaning |
|---|---|
| `id` | block and item id, e.g. `frost_nova_globe` (globes, like Ad Astra's, are named after their world) |
| `name` | optional English name |

Also provide the **resource pack** texture `assets/adastraplanets/textures/block/globe/<id>.png` (64x64, the same
layout as the built-in globes: the six faces of the globe at the top left, the stand below), and a recipe from a datapack or KubeJS. The globe spins when clicked or powered.
