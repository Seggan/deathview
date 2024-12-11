package io.github.seggan.kmixin

import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.ASM9
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import java.io.File

class JavaGenerator(private val file: File) {

    private val reader = ClassReader(file.readBytes())

    val isKotlinMixin: Boolean
    val emittedName = file.nameWithoutExtension + "JavaMixinGen"

    init {
        val visitor = AnnotationFinder()
        reader.accept(visitor, 0)
        isKotlinMixin = visitor.foundMixin && visitor.foundKMeta
    }

    private class AnnotationFinder : ClassVisitor(ASM9) {

        var foundMixin = false
        var foundKMeta = false

        override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
            if (descriptor == "Lkotlin/Metadata;") {
                foundKMeta = true
            } else if (descriptor == "Lorg/spongepowered/asm/mixin/Mixin;") {
                foundMixin = true
            }
            return null
        }
    }

    private class AnnotationReplacer(delegate: ClassVisitor) : ClassVisitor(ASM9, delegate) {
        private val replace = listOf(Metadata::class, Mixin::class, Inject::class)

        override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
            if (replace.any { descriptor == it.java.descriptorString() }) {
                return null
            }
            return super.visitAnnotation(descriptor, visible)
        }
    }

    private inner class WrapperGenerator(delegate: ClassVisitor) : ClassVisitor(ASM9, delegate) {

        lateinit var name: String

        override fun visit(
            version: Int,
            access: Int,
            name: String,
            signature: String?,
            superName: String,
            interfaces: Array<out String>
        ) {
            if (Opcodes.ACC_RECORD and access != 0) {
                throw IllegalStateException("Records are not supported")
            }
            val split = name.split('/')
            this.name = split.dropLast(1).joinToString("/") + "/$emittedName"
            super.visit(version, access, this.name, signature, superName, interfaces)
        }

        override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
            if (descriptor != "Lorg/spongepowered/asm/mixin/Mixin;") {
                return null
            }
            return super.visitAnnotation(descriptor, visible)
        }

        override fun visitInnerClass(name: String, outerName: String, innerName: String, access: Int) {
            // do nothing
        }

        override fun visitNestHost(nestHost: String) {
            // do nothing
        }

        override fun visitOuterClass(owner: String, name: String, descriptor: String) {
            // do nothing
        }

        override fun visitTypeAnnotation(
            typeRef: Int,
            typePath: TypePath?,
            descriptor: String?,
            visible: Boolean
        ): AnnotationVisitor? {
            return null
        }

        override fun visitAttribute(attribute: Attribute) {
            // do nothing
        }

        override fun visitNestMember(nestMember: String) {
            // do nothing
        }

        override fun visitRecordComponent(
            name: String,
            descriptor: String?,
            signature: String?
        ): RecordComponentVisitor? {
            return null
        }

        override fun visitPermittedSubclass(permittedSubclass: String) {
            // do nothing
        }

        override fun visitField(
            access: Int,
            name: String,
            descriptor: String,
            signature: String?,
            value: Any
        ): FieldVisitor? {
            return null
        }

        override fun visitMethod(
            access: Int,
            name: String,
            descriptor: String,
            signature: String?,
            exceptions: Array<out String>?
        ): MethodVisitor {
            if (Opcodes.ACC_STATIC and access == 0) {
                throw IllegalStateException("Non-static methods are not supported")
            }
            return WrappedMethodGenerator(
                super.visitMethod(access, name, descriptor, signature, exceptions),
                this.name,
                Type.getMethodType(descriptor),
                name
            )
        }
    }

    private class WrappedMethodGenerator(
        private val delegate: MethodVisitor,
        private val ownerName: String,
        private val type: Type,
        private val name: String
    ) : MethodVisitor(ASM9) {
        override fun visitCode() {
            delegate.visitCode()
            for ((i, arg) in type.argumentTypes.withIndex()) {
                delegate.visitVarInsn(arg.getOpcode(Opcodes.ILOAD), i)
            }
            delegate.visitMethodInsn(Opcodes.INVOKESTATIC, ownerName, name, type.descriptor, false)
            delegate.visitInsn(type.returnType.getOpcode(Opcodes.IRETURN))
            delegate.visitEnd()
        }

        override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor {
            return delegate.visitAnnotation(descriptor, visible)
        }
    }

    fun doStuff() {
        val writer = ClassWriter(ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)
        val visitor = WrapperGenerator(writer)
        reader.accept(visitor, 0)
        file.resolveSibling("$emittedName.class").writeBytes(writer.toByteArray())
        removeOldAnnotations()
    }

    private fun removeOldAnnotations() {
        val writer = ClassWriter(reader, 0)
        val visitor = AnnotationReplacer(writer)
        reader.accept(visitor, 0)
        file.writeBytes(writer.toByteArray())
    }
}