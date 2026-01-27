package ru.koshkin.PerfTestHelper.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.koshkin.PerfTestHelper.Entities.User;

import java.util.Optional;

public interface UserRepo extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
