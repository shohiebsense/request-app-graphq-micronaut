package io.shohiebsense


import graphql.GraphQL
import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import graphql.schema.idl.TypeRuntimeWiring
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.core.io.ResourceResolver
import io.shohiebsense.learningpurposes.GraphQLDataFetchers
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import jakarta.inject.Singleton

//some mixed with learning ground purposes
@Factory
class GraphQLFactory {

    @Bean
    @Singleton
    fun graphQL(resourceResolver: ResourceResolver, graphQLDataFetchers: GraphQLDataFetchers, requestDataFetcher: GraphQLRequestDataFetchers): GraphQL {
        val schemaParser = SchemaParser()

        val typeRegistry = TypeDefinitionRegistry()
        val graphqlSchema = resourceResolver.getResourceAsStream("classpath:schema.graphqls")

        return if (graphqlSchema.isPresent) {
            typeRegistry.merge(schemaParser.parse(BufferedReader(InputStreamReader(graphqlSchema.get()))))
            val runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Query") { typeWiring -> typeWiring
                    .dataFetcher("hello", graphQLDataFetchers.helloDataFetcher())
                    .dataFetcher("getAllBooks", graphQLDataFetchers.booksDataFetcher())
                    .dataFetcher("bookById", graphQLDataFetchers.bookByIdDataFetcher())
                    .dataFetcher("getAllRequests", requestDataFetcher.getAllRequestsDataFetcher())
                }
                .type(TypeRuntimeWiring.newTypeWiring("Book")
                    .dataFetcher("author", graphQLDataFetchers.authorDataFetcher()))

                .type("Mutation") { typeWiring ->
                    typeWiring
                        .dataFetcher("addRequest", requestDataFetcher.addRequestDataFetcher())
                        .dataFetcher("updateRequestStatus", requestDataFetcher.editRequestDataFetcher())
                }

                .build()
            val schemaGenerator = SchemaGenerator()
            val graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring)
            GraphQL.newGraphQL(graphQLSchema).build()
        } else {
            LOG.debug("No GraphQL services found, returning empty schema")
            GraphQL.Builder(GraphQLSchema.newSchema().build()).build()
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(GraphQLFactory::class.java)
    }
}