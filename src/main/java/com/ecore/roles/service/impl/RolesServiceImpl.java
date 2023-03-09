package com.ecore.roles.service.impl;

import com.ecore.roles.client.model.Team;
import com.ecore.roles.exception.InvalidArgumentException;
import com.ecore.roles.exception.ResourceExistsException;
import com.ecore.roles.exception.ResourceNotFoundException;
import com.ecore.roles.model.Membership;
import com.ecore.roles.model.Role;
import com.ecore.roles.repository.MembershipRepository;
import com.ecore.roles.repository.RoleRepository;
import com.ecore.roles.service.RolesService;
import com.ecore.roles.service.TeamsService;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RolesServiceImpl implements RolesService {

    public static final String DEFAULT_ROLE = "Developer";

    private final RoleRepository roleRepository;
    private final MembershipRepository membershipRepository;
    private final TeamsService teamsService;

    @Autowired
    public RolesServiceImpl(
            RoleRepository roleRepository,
            MembershipRepository membershipRepository,
            TeamsService teamsService
            ) {
        this.roleRepository = roleRepository;
        this.membershipRepository = membershipRepository;
        this.teamsService = teamsService;
    }

    @Override
    public Role CreateRole(@NonNull Role r) {
        if (roleRepository.findByName(r.getName()).isPresent()) {
            throw new ResourceExistsException(Role.class);
        }
        return roleRepository.save(r);
    }

    @Override
    public Role GetRole(@NonNull UUID rid) {
        return roleRepository.findById(rid)
                .orElseThrow(() -> new ResourceNotFoundException(Role.class, rid));
    }

    @Override
    public List<Role> GetRoles() {
        return roleRepository.findAll();
    }

    @Override
    public List<Membership> filterRoles(@NonNull UUID userId, @NonNull UUID teamId) {
        if(teamsService.getTeam(teamId) == null){
            throw new ResourceNotFoundException(Team.class, teamId);
        }
        return membershipRepository.findByUserIdAndTeamId(userId, teamId).stream().collect(Collectors.toList());
    }

    private Role getDefaultRole() {
        return roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new IllegalStateException("Default role is not configured"));
    }
}
