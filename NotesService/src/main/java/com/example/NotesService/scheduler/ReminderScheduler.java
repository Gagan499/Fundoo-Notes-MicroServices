package com.example.NotesService.scheduler;

import com.example.NotesService.service.ReminderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReminderScheduler {

    private final ReminderService reminderService;

    public ReminderScheduler(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @Scheduled(fixedDelayString = "${notes.reminders.scheduler-interval-ms}")
    public void publishDueReminders() {
        reminderService.publishDueReminders();
    }
}
