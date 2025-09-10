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


import java.util.List;
import java.util.Optional;
import com.kingsrook.qbits.customizabletableviews.BaseTest;
import com.kingsrook.qbits.customizabletableviews.model.FieldAccessLevel;
import com.kingsrook.qbits.customizabletableviews.model.TableViewField;
import com.kingsrook.qqq.backend.core.context.QContext;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldType;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


/*******************************************************************************
 ** Unit test for TableViewFieldCustomizer 
 *******************************************************************************/
class TableViewFieldCustomizerTest extends BaseTest
{

   /*******************************************************************************
    **
    *******************************************************************************/
   @Test
   void test() throws QException
   {
      QContext.getQInstance().addTable(new QTableMetaData()
         .withName("testTable")
         .withField(new QFieldMetaData("secret", QFieldType.STRING).withIsHidden(true))
         .withField(new QFieldMetaData("mandatory", QFieldType.STRING).withIsRequired(true))
         .withField(new QFieldMetaData("optional", QFieldType.STRING)));

      assertError(new TableViewField().withFieldName("testTable.notAField").withAccessLevel(FieldAccessLevel.READ_ONLY));
      assertError(new TableViewField().withFieldName("testTable.notAField").withAccessLevel(FieldAccessLevel.EDITABLE_REQUIRED));
      assertError(new TableViewField().withFieldName("testTable.notAField").withAccessLevel(FieldAccessLevel.EDITABLE_OPTIONAL));

      assertError(new TableViewField().withFieldName("testTable.secret").withAccessLevel(FieldAccessLevel.READ_ONLY));
      assertError(new TableViewField().withFieldName("testTable.secret").withAccessLevel(FieldAccessLevel.EDITABLE_REQUIRED));
      assertError(new TableViewField().withFieldName("testTable.secret").withAccessLevel(FieldAccessLevel.EDITABLE_OPTIONAL));

      assertError(new TableViewField().withFieldName("testTable.mandatory").withAccessLevel(FieldAccessLevel.READ_ONLY));
      assertNoError(new TableViewField().withFieldName("testTable.mandatory").withAccessLevel(FieldAccessLevel.EDITABLE_REQUIRED));
      assertError(new TableViewField().withFieldName("testTable.mandatory").withAccessLevel(FieldAccessLevel.EDITABLE_OPTIONAL));

      assertNoError(new TableViewField().withFieldName("testTable.optional").withAccessLevel(FieldAccessLevel.READ_ONLY));
      assertNoError(new TableViewField().withFieldName("testTable.optional").withAccessLevel(FieldAccessLevel.EDITABLE_REQUIRED));
      assertNoError(new TableViewField().withFieldName("testTable.optional").withAccessLevel(FieldAccessLevel.EDITABLE_OPTIONAL));
   }



   /***************************************************************************
    *
    ***************************************************************************/
   private void assertNoError(TableViewField tableViewField) throws QException
   {
      QRecord qRecord = tableViewField.toQRecord();
      new TableViewFieldCustomizer().preInsertOrUpdate(null, List.of(qRecord), false, Optional.empty());
      assertThat(qRecord.getErrors()).isNullOrEmpty();
   }



   /***************************************************************************
    *
    ***************************************************************************/
   private void assertError(TableViewField tableViewField) throws QException
   {
      QRecord qRecord = tableViewField.toQRecord();
      new TableViewFieldCustomizer().preInsertOrUpdate(null, List.of(qRecord), false, Optional.empty());
      assertThat(qRecord.getErrors()).hasSizeGreaterThan(0);
   }

}