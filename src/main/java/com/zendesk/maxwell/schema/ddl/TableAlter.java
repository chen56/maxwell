package com.zendesk.maxwell.schema.ddl;

import java.util.ArrayList;

import com.zendesk.maxwell.schema.Database;
import com.zendesk.maxwell.schema.Schema;
import com.zendesk.maxwell.schema.Table;

public class TableAlter extends SchemaChange {
	public String dbName;
	public String tableName;
	public ArrayList<ColumnMod> columnMods;
	public String newTableName;
	public String newDatabase;

	public String convertCharset;
	public String defaultCharset;


	public TableAlter(String database, String tableName) {
		this.dbName = database;
		this.tableName = tableName;
		this.columnMods = new ArrayList<>();
	}

	@Override
	public String toString() {
		return "TableAlter<database: " + dbName + ", table:" + tableName + ">";
	}

	@Override
	public Schema apply(Schema originalSchema) throws SchemaSyncError {
		Schema newSchema = originalSchema.copy();

		Database database = findDatabase(newSchema, this.dbName);
		Table table = findTable(database, this.tableName);


		if ( newTableName != null && newDatabase != null ) {
			Database destDB = newSchema.findDatabase(this.newDatabase);
			if ( destDB == null )
				throw new SchemaSyncError("Couldn't find database " + this.dbName);

			table.rename(newTableName);

			database.getTableList().remove(table);
			destDB.addTable(table);
		}

		for (ColumnMod mod : columnMods) {
			mod.apply(table);
		}

		return newSchema;
	}
}