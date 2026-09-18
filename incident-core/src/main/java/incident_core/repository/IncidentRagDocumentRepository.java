package incident_core.repository;

import incident_core.entity.IncidentRagDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IncidentRagDocumentRepository
        extends JpaRepository<IncidentRagDocument, Long> {

    boolean existsByIncidentId(Long incidentId);

    @Modifying
    @Query(
        value = """
            INSERT INTO incident_rag_documents
                (incident_id, content, embedding, created_at)
            VALUES
                (:incidentId, :content, CAST(:embedding AS vector), :createdAt)
            """,
        nativeQuery = true
    )
    void insertWithEmbedding(
            @Param("incidentId") Long incidentId,
            @Param("content") String content,
            @Param("embedding") String embedding,
            @Param("createdAt") LocalDateTime createdAt
    );

    @Query(
        value = """
            SELECT *
            FROM incident_rag_documents
            WHERE embedding IS NOT NULL
            ORDER BY embedding <=> CAST(:embedding AS vector)
            LIMIT :limit
            """,
        nativeQuery = true
    )
    List<IncidentRagDocument> findSimilarIncidents(
            @Param("embedding") String embedding,
            @Param("limit") int limit
    );
}