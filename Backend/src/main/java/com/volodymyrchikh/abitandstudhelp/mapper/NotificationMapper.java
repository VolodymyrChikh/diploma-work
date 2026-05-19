package com.volodymyrchikh.abitandstudhelp.mapper;

import com.volodymyrchikh.abitandstudhelp.domain.Notification;
import com.volodymyrchikh.abitandstudhelp.dto.NotificationRequest;
import com.volodymyrchikh.abitandstudhelp.dto.NotificationResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NotificationMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "type", expression = "java(com.volodymyrchikh.abitandstudhelp.common.NotificationType.forValue(request.getType()))")
    @Mapping(target = "isRead", expression = "java(request.getIsRead() != null ? request.getIsRead() : false)")
    Notification toEntity(NotificationRequest request);

    NotificationResponse toResponse(Notification notification);

    void updateEntity(NotificationRequest notificationRequest, @MappingTarget Notification notification);
}
