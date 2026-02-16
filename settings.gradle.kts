plugins {
    // Allow automatic download of JDKs if missing
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "VanillaEnoughItems"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("vanillaenoughitems-api")
include("vanillaenoughitems-paper")
include("vanillaenoughitems-playground-api")
