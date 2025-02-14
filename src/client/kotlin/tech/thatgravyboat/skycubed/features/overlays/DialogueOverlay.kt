package tech.thatgravyboat.skycubed.features.overlays

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.level.LeftClickEntityEvent
import tech.thatgravyboat.skyblockapi.api.events.level.RightClickEntityEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenForegroundEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.extentions.left
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentRegex
import tech.thatgravyboat.skyblockapi.utils.regex.component.match
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.api.displays.*
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.utils.SkyCubedTextures.backgroundBox
import tech.thatgravyboat.skycubed.utils.pushPop
import kotlin.math.max

object DialogueOverlay : Overlay {

    private val regex = ComponentRegex("\\[NPC] (?<name>[\\w.\\s]+): (?<message>.+)")
    private val yesNoRegex = listOf(
        ComponentRegex("Select an option: (?<yes>\\[YES]) (?<no>\\[NO]) "),
        ComponentRegex("\\nAccept the trapper's task to hunt the animal\\?\\nClick an option: (?<yes>\\[YES]) - (?<no>\\[NO])"),
    )

    private val queue = mutableListOf<Pair<Component, Component>>()
    private var nextCheck = 0L
    private var yesNo: Pair<String, String>? = null
    private var displayedYesNo = false
    private var hudOverlayDisplay: Display = Displays.empty()
    private var inventoryOverlayDisplay: Display = Displays.empty()
    private var lastClickedEntities: MutableMap<LivingEntity, Long> = mutableMapOf()

    override val name: Component = Text.of("Dialogue")
    override val position: Position = Position()
    override val bounds: Pair<Int, Int> = 0 to 0
    override val moveable: Boolean = false
    override val enabled: Boolean get() = config.enabled

    private val config get() = OverlaysConfig.npc
    private val displayDuration get() = (config.durationPerMessage * 1000f).toLong()
    private val displayActionDuration get() = (config.durationForActionMessage * 1000f).toLong()

    private var containerLeftPos: Int? = null

    @Subscription
    @OnlyOnSkyBlock
    fun onChatReceived(event: ChatReceivedEvent.Pre) {
        if (!enabled) return

        regex.match(event.component, "name", "message") { (name, message) ->
            queue.add(name to message)
            if (config.hideChatMessage) event.cancel()
        }
        yesNoRegex.match(event.component, "yes", "no") { (yes, no) ->
            yesNo = (yes.style.clickEvent?.value ?: "") to (no.style.clickEvent?.value ?: "")
            if (config.hideChatMessage) event.cancel()
        }
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onInventoryInit(event: ContainerInitializedEvent) {
        containerLeftPos = event.screen.left
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onEntityClick(event: RightClickEntityEvent) {
        handleEntityClick(event.entity)
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onEntityLeftClick(event: LeftClickEntityEvent) {
        handleEntityClick(event.entity)
    }

    private fun handleEntityClick(event: Entity) {
        if (!enabled) return

        val entity = event as? LivingEntity ?: return
        lastClickedEntities[entity] = System.currentTimeMillis()
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onTick(event: TickEvent) {
        if (!enabled) return

        if (System.currentTimeMillis() > nextCheck) {
            nextCheck = System.currentTimeMillis() + displayDuration

            if (queue.isEmpty()) {
                if (yesNo != null && !displayedYesNo) {
                    hudOverlayDisplay = createYesNoDisplay()
                } else {
                    reset()
                }
            } else {
                val (name, message) = queue.removeFirstOrNull() ?: return
                createMainDisplay(name, message, McClient.window.guiScaledWidth / 3)?.let { hudOverlayDisplay = it }
                val inventoryOverlayWidth =
                    containerLeftPos.takeIf { it != null } ?: (McClient.window.guiScaledWidth / 4)
                createMainDisplay(name, message, inventoryOverlayWidth - 30)?.let { inventoryOverlayDisplay = it }
            }
        }

        val (yesCommand, noCommand) = yesNo ?: return
        val isYes = InputConstants.isKeyDown(McClient.window.window, InputConstants.KEY_Y)
        val isNo = InputConstants.isKeyDown(McClient.window.window, InputConstants.KEY_N)

        val command = if (isYes) yesCommand else if (isNo) noCommand else return
        McClient.sendCommand(command.removePrefix("/"))
        reset()
    }

    private fun createMainDisplay(name: Component, message: Component, maxWidth: Int): Display? {
        val entity = lastClickedEntities.keys.find { npc ->
            McLevel.self.getEntitiesOfClass(ArmorStand::class.java, npc.boundingBox)
                .any { it.customName?.stripped == name.stripped }
        } ?: lastClickedEntities.keys.firstOrNull()

        entity?.let { lastClickedEntities[it] = System.currentTimeMillis() }

        val entityDisplay = entity?.let {
            Displays.pushPop(
                { translate(0f, 0f, -100f) },
                Displays.entity(it, 60, 60, 35, 80f, 40f)
            )
        }

        val npcNameDisplay = Displays.pushPop(
            { translate(60f, -8f, 0f) },
            Displays.background(
                backgroundBox,
                Displays.padding(5, Displays.component(name, maxWidth))
            )
        )

        val npcTextDisplay = Displays.padding(15, Displays.component(message, maxWidth))

        return listOfNotNull(
            entityDisplay,
            Displays.pushPop(
                { translate(0f, 40f, 0f) },
                Displays.background(
                    backgroundBox,
                    listOf(
                        npcNameDisplay,
                        npcTextDisplay,
                    ).asLayer(),
                ),
            )
        ).asLayer()
    }

    private fun createYesNoDisplay(): Display {
        displayedYesNo = true
        nextCheck = System.currentTimeMillis() + displayActionDuration

        val options = listOf(
            Text.of("[Y]es") { this.color = TextColor.GREEN },
            Text.of("[N]o") { this.color = TextColor.RED }
        )

        val yesNoDisplay = options.map {
            Displays.background(backgroundBox, Displays.padding(5, Displays.text(it)))
        }.toColumn(10, Alignment.START)

        return listOf(
            hudOverlayDisplay,
            Displays.pushPop(
                {
                    translate(
                        hudOverlayDisplay.getWidth() - yesNoDisplay.getWidth() - 10f,
                        -1f * yesNoDisplay.getHeight() + 30f, // 40f because of the text box move, -10f for padding
                        -1000f
                    )
                },
                yesNoDisplay
            ),
        ).asLayer()
    }

    private fun reset() {
        lastClickedEntities = lastClickedEntities.filterValues {
            it + max(displayDuration, displayActionDuration) + 5000 > System.currentTimeMillis()
        }.toMutableMap()
        yesNo = null
        displayedYesNo = false
        hudOverlayDisplay = Displays.empty()
        inventoryOverlayDisplay = Displays.empty()
        nextCheck = 0
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onForeground(event: RenderScreenForegroundEvent) {
        if (!enabled) return
        val graphics = event.graphics

        graphics.pushPop {
            translate(0f, 0f, 100f)
            render(graphics, 0, 0)
        }
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        if (McScreen.self != null && !McScreen.isOf<ChatScreen>()) {
            inventoryOverlayDisplay.render(
                graphics,
                5,
                graphics.guiHeight() / 2 - inventoryOverlayDisplay.getHeight() / 2,
            )
        } else {
            hudOverlayDisplay.render(graphics, graphics.guiWidth() / 2, graphics.guiHeight() - 120, 0.5f, 1f)
        }
    }

}