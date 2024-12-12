package io.github.seggan.kmixin

import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.ASM9
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import java.io.File

class JavaGenerator(private val pkg: String, private val file: File) {

    private val reader = ClassReader(file.readBytes())
    private val slashPkg = pkg.replace('.', '/')

    private val mixinName = "$slashPkg/${file.nameWithoutExtension}"
    private val implName = "$slashPkg-impl/${file.nameWithoutExtension}"

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
            if (descriptor == "Lkotlin/Metadata;") {
                foundKMeta = true
            } else if (descriptor == "Lorg/spongepowered/asm/mixin/Mixin;") {
                foundMixin = true
            }
            return null
        }
    }

    private inner class AnnotationReplacer(delegate: ClassVisitor) : ClassVisitor(ASM9, delegate) {
        private val replace = listOf(Metadata::class, Mixin::class, Inject::class)

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
            if (replace.any { descriptor == it.java.descriptorString() }) {
                return null
            }
            return super.visitAnnotation(descriptor, visible)
        }
    }

    private inner class WrapperGenerator(delegate: ClassVisitor) : ClassVisitor(ASM9, delegate) {

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
            super.visit(version, access, mixinName, signature, superName, interfaces)
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
                super.visitMethod(access and (Opcodes.ACC_STATIC.inv()), name, descriptor, signature, exceptions),
                Type.getMethodType(descriptor),
                name
            )
        }
    }

    private inner class WrappedMethodGenerator(
        private val delegate: MethodVisitor,
        private val type: Type,
        private val name: String
    ) : MethodVisitor(ASM9) {
        override fun visitCode() {
            delegate.visitCode()
            for ((i, arg) in type.argumentTypes.withIndex()) {
                delegate.visitVarInsn(arg.getOpcode(Opcodes.ILOAD), i + 1)
            }
            delegate.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                implName,
                name,
                type.descriptor,
                false
            )
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