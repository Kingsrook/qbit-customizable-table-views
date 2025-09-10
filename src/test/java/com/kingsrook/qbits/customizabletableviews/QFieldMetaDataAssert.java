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


import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldMetaData;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;


/***************************************************************************
 *
 ***************************************************************************/
public class QFieldMetaDataAssert extends AbstractAssert<QFieldMetaDataAssert, QFieldMetaData>
{

   /***************************************************************************
    *
    ***************************************************************************/
   protected QFieldMetaDataAssert(QFieldMetaData qFieldMetaData, Class<?> selfType)
   {
      super(qFieldMetaData, selfType);
   }



   /***************************************************************************
    *
    ***************************************************************************/
   public static QFieldMetaDataAssert assertThat(QFieldMetaData qFieldMetaData)
   {
      return (new QFieldMetaDataAssert(qFieldMetaData, QFieldMetaDataAssert.class));
   }



   /***************************************************************************
    *
    ***************************************************************************/
   public QFieldMetaDataAssert isHidden()
   {
      Assertions.assertThat(actual.getIsHidden()).isTrue();
      return (this);
   }



   /***************************************************************************
    *
    ***************************************************************************/
   public QFieldMetaDataAssert isNotHidden()
   {
      Assertions.assertThat(actual.getIsHidden()).isFalse();
      return (this);
   }



   /***************************************************************************
    *
    ***************************************************************************/
   public QFieldMetaDataAssert isRequired()
   {
      Assertions.assertThat(actual.getIsRequired()).isTrue();
      return (this);
   }



   /***************************************************************************
    *
    ***************************************************************************/
   public QFieldMetaDataAssert isNotRequired()
   {
      Assertions.assertThat(actual.getIsRequired()).isFalse();
      return (this);
   }



   /***************************************************************************
    *
    ***************************************************************************/
   public QFieldMetaDataAssert isEditable()
   {
      Assertions.assertThat(actual.getIsEditable()).isTrue();
      return (this);
   }



   /***************************************************************************
    *
    ***************************************************************************/
   public QFieldMetaDataAssert isNotEditable()
   {
      Assertions.assertThat(actual.getIsEditable()).isFalse();
      return (this);
   }
}
