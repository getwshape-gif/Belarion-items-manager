# Modeles OptiFine CIT — Belarion Items Manager

Ces fichiers sont des **modeles a copier** dans le Resource Pack (pas dans le
plugin lui-meme). A utiliser quand tu refais le pack.

## Principe

Le plugin donne chaque Custom Item avec un tag NBT `BelarionItemId` (ex:
`BelarionItemId=emerald_hammer`, voir `items.yml`). OptiFine CIT lit ce tag
directement — **aucun CustomModelData, aucune durability truquee**. Les
items vanilla (pioche diamant normale, etc.) n'ont pas ce tag et gardent
donc automatiquement leur texture d'origine : les deux systemes coexistent
sans jamais se toucher.

## Ou copier ces fichiers

```
assets/minecraft/optifine/cit/items/emerald_hammer.properties
assets/minecraft/optifine/cit/items/emerald_pickaxe.properties
assets/minecraft/optifine/cit/armor/exemple_armure.properties   (a adapter)
```

Et les textures PNG correspondantes directement a la racine de :

```
assets/minecraft/optifine/cit/emerald_hammer.png
assets/minecraft/optifine/cit/emerald_pickaxe.png
```

(le nom seul dans `texture=emerald_hammer` pointe vers
`optifine/cit/emerald_hammer.png` — pas besoin du sous-dossier `items/`).

Pour l'armure, les textures vont dans le dossier vanilla standard :
```
assets/minecraft/textures/models/armor/<nom>_layer_1.png
assets/minecraft/textures/models/armor/<nom>_layer_2.png
```

## Ajouter un nouvel item

1. Ajoute l'entree dans `items.yml` du plugin (id, material, texture, name, lore).
2. Cree `assets/minecraft/optifine/cit/items/<id>.properties` sur ce modele :
   ```
   type=item
   items=<material_vanilla_minuscule>
   nbt.BelarionItemId=<id>
   texture=<texture>
   weight=1000
   ```
3. Copie `<texture>.png` a la racine de `assets/minecraft/optifine/cit/`.
4. `/bi reload` cote serveur (le plugin n'a besoin de rien recompiler).

Aucun fichier vanilla (`textures/items/diamond_pickaxe.png`, modeles JSON
vanilla, etc.) n'est jamais modifie.

## Compatibilite avec les Resource Packs personnels des joueurs (IMPORTANT)

Certains joueurs activent en plus leur propre pack (ex: un pack PvP qui
recolore les epees, les blocs, l'interface...). Le systeme est concu pour
que :

- **Les Custom Items Belarion affichent toujours leur vraie texture**, peu
  importe le pack personnel du joueur.
- **Les items vanilla restent libres** : le pack personnel du joueur continue
  de s'appliquer normalement dessus (epee diamant, blocs, particules,
  interface...). Le pack serveur Belarion ne les touche jamais.

### Pourquoi ca marche

1. **Empilement des packs.** Quand un joueur accepte le Resource Pack du
   serveur, Minecraft l'insere avec la priorite la plus haute au-dessus des
   packs locaux du joueur. Comme notre pack ne fournit **jamais** de fichier
   vanilla (`textures/items/diamond_sword.png`, etc.), il n'y a rien a
   "ecraser" : pour tout ce que notre pack ne definit pas, le pack personnel
   du joueur (ou le defaut vanilla) s'applique normalement en dessous.

2. **Le vrai point d'attention : les regles CIT, pas les fichiers.** Un item
   Custom Belarion reste techniquement un material vanilla (ex:
   `DIAMOND_AXE` pour le Hammer en Emeraude). Si le pack personnel du joueur
   contient une regle CIT generique du style `type=item items=diamond_axe`
   *sans condition NBT* (pour recolorer toutes les haches en diamant), cette
   regle et la notre matchent **toutes les deux** le meme item. OptiFine doit
   alors choisir laquelle appliquer. D'apres la doc OptiFine : *"si plusieurs
   regles CIT matchent le meme item, seule la premiere est utilisee (triee
   par weight, puis par nom de fichier)"* — **l'ordre des packs n'entre pas
   en jeu ici**, seul le `weight` (et le nom de fichier en cas d'egalite)
   depart age.
   
   C'est pour ca que **tous les fichiers CIT Belarion doivent avoir
   `weight=1000`** (deja fait dans les modeles de ce dossier) : un pack
   personnel de joueur utilise presque toujours le `weight` par defaut (0,
   quand la ligne est absente), donc notre regle gagne systematiquement.

3. **Nos regles ne peuvent jamais "deborder" sur du vanilla.** Chaque fichier
   CIT Belarion est conditionne par `nbt.BelarionItemId=<id>` — un tag qu'
   *aucun* item vanilla ne possede jamais. Nos regles ne matchent donc
   *que* nos propres Custom Items, jamais les items vanilla normaux d'un
   joueur, meme s'ils partagent le meme material de base.

### Limite connue (Minecraft 1.8)

Un joueur peut refuser le Resource Pack du serveur (pas de "pack obligatoire"
natif en 1.8, contrairement aux versions recentes). Dans ce cas, il ne voit
simplement **pas** la texture custom : l'item s'affiche avec l'apparence
vanilla par defaut du material de base (ex: une pioche diamant normale). Ce
n'est **pas** un bug visuel (pas de texture rose/noire) — le nom, le lore et
le tag NBT `BelarionItemId` restent corrects independamment du rendu
graphique. Si tu veux forcer l'acceptation du pack, ca se gere cote serveur
(hors resource pack), par exemple via un plugin qui kick/avertit les joueurs
qui refusent.
