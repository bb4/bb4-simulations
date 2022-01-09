package com.barrybecker4.simulation.dungeon.generator.organic.room

import com.barrybecker4.simulation.dungeon.generator.organic.room.sprout.SproutLocation
import com.barrybecker4.simulation.dungeon.model.{DungeonMap, Room}
import com.barrybecker4.simulation.dungeon.model.Orientation.*
import java.awt.Dimension


case class RoomExpander(dungeonMap: DungeonMap) {

  /**
   * Incrementally expand the room until it hits something or reaches size preference,
   * then back up two cells to leave room for buffer between rooms
   * @return a version of the room that has been expanded to as close to its desired size as we can get
   */
  def expand(room: Room, preferredDim: Dimension, sproutLocation: SproutLocation): Room = {
    var stillExpanding = true
    var expandedRoom: Room = room

    while (stillExpanding) {
      var newRoom = attemptToExpandHorizontally(expandedRoom, preferredDim, sproutLocation)
      if (newRoom.isDefined) {
        expandedRoom = newRoom.get
        newRoom = attemptToExpandVertically(expandedRoom, preferredDim, sproutLocation)
        if (newRoom.isDefined)
          expandedRoom = newRoom.get
        else
          stillExpanding = false
      } else {
        stillExpanding = false
      }
    }
    expandedRoom
  }

  private def attemptToExpandHorizontally(room: Room, dimension: Dimension, sprout: SproutLocation): Option[Room] = {
    if (sprout.orientation == Horizontal) {
      // expand 1 cell in both vertical directions
      // check 4 corners, if clear, then add
      //if (okToExpand(expandedBox)) {
      //  return Some(Room(expandedBox))
      //}
      None
    } else {
      // expand 2 cells to the left or right depending on direction
      None
    }
  }

  private def attemptToExpandVertically(room: Room, dimension: Dimension, sprout: SproutLocation): Option[Room] = {
    if (sprout.orientation == Vertical) {
      // expand 1 cell in both horizontal directions
      None
    } else {
      // expand 2 cells to the top or bottom depending on direction
      None
    }
  }
}
