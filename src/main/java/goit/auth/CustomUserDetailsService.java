package goit.auth;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDate userDate = getByIdOrNull(username);
        if (userDate == null) {
            throw new UsernameNotFoundException(username);
        }

        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Arrays.stream(userDate.getAuthority().split(","))
                        .map(it -> (GrantedAuthority) () -> it)
                        .collect(Collectors.toList());
            }

            @Override
            public String getPassword() {
                return userDate.getPassword();
            }

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }


    private UserDate getByIdOrNull(String email) {
        String sql =
                "SELECT T1.password, STRING_AGG(T3.name,',') AS authority " +
                        "FROM \"user\" AS T1 " +
                        "JOIN user_role AS T2 ON T1.id=T2.user_id " +
                        "JOIN role AS T3 ON T2.role_id=T3.id " +
                        "WHERE T1.email = :email " +
                        "GROUP BY T1.password";
        return jdbcTemplate.queryForObject(
                sql,
                Map.of("email", email),
                new UserDataMapper()
        );
    }


    private static class UserDataMapper implements RowMapper<UserDate> {
        @Override
        public UserDate mapRow(ResultSet rs, int rowNum) throws SQLException {
            return UserDate.builder()
                    .password(rs.getString("password"))
                    .authority(rs.getString("authority"))
                    .build();
        }
    }

    @Builder
    @Data
    private static class UserDate {
        private String password;
        private String authority;
    }
}