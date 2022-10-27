package com.example.careerblog.Repository;

import org.springframework.data.repository.CrudRepository;

import com.example.careerblog.Models.Message;

public interface MessageRepository extends CrudRepository<Message, Long> {
	
	Iterable<Message> findByTag(String tag); //method to find messages by tags
	
}
