# Overworld in a custom solar system

Ad Astra decides which planet is the Overworld by the planet JSON whose `dimension` is `minecraft:overworld`
(`data/ad_astra/planets/earth.json`). A datapack that overrides that file (same path) can move it to any solar
system and change its gravity, temperature, tier, etc.; override `earth_orbit.json` too so its orbit follows.

- `datapack/` — moves the Overworld and its orbit into `frost:frost_nova`.
- `resourcepack/` — draws Frost Nova on the planet screen and renames the Overworld to "Frost Nova Prime" (the
  planet name is the lang key `planet.<dimension namespace>.<dimension path>`).

With Ad Astra's own system now holding only the other planets, it can be hidden in
`config/adastraplanets-common.toml`:

```toml
[solar_systems]
    hidden = ["ad_astra:solar_system", "ad_astra:proxima_centauri"]
```

Hidden systems keep working (gravity, oxygen, launching from them); they just are not destinations any more.
