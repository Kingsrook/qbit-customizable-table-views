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

package com.kingsrook.qbits.customizabletableviews.logic;


import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import com.kingsrook.qbits.customizabletableviews.model.CustomizableTable;
import com.kingsrook.qbits.customizabletableviews.model.FieldAccessLevel;
import com.kingsrook.qbits.customizabletableviews.model.TableView;
import com.kingsrook.qbits.customizabletableviews.model.TableViewField;
import com.kingsrook.qbits.customizabletableviews.model.TableViewRoleInt;
import com.kingsrook.qbits.customizabletableviews.model.TableViewWidget;
import com.kingsrook.qbits.customizabletableviews.model.WidgetAccessLevel;
import com.kingsrook.qqq.backend.core.actions.customizers.TableCustomizerInterface;
import com.kingsrook.qqq.backend.core.actions.customizers.TableCustomizers;
import com.kingsrook.qqq.backend.core.actions.metadata.personalization.TableMetaDataPersonalizerInterface;
import com.kingsrook.qqq.backend.core.actions.tables.GetAction;
import com.kingsrook.qqq.backend.core.actions.tables.QueryAction;
import com.kingsrook.qqq.backend.core.context.QContext;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.logging.QLogger;
import com.kingsrook.qqq.backend.core.model.actions.AbstractActionInput;
import com.kingsrook.qqq.backend.core.model.actions.AbstractTableActionInput;
import com.kingsrook.qqq.backend.core.model.actions.metadata.personalization.TableMetaDataPersonalizerInput;
import com.kingsrook.qqq.backend.core.model.actions.processes.RunBackendStepInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.QInputSource;
import com.kingsrook.qqq.backend.core.model.actions.tables.delete.DeleteInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.get.GetInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.insert.InsertInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QCriteriaOperator;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QFilterCriteria;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QQueryFilter;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QueryInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QueryJoin;
import com.kingsrook.qqq.backend.core.model.actions.tables.update.UpdateInput;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.metadata.code.QCodeReference;
import com.kingsrook.qqq.backend.core.model.metadata.fields.DynamicDefaultValueBehavior;
import com.kingsrook.qqq.backend.core.model.metadata.fields.FieldAndJoinTable;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QFieldSection;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qqq.backend.core.model.session.QUser;
import com.kingsrook.qqq.backend.core.utils.CollectionUtils;
import com.kingsrook.qqq.backend.core.utils.Pair;
import com.kingsrook.qqq.backend.core.utils.StringUtils;
import com.kingsrook.qqq.backend.core.utils.memoization.Memoization;
import org.apache.commons.lang3.BooleanUtils;
import static com.kingsrook.qqq.backend.core.logging.LogUtils.logPair;


/*******************************************************************************
 * TableMetaDataPersonalizerInterface implementation to apply the logic of this
 * QBit.
 *******************************************************************************/
public class CustomizableTableViewsTablePersonalizer implements TableMetaDataPersonalizerInterface
{
   private static final QLogger LOG = QLogger.getLogger(CustomizableTableViewsTablePersonalizer.class);

   private static Memoization<String, Boolean> isTableCustomizableMemoization = new Memoization<>(Duration.ofMinutes(5));

   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   // todo ideally we would clear this memoization if user-role-int is edited - but that would require topics or similar, which we don't have at this time. //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   private static Memoization<Pair<String, String>, TableView> getEffectiveTableViewByUserMemoization = new Memoization<>(Duration.ofMinutes(5));



   /***************************************************************************
    *
    ***************************************************************************/
   public static void clearMemoizations()
   {
      isTableCustomizableMemoization.clear();
      getEffectiveTableViewByUserMemoization.clear();
   }



   /***************************************************************************
    *
    ***************************************************************************/
   @Override
   public QTableMetaData execute(TableMetaDataPersonalizerInput input) throws QException
   {
      RunBackendStepInput runBackendStepInput = new RunBackendStepInput();

      QTableMetaData tableMetaData = input.getTable();
      if(tableMetaData == null)
      {
         return (null);
      }

      ////////////////////////////////////////////////////////
      // only personalize meta data for input-source = USER //
      ////////////////////////////////////////////////////////
      if(!QInputSource.USER.equals(input.getInputSource()))
      {
         return (tableMetaData);
      }

      if(isTableCustomizable(tableMetaData.getName()))
      {
         return customizeTable(input);
      }
      else
      {
         return (tableMetaData);
      }
   }



   /***************************************************************************
    *
    ***************************************************************************/
   QTableMetaData customizeTable(AbstractTableActionInput tableActionInput) throws QException
   {
      QTableMetaData table = tableActionInput.getTable();
      if(table == null || table.getName() == null)
      {
         return (table);

      }
      TableView tableView = getEffectiveTableViewForCurrentSession(table.getName());

      if(tableView != null)
      {
         return applyViewToTable(tableView, tableActionInput.getTable().clone(), tableActionInput);
      }

      return tableActionInput.getTable();
   }



   /***************************************************************************
    *
    ***************************************************************************/
   QTableMetaData applyViewToTable(TableView tableView, QTableMetaData cloneTable, AbstractTableActionInput tableActionInput)
   {
      Map<String, QFieldMetaData> fieldsToKeep = getFieldsToKeepForTable(tableView, cloneTable, tableActionInput);
      cloneTable.setFields(fieldsToKeep);

      ///////////////////////////////////////////////////////////
      // remove field names which aren't present from sections //
      ///////////////////////////////////////////////////////////
      for(QFieldSection section : CollectionUtils.nonNullList(cloneTable.getSections()))
      {
         Map<String, Set<String>> getFieldsToKeepFromJoinTableCache = null;

         Iterator<String> fieldListIterator = CollectionUtils.nonNullList(section.getFieldNames()).iterator();
         while(fieldListIterator.hasNext())
         {
            try
            {
               String            fieldName         = fieldListIterator.next();
               FieldAndJoinTable fieldAndJoinTable = FieldAndJoinTable.get(cloneTable, fieldName);

               if(fieldAndJoinTable.joinTable().getName().equals(cloneTable.getName()))
               {
                  ////////////////////////////////////////////////////////////////////////////////////////////
                  // if the field is from this table, then remove it unless it's in the set of ones to keep //
                  ////////////////////////////////////////////////////////////////////////////////////////////
                  if(!fieldsToKeep.containsKey(fieldName))
                  {
                     fieldListIterator.remove();
                  }
               }
               else
               {
                  getFieldsToKeepFromJoinTableCache = Objects.requireNonNullElseGet(getFieldsToKeepFromJoinTableCache, HashMap::new);

                  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                  // else the field is from a join table - get that join table's allowed fields for the user, and keep or remove based on that //
                  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                  Set<String> fieldsToKeepFromJoinTable = getFieldsToKeepFromJoinTable(fieldAndJoinTable.joinTable().getName(), tableActionInput, getFieldsToKeepFromJoinTableCache);
                  if(!fieldsToKeepFromJoinTable.contains(fieldAndJoinTable.field().getName()))
                  {
                     fieldListIterator.remove();
                  }
               }
            }
            catch(Exception e)
            {
               /////////////////////////////////////////////////////////
               // an unknown field, let's assume it should be removed //
               /////////////////////////////////////////////////////////
               fieldListIterator.remove();
            }
         }
      }

      //////////////////////////////////////////////////////////
      // remove sections that had all of their fields removed //
      //////////////////////////////////////////////////////////
      CollectionUtils.nonNullList(cloneTable.getSections()).removeIf(section ->
         section.getFieldNames() != null && section.getFieldNames().isEmpty() && !StringUtils.hasContent(section.getWidgetName()));

      //////////////////////////////////////
      // figure out which widgets we keep //
      //////////////////////////////////////
      Set<String> widgetsToKeep = new HashSet<>();
      for(TableViewWidget tableViewWidget : CollectionUtils.nonNullList(tableView.getWidgets()))
      {
         try
         {
            String widgetName = tableViewWidget.getWidgetName();
            widgetsToKeep.add(widgetName);
         }
         catch(Exception e)
         {
            LOG.warn("Error processing tableViewWidget", e, logPair("widgetName", tableViewWidget.getWidgetName()));
         }
      }

      //////////////////////////////////////////////////////
      // remove sections w/widgets that we aren't keeping //
      //////////////////////////////////////////////////////
      if(cloneTable.getSections() != null)
      {
         cloneTable.getSections().removeIf(section -> section.getWidgetName() != null && !widgetsToKeep.contains(section.getWidgetName()));
      }

      return (cloneTable);
   }



   /***************************************************************************
    *
    ***************************************************************************/
   private Set<String> getFieldsToKeepFromJoinTable(String joinTableName, AbstractTableActionInput tableActionInput, Map<String, Set<String>> getFieldsToKeepFromJoinTableCache)
   {
      return (getFieldsToKeepFromJoinTableCache.computeIfAbsent(joinTableName, k ->
      {
         try
         {
            if(isTableCustomizable(joinTableName))
            {
               ////////////////////////////////////////////////////////////////
               // if the table is customizable, return set of fields to keep //
               ////////////////////////////////////////////////////////////////
               QTableMetaData joinTableClone = QContext.getQInstance().getTable(joinTableName).clone();
               TableView      joinTableView  = getEffectiveTableViewForCurrentSession(joinTableName);
               Map<String, QFieldMetaData> joinTableFieldsToKeep = getFieldsToKeepForTable(joinTableView, joinTableClone, tableActionInput);
               return joinTableFieldsToKeep.keySet();
            }
            else
            {
               ///////////////////////////////////////////////
               // else, not customizable, return all fields //
               ///////////////////////////////////////////////
               return (QContext.getQInstance().getTable(joinTableName).getFields().keySet());
            }
         }
         catch(QException e)
         {
            return Collections.emptySet();
         }
      }));
   }



   /***************************************************************************
    *
    ***************************************************************************/
   private static Map<String, QFieldMetaData> getFieldsToKeepForTable(TableView tableView, QTableMetaData cloneTable, AbstractTableActionInput tableActionInput)
   {
      Map<String, QFieldMetaData> cloneFields = cloneTable.getFields();
      if(cloneFields == null)
      {
         cloneFields = new LinkedHashMap<>();
         cloneTable.setFields(cloneFields);
      }

      Map<String, QFieldMetaData> fieldsToKeep = new LinkedHashMap<>();

      /////////////////////////////////////////////
      // figure out which fields the user gets   //
      // start with ones in the user's TableView //
      /////////////////////////////////////////////
      for(TableViewField tableViewField : CollectionUtils.nonNullList(tableView.getFields()))
      {
         try
         {
            String         fieldName     = tableViewField.getFieldName().split("\\.")[1];
            QFieldMetaData fieldMetaData = cloneFields.get(fieldName);
            if(fieldMetaData != null)
            {
               FieldAccessLevel fieldAccessLevel = FieldAccessLevel.getById(tableViewField.getAccessLevel());
               if(fieldAccessLevel != null)
               {
                  fieldAccessLevel.apply(fieldMetaData);
               }

               fieldsToKeep.put(fieldName, fieldMetaData);
            }
         }
         catch(Exception e)
         {
            LOG.warn("Error processing tableViewField", e, logPair("fieldName", tableViewField.getFieldName()));
         }
      }

      /////////////////////////////////////////////
      // next look for ones that always get kept //
      /////////////////////////////////////////////
      boolean isInsertOrUpdate = (tableActionInput instanceof InsertInput) || (tableActionInput instanceof UpdateInput);
      for(QFieldMetaData field : cloneTable.getFields().values())
      {
         String fieldName = field.getName();
         if(fieldsToKeep.containsKey(fieldName))
         {
            continue;
         }

         DynamicDefaultValueBehavior x    = null;
         boolean                     keep = false;
         if(field.getIsRequired() || Objects.equals(fieldName, cloneTable.getPrimaryKeyField()))
         {
            ///////////////////////////////////////////////////////////
            // always keep the primary key field and required fields //
            ///////////////////////////////////////////////////////////
            keep = true;
         }
         else if(isInsertOrUpdate && field.getBehaviorOnlyIfSet(DynamicDefaultValueBehavior.class) != null)
         {
            //////////////////////////////////////////////////////////////////////////////////////////////////////
            // if this is for an insert or update, then keep any fields that have a DynamicDefaultValueBehavior //
            // e.g., createDates, modifyDates, things that capture userId                                       //
            //////////////////////////////////////////////////////////////////////////////////////////////////////
            keep = true;
         }

         if(keep)
         {
            fieldsToKeep.put(fieldName, cloneFields.get(fieldName));
         }
      }
      return fieldsToKeep;
   }



   /***************************************************************************
    * the assumption is, that this method is called AFTER we've identified that
    * a table is customizable (and customizaztion is active) - so - if we fail
    * to find a view, we return a new/blank/empty view - so user gets "nothing"
    * (other than minimum that's required), rather than returning null, which
    * would give the user the full table.
    ***************************************************************************/
   TableView getEffectiveTableViewForCurrentSession(String tableName) throws QException
   {
      QUser user = QContext.getQSession().getUser();
      if(user == null)
      {
         return (new TableView());
      }

      Pair<String, String> key = Pair.of(user.getIdReference(), tableName);
      return (getEffectiveTableViewByUserMemoization.getResultThrowing(key, (k) ->
      {
         List<QRecord> tableViews = null;

         ///////////////////////////////////////////////////////////////////////////////
         // if we have role Ids, then look for any tableViews assigned to those roles //
         ///////////////////////////////////////////////////////////////////////////////
         String roleIdsString = QContext.getQSession().getValue("roleIds");
         if(StringUtils.hasContent(roleIdsString))
         {
            Set<Integer> roleIds = Arrays.stream(roleIdsString.split(",")).map(Integer::parseInt).collect(Collectors.toSet());
            tableViews = new QueryAction().execute(new QueryInput(TableView.TABLE_NAME)
                  .withFilter(new QQueryFilter()
                     .withCriteria(new QFilterCriteria(TableViewRoleInt.TABLE_NAME + ".roleId", QCriteriaOperator.IN, roleIds))
                     .withCriteria(new QFilterCriteria(CustomizableTable.TABLE_NAME + ".tableName", QCriteriaOperator.EQUALS, tableName)))
                  .withQueryJoin(new QueryJoin(TableViewRoleInt.TABLE_NAME))
                  .withIncludeAssociations(true))
               .getRecords();
         }

         //////////////////////////////////////////////////////////////////////////
         // if there are no role views, then look for a default one on the table //
         //////////////////////////////////////////////////////////////////////////
         if(CollectionUtils.nullSafeIsEmpty(tableViews))
         {
            tableViews = new ArrayList<>();
            QRecord customizableTableRecord = GetAction.execute(CustomizableTable.TABLE_NAME, Map.of("tableName", tableName));
            if(customizableTableRecord != null)
            {
               Integer tableViewId = customizableTableRecord.getValueInteger("defaultTableViewId");
               if(tableViewId != null)
               {
                  QRecord tableView = new GetAction().executeForRecord(new GetInput(TableView.TABLE_NAME).withPrimaryKey(tableViewId).withIncludeAssociations(true));
                  CollectionUtils.addIfNotNull(tableViews, tableView);
               }
            }
         }

         //////////////////////////////////////////////////////////////////////////////////////////////////
         // if no view was found, then return an empty one, so user sees nothing rather than everything. //
         //////////////////////////////////////////////////////////////////////////////////////////////////
         if(tableViews.isEmpty())
         {
            return (new TableView());
         }

         ////////////////////////////////////
         // return the merger of the views //
         ////////////////////////////////////
         return mergeTableViewRecords(tableViews);
      }).orElse(null));
   }



   /***************************************************************************
    *
    ***************************************************************************/
   TableView mergeTableViewRecords(List<QRecord> tableViewRecords)
   {
      if(CollectionUtils.nullSafeIsEmpty(tableViewRecords))
      {
         return null;
      }

      List<TableView> tableViewList = tableViewRecords.stream().map(r -> new TableView(r)).toList();
      return mergeTableViewEntities(tableViewList);
   }



   /***************************************************************************
    *
    ***************************************************************************/
   TableView mergeTableViewEntities(List<TableView> tableViewList)
   {
      if(CollectionUtils.nullSafeIsEmpty(tableViewList))
      {
         return null;
      }

      if(tableViewList.size() == 1)
      {
         //////////////////////////////////////////
         // if only 1 table view, just return it //
         //////////////////////////////////////////
         return tableViewList.get(0);
      }

      /////////////////////
      // merge the views //
      /////////////////////
      TableView mergedTableView = new TableView();

      ////////////////////////////////////////////////////////////////////////////////////
      // start with fields, using merge method in enum for the logic of which rule wins //
      ////////////////////////////////////////////////////////////////////////////////////
      Map<String, FieldAccessLevel> fieldAccessLevels = new HashMap<>();
      for(TableView tableView : tableViewList)
      {
         for(TableViewField field : CollectionUtils.nonNullList(tableView.getFields()))
         {
            String           fieldName        = field.getFieldName();
            FieldAccessLevel fieldAccessLevel = FieldAccessLevel.getById(field.getAccessLevel());
            if(fieldAccessLevel != null)
            {
               fieldAccessLevels.put(fieldName, fieldAccessLevel.merge(fieldAccessLevels.get(fieldName)));
            }
         }
      }

      ///////////////////////////////////////////////////////////////////////////
      // build a new list of TableViewField's for our merged entity to contain //
      ///////////////////////////////////////////////////////////////////////////
      List<TableViewField> mergedFieldsList = new ArrayList<>();
      mergedTableView.setFields(mergedFieldsList);
      for(Map.Entry<String, FieldAccessLevel> entry : fieldAccessLevels.entrySet())
      {
         mergedFieldsList.add(new TableViewField()
            .withFieldName(entry.getKey())
            .withAccessLevel(entry.getValue().getId()));
      }

      //////////////////////////
      // build set of widgets //
      //////////////////////////
      Map<String, WidgetAccessLevel> widgets = new HashMap<>();
      for(TableView tableView : tableViewList)
      {
         for(TableViewWidget widget : CollectionUtils.nonNullList(tableView.getWidgets()))
         {
            widgets.put(widget.getWidgetName(), WidgetAccessLevel.getById(widget.getAccessLevel()));
         }
      }

      //////////////////////////////////////////////////////////////////////////
      // build new list of TableViewWidget's for our merged entity to contain //
      //////////////////////////////////////////////////////////////////////////
      List<TableViewWidget> mergedWidgetsList = new ArrayList<>();
      mergedTableView.setWidgets(mergedWidgetsList);
      for(Map.Entry<String, WidgetAccessLevel> entry : widgets.entrySet())
      {
         mergedWidgetsList.add(new TableViewWidget()
            .withWidgetName(entry.getKey())
            .withAccessLevel(entry.getValue().getId()));
      }

      return (mergedTableView);
   }



   /***************************************************************************
    *
    ***************************************************************************/
   boolean isTableCustomizable(String tableName) throws QException
   {
      return (isTableCustomizableMemoization.getResultThrowing(tableName, (name) ->
      {
         QRecord customizableTableRecord = GetAction.execute(CustomizableTable.TABLE_NAME, Map.of("tableName", tableName));
         if(customizableTableRecord != null)
         {
            return BooleanUtils.isTrue(customizableTableRecord.getValueBoolean("isActive"));
         }

         return false;
      }).orElse(false));
   }



   /***************************************************************************
    *
    ***************************************************************************/
   public static class MemoizationClearer implements TableCustomizerInterface
   {

      /***************************************************************************
       *
       ***************************************************************************/
      public static void addPostActionCustomizersToTable(QTableMetaData table)
      {
         table.withCustomizer(TableCustomizers.POST_INSERT_RECORD, new QCodeReference(CustomizableTableViewsTablePersonalizer.MemoizationClearer.class));
         table.withCustomizer(TableCustomizers.POST_UPDATE_RECORD, new QCodeReference(CustomizableTableViewsTablePersonalizer.MemoizationClearer.class));
         table.withCustomizer(TableCustomizers.POST_DELETE_RECORD, new QCodeReference(CustomizableTableViewsTablePersonalizer.MemoizationClearer.class));
      }



      /***************************************************************************
       *
       ***************************************************************************/
      @Override
      public List<QRecord> postInsertOrUpdate(AbstractActionInput input, List<QRecord> records, Optional<List<QRecord>> oldRecordList) throws QException
      {
         CustomizableTableViewsTablePersonalizer.clearMemoizations();
         return records;
      }



      /***************************************************************************
       *
       ***************************************************************************/
      @Override
      public List<QRecord> postDelete(DeleteInput deleteInput, List<QRecord> records) throws QException
      {
         CustomizableTableViewsTablePersonalizer.clearMemoizations();
         return records;
      }
   }
}
