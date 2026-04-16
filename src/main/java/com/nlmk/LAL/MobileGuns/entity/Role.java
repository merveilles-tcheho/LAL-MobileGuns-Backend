package com.nlmk.LAL.MobileGuns.entity;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

import lombok.Data;

@Data

public class Role implements SQLData {

	private String sqlType;
	private String role;

	@Override
	public String getSQLTypeName() throws SQLException {
		return "UTILITIES.TYP_USER_GROUPS";
	}

	@Override
	public void readSQL(SQLInput stream, String typeName) throws SQLException {
		sqlType = typeName;

		role = stream.readString();
	}

	@Override
	public void writeSQL(SQLOutput stream) throws SQLException {
		// TODO Auto-generated method stub

	}

}
