package com.ocena.qlsc.user.repository;

import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.user.model.Role;
import com.ocena.qlsc.user.model.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepository extends BaseRepository<Role> {
}
