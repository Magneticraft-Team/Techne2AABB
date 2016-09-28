package com.cout970.techne2aabb

import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.vec3Of
import com.cout970.vector.impl.Vector3f
import com.cout970.vector.impl.Vector3i
import java.io.File
import java.io.FileOutputStream
import java.io.Writer
import java.util.zip.ZipFile
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by cout970 on 21/08/2016.
 */
object Translator {
    fun translate(input: File, output: File) {
        try {
            val zip = ZipFile(input)
            val entry = zip.getEntry("model.xml")
            val stream = zip.getInputStream(entry)
            val documentBuilderFactory = DocumentBuilderFactory.newInstance()
            val documentBuilder = documentBuilderFactory.newDocumentBuilder()
            val document = documentBuilder.parse(stream)

            val shapes = document.getElementsByTagName("Shape")
            val list = mutableListOf<AABB>()

            for (i in 0 until shapes.length) {
                val shape = shapes.item(i)
                val shapeAttributes = shape.attributes ?: error("Shape #" + (i + 1) + " in " + input + " has no attributes")

                val name = shapeAttributes.getNamedItem("name")
                var shapeName: String? = null
                if (name != null) {
                    shapeName = name.nodeValue
                }
                if (shapeName == null) {
                    shapeName = "Shape #" + (i + 1)
                }

                var shapeType: String? = null
                val type = shapeAttributes.getNamedItem("type")
                if (type != null) {
                    shapeType = type.nodeValue
                }
                if (shapeType != "d9e621f7-957f-4b77-b1ae-20dcd0da7751") {
                    error("Model shape [$shapeName] in $input is not a cube, ignoring")
                }

                var offset = arrayOfNulls<String>(3)
                var position = arrayOfNulls<String>(3)
                var size = arrayOfNulls<String>(3)

                val shapeChildren = shape.childNodes
                for (j in 0..shapeChildren.length - 1) {
                    val shapeChild = shapeChildren.item(j)

                    val shapeChildName = shapeChild.nodeName
                    var shapeChildValue: String? = shapeChild.textContent
                    if (shapeChildValue != null) {
                        shapeChildValue = shapeChildValue.trim { it <= ' ' }

                        when (shapeChildName) {
                            "Offset" -> offset = shapeChildValue.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            "Position" -> position = shapeChildValue.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            "Size" -> size = shapeChildValue.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        }
                    }
                }

                val cubeSize = Vector3i(Integer.parseInt(size[0]), Integer.parseInt(size[1]), Integer.parseInt(size[2]))
                val cubeOffset = Vector3f(java.lang.Float.parseFloat(offset[0]), (-java.lang.Float.parseFloat(offset[1])), java.lang.Float.parseFloat(offset[2]))
                val cubePosition = Vector3f(java.lang.Float.parseFloat(position[0]), -cubeSize.y - java.lang.Float.parseFloat(position[1]), java.lang.Float.parseFloat(position[2]))

                val pos = (cubePosition + cubeOffset) + vec3Of(8, 24, 8)
                list.add(AABB(pos, pos + cubeSize))
            }

            export(list, output)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun export(list: List<AABB>, output: File) {
        val stream = FileOutputStream(output)
        stream.use {
            val writer = stream.writer()

            writer.print("listOf(\n")
            for (i in list) {
                writer.print("Vec3d(${i.min.x}, ${i.min.y}, ${i.min.z}) * PIXEL to Vec3d(${i.max.x}, ${i.max.y}, ${i.max.z}) * PIXEL,\n")
            }
            writer.print(")\n")
            writer.flush()
        }
    }

    fun Writer.print(str: String) {
        println(str)
        append(str)
    }
}