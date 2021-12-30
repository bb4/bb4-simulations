package com.barrybecker4.simulation.dungeon.model

import com.barrybecker4.common.geometry.IntLocation


class Corridor(val paths: Seq[(IntLocation, IntLocation)], var rooms: Set[Room] = Set())
