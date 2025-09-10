/*
 * QQQ - Low-code Application Framework for Engineers.
 * Copyright (C) 2021-2025.  Kingsrook, LLC
 * 651 N Broad St Ste 205 # 6917 | Middletown DE 19709 | United States
 * contact@kingsrook.com
 * https://github.com/Kingsrook/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.kingsrook.qbits.customizabletableviews.model;


import java.time.Instant;
import com.kingsrook.qbits.customizabletableviews.logic.CustomizableTableViewsTablePersonalizer;
import com.kingsrook.qbits.customizabletableviews.tables.customizers.TableViewFieldCustomizer;
import com.kingsrook.qqq.backend.core.actions.customizers.TableCustomizers;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.data.QField;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.data.QRecordEntity;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.code.QCodeReference;
import com.kingsrook.qqq.backend.core.model.metadata.fields.ValueTooLongBehavior;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QIcon;
import com.kingsrook.qqq.backend.core.model.metadata.producers.MetaDataCustomizerInterface;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.QMetaDataProducingEntity;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.tables.SectionFactory;
import com.kingsrook.qqq.backend.core.model.metadata.tables.UniqueKey;


/*******************************************************************************
 ** QRecord Entity for TableViewField table
 *******************************************************************************/
@QMetaDataProducingEntity(
   producePossibleValueSource = true,
   produceTableMetaData = true,
   tableMetaDataCustomizer = TableViewField.TableMetaDataCustomizer.class
)
public class TableViewField extends QRecordEntity
{
   public static final String TABLE_NAME = "TableViewField";



   /***************************************************************************
    **
    ***************************************************************************/
   public static class TableMetaDataCustomizer implements MetaDataCustomizerInterface<QTableMetaData>
   {

      /***************************************************************************
       **
       ***************************************************************************/
      @Override
      public QTableMetaData customizeMetaData(QInstance qInstance, QTableMetaData table) throws QException
      {
         table
            .withUniqueKey(new UniqueKey("tableViewId", "fieldName"))
            .withIcon(new QIcon().withName("input"))
            .withRecordLabelFormat("%s: %s")
            .withRecordLabelFields("tableViewId", "fieldName")
            .withSection(SectionFactory.defaultT1("id", "tableViewId", "fieldName"))
            .withSection(SectionFactory.defaultT2("accessLevel"))
            .withSection(SectionFactory.defaultT3("createDate", "modifyDate"));

         CustomizableTableViewsTablePersonalizer.MemoizationClearer.addPostActionCustomizersToTable(table);
         table.withCustomizer(TableCustomizers.PRE_INSERT_RECORD, new QCodeReference(TableViewFieldCustomizer.class));
         table.withCustomizer(TableCustomizers.PRE_UPDATE_RECORD, new QCodeReference(TableViewFieldCustomizer.class));

         return (table);
      }
   }



   @QField(isEditable = false, isPrimaryKey = true)
   private Integer id;

   @QField(isRequired = true, possibleValueSourceName = TableView.TABLE_NAME)
   private Integer tableViewId;

   @QField(label = "Field", isRequired = true, maxLength = 100, valueTooLongBehavior = ValueTooLongBehavior.ERROR, possibleValueSourceName = CustomizableTableFieldPVS.NAME)
   private String fieldName;

   @QField(isRequired = true, possibleValueSourceName = FieldAccessLevel.NAME)
   private String accessLevel;

   @QField(isEditable = false)
   private Instant createDate;

   @QField(isEditable = false)
   private Instant modifyDate;



   /*******************************************************************************
    ** Default constructor
    *******************************************************************************/
   public TableViewField()
   {
   }



   /*******************************************************************************
    ** Constructor that takes a QRecord
    *******************************************************************************/
   public TableViewField(QRecord record)
   {
      populateFromQRecord(record);
   }



   /*******************************************************************************
    * Getter for id
    * @see #withId(Integer)
    *******************************************************************************/
   public Integer getId()
   {
      return (this.id);
   }



   /*******************************************************************************
    * Setter for id
    * @see #withId(Integer)
    *******************************************************************************/
   public void setId(Integer id)
   {
      this.id = id;
   }



   /*******************************************************************************
    * Fluent setter for id
    *
    * @param id
    * TODO document this property
    *
    * @return this
    *******************************************************************************/
   public TableViewField withId(Integer id)
   {
      this.id = id;
      return (this);
   }



   /*******************************************************************************
    * Getter for tableViewId
    * @see #withTableViewId(Integer)
    *******************************************************************************/
   public Integer getTableViewId()
   {
      return (this.tableViewId);
   }



   /*******************************************************************************
    * Setter for tableViewId
    * @see #withTableViewId(Integer)
    *******************************************************************************/
   public void setTableViewId(Integer tableViewId)
   {
      this.tableViewId = tableViewId;
   }



   /*******************************************************************************
    * Fluent setter for tableViewId
    *
    * @param tableViewId
    * TODO document this property
    *
    * @return this
    *******************************************************************************/
   public TableViewField withTableViewId(Integer tableViewId)
   {
      this.tableViewId = tableViewId;
      return (this);
   }



   /*******************************************************************************
    * Getter for fieldName
    * @see #withFieldName(String)
    *******************************************************************************/
   public String getFieldName()
   {
      return (this.fieldName);
   }



   /*******************************************************************************
    * Setter for fieldName
    * @see #withFieldName(String)
    *******************************************************************************/
   public void setFieldName(String fieldName)
   {
      this.fieldName = fieldName;
   }



   /*******************************************************************************
    * Fluent setter for fieldName
    *
    * @param fieldName
    * TODO document this property
    *
    * @return this
    *******************************************************************************/
   public TableViewField withFieldName(String fieldName)
   {
      this.fieldName = fieldName;
      return (this);
   }



   /*******************************************************************************
    * Getter for accessLevel
    * @see #withAccessLevel(String)
    *******************************************************************************/
   public String getAccessLevel()
   {
      return (this.accessLevel);
   }



   /*******************************************************************************
    * Setter for accessLevel
    * @see #withAccessLevel(String)
    *******************************************************************************/
   public void setAccessLevel(String accessLevel)
   {
      this.accessLevel = accessLevel;
   }



   /*******************************************************************************
    * Fluent setter for accessLevel
    *
    * @param accessLevel
    * TODO document this property
    *
    * @return this
    *******************************************************************************/
   public TableViewField withAccessLevel(String accessLevel)
   {
      this.accessLevel = accessLevel;
      return (this);
   }



   /*******************************************************************************
    * Getter for createDate
    * @see #withCreateDate(Instant)
    *******************************************************************************/
   public Instant getCreateDate()
   {
      return (this.createDate);
   }



   /*******************************************************************************
    * Setter for createDate
    * @see #withCreateDate(Instant)
    *******************************************************************************/
   public void setCreateDate(Instant createDate)
   {
      this.createDate = createDate;
   }



   /*******************************************************************************
    * Fluent setter for createDate
    *
    * @param createDate
    * TODO document this property
    *
    * @return this
    *******************************************************************************/
   public TableViewField withCreateDate(Instant createDate)
   {
      this.createDate = createDate;
      return (this);
   }



   /*******************************************************************************
    * Getter for modifyDate
    * @see #withModifyDate(Instant)
    *******************************************************************************/
   public Instant getModifyDate()
   {
      return (this.modifyDate);
   }



   /*******************************************************************************
    * Setter for modifyDate
    * @see #withModifyDate(Instant)
    *******************************************************************************/
   public void setModifyDate(Instant modifyDate)
   {
      this.modifyDate = modifyDate;
   }



   /*******************************************************************************
    * Fluent setter for modifyDate
    *
    * @param modifyDate
    * TODO document this property
    *
    * @return this
    *******************************************************************************/
   public TableViewField withModifyDate(Instant modifyDate)
   {
      this.modifyDate = modifyDate;
      return (this);
   }



   /***************************************************************************
    * fluent setter by enum
    ***************************************************************************/
   public TableViewField withAccessLevel(FieldAccessLevel fieldAccessLevel)
   {
      if(fieldAccessLevel == null)
      {
         return withAccessLevel((String) null);
      }
      return withAccessLevel(fieldAccessLevel.getId());
   }

}
