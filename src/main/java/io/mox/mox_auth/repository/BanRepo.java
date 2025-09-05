package io.mox.mox_auth.repository;

import io.mox.mox_auth.model.BannedNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BanRepo extends JpaRepository<BannedNote, Long> {
    List<BannedNote> findAllByTarget(String target);
    List<BannedNote> findAllByType(String type);
}
