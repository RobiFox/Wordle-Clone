package me.robi.wordle.repositories;

import me.robi.wordle.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, String> {
    UserEntity findByUuid(@Param("uuid") String uuid);
}
