# Ad Astra: Planets

*English: [README.md](README.md)*

Um addon para o [Ad Astra](https://modrinth.com/mod/ad-astra) (NeoForge, Minecraft 1.21.1) que leva a corrida
espacial além de Vênus: **foguetes do tier 5 ao 10**, **todo o Sistema Solar exterior** com suas luas, e uma trilha de
ruínas deixada por uma civilização desaparecida, que termina numa porta na borda do sistema.

## A jornada

Cada tier alcança um planeta e suas luas. Cada foguete novo é feito com materiais minerados no sistema anterior, então
é preciso pisar no planeta *e* nas luas para subir.

| Tier | Destinos |
|---|---|
| 1–4 | Lua, Marte, Vênus e Glacio do Ad Astra, mais **Fobos** e **Deimos** (tier 2) e **Mercúrio** (tier 4) |
| 5 | **Júpiter**, Io, Europa, Ganimedes, Calisto |
| 6 | **Saturno**, Titã, Encélado, Reia, Jápeto |
| 7 | **Urano**, Miranda, Titânia, Oberon |
| 8 | **Netuno**, Tritão |
| 9 | **Plutão**, Caronte |
| 10 | **Éris**, Disnomia |

- **O Sistema Solar num mapa só**: a tela de planetas mostra o Sol com cada planeta na sua órbita e as luas em volta;
  clique num planeta para ver ele de perto com as luas.
- **Cada corpo é um lugar próprio**: pedra e terreno próprios, seu metal e uma ou duas marcas: a crosta de gelo de
  Europa sobre um oceano com chaminés hidrotermais e gêiseres, o enxofre de Io, os lagos de metano de Titã, os gêiseres
  de nitrogênio de Tritão, as camadas de nuvens dos gigantes gasosos onde dá para pousar, as planícies espelhadas de Éris.
- **23 metais novos**, cada um com minério, bruto, lingote, pepita, placa e blocos, e **peças de foguete** feitas deles.
- **Plataformas de lançamento maiores**: 5x5 para os tiers 6–7 e 7x7 para os tiers 8–10.
- **Forro Ambiental**: depois da Terra, cada mundo exige proteção térmica e de pressão, um forro de quatro peças em
  cinco níveis usado por baixo do traje espacial (precisa do Curios).
- **Advanced e Stellar NASA Workbench** constroem os foguetes novos.

## Exploração e história

- **Pontos de interesse** em todo mundo rochoso: sondas caídas, acampamentos abandonados, crateras de meteorito,
  geodos de cristal, cavernas de gelo e ruínas alienígenas com soquetes de cristal.
- **Sete grandes marcos**, um por sistema, cada um com um enigma, armadilhas e um **Núcleo de Luz** no fim:
  o Posto Avançado da Expedição (Fobos), o Santuário Submerso (Europa), a Biblioteca dos Anéis (Titã), o Observatório
  Inclinado (Miranda), a Forja Abissal (Tritão), o Coração Selado (Plutão) e a Prova Gélida sob **a Última Porta**
  (Éris). Encontre com `/locate structure adastraplanets:<mundo>/<nome>`.
- **A expedição perdida**: diários e inscrições contam o que aconteceu com a primeira tripulação que seguiu os antigos.
- **A Última Porta** abre com os sete Núcleos de Luz e guarda o final, um tesouro, o **Traje dos Antigos** (dispensa
  oxigênio, temperatura e forro; pode ir nos slots de Forro do Curios, deixando os de armadura livres) e a **Nave dos
  Antigos** (alcança qualquer mundo, sem combustível).

## Requisitos

- Minecraft 1.21.1, NeoForge 21.1
- Ad Astra 1.16.26 ou mais novo (com as bibliotecas dele: Resourceful Lib, Resourceful Config, Common Storage Lib)
- GeckoLib 4.9.3 ou mais novo
- Opcionais: Curios (o Forro Ambiental; sem ele o sistema de forro fica desligado), JEI/EMI, KubeJS

## Funciona com

Tudo opcional; cada receita só carrega com o mod dela instalado.

- **Create**: prensa e moagem. **Mekanism**: enriquecimento. **Immersive Engineering**: prensa de metal e triturador.
- **Modern Industrialization**: macerador e compressor. **Ender IO**: moinho SAG. **PneumaticCraft**: câmara de pressão.
- **Industrial Foregoing**: laser drill, no mundo de onde cada minério vem.
- **Mystical Agriculture**: uma semente para cada metal. **Productive Bees**: uma abelha para cada metal, criada ao
  longo da progressão.
- **Hephaestus Forge** e **Productive Metalworks**: os metais derretem e são moldados (lingotes, pepitas, placas,
  blocos).
- **Replication**: valores de matéria. **Applied Energistics 2**: meteoritos nos mundos rochosos.
- **Apotheosis**: gemas e equipamentos com afixos aparecem nos baús dos marcos.

Não é compatível com o *Ad Astra: Rocketed*, que só existe para 1.20 e também adiciona tiers de foguete.

## Para modpacks

Quase tudo é dado: atributos dos foguetes, planetas e tiers, o mapa do sistema solar, receitas, geração de mundo,
níveis do forro, loot dos marcos e atributos de armadura são JSON ou tags, e o KubeJS pode cancelar lançamentos,
embarques e chegadas. Veja [docs/MODPACK.md](docs/MODPACK.md) (em inglês).

## Licença

**Livre para usar em qualquer modpack**, em qualquer plataforma, sem pedir. Os demais direitos são reservados: não
reenvie o jar sozinho. Termos completos em [LICENSE.md](LICENSE.md) (em inglês). A arte é do próprio addon; nenhuma
textura do Ad Astra é copiada ou recolorida.
