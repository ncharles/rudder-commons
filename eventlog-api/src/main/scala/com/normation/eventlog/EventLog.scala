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

import org.joda.time.DateTime
import org.joda.time.format._
import scala.collection._
import scala.xml._
import java.security.Principal
import com.normation.utils.HashcodeCaching


final case class EventActor(name:String) extends HashcodeCaching

/**
 * A type that describe on what category an event belongs to. 
 */
trait EventLogCategory

private[eventlog] final case object UnknownLogCategory extends EventLogCategory


/**
 * An EventLog is an object tracing activities on an entity.
 * It has an id (generated by the serialisation method), a type, a creation date,
 * a principal (the actor doing the action), a cause, a severity (like in syslog) and some raw data
 */
trait EventLog {
  val id : Option[Int] = None // autogenerated id, by the serialization system

  //event log type is given by the implementation class. 
  //we only precise the category. 
  /**
   * Big category of the event
   */
  val eventLogCategory : EventLogCategory
  
  /**
   * A string used as identifier for that type of event.
   * Must be unique among all events. 
   * Most of the time, the event class name minus "EventLog" is OK. 
   */
  val eventType : String
  
  val principal : EventActor
  
  val creationDate : DateTime = DateTime.now()
  
  /**
   * When we create the EventLog, it usually shouldn't have an id, so the cause cannot be set
   * That why we have the EventLogTree that holds the hierarchy of EventLogs, the cause being used only when deserializing the object 
   */
  val cause : Option[Int] = None 

  
  val severity : Int // the higher, the most important
  
  /**
   * Some more (technical) details about the event, in a semi-structured
   * format (XML). 
   * 
   * Usually, the rawData will be computed from the fields when serializing, 
   * and be used to fill the fields when deserializing
   */
  def details : NodeSeq

  /**
   * Return a copy of the object with the cause set to given Id
   */
  def copySetCause(causeId:Int) : EventLog
}

/**
 * The unspecialized Event Log. Used as a container when unserializing data, to be specialized later by the EventLogSpecializers 
 */
case class UnspecializedEventLog(
    override val id : Option[Int],
    override val principal : EventActor,
    override val creationDate : DateTime, 
    override val cause : Option[Int],
    override val severity : Int,
    override val details : NodeSeq
) extends EventLog with HashcodeCaching { 
  override val eventType = "Unknow"
  override val eventLogCategory = UnknownLogCategory
  override def copySetCause(causeId:Int) = this.copy(cause = Some(causeId))
}

object EventLog {  
  def withContent(nodes:NodeSeq) = <entry>{nodes}</entry>
  val emptyDetails = withContent(NodeSeq.Empty)
}
