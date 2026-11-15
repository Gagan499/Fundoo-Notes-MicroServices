package com.example.NotesService.repository;

import com.example.NotesService.entity.NoteReminder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteReminderRepository extends JpaRepository<NoteReminder, Long> {
    List<NoteReminder> findByUserIdOrderByReminderTimeAsc(Long userId);

    Optional<NoteReminder> findByReminderIdAndUserId(Long reminderId, Long userId);

    List<NoteReminder> findByNotifiedFalseAndReminderTimeLessThanEqual(LocalDateTime reminderTime);
}
