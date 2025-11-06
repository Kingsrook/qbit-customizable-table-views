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


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import com.kingsrook.qbits.customizabletableviews.BaseTest;
import com.kingsrook.qbits.customizabletableviews.QFieldMetaDataAssert;
import com.kingsrook.qbits.customizabletableviews.model.CustomizableTable;
import com.kingsrook.qbits.customizabletableviews.model.FieldAccessLevel;
import com.kingsrook.qbits.customizabletableviews.model.TableView;
import com.kingsrook.qbits.customizabletableviews.model.TableViewField;
import com.kingsrook.qbits.customizabletableviews.model.TableViewRoleInt;
import com.kingsrook.qbits.customizabletableviews.model.TableViewWidget;
import com.kingsrook.qbits.customizabletableviews.model.WidgetAccessLevel;
import com.kingsrook.qqq.backend.core.actions.tables.DeleteAction;
import com.kingsrook.qqq.backend.core.actions.tables.InsertAction;
import com.kingsrook.qqq.backend.core.context.QContext;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.actions.AbstractTableActionInput;
import com.kingsrook.qqq.backend.core.model.actions.metadata.TableMetaDataInput;
import com.kingsrook.qqq.backend.core.model.actions.metadata.personalization.TableMetaDataPersonalizerInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.QInputSource;
import com.kingsrook.qqq.backend.core.model.actions.tables.delete.DeleteInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.insert.InsertInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QCriteriaOperator;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QFilterCriteria;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QQueryFilter;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QueryInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.update.UpdateInput;
import com.kingsrook.qqq.backend.core.model.metadata.fields.DynamicDefaultValueBehavior;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldType;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.tables.SectionFactory;
import com.kingsrook.qqq.backend.core.model.session.QUser;
import com.kingsrook.qqq.backend.core.modules.backend.implementations.memory.MemoryRecordStore;
import com.kingsrook.qqq.backend.core.utils.CollectionUtils;
import org.junit.jupiter.api.Test;
import static com.kingsrook.qbits.customizabletableviews.model.FieldAccessLevel.EDITABLE_OPTIONAL;
import static com.kingsrook.qbits.customizabletableviews.model.FieldAccessLevel.EDITABLE_REQUIRED;
import static com.kingsrook.qbits.customizabletableviews.model.FieldAccessLevel.READ_ONLY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


/*******************************************************************************
 ** Unit test for CustomizableTableViewsTablePersonalizer 
 *******************************************************************************/
class CustomizableTableViewsTablePersonalizerTest extends BaseTest
{

   /*******************************************************************************
    **
    *******************************************************************************/
   @Test
   void testIsTableCustomizable() throws QException
   {
      CustomizableTableViewsTablePersonalizer personalizer = new CustomizableTableViewsTablePersonalizer();

      String customizableWithIsActiveNull  = "customizableWithIsActiveNull";
      String customizableWithIsActiveFalse = "customizableWithIsActiveFalse";
      String customizableWithIsActiveTrue  = "customizableWithIsActiveTrue";
      String customizableButInsertedLater  = "customizableButInsertedLater";

      new InsertAction().execute(new InsertInput(CustomizableTable.TABLE_NAME).withRecordEntities(List.of(
         new CustomizableTable().withTableName(customizableWithIsActiveNull).withIsActive(null),
         new CustomizableTable().withTableName(customizableWithIsActiveFalse).withIsActive(false),
         new CustomizableTable().withTableName(customizableWithIsActiveTrue).withIsActive(true)
      )));

      assertFalse(personalizer.isTableCustomizable("notCustomizable"));
      assertFalse(personalizer.isTableCustomizable(customizableWithIsActiveNull));
      assertFalse(personalizer.isTableCustomizable(customizableWithIsActiveFalse));
      assertTrue(personalizer.isTableCustomizable(customizableWithIsActiveTrue));

      ///////////////////////////////////////////////////////////
      // make sure that memoizations are cleared appropriately //
      ///////////////////////////////////////////////////////////
      assertFalse(personalizer.isTableCustomizable(customizableButInsertedLater));
      new InsertAction().execute(new InsertInput(CustomizableTable.TABLE_NAME).withRecordEntities(List.of(
         new CustomizableTable().withTableName(customizableButInsertedLater).withIsActive(true)
      )));
      assertTrue(personalizer.isTableCustomizable(customizableButInsertedLater));

      new DeleteAction().execute(new DeleteInput(CustomizableTable.TABLE_NAME).withQueryFilter(new QQueryFilter(new QFilterCriteria("tableName", QCriteriaOperator.EQUALS, customizableButInsertedLater))));
      assertFalse(personalizer.isTableCustomizable(customizableButInsertedLater));
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Test
   void testMergeTableViews()
   {
      CustomizableTableViewsTablePersonalizer personalizer = new CustomizableTableViewsTablePersonalizer();

      //////////////////////////////////////////
      // handle empty and null lists of views //
      //////////////////////////////////////////
      assertNull(personalizer.mergeTableViewEntities(null));
      assertNull(personalizer.mergeTableViewEntities(Collections.emptyList()));
      assertNull(personalizer.mergeTableViewRecords(null));
      assertNull(personalizer.mergeTableViewRecords(Collections.emptyList()));

      /////////////////////////////////////////////
      // define some views with a little overlap //
      /////////////////////////////////////////////
      TableView tableView1 = new TableView().withFields(List.of(
         new TableViewField().withFieldName("a").withAccessLevel(EDITABLE_OPTIONAL),
         new TableViewField().withFieldName("b").withAccessLevel(EDITABLE_REQUIRED),
         new TableViewField().withFieldName("c").withAccessLevel(READ_ONLY)
      )).withWidgets(List.of(
         new TableViewWidget().withWidgetName("w1").withAccessLevel(WidgetAccessLevel.HAS_ACCESS),
         new TableViewWidget().withWidgetName("w3").withAccessLevel(WidgetAccessLevel.HAS_ACCESS)
      ));

      TableView tableView2 = new TableView().withFields(List.of(
         new TableViewField().withFieldName("c").withAccessLevel(EDITABLE_OPTIONAL),
         new TableViewField().withFieldName("d").withAccessLevel(EDITABLE_REQUIRED),
         new TableViewField().withFieldName("e").withAccessLevel(READ_ONLY)
      ));

      TableView tableView3 = new TableView().withFields(List.of(
         new TableViewField().withFieldName("d").withAccessLevel(EDITABLE_OPTIONAL),
         new TableViewField().withFieldName("e").withAccessLevel(EDITABLE_REQUIRED),
         new TableViewField().withFieldName("f").withAccessLevel(READ_ONLY)
      )).withWidgets(List.of(
         new TableViewWidget().withWidgetName("w9").withAccessLevel(WidgetAccessLevel.HAS_ACCESS)
      ));

      ////////////////////////////////////////////////////
      // handle empty or null list of fields or widgets //
      ////////////////////////////////////////////////////
      assertEquals(Collections.emptyMap(), tableViewToFieldMap(personalizer.mergeTableViewEntities(List.of(new TableView().withFields(null)))));
      assertEquals(Collections.emptyMap(), tableViewToFieldMap(personalizer.mergeTableViewEntities(List.of(new TableView().withFields(Collections.emptyList())))));
      assertEquals(Collections.emptyMap(), tableViewToWidgetMap(personalizer.mergeTableViewEntities(List.of(new TableView().withWidgets(null)))));
      assertEquals(Collections.emptyMap(), tableViewToWidgetMap(personalizer.mergeTableViewEntities(List.of(new TableView().withWidgets(Collections.emptyList())))));

      //////////////////////////////////////////////////////////
      // test view 1 by itself, then merged with some empties //
      //////////////////////////////////////////////////////////
      Map<String, FieldAccessLevel>  fieldMap1  = Map.of("a", EDITABLE_OPTIONAL, "b", EDITABLE_REQUIRED, "c", READ_ONLY);
      Map<String, WidgetAccessLevel> widgetMap1 = Map.of("w1", WidgetAccessLevel.HAS_ACCESS, "w3", WidgetAccessLevel.HAS_ACCESS);
      assertEquals(fieldMap1, tableViewToFieldMap(personalizer.mergeTableViewEntities(List.of(tableView1))));
      assertEquals(fieldMap1, tableViewToFieldMap(personalizer.mergeTableViewEntities(List.of(tableView1, new TableView().withFields(null)))));
      assertEquals(fieldMap1, tableViewToFieldMap(personalizer.mergeTableViewEntities(List.of(tableView1, new TableView().withFields(Collections.emptyList())))));
      assertEquals(fieldMap1, tableViewToFieldMap(personalizer.mergeTableViewEntities(List.of(new TableView().withFields(Collections.emptyList()), tableView1))));

      assertEquals(widgetMap1, tableViewToWidgetMap(personalizer.mergeTableViewEntities(List.of(tableView1))));
      assertEquals(widgetMap1, tableViewToWidgetMap(personalizer.mergeTableViewEntities(List.of(tableView1, new TableView().withWidgets(null)))));
      assertEquals(widgetMap1, tableViewToWidgetMap(personalizer.mergeTableViewEntities(List.of(tableView1, new TableView().withWidgets(Collections.emptyList())))));
      assertEquals(widgetMap1, tableViewToWidgetMap(personalizer.mergeTableViewEntities(List.of(new TableView().withWidgets(Collections.emptyList()), tableView1))));

      ///////////////////////////////////////////////////
      // now add view 2, first alone, then with view 1 //
      ///////////////////////////////////////////////////
      assertEquals(Map.of("c", EDITABLE_OPTIONAL, "d", EDITABLE_REQUIRED, "e", READ_ONLY), tableViewToFieldMap(personalizer.mergeTableViewEntities(List.of(tableView2))));
      assertEquals(Map.of("a", EDITABLE_OPTIONAL, "b", EDITABLE_REQUIRED, "c", EDITABLE_OPTIONAL, "d", EDITABLE_REQUIRED, "e", READ_ONLY), tableViewToFieldMap(personalizer.mergeTableViewEntities(List.of(tableView1, tableView2))));
      assertEquals(widgetMap1, tableViewToWidgetMap(personalizer.mergeTableViewEntities(List.of(tableView1, tableView2))));

      /////////////////////////////////////////////////////////////////////
      // now 3 merged - as entities, then as records for a check of that //
      /////////////////////////////////////////////////////////////////////
      assertEquals(Map.of("a", EDITABLE_OPTIONAL, "b", EDITABLE_REQUIRED, "c", EDITABLE_OPTIONAL, "d", EDITABLE_OPTIONAL, "e", EDITABLE_REQUIRED, "f", READ_ONLY), tableViewToFieldMap(personalizer.mergeTableViewEntities(List.of(tableView1, tableView2, tableView3))));
      assertEquals(Map.of("a", EDITABLE_OPTIONAL, "b", EDITABLE_REQUIRED, "c", EDITABLE_OPTIONAL, "d", EDITABLE_OPTIONAL, "e", EDITABLE_REQUIRED, "f", READ_ONLY), tableViewToFieldMap(personalizer.mergeTableViewRecords(List.of(tableView1.toQRecord(), tableView2.toQRecord(), tableView3.toQRecord()))));
      assertEquals(Map.of("w1", WidgetAccessLevel.HAS_ACCESS, "w3", WidgetAccessLevel.HAS_ACCESS, "w9", WidgetAccessLevel.HAS_ACCESS), tableViewToWidgetMap(personalizer.mergeTableViewEntities(List.of(tableView1, tableView3))));
      assertEquals(Map.of("w1", WidgetAccessLevel.HAS_ACCESS, "w3", WidgetAccessLevel.HAS_ACCESS, "w9", WidgetAccessLevel.HAS_ACCESS), tableViewToWidgetMap(personalizer.mergeTableViewRecords(List.of(tableView1.toQRecord(), tableView3.toQRecord()))));

      ////////////////////////////////////////////////
      // now a view with some invalid access levels //
      ////////////////////////////////////////////////
      TableView tableViewBadAccess = new TableView().withFields(List.of(
         new TableViewField().withFieldName("w").withAccessLevel(EDITABLE_OPTIONAL),
         new TableViewField().withFieldName("x").withAccessLevel("invalid"),
         new TableViewField().withFieldName("y").withAccessLevel(""),
         new TableViewField().withFieldName("z").withAccessLevel((String) null)
      ));

      ///////////////////////////////////////////////////////////////////////////////////////
      // note, here we're testing that bad access levels don't break the merger -          //
      // but - this method CAN return bad access levels, for the use-case of a single view //
      // see any-match accessLevel == null and == invalid below.                           //
      ///////////////////////////////////////////////////////////////////////////////////////
      assertEquals(Map.of("w", EDITABLE_OPTIONAL), tableViewToFieldMap(personalizer.mergeTableViewEntities(List.of(new TableView(), tableViewBadAccess))));
      assertThat(personalizer.mergeTableViewEntities(List.of(tableViewBadAccess)).getFields())
         .anyMatch(f -> "invalid".equals(f.getAccessLevel()))
         .anyMatch(f -> f.getAccessLevel() == null);
   }



   /***************************************************************************
    *
    ***************************************************************************/
   private Map<String, FieldAccessLevel> tableViewToFieldMap(TableView tableView)
   {
      Map<String, FieldAccessLevel> rs = new HashMap<>();

      for(TableViewField field : CollectionUtils.nonNullList(tableView.getFields()))
      {
         rs.put(field.getFieldName(), FieldAccessLevel.valueOf(field.getAccessLevel()));
      }

      return (rs);
   }



   /***************************************************************************
    *
    ***************************************************************************/
   private Map<String, WidgetAccessLevel> tableViewToWidgetMap(TableView tableView)
   {
      Map<String, WidgetAccessLevel> rs = new HashMap<>();

      for(TableViewWidget widget : CollectionUtils.nonNullList(tableView.getWidgets()))
      {
         rs.put(widget.getWidgetName(), WidgetAccessLevel.valueOf(widget.getAccessLevel()));
      }

      return (rs);
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Test
   void testGetEffectiveTableViewForCurrentSession() throws QException
   {
      CustomizableTableViewsTablePersonalizer personalizer = new CustomizableTableViewsTablePersonalizer();

      String tableWithoutDefaultView = "tableWithoutDefaultView";
      String tableWithDefaultView    = "tableWithDefaultView";

      QContext.getQInstance().addTable(new QTableMetaData()
         .withName(tableWithDefaultView)
         .withField(new QFieldMetaData("a", QFieldType.STRING))
         .withField(new QFieldMetaData("b", QFieldType.STRING))
         .withField(new QFieldMetaData("c", QFieldType.STRING)));

      new InsertAction().execute(new InsertInput(CustomizableTable.TABLE_NAME).withRecordEntities(List.of(
         new CustomizableTable().withId(1).withTableName(tableWithoutDefaultView).withDefaultTableViewId(null),
         new CustomizableTable().withId(2).withTableName(tableWithDefaultView).withDefaultTableViewId(1)
      )));

      //////////////////////
      // handle null user //
      //////////////////////
      QContext.getQSession().setUser(null);
      assertEmptyView(personalizer.getEffectiveTableViewForCurrentSession(tableWithoutDefaultView));

      ///////////////////////////////////////////////////////////////////////////////////////////
      // handle user with various versions of no role - where it'll try to use table's default //
      ///////////////////////////////////////////////////////////////////////////////////////////
      QContext.getQSession().setUser(new QUser().withIdReference(UUID.randomUUID().toString()));
      QContext.getQSession().setValue("roleIds", null);
      assertEmptyView(personalizer.getEffectiveTableViewForCurrentSession(tableWithoutDefaultView));

      QContext.getQSession().setUser(new QUser().withIdReference(UUID.randomUUID().toString()));
      QContext.getQSession().setValue("roleIds", "");
      assertEmptyView(personalizer.getEffectiveTableViewForCurrentSession(tableWithoutDefaultView));

      //////////////////////////////////////////////////////////////////////////////////////
      // this table has a defaultViewId, but that view doesn't exist, so get null for now //
      //////////////////////////////////////////////////////////////////////////////////////
      QContext.getQSession().setUser(new QUser().withIdReference(UUID.randomUUID().toString()));
      QContext.getQSession().setValue("roleIds", null);
      assertEmptyView(personalizer.getEffectiveTableViewForCurrentSession(tableWithDefaultView));

      /////////////////////
      // insert the view //
      /////////////////////
      new InsertAction().execute(new InsertInput(TableView.TABLE_NAME).withRecordEntities(List.of(
         new TableView().withId(1).withCustomizableTableId(2).withName("a").withFields(List.of(new TableViewField().withFieldName(tableWithDefaultView + ".a").withAccessLevel(EDITABLE_OPTIONAL))),
         new TableView().withId(2).withCustomizableTableId(2).withName("b").withFields(List.of(new TableViewField().withFieldName(tableWithDefaultView + ".b").withAccessLevel(EDITABLE_OPTIONAL))),
         new TableView().withId(3).withCustomizableTableId(2).withName("c").withFields(List.of(new TableViewField().withFieldName(tableWithDefaultView + ".c").withAccessLevel(EDITABLE_OPTIONAL)))
      )));

      ///////////////////////
      // now we can get it //
      ///////////////////////
      QContext.getQSession().setUser(new QUser().withIdReference(UUID.randomUUID().toString()));
      QContext.getQSession().setValue("roleIds", null);
      assertThat(personalizer.getEffectiveTableViewForCurrentSession(tableWithDefaultView))
         .isNotNull()
         .extracting("name").isEqualTo("a");

      /////////////////////////////////////////////////////////////
      // put roles in session - but fail to find a view-role-int //
      /////////////////////////////////////////////////////////////
      QContext.getQSession().setUser(new QUser().withIdReference(UUID.randomUUID().toString()));
      QContext.getQSession().setValue("roleIds", "1,2");
      assertEmptyView(personalizer.getEffectiveTableViewForCurrentSession(tableWithoutDefaultView));

      ///////////////////////////////
      // build some view-role ints //
      ///////////////////////////////
      new InsertAction().execute(new InsertInput(TableViewRoleInt.TABLE_NAME).withRecordEntities(List.of(
         new TableViewRoleInt().withRoleId(1).withTableViewId(1),
         new TableViewRoleInt().withRoleId(2).withTableViewId(2),
         new TableViewRoleInt().withRoleId(3).withTableViewId(3)
      )));

      //////////////////////////////////////////
      // find a single view, through role-int //
      //////////////////////////////////////////
      QContext.getQSession().setUser(new QUser().withIdReference(UUID.randomUUID().toString()));
      QContext.getQSession().setValue("roleIds", "1");
      assertThat(personalizer.getEffectiveTableViewForCurrentSession(tableWithoutDefaultView))
         .isNotNull()
         .extracting("name").isEqualTo("a");

      ////////////////////////////////////////////////////
      // find a different single view, through role-int //
      ////////////////////////////////////////////////////
      QContext.getQSession().setUser(new QUser().withIdReference(UUID.randomUUID().toString()));
      QContext.getQSession().setValue("roleIds", "2");
      assertThat(personalizer.getEffectiveTableViewForCurrentSession(tableWithoutDefaultView))
         .isNotNull()
         .extracting("name").isEqualTo("b");

      /////////////////////////////////////////////////////////////////////////////
      // find 2 views (and when they merge, their name is lost) through role int //
      /////////////////////////////////////////////////////////////////////////////
      QContext.getQSession().setUser(new QUser().withIdReference(UUID.randomUUID().toString()));
      QContext.getQSession().setValue("roleIds", "2,3");

      /////////////////////////////////////////////////////////////
      // do this last lookup twice, clearing memory record store //
      // in between to demonstrate memoization being used.       //
      /////////////////////////////////////////////////////////////
      for(int i = 0; i < 2; i++)
      {
         TableView effectiveTableViewForCurrentSession = personalizer.getEffectiveTableViewForCurrentSession(tableWithoutDefaultView);
         assertThat(effectiveTableViewForCurrentSession)
            .isNotNull()
            .extracting("name").isNull();
         assertEquals(2, effectiveTableViewForCurrentSession.getFields().size());
         assertThat(effectiveTableViewForCurrentSession.getFields())
            .anyMatch(f -> f.getFieldName().endsWith(".b"))
            .anyMatch(f -> f.getFieldName().endsWith(".c"));

         MemoryRecordStore.getInstance().reset();
      }
   }



   /***************************************************************************
    *
    ***************************************************************************/
   private void assertEmptyView(TableView tableView)
   {
      assertNotNull(tableView);
      assertThat(tableView.getFields()).isNullOrEmpty();
      assertThat(tableView.getWidgets()).isNullOrEmpty();
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Test
   void testApplyViewToTable()
   {
      CustomizableTableViewsTablePersonalizer personalizer = new CustomizableTableViewsTablePersonalizer();
      QTableMetaData                          tableMetaData;

      TableMetaDataInput tableActionInput = new TableMetaDataInput();

      ///////////////////////////
      // empty with empty case //
      ///////////////////////////
      tableMetaData = personalizer.applyViewToTable(
         new TableView(),
         new QTableMetaData(),
         tableActionInput);
      assertThat(tableMetaData.getFields()).isNullOrEmpty();
      assertThat(tableMetaData.getSections()).isNullOrEmpty();

      QTableMetaData baseTable = new QTableMetaData()
         .withPrimaryKeyField("id")
         .withField(new QFieldMetaData("id", QFieldType.STRING).withIsEditable(false))
         .withField(new QFieldMetaData("secret", QFieldType.STRING).withIsHidden(true))
         .withField(new QFieldMetaData("mandatory", QFieldType.STRING).withIsRequired(true))
         .withField(new QFieldMetaData("optional", QFieldType.STRING))
         .withSection(SectionFactory.defaultT1("id").withName("s0"))
         .withSection(SectionFactory.defaultT2("secret", "mandatory").withName("s1"))
         .withSection(SectionFactory.defaultT2("optional").withName("s2"))
         .withSection(SectionFactory.defaultT2().withWidgetName("lilWidgy").withName("w0"));

      ////////////////////////////////////////////////////////////////////////////////
      // no fields or widgets in view - just get the primaryKey and mandatory field //
      ////////////////////////////////////////////////////////////////////////////////
      tableMetaData = personalizer.applyViewToTable(
         new TableView().withFields(List.of()),
         baseTable.clone(), tableActionInput);

      assertEquals(2, tableMetaData.getFields().size());
      QFieldMetaDataAssert.assertThat(tableMetaData.getField("id")).isNotHidden().isNotEditable().isNotRequired();
      QFieldMetaDataAssert.assertThat(tableMetaData.getField("mandatory")).isNotHidden().isEditable().isRequired();
      assertEquals(2, tableMetaData.getSections().size());
      assertWith(tableMetaData.getSections().get(0),
         section -> assertEquals("s0", section.getName()),
         section -> assertEquals(List.of("id"), section.getFieldNames()));
      assertWith(tableMetaData.getSections().get(1),
         section -> assertEquals("s1", section.getName()),
         section -> assertEquals(List.of("mandatory"), section.getFieldNames()));

      ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      // make sure access levels are applied correctly - meaning, they don't open up things that aren't supposed to be //
      ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      tableMetaData = personalizer.applyViewToTable(
         new TableView().withFields(List.of(
            new TableViewField().withFieldName("x.id").withAccessLevel(EDITABLE_OPTIONAL),
            new TableViewField().withFieldName("x.secret").withAccessLevel(EDITABLE_OPTIONAL),
            new TableViewField().withFieldName("x.mandatory").withAccessLevel(EDITABLE_OPTIONAL),
            new TableViewField().withFieldName("x.optional").withAccessLevel(EDITABLE_OPTIONAL)
         )),
         baseTable.clone(), tableActionInput);

      assertEquals(4, tableMetaData.getFields().size());
      QFieldMetaDataAssert.assertThat(tableMetaData.getField("id")).isNotHidden().isNotEditable().isNotRequired();
      QFieldMetaDataAssert.assertThat(tableMetaData.getField("secret")).isHidden().isEditable().isNotRequired();
      QFieldMetaDataAssert.assertThat(tableMetaData.getField("mandatory")).isNotHidden().isEditable().isRequired();
      QFieldMetaDataAssert.assertThat(tableMetaData.getField("optional")).isNotHidden().isEditable().isNotRequired();
      assertEquals(3, tableMetaData.getSections().size());
      assertWith(tableMetaData.getSections().get(0),
         section -> assertEquals("s0", section.getName()),
         section -> assertEquals(List.of("id"), section.getFieldNames()));
      assertWith(tableMetaData.getSections().get(1),
         section -> assertEquals("s1", section.getName()),
         section -> assertEquals(List.of("secret", "mandatory"), section.getFieldNames()));
      assertWith(tableMetaData.getSections().get(2),
         section -> assertEquals("s2", section.getName()),
         section -> assertEquals(List.of("optional"), section.getFieldNames()));

      Asserter.of(tableMetaData.getSections().get(2))
         .doAssert(it -> assertEquals("s2", it.getName()))
         .doAssert(it -> assertEquals(List.of("optional"), it.getFieldNames()));

      ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      // make sure access levels are applied correctly - meaning, they don't open up things that aren't supposed to be //
      ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      tableMetaData = personalizer.applyViewToTable(
         new TableView().withWidgets(List.of(
            new TableViewWidget().withWidgetName("lilWidgy").withAccessLevel(WidgetAccessLevel.HAS_ACCESS)
         )),
         baseTable.clone(), tableActionInput);
      assertEquals(2, tableMetaData.getFields().size());
      assertEquals(3, tableMetaData.getSections().size()); // one for id, one for mandatory, and 1 for our widget
      assertEquals(List.of("s0", "s1", "w0"), tableMetaData.getSections().stream().map(s -> s.getName()).toList());
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Test
   void testDynamicDefaultValueFields()
   {
      ///////////////////////////
      // empty with empty case //
      ///////////////////////////
      QTableMetaData baseTable = new QTableMetaData()
         .withPrimaryKeyField("id")
         .withField(new QFieldMetaData("id", QFieldType.STRING).withIsEditable(false))
         .withField(new QFieldMetaData("secret", QFieldType.STRING).withIsHidden(true))
         .withField(new QFieldMetaData("mandatory", QFieldType.STRING).withIsRequired(true))
         .withField(new QFieldMetaData("createDate", QFieldType.DATE_TIME).withBehavior(DynamicDefaultValueBehavior.CREATE_DATE))
         .withField(new QFieldMetaData("optional", QFieldType.STRING))
         .withSection(SectionFactory.defaultT1("id").withName("s0"))
         .withSection(SectionFactory.defaultT2("secret", "mandatory").withName("s1"))
         .withSection(SectionFactory.defaultT2("optional", "createDate").withName("s2"));

      ////////////////////////////////////////////////////////////////////////
      // for actions that aren't insert or update, just get pkey & required //
      ////////////////////////////////////////////////////////////////////////
      for(AbstractTableActionInput actionInput : List.of(new QueryInput(), new DeleteInput(), new TableMetaDataInput()))
      {
         QTableMetaData tableMetaData = new CustomizableTableViewsTablePersonalizer().applyViewToTable(
            new TableView().withFields(List.of()),
            baseTable.clone(), actionInput);

         assertEquals(2, tableMetaData.getFields().size());
         QFieldMetaDataAssert.assertThat(tableMetaData.getField("id")).isNotNull();
         QFieldMetaDataAssert.assertThat(tableMetaData.getField("mandatory")).isNotNull();
      }

      /////////////////////////////////////////////////////
      // for insert or update action, get createDate too //
      /////////////////////////////////////////////////////
      for(AbstractTableActionInput actionInput : List.of(new InsertInput(), new UpdateInput()))
      {
         QTableMetaData tableMetaData = new CustomizableTableViewsTablePersonalizer().applyViewToTable(
            new TableView().withFields(List.of()),
            baseTable.clone(), actionInput);

         assertEquals(3, tableMetaData.getFields().size());
         QFieldMetaDataAssert.assertThat(tableMetaData.getField("id")).isNotNull();
         QFieldMetaDataAssert.assertThat(tableMetaData.getField("mandatory")).isNotNull();
         QFieldMetaDataAssert.assertThat(tableMetaData.getField("createDate")).isNotNull();
      }
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Test
   void testCustomizeTable() throws QException
   {
      CustomizableTableViewsTablePersonalizer personalizer = new CustomizableTableViewsTablePersonalizer();
      QTableMetaData                          tableMetaData;

      /////////////////
      // empty cases //
      /////////////////
      assertNull(personalizer.customizeTable(new TableMetaDataPersonalizerInput()));
      QTableMetaData tableWithNoName = new QTableMetaData();
      assertSame(tableWithNoName, personalizer.customizeTable(new TableMetaDataPersonalizerInput().withTableMetaData(tableWithNoName)));

      ////////////////////////////////////////////////////////////////
      // normal case let's assume adequately covered by testExecute //
      ////////////////////////////////////////////////////////////////
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Test
   void testExecute() throws QException
   {
      CustomizableTableViewsTablePersonalizer personalizer = new CustomizableTableViewsTablePersonalizer();
      QTableMetaData                          tableMetaData;

      /////////////////
      // empty cases //
      /////////////////
      assertNull(personalizer.execute(new TableMetaDataPersonalizerInput()));

      QTableMetaData baseTable = new QTableMetaData()
         .withName("baseTable")
         .withPrimaryKeyField("id")
         .withField(new QFieldMetaData("id", QFieldType.STRING).withIsEditable(false))
         .withField(new QFieldMetaData("optional", QFieldType.STRING))
         .withSection(SectionFactory.defaultT1("id").withName("s0"))
         .withSection(SectionFactory.defaultT2("optional").withName("s1"))
         .withSection(SectionFactory.defaultT2().withWidgetName("lilWidgy").withName("w0"));
      QContext.getQInstance().addTable(baseTable);

      ///////////////////////////////////////////////////
      // system inputSource gets same as default table //
      ///////////////////////////////////////////////////
      assertSame(baseTable, personalizer.execute(new TableMetaDataPersonalizerInput().withTableMetaData(baseTable).withInputSource(QInputSource.SYSTEM)));

      ////////////////////////////////////////////////////////////////
      // user gets same table as input if table is not customizable //
      ////////////////////////////////////////////////////////////////
      TableMetaDataPersonalizerInput input = new TableMetaDataPersonalizerInput().withTableMetaData(baseTable).withInputSource(QInputSource.USER);
      assertSame(baseTable, personalizer.execute(input));

      /////////////////////////////////////////
      // mark table as actively customizable //
      /////////////////////////////////////////
      new InsertAction().execute(new InsertInput(CustomizableTable.TABLE_NAME).withRecordEntities(List.of(
         new CustomizableTable().withTableName(baseTable.getName()).withIsActive(true))));

      ///////////////////////////////////////////
      // assert it's pretty empty with no view //
      ///////////////////////////////////////////
      QTableMetaData personalizedTable = personalizer.execute(input);
      assertNotSame(baseTable, personalizedTable);
      assertEquals(1, personalizedTable.getFields().size());
      assertEquals(List.of("id"), personalizedTable.getFields().values().stream().map(s -> s.getName()).toList());
      assertEquals(1, personalizedTable.getSections().size());
      assertEquals(List.of("s0"), personalizedTable.getSections().stream().map(s -> s.getName()).toList());

      ////////////////
      // add a view //
      ////////////////
      new InsertAction().execute(new InsertInput(TableView.TABLE_NAME).withRecordEntities(List.of(
         new TableView().withId(1).withCustomizableTableId(1).withName("a")
            .withFields(List.of(new TableViewField().withFieldName(baseTable.getName() + ".optional").withAccessLevel(EDITABLE_OPTIONAL)))
            .withWidgets(List.of(new TableViewWidget().withWidgetName("lilWidgy").withAccessLevel(WidgetAccessLevel.HAS_ACCESS))))));

      new InsertAction().execute(new InsertInput(TableViewRoleInt.TABLE_NAME).withRecordEntities(List.of(
         new TableViewRoleInt().withRoleId(101).withTableViewId(1))));

      QContext.getQSession().setUser(new QUser().withIdReference(UUID.randomUUID().toString()));
      QContext.getQSession().setValue("roleIds", "101");

      //////////////////////////////////////
      // assert we got the customizations //
      //////////////////////////////////////
      personalizedTable = personalizer.execute(input);
      assertNotSame(baseTable, personalizedTable);
      assertEquals(2, personalizedTable.getFields().size());
      assertEquals(Set.of("id", "optional"), personalizedTable.getFields().values().stream().map(s -> s.getName()).collect(Collectors.toSet()));
      assertEquals(3, personalizedTable.getSections().size());
      assertEquals(List.of("s0", "s1", "w0"), personalizedTable.getSections().stream().map(s -> s.getName()).toList());
   }



   /***************************************************************************
    *
    ***************************************************************************/
   public static class Asserter<E>
   {
      public E e;



      private Asserter(E e)
      {
         this.e = e;
      }



      public static <X> Asserter<X> of(X e)
      {
         return (new Asserter<X>(e));
      }



      public Asserter<E> doAssert(Consumer<E> assertion)
      {
         assertion.accept(e);
         return (this);
      }
   }



   /***************************************************************************
    *
    ***************************************************************************/
   @SafeVarargs
   private <T> void assertWith(T o, Consumer<T>... consumers)
   {
      Arrays.stream(consumers).forEach(c -> c.accept(o));
   }



   /***************************************************************************
    *
    ***************************************************************************/
   private Set<String> getFieldNames(QTableMetaData tableMetaData)
   {
      return CollectionUtils.nonNullMap(tableMetaData.getFields()).keySet();
   }

}