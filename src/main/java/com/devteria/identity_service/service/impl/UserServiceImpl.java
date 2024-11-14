package com.devteria.identity_service.service.impl;

import com.devteria.identity_service.constant.PredefinedRole;
import com.devteria.identity_service.dto.request.UserCreationRequest;
import com.devteria.identity_service.dto.request.UserUpdateRequest;
import com.devteria.identity_service.dto.response.PageRespone;
import com.devteria.identity_service.dto.response.UserRespone;
import com.devteria.identity_service.entity.Role;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.exception.AppException;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.mapper.RoleMapper;
import com.devteria.identity_service.mapper.UserMapper;
import com.devteria.identity_service.repository.RoleRepository;
import com.devteria.identity_service.repository.SearchRepository;
import com.devteria.identity_service.repository.UserRepository;
import com.devteria.identity_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.devteria.identity_service.constant.AppConst.SORT_BY;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    SearchRepository searchRepository;
    UserMapper userMapper;
    RoleMapper roleMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    @Override
    public UserRespone createUser(UserCreationRequest request) {
        log.info("Service: Create user");
        if (Boolean.TRUE.equals(userRepository.existsByUsername(request.getUsername())))
            throw new AppException(ErrorCode.USER_EXISTED);
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setGender(request.getGender());
        HashSet<Role> roles = new HashSet<>();
        if (!request.getRoles().isEmpty()){
            for(String role : request.getRoles()){
                if (roleRepository.existsById(role)){
                    roleRepository.findById(role).ifPresent(roles::add);
                }
                else {
                    log.error("Role " + role + " not existed.");
                    throw new AppException(ErrorCode.ROLES_NOT_EXISTED);
                }
            }
        }
        else
        {
            roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        }
        user.setRoles(roles);
        return userMapper.toUserRespone(userRepository.save(user));
    }

    @Override
    // @PreAuthorize(("hasAuthority('APPROVE_POST')"))
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserRespone> getUser() {
        return userRepository.findAll().stream().map(userMapper::toUserRespone).toList();
    }

    @Override
    public PageRespone<?> getAllUserWithSortBy(int pageNo, int pageSize, String sortBy) {
        List<Sort.Order> sorts = new ArrayList<>();

        if(StringUtils.hasLength(sortBy)){
            Pattern pattern = Pattern.compile(SORT_BY);
            Matcher matcher = pattern.matcher(sortBy);

            if (matcher.find()){
                if (matcher.group(3).equalsIgnoreCase("asc")){
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                }
                else if (matcher.group(3).equalsIgnoreCase("desc")){
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
                else {
                    throw new AppException(ErrorCode.INVALID_KEY);
                }
            }
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sorts));
        Page<User> users = userRepository.findAll(pageable);
        return convertToPageRespone(users, pageable);
    }

    @Override
    public PageRespone<?> getAllUserWithSortByMultipleColumn(int pageNo, int pageSize, String... orders) {
        List<Sort.Order> sorts = new ArrayList<>();

        for (String sortBy : orders){
            if (StringUtils.hasLength(sortBy)){
                Pattern pattern = Pattern.compile(SORT_BY);
                Matcher matcher = pattern.matcher(sortBy);

                if (matcher.find()){
                    if (matcher.group(3).equalsIgnoreCase("asc")){
                        sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                    }
                    else if (matcher.group(3).equalsIgnoreCase("desc")){
                        sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                    }
                    else {
                        throw new AppException(ErrorCode.INVALID_KEY);
                    }
                }
            }
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sorts));
        Page<User> users = userRepository.findAll(pageable);
        return convertToPageRespone(users, pageable);
    }


    /*
    * Convert Page<User> to PageResponse
    *
    * @param users
    * @param pageable
    * @return
    * */
    private PageRespone<?> convertToPageRespone(Page<User> users, Pageable pageable) {
        List<UserRespone> userRespones = users.stream()
                .map(user -> UserRespone
                        .builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .dob(user.getDob())
                        .gender(String.valueOf(user.getGender()))
                        .userStatus(String.valueOf(user.getUserStatus()))
                        .roles(user.getRoles().stream().map(roleMapper::toRoleRespone).collect(Collectors.toSet()))
                        .build())
                .toList();

        return PageRespone.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(users.getTotalPages())
                .totalElements(users.getTotalElements())
                .items(userRespones)
                .build();
    }

    @Override
    public UserRespone getMyInfo() {
        var context = SecurityContextHolder.getContext();
        val name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserRespone(user);
    }

    @PostAuthorize("returnObject.username == authentication.name")
    @Override
    public UserRespone getUserById(String id) {
        return userMapper.toUserRespone(
                userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.details")
    @Override
    public UserRespone updateUser(String id, UserUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authentication: " + authentication.toString());
        User oldUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUser(oldUser, request);
        oldUser.setPassword(passwordEncoder.encode(request.getPassword()));
        oldUser.setRoles(new HashSet<>(roleRepository.findAllById(request.getRoles())));
        return userMapper.toUserRespone(userRepository.save(oldUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public PageRespone<?> getAllUserWithSortByColumnAndSearch(int pageNo, int pageSize, String sortBy, String search) {
        return searchRepository.getAllUserWithSortByColumnAndSearch(pageNo, pageSize, sortBy, search);
    }

}
