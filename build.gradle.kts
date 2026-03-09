plugins {
    id("com.google.cloud.artifactregistry.gradle-plugin") version "2.2.5"
    id("java")
    id("maven-publish")
}

val javaVersion: String by project
val asmVersion: String by project
val bombeVersion: String by project

java {
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
}

group = "org.cadixdev"
project.setProperty("archivesBaseName", project.name.toLowerCase())
version = "0.3.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("artifactregistry://us-maven.pkg.dev/mw-lunarclient-maven-repo/virtual")
    }
}

dependencies {
    implementation("org.ow2.asm:asm-commons:$asmVersion")
    implementation("org.cadixdev:bombe:$bombeVersion")
    implementation("org.cadixdev:bombe-jar:$bombeVersion")
}

tasks.processResources {
    from("LICENSE.txt")
}

tasks.register<Jar>("javadocJar") {
    dependsOn("javadoc")
    from(tasks.javadoc.get().destinationDir)
    archiveClassifier.set("javadoc")
}

tasks.register<Jar>("sourcesJar") {
    dependsOn("classes")
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

tasks.jar {
    manifest {
        attributes("Automatic-Module-Name" to "${project.group}.atlas")
    }
}

artifacts {
    archives(tasks.named("javadocJar"))
    archives(tasks.named("sourcesJar"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = project.property("archivesBaseName").toString()
            from(components["java"])

            artifact(tasks["javadocJar"])
            artifact(tasks["sourcesJar"])

            pom {
                name.set(project.name)
                description.set(project.description)
                packaging = "jar"
                url.set(project.property("url").toString())
                inceptionYear.set(project.property("inceptionYear").toString())

                scm {
                    url.set("https://github.com/CadixDev/Atlas")
                    connection.set("scm:git:https://github.com/CadixDev/Atlas.git")
                    developerConnection.set("scm:git:git@github.com:CadixDev/Atlas.git")
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/CadixDev/Atlas/issues")
                }

                licenses {
                    license {
                        name.set("Mozilla Public License 2.0")
                        url.set("https://opensource.org/licenses/MPL-2.0")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("jamierocks")
                        name.set("Jamie Mansfield")
                        email.set("jmansfield@cadixdev.org")
                        url.set("https://www.jamiemansfield.me/")
                        timezone.set("Europe/London")
                    }
                }
            }
        }
    }

    repositories {
        maven {
            name = "artifactRegistry"
            url = uri("artifactregistry://us-central1-maven.pkg.dev/mw-lunarclient-maven-repo/public")
        }
    }
}
