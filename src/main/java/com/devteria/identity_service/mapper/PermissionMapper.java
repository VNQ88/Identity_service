package com.devteria.identity_service.mapper;

import org.mapstruct.Mapper;

import com.devteria.identity_service.dto.request.PermissionRequest;
import com.devteria.identity_service.dto.response.PermissionRespone;
import com.devteria.identity_service.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionRespone toPermissionRespone(Permission permission);
}
