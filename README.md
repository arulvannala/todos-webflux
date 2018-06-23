## Todo(s) Webflux Backing API

Howdy and welcome.  This repository contains a Reactive API implemented in Spring Boot 2.0 and Spring Cloud.  This API uses Spring WebFlux to map endpoints to methods using the time tested ``@RestController`` and ``@RequestMapping`` annotations.  This API is a backend for the [Vue.js version](http://todomvc.com/examples/vue/) of [TodoMVC](http://todomvc.com/).  Frontend repo is [here](https://github.com/corbtastik/todos-ui).

### Primary dependencies

* Spring Boot Starter WebFlux (non-blocking API)
* Spring Boot Actuators (ops endpoints)
* Spring Cloud Netflix Eureka Client (service discovery)
* Spring Cloud Config Client (central config)
* Spring Cloud Sleuth (request tracing)

This API is part of the [Todo collection](https://github.com/corbtastik/todos-ecosystem) of Microservices used in Cloud Native Developer workshops.  For a full list of Microservices checkout the [Todo(s) EcoSystem repo](https://github.com/corbtastik/todos-ecosystem).

### Spring Boot Reactive Stack

This Microservice is similar to Todo(s) API but with one major difference...it's non-blocking whereas Todo(s) API is synchronous.  With the release of Spring 5 & Spring Boot 2 earlier this Spring :) the framework offers up a sibling Web Stack thats fully non-blocking and runs on [Netty](http://netty.io/).  Traditional Spring Boot Microservice run on the Tomcat Servlet Container but developers can choose which embedded container to use, for example ``spring-boot-starter-jetty`` or ``spring-boot-starter-undertow`` in addition to the default ``spring-boot-starter-tomcat`` which is pulled in by default when ``spring-boot-starter-web`` is included.

Now developer have another embedded container stack to use that's reactive and provided by another starter, ``spring-boot-starter-webflux``.  The new Reactive Stack takes advantage of multi-core CPU(s) in a manner that allows for a higher number of connections than a Servlet Stack Microservice.

Prior to Spring Boot 2 if a developer wanted to implement a Reactive Microservice they had to step outside the Spring Framework to implement.  Now developers can stay within one Framework and get Reactive support...awe yeah.  Reactive Spring will continue to take hold, grow and improve Reactive support at every level of the stack - web, security, data and messaging.

### Reactive Stack Hierarchy

App | 
------------ | ------------- 

Spring Boot 2 | Reactive support from ``spring-boot-starter-webflux``
Spring 5 Framework | Provides WebFlux module
Reactor Core | Core non-blocking libs (see [projectreactor](http://projectreactor.io/)

### Reactive Controller

We can define a Reactive Controller using the same web binding annotations, ``@RestController`` and ``@RequestMapping`` that we know and love from SpringMVC (yeah thats pretty cool and exactly what a framework is suppose to enable wink wink).  In fact if you look at the ``@RestController`` for [Todo(s) API](https://github.com/corbtastik/todos-api) you'll see the same framework annotations on the non-reactive implementation.

The key difference is how we type inputs and outputs from the ``@RestController``, in the Reactive Stack the types are Non-blocking by design so we don't merely return rendered values we return "promises" to provide rendered values.  The reactive types are core to [reactor](http://projectreactor.io/) and are more accurately called ``Publisher(s)`` and they "publish" to ``Subscriber(s)`` who have explicitly subscribed and asked for information.  These are types specific to the [Reactive Streams](http://www.reactive-streams.org/) spec, a spec that's implemented by [reactor](http://projectreactor.io/).

At the framework level we work with 2 key non-blocking types, the first and simplest is [``Mono<T>``](http://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html).  ``Mono<T>`` is (brace yourself Mono is a mouthful) a Generically Typed Reactive Stream Publisher that completes by emitting a `T` or raising an Error.

annotations on a ``class`` we can encapsulates and provide context for an API.  ``TodoAPI`` maps http requests starting with `/todos` to CRUD methods implemented in this class.  The [Todo(s) Data](https://github.com/corbtastik/todos-data) Microservice exposes a similar CRUD API but with zero code from us, it uses Spring Data Rest to blanket a Data Model with a CRUD ops.  Check out [repo](https://github.com/corbtastik/todos-data) for more information on Spring Boot with Spring Data Rest.

Using the same eex

