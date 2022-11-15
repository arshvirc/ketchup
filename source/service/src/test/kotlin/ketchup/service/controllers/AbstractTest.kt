package ketchup.service.controllers

import java.io.File
import java.io.PrintWriter

abstract class AbstractTest {
    protected fun createFile(filename: String) {
        var file = File(filename)
        file.createNewFile()
        val writer = PrintWriter(file)
        writer.print("")
        writer.close()
    }

    protected fun createBlankFile(f : File) {
        f.createNewFile()
        // clear file contents
        val writer = PrintWriter(f)
        writer.print("")
        writer.close()
    }

    protected fun deleteFile(filename: String) {
        val file = File(filename);
        val result = file.delete()
        if (result == false) {
            println("Error deleting $filename")
        }
    }
}