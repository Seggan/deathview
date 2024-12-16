package io.github.seggan.kmixin.gen

import org.objectweb.asm.*
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import kotlin.metadata.KmClassifier
import kotlin.metadata.isInline
import kotlin.metadata.jvm.KotlinClassMetadata
import kotlin.metadata.jvm.Metadata
import kotlin.metadata.jvm.signature

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
            throw MixinGenerationException("Records are not supported")
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
        if (!annotations.any { it.startsWith("Lorg/spongepowered") }) return null
        if (Opcodes.ACC_STATIC and access == 0) {
            throw MixinGenerationException("Non-static methods are not supported")
        }
        if (Opcodes.ACC_PRIVATE and access == 0) {
            throw MixinGenerationException("All methods in a mixin must be private")
        }

        val type = Type.getMethodType(descriptor)
        val argumentTypes = type.argumentTypes.toMutableList()
        if (!argumentTypes.any { it.descriptor == Descriptors.SPONGE_CALLBACK_INFO } && annotations.any { it == Descriptors.SPONGE_INJECT }) {
            argumentTypes.add(Type.getType(CallbackInfo::class.java))
        }

        val receiverType: String?
        var newAccess = access
        if (generator.metadata is KotlinClassMetadata.FileFacade) {
            val pkg = generator.metadata.kmPackage
            val func = pkg.functions.first { it.name == name && it.signature?.descriptor == descriptor }
            if (func.receiverParameterType != null) {
                val classifier = func.receiverParameterType!!.classifier
                if (classifier !is KmClassifier.Class) {
                    throw MixinGenerationException("Unsupported receiver type: $classifier")
                }
                receiverType = classifier.name
                newAccess = newAccess and Opcodes.ACC_STATIC.inv()
                argumentTypes.removeAt(0)
            } else {
                receiverType = null
            }

            if (func.isInline) {
                // Not my fault if your function explodes
                return super.visitMethod(
                    access,
                    name,
                    type.descriptor,
                    signature,
                    exceptions
                )
            }
        } else {
            receiverType = null
        }

        val newType = Type.getMethodType(type.returnType, *argumentTypes.toTypedArray())
        return WrappedMethodGenerator(
            super.visitMethod(
                newAccess,
                name,
                newType.descriptor,
                signature,
                exceptions
            ),
            name,
            type,
            receiverType
        )
    }

    private inner class WrappedMethodGenerator(
        private val delegate: MethodVisitor,
        private val name: String,
        private val type: Type,
        private val castType: String?
    ) : MethodVisitor(Opcodes.ASM9) {

        override fun visitCode() {
            delegate.visitCode()
            var i = 0
            if (castType != null) {
                delegate.visitVarInsn(Opcodes.ALOAD, 0)
                delegate.visitTypeInsn(Opcodes.CHECKCAST, castType)
                i++
            }
            while (i < type.argumentTypes.size) {
                delegate.visitVarInsn(type.argumentTypes[i].getOpcode(Opcodes.ILOAD), i)
                i++
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
            return delegate.visitAnnotation(descriptor, visible)
        }
    }
}