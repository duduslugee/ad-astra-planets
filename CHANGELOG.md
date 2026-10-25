# Changelog

## 1.0.0 — first release (Minecraft 1.21.1, NeoForge)

### Rockets and launch pads
- Rockets of **tiers 5 to 10**, each with its own model, built from five parts (nose cone, hull, fins, tank, engine)
  made of the metals of the system before it.
- **Advanced** and **Stellar NASA Workbenches** build them (each also builds the tiers below).
- **Large (5x5)** and **Huge (7x7)** launch pads: tiers 6–7 need the large one, tiers 8–10 the huge one.
- Tank, launch cost and speed rise with the tier; every number lives in each rocket's JSON definition.

### The Solar System
- **Mercury** becomes tier 4; **Phobos** and **Deimos** (tier 2) join Mars.
- New worlds with their own terrain, stone, metal and sky: **Jupiter** and Io, Europa, Ganymede, Callisto;
  **Saturn** and Titan, Enceladus, Rhea, Iapetus; **Uranus** and Miranda, Titania, Oberon; **Neptune** and Triton;
  **Pluto** and Charon; **Eris** and Dysnomia. The gas giants have cloud layers to land on.
- Features: Europa's ice shell over an ocean with geysers and hydrothermal chimneys, Titan's methane lakes, Triton's
  nitrogen geysers, Uranus's pressure crystals, Pluto's heart of ice, Eris's mirror plains, and more.
- **The planet screen draws the whole Solar System**: every planet on its orbit with its moons, a close-up with an
  information panel (tier, gravity, temperature, oxygen, resources), a list where the moons unfold under their planet.
- **23 metals**, each with ore, raw metal, ingot, nugget, plate, storage and raw blocks; seven crystals grown in geodes.
- **Desk globes** of all 22 worlds.

### Survival
- **Environmental Liner** (Curios): five levels of thermal and pressure protection; each world demands a level.
- Shift tooltips tell how to get every item.

### Exploration and story
- Points of interest on every rocky world: crashed probes, abandoned camps, meteorite craters, crystal geodes, ice
  caves, alien ruins with crystal sockets and gates.
- Seven landmarks, each with a puzzle, traps and a **Light Core**: the Expedition Outpost (Phobos), the Drowned
  Sanctuary (Europa), the Library of the Rings (Titan), the Tilted Observatory (Miranda), the Abyssal Forge (Triton),
  the Sealed Heart (Pluto) and the Last Door with its Frost Trial (Eris).
- The lost expedition's logs and the ancients' inscriptions tell the story to its end.
- Light Cores are catalysts in the workbench recipes (they are not used up).
- **The Last Door** opens with the seven cores: the **Ancients' Suit** (no oxygen, temperature or liner needed; wear
  it in the armor slots or the liner's Curios slots), the **Ancients' Ship** (any world, no fuel) and the ending.

### For modpacks
- Rocket definitions, planets and tiers, solar system maps, worldgen, environments, liner and armor stats, geysers,
  socket keys and loot are all data; config files switch rockets, launch pads, ore generation, solar systems and the
  liner off; `config/adastraplanets/content.json` adds rockets, ores and globes.
- KubeJS events: rocket launch, boarding and arrival on a planet (cancellable where it makes sense).
- JEI categories for the workbenches and information pages for what is only found by exploring.

### Other mods (all optional)
- Create, Mekanism, Immersive Engineering, Modern Industrialization, Ender IO, PneumaticCraft, Industrial Foregoing,
  Mystical Agriculture, Productive Bees, Hephaestus Forge, Productive Metalworks, Replication and Applied
  Energistics 2 handle the new metals; Apotheosis loot appears in the landmarks' chests.
