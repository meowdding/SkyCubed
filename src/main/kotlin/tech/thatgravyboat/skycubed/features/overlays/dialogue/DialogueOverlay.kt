package tech.thatgravyboat.skycubed.features.overlays.dialogue

import me.owdding.ktmodules.Module
import me.owdding.lib.displays.*
import me.owdding.lib.overlays.ConfigPosition
import me.owdding.lib.overlays.EditableProperty
import me.owdding.lib.utils.KeyboardInputs
import me.owdding.lib.utils.keys
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
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
import tech.thatgravyboat.skyblockapi.utils.extentions.scissor
import tech.thatgravyboat.skyblockapi.utils.extentions.translated
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentMatchResult
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentRegex
import tech.thatgravyboat.skyblockapi.utils.regex.component.match
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.command
import tech.thatgravyboat.skycubed.config.overlays.NpcOverlayConfig
import tech.thatgravyboat.skycubed.utils.BackgroundLessSkyCubedOverlay
import tech.thatgravyboat.skycubed.utils.RegisterOverlay
import tech.thatgravyboat.skycubed.utils.SkyCubedTextures
import kotlin.math.max

@Module
@RegisterOverlay
object DialogueOverlay : BackgroundLessSkyCubedOverlay {

    private val messageRegex = ComponentRegex("\\[NPC] (?<name>[^:]+): (?<message>.+)")
    private val selectAnOptionRegex = listOf(
        ComponentRegex(".*(?:Select|Click) an option: (?<options>.+)"),
        ComponentRegex("\\nAccept the trapper's task to hunt the animal\\?\\nClick an option: (?<options>.+)"),
    )
    private val optionRegex = ComponentRegex("\\[(?<option>.*?)]")
    private val wordRegex = Regex("\\s+")

    private val queue = mutableListOf<Pair<Component, Component>>()
    private var nextCheck = 0L

    private var options: List<Option> = emptyList()

    private var displayedOptions = false
    private var hudOverlayDisplay: Display = Displays.empty()
    private var inventoryOverlayDisplay: Display = Displays.empty()

    override val name: Component = Text.of("Dialogue")
    override val position: ConfigPosition = ConfigPosition(0, 0)
    override val actualBounds: Pair<Int, Int> = 0 to 0
    override val properties: Collection<EditableProperty> = setOf()
    override val enabled: Boolean get() = config.enabled

    private val config get() = NpcOverlayConfig
    private val messageWordsPerMinute get() = config.messageWordsPerMinute
    private val minimumDurationPerMessage get() = (config.minimumDurationPerMessage * 1000).toLong()
    private val displayActionDuration get() = (config.durationForActionMessage * 1000f).toLong()

    private var containerLeftPos: Int? = null

    @Subscription
    @OnlyOnSkyBlock
    fun onChatReceived(event: ChatReceivedEvent.Pre) {
        if (!enabled) return

        messageRegex.match(event.component, "name", "message") { (name, message) ->
            queue.add(name to message)
            if (config.hideChatMessage) event.cancel()
        }
        selectAnOptionRegex.match(event.component, "options") { (optionsComponent) ->
            options = optionRegex.findAll(optionsComponent).mapNotNull { it["option"] }.mapIndexed { index, option ->
                Option(
                    component = option,
                    keys = keys {
                        withKey(49 + index)
                    },
                    command = option.command ?: "",
                )
            }
            if (config.hideSelectAnOptionMessage) event.cancel()
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
                nextCheck = System.currentTimeMillis() + minimumDurationPerMessage
                if (options.isNotEmpty() && !displayedOptions) {
                    hudOverlayDisplay = createOptionsDisplay()
                } else {
                    reset()
                }
            } else {
                val (name, message) = queue.removeFirstOrNull() ?: return
                val npc = DialogueNpcs.get(name.stripped)
                val inventoryOverlayWidth = containerLeftPos.takeIf { it != null } ?: (McClient.window.guiScaledWidth / 4)

                nextCheck = System.currentTimeMillis() + calculateDisplayDuration(message)

                hudOverlayDisplay = createMainDisplay(name, message, npc, McClient.window.guiScaledWidth / 3)
                inventoryOverlayDisplay = createMainDisplay(name, message, npc, inventoryOverlayWidth - 30)
            }
        }

        options.forEach { (_, keys, command) ->
            if (keys.isDown()) {
                McClient.sendCommand(command.removePrefix("/"))
                reset()
                return
            }
        }
    }

    private fun createMainDisplay(name: Component, message: Component, npc: DialogueNpc, maxWidth: Int): Display {
        val entity = DialogueEntities.get(name.stripped, npc)
        val npcNameDisplay = Displays.background(
            SkyCubedTextures.backgroundBox,
            Displays.padding(5, Displays.component(name, maxWidth)),
        )
        val npcTextDisplay = Displays.component(message, maxWidth).let { display ->
            Displays.background(
                SkyCubedTextures.backgroundBox,
                Displays.padding(15, ((maxWidth * 0.8f).toInt() - display.getWidth()).coerceAtLeast(0) + 15, 15, 15, display),
            )
        }

        return object : Display {
            override fun getWidth(): Int = npcTextDisplay.getWidth()
            override fun getHeight(): Int = npcTextDisplay.getHeight()

            override fun render(graphics: GuiGraphics) {
                npcTextDisplay.render(graphics)
                npcNameDisplay.render(graphics, 60.takeIf { entity != null } ?: 5, -npcNameDisplay.getHeight() / 2)

                if (entity != null) {
                    val display = Displays.entity(entity, 60, 80, 35, 80f, 40f)
                    graphics.translated(0, -45) {
                        graphics.scissor(0..60, 0..45) {
                            display.render(graphics)
                        }
                    }
                }
            }
        }
    }

    private fun createOptionsDisplay(): Display {
        displayedOptions = true
        nextCheck = System.currentTimeMillis() + displayActionDuration

        val yesNoDisplay = options.mapIndexed { index, (component) ->
            val text = Text.join(Text.of("${index + 1}. ").withColor(TextColor.GRAY), component)
            Displays.background(SkyCubedTextures.backgroundBox, Displays.text(text).withPadding(5))
        }.toColumn(5, Alignment.START)

        return object : Display {
            private val main = hudOverlayDisplay
            override fun getWidth(): Int = main.getWidth()
            override fun getHeight(): Int = main.getHeight()

            override fun render(graphics: GuiGraphics) {
                main.render(graphics)
                graphics.translated(main.getWidth() - yesNoDisplay.getWidth() - 10f, -1f * yesNoDisplay.getHeight() - 10f) {
                    yesNoDisplay.render(graphics)
                }
            }
        }
    }

    private fun reset() {
        DialogueEntities.updateCache(max(minimumDurationPerMessage, displayActionDuration) + 5000)
        options = emptyList()
        displayedOptions = false
        hudOverlayDisplay = Displays.empty()
        inventoryOverlayDisplay = Displays.empty()
        nextCheck = 0
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onForeground(event: RenderScreenForegroundEvent) {
        if (!enabled) return
        val graphics = event.graphics

        render(graphics, 0, 0)
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

    private fun calculateDisplayDuration(message: Component): Long {
        val wpmDuration = ((message.stripped.split(wordRegex).count().toLong() * 60000) / messageWordsPerMinute)
        return wpmDuration.coerceAtLeast(minimumDurationPerMessage)
    }

    private fun ComponentRegex.findAll(component: Component) = regex().findAll(component.stripped).map { ComponentMatchResult(component, it) }.toList()


    private data class Option(
        val component: Component,
        val keys: KeyboardInputs,
        val command: String,
    )
}
