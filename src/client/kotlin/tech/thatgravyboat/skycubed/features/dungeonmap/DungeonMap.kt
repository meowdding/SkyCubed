package tech.thatgravyboat.skycubed.features.dungeonmap

import earth.terrarium.olympus.client.utils.Orientation
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.Items
import net.minecraft.world.level.saveddata.maps.MapId
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import org.joml.Vector2i
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.dungeonmap.position.MapPosition
import tech.thatgravyboat.skycubed.features.dungeonmap.position.MutableVec2i
import tech.thatgravyboat.skycubed.features.dungeonmap.position.Rectangle2D
import tech.thatgravyboat.skycubed.features.dungeonmap.position.RoomPosition

private val HALLWAY_SIZE = 4

class DungeonMap(val instance: DungeonInstance) {

    val roomsPerAxis = MutableVec2i()
    val mapBox = Rectangle2D()
    val roomMap: Array<Array<DungeonRoom?>> = Array(instance.getRoomAmount()) { arrayOfNulls(instance.getRoomAmount()) }
    val puzzles: MutableList<DungeonRoom> = mutableListOf()
    val doors = mutableSetOf<DungeonDoor>()
    var lastPlayerPosition = RoomPosition(-1, -1, instance)
    var lastRoomSwitch = 0L
    var cachedMapId: Int? = null

    var roomWidth = 0
    val halfRoomWidth get() = roomWidth / 2
    val roomAndDoorSize get() = roomWidth + HALLWAY_SIZE
    val doorPositions by lazy {
        mapOf(
            Orientation.HORIZONTAL to Vector2i(-halfRoomWidth - 3, halfRoomWidth / 2 - 2),
            Orientation.VERTICAL to Vector2i(halfRoomWidth / 2 - 2, -halfRoomWidth - 3),
        )
    }

    init {
        roomMap.forEach {
            println(it.joinToString(" | ", prefix = "[ ", postfix = " ]"))
        }
        SkyBlockAPI.eventBus.register(this)
    }

    fun onRemove() {
        SkyBlockAPI.eventBus.unregister(this)
    }

    private fun processWithCachedInfo() {
        val mapId = MapId(this.cachedMapId ?: return)
        val state = this.getMapData(mapId) ?: return
        processMapData(state)
    }

    private fun getMapData(mapId: MapId) = McLevel.self.getMapData(mapId)

    @Subscription
    @TimePassed("5t")
    fun onTick(event: TickEvent) {
        if (McPlayer.self == null) return
        val stack = McPlayer.inventory[8]
        val isMap = stack.`is`(Items.FILLED_MAP)

        if (!isMap && cachedMapId != null) {
            processWithCachedInfo()
            return
        }

        val mapIdComponent = stack.get(DataComponents.MAP_ID)
        if (mapIdComponent != null && this.cachedMapId == null) {
            this.cachedMapId = mapIdComponent.id()
        }
        if (mapIdComponent == null) {
            return
        }
        val mapData = getMapData(mapIdComponent) ?: return
        processMapData(mapData)
    }

    private fun timeout() {
        val roomPosition = instance.getPlayerPosition().convertTo<RoomPosition>()
        val lastPosition = this.lastPlayerPosition

        if (roomPosition == lastPosition) {
            return
        }

        lastPosition.set(roomPosition)
        lastRoomSwitch = System.currentTimeMillis()
    }

    private fun canUpdateRoom(): Boolean = System.currentTimeMillis() - lastRoomSwitch > 1000

    private fun processMapData(mapData: MapItemSavedData) {
        if (instance.phase == DungeonPhase.BEFORE) instance.phase = DungeonPhase.CLEAR
        this.timeout()
        if (!this.mapBox.isDefined()) this.tryFindMapBox(mapData)
        if (this.mapBox.isDefined()) parseRooms(mapData)

        if (mapBox.isDefined()) {
            mapData.colorAt(mapBox.topLeft(), debug = true)
            mapData.colorAt(mapBox.bottomRight(), debug = true)
        }
        parseDecorations(mapData)
    }

    private fun parseDecorations(mapData: MapItemSavedData) {
        var index = 0
        mapData.decorations.forEach { decoration ->
            index = this.instance.applyOffset(index)
            val player = instance.players[index] ?: return@forEach
            with(decoration) {
                player.setPosition(MapPosition((x + 128) / 2, (y + 128) / 2, instance))
            }
            player.setRotation(decoration.rot)
        }
    }

    private fun isSpawn(color: Byte) = DungeonRoomType.SPAWN.isColor(color)
    private fun MapItemSavedData.colorAt(index: Int, debug: Boolean = false): Byte {
        if (index >= this.colors.size) return -1
        val byte = this.colors[index]

        if (debug && index >= 128) {
            this.colors[index - 128] = DungeonRoomType.BLOOD.color
        }

        return byte
    }

    private fun MapItemSavedData.colorAt(column: Int, row: Int, debug: Boolean = false) =
        colorAt(row * 128 + column, debug)

    private fun MapItemSavedData.colorAt(vector2i: Vector2i, debug: Boolean = false) =
        colorAt(vector2i.x, vector2i.y, debug)

    private fun tryFindMapBox(mapData: MapItemSavedData) {
        for ((index, color) in mapData.colors.withIndex()) {
            val column = index % 128
            val row = index / 128

            if (column + 15 > 128) continue
            if (!isSpawn(color) || !isSpawn(mapData.colorAt(index + 7)) || !isSpawn(mapData.colorAt(index + 15))) continue

            var width = 0
            while (isSpawn(mapData.colorAt(index + width))) {
                width++
            }

            this.roomWidth = width
            var left = column % roomAndDoorSize
            var top = row % roomAndDoorSize

            if (this.instance.floor <= 1) left += roomAndDoorSize
            if (this.instance.floor == 0) top += roomAndDoorSize

            this.mapBox.left = left
            this.mapBox.top = top
            mapData.colorAt(left, top, debug = true)

            var roomsPerRow = 0
            var mostRight = this.mapBox.left

            while (mostRight < 128 - this.mapBox.left) {
                mostRight += this.roomAndDoorSize
                roomsPerRow++
            }

            var roomsPerColumn = 0
            var mostDown = this.mapBox.top

            while (mostDown < 128 - this.mapBox.top) {
                mostDown += this.roomAndDoorSize
                roomsPerColumn++
            }

            this.roomsPerAxis.x = roomsPerRow
            this.roomsPerAxis.y = roomsPerColumn

            this.mapBox.right = mostRight
            this.mapBox.bottom = mostDown
            mapData.colorAt(mostDown, mostDown, debug = true)

            break
        }
    }

    private val roomPositions
        get() = buildList {
            roomMap.indices.map { x ->
                roomMap.indices.map { y ->
                    add(RoomPosition(x, y, instance))
                }
            }
        }


    private fun setRoomAt(position: RoomPosition, room: DungeonRoom) {
        room.positions.add(position)
        roomMap[position] = room
    }

    private fun getRoomAt(position: RoomPosition): DungeonRoom? {
        val dungeonRoom = roomMap[position] ?: return null
        dungeonRoom.mergedWith?.let { merged ->
            setRoomAt(position, merged)

            if (dungeonRoom == merged) {
                dungeonRoom.mergedWith = null
                return dungeonRoom
            }

            merged.positions.addAll(dungeonRoom.positions)
            return merged
        }

        return dungeonRoom
    }

    private fun parseRooms(mapData: MapItemSavedData) {
        parseDoors(mapData)

        var puzzleCount = 0
        this.puzzles.forEach { it.puzzleDirty = true }
        roomPositions.forEach { roomPosition ->
            val balls = roomPosition.copy()
            val mapPosition = roomPosition.convertTo<MapPosition>()
            val roomType = DungeonRoomType.getByColor(mapData.colorAt(mapPosition))

            if (roomType == DungeonRoomType.PUZZLE) {
                val puzzleRoom = getRoomAt(roomPosition) ?: return@forEach
                if (puzzleRoom.puzzleId == -1) {
                    puzzles.add(puzzleRoom)
                }
                if (!(instance.isLastColumnPuzzlesOnly() && roomPosition.x == this.roomMap.size - 1)) {
                    puzzleRoom.puzzleId = puzzleCount++
                    puzzleRoom.puzzleDirty = false
                }
            }

            roomType?.let { updateRoom(roomPosition, it) }
            updateCheckmark(roomPosition, mapData)
            roomType?.takeIf { it == DungeonRoomType.NORMAL }
                ?.let { updateNormalRoom(balls, balls.convertTo(), mapData) }
        }
    }

    private fun updateNormalRoom(roomPosition: RoomPosition, mapPosition: MapPosition, mapData: MapItemSavedData) {
        val currentRoom = getRoomAt(roomPosition)
        val roomAbove = getRoomIfNormal(
            roomPosition.copy().subtract(0, 1),
            mapPosition.copy().subtract(0, 1),
            mapData
        )
        val roomLeft = getRoomIfNormal(
            roomPosition.copy().subtract(1, 0),
            mapPosition.copy().subtract(1, 0),
            mapData
        )
        val roomAboveRight = getRoomIfNormal(
            roomPosition.copy().plus(1, -1),
            mapPosition.copy().plus(roomWidth, -1),
            mapData
        )

        if (roomAbove == null && roomLeft == null && currentRoom == null) {
            setNewRoomAt(roomPosition, DungeonRoomType.NORMAL)
            return
        }

        if (currentRoom == null) return

        if (currentRoom.roomType == DungeonRoomType.UNKNOWN || currentRoom.checkmark == Checkmark.UNKNOWN) {
            currentRoom.roomType = DungeonRoomType.NORMAL
            currentRoom.checkmark = Checkmark.OPENED
        }

        if (roomAbove != null && roomAbove !== currentRoom && roomAbove.roomType == DungeonRoomType.NORMAL) {
            setMerged(roomPosition.copy().subtract(0, 1), currentRoom)
        }
        if (roomLeft != null && roomLeft !== currentRoom && roomLeft.roomType == DungeonRoomType.NORMAL) {
            setMerged(roomPosition.copy().subtract(1, 0), currentRoom)
        }
        if (roomAboveRight != null && roomAboveRight !== currentRoom && roomAboveRight.roomType == DungeonRoomType.NORMAL && currentRoom === roomAbove) {
            setMerged(roomPosition.copy().plus(1, -1), currentRoom)
        }
    }

    private fun setMerged(roomPosition: RoomPosition, mergedWith: DungeonRoom) {
        getRoomAt(roomPosition)?.mergedWith = mergedWith
        setRoomAt(roomPosition, mergedWith)
    }

    private fun getRoomIfNormal(
        roomPosition: RoomPosition,
        mapPosition: MapPosition,
        mapData: MapItemSavedData,
    ): DungeonRoom? {
        if (DungeonRoomType.NORMAL.isColor(mapData.colorAt(mapPosition, debug = true))) {
            return getRoomAt(roomPosition)
        }
        return null
    }


    private fun updateCheckmark(roomPosition: RoomPosition, mapData: MapItemSavedData) {
        val room = getRoomAt(roomPosition)?.takeUnless { it.checkmark == Checkmark.DONE } ?: return

        val roomMiddle = roomPosition.convertTo<MapPosition>().add(this.halfRoomWidth, this.halfRoomWidth)
        val color = mapData.colorAt(roomMiddle)
        val checkmark = Checkmark.getByColor(color)?.takeUnless { it == room.checkmark } ?: return

        room.checkmark = checkmark
    }

    private fun updateRoom(roomPosition: RoomPosition, roomType: DungeonRoomType) {
        if (roomType == DungeonRoomType.BLOOD && instance.phase.ordinal < DungeonPhase.BLOOD.ordinal) {
            instance.phase = DungeonPhase.BLOOD
        }

        val roomAt = getRoomAt(roomPosition)
        if (roomAt == null) {
            setNewRoomAt(roomPosition, roomType)
            return
        }

        if (roomAt.roomType != roomType) {
            roomAt.roomType = roomType
            roomAt.checkmark = Checkmark.OPENED
        }
        if (roomAt.checkmark == Checkmark.UNKNOWN && roomAt.roomType != DungeonRoomType.UNKNOWN) {
            roomAt.checkmark = Checkmark.OPENED
        }
    }

    private fun setNewRoomAt(roomPosition: RoomPosition, roomType: DungeonRoomType) {
        val dungeonRoom = DungeonRoom(instance, roomType)
        dungeonRoom.checkmark = if (roomType == DungeonRoomType.UNKNOWN) Checkmark.UNKNOWN else Checkmark.OPENED
        setRoomAt(roomPosition, dungeonRoom)
    }

    private fun parseDoors(mapData: MapItemSavedData) {
        roomPositions.forEach { roomPosition ->
            val mapPosition = roomPosition.convertTo<MapPosition>().add(
                halfRoomWidth,
                halfRoomWidth,
                MapPosition(instance)
            ) as MapPosition // new vector may not be needed since convertTo creates a new instance anyway (most times)?

            doorPositions.entries.forEach doors@{ (orientation, position) ->
                val doorType = DungeonDoorType.getByColor(mapData.colorAt(mapPosition.copy().add(position), true)) ?: return@doors

                doors.find { it.isAt(orientation, mapPosition) }?.let { door ->
                    door.type = doorType
                    return@doors
                }

                doors.add(DungeonDoor(roomPosition, orientation, doorType))
            }
        }
    }

    private operator fun <T> Array<Array<T?>>.get(roomPos: RoomPosition): T? {
        if (roomPos.x < 0 || roomPos.y < 0 || roomPos.x >= this.size || roomPos.y >= this[0].size) {
            SkyCubed.warn("Tried to access room out of bounds at ({},{})", roomPos.x, roomPos.y)
            return null
        }
        return this[roomPos.x][roomPos.y]
    }

    private operator fun <T> Array<Array<T?>>.set(roomPos: RoomPosition, value: T?) {
        if (roomPos.x < 0 || roomPos.y < 0 || roomPos.x >= this.size || roomPos.y >= this[0].size) {
            SkyCubed.warn("Tried to set room out of bounds at ({},{})", roomPos.x, roomPos.y)
            return
        }
        this[roomPos.x][roomPos.y] = value
    }
}
