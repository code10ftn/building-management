package com.code10.kts.repository;

import com.code10.kts.model.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    <E extends User> Optional<E> findById(long id);

    <E extends User> Optional<E> findByUsernameIgnoreCase(String username);

    <E extends User> Optional<E> findByEmail(String email);

    <E extends User> E save(E user);
}
