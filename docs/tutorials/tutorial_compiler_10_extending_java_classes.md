# Tutorial Compiler 10: Extending Java Classes

In this tutorial, we are going to learn the following.

* Extend a Java standard library class with a fully qualified name.
* Use inherited methods from the Java parent class.
* Use type aliases as a shorthand for fully qualified Java type names.

## Preparation

* Follow the [instructions](../../) to add swc4j to our project.

## Extend ArrayList

A compiled class can extend existing Java classes by using the fully qualified class name. Let's extend `java.util.ArrayList` and add a custom method.

* Create a simple Java application with the code as follows.

```java
// Create a compiler targeting JDK 17.
ByteCodeCompiler compiler = ByteCodeCompiler.of(
        ByteCodeCompilerOptions.builder()
                .jdkVersion(JdkVersion.JDK_17)
                .build());
// Prepare a class that extends ArrayList.
String code = """
        namespace com {
          export class MyList extends java.util.ArrayList<Object> {
            label: String = "my-list"
            getLabel(): String {
              return this.label
            }
          }
        }""";
// Compile.
ByteCodeRunner runner = compiler.compile(code);
ByteCodeClassRunner classRunner = runner.createInstanceRunner("com.MyList");
// Use the custom method.
String label = classRunner.invoke("getLabel");
// Cast to ArrayList and use inherited methods.
@SuppressWarnings("unchecked")
java.util.ArrayList<Object> list = (java.util.ArrayList<Object>) classRunner.getInstance();
list.add("Hello");
list.add("World");
// Print the results.
System.out.println("/*********************************************");
System.out.println("     Extend ArrayList.");
System.out.println("*********************************************/");
System.out.println("label = " + label);
System.out.println("size  = " + list.size());
System.out.println("items = " + list);
```

* The output is as follows. The compiled class extends `ArrayList`, so inherited methods like `add()` and `size()` work as expected.

```java
/*********************************************
     Extend ArrayList.
*********************************************/
label = my-list
size  = 2
items = [Hello, World]
```

## Type Aliases

Instead of writing fully qualified names in TypeScript, you can register type aliases in the compiler options. As shown in [Tutorial 08](tutorial_compiler_08_functions_and_recursion.md), calling `typeAliasMap()` **replaces** the defaults, so include all types the code needs.

* Append the following code to that Java application.

```java
// Create a compiler with type aliases.
compiler = ByteCodeCompiler.of(
        ByteCodeCompilerOptions.builder()
                .jdkVersion(JdkVersion.JDK_17)
                .typeAliasMap(Map.of(
                        "int", "int",
                        "void", "void",
                        "String", "java.lang.String",
                        "Object", "java.lang.Object",
                        "ArrayList", "java.util.ArrayList"))
                .build());
// Now the TypeScript code can use the short name ArrayList.
code = """
        namespace com {
          export class NameList extends ArrayList<Object> {
            addName(name: String): void {
              this.add(name)
            }
            getCount(): int {
              return this.size()
            }
          }
        }""";
// Compile.
runner = compiler.compile(code);
classRunner = runner.createInstanceRunner("com.NameList");
classRunner.invoke("addName", "Alice");
classRunner.invoke("addName", "Bob");
int count = classRunner.invoke("getCount");
// Print the results.
System.out.println("/*********************************************");
System.out.println("     Type aliases.");
System.out.println("*********************************************/");
System.out.println("count = " + count);
```

* The output is as follows. The `ArrayList` short name was resolved via the type alias map.

```java
/*********************************************
     Type aliases.
*********************************************/
count = 2
```

## Conclusion

In this tutorial we've learned how to extend Java standard library classes, use inherited methods, and register type aliases for convenience. The compiled classes are standard JVM classes that fully participate in the Java type hierarchy and can be cast to their parent types.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/compiler/tutorials/TutorialCompiler10ExtendingJavaClasses.java).
