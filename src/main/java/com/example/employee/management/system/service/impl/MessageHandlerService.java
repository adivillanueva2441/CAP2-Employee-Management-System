package com.example.employee.management.system.service.impl;

import com.example.employee.management.system.service.IMessageHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MessageHandlerService implements IMessageHandlerService {

    @Autowired
    private MessageSource messageSource;

    public String get(String key, Object... args) {
        return messageSource.getMessage(key, args, Locale.getDefault());
    }
}
