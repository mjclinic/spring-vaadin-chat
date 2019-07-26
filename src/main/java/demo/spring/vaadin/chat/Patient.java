package demo.spring.vaadin.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "patients")
public class Patient {

    @Id
    private String id;

    private String name;

    private int zumin;

    private String tel;

    private LocalDate lastday;

    private String farday;


}