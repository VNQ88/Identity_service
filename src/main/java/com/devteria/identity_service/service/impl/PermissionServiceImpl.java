package com.devteria.identity_service.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devteria.identity_service.dto.request.PermissionRequest;
import com.devteria.identity_service.dto.response.PermissionRespone;
import com.devteria.identity_service.entity.Permission;
import com.devteria.identity_service.mapper.PermissionMapper;
import com.devteria.identity_service.repository.PermissionRepository;
import com.devteria.identity_service.service.PermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionServiceImpl implements PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @Override
    public PermissionRespone create(PermissionRequest permissionRequest) {
        Permission permission = permissionMapper.toPermission(permissionRequest);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionRespone(permission);
    }

    @Override
    public List<PermissionRespone> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionRespone).toList();
    }

    @Override
    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }
}
