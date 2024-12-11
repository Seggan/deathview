package io.github.seggan.kmixin

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes.ASM9
import java.io.File

class JavaGenerator(val file: File) {

    private val reader = ClassReader(file.readBytes())

    val isKotlinMixin: Boolean

    init {
        val visitor = AnnotationFinder()
        reader.accept(visitor, 0)
        isKotlinMixin = visitor.foundMixin && visitor.foundKMeta
    }

    private class AnnotationFinder : ClassVisitor(ASM9) {

        var foundMixin = false
        var foundKMeta = false

        override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
            println(descriptor)
            return super.visitAnnotation(descriptor, visible)
        }
    }

    fun doStuff(): String {
        return file.name + "JavaMixinGen"
    }
}