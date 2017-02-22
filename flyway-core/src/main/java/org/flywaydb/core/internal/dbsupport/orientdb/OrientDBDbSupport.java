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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import org.flywaydb.core.internal.util.jdbc.JdbcUtils;

public class OrientDBDbSupport extends DbSupport {

  public OrientDBDbSupport(Connection connection) {
    super(new JdbcTemplate(connection, Types.NULL));
  }

  @Override
  public boolean catalogIsSchema() {
    return false;
  }

  @Override
  public SqlStatementBuilder createSqlStatementBuilder() {
    return new OrientDBSqlStatementBuilder();
  }

  @Override
  protected String doGetCurrentSchemaName() throws SQLException {
    ResultSet resultSet = null;
    String schema = null;

    try {
      resultSet = jdbcTemplate.getMetaData().getTableTypes();
      while (resultSet.next()) {
        if (!"SYSTEM_TABLE".equals(resultSet.getString("TABLE_TYPE"))) {
          schema = resultSet.getString("TABLE_TYPE");
          break;
        }
      }
    } finally {
      JdbcUtils.closeResultSet(resultSet);
    }

    return schema;
  }


  @Override
  protected void doChangeCurrentSchemaTo(String schema) throws SQLException {

  }

  @Override
  protected String doQuote(String quotable) {
//    return "`" + quotable + "`";
    return quotable;
  }

  @Override
  public String getBooleanFalse() {
    return "false";
  }

  @Override
  public String getBooleanTrue() {
    return "true";
  }

  @Override
  public String getCurrentUserFunction() {
    return "nothing";
  }

  @Override
  public String getDbName() {
    return "orientdb";
  }

  @Override
  public Schema getSchema(String name) {
    return new OrientDBClassType(jdbcTemplate, this, name);
  }

  @Override
  public boolean supportsDdlTransactions() {
    return false;
  }

  @Override
  public boolean useSingleConnection() {
    return true;
  }
}
