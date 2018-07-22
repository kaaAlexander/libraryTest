package by.tut.alexander.kaa.library.book.service.util;

import by.tut.alexander.kaa.library.book.repository.model.Book;
import by.tut.alexander.kaa.library.book.service.modelDTO.BookDTO;

public class BookConverter {

    public Book convert(BookDTO bookDTO) {
        Book book = new Book();
        book.setId(bookDTO.getId());
        book.setAuthor(bookDTO.getAuthor());
        book.setTitle(bookDTO.getTitle());
        book.setYear(bookDTO.getYear());
        book.setDescription(bookDTO.getDescription());
        book.setContentLink(bookDTO.getContentLink());
        return book;
    }

    public BookDTO convert(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setYear(book.getYear());
        bookDTO.setDescription(book.getDescription());
        bookDTO.setContentLink(book.getContentLink());
        return bookDTO;
    }
}
