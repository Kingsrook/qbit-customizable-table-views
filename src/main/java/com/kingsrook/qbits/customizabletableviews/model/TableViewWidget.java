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
import com.kingsrook.qqq.backend.core.model.metadata.fields.ValueTooLongBehavior;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QIcon;
import com.kingsrook.qqq.backend.core.model.metadata.producers.MetaDataCustomizerInterface;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.QMetaDataProducingEntity;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.tables.SectionFactory;
import com.kingsrook.qqq.backend.core.model.metadata.tables.UniqueKey;


/*******************************************************************************
 ** QRecord Entity for TableViewWidget table
 *******************************************************************************/
@QMetaDataProducingEntity(
   producePossibleValueSource = true,
   produceTableMetaData = true,
   tableMetaDataCustomizer = TableViewWidget.TableMetaDataCustomizer.class
)
public class TableViewWidget extends QRecordEntity
{
   public static final String TABLE_NAME = "TableViewWidget";



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
            .withUniqueKey(new UniqueKey("tableViewId", "widgetName"))
            .withIcon(new QIcon().withName("widgets"))
            .withRecordLabelFormat("%s: %s")
            .withRecordLabelFields("tableViewId", "widgetName")
            .withSection(SectionFactory.defaultT1("id", "tableViewId", "widgetName"))
            .withSection(SectionFactory.defaultT2("accessLevel"))
            .withSection(SectionFactory.defaultT3("createDate", "modifyDate"));

         CustomizableTableViewsTablePersonalizer.MemoizationClearer.addPostActionCustomizersToTable(table);

         return (table);
      }
   }



   @QField(isEditable = false, isPrimaryKey = true)
   private Integer id;

   @QField(isRequired = true, possibleValueSourceName = TableView.TABLE_NAME)
   private Integer tableViewId;

   @QField(label = "Widget", isRequired = true, maxLength = 100, valueTooLongBehavior = ValueTooLongBehavior.ERROR, possibleValueSourceName = CustomizableTableWidgetPVS.NAME)
   private String widgetName;

   @QField(isRequired = true, possibleValueSourceName = WidgetAccessLevel.NAME)
   private String accessLevel;

   @QField(isEditable = false)
   private Instant createDate;

   @QField(isEditable = false)
   private Instant modifyDate;



   /*******************************************************************************
    ** Default constructor
    *******************************************************************************/
   public TableViewWidget()
   {
   }



   /*******************************************************************************
    ** Constructor that takes a QRecord
    *******************************************************************************/
   public TableViewWidget(QRecord record)
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
   public TableViewWidget withId(Integer id)
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
   public TableViewWidget withTableViewId(Integer tableViewId)
   {
      this.tableViewId = tableViewId;
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
   public TableViewWidget withAccessLevel(String accessLevel)
   {
      this.accessLevel = accessLevel;
      return (this);
   }



   /*******************************************************************************
    * Fluent setter for accessLevel
    *
    * @param accessLevel
    * TODO document this property
    *
    * @return this
    *******************************************************************************/
   public TableViewWidget withAccessLevel(WidgetAccessLevel accessLevel)
   {
      if(accessLevel == null)
      {
         return withAccessLevel((String) null);
      }

      return withAccessLevel(accessLevel.getId());
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
   public TableViewWidget withCreateDate(Instant createDate)
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
   public TableViewWidget withModifyDate(Instant modifyDate)
   {
      this.modifyDate = modifyDate;
      return (this);
   }



   /*******************************************************************************
    * Getter for widgetName
    * @see #withWidgetName(String)
    *******************************************************************************/
   public String getWidgetName()
   {
      return (this.widgetName);
   }



   /*******************************************************************************
    * Setter for widgetName
    * @see #withWidgetName(String)
    *******************************************************************************/
   public void setWidgetName(String widgetName)
   {
      this.widgetName = widgetName;
   }



   /*******************************************************************************
    * Fluent setter for widgetName
    *
    * @param widgetName
    * TODO document this property
    *
    * @return this
    *******************************************************************************/
   public TableViewWidget withWidgetName(String widgetName)
   {
      this.widgetName = widgetName;
      return (this);
   }

}
