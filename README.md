## mini json parser

Really small, really fast, really specific JSON parser for the JVM. Written in Java but nice for Kotlin.

### Why?

Because the JVM needs another JSON parser that keeps things small and fast. This parser decodes to `Map`, `List` and
standard Java wrapper objects, and nothing else.

There's no:

- Configuration
- Reflection
- POJO Mapping
- Annotations / Annotation Processing

It's not a substitute for tools like Moshi, Jackson, dsl-json, or other major JSON parsers.

## You want this API when

- You need to encode / decode flexible JSON models
- You need something really lightweight (3 classes - that's it)
- You don't require POJO binding (or
  have [a suitable substitute](https://kotlinlang.org/docs/delegated-properties.html#storing-properties-in-a-map))
- You want something really fast
- You want a JSON parser written by a guy named Jason