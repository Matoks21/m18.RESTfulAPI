package goit.library.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;


import java.util.List;
@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorInfoDTO {

    private String name;
    private String bio;
    private String key;
    private String top_work;
    private String description;
    private List<AuthorInfoDTO> docs;
    private String imageUrl;
}

