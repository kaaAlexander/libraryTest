package by.tut.alexander.kaa.library.book.service.ServiceImpl;

import by.tut.alexander.kaa.library.book.repository.BookRepository;
import by.tut.alexander.kaa.library.book.repository.model.Book;
import by.tut.alexander.kaa.library.book.service.BookService;
import by.tut.alexander.kaa.library.book.service.modelDTO.BookDTO;
import by.tut.alexander.kaa.library.book.service.util.BookConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class BookServiceImpl implements BookService {

    private BookConverter bookConverter = new BookConverter();
    private Path path = Paths.get(System.getProperty("user.dir") + File.separator + "storage");

    @Autowired
    BookRepository bookRepository;

    @Override
    public List<BookDTO> findAllBooks() {
        List<Book> bookList = bookRepository.findAll();
        List<BookDTO> bookDTOList = new ArrayList<>();
        for (Book book : bookList) {
            BookDTO bookDTO = bookConverter.convert(book);
            bookDTOList.add(bookDTO);
        }
        return bookDTOList;
    }

    @Override
    @Cacheable(value = "books", key = "#id")
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.getById(id);
        return bookConverter.convert(book);
    }

    @Override
    public void deleteBookById(Long id, String contentLink) {
        bookRepository.deleteBookById(id);
        deleteBookContent(contentLink);
    }

    @Override
    public void addBook(BookDTO bookDTO, MultipartFile file) {
        byte[] buffer = new byte[1024];
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Path filePath = Paths.get(path.toString() + File.separator + file.getOriginalFilename() + ".zip");
        if (Files.exists(filePath)) {
            String addToFileName = new SimpleDateFormat("hhmmssddMMyy").format(new Date());
            filePath = Paths.get(path.toString() + File.separator + file.getOriginalFilename() + addToFileName + ".zip");
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath.toString());
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
            zipOutputStream.setLevel(Deflater.BEST_COMPRESSION);
            ZipEntry zipEntry = new ZipEntry(file.getOriginalFilename());
            zipOutputStream.putNextEntry(zipEntry);
            FileInputStream inputStream = (FileInputStream) file.getInputStream();
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, length);
            }
            inputStream.close();
            zipOutputStream.closeEntry();
            zipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bookDTO.setContentLink(filePath.toString());
        Book book = bookConverter.convert(bookDTO);
        bookRepository.save(book);
    }


    @Override
    public Resource loadContent(String contentLink) {
        String unzipFile = unZipContent(contentLink);
        try {
            Path file = path.resolve(unzipFile);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Loading failed!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            deleteBookContent(unzipFile);
        }
    }

    @Override
    public void updateBook(BookDTO bookDTO) {
        Book book = bookConverter.convert(bookDTO);
        bookRepository.save(book);
    }

    @Override
    public void updateBook(BookDTO bookDTO, MultipartFile file, String oldLink) {
        addBook(bookDTO, file);
        deleteBookContent(oldLink);
    }

    private String unZipContent(String contentLink) {
        byte[] buffer = new byte[1024];
        String filePath = null;
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(contentLink));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(String.valueOf(path) + File.separator + fileName);
                filePath = newFile.getAbsolutePath();
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fileOutputStream = new FileOutputStream(newFile);
                int length;
                while ((length = zipInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }
                fileOutputStream.close();
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.closeEntry();
            zipInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    private void deleteBookContent(String contentLink) {
        if (bookRepository.countBooksByContentLink(contentLink) == 0) {
            File file = new File(contentLink);
            try {
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
