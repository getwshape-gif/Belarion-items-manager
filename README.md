Belarion Items Manager

Gestionnaire de Custom Items compatible Minecraft 1.8.x / Spigot-Paper 1.8, independant
du plugin Bela-Customs existant (qui continue de gerer ses 20 items actuels tel quel).
Ce plugin sert a ajouter facilement de nouveaux Custom Items sans toucher au code Java :
tout se configure dans items.yml.

Compilation

Pas de Maven/JDK necessaire en local : pousse ce dossier sur GitHub, le workflow
.github/workflows/build.yml compile automatiquement a chaque push (JDK 8 + Maven) et
depose le .jar compile dans l'onglet Actions -> Artifacts du repo.

Commandes


/bi give <joueur> <item> — donner un Custom Item (permission belarionitems.admin)
/bi list — lister tous les items configures
/bi reload — recharger items.yml sans redemarrer le serveur
/bi debug — infos sur l'item tenu en main (material, tag NBT BelarionItemId, id, texture)


Ajouter un nouvel item (aucun code requis)


Ajoute une entree dans items.yml :


yamlitems:
  emerald_hoe:
    material: DIAMOND_HOE
    texture: emerald_hoe
    name: "&aHoue en Émeraude"
    lore:
      - "&7Outil custom"


/bi reload
Cree le fichier resource pack correspondant (voir section suivante et le
dossier resourcepack-templates/ qui contient des modeles prets a copier).


Identification : tag NBT, pas de durability ni de CustomModelData

Chaque item cree par le plugin recoit automatiquement un tag NBT BelarionItemId
(ex: BelarionItemId=emerald_hoe, meme valeur que l'id dans items.yml). C'est
l'unique moyen d'identification utilise par le plugin (ItemRegistry.identify) :
il survit a la reparation a l'enclume, a l'enchantement, au renommage et au
deplacement d'inventaire. Il n'y a aucune durability truquee et aucun tag
CustomModelData — l'item garde sa durabilite vanilla reelle et son
comportement d'usure normal.

Correspondance avec le Resource Pack (IMPORTANT)

Belarion Items Manager est 100% compatible OptiFine CIT 1.8, sans CustomModelData.
OptiFine sait matcher un item sur n'importe quel tag NBT arbitraire via
nbt.<nom_du_tag>=<valeur> (c'est le meme mecanisme que celui qu'OptiFine utilise
en interne pour reconnaitre par exemple le type de potion via nbt.Potion=...).
On l'utilise donc directement sur le tag pose par le plugin :

nbt.BelarionItemId=<id de l'item dans items.yml>

Les items vanilla n'ont pas ce tag : ils ne sont donc jamais affectes par ces
regles CIT et gardent leurs textures d'origine sans modification.

Pour chaque item de items.yml, cree dans le resource pack :

1) La texture (icone inventaire/main/sol — un seul fichier suffit, type=item
s'applique automatiquement aux trois contextes) :

assets/minecraft/optifine/cit/<texture>.png

2) Le fichier CIT :

assets/minecraft/optifine/cit/items/<id>.properties

avec, pour l'exemple emerald_hoe ci-dessus :

type=item
items=diamond_hoe
nbt.BelarionItemId=emerald_hoe
texture=emerald_hoe
weight=1000

weight=1000 est obligatoire sur tous les fichiers CIT Belarion, items comme
armures. Un joueur peut avoir son propre Resource Pack personnel (pack PvP, etc.)
en plus de celui du serveur. Si ce pack contient une regle CIT generique qui
recolore tous les items d'un material sans condition NBT (ex: toutes les haches
diamant), notre regle et la sienne matchent toutes les deux notre Custom Item :
OptiFine departage uniquement par weight puis par nom de fichier (pas par
priorite de pack). Un weight eleve garantit que la texture Belarion l'emporte
toujours. Voir resourcepack-templates/README.md pour le detail complet
(empilement des packs, pourquoi les items vanilla restent libres, limite du
"pack obligatoire" en 1.8).

3) Pour une armure, meme principe avec type=armor (necessaire pour l'affichage
sur le corps du joueur) :

type=armor
items=diamond_helmet
nbt.BelarionItemId=emerald_helmet
texture.diamond_layer_1=emerald_helmet_layer_1
texture.diamond_layer_2=emerald_helmet_layer_2
weight=1000

Ici les textures vont dans assets/minecraft/textures/models/armor/ (dossier
vanilla standard), pas dans optifine/cit/. Voir resourcepack-templates/armor/
pour un modele complet a dupliquer par piece d'armure (helmet/chestplate/leggings/boots).

4) Modele 3D : pour un outil/arme/armure qui garde la forme vanilla (cas normal),
rien a faire — type=item/type=armor ne changent que la texture, le modele reste
celui du material de base (ex: item/handheld pour un outil). Un vrai modele 3D
personnalise (geometrie differente) necessiterait la propriete model= d'OptiFine
CIT, hors perimetre ici.

Cette approche ne modifie jamais les fichiers vanilla existants (textures/items/diamond_*.png,
textures/models/armor/diamond_layer_*.png, modeles JSON vanilla) : elle ajoute
uniquement de nouveaux fichiers .properties + textures dediees, exactement comme
le reste du Resource Pack Belarion. Voir resourcepack-templates/README.md pour
l'arborescence exacte et la marche a suivre a chaque nouvel item.

Architecture

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

resourcepack-templates/    modeles OptiFine CIT prets a copier dans le Resource Pack
├── README.md
├── items/emerald_hammer.properties
├── items/emerald_pickaxe.properties
└── armor/exemple_armure.properties
