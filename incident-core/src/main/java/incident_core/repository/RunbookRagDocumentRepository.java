package incident_core.repository;

import incident_core.entity.RunbookRagDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RunbookRagDocumentRepository
        extends JpaRepository<RunbookRagDocument, Long> {

    @Modifying
    @Query(
        value = """
            INSERT INTO runbook_rag_documents
                (title, content, embedding, created_at)
            VALUES
                (:title, :content, CAST(:embedding AS vector), :createdAt)
            """,
        nativeQuery = true
    )
    void insertWithEmbedding(
            @Param("title") String title,
            @Param("content") String content,
            @Param("embedding") String embedding,
            @Param("createdAt") LocalDateTime createdAt
    );

    @Query(
        value = """
            SELECT *
            FROM runbook_rag_documents
            WHERE embedding IS NOT NULL
            ORDER BY embedding <=> CAST(:embedding AS vector)
            LIMIT :limit
            """,
        nativeQuery = true
    )
    List<RunbookRagDocument> findSimilarRunbooks(
            @Param("embedding") String embedding,
            @Param("limit") int limit
    );
}