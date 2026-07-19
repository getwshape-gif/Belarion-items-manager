# Belarion Items Manager

Gestionnaire de Custom Items compatible Minecraft 1.8.x / Spigot-Paper 1.8, independant
du plugin Bela-Customs existant (qui continue de gerer ses 20 items actuels tel quel).
Ce plugin sert a ajouter facilement de **nouveaux** Custom Items sans toucher au code Java :
tout se configure dans `items.yml`.

## Compilation

Pas de Maven/JDK necessaire en local : pousse ce dossier sur GitHub, le workflow
`.github/workflows/build.yml` compile automatiquement a chaque push (JDK 8 + Maven) et
depose le `.jar` compile dans l'onglet **Actions -> Artifacts** du repo.

## Commandes

- `/bi give <joueur> <item>` — donner un Custom Item (permission `belarionitems.admin`)
- `/bi list` — lister tous les items configures
- `/bi reload` — recharger `items.yml` sans redemarrer le serveur
- `/bi debug` — infos sur l'item tenu en main (material, durability-data, id, texture)

## Ajouter un nouvel item (aucun code requis)

1. Ajoute une entree dans `items.yml` :

```yaml
items:
  emerald_hoe:
    material: DIAMOND_HOE
    texture: emerald_hoe
    durability-data: 2003
    name: "&aHoue en Émeraude"
    lore:
      - "&7Outil custom"
```

2. `/bi reload`
3. Cree le fichier resource pack correspondant (voir section suivante).

**Important : plage de `durability-data`.** Le plugin Bela-Customs existant utilise deja
les valeurs 1001 a 1020. Pour ce plugin, reserve **2001 et au-dessus** afin d'eviter tout
conflit visuel entre les deux systemes sur un meme material vanilla.

## Correspondance avec le Resource Pack (IMPORTANT)

Contrairement a Bela-Customs (dont la technique exacte cote resource pack n'est pas
documentee ici), **Belarion Items Manager utilise directement la valeur brute de
durability/Damage de l'ItemStack** (pas un tag NBT `CustomModelData` separe). Cote
resource pack, avec OptiFine (technique CIT 1.8-compatible), il faut donc utiliser le
predicat `damage=` et non `nbt.CustomModelData=`.

Pour chaque item de `items.yml`, cree dans le resource pack :

**1) La texture** (icone inventaire/main/sol) :
```
assets/minecraft/textures/items/<texture>.png
```

**2) Le fichier CIT** (icone conditionnelle selon la durability-data) :
```
assets/minecraft/optifine/cit/items/<id>.properties
```
avec, pour l'exemple `emerald_hoe` ci-dessus :
```
type=item
items=diamond_hoe
damage=2003
texture=emerald_hoe
```
Et copier `emerald_hoe.png` directement dans `assets/minecraft/optifine/cit/` (a la
racine de ce dossier, a cote du fichier `.properties`).

**3) Pour une armure**, meme principe avec `type=armor` :
```
type=armor
items=diamond_helmet
damage=2010
texture.diamond_layer_2=mon_armure_layer_2
```
(voir la doc OptiFine CIT « Armor » pour le detail layer_1/layer_2 selon la piece).

Cette approche ne modifie **jamais** les fichiers vanilla existants (`textures/items/diamond_*.png`,
`textures/models/armor/diamond_layer_*.png`) : elle ajoute uniquement de nouveaux fichiers
`.properties` + textures dedies, exactement comme le reste du Resource Pack Belarion.

## Architecture

```
fr.belarion.belarionitems
├── BelarionItemsManager.java   point d'entree
├── model/CustomItemDefinition.java
├── config/ItemsConfig.java     chargement/validation de items.yml
├── registry/ItemRegistry.java  construction + identification des ItemStack
├── nbt/NBTUtil.java            tag NBT "BelarionItemId" (reflexion, version-safe 1.8)
├── api/BelarionItemsAPI.java   interface publique (ServicesManager)
├── api/BelarionItemsAPIImpl.java
├── commands/BelarionItemsCommand.java
└── listeners/ItemIdentityListener.java   preservation d'identite (enclume/enchant)
```

## API pour les autres plugins (ex: Belarion-Enchants)

```java
RegisteredServiceProvider<BelarionItemsAPI> provider =
        Bukkit.getServicesManager().getRegistration(BelarionItemsAPI.class);

if (provider != null) {
    BelarionItemsAPI api = provider.getProvider();
    if (api.isCustomItem(itemStack)) {
        CustomItemDefinition def = api.getDefinition(itemStack);
        // def.getId(), def.getMaterial(), def.getTexture(), def.getDurabilityData()...
    }
}
```
