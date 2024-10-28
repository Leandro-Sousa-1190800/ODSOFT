package pt.psoft.g1.psoftg1.authormanagement.repository;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorLendingView;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Based on https://www.baeldung.com/spring-boot-testing
 * <p>Adaptations to Junit 5 with ChatGPT
 */
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthorRepositoryIntegrationTest {

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ReaderRepository readerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LendingRepository lendingRepository;

    private Author beatriz = new Author("Beatriz", "Olá, sou a Beatriz", null);

    @Test
    @Order(1)
    public void emptyDatabase_whenFindAll_thenShouldReturnEmpty(){
        Iterable<Author> authors = authorRepository.findAll();
        Assertions.assertNotNull(authors);
        Assertions.assertEquals(0, Iterables.size(authors));
    }

    @Test
    @Order(2)
    public void emptyDatabase_whenFindByAuthorNumber_thenReturnAuthor(){
        Optional<Author> author = authorRepository.findByAuthorNumber(1L);
        Assertions.assertNotNull(author);
        Assertions.assertEquals(Optional.empty(), author);
    }

    @Test
    @Order(3)
    public void emptyDatabase_whenFindByName_thenReturnEmptyList() {
        List<Author> list = authorRepository.searchByNameName("Rui");

        // Assert
        Assertions.assertNotNull(list);
        Assertions.assertEquals(0,list.size());
    }

    @Test
    @Order(4)
    public void emptyDatabase_whenSearchByNameNameStartsWith_thenShouldReturnEmpty(){
        List<Author> authors = authorRepository.searchByNameNameStartsWith("Francisco");
        Assertions.assertEquals(0,authors.size());
    }

    @Test
    @Order(5)
    public void whenSaveAuthor_thenShouldReturnAuthor(){
        Author author = authorRepository.save(beatriz);
        Assertions.assertNotNull(beatriz);
        Assertions.assertEquals(author.getName(),beatriz.getName());
        Assertions.assertTrue(beatriz.getAuthorNumber() > 0);
    }

    @Test
    @Order(6)
    public void whenFindAll_thenShouldReturnAll(){
        Iterable<Author> authors = authorRepository.findAll();
        Assertions.assertNotNull(authors);
        Assertions.assertEquals(1, Iterables.size(authors));
    }

    @Test
    @Order(7)
    public void whenFindByName_thenReturnAuthorList() {
        Author beatriz1 = new Author("Beatriz Carmo do Santos", "Olá, sou a Beatriz", null);
        Author beatriz2 = new Author("Beatriz Santos do Carmo", "Olá, sou a Beatriz", null);
        authorRepository.save(beatriz1);
        authorRepository.save(beatriz2);
        List<Author> list = authorRepository.searchByNameName(beatriz1.getName());

        // Assert
        Assertions.assertNotNull(list);
        Assertions.assertEquals(1,list.size());
        Assertions.assertEquals(beatriz1.getName(),list.get(0).getName());
    }

    @Test
    @Order(8)
    public void whenSearchByNameNameStartsWith_thenShouldReturnList(){
        List<Author> authors = authorRepository.searchByNameNameStartsWith("Beatriz");
        Assertions.assertEquals(3,authors.size());
    }

    @Test
    @Order(9)
    public void whenFindByAuthorNumber_thenReturnAuthor(){
        Optional<Author> author = authorRepository.findByAuthorNumber(1L);
        Assertions.assertNotNull(author);
        Assertions.assertEquals(beatriz.getName(), author.get().getName());
        Assertions.assertEquals(beatriz.getBio(), author.get().getBio());
        Assertions.assertEquals(1L, author.get().getAuthorNumber());
    }

    @Test
    @Order(10)
    public void whenDeleteAuthor_thenShouldAuthorShouldNotExist(){
        // Arrange
        Long id = 1L;
        Optional<Author> authorDelete = authorRepository.findByAuthorNumber(id);

        // Act
        Assertions.assertAll(() -> authorRepository.delete(authorDelete.get()));

        // Assert
        Optional<Author> author = authorRepository.findByAuthorNumber(id);
        Assertions.assertEquals(Optional.empty(),author );
    }


    @Test
    @Order(11)
    public void whenFindCoAuthorsByAuthorNumber_thenShouldReturnCoAuthors(){
        Author alex = authorRepository.save(new Author("Alex", "Olá, sou o Alex", null));
        Author charles = authorRepository.save(new Author("Charles", "Olá, sou o Charles", null));
        Author patricia = authorRepository.save(new Author("Patricia", "Olá, sou a Patricia", null));
        Author pedro = authorRepository.save(new Author("Pedro", "Olá, sou o Pedro", null));

        Author ana = authorRepository.save(new Author("Ana", "Olá, sou a Ana", null));

        ArrayList<Author> b1_authors = new ArrayList<>();
        ArrayList<Author> b2_authors = new ArrayList<>();
        ArrayList<Author> b3_authors = new ArrayList<>();
        ArrayList<Author> b4_authors = new ArrayList<>();
        ArrayList<Author> b5_authors = new ArrayList<>();

        b1_authors.add(alex);
        b2_authors.add(charles);
        b2_authors.add(alex);
        b3_authors.add(patricia);
        b3_authors.add(alex);
        b4_authors.add(pedro);
        b5_authors.add(ana);

        Genre g1 = new Genre("Ação");

        genreRepository.save(g1);

        Book b1 = new Book("9789720706386", "Book 1", "Book 1", g1, b1_authors, null);
        Book b2 = new Book("9782722203426", "Book 2", "Book 2", g1, b2_authors, null);
        Book b3 = new Book("9783161484100", "Book 3", "Book 3", g1, b3_authors, null);
        Book b4 = new Book("9780596520687", "Book 4", "Book 4", g1, b4_authors, null);
        Book b5 = new Book("0596520689", "Book 5", "Book 5", g1, b5_authors, null);

        bookRepository.save(b1);
        bookRepository.save(b2);
        bookRepository.save(b3);
        bookRepository.save(b4);
        bookRepository.save(b5);

        // Act
        List<Author> coAuthors = authorRepository.findCoAuthorsByAuthorNumber(alex.getAuthorNumber());

        // Assert
        Assertions.assertNotNull(coAuthors);
        Assertions.assertEquals(2, coAuthors.size());
        Assertions.assertTrue(coAuthors.stream().filter(e -> Objects.equals(e.getAuthorNumber(), patricia.getAuthorNumber())).count() == 1
                && coAuthors.stream().filter(e -> Objects.equals(e.getAuthorNumber(), charles.getAuthorNumber())).count() == 1);
    }

    @Test
    @Order(12)
    public void whenFindTopAuthorByLendings_thenShouldReturnAll(){

        // Arrange
        Pageable pageableRules = PageRequest.of(0,5);

        Reader r1 = new Reader("Afonso@teste.com", "passwordSuperSecretaEIndecifrável");
        Reader r2 = new Reader("Maria@teste.com", "passwordSuperSecretaEIndecifrável");
        Reader r3 = new Reader("Carla@teste.com", "passwordSuperSecretaEIndecifrável");
        Reader r4 = new Reader("Tiago@teste.com", "passwordSuperSecretaEIndecifrável");
        Reader r5 = new Reader("Rui@teste.com", "passwordSuperSecretaEIndecifrável");

        userRepository.save(r1);
        userRepository.save(r2);
        userRepository.save(r3);
        userRepository.save(r4);
        userRepository.save(r5);

        ReaderDetails rd1 = new ReaderDetails(1,r1,
                "1989-12-08",
                "912345678",
                true,
                false,
                false,
                null,
                new ArrayList<>());

        ReaderDetails rd2 = new ReaderDetails(2,r2,
                "1999-02-24",
                "912345678",
                true,
                false,
                false,
                null,
                new ArrayList<>());

        ReaderDetails rd3 = new ReaderDetails(3,r3,
                "2005-01-31",
                "912345678",
                true,
                false,
                false,
                null,
                new ArrayList<>());

        ReaderDetails rd4 = new ReaderDetails(4,r4,
                "1999-10-10",
                "912345678",
                true,
                false,
                false,
                null,
                new ArrayList<>());

        ReaderDetails rd5 = new ReaderDetails(5,r5,
                "2001-12-02",
                "912345678",
                true,
                false,
                false,
                null,
                new ArrayList<>());

        readerRepository.save(rd1);
        readerRepository.save(rd2);
        readerRepository.save(rd3);
        readerRepository.save(rd4);
        readerRepository.save(rd5);

        List<Book> books = Lists.newArrayList(bookRepository.findByGenre("Ação"));

        Book b5 = books.get(4);
        Book b4 = books.get(3);
        Book b3 = books.get(2);
        Book b2 = books.get(1);
        Book b1 = books.get(0);

        Lending l1 = new Lending(b5, rd1, 1,100,50);
        Lending l2 = new Lending(b5, rd2, 2,100,50);
        Lending l3 = new Lending(b5, rd3, 3,100,50);
        Lending l4 = new Lending(b5, rd4, 4,100,50);
        Lending l5 = new Lending(b5, rd5, 5,100,50);
        Lending l9 = new Lending(b3, rd2, 9,100,50);
        Lending l10 = new Lending(b3, rd3, 10,100,50);
        Lending l11 = new Lending(b3, rd4, 11,100,50);
        Lending l6 = new Lending(b1, rd1, 6,100,50);
        Lending l7 = new Lending(b1, rd2, 7,100,50);
        Lending l8 = new Lending(b1, rd3, 8,100,50);
        Lending l12 = new Lending(b4, rd1, 12,100,50);
        Lending l13 = new Lending(b4, rd5, 13,100,50);
        Lending l14 = new Lending(b2, rd2, 14,100,50);

        lendingRepository.save(l1);
        lendingRepository.save(l2);
        lendingRepository.save(l3);
        lendingRepository.save(l4);
        lendingRepository.save(l5);
        lendingRepository.save(l6);
        lendingRepository.save(l7);
        lendingRepository.save(l8);
        lendingRepository.save(l9);
        lendingRepository.save(l10);
        lendingRepository.save(l11);
        lendingRepository.save(l12);
        lendingRepository.save(l13);
        lendingRepository.save(l14);

        List<AuthorLendingView> expectedList = new ArrayList<AuthorLendingView>();
        expectedList.add(new AuthorLendingView("Alex",7L));
        expectedList.add(new AuthorLendingView("Ana",5L));
        expectedList.add(new AuthorLendingView("Patricia",3L));
        expectedList.add(new AuthorLendingView("Pedro",2L));
        expectedList.add(new AuthorLendingView("Charles",1L));
        Page<AuthorLendingView> expected = new PageImpl<>(expectedList);

        // Act
        Page<AuthorLendingView> lendingViews = authorRepository.findTopAuthorByLendings(pageableRules);

        // Assert
        Assertions.assertNotNull(lendingViews);
        Assertions.assertEquals(lendingViews.get().count(), expected.get().count());
        Assertions.assertEquals(lendingViews.get().toList().get(0), expected.get().toList().get(0));
        Assertions.assertEquals(lendingViews.get().toList().get(1), expected.get().toList().get(1));
        Assertions.assertEquals(lendingViews.get().toList().get(2), expected.get().toList().get(2));
        Assertions.assertEquals(lendingViews.get().toList().get(3), expected.get().toList().get(3));
        Assertions.assertEquals(lendingViews.get().toList().get(4), expected.get().toList().get(4));
    }
}
