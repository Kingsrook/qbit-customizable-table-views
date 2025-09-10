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

package com.kingsrook.qbits.customizabletableviews.tables.customizers;


import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.kingsrook.qbits.customizabletableviews.model.FieldAccessLevel;
import com.kingsrook.qqq.backend.core.actions.customizers.RecordCustomizerUtilityInterface;
import com.kingsrook.qqq.backend.core.actions.customizers.TableCustomizerInterface;
import com.kingsrook.qqq.backend.core.context.QContext;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.actions.AbstractActionInput;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldMetaData;
import com.kingsrook.qqq.backend.core.model.statusmessages.BadInputStatusMessage;
import com.kingsrook.qqq.backend.core.model.statusmessages.SystemErrorStatusMessage;


/*******************************************************************************
 **
 *******************************************************************************/
public class TableViewFieldCustomizer implements TableCustomizerInterface
{

   /***************************************************************************
    *
    ***************************************************************************/
   @Override
   public List<QRecord> preInsertOrUpdate(AbstractActionInput input, List<QRecord> records, boolean isPreview, Optional<List<QRecord>> oldRecordList) throws QException
   {
      Optional<Map<Serializable, QRecord>> oldRecordMap = oldRecordListToMap("id", oldRecordList);

      for(QRecord record : records)
      {
         try
         {
            Integer id            = record.getValueInteger("id");
            String  fieldName     = RecordCustomizerUtilityInterface.getValueFromRecordOrOldRecord("fieldName", record, id, oldRecordMap);
            String  accessLevelId = RecordCustomizerUtilityInterface.getValueFromRecordOrOldRecord("accessLevel", record, id, oldRecordMap);

            String[] parts     = fieldName.split("\\.");
            String   tableName = parts[0];
            fieldName = parts[1];

            QFieldMetaData field = QContext.getQInstance().getTable(tableName).getField(fieldName);

            FieldAccessLevel fieldAccessLevel = FieldAccessLevel.getById(accessLevelId);
            if(fieldAccessLevel != null)
            {
               String errorMessage = fieldAccessLevel.validateForField(field);
               if(errorMessage != null)
               {
                  record.addError(new BadInputStatusMessage(errorMessage));
               }
            }
         }
         catch(Exception e)
         {
            record.addError(new SystemErrorStatusMessage("Error evaluating record prior to saving: " + e.getMessage()));
         }
      }
      return records;
   }

}
