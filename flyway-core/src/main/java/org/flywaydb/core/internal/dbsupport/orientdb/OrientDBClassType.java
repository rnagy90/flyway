/*
 * Copyright 2010-2017 Boxfuse GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flywaydb.core.internal.dbsupport.orientdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.Table;
import org.flywaydb.core.internal.util.jdbc.JdbcUtils;

/**
 * Represents a class type, because the orientdb represents the schemas with this kind of implementation. Currently there are two types available
 * from the OrientDB jdbc implementation: TABLE and SYSTEM_TABLE
 * @author rnagy
 */
public class OrientDBClassType extends Schema<OrientDBDbSupport> {

  /**
   * @param name - The name represents the table collection type
   */
  public OrientDBClassType(JdbcTemplate jdbcTemplate, OrientDBDbSupport dbSupport, String name) {
    super(jdbcTemplate, dbSupport, name);
  }

  @Override
  protected boolean doExists() throws SQLException {
    ResultSet resultSet = null;
    boolean typeFound = false;
    try {
      resultSet = jdbcTemplate.getMetaData().getTableTypes();
      while(resultSet.next()) {
        if (name.equals(resultSet.getString("TABLE_TYPE"))) {
          typeFound = true;
        }
      }
    } finally {
      JdbcUtils.closeResultSet(resultSet);
    }

    return typeFound;
  }

  @Override
  protected boolean doEmpty() throws SQLException {
    ResultSet resultSet = null;
    try {
      resultSet = jdbcTemplate.getMetaData().getTables(null, null, null, new String[] { name });
      return !resultSet.first();
    } finally {
      JdbcUtils.closeResultSet(resultSet);
    }
  }

  @Override
  protected void doCreate() throws SQLException {
    // Unable to create, because this is not an actual schema
  }

  @Override
  protected void doDrop() throws SQLException {
    // Unable to drop, because this is not an actual schema
  }

  @Override
  protected void doClean() throws SQLException {
    throw new SQLException("Unimplemented!");
  }

  @Override
  protected Table[] doAllTables() throws SQLException {
    List<String> typeSpecificClassNames = new ArrayList<String>();

    ResultSet resultSet = null;
    try {
      resultSet = jdbcTemplate.getMetaData().getTables(null, null, null, new String[] { name });
      while (resultSet.next()) {
        typeSpecificClassNames.add(resultSet.getString("TABLE_NAME"));
      }
    } finally {
      JdbcUtils.closeResultSet(resultSet);
    }

    Table[] tables = new Table[typeSpecificClassNames.size()];
    for (int i = 0; i < typeSpecificClassNames.size(); i++) {
      tables[i] = new OrientDBClass(jdbcTemplate, dbSupport, this, typeSpecificClassNames.get(i));
    }
    return tables;
  }

  @Override
  public Table getTable(String tableName) {
    return new OrientDBClass(jdbcTemplate, dbSupport, this, tableName);
  }

}
