package io.shohiebsense.learningpurposes


import jakarta.inject.Singleton

//learning purposes
@Singleton
class DbRepository {

    fun findAllBooks(): List<Book> {
        return books
    }

    fun findAllAuthors(): List<Author> {
        return books.map(Book::author)
    }

    companion object {
        private val books = listOf(
            Book("book-1", "Harry Potter and the Philosopher's Stone", 223, Author("author-1", "Joanne", "Rowling")),
            Book("book-2", "Moby Dick", 635, Author("author-2", "Herman", "Melville")),
            Book("book-3", "Interview with the vampire", 371, Author("author-3", "Anne", "Rice"))
        )
    }
}