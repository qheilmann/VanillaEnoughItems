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
        archiveBaseName.set("VanillaEnoughItems-Playground-Addon")
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
}
