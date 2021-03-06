/*
 * Copyright 2010-2017 Boxfuse GmbH Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package org.flywaydb.core.internal.dbsupport.orientdb;

import java.sql.SQLException;

import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.Table;

public class OrientDBClass extends Table {

  public OrientDBClass(JdbcTemplate jdbcTemplate, DbSupport dbSupport, Schema schema, String name) {
    super(jdbcTemplate, dbSupport, schema, name);
  }

  @Override
  protected boolean doExists() throws SQLException {
    return jdbcTemplate.queryForInt("SELECT COUNT(name) FROM (SELECT expand(classes) from metadata:schema) WHERE name = ?", name) > 0;
  }

  @Override
  protected void doLock() throws SQLException {
    // TODO: maybe it will cause a deadlock, or simply not working
    jdbcTemplate.execute("SELECT FROM " + this + " LOCK RECORD");
  }

  @Override
  protected void doDrop() throws SQLException {
    jdbcTemplate.execute("DROP CLASS " + this);
  }

  @Override
  public String toString() {
    return dbSupport.quote(this.name);
  }
}
