# Anime Witcher Morphe Patches

Patches for **Anime Witcher** (`com.anime.witcher`) v1.4.8.

> **Note:** Google login is no longer working.

### How to use

Click here to add these patches to Morphe: https://morphe.software/add-source?github=catsmoker/anime-witcher-patches

Or build locally:
```
./gradlew buildAndroid
```
The `.mpp` file will be in `patches/build/libs/patches-*.mpp`. Apply it using [Morphe Desktop](https://github.com/MorpheApp/morphe-desktop).

## 🩹 Patches list

<!-- PATCHES_START EXPANDED -->
> **[v1.0.0](https://github.com/catsmoker/anime-witcher-patches/releases/tag/v1.0.0)**&nbsp;&nbsp;•&nbsp;&nbsp;`main`&nbsp;&nbsp;•&nbsp;&nbsp;6 patches total
<details open>
<summary>📦 Anime Witcher&nbsp;&nbsp;•&nbsp;&nbsp;6 patches</summary>
<br>

**🎯 Supported versions:**

| 1.4.8 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Android TV support](#android-tv-support) | Adds Android TV / Fire TV support (manifest declarations + TV launcher entry). |  |
| [Disable ads](#disable-ads) | Disables all ad display logic in the app. |  |
| [Disable VPN detection and alerts](#disable-vpn-detection-and-alerts) | Removes VPN/DNS detection and the VPN alert dialogs. |  |
| [Remove AppLovin initialization](#remove-applovin-initialization) | Removes AppLovin SDK initialization from the Application class. |  |
| [Remove startup popup](#remove-startup-popup) | Removes the update/changelog popup that appears when the app starts. |  |
| [Play with external player](#play-with-external-player) | Opens episodes in a system intent chooser so the user can pick their favorite video player (VLC, MX Player, etc.). |  |

</details>

<!-- PATCHES_END -->

## License

GPLv3
