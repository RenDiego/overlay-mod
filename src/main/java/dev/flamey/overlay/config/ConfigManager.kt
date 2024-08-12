package dev.flamey.overlay.config

import dev.flamey.overlay.Main.toggled
import dev.flamey.overlay.Overlay
import dev.flamey.overlay.Overlay.x
import dev.flamey.overlay.Overlay.y
import dev.flamey.overlay.command.gui.CommandGUI
import dev.flamey.overlay.command.gui.CommandGUI.Companion.keybind
import net.minecraft.client.Minecraft
import java.io.*

class ConfigManager {

    private val file = File(Minecraft.getMinecraft().mcDataDir.canonicalPath + "/config/overlay.bruh").also {
           if (!it.exists()) {
               it.createNewFile()
           }
    }

    fun save() {
        val writer = BufferedWriter(FileWriter(file))
        val keybind = CommandGUI.keybind
        val x = Overlay.x
        val y = Overlay.y
        val toggled = toggled
        writer.write("keybind=$keybind\nx=$x\ny=$y\ntoggled=$toggled")
        writer.flush()
        writer.close()
    }

    fun load() {
        BufferedReader(FileReader(file)).use { reader ->
            val lines = reader.readLines()
            if (lines.isEmpty()) {
                save()
                return
            }

            for (line in lines) {
                val parts = line.split("=")
                when (parts[0]) {
                    "keybind" -> keybind = parts[1].trim().toInt()
                    "x" -> x = parts[1].trim().toInt()
                    "y" -> y = parts[1].trim().toInt()
                    "toggled" -> toggled = parts[1].trim().toBoolean()
                    else -> {
                        println("[ConfigManager] Invalid Config Detected")
                        save()
                    }
                }
            }
        }
    }


}