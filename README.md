# Kotlin Util Libraries

![GitHub License](https://img.shields.io/github/license/goquati/kotlin-util)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/goquati/kotlin-util/check.yml)
![Static Badge](https://img.shields.io/badge/coverage-100%25-success)

This repository hosts a collection of Kotlin utility libraries designed to simplify code development. The modules span across various functionalities, offering multiplatform support.

## Subprojects

### 1. `kotlin-util`
Enhanced Kotlin utility functions for streamlined coding.

### 2. `kotlin-util-coroutine`
Utility functions to facilitate working with Kotlin coroutines.

### 3. `kotlin-util-csv`
A type-safe CSV writing library with coroutine support. See [kotlin-util-csv/README.md](kotlin-util-csv/README.md)

### 4. `kotlin-util-cache`
Kotlin wrapper for the Caffeine caching library (JVM only).

### 5. `kotlin-util-logging`
SLF4J helper functions for Kotlin (JVM only).

## Installation

Each subproject is available as a separate dependency. Include the relevant ones in your `build.gradle.kts`:

```kotlin
implementation("io.github.goquati:kotlin-util:$VERSION")
implementation("io.github.goquati:kotlin-util-coroutine:$VERSION")
implementation("io.github.goquati:kotlin-util-csv:$VERSION")
// For JVM-only:
implementation("io.github.goquati:kotlin-util-cache:$VERSION")
implementation("io.github.goquati:kotlin-util-logging:$VERSION")
```

## Documentation

For more detailed documentation, visit the [official site](https://goquati.github.io/kotlin-util/).

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contributions

Contributions are welcome! Feel free to open an issue or submit a pull request. Please ensure that your code adheres to the existing coding standards and includes appropriate tests.
