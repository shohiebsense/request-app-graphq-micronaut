package io.shohiebsense.learningpurposes


import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import jakarta.inject.Singleton

//learning ground purposes
@Singleton
class GraphQLDataFetchers(private val dbRepository: DbRepository) {

    fun helloDataFetcher() : DataFetcher<String> {
        return DataFetcher { env: DataFetchingEnvironment ->
            var name = env.getArgument<String>("name")
            if (name == null || name.trim().isEmpty()) {
                name = "World"
            }
            "Hello $name!"
        }
    }

    fun booksDataFetcher(): DataFetcher<List<Book>> {
        return DataFetcher { dataFetchingEnvironment: DataFetchingEnvironment ->
            dbRepository.findAllBooks()
        }
    }

    fun bookByIdDataFetcher(): DataFetcher<Book> {
        return DataFetcher { dataFetchingEnvironment: DataFetchingEnvironment ->
            val bookId: String? = dataFetchingEnvironment.getArgument("id")
            dbRepository.findAllBooks()
                .firstOrNull { book: Book -> (book.id == bookId) }
        }
    }

    fun authorDataFetcher(): DataFetcher<Author> {
        return DataFetcher { dataFetchingEnvironment: DataFetchingEnvironment ->
            val book: Book? = dataFetchingEnvironment.getSource()
            if (book != null) {
                val authorBook: Author = book.author
                dbRepository.findAllAuthors()
                    .firstOrNull { author: Author -> (author.id == authorBook.id) }
            } else {
                null
            }
        }
    }


}