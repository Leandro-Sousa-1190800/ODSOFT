package pt.psoft.g1.psoftg1.authormanagement.services;

import com.google.common.collect.Iterables;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorLendingView;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

/**
 * Based on https://www.baeldung.com/spring-boot-testing
 * <p>Adaptations to Junit 5 with ChatGPT
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AuthorServiceImplIntegrationTest {
    @Autowired
    private AuthorService authorService;
    @MockBean
    private AuthorRepository authorRepository;

    private Author alex, charles;

    @BeforeEach
    public void setUp() {
        this.alex = new Author("Alex", "O Alex escreveu livros", null);
        this.charles = new Author("Charles", "O Charles escreveu livros", null);

        when(authorRepository.findByAuthorNumber(0L))
                .thenReturn(Optional.empty());

        when(authorRepository.findByAuthorNumber(1L))
                .thenReturn(Optional.of(alex));

        when(authorRepository.findByAuthorNumber(2L))
                .thenReturn(Optional.of(charles));
    }

    @Test
    public void whenNoAuthor_thenFindAllShouldReturnEmptyList(){
        when(authorRepository.findAll())
                .thenReturn(new ArrayList<>());

        Iterable<Author> authors = authorService.findAll();

        Assertions.assertNotNull(authors);
        Assertions.assertEquals(0, Iterables.size(authors));
    }

    @Test
    public void whenExistAuthors_thenFindAllShouldReturnAuthorList(){
        List<Author> list = new ArrayList<>();
        list.add(alex);
        list.add(charles);

        when(authorRepository.findAll())
                .thenReturn(list);

        Iterable<Author> authors = authorService.findAll();

        Assertions.assertNotNull(authors);
        Assertions.assertEquals(2,Iterables.size(authors));
    }

    @Test
    public void whenValidId_thenAuthorShouldBeFound() {
        Long id = 1L;
        Optional<Author> found = authorService.findByAuthorNumber(id);
        Assertions.assertNotNull(found);
        Assertions.assertEquals(found.get().getName(),"Alex");
    }
    @Test
    public void whenInvalidId_thenAuthorShouldBeNotFound() {
        Long id = 2L;
        Optional<Author> found = authorService.findByAuthorNumber(id);
        Assertions.assertEquals(Optional.empty(),found);
    }

    @Test
    public void whenExistingName_thenListAuthorShouldBeFound() {
        Author alex = new Author("Alex", "O Alex escreveu livros", null);
        List<Author> list = new ArrayList<>();
        list.add(alex);

        when(authorRepository.searchByNameNameStartsWith(alex.getName()))
                .thenReturn(list);

        List<Author> authors = authorService.findByName("Alex");
        Assertions.assertNotNull(authors);
        Assertions.assertEquals(1,authors.size());
    }
    @Test
    public void whenNoExistingName_thenListAuthorShouldBeFound() {
        List<Author> authors = authorService.findByName("Charles");
        Assertions.assertNotNull(authors);
        Assertions.assertEquals(0,authors.size());
    }

    @Test
    public void whenAuthorCreate_thenShouldReturnAuthor(){

        Author author = new Author("Eva","Sou a Eva",null);
        when(authorRepository.save(Mockito.any(Author.class))).thenReturn(author);

        CreateAuthorRequest request = new CreateAuthorRequest("Eva","Sou a Eva",null,"");

        Assertions.assertNotNull(authorService.create(request));
    }

    @Test
    public void whenPartialUpdateInvalidAuthor_thenShouldThrowNotFound(){
        UpdateAuthorRequest updateRequest = new UpdateAuthorRequest("João","Olá, sou o João", null,"");

        Assertions.assertThrows(NotFoundException.class, () -> authorService.partialUpdate(0L,updateRequest,1));
    }

    @Test
    public void whenPartialUpdateValidAuthor_thenShouldReturnAuthor(){
        Author author = new Author("Alex","Olá, sou o Alex",null);
        when(authorRepository.save(Mockito.any(Author.class))).thenReturn(author);

        UpdateAuthorRequest updateRequest = new UpdateAuthorRequest("Olá, sou o Alex", "Alex",null,"");
        Assertions.assertNotNull(authorService.partialUpdate(1L,updateRequest,0));
    }

    /**
     * Não tenho a certeza se é relevante ter este teste visto a excepção ser levantada no Author.class
     */
    @Test
    public void whenPartialUpdateInvalidVersion_thenShouldThrowNotFoundExpecption(){
        UpdateAuthorRequest updateRequest = new UpdateAuthorRequest("Olá, sou o Alex", "Alex",null,"");
        Assertions.assertThrows(StaleObjectStateException.class, () -> authorService.partialUpdate(1L,updateRequest,1));
    }

    @Test
    public void whenFindTopAuthorByLendingsWithNoAuthors_thenShouldReturnEmpty(){
        List<AuthorLendingView> list = new ArrayList<AuthorLendingView>();
        Page<AuthorLendingView> page = new PageImpl<>(list);

        Pageable pageableRules = PageRequest.of(0,5);
        when(authorRepository.findTopAuthorByLendings(pageableRules)).thenReturn(page);

        Assertions.assertNotNull(authorService.findTopAuthorByLendings());
    }

    @Test
    public void whenFindTopAuthorByLendingsWithAuthors_thenShouldReturnOrderedList(){
        List<AuthorLendingView> list = new ArrayList<AuthorLendingView>();

        AuthorLendingView a1 = new AuthorLendingView("Alex", 5L);
        AuthorLendingView a2 = new AuthorLendingView("Charles", 4L);
        AuthorLendingView a3 = new AuthorLendingView("Maria Alberta", 2L);
        AuthorLendingView a4 = new AuthorLendingView("Patrícia", 2L);
        AuthorLendingView a5 = new AuthorLendingView("João", 1L);

        list.add(a1);
        list.add(a2);
        list.add(a3);
        list.add(a4);
        list.add(a5);

        Page<AuthorLendingView> page = new PageImpl<>(list);

        Pageable pageableRules = PageRequest.of(0,5);
        when(authorRepository.findTopAuthorByLendings(pageableRules)).thenReturn(page);

        Assertions.assertNotNull(authorService.findTopAuthorByLendings());
        Assertions.assertEquals(5,authorService.findTopAuthorByLendings().size());
        Assertions.assertEquals(a1,authorService.findTopAuthorByLendings().get(0));
        Assertions.assertEquals(a2,authorService.findTopAuthorByLendings().get(1));
        Assertions.assertEquals(a3,authorService.findTopAuthorByLendings().get(2));
        Assertions.assertEquals(a4,authorService.findTopAuthorByLendings().get(3));
        Assertions.assertEquals(a5,authorService.findTopAuthorByLendings().get(4));
    }
}
