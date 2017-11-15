/**
 * Copyright (C) 2015-2017 Lightbend Inc. <http://www.lightbend.com>
 */
package akka.stream

import akka.stream.impl.{ GraphStageTag, IslandTag, TraversalBuilder }

import scala.annotation.unchecked.uncheckedVariance

trait Graph[+S <: Shape, +M] {
  /**
   * Type-level accessor for the shape parameter of this graph.
   */
  type Shape = S @uncheckedVariance
  /**
   * The shape of a graph is all that is externally visible: its inlets and outlets.
   */
  def shape: S
  /**
   * INTERNAL API.
   *
   * Every materializable element must be backed by a stream layout module
   */
  private[stream] def traversalBuilder: TraversalBuilder

  def withAttributes(attr: Attributes): Graph[S, M]

  def named(name: String): Graph[S, M] = addAttributes(Attributes.name(name))

  /**
   * Put an asynchronous boundary around this `Graph`
   */
  def async: Graph[S, M] = addAttributes(Attributes.asyncBoundary)

  /**
   * Put an asynchronous boundary around this `Graph`
   *
   * @param dispatcher Run the graph on this dispatcher
   */
  def async(dispatcher: String) =
    addAttributes(
      Attributes.asyncBoundary and ActorAttributes.dispatcher(dispatcher)
    )

  /**
   * Put an asynchronous boundary around this `Graph`
   *
   * @param dispatcher Run the graph on this dispatcher
   * @param inputBufferSize Set the input buffer to this size for the graph
   */
  def async(dispatcher: String, inputBufferSize: Int) =
    addAttributes(
      Attributes.asyncBoundary and ActorAttributes.dispatcher(dispatcher)
        and Attributes.inputBuffer(inputBufferSize, inputBufferSize)
    )

  def addAttributes(attr: Attributes): Graph[S, M] = withAttributes(traversalBuilder.attributes and attr)
}
