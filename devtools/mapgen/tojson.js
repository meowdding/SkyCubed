Blockly.JSON = new Blockly.Generator("JSON");

Blockly.JSON.fromWorkspace = (workspace) => {
    let output = '';
    for (const block of workspace.getTopBlocks(false)) {
        output += JSON.stringify(Blockly.JSON.generalBlockToObj(block), null, 4) + '\n\n';
    }
    return output;
}

Blockly.JSON.generalBlockToObj = (block) => {
    if (!block) return null;
    const generator = Blockly.JSON[block.type];
    if (generator) return generator.call(this, block);
    console.log(`Can't generate json for '${block.type}'`)
}