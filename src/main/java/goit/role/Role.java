package goit.role;



import goit.user.User;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Table(name = "role")
@Data
@Entity
public class Role {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @NonNull
    @Column(name = "name")
    private String name;

    @ManyToMany(
            targetEntity = User.class,
            mappedBy = "roles",
            cascade = {CascadeType.REMOVE})
    private static Set<User> users = new HashSet<>();

    public Role() {
    }
}