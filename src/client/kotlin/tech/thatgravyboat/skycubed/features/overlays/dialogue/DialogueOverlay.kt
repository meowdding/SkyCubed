package tech.thatgravyboat.skycubed.features.overlays.dialogue

import com.mojang.blaze3d.platform.InputConstants
import me.owdding.ktmodules.Module
import me.owdding.lib.displays.*
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenForegroundEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.extentions.left
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop
import tech.thatgravyboat.skyblockapi.utils.extentions.scissor
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentRegex
import tech.thatgravyboat.skyblockapi.utils.regex.component.match
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.api.overlays.EditableProperty
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.api.overlays.RegisterOverlay
import tech.thatgravyboat.skycubed.config.overlays.NpcOverlayConfig
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.utils.SkyCubedTextures
import kotlin.math.max

@Module
@RegisterOverlay
object DialogueOverlay : Overlay {

    private val regex = ComponentRegex("\\[NPC] (?<name>[^:]+): (?<message>.+)")
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

    override val name: Component = Text.of("Dialogue")
    override val position: Position = Position()
    override val bounds: Pair<Int, Int> = 0 to 0
    override val properties: Collection<EditableProperty> = setOf()
    override val enabled: Boolean get() = config.enabled

    private val config get() = NpcOverlayConfig
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
            yesNo = Pair(
                ((yes.style.clickEvent as? ClickEvent.RunCommand)?.command ?: ""),
                ((no.style.clickEvent as? ClickEvent.RunCommand)?.command ?: ""),
            )
            if (config.hideChatMessage) event.cancel()
        }
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onServerChange(event: ServerChangeEvent) {
        queue.clear()
        reset()
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onInventoryInit(event: ContainerInitializedEvent) {
        containerLeftPos = event.screen.left
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onTick(event: TickEvent) {
        if (!enabled) return

        if (System.currentTimeMillis() > nextCheck) {
            if (queue.isEmpty()) {
                nextCheck = System.currentTimeMillis() + displayDuration
                if (yesNo != null && !displayedYesNo) {
                    hudOverlayDisplay = createYesNoDisplay()
                } else {
                    reset()
                }
            } else {
                val (name, message) = queue.removeFirstOrNull() ?: return
                val npc = DialogueNpcs.get(name.stripped)
                val inventoryOverlayWidth = containerLeftPos.takeIf { it != null } ?: (McClient.window.guiScaledWidth / 4)

                nextCheck = System.currentTimeMillis() + (displayDuration * npc.durationModifier).toLong()

                createMainDisplay(name, message, npc, McClient.window.guiScaledWidth / 3)?.let { hudOverlayDisplay = it }
                createMainDisplay(name, message, npc, inventoryOverlayWidth - 30)?.let { inventoryOverlayDisplay = it }
            }
        }

        val (yesCommand, noCommand) = yesNo ?: return
        val isYes = InputConstants.isKeyDown(McClient.window.window, InputConstants.KEY_1)
        val isNo = InputConstants.isKeyDown(McClient.window.window, InputConstants.KEY_2)

        val command = if (isYes) yesCommand else if (isNo) noCommand else return
        McClient.sendCommand(command.removePrefix("/"))
        reset()
    }

    private fun createMainDisplay(name: Component, message: Component, npc: DialogueNpc, maxWidth: Int): Display? {
        val entity = DialogueEntities.get(name.stripped, npc)

        val entityDisplay = entity?.let {
            val display = Displays.entity(it, 60, 60, 35, 80f, 40f)
            object : Display {
                override fun getWidth(): Int = display.getWidth()
                override fun getHeight(): Int = display.getHeight()
                override fun render(graphics: GuiGraphics) {
                    val width = getWidth()
                    val height = getHeight()
                    val half = width / 2
                    graphics.pushPop {
                        translate(0f, 0f, -100f)
                        graphics.scissor(-half, -height, width * 2, height * 2) {
                            display.render(graphics)
                        }
                    }
                }
            }
        }

        val npcNameDisplay = Displays.pushPop(
            Displays.background(
                SkyCubedTextures.backgroundBox,
                Displays.padding(5, Displays.component(name, maxWidth))
            )
        ) { translate(60f.takeIf { entityDisplay != null } ?: 8f, -8f, 0f) }

        val npcTextDisplay = Displays.component(message, maxWidth)

        return listOfNotNull(
            entityDisplay,
            Displays.pushPop(
                Displays.background(
                    SkyCubedTextures.backgroundBox,
                    listOf(
                        npcNameDisplay,
                        Displays.padding(
                            15,
                            ((maxWidth * 0.8f).toInt() - npcTextDisplay.getWidth()).coerceAtLeast(0) + 15,
                            15,
                            15,
                            npcTextDisplay
                        )
                    ).asLayer(),
                )
            )
            { translate(0f, 40f, 0f) },
        ).asLayer()
    }

    private fun createYesNoDisplay(): Display {
        displayedYesNo = true
        nextCheck = System.currentTimeMillis() + displayActionDuration

        val options = listOf(
            Text.of("[1] Yes") { this.color = TextColor.GREEN },
            Text.of("[2] No") { this.color = TextColor.RED }
        )

        val yesNoDisplay = options.map {
            Displays.background(SkyCubedTextures.backgroundBox, Displays.padding(5, Displays.text(it)))
        }.toColumn(10, Alignment.START)

        return listOf(
            hudOverlayDisplay,
            Displays.pushPop(yesNoDisplay)
            {
                translate(
                    hudOverlayDisplay.getWidth() - yesNoDisplay.getWidth() - 10f,
                    -1f * yesNoDisplay.getHeight() + 30f, // 40f because of the text box move, -10f for padding
                    -1000f
                )
            }
        ).asLayer()
    }

    private fun reset() {
        DialogueEntities.updateCache(max(displayDuration, displayActionDuration) + 5000)
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
        val screen = McScreen.self
        if (screen is AbstractContainerScreen<*>) {
            inventoryOverlayDisplay.render(
                graphics,
                5,
                graphics.guiHeight() / 2 - inventoryOverlayDisplay.getHeight() / 2,
            )
        } else if (screen is ChatScreen || screen == null) {
            hudOverlayDisplay.render(graphics, graphics.guiWidth() / 2, graphics.guiHeight() - 120, 0.5f, 1f)
        }
    }

}
