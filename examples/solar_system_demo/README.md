# Solar system map demo

A datapack planet in a new solar system plus its map for Ad Astra's planet screen, as a KubeJS folder
(`kubejs/data` and `kubejs/assets` apply to every world). Copy `kubejs/` into the game folder, start a world and run
`/adastra planets`: the solar system list gets an "Ember System" entry drawn from
`assets/demo/adastraplanets/solar_systems/ember_system.json`.

Ad Astra requires every planet's dimension to exist (otherwise the planet screen fails), so the demo also adds a
small `demo:ember` dimension reusing Mars terrain, plus `demo:ember_orbit` (Ad Astra only lists planets that have
an orbit). Datapack dimensions are read when a world loads: restart the
world (not just `/reload`) after adding it. Any datapack/resource pack can do the same without KubeJS.
