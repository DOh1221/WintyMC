plugins {
    id("java")
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "ru.armlix"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application{
    mainClass.set("ru.doh1221.wintymc.server.WintyMC")
}

dependencies {
    // JUnit
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Netty (последняя стабильная версия 4.2.x)
    implementation("io.netty:netty-all:4.2.7.Final")

    // FastUtil (последняя стабильная версия 9.1.0)
    implementation("it.unimi.dsi:fastutil:8.5.9")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

    // Google Guava (для ThreadFactoryBuilder и других утилит)
    implementation("com.google.guava:guava:32.1.2-jre")
}



tasks.shadowJar {
    archiveBaseName.set("WintyMC")
    archiveVersion.set("1.0")
    archiveClassifier.set("") // ← убирает "-all"

    manifest {
        attributes["Main-Class"] = "ru.doh1221.wintymc.server.WintyMC"
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    options.encoding = "UTF-8"
}