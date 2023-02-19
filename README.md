[![Build Status](https://github.com/muehmar/pojo-extension/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/muehmar/pojo-extension/blob/master/.github/workflows/gradle.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/muehmar/pojo-extenstion/blob/master/LICENSE)

# Pojo Extension

IMPORTANT NOTE: This project is no longer under active development. The extension class is not considered as helpful
enough by the author. The generated builder is developed and released by the fork [Pojo-Builder](https://github.com/muehmar/pojo-builder).

Generates advanced boilerplate code for your immutable data classes or Java 16 records.

This annotation processor extends your data classes or records with simple, well known boilerplate code but also with
more advanced features like the Safe Builder or allows to distinguish between required and optional fields in the class.
The generated code follows the convention not using `null` to improve compiler support.

This annotation processor does intentionally not modify the AST. The processor creates new classes or interfaces to add
the features to your data classes or records.

The annotation processor can distinguish between optional and required fields in a data class or a record, i.e. which
fields must be present all times and which may be absent.

Currently, the following features are supported:

* SafeBuilder - Special builder class which enforces (at compile time) setting all required attributes.
    * User defined methods for fields in SafeBuilder
* `withXX()` method for each field
* `mapXX()` methods for fluent 'updates'
* Convenience getters for optional fields

This processor does not generate `equals/hashCode` as well as the `toString` method. Records since Java 16 provide this
out of the box, if an earlier version is used, there are a lot of tools which do this already (for example lombok
with `@Value`).

## Usage

### Dependency

Add the `pojo-extension-annotations` module as compile-time dependency and register the `pojo-extension` module as
annotation processor.In gradle this would look like the following:

```
dependencies {
    compileOnly "io.github.muehmar:pojo-extension-annotations:0.15.1"
    annotationProcessor "io.github.muehmar:pojo-extension:0.15.1"
}
```

### Data class

Annotate a simple data class:

```
@PojoExtension
public class Customer implements CustomerExtension {
private final String name;
private final String email;
private final Optional<String> nickname;

    public Customer(String name, String email, String nickname) {
        this.name = name;
        this.email = email;
        this.nickname = Optional.ofNullable(nickname);
    }
    
    // Remaing part omitted...
}
```

The processor will now create the interface `CustomerExtension` and the class `CustomerBuilder`:

* `CustomerExtension`: This interface contains the generated methods as default methods.
* `CustomerBuilder`: This class contains the SafeBuilder.

### Records

Since Java 16 one can use records with a dedicated annotation `RecordExtension`:

```
@RecordExtension
public records Customer implements CustomerExtension(
    String name, 
    String email, 
    Optional<String> nickname
  ) implements CustomerExtension {

}
```

## Features

### SafeBuilder

The 'Safe Builder' is an extended builder pattern which enforces one to create valid instances, i.e. every required
property in a pojo will be set. The pattern follows also the convention not using `null` in the code which improves
support by the compiler.

The builder can be part of the extension class or a discrete java file,
see [Annotation Parameters](#annotation-parameters). There exists a predefined annotation `@SafeBuilder` which creates a
builder in a discrete class which can be used in case the other features are not used.

The pattern is implemented by creating a single builder class for each required property, with a single method setting
the corresponding property and returning the next builder for the next property. The `build`
method will only be present after each required property is set.

For example, given the following class:

```
@PojoExtension
public class Customer implements CustomerExtension {
private final String name;
private final String email;
private final Optional<String> nickname;

    public Customer(String name, String email, Optional<String> nickname) {
        this.name = name;
        this.email = email;
        this.nickname = nickname;
    }
    
    // Remaing part omitted...
}

```

will lead to a builder which can be used like the following:

```
  CustomerBuilder.create()
    .name("Dexter")
    .email("dexter@miami-metro.us")
    .andAllOptionals()
    .nickname("Dex")
    .build();
```

This does not seem to be very different from the normal builder pattern at a first glance but calling `create()`
will return a class which has only a single method `name()`, i.e. the compiler enforces one to set the name. The
returned class after setting the name has again one single method `email()`. As the property `email` is the last
required property in this example the returned class for `email()` offers three methods:

* `build()` As all required properties are set at that time, building the instance is allowed here.
* `andOptionals()` Returns the normal builder allowing one to set certain optional properties before creating the
  instance. This method returns just the normal builder populated with all required properties.
* `andAllOptionals()` Enforces one to set all optional properties in the same way as it is done for the required
  properties. The `build()` method will only be available after all optional properties have been set. This method is
  used in the example above, i.e. the compiler enforces one to set the `nickname` property too. This is especially
  useful in case of mapping from another data structure.

Setting all required properties in a class could theoretically also be achieved with a constructor with all required
properties as arguments, but the pattern used here is safer in terms of refactoring, i.e. adding or removing properties,
changing the required properties or changing the order of the properties.

When using `andAllOptionals()` or `andOptionals()` after all required properties are set, the builder provides
overloaded methods to add the optional properties. The property can be set directly or wrapped in an `Optional`. In the
example above, the builder provides methods with the following signature:

```
  public Builder nickname(String nickname);
  
  public Builder nickname(Optional<String> nickname);
```

#### Prefix for the setter method

You could configure a prefix with `builderSetMethodPrefix` for the setter methods like `set` which is used for the
generation
(see [Annotation Parameters](#annotation-parameters)):

```
  Customer.newBuilder()
    .setName("Dexter")
    .setEmail("dexter@miami-metro.us")
    .andAllOptionals()
    .setNickname("Dex")
    .build();
```

The first character of the field name is automatically converted to uppercase if a prefix is used.

### Custom methods for fields in SafeBuilder

It is possible define custom methods for the SafeBuilder for a specific field which can be used to populate the
corresponding field when using the builder. One could define one or more methods which return an instance of the
corresponding field, where the methods must be static and at least package private:

```
@FieldBuilder(fieldName = "name")
static String fromObject(Object o) {
  return o.toString();
}
```

The builder now contains also a method `fromObject` for populating the `name` field which accepts an `Object`.

If more than one custom method is defined for a field, all methods will be present when the corresponding field must be
populated. One could also annotate a static class with `@FieldBuilder` to group all custom methods for one field:

```
@FieldBuilder(fieldName = "name")
static class FieldBuilder {
  private FieldBuilder(){}
  
  static String fromObject(Object o) {
    return o.toString();
  } 
  
  static String unknown() {
    return "unknown";
  }
}
```

This enables one to create convenience methods to reduce code and/or make the call of the builder more readable.

#### Disable default methods in builder

One could disable the default methods which are generated by the processor in the builders when some custom methods are
defined. To achieve this, set `disableDefaultMethods` to true:

```
@FieldBuilder(fieldName = "name", disableDefaultMethods = true)
static class FieldBuilder {
  private FieldBuilder(){}
  
  static String fromObject(Object o) {
    return o.toString();
  } 
  
  static String unknown() {
    return "unknown";
  }
}
```

The method `setName` will now not be available in the builder for the name property, only the custom
methods `fromObject` and `unknown`. Note that this flag must be set consistently in all `@FieldBuilder` annotations for
the same field, therefore it is recommended to use a static class if you have more than one custom method for the same
field.

#### Generic

To create a custom method for a generic field of the class, the method needs to be declared with the same type parameter
name as the type parameter. For example if your class has a type parameter `T` and a field is a `List<T>` you have to
declare the custom method like following:

```
@FieldBuilder(fieldName = "items")
static <T> List<T> singleItem(T item) {
  return Collections.singletonList(item);
}
```

Other generic methods are not yet supported (it will lead currently to a compile error when used).

### Custom build method

A custom build method can be defined, which may return another type than the actual type of the class. A custom build
method maps the actual build instance to another type and is called by the builder automatically. The following example
returns a String instead of the `Customer` instance:

```
@SafeBuilder
public class Customer {
    ...
    
    @BuildMethod
    String customBuildMethod(Customer customer) {
        return customer.toString();
    }
}
```

```
final String customerString = CustomerBuilder.create()
                                .foo(1)
                                .bar("Test")
                                .build();
```

A custom build method must be statis and can be package-private.

### Method `withXX`

The extension contains with methods for each field. The methods return a new instance of the data class where the
corresponding property is set to the provided value.

The method for the optional fields is overloaded, i.e. it accepts also the value wrapped into an Optional. This makes it
possible to remove a value without the need to pass `null` or can be used for cases the value is already wrapped into an
Optional.

Utilizing the customer example above, the extension would create the following with methods:

```
  default Customer withName(String name);

  default Customer withEmail(String email);

  default Customer withNickname(String nickname);

  default Customer withNickname(Optional<String> nickname);
```

### Convenience getters for optional fields

Getters for optional fields may return the value wrapped into an `Optional.` If one wants to use a default value in case
the field is not present, one can write the following:

```
  customer.getNickname().orElse("NoNickname");
```

The generated optional getter provides this functionality directly where you could provide the default value. The method
name is equal to the existing getter suffixed with `Or`:

```
default String getNicknameOr(String nickname);
```

### Method `mapXX`

The `map` methods allows one to 'update' the immutable data class in a fluent style without the need to create a bunch
of local variables, especially when the update should happen conditionally. There exists the following methods:

```
  default <T> T map(Function<Customer, T> f);

  default Customer mapIf(boolean shouldMap, UnaryOperator<Customer> f);

  default <T> Customer mapIfPresent(Optional<T> value, BiFunction<Customer, T, Customer> f);
```

Consider the case with a mutable data class, where a property should be updated based on some condition:

```
final Customer customer = getCustomer();

if(isSomeCondition()) {
  customer.setNickname("nickname");
}
```

With an immutable data class we have to write something like the following to get the same behaviour:

```
final Customer customer = getCustomer();

final Customer updatedCustomer = isSomeCondition() 
  ? customer.withNickname("nickname")
  : customer; 
```

This introduces a new local variable and we have to repeat the variable 'customer' twice in the ternary statement. This
is error-prone, especially if there is more than one conditional update and more than two different versions of the
customer. With the `mapIf` method one could write the following:

```
final Customer customer = getCustomer()
  .mapIf(isSomeCondition(), c -> c.withNickname("nickname));
```

One could chain all the map operations if needed:

```
final Customer customer = getCustomer()
  .map(c -> doSomething(c, true))
  .mapIf(isSomeCondition(), c -> c.withNickname("nickname))
  .mapIfPresent(getOptionalStringValue(), Customer::withNickname);
```

The `map` method allows one also to return an arbitrary value if needed but is especially useful in cases where one
needs to pass the data class to another method which returns a new different instance of the data class without the need
to declare a new local variable like in the example above.

## Pojo Requirements

A data class must provide a constructor with all fields as arguments and getters for each field. The new builder and the
interface are created in the same package as the class, therefore the constructor as well as the getters can be package
private if needed.

A record (Java 16) provides already a constructor as well as getters and therefore satisfies all requirements
automatically. If Java below version 16 is used, one of the easiest way is to use lombok with the `@Value` annotation
until you could upgrade Java 16 or higher.

### Constructor

The constructor must accept all fields as arguments and have to be currently in the same order of declaration. The types
of the required fields must match exactly. The types of the optional fields can either be the actual type which may be
nullable in case of absence or wrapped into a `java.util.Optional`. The annotation processor is smart enough to detect
which case is used, there can also be a mix for the optional fields in case you really need it.

```
@PojoExtension
public class Customer extends CustomerBase {
private final String name;
private final String email;
private final Optional<String> nickname;

    // Package private constructor, nickname is String instead of Optional and wrapped with Optional.ofNullable()
    Customer(String name, String email, String nickname) {
        this.name = name;
        this.email = email;
        this.nickname = Optional.ofNullable(nickname);
    }
    
    // Remaing part omitted...
}
```

In case there is a field which is instantiated in the constructor and no argument is present, you can use the `@Ignore`
annotation for this field:

```
@SafeBuilder
public class IgnoreFieldClass {
  private final String id;
  private final String name;
  @Ignore private final String deviated;

  public IgnoreFieldClass(String id, String name) {
    this.id = id;
    this.name = name;
    this.deviated = String.format("%s-%s", id, name);
  }
}
```

### Getter

For each field a getter must be present. A getter is detected by the processor for the following cases:

* The getter name is according to the java bean naming convention
* The getter name is equally to the field name
* The getter is annotated with `@Getter("fieldName")` where `fieldName` is the name of the corresponding field

The return type for required fields must match exactly where a return type for optional fields can be (like for the
constructor) either the actual type and nullable or wrapped into an `Optional`.

## Annotations

The following annotations exists, where only one single annotation should be used.

* `@PojoExtension` Creates the extension interface, abstract base class and the builder class. Is parameterized and can
  be used as meta annotation to create your custom annotations.
* `@SafeBuilder` Creates the builder class
* `@RecordExtension` Can be used for records, where no `equals`, `hashCode` and `toString` methods are generated, as
  well as the abstract base class.
* `@FieldBuilder` Used to mark custom methods used in the SafeBuilder. `fieldName` is required and defines the field for
  which the custom method should be used.
* `@Ignore` Used to mark a field which should get ignored by the processor. Used particularly for fields which are
  instantiated withing the constructor and not present as argument in the constructor.

### Annotation Parameters

The `@PojoExtension` annotation contains the following parameters. The other annotations may contain a subset thereof
with different default values.

| Parameter                 | Default value                         | Description                                                                                                                                                    |
|---------------------------|---------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `optionalDetection`       | [OPTIONAL_CLASS, NULLABLE_ANNOTATION] | Defines how optional fields in data class are detected by the processor. See the next section for details.                                                     |
| `extensionName`           | "{CLASSNAME}Extension"                | Allows to override the default name of the created extension. `{CLASSNAME}` gets replaced by the name of the data class.                                       |
| `enableSafeBuilder`       | true                                  | Allows to disable the generation of the safe builder                                                                                                           |
| `builderName`             | "{CLASSNAME}Builder"                  | Allows to override the default name of the discrete builder. `{CLASSNAME}` gets replaced by the name of the data class. Ignored if `discreteBuilder` is false. |
| `builderSetMethodPrefix`  | ""                                    | Prefix which is used for the setter methods of the builder.                                                                                                    |
| `enableEqualsAndHashCode` | true                                  | Allows to disable the generation of the equals and hashCode method                                                                                             |
| `enableToString`          | true                                  | Allows to disable the generation of the toString method                                                                                                        |
| `enableWithers`           | true                                  | Allows to disable the generation of the with methods                                                                                                           |
| `enableOptionalGetters`   | true                                  | Allows to disable the generation of the optional getters                                                                                                       |                                                                                                      |
| `enableMappers`           | true                                  | Allows to disable the generation the map methods                                                                                                               |
| `enableBaseClass`         | true                                  | Enables the generation of the abstract base class                                                                                                              |
| `baseClassName`           | "{CLASSNAME}Base"                     | Allows to override the default name of the created base class. `{CLASSNAME}` gets replaced by the name of the data class.                                      |

#### Parameter `optionalDetection`

There are multiple ways to tell the processor which attributes of a pojo are required and which are not. The annotation
has the parameter `optionalDetection` which is an array of `OptionalDetection` and allows customisation of each pojo if
necessary:

| OptionalDetection                     | Description                                                                                                                                                                                                                          |
|---------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| OptionalDetection.OPTIONAL_CLASS      | In this case every field in the pojo which is wrapped in an Optional is considered as optional.                                                                                                                                      |
| OptionalDetection.NULLABLE_ANNOTATION | With this option a field in the pojo can be annotated with the `Nullable` annotation to mark it as optional. The `Nullable` annotation is delivered within this package, `javax.annotations.Nullable` (JSR305) is not yet supported. |
| OptionalDetection.NONE                | All fields are treated as required. This setting gets ignored in case it is used in combination with one of the others.                                                                                                              |

Both options are active as default.

## Custom Annotation / Meta Annotation

The `@PojoExtension` annotation can be used as meta annotation to create your own annotation with predefined behaviour.

For example if you want to treat every field in an annotated class as required, you could create your own
annotation `@AllRequiredExtension` which is annotated with `@PojoExtension` with disabled optional detection.

```
@PojoExtension(optionalDetection = OptionalDetection.NONE)
public @interface AllRequiredExtension {}
```

If one wants to create a custom annotation, with default values but allow overriding for certain classes, simply create
the corresponding method in the annotation with the same name providing the default value, i.e.:

```
@PojoExtension
public @interface AllRequiredExtension {

  OptionalDetection[] optionalDetection() default {OptionalDetection.NONE};
  
}
```

## Known Issues

* Fields in superclasses of data classes are currently ignored.

## Change Log

* 0.15.1 - Fix import for nested classes (issue `#15`)
* 0.15.0
    * Add support for wildcards (issue `#13`)
* 0.14.0
    * Add custom build method (issue `#6`)
    * Remove obsolete base class settings (issue `#8`)
    * Add option for package-private builder class (issue `#2`)
* 0.13.0 - Add `@Ignore` annotation
* 0.12.0
    * Drop support for `equals/hashCode` and `toString` method
    * Allow to disable the default methods in SafeBuilder when defining custom methods
    * Support varargs in custom SafeBuilder methods
* 0.11.0 - Add `@FieldBuilder` annotation to create custom methods for the SafeBuilder
* 0.10.1 - Make the base class and the extension interface package private
* 0.10.0
    * Add configurable prefix for the builder set methods
    * Generate convenience getters for optional fields
* 0.9.0
    * Support Java 16 records
    * Use an interface instead of an abstract class
* 0.8.0
    * Support generic data classes
    * Support newer Java versions
* 0.7.2 - Fix type conversion for annotated getter method for optional fields
* 0.7.1 - Fix possible stackoverflow caused by circular annotation paths
* 0.7.0
    * SafeBuilder can be created as discrete class
    * Improve meta annotation processing
    * Classname of the data class can be used to create custom builder or extension class names
* 0.6.0
    * Fix `toString` method
    * Add possibility to disable specific features
    * Add separate module for annotations to be used as compile time dependency
    * Make static methods private if data class inherits extension class
* 0.5.0
    * Add `mapXX` methods
    * Fix import for generic fields
* 0.4.0
    * Add `toString` method
    * Support inner classes
    * Support custom extension name
    * Support meta annotation
    * Support arbitrary getter names with `@Getter` annotation
* 0.3.1 - Ignore constants in data classes
* 0.3.0 - Add `equals`, `hashCode` and `withXX` methods
* 0.2.5 - Remove newline character from writer output
* 0.2.4 - Fix generated package structure for the extension
* 0.2.3 - Make the extension be extendable by the pojo itself
* 0.2.2 - Support constructors with optional fields wrapped into `java.util.Optional`
* 0.2.1 - Add support for primitives and arrays
* 0.2.0 - Add SafeBuilder to the extension class
* 0.1.0 - Initial release, creates empty extension class
