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


import java.util.function.BiFunction;
import com.kingsrook.qbits.customizabletableviews.BaseTest;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldMetaData;
import org.junit.jupiter.api.Test;
import static com.kingsrook.qbits.customizabletableviews.QFieldMetaDataAssert.assertThat;
import static com.kingsrook.qbits.customizabletableviews.model.FieldAccessLevel.EDITABLE_OPTIONAL;
import static com.kingsrook.qbits.customizabletableviews.model.FieldAccessLevel.EDITABLE_REQUIRED;
import static com.kingsrook.qbits.customizabletableviews.model.FieldAccessLevel.READ_ONLY;
import static org.junit.jupiter.api.Assertions.assertEquals;


/*******************************************************************************
 ** Unit test for FieldAccessLevel 
 *******************************************************************************/
class FieldAccessLevelTest extends BaseTest
{


   /*******************************************************************************
    **
    *******************************************************************************/
   @Test
   void testMerge()
   {
      FieldAccessLevel HIDDEN = null;

      assertEquals(EDITABLE_OPTIONAL, EDITABLE_OPTIONAL.merge(EDITABLE_OPTIONAL));
      assertEquals(EDITABLE_OPTIONAL, EDITABLE_OPTIONAL.merge(EDITABLE_REQUIRED));
      assertEquals(EDITABLE_OPTIONAL, EDITABLE_OPTIONAL.merge(READ_ONLY));
      assertEquals(EDITABLE_OPTIONAL, EDITABLE_OPTIONAL.merge(HIDDEN));

      assertEquals(EDITABLE_OPTIONAL, EDITABLE_REQUIRED.merge(EDITABLE_OPTIONAL));
      assertEquals(EDITABLE_REQUIRED, EDITABLE_REQUIRED.merge(EDITABLE_REQUIRED));
      assertEquals(EDITABLE_REQUIRED, EDITABLE_REQUIRED.merge(READ_ONLY));
      assertEquals(EDITABLE_REQUIRED, EDITABLE_REQUIRED.merge(HIDDEN));

      assertEquals(EDITABLE_OPTIONAL, READ_ONLY.merge(EDITABLE_OPTIONAL));
      assertEquals(EDITABLE_REQUIRED, READ_ONLY.merge(EDITABLE_REQUIRED));
      assertEquals(READ_ONLY, READ_ONLY.merge(READ_ONLY));
      assertEquals(READ_ONLY, READ_ONLY.merge(HIDDEN));

      ///////////////////////////////////////////////////////////
      // since HIDDEN means null, we can't do HIDDEN.merge...  //
      // but those use-cases are covered above, so we're good. //
      ///////////////////////////////////////////////////////////
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Test
   void testApply()
   {
      BiFunction<FieldAccessLevel, QFieldMetaData, QFieldMetaData> doApply = (accessLevel, field) ->
      {
         QFieldMetaData clone = field.clone();
         accessLevel.apply(clone);
         return (clone);
      };

      QFieldMetaData hiddenField   = new QFieldMetaData().withIsHidden(true);
      QFieldMetaData readOnlyField = new QFieldMetaData().withIsEditable(false);
      QFieldMetaData requiredField = new QFieldMetaData().withIsRequired(true);
      QFieldMetaData optionalField = new QFieldMetaData().withIsRequired(false);

      /////////////////////////////////////////////////////////
      // an optional field can be made required or read-only //
      /////////////////////////////////////////////////////////
      assertThat(optionalField)/*..............................*/.isNotHidden().isNotRequired().isEditable(); // baseline
      assertThat(doApply.apply(EDITABLE_OPTIONAL, optionalField)).isNotHidden().isNotRequired().isEditable(); // no changes
      assertThat(doApply.apply(EDITABLE_REQUIRED, optionalField)).isNotHidden().isRequired().isEditable(); // a change!
      assertThat(doApply.apply(READ_ONLY, optionalField))/*....*/.isNotHidden().isNotRequired().isNotEditable(); // a change!

      ////////////////////////////////////////////////////////////////////////////
      // a required field cannot be made read-only (but can't be made optional) //
      ////////////////////////////////////////////////////////////////////////////
      assertThat(requiredField)/*..............................*/.isNotHidden().isRequired().isEditable(); // baseline
      assertThat(doApply.apply(EDITABLE_OPTIONAL, requiredField)).isNotHidden().isRequired().isEditable(); // no changes
      assertThat(doApply.apply(EDITABLE_REQUIRED, requiredField)).isNotHidden().isRequired().isEditable(); // no changes
      assertThat(doApply.apply(READ_ONLY, requiredField))/*....*/.isNotHidden().isRequired().isEditable(); // no changes.

      /////////////////////////////////////////////////////////
      // a read only field can't have any changes made to it //
      /////////////////////////////////////////////////////////
      assertThat(readOnlyField)/*..............................*/.isNotHidden().isNotRequired().isNotEditable(); // baseline
      assertThat(doApply.apply(EDITABLE_OPTIONAL, readOnlyField)).isNotHidden().isNotRequired().isNotEditable(); // no changes
      assertThat(doApply.apply(EDITABLE_REQUIRED, readOnlyField)).isNotHidden().isNotRequired().isNotEditable(); // no changes
      assertThat(doApply.apply(READ_ONLY, readOnlyField))/*....*/.isNotHidden().isNotRequired().isNotEditable(); // no changes

      //////////////////////////////////////////////////////
      // a hidden field can't have any changes made to it //
      //////////////////////////////////////////////////////
      assertThat(hiddenField)/*..............................*/.isHidden().isNotRequired().isEditable(); // baseline
      assertThat(doApply.apply(EDITABLE_OPTIONAL, hiddenField)).isHidden().isNotRequired().isEditable(); // no changes
      assertThat(doApply.apply(EDITABLE_REQUIRED, hiddenField)).isHidden().isNotRequired().isEditable(); // no changes
      assertThat(doApply.apply(READ_ONLY, hiddenField))/*....*/.isHidden().isNotRequired().isEditable(); // no changes
   }

}