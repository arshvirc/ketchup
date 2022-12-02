package ketchup.app

import java.io.File

data class WindowSize(val h: Double, val w: Double, val x: Double, val y: Double, var max: Double)

class Window {
    private var windowHeight: Double = 0.0
    private var windowWidth: Double = 0.0
    private var windowPosX: Double = 30.0
    private var windowPosY: Double = 30.0
    private var isMaximized: Double = 0.0
    private val fileName = "window.json"


    private val file = File(fileName)

    fun saveWindowSize(h: Double, w: Double, x: Double, y: Double, max: Double) {
        file.printWriter().use { out ->
            out.println("Height:$h")
            out.println("Width:$w")
            out.println("X:$x")
            out.println("Y:$y")
            out.println("Maximized:$max")
        }
    }

    fun getWindowSize(): WindowSize {
        if(file.exists()) {
            file.forEachLine { it ->
                val str = it.split(":")
                when(str[0]) {
                    "Height" -> windowHeight = str[1].toDouble()
                    "Width" -> windowWidth = str[1].toDouble()
                    "X" -> windowPosX = str[1].toDouble()
                    "Y" -> windowPosY = str[1].toDouble()
                    "Maximized" -> isMaximized = str[1].toDouble()
                }
            }

            return WindowSize(windowHeight, windowWidth, windowPosX, windowPosY, isMaximized)
        } else {
            saveWindowSize(550.0, 900.0, 0.0, 0.0,0.0)
            return WindowSize(550.0 ,900.0, 0.0, 0.0, 0.0)
        }
    }
}