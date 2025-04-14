package tech.thatgravyboat.skycubed.features.dungeonmap

import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonAPI
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonFloor
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.info.SecretsActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.AreaChangeEvent
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skycubed.features.dungeonmap.position.DungeonPosition
import tech.thatgravyboat.skycubed.features.dungeonmap.position.WorldPosition

class DungeonInstance(val serverId: String) {
    var floor: Int = -1
        set(value) {
            field = value
            if (field != -1) {
                map = DungeonMap(this)
            }
        }

    var map: DungeonMap? = null
        private set

    var playerNick: String? = null

    val players = Array<DungeonPlayer?>(5) { null }
    val currentPlayer: DungeonPlayer? = null

    val playerIdMap = mutableMapOf<String, Int>()

    var puzzles: Array<String>? = null

    val knownPuzzles: MutableList<Pair<PuzzleType, Int>> = mutableListOf()

    var timeStarted: Long = 0

    var phase: DungeonPhase = DungeonPhase.BEFORE
        set(value) {
            if (field == DungeonPhase.BEFORE) {
                timeStarted = System.currentTimeMillis()
            }
            field = value
        }

    init {
        SkyBlockAPI.eventBus.register(this)
    }

    @Subscription
    fun onSecretWidgetChange(event: SecretsActionBarWidgetChangeEvent) {
        event.new.forEach { println(it) }
    }

    @Subscription(priority = Subscription.LOWEST)
    fun onAreaChange(event: AreaChangeEvent) {
        floor = when (DungeonAPI.dungeonFloor) { // todo move to sbapi
            DungeonFloor.E -> 0
            DungeonFloor.F1, DungeonFloor.M1 -> 1
            DungeonFloor.F2, DungeonFloor.M2 -> 2
            DungeonFloor.F3, DungeonFloor.M3 -> 3
            DungeonFloor.F4, DungeonFloor.M4 -> 4
            DungeonFloor.F5, DungeonFloor.M5 -> 5
            DungeonFloor.F6, DungeonFloor.M6 -> 6
            DungeonFloor.F7, DungeonFloor.M7 -> 7
            else -> -1
        }
    }

    @Subscription(Subscription.LOWEST)
    fun onTabWidgetChange(event: TabListChangeEvent) {
        var index = 0
        DungeonAPI.teammates.filterNot { it === DungeonAPI.ownPlayer }.forEach { player ->
            if (players[index] == null) {
                players[index] = DungeonPlayer(player.name, player.classLevel ?: -1, player.dungeonClass, this)
            }
            this.players[index]?.update(player)
            println("Setting player ${player.name} to $index")
            this.playerIdMap[player.name] = index++
        }
        DungeonAPI.ownPlayer?.let { player ->
            if (players[index] == null) {
                players[index] = DungeonPlayer(player.name, player.classLevel ?: -1, player.dungeonClass, this)
            }
            players[index]?.update(player)
            println("Setting player ${player.name} to $index")
            playerIdMap[player.name] = index
        }
    }

    private fun createPuzzlesArray(event: TabWidgetChangeEvent): Array<String>? {
        return event.new.firstOrNull()?.filter { it.isDigit() }?.toIntOrNull()?.let { Array(it) { "???" } }
    }

    @Subscription
    @OnlyWidget(TabWidget.PUZZLES)
    fun onPuzzleWidgetChange(event: TabWidgetChangeEvent) {
        puzzles = puzzles ?: createPuzzlesArray(event)

        val localPuzzles = puzzles ?: return

        event.new.drop(1).forEachIndexed { index, puzzle ->
            if (puzzle.equals(localPuzzles[index], ignoreCase = true)) {
                return@forEachIndexed
            }
            localPuzzles[index] = puzzle
            this.knownPuzzles.removeIf { it.second == index }
            this.knownPuzzles.add(PuzzleType.byTabName(puzzle) to index)
            this.knownPuzzles.sortWith(Comparator.comparingInt { it.second })
        }
    }

    fun onRemove() {
        SkyBlockAPI.eventBus.unregister(this)
        this.map?.onRemove()
    }

    fun applyOffset(index: Int): Int {
        if (index < players.size - 1 && players[index]?.isDead == true) {
            return applyOffset(index + 1)
        }
        return index
    }

    fun getRoomAmount(): Int {
        return when (this.floor) {
            0 -> 4
            in 1..3 -> 5
            in 4..7 -> 6
            else -> -1
        }
    }

    fun isLastColumnPuzzlesOnly(): Boolean {
        return when (this.floor) {
            in 4..6 -> true
            else -> false
        }
    }

    fun getPlayerPosition(): DungeonPosition<*> {
        return (McPlayer.self?.position() ?: Vec3.ZERO).let { WorldPosition(it.x.toInt(), it.z.toInt(), this) }
    }

}