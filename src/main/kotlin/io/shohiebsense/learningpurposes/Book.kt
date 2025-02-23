package io.shohiebsense.learningpurposes


import io.micronaut.core.annotation.Introspected

//learning ground purposes
@Introspected
class Book(val id: String, val name: String, val pageCount: Int, val author: Author)