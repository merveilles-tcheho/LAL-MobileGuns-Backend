package com.nlmk.LAL.MobileGuns.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;
import javax.sql.rowset.serial.SQLInputImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nlmk.LAL.MobileGuns.entity.Role;
import com.nlmk.LAL.MobileGuns.tools.MyTools;

import oracle.jdbc.OracleTypes;

@Service
public class UserRoleServiceImpl implements UserRoleService {

	@Autowired
	DataSource dataSource;

	@Override
	public List<Role> getUserRoles(String userId) throws Exception {
		Connection con = null;
		CallableStatement stm = null;

		List<Role> roleList = new ArrayList<Role>();

		try {
			con = dataSource.getConnection();

			stm = con.prepareCall("{? = call UTILITIES.PKG001_MENU_UTIL.FCT_GET_USER_GROUPS (?, ?)}");

			stm.registerOutParameter(1, OracleTypes.ARRAY, "UTILITIES.TYP_TABLE_USER_GROUPS");

			stm.setString(2, userId);
			stm.setString(3, "GESFAB_WEB");

			stm.execute();

			Object[] objectList = (Object[]) stm.getArray(1).getArray();

			for (Object object : objectList) {

				Struct struct = (Struct) object;

				Role role = new Role();

				role.readSQL(new SQLInputImpl(struct.getAttributes(), new HashMap<String, Class<?>>()),
						struct.getSQLTypeName());

				roleList.add(role);
			}
		} catch (Exception e) {
			MyTools.logError("UserRoleServiceImpl - getUserRoles exception: " + e.getMessage(), e);

			throw e;
		} finally {
			try {
				if (stm != null)
					stm.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				MyTools.logError("UserRoleServiceImpl - getUserRoles exception: " + e.getMessage(), e);

				throw e;
			}
		}

		return roleList;
	}

}
