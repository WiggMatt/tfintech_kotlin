class NewsPrinter {
    private val builder = StringBuilder()

    // Функция для добавления заголовков
    fun header(level: Int, content: String) {
        builder.appendLine("#".repeat(level) + " $content")
        builder.appendLine()
    }

    // Функция для добавления текста
    fun text(block: TextBuilder.() -> Unit) {
        val textBuilder = TextBuilder().apply(block)
        builder.appendLine(textBuilder.toString())
    }

    // Функция для добавления кода
    fun code(language: String, block: CodeBuilder.() -> Unit) {
        val codeBuilder = CodeBuilder(language).apply(block)
        builder.appendLine(codeBuilder.toString())
    }

    // Функция для добавления ссылок
    fun link(text: String, url: String): String {
        return "[$text]($url)"
    }

    // Функция для вывода результатов
    fun print() {
        println(builder.toString())
    }
}

class TextBuilder {
    private val builder = StringBuilder()

    fun bold(content: String) {
        builder.append("**$content**")
    }

    fun italic(content: String) {
        builder.append("*$content*")
    }

    fun link(text: String, url: String) {
        builder.append("[${text}](${url})")
    }

    override fun toString(): String = builder.toString()
}

class CodeBuilder(private val language: String) {
    private val builder = StringBuilder()

    fun code(content: String) {
        builder.appendLine(content)
    }

    override fun toString(): String {
        return "```$language\n${builder.toString()}```"
    }
}
