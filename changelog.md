### 2.0

* Added `inspectBiome` command, to show Biome Properties.
* `inspectBiome` arguments:
> `color` | Returns all Color properties. \
> `tags` | Returns all Biome Tags. \
> `features` | Returns all Biome Features. \
> `sounds` | Returns all Biome Sounds.
* All commands are now camelCase.
* All commands now return Errors, if something went wrong.
* Commands that return Tags no longer have Whitespace before tag IDs.