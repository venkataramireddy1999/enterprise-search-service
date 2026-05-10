package com.nonsyncbobbal.enterprise_search_service.repository;

import com.nonsyncbobbal.enterprise_search_service.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
}
