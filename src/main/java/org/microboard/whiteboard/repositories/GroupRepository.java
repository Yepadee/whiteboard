package org.microboard.whiteboard.repositories;

import org.microboard.whiteboard.model.project.GroupProject;
import org.microboard.whiteboard.model.user.Group;
import org.springframework.data.repository.CrudRepository;

public interface GroupRepository extends CrudRepository<Group, Long> {

}