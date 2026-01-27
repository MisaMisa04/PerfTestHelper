package ru.koshkin.PerfTestHelper.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.koshkin.PerfTestHelper.Entities.JwtToken;
import ru.koshkin.PerfTestHelper.enums.TokenStatus;

public interface JwtTokenRepo extends CrudRepository<JwtToken, Long> {

    @Query("SELECT t.status FROM JwtToken t JOIN User u on u.id=t.userId WHERE u.username = :username AND t.token=:token")
    TokenStatus getTokenStatusByUserNameAndToken(@Param("username") String username, @Param("token") String token);

    JwtToken getByToken(String token);

}
