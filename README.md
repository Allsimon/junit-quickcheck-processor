# junit-quickcheck-processor

junit-quickcheck-processor is an annotation processor based way to generate boilerplate Generators for junit-quickcheck.

## Basic example
Write a Pojo
```java
public class SimplePojo {

  String foo;
  int bar;
}
```

Add a `@Generator(SimplePojo.class)` on your unit test class.
```java
@Generator(SimplePojo.class)
@RunWith(JUnitQuickcheck.class)
public class GeneratorTest {

  @Property
  public void sampleTest(SimplePojo pojo) {
    assertNotNull(pojo);
  }
}
```

A single `@Generator` by pojo is needed. If you have multiple test classes, it's probably best to create a single empty interface annotated with all your pojo to keep track of all your generators. 

```java
@Generator(SimplePojo.class)
@Generator(SecondPojo.class)
public interface Generators {
}
```

### JSR-380 support
If the pojo is annotated with `javax.validation` annotations, then a second generator will be built. This generator provides only valid pojo.

```java
public class ValidatedPojo {
  @Size(max=45)
  String foo;
  @Positive
  int bar;
}
```

You can get a valid Pojo in your properties tests using the `@Valid` annotations

```java
public class GeneratorTest {

  @Property
  public void sampleTest(@Valid ValidatedPojo pojo) {
    assertThat(pojo.foo.length()).isLessThan(45);
    assertThat(pojo.bar).isGreaterThan(0);
  }
}
```
