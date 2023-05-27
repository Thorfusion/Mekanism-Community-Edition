![](https://files.thorfusion.com/mekanism/MEKBANNERWHITE1122.png)

Originally made for Terralization Modpack with fixes from several different forks. Now with new features, bugfixes and performance enhancement.

#### Download at [curseforge](https://www.curseforge.com/minecraft/mc-mods/mekanism-ce)

#### Builds at our [maven](https://maven.thorfusion.com/ui/repos/tree/General/thorfusion/mekanism/Mekanism-1.12.2-Community-Edition)

### Building Mekanism 1.12.2 Community Edition

```bash
./gradlew fullBuild
```
You will find the files inside ./output/



## For modpacks
You need to include the [LICENSE](https://raw.githubusercontent.com/Thorfusion/Mekanism-1.7.10-Community-Edition/1.12.2/LICENSE.md) for Mekanism 1.12.2 Community Edition and Aidanbrady as author, if your system supports it indicate that this is an custom version and give appreciable credits

Mekanism CE has continued the use of the update notifier but changed the config name to v2. This is to notify people making the switch to CE of this feature. It is recommended for modpacks to disable this.

## Changes

### Featured changes

| MEK:CE 1.12.2 Featured Changes                                                                                              | MK                 | MK:[CE](https://www.curseforge.com/minecraft/mc-mods/mekanism-ce) |
|-----------------------------------------------------------------------------------------------------------------------------|--------------------|-------------------------------------------------------------------|
| Open Source                                                                                                                 | :heavy_check_mark: | :heavy_check_mark:                                                |
| Dupe: [Wood dupe with Precision Sawmill and trapdoors](https://github.com/Thorfusion/Mekanism-Community-Edition/issues/124) | :x:                | :heavy_check_mark:                                                |
| Crash: [fixes an npe crash for cables](https://github.com/Thorfusion/Mekanism-Community-Edition/issues/103)                 | :x:                | :heavy_check_mark:                                                |
| Crash: Fix Tier installer NullPointerException onItemUse                                                                    | :x:                | :heavy_check_mark:                                                |
| Bug: limit crystallizer to receive gas mutiples of recipe input                                                             | :x:                | :heavy_check_mark:                                                |
| Performance: Optimize cable emitting                                                                                        | :x:                | :heavy_check_mark:                                                |
| Performance: New factory sort algorithm                                                                                     | :x:                | :heavy_check_mark:                                                |

### Full changelog

Note that changes not by the mekanism ce team has been added by us from their respective forks. any issues with these changes should be directed to us.

#### thedeadferryman
+ Update auto-sorting in factories, replaced by Thorfusion#107

#### Clem-Fern
+ Fix Tier installer NullPointerException onItemUse [crash fix]
  + "I've had crashes from using tier installers in e2e, so I'm assuming this is what the fix is" - [SteaSteaStea](https://github.com/Krutoy242/Enigmatica2Expert-Extended/issues/176#issuecomment-1452396981)

#### Addalyn
+ Check and make sure toolsItem is not a empty stack [bug fix]
+ Fix DigitalMiner to use getDiameter() instead of radius was issue with

#### Gurkonier
+ Add implementation for dynamic tank [feature]
v
#### fs-vault
+ Optimize cable emitting [Performance improvement on server tick]
+ Use O(1) operations where possible in FrequencyManager [Performance improvement]
+ Rework FrequencyManager [Performance improvement]

#### sapphi-red
+ limit crystallizer to receive gas mutiples of receipe input [bug fix]
  + "this should fix gases getting stuck in machines and blocking your whole ore processing setup" - [SteaSteaStea](https://github.com/Krutoy242/Enigmatica2Expert-Extended/issues/176#issuecomment-1452396981)

#### [maggi373](https://github.com/maggi373) - Mekanism CE Team
+ Ported config file structure from mek:ce 1.7.10
+ updates gradle to 5.6.4 Thorfusion#83
+ ported mek:ce 1.7.10 config file structure Thorfusion#83
+ fixed versioning
+ add publish to maven for 1.12.2 Thorfusion#93
+ port some changes done in mek:ce 1.7.10 Thorfusion#95
  + ports terralizationcompat
    + adds quartzcompat
    + adds diamondcompat
    + adds poororescompat
  + port siliconcompat
  + port recipechange for rubber in sawmill
  + ports the recipe change for enriched alloy from iron to steel
  + also adds a new configfile for mek:ce configs
  + fixes gui settings for mek generators and mek tools
+ fixes an npe crash for cables Thorfusion#103
+ fixes wood dupe with sawmill and trapdoors Thorfusion#124

#### KasumiNova
+ New factory sort algorithm for MekCE by KasumiNova Thorfusion#107 [Performance improvement]

# License

[LICENSE](https://raw.githubusercontent.com/Thorfusion/Mekanism-1.7.10-Community-Edition/1.12.2/LICENSE.md)

[ORIGINAL MOD](https://github.com/mekanism/Mekanism)
