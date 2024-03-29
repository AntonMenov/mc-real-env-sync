plugins {
    idea
    java
}

group = "io.github.antonmenov"
version = "0.1.0"

repositories {
    mavenCentral()
    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly("org.jetbrains:annotations:24.1.0")

    compileOnly("org.checkerframework:checker-qual:3.42.0")
    testCompileOnly("org.checkerframework:checker-qual:3.42.0")
    compileOnly("com.google.guava:guava:33.0.0-jre")
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT") {
        exclude("org.checkerframework", "checker-qual")
        exclude("com.google.guava", "guava")
    }
}

val targetJavaVersion = 17
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"

        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }

    named<ProcessResources>("processResources") {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    named<Test>("test") {
        useJUnitPlatform()
    }
}
