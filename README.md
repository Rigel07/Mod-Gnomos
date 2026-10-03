# Gnomos — mod de Minecraft (Forge 1.20.1)

Pequeños gnomos con gorro puntiagudo que viven en sus propias aldeas.

## Gnomos
- 5 colores (rojo, azul, verde, amarillo, morado) y 5 profesiones: agricultor, minero, herrero, recolector y anciano.
- Miden unos 0,6 bloques, llevan mochila y una herramienta según su profesión.
- Se quedan cerca de su aldea, huyen de los monstruos y se asustan si los golpeas.
- **Clic derecho** sobre un gnomo para **hablar** con él: charla, trabajo, consejos, rumores y **trueques**.
- Si llevas puesto el **Gorro de gnomo**, te saludan distinto y los tratos te cuestan 1 menos.

## Aldeas (cada una con su aspecto y sus gnomos)
| Aldea | Biomas | Características |
|---|---|---|
| Prado | llanuras, bosques, prados | casas con gorros de colores, granjas, estatuas de gnomo y fogatas |
| Hongos | pantano, manglar, bosque oscuro | casas de seta, torre de setas luminosas y mucho verde |
| Nevada | taiga, llanuras nevadas | casas de abeto con techo de nieve y hoguera central |
| Minera | colinas ventosas, tierras baldías | casas de piedra, pozo de mina y montones de mineral |

Todas tienen casa del anciano, almacén con cofre de botín, farolas y gnomos paseando.

## Bloques y objetos
- Ladrillos de gnomo (con escaleras y losa), Bloque de paja, Lámpara de seta (luz).
- Estatua de gnomo (5 colores: sale uno al azar al colocarla; **clic derecho con la mano vacía** para cambiarlo).
- Gorro de gnomo (casco), Pan de gnomo, Seta luminosa (visión nocturna) y huevo generador.
- Pestaña propia en el creativo: **Gnomos**.

## Probarlo
- `/locate structure gnomos:meadow_village` (también `mushroom_village`, `frost_village`, `mine_village`)
- `/place structure gnomos:meadow_village` para generar una aldea donde estás
- `/summon gnomos:gnome ~ ~ ~ {Variant:1,Profession:2}` (Variant 0-4, Profession 0-4)

## Compilar con GitHub
1. Sube **todo el contenido** de esta carpeta a un repositorio (son menos de 100 archivos; incluye la carpeta oculta `.github`, y si no se sube, créala a mano: `.github/workflows/build.yml`).
2. Pestaña **Actions** → se ejecuta sola. Cuando salga el check verde, baja el `.jar` desde **Artifacts → gnomos-jar**.
3. Ponlo en la carpeta `mods` de Forge 1.20.1.
