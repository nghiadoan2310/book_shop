package com.java_project.chat_service.repository;

import com.java_project.chat_service.entity.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    @Query("{'participants.userId'}")
    List<Conversation> findAllByParticipantIdsContains(String userId);

    Conversation findByParticipantsHash(String participant);
}
