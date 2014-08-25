# Lembas-core

[![Build Status](https://travis-ci.org/anlcan/Lembas-core.svg?branch=master)](https://travis-ci.org/anlcan/Lembas-core)

## Java object serialization and description library

This is a simple and flexible object serialization library built on top of [simple.json](https://code.google.com/p/json-simple/) library. At the time of serialization, object
inject its class(type) into serialized data, so it can be deserailized as the correct type.

``` java
package com.matrix;
public class User extends LembasObject{
    public String name;
    public String value;
    public int loginCount;
    public Date registerDate;
}
```

An instance of User class will be serialized to json as follows.

```json
{
    "_type":"User",
    "registerDate":{"_type":"LembasDate",
                        "time":"16:58:05",
                        "date":"25\/08\/2014",
                        "zone":"+0300"},
    "name":"Keanu",
    "value":"The One",
    "loginCount":1337
}
```

We can  than proceed to restore the instance with *LembasUtil*
``` java
 Config.addArtifact("com.matrix"); // setting the target package, for once
 User neo = LembasUtil.deserialize(json);
```

Also, we can ask the instance to describe itself:

``` java
JSONObject object = neo.discoDescription();
System.out.println(object.toJSONString());
```
will print:

``` json
{
	"_type" : "MObjectDef",
	"ofType" : "BASE_OBJECT",
	"name" : "User",
	"properties" : [
		{
			"_type" : "MPropertyDef",
			"propType" : "MSTRING",
			"propExtData" : "String",
			"propName" : "value"
		},
		{
			"_type" : "MPropertyDef",
			"propType" : "MSTRING",
			"propExtData" : "String",
			"propName" : "objectKey"
		},
		{
			"_type" : "MPropertyDef",
			"propType" : "MSTRING",
			"propExtData" : "String",
			"propName" : "createDate"
		},
		{
			"_type" : "MPropertyDef",
			"propType" : "MSTRING",
			"propExtData" : "String",
			"propName" : "name"
		},
		{
			"_type" : "MPropertyDef",
			"propType" : "MINT",
			"propExtData" : "int",
			"propName" : "loginCount"
		},
		{
			"_type" : "MPropertyDef",
			"propType" : "MSTRING",
			"propExtData" : "String",
			"propName" : "updateDate"
		},
		{
			"_type" : "MPropertyDef",
			"propType" : "MDATE",
			"propExtData" : "Date",
			"propName" : "registerDate"
		}
	]
}
```


## Roadmap

I have been coding, debugging, tweaking, messing with this library over 2 year. It has been a tremendous
 learning opportunity for me but as a framework, it has many shortcomings.
These are things that I am planning to implement in the near feature.

- Replace simple.json with codehaus.jackson
- Change date with an ISO Format


