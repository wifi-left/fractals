# Overview
This mod seeks to fill the gaps in Minecraft's world generation by adding large new trees in a new biome, the Fractal Forest. In this biome you will find new procedurally generated trees which tower over the landscape generated using L-Systems, a method of generating fractal structures.

# Features
- Procedurally generated 'Fractal Trees'
- New Leaf Type - 'Fractal Leaves'
  - These leaves let all light through, reducing large shadows at the base of the tree.
- Fractal Forest biome
- Command support for manual giant tree generation

# Commands
- `/GIANT_TREE`  
  Generates a giant tree at your current position with a default target height of ~150 blocks.
- `/GIANT_TREE <height>`  
  Generates a giant tree at your current position with a custom target height (40-300 blocks).
- `/GIANT_TREE at <pos>`  
  Generates a giant tree at the target block position with default height and default blocks.
- `/GIANT_TREE at <pos> <trunk_block> <leaf_block>`  
  Generates a giant tree at the target position with default height and custom trunk/leaf blocks.
- `/GIANT_TREE at <pos> <height> <trunk_block> <leaf_block>`  
  Generates a giant tree at the target position with custom height, custom trunk block, and custom leaf block.

# Warnings
- This mod is still in development, and may contain some bugs...
- Generating new chunks with the custom trees can take a few seconds after the chunk loads in, so please be patient. Do consider turning down your render distance to prevent the number of trees being loaded into the game.
