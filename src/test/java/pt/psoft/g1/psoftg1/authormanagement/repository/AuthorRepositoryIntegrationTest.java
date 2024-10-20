package pt.psoft.g1.psoftg1.authormanagement.repository;

import com.google.common.collect.Iterables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorLendingView;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Based on https://www.baeldung.com/spring-boot-testing
 * <p>Adaptations to Junit 5 with ChatGPT
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class AuthorRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private AuthorRepository authorRepository;

    private Author alex, charles, patricia, pedro, ana;
    private Book b1, b2, b3, b4, b5;

    @BeforeEach
    public void setUp(){

        alex = new Author("Alex", "O Alex escreveu livros", null);
        charles = new Author("Charles", "O Charles escreveu livros", "charles.png");
        patricia = new Author("Patricia", "Sou a Patricia", null);
        pedro = new Author("Pedro", "Olá, sou o Pedro", null);
        ana = new Author("Ana", "Olá, sou a Ana", null);

        entityManager.persist(alex);
        entityManager.persist(charles);
        entityManager.persist(patricia);
        entityManager.persist(pedro);
        entityManager.persist(ana);
        entityManager.flush();
    }

    @Test
    public void whenFindByAuthorNumber_thenReturnAuthor(){

        Optional<Author> author = authorRepository.findByAuthorNumber(alex.getAuthorNumber());
        Assertions.assertNotNull(author);
        Assertions.assertEquals("Alex", author.get().getName());
    }

    @Test
    public void whenSearchByNameNameStartsWith_thenShouldReturnAuthors(){
        List<Author> authors = authorRepository.searchByNameNameStartsWith("Alex");
        Assertions.assertEquals(1,authors.size());
        Assertions.assertEquals(alex,authors.get(0));
    }

    @Test
    public void whenSearchByNameNameStartsWith_thenShouldReturnEmpty(){
        List<Author> authors = authorRepository.searchByNameNameStartsWith("Francisco");
        Assertions.assertEquals(0,authors.size());
    }

    @Test
    public void whenFindByName_thenReturnAuthor() {
        // Arrange
        // alex = new Author("Alex", "O Alex escreveu livros", null);
        // entityManager.persist(alex);
        // entityManager.flush();

        // Act
        /*
        List<Author> list = authorRepository.searchByNameName(alex.getName());

        // Assert
        assertThat(list).isNotEmpty();
        assertThat(list.get(0).getName())
                .isEqualTo(alex.getName());
         */
        List<Author> list = authorRepository.searchByNameName(alex.getName());

        // Assert
        Assertions.assertNotEquals(0,list.size());
        Assertions.assertEquals(alex.getName(), list.get(0).getName());
    }

    @Test
    public void whenSaveAuthor_thenShouldReturnAuthor(){
        Author beatriz = new Author("Beatriz", "Olá, sou a Beatriz", null);
        Author author = authorRepository.save(beatriz);
        Assertions.assertNotNull(author);
        Assertions.assertEquals(beatriz.getName(),author.getName());
        Assertions.assertTrue(beatriz.getAuthorNumber() > 0);
    }

    @Test
    public void whenFindAll_thenShouldReturnAll(){
        Iterable<Author> authors = authorRepository.findAll();
        Assertions.assertNotNull(authors);
        Assertions.assertEquals(5, Iterables.size(authors));
    }

    @Test
    public void whenFindTopAuthorByLendings_thenShouldReturnAll(){

        // Arrange
        Pageable pageableRules = PageRequest.of(0,5);
        ArrayList<Author> b1_authors = new ArrayList<>();
        ArrayList<Author> b2_authors = new ArrayList<>();
        ArrayList<Author> b3_authors = new ArrayList<>();
        ArrayList<Author> b4_authors = new ArrayList<>();
        ArrayList<Author> b5_authors = new ArrayList<>();

        Reader r1 = new Reader("Afonso@teste.com", "passwordSuperSecretaEIndecifrável");
        Reader r2 = new Reader("Maria@teste.com", "passwordSuperSecretaEIndecifrável");
        Reader r3 = new Reader("Carla@teste.com", "passwordSuperSecretaEIndecifrável");
        Reader r4 = new Reader("Tiago@teste.com", "passwordSuperSecretaEIndecifrável");
        Reader r5 = new Reader("Rui@teste.com", "passwordSuperSecretaEIndecifrável");

        r1.setCreatedAt(LocalDateTime.now());
        r1.setModifiedAt(LocalDateTime.now());
        r1.setCreatedBy("admin");
        r1.setModifiedBy("admin");
        r2.setCreatedAt(LocalDateTime.now());
        r2.setModifiedAt(LocalDateTime.now());
        r2.setCreatedBy("admin");
        r2.setModifiedBy("admin");
        r3.setCreatedAt(LocalDateTime.now());
        r3.setModifiedAt(LocalDateTime.now());
        r3.setCreatedBy("admin");
        r3.setModifiedBy("admin");
        r4.setCreatedAt(LocalDateTime.now());
        r4.setModifiedAt(LocalDateTime.now());
        r4.setCreatedBy("admin");
        r4.setModifiedBy("admin");
        r5.setCreatedAt(LocalDateTime.now());
        r5.setModifiedAt(LocalDateTime.now());
        r5.setCreatedBy("admin");
        r5.setModifiedBy("admin");

        entityManager.persist(r1);
        entityManager.persist(r2);
        entityManager.persist(r3);
        entityManager.persist(r4);
        entityManager.persist(r5);

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

        entityManager.persist(rd1);
        entityManager.persist(rd2);
        entityManager.persist(rd3);
        entityManager.persist(rd4);
        entityManager.persist(rd5);

        b1_authors.add(alex);
        b2_authors.add(charles);
        b3_authors.add(patricia);
        b4_authors.add(pedro);
        b5_authors.add(ana);

        Genre g1 = new Genre("Ação");
        Genre g2 = new Genre("Ficção");
        Genre g3 = new Genre("Fantasia");
        Genre g4 = new Genre("Romance");
        Genre g5 = new Genre("Banda Desenhada");

        entityManager.persist(g1);
        entityManager.persist(g2);
        entityManager.persist(g3);
        entityManager.persist(g4);
        entityManager.persist(g5);

        b1 = new Book("9789720706386", "Book 1", "Book 1", g1, b1_authors, null);
        b2 = new Book("9782722203426", "Book 2", "Book 2", g2, b2_authors, null);
        b3 = new Book("9783161484100", "Book 3", "Book 3", g3, b3_authors, null);
        b4 = new Book("9780596520687", "Book 4", "Book 4", g4, b4_authors, null);
        b5 = new Book("0596520689", "Book 5", "Book 5", g5, b5_authors, null);

        entityManager.persist(b1);
        entityManager.persist(b2);
        entityManager.persist(b3);
        entityManager.persist(b4);
        entityManager.persist(b5);

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

        entityManager.persist(l1);
        entityManager.persist(l2);
        entityManager.persist(l3);
        entityManager.persist(l4);
        entityManager.persist(l5);
        entityManager.persist(l6);
        entityManager.persist(l7);
        entityManager.persist(l8);
        entityManager.persist(l9);
        entityManager.persist(l10);
        entityManager.persist(l11);
        entityManager.persist(l12);
        entityManager.persist(l13);
        entityManager.persist(l14);

        entityManager.flush();

        List<AuthorLendingView> expectedList = new ArrayList<AuthorLendingView>();
        expectedList.add(new AuthorLendingView("Ana",5L));
        expectedList.add(new AuthorLendingView("Alex",3L));
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

    @Test
    public void whenDeleteAuthor_thenShouldAuthorShouldNotExist(){
        // Arrange no setup
        Long id = alex.getAuthorNumber();

        // Act & Assert
        Assertions.assertAll(() -> authorRepository.delete(alex));
        Optional<Author> author = authorRepository.findByAuthorNumber(id);
        Assertions.assertEquals(Optional.empty(),author );
    }

    @Test
    public void whenFindCoAuthorsByAuthorNumber_thenShouldReturnCoAuthors(){

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
        Genre g2 = new Genre("Ficção");
        Genre g3 = new Genre("Fantasia");
        Genre g4 = new Genre("Romance");
        Genre g5 = new Genre("Banda Desenhada");

        entityManager.persist(g1);
        entityManager.persist(g2);
        entityManager.persist(g3);
        entityManager.persist(g4);
        entityManager.persist(g5);
        entityManager.flush();

        b1 = new Book("9789720706386", "Book 1", "Book 1", g1, b1_authors, null);
        b2 = new Book("9782722203426", "Book 2", "Book 2", g2, b2_authors, null);
        b3 = new Book("9783161484100", "Book 3", "Book 3", g3, b3_authors, null);
        b4 = new Book("9780596520687", "Book 4", "Book 4", g4, b4_authors, null);
        b5 = new Book("0596520689", "Book 5", "Book 5", g5, b5_authors, null);

        entityManager.persist(b1);
        entityManager.persist(b2);
        entityManager.persist(b3);
        entityManager.persist(b4);
        entityManager.persist(b5);

        // Act
        List<Author> coAuthors = authorRepository.findCoAuthorsByAuthorNumber(alex.getAuthorNumber());

        // Assert
        Assertions.assertNotNull(coAuthors);
        Assertions.assertEquals(2, coAuthors.size());
        Assertions.assertTrue(coAuthors.contains(patricia) && coAuthors.contains(charles));
    }

}
