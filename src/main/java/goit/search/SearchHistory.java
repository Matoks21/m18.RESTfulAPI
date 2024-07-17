package goit.search;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@RequiredArgsConstructor
@Entity
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID userId;
    private String username;
    private String firstName;
    private String searchType;
    private String searchTerm;
    private LocalDateTime timestamp;

}
