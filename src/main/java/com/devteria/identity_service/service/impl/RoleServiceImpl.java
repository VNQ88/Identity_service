package com.devteria.identity_service.service.impl;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.devteria.identity_service.dto.request.RoleRequest;
import com.devteria.identity_service.dto.response.RoleRespone;
import com.devteria.identity_service.entity.Role;
import com.devteria.identity_service.mapper.RoleMapper;
import com.devteria.identity_service.repository.PermissionRepository;
import com.devteria.identity_service.repository.RoleRepository;
import com.devteria.identity_service.service.RoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionRepository permissionRepository;

    @Override
    public RoleRespone create(RoleRequest roleRequest) {
        Role role = roleMapper.toRole(roleRequest);
        var permissions = permissionRepository.findAllById(roleRequest.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        role = roleRepository.save(role);
        return roleMapper.toRoleRespone(role);
    }

    @Override
    public List<RoleRespone> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleRespone).toList();
    }

    @Override
    public void delete(String role) {
        roleRepository.deleteById(role);
    }
}
