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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.kingsrook.qqq.backend.core.actions.tables.QueryAction;
import com.kingsrook.qqq.backend.core.actions.values.QCustomPossibleValueProvider;
import com.kingsrook.qqq.backend.core.context.QContext;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QCriteriaOperator;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QQueryFilter;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QueryInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QueryJoin;
import com.kingsrook.qqq.backend.core.model.actions.values.SearchPossibleValueSourceInput;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducerInterface;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.code.QCodeReference;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.possiblevalues.QPossibleValue;
import com.kingsrook.qqq.backend.core.model.metadata.possiblevalues.QPossibleValueSource;
import com.kingsrook.qqq.backend.core.model.metadata.possiblevalues.QPossibleValueSourceType;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qqq.backend.core.utils.CollectionUtils;
import com.kingsrook.qqq.backend.core.utils.ValueUtils;


/*******************************************************************************
 ** Meta Data Producer and custom value provider for CustomizableTableFieldPVS
 *******************************************************************************/
public class CustomizableTableFieldPVS implements QCustomPossibleValueProvider<String>, MetaDataProducerInterface<QPossibleValueSource>
{
   public static final String NAME = "CustomizableTableFieldPVS";



   /*******************************************************************************
    **
    *******************************************************************************/
   @Override
   public QPossibleValueSource produce(QInstance qInstance) throws QException
   {
      return (new QPossibleValueSource()
         .withName(NAME)
         .withType(QPossibleValueSourceType.CUSTOM)
         .withCustomCodeReference(new QCodeReference(getClass())));
   }



   /***************************************************************************
    *
    ***************************************************************************/
   @Override
   public QPossibleValue<String> getPossibleValue(Serializable id)
   {
      /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      // require the id to be table.field - since we can't see other values in the record to otherwise know the table... //
      /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      String idString = ValueUtils.getValueAsString(id);
      if(idString.contains("."))
      {
         String[] parts     = idString.split("\\.");
         String   tableName = parts[0];
         String   fieldName = parts[1];

         QTableMetaData table = QContext.getQInstance().getTable(tableName);
         if(table != null)
         {
            QFieldMetaData fieldMetaData = table.getFields().get(fieldName);
            if(fieldMetaData != null)
            {
               return (new QPossibleValue<>(idString, fieldMetaData.getLabel()));
            }
         }
      }

      return null;
   }



   /***************************************************************************
    *
    ***************************************************************************/
   @Override
   public List<QPossibleValue<String>> search(SearchPossibleValueSourceInput searchPossibleValueSourceInput) throws QException
   {
      List<QPossibleValue<String>> allPossibleValues = new ArrayList<>();

      Set<String> usedIds = new HashSet<>();

      Integer tableViewId = ValueUtils.getValueAsInteger(CollectionUtils.nonNullMap(searchPossibleValueSourceInput.getOtherValues()).get("tableViewId"));
      if(tableViewId != null)
      {
         List<QRecord> tableRecords = new QueryAction().execute(new QueryInput(CustomizableTable.TABLE_NAME)
            .withFilter(new QQueryFilter().withCriteria(TableView.TABLE_NAME + ".id", QCriteriaOperator.EQUALS, tableViewId))
            .withQueryJoin(new QueryJoin(TableView.TABLE_NAME))).getRecords();

         if(CollectionUtils.nullSafeHasContents(tableRecords))
         {
            String         tableName = tableRecords.get(0).getValueString("tableName");
            QTableMetaData table     = QContext.getQInstance().getTable(tableName);

            if(table != null)
            {
               for(QFieldMetaData field : table.getFields().values())
               {
                  if(field.getName().equals(table.getPrimaryKeyField()))
                  {
                     ////////////////////////////////////////////////////////////////////////////////////////
                     // don't put the PKey in the PVS - it's always shown, and you can't change its rules. //
                     ////////////////////////////////////////////////////////////////////////////////////////
                     continue;
                  }

                  if(field.getIsRequired())
                  {
                     //////////////////////////////////////////////////////////////////////////////////////
                     // don't put required fields in the PVS - you can't hide them or make them optional //
                     //////////////////////////////////////////////////////////////////////////////////////
                     continue;
                  }

                  if(field.getIsHidden())
                  {
                     ////////////////////////////////////////////////////////////////////////////////
                     // don't put hidden fields in the PVS - users aren't allowed to turn them on. //
                     ////////////////////////////////////////////////////////////////////////////////
                     continue;
                  }

                  QPossibleValue<String> possibleValue = getPossibleValue(tableName + "." + field.getName());
                  allPossibleValues.add(possibleValue);
                  usedIds.add(possibleValue.getId());
               }
            }
         }
      }

      //////////////////////////////////////////////////////////////////////////////////
      // if a list of ids is given in the search input, do specific searches for them //
      //////////////////////////////////////////////////////////////////////////////////
      if(searchPossibleValueSourceInput.getIdList() != null)
      {
         for(Serializable id : searchPossibleValueSourceInput.getIdList())
         {
            String idString = ValueUtils.getValueAsString(id);
            if(!usedIds.contains(idString))
            {
               QPossibleValue<String> possibleValue = getPossibleValue(idString);
               if(possibleValue != null)
               {
                  allPossibleValues.add(possibleValue);
                  usedIds.add(possibleValue.getId());
               }
            }
         }
      }

      return completeCustomPVSSearch(searchPossibleValueSourceInput, allPossibleValues);
   }
}
