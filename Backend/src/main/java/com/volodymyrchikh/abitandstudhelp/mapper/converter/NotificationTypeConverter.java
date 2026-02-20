package com.volodymyrchikh.abitandstudhelp.mapper.converter;

import com.volodymyrchikh.abitandstudhelp.common.NotificationType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class NotificationTypeConverter implements AttributeConverter<NotificationType, String> {

    @Override
    public String convertToDatabaseColumn(NotificationType notificationType) {
        if (notificationType == null) {
            return null;
        }
        return notificationType.getName();
    }

    @Override
    public NotificationType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return NotificationType.forValue(dbData);
    }
}