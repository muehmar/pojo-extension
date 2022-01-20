# Tests

One can automatically update the templates which some tests use to test the generated code against with setting the
project property `updateTemplates` to true:

```
 ./gradlew test -PupdateTemplates=true
```

The tests will update the files directly in the src folder. The comparison is always done, therefore the tests will fail
the first time as the newly generated resource files are not yet compiled into the build folder. This helps to indicate
some files get updated.