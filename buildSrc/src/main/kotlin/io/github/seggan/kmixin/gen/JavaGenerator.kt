package io.github.seggan.kmixin.gen

import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.ASM9
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import java.io.File
import kotlin.metadata.jvm.KotlinClassMetadata

class JavaGenerator(private val pkg: String, private val file: File) {

    private val reader = ClassReader(file.readBytes())
    private val slashPkg = pkg.replace('.', '/')

    val mixinName = "$slashPkg/${file.nameWithoutExtension}"
    val implName = "$slashPkg-impl/${file.nameWithoutExtension}"

    val annotations: Map<String, List<String>>

    val metadata: KotlinClassMetadata?
    val isMixin: Boolean

    init {
        val visitor = AnnotationFinder()
        reader.accept(visitor, 0)
        isMixin = visitor.foundMixin
        metadata = visitor.metadata
        annotations = visitor.annotations
    }

    private class AnnotationFinder : ClassVisitor(ASM9) {

        var foundMixin = false
        var metadata: KotlinClassMetadata? = null

        val annotations = mutableMapOf<String, MutableList<String>>()

        override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
            if (descriptor == Descriptors.KOTLIN_METADATA) {
                return MetadataReader(::metadata)
            } else if (descriptor == Descriptors.SPONGE_MIXIN) {
                foundMixin = true
            }
            return null
        }

        override fun visitMethod(
            access: Int,
            name: String,
            descriptor: String,
            signature: String?,
            exceptions: Array<out String>?
        ): MethodVisitor {
            return object : MethodVisitor(ASM9) {
                override fun visitAnnotation(aDescriptor: String, visible: Boolean): AnnotationVisitor? {
                    annotations.computeIfAbsent(name + descriptor) { mutableListOf() }.add(aDescriptor)
                    return null
                }
            }
        }
    }

    private inner class AnnotationReplacer(delegate: ClassVisitor) : ClassVisitor(ASM9, delegate) {
        private val replace = listOf(Metadata::class, Mixin::class, Inject::class).map { it.java.descriptorString() }

        override fun visit(
            version: Int,
            access: Int,
            name: String,
            signature: String?,
            superName: String,
            interfaces: Array<out String>?
        ) {
            super.visit(version, access, implName, signature, superName, interfaces)
        }

        override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
            if (replace.any { descriptor == it }) {
                return null
            }
            return super.visitAnnotation(descriptor, visible)
        }
    }

    fun doStuff() {
        val writer = ClassWriter(ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)
        val visitor = WrapperGenerator(writer, this)
        reader.accept(visitor, 0)
        file.writeBytes(writer.toByteArray())
        removeOldAnnotations()
    }

    private fun removeOldAnnotations() {
        val genDir = file.parentFile.resolveSibling("${pkg.substringAfterLast('.')}-impl")
        genDir.mkdirs()
        val writer = ClassWriter(reader, 0)
        val visitor = AnnotationReplacer(writer)
        reader.accept(visitor, 0)
        genDir.resolve(file.name).writeBytes(writer.toByteArray())
    }
}