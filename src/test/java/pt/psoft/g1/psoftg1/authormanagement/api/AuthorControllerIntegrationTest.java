package pt.psoft.g1.psoftg1.authormanagement.api;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.services.AuthorService;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.usermanagement.model.User;

import java.time.Instant;
import java.util.*;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Based on https://www.baeldung.com/spring-boot-testing
 * <p>Adaptations to Junit 5 with ChatGPT
 */

@ExtendWith(MockitoExtension.class)
//@WebMvcTest(AuthorController.class)
@AutoConfigureMockMvc
@SpringBootTest
//@ActiveProfiles("integrationtest")
public class AuthorControllerIntegrationTest {

    private AuthorService authorService;
    private final MockMvc mockMvc;
    private JwtEncoder jwtEncoder;
    private User librarian;
    private User reader;
    private Author author1 = mock(Author.class);
    private String librarianToken;
    private String readerToken;
    private Book book1;
    private List<Book> bookList = new ArrayList<>();

    @Autowired
    public AuthorControllerIntegrationTest(MockMvc mockMvc, JwtEncoder encoder) {
        this.mockMvc = mockMvc;
        this.jwtEncoder = encoder;
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        librarian = new User("maria@gmail.com","Mariaroberta!123");
        reader = new User("pedro@gmail.com","Pedro!123");
        //author1 = new Author("Robert", "Hello, I'm Robert", "");
        final Instant now = Instant.now();
        final long expiry = 36000L; // 1 hours is usually too long for a token to be valid. adjust for production

        final JwtClaimsSet librarianClaims = JwtClaimsSet.builder().issuer("example.io").issuedAt(now)
                .expiresAt(now.plusSeconds(expiry)).subject(format("%s,%s", librarian.getId(), librarian.getUsername()))
                .claim("roles", "LIBRARIAN").build();

        final JwtClaimsSet readerClaims = JwtClaimsSet.builder().issuer("example.io").issuedAt(now)
                .expiresAt(now.plusSeconds(expiry)).subject(format("%s,%s", reader.getId(), reader.getUsername()))
                .claim("roles", "READER").build();

        librarianToken= this.jwtEncoder.encode(JwtEncoderParameters.from(librarianClaims)).getTokenValue();
        readerToken= this.jwtEncoder.encode(JwtEncoderParameters.from(readerClaims)).getTokenValue();

    }

    @Test
    void shouldCreateMockMvc(){
        assertNotNull(mockMvc);
    }

    // As Reader

    @Test
    void FindBooksByAuthorNumber() throws Exception {
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/authors/3/books")
                                .header("Authorization", "Bearer " + readerToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.not(Matchers.emptyArray())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize(1)));
    }

    @Test
    void GetAuthorById_AuthorDoesntExist() throws Exception {
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/authors/30")
                                .header("Authorization", "Bearer " + readerToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void GetAuthorById() throws Exception {
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/authors/3")
                                .header("Authorization", "Bearer " + readerToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorNumber",Matchers.is(3)));
    }

    /*
    @MockBean
    private AuthorService authorService;
    private final MockMvc mockMvc;
    private JwtEncoder jwtEncoder;
    private User librarian;
    private User reader;
    private Author author1 = mock(Author.class);
    private String librarianToken;
    private String readerToken;
    private Book book1;
    private List<Book> bookList = new ArrayList<>();

    @Autowired
    public AuthorControllerIntegrationTest(MockMvc mockMvc, JwtEncoder encoder) {
        this.mockMvc = mockMvc;
        this.jwtEncoder = encoder;
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        librarian = new User("maria@gmail.com","Mariaroberta!123");
        reader = new User("pedro@gmail.com","Pedro!123");
        //author1 = new Author("Robert", "Hello, I'm Robert", "");
        when(author1.getAuthorNumber()).thenReturn(1L);
        final Instant now = Instant.now();
        final long expiry = 36000L; // 1 hours is usually too long for a token to be valid. adjust for production

        final JwtClaimsSet librarianClaims = JwtClaimsSet.builder().issuer("example.io").issuedAt(now)
                .expiresAt(now.plusSeconds(expiry)).subject(format("%s,%s", librarian.getId(), librarian.getUsername()))
                .claim("roles", "LIBRARIAN").build();

        final JwtClaimsSet readerClaims = JwtClaimsSet.builder().issuer("example.io").issuedAt(now)
                .expiresAt(now.plusSeconds(expiry)).subject(format("%s,%s", reader.getId(), reader.getUsername()))
                .claim("roles", "READER").build();

        librarianToken= this.jwtEncoder.encode(JwtEncoderParameters.from(librarianClaims)).getTokenValue();
        readerToken= this.jwtEncoder.encode(JwtEncoderParameters.from(readerClaims)).getTokenValue();
        List<Author> b1AL = new ArrayList<>();
        b1AL.add(author1);
        book1 = new Book("9782722203426", "Book1", "Book1", new Genre("Punk"),b1AL,"");
        bookList.add(book1);
    }

    @Test
    void shouldCreateMockMvc(){
        assertNotNull(mockMvc);
    }

    // As Reader

    @Test
    void getBooksByAuthorNumber_AuthorNotFound() throws Exception {
        when(authorService.findByAuthorNumber(1L)).thenThrow(new NotFoundException(Author.class, 1L));
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/api/authors/" + author1.getAuthorNumber()+"/books")
                        .header("Authorization", "Bearer " + readerToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getBooksByAuthorNumber_Reader_EmptyAuthorBooks() throws Exception {
        when(authorService.findByAuthorNumber(author1.getAuthorNumber())).thenReturn(Optional.ofNullable(author1));
        when(authorService.findBooksByAuthorNumber(author1.getAuthorNumber())).thenReturn(new ArrayList<>());
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/authors/" + author1.getAuthorNumber()+"/books")
                                .header("Authorization", "Bearer " + readerToken))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getBooksByAuthorNumber_Reader_AuthorBooks() throws Exception {
        when(authorService.findByAuthorNumber(author1.getAuthorNumber())).thenReturn(Optional.ofNullable(author1));
        when(authorService.findBooksByAuthorNumber(author1.getAuthorNumber())).thenReturn(bookList);
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/authors/" + author1.getAuthorNumber()+"/books")
                                .header("Authorization", "Bearer " + readerToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.not(Matchers.emptyArray())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].title", Matchers.is(book1.getTitle().getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].description", Matchers.is(book1.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].isbn", Matchers.is(book1.getIsbn())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].genre", Matchers.is(book1.getGenre().getGenre())))
        ;
    }


    @Test
    void getAuthorByIdShouldReturnUnauthorized() throws Exception {
        when(authorService.findByAuthorNumber(author1.getAuthorNumber())).thenReturn(Optional.ofNullable(author1));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/authors/" + author1.getAuthorNumber()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

@Test
    void shouldReturnAuthorById() throws Exception {
        when(authorService.findByAuthorNumber(author1.getAuthorNumber())).thenReturn(Optional.ofNullable(author1));
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/authors/1")
                                .header("Authorization", "Bearer " + readerToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorNumber", Matchers.is(author1.getAuthorNumber().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bio", Matchers.is(author1.getBio())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(author1.getName())));

    }
*/
    // As Librarian
}
