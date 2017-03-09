/*
 * Copyright 2010-2017 Boxfuse GmbH Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package org.flywaydb.core.internal.dbsupport.orientdb;

import java.util.regex.Pattern;

import org.flywaydb.core.internal.dbsupport.SqlStatement;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;

public class OrientDBSqlStatementBuilder extends SqlStatementBuilder {

  private Pattern NON_TRANSACTION_SUPPORTED_SCRIPT =
    Pattern.compile("(DROP|CREATE|ALTER) (CLASS|PROPERTY|INDEX|DATABASE) .*");

  @Override
  protected void applyStateChanges(String line) {
    super.applyStateChanges(line);

    if (!executeInTransaction) {
      return;
    }

    executeInTransaction = !NON_TRANSACTION_SUPPORTED_SCRIPT.matcher(line).matches();
  }

  @Override
  public SqlStatement getSqlStatement() {
    SqlStatement statement = super.getSqlStatement();
    String sql = statement.getSql();
    if (functionScript(sql)) {
      String cleanedSql = rebuildSql(sql);
      statement = new SqlStatement(statement.getLineNumber(), cleanedSql, isPgCopyFromStdIn());
    }

    return statement;
  }

  private boolean functionScript(String sql) {
    return sql.toUpperCase().startsWith("CREATE FUNCTION");
  }

  private String rebuildSql(String sql) {
    StringBuilder cleanScriptBuilder = new StringBuilder();

    boolean openQuot = false;
    String[] split = sql.split("\n");

    for (int i = 0; i < split.length; i++) {
      String current = split[i];
      if (current.contains("\"")) {
        openQuot = !openQuot;
      }
      cleanScriptBuilder.append(current.trim());
      if (!openQuot) {
        cleanScriptBuilder.append("\n");
      } else {
        cleanScriptBuilder.append(" ");
      }
    }

    return cleanScriptBuilder.toString();
  }
}
