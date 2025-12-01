package com.reading.ms_catalog.entity;


import com.reading.ms_catalog.service.BookUpdate;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Book")
@Table(name = "books")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")

public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private int pages;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "publication_year")
    private int publicationYear;


    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "finished", nullable = false)
    private Boolean finished = false;

    @Column(name = "origin_country")
    private String originCountry;

    public void update(BookUpdate date) {
        if(date.pages() != 0){
            this.pages = date.pages();
        }

        if (date.publicationYear() != 0 && String.valueOf(date.publicationYear()).length() <= 4){
            this.publicationYear = date.publicationYear();
        }
    }
}