package goit.library.book.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkInfoDTO {
    private String title;
    private List<String> author_name;
    private String description;
    private String key;


}