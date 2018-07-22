package by.tut.alexander.kaa.library.book.repository;

import by.tut.alexander.kaa.library.book.repository.model.Book;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface BookRepository extends CrudRepository<Book, Long> {

    List<Book> findAll();

    Book getById(Long id);

    @Transactional
    void delete(Book book);

    @Transactional
    void deleteBookById(Long id);

    Integer countBooksByContentLink(String contentLink);

}
