/*
 * Copyright (C) 2026 piko <https://github.com/crimera/piko>
 *
 * See the included NOTICE file for GPLv3 ยง7(b) terms that apply to this code.
 */

package app.crimera.patches.instagram.misc.highPerformanceMode

import app.crimera.patches.instagram.misc.extension.hooks.instagramInitHook
import app.crimera.patches.instagram.misc.settings.settingsPatch
import app.crimera.patches.instagram.utils.Constants.COMPATIBILITY_INSTAGRAM
import app.crimera.patches.instagram.utils.Constants.PATCHES_DESCRIPTOR
import app.crimera.patches.instagram.utils.enableSettings
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.instructions
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode

private const val EXTENSION_CLASS = "$PATCHES_DESCRIPTOR/performance/HighPerformanceMode;"

@Suppress("unused")
val highPerformanceModePatch =
    bytecodePatch(
        name = "High performance mode",
        description = "Adds a setting that asks Android to keep Instagram at 60 Hz.",
    ) {
        dependsOn(settingsPatch)
        compatibleWith(COMPATIBILITY_INSTAGRAM)

        execute {
            instagramInitHook.fingerprint.method.apply {
                val superIndex = instructions.indexOfFirst { it.opcode == Opcode.INVOKE_SUPER }
                addInstruction(superIndex + 1, "invoke-static {}, $EXTENSION_CLASS->init()V")
            }

            enableSettings("highPerformanceMode")
        }
    }
    
