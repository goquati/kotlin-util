# kotlin-util

![GitHub License](https://img.shields.io/github/license/klahap/kotlin-util)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/klahap/kotlin-util/check.yml)
![Static Badge](https://img.shields.io/badge/coverage-100%25-success)

## Overview

Welcome to `kotlin-util`, a utility library for Kotlin that extends the standard library with additional functions to make working with collections more powerful and expressive. This library provides various extension functions for `Iterable`, `Sequence` and `Map` to enhance their functionality, especially focusing on handling nullability and performing common grouping and association tasks.

## Features

- **Distinct Elements Check**: Easily check if elements in a collection are distinct by a specified key.
- **Non-Null Grouping**: Group elements by non-null keys and transform values with non-null results.
- **Non-Null Association**: Create maps from collections by associating elements with non-null keys and values.
- **Enhanced Map Operations**: Transform map keys and values with non-null results.
- **Grouping Enhancements**: Find the maximum and minimum elements in groups based on a selector function.

## Installation

Add the following dependency to your `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("io.github.klahap:kotlin-util:$VERSION")
}
```

## Usage

Here's a brief example demonstrating some of the provided functions:

```kotlin
val list = listOf(1, 2, 3, 4, 5, 6)

val associated = list.associateNotNull { if (it % 2 == 0) it to it * 2 else null }
println(associated)  // Output: {2=4, 4=8, 6=12}

val grouped = list.groupByNotNull { if (it % 2 == 0) it else null }
println(grouped)  // Output: {2=[2], 4=[4], 6=[6]}

val isDistinct = list.isDistinctBy { it % 2 }
println(isDistinct)  // Output: false

// ...
```

## Contributions

Contributions are welcome! Feel free to open an issue or submit a pull request. Please ensure that your code adheres to the existing coding standards and includes appropriate tests.
