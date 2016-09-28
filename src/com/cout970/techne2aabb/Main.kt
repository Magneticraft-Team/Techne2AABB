package com.cout970.techne2aabb

import java.io.File

/**
 * Created by cout970 on 21/08/2016.
 */

fun main(args: Array<String>) {
    val input = File("./models")
    val output = File("./result")
    input.mkdir()
    output.mkdir()
    for(f in input.listFiles()){
        Translator.translate(f, File(output, f.name.replace(".tcn", ".txt")))
    }
    println("Finish")
}

