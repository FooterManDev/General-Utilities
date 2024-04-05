General Utilities adds useful commands for Mod/ModPack developers.
---
### Current features:
#### Inspect Block command: 
Inspects the Block the Player is looking at, for Properties, and sends those properties to the Player:
![image](https://github.com/FooterManDev/General-Utilities/assets/71946346/d85e7746-7dca-451e-8984-58d37cfdacb3)
`/inspect_block`

Args:                                                              
`sounds`: Inspects the Block's set sounds. Includes full paths.

This is not all General Utilities will offer. I want to add much more than this. Please, leave some Suggestions!

---
### Project pages:
Modrinth: https://modrinth.com/mod/general-utilities \
Curseforge: https://legacy.curseforge.com/minecraft/mc-mods/general-utilities/

### Contributing:
Fork the repo, then Clone the fork to your machine, open with Intellij. You know what to do from there.
Suggestions, Pulls, are all welcome.

### Maven:
Sorry, I don't have a Maven yet! Modrinth's Maven will do, if you want to use in your project for testing.

```gradle
repositories {
  exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth's Maven"
                url = "https://api.modrinth.com/maven"
            }
        }
        filter {
            includeGroup "maven.modrinth"
        }
    }
}

dependencies {
  modImplementation("maven.modrinth:general-utilities:${gutils_ver}")
}
```
This example is made with Arch Loom, not ForgeGradle.

---
