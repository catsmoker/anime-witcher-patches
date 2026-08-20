# Anime Witcher Morphe Patches

Patches for **Anime Witcher** (`com.anime.witcher`) v1.4.8.

### Available patches

| Patch | Description |
|---|---|
| **Disable ads** | Disables all ad display logic (AppLovin, AdMob, StartApp) |
| **Remove AppLovin initialization** | Prevents AppLovin SDK from initializing on app start |
| **Replace AWPlayer with VLC** | Replaces the AWPlayer video player with VLC via system intent chooser |

### How to use

Click here to add these patches to Morphe: https://morphe.software/add-source?github=catsmoker/anime-witcher-patches

Or build locally:
```
./gradlew buildAndroid
```
The `.mpp` file will be in `patches/build/libs/patches-*.mpp`. Apply it using [Morphe Desktop](https://github.com/MorpheApp/morphe-desktop).

## License

GPLv3
