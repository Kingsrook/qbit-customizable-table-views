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
import java.util.List;
import com.kingsrook.qbits.customizabletableviews.logic.CustomizableTableViewsTablePersonalizer;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.data.QAssociation;
import com.kingsrook.qqq.backend.core.model.data.QField;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.data.QRecordEntity;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.fields.ValueTooLongBehavior;
import com.kingsrook.qqq.backend.core.model.metadata.joins.QJoinMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QIcon;
import com.kingsrook.qqq.backend.core.model.metadata.producers.MetaDataCustomizerInterface;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.ChildJoin;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.ChildRecordListWidget;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.ChildTable;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.QMetaDataProducingEntity;
import com.kingsrook.qqq.backend.core.model.metadata.tables.Association;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.tables.SectionFactory;
import com.kingsrook.qqq.backend.core.model.metadata.tables.UniqueKey;


/*******************************************************************************
 ** QRecord Entity for TableView table
 *******************************************************************************/
@QMetaDataProducingEntity(
   producePossibleValueSource = true,
   produceTableMetaData = true,
   tableMetaDataCustomizer = TableView.TableMetaDataCustomizer.class,
   childTables = {
      @ChildTable(
         childTableEntityClass = TableViewField.class,
         joinFieldName = "tableViewId",
         childJoin = @ChildJoin(enabled = true),
         childRecordListWidget = @ChildRecordListWidget(label = "Fields", enabled = true, maxRows = 250, canAddChildRecords = true)),
      @ChildTable(
         childTableEntityClass = TableViewWidget.class,
         joinFieldName = "tableViewId",
         childJoin = @ChildJoin(enabled = true),
         childRecordListWidget = @ChildRecordListWidget(label = "Widgets", enabled = true, maxRows = 250, canAddChildRecords = true)),

      @ChildTable(
         childTableEntityClass = TableViewRoleInt.class,
         joinFieldName = "tableViewId",
         childJoin = @ChildJoin(enabled = true),
         childRecordListWidget = @ChildRecordListWidget(label = "Roles", enabled = true, maxRows = 250, canAddChildRecords = true))
   }
)
public class TableView extends QRecordEntity
{
   public static final String TABLE_NAME = "TableView";

   public static final String FIELDS_ASSOCIATION_NAME  = "fields";
   public static final String WIDGETS_ASSOCIATION_NAME = "widgets";



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
         String fieldChildJoinName  = QJoinMetaData.makeInferredJoinName(TABLE_NAME, TableViewField.TABLE_NAME);
         String widgetChildJoinName = QJoinMetaData.makeInferredJoinName(TABLE_NAME, TableViewWidget.TABLE_NAME);
         String roleChildJoinName   = QJoinMetaData.makeInferredJoinName(TABLE_NAME, TableViewRoleInt.TABLE_NAME);

         table
            .withUniqueKey(new UniqueKey("customizableTableId", "name"))
            .withIcon(new QIcon().withName("table_chart"))
            .withRecordLabelFormat("%s: %s")
            .withRecordLabelFields("customizableTableId", "name")
            .withSection(SectionFactory.defaultT1("id", "customizableTableId", "name"))
            .withSection(SectionFactory.customT2("fields", new QIcon("input")).withWidgetName(fieldChildJoinName))
            .withSection(SectionFactory.customT2("widgets", new QIcon("widgets")).withWidgetName(widgetChildJoinName))
            .withSection(SectionFactory.customT2("roles", new QIcon("diversity_3")).withWidgetName(roleChildJoinName))
            .withSection(SectionFactory.defaultT3("createDate", "modifyDate"))
            .withAssociation(new Association().withName(FIELDS_ASSOCIATION_NAME).withJoinName(fieldChildJoinName).withAssociatedTableName(TableViewField.TABLE_NAME))
            .withAssociation(new Association().withName(WIDGETS_ASSOCIATION_NAME).withJoinName(widgetChildJoinName).withAssociatedTableName(TableViewWidget.TABLE_NAME))
         ;

         CustomizableTableViewsTablePersonalizer.MemoizationClearer.addPostActionCustomizersToTable(table);

         return (table);
      }
   }



   @QField(isEditable = false, isPrimaryKey = true)
   private Integer id;

   @QField(isRequired = true, possibleValueSourceName = CustomizableTable.TABLE_NAME)
   private Integer customizableTableId;

   @QField(isRequired = true, maxLength = 100, valueTooLongBehavior = ValueTooLongBehavior.ERROR)
   private String name;

   @QField(isEditable = false)
   private Instant createDate;

   @QField(isEditable = false)
   private Instant modifyDate;

   @QAssociation(name = FIELDS_ASSOCIATION_NAME)
   private List<TableViewField> fields;

   @QAssociation(name = WIDGETS_ASSOCIATION_NAME)
   private List<TableViewWidget> widgets;



   /*******************************************************************************
    ** Default constructor
    *******************************************************************************/
   public TableView()
   {
   }



   /*******************************************************************************
    ** Constructor that takes a QRecord
    *******************************************************************************/
   public TableView(QRecord record)
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
   public TableView withId(Integer id)
   {
      this.id = id;
      return (this);
   }



   /*******************************************************************************
    * Getter for customizableTableId
    * @see #withCustomizableTableId(Integer)
    *******************************************************************************/
   public Integer getCustomizableTableId()
   {
      return (this.customizableTableId);
   }



   /*******************************************************************************
    * Setter for customizableTableId
    * @see #withCustomizableTableId(Integer)
    *******************************************************************************/
   public void setCustomizableTableId(Integer customizableTableId)
   {
      this.customizableTableId = customizableTableId;
   }



   /*******************************************************************************
    * Fluent setter for customizableTableId
    *
    * @param customizableTableId
    * TODO document this property
    *
    * @return this
    *******************************************************************************/
   public TableView withCustomizableTableId(Integer customizableTableId)
   {
      this.customizableTableId = customizableTableId;
      return (this);
   }



   /*******************************************************************************
    * Getter for name
    * @see #withName(String)
    *******************************************************************************/
   public String getName()
   {
      return (this.name);
   }



   /*******************************************************************************
    * Setter for name
    * @see #withName(String)
    *******************************************************************************/
   public void setName(String name)
   {
      this.name = name;
   }



   /*******************************************************************************
    * Fluent setter for name
    *
    * @param name
    * TODO document this property
    *
    * @return this
    *******************************************************************************/
   public TableView withName(String name)
   {
      this.name = name;
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
   public TableView withCreateDate(Instant createDate)
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
   public TableView withModifyDate(Instant modifyDate)
   {
      this.modifyDate = modifyDate;
      return (this);
   }



   /*******************************************************************************
    * Getter for fields
    * @see #withFields(List)
    *******************************************************************************/
   public List<TableViewField> getFields()
   {
      return (this.fields);
   }



   /*******************************************************************************
    * Setter for fields
    * @see #withFields(List)
    *******************************************************************************/
   public void setFields(List<TableViewField> fields)
   {
      this.fields = fields;
   }



   /*******************************************************************************
    * Fluent setter for fields
    *
    * @param fields
    * TODO document this property
    *
    * @return this
    *******************************************************************************/
   public TableView withFields(List<TableViewField> fields)
   {
      this.fields = fields;
      return (this);
   }



   /*******************************************************************************
    * Getter for widgets
    * @see #withWidgets(List)
    *******************************************************************************/
   public List<TableViewWidget> getWidgets()
   {
      return (this.widgets);
   }



   /*******************************************************************************
    * Setter for widgets
    * @see #withWidgets(List)
    *******************************************************************************/
   public void setWidgets(List<TableViewWidget> widgets)
   {
      this.widgets = widgets;
   }



   /*******************************************************************************
    * Fluent setter for widgets
    *
    * @param widgets
    * TODO document this property
    *
    * @return this
    *******************************************************************************/
   public TableView withWidgets(List<TableViewWidget> widgets)
   {
      this.widgets = widgets;
      return (this);
   }

}
