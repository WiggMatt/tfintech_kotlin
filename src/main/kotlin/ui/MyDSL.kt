package ui

// Интерфейс для всех билдоров HTML-элементов
interface HtmlElementBuilder {
    fun appendTo(builder: StringBuilder)
}

// Абстрактный класс для билдоров с содержимым
abstract class ContentBuilder : HtmlElementBuilder {
    protected val content = StringBuilder()

    protected fun addContent(tag: String, contentBuilder: ContentBuilder.() -> Unit) {
        content.append("<$tag>")
        this.contentBuilder()
        content.append("</$tag>")
    }

    protected fun addText(text: String) {
        content.append(text)
    }

    override fun appendTo(builder: StringBuilder) {
        builder.append(content)
    }
}

// Билдер HTML документа
class HtmlBuilder : ContentBuilder() {
    fun html(init: ContentBuilder.() -> Unit) {
        addContent("html", init)
    }

    fun head(init: HeadBuilder.() -> Unit) {
        addContent("head") {
            HeadBuilder().apply(init).appendTo(content)
        }
    }

    fun body(init: BodyBuilder.() -> Unit) {
        addContent("body") {
            BodyBuilder().apply(init).appendTo(content)
        }
    }

    fun toHtmlString(): String = content.toString()
}

// Билдер заголовка
class HeadBuilder : ContentBuilder() {
    fun title(text: String) {
        addText("<title>$text</title>")
    }
}

// Билдер тела документа
class BodyBuilder : ContentBuilder() {
    fun h1(init: TextBuilder.() -> Unit) {
        addContent("h1") {
            TextBuilder().apply(init).appendTo(content)
        }
    }

    fun h2(init: TextBuilder.() -> Unit) {
        addContent("h2") {
            TextBuilder().apply(init).appendTo(content)
        }
    }

    fun table(init: TableBuilder.() -> Unit) {
        addContent("table") {
            TableBuilder().apply(init).appendTo(content)
        }
    }

    fun div(init: DivBuilder.() -> Unit) {
        addContent("div") {
            DivBuilder().apply(init).appendTo(content)
        }
    }
}

// Билдер текста
class TextBuilder : ContentBuilder() {
    fun plus(text: String) {
        addText(text)
    }
}

// Билдер таблицы
class TableBuilder : ContentBuilder() {
    fun tr(init: TrBuilder.() -> Unit) {
        addContent("tr") {
            TrBuilder().apply(init).appendTo(content)
        }
    }
}

// Билдер строки таблицы
class TrBuilder : ContentBuilder() {
    fun th(init: TextBuilder.() -> Unit) {
        addContent("th") {
            TextBuilder().apply(init).appendTo(content)
        }
    }

    fun td(init: TdBuilder.() -> Unit) {
        addContent("td") {
            TdBuilder().apply(init).appendTo(content)
        }
    }
}

// Билдер ячейки таблицы
class TdBuilder : ContentBuilder() {
    fun plus(text: String) {
        addText(text)
    }

    fun a(href: String, init: TextBuilder.() -> Unit) {
        addContent("a href=\"$href\"") {
            TextBuilder().apply(init).appendTo(content)
        }
    }
}

// Билдер блока div
class DivBuilder : ContentBuilder() {
    fun h3(init: TextBuilder.() -> Unit) {
        addContent("h3") {
            TextBuilder().apply(init).appendTo(content)
        }
    }

    fun h4(init: TextBuilder.() -> Unit) {
        addContent("h4") {
            TextBuilder().apply(init).appendTo(content)
        }
    }

    fun p(init: PBuilder.() -> Unit) {
        addContent("p") {
            PBuilder().apply(init).appendTo(content)
        }
    }

    fun hr() {
        addText("<hr>")
    }

    fun a(href: String, init: TextBuilder.() -> Unit) {
        addContent("a href=\"$href\"") {
            TextBuilder().apply(init).appendTo(content)
        }
    }
}

// Билдер абзаца
class PBuilder : ContentBuilder() {
    fun plus(text: String) {
        addText(text)
    }

    fun unsafe(init: UnsafeBuilder.() -> Unit) {
        addContent("p") {
            UnsafeBuilder().apply(init).appendTo(content)
        }
    }
}

// Билдер небезопасного содержимого
class UnsafeBuilder : ContentBuilder() {
    fun plus(text: String) {
        addText(text)
    }
}
