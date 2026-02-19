val pluginVersion = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // VEI API (compile-only — provided at runtime by the VEI plugin)
    compileOnly(projects.vanillaenoughitemsApi)

    // Paper API
    compileOnly(libs.paper.api)
}

tasks {
    jar {
        archiveBaseName.set("VanillaEnoughItems-Playground-API")
        archiveVersion.set(pluginVersion)
    }

    processResources {
        val props = mapOf(
            "version" to pluginVersion,
            "apiVersion" to libs.versions.paper.api.get().substringBefore("-R"),
        )
        inputs.properties(props)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
    
    register<Copy>("deployJarToServer") {
        group = "deployment"
        description = "Deploy the current built jar to the development server"

        from(jar.flatMap { it.archiveFile })
        into("${env.fetch("SERVER_PATH")}/plugins")

        val baseNameProvider = jar.flatMap { it.archiveBaseName }

        doFirst {
            val baseName = baseNameProvider.get()
            destinationDir.listFiles()
                ?.filter { it.name.matches(Regex("^${Regex.escape(baseName)}-[0-9][0-9.\\-]*\\.jar$")) }
                ?.forEach { file ->
                    logger.lifecycle("Cleaning old ${baseName} plugin files: ${file.name}")
                    file.delete()
                }
            logger.lifecycle("Deploy ${baseName} to ${destinationDir.absolutePath} ...")
        }
    }

    register("jarAndDeploy") {
        group = "deployment"
        description = "Build shadow jar and deploy to the development server"

        dependsOn(jar)
        finalizedBy(named("deployJarToServer"))
    }
}
