buildscript {
    val kotlinVersion = "1.8.21"
    val aimyboxVersion = "0.17.6-alpha.2"
    val hiltVersion = "2.44"

    extra.set("kotlinVersion", kotlinVersion)
    extra.set("aimyboxVersion", aimyboxVersion)
    extra.set("hiltVersion", hiltVersion)

    repositories {
        mavenCentral()
        google()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
    }

}

allprojects {

    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

val Project.isSubmodule get() = name != rootProject.name
