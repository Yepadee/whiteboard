package org.microboard.whiteboard.repositories;

import java.util.Optional;

import org.microboard.whiteboard.model.user.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
	Optional<User> findByUserName(String userName);
	
}