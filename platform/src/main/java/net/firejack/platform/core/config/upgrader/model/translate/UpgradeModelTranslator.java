/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.core.config.upgrader.model.translate;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.api.registry.model.RelationshipOption;
import net.firejack.platform.core.config.meta.IEntityElement;
import net.firejack.platform.core.config.meta.IFieldElement;
import net.firejack.platform.core.config.meta.IIndexElement;
import net.firejack.platform.core.config.translate.AbstractUpdateTranslator;
import net.firejack.platform.model.upgrader.operator.bean.*;
import org.apache.log4j.Logger;


@SuppressWarnings("unused")
public class UpgradeModelTranslator extends AbstractUpdateTranslator<DataSourceType, UpgradeTranslationResult> {

    private static final Logger logger = Logger.getLogger(UpgradeModelTranslator.class);

    /***/
    public UpgradeModelTranslator() {
        super(UpgradeTranslationResult.class);
    }

    @Override
    public void addForeignKey(String keyName, String sourceTableName, String referenceFieldName, String targetTableName, String targetFieldName, RelationshipOption onUpdateOptions, RelationshipOption onDeleteOptions) {
        AddForeignKeyType addForeignKeyType = new AddForeignKeyType();
        addForeignKeyType.setName(keyName);
        addForeignKeyType.setTable(sourceTableName);
        addForeignKeyType.setField(referenceFieldName);
        addForeignKeyType.setReferenceField(getSqlNameResolver().resolveIdColumn());
        addForeignKeyType.setReferenceTable(targetTableName);
        addForeignKeyType.setOnDelete(onDeleteOptions);
        addForeignKeyType.setOnUpdate(onUpdateOptions);

        getResultState().addForeignKey(addForeignKeyType);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void addColumn(IEntityElement rootEntity, IFieldElement column) {
        AddColumnType addColumnType = new AddColumnType();
        addColumnType.setType(column.getType().name());
        addColumnType.setName(column.getName());
        addColumnType.setDefault(column.getDefaultValue() == null ? null : String.valueOf(column.getDefaultValue()));//todo: discuss
        String idName = getSqlNameResolver().resolveIdColumn();
        addColumnType.setPrimaryKey(column.isAutoGenerated() && idName.equalsIgnoreCase(column.getName()));//todo: detect primary key somehow
        addColumnType.setRequired(column.isRequired());

        String tableName = getSqlSupport().getTableName(rootEntity);
        addColumnType.setTable(tableName);

        //addColumnType.setValue(column.getDefaultValue() == null ? null : String.valueOf(column.getDefaultValue()));

        getResultState().addAlterTable(addColumnType);//todo: add separate list for new columns
        column.setProcessed(true);
    }

    @Override
    protected void dropColumn(IEntityElement rootEntity, IFieldElement column) {
        DropColumnType dropColumnType = new DropColumnType();
        dropColumnType.setName(getSqlNameResolver().resolveColumnName(column));
        dropColumnType.setTable(getSqlSupport().getTableName(rootEntity));

        getResultState().addDropColumn(dropColumnType);
    }

    @Override
    protected void dropColumn(String table, String column) {
        DropColumnType dropColumnType = new DropColumnType();
        dropColumnType.setName(column);
        dropColumnType.setTable(table);

        getResultState().addDropColumn(dropColumnType);
    }

    @Override
    protected void modifyColumn(IEntityElement rootEntity, IFieldElement oldColumn, IFieldElement newColumn) {
        String tableName = getSqlSupport().getTableName(rootEntity);

        ModifyColumnType modifyColumnType = new ModifyColumnType();
        modifyColumnType.setTable(tableName);
        modifyColumnType.setOldName(oldColumn.getName());
        modifyColumnType.setNewName(newColumn.getName());
        modifyColumnType.setDefault(newColumn.getDefaultValue() == null ?
                null : String.valueOf(newColumn.getDefaultValue()));//todo:discuss
        modifyColumnType.setType(newColumn.getType().name());
        modifyColumnType.setRequired(newColumn.isRequired());

        getResultState().addAlterTable(modifyColumnType);//todo:maybe should add separate list for such changes -
    }

    @Override
    protected void createTable(IEntityElement rootEntity, boolean createIdColumn) {
        CreateTableType createTableType = new CreateTableType();
        createTableType.setName(getSqlSupport().getTableName(rootEntity));

        getResultState().addCreateTable(createTableType);

        IFieldElement[] fields = rootEntity.getFields();
        if (fields != null) {
            for (IFieldElement field : fields) {
                addColumnToTableDef(createTableType, field);
                field.setProcessed(true);
            }
        }
    }

    @Override
    protected void createRootTable(IEntityElement rootEntity, boolean createIdColumn) {
        CreateTableType createTableType = new CreateTableType();
        createTableType.setName(getSqlSupport().getTableName(rootEntity));

        getResultState().addCreateRootTable(createTableType);

        IFieldElement[] fields = rootEntity.getFields();
        if (fields != null) {
            for (IFieldElement field : fields) {
                addColumnToTableDef(createTableType, field);
                field.setProcessed(true);
            }
        }
    }

    @Override
    protected void createStagingTable(IEntityElement rootEntity, String refFieldName1, String refFieldName2) {
        String tableName = getSqlSupport().getTableName(rootEntity);
        CreateTableType createTableType = new CreateTableType();
        createTableType.setName(tableName);

        getResultState().addCreateTable(createTableType);
        ColumnType columnType = new ColumnType();
        columnType.setName(refFieldName1);
        columnType.setPrimaryKey(true);
        columnType.setRequired(true);
        columnType.setType(FieldType.NUMERIC_ID.name());
        createTableType.getColumn().add(columnType);

        ColumnType columnType2 = new ColumnType();
        columnType2.setName(refFieldName2);
        columnType2.setPrimaryKey(true);
        columnType2.setRequired(true);
        columnType2.setType(FieldType.NUMERIC_ID.name());
        createTableType.getColumn().add(columnType2);

        IFieldElement[] fields = rootEntity.getFields();
        if (fields != null) {
            for (IFieldElement field : fields) {
                String columnName = getSqlNameResolver().resolveColumnName(field);
                if (!columnName.equalsIgnoreCase(refFieldName1) &&
                        !columnName.equalsIgnoreCase(refFieldName2)) {
                    addColumnToTableDef(createTableType, field);
                    field.setProcessed(true);
                }
            }
        }
    }

    @Override
    protected void modifyTableName(String oldTableName, String newTableName) {
        ChangeTableNameType changeTableNameType = new ChangeTableNameType();
        changeTableNameType.setName(oldTableName);
        changeTableNameType.setNewName(newTableName);

        getResultState().addAlterTableName(changeTableNameType);
    }

    @Override
    protected void dropTable(IEntityElement rootEntity) {
        DropTableType dropTableType = new DropTableType();
        dropTableType.setName(getSqlSupport().getTableName(rootEntity));

        getResultState().addDropTable(dropTableType);
    }

    @Override
    protected void dropTable(String tableName) {
        DropTableType dropTableType = new DropTableType();
        dropTableType.setName(tableName);

        getResultState().addDropTable(dropTableType);
    }

    @Override
    protected void dropForeignKey(String tableName, String fkName) {
        DropForeignKeyType dropForeignKeyType = new DropForeignKeyType();
        dropForeignKeyType.setName(fkName);
        dropForeignKeyType.setTable(tableName);

        getResultState().addDropIndex(dropForeignKeyType);
    }

    @Override
    protected void addIndex(IEntityElement rootEntity, IIndexElement diffTarget) {
        int i = 0;
    }

    @Override
    protected void dropIndex(IEntityElement rootEntity, IIndexElement diffTarget) {
        int i = 0;
    }

    @Override
    protected void modifyIndex(IEntityElement rootEntity, IIndexElement diffTarget, IIndexElement newElement) {
        int i = 0;
    }

    private void addColumnToTableDef(CreateTableType tableToCreate, IFieldElement field) {
        String columnName = sqlNameResolver.resolveColumnName(field);
        ColumnType columnType = new ColumnType();
        columnType.setName(columnName);
        columnType.setPrimaryKey("id".equalsIgnoreCase(columnName));
        columnType.setRequired(field.isRequired());
        columnType.setType(field.getType().name());
        tableToCreate.getColumn().add(columnType);
    }

}