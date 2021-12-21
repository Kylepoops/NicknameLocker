import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    kotlin("jvm") version "1.6.10"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17-R0.1-SNAPSHOT")
}

dependencies {
    @Suppress("GradlePackageUpdate") compileOnly(kotlin("stdlib"))
    // compileOnly("com.github.mcdoeswhat:AmazingBot-3.0:3.2.31")
    compileOnly("net.mamoe:mirai-core:2.8.3")
    compileOnly(fileTree("libs"))
}

tasks {
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_16.toString()
            freeCompilerArgs = listOf("-Xlambdas=indy")
        }
    }
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_17
}

bukkit {
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    main = "cn.cubegarden.nicknamelocker.Main"
    apiVersion = "1.17"
    author = "Kylepoops"
    depend = listOf("AmazingBot")
    loadBefore = depend
    libraries = listOf("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
}
