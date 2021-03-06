/*
*************************************************************************************
* Copyright 2011 Normation SAS
*************************************************************************************
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*************************************************************************************
*/

package com.normation.eventlog

import java.security.Principal
import org.joda.time.DateTime
import scala.xml.NodeSeq
import com.normation.utils.HashcodeCaching

final case object DummyEventLogCategory extends EventLogCategory

final case class EventLogWithValues(
    override val eventDetails : EventLogDetails,
    val firstValue : String,
    val secondValue : String
) extends EventLog with HashcodeCaching {
  override val eventType = DummyEventLogType
  override val eventLogCategory = DummyEventLogCategory
  override def details =     
    <Entry>
      <firstValue>{firstValue}</firstValue>
      <secondValue>{secondValue}</secondValue>
    </Entry>
  override def copySetCause(causeId:Int) = this.copy(eventDetails.copy(cause = Some(causeId)))
    
}


object DummyEventLogType extends EventLogType {
  def serialize = "dummy"
}

