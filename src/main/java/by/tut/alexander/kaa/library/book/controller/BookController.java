package by.tut.alexander.kaa.library.book.controller;


import by.tut.alexander.kaa.library.book.service.BookService;
import by.tut.alexander.kaa.library.book.service.modelDTO.BookDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;

@Controller
public class BookController {

    @Autowired
    BookService bookService;

    @GetMapping("/")
    public String findAllBooks(Model model) {
        List<BookDTO> bookDTOList = bookService.findAllBooks();
        model.addAttribute("books", bookDTOList);
        return "books";
    }

    @GetMapping("/viewBook")
    public String finBookById(@RequestParam("id") Long id, Model model) {
        BookDTO bookDTO = bookService.getBookById(id);
        model.addAttribute("book", bookDTO);
        return "viewBook";
    }

    @GetMapping("/deleteBook")
    public String deleteBookById(@RequestParam("id") Long id, @RequestParam("contentLink") String contentLink) {
        bookService.deleteBookById(id, contentLink);
        return "redirect:/";
    }

    @GetMapping("/addBook")
    public String newBookPage(Model model) {
        model.addAttribute("book", new BookDTO());
        return "addBook";
    }

    @PostMapping("/addBook")
    public String createNewBook(@ModelAttribute("book") @Valid BookDTO bookDTO, BindingResult bindingResult,
                                @RequestParam("file") MultipartFile file) {
        if (bindingResult.hasErrors()) {
            return "addBook";
        }
        bookService.addBook(bookDTO, file);
        return "redirect:/";
    }

    @GetMapping(path = "/download")
    public ResponseEntity<Resource> downloadFile(@PathParam("contentLink") String contentLink) {
        Resource file = bookService.loadContent(contentLink);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @GetMapping("/editBook")
    public String editBook(@RequestParam("id") Long id, Model model) {
        BookDTO bookDTO = bookService.getBookById(id);
        model.addAttribute("book", bookDTO);
        return "editBook";
    }

    @PostMapping("/editBook")
    public String saveBookChanges(@ModelAttribute("book") @Valid BookDTO bookDTO, BindingResult bindingResult, @RequestParam(value = "file", required = false) MultipartFile file,
                                  @RequestParam("oldLink") String oldLink) {
        if (bindingResult.hasErrors()) {
            return "editBook";
        }
        if (file.isEmpty()) {
            bookService.updateBook(bookDTO);
        } else {
            bookService.updateBook(bookDTO, file, oldLink);
        }
        return "redirect:/";
    }
}

