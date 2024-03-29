
# json

json is small library for manipulating json data structures.It is useful, 
when you need to create small json data directly (without java binding).

It is possible to parse json string info java tree like data structure and
build java tree like data structure and convert them into json string. 


## Data structures

All provided data structures are defined by set of interfaces. Library provides 
implementation for all of those interfaces. Most of them can be instantiated using 
factory class JsonFactory. 

### JsonValue

Generic json value. It is abstract representation of one of JsonObject, JsonArray 
or one onf JsonLiteral instance. It is not possible to instantiate this type of object.

### JsonObject

json object representation with set of named attrinutes. (JsonFactory.object())

### JsonArray

json array representation with set of json values. (JsonFactory.array())
.
### JsonLiteral

Generic json literal value. There are set of subclasses of this class. 
 - JsonBoolLiteral - boolean json literal (true, false) (JsonFactory.boolLiteral())
 - JsonExpLiteral - exponential json literal (12E-1, -2E12, ...) (JsonFactory.expLiteral())
 - JsonFracLiteral - fraction json literal (12.3, -33.23, ...) (JsonFactory.fracLiteral())
 - JsonIntLiteral - int json literal (1, 2212342, ...) (JsonFactory.intLiteral())
 - JsonNullLiteral - null json literal (null) (JsonFactory.nullLiteral())
 - JsonStringLiteral - string json literal ("foo", "bar", ...) (JsonFactory.stringLiteral())

## Json parsing to tree 

You can parse json strings easily by calling 
```java
  String jsonvalue = .....
  JsonValue value = JsonParser.parse(jsonvalue);
```
or 
```java
  Reader jsonvalue = .....
  JsonValue value = JsonParser.parse(jsonvalue);
```

## Json parsing to stream 

  You can traverse json tree and produces stream of json values 
  
  Useful, when you have big json and you want to extract only small 
  json sub tree at once.
 
  Imagine you have json like 
```
  { "items" : [
 		{"name": "name-1", "value": 1},
 		{"name": "name-2", "value": 2},
 		{"name": "name-3", "value": 3},
 		{"name": "name-4", "value": 4},
 		...
  ]
  }
```
  So you can parse whole json and iterate parts. In this case whole 
  json is loaded before traversal. (It is effective for small jsons 
  only)
```java
    JsonValue root = JsonParser.parse(inputstream);
    root.find(SPM.path("items", "*")).stream()
    // or if you want traverse only values
    // root.find(SPM.path("items", "*", "value")).stream()
```
  This class allows you to read only parts you want. But it is little 
  bit slower.
```java
    JsonStream.instance(inputstream, SPM.path("items", "*")).stream();
    // or if you want traverse only values
    // JsonStream.instance(inputstream, SPM.path("items", "*", "value")).stream();
```
  So if you have pretty big json which is json array and you are goinig to 
  process it item by item, you can use JsonStream with path "*".  


## Json to string conversion

To obtain string representation of json data structure you can call 
```java
  JsonValue jsonvalue = .....
  String onelinestring = jsonvalue.toCompactString();
  String indendedstirng = jsonvalue.toPrettyString("\t");
```

## Building json structure

To create simple instances of json data structures you can use JsonFactory class.
Than it is possible to use appropriate API from JsonObject or JsonArray to build 
whole json data structure tree. 


This is simple example of such construction of following data structure

```json
{
  "name" : "John",
  "surname" : "Smith",
  "titles" : [
    "Mudr.",
    "Phd."
  ]
}
```

```java
  JsonObject person = JsonFactory.object()
    .add("name", JsonFactory.stringLiteral("John"))
    .add("surname", JsonFactory.stringLiteral("Smith"))
    .add("titles", JsonFactory.array()
        .add(JsonFactory.stringLiteral("Mudr."))
        .add(JsonFactory.stringLiteral("Phd."))
      );
```

## Searching json structure

If you have json structure and you can identify some substructure by 
path, you can find them by calling  

```java
  JsonObject o = ...
  JsonValue v = o.find("menu", "popup", "menuitem", "*", "value").first();
  JsonValue v = o.findFirst(SimplePathMatcher.instance("menu", "popup", "menuitem", "*", "value"));
  List<JsonValue> list = o.find("menu", "popup", "menuitem", "*", "value").all();
  List<JsonValue> list = o.findAll(SimplePathMatcher.instance("menu", "popup", "menuitem", "*", "value"));
```

Character '\*' stands for any path item. 

If you search for literal values you can call 

```java
  JsonObject o = ...
  Stirng v = o.find("menu", "popup", "menuitem", "*", "value").findLiteral();
  Stirng v = o.findFirstLiteral(SimplePathMatcher.instance("menu", "popup", "menuitem", "*", "value"));
  List<String> list = o.find("menu", "popup", "menuitem", "*", "value").allLiterals();
  List<String> list = o.findAllLiterals(SimplePathMatcher.instance("menu", "popup", "menuitem", "*", "value"));
```

If you search for literal from big structure and you don;t want to parse if first.
values you can call following code. But it is faster only for special cases 

```java
  String value = LiteralParser.firstAsString(
                    StringSource.instance(json)
                    , SimplePathMatcher.instance("*", "cars", "*"));
```

## Wild Searching json structure

It is possible to use WildPathMatcher for searching. In this case there are two types of path 
elements. 
 - single wild path element - it is direct name or you van use * and ? to represent more/single
   characters in single path element. (like "car-*" or "car-??")
 - multiple wild path element "**" - it represents any sequence of path elements. 
JsonObject
Example:
```java
  List<String> list = o.findAllLiterals(WPM.fromPath("**/person-*/surname"));
```



## Formatting without complete parsing

If you have a json structure and you want to transform it to noindent form or to 
indent form and you don't want to parse it You can use simplified formatting.
It works for well formated json structures only. 

The class don't make full parsing of input. Implementation just tries to recognize 
blank characters and string literals and transform them somehow.

```java
  String json = ...
  String formatedJson = JsonFormat.from(json).indent(2, ' ').toText();
  String formatedJson = JsonFormat.from(json).noindent().toText();
```
Or you can use Reader/Writer version

```java
  Reader jsonReader = ...
  Writer jsonWriter = ...
  JsonFormat.from(jsonReader).indent("  ").toWriter(jsonWriter);
  JsonFormat.from(jsonReader).noindent().toWriter(jsonWriter);
```
There is also possible to cut string literals to defined length. This can be useful
for logging json which contains very long data (like binaries), which are not necessary 
to log fully.

```java
  String json = ...
  String formatedJson = JsonFormat.from(json).indent("  ").cutStringLiterals(50).toText();
```

The result can looks like 

```json
{
  "name" : "image.jpg",
  "bytes" : "VGhlcmUgaXMgYWxzbyBwb3NzaWJsZSB0byBjdXQgc3RyaW5nIGxpdG ..."
}
```



## Maven usage

```
   <dependency>
      <groupId>io.github.antonsjava</groupId>
      <artifactId>json</artifactId>
      <version>LATESTVERSION</version>
   </dependency>
```
