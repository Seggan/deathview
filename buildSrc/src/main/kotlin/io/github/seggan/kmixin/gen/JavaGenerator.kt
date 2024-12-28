package io.github.seggan.kmixin.gen

import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree
import org.jetbrains.kotlin.spec.grammar.tools.parseKotlinCode

// https://github.com/Kotlin/kotlin-spec/blob/release/grammar/src/main/antlr/KotlinParser.g4
class JavaGenerator private constructor(private val pkg: String, name: String, code: String) {

    private val javaCode = StringBuilder()

    private val ast = parseKotlinCode(code)
    private val implName = extractJvmName() ?: (name + "Kt")

    private fun generate() {
        javaCode += "package $pkg.java;\n\n"
        println(ast)
    }

    private fun extractJvmName(): String? {
        val annotations = ast.findChildren(AstNodeName.FILE_ANNOTATION)
            .mapChildren(AstNodeName.UNESCAPED_ANNOTATION)
            .mapChildren(AstNodeName.CONSTRUCTOR_INVOCATION)

        for (annotation in annotations) {
            if (annotation.findChildOrNull(AstNodeName.USER_TYPE)?.allText == "JvmName") {
                return annotation.findChildrenRecursive(AstNodeName.STRING_LITERAL).first().allText.trim { it == '"' }
            }
        }

        return null
    }

    companion object {
        fun generate(pkg: String, name: String, source: String): String {
            val generator = JavaGenerator(pkg, name, source)
            generator.generate()
            return generator.javaCode.toString()
        }
    }
}

private operator fun StringBuilder.plusAssign(value: String) {
    append(value)
}

private fun KotlinParseTree.findChildOrNull(name: String): KotlinParseTree? {
    return children.firstOrNull { it.name == name }
}

private fun KotlinParseTree.findChildren(name: String): List<KotlinParseTree> {
    return children.filter { it.name == name }
}

private fun KotlinParseTree.findChildrenRecursive(name: String): List<KotlinParseTree> {
    if (this.name == name) return listOf(this)
    return children.flatMap { it.findChildrenRecursive(name) }
}

private fun List<KotlinParseTree>.mapChildren(name: String): List<KotlinParseTree> {
    return flatMap { it.findChildren(name) }
}

private val KotlinParseTree.allText: String
    get() = text.orEmpty() + children.joinToString("") { it.allText }