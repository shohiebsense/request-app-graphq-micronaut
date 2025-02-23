package io.shohiebsense.learningpurposes


import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces

//learning ground purposes
@Controller("/hello")
class HelloController {

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    fun index() = "Hello World hhh"


}