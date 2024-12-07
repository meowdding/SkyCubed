const toolboxCategories = {}
Object.entries(blocks)
    .filter(([_, value]) => value.include !== false)
    .forEach(([key, value]) => {
        let list = toolboxCategories[value.group] || []
        list.push({kind: "block", type: key})
        toolboxCategories[value.group] = list
    })

const workspace = Blockly.inject("blocklyDiv", {
    toolbox: {
        kind: 'categoryToolbox',
        contents: Object.entries(toolboxCategories).flatMap(([name, contents]) => {
            return [{
                kind: "category",
                name: name,
                contents: contents,
                colour: "100"
            }]
        }),
    }
});

workspace.addChangeListener(() => {
    const output = document.getElementById("output");
    output.innerHTML = Blockly.JSON.fromWorkspace(workspace);
    localStorage.setItem("data", JSON.stringify(Blockly.serialization.workspaces.save(workspace)));
});

(function() {
    if (localStorage.getItem("data")) {
        const data = JSON.parse(localStorage.getItem("data"));
        if (data) {
            Blockly.serialization.workspaces.load(data, workspace);
            return;
        }
    }

    workspace.clear();
    const map = workspace.newBlock("map");
    map.initSvg();
    map.render();
    map.setMovable(false);
    map.setDeletable(false);
    map.moveBy(15, 10);

    const island = workspace.newBlock("island");
    island.initSvg();
    island.render();
    island.moveBy(15, 10);

    map.getInput("islands").connection.connect(island.previousConnection);
})();
