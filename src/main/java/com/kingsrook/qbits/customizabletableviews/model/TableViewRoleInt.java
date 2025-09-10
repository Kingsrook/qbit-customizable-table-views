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
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.data.QField;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.data.QRecordEntity;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QIcon;
import com.kingsrook.qqq.backend.core.model.metadata.producers.MetaDataCustomizerInterface;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.QMetaDataProducingEntity;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.tables.SectionFactory;
import com.kingsrook.qqq.backend.core.model.metadata.tables.UniqueKey;


/*******************************************************************************
 ** QRecord Entity for TableView table
 *******************************************************************************/
@QMetaDataProducingEntity(
   producePossibleValueSource = true,
   produceTableMetaData = true,
   tableMetaDataCustomizer = TableViewRoleInt.TableMetaDataCustomizer.class
)
public class TableViewRoleInt extends QRecordEntity
{
   public static final String TABLE_NAME = "TableViewRoleInt";



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
            .withUniqueKey(new UniqueKey("tableViewId", "roleId"))
            .withIcon(new QIcon().withName("switch_account"))
            .withRecordLabelFormat("%s: %s")
            .withRecordLabelFields("tableViewId", "roleId")
            .withSection(SectionFactory.defaultT1("id", "tableViewId", "roleId"))
            .withSection(SectionFactory.defaultT3("createDate", "modifyDate"));

         CustomizableTableViewsTablePersonalizer.MemoizationClearer.addPostActionCustomizersToTable(table);

         return (table);
      }
   }



   @QField(isEditable = false, isPrimaryKey = true)
   private Integer id;

   @QField(isRequired = true, possibleValueSourceName = TableView.TABLE_NAME)
   private Integer tableViewId;

   @QField(isRequired = true, possibleValueSourceName = "role")
   private Integer roleId;

   @QField(isEditable = false)
   private Instant createDate;

   @QField(isEditable = false)
   private Instant modifyDate;



   /*******************************************************************************
    ** Default constructor
    *******************************************************************************/
   public TableViewRoleInt()
   {
   }



   /*******************************************************************************
    ** Constructor that takes a QRecord
    *******************************************************************************/
   public TableViewRoleInt(QRecord record)
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
   public TableViewRoleInt withId(Integer id)
   {
      this.id = id;
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
   public TableViewRoleInt withCreateDate(Instant createDate)
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
   public TableViewRoleInt withModifyDate(Instant modifyDate)
   {
      this.modifyDate = modifyDate;
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
   public TableViewRoleInt withTableViewId(Integer tableViewId)
   {
      this.tableViewId = tableViewId;
      return (this);
   }




   /*******************************************************************************
    * Getter for roleId
    * @see #withRoleId(Integer)
    *******************************************************************************/
   public Integer getRoleId()
   {
      return (this.roleId);
   }



   /*******************************************************************************
    * Setter for roleId
    * @see #withRoleId(Integer)
    *******************************************************************************/
   public void setRoleId(Integer roleId)
   {
      this.roleId = roleId;
   }



   /*******************************************************************************
    * Fluent setter for roleId
    *
    * @param roleId
    * TODO document this property
    *
    * @return this
    *******************************************************************************/
   public TableViewRoleInt withRoleId(Integer roleId)
   {
      this.roleId = roleId;
      return (this);
   }


}
