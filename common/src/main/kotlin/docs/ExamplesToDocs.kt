package de.peekandpoke.ultra.common.docs

import de.peekandpoke.ultra.common.ensureDirectory
import de.peekandpoke.ultra.common.toUri
import java.io.File

fun examplesToDocs(
    title: String,
    chapters: List<ExampleChapter>,
    sourceLocation: File = File("src/examples"),
    outputLocation: File = File("docs/ultra::docs")
) {
    ExamplesToDocs(
        title = title,
        chapters = chapters,
        sourceLocation = sourceLocation,
        outputLocation = outputLocation
    ).run()
}

class ExamplesToDocs internal constructor(
    private val title: String,
    private val chapters: List<ExampleChapter>,
    private val sourceLocation: File,
    private val outputLocation: File
) {

    private val builder = StringBuilder()

    init {
        outputLocation.deleteRecursively()
        outputLocation.ensureDirectory()
    }

    fun run() {

        builder.appendln("# $title").appendln()

        generateToc()

        generateExamples()

        File(outputLocation, "index.md").apply {
            writeText(builder.toString())
        }
    }

    private fun generateToc() {

        builder.appendln("## TOC")

        chapters.forEachIndexed { chapterIndex, chapter ->

            builder.appendln("${chapterIndex + 1}. ${chapter.name}").appendln()

            chapter.examples.forEachIndexed { exampleIndex, example ->

                val title = example.title
                val dashed = title.toLowerCase().toUri().replace(" ", "-")

                builder.appendln("    ${exampleIndex + 1}. [${title}](#${dashed})")
            }
        }
    }

    private fun generateExamples() {

        chapters.forEach { chapter ->

            builder.appendln("## ${chapter.name}").appendln()

            val srcDir = File(sourceLocation, chapter.packageLocation)

            chapter.examples.forEach { example ->

                val exampleCode = ExampleCodeExtractor.extract(example, srcDir)

                builder
                    .appendln("### ${example.title}")
                    .appendln("```kotlin")
                    .appendln(exampleCode ?: "no code available")
                    .appendln("```")

                val output = example.runAndRecordOutput()

                if (output.isNotEmpty()) {
                    builder
                        .appendln("Will output:")
                        .appendln("```")
                        .appendln(output.trim())
                        .appendln("```")
                        .appendln()
                }
            }
        }
    }

}
