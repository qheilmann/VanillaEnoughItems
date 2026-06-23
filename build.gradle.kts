plugins {
    alias(libs.plugins.dotenv)
}

subprojects {
    apply(plugin = "java-library")

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion = JavaLanguageVersion.of(25)
    }

    tasks {
        withType<JavaCompile>().configureEach {
            options.encoding = Charsets.UTF_8.name()
            options.release = 25
            options.compilerArgs.addAll(listOf("-Xlint:-deprecation", "-Xlint:-removal"))
        }

        withType<Javadoc>().configureEach {
            options.encoding = Charsets.UTF_8.name()
        }

        withType<ProcessResources>().configureEach {
            filteringCharset = Charsets.UTF_8.name()
        }

        withType<Test>().configureEach {
            useJUnitPlatform()
        }
    }
}
