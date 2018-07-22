package by.tut.alexander.kaa.library.book.service;

import by.tut.alexander.kaa.library.book.repository.model.Book;
import by.tut.alexander.kaa.library.book.service.modelDTO.BookDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookService {

    List<BookDTO> findAllBooks();

    BookDTO getBookById(Long id);

    void deleteBookById(Long id, String contenLink);

    void addBook(BookDTO bookDTO, MultipartFile file);

    void updateBook(BookDTO bookDTO);

    void updateBook(BookDTO bookDTO, MultipartFile file,String oldLink);

    Resource loadContent(String contentLink);
}
