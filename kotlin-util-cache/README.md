# Kotlin Util Cache

The `kotlin-util-cache` project provides a wrapper for asynchronous caching with coroutine support, based on [Caffeine](https://github.com/ben-manes/caffeine). It enables efficient, flexible, and coroutine-compatible caching in Kotlin projects.

## Features

- **Asynchronous Caching**: Supports coroutine-based cache retrieval, avoiding blocking threads.
- **Automatic Eviction**: Configure size-based or time-based cache eviction.
- **Customizable Expiry Policies**: Define custom expiry logic using size or time-based policies.
- **Scope Support**: Uses specified coroutine scopes to manage asynchronous operations.
- **Removal Listener**: Execute custom actions upon cache entry removal.

## Installation

To use this subproject, add it as a dependency in your project:

```kotlin
implementation("io.github.goquati.kotlin-util-cache:$VERSION")
```

## Usage

### Creating a Cache

Create a cache instance using the `cacheBuilder` function:

```kotlin
val cache = cacheBuilder<String, Int> {
    capacity(100)
    expiryTimeBased(afterWrite = Duration.minutes(10))
}
```

### Basic Operations

#### Putting and Getting Data

```kotlin
runBlocking {
    cache.put("key1", 100) // Store data
    val value = cache.get("key1") { key -> key.length * 10 } // Retrieve or compute data
}
```

#### Removing Data

```kotlin
cache.invalidate("key1") // Remove a single entry
cache.invalidateAll()     // Clear the entire cache
```

### Advanced Configuration

#### Custom Removal Listener

Execute actions on item removal from the cache:

```kotlin
cacheBuilder<String, String> {
    removalListener(CoroutineScope(Dispatchers.IO)) { key, value, cause ->
        println("Removed key=$key, value=$value due to $cause")
    }
}
```

#### Eviction Policies

You can set cache capacity limits based on item count or custom weight:

```kotlin
cacheBuilder<Int, String> {
    expirySizeBased(maxSize = 500)
}
```

### Using the Cache in a Coroutine

The cache supports retrieving data asynchronously:

```kotlin
val result = cache.get(1) { key -> loadDataForKey(key) }
```

## API Overview

### Cache Methods

- **`get(key: K, block: suspend CoroutineScope.(K) -> V): V`**: Retrieves a value by key or computes it.
- **`put(key: K, value: V): V`**: Stores a value in the cache.
- **`invalidate(key: K)`**: Removes an entry by key.
- **`invalidateAll()`**: Clears all entries in the cache.
- **`asMap()`**: Returns the cache as a map of deferred values.

### Builder Methods

- **`capacity(size: Int)`**: Sets the initial capacity of the cache.
- **`expiryTimeBased(afterWrite: Duration?)`**: Defines time-based eviction.
- **`expirySizeBased(maxSize: Long)`**: Defines size-based eviction.

## License

This project is licensed under the MIT License. See the [LICENSE](../LICENSE) file for details.