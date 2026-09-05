package com.anime.witcher.patches

import app.morphe.patcher.patch.resourcePatch
import java.awt.BasicStroke
import java.awt.Color
import java.awt.RenderingHints
import java.awt.geom.Line2D
import java.io.File
import javax.imageio.ImageIO
import org.w3c.dom.Element

/**
 * Rebrands the modded build into its own "Anime Witcher +" entry. Optional — when
 * enabled it does all of:
 *
 * - Changes the install/launcher package ("application id") to
 *   `app.catsmoker.anime.witcher` so the mod is its own app entry. Every manifest
 *   component that references a class by a relative `.Name` is rewritten to its
 *   absolute `com.anime.witcher.Name` form, because relative names resolve against
 *   the (new) package while the classes themselves stay put.
 * - Renames the visible app label to "Anime Witcher +".
 * - Adds a small red "+" badge to the launcher icon (adaptive foreground +
 *   legacy icons, all densities). The `android:banner` points at the same mipmap,
 *   so the TV banner picks up the badge automatically.
 * - Pulls in [replaceBrandingPatch] (the Telegram + About-screen credit), which
 *   can only exist as a bytecode patch because those strings live in dex.
 */
@Suppress("unused")
val rebrandingPatch = resourcePatch(
    name = "Rebranding",
    description = "Renames the app to 'Anime Witcher +', changes the package id to app.catsmoker.anime.witcher, badges the icon with a red +, points Telegram links to https://t.me/CATSM0KER and credits the About screen. Original APK: https://www.animewitcher.com/",
    default = false,
) {
    compatibleWith(COMPATIBILITY_ANIME_WITCHER)
    dependsOn(replaceBrandingPatch)

    execute {
        document("AndroidManifest.xml").use { document ->
            val manifest = document.getElementsByTagName("manifest").item(0) as Element
            manifest.setAttribute("package", "app.catsmoker.anime.witcher")

            val application = document.getElementsByTagName("application").item(0) as Element
            application.setAttribute("android:label", "Anime Witcher +")

            fun absolutizePackageRelative(el: Element, attribute: String) {
                val value = el.getAttribute(attribute)
                if (value.startsWith(".")) {
                    el.setAttribute(attribute, "com.anime.witcher$value")
                }
            }

            listOf(
                "application",
                "activity",
                "activity-alias",
                "service",
                "receiver",
                "provider",
            ).forEach { tag ->
                val nodes = document.getElementsByTagName(tag)
                for (i in 0 until nodes.length) {
                    val el = nodes.item(i) as Element
                    absolutizePackageRelative(el, "android:name")
                    absolutizePackageRelative(el, "android:process")
                    absolutizePackageRelative(el, "android:taskAffinity")
                }
            }

            // Permissions declared with an absolute name owned by the stock package
            // (DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION, C2D_MESSAGE, ...) collide on
            // devices where the stock app is installed: installing a second package
            // that redeclares them fails with INSTALL_FAILED_DUPLICATE_PERMISSION. Move
            // every com.anime.witcher.* permission under our own package instead.
            listOf("permission", "uses-permission").forEach { tag ->
                val nodes = document.getElementsByTagName(tag)
                for (i in 0 until nodes.length) {
                    val el = nodes.item(i) as Element
                    val value = el.getAttribute("android:name")
                    if (value.startsWith("com.anime.witcher.")) {
                        el.setAttribute("android:name", "app.catsmoker.anime.witcher${value.removePrefix("com.anime.witcher")}")
                    }
                }
            }
        }

        val densities = listOf("mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi")
        val variants = listOf("ic_launcher_foreground.png", "ic_launcher.png")

        densities.forEach { density ->
            variants.forEach { variant ->
                val path = "res/mipmap-$density/$variant"
                val icon =
                    try {
                        get(path, false)
                    } catch (e: Exception) {
                        null
                    }
                if (icon != null) {
                    drawCornerPlus(icon)
                }
            }
        }
    }
}

/**
 * Paints a small red "+" badge in the top-right quadrant of the icon, with a thin
 * white outline so it stays readable over the dark logo. Placement inside the
 * adaptive icon safe zone keeps it visible even on launchers that mask the icon.
 */
private fun drawCornerPlus(file: File) {
    val image = try {
        ImageIO.read(file)
    } catch (e: Exception) {
        return
    } ?: return

    val size = minOf(image.width, image.height)
    if (size < 24) {
        return
    }

    val graphics = image.createGraphics() ?: return
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)

    val thickness = size * 0.050f
    val arm = size * 0.035f
    val centerX = size * 0.640f
    val centerY = size * 0.360f

    // White outline drawn a bit larger behind the red cross for contrast.
    graphics.color = Color.WHITE
    graphics.stroke = BasicStroke(thickness * 1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
    graphics.draw(Line2D.Float(centerX - arm, centerY, centerX + arm, centerY))
    graphics.draw(Line2D.Float(centerX, centerY - arm, centerX, centerY + arm))

    graphics.color = Color(229, 57, 53, 255)
    graphics.stroke = BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
    graphics.draw(Line2D.Float(centerX - arm, centerY, centerX + arm, centerY))
    graphics.draw(Line2D.Float(centerX, centerY - arm, centerX, centerY + arm))

    graphics.dispose()

    try {
        ImageIO.write(image, "png", file)
    } catch (e: Exception) {
        // Ignore: the badge is cosmetic and must never fail the patch.
    }
}