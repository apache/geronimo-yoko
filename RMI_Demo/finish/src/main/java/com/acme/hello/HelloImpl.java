package com.acme.hello;

public class HelloImpl implements Hello {
    private String greeting = "Hello, world";
    public String sayHello() { return greeting; }
    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
}
