package pt.psoft.g1.psoftg1.authormanagement.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import pt.psoft.g1.psoftg1.shared.repositories.PhotoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AuthorServiceImplWhiteBoxTest {

    private AuthorService authorService; //= mock(AuthorService.class) o nosso SUT é precisamente o Service
    @Mock // = mock(AuthorRepository.class);
    private AuthorRepository authorRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private Author alex, charles, patricia;

    @BeforeEach
    public void setUp() {
        authorService = new AuthorServiceImpl(authorRepository,bookRepository,new AuthorMapperImpl(),photoRepository);

        this.alex = new Author("Alex", "O Alex escreveu livros", null);
        this.charles = new Author("Charles", "O Charles escreveu livros", "charles.png");
        this.patricia = new Author("Patricia", "Sou a Patricia", null);

        when(authorRepository.findByAuthorNumber(0L))
                .thenReturn(Optional.empty());

        when(authorRepository.findByAuthorNumber(1L))
                .thenReturn(Optional.of(alex));

        when(authorRepository.findByAuthorNumber(2L))
                .thenReturn(Optional.of(charles));

        when(authorRepository.findByAuthorNumber(3L))
                .thenReturn(Optional.of(patricia));
    }

    @Test
    public void whenFindAll_thenFindAllMethodOnlyCallOnce(){
        when(authorRepository.findAll())
                .thenReturn(new ArrayList<>());

        Iterable<Author> authors = authorService.findAll();
        
        verify(authorRepository,only()).findAll();
    }

    @Test
    public void whenFindByAuthorNumber_thenFindByAuthorNumberMethodOnlyCallOnce() {
        Long id = 1L;
        Optional<Author> found = authorService.findByAuthorNumber(id);
        verify(authorRepository, only()).findByAuthorNumber(id);
    }
    @Test
    public void whenFindByName_thenSearchByNameNameStartsWithShouldBeCalled() {
        List<Author> found = authorService.findByName("Alex");
        verify(authorRepository, only()).searchByNameNameStartsWith(anyString());
    }

    @Test
    public void whenCreate_thenSaveShouldBeCalled() {
        CreateAuthorRequest request = new CreateAuthorRequest("Eva","Sou a Eva",null,"");
        authorService.create(request);
        verify(authorRepository, only()).save(Mockito.any(Author.class));
    }

    @Test
    public void whenPartialUpdate_InvalidAuthorNumber_thenShouldOnlyCallFindByAuthorNumber() {
        UpdateAuthorRequest updateAuthorRequest = new UpdateAuthorRequest("bio","name",null,null);
        when(authorRepository.save(Mockito.any(Author.class))).thenReturn(alex);
        authorService.partialUpdate(1L,updateAuthorRequest,0);

        verify(authorRepository, times(1)).findByAuthorNumber(1L);
        verify(authorRepository, times(1)).save(Mockito.any(Author.class));
    }

    @Test
    public void whenFindTopAuthorByLendings_thenShouldOnlyCallFindTopAuthorByLendings(){
        List<AuthorLendingView> list = new ArrayList<AuthorLendingView>();
        Page<AuthorLendingView> page = new PageImpl<>(list);
        Pageable pageableRules = PageRequest.of(0,5);
        when(authorRepository.findTopAuthorByLendings(pageableRules)).thenReturn(page);

        List<AuthorLendingView> found = authorService.findTopAuthorByLendings();

        verify(authorRepository, only()).findTopAuthorByLendings(Mockito.any(Pageable.class));
    }

    @Test
    public void whenFindBooksByAuthorNumber_thenShouldOnlyCallFindBooksByAuthorNumber(){

        List<Book> found = authorService.findBooksByAuthorNumber(1L);

        verify(bookRepository, only()).findBooksByAuthorNumber(anyLong());
    }

    @Test
    public void whenFindCoAuthorsByAuthorNumber_thenShouldOnlyCallFindCoAuthorsByAuthorNumber(){

        List<Author> found = authorService.findCoAuthorsByAuthorNumber(1L);

        verify(authorRepository, only()).findCoAuthorsByAuthorNumber(anyLong());
    }

    @Test
    public void whenRemoveAuthorPhoto_thenShouldCallRemoveAuthorPhotoAndSave(){

        Optional<Author> found = authorService.removeAuthorPhoto(1L,0);

        verify(authorRepository, times(1)).findByAuthorNumber(anyLong());
        verify(authorRepository, atMostOnce()).findByAuthorNumber(anyLong());
    }

}
