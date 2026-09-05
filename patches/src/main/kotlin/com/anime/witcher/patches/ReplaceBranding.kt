package com.anime.witcher.patches

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.removeInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.StringReference

/**
 * Telegram + About-screen credit part of [rebrandingPatch].
 *
 * These strings live in dex, so they can only be changed by a bytecode patch. The
 * patch is wired as a dependency of "Rebranding": it only runs when that patch is
 * enabled, so a stock build stays completely untouched.
 *
 * - Points every Telegram contact link in the app to https://t.me/CATSM0KER by replacing
 *   the original support username "animewitcher_support". A handful of call sites compose
 *   the URL ("https://t.me/" + username), one already embeds the full URL.
 * - Credits the builder inside the in-app About screen: appends a styled
 *   "✦ Patched by Catsmoker ✦" line under the version text and renders it bold.
 */
@Suppress("unused")
val replaceBrandingPatch = bytecodePatch(
    name = "Rebranding: Telegram & About",
    description = "Part of Rebranding: points Telegram links to https://t.me/CATSM0KER and adds a bold -Patched by Catsmoker- line to the About screen. Original APK: https://www.animewitcher.com/",
    default = false,
) {
    compatibleWith(COMPATIBILITY_ANIME_WITCHER)

    execute {
        val replacements = mapOf(
            "animewitcher_support" to "CATSM0KER",
            "https://t.me/animewitcher_support?text=" to "https://t.me/CATSM0KER?text=",
            "اصدار التطبيق : 1.4.8" to "اصدار التطبيق : 1.4.8\\n\\n\u2726 Patched by Catsmoker \u2726",
        )

        classDefForEach { classDef ->
            val mutableClass = mutableClassDefBy(classDef)
            mutableClass.methods.forEach methodLoop@{ method ->
                val implementation = method.implementation ?: return@methodLoop

                val matches = implementation.instructions.withIndex().mapNotNull { (index, instruction) ->
                    val reference = (instruction as? ReferenceInstruction)?.reference as? StringReference
                    val oldValue = (reference as? StringReference)?.string
                    val newValue = oldValue?.let { old -> replacements[old] }
                    if (newValue == null) {
                        return@mapNotNull null
                    }
                    val register = (instruction as? OneRegisterInstruction)?.registerA
                    if (register == null) {
                        return@mapNotNull null
                    }
                    index to (register to newValue)
                }

                matches.asReversed().forEach { (index, registerAndValue) ->
                    method.removeInstruction(index)
                    method.addInstruction(
                        index,
                        "const-string v${registerAndValue.first}, \"${registerAndValue.second}\""
                    )
                }
            }
        }

        val aboutOnCreate = AboutActivityOnCreateFingerprint.method
        val aboutImplementation = aboutOnCreate.implementation ?: return@execute
        val insertIndex = aboutImplementation.instructions.size - 1
        aboutOnCreate.addInstructions(
            insertIndex,
            """
            const/4 v1, 0x1

            invoke-virtual {v0, v1}, Landroid/widget/TextView;->setTypeface(Landroid/graphics/Typeface;I)V
            """.trimIndent()
        )
    }
}