package io.github.seggan.kmixin.gen

import org.objectweb.asm.*
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import kotlin.metadata.jvm.Metadata

class WrapperGenerator(delegate: ClassVisitor, private val generator: JavaGenerator) :
    ClassVisitor(Opcodes.ASM9, delegate) {

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
        super.visit(version, access, generator.mixinName, signature, superName, interfaces)
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
    ): MethodVisitor? {
        val annotations = generator.annotations[name + descriptor] ?: return null
        if (Descriptors.SPONGE_INJECT !in annotations) return null
        if (Opcodes.ACC_STATIC and access == 0) {
            throw IllegalStateException("Non-static methods are not supported")
        }
        val type = Type.getMethodType(descriptor)
        val argumentTypes = type.argumentTypes.toMutableList()
        if (!argumentTypes.any { it.descriptor == Descriptors.SPONGE_CALLBACK_INFO }) {
            argumentTypes.add(Type.getType(CallbackInfo::class.java))
        }
        val newType = Type.getMethodType(type.returnType, *argumentTypes.toTypedArray())
        return WrappedMethodGenerator(
            super.visitMethod(
                access and (Opcodes.ACC_STATIC.inv()),
                name,
                newType.descriptor,
                signature,
                exceptions
            ),
            name,
            type,
            null
        )
    }

    private inner class WrappedMethodGenerator(
        private val delegate: MethodVisitor,
        private val name: String,
        private val type: Type,
        private val castType: Type?
    ) : MethodVisitor(Opcodes.ASM9) {

        private val annotations = mutableSetOf<String>()

        override fun visitCode() {
            delegate.visitCode()
            for ((i, arg) in type.argumentTypes.withIndex()) {
                delegate.visitVarInsn(arg.getOpcode(Opcodes.ILOAD), i + 1)
            }
            Metadata()
            delegate.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                generator.implName,
                name,
                type.descriptor,
                false
            )
            delegate.visitInsn(type.returnType.getOpcode(Opcodes.IRETURN))
            delegate.visitEnd()
        }

        override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor {
            annotations.add(descriptor)
            return delegate.visitAnnotation(descriptor, visible)
        }
    }
}