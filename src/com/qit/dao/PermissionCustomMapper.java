package com.qit.dao;

import com.qit.pojo.Permission;
import com.qit.pojo.PermissionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PermissionCustomMapper {
	List<Permission> findPermissionListByUserId(String userId) throws Exception;

	List<Permission> findMenuListByUserId(String userId) throws Exception;
}