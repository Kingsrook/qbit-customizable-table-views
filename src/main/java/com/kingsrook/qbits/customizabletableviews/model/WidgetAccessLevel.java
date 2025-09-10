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
import com.kingsrook.qqq.backend.core.model.metadata.possiblevalues.PossibleValueEnum;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.QMetaDataProducingPossibleValueEnum;


/*******************************************************************************
 ** WidgetAccessLevel - possible value enum
 *******************************************************************************/
@QMetaDataProducingPossibleValueEnum()
public enum WidgetAccessLevel implements PossibleValueEnum<String>
{
   HAS_ACCESS("Has Access");

   private final String label;

   public static final String NAME = "WidgetAccessLevel";



   /*******************************************************************************
    **
    *******************************************************************************/
   WidgetAccessLevel(String label)
   {
      this.label = label;
   }



   /*******************************************************************************
    ** Get instance by id
    **
    *******************************************************************************/
   public static WidgetAccessLevel getById(String id)
   {
      if(id == null)
      {
         return (null);
      }

      for(WidgetAccessLevel value : WidgetAccessLevel.values())
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
}
