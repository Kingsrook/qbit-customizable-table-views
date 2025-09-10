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

package com.kingsrook.qbits.customizabletableviews;


import com.kingsrook.qbits.customizabletableviews.logic.CustomizableTableViewsTablePersonalizer;
import com.kingsrook.qbits.customizabletableviews.model.CustomizableTable;
import com.kingsrook.qbits.customizabletableviews.model.TableView;
import com.kingsrook.qbits.customizabletableviews.model.TableViewField;
import com.kingsrook.qbits.customizabletableviews.model.TableViewRoleInt;
import com.kingsrook.qbits.customizabletableviews.model.TableViewWidget;
import com.kingsrook.qqq.backend.core.actions.metadata.personalization.TableMetaDataPersonalizerInterface;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.code.QCodeReference;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QAppSection;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitMetaDataProducer;


/*******************************************************************************
 **
 *******************************************************************************/
public class CustomizableTableViewsQBitProducer implements QBitMetaDataProducer<CustomizableTableViewsQBitConfig>
{
   private CustomizableTableViewsQBitConfig customizableTableViewsQBitConfig;



   /***************************************************************************
    **
    ***************************************************************************/
   @Override
   public QBitMetaData getQBitMetaData()
   {
      QBitMetaData qBitMetaData = new QBitMetaData()
         .withGroupId("com.kingsrook.qbits")
         .withArtifactId("customizable-table-views")
         .withVersion("0.1.0")
         .withNamespace(getNamespace())
         .withConfig(getQBitConfig());

      return qBitMetaData;
   }



   /***************************************************************************
    *
    ***************************************************************************/
   public static QAppSection getAppSection(QInstance qInstance)
   {
      return (new QAppSection().withName("tableViews")
         .withTable(CustomizableTable.TABLE_NAME)
         .withTable(TableView.TABLE_NAME)
         .withTable(TableViewField.TABLE_NAME)
         .withTable(TableViewWidget.TABLE_NAME)
         .withTable(TableViewRoleInt.TABLE_NAME)
      );
   }



   /***************************************************************************
    *
    ***************************************************************************/
   public static void activateTableMetaDataPersonalizerInterface(QInstance qInstance)
   {
      qInstance.addSupplementalCustomizer(TableMetaDataPersonalizerInterface.CUSTOMIZER_TYPE, new QCodeReference(CustomizableTableViewsTablePersonalizer.class));
   }



   /*******************************************************************************
    ** Getter for qBitConfig
    *******************************************************************************/
   @Override
   public CustomizableTableViewsQBitConfig getQBitConfig()
   {
      return (this.customizableTableViewsQBitConfig);
   }



   /*******************************************************************************
    ** Setter for qBitConfig
    *******************************************************************************/
   public void setQBitConfig(CustomizableTableViewsQBitConfig customizableTableViewsQBitConfig)
   {
      this.customizableTableViewsQBitConfig = customizableTableViewsQBitConfig;
   }



   /*******************************************************************************
    ** Fluent setter for qBitConfig
    *******************************************************************************/
   public CustomizableTableViewsQBitProducer withQBitConfig(CustomizableTableViewsQBitConfig customizableTableViewsQBitConfig)
   {
      this.customizableTableViewsQBitConfig = customizableTableViewsQBitConfig;
      return (this);
   }

}
