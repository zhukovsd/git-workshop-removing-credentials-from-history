plugins {
    id("java")
    id("war")
}

group = "ru.prplhd"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework:spring-test")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.glassfish.expressly:expressly:5.0.0")
    testImplementation("org.wiremock:wiremock:3.13.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")

    implementation(platform("org.springframework:spring-framework-bom:7.0.7"))
    implementation(platform("org.springframework.data:spring-data-bom:2025.1.5"))

    implementation("org.springframework:spring-webmvc")
    implementation("org.springframework:spring-orm")
    implementation("org.springframework.data:spring-data-jpa")
    implementation("org.springframework.security:spring-security-crypto:7.1.0-M3")

    implementation("org.thymeleaf:thymeleaf-spring6:3.1.5.RELEASE")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")
    compileOnly("jakarta.annotation:jakarta.annotation-api:3.0.0")
    implementation("org.projectlombok:lombok:1.18.46")
    annotationProcessor ("org.projectlombok:lombok:1.18.38")

    implementation("com.zaxxer:HikariCP:7.0.2")
    runtimeOnly("org.postgresql:postgresql:42.7.11")
    implementation("org.liquibase:liquibase-core:5.0.2")
    implementation("org.hibernate.orm:hibernate-core:7.3.2.Final")
    implementation("org.hibernate.validator:hibernate-validator:8.0.3.Final")

    implementation("tools.jackson.core:jackson-databind:3.1.3")
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    implementation("com.github.ben-manes.caffeine:caffeine:3.2.4")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Test>("authTest") {
    group = "verification"
    description = "Runs tests tagged \"Auth\""

    useJUnitPlatform {
        includeTags("auth")
    }

    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
}

tasks.register<Test>("httpServerTest") {
    group = "verification"
    description = "Runs tests tagged \"Http Server\""

    useJUnitPlatform {
        includeTags("httpServer")
    }

    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
}

tasks.register<Test>("locationWeatherTest") {
    group = "verification"
    description = "Runs tests tagged \"Location Weather\""

    useJUnitPlatform {
        includeTags("locationWeather")
    }

    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
}