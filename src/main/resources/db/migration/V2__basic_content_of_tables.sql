--V2__content__tables

-- Вставка ролей
INSERT INTO role (id, name) VALUES ('a9731fb3-36bf-4ae2-9e70-9277fae845f1', 'ADMIN');
INSERT INTO role (id, name) VALUES ('b9731fb3-36bf-4ae2-9e70-9277fae845f2', 'USER');

-- Вставка логін : admin@gmail.com   пароль : admin
INSERT INTO "user" (id, email, password, first_name, last_name)
VALUES ('a206b559-2f75-4102-9b19-7b4710e12892', 'admin@gmail.com', '{bcrypt}$2a$10$oz4scNUI1l7O.sqB0sBonu/ovNEhi2ijYjhaflG2u7RQFDOwVjA4q', 'Oksana', 'Matviichuk');

--логін : user@gmail.com  пароль : jdbcDefault
INSERT INTO "user" (id, email, password, first_name, last_name)
VALUES ('b206b559-2f75-4102-9b19-7b4710e12893', 'user@gmail.com', '{bcrypt}$2a$10$cwf8Fj3R4q5rKcux4wW40erRaCbbYmC1ayT/d54JkYxfad40LpJHS', 'User', 'User');

-- Призначення ролей користувачам
INSERT INTO user_role (user_id, role_id) VALUES ('a206b559-2f75-4102-9b19-7b4710e12892', 'a9731fb3-36bf-4ae2-9e70-9277fae845f1');
INSERT INTO user_role (user_id, role_id) VALUES ('b206b559-2f75-4102-9b19-7b4710e12893', 'b9731fb3-36bf-4ae2-9e70-9277fae845f2');


