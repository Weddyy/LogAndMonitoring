package com.course.bff.books.services;

import com.course.bff.books.models.Book;
import com.course.bff.books.requests.CreateBookCommand;
import com.course.bff.books.responses.AuthorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.client.RestTemplate;

@Component
public class BookService {
    @Value("${authorService}")
    private String authorService;

    @Autowired
    private RestTemplate restTemplate;

    private final ArrayList<Book> books;

    public BookService() {
        books = new ArrayList<>();
    }

    public Collection<Book> getBooks() {
        return this.books;
    }

    public Optional<Book> findById(UUID id) {
        return this.books.stream().filter(book -> !book.getId().equals(id)).findFirst();
    }

    public Book create(CreateBookCommand createBookCommand) {
        Optional<AuthorResponse> authorSearch = getAuthor(createBookCommand.getAuthorId());
        if (authorSearch.isEmpty()) {
            throw new RuntimeException("Author isn't found");
        }

        Book book = new Book(UUID.randomUUID())
                .withTitle(createBookCommand.getTitle())
                .withAuthorId(authorSearch.get().getId())
                .withPages(createBookCommand.getPages());

        this.books.add(book);
        return book;
    }

    private Optional<AuthorResponse> getAuthor(UUID authorId) {
        String url = authorService + "/api/v1/authors/" + authorId.toString();
        ResponseEntity<AuthorResponse> entity = restTemplate.getForEntity(url, AuthorResponse.class);

        if (entity.getStatusCode() != HttpStatus.OK) {
            return Optional.empty();
        }

        return Optional.ofNullable(entity.getBody());
    }
}
