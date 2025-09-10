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


import java.util.List;
import java.util.Map;
import com.kingsrook.qbits.customizabletableviews.BaseTest;
import com.kingsrook.qqq.backend.core.actions.tables.InsertAction;
import com.kingsrook.qqq.backend.core.context.QContext;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.actions.tables.insert.InsertInput;
import com.kingsrook.qqq.backend.core.model.actions.values.SearchPossibleValueSourceInput;
import com.kingsrook.qqq.backend.core.model.metadata.dashboard.QWidgetMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldType;
import com.kingsrook.qqq.backend.core.model.metadata.possiblevalues.QPossibleValue;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.tables.SectionFactory;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


/*******************************************************************************
 ** Unit test for CustomizableTableWidgetPVS 
 *******************************************************************************/
class CustomizableTableWidgetPVSTest extends BaseTest
{

   /*******************************************************************************
    **
    *******************************************************************************/
   @Test
   void test() throws QException
   {
      QTableMetaData testTable = new QTableMetaData()
         .withName("testTable")
         .withPrimaryKeyField("id")
         .withField(new QFieldMetaData("id", QFieldType.STRING).withIsEditable(false))
         .withField(new QFieldMetaData("optional", QFieldType.STRING))
         .withSection(SectionFactory.defaultT1("id").withName("s0"))
         .withSection(SectionFactory.defaultT2("optional").withName("s1"))
         .withSection(SectionFactory.defaultT2().withWidgetName("lilWidgy").withName("w0"))
         .withSection(SectionFactory.defaultT2().withWidgetName("biggyWidgy").withName("w1"));
      QContext.getQInstance().addTable(testTable);
      QContext.getQInstance().addWidget(new QWidgetMetaData().withName("lilWidgy").withLabel("Lil' Widgy"));
      QContext.getQInstance().addWidget(new QWidgetMetaData().withName("biggyWidgy").withLabel("Biggy Widgy"));

      /////////////////////////////////////////////
      // mark table as customizable, with a view //
      /////////////////////////////////////////////
      new InsertAction().execute(new InsertInput(CustomizableTable.TABLE_NAME).withRecordEntities(List.of(
         new CustomizableTable().withTableName(testTable.getName()).withIsActive(true))));

      Integer tableViewId = 17;
      new InsertAction().execute(new InsertInput(TableView.TABLE_NAME).withRecordEntities(List.of(
         new TableView().withId(tableViewId).withCustomizableTableId(1).withName("a"))));

      List<QPossibleValue<String>> possibleValues = new CustomizableTableWidgetPVS().search(new SearchPossibleValueSourceInput().withOtherValues(Map.of("tableViewId", tableViewId)));
      assertEquals(2, possibleValues.size());
      assertThat(possibleValues).anyMatch(pv -> pv.getId().equals("biggyWidgy"));
      assertThat(possibleValues).anyMatch(pv -> pv.getId().equals("lilWidgy"));

      possibleValues = new CustomizableTableWidgetPVS().search(new SearchPossibleValueSourceInput().withSearchTerm("Big").withOtherValues(Map.of("tableViewId", tableViewId)));
      assertEquals(1, possibleValues.size());
      assertThat(possibleValues).anyMatch(pv -> pv.getId().equals("biggyWidgy"));

      possibleValues = new CustomizableTableWidgetPVS().search(new SearchPossibleValueSourceInput().withPossibleValueSourceName(CustomizableTableWidgetPVS.NAME).withIdList(List.of("lilWidgy")).withOtherValues(Map.of("tableViewId", tableViewId)));
      assertEquals(1, possibleValues.size());
      assertThat(possibleValues).anyMatch(pv -> pv.getId().equals("lilWidgy"));
   }

}