package com.example.fromagiabackend.Service.Notification;

import com.example.fromagiabackend.Entity.Notification;
import com.example.fromagiabackend.Repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService{

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository _notificationRepository){
        notificationRepository = _notificationRepository;
    }
    @Override
    public void createNotification(String message, String createdBy) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setCreatedBy(createdBy);
        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }
}
