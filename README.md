# Lembas-core

[![Build Status](https://travis-ci.org/anlcan/Lembas-core.svg?branch=master)](https://travis-ci.org/anlcan/Lembas-core)

## Java object serialization and description library

This is a simple and flexible object serialization library built on top of [simple.json](https://code.google.com/p/json-simple/) library. At the time of serialization, object
inject its class(type) into serialized data, so it can be deserailized as the correct type.

``` java
public class User extends LembasObject{

    public String name;
    public String value;
    public int loginCount;
    public Date registerDate;

}
```

An instance of User class will be serialized as follows.

```json

{
    "_type":"User",
    "registerDate":{"_type":"LembasDate","time":"16:58:05","date":"25\/08\/2014","zone":"+0300"},
    "name":"Keanu",
    "value":"The One",
    "loginCount":1337
}

```

We can  than proceed to restore the instance with *LembasUtil*
``` java

 User neo = LembasUtil.deserialize(json);

```



## Roadmap

I have been coding, debugging, tweaking, messing with this library over 2 year. It has been a tremendous
 learning opportunity for me but as a framework, it has many shortcomings.
These are things that I am planning to implement in the near feature.

- Replace simple.json with codehaus.jackson
- Change date with an ISO Format


