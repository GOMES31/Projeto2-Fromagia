package com.example.fromagiabackend.Service.Notification;

import com.example.fromagiabackend.Entity.Notification;

import java.util.List;

public interface NotificationService {

    void createNotification(String message, String createdBy);

    List<Notification> findAll();
}
