package net.codejava.repository;

import net.codejava.entity.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepo extends CrudRepository<Message, Long>
{

    Iterable<Message> findByTag(String filter);
}
