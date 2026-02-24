plugins {
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.jspecify)
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name = "VanillaEnoughItems API"
                description = "API module for VanillaEnoughItems"
                url = "https://github.com/qheilmann/VanillaEnoughItems"
            }
        }
    }
}
