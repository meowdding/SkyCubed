package tech.thatgravyboat.skycubed.features.dungeonmap

import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabListChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.AreaChangeEvent
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skycubed.SkyCubed
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

    @Subscription(priority = Subscription.LOWEST)
    fun onAreaChange(event: AreaChangeEvent) {
        floor = DungeonAPI.dungeonFloor?.floorNumber ?: -1
    }

    @Subscription(priority = Subscription.LOWEST)
    fun onTabWidgetChange(event: TabListChangeEvent) = runCatching {
        var index = 0
        DungeonAPI.teammates.filterNot { it === DungeonAPI.ownPlayer }.forEach { player ->
            if (index >= players.size) {
                SkyCubed.error("Requested index $index is out of bounds for player list length ${this.players.size}")
                return@forEach
            }
            if (players[index] == null) {
                players[index] = DungeonPlayer(player.name, player.classLevel ?: -1, player.dungeonClass, this)
            }
            this.players[index]?.update(player)
            this.playerIdMap[player.name] = index++
        }
        DungeonAPI.ownPlayer?.let { player ->
            if (index >= players.size) {
                SkyCubed.error("Requested own player index $index is out of bounds for player list length ${this.players.size}")
                return@let
            }
            if (players[index] == null) {
                players[index] = DungeonPlayer(player.name, player.classLevel ?: -1, player.dungeonClass, this)
            }
            players[index]?.update(player)
            playerIdMap[player.name] = index
        }
    }

    private fun createPuzzlesArray(event: TabWidgetChangeEvent): Array<String>? {
        return event.new.firstOrNull()?.filter { it.isDigit() }?.toIntOrNull()?.let { Array(it) { "???" } }
    }

    @Subscription
    @OnlyWidget(TabWidget.PUZZLES)
    fun onPuzzleWidgetChange(event: TabWidgetChangeEvent) = runCatching {
        puzzles = puzzles ?: createPuzzlesArray(event)

        val localPuzzles = puzzles ?: return@runCatching

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

    fun getRoomAmount(): Int = when (this.floor) {
        0 -> 4
        in 1..3 -> 5
        in 4..7 -> 6
        else -> -1
    }

    fun isLastColumnPuzzlesOnly(): Boolean = this.floor in 4..6

    fun getPlayerPosition(): DungeonPosition<*> {
        return (McPlayer.self?.position() ?: Vec3.ZERO).let { WorldPosition(it.x.toInt(), it.z.toInt(), this) }
    }

    inline fun runCatching(runnable: () -> Unit) {
        try {
            runnable()
        } catch (throwable: Throwable) {
            SkyCubed.warn("Uncaught exception!", throwable)
        }
    }

    inline fun <T> runCatching(runnable: () -> T): T? {
        try {
            return runnable()
        } catch (throwable: Throwable) {
            SkyCubed.warn("Uncaught exception!", throwable)
        }
        return null
    }
}
