import xyz.jpenilla.resourcefactory.bukkit.Permission
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    java
    alias(libs.plugins.paperweight.userdev)
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.resource.factory.paper)
}

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper.api.get())
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

paperPluginYaml {
    name = "LightHealth"
    version = project.version.toString()
    main = "me.bedepay.lighthealth.LightHealth"
    apiVersion = "1.21"
    authors = listOf("bedepay")
    description = "Modern mob health feedback for Spigot/Paper/Folia: hologram, damage numbers, actionbar, bossbar."
    foliaSupported = true

    permissions.register("lighthealth.see") {
        description = "See mob health displays"
        default = Permission.Default.TRUE
    }
    permissions.register("lighthealth.toggle") {
        description = "Toggle personal health displays"
        default = Permission.Default.TRUE
    }
    permissions.register("lighthealth.admin") {
        description = "Admin commands (reload)"
        default = Permission.Default.OP
    }
}

tasks {
    jar {
        archiveClassifier.set("plain")
    }

    shadowJar {
        archiveClassifier.set("")
        mergeServiceFiles()
    }

    assemble {
        dependsOn(shadowJar)
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion(libs.versions.minecraft.get())
        jvmArgs("-Xms1G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
    }

    compileJava {
        options.encoding = "UTF-8"
        options.release.set(25)
        options.compilerArgs.add("-parameters")
    }

    processResources {
        filteringCharset = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }
}
