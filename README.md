[![Build Status](https://github.com/muehmar/pojo-extension/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/muehmar/pojo-extenstion/actions/workflows/gradle.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/muehmar/pojo-extenstion/blob/master/LICENSE)

# Pojo Extension

Generates advanced boilerplate code for your immutable data classes. This annotation processor extends your data classes
with simple, well known boilerplate code but also with more advanced features like the Safe Builder or allows to
distinguish between required and optional fields in the class. The generated code follows the convention not
using `null` to improve the compiler support.

This annotation processor does intentionally not modify the AST of the pojo like Lombok. It uses the standard Java
features of annotation processors which are designed to create new classes but not really modify existing ones. The
features are added to the data classes by creating an extension class which the data class can inherit from. If your
data classes cannot inherit from the extension class (or you dont want to use it this way), you can make use of some
features by delegate the method calls to the extension class for which it contains static methods (i.e. Safe
Builder, `equals` and `hashCode`).

The annotation processor can distinguish between optional and required fields in a data class, i.e. which fields must be
present all times and which may be absent.

Currently, the extension class contains the following features:

* SafeBuilder - Special builder class which enforces setting all required attributes
* `equals` and `hashCode`
* `withXX` method for each field
* More to follow...

## Usage

Add the dependency and register it as annotation processor. In gradle this would look like the following:

```
dependencies {
    implementation "io.github.muehmar:pojo-extension:0.2.4"
    annotationProcessor "io.github.muehmar:pojo-extension:0.2.4"
}
```

Annotate a simple data class:

```
@PojoExtension
public class Customer extends CustomerExtension {
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

This will create the class `CustomerExtension` which provides the utilities for the `Customer` class. In case of the
SafeBuilder one could now use the static method `Customer.newBuilder()` which is provided by the extension class.

If one the data class does not extend the extension class, one could delegate the method call to the extension class:

```
@PojoExtension
public class Customer {
private final String name;
private final String email;
private final Optional<String> nickname;

    public Customer(String name, String email, String nickname) {
        this.name = name;
        this.email = email;
        this.nickname = Optional.ofNullable(nickname);
    }
    
    // Delegate the creation of the SafeBuilder
    public static Builder0 newBuilder() {
        return CustomerExtension.newBuilder();
    }
    
    // Remaing part omitted...
}

```

## Features

### SafeBuilder

The 'Safe Builder' is an extended builder pattern which enforces one to create valid instances, i.e. every required
property in a pojo will be set. The pattern follows also the convention not using `null` in the code which improves
support by the compiler.

The pattern is realized by creating a single builder class for each required property, with a single method setting the
corresponding property and returning the next builder for the next property. The `build`
method will only be present after each required property is set.

For example, given the following class:

```
@PojoExtension
public class Customer extends CustomerExtension {
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
  Customer.newBuilder()
    .setName("Dexter")
    .setEmail("dexter@miami-metro.us")
    .andAllOptionals()
    .setNickname("Dex")
    .build();
```

This does not seem to be very different from the normal builder pattern at a first glance but calling `newBuilder()`
will return a class which has only a single method `setName()`, i.e. the compiler enforces one to set the name. The
returned class after setting the name has again one single method `setEmail()`. As the property `email` is the last
required property in this example the returned class for `setEmail()` offers three methods:

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

When using `andAllOptionals()` or `andOptinoals()` after all required properties are set, the builder provides
overloaded methods to add the optional properties. The property can be set directly or wrapped in an `Optional`. In the
example above, the builder provides methods with the following signature:

```
  public Builder setNickname(String nickname);
  
  public Builder setNickname(Optional<String> nickname);
```

### Methods `equals` and `hashCode`

The extension contains both methods which are automatically used in case the data class extends the extension.

The extension also contains static methods which can be used in case the data class does not extend the extension. The
static methods have the following signatures, the data class should pass itself to the methods in case of delegation:

```
public static boolean equals(Customer o1, Object obj);

public static int hashCode(Customer o);
```

### Method `withXX`

The extension contains with method for each field. The methods return a new instance of the data class where the
corresponding property is set to the provided value.

The method for the optional fields is overloaded, i.e. it accepts also the value wrapped into an Optional. This makes it
possible to remove a value without the need to pass `null` or can be used for cases the value is already wrapped into an
Optional.

Utilizing the customer example above, the extension would create the following with methods:

```
  public Customer withName(String name);

  public Customer withEmail(String email);

  public Customer withNickname(String nickname);

  public Customer withNickname(Optional<String> nickname);
```

## Pojo Requirements

The extension class has no access to the fields of the data class, therefore the data class must provide a constructor
with all fields as arguments and getters for each field. Reflection is not used intentionally. The extension class is
created in the same package as the class, therefore the constructor as well as the getters can be package private if
needd.

### Constructor

The constructor must accept all fields as arguments and have to be currently in the same order of declaration. The types
of the required fields must match exactly. The types of the optional fields can either be the actual type which may be
nullable in case of absence or wrapped into a `java.util.Optional`. The annotation processor is smart enough to detect
which case is used, there can also be a mix for the optional fields in case you really need it.

```
@PojoExtension
public class Customer extends CustomerExtension {
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

### Getter

For each field a getter must be present and its name must be according to the java bean naming conventions. The type of
the required fields must match exactly where the types of the optional fields can be (like for the constructor) either
the actual type and nullable or wrapped into an `Optional`.

## Annotation Parameters

### Optional detection

There are multiple ways to tell the processor which attributes of a pojo are required and which are not. The annotation
has the parameter `optionalDetection` which is an array of `OptionalDetection` and allows customisation of each pojo if
necessary:

| OptionalDetection | Description |
| --- | --- |
| OptionalDetection.OPTIONAL_CLASS | In this case every field in the pojo which is wrapped in an Optional is considered as optional. |
| OptionalDetection.NULLABLE_ANNOTATION | With this option a field in the pojo can be annotated with the `Nullable` annotation to mark it as optional. The `Nullable` annotation is delivered within this package, `javax.annotations.Nullable` (JSR305) is not yet supported.

Both options are active as default.

## Known Issues

* Wildcard types are not yet supported
* Generic data classes are not yet supported

## Change Log

* 0.2.5 - Remove newline character from writer output
* 0.2.4 - Fix generated package structure for the extension
* 0.2.3 - Make the extension be extendable by the pojo itself
* 0.2.2 - Support constructors with optional fields wrapped into `java.util.Optional`
* 0.2.1 - Add support for primitives and arrays
* 0.2.0 - Add SafeBuilder to the extension class
* 0.1.0 - Initial release, creates empty extension class
