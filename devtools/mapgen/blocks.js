const blocks = {
    map: {
        include: false,
        init: function() {
            this.appendDummyInput().appendField("Map");
            this.appendStatementInput("islands").setCheck("Island");
            this.setColour(285);
            this.setDeletable(false);
        },
        generator: function (block) {
            const islands = [];
            let blockPoi = block.getInputTargetBlock("islands");
            while (blockPoi) {
                islands.push(Blockly.JSON.generalBlockToObj(blockPoi))
                blockPoi = blockPoi.getNextBlock()
            }

            return islands
        }
    },
    island: {
        group: "Misc",
        init: function() {
            this.appendEndRowInput("island")
                .appendField("Island")
                .appendField(new Blockly.FieldDropdown([
                    ["Private Island", "PRIVATE_ISLAND"],
                    ["Hub", "HUB"],
                    ["Dungeon Hub", "DUNGEON_HUB"],
                    ["Farming Islands", "THE_BARN"],
                    ["The Park", "THE_PARK"],
                    ["Gold Mines", "GOLD_MINES"],
                    ["Deep Caverns", "DEEP_CAVERNS"],
                    ["Dwarven Mines", "DWARVEN_MINES"],
                    ["Crystal Hollows", "CRYSTAL_HOLLOWS"],
                    ["Mineshaft", "MINESHAFT"],
                    ["Spiders Den", "SPIDERS_DEN"],
                    ["The End", "THE_END"],
                    ["Crimson Isles", "CRIMSON_ISLE"],
                    ["Garden", "GARDEN"],
                    ["The Rift", "THE_RIFt"],
                    ["Dark Auction", "DARK_AUCTION"],
                    ["Dungeons", "THE_CATACOMBS"],
                    ["Kuudra", "KUUDRA"],
                    ["Jerry\"s Workshop", "JERRYS_WORKSHOP"]
                ]), "island");
            this.appendStatementInput("pois").setCheck("Poi");
            this.setPreviousStatement(true, "Island");
            this.setNextStatement(true, "Island");
            this.setColour(160);
        },
        generator: function(block) {
            const pois = [];
            let blockPoi = block.getInputTargetBlock("pois");
            while (blockPoi) {
                pois.push(Blockly.JSON.generalBlockToObj(blockPoi))
                blockPoi = blockPoi.getNextBlock()
            }

            return {
                island: block.getFieldValue("island"),
                pois: pois
            }
        }
    },
    npc_poi: {
        group: "Poi",
        init: function() {
            this.appendDummyInput().appendField("NPC");
            this.appendDummyInput();
            this.appendEndRowInput("name").appendField("Name").appendField(new Blockly.FieldTextInput(""), "name");
            this.appendEndRowInput("texture").appendField("Texture").appendField(new Blockly.FieldTextInput(""), "texture");
            this.appendEndRowInput("link").appendField("Link").appendField(new Blockly.FieldTextInput(""), "link");
            this.appendEndRowInput("position")
                .appendField("X:").appendField(new Blockly.FieldNumber(0), "x")
                .appendField("Z:").appendField(new Blockly.FieldNumber(0), "z");
            this.setPreviousStatement(true, "Poi");
            this.setNextStatement(true, "Poi");
            this.setColour(255);
        },
        generator: function(block) {
            return {
                type: "npc",
                texture: block.getFieldValue("texture"),
                link: block.getFieldValue("link"),
                position: {
                    x: Number(block.getFieldValue("x")),
                    z: Number(block.getFieldValue("z")),
                },
                tooltip: [
                    `§9${block.getFieldValue("name")}`,
                    "",
                    "§7§lClick to view wiki"
                ]
            }
        }
    },
    conditional_poi: {
        group: "Poi",
        init: function() {
            this.appendDummyInput().appendField("Conditional");
            this.appendValueInput("enabled").setCheck("Condition").appendField("Enable Condition");
            this.appendValueInput("significant").setCheck("Condition").appendField("Significance Condition");
            this.appendStatementInput("poi").setCheck("Poi").appendField("Poi");
            this.setPreviousStatement(true, "Poi");
            this.setNextStatement(true, "Poi");
            this.setColour(255);
        },
        generator: function (block) {
            return {
                type: "conditional",
                enabled: Blockly.JSON.generalBlockToObj(block.getInputTargetBlock("enabled")) || undefined,
                significant: Blockly.JSON.generalBlockToObj(block.getInputTargetBlock("significant")) || undefined,
                poi: Blockly.JSON.generalBlockToObj(block.getInputTargetBlock("poi"))
            }
        }
    },
    // Conditions
    condtional_not: {
        group: "Conditions",
        init: function() {
            this.appendValueInput("condition").setCheck("Condition").appendField("not");
            this.setOutput(true, "Condition");
            this.setColour(210);
        },
        generator: function (block) {
            return {
                condition: "not",
                value: Blockly.JSON.generalBlockToObj(block.getInputTargetBlock("condition"))
            }
        }
    },
    conditional_area: {
        group: "Conditions",
        init: function() {
            this.appendEndRowInput("area").appendField("Area").appendField(new Blockly.FieldTextInput(""), "area");
            this.setOutput(true, "Condition");
            this.setColour(210);
        },
        generator: function (block) {
            return {
                condition: "area",
                area: block.getFieldValue("area")
            }
        }
    },
    conditional_season: {
        group: "Conditions",
        init: function() {
            this.appendEndRowInput("season")
                .appendField("Season")
                .appendField(new Blockly.FieldDropdown([
                    ["Early Spring", "EARLY_SPRING"],
                    ["Spring", "SPRING"],
                    ["Late Spring", "LATE_SPRING"],
                    ["Early Summer", "EARLY_SUMMER"],
                    ["Summer", "SUMMER"],
                    ["Late Summer", "LATE_SUMMER"],
                    ["Early Autumn", "EARLY_AUTUMN"],
                    ["Autumn", "AUTUMN"],
                    ["Late Autumn", "LATE_AUTUMN"],
                    ["Early Winter", "EARLY_WINTER"],
                    ["Winter", "WINTER"],
                    ["Late Winter", "LATE_WINTER"]
                ]), "season");
            this.setOutput(true, "Condition");
            this.setColour(210);
        },
        generator: function (block) {
            return {
                condition: "season",
                season: block.getFieldValue("season")
            }
        }
    },
    conditional_island: {
        group: "Conditions",
        init: function() {
            this.appendEndRowInput("island")
                .appendField("Island")
                .appendField(new Blockly.FieldDropdown([
                    ["Private Island", "PRIVATE_ISLAND"],
                    ["Hub", "HUB"],
                    ["Dungeon Hub", "DUNGEON_HUB"],
                    ["Farming Islands", "THE_BARN"],
                    ["The Park", "THE_PARK"],
                    ["Gold Mines", "GOLD_MINES"],
                    ["Deep Caverns", "DEEP_CAVERNS"],
                    ["Dwarven Mines", "DWARVEN_MINES"],
                    ["Crystal Hollows", "CRYSTAL_HOLLOWS"],
                    ["Mineshaft", "MINESHAFT"],
                    ["Spiders Den", "SPIDERS_DEN"],
                    ["The End", "THE_END"],
                    ["Crimson Isles", "CRIMSON_ISLE"],
                    ["Garden", "GARDEN"],
                    ["The Rift", "THE_RIFt"],
                    ["Dark Auction", "DARK_AUCTION"],
                    ["Dungeons", "THE_CATACOMBS"],
                    ["Kuudra", "KUUDRA"],
                    ["Jerry\"s Workshop", "JERRYS_WORKSHOP"],
                ]), "island");
            this.setOutput(true, "Condition");
            this.setColour(210);
        },
        generator: function (block) {
            return {
                condition: "island",
                island: block.getFieldValue("island")
            }
        }
    },
    conditional_or: {
        group: "Conditions",
        init: function() {
            this.appendValueInput("condition_1").setCheck("Condition");
            this.appendDummyInput("text").appendField("or");
            this.appendValueInput("condition_2").setCheck("Condition");
            this.setInputsInline(true)
            this.setOutput(true, "Condition");
            this.setColour(210);
        },
        generator: function (block) {
            const flatten = (block, outputs) => {
                if (block?.type === "conditional_or") {
                    flatten(block.getInputTargetBlock("condition_1"), outputs);
                    flatten(block.getInputTargetBlock("condition_2"), outputs);
                } else if (block) {
                    outputs.push(Blockly.JSON.generalBlockToObj(block));
                }
                return outputs;
            }
            return {
                condition: "or",
                conditions: flatten(block, [])
            }
        }
    },
    conditional_and: {
        group: "Conditions",
        init: function() {
            this.appendValueInput("condition_1").setCheck("Condition");
            this.appendDummyInput("text").appendField("and");
            this.appendValueInput("condition_2").setCheck("Condition");
            this.setInputsInline(true)
            this.setOutput(true, "Condition");
            this.setColour(210);
        },
        generator: function (block) {
            const flatten = (block, outputs) => {
                if (block?.type === "conditional_and") {
                    flatten(block.getInputTargetBlock("condition_1"), outputs);
                    flatten(block.getInputTargetBlock("condition_2"), outputs);
                } else if (block) {
                    outputs.push(Blockly.JSON.generalBlockToObj(block));
                }
                return outputs;
            }
            return {
                condition: "and",
                conditions: flatten(block, [])
            }
        }
    },
    conditional_position: {
        group: "Conditions",
        init: function() {
            this.appendEndRowInput("operation")
                .appendField(new Blockly.FieldDropdown([
                    ["x", "x"],
                    ["y", "y"],
                    ["z", "z"]
                ]), "variable")
                .appendField(new Blockly.FieldDropdown([
                    ["<", "<"],
                    [">", ">"]
                ]), "operator")
                .appendField(new Blockly.FieldNumber(0), "number");
            this.setInputsInline(true)
            this.setOutput(true, "Condition");
            this.setColour(210);
        },
        generator: function (block) {
            return {
                condition: "position",
                position: `${block.getFieldValue("variable")} ${block.getFieldValue("operator")} ${Number(block.getFieldValue("number"))}`
            }
        }
    },
}

Blockly.common.defineBlocks(blocks);

for (let [id, block] of Object.entries(blocks)) {
    Blockly.JSON[id] = block.generator
}