package goit.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDTO {
    private Long id;
    private UUID userId;
    private String username;
    private String firstName;
    private String searchType;
    private String searchTerm;
    private LocalDateTime timestamp;

}
