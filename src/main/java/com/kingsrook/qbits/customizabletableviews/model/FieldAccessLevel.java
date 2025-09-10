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


import java.util.Objects;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.possiblevalues.PossibleValueEnum;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.QMetaDataProducingPossibleValueEnum;


/*******************************************************************************
 ** FieldAccessLevel - possible value enum
 *******************************************************************************/
@QMetaDataProducingPossibleValueEnum()
public enum FieldAccessLevel implements PossibleValueEnum<String>
{
   EDITABLE_OPTIONAL("Editable - Optional", 1),
   EDITABLE_REQUIRED("Editable - Required", 2),
   READ_ONLY("Read Only", 3);

   private final String label;
   private final int    restrictiveness;

   public static final String NAME = "FieldAccessLevel";



   /*******************************************************************************
    **
    *******************************************************************************/
   FieldAccessLevel(String label, int restrictiveness)
   {
      this.label = label;
      this.restrictiveness = restrictiveness;
   }



   /*******************************************************************************
    ** Get instance by id
    **
    *******************************************************************************/
   public static FieldAccessLevel getById(String id)
   {
      if(id == null)
      {
         return (null);
      }

      for(FieldAccessLevel value : FieldAccessLevel.values())
      {
         if(Objects.equals(value.name(), id))
         {
            return (value);
         }
      }

      return (null);
   }



   /*******************************************************************************
    ** Getter for id
    **
    *******************************************************************************/
   public String getId()
   {
      return name();
   }



   /*******************************************************************************
    ** Getter for label
    **
    *******************************************************************************/
   public String getLabel()
   {
      return label;
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Override
   public String getPossibleValueId()
   {
      return (getId());
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Override
   public String getPossibleValueLabel()
   {
      return (getLabel());
   }



   /***************************************************************************
    *
    ***************************************************************************/
   public FieldAccessLevel merge(FieldAccessLevel that)
   {
      if(that == null)
      {
         return (this);
      }

      if(that.restrictiveness < this.restrictiveness)
      {
         return (that);
      }

      return (this);
   }



   /***************************************************************************
    *
    ***************************************************************************/
   public void apply(QFieldMetaData fieldMetaData)
   {
      if(fieldMetaData.getIsHidden())
      {
         //////////////////////////////////////////////////////////////////////////////////////
         // if the field is hidden, then no access level is allowed to un-hide it. so, noop. //
         //////////////////////////////////////////////////////////////////////////////////////
         return;
      }

      if(!fieldMetaData.getIsEditable())
      {
         /////////////////////////////////////////////////////////////////////////////////
         // if the field is read-only, only a "hidden" level would be allowed to change //
         // but, we don't actually have a hidden level - that's just the default if no  //
         // level is defined.  so, also noop if field is editable.                      //
         /////////////////////////////////////////////////////////////////////////////////
         return;
      }

      if(fieldMetaData.getIsRequired())
      {
         ////////////////////////////////////////////////////////////////////////////////////
         // if the field is required, we're not allowed to change it.  it must be entered. //
         ////////////////////////////////////////////////////////////////////////////////////
         return;
      }

      /////////////////////////////////////////////////////////////////////////////////////////////
      // so - we'll only be here for "editable, optional" fields - where we can change 2 things: //
      /////////////////////////////////////////////////////////////////////////////////////////////
      if(this == READ_ONLY)
      {
         fieldMetaData.setIsEditable(false);
      }
      else if(this == EDITABLE_REQUIRED)
      {
         fieldMetaData.setIsRequired(true);
      }
   }



   /***************************************************************************
    * check if this field access level is valid for the input field meta data.
    * @return string error message if it isn't valid - null if it is.
    ***************************************************************************/
   public String validateForField(QFieldMetaData fieldMetaData)
   {
      if(fieldMetaData.getIsHidden())
      {
         //////////////////////////////////////////////////////////////////////////////////////
         // if the field is hidden, then no access level is allowed to un-hide it. so, noop. //
         //////////////////////////////////////////////////////////////////////////////////////
         return ("This field is defined as hidden by the system - you may not make it " + getLabel());
      }

      if(!fieldMetaData.getIsEditable())
      {
         if(this == READ_ONLY)
         {
            return (null);
         }

         /////////////////////////////////////////////////////////////////////////////////
         // if the field is read-only, only a "hidden" level would be allowed to change //
         // but, we don't actually have a hidden level - that's just the default if no  //
         // level is defined.  so, also noop if field is editable.                      //
         /////////////////////////////////////////////////////////////////////////////////
         return ("This field is defined as Read Only by the system - you may not make it " + getLabel());
      }

      if(fieldMetaData.getIsRequired())
      {
         if(this == EDITABLE_REQUIRED)
         {
            return (null);
         }

         ////////////////////////////////////////////////////////////////////////////////////
         // if the field is required, we're not allowed to change it.  it must be entered. //
         ////////////////////////////////////////////////////////////////////////////////////
         return ("This field is defined as Required by the system - you may not make it " + getLabel());
      }

      ///////////////////////////////////////////////////////////////////////
      // else - field is optional - so you can change it however you like. //
      ///////////////////////////////////////////////////////////////////////
      return (null);
   }

}
