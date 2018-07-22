package by.tut.alexander.kaa.library.book.service.modelDTO;


import javax.validation.constraints.*;

public class BookDTO {
    private Long id;
    @NotNull
    private String author;
    @NotNull
    @Pattern(regexp = "[A-Za-zа-яёА-ЯЁ\\s]+")
    private String title;
    @NotNull
    @Min(1800)
    @Max(2018)
    private Integer year;
    @NotNull
    @Size(max = 255)
    private String description;
    private String contentLink;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContentLink() {
        return contentLink;
    }

    public void setContentLink(String contentLink) {
        this.contentLink = contentLink;
    }
}
