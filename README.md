![](https://files.thorfusion.com/mekanism/header1122b.png)

Originally made for Terralization Modpack with fixes from several different forks. Now with new features, bugfixes and performance enhancement.

#### Download at [curseforge](https://www.curseforge.com/minecraft/mc-mods/mekanism-1-12-2-ce)

#### Builds at our [maven](https://maven.thorfusion.com/ui/repos/tree/General/thorfusion/mekanism/Mekanism-1.12.2-Community-Edition)

### Building Mekanism 1.12.2 Community Edition

```bash
./gradlew fullBuild
```
You will find the files inside ./build/libs/



## For modpacks
You need to include the [LICENSE](https://raw.githubusercontent.com/Thorfusion/Mekanism-1.7.10-Community-Edition/1.12.2/LICENSE.md) for Mekanism 1.12.2 Community Edition and Aidanbrady as author, if your system supports it indicate that this is an custom version and give appreciable credits

Mekanism CE has continued the use of the update notifier but changed the config name to v2. This is to notify people making the switch to CE of this feature. It is recommended for modpacks to disable this.

### Full changelog

Note that changes not by the mekanism ce team has been added by us from their respective forks. any issues with these changes should be directed to us.

#### thedeadferryman
+ Update auto-sorting in factories

#### Clem-Fern
+ Fix Tier installer NullPointerException onItemUse

#### Addalyn
+ Check and make sure toolsItem is not a empty stack
+ Fix DigitalMiner to use getDiameter() instead of radius was issue with

#### Gurkonier
+ Add implementation for dynamic tank

#### fs-vault
+ Optimize cable emitting
+ Use O(1) operations where possible in FrequencyManager
+ Rework FrequencyManager

# License

[LICENSE](https://raw.githubusercontent.com/Thorfusion/Mekanism-1.7.10-Community-Edition/1.12.2/LICENSE.md)

[ORIGINAL MOD](https://github.com/mekanism/Mekanism)
